package backend.team.ahachul_backend.api.member.adapter.web.`in`.dto

class DeleteMemberDto {

    data class Request(
        val authorizationHeader: String,
    ) {
        val accessToken: String = authorizationHeader.removeAuthPrefix()

        companion object {
            private const val AUTH_PREFIX = "Bearer "

            private fun String.removeAuthPrefix(): String {
                return if (this.startsWith(AUTH_PREFIX)) this.substring(AUTH_PREFIX.length) else this
            }
        }
    }
}