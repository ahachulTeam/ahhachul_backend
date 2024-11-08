package backend.team.ahachul_backend.api.lost.application.port.out

import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity

interface LostPostWriter {

    fun save(lostPostEntity: LostPostEntity): LostPostEntity

    fun saveAll(lostPosts: List<LostPostEntity>)
}
