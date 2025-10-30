package ru.pachan.main_kotlin.service.writer

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.pachan.main_kotlin.dto.writer.WriterDto

@Service
class IncreaseCount(
    private val kafkaTemplate: KafkaTemplate<String, WriterDto>,
    private val topicName: String,
) {

    @Scheduled(
        fixedDelayString = "\${task.schedule.increase-count.delay}",
        initialDelayString = "\${task.schedule.increase-count.initialDelay}",
    )
    @SchedulerLock(
        name = "TaskScheduler_increaseCount",
        lockAtLeastFor = "9s", // EXPLAIN_V минимальное время блокировки
        lockAtMostFor = "20s", // EXPLAIN_V максимальное время блокировки (должно быть больше времени выполнения работы)
    )
    fun increaseCount() {
        kafkaTemplate.send(topicName, WriterDto(personId = 1L, count = 1))
    }
}