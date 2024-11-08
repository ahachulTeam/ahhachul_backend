package backend.team.ahachul_backend.api.community.application.service

import backend.team.ahachul_backend.api.community.application.port.`in`.CommunityPostHashTagUseCase
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostHashTagWriter
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostHashTagEntity
import backend.team.ahachul_backend.common.domain.entity.HashTagEntity
import backend.team.ahachul_backend.common.persistence.HashTagReader
import backend.team.ahachul_backend.common.persistence.HashTagWriter
import org.springframework.stereotype.Service

@Service
class CommunityPostHashTagService(
    private val communityPostHashTagWriter: CommunityPostHashTagWriter,

    private val hashTagWriter: HashTagWriter,
    private val hashTagReader: HashTagReader,
): CommunityPostHashTagUseCase {

    override fun createCommunityPostHashTag(communityPost: CommunityPostEntity, hashTags: List<String>) {
        for (hashTagName in hashTags) {
            val hashTag = hashTagReader.find(hashTagName) ?: hashTagWriter.save(
                HashTagEntity(name = hashTagName)
            )
            communityPostHashTagWriter.save(
                CommunityPostHashTagEntity(
                    communityPost = communityPost,
                    hashTag = hashTag
                )
            )
        }
    }
}