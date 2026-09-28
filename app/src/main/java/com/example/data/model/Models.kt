package com.example.data.model

import java.util.UUID

enum class TopicStatus {
    IDLE,
    QUEUED,
    SCRAPING,
    CLASSIFYING,
    SYNTHESIZING,
    COMPLETED,
    FAILED
}

data class TaxonomyCategory(
    val key: String,
    val displayName: String,
    val description: String,
    val colorHex: String
)

object PredefinedCategories {
    val CAUSES = TaxonomyCategory(
        key = "causes",
        displayName = "Causes & Drivers",
        description = "Root factors, systemic causes, origins, and primary triggers",
        colorHex = "#EF4444"
    )
    val EFFECTS = TaxonomyCategory(
        key = "effects",
        displayName = "Observed Impacts & Risks",
        description = "Downstream consequences, environmental/economic impacts, and risks",
        colorHex = "#F59E0B"
    )
    val SOLUTIONS = TaxonomyCategory(
        key = "solutions",
        displayName = "Solutions & Mitigation",
        description = "Technological solutions, policies, frameworks, and actionable responses",
        colorHex = "#10B981"
    )
    val ORGANIZATIONS = TaxonomyCategory(
        key = "organizations",
        displayName = "Key Organizations & Actors",
        description = "Institutions, research bodies, NGOs, key pioneers, and governing bodies",
        colorHex = "#3B82F6"
    )
    val METRICS = TaxonomyCategory(
        key = "metrics",
        displayName = "Empirical Data & Metrics",
        description = "Quantitative statistics, historical trends, rates, and measurements",
        colorHex = "#8B5CF6"
    )

    val ALL = listOf(CAUSES, EFFECTS, SOLUTIONS, ORGANIZATIONS, METRICS)

    fun find(key: String): TaxonomyCategory {
        return ALL.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: TaxonomyCategory(
            key = key,
            displayName = key.replaceFirstChar { it.uppercase() },
            description = "Topical research findings",
            colorHex = "#00E5FF"
        )
    }
}

data class EntityReference(
    val name: String,
    val type: String // ORGANIZATION, PERSON, LAW, LOCATION, TECH
)

data class ResearchMetric(
    val label: String,
    val value: String,
    val context: String? = null
)

data class ScraperLog(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val tag: String,
    val message: String,
    val isError: Boolean = false
)
