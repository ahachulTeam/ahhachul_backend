package backend.team.ahachul_backend.api.member.adapter.web.out

import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<MemberEntity, Long> {

    fun findByProviderUserId(providerUserId: String): MemberEntity?

    fun existsByNickname(nickname: String): Boolean
}