package com.example.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.flow.Flow
import java.util.UUID

object ResearchWorkScheduler {

    fun scheduleResearchScrape(
        context: Context,
        topicId: String,
        topicName: String
    ): UUID {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = workDataOf(
            ResearchScraperWorker.KEY_TOPIC_ID to topicId,
            ResearchScraperWorker.KEY_TOPIC_NAME to topicName
        )

        val workRequest = OneTimeWorkRequestBuilder<ResearchScraperWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .addTag("research_work")
            .addTag("topic_$topicId")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "research_work_$topicId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        return workRequest.id
    }

    fun cancelResearchScrape(context: Context, topicId: String) {
        WorkManager.getInstance(context).cancelUniqueWork("research_work_$topicId")
    }

    fun getWorkInfoForTopic(context: Context, topicId: String): Flow<List<WorkInfo>> {
        return WorkManager.getInstance(context).getWorkInfosForUniqueWorkFlow("research_work_$topicId")
    }
}
