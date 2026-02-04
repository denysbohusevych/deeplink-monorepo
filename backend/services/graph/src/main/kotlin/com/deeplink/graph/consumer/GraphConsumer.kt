package com.deeplink.graph.consumer

import com.deeplink.domain.v1.Person
import com.deeplink.graph.mapper.PersonMapper
import com.deeplink.graph.repository.PersonRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class GraphConsumer(
    private val personRepository: PersonRepository,
    private val personMapper: PersonMapper
) {
    private val logger = LoggerFactory.getLogger(GraphConsumer::class.java)

    // Слушаем топик 'ingestion.raw.fake' (для теста)
    @KafkaListener(topics = ["ingestion.raw.fake"])
    fun consumePerson(message: ByteArray) {
        try {
            logger.info("📨 Received message from Kafka (${message.size} bytes)")

            // 1. Десериализация Protobuf
            val personProto = Person.parseFrom(message)

            logger.info("👤 Processing Person: ${personProto.displayName}")

            // 2. Маппинг и Сохранение (та же логика, что в gRPC)
            val node = personMapper.toNode(personProto)
            personRepository.save(node)

            logger.info("✅ Saved to Neo4j with ID: ${node.id}")

        } catch (e: Exception) {
            logger.error("❌ Failed to process message", e)
        }
    }
}