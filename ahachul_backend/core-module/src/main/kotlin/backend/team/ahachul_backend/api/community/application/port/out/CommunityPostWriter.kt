package backend.team.ahachul_backend.api.community.application.port.out

import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity

interface CommunityPostWriter {

    fun save(entity: CommunityPostEntity): CommunityPostEntity
}