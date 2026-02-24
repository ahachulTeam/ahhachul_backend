package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.member.application.port.`in`.command.UpdateMemberVisibilityCommand
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity

class MemberVisibilityDto {
    data class Request(
        val profilePublic: Boolean?,
        val emailPublic: Boolean?,
        val genderAgePublic: Boolean?,
        val postsPublic: Boolean?,
        val commentsPublic: Boolean?,
    ) {
        fun toCommand(): UpdateMemberVisibilityCommand {
            return UpdateMemberVisibilityCommand(
                profilePublic = profilePublic,
                emailPublic = emailPublic,
                genderAgePublic = genderAgePublic,
                postsPublic = postsPublic,
                commentsPublic = commentsPublic,
            )
        }
    }

    data class Response(
        val profilePublic: Boolean,
        val emailPublic: Boolean,
        val genderAgePublic: Boolean,
        val postsPublic: Boolean,
        val commentsPublic: Boolean,
    ) {
        companion object {
            fun from(member: MemberEntity): Response {
                return Response(
                    profilePublic = member.isProfilePublic(),
                    emailPublic = member.isEmailPublic(),
                    genderAgePublic = member.isGenderAgePublic(),
                    postsPublic = member.isActivityPostsPublic(),
                    commentsPublic = member.isActivityCommentsPublic(),
                )
            }
        }
    }
}
