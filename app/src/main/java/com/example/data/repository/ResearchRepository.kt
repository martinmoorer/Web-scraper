package com.example.data.repository

import com.example.data.local.ClassifiedItemEntity
import com.example.data.local.MindMapNodeEntity
import com.example.data.local.ResearchDao
import com.example.data.local.ScrapedPageEntity
import com.example.data.local.ScraperLogEntity
import com.example.data.local.ThesisSegmentEntity
import com.example.data.local.TopicEntity
import kotlinx.coroutines.flow.Flow

class ResearchRepository(private val dao: ResearchDao) {

    val allTopics: Flow<List<TopicEntity>> = dao.getAllTopics()

    fun getTopic(topicId: String): Flow<TopicEntity?> = dao.getTopicById(topicId)

    suspend fun getTopicOnce(topicId: String): TopicEntity? = dao.getTopicByIdOnce(topicId)

    suspend fun saveTopic(topic: TopicEntity) = dao.insertTopic(topic)

    suspend fun updateTopicProgress(topicId: String, status: String, progress: Int, step: String) {
        dao.updateTopicProgress(topicId, status, progress, step)
    }

    suspend fun updateTopicCounts(topicId: String, pages: Int, items: Int) {
        dao.updateTopicCounts(topicId, pages, items)
    }

    suspend fun deleteTopic(topicId: String) {
        dao.deleteTopic(topicId)
        dao.deleteClassifiedItems(topicId)
        dao.deleteThesisSegments(topicId)
        dao.deleteMindMapNodes(topicId)
        dao.deleteLogs(topicId)
    }

    // Scraped pages
    fun getScrapedPages(topicId: String): Flow<List<ScrapedPageEntity>> = dao.getScrapedPages(topicId)

    suspend fun insertScrapedPage(page: ScrapedPageEntity) = dao.insertScrapedPage(page)

    // Classified items
    fun getClassifiedItems(topicId: String): Flow<List<ClassifiedItemEntity>> = dao.getClassifiedItems(topicId)

    fun getClassifiedItemsByCategory(topicId: String, categoryKey: String): Flow<List<ClassifiedItemEntity>> =
        dao.getClassifiedItemsByCategory(topicId, categoryKey)

    suspend fun saveClassifiedItems(topicId: String, items: List<ClassifiedItemEntity>) {
        dao.deleteClassifiedItems(topicId)
        dao.insertClassifiedItems(items)
    }

    // Thesis
    fun getThesisSegments(topicId: String): Flow<List<ThesisSegmentEntity>> = dao.getThesisSegments(topicId)

    suspend fun saveThesisSegments(topicId: String, segments: List<ThesisSegmentEntity>) {
        dao.deleteThesisSegments(topicId)
        dao.insertThesisSegments(segments)
    }

    // Mind Map
    fun getMindMapNodes(topicId: String): Flow<List<MindMapNodeEntity>> = dao.getMindMapNodes(topicId)

    suspend fun saveMindMapNodes(topicId: String, nodes: List<MindMapNodeEntity>) {
        dao.deleteMindMapNodes(topicId)
        dao.insertMindMapNodes(nodes)
    }

    // Logs
    fun getLogs(topicId: String): Flow<List<ScraperLogEntity>> = dao.getLogsForTopic(topicId)

    suspend fun log(topicId: String, tag: String, message: String, isError: Boolean = false) {
        dao.insertLog(
            ScraperLogEntity(
                topicId = topicId,
                tag = tag,
                message = message,
                isError = isError
            )
        )
    }
}
