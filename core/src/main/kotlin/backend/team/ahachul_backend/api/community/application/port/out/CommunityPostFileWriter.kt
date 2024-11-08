package backend.team.ahachul_backend.api.community.application.port.out

import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostFileEntity

interface CommunityPostFileWriter {

    fun save(entity: CommunityPostFileEntity): CommunityPostFileEntity

    fun deleteByFileId(fileId: Long)
}