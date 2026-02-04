package com.deeplink.graph.mapper

import com.deeplink.common.v1.FuzzyDate
import com.deeplink.common.v1.Origin
import com.deeplink.domain.v1.Person
import com.deeplink.domain.v1.Gender
import com.deeplink.graph.domain.Neo4jFuzzyDate
import com.deeplink.graph.domain.Neo4jOrigin
import com.deeplink.graph.domain.PersonNode
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class PersonMapper {

    fun toNode(proto: Person): PersonNode {
        return PersonNode(
            id = proto.id.ifEmpty { java.util.UUID.randomUUID().toString() },
            displayName = if (proto.displayName.isEmpty()) null else proto.displayName,
            aliases = proto.aliasesList,
            notes = if (proto.notes.isEmpty()) null else proto.notes,

            // Map Gender Enum to String
            gender = if (proto.gender != Gender.GENDER_UNSPECIFIED) proto.gender.name else null,

            nationalities = proto.nationalitiesList,

            // Map Complex Objects
            birthDate = if (proto.hasBirthDate()) toNeo4jDate(proto.birthDate) else null,
            deathDate = if (proto.hasDeathDate()) toNeo4jDate(proto.deathDate) else null,

            biometricsRef = if (proto.biometricsRef.isEmpty()) null else proto.biometricsRef,

            origin = if (proto.hasOrigin()) toNeo4jOrigin(proto.origin) else null
        )
    }

    fun toProto(node: PersonNode): Person {
        val builder = Person.newBuilder()
            .setId(node.id)
            .addAllAliases(node.aliases)
            .addAllNationalities(node.nationalities)

        node.displayName?.let { builder.displayName = it }
        node.notes?.let { builder.notes = it }

        node.gender?.let {
            try {
                builder.gender = Gender.valueOf(it)
            } catch (e: Exception) {
                builder.gender = Gender.GENDER_UNSPECIFIED
            }
        }

        node.birthDate?.let { builder.birthDate = toProtoDate(it) }
        node.deathDate?.let { builder.deathDate = toProtoDate(it) }
        node.biometricsRef?.let { builder.biometricsRef = it }
        node.origin?.let { builder.origin = toProtoOrigin(it) }

        return builder.build()
    }

    // --- Helper Methods ---

    private fun toNeo4jDate(proto: FuzzyDate): Neo4jFuzzyDate {
        return Neo4jFuzzyDate(
            year = if (proto.year != 0) proto.year else null,
            month = if (proto.month != 0) proto.month else null,
            day = if (proto.day != 0) proto.day else null,
            originalString = if (proto.originalString.isNotEmpty()) proto.originalString else null
        )
    }

    private fun toProtoDate(neo: Neo4jFuzzyDate): FuzzyDate {
        val builder = FuzzyDate.newBuilder()
        neo.year?.let { builder.year = it }
        neo.month?.let { builder.month = it }
        neo.day?.let { builder.day = it }
        neo.originalString?.let { builder.originalString = it }
        return builder.build()
    }

    private fun toNeo4jOrigin(proto: Origin): Neo4jOrigin {
        return Neo4jOrigin(
            sourceArtifactId = if (proto.sourceArtifactId.isNotEmpty()) proto.sourceArtifactId else null,
            provider = if (proto.provider.isNotEmpty()) proto.provider else null,
            collectedAt = if (proto.collectedAtUnix > 0) Instant.ofEpochSecond(proto.collectedAtUnix) else null
        )
    }

    private fun toProtoOrigin(neo: Neo4jOrigin): Origin {
        val builder = Origin.newBuilder()
        neo.sourceArtifactId?.let { builder.sourceArtifactId = it }
        neo.provider?.let { builder.provider = it }
        neo.collectedAt?.let { builder.collectedAtUnix = it.epochSecond }
        return builder.build()
    }
}