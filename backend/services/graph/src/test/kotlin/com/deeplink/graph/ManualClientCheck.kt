package com.deeplink.graph

import com.deeplink.common.v1.FuzzyDate
import com.deeplink.domain.v1.Gender
import com.deeplink.domain.v1.Person
import com.deeplink.graph.v1.GraphServiceGrpcKt
import com.deeplink.graph.v1.SavePersonRequest
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking

/**
 * Ручной скрипт для проверки работы сервиса.
 * Запускается как обычная программа (функция main), пока запущен основной сервер.
 */
fun main() = runBlocking {
    println("🔌 Подключаемся к локальному gRPC серверу на порту 9090...")

    // 1. Создаем канал связи (Connection)
    val channel = ManagedChannelBuilder.forAddress("localhost", 9090)
        .usePlaintext() // Используем без SSL для локальных тестов
        .build()

    // 2. Создаем клиента (Stub)
    val stub = GraphServiceGrpcKt.GraphServiceCoroutineStub(channel)

    // 3. Подготавливаем тестовые данные (Pavel Durov)
    val person = Person.newBuilder()
        .setDisplayName("Pavel Durov")
        .addAliases("CEO of Telegram")
        .addAliases("Architect")
        .setGender(Gender.GENDER_MALE)
        .setNotes("Created via Manual Client Test")
        .setBirthDate(
            FuzzyDate.newBuilder()
                .setYear(1984)
                .setMonth(10)
                .setDay(10)
                .build()
        )
        .build()

    println("📤 Отправляем запрос SavePerson...")

    try {
        // 4. Вызываем удаленный метод
        val response = stub.savePerson(
            SavePersonRequest.newBuilder()
                .setPerson(person)
                .build()
        )

        println("✅ УСПЕХ! Объект сохранен.")
        println("🆔 Neo4j Node ID: ${response.id}")

    } catch (e: Exception) {
        println("❌ ОШИБКА: ${e.message}")
        e.printStackTrace()
    } finally {
        channel.shutdown()
    }
}