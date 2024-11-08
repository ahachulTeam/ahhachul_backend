package backend.team.ahachul_backend.api.lost.adapter.web.out

import backend.team.ahachul_backend.api.lost.application.port.out.LostPostReader
import backend.team.ahachul_backend.api.lost.application.port.out.LostPostWriter
import backend.team.ahachul_backend.api.lost.application.service.command.out.GetRecommendLostPostsCommand
import backend.team.ahachul_backend.api.lost.application.service.command.out.GetSliceLostPostsCommand
import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component

@Component
class LostPostPersistence(
    private val lostPostRepository: LostPostRepository,
    private val customLostPostRepository: CustomLostPostRepository
): LostPostWriter, LostPostReader {

    override fun save(lostPostEntity: LostPostEntity): LostPostEntity {
        return lostPostRepository.save(lostPostEntity)
    }

    override fun saveAll(lostPosts: List<LostPostEntity>) {
        customLostPostRepository.saveAll(lostPosts)
    }

    override fun getLostPost(id: Long): LostPostEntity {
        return lostPostRepository.findById(id)
            .orElseThrow { throw AdapterException(ResponseCode.INVALID_DOMAIN) }
    }

    override fun getLostPosts(command: GetSliceLostPostsCommand): List<LostPostEntity> {
        return customLostPostRepository.searchLostPosts(command)
    }

    override fun getRecommendLostPosts(command: GetRecommendLostPostsCommand): List<LostPostEntity> {
        return customLostPostRepository.searchRecommendPost(command)
    }

    override fun getRandomLostPosts(command: GetRecommendLostPostsCommand): List<LostPostEntity> {
        return customLostPostRepository.searchRandomPostNotEqualCategory(command)
    }
}
