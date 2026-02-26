package backend.team.ahachul_backend.api.story.adapter.`in`

import backend.team.ahachul_backend.api.story.adapter.`in`.dto.StoryDto
import backend.team.ahachul_backend.api.story.application.port.`in`.StoryUseCase
import backend.team.ahachul_backend.common.annotation.Authentication
import backend.team.ahachul_backend.common.response.CommonResponse
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class StoryController(
    private val storyUseCase: StoryUseCase,
) {

    @Authentication
    @GetMapping("/v2/stories/me")
    fun getMyStories(
        @RequestParam(required = false, defaultValue = "24") limit: Int,
    ): CommonResponse<StoryDto.ProfileStoriesResponse> {
        return CommonResponse.success(storyUseCase.getMyStories(limit))
    }

    @Authentication(required = false)
    @GetMapping("/v2/members/{nickname}/stories")
    fun getMemberStories(
        @PathVariable nickname: String,
        request: StoryDto.MemberStoriesRequest,
    ): CommonResponse<StoryDto.ProfileStoriesResponse> {
        return CommonResponse.success(storyUseCase.getMemberStories(request.toCommand(nickname)))
    }

    @Authentication
    @PostMapping(
        "/v2/stories",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
    )
    fun createStory(
        @RequestPart("content") request: StoryDto.CreateRequest,
        @RequestPart("image") image: MultipartFile,
    ): CommonResponse<StoryDto.CreateResponse> {
        return CommonResponse.success(storyUseCase.createStory(request.toCommand(image)))
    }

    @Authentication
    @DeleteMapping("/v2/stories/{storyId}")
    fun deleteStory(
        @PathVariable storyId: Long,
    ): CommonResponse<StoryDto.DeleteResponse> {
        return CommonResponse.success(storyUseCase.deleteStory(storyId))
    }
}
