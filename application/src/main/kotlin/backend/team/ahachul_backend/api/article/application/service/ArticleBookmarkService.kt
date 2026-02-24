package backend.team.ahachul_backend.api.article.application.service

import backend.team.ahachul_backend.api.article.application.port.`in`.ArticleBookmarkUseCase
import backend.team.ahachul_backend.api.article.application.port.out.ArticleBookmarkReader
import backend.team.ahachul_backend.api.article.application.port.out.ArticleBookmarkWriter
import backend.team.ahachul_backend.api.article.domain.entity.ArticleBookmarkEntity
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
class ArticleBookmarkService(
    private val articleBookmarkReader: ArticleBookmarkReader,
    private val articleBookmarkWriter: ArticleBookmarkWriter,
    private val memberReader: MemberReader,
    private val communityPostReader: CommunityPostReader,
    private val complaintPostReader: ComplaintPostReader,
    private val lostPostReader: LostPostReader,
) : ArticleBookmarkUseCase {

    @Transactional
    override fun bookmark(articleType: ArticleType, articleId: Long) {
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong()
        validateTarget(articleType, articleId)

        if (articleBookmarkReader.exists(articleType, articleId, memberId)) {
            throw CommonException(ResponseCode.ALREADY_BOOKMARKED_POST)
        }

        articleBookmarkWriter.save(
            ArticleBookmarkEntity.of(
                articleType = articleType,
                articleId = articleId,
                member = memberReader.getMember(memberId)
            )
        )
    }

    @Transactional
    override fun unbookmark(articleType: ArticleType, articleId: Long) {
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong()
        if (!articleBookmarkReader.exists(articleType, articleId, memberId)) {
            throw CommonException(ResponseCode.NOT_BOOKMARKED_POST)
        }

        articleBookmarkWriter.delete(articleType, articleId, memberId)
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
