package backend.team.ahachul_backend.api.lost.application.port.out

import backend.team.ahachul_backend.api.lost.application.service.command.out.GetRecommendLostPostsCommand
import backend.team.ahachul_backend.api.lost.application.service.command.out.GetSliceLostPostsCommand
import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import org.springframework.data.domain.Slice

interface LostPostReader {

    fun getLostPost(id: Long): LostPostEntity

    fun getLostPosts(command: GetSliceLostPostsCommand): List<LostPostEntity>

    fun getRecommendLostPosts(command: GetRecommendLostPostsCommand): List<LostPostEntity>

    fun getRandomLostPosts(command: GetRecommendLostPostsCommand): List<LostPostEntity>
}
