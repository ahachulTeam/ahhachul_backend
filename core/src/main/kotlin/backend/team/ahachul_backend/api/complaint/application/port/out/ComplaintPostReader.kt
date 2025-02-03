package backend.team.ahachul_backend.api.complaint.application.port.out

import backend.team.ahachul_backend.api.complaint.application.command.out.GetSliceComplaintPostsCommand
import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintPostEntity

interface ComplaintPostReader {

    fun getComplaintPost(id: Long): ComplaintPostEntity

    fun getComplaintPosts(command: GetSliceComplaintPostsCommand): List<ComplaintPostEntity>
}