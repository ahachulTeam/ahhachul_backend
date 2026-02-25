package backend.team.ahachul_backend.api.station.application.service

import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.common.domain.entity.SubwayLineStationEntity
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.GetStationTimesDto
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.SearchSubwayRouteDto
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.SearchSubwayRouteQualityV3Dto
import backend.team.ahachul_backend.api.station.adapter.`in`.dto.StationTimeWeekType
import backend.team.ahachul_backend.api.station.application.port.`in`.StationUseCase
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesFullCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationLastTrainRiskCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationQuickExitCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesSummaryCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.GetStationTimesQualityReportCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.SearchSubwayRouteCommand
import backend.team.ahachul_backend.api.station.application.port.`in`.dto.SearchSubwayRouteQualityV3Command
import backend.team.ahachul_backend.api.train.domain.model.UpDownType
import backend.team.ahachul_backend.common.client.SeoulTrainClient
import backend.team.ahachul_backend.common.config.CircuitBreakerConfig.Companion.CUSTOM_CIRCUIT_BREAKER
import backend.team.ahachul_backend.common.exception.BusinessException
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.logging.Logger
import backend.team.ahachul_backend.common.response.ResponseCode
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.PriorityQueue

@Service
@Transactional(readOnly = true)
class StationService(
    private val subwayLineStationReader: SubwayLineStationReader,
    private val stationTimesCacheUtils: StationTimesCacheUtils,
    private val seoulTrainClient: SeoulTrainClient,
): StationUseCase {

    private val logger: Logger = Logger(javaClass)

    private data class GraphEdge(
        val fromStationId: Long,
        val toStationId: Long,
        val subwayLineId: Long,
        val subwayLineName: String,
    )

    private data class RouteState(
        val stationId: Long,
        val lineId: Long?,
    )

    private data class RouteMetric(
        val stops: Int,
        val transfers: Int,
    )

    private data class StateWithMetric(
        val state: RouteState,
        val metric: RouteMetric,
    )

    private data class Prev(
        val previousState: RouteState,
        val edge: GraphEdge,
    )

    private data class StationCodeSortKey(
        val numericPrefix: Int,
        val suffix: String,
        val normalized: String,
    )

    private data class PathResult(
        val nodes: List<Long>,
        val edges: List<GraphEdge>,
        val metric: RouteMetric,
        val stationNamesById: Map<Long, String>,
    )

    private data class SummaryStationTimesLoadResult(
        val upDownType: UpDownType,
        val stationTimes: List<GetStationTimesDto.StationTimes>,
        val dataSource: GetStationTimesDto.StationSummaryDataSource,
        val fallbackReasonCode: String?,
    )

    private data class LastTrainSafetyEvaluation(
        val score: Int,
        val confidenceLevel: SearchSubwayRouteQualityV3Dto.RouteQualityConfidenceLevel,
        val reason: String,
        val isRisk: Boolean,
        val hasData: Boolean,
    )

    @CircuitBreaker(name = CUSTOM_CIRCUIT_BREAKER, fallbackMethod = "fallbackOnExternalStationTimesApiGet")
    override fun getStationTimes(command: GetStationTimesCommand): GetStationTimesDto.Response {
        return GetStationTimesDto.Response(loadStationTimes(command))
    }

    override fun getStationTimesFull(command: GetStationTimesFullCommand): GetStationTimesDto.FullResponse {
        val weeks = StationTimeWeekType.values().map { weekType ->
            val upDownTimetables = UpDownType.values().map { upDownType ->
                val stationTimes = loadStationTimesSafely(
                    GetStationTimesCommand(
                        stationId = command.stationId,
                        subwayLineId = command.subwayLineId,
                        upDownType = upDownType,
                        stationTimeWeekType = weekType,
                    )
                ).sortedBy { it.departureTime }

                GetStationTimesDto.UpDownTimetable(
                    upDownType = upDownType,
                    stationTimes = stationTimes,
                )
            }

            GetStationTimesDto.WeekTimetable(
                stationTimeWeekType = weekType,
                upDownTimetables = upDownTimetables,
            )
        }

        return GetStationTimesDto.FullResponse(
            generatedAt = OffsetDateTime.now().toString(),
            stationId = command.stationId,
            subwayLineId = command.subwayLineId,
            weeks = weeks,
        )
    }

    @CircuitBreaker(name = CUSTOM_CIRCUIT_BREAKER, fallbackMethod = "fallbackOnExternalStationTimesSummaryApiGet")
    override fun getStationTimesSummary(command: GetStationTimesSummaryCommand): GetStationTimesDto.SummaryResponse {
        val loadResults = UpDownType.values().map { upDownType ->
            loadStationTimesForSummary(
                GetStationTimesCommand(
                    stationId = command.stationId,
                    subwayLineId = command.subwayLineId,
                    upDownType = upDownType,
                    stationTimeWeekType = command.stationTimeWeekType,
                )
            )
        }

        val summaries = loadResults.map { result ->
            val sortedByDepartureTime = result.stationTimes.sortedBy { it.departureTime }
            val firstTrain = sortedByDepartureTime.firstOrNull()
            val lastTrain = sortedByDepartureTime.lastOrNull()

            GetStationTimesDto.UpDownSummary(
                upDownType = result.upDownType,
                firstDepartureTime = firstTrain?.departureTime,
                lastDepartureTime = lastTrain?.departureTime,
                firstDestinationStationName = firstTrain?.arrivalStationName,
                lastDestinationStationName = lastTrain?.arrivalStationName,
            )
        }

        return GetStationTimesDto.SummaryResponse(
            stationTimeWeekType = command.stationTimeWeekType,
            summaries = summaries,
            meta = buildSummaryMeta(loadResults, summaries),
        )
    }

    override fun getStationTimesQualityReport(command: GetStationTimesQualityReportCommand): GetStationTimesDto.QualityReportResponse {
        val lineReports = subwayLineStationReader.findAll()
            .groupBy { it.subwayLine.id to it.subwayLine.name }
            .toSortedMap(compareBy({ it.first }, { it.second }))
            .map { (lineInfo, stations) ->
                val sampledStations = stations
                    .distinctBy { it.station.id }
                    .sortedBy { it.station.id }
                    .take(command.samplePerLine)

                var noDataStations = 0
                var fallbackStations = 0
                var missingStationCodeStations = 0

                sampledStations.forEach { station ->
                    if (station.stationCode.isNullOrBlank()) {
                        noDataStations += 1
                        missingStationCodeStations += 1
                        fallbackStations += 1
                        return@forEach
                    }

                    val upResult = loadStationTimesForSummary(
                        GetStationTimesCommand(
                            stationId = station.station.id,
                            subwayLineId = station.subwayLine.id,
                            upDownType = UpDownType.UP,
                            stationTimeWeekType = command.stationTimeWeekType,
                        )
                    )
                    val downResult = loadStationTimesForSummary(
                        GetStationTimesCommand(
                            stationId = station.station.id,
                            subwayLineId = station.subwayLine.id,
                            upDownType = UpDownType.DOWN,
                            stationTimeWeekType = command.stationTimeWeekType,
                        )
                    )

                    val isNoDataStation = upResult.stationTimes.isEmpty() && downResult.stationTimes.isEmpty()
                    if (isNoDataStation) {
                        noDataStations += 1
                    }

                    if (upResult.dataSource == GetStationTimesDto.StationSummaryDataSource.FALLBACK_EMPTY ||
                        downResult.dataSource == GetStationTimesDto.StationSummaryDataSource.FALLBACK_EMPTY
                    ) {
                        fallbackStations += 1
                    }
                }

                val sampledCount = sampledStations.size
                val noDataRatioPercent = calculateRatioPercent(noDataStations, sampledCount)

                GetStationTimesDto.LineQualityReport(
                    subwayLineId = lineInfo.first,
                    subwayLineName = lineInfo.second,
                    sampledStations = sampledCount,
                    noDataStations = noDataStations,
                    noDataRatioPercent = noDataRatioPercent,
                    fallbackStations = fallbackStations,
                    missingStationCodeStations = missingStationCodeStations,
                    qualityLevel = resolveQualityLevel(noDataRatioPercent),
                )
            }

        val totalSampledStations = lineReports.sumOf { it.sampledStations }
        val totalNoDataStations = lineReports.sumOf { it.noDataStations }

        return GetStationTimesDto.QualityReportResponse(
            generatedAt = OffsetDateTime.now().toString(),
            stationTimeWeekType = command.stationTimeWeekType,
            totalLineCount = lineReports.size,
            totalSampledStations = totalSampledStations,
            totalNoDataStations = totalNoDataStations,
            overallNoDataRatioPercent = calculateRatioPercent(totalNoDataStations, totalSampledStations),
            lines = lineReports,
        )
    }

    private fun loadStationTimesForSummary(command: GetStationTimesCommand): SummaryStationTimesLoadResult {
        return try {
            val subwayLineStation = subwayLineStationReader.findBySubwayLineIdAndStationId(command.subwayLineId, command.stationId)
            val stationCode = subwayLineStation.stationCode ?: throw BusinessException(ResponseCode.NOT_EXIST_PUBLIC_STATION_CODE)

            val cacheCommand = command.toCacheCommand(stationCode)
            val cached = stationTimesCacheUtils.getStationTimesByCache(cacheCommand)
            if (cached != null) {
                return SummaryStationTimesLoadResult(
                    upDownType = command.upDownType,
                    stationTimes = cached,
                    dataSource = GetStationTimesDto.StationSummaryDataSource.CACHE,
                    fallbackReasonCode = null,
                )
            }

            val response = seoulTrainClient.getStationTimesByApi(command.toRequest(stationCode))
            if (response.isFail()) {
                throw BusinessException(ResponseCode.INVALID_STATION_TIMES_API_RESPONSE)
            }

            val stationTimes = response.toStationTimes()
            stationTimesCacheUtils.setStationTimesCache(cacheCommand, stationTimes)
            SummaryStationTimesLoadResult(
                upDownType = command.upDownType,
                stationTimes = stationTimes,
                dataSource = GetStationTimesDto.StationSummaryDataSource.API,
                fallbackReasonCode = null,
            )
        } catch (e: BusinessException) {
            if (e.code == ResponseCode.FAILED_STATION_TIMES_API ||
                e.code == ResponseCode.INVALID_STATION_TIMES_API_RESPONSE ||
                e.code == ResponseCode.NOT_EXIST_PUBLIC_STATION_CODE
            ) {
                logger.error("station times summary fallback to empty list", e)
                SummaryStationTimesLoadResult(
                    upDownType = command.upDownType,
                    stationTimes = emptyList(),
                    dataSource = GetStationTimesDto.StationSummaryDataSource.FALLBACK_EMPTY,
                    fallbackReasonCode = e.code.code,
                )
            } else {
                throw e
            }
        }
    }

    private fun buildSummaryMeta(
        loadResults: List<SummaryStationTimesLoadResult>,
        summaries: List<GetStationTimesDto.UpDownSummary>,
    ): GetStationTimesDto.SummaryMeta {
        val availabilityStatus = resolveSummaryAvailabilityStatus(summaries)
        return GetStationTimesDto.SummaryMeta(
            generatedAt = OffsetDateTime.now().toString(),
            availabilityStatus = availabilityStatus,
            coveragePercent = calculateCoveragePercent(summaries),
            guidanceMessage = resolveSummaryGuidanceMessage(availabilityStatus, loadResults),
            sourceDetails = loadResults.map {
                GetStationTimesDto.SummarySourceDetail(
                    upDownType = it.upDownType,
                    dataSource = it.dataSource,
                    stationTimesCount = it.stationTimes.size,
                    fallbackReasonCode = it.fallbackReasonCode,
                )
            },
        )
    }

    private fun resolveSummaryAvailabilityStatus(
        summaries: List<GetStationTimesDto.UpDownSummary>,
    ): GetStationTimesDto.StationSummaryAvailabilityStatus {
        val availableDirectionCount = summaries.count {
            !it.firstDepartureTime.isNullOrBlank() || !it.lastDepartureTime.isNullOrBlank()
        }
        return when {
            availableDirectionCount >= UpDownType.values().size -> GetStationTimesDto.StationSummaryAvailabilityStatus.AVAILABLE
            availableDirectionCount == 0 -> GetStationTimesDto.StationSummaryAvailabilityStatus.EMPTY
            else -> GetStationTimesDto.StationSummaryAvailabilityStatus.PARTIAL
        }
    }

    private fun calculateCoveragePercent(
        summaries: List<GetStationTimesDto.UpDownSummary>,
    ): Int {
        val totalDirections = UpDownType.values().size
        val availableDirections = summaries.count {
            !it.firstDepartureTime.isNullOrBlank() || !it.lastDepartureTime.isNullOrBlank()
        }
        return calculateRatioPercent(availableDirections, totalDirections)
    }

    private fun resolveSummaryGuidanceMessage(
        availabilityStatus: GetStationTimesDto.StationSummaryAvailabilityStatus,
        loadResults: List<SummaryStationTimesLoadResult>,
    ): String {
        return when (availabilityStatus) {
            GetStationTimesDto.StationSummaryAvailabilityStatus.AVAILABLE -> "첫차/막차 정보를 정상적으로 제공 중입니다."
            GetStationTimesDto.StationSummaryAvailabilityStatus.PARTIAL -> "일부 방향의 시간표 정보만 제공됩니다."
            GetStationTimesDto.StationSummaryAvailabilityStatus.EMPTY -> {
                if (loadResults.any { it.dataSource == GetStationTimesDto.StationSummaryDataSource.FALLBACK_EMPTY }) {
                    "시간표 연동이 지연되어 정보가 비어 있습니다."
                } else {
                    "해당 역/노선의 시간표 데이터가 제공되지 않습니다."
                }
            }
        }
    }

    private fun calculateRatioPercent(numerator: Int, denominator: Int): Int {
        if (denominator <= 0) {
            return 0
        }
        return ((numerator * 100.0) / denominator).toInt()
    }

    private fun resolveQualityLevel(noDataRatioPercent: Int): GetStationTimesDto.StationTimeQualityLevel {
        return when {
            noDataRatioPercent <= 20 -> GetStationTimesDto.StationTimeQualityLevel.GOOD
            noDataRatioPercent <= 60 -> GetStationTimesDto.StationTimeQualityLevel.WARN
            else -> GetStationTimesDto.StationTimeQualityLevel.CRITICAL
        }
    }

    @CircuitBreaker(name = CUSTOM_CIRCUIT_BREAKER, fallbackMethod = "fallbackOnExternalStationTimesLastTrainRiskApiGet")
    override fun getLastTrainRisk(command: GetStationLastTrainRiskCommand): GetStationTimesDto.LastTrainRiskResponse {
        val stationTimes = loadStationTimesForLastTrainRisk(
            GetStationTimesCommand(
                stationId = command.stationId,
                subwayLineId = command.subwayLineId,
                upDownType = command.upDownType,
                stationTimeWeekType = command.stationTimeWeekType,
            )
        )

        val lastDepartureTime = stationTimes
            .maxByOrNull { it.departureTime }
            ?.departureTime

        val calculated = StationLastTrainRiskCalculator.calculate(
            nowAt = OffsetDateTime.now(),
            lastDepartureTime = lastDepartureTime,
            walkingMinutes = command.walkingMinutes,
        )

        return GetStationTimesDto.LastTrainRiskResponse(
            stationTimeWeekType = command.stationTimeWeekType,
            upDownType = command.upDownType,
            walkingMinutes = command.walkingMinutes,
            nowAt = calculated.nowAt.toString(),
            lastDepartureTime = lastDepartureTime,
            minutesToLastTrain = calculated.minutesToLastTrain,
            isLastTrainRisk = calculated.isLastTrainRisk,
            riskLevel = calculated.riskLevel,
            message = calculated.message,
        )
    }

    private fun loadStationTimesForLastTrainRisk(command: GetStationTimesCommand): List<GetStationTimesDto.StationTimes> {
        return try {
            loadStationTimes(command)
        } catch (e: BusinessException) {
            if (e.code == ResponseCode.FAILED_STATION_TIMES_API ||
                e.code == ResponseCode.INVALID_STATION_TIMES_API_RESPONSE
            ) {
                logger.error("station times last-train-risk fallback to empty list", e)
                emptyList()
            } else {
                throw e
            }
        }
    }

    private fun loadStationTimesSafely(command: GetStationTimesCommand): List<GetStationTimesDto.StationTimes> {
        return try {
            loadStationTimes(command)
        } catch (e: BusinessException) {
            if (e.code == ResponseCode.FAILED_STATION_TIMES_API ||
                e.code == ResponseCode.INVALID_STATION_TIMES_API_RESPONSE
            ) {
                logger.error("station times full fallback to empty list", e)
                emptyList()
            } else {
                throw e
            }
        }
    }

    private fun resolveStrategyOrder(
        primary: SearchSubwayRouteDto.RouteSearchStrategy,
    ): List<SearchSubwayRouteDto.RouteSearchStrategy> {
        val defaults = listOf(
            SearchSubwayRouteDto.RouteSearchStrategy.BALANCED,
            SearchSubwayRouteDto.RouteSearchStrategy.MIN_TRANSFER,
            SearchSubwayRouteDto.RouteSearchStrategy.MIN_STOP,
        )
        return listOf(primary) + defaults.filterNot { it == primary }
    }

    private fun buildAdjacency(
        allLineStations: List<SubwayLineStationEntity>,
    ): Map<Long, List<GraphEdge>> {
        val adjacency = mutableMapOf<Long, MutableSet<GraphEdge>>()

        allLineStations
            .groupBy { it.subwayLine.id }
            .forEach { (_, lineStations) ->
                val sortedLineStations = lineStations
                    .sortedWith(::compareLineStationOrder)
                sortedLineStations
                    .windowed(size = 2, step = 1, partialWindows = false)
                    .forEach { pair ->
                        val left = pair[0]
                        val right = pair[1]
                        addBidirectionalEdge(adjacency, left, right)
                    }

                if (isCircularLine(lineStations.firstOrNull()?.subwayLine?.name) &&
                    sortedLineStations.size > 2
                ) {
                    addBidirectionalEdge(adjacency, sortedLineStations.first(), sortedLineStations.last())
                }
            }

        return adjacency.mapValues { (_, edges) -> edges.toList() }
    }

    private fun addBidirectionalEdge(
        adjacency: MutableMap<Long, MutableSet<GraphEdge>>,
        left: SubwayLineStationEntity,
        right: SubwayLineStationEntity,
    ) {
        if (left.station.id == right.station.id) {
            return
        }

        val forward = GraphEdge(
            fromStationId = left.station.id,
            toStationId = right.station.id,
            subwayLineId = left.subwayLine.id,
            subwayLineName = left.subwayLine.name,
        )
        val backward = GraphEdge(
            fromStationId = right.station.id,
            toStationId = left.station.id,
            subwayLineId = left.subwayLine.id,
            subwayLineName = left.subwayLine.name,
        )
        adjacency.getOrPut(forward.fromStationId) { mutableSetOf() }.add(forward)
        adjacency.getOrPut(backward.fromStationId) { mutableSetOf() }.add(backward)
    }

    private fun isCircularLine(lineName: String?): Boolean {
        return lineName == "2호선"
    }

    private fun compareLineStationOrder(
        left: SubwayLineStationEntity,
        right: SubwayLineStationEntity,
    ): Int {
        val leftKey = toStationCodeSortKey(left.stationCode)
        val rightKey = toStationCodeSortKey(right.stationCode)

        if (leftKey == null && rightKey == null) {
            return left.id.compareTo(right.id)
        }
        if (leftKey == null) {
            return 1
        }
        if (rightKey == null) {
            return -1
        }

        if (leftKey.numericPrefix != rightKey.numericPrefix) {
            return leftKey.numericPrefix.compareTo(rightKey.numericPrefix)
        }
        if (leftKey.suffix != rightKey.suffix) {
            return leftKey.suffix.compareTo(rightKey.suffix)
        }
        if (leftKey.normalized != rightKey.normalized) {
            return leftKey.normalized.compareTo(rightKey.normalized)
        }
        return left.id.compareTo(right.id)
    }

    private fun toStationCodeSortKey(stationCode: String?): StationCodeSortKey? {
        val normalized = stationCode
            ?.trim()
            ?.uppercase()
            ?.takeIf { it.isNotEmpty() }
            ?: return null

        val match = Regex("^(\\d+)([A-Z]*)$").matchEntire(normalized)
        if (match != null) {
            return StationCodeSortKey(
                numericPrefix = match.groupValues[1].toInt(),
                suffix = match.groupValues[2],
                normalized = normalized,
            )
        }

        val numericPrefix = normalized
            .takeWhile { it.isDigit() }
            .toIntOrNull()
            ?: Int.MAX_VALUE

        return StationCodeSortKey(
            numericPrefix = numericPrefix,
            suffix = normalized.dropWhile { it.isDigit() },
            normalized = normalized,
        )
    }

    private fun findShortestPath(
        sourceStationId: Long,
        destinationStationId: Long,
        strategy: SearchSubwayRouteDto.RouteSearchStrategy,
        adjacency: Map<Long, List<GraphEdge>>,
        stationNamesById: Map<Long, String>,
    ): PathResult {
        if (sourceStationId == destinationStationId) {
            return PathResult(
                nodes = listOf(sourceStationId),
                edges = emptyList(),
                metric = RouteMetric(stops = 0, transfers = 0),
                stationNamesById = stationNamesById,
            )
        }

        val comparator = routeMetricComparator(strategy)
        val queue = PriorityQueue<StateWithMetric> { left, right ->
            comparator.compare(left.metric, right.metric)
        }

        val start = RouteState(sourceStationId, null)
        val distance = mutableMapOf(start to RouteMetric(stops = 0, transfers = 0))
        val previous = mutableMapOf<RouteState, Prev>()
        queue.add(StateWithMetric(start, RouteMetric(stops = 0, transfers = 0)))

        var endState: RouteState? = null
        var endMetric: RouteMetric? = null

        while (queue.isNotEmpty()) {
            val current = queue.poll()
            val currentBest = distance[current.state] ?: continue
            if (current.metric != currentBest) {
                continue
            }

            if (current.state.stationId == destinationStationId) {
                endState = current.state
                endMetric = current.metric
                break
            }

            adjacency[current.state.stationId].orEmpty().forEach { edge ->
                val nextState = RouteState(stationId = edge.toStationId, lineId = edge.subwayLineId)
                val isTransfer = current.state.lineId != null && current.state.lineId != edge.subwayLineId
                val nextMetric = RouteMetric(
                    stops = current.metric.stops + 1,
                    transfers = current.metric.transfers + if (isTransfer) 1 else 0,
                )
                val known = distance[nextState]
                if (known == null || comparator.compare(nextMetric, known) < 0) {
                    distance[nextState] = nextMetric
                    previous[nextState] = Prev(previousState = current.state, edge = edge)
                    queue.add(StateWithMetric(nextState, nextMetric))
                }
            }
        }

        if (endState == null || endMetric == null) {
            throw BusinessException(ResponseCode.ROUTE_NOT_FOUND)
        }

        val edgesReversed = mutableListOf<GraphEdge>()
        var cursor = endState
        while (cursor != start) {
            val prev = previous[cursor] ?: break
            edgesReversed.add(prev.edge)
            cursor = prev.previousState
        }

        val edges = edgesReversed.reversed()
        val nodes = if (edges.isEmpty()) {
            listOf(sourceStationId)
        } else {
            buildList {
                add(sourceStationId)
                edges.forEach { add(it.toStationId) }
            }
        }

        if (nodes.size <= 1) {
            throw BusinessException(ResponseCode.ROUTE_NOT_FOUND)
        }

        return PathResult(
            nodes = nodes,
            edges = edges,
            metric = endMetric,
            stationNamesById = stationNamesById,
        )
    }

    private fun routeMetricComparator(
        strategy: SearchSubwayRouteDto.RouteSearchStrategy,
    ): Comparator<RouteMetric> {
        return Comparator { left, right ->
            when (strategy) {
                SearchSubwayRouteDto.RouteSearchStrategy.BALANCED -> {
                    val leftScore = estimateRouteSelectionScore(left.stops, left.transfers)
                    val rightScore = estimateRouteSelectionScore(right.stops, right.transfers)
                    if (leftScore != rightScore) {
                        leftScore.compareTo(rightScore)
                    } else if (left.transfers != right.transfers) {
                        left.transfers.compareTo(right.transfers)
                    } else {
                        left.stops.compareTo(right.stops)
                    }
                }

                SearchSubwayRouteDto.RouteSearchStrategy.MIN_TRANSFER -> {
                    if (left.transfers != right.transfers) {
                        left.transfers.compareTo(right.transfers)
                    } else {
                        left.stops.compareTo(right.stops)
                    }
                }

                SearchSubwayRouteDto.RouteSearchStrategy.MIN_STOP -> {
                    if (left.stops != right.stops) {
                        left.stops.compareTo(right.stops)
                    } else {
                        left.transfers.compareTo(right.transfers)
                    }
                }
            }
        }
    }

    private fun PathResult.toRoute(rank: Int): SearchSubwayRouteDto.Route {
        val transferStationIds = edges
            .zipWithNext()
            .filter { (left, right) -> left.subwayLineId != right.subwayLineId }
            .map { (left, _) -> left.toStationId }
            .toSet()

        return SearchSubwayRouteDto.Route(
            rank = rank,
            nodes = nodes.mapIndexed { index, stationId ->
                SearchSubwayRouteDto.Node(
                    stationId = stationId,
                    stationName = stationNamesById[stationId] ?: "알 수 없음",
                    order = index,
                    isTransfer = stationId in transferStationIds,
                )
            },
            edges = edges.map {
                SearchSubwayRouteDto.Edge(
                    fromStationId = it.fromStationId,
                    toStationId = it.toStationId,
                    subwayLineId = it.subwayLineId,
                    subwayLineName = it.subwayLineName,
                )
            },
            summary = SearchSubwayRouteDto.Summary(
                totalStops = metric.stops,
                transferCount = metric.transfers,
                estimatedMinutes = estimateRouteMinutes(metric.stops, metric.transfers),
            ),
        )
    }

    private fun estimateRouteSelectionScore(totalStops: Int, transferCount: Int): Int {
        val baseTravelMinutes = totalStops * 2
        if (transferCount <= 0) {
            return baseTravelMinutes
        }

        val transferPenaltyMinutes = 14 + (transferCount - 1) * 20
        return baseTravelMinutes + transferPenaltyMinutes
    }

    private fun estimateRouteMinutes(totalStops: Int, transferCount: Int): Int {
        return totalStops * 2 + transferCount * 8
    }

    override fun getQuickExits(command: GetStationQuickExitCommand): GetStationTimesDto.QuickExitResponse {
        val recommendations = StationQuickExitRecommendationCalculator.recommend(
            stationId = command.stationId,
            subwayLineId = command.subwayLineId,
            upDownType = command.upDownType,
        )

        return GetStationTimesDto.QuickExitResponse(
            stationId = command.stationId,
            subwayLineId = command.subwayLineId,
            upDownType = command.upDownType,
            recommendations = recommendations,
        )
    }

    override fun searchSubwayRoutes(command: SearchSubwayRouteCommand): SearchSubwayRouteDto.Response {
        val allLineStations = subwayLineStationReader.findAllOrderedForGraph()
        if (allLineStations.isEmpty()) {
            throw BusinessException(ResponseCode.ROUTE_NOT_FOUND)
        }

        val stationNamesById = allLineStations.associate { it.station.id to it.station.name }
        if (!stationNamesById.containsKey(command.sourceStationId) ||
            !stationNamesById.containsKey(command.destinationStationId)
        ) {
            throw BusinessException(ResponseCode.ROUTE_NOT_FOUND)
        }

        val adjacency = buildAdjacency(allLineStations)
        val strategyOrder = resolveStrategyOrder(command.strategy)
        val routes = mutableListOf<SearchSubwayRouteDto.Route>()
        val routeSignatures = mutableSetOf<String>()

        strategyOrder.forEach { strategy ->
            if (routes.size >= command.alternatives) {
                return@forEach
            }

            runCatching {
                findShortestPath(
                    sourceStationId = command.sourceStationId,
                    destinationStationId = command.destinationStationId,
                    strategy = strategy,
                    adjacency = adjacency,
                    stationNamesById = stationNamesById,
                )
            }.onSuccess { path ->
                val signature = path.nodes.joinToString("-")
                if (routeSignatures.add(signature)) {
                    routes.add(path.toRoute(rank = routes.size + 1))
                }
            }
        }

        if (routes.isEmpty()) {
            throw BusinessException(ResponseCode.ROUTE_NOT_FOUND)
        }

        return SearchSubwayRouteDto.Response(
            generatedAt = OffsetDateTime.now().toString(),
            sourceStationId = command.sourceStationId,
            destinationStationId = command.destinationStationId,
            strategy = command.strategy,
            routes = routes,
        )
    }

    override fun searchSubwayRoutesV3(command: SearchSubwayRouteQualityV3Command): SearchSubwayRouteQualityV3Dto.Response {
        val nowAt = OffsetDateTime.now()
        val base = searchSubwayRoutes(
            SearchSubwayRouteCommand(
                sourceStationId = command.sourceStationId,
                destinationStationId = command.destinationStationId,
                strategy = command.strategy,
                alternatives = command.alternatives,
            )
        )

        val scoredRoutes = base.routes
            .map { route ->
                val accessibilityProfile = buildAccessibilityProfile(route, command)
                val boardingGuide = buildBoardingGuide(route, command)
                val crowdingGuide = buildCrowdingGuide(route, command, nowAt)
                val nearbyEssentials = buildNearbyEssentials(route)
                val travelModeTags = buildTravelModeTags(route, command)
                val quality = evaluateRouteQuality(
                    route = route,
                    command = command,
                    nowAt = nowAt,
                    accessibilityProfile = accessibilityProfile,
                    crowdingGuide = crowdingGuide,
                    travelModeTags = travelModeTags,
                )
                SearchSubwayRouteQualityV3Dto.Route(
                    rank = route.rank,
                    nodes = route.nodes.map {
                        SearchSubwayRouteQualityV3Dto.Node(
                            stationId = it.stationId,
                            stationName = it.stationName,
                            order = it.order,
                            isTransfer = it.isTransfer,
                        )
                    },
                    edges = route.edges.map {
                        SearchSubwayRouteQualityV3Dto.Edge(
                            fromStationId = it.fromStationId,
                            toStationId = it.toStationId,
                            subwayLineId = it.subwayLineId,
                            subwayLineName = it.subwayLineName,
                        )
                    },
                    summary = SearchSubwayRouteQualityV3Dto.Summary(
                        totalStops = route.summary.totalStops,
                        transferCount = route.summary.transferCount,
                        estimatedMinutes = route.summary.estimatedMinutes,
                    ),
                    quality = quality,
                    accessibilityProfile = accessibilityProfile,
                    boardingGuide = boardingGuide,
                    crowdingGuide = crowdingGuide,
                    nearbyEssentials = nearbyEssentials,
                    travelModeTags = travelModeTags,
                )
            }
            .sortedWith(
                compareByDescending<SearchSubwayRouteQualityV3Dto.Route> { it.quality.totalScore }
                    .thenBy { it.summary.estimatedMinutes }
                    .thenBy { it.summary.transferCount }
            )
            .mapIndexed { index, route ->
                val badges = if (index == 0) {
                    (listOf(SearchSubwayRouteQualityV3Dto.RouteQualityBadge.BEST_RECOMMENDED) + route.quality.badges)
                        .distinct()
                } else {
                    route.quality.badges
                }
                route.copy(
                    rank = index + 1,
                    quality = route.quality.copy(badges = badges),
                )
            }

        val primaryLineId = scoredRoutes.firstOrNull()?.edges?.firstOrNull()?.subwayLineId

        return SearchSubwayRouteQualityV3Dto.Response(
            modelVersion = "ROUTE_QUALITY_V3",
            generatedAt = nowAt.toString(),
            sourceStationId = command.sourceStationId,
            destinationStationId = command.destinationStationId,
            strategy = command.strategy,
            walkingPreference = command.walkingPreference,
            stationTimeWeekType = command.stationTimeWeekType,
            accessibilityMode = command.accessibilityMode,
            crowdingPreference = command.crowdingPreference,
            luggageMode = command.luggageMode,
            travelerContext = command.travelerContext,
            locale = command.locale,
            oneClickActions = buildOneClickActions(command, primaryLineId),
            routes = scoredRoutes,
        )
    }

    private fun evaluateRouteQuality(
        route: SearchSubwayRouteDto.Route,
        command: SearchSubwayRouteQualityV3Command,
        nowAt: OffsetDateTime,
        accessibilityProfile: SearchSubwayRouteQualityV3Dto.AccessibilityProfile,
        crowdingGuide: SearchSubwayRouteQualityV3Dto.CrowdingGuide,
        travelModeTags: List<SearchSubwayRouteQualityV3Dto.RouteTravelModeTag>,
    ): SearchSubwayRouteQualityV3Dto.Quality {
        val transferRiskScore = (100 - route.summary.transferCount * 25).coerceIn(0, 100)
        val walkingTransferPenalty = when (command.walkingPreference) {
            SearchSubwayRouteQualityV3Dto.RouteWalkingPreference.FAST -> 20
            SearchSubwayRouteQualityV3Dto.RouteWalkingPreference.LESS_STAIRS -> 30
        }
        val walkingStopPenalty = when (command.walkingPreference) {
            SearchSubwayRouteQualityV3Dto.RouteWalkingPreference.FAST -> 3
            SearchSubwayRouteQualityV3Dto.RouteWalkingPreference.LESS_STAIRS -> 2
        }
        val walkingScore = (
            100 - route.summary.transferCount * walkingTransferPenalty - route.summary.totalStops * walkingStopPenalty
            ).coerceIn(0, 100)

        val lastTrainSafety = evaluateLastTrainSafety(route, command, nowAt)
        val delayProbabilityPercent = estimateDelayProbabilityPercent(route, nowAt, crowdingGuide)
        val delayResilienceScore = (100 - delayProbabilityPercent).coerceIn(0, 100)
        val accessibilityScore = estimateAccessibilityScore(route, command, accessibilityProfile)
        val inStationDifficultyScore = estimateInStationDifficultyScore(accessibilityProfile)
        val crowdingComfortScore = estimateCrowdingComfortScore(crowdingGuide, command)
        val totalScore = (
            transferRiskScore * 20 +
                walkingScore * 15 +
                lastTrainSafety.score * 20 +
                delayResilienceScore * 15 +
                accessibilityScore * 15 +
                inStationDifficultyScore * 5 +
                crowdingComfortScore * 10
            ) / 100

        val badges = mutableListOf<SearchSubwayRouteQualityV3Dto.RouteQualityBadge>()
        val reasons = mutableListOf<String>()

        if (route.summary.transferCount >= 3) {
            badges.add(SearchSubwayRouteQualityV3Dto.RouteQualityBadge.TRANSFER_HEAVY)
            reasons.add("환승 ${route.summary.transferCount}회로 이동 실패 리스크가 있습니다.")
        } else {
            reasons.add("환승 ${route.summary.transferCount}회로 비교적 안정적인 환승 동선입니다.")
        }

        if (walkingScore <= 45) {
            badges.add(SearchSubwayRouteQualityV3Dto.RouteQualityBadge.WALKING_HEAVY)
            reasons.add("보행/계단 부담이 큰 경로입니다.")
        }

        if (command.accessibilityMode != SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.BALANCED) {
            badges.add(SearchSubwayRouteQualityV3Dto.RouteQualityBadge.ACCESSIBILITY_RECOMMENDED)
            reasons.add(accessibilityProfile.mobilityNote)
        }

        if (lastTrainSafety.isRisk) {
            badges.add(SearchSubwayRouteQualityV3Dto.RouteQualityBadge.LAST_TRAIN_RISK)
        }
        reasons.add(lastTrainSafety.reason)

        if (delayProbabilityPercent >= 45) {
            badges.add(SearchSubwayRouteQualityV3Dto.RouteQualityBadge.DELAY_RISK)
            reasons.add("지연 가능성 ${delayProbabilityPercent}%로 우회 경로 검토가 필요합니다.")
        } else {
            reasons.add("지연 가능성 ${delayProbabilityPercent}%로 상대적으로 안정적인 구간입니다.")
        }

        if (crowdingGuide.predictedLevel == SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.HIGH ||
            crowdingGuide.predictedLevel == SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.VERY_HIGH ||
            command.crowdingPreference == SearchSubwayRouteQualityV3Dto.RouteCrowdingPreference.LESS_CROWDED
        ) {
            badges.add(SearchSubwayRouteQualityV3Dto.RouteQualityBadge.CROWDING_AVOIDANCE)
            reasons.add(crowdingGuide.recommendation)
        }

        if (travelModeTags.contains(SearchSubwayRouteQualityV3Dto.RouteTravelModeTag.AIRPORT_FRIENDLY)) {
            badges.add(SearchSubwayRouteQualityV3Dto.RouteQualityBadge.AIRPORT_FRIENDLY)
            reasons.add("짐 이동을 고려한 공항 친화 경로입니다.")
        }
        if (travelModeTags.contains(SearchSubwayRouteQualityV3Dto.RouteTravelModeTag.TOURIST_FRIENDLY)) {
            badges.add(SearchSubwayRouteQualityV3Dto.RouteQualityBadge.TOURIST_FRIENDLY)
            reasons.add("관광 이동을 고려해 환승/도보 부담을 낮춘 경로입니다.")
        }

        if (!lastTrainSafety.hasData) {
            badges.add(SearchSubwayRouteQualityV3Dto.RouteQualityBadge.DATA_LIMITED)
        }

        return SearchSubwayRouteQualityV3Dto.Quality(
            totalScore = totalScore.coerceIn(0, 100),
            transferRiskScore = transferRiskScore,
            walkingScore = walkingScore,
            lastTrainSafetyScore = lastTrainSafety.score,
            delayResilienceScore = delayResilienceScore,
            accessibilityScore = accessibilityScore,
            inStationDifficultyScore = inStationDifficultyScore,
            crowdingComfortScore = crowdingComfortScore,
            delayProbabilityPercent = delayProbabilityPercent,
            confidenceLevel = resolveConfidenceLevel(lastTrainSafety),
            badges = badges.distinct(),
            reasons = reasons,
        )
    }

    private fun estimateAccessibilityScore(
        route: SearchSubwayRouteDto.Route,
        command: SearchSubwayRouteQualityV3Command,
        profile: SearchSubwayRouteQualityV3Dto.AccessibilityProfile,
    ): Int {
        var score = 100
        score -= profile.estimatedStairSections * 10
        score -= route.summary.transferCount * 6

        score += when (command.accessibilityMode) {
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.BALANCED -> 0
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.ELEVATOR_PRIORITY -> 8
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.STAIRS_MINIMIZED -> 10
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.WHEELCHAIR -> 14
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.STROLLER -> 12
        }

        score += when (command.luggageMode) {
            SearchSubwayRouteQualityV3Dto.RouteLuggageMode.NORMAL -> 0
            SearchSubwayRouteQualityV3Dto.RouteLuggageMode.HEAVY_LUGGAGE -> 6
            SearchSubwayRouteQualityV3Dto.RouteLuggageMode.AIRPORT_TRAVEL -> 8
        }

        return score.coerceIn(0, 100)
    }

    private fun estimateInStationDifficultyScore(
        profile: SearchSubwayRouteQualityV3Dto.AccessibilityProfile,
    ): Int {
        return when (profile.inStationDifficultyLevel) {
            SearchSubwayRouteQualityV3Dto.InStationDifficultyLevel.EASY -> 90
            SearchSubwayRouteQualityV3Dto.InStationDifficultyLevel.MODERATE -> 65
            SearchSubwayRouteQualityV3Dto.InStationDifficultyLevel.HARD -> 40
        }
    }

    private fun estimateCrowdingComfortScore(
        crowdingGuide: SearchSubwayRouteQualityV3Dto.CrowdingGuide,
        command: SearchSubwayRouteQualityV3Command,
    ): Int {
        var score = when (crowdingGuide.predictedLevel) {
            SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.LOW -> 90
            SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.MEDIUM -> 72
            SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.HIGH -> 52
            SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.VERY_HIGH -> 34
        }

        if (command.crowdingPreference == SearchSubwayRouteQualityV3Dto.RouteCrowdingPreference.LESS_CROWDED) {
            score += 8
        }

        return score.coerceIn(0, 100)
    }

    private fun buildAccessibilityProfile(
        route: SearchSubwayRouteDto.Route,
        command: SearchSubwayRouteQualityV3Command,
    ): SearchSubwayRouteQualityV3Dto.AccessibilityProfile {
        val baseStairSections = route.summary.transferCount * 2 + (route.summary.totalStops / 8)
        val adjustedStairSections = (baseStairSections + when (command.accessibilityMode) {
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.BALANCED -> 0
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.ELEVATOR_PRIORITY -> -1
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.STAIRS_MINIMIZED -> -2
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.WHEELCHAIR -> -3
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.STROLLER -> -2
        }).coerceAtLeast(0)

        val elevatorFriendlyTransferCount = when (command.accessibilityMode) {
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.BALANCED -> route.summary.transferCount / 2
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.ELEVATOR_PRIORITY,
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.STAIRS_MINIMIZED,
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.WHEELCHAIR,
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.STROLLER -> route.summary.transferCount
        }

        val inStationDifficultyLevel = when {
            adjustedStairSections <= 1 && route.summary.transferCount <= 1 ->
                SearchSubwayRouteQualityV3Dto.InStationDifficultyLevel.EASY
            adjustedStairSections <= 3 && route.summary.transferCount <= 2 ->
                SearchSubwayRouteQualityV3Dto.InStationDifficultyLevel.MODERATE
            else -> SearchSubwayRouteQualityV3Dto.InStationDifficultyLevel.HARD
        }

        val mobilityNote = when (command.accessibilityMode) {
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.BALANCED ->
                "기본 이동 기준으로 계산된 경로입니다."
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.ELEVATOR_PRIORITY ->
                "엘리베이터 접근성이 높은 환승 동선을 우선 반영했습니다."
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.STAIRS_MINIMIZED ->
                "계단 이동이 적은 동선을 우선 반영했습니다."
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.WHEELCHAIR ->
                "휠체어 이동 가능성을 고려해 역사 내 난이도를 낮춘 경로입니다."
            SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.STROLLER ->
                "유모차 이동에 유리한 동선을 우선 반영했습니다."
        }

        return SearchSubwayRouteQualityV3Dto.AccessibilityProfile(
            mode = command.accessibilityMode,
            elevatorFriendlyTransferCount = elevatorFriendlyTransferCount,
            estimatedStairSections = adjustedStairSections,
            inStationDifficultyLevel = inStationDifficultyLevel,
            mobilityNote = mobilityNote,
        )
    }

    private fun buildBoardingGuide(
        route: SearchSubwayRouteDto.Route,
        command: SearchSubwayRouteQualityV3Command,
    ): SearchSubwayRouteQualityV3Dto.BoardingGuide {
        val firstNode = route.nodes.firstOrNull()
        val firstEdge = route.edges.firstOrNull()
        if (firstNode == null || firstEdge == null) {
            return SearchSubwayRouteQualityV3Dto.BoardingGuide(
                primaryCarNo = "중앙",
                transferOptimizedCarNo = null,
                recommendedDoorPosition = "플랫폼 중앙",
                reason = "경로 데이터가 제한되어 일반 탑승 위치를 권장합니다.",
                confidenceLevel = SearchSubwayRouteQualityV3Dto.BoardingGuideConfidenceLevel.LOW,
            )
        }

        val recommendations = StationQuickExitRecommendationCalculator.recommend(
            stationId = firstNode.stationId,
            subwayLineId = firstEdge.subwayLineId,
            upDownType = resolveRouteUpDownType(route, command),
        )

        val primary = recommendations.firstOrNull()
        val secondary = recommendations.getOrNull(1)
        if (primary == null) {
            return SearchSubwayRouteQualityV3Dto.BoardingGuide(
                primaryCarNo = "중앙",
                transferOptimizedCarNo = null,
                recommendedDoorPosition = "플랫폼 중앙",
                reason = "추천 규칙이 없어 일반 탑승 위치를 권장합니다.",
                confidenceLevel = SearchSubwayRouteQualityV3Dto.BoardingGuideConfidenceLevel.LOW,
            )
        }

        val confidenceLevel = when (primary.confidenceLevel) {
            GetStationTimesDto.QuickExitConfidenceLevel.HIGH ->
                SearchSubwayRouteQualityV3Dto.BoardingGuideConfidenceLevel.HIGH
            GetStationTimesDto.QuickExitConfidenceLevel.MEDIUM ->
                SearchSubwayRouteQualityV3Dto.BoardingGuideConfidenceLevel.MEDIUM
            GetStationTimesDto.QuickExitConfidenceLevel.LOW ->
                SearchSubwayRouteQualityV3Dto.BoardingGuideConfidenceLevel.LOW
        }

        return SearchSubwayRouteQualityV3Dto.BoardingGuide(
            primaryCarNo = primary.carNo,
            transferOptimizedCarNo = secondary?.carNo,
            recommendedDoorPosition = primary.directionHint,
            reason = "빠른하차 추천 기준으로 약 ${primary.walkingBenefitMinutes}분 단축 가능한 위치입니다.",
            confidenceLevel = confidenceLevel,
        )
    }

    private fun resolveRouteUpDownType(
        route: SearchSubwayRouteDto.Route,
        command: SearchSubwayRouteQualityV3Command,
    ): UpDownType {
        val firstStationId = route.nodes.firstOrNull()?.stationId ?: command.sourceStationId
        val lastStationId = route.nodes.lastOrNull()?.stationId ?: command.destinationStationId
        return if (lastStationId >= firstStationId) {
            UpDownType.UP
        } else {
            UpDownType.DOWN
        }
    }

    private fun buildCrowdingGuide(
        route: SearchSubwayRouteDto.Route,
        command: SearchSubwayRouteQualityV3Command,
        nowAt: OffsetDateTime,
    ): SearchSubwayRouteQualityV3Dto.CrowdingGuide {
        var congestionIndex = if (isPeakHour(nowAt)) 55 else 34
        congestionIndex += route.summary.transferCount * 10
        congestionIndex += route.summary.totalStops / 2

        if (route.edges.any { it.subwayLineName == "2호선" || it.subwayLineName == "9호선" }) {
            congestionIndex += 13
        }
        if (command.travelerContext == SearchSubwayRouteQualityV3Dto.RouteTravelerContext.COMMUTE) {
            congestionIndex += 5
        }
        if (command.luggageMode != SearchSubwayRouteQualityV3Dto.RouteLuggageMode.NORMAL) {
            congestionIndex += 5
        }
        if (command.crowdingPreference == SearchSubwayRouteQualityV3Dto.RouteCrowdingPreference.LESS_CROWDED) {
            congestionIndex -= 8
        }

        val predictedLevel = when {
            congestionIndex >= 85 -> SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.VERY_HIGH
            congestionIndex >= 65 -> SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.HIGH
            congestionIndex >= 45 -> SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.MEDIUM
            else -> SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.LOW
        }

        val lessCrowdedCars = when (predictedLevel) {
            SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.LOW -> listOf("5-2", "6-2")
            SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.MEDIUM -> listOf("3-2", "7-2")
            SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.HIGH -> listOf("2-1", "8-1")
            SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.VERY_HIGH -> listOf("1-1", "10-1")
        }

        val recommendation = when {
            predictedLevel == SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.VERY_HIGH ->
                "혼잡이 매우 높아 후미 칸 또는 앞칸으로 분산 탑승을 권장합니다."
            predictedLevel == SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.HIGH ->
                "혼잡이 높아 추천 칸 우선 탑승을 권장합니다."
            command.crowdingPreference == SearchSubwayRouteQualityV3Dto.RouteCrowdingPreference.LESS_CROWDED ->
                "혼잡 회피 선호를 반영해 상대적으로 여유 있는 칸을 우선 제안합니다."
            else -> "현재 시간대 기준으로 무난한 혼잡 수준입니다."
        }

        return SearchSubwayRouteQualityV3Dto.CrowdingGuide(
            predictedLevel = predictedLevel,
            lessCrowdedCars = lessCrowdedCars,
            recommendation = recommendation,
            basedOn = "historical+line-heuristic",
        )
    }

    private fun buildNearbyEssentials(
        route: SearchSubwayRouteDto.Route,
    ): SearchSubwayRouteQualityV3Dto.NearbyEssentials {
        val targetNode = route.nodes.lastOrNull() ?: route.nodes.firstOrNull()
        if (targetNode == null) {
            return SearchSubwayRouteQualityV3Dto.NearbyEssentials(
                stationId = -1,
                stationName = "정보 없음",
                items = emptyList(),
            )
        }

        val lineId = route.edges.lastOrNull()?.subwayLineId
            ?: route.edges.firstOrNull()?.subwayLineId
            ?: 1L

        val places = StationNearbyPlacesRecommendationGenerator.generate(
            stationId = targetNode.stationId,
            subwayLineId = lineId,
            exitNo = null,
            limit = 4,
        )

        return SearchSubwayRouteQualityV3Dto.NearbyEssentials(
            stationId = targetNode.stationId,
            stationName = targetNode.stationName,
            items = places.map { place ->
                SearchSubwayRouteQualityV3Dto.NearbyEssentialItem(
                    essentialType = SearchSubwayRouteQualityV3Dto.NearbyEssentialType.valueOf(
                        place.essentialType.name
                    ),
                    name = place.name,
                    walkingMinutes = place.walkingMinutes,
                    openNow = place.openNow,
                    reliabilityScore = place.reliabilityScore,
                    reliabilityReason = place.reliabilityReason,
                )
            },
        )
    }

    private fun buildTravelModeTags(
        route: SearchSubwayRouteDto.Route,
        command: SearchSubwayRouteQualityV3Command,
    ): List<SearchSubwayRouteQualityV3Dto.RouteTravelModeTag> {
        val tags = linkedSetOf<SearchSubwayRouteQualityV3Dto.RouteTravelModeTag>()

        if (command.luggageMode == SearchSubwayRouteQualityV3Dto.RouteLuggageMode.AIRPORT_TRAVEL ||
            route.edges.any { it.subwayLineName.contains("공항") }
        ) {
            tags.add(SearchSubwayRouteQualityV3Dto.RouteTravelModeTag.AIRPORT_FRIENDLY)
        }

        if (command.travelerContext == SearchSubwayRouteQualityV3Dto.RouteTravelerContext.TRAVEL) {
            tags.add(SearchSubwayRouteQualityV3Dto.RouteTravelModeTag.TOURIST_FRIENDLY)
        }

        if (command.accessibilityMode != SearchSubwayRouteQualityV3Dto.RouteAccessibilityMode.BALANCED) {
            tags.add(SearchSubwayRouteQualityV3Dto.RouteTravelModeTag.ACCESSIBILITY_PRIORITY)
        }

        if (command.crowdingPreference == SearchSubwayRouteQualityV3Dto.RouteCrowdingPreference.LESS_CROWDED) {
            tags.add(SearchSubwayRouteQualityV3Dto.RouteTravelModeTag.LESS_CROWDED_RECOMMENDED)
        }

        return tags.toList()
    }

    private data class OneClickActionText(
        val emergencyTitle: String,
        val emergencyDescription: String,
        val lostTitle: String,
        val lostDescription: String,
        val complaintTitle: String,
        val complaintDescription: String,
        val copyTitle: String,
        val copyDescription: String,
        val emergencyPhrase: String,
    )

    private fun buildOneClickActions(
        command: SearchSubwayRouteQualityV3Command,
        primaryLineId: Long?,
    ): List<SearchSubwayRouteQualityV3Dto.OneClickAction> {
        val text = resolveOneClickActionText(command.locale)
        val lineQuery = primaryLineId?.let { "&subwayLineId=$it" } ?: ""
        val prefillQuery = "prefill=1&templateLocale=${command.locale}&stationId=${command.sourceStationId}$lineQuery"

        return listOf(
            SearchSubwayRouteQualityV3Dto.OneClickAction(
                actionType = SearchSubwayRouteQualityV3Dto.OneClickActionType.CALL_EMERGENCY_112,
                title = text.emergencyTitle,
                description = text.emergencyDescription,
                deepLink = "tel:112",
                payloadTemplate = null,
            ),
            SearchSubwayRouteQualityV3Dto.OneClickAction(
                actionType = SearchSubwayRouteQualityV3Dto.OneClickActionType.OPEN_LOST_REPORT,
                title = text.lostTitle,
                description = text.lostDescription,
                deepLink = "/lost-found/new?$prefillQuery",
                payloadTemplate = null,
            ),
            SearchSubwayRouteQualityV3Dto.OneClickAction(
                actionType = SearchSubwayRouteQualityV3Dto.OneClickActionType.OPEN_COMPLAINT_REPORT,
                title = text.complaintTitle,
                description = text.complaintDescription,
                deepLink = "/complaint/new?$prefillQuery",
                payloadTemplate = null,
            ),
            SearchSubwayRouteQualityV3Dto.OneClickAction(
                actionType = SearchSubwayRouteQualityV3Dto.OneClickActionType.COPY_EMERGENCY_PHRASE,
                title = text.copyTitle,
                description = text.copyDescription,
                deepLink = "copy://emergency-phrase",
                payloadTemplate = text.emergencyPhrase,
            ),
        )
    }

    private fun resolveOneClickActionText(locale: String): OneClickActionText {
        return when (locale) {
            "en" -> OneClickActionText(
                emergencyTitle = "Call 112",
                emergencyDescription = "Emergency call to police and station support.",
                lostTitle = "Lost item report",
                lostDescription = "Open lost-item report with station prefilled.",
                complaintTitle = "Service complaint",
                complaintDescription = "Open complaint form with station prefilled.",
                copyTitle = "Copy emergency phrase",
                copyDescription = "Copy a ready-to-use emergency sentence.",
                emergencyPhrase = "There is an urgent incident in the subway. Please send help immediately.",
            )
            "th" -> OneClickActionText(
                emergencyTitle = "โทร 112",
                emergencyDescription = "โทรฉุกเฉินถึงตำรวจ/เจ้าหน้าที่ทันที",
                lostTitle = "แจ้งของหาย",
                lostDescription = "เปิดฟอร์มแจ้งของหายพร้อมข้อมูลสถานี",
                complaintTitle = "แจ้งปัญหาการใช้งาน",
                complaintDescription = "เปิดฟอร์มร้องเรียนพร้อมข้อมูลสถานี",
                copyTitle = "คัดลอกประโยคฉุกเฉิน",
                copyDescription = "คัดลอกข้อความพร้อมใช้สำหรับเหตุฉุกเฉิน",
                emergencyPhrase = "เกิดเหตุฉุกเฉินในรถไฟใต้ดิน กรุณาส่งเจ้าหน้าที่ด่วน",
            )
            "cn" -> OneClickActionText(
                emergencyTitle = "拨打112",
                emergencyDescription = "一键拨打紧急电话并联系车站工作人员",
                lostTitle = "失物申报",
                lostDescription = "打开已预填车站信息的失物表单",
                complaintTitle = "服务投诉",
                complaintDescription = "打开已预填车站信息的投诉表单",
                copyTitle = "复制紧急短语",
                copyDescription = "复制可直接使用的紧急求助语句",
                emergencyPhrase = "地铁发生紧急情况，请立即派人支援。",
            )
            else -> OneClickActionText(
                emergencyTitle = "112 긴급전화",
                emergencyDescription = "긴급 상황 발생 시 즉시 112로 연결합니다.",
                lostTitle = "분실 신고 바로가기",
                lostDescription = "역/노선 정보가 포함된 분실물 신고 화면으로 이동합니다.",
                complaintTitle = "민원 신고 바로가기",
                complaintDescription = "역/노선 정보가 포함된 민원 접수 화면으로 이동합니다.",
                copyTitle = "긴급 문구 복사",
                copyDescription = "역무원/주변인에게 보여줄 긴급 문구를 복사합니다.",
                emergencyPhrase = "지하철에서 긴급 상황이 발생했습니다. 즉시 도움을 요청합니다.",
            )
        }
    }

    private fun resolveConfidenceLevel(
        lastTrainSafety: LastTrainSafetyEvaluation,
    ): SearchSubwayRouteQualityV3Dto.RouteQualityConfidenceLevel {
        return when {
            !lastTrainSafety.hasData -> SearchSubwayRouteQualityV3Dto.RouteQualityConfidenceLevel.LOW
            lastTrainSafety.isRisk -> SearchSubwayRouteQualityV3Dto.RouteQualityConfidenceLevel.MEDIUM
            else -> SearchSubwayRouteQualityV3Dto.RouteQualityConfidenceLevel.HIGH
        }
    }

    private fun evaluateLastTrainSafety(
        route: SearchSubwayRouteDto.Route,
        command: SearchSubwayRouteQualityV3Command,
        nowAt: OffsetDateTime,
    ): LastTrainSafetyEvaluation {
        val firstLineId = route.edges.firstOrNull()?.subwayLineId
            ?: return LastTrainSafetyEvaluation(
                score = 55,
                confidenceLevel = SearchSubwayRouteQualityV3Dto.RouteQualityConfidenceLevel.LOW,
                reason = "단일 역 경로로 막차 안전도 산정 데이터가 부족합니다.",
                isRisk = false,
                hasData = false,
            )

        val allTimes = runCatching {
            val upTimes = loadStationTimesForLastTrainRisk(
                GetStationTimesCommand(
                    stationId = command.sourceStationId,
                    subwayLineId = firstLineId,
                    upDownType = UpDownType.UP,
                    stationTimeWeekType = command.stationTimeWeekType,
                )
            )
            val downTimes = loadStationTimesForLastTrainRisk(
                GetStationTimesCommand(
                    stationId = command.sourceStationId,
                    subwayLineId = firstLineId,
                    upDownType = UpDownType.DOWN,
                    stationTimeWeekType = command.stationTimeWeekType,
                )
            )
            upTimes + downTimes
        }.getOrElse {
            emptyList()
        }

        val lastDepartureTime = allTimes
            .maxByOrNull { it.departureTime }
            ?.departureTime
            ?: return LastTrainSafetyEvaluation(
                score = 55,
                confidenceLevel = SearchSubwayRouteQualityV3Dto.RouteQualityConfidenceLevel.LOW,
                reason = "막차 데이터가 부족해 안전도를 중립 점수로 반영했습니다.",
                isRisk = false,
                hasData = false,
            )

        val minutesToLastTrain = resolveMinutesUntilDeparture(nowAt, lastDepartureTime)
            ?: return LastTrainSafetyEvaluation(
                score = 50,
                confidenceLevel = SearchSubwayRouteQualityV3Dto.RouteQualityConfidenceLevel.LOW,
                reason = "막차 시각 형식이 불완전하여 안전도 정확도가 낮습니다.",
                isRisk = false,
                hasData = false,
            )

        val requiredMinutes = route.summary.estimatedMinutes + 8
        val margin = minutesToLastTrain - requiredMinutes

        return when {
            margin >= 45 -> LastTrainSafetyEvaluation(
                score = 95,
                confidenceLevel = SearchSubwayRouteQualityV3Dto.RouteQualityConfidenceLevel.HIGH,
                reason = "막차 대비 ${margin}분 여유가 있어 안전도가 높습니다.",
                isRisk = false,
                hasData = true,
            )

            margin >= 25 -> LastTrainSafetyEvaluation(
                score = 80,
                confidenceLevel = SearchSubwayRouteQualityV3Dto.RouteQualityConfidenceLevel.HIGH,
                reason = "막차 대비 ${margin}분 여유가 있습니다.",
                isRisk = false,
                hasData = true,
            )

            margin >= 10 -> LastTrainSafetyEvaluation(
                score = 65,
                confidenceLevel = SearchSubwayRouteQualityV3Dto.RouteQualityConfidenceLevel.MEDIUM,
                reason = "막차 여유가 ${margin}분으로 촉박할 수 있습니다.",
                isRisk = false,
                hasData = true,
            )

            margin >= 0 -> LastTrainSafetyEvaluation(
                score = 45,
                confidenceLevel = SearchSubwayRouteQualityV3Dto.RouteQualityConfidenceLevel.MEDIUM,
                reason = "막차 여유가 ${margin}분으로 낮아 위험 구간입니다.",
                isRisk = true,
                hasData = true,
            )

            else -> LastTrainSafetyEvaluation(
                score = 20,
                confidenceLevel = SearchSubwayRouteQualityV3Dto.RouteQualityConfidenceLevel.MEDIUM,
                reason = "예상 소요가 막차보다 ${-margin}분 늦어 막차 위험이 큽니다.",
                isRisk = true,
                hasData = true,
            )
        }
    }

    private fun resolveMinutesUntilDeparture(nowAt: OffsetDateTime, departureTime: String): Int? {
        val parsed = runCatching { LocalTime.parse(departureTime) }.getOrNull() ?: return null
        var departureAt = nowAt
            .withHour(parsed.hour)
            .withMinute(parsed.minute)
            .withSecond(parsed.second)
            .withNano(0)

        if (departureAt.isBefore(nowAt)) {
            departureAt = departureAt.plusDays(1)
        }
        return ((departureAt.toEpochSecond() - nowAt.toEpochSecond()) / 60).toInt()
    }

    private fun estimateDelayProbabilityPercent(
        route: SearchSubwayRouteDto.Route,
        nowAt: OffsetDateTime,
        crowdingGuide: SearchSubwayRouteQualityV3Dto.CrowdingGuide,
    ): Int {
        var probability = 10
        probability += route.summary.transferCount * 14
        probability += (route.summary.totalStops / 2)

        if (isPeakHour(nowAt)) {
            probability += 12
        }
        if (route.summary.estimatedMinutes >= 45) {
            probability += 8
        }
        if (route.summary.estimatedMinutes >= 60) {
            probability += 8
        }
        if (route.edges.any { it.subwayLineName == "2호선" || it.subwayLineName == "9호선" }) {
            probability += 10
        }
        probability += when (crowdingGuide.predictedLevel) {
            SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.LOW -> -5
            SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.MEDIUM -> 0
            SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.HIGH -> 6
            SearchSubwayRouteQualityV3Dto.RouteCrowdingLevel.VERY_HIGH -> 12
        }

        return probability.coerceIn(5, 95)
    }

    private fun isPeakHour(nowAt: OffsetDateTime): Boolean {
        val hour = nowAt.hour
        return (hour in 7..9) || (hour in 18..20)
    }

    private fun loadStationTimes(command: GetStationTimesCommand): List<GetStationTimesDto.StationTimes> {
        val subwayLineStation = subwayLineStationReader.findBySubwayLineIdAndStationId(command.subwayLineId, command.stationId)
        val stationCode = subwayLineStation.stationCode ?: throw BusinessException(ResponseCode.NOT_EXIST_PUBLIC_STATION_CODE)

        val cacheCommand = command.toCacheCommand(stationCode)
        stationTimesCacheUtils.getStationTimesByCache(cacheCommand)?.let {
            return it
        }

        val response = seoulTrainClient.getStationTimesByApi(command.toRequest(stationCode))
        if (response.isFail()) {
            throw BusinessException(ResponseCode.INVALID_STATION_TIMES_API_RESPONSE)
        }

        val stationTimes = response.toStationTimes()
        stationTimesCacheUtils.setStationTimesCache(cacheCommand, stationTimes)
        return stationTimes
    }

    /**
     * Redis 통신 오류에 대한 FallBack 메서드
     */
    fun fallbackOnExternalStationTimesApiGet(
        command: GetStationTimesCommand, e: RedisConnectionFailureException
    ): GetStationTimesDto.Response {
        logger.error("can't connect to redis server")
        throw CommonException(ResponseCode.FAILED_TO_CONNECT_TO_REDIS, e)
    }

    /**
     * 열차 도착 정보 API 오류에 대한 FallBack 메서드
     */
    fun fallbackOnExternalStationTimesApiGet(
        command: GetStationTimesCommand, e : CallNotPermittedException
    ): GetStationTimesDto.Response {
        logger.error("circuit breaker opened for external station times api")
        throw CommonException(ResponseCode.FAILED_TO_GET_STATION_TIMES, e)
    }

    /**
     * Redis 통신 오류에 대한 첫차/막차 요약 FallBack 메서드
     */
    fun fallbackOnExternalStationTimesSummaryApiGet(
        command: GetStationTimesSummaryCommand, e: RedisConnectionFailureException
    ): GetStationTimesDto.SummaryResponse {
        logger.error("can't connect to redis server")
        throw CommonException(ResponseCode.FAILED_TO_CONNECT_TO_REDIS, e)
    }

    /**
     * 열차 도착 정보 API 오류에 대한 첫차/막차 요약 FallBack 메서드
     */
    fun fallbackOnExternalStationTimesSummaryApiGet(
        command: GetStationTimesSummaryCommand, e : CallNotPermittedException
    ): GetStationTimesDto.SummaryResponse {
        logger.error("circuit breaker opened for external station times summary api")
        throw CommonException(ResponseCode.FAILED_TO_GET_STATION_TIMES, e)
    }

    /**
     * Redis 통신 오류에 대한 막차 리스크 FallBack 메서드
     */
    fun fallbackOnExternalStationTimesLastTrainRiskApiGet(
        command: GetStationLastTrainRiskCommand, e: RedisConnectionFailureException
    ): GetStationTimesDto.LastTrainRiskResponse {
        logger.error("can't connect to redis server")
        throw CommonException(ResponseCode.FAILED_TO_CONNECT_TO_REDIS, e)
    }

    /**
     * 열차 도착 정보 API 오류에 대한 막차 리스크 FallBack 메서드
     */
    fun fallbackOnExternalStationTimesLastTrainRiskApiGet(
        command: GetStationLastTrainRiskCommand, e : CallNotPermittedException
    ): GetStationTimesDto.LastTrainRiskResponse {
        logger.error("circuit breaker opened for external station times last-train-risk api")
        throw CommonException(ResponseCode.FAILED_TO_GET_STATION_TIMES, e)
    }
}
