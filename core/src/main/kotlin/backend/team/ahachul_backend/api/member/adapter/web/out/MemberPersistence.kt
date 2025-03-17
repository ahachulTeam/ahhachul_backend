package backend.team.ahachul_backend.api.member.adapter.web.out

import backend.team.ahachul_backend.api.member.application.command.SearchMemberCommand
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.api.member.application.port.out.MemberWriter
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.common.exception.AdapterException
import backend.team.ahachul_backend.common.exception.DomainException
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.stereotype.Component

@Component
class MemberPersistence(
        private val memberRepository: MemberRepository
): MemberReader, MemberWriter {

    override fun save(memberEntity: MemberEntity): MemberEntity {
        return memberRepository.save(memberEntity)
    }

    override fun getMember(memberId: Long): MemberEntity {
        val member = memberRepository.findById(memberId)
            .orElseThrow { throw AdapterException(ResponseCode.INVALID_DOMAIN) }

        validateMember(member)

        return member
    }

    override fun findMember(providerUserId: String): MemberEntity? {
        val member = memberRepository.findByProviderUserId(providerUserId)

        member?.let {
            validateMember(member)
        }

        return member
    }

    override fun existMember(nickname: String): Boolean {
        return memberRepository.existsByNickname(nickname)
    }

    override fun searchMembers(command: SearchMemberCommand): List<MemberEntity> {
        return memberRepository.findByNicknameContaining(command.nickname)
    }

    private fun validateMember(member: MemberEntity) {
        if (member.isDeleted()) {
            throw DomainException(ResponseCode.ALREADY_DELETE_MEMBER)
        }
    }
}