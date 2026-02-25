package backend.team.ahachul_backend.api.foreigner.application.port.out

import backend.team.ahachul_backend.api.foreigner.domain.entity.StationSocialMeetupParticipantEntity

interface StationSocialMeetupParticipantWriter {

    fun save(participant: StationSocialMeetupParticipantEntity): StationSocialMeetupParticipantEntity
}
