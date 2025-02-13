package backend.team.ahachul_backend.api.member.application.port.out

import backend.team.ahachul_backend.api.member.application.command.SearchMemberCommand
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity

interface MemberReader {

    fun getMember(memberId: Long): MemberEntity

    fun findMember(providerUserId: String): MemberEntity?

    fun existMember(nickname: String): Boolean

    fun searchMembers(command: SearchMemberCommand): List<MemberEntity>
}