package backend.team.ahachul_backend.api.member.domain.entity

import backend.team.ahachul_backend.api.member.application.port.`in`.command.LoginMemberCommand
import backend.team.ahachul_backend.api.member.domain.model.GenderType
import backend.team.ahachul_backend.api.member.domain.model.ProviderType

import backend.team.ahachul_backend.api.member.domain.model.MemberStatusType
import backend.team.ahachul_backend.api.report.domain.ReportEntity
import backend.team.ahachul_backend.common.domain.entity.BaseEntity
import backend.team.ahachul_backend.common.exception.DomainException
import backend.team.ahachul_backend.common.domain.model.RegionType
import backend.team.ahachul_backend.common.dto.AppleUserInfoDto
import backend.team.ahachul_backend.common.dto.GoogleUserInfoDto
import backend.team.ahachul_backend.common.dto.KakaoMemberInfoDto
import backend.team.ahachul_backend.common.response.ResponseCode
import jakarta.persistence.*

@Entity
class MemberEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "member_id")
        val id: Long = 0,

        var nickname: String?,

        val providerUserId: String,

        @Enumerated(EnumType.STRING)
        val provider: ProviderType,

        var email: String?,

        @Enumerated(EnumType.STRING)
        var gender: GenderType?,

        var ageRange: String?,

        @Enumerated(EnumType.STRING)
        var status: MemberStatusType,

        @Enumerated(EnumType.STRING)
        var regionType: RegionType = RegionType.METROPOLITAN,

        @OneToMany(mappedBy = "targetMember")
        var memberReports: MutableList<ReportEntity> = mutableListOf()
): BaseEntity() {

        companion object {
                fun ofKakao(command: LoginMemberCommand, userInfo: KakaoMemberInfoDto): MemberEntity {
                        return MemberEntity(
                                nickname = null,
                                providerUserId = userInfo.id,
                                provider = command.providerType,
                                email = userInfo.kakaoAccount.email,
                                gender = userInfo.kakaoAccount.gender?.let { GenderType.of(it) },
                                ageRange = userInfo.kakaoAccount.ageRange?.let { it.split("~")[0] },
                                status = MemberStatusType.ACTIVE,
                        )
                }

                fun ofGoogle(command: LoginMemberCommand, userInfo: GoogleUserInfoDto): MemberEntity {
                        return MemberEntity(
                                nickname = null,
                                providerUserId = userInfo.id,
                                provider = command.providerType,
                                email = userInfo.email,
                                gender = null,
                                ageRange = null,
                                status = MemberStatusType.ACTIVE
                        )
                }

                fun ofApple(command: LoginMemberCommand, userInfo: AppleUserInfoDto): MemberEntity {
                        return MemberEntity(
                                nickname = null,
                                providerUserId = userInfo.sub,
                                provider = command.providerType,
                                email = userInfo.email,
                                gender = null,
                                ageRange = null,
                                status = MemberStatusType.ACTIVE
                        )
                }
        }

        fun changeNickname(nickname: String) {
                this.nickname = nickname
        }

        fun changeGender(gender: GenderType) {
                this.gender = gender
        }

        fun changeAgeRange(ageRange: String) {
                this.ageRange = ageRange
        }

        fun isNeedAdditionalUserInfo(): Boolean {
                return nickname == null
        }

        fun isConditionsMetToBlock(reportedCount: Int): Boolean {
                return memberReports.size >= reportedCount
        }

        fun blockMember() {
                if (status == MemberStatusType.SUSPENDED) {
                        throw DomainException(ResponseCode.INVALID_REPORT_ACTION)
                }
                this.status = MemberStatusType.SUSPENDED
        }
}
