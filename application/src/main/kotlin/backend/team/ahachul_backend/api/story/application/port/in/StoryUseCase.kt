package backend.team.ahachul_backend.api.story.application.port.`in`

import backend.team.ahachul_backend.api.story.adapter.`in`.dto.StoryDto
import backend.team.ahachul_backend.api.story.application.port.`in`.dto.CreateStoryCommand
import backend.team.ahachul_backend.api.story.application.port.`in`.dto.GetMemberStoriesCommand

interface StoryUseCase {
    fun getMyStories(limit: Int): StoryDto.ProfileStoriesResponse

    fun getMemberStories(command: GetMemberStoriesCommand): StoryDto.ProfileStoriesResponse

    fun getPublicStories(limit: Int, stationId: Long?, subwayLineId: Long?): StoryDto.PublicStoriesResponse

    fun createStory(command: CreateStoryCommand): StoryDto.CreateResponse

    fun deleteStory(storyId: Long): StoryDto.DeleteResponse
}
