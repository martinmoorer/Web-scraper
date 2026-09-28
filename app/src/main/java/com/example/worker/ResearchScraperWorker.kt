package com.example.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.data.local.ResearchDatabase
import com.example.data.local.ScrapedPageEntity
import com.example.data.remote.GeminiClassifier
import com.example.data.remote.WebScraper
import com.example.data.repository.ResearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResearchScraperWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "ResearchScraperWorker"
        const val KEY_TOPIC_ID = "key_topic_id"
        const val KEY_TOPIC_NAME = "key_topic_name"
        const val KEY_PROGRESS_STEP = "key_progress_step"
        const val KEY_PROGRESS_PERCENT = "key_progress_percent"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val topicId = inputData.getString(KEY_TOPIC_ID) ?: return@withContext Result.failure()
        val topicName = inputData.getString(KEY_TOPIC_NAME) ?: "General Topic"

        val database = ResearchDatabase.getInstance(applicationContext)
        val repository = ResearchRepository(database.researchDao())
        val scraper = WebScraper()
        val classifier = GeminiClassifier()

        try {
            Log.d(TAG, "Starting background research orchestration for: $topicName ($topicId)")
            
            // 1. Mark status SCRAPING
            repository.updateTopicProgress(topicId, "SCRAPING", 10, "Deploying web scraping crawler agents...")
            repository.log(topicId, "WORKER", "Background WorkManager worker activated for topic '$topicName'")
            setProgress(workDataOf(KEY_PROGRESS_PERCENT to 10, KEY_PROGRESS_STEP to "Crawling web sources..."))

            // 2. Perform web scraping
            val scrapedArticles = scraper.scrapeTopic(topicName) { msg ->
                repository.log(topicId, "SCRAPER", msg)
                repository.updateTopicProgress(topicId, "SCRAPING", 25, msg)
                setProgress(workDataOf(KEY_PROGRESS_PERCENT to 25, KEY_PROGRESS_STEP to msg))
            }

            // Persist raw pages
            for (article in scrapedArticles) {
                repository.insertScrapedPage(
                    ScrapedPageEntity(
                        topicId = topicId,
                        url = article.url,
                        title = article.title,
                        snippet = article.snippet,
                        content = article.fullText
                    )
                )
            }
            repository.updateTopicCounts(topicId, pages = scrapedArticles.size, items = 0)
            repository.log(topicId, "SCRAPER", "Harvested and archived ${scrapedArticles.size} raw web articles.")

            // 3. Mark status CLASSIFYING
            repository.updateTopicProgress(topicId, "CLASSIFYING", 50, "Engaging Gemini AI semantic categorizer...")
            repository.log(topicId, "AI", "Sending harvested text to classification & structuring pipeline...")
            setProgress(workDataOf(KEY_PROGRESS_PERCENT to 50, KEY_PROGRESS_STEP to "Gemini AI categorizing findings..."))

            // 4. Classify and structure with Gemini AI (or local semantic fallback)
            val result = classifier.classifyAndStructure(topicId, topicName, scrapedArticles) { msg ->
                repository.log(topicId, "AI", msg)
                repository.updateTopicProgress(topicId, "CLASSIFYING", 65, msg)
                setProgress(workDataOf(KEY_PROGRESS_PERCENT to 65, KEY_PROGRESS_STEP to msg))
            }

            // 5. Mark status SYNTHESIZING
            repository.updateTopicProgress(topicId, "SYNTHESIZING", 85, "Persisting structured categories, thesis, and mind map...")
            repository.log(topicId, "STORAGE", "Saving ${result.classifiedItems.size} classified items into Room database.")
            setProgress(workDataOf(KEY_PROGRESS_PERCENT to 85, KEY_PROGRESS_STEP to "Synthesizing research deliverables..."))

            repository.saveClassifiedItems(topicId, result.classifiedItems)
            repository.saveThesisSegments(topicId, result.thesisSegments)
            repository.saveMindMapNodes(topicId, result.mindMapNodes)

            repository.updateTopicCounts(topicId, pages = scrapedArticles.size, items = result.classifiedItems.size)

            // 6. Complete
            val completionMsg = if (result.isAiPowered) {
                "Research completed via Gemini AI: ${result.classifiedItems.size} categories, full thesis, and mind map generated."
            } else {
                "Research completed via local semantic engine: ${result.classifiedItems.size} categories, thesis draft, and mind map generated."
            }
            repository.updateTopicProgress(topicId, "COMPLETED", 100, completionMsg)
            repository.log(topicId, "WORKER", completionMsg)
            setProgress(workDataOf(KEY_PROGRESS_PERCENT to 100, KEY_PROGRESS_STEP to completionMsg))

            Log.d(TAG, "Completed research scrape orchestration successfully for: $topicName")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Worker failed for topic $topicId", e)
            repository.updateTopicProgress(topicId, "FAILED", 0, "Error: ${e.localizedMessage ?: "Unknown failure"}")
            repository.log(topicId, "ERROR", "Scraper worker terminated with exception: ${e.message}", isError = true)
            Result.failure(workDataOf("error" to (e.localizedMessage ?: "Worker failure")))
        }
    }
}
