package backend.team.ahachul_backend.schedule.config

import backend.team.ahachul_backend.common.logging.Logger
import backend.team.ahachul_backend.schedule.listener.JobFailureHandlingListener
import jakarta.annotation.PostConstruct
import org.quartz.*
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.util.concurrent.TimeUnit


@Configuration
@Profile("dev")
class ScheduleConfig(
    private val scheduler: Scheduler
){

    val logger = Logger(javaClass)
    val jobMap: MutableMap<JobDetail, Set<Trigger>> = mutableMapOf()

    @PostConstruct
    fun init() {
        try {
            setJobTriggerMap()
            setTriggerListener()
            scheduler.scheduleJobs(jobMap, true)
        } catch (e: SchedulerException) {
            TimeUnit.MINUTES.sleep(1)
            throw JobExecutionException(true)
        }
    }

    /**
     * Init 메서드
     * JobConfig 을 구현하고 있는 자식 클래스들을 가져온다.
     */
    private fun setJobTriggerMap() {
        val reflections = Reflections(
                ConfigurationBuilder()
                    .addScanners(Scanners.SubTypes.filterResultsBy { true })
                    .forPackage("backend.team.ahachul_backend.schedule.config")
        )

        reflections.getSubTypesOf(AbstractJobConfig::class.java).forEach {
            val ob = it.getDeclaredConstructor().newInstance()
            val pair = ob.getJobTriggerPair()
            jobMap[pair.first] = pair.second
        }
        println(jobMap)
    }

    private fun setTriggerListener() {
        val listenerManager = scheduler.listenerManager
        listenerManager.addTriggerListener(JobFailureHandlingListener())
    }

}
