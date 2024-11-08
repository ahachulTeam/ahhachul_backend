package backend.team.ahachul_backend.admin.application.service

import backend.team.ahachul_backend.admin.application.port.`in`.AdminUseCase
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.admin.adapter.`in`.dto.ActionReportDto
import backend.team.ahachul_backend.api.report.application.port.`in`.command.ActionReportCommand
import backend.team.ahachul_backend.api.report.domain.BlockType
import backend.team.ahachul_backend.common.client.RedisClient
import backend.team.ahachul_backend.common.exception.DomainException
import backend.team.ahachul_backend.common.response.ResponseCode
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class AdminService(
    private val memberReader: MemberReader,
    private val redisClient: RedisClient
): AdminUseCase {

    override fun actionOnReport(command: ActionReportCommand): ActionReportDto.Response {
        val blockMemberId = command.targetMemberId
        val member = memberReader.getMember(blockMemberId)
        val blockType = BlockType.of(command.blockType)

        if (!member.isConditionsMetToBlock(blockType.blockDays)) {
            throw DomainException(ResponseCode.INVALID_CONDITION_TO_BLOCK_MEMBER)
        }

        member.blockMember()

        redisClient.set("blocked-member:${blockMemberId}",
            blockMemberId.toString(),
            blockType.blockDays.toLong(),
            TimeUnit.MINUTES
        )
        return ActionReportDto.Response(command.targetMemberId)
    }
}
