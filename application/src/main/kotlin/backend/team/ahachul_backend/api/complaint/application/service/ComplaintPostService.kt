package backend.team.ahachul_backend.api.complaint.application.service

import backend.team.ahachul_backend.api.comment.application.port.out.CommentReader
import backend.team.ahachul_backend.api.complaint.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.complaint.application.command.`in`.CreateComplaintPostCommand
import backend.team.ahachul_backend.api.complaint.application.command.`in`.SearchComplaintPostCommand
import backend.team.ahachul_backend.api.complaint.application.command.`in`.UpdateComplaintPostCommand
import backend.team.ahachul_backend.api.complaint.application.command.`in`.UpdateComplaintPostStatusCommand
import backend.team.ahachul_backend.api.complaint.application.command.out.GetSliceComplaintPostsCommand
import backend.team.ahachul_backend.api.complaint.application.port.`in`.ComplaintPostUseCase
import backend.team.ahachul_backend.api.complaint.application.port.out.ComplaintPostFileReader
import backend.team.ahachul_backend.api.complaint.application.port.out.ComplaintPostReader
import backend.team.ahachul_backend.api.complaint.application.port.out.ComplaintPostWriter
import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostEntity
import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostFileEntity
import backend.team.ahachul_backend.api.complaint.domain.model.ComplaintPostType
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.common.dto.ImageDto
import backend.team.ahachul_backend.common.dto.PageInfoDto
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.persistence.SubwayLineReader
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.utils.RequestUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter

@Service
@Transactional(readOnly = true)
class ComplaintPostService(
    private val complaintPostReader: ComplaintPostReader,
    private val complaintPostWriter: ComplaintPostWriter,
    private val complaintPostFileReader: ComplaintPostFileReader,
    private val complaintPostFileService: ComplaintPostFileService,
    private val subwayLineReader: SubwayLineReader,
    private val memberReader: MemberReader,
    private val commentReader: CommentReader,
): ComplaintPostUseCase {

    override fun searchComplaintPosts(command: SearchComplaintPostCommand): PageInfoDto<SearchComplaintPostDto.Response> {
        val subwayLine = command.subwayLineId?.let { subwayLineReader.getById(it) }

        val complaintPosts = complaintPostReader.getComplaintPosts(
            GetSliceComplaintPostsCommand.of(
                command = command,
                subwayLine = subwayLine
            )
        ).map {
            val file = complaintPostFileReader.findByPostId(it.id)?.file
            SearchComplaintPostDto.Response(
                id = it.id,
                complaintType = it.complaintType,
                shortContentType = it.shortContentType,
                content = it.content,
                phoneNumber = it.phoneNumber,
                trainNo = it.trainNo,
                location = it.location,
                status = it.status,
                commentCnt = commentReader.countComplaint(it.id),
                subwayLineId = it.subwayLine.id,
                createdBy = it.createdBy,
                createdAt = it.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
                writer = it.member?.nickname,
                image = file?.let { it1 -> ImageDto.of(it1.id, file.filePath) }
            )
        }

        return PageInfoDto.of(
            data=complaintPosts,
            pageSize=command.pageSize,
            arrayOf(SearchComplaintPostDto.Response::createdAt, SearchComplaintPostDto.Response::id)
        )
    }

    override fun getComplaintPost(postId: Long): GetComplaintPostDto.Response {
        val complaintPost = complaintPostReader.getComplaintPost(postId)

        if (complaintPost.status == ComplaintPostType.DELETED) {
            throw CommonException(ResponseCode.POST_NOT_FOUND)
        }

        val commentCnt = commentReader.countComplaint(postId)
        val files = complaintPostFileReader.findAllByPostId(postId)

        return GetComplaintPostDto.Response.of(
            complaintPost, commentCnt, convertToImageDto(files)
        )
    }

    @Transactional
    override fun createComplaintPost(command: CreateComplaintPostCommand): CreateComplaintPostDto.Response {
        val memberId = RequestUtils.getAttribute("memberId")!!
        val member = memberReader.getMember(memberId.toLong())
        val subwayLine = subwayLineReader.getById(command.subwayLine)

        val complaintPost = complaintPostWriter.save(
            ComplaintPostEntity.of(
                command = command,
                member = member,
                subwayLine = subwayLine,
            )
        )

        val images = command.imageFiles?.let {
            complaintPostFileService.createComplaintPostFiles(complaintPost, command.imageFiles!!)
        }

        return CreateComplaintPostDto.Response.of(complaintPost.id, images)
    }

    @Transactional
    override fun updateComplaintPost(command: UpdateComplaintPostCommand): UpdateComplaintPostDto.Response {
        val memberId = RequestUtils.getAttribute("memberId")!!
        val complaintPost = complaintPostReader.getComplaintPost(command.id)
        complaintPost.checkMe(memberId)

        val subwayLine = command.subwayLineId?.let {
            subwayLineReader.getById(it)
        }

        complaintPost.update(command, subwayLine)
        updateImageFiles(command, complaintPost)

        return UpdateComplaintPostDto.Response.of(complaintPost)
    }

    @Transactional
    override fun updateComplaintPostStatus(command: UpdateComplaintPostStatusCommand): UpdateComplaintPostStatusDto.Response {
        val memberId = RequestUtils.getAttribute("memberId")!!
        val complaintPost = complaintPostReader.getComplaintPost(command.id)

        if (complaintPost.status == ComplaintPostType.DELETED) {
            throw CommonException(ResponseCode.POST_NOT_FOUND)
        }

        complaintPost.checkMe(memberId)
        complaintPost.updateStatus(command.status)

        return UpdateComplaintPostStatusDto.Response.of(complaintPost)
    }

    @Transactional
    override fun deleteComplaintPost(postId: Long): DeleteComplaintPostDto.Response {
        val memberId = RequestUtils.getAttribute("memberId")!!
        val complaintPost = complaintPostReader.getComplaintPost(postId)

        if (complaintPost.status == ComplaintPostType.DELETED) {
            throw CommonException(ResponseCode.POST_NOT_FOUND)
        }

        complaintPost.checkMe(memberId)
        complaintPost.delete()
        return DeleteComplaintPostDto.Response.of(complaintPost)
    }

    private fun updateImageFiles(command: UpdateComplaintPostCommand, complaintPost: ComplaintPostEntity) {
        command.imageFiles?.let {
            complaintPostFileService.createComplaintPostFiles(complaintPost, it)
        }

        command.removeFileIds?.let {
            complaintPostFileService.deleteComplaintPostFiles(it)
        }
    }

    private fun convertToImageDto(files: List<ComplaintPostFileEntity>): List<ImageDto> {
        return files.map {
            ImageDto.of(
                imageId = it.id,
                imageUrl = it.file.filePath
            )
        }
    }
}
