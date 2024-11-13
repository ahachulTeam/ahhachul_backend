package backend.team.ahachul_backend.schedule.job

import backend.team.ahachul_backend.api.lost.application.port.out.LostPostWriter
import backend.team.ahachul_backend.api.lost.domain.entity.CategoryEntity
import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.logging.Logger
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.storage.CategoryStorage
import backend.team.ahachul_backend.common.storage.SubwayLineStorage
import backend.team.ahachul_backend.api.lost.domain.model.Lost112Data
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import org.quartz.JobExecutionContext
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException


@Component
class UpdateLostDataJob(
    private val lostPostWriter: LostPostWriter,
    private val subwayLineStorage: SubwayLineStorage,
    private val categoryStorage: CategoryStorage,
    private val transactionManager: PlatformTransactionManager
): QuartzJobBean() {

    private val logger = Logger(javaClass)

    override fun executeInternal(context: JobExecutionContext) {
        val jobDataMap = context.jobDetail.jobDataMap
        val fileReadPath = jobDataMap.getString("FILE_READ_PATH")
        val transactionStatus = transactionManager.getTransaction(DefaultTransactionDefinition())
        try {
            readFileDataAndSave(fileReadPath)
            transactionManager.commit(transactionStatus)
        } catch (ex: Exception) {
            transactionManager.rollback(transactionStatus)
            throw CommonException(ResponseCode.FILE_READ_FAILED, ex)
        }
    }

    fun readFileDataAndSave(readPath: String) {
        val gson = GsonBuilder().setLenient().create()
        var totalItems = mutableListOf<Lost112Data>()

        try {
            BufferedReader(FileReader(readPath)).use {
                var jsonStr = ""
                var isRecord = false
                var line: String?
                var totalReadCount = 0

                while (true) {
                    line = it.readLine()
                    if (totalReadCount == BATCH_SIZE || line == null) {
                        saveLostPosts(totalItems)
                        totalReadCount = 0
                        totalItems = mutableListOf()
                        if (line == null) break
                        continue
                    }

                    if (line.trim().startsWith("{")) {
                        jsonStr = "{"
                        isRecord = true
                        continue
                    }

                    if (!isRecord) continue
                    jsonStr += line

                    if (line.trim().startsWith("}")) {
                        if (jsonStr[jsonStr.length - 1] == ',') {
                            jsonStr = jsonStr.substring(0, jsonStr.length - 1)
                        }
                        totalItems.add(gson.fromJson(jsonStr, Lost112Data::class.java))
                        totalReadCount += 1
                        isRecord = false
                    }
                }
            }
        } catch (e: JsonSyntaxException) {
            logger.error("invalid json format : $readPath", ResponseCode.INTERNAL_SERVER_ERROR, e)
        } catch (e: IOException) {
            logger.error("i/o exception occurred from file : $readPath", ResponseCode.INTERNAL_SERVER_ERROR, e)
        } catch (e: Exception) {
            logger.error("failed to read lost112 crawling file : $readPath", ResponseCode.INTERNAL_SERVER_ERROR, e)
        }
    }

    private fun saveLostPosts(response: List<Lost112Data>) {
        val lostPosts = mutableListOf<LostPostEntity>()
        response.forEach {
            val subwayLine = getSubwayLineEntity(it.receiptPlace)
            val category = getCategory(it.categoryName)
            val lostPost = LostPostEntity.ofLost112(it, subwayLine, category, it.imageUrl)
            lostPosts.add(lostPost)
        }
        lostPostWriter.saveAll(lostPosts)
    }

    private fun getSubwayLineEntity(receivedPlace: String): SubwayLineEntity? {
        val subwayLineName = subwayLineStorage.extractSubWayLine(receivedPlace)
        return subwayLineStorage.getSubwayLineEntityByName(subwayLineName)
    }

    private fun getCategory(categoryName: String): CategoryEntity? {
        val primaryCategoryName = categoryStorage.extractPrimaryCategory(categoryName)
        return categoryStorage.getCategoryByName(primaryCategoryName)
    }

    companion object {
        const val BATCH_SIZE = 100
    }
}
