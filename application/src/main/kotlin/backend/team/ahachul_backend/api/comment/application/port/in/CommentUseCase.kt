package backend.team.ahachul_backend.api.comment.application.port.`in`

import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.CreateCommentDto
import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.DeleteCommentDto
import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.GetCommentsDto
import backend.team.ahachul_backend.api.comment.adapter.web.`in`.dto.UpdateCommentDto
import backend.team.ahachul_backend.api.comment.application.command.CreateCommentCommand
import backend.team.ahachul_backend.api.comment.application.command.DeleteCommentCommand
import backend.team.ahachul_backend.api.comment.application.command.GetCommentsCommand
import backend.team.ahachul_backend.api.comment.application.command.UpdateCommentCommand

interface CommentUseCase {

    fun getComments(command: GetCommentsCommand): GetCommentsDto.Response

    fun createComment(command: CreateCommentCommand): CreateCommentDto.Response

    fun updateComment(command: UpdateCommentCommand): UpdateCommentDto.Response

    fun deleteComment(command: DeleteCommentCommand): DeleteCommentDto.Response
}