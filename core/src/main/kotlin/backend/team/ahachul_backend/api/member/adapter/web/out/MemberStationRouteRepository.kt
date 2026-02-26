package backend.team.ahachul_backend.api.member.adapter.web.out

import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.entity.MemberStationRouteEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MemberStationRouteRepository : JpaRepository<MemberStationRouteEntity, Long> {

    @EntityGraph(attributePaths = ["member", "sourceStation", "destinationStation"])
    override fun findById(id: Long): Optional<MemberStationRouteEntity>

    @EntityGraph(attributePaths = ["member", "sourceStation", "destinationStation"])
    fun findAllByMemberOrderByCreatedAtDesc(member: MemberEntity): List<MemberStationRouteEntity>

    @EntityGraph(attributePaths = ["member", "sourceStation", "destinationStation"])
    fun findAllByMemberIdNotOrderByCreatedAtDesc(memberId: Long): List<MemberStationRouteEntity>

    fun countByMember(member: MemberEntity): Long

    fun existsByMemberAndSourceStationIdAndDestinationStationId(
        member: MemberEntity,
        sourceStationId: Long,
        destinationStationId: Long,
    ): Boolean
}
