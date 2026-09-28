package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ClassifiedItemEntity
import com.example.data.local.ScraperLogEntity
import com.example.data.local.ThesisSegmentEntity
import com.example.data.local.TopicEntity
import com.example.data.model.PredefinedCategories
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ResearchCyan
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResearchScreen(
    viewModel: ResearchViewModel,
    modifier: Modifier = Modifier
) {
    val topics by viewModel.allTopics.collectAsStateWithLifecycle()
    val currentTopic by viewModel.currentTopic.collectAsStateWithLifecycle()
    val classifiedItems by viewModel.classifiedItems.collectAsStateWithLifecycle()
    val thesisSegments by viewModel.thesisSegments.collectAsStateWithLifecycle()
    val mindMapNodes by viewModel.mindMapNodes.collectAsStateWithLifecycle()
    val scraperLogs by viewModel.scraperLogs.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val selectedCategoryKey by viewModel.selectedCategoryKey.collectAsStateWithLifecycle()
    val isCreatingDialog by viewModel.isCreatingDialog.collectAsStateWithLifecycle()

    var topicDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(ResearchCyan.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TravelExplore,
                                contentDescription = "App Icon",
                                tint = ResearchCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Deep Research",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Autonomous Web Scraper & Gemini AI",
                                style = MaterialTheme.typography.labelSmall,
                                color = ResearchCyan,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                actions = {
                    // API Key Badge
                    val hasKey = viewModel.isApiKeyConfigured()
                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (hasKey) Color(0xFF10B981).copy(alpha = 0.15f)
                                else Color(0xFFF59E0B).copy(alpha = 0.15f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (hasKey) Color(0xFF10B981).copy(alpha = 0.4f)
                                else Color(0xFFF59E0B).copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (hasKey) Color(0xFF10B981) else Color(0xFFF59E0B),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (hasKey) "Gemini AI" else "Local AI",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (hasKey) Color(0xFF10B981) else Color(0xFFF59E0B),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // New Topic Button
                    IconButton(
                        onClick = { viewModel.setCreatingDialog(true) },
                        modifier = Modifier.testTag("add_topic_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Research Topic",
                            tint = ResearchCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Topic Switcher Bar & Status Card
            TopicStatusCard(
                topic = currentTopic,
                topics = topics,
                dropdownExpanded = topicDropdownExpanded,
                onDropdownToggle = { topicDropdownExpanded = it },
                onSelectTopic = {
                    viewModel.selectTopic(it)
                    topicDropdownExpanded = false
                },
                onStartScrape = { id, name -> viewModel.startScraping(id, name) },
                onCancelScrape = { id -> viewModel.cancelScraping(id) },
                onDeleteTopic = { viewModel.deleteCurrentTopic() },
                onOpenAddDialog = { viewModel.setCreatingDialog(true) }
            )

            // Category Filter Chips
            CategoryFilterBar(
                selectedCategoryKey = selectedCategoryKey,
                onSelectCategory = { viewModel.selectCategory(it) }
            )

            // Tab Navigation
            val tabTitles = listOf("Findings", "Thesis", "Mind Map", "Live Logs")
            val tabIcons = listOf(
                Icons.Default.Article,
                Icons.Default.MenuBook,
                Icons.Default.Hub,
                Icons.Default.Terminal
            )

            TabRow(
                selectedTabIndex = activeTab,
                containerColor = DarkSurface,
                contentColor = TextPrimaryDark,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = ResearchCyan,
                        height = 3.dp
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = activeTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (activeTab == index) ResearchCyan else TextSecondaryDark
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = tabIcons[index],
                                contentDescription = title,
                                tint = if (activeTab == index) ResearchCyan else TextSecondaryDark,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier.testTag("tab_$index")
                    )
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (activeTab) {
                    0 -> FindingsTabContent(classifiedItems = classifiedItems)
                    1 -> ThesisTabContent(thesisSegments = thesisSegments)
                    2 -> MindMapCanvas(nodes = mindMapNodes)
                    3 -> LogsTabContent(logs = scraperLogs)
                }
            }
        }
    }

    // New Topic Dialog
    if (isCreatingDialog) {
        CreateTopicDialog(
            onDismiss = { viewModel.setCreatingDialog(false) },
            onConfirm = { title -> viewModel.createTopicAndScrape(title) }
        )
    }
}

@Composable
private fun TopicStatusCard(
    topic: TopicEntity?,
    topics: List<TopicEntity>,
    dropdownExpanded: Boolean,
    onDropdownToggle: (Boolean) -> Unit,
    onSelectTopic: (String) -> Unit,
    onStartScrape: (String, String) -> Unit,
    onCancelScrape: (String) -> Unit,
    onDeleteTopic: () -> Unit,
    onOpenAddDialog: () -> Unit
) {
    if (topic == null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No Research Topic Selected",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onOpenAddDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = ResearchCyan, contentColor = Color(0xFF00363D))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create Research Topic")
                }
            }
        }
        return
    }

    val isRunning = topic.status in listOf("QUEUED", "SCRAPING", "CLASSIFYING", "SYNTHESIZING")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("topic_status_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Topic selector row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onDropdownToggle(true) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = topic.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "▼",
                            style = MaterialTheme.typography.labelSmall,
                            color = ResearchCyan,
                            fontSize = 10.sp
                        )
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { onDropdownToggle(false) },
                        modifier = Modifier.background(DarkSurfaceElevated)
                    ) {
                        topics.forEach { t ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = t.title,
                                        fontWeight = if (t.id == topic.id) FontWeight.Bold else FontWeight.Normal,
                                        color = if (t.id == topic.id) ResearchCyan else TextPrimaryDark
                                    )
                                },
                                onClick = { onSelectTopic(t.id) }
                            )
                        }
                    }
                }

                // Status chip
                val statusColor = when (topic.status) {
                    "COMPLETED" -> Color(0xFF10B981)
                    "SCRAPING", "CLASSIFYING", "SYNTHESIZING" -> ResearchCyan
                    "QUEUED" -> Color(0xFFF59E0B)
                    "FAILED" -> Color(0xFFEF4444)
                    else -> Color(0xFF64748B)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = topic.status,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar and step description
            if (isRunning || topic.progress > 0) {
                LinearProgressIndicator(
                    progress = { topic.progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = ResearchCyan,
                    trackColor = DarkBorder
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = topic.currentStep,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark,
                    modifier = Modifier.weight(1f)
                )
                if (isRunning) {
                    Text(
                        text = "${topic.progress}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ResearchCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats and Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    StatPill(label = "Pages", value = "${topic.pagesScrapedCount}")
                    Spacer(modifier = Modifier.width(8.dp))
                    StatPill(label = "Items", value = "${topic.itemsClassifiedCount}")
                }

                Row {
                    if (isRunning) {
                        OutlinedButton(
                            onClick = { onCancelScrape(topic.id) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                            modifier = Modifier.testTag("cancel_scrape_button")
                        ) {
                            Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cancel", fontSize = 12.sp)
                        }
                    } else {
                        Button(
                            onClick = { onStartScrape(topic.id, topic.title) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ResearchCyan,
                                contentColor = Color(0xFF00363D)
                            ),
                            modifier = Modifier.testTag("start_scrape_button")
                        ) {
                            Icon(
                                imageVector = if (topic.status == "COMPLETED") Icons.Default.Refresh else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (topic.status == "COMPLETED") "Re-run Scraper" else "Start Scraper",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onDeleteTopic,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Topic",
                            tint = TextMutedDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatPill(label: String, value: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceVariant)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$label: ",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark,
                fontSize = 11.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun CategoryFilterBar(
    selectedCategoryKey: String?,
    onSelectCategory: (String?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        FilterChip(
            selected = selectedCategoryKey == null,
            onClick = { onSelectCategory(null) },
            label = { Text("All Categories", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ResearchCyan,
                selectedLabelColor = Color(0xFF00363D),
                containerColor = DarkSurfaceElevated,
                labelColor = TextSecondaryDark
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selectedCategoryKey == null,
                borderColor = DarkBorder,
                selectedBorderColor = ResearchCyan
            )
        )

        PredefinedCategories.ALL.forEach { category ->
            val isSelected = selectedCategoryKey == category.key
            val accent = try {
                Color(android.graphics.Color.parseColor(category.colorHex))
            } catch (e: Exception) {
                ResearchCyan
            }

            FilterChip(
                selected = isSelected,
                onClick = { onSelectCategory(if (isSelected) null else category.key) },
                label = { Text(category.displayName, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = accent,
                    selectedLabelColor = Color.White,
                    containerColor = DarkSurfaceElevated,
                    labelColor = TextSecondaryDark
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = DarkBorder,
                    selectedBorderColor = accent
                )
            )
        }
    }
}

@Composable
private fun FindingsTabContent(classifiedItems: List<ClassifiedItemEntity>) {
    if (classifiedItems.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.TravelExplore,
            title = "No Categorized Findings",
            subtitle = "Start the background scraper to ingest, classify, and structure topical findings."
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(classifiedItems, key = { it.id }) { item ->
            ClassifiedItemCard(item = item)
        }
    }
}

@Composable
private fun ClassifiedItemCard(item: ClassifiedItemEntity) {
    val category = PredefinedCategories.find(item.categoryKey)
    val catColor = try {
        Color(android.graphics.Color.parseColor(category.colorHex))
    } catch (e: Exception) {
        ResearchCyan
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("finding_card_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Category Badge & Confidence
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(catColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = catColor,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "${(item.confidenceScore * 100).toInt()}% Match",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Content
            Text(
                text = item.content,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryDark,
                lineHeight = 20.sp
            )

            // Entities Tag row
            if (item.entitiesJson.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Entities: ",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark,
                        fontSize = 10.sp
                    )
                    Text(
                        text = item.entitiesJson,
                        style = MaterialTheme.typography.labelSmall,
                        color = ResearchCyan,
                        fontSize = 10.sp
                    )
                }
            }

            // Metrics Box
            if (item.metricsJson.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.metricsJson,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Source URL
            if (!item.sourceUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = TextMutedDark,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.sourceUrl,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ThesisTabContent(thesisSegments: List<ThesisSegmentEntity>) {
    if (thesisSegments.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.MenuBook,
            title = "No Thesis Synthesized Yet",
            subtitle = "Execute the research scraper. Gemini AI will compose full academic sections from collected telemetry."
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(thesisSegments, key = { it.id }) { segment ->
            ThesisSegmentCard(segment = segment)
        }
    }
}

@Composable
private fun ThesisSegmentCard(segment: ThesisSegmentEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("thesis_card_${segment.orderIndex}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ResearchCyan.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${segment.orderIndex}. ${segment.sectionType}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ResearchCyan,
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = "Confidence: ${(segment.confidenceRating * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = segment.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = segment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryDark,
                lineHeight = 22.sp
            )

            // Takeaways bullets
            if (segment.takeawaysJson.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                val bullets = segment.takeawaysJson.split(";", "\n").filter { it.isNotBlank() }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceVariant)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Key Findings & Evidence Links:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ResearchCyan,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    bullets.forEach { bullet ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text(text = "• ", color = ResearchCyan, fontSize = 11.sp)
                            Text(
                                text = bullet.trim(),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimaryDark,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LogsTabContent(logs: List<ScraperLogEntity>) {
    if (logs.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.Terminal,
            title = "No Crawler Activity",
            subtitle = "Worker logs and real-time scraping events will be displayed here."
        )
        return
    }

    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B12))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(logs, key = { it.id }) { log ->
            val tagColor = when (log.tag) {
                "SCRAPER" -> Color(0xFF38BDF8)
                "AI" -> Color(0xFFC084FC)
                "WORKER" -> Color(0xFF34D399)
                "ERROR" -> Color(0xFFF87171)
                else -> Color(0xFF94A3B8)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                Text(
                    text = "${dateFormat.format(Date(log.timestamp))} ",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF475569),
                    fontSize = 11.sp
                )
                Text(
                    text = "[${log.tag}] ",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = tagColor,
                    fontSize = 11.sp
                )
                Text(
                    text = log.message,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = if (log.isError) Color(0xFFF87171) else Color(0xFFE2E8F0),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun EmptyStateView(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ResearchCyan.copy(alpha = 0.5f),
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun CreateTopicDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var topicText by remember { mutableStateOf("") }
    val suggestions = listOf(
        "Climate Change",
        "Quantum Computing",
        "Ocean Acidification",
        "Synthetic Biology",
        "Renewable Microgrids"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New Research Topic",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
        },
        text = {
            Column {
                Text(
                    text = "Specify any topic. The crawler family will scrape authoritative sources and Gemini AI will structure the findings into categories, a thesis, and a mind map.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = topicText,
                    onValueChange = { topicText = it },
                    label = { Text("Topic name") },
                    placeholder = { Text("e.g. Artificial Photosynthesis") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("topic_name_input")
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Quick suggestions:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    suggestions.forEach { suggestion ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceElevated)
                                .clickable { topicText = suggestion }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.labelSmall,
                                color = ResearchCyan,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (topicText.isNotBlank()) {
                        onConfirm(topicText.trim())
                    }
                },
                enabled = topicText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ResearchCyan,
                    contentColor = Color(0xFF00363D)
                ),
                modifier = Modifier.testTag("confirm_topic_button")
            ) {
                Text("Launch Scraper")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondaryDark)
            }
        },
        containerColor = DarkSurfaceElevated
    )
}
