package backend.team.ahachul_backend.api.article.application.service

import backend.team.ahachul_backend.api.article.application.port.`in`.ArticleLikeUseCase
import backend.team.ahachul_backend.api.article.application.port.out.ArticleLikeReader
import backend.team.ahachul_backend.api.article.application.port.out.ArticleLikeWriter
import backend.team.ahachul_backend.api.article.domain.entity.ArticleLikeEntity
import backend.team.ahachul_backend.api.article.domain.model.ArticleType
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostReader
import backend.team.ahachul_backend.api.community.domain.model.CommunityPostType
import backend.team.ahachul_backend.api.complaint.application.port.out.ComplaintPostReader
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintPostType
import backend.team.ahachul_backend.api.lost.application.port.out.LostPostReader
import backend.team.ahachul_backend.api.lost.domain.model.LostPostType
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.RequestUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ArticleLikeService(
    private val articleLikeReader: ArticleLikeReader,
    private val articleLikeWriter: ArticleLikeWriter,
    private val memberReader: MemberReader,
    private val communityPostReader: CommunityPostReader,
    private val complaintPostReader: ComplaintPostReader,
    private val lostPostReader: LostPostReader,
) : ArticleLikeUseCase {

    @Transactional
    override fun like(articleType: ArticleType, articleId: Long) {
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong()
        validateTarget(articleType, articleId)

        if (articleLikeReader.exists(articleType, articleId, memberId)) {
            throw CommonException(ResponseCode.ALREADY_LIKED_POST)
        }

        articleLikeWriter.save(
            ArticleLikeEntity.of(
                articleType = articleType,
                articleId = articleId,
                member = memberReader.getMember(memberId)
            )
        )
    }

    @Transactional
    override fun unlike(articleType: ArticleType, articleId: Long) {
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong()
        if (!articleLikeReader.exists(articleType, articleId, memberId)) {
            throw CommonException(ResponseCode.BAD_REQUEST)
        }

        articleLikeWriter.delete(articleType, articleId, memberId)
    }

    private fun validateTarget(articleType: ArticleType, articleId: Long) {
        when (articleType) {
            ArticleType.COMMUNITY -> {
                val post = communityPostReader.getCommunityPost(articleId)
                if (post.status == CommunityPostType.DELETED) {
                    throw CommonException(ResponseCode.POST_NOT_FOUND)
                }
            }

            ArticleType.COMPLAINT -> {
                val post = complaintPostReader.getComplaintPost(articleId)
                if (post.status == ComplaintPostType.DELETED) {
                    throw CommonException(ResponseCode.POST_NOT_FOUND)
                }
            }

            ArticleType.LOST -> {
                val post = lostPostReader.getLostPost(articleId)
                if (post.type == LostPostType.DELETED) {
                    throw CommonException(ResponseCode.POST_NOT_FOUND)
                }
            }
        }
    }
}
