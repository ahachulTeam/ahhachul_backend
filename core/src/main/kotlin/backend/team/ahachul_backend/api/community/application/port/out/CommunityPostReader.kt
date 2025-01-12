package backend.team.ahachul_backend.api.community.application.port.out

import backend.team.ahachul_backend.api.community.application.command.out.GetSliceCommunityHotPostCommand
import backend.team.ahachul_backend.api.community.application.command.out.GetSliceCommunityPostCommand
import backend.team.ahachul_backend.api.community.domain.GetCommunityPost
import backend.team.ahachul_backend.api.community.domain.SearchCommunityPost
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity

interface CommunityPostReader {

    fun getCommunityPost(id: Long): CommunityPostEntity

    fun getByCustom(postId: Long, memberId: String?): GetCommunityPost

    fun searchCommunityPosts(command: GetSliceCommunityPostCommand): List<SearchCommunityPost>

    fun searchCommunityHotPosts(command: GetSliceCommunityHotPostCommand): List<SearchCommunityPost>
}