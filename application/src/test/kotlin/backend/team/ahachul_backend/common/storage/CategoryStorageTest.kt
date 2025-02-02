package backend.team.ahachul_backend.common.storage

import backend.team.ahachul_backend.config.controller.CommonServiceTestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CategoryStorageTest(
    @Autowired private val categoryStorage: CategoryStorage
): CommonServiceTestConfig() {

    @Test
    @DisplayName("주요 카테고리만 추출해서 반환한다.")
    fun extractSubwayLine() {
        // given
        val categoryName = "가방 > 기타가방"

        // when
        val result = categoryStorage.extractPrimaryCategory(categoryName)

        // then
        assertThat(result).isEqualTo("가방")
    }

    @Test
    @DisplayName("시드 카테고리와 일치하는 데이터가 없다면 null을 반환한다.")
    fun getSubwayLineNull() {
        // given
        val categoryName = "음식 > 과자"

        // when
        val result = categoryStorage.getCategoryByName(categoryName)

        // then
        assertThat(result).isEqualTo(null)
    }
}
