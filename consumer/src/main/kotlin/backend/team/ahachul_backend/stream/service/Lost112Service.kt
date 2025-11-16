package backend.team.ahachul_backend.stream.service

import backend.team.ahachul_backend.api.lost.application.port.out.LostPostWriter
import backend.team.ahachul_backend.api.lost.domain.entity.CategoryEntity
import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import backend.team.ahachul_backend.api.lost.domain.model.Lost112Data
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
import backend.team.ahachul_backend.common.storage.CategoryStorage
import backend.team.ahachul_backend.common.storage.SubwayLineStorage
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class Lost112Service(
    private val objectMapper: ObjectMapper,
    private val lostPostWriter: LostPostWriter,
    private val subwayLineStorage: SubwayLineStorage,
    private val categoryStorage: CategoryStorage
) {

    fun convertAndSaveLostPost(lost112MapData: Map<String, Any>) {
        val jsonStr = objectMapper.writeValueAsString(lost112MapData)
        val lost112Data = objectMapper.readValue(jsonStr, Lost112Data::class.java)
        val subwayLine = getSubwayLineEntity(lost112Data.receiptPlace)
        val category = getCategory(lost112Data.categoryName)
        val lostPost = LostPostEntity.ofLost112(lost112Data, subwayLine, category, lost112Data.imageUrl)
        lostPostWriter.save(lostPost)
    }

    private fun getSubwayLineEntity(receivedPlace: String): SubwayLineEntity? {
        val subwayLineName = subwayLineStorage.extractSubWayLine(receivedPlace)
        return subwayLineStorage.getSubwayLineEntityByName(subwayLineName)
    }

    private fun getCategory(categoryName: String): CategoryEntity? {
        val primaryCategoryName = categoryStorage.extractPrimaryCategory(categoryName)
        return categoryStorage.getCategoryByName(primaryCategoryName)
    }
}
