package backend.team.ahachul_backend.api.lost.application.service

import backend.team.ahachul_backend.api.comment.application.port.out.CommentReader
import backend.team.ahachul_backend.api.common.application.port.out.StationReader
import backend.team.ahachul_backend.api.common.application.port.out.SubwayLineStationReader
import backend.team.ahachul_backend.api.lost.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.lost.application.port.`in`.LostPostUseCase
import backend.team.ahachul_backend.api.lost.application.port.out.CategoryReader
import backend.team.ahachul_backend.api.lost.application.port.out.LostPostFileReader
import backend.team.ahachul_backend.api.lost.application.port.out.LostPostReader
import backend.team.ahachul_backend.api.lost.application.port.out.LostPostWriter
import backend.team.ahachul_backend.api.lost.application.service.command.`in`.CreateLostPostCommand
import backend.team.ahachul_backend.api.lost.application.service.command.`in`.SearchLostPostCommand
import backend.team.ahachul_backend.api.lost.application.service.command.`in`.UpdateLostPostCommand
import backend.team.ahachul_backend.api.lost.application.service.command.`in`.UpdateLostPostStatusCommand
import backend.team.ahachul_backend.api.lost.application.service.command.out.GetRecommendLostPostsCommand
import backend.team.ahachul_backend.api.lost.application.service.command.out.GetSliceLostPostsCommand
import backend.team.ahachul_backend.api.common.domain.entity.StationEntity
import backend.team.ahachul_backend.api.lost.domain.entity.CategoryEntity
import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import backend.team.ahachul_backend.api.lost.domain.entity.LostPostFileEntity
import backend.team.ahachul_backend.api.lost.domain.model.LostOrigin
import backend.team.ahachul_backend.api.lost.domain.model.LostPostType
import backend.team.ahachul_backend.api.member.application.port.out.MemberReader
import backend.team.ahachul_backend.common.domain.entity.SubwayLineEntity
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
class LostPostService(
    private val lostPostWriter: LostPostWriter,
    private val lostPostReader: LostPostReader,
    private val lostPostFileReader: LostPostFileReader,
    private val lostPostFileService: LostPostFileService,
    private val subwayLineReader: SubwayLineReader,
    private val stationReader: StationReader,
    private val subwayLineStationReader: SubwayLineStationReader,
    private val memberReader: MemberReader,
    private val categoryReader: CategoryReader,
    private val commentReader: CommentReader,
): LostPostUseCase {

    override fun getLostPost(id: Long): GetLostPostDto.Response {
        val entity = lostPostReader.getLostPost(id)

        if (entity.type == LostPostType.DELETED) {
            throw CommonException(ResponseCode.POST_NOT_FOUND)
        }

        val recommendPosts = getRecommendPosts(entity.subwayLine, entity.category)
        val recommendPostsDto = mapRecommendPostsDto(recommendPosts)
        val commentCnt = commentReader.countLost(id)

        if (entity.origin == LostOrigin.LOST112) {
            return GetLostPostDto.Response.of(entity, commentCnt, listOf(), recommendPostsDto)
        }

        val files = lostPostFileReader.findAllByPostId(id)
        return GetLostPostDto.Response.of(entity, commentCnt, convertToImageDto(files), recommendPostsDto)
    }

    private fun getRecommendPosts(subwayLine: SubwayLineEntity?, category:CategoryEntity?): List<LostPostEntity> {
        if (subwayLine === null || category === null) {
            return listOf()
        }

        val command = GetRecommendLostPostsCommand.from(DEFAULT_RECOMMEND_SIZE, subwayLine, category)
        val recommendPosts = lostPostReader.getRecommendLostPosts(command)

        if (recommendPosts.size >= DEFAULT_RECOMMEND_SIZE) {
            return recommendPosts
        }

        val randomPosts = getRandomPostIfNotDefaultSize(recommendPosts.size, subwayLine, category)
        return recommendPosts.plus(randomPosts)
    }

    private fun getRandomPostIfNotDefaultSize(
        recommendPostSize: Int, subwayLine: SubwayLineEntity?, category: CategoryEntity
    ): List<LostPostEntity> {
        val randomCommand = GetRecommendLostPostsCommand.from(
            DEFAULT_RECOMMEND_SIZE - recommendPostSize, subwayLine, category
        )
        return lostPostReader.getRandomLostPosts(randomCommand)
    }

    private fun mapRecommendPostsDto(recommendPosts: List<LostPostEntity>): List<GetLostPostDto.RecommendResponse> {
        val recommendPostsDto = recommendPosts.map { post ->
            GetLostPostDto.RecommendResponse(
                id = post.id,
                title = post.title,
                writer = post.createdBy,
                imageUrl = getFileSource(post),
                createdAt = post.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            )
        }
        return recommendPostsDto
    }

    private fun convertToImageDto(lostPostFiles: List<LostPostFileEntity>): List<ImageDto> {
        return lostPostFiles.map {
            ImageDto.of(
                imageId = it.id,
                imageUrl = it.file!!.filePath
            )
        }
    }

    override fun searchLostPosts(command: SearchLostPostCommand): PageInfoDto<SearchLostPostsDto.Response> {
        val subwayLineIds = resolveSubwayLineIds(command.subwayLineIds, command.stationId)
        val subwayLines = subwayLineIds?.stream()
            ?.map { subwayLineReader.getById(it) }
            ?.toList()

        val category = command.category?.let { categoryReader.getCategoryByName(it) }

        val lostPostList = lostPostReader.getLostPosts(
            GetSliceLostPostsCommand.from(
                command=command, subwayLines=subwayLines, category=category
            )
        )
        val commentCountMap = commentReader.countLostByPostIds(lostPostList.map { it.id })

        val searchLostPostsDtoList = lostPostList.map {
            SearchLostPostsDto.Response(
                id = it.id,
                title = it.title,
                content = it.content,
                writer = it.member?.nickname,
                createdBy = it.createdBy,
                createdAt = it.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
                subwayLineId = it.subwayLine?.id,
                stationId = it.station?.id,
                commentCnt = commentCountMap[it.id] ?: 0,
                status = it.status,
                imageUrl = getFileSource(it),
                categoryName = it.category?.name
            )
        }

        return PageInfoDto.of(
            data=searchLostPostsDtoList,
            pageSize=command.pageSize,
            arrayOf(SearchLostPostsDto.Response::createdAt, SearchLostPostsDto.Response::id)
        )
    }

    private fun getFileSource(lostPost: LostPostEntity): String? {
        if (lostPost.origin == LostOrigin.LOST112) {
            return lostPost.externalSourceFileUrl
        }

        val lostPostFile = lostPostFileReader.findByPostId(lostPost.id)
        return lostPostFile?.file?.filePath
    }

    @Transactional
    override fun createLostPost(command: CreateLostPostCommand): CreateLostPostDto.Response {
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!
        val member = memberReader.getMember(memberId.toLong())
        val subwayLine = subwayLineReader.getById(command.subwayLine)
        val station = resolveStation(command.subwayLine, command.stationId)
        val category = command.categoryName?.let { categoryReader.getCategoryByName(it) }

        val entity = lostPostWriter.save(
            LostPostEntity.of(
                command = command,
                member = member,
                subwayLine = subwayLine,
                category = category,
                station = station,
            )
        )

        val images = command.imageFiles?.let {
            lostPostFileService.createLostPostFiles(entity, command.imageFiles!!)
        }
        return CreateLostPostDto.Response.from(entity.id, images)
    }

    @Transactional
    override fun updateLostPost(command: UpdateLostPostCommand): UpdateLostPostDto.Response {
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!
        val entity = lostPostReader.getLostPost(command.id)
        entity.checkMe(memberId)

        val subwayLine = command.subwayLineId?.let {
            subwayLineReader.getById(it)
        }
        val targetSubwayLineId = subwayLine?.id ?: entity.subwayLine?.id
        val targetStationId = when {
            command.stationId != null -> command.stationId
            command.subwayLineId != null -> null
            else -> entity.station?.id
        }

        val category = command.categoryName?.let {
            categoryReader.getCategoryByName(it)
        }

        val station = targetSubwayLineId?.let { resolveStation(it, targetStationId) }

        entity.update(command, subwayLine, category, station)
        updateImageFiles(command, entity)
        return UpdateLostPostDto.Response.from(entity)
    }

    private fun updateImageFiles(command: UpdateLostPostCommand, post: LostPostEntity) {
        command.imageFiles?.let {
            lostPostFileService.createLostPostFiles(post, it)
        }

        command.removeFileIds?.let {
            lostPostFileService.deleteLostPostFiles(it)
        }
    }

    @Transactional
    override fun updateLostPostStatus(command: UpdateLostPostStatusCommand): UpdateLostPostStatusDto.Response {
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!
        val entity = lostPostReader.getLostPost(command.id)

        if (entity.origin == LostOrigin.LOST112) {
            throw CommonException(ResponseCode.BAD_REQUEST)
        }

        entity.checkMe(memberId)

        entity.updateStatus(command.status)
        return UpdateLostPostStatusDto.Response.from(entity)
    }

    @Transactional
    override fun deleteLostPost(id: Long): DeleteLostPostDto.Response {
        val memberId = RequestUtils.getAttribute(RequestUtils.Attribute.MEMBER_ID)!!
        val entity = lostPostReader.getLostPost(id)
        entity.checkMe(memberId)
        entity.delete()
        return DeleteLostPostDto.Response.from(entity)
    }

    companion object {
        const val DEFAULT_RECOMMEND_SIZE = 12L
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
