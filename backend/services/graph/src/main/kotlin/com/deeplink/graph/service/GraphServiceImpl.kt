package com.deeplink.graph.service

import com.deeplink.graph.v1.GraphServiceGrpcKt
import com.deeplink.graph.v1.SavePersonRequest
import com.deeplink.graph.v1.SavePersonResponse
import com.deeplink.graph.v1.GetPersonRequest
import com.deeplink.graph.v1.GetPersonResponse
import com.deeplink.graph.mapper.PersonMapper
import com.deeplink.graph.repository.PersonRepository
import io.grpc.Status
import io.grpc.StatusException
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.stereotype.Service

@GrpcService
class GraphServiceImpl(
    private val personRepository: PersonRepository,
    private val personMapper: PersonMapper
) : GraphServiceGrpcKt.GraphServiceCoroutineImplBase() {

    override suspend fun savePerson(request: SavePersonRequest): SavePersonResponse {
        // 1. Конвертируем Proto -> Node
        val node = personMapper.toNode(request.person)

        // 2. Сохраняем в базу (Repository save блокирующий, но в MVP допустимо)
        // В идеале использовать withContext(Dispatchers.IO) для блокирующих вызовов
        val savedNode = personRepository.save(node)

        // 3. Формируем ответ
        return SavePersonResponse.newBuilder()
            .setId(savedNode.id)
            // .setOrigin(...) - пока пропустим, так как в Node еще нет поля Origin
            .build()
    }

    override suspend fun getPerson(request: GetPersonRequest): GetPersonResponse {
        // 1. Ищем в базе
        val nodeOptional = personRepository.findById(request.id)

        if (nodeOptional.isEmpty) {
            throw StatusException(Status.NOT_FOUND.withDescription("Person with ID ${request.id} not found"))
        }

        // 2. Конвертируем Node -> Proto
        val protoPerson = personMapper.toProto(nodeOptional.get())

        return GetPersonResponse.newBuilder()
            .setPerson(protoPerson)
            .build()
    }
}