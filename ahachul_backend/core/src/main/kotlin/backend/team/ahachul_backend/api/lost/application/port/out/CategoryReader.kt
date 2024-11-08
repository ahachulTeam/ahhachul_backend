package backend.team.ahachul_backend.api.lost.application.port.out

import backend.team.ahachul_backend.api.lost.domain.entity.CategoryEntity

interface CategoryReader {

    fun getCategory(id: Long): CategoryEntity

    fun getCategoryByName(name: String): CategoryEntity

    fun getCategories(): List<CategoryEntity>
}
