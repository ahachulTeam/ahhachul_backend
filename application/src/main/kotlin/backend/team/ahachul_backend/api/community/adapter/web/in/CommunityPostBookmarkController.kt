package backend.team.ahachul_backend.api.community.adapter.web.`in`

import backend.team.ahachul_backend.api.article.application.port.`in`.ArticleBookmarkUseCase
import backend.team.ahachul_backend.api.article.domain.model.ArticleType
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CommunityPostBookmarkController(
    private val articleBookmarkUseCase: ArticleBookmarkUseCase
) {

    @Authentication
    @PostMapping("/v1/community-posts/{postId}/bookmark")
    fun bookmark(@PathVariable postId: Long): CommonResponse<*> {
        articleBookmarkUseCase.bookmark(ArticleType.COMMUNITY, postId)
        return CommonResponse.success()
    }

    @Authentication
    @DeleteMapping("/v1/community-posts/{postId}/bookmark")
    fun unbookmark(@PathVariable postId: Long): CommonResponse<*> {
        articleBookmarkUseCase.unbookmark(ArticleType.COMMUNITY, postId)
        return CommonResponse.success()
    }
}
