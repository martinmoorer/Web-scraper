package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "research_topics")
data class TopicEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val status: String = "IDLE", // IDLE, QUEUED, SCRAPING, CLASSIFYING, SYNTHESIZING, COMPLETED, FAILED
    val progress: Int = 0,
    val currentStep: String = "Ready",
    val pagesScrapedCount: Int = 0,
    val itemsClassifiedCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "scraped_pages")
data class ScrapedPageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val topicId: String,
    val url: String,
    val title: String,
    val snippet: String,
    val content: String,
    val scrapedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "classified_items")
data class ClassifiedItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val topicId: String,
    val categoryKey: String, // causes, effects, solutions, organizations, metrics
    val title: String,
    val content: String,
    val entitiesJson: String = "",
    val metricsJson: String = "",
    val confidenceScore: Float = 0.95f,
    val sourceUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "thesis_segments")
data class ThesisSegmentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val topicId: String,
    val orderIndex: Int,
    val sectionType: String, // ABSTRACT, INTRODUCTION, CAUSES, IMPACTS, SOLUTIONS, SYNTHESIS, CONCLUSION
    val title: String,
    val content: String,
    val takeawaysJson: String = "",
    val confidenceRating: Float = 0.92f
)

@Entity(tableName = "mind_map_nodes")
data class MindMapNodeEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val topicId: String,
    val parentId: String? = null,
    val label: String,
    val detail: String? = null,
    val level: Int = 0,
    val posX: Float = 0f,
    val posY: Float = 0f,
    val categoryKey: String? = null
)

@Entity(tableName = "scraper_logs")
data class ScraperLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val topicId: String,
    val tag: String,
    val message: String,
    val isError: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
