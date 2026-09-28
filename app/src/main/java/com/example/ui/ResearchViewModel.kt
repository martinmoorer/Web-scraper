package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.local.ClassifiedItemEntity
import com.example.data.local.MindMapNodeEntity
import com.example.data.local.ResearchDatabase
import com.example.data.local.ScrapedPageEntity
import com.example.data.local.ScraperLogEntity
import com.example.data.local.ThesisSegmentEntity
import com.example.data.local.TopicEntity
import com.example.data.repository.ResearchRepository
import com.example.worker.ResearchWorkScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class ResearchUiState(
    val topics: List<TopicEntity> = emptyList(),
    val selectedTopicId: String? = null,
    val selectedTopic: TopicEntity? = null,
    val selectedCategoryKey: String? = null, // null for ALL
    val classifiedItems: List<ClassifiedItemEntity> = emptyList(),
    val thesisSegments: List<ThesisSegmentEntity> = emptyList(),
    val mindMapNodes: List<MindMapNodeEntity> = emptyList(),
    val scraperLogs: List<ScraperLogEntity> = emptyList(),
    val scrapedPages: List<ScrapedPageEntity> = emptyList(),
    val activeTab: Int = 0, // 0 = Findings, 1 = Thesis, 2 = Mind Map, 3 = Live Logs
    val isCreatingDialog: Boolean = false,
    val isApiKeyConfigured: Boolean = false
)

class ResearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ResearchRepository
    private val _selectedTopicId = MutableStateFlow<String?>(null)
    private val _selectedCategoryKey = MutableStateFlow<String?>(null)
    private val _activeTab = MutableStateFlow(0)
    private val _isCreatingDialog = MutableStateFlow(false)

    val activeTab = _activeTab.asStateFlow()
    val isCreatingDialog = _isCreatingDialog.asStateFlow()
    val selectedCategoryKey = _selectedCategoryKey.asStateFlow()

    init {
        val db = ResearchDatabase.getInstance(application)
        repository = ResearchRepository(db.researchDao())
        seedDefaultTopicIfEmpty()
    }

    private fun seedDefaultTopicIfEmpty() {
        viewModelScope.launch {
            repository.allTopics.collect { topics ->
                if (topics.isEmpty()) {
                    val defaultTopicId = UUID.randomUUID().toString()
                    val defaultTopic = TopicEntity(
                        id = defaultTopicId,
                        title = "Climate Change",
                        status = "IDLE",
                        progress = 0,
                        currentStep = "Tap 'Start Scraper' to begin research orchestration"
                    )
                    repository.saveTopic(defaultTopic)
                    _selectedTopicId.value = defaultTopicId
                } else if (_selectedTopicId.value == null) {
                    _selectedTopicId.value = topics.first().id
                }
            }
        }
    }

    val allTopics: StateFlow<List<TopicEntity>> = repository.allTopics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentTopic: StateFlow<TopicEntity?> = _selectedTopicId
        .flatMapLatest { id ->
            if (id != null) repository.getTopic(id) else flowOf(null)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val classifiedItems: StateFlow<List<ClassifiedItemEntity>> = combine(
        _selectedTopicId,
        _selectedCategoryKey
    ) { topicId, categoryKey ->
        topicId to categoryKey
    }.flatMapLatest { (topicId, categoryKey) ->
        if (topicId == null) flowOf(emptyList())
        else if (categoryKey == null) repository.getClassifiedItems(topicId)
        else repository.getClassifiedItemsByCategory(topicId, categoryKey)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val thesisSegments: StateFlow<List<ThesisSegmentEntity>> = _selectedTopicId
        .flatMapLatest { id ->
            if (id != null) repository.getThesisSegments(id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val mindMapNodes: StateFlow<List<MindMapNodeEntity>> = _selectedTopicId
        .flatMapLatest { id ->
            if (id != null) repository.getMindMapNodes(id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val scraperLogs: StateFlow<List<ScraperLogEntity>> = _selectedTopicId
        .flatMapLatest { id ->
            if (id != null) repository.getLogs(id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val scrapedPages: StateFlow<List<ScrapedPageEntity>> = _selectedTopicId
        .flatMapLatest { id ->
            if (id != null) repository.getScrapedPages(id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTopic(topicId: String) {
        _selectedTopicId.value = topicId
        _selectedCategoryKey.value = null
    }

    fun selectCategory(categoryKey: String?) {
        _selectedCategoryKey.value = categoryKey
    }

    fun selectTab(index: Int) {
        _activeTab.value = index
    }

    fun setCreatingDialog(visible: Boolean) {
        _isCreatingDialog.value = visible
    }

    fun startScraping(topicId: String, topicName: String) {
        viewModelScope.launch {
            repository.updateTopicProgress(topicId, "QUEUED", 5, "Enqueuing background crawler worker...")
            repository.log(topicId, "DISPATCHER", "Enqueued background WorkManager task for '$topicName'")
            ResearchWorkScheduler.scheduleResearchScrape(getApplication(), topicId, topicName)
        }
    }

    fun cancelScraping(topicId: String) {
        viewModelScope.launch {
            ResearchWorkScheduler.cancelResearchScrape(getApplication(), topicId)
            repository.updateTopicProgress(topicId, "IDLE", 0, "Scraping task cancelled by user.")
            repository.log(topicId, "DISPATCHER", "Cancelled WorkManager scraping task.")
        }
    }

    fun createTopicAndScrape(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val newId = UUID.randomUUID().toString()
            val newTopic = TopicEntity(
                id = newId,
                title = title.trim(),
                status = "QUEUED",
                progress = 5,
                currentStep = "Enqueuing crawler worker..."
            )
            repository.saveTopic(newTopic)
            _selectedTopicId.value = newId
            _selectedCategoryKey.value = null
            _isCreatingDialog.value = false

            // Schedule background WorkManager job
            ResearchWorkScheduler.scheduleResearchScrape(getApplication(), newId, newTopic.title)
        }
    }

    fun deleteCurrentTopic() {
        val topicId = _selectedTopicId.value ?: return
        viewModelScope.launch {
            repository.deleteTopic(topicId)
            _selectedTopicId.value = null
        }
    }

    fun isApiKeyConfigured(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }
}
