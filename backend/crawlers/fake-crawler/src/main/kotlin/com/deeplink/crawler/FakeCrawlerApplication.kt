package com.deeplink.crawler

import com.deeplink.common.v1.FuzzyDate
import com.deeplink.domain.v1.Gender
import com.deeplink.domain.v1.Person
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import java.util.UUID

@SpringBootApplication
class FakeCrawlerApplication : CommandLineRunner {
    private val logger = LoggerFactory.getLogger(FakeCrawlerApplication::class.java)

    // Внедряем шаблон Kafka для отправки сообщений
    // Ключ - String, Значение - ByteArray (Protobuf)
    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, ByteArray> {
        return KafkaTemplate(producerFactory())
    }

    // Конфигурация продюсера (обычно в application.yml, но для скрипта можно и тут)
    fun producerFactory(): ProducerFactory<String, ByteArray> {
        val configProps = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:19092",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to ByteArraySerializer::class.java
        )
        return DefaultKafkaProducerFactory(configProps)
    }

    override fun run(vararg args: String?) {
        val template = kafkaTemplate()
        val topic = "ingestion.raw.fake"

        logger.info("🚀 Starting Fake Kotlin Crawler...")

        for (i in 1..5) {
            // 1. Создаем объект Person используя сгенерированный Protobuf Builder
            val person = Person.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setDisplayName("Kotlin Generated User #$i")
                .setNotes("Created by Kotlin Fake Crawler")
                .setGender(if (i % 2 == 0) Gender.GENDER_FEMALE else Gender.GENDER_MALE)
                .setBirthDate(
                    FuzzyDate.newBuilder()
                        .setYear(1990 + i)
                        .setOriginalString("Early 90s")
                        .build()
                )
                .addAliases("Bot_$i")
                .build()

            // 2. Сериализуем в байты
            val bytes = person.toByteArray()

            // 3. Отправляем в Kafka
            logger.info("📤 Sending person: ${person.displayName}")
            template.send(topic, person.id, bytes)

            Thread.sleep(1000) // Пауза для наглядности
        }

        logger.info("✅ Done. Exiting.")
    }
}

fun main(args: Array<String>) {
    runApplication<FakeCrawlerApplication>(*args)
}