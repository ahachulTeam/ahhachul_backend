package backend.team.ahachul_backend.api.community.application.port.`in`

interface CommunityPostLikeUseCase {

    fun like(postId: Long)

    fun notLike(postId: Long)

    fun hate(postId: Long)

    fun notHate(postId: Long)
}