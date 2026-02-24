package backend.team.ahachul_backend.api.complaint.adapter.web.`in`

import backend.team.ahachul_backend.api.article.application.port.`in`.ArticleBookmarkUseCase
import backend.team.ahachul_backend.api.article.domain.model.ArticleType
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ComplaintPostBookmarkController(
    private val articleBookmarkUseCase: ArticleBookmarkUseCase
) {

    @Authentication
    @PostMapping("/v1/complaint-posts/{postId}/bookmark")
    fun bookmark(@PathVariable postId: Long): CommonResponse<*> {
        articleBookmarkUseCase.bookmark(ArticleType.COMPLAINT, postId)
        return CommonResponse.success()
    }

    @Authentication
    @DeleteMapping("/v1/complaint-posts/{postId}/bookmark")
    fun unbookmark(@PathVariable postId: Long): CommonResponse<*> {
        articleBookmarkUseCase.unbookmark(ArticleType.COMPLAINT, postId)
        return CommonResponse.success()
    }
}
