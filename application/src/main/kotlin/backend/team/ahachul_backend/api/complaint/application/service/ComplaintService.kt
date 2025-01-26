package backend.team.ahachul_backend.api.complaint.application.service

import backend.team.ahachul_backend.api.complaint.adapter.web.`in`.dto.SearchComplaintMessagesDto
import backend.team.ahachul_backend.api.complaint.application.command.SendComplaintMessageCommand
import backend.team.ahachul_backend.api.complaint.application.port.`in`.ComplaintUseCase
import backend.team.ahachul_backend.api.complaint.application.port.out.ComplaintMessageFileReader
import backend.team.ahachul_backend.api.complaint.application.port.out.ComplaintMessageReader
import backend.team.ahachul_backend.api.complaint.application.port.out.ComplaintMessageWriter
import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintMessageHistoryFileEntity
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.common.dto.ImageDto
import backend.team.ahachul_backend.common.persistence.SubwayLineReader
import backend.team.ahachul_backend.common.utils.RequestUtils
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ComplaintService(
    private val complaintMessageWriter: ComplaintMessageWriter,
    private val complaintMessageReader: ComplaintMessageReader,
    private val complaintMessageFileReader: ComplaintMessageFileReader,
    private val memberReader: MemberReader,
    private val subwayLineReader: SubwayLineReader,

    private val complaintMessageFileService: ComplaintMessageFileService,
): ComplaintUseCase {

    override fun searchComplaintMessages(pageable: Pageable): SearchComplaintMessagesDto.Response {
        val complaintMessages = complaintMessageReader.findAll(pageable)
            .map { SearchComplaintMessagesDto.ComplaintMessage(
                content = it.sentContent,
                complainType = it.complaintType,
                shortContentType = it.shortContentType,
                complaintMessageStatusType = it.complaintMessageStatusType,
                phoneNumber = it.sentPhoneNumber,
                trainNo = it.sentTrainNo,
                location = it.location,
                subwayLineId = it.subwayLine.id,
                createdAt = it.createdAt,
                createdBy = it.createdBy,
                writer = it.member!!.nickname!!,
                images = convertToImageDto(complaintMessageFileReader.findAllByComplaintId(it.id))
            ) }

        return SearchComplaintMessagesDto.Response.of(
            hasNext = complaintMessages.size > complaintMessages.pageable.pageSize,
            currentPageNum = pageable.pageNumber,
            complaintMessages = complaintMessages.content
        )
    }

    @Transactional
    override fun sendComplaintMessage(command: SendComplaintMessageCommand) {
        val memberId = RequestUtils.getAttribute("memberId")!!.toLong()
        val member = memberReader.getMember(memberId)

        val complaintMessage = complaintMessageWriter.save(
            command.toEntity(
                member,
                subwayLineReader.getById(command.subwayLineId)
            )
        )

        complaintMessageFileService.createComplaintMessageFiles(complaintMessage, command.imageFiles)
    }

    private fun convertToImageDto(complaintMessageFiles: List<ComplaintMessageHistoryFileEntity>): List<ImageDto> {
        return complaintMessageFiles.map {
            ImageDto.of(
                imageId = it.file.id,
                imageUrl = it.file.filePath
            )
        }
    }
}
