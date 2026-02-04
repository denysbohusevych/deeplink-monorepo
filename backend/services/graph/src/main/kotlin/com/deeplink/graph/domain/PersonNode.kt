package com.deeplink.graph.domain
import com.deeplink.common.v1.FuzzyDate


import org.springframework.data.annotation.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Property

/**
 * Represents a "Person" node in the Neo4j database.
 * This class mirrors the protobuf Person message but is adapted for OGM (Object Graph Mapping).
 */
@Node("Person")
data class PersonNode(
    // We use the same UUID as in the protobuf message
    @Id
    val id: String,

    @Property("display_name")
    val displayName: String?,

    // Storing notes as a simple property
    @Property("notes")
    val notes: String?,

    // Storing aliases as a list of strings
    @Property("aliases")
    val aliases: List<String> = emptyList(),

    // We will map other complex fields (like FuzzyDate) later using Converters,
    // for now, let's keep it simple to test the connection.
    @Property("gender")
    val gender: String?
)