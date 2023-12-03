package backend.team.ahachul_backend.api.train.application.service

import backend.team.ahachul_backend.api.common.application.port.out.StationReader
import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetCongestionDto
import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainDto
import backend.team.ahachul_backend.api.train.adapter.`in`.dto.GetTrainRealTimesDto
import backend.team.ahachul_backend.api.train.application.port.`in`.TrainUseCase
import backend.team.ahachul_backend.api.train.application.port.`in`.command.GetCongestionCommand
import backend.team.ahachul_backend.api.train.application.port.out.TrainReader
import backend.team.ahachul_backend.api.train.domain.entity.TrainEntity
import backend.team.ahachul_backend.api.train.domain.model.TrainArrivalCode
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import backend.team.ahachul_backend.common.client.SeoulTrainClient
import backend.team.ahachul_backend.common.client.TrainCongestionClient
import backend.team.ahachul_backend.common.client.dto.TrainCongestionDto
import backend.team.ahachul_backend.common.dto.TrainRealTimeDto
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.logging.Logger
import backend.team.ahachul_backend.common.persistence.SubwayLineReader
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TrainService(
    private val trainReader: TrainReader,
    private val stationLineReader: StationReader,
    private val subwayLineReader: SubwayLineReader,

    private val seoulTrainClient: SeoulTrainClient,

    private val trainCacheUtils: TrainCacheUtils,
    private val congestionCacheUtils: CongestionCacheUtils,
    private val trainCongestionClient: TrainCongestionClient,
): TrainUseCase {

    private val logger: Logger = Logger(javaClass)

    override fun getTrain(trainNo: String): GetTrainDto.Response {
        val (prefixTrainNo, location, organizationTrainNo) = decompositionTrainNo(trainNo)
        val train: TrainEntity
        try {
             train = trainReader.getTrain(prefixTrainNo)
        } catch (e: AdapterException) {
            logger.info("prefixTrainNo is no matching train. prefixTrainNo : {}".format(prefixTrainNo))
            throw BusinessException(ResponseCode.INVALID_PREFIX_TRAIN_NO)
        }

        return GetTrainDto.Response.of(
            train = train,
            location = location,
            organizationTrainNo = organizationTrainNo,
        )
    }

    private fun decompositionTrainNo(trainNo: String): Triple<String, Int, String> {
        return Triple(
            trainNo.dropLast(3),
            trainNo[trainNo.length - 3].digitToInt(),
            trainNo.takeLast(2),
        )
    }

    override fun getTrainRealTimes(stationId: Long, subwayLineId: Long): List<GetTrainRealTimesDto.TrainRealTime> {
        val station = stationLineReader.getById(stationId)
        val subwayLine = subwayLineReader.getById(subwayLineId)
        val subwayLineIdentity = subwayLine.identity

        trainCacheUtils.getCache(subwayLineIdentity, stationId)?.let { return it }

        val trainRealTimeMap = requestTrainRealTimesAndSorting(station.name)
        trainRealTimeMap.forEach {
            trainCacheUtils.setCache(it.key.toLong(), stationId, it.value)
        }
        return trainRealTimeMap.getOrElse(subwayLineIdentity.toString()) { emptyList() }
    }

    private fun requestTrainRealTimesAndSorting(stationName: String): Map<String, List<GetTrainRealTimesDto.TrainRealTime>> {
        var startIndex = 1
        var endIndex = 5
        var totalSize = startIndex
        val trainRealTimes = mutableListOf<GetTrainRealTimesDto.TrainRealTime>()

        while (startIndex <= totalSize) {
            val trainRealTimesPublicData = seoulTrainClient.getTrainRealTimes(stationName, startIndex, endIndex)
            totalSize = trainRealTimesPublicData.errorMessage?.total ?: break
            startIndex = endIndex + 1
            endIndex = startIndex + 4
            trainRealTimes.addAll(generateTrainRealTimeList(trainRealTimesPublicData))
        }

        if (trainRealTimes.size == 0) {
            throw BusinessException(ResponseCode.NOT_EXIST_ARRIVAL_TRAIN)
        }

        return trainRealTimes
            .groupBy { it.subwayId!! }
            .mapValues {
                trainCacheUtils.getSortedData( it.value )
            }
    }

    private fun generateTrainRealTimeList(trainRealTime: TrainRealTimeDto): List<GetTrainRealTimesDto.TrainRealTime> {
        return trainRealTime.realtimeArrivalList
            ?.map {
                val trainDirection = it.trainLineNm.split("-")
                GetTrainRealTimesDto.TrainRealTime(
                    subwayId = it.subwayId,
                    stationOrder = extractStationOrder(it.arvlMsg2),
                    upDownType = UpDownType.from(it.updnLine),
                    nextStationDirection = trainDirection[1].trim(),
                    destinationStationDirection = trainDirection[0].trim(),
                    trainNum = it.btrainNo,
                    currentLocation = it.arvlMsg2.replace("\\d+초 후".toRegex(), "").trim(),
                    currentTrainArrivalCode = TrainArrivalCode.from(it.arvlCd)
                )
            }
            ?: emptyList()
    }

    private fun extractStationOrder(destinationMessage: String): Int {
        return if (destinationMessage.startsWith("[")) {
            pattern.find(destinationMessage)?.groupValues
                ?.getOrNull(1)
                ?.toInt()
                ?: Int.MAX_VALUE
        } else {
            Int.MAX_VALUE
        }
    }

    override fun getTrainCongestion(command: GetCongestionCommand): GetCongestionDto.Response {
        val subwayLineId = subwayLineReader.getById(command.subwayLineId).id
        val trainNo = command.trainNo

        congestionCacheUtils.getCache(subwayLineId, trainNo)?.let { return it }

        val correctTrainNum = getCorrectTrainNum(subwayLineId, trainNo)
        val response = trainCongestionClient.getCongestions(subwayLineId, correctTrainNum.toInt())
        val trainCongestion = response.data!!

        val congestions = mapCongestionDto(response.success, trainCongestion)
        val congestionDto = GetCongestionDto.Response.from(correctTrainNum, congestions)
        congestionCacheUtils.setCache(subwayLineId, correctTrainNum, congestionDto)
        return congestionDto
    }

    /**
     * API 자체에서 발생하는 열차 번호 에러 수정
     */
    private fun getCorrectTrainNum(subwayLineId: Long, trainNo: String): String {
        return when (trainNo[0] != subwayLineId.toString()[0]) {
            false -> "${subwayLineId}${trainNo.substring(1, trainNo.length)}"
            true -> trainNo
        }
    }

    private fun mapCongestionDto(
        success: Boolean, trainCongestion: TrainCongestionDto.Train
    ): List<GetCongestionDto.Section>  {
        if (success) {
            val congestionList = parse(trainCongestion.congestionResult.congestionCar)
            return congestionList.mapIndexed {
                    idx, it -> GetCongestionDto.Section.from(idx, it)
            }
        }
        return emptyList()
    }

    private fun parse(congestion: String): List<Int> {
        val congestions = congestion.trim().split(DELIMITER)
        return congestions.map { it.toInt() }
    }

    companion object {
        const val DELIMITER = "|"
        val pattern = "\\[(\\d+)]".toRegex()
    }
}
