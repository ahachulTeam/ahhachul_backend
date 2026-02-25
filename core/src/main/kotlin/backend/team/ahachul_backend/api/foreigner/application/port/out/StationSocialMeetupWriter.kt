package backend.team.ahachul_backend.api.foreigner.application.port.out

import backend.team.ahachul_backend.api.foreigner.domain.entity.StationSocialMeetupEntity

interface StationSocialMeetupWriter {

    fun save(meetup: StationSocialMeetupEntity): StationSocialMeetupEntity
}
