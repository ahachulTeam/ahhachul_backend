package backend.team.ahachul_backend.admin.adapter.`in`.dto

import backend.team.ahachul_backend.api.report.application.port.`in`.command.ActionReportCommand

class ActionReportDto {

    data class Request(
        val targetMemberId: Long,
        val blockType: String
    ) {
        fun toCommand(): ActionReportCommand {
            return ActionReportCommand(
                targetMemberId = targetMemberId,
                blockType = blockType
            )
        }
    }

    data class Response(
        val id: Long,
    )
}