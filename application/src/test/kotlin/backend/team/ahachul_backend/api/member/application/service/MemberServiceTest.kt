package backend.team.ahachul_backend.api.member.application.service

import backend.team.ahachul_backend.api.member.application.command.SearchMemberCommand
import backend.team.ahachul_backend.api.member.application.port.`in`.MemberUseCase
import backend.team.ahachul_backend.api.member.application.port.out.MemberWriter
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.member.domain.model.GenderType
import backend.team.ahachul_backend.api.member.domain.model.MemberStatusType
import backend.team.ahachul_backend.api.member.domain.model.ProviderType
import backend.team.ahachul_backend.config.controller.CommonServiceTestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired

class MemberServiceTest(
    @Autowired val memberWriter: MemberWriter,
    @Autowired val memberUseCase: MemberUseCase,
) : CommonServiceTestConfig() {

    @BeforeEach
    fun setUp() {
        for (i in 1..10) {
            memberWriter.save(
                MemberEntity(
                    nickname = "nickname$i",
                    provider = ProviderType.GOOGLE,
                    providerUserId = "providerUserId$i",
                    email = "email$i",
                    gender = GenderType.MALE,
                    ageRange = "20",
                    status = MemberStatusType.ACTIVE
                )
            )
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["nickname", "nick", "name", "ckna"])
    fun 회원_닉네임_일치_조회(keyword: String) {
        //when
        val searchMemberCommand = SearchMemberCommand(nickname = keyword)
        val searchMembers = memberUseCase.searchMembers(searchMemberCommand)

        //then
        assertThat(searchMembers.members.size).isEqualTo(10)
    }

    @ParameterizedTest
    @ValueSource(strings = ["ahhachul", "test"])
    fun 회원_닉네임_불일치_조회(keyword: String) {
        //when
        val searchMemberCommand = SearchMemberCommand(nickname = keyword)
        val searchMembers = memberUseCase.searchMembers(searchMemberCommand)

        //then
        assertThat(searchMembers.members.size).isEqualTo(0)
    }
}