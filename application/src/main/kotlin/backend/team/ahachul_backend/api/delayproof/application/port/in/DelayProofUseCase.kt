package backend.team.ahachul_backend.api.delayproof.application.port.`in`

import backend.team.ahachul_backend.api.delayproof.adapter.`in`.dto.DelayProofDto
import backend.team.ahachul_backend.api.delayproof.application.port.`in`.command.CreateDelayProofCommand
import backend.team.ahachul_backend.api.delayproof.application.port.`in`.command.GetCommunityDelaySignalsCommand
import backend.team.ahachul_backend.api.delayproof.application.port.`in`.command.GetSubwayIncidentsCommand

interface DelayProofUseCase {

    fun createDelayProof(command: CreateDelayProofCommand): DelayProofDto.CreateResponse

    fun getDelayProof(proofId: String): DelayProofDto.GetResponse

    fun getSubwayIncidents(command: GetSubwayIncidentsCommand): DelayProofDto.GetSubwayIncidentsResponse

    fun getCommunityDelaySignals(command: GetCommunityDelaySignalsCommand): DelayProofDto.GetCommunityDelaySignalsResponse
}
