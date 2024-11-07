package backend.team.ahachul_backend.api.community.application.port.`in`

import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import org.springframework.web.multipart.MultipartFile

interface CommunityPostFileUseCase {

    fun createCommunityPostFiles(post: CommunityPostEntity, files: List<MultipartFile>): List<ImageDto>

    fun deleteCommunityPostFiles(fileIds: List<Long>)
}
