package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ResearchDao {

    // Topics
    @Query("SELECT * FROM research_topics ORDER BY updatedAt DESC")
    fun getAllTopics(): Flow<List<TopicEntity>>

    @Query("SELECT * FROM research_topics WHERE id = :topicId LIMIT 1")
    fun getTopicById(topicId: String): Flow<TopicEntity?>

    @Query("SELECT * FROM research_topics WHERE id = :topicId LIMIT 1")
    suspend fun getTopicByIdOnce(topicId: String): TopicEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: TopicEntity)

    @Update
    suspend fun updateTopic(topic: TopicEntity)

    @Query("UPDATE research_topics SET status = :status, progress = :progress, currentStep = :step, updatedAt = :time WHERE id = :topicId")
    suspend fun updateTopicProgress(topicId: String, status: String, progress: Int, step: String, time: Long = System.currentTimeMillis())

    @Query("UPDATE research_topics SET pagesScrapedCount = :pages, itemsClassifiedCount = :items, updatedAt = :time WHERE id = :topicId")
    suspend fun updateTopicCounts(topicId: String, pages: Int, items: Int, time: Long = System.currentTimeMillis())

    @Query("DELETE FROM research_topics WHERE id = :topicId")
    suspend fun deleteTopic(topicId: String)

    // Scraped Pages
    @Query("SELECT * FROM scraped_pages WHERE topicId = :topicId ORDER BY scrapedAt DESC")
    fun getScrapedPages(topicId: String): Flow<List<ScrapedPageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScrapedPage(page: ScrapedPageEntity)

    // Classified Items
    @Query("SELECT * FROM classified_items WHERE topicId = :topicId ORDER BY createdAt DESC")
    fun getClassifiedItems(topicId: String): Flow<List<ClassifiedItemEntity>>

    @Query("SELECT * FROM classified_items WHERE topicId = :topicId AND categoryKey = :categoryKey ORDER BY createdAt DESC")
    fun getClassifiedItemsByCategory(topicId: String, categoryKey: String): Flow<List<ClassifiedItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassifiedItems(items: List<ClassifiedItemEntity>)

    @Query("DELETE FROM classified_items WHERE topicId = :topicId")
    suspend fun deleteClassifiedItems(topicId: String)

    // Thesis Segments
    @Query("SELECT * FROM thesis_segments WHERE topicId = :topicId ORDER BY orderIndex ASC")
    fun getThesisSegments(topicId: String): Flow<List<ThesisSegmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThesisSegments(segments: List<ThesisSegmentEntity>)

    @Query("DELETE FROM thesis_segments WHERE topicId = :topicId")
    suspend fun deleteThesisSegments(topicId: String)

    // Mind Map Nodes
    @Query("SELECT * FROM mind_map_nodes WHERE topicId = :topicId ORDER BY level ASC")
    fun getMindMapNodes(topicId: String): Flow<List<MindMapNodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMindMapNodes(nodes: List<MindMapNodeEntity>)

    @Query("DELETE FROM mind_map_nodes WHERE topicId = :topicId")
    suspend fun deleteMindMapNodes(topicId: String)

    // Logs
    @Query("SELECT * FROM scraper_logs WHERE topicId = :topicId ORDER BY timestamp DESC LIMIT 50")
    fun getLogsForTopic(topicId: String): Flow<List<ScraperLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ScraperLogEntity)

    @Query("DELETE FROM scraper_logs WHERE topicId = :topicId")
    suspend fun deleteLogs(topicId: String)
}
