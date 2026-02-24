package backend.team.ahachul_backend.api.member.application.port.`in`.command

data class UpdateMemberVisibilityCommand(
    val profilePublic: Boolean?,
    val emailPublic: Boolean?,
    val genderAgePublic: Boolean?,
    val postsPublic: Boolean?,
    val commentsPublic: Boolean?,
)
