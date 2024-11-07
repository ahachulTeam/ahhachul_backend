package backend.team.ahachul_backend.schedule.job

import backend.team.ahachul_backend.common.client.RedisClient
import backend.team.ahachul_backend.common.constant.CommonConstant.Companion.HASHTAG_LOG_DATETIME_FORMATTER
import backend.team.ahachul_backend.common.constant.CommonConstant.Companion.HASHTAG_REDIS_KEY
import backend.team.ahachul_backend.common.logging.Logger
import backend.team.ahachul_backend.common.utils.LogAnalyzeUtils
import org.quartz.JobExecutionContext
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component
import java.io.FileNotFoundException
import java.io.RandomAccessFile
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Component
class RankHashTagJob(
    private val redisClient: RedisClient
): QuartzJobBean() {

    private val logger = Logger(javaClass)

    override fun executeInternal(context: JobExecutionContext) {
        val jobDataMap = context.jobDetail.jobDataMap
        val fileReadPath = jobDataMap.getString("FILE_READ_PATH")
        val sortedHashtagCountMap = readHashTagLogFile(fileReadPath)
        setCache(sortedHashtagCountMap)
    }

    /**
     * 배치성 메서드
     * 일정 주기마다 파일에서 정보를 가져와서 순위를 매긴다.
     */
    fun readHashTagLogFile(fileUrl: String): List<String> {
        val endTime = LocalDateTime.now()
        val startTime = endTime.minusMinutes(5)
        val map = mutableMapOf<String, Int>()

        try {
            RandomAccessFile(fileUrl, "r").use {
                val fileLength = it.length()
                var pointer = fileLength - 2

                while (pointer > START_OF_FILE) {
                    it.seek(pointer)
                    while ((it.read().toChar() != NEW_LINE)) {        // 각 줄 앞으로 포인터 이동
                        pointer -= 1
                        if (pointer < START_OF_FILE) {                // 파일의 처음에 도달한 경우 포인터 보정
                            it.seek(pointer + 1)
                            break
                        }
                        it.seek(pointer)
                    }

                    val logStr = String(it.readLine().toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
                    val (timestamp, name) = LogAnalyzeUtils.extractArgsFromLogStr(logStr, HASHTAG_PATTERN, listOf("timestamp", "name"))
                    val date = LocalDateTime.parse(timestamp, HASHTAG_LOG_DATETIME_FORMATTER)

                    if (date.isBefore(endTime) && date.isAfter(startTime)) {
                        logger.debug("read data between $startTime ~ $endTime : $timestamp")
                        if (!map.containsKey(name)) {
                            map[name] = 1
                        } else {
                            map[name] = map[name]!! + 1
                        }
                    } else if (date.isBefore(startTime)) {
                        logger.debug("terminate extracting data between $startTime ~ $endTime : $timestamp")
                        break
                    }
                    pointer -= 1
                }
            }
        } catch (ex: FileNotFoundException) {
            logger.error("invalid file to read : $fileUrl")
        }

        return sortByTop(map)
    }

    private fun sortByTop(hashtagCountMap: Map<String, Int>): List<String> {
        return hashtagCountMap.toList()
            .sortedByDescending { it.second }
            .map { it.first }
    }

    private fun setCache(sortedResult: List<String>) {
        redisClient.set(HASHTAG_REDIS_KEY, sortedResult, 5, TimeUnit.MINUTES)
    }

    companion object {
        const val NEW_LINE = '\n'
        const val START_OF_FILE = 0L
        const val HASHTAG_PATTERN = "(?<timestamp>.*) \\[(?<logger>.*)\\] userId = (?<userId>.*) hashtag = (?<name>.*)"
    }
}
