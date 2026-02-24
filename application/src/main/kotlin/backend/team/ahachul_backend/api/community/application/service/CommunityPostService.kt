package backend.team.ahachul_backend.api.community.application.service

import backend.team.ahachul_backend.api.article.application.port.out.ArticleBookmarkReader
import backend.team.ahachul_backend.api.article.domain.model.ArticleType
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
import backend.team.ahachul_backend.api.common.application.port.out.StationReader
import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
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
    private val stationReader: StationReader,
    private val subwayLineReader: SubwayLineReader,
    private val subwayLineStationReader: SubwayLineStationReader,
    private val communityPostHashTagReader: CommunityPostHashTagReader,
    private val communityPostFileReader: CommunityPostFileReader,

    private val communityPostHashTagService: CommunityPostHashTagService,
    private val communityPostFileService: CommunityPostFileService,

    private val viewsSupport: ViewsSupport,
    private val articleBookmarkReader: ArticleBookmarkReader,
): CommunityPostUseCase {

    private val logger = NamedLogger("HASHTAG_LOGGER")

    override fun searchCommunityPosts(command: SearchCommunityPostCommand): PageInfoDto<SearchCommunityPostDto.Response> {
        val userId: String? = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)
        val subwayLineIds = resolveSubwayLineIds(command.subwayLineIds, command.stationId)
        val subwayLines = subwayLineIds?.stream()
            ?.map { subwayLineReader.getById(it) }
            ?.toList()

        val searchCommunityPosts = communityPostReader.searchCommunityPosts(
            GetSliceCommunityPostCommand.from(
                command = command,
                subwayLines = subwayLines
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
        val userId: String? = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)
        val subwayLineIds = resolveSubwayLineIds(command.subwayLineIds, command.stationId)
        val subwayLines = subwayLineIds?.stream()
            ?.map { subwayLineReader.getById(it) }
            ?.toList()

        val searchCommunityHotPosts = communityPostReader.searchCommunityHotPosts(
            GetSliceCommunityHotPostCommand.from(
                command = command,
                subwayLines = subwayLines
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
        val userId: String? = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)
        val memberId = userId?.toLongOrNull()
        val communityPost = communityPostReader.getByCustom(command.id, userId)

        if (communityPost.status == CommunityPostType.DELETED) {
            throw CommonException(ResponseCode.POST_NOT_FOUND)
        }

        val views = viewsSupport.increase(command.id)
        val hashTags = communityPostHashTagReader.findAllByPostId(communityPost.id).map { it.hashTag.name }
        val communityPostFiles = communityPostFileReader.findAllByPostId(communityPost.id)
        val bookmarkCnt = articleBookmarkReader.count(ArticleType.COMMUNITY, command.id)
        val bookmarkYn = memberId?.let {
            articleBookmarkReader.exists(ArticleType.COMMUNITY, command.id, it)
        } ?: false
        return GetCommunityPostDto.Response.of(
            getCommunityPost = communityPost,
            hashTags = hashTags,
            views = views,
            images = convertToImageDto(communityPostFiles),
            bookmarkCnt = bookmarkCnt,
            bookmarkYn = bookmarkYn
        )
    }

    @Transactional
    override fun createCommunityPost(command: CreateCommunityPostCommand): CreateCommunityPostDto.Response {
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!
        val member = memberReader.getMember(memberId.toLong())
        val subwayLine = subwayLineReader.getById(command.subwayLineId)
        val station = resolveStation(command.subwayLineId, command.stationId)
        val communityPost = communityPostWriter.save(CommunityPostEntity.of(command, member, subwayLine, station))
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
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!
        val communityPost = communityPostReader.getCommunityPost(command.id)
        communityPost.checkMe(memberId)
        val subwayLineId = command.subwayLineId ?: communityPost.subwayLineEntity.id
        val subwayLine = subwayLineReader.getById(subwayLineId)
        val station = resolveStation(subwayLineId, command.stationId ?: communityPost.station?.id)
        communityPost.update(command, subwayLine, station)
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
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!
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
                    stationId = it.stationId,
                    createdAt = it.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
                    createdBy = it.createdBy,
                    writer = it.writer,
                    image = file?.let { it1 -> ImageDto.of(it1.id, file.filePath) }
                )
            }.toList()
    }

    private fun resolveStation(subwayLineId: Long, stationId: Long?): StationEntity? {
        if (stationId == null) {
            return null
        }

        return subwayLineStationReader.findBySubwayLineIdAndStationId(subwayLineId, stationId).station
    }

    private fun resolveSubwayLineIds(requestedLineIds: List<Long>?, stationId: Long?): List<Long>? {
        val stationLineIds = stationId?.let {
            val station = stationReader.getById(it)
            subwayLineStationReader.findByStation(station)
                .map { subwayLineStation -> subwayLineStation.subwayLine.id }
                .distinct()
        }

        if (requestedLineIds == null && stationLineIds == null) {
            return null
        }

        if (requestedLineIds == null) {
            return stationLineIds
        }

        if (stationLineIds == null) {
            return requestedLineIds
        }

        return requestedLineIds
            .intersect(stationLineIds.toSet())
            .toList()
    }
}
