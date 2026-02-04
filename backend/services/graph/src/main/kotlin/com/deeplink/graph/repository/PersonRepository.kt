package com.deeplink.graph.repository

import com.deeplink.graph.domain.PersonNode
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.stereotype.Repository

/**
 * Repository for accessing Person nodes in Neo4j.
 * Spring Data Neo4j automatically implements these methods.
 */
@Repository
interface PersonRepository : Neo4jRepository<PersonNode, String> {
    // You can define custom Cypher queries here later
    // fun findByAliasesContaining(alias: String): List<PersonNode>
}