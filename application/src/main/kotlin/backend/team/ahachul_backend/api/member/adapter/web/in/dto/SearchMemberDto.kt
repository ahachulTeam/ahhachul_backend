package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

import backend.team.ahachul_backend.api.member.application.command.SearchMemberCommand

class SearchMemberDto {

    data class Request(
        val nickname: String
    ) {
        fun toCommand(): SearchMemberCommand {
            return SearchMemberCommand(
                nickname = nickname
            )
        }
    }

    data class Response(
        val members: List<SearchMemberResponse>
    ) {
        companion object {
            fun of(members: List<SearchMemberResponse>): Response {
                return Response(
                    members = members
                )
            }
        }
    }

    data class SearchMemberResponse(
        val id: Long,
        val nickname: String?
    )
}