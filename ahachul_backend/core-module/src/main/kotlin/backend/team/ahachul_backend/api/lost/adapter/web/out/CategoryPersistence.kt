package backend.team.ahachul_backend.api.lost.adapter.web.out

import backend.team.ahachul_backend.api.lost.application.port.out.CategoryReader
import backend.team.ahachul_backend.api.lost.domain.entity.CategoryEntity
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.stereotype.Component

@Component
class CategoryPersistence(
    private val categoryRepository: CategoryRepository
): CategoryReader {

    override fun getCategory(id: Long): CategoryEntity {
        return categoryRepository.findById(id)
            .orElseThrow { throw AdapterException(ResponseCode.INVALID_DOMAIN) }
    }

    override fun getCategoryByName(name: String): CategoryEntity {
        return categoryRepository.findByName(name)
            ?: throw AdapterException(ResponseCode.INVALID_DOMAIN)
    }

    override fun getCategories(): List<CategoryEntity> {
        return categoryRepository.findAll()
    }
}
