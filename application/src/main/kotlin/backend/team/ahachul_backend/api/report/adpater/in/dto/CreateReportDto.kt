package backend.team.ahachul_backend.api.report.adpater.`in`.dto


class CreateReportDto {

    data class Response(
        val id: Long,
        val sourceMemberId: Long,
        val targetMemberId: Long,
        val targetId: Long
    ) {
    }
}