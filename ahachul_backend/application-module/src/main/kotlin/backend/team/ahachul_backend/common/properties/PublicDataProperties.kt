package backend.team.ahachul_backend.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "public-data")
class PublicDataProperties(
    var realTimeStationArrivalPrefixUri: String = "",
    var realTimeStationArrivalSuffixUri: String = "",
    var realTimeStationArrivalToken: String = "",
    var realTimeCongestionUrl: String = "",
    var realTimeCongestionAppKey: String = ""
){
}
