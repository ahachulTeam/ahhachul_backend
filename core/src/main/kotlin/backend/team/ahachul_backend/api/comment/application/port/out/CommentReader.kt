package backend.team.ahachul_backend.api.comment.application.port.out

import backend.team.ahachul_backend.api.comment.application.command.GetCommentsCommand
import backend.team.ahachul_backend.api.comment.domain.SearchComment
import backend.team.ahachul_backend.api.comment.domain.entity.CommentEntity

interface CommentReader {

    fun getById(id: Long): CommentEntity

    fun findById(id: Long): CommentEntity?

    fun countCommunity(postId: Long): Int

    fun countLost(postId: Long): Int

    fun countComplaint(postId: Long): Int

    fun searchComments(command: GetCommentsCommand): List<SearchComment>
}