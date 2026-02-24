package backend.team.ahachul_backend.api.complaint.adapter.web.`in`

import backend.team.ahachul_backend.api.article.application.port.`in`.ArticleLikeUseCase
import backend.team.ahachul_backend.api.article.domain.model.ArticleType
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ComplaintPostLikeController(
    private val articleLikeUseCase: ArticleLikeUseCase
) {

    @Authentication
    @PostMapping("/v1/complaint-posts/{postId}/like")
    fun like(@PathVariable postId: Long): CommonResponse<*> {
        articleLikeUseCase.like(ArticleType.COMPLAINT, postId)
        return CommonResponse.success()
    }

    @Authentication
    @DeleteMapping("/v1/complaint-posts/{postId}/like")
    fun unlike(@PathVariable postId: Long): CommonResponse<*> {
        articleLikeUseCase.unlike(ArticleType.COMPLAINT, postId)
        return CommonResponse.success()
    }
}
