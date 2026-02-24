package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.member.application.port.`in`.command.UpdateMemberCommand
import backend.team.ahachul_backend.api.member.domain.model.GenderType

class UpdateMemberDto {
    data class Request(
            val nickname: String?,
            val gender: GenderType?,
            val ageRange: String?,
            val profilePublic: Boolean?,
            val emailPublic: Boolean?,
            val genderAgePublic: Boolean?,
            val postsPublic: Boolean?,
            val commentsPublic: Boolean?,
    ) {
        fun toCommand(): UpdateMemberCommand {
            return UpdateMemberCommand(
                    nickname = nickname,
                    gender = gender,
                    ageRange = ageRange,
                    profilePublic = profilePublic,
                    emailPublic = emailPublic,
                    genderAgePublic = genderAgePublic,
                    postsPublic = postsPublic,
                    commentsPublic = commentsPublic,
            )
        }
    }

    data class Response(
            val nickname: String?,
            val gender: GenderType?,
            val ageRange: String?,
            val profilePublic: Boolean,
            val emailPublic: Boolean,
            val genderAgePublic: Boolean,
            val postsPublic: Boolean,
            val commentsPublic: Boolean,
    ) {
        companion object {
            fun of(
                nickname: String?,
                gender: GenderType?,
                ageRange: String?,
                profilePublic: Boolean,
                emailPublic: Boolean,
                genderAgePublic: Boolean,
                postsPublic: Boolean,
                commentsPublic: Boolean,
            ): Response {
                return Response(
                        nickname = nickname,
                        gender = gender,
                        ageRange = ageRange,
                        profilePublic = profilePublic,
                        emailPublic = emailPublic,
                        genderAgePublic = genderAgePublic,
                        postsPublic = postsPublic,
                        commentsPublic = commentsPublic,
                )
            }
        }
    }
}
