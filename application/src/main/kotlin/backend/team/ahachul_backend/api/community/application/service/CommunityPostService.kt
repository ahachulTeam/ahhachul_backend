package backend.team.ahachul_backend.api.community.application.service

import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.community.application.command.`in`.*
import backend.team.ahachul_backend.api.community.application.command.out.GetSliceCommunityHotPostCommand
import backend.team.ahachul_backend.api.community.application.command.out.GetSliceCommunityPostCommand
import backend.team.ahachul_backend.api.community.application.port.`in`.CommunityPostUseCase
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostFileReader
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostHashTagReader
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostReader
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostWriter
import backend.team.ahachul_backend.api.community.domain.SearchCommunityPost
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostFileEntity
import backend.team.ahachul_backend.api.community.domain.model.CommunityPostType
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.common.dto.ImageDto
import backend.team.ahachul_backend.common.dto.PageInfoDto
import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.logging.NamedLogger
import backend.team.ahachul_backend.common.persistence.SubwayLineReader
import backend.team.ahachul_backend.common.response.ResponseCode
import backend.team.ahachul_backend.common.support.ViewsSupport
import backend.team.ahachul_backend.common.utils.RequestUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter

@Service
@Transactional(readOnly = true)
class CommunityPostService(
    private val communityPostWriter: CommunityPostWriter,
    private val communityPostReader: CommunityPostReader,

    private val memberReader: MemberReader,
    private val subwayLineReader: SubwayLineReader,
    private val communityPostHashTagReader: CommunityPostHashTagReader,
    private val communityPostFileReader: CommunityPostFileReader,

    private val communityPostHashTagService: CommunityPostHashTagService,
    private val communityPostFileService: CommunityPostFileService,

    private val viewsSupport: ViewsSupport
): CommunityPostUseCase {

    private val logger = NamedLogger("HASHTAG_LOGGER")

    override fun searchCommunityPosts(command: SearchCommunityPostCommand): PageInfoDto<SearchCommunityPostDto.Response> {
        val userId: String? = RequestUtils.getAttribute("memberId")
        val subwayLine = command.subwayLineId?.let { subwayLineReader.getById(it) }

        val searchCommunityPosts = communityPostReader.searchCommunityPosts(
            GetSliceCommunityPostCommand.from(
                command = command,
                subwayLine = subwayLine
            )
        )

        loggingHashTag(userId, command.hashTag, command.content)

        return PageInfoDto.of(
            data=convertCommunityPostDto(searchCommunityPosts),
            pageSize=command.pageSize,
            arrayOf(SearchCommunityPostDto.Response::createdAt, SearchCommunityPostDto.Response::id)
        )
    }

    override fun searchCommunityHotPosts(command: SearchCommunityHotPostCommand): PageInfoDto<SearchCommunityPostDto.Response> {
        val userId: String? = RequestUtils.getAttribute("memberId")
        val subwayLine = command.subwayLineId?.let { subwayLineReader.getById(it) }

        val searchCommunityHotPosts = communityPostReader.searchCommunityHotPosts(
            GetSliceCommunityHotPostCommand.from(
                command = command,
                subwayLine = subwayLine
            )
        )

        loggingHashTag(userId, command.hashTag, command.content)

        return PageInfoDto.of(
            data=convertCommunityPostDto(searchCommunityHotPosts),
            pageSize=command.pageSize,
            arrayOf(SearchCommunityPostDto.Response::createdAt, SearchCommunityPostDto.Response::id)
        )
    }

    override fun getCommunityPost(command: GetCommunityPostCommand): GetCommunityPostDto.Response {
        val userId: String? = RequestUtils.getAttribute("memberId")
        val communityPost = communityPostReader.getByCustom(command.id, userId)

        if (communityPost.status == CommunityPostType.DELETED) {
            throw CommonException(ResponseCode.POST_NOT_FOUND)
        }

        val views = viewsSupport.increase(command.id)
        val hashTags = communityPostHashTagReader.findAllByPostId(communityPost.id).map { it.hashTag.name }
        val communityPostFiles = communityPostFileReader.findAllByPostId(communityPost.id)
        return GetCommunityPostDto.Response.of(
            communityPost,
            hashTags,
            views,
            convertToImageDto(communityPostFiles)
        )
    }

    @Transactional
    override fun createCommunityPost(command: CreateCommunityPostCommand): CreateCommunityPostDto.Response {
        val memberId = RequestUtils.getAttribute("memberId")!!
        val member = memberReader.getMember(memberId.toLong())
        val subwayLine = subwayLineReader.getById(command.subwayLineId)
        val communityPost = communityPostWriter.save(CommunityPostEntity.of(command, member, subwayLine))
        communityPostHashTagService.createCommunityPostHashTag(communityPost, command.hashTags)

        val images = command.imageFiles?.let {
            communityPostFileService.createCommunityPostFiles(communityPost, command.imageFiles!!)
        }

        return CreateCommunityPostDto.Response.of(
            communityPost,
            images
        )
    }

    @Transactional
    override fun updateCommunityPost(command: UpdateCommunityPostCommand): UpdateCommunityPostDto.Response {
        val memberId = RequestUtils.getAttribute("memberId")!!
        val communityPost = communityPostReader.getCommunityPost(command.id)
        communityPost.checkMe(memberId)
        communityPost.update(command)
        communityPostHashTagService.createCommunityPostHashTag(communityPost, command.hashTags)
        command.uploadFiles?.let {
            communityPostFileService.createCommunityPostFiles(communityPost, command.uploadFiles!!)
        }
        communityPostFileService.deleteCommunityPostFiles(command.removeFileIds)
        val communityPostFiles = communityPostFileReader.findAllByPostId(communityPost.id)
        return UpdateCommunityPostDto.Response.of(
            communityPost,
            convertToImageDto(communityPostFiles)
        )
    }

    @Transactional
    override fun deleteCommunityPost(command: DeleteCommunityPostCommand): DeleteCommunityPostDto.Response {
        val memberId = RequestUtils.getAttribute("memberId")!!
        val entity = communityPostReader.getCommunityPost(command.id)
        entity.checkMe(memberId)
        entity.delete()
        return DeleteCommunityPostDto.Response(entity.id)
    }

    private fun convertToImageDto(communityPostFiles: List<CommunityPostFileEntity>): List<ImageDto> {
        return communityPostFiles.map {
            ImageDto.of(
                imageId = it.file.id,
                imageUrl = it.file.filePath
            )
        }
    }

    private fun loggingHashTag(userId: String?, hashtag: String?, content: String?) {
        if (isHashTagSearchCond(hashtag, content)) {
            logger.info("userId = $userId hashtag = $hashtag")
        }
    }

    private fun isHashTagSearchCond(hashTag: String?, content: String?): Boolean {
        return !hashTag.isNullOrEmpty() && content.isNullOrEmpty()
    }

    private fun convertCommunityPostDto(searchCommunityPosts: List<SearchCommunityPost>): List<SearchCommunityPostDto.Response> {
        return searchCommunityPosts
            .map {
                val file = communityPostFileReader.findByPostId(it.id)?.file
                SearchCommunityPostDto.Response(
                    id = it.id,
                    title = it.title,
                    content = it.content,
                    categoryType = it.categoryType,
                    hashTags = communityPostHashTagReader.findAllByPostId(it.id).map { it.hashTag.name },
                    commentCnt = it.commentCnt,
                    viewCnt = viewsSupport.get(it.id),
                    likeCnt = it.likeCnt,
                    regionType = it.regionType,
                    subwayLineId = it.subwayLineId,
                    createdAt = it.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
                    createdBy = it.createdBy,
                    writer = it.writer,
                    image = file?.let { it1 -> ImageDto.of(it1.id, file.filePath) }
                )
            }.toList()
    }
}
