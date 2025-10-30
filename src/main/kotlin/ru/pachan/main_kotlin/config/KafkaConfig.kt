package ru.pachan.main_kotlin.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.JacksonUtils
import org.springframework.kafka.support.serializer.JsonSerializer
import ru.pachan.main_kotlin.dto.writer.WriterDto

@Configuration
class KafkaConfig {

    @Value("\${application.kafka.topic}")
    private lateinit var topicName: String

    @Value("\${spring.kafka.producer.bootstrap-servers}")
    private lateinit var servers: String

    @Bean
    fun objectMapper(): ObjectMapper? {
        return JacksonUtils.enhancedObjectMapper()
    }

    @Bean
    fun producerFactory(kafkaProperties: KafkaProperties, mapper: ObjectMapper): ProducerFactory<String, WriterDto> {
        val props = kafkaProperties.buildProducerProperties(null)
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java

        val kafkaProducerFactory: DefaultKafkaProducerFactory<String, WriterDto> = DefaultKafkaProducerFactory(props)
        kafkaProducerFactory.valueSerializer = JsonSerializer(mapper)
        return kafkaProducerFactory
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, WriterDto>): KafkaTemplate<String, WriterDto> {
        return KafkaTemplate(producerFactory)
    }

    @Bean
    fun topicName(): String {
        return TopicBuilder.name(topicName).partitions(1).replicas(1).build().name()
    }
}