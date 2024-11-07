package backend.team.ahachul_backend.api.community.application.port.out

import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.post.SearchCommunityPostCommand
import backend.team.ahachul_backend.api.community.domain.GetCommunityPost
import backend.team.ahachul_backend.api.community.domain.SearchCommunityPost
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import org.springframework.data.domain.Slice

interface CommunityPostReader {

    fun getCommunityPost(id: Long): CommunityPostEntity

    fun getByCustom(postId: Long, memberId: String?): GetCommunityPost

    fun searchCommunityPosts(command: SearchCommunityPostCommand): Slice<SearchCommunityPost>
}