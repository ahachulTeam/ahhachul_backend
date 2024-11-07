package backend.team.ahachul_backend.api.community.application.service

import backend.team.ahachul_backend.api.community.adapter.web.`in`.dto.post.*
import backend.team.ahachul_backend.api.community.application.port.`in`.CommunityPostUseCase
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostFileReader
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostHashTagReader
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostReader
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostWriter
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostFileEntity
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.common.dto.ImageDto
import backend.team.ahachul_backend.common.logging.NamedLogger
import backend.team.ahachul_backend.common.persistence.SubwayLineReader
import backend.team.ahachul_backend.common.support.ViewsSupport
import backend.team.ahachul_backend.common.utils.RequestUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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

    override fun searchCommunityPosts(command: SearchCommunityPostCommand): SearchCommunityPostDto.Response {
        val userId: String? = RequestUtils.getAttribute("memberId")
        val searchCommunityPosts = communityPostReader.searchCommunityPosts(command)
        val posts = searchCommunityPosts
            .map {
                val file = communityPostFileReader.findByPostId(it.id)?.file
                SearchCommunityPostDto.CommunityPost.of(
                    searchCommunityPost = it,
                    image = file?.let { it1 -> ImageDto.of(it1.id, file.filePath) },
                    views = viewsSupport.get(it.id),
                    hashTags = communityPostHashTagReader.findAllByPostId(it.id).map { it.hashTag.name }
                )
            }.toList()

        if (isHashTagSearchCond(command.hashTag, command.content)) {
            logger.info("userId = $userId hashtag = ${command.hashTag}")
        }

        return SearchCommunityPostDto.Response.of(
            hasNext = searchCommunityPosts.hasNext(),
            posts = posts,
            command.pageable.pageNumber,
        )
    }

    private fun isHashTagSearchCond(hashTag: String?, content: String?): Boolean {
        return !hashTag.isNullOrEmpty() && content.isNullOrEmpty()
    }

    override fun getCommunityPost(command: GetCommunityPostCommand): GetCommunityPostDto.Response {
        val userId: String? = RequestUtils.getAttribute("memberId")
        val communityPost = communityPostReader.getByCustom(command.id, userId)
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
        val images = communityPostFileService.createCommunityPostFiles(communityPost, command.imageFiles)

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
        communityPostFileService.createCommunityPostFiles(communityPost, command.uploadFiles)
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
}
