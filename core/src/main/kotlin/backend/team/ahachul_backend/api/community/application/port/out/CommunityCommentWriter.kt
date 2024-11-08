package backend.team.ahachul_backend.api.community.application.port.out

import backend.team.ahachul_backend.api.community.domain.entity.CommunityCommentEntity

interface CommunityCommentWriter {

    fun save(entity: CommunityCommentEntity): CommunityCommentEntity
}