package com.deeplink.graph.domain

import java.time.Instant

/**
 * Represents a fuzzy date (e.g. "1990", "May 1990") in the database.
 * Spring Data Neo4j will flatten this into properties prefixed with the parent field name.
 */
data class Neo4jFuzzyDate(
    val year: Int? = null,
    val month: Int? = null,
    val day: Int? = null,
    val originalString: String? = null
)

/**
 * Metadata about where the data came from.
 */
data class Neo4jOrigin(
    val sourceArtifactId: String? = null,
    val provider: String? = null,
    val collectedAt: Instant? = null
)