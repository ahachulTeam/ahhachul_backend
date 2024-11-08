package backend.team.ahachul_backend.api.lost.application.port.out

import backend.team.ahachul_backend.api.lost.domain.entity.LostPostFileEntity

interface LostPostFileReader {

    fun findByPostId(postId: Long): LostPostFileEntity?

    fun findAllByPostId(postId: Long): List<LostPostFileEntity>
}
