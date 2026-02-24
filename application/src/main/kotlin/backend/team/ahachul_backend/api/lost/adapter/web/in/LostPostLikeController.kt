package backend.team.ahachul_backend.api.lost.adapter.web.`in`

import backend.team.ahachul_backend.api.article.application.port.`in`.ArticleLikeUseCase
import backend.team.ahachul_backend.api.article.domain.model.ArticleType
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LostPostLikeController(
    private val articleLikeUseCase: ArticleLikeUseCase
) {

    @Authentication
    @PostMapping("/v1/lost-posts/{postId}/like")
    fun like(@PathVariable postId: Long): CommonResponse<*> {
        articleLikeUseCase.like(ArticleType.LOST, postId)
        return CommonResponse.success()
    }

    @Authentication
    @DeleteMapping("/v1/lost-posts/{postId}/like")
    fun unlike(@PathVariable postId: Long): CommonResponse<*> {
        articleLikeUseCase.unlike(ArticleType.LOST, postId)
        return CommonResponse.success()
    }
}
