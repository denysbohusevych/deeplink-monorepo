package com.deeplink.graph.mapper

import com.deeplink.domain.v1.Person
import com.deeplink.domain.v1.Gender
import com.deeplink.graph.domain.PersonNode
import org.springframework.stereotype.Component

@Component
class PersonMapper {

    // Превращаем входящий gRPC запрос в сущность базы данных
    fun toNode(proto: Person): PersonNode {
        return PersonNode(
            id = proto.id.ifEmpty { java.util.UUID.randomUUID().toString() }, // Если ID не пришел, генерируем новый
            displayName = proto.displayName,
            notes = proto.notes,
            aliases = proto.aliasesList,
            // Простой маппинг Enum в String для базы
            gender = if (proto.gender != Gender.GENDER_UNSPECIFIED) proto.gender.name else null
        )
    }

    // Превращаем сущность из базы обратно в gRPC ответ
    fun toProto(node: PersonNode): Person {
        val builder = Person.newBuilder()
            .setId(node.id)
            .addAllAliases(node.aliases)

        node.displayName?.let { builder.displayName = it }
        node.notes?.let { builder.notes = it }

        // Восстанавливаем Enum из строки
        node.gender?.let {
            try {
                builder.gender = Gender.valueOf(it)
            } catch (e: IllegalArgumentException) {
                builder.gender = Gender.GENDER_UNSPECIFIED
            }
        }

        return builder.build()
    }
}