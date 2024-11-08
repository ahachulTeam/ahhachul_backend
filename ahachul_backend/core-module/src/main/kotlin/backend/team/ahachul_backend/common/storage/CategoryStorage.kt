package backend.team.ahachul_backend.common.storage

import backend.team.ahachul_backend.api.lost.application.port.out.CategoryReader
import backend.team.ahachul_backend.api.lost.domain.entity.CategoryEntity
import org.springframework.stereotype.Component

@Component
class CategoryStorage(
    private var categories: List<CategoryEntity>,
    private val categoryReader: CategoryReader
) {

    init {
        categories = categoryReader.getCategories()
    }

    fun extractPrimaryCategory(categoryName: String): String{
        val idx = categoryName.indexOf(">")
        val primaryCategory = categoryName.substring(0, idx)
        return primaryCategory.trim()
    }

    fun getCategoryByName(categoryName: String): CategoryEntity? {
        return runCatching {
            categories.first { category -> category.name == categoryName }
        }.getOrNull()
    }
}
