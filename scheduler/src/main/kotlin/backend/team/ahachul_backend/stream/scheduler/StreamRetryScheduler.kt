package backend.team.ahachul_backend.stream.scheduler

import backend.team.ahachul_backend.common.client.RedisClient
import backend.team.ahachul_backend.common.logging.Logger
import backend.team.ahachul_backend.stream.service.Lost112Service
import org.springframework.beans.factory.InitializingBean
import org.springframework.data.redis.connection.stream.*
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * 1. 방안 1 - 실패한 것들은 특정 자료구조에 저장(예약 시간과 함께) & 아예 메시지 삭제
 * - 별도 스프링 스케줄러에서 1분마다 자료구조를 탐색하면서 예약 시간이 된 것들을 꺼내서 다시 원본 스트림으로 produce
 *
 * 2. 방안 2(pending DLQ?) - ACK를 안날리면 자동으로 pending 처리가 돼서 다시 시도할 수 있음
 * - 근데 계속 시도하는 건 의미가 없으니까, 시간을 늘려가면서 회복할 시간을 주고 재시도 하는게 합리적
 *
 *  스케줄러에서 주기적으로 각 “재시도 회차(deliveryCount)”에 맞는 최소 유휴시간(minIdle)을 계산해서 XAUTOCLAIM 호출.
 *  elapsedTimeSinceLastDelivery : 메시지가 소비자에게 마지막으로 전달된 이후 경과된 시간(밀리초 단위)
 *  새로 XAUTOCLAIM(또는 XCLAIM) 하면 그 순간 소유권이 바뀌고 idle=0 으로 리셋됨.
 *  deliveredCount(=재시도 회차) : 같은 메시지가 다시 전달/클레임 될 때마다 Redis가 자동으로 +1 해줌
 *  XAUTOCLAIM(minIdle=...) : “이 스트림에서 idle ≥ minIdle인 pending 메시지만 내게 주세요.” 다음 재시도까지 기다려야 하는 최소 대기시간
 *
 *  deliveryCount = X(1)인 애들 & idle >= backOff인 애들만 찾는다. 리트 -> 1분
 *  deliveryCount = X(2)인 애들 & idle >= backOff인 애들만 찾는다. 리트 -> 2분
 *  deliveryCount = X(3)인 애들 & idle >= backOff인 애들만 찾는다. 리트 -> 4분
 *  deliveryCount = X(4)인 애들 & idle >= backOff인 애들만 찾는다. 리트 -> 8분
 *  deliveryCount = X(5)인 애들 & idle >= backOff인 애들만 찾는다. 리트 -> 16분
 */
@Component
class StreamRetryScheduler(
    private val redisClient: RedisClient,
    private val lostPostUtil: Lost112Service
): InitializingBean {

    private val logger = Logger(javaClass)
    private lateinit var streamKey: String
    private lateinit var consumerGroupName: String
    private lateinit var consumerName: String
    private val maxRetry = 5

    @Scheduled(fixedDelay = 60000)
    fun reclaimAndRetry() {
        // pending 상태인 메시지들을 가져온다.
        val pendingMessages: PendingMessages = redisClient.findPendingMessages(
            streamKey, consumerGroupName, consumerName
        )

        if (pendingMessages.isEmpty) {
            return
        }

        // 여유가 있는 컨슈머들을 찾아서 XAUTOCLAIM 을 통해 소유권을 이전하고 처리 후 ack를 날린다. (기존 consumer가 죽으면 ack 불가)
        val idleConsumers = redisClient.pickIdleConsumers(3, streamKey, consumerGroupName)
            .ifEmpty { listOf(consumerName) }

        for (attempt in 1.. maxRetry) {  // 최대 리트 횟수를 5번이라고 했을 때
            val minutes = 1L * (1L shl (attempt - 1))
            val backOff = Duration.ofMinutes(minutes)

            val eligible: List<PendingMessage> = pendingMessages
                .filter { it.totalDeliveryCount == attempt.toLong() && it.elapsedTimeSinceLastDelivery >= backOff }
                .toList()

            if (eligible.isEmpty()) {
                continue
            }

            // 특정 회차에 도달하면(예: maxRetry 초과) 알림만 보내고 재시도(Claim) 생략 -> 개발자 수동 처리 정책
            if (attempt >= maxRetry) {
                logger.error("Exceeded max retry. Pending IDs: ${eligible.map { it.id }.joinToString()}")
                continue
            }

            val targetConsumer = Consumer.from(consumerGroupName, idleConsumers[0])
            for (pendingMessage: PendingMessage in eligible) {
                val message: MapRecord<String, String, String>? = redisClient
                    .findStreamMessageById(streamKey, pendingMessage.idAsString)

                runCatching {
                    message?.value?.let {
                        lostPostUtil.convertAndSaveLostPost(it)
                    }
                }.onSuccess {
                    redisClient.claimStreamMessage(streamKey, targetConsumer, backOff, pendingMessage.id)
                    redisClient.ackStream(streamKey, consumerGroupName, pendingMessage.id)
                }.onFailure {
                    logger.error("Process failed. Keep pending. id=${pendingMessage.id}}", it)
                }
            }
        }
    }

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        streamKey = "lostpost-stream"
        consumerGroupName = "ahachul"
        consumerName = "ahachul-server"
    }

}
