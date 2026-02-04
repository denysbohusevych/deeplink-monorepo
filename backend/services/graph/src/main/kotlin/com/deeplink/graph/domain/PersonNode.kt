package com.deeplink.graph.domain

import org.springframework.data.annotation.Id
import org.springframework.data.neo4j.core.schema.CompositeProperty
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Property

@Node("Person")
data class PersonNode(
    @Id
    val id: String,

    // --- Basic Info ---
    @Property("display_name")
    val displayName: String?,

    @Property("aliases")
    val aliases: List<String> = emptyList(),

    @Property("notes")
    val notes: String?,

    // --- Demographics ---
    @Property("gender")
    val gender: String?, // Stored as String (MALE, FEMALE)

    @Property("nationalities")
    val nationalities: List<String> = emptyList(),

    // --- Complex Types (Composite Properties) ---
    // In DB this will look like: birthDate.year, birthDate.month
    @CompositeProperty(prefix = "birthDate.")
    val birthDate: Neo4jFuzzyDate? = null,

    @CompositeProperty(prefix = "deathDate.")
    val deathDate: Neo4jFuzzyDate? = null,

    // --- Technical ---
    @Property("biometrics_ref")
    val biometricsRef: String?,

    // --- Metadata ---
    @CompositeProperty(prefix = "origin.")
    val origin: Neo4jOrigin? = null
)