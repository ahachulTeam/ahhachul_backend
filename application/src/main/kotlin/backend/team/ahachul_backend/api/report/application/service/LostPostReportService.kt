package backend.team.ahachul_backend.api.report.application.service

import backend.team.ahachul_backend.api.lost.application.port.out.LostPostReader
import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import backend.team.ahachul_backend.api.lost.domain.model.LostOrigin
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.api.member.domain.entity.MemberEntity
import backend.team.ahachul_backend.api.report.adpater.`in`.dto.CreateReportDto
import backend.team.ahachul_backend.api.report.application.port.`in`.ReportUseCase
import backend.team.ahachul_backend.api.report.application.port.out.ReportWriter
import backend.team.ahachul_backend.api.report.domain.ReportEntity
import backend.team.ahachul_backend.common.exception.DomainException
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.RequestUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LostPostReportService(
    private val lostPostReader: LostPostReader,
    private val memberReader: MemberReader,
    private val reportWriter: ReportWriter
): ReportUseCase {


    override fun save(targetId: Long): CreateReportDto.Response {
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!
        val sourceMember = memberReader.getMember(memberId.toLong())
        val targetPost = lostPostReader.getLostPost(targetId)

        if (targetPost.origin == LostOrigin.LOST112) {
            throw DomainException(ResponseCode.EXTERNAL_REPORT_REQUEST)
        }

        val targetMember = targetPost.member!!
        validate(sourceMember, targetMember, targetPost)

        val entity = reportWriter.save(
            ReportEntity.from(sourceMember, targetMember, targetPost)
        )

        if (targetPost.exceedMinReportCount()) {
            targetPost.block()
        }

        return CreateReportDto.Response(
            id = entity.id,
            sourceMemberId = sourceMember.id,
            targetMemberId = targetMember.id,
            targetId = targetId
        )
    }

    private fun validate(sourceMember: MemberEntity, targetMember: MemberEntity, target: LostPostEntity) {
        if (sourceMember == targetMember) {
            throw DomainException(ResponseCode.INVALID_REPORT_REQUEST)
        }

        if (target.hasDuplicateReportByMember(sourceMember)) {
            throw DomainException(ResponseCode.DUPLICATE_REPORT_REQUEST)
        }
    }
}
