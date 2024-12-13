package backend.team.ahachul_backend.api.community.application.service

import backend.team.ahachul_backend.api.community.application.port.`in`.CommunityPostLikeUseCase
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostLikeReader
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostLikeWriter
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostReader
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostLikeEntity
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.domain.model.YNType
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.RequestUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class CommunityPostLikeService (
    private val communityPostLikeWriter: CommunityPostLikeWriter,
    private val communityPostLikeReader: CommunityPostLikeReader,

    private val communityPostReader: CommunityPostReader,
    private val memberReader: MemberReader,

    private val communityPostLikeSupport: CommunityPostLikeSupport,

): CommunityPostLikeUseCase {

    @Transactional
    override fun like(postId: Long) {
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong()
        val postLike = communityPostLikeReader.find(postId, memberId)
        if (postLike?.likeYn == YNType.Y) {
            throw CommonException(ResponseCode.ALREADY_LIKED_POST)
        }
        if (postLike?.likeYn == YNType.N) {
            postLike.like()
            return
        }
        val communityPost = communityPostReader.getCommunityPost(postId)
        communityPostLikeWriter.save(
            CommunityPostLikeEntity.of(
                communityPost = communityPost,
                member = memberReader.getMember(memberId),
                YNType.Y
            )
        )

        if (communityPostLikeSupport.isPossibleHotPost(communityPost)) {
            communityPost.hotPostYn = YNType.Y
            communityPost.hotPostSelectedDate = LocalDateTime.now()
        }
    }

    @Transactional
    override fun notLike(postId: Long) {
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong()
        communityPostLikeReader.find(postId, memberId)?.let {
            if (it.likeYn == YNType.N) {
                throw CommonException(ResponseCode.REJECT_BY_HATE_STATUS)
            }
        } ?: throw CommonException(ResponseCode.BAD_REQUEST)

        communityPostLikeWriter.delete(postId, memberId)
    }

    @Transactional
    override fun hate(postId: Long) {
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong()
        val postLike = communityPostLikeReader.find(postId, memberId)
        if (postLike?.likeYn == YNType.N) {
            throw CommonException(ResponseCode.ALREADY_HATED_POST)
        }
        if (postLike?.likeYn == YNType.Y) {
            postLike.hate()
            return
        }
        communityPostLikeWriter.save(
            CommunityPostLikeEntity.of(
                communityPost = communityPostReader.getCommunityPost(postId),
                member = memberReader.getMember(memberId),
                YNType.N
            )
        )
    }

    @Transactional
    override fun notHate(postId: Long) {
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!.toLong()
        communityPostLikeReader.find(postId, memberId)?.let {
            if (it.likeYn == YNType.Y) {
                throw CommonException(ResponseCode.REJECT_BY_LIKE_STATUS)
            }
        } ?: throw CommonException(ResponseCode.BAD_REQUEST)

        communityPostLikeWriter.delete(postId, memberId)
    }
}
