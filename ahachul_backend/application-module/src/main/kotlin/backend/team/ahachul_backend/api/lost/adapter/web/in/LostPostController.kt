package backend.team.ahachul_backend.api.lost.adapter.web.`in`

import backend.team.ahachul_backend.api.lost.adapter.web.`in`.dto.*
import backend.team.ahachul_backend.api.lost.application.port.`in`.LostPostUseCase
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.dto.PageInfoDto
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
class LostPostController(
    private val lostPostService: LostPostUseCase
) {

    @GetMapping("/v1/lost-posts/{lostId}")
    fun getLostPost(@PathVariable("lostId") lostId: Long): CommonResponse<GetLostPostDto.Response> {
        return CommonResponse.success(lostPostService.getLostPost(lostId))
    }

    @GetMapping("/v1/lost-posts")
    fun searchLostPosts(
        @RequestParam(required = false) pageToken: String?,
        @RequestParam pageSize: Int,
        request: SearchLostPostsDto.Request
    ): CommonResponse<PageInfoDto<SearchLostPostsDto.Response>> {
        return CommonResponse.success(lostPostService.searchLostPosts(request.toCommand(pageToken, pageSize)))
    }

    @Authentication
    @PostMapping("/v1/lost-posts")
    fun createLostPost(
        @RequestPart(value = "content") request: CreateLostPostDto.Request,
        @RequestPart(value = "files", required = false) imageFiles: List<MultipartFile>?
    ): CommonResponse<CreateLostPostDto.Response> {
        return CommonResponse.success(lostPostService.createLostPost(request.toCommand(imageFiles)))
    }

    @Authentication
    @PostMapping("/v1/lost-posts/{lostId}")
    fun updateLostPost(
        @PathVariable("lostId") lostId: Long,
        @RequestPart(value = "content") request: UpdateLostPostDto.Request,
        @RequestPart(value = "files", required = false) imageFiles: List<MultipartFile>?
    ): CommonResponse<UpdateLostPostDto.Response> {
        return CommonResponse.success(lostPostService.updateLostPost(request.toCommand(lostId, imageFiles)))
    }

    @Authentication
    @DeleteMapping("/v1/lost-posts/{lostId}")
    fun deleteLostPost(@PathVariable("lostId") lostId: Long): CommonResponse<DeleteLostPostDto.Response> {
        return CommonResponse.success(lostPostService.deleteLostPost(lostId))
    }
}
