package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MindMapNodeEntity
import com.example.data.model.PredefinedCategories
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ResearchCyan

@Composable
fun MindMapCanvas(
    nodes: List<MindMapNodeEntity>,
    modifier: Modifier = Modifier
) {
    var selectedNode by remember { mutableStateOf<MindMapNodeEntity?>(null) }

    if (nodes.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Hub,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No mind map nodes generated yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Run the research scraper to extract conceptual nodes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
        return
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("mind_map_canvas")
                .pointerInput(nodes) {
                    detectTapGestures { tapOffset ->
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        // Find tapped node within touch target threshold
                        val hitNode = nodes.firstOrNull { node ->
                            val nx = node.posX * canvasWidth
                            val ny = node.posY * canvasHeight
                            val dist = Math.hypot((tapOffset.x - nx).toDouble(), (tapOffset.y - ny).toDouble())
                            dist < 60f
                        }
                        selectedNode = hitNode
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val nodeMap = nodes.associateBy { it.id }

            // 1. Draw connecting lines (edges)
            nodes.forEach { node ->
                if (node.parentId != null) {
                    val parent = nodeMap[node.parentId]
                    if (parent != null) {
                        val start = Offset(parent.posX * canvasWidth, parent.posY * canvasHeight)
                        val end = Offset(node.posX * canvasWidth, node.posY * canvasHeight)

                        val edgeColor = when (node.level) {
                            1 -> Color(0xFF00E5FF).copy(alpha = 0.6f)
                            else -> Color(0xFF38BDF8).copy(alpha = 0.35f)
                        }

                        drawLine(
                            brush = Brush.linearGradient(listOf(Color(0xFF00E5FF).copy(alpha = 0.7f), edgeColor)),
                            start = start,
                            end = end,
                            strokeWidth = if (node.level == 1) 4f else 2.5f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            // 2. Draw nodes
            nodes.forEach { node ->
                val center = Offset(node.posX * canvasWidth, node.posY * canvasHeight)
                val isSelected = selectedNode?.id == node.id

                val (radius, baseColor) = when (node.level) {
                    0 -> 36f to Color(0xFF00E5FF)
                    1 -> {
                        val cat = node.categoryKey?.let { PredefinedCategories.find(it) }
                        val color = try {
                            Color(android.graphics.Color.parseColor(cat?.colorHex ?: "#38BDF8"))
                        } catch (e: Exception) {
                            Color(0xFF38BDF8)
                        }
                        26f to color
                    }
                    else -> 18f to Color(0xFF818CF8)
                }

                // Glowing outer halo for root or selected node
                if (isSelected || node.level == 0) {
                    drawCircle(
                        color = baseColor.copy(alpha = if (isSelected) 0.35f else 0.2f),
                        radius = radius + 14f,
                        center = center
                    )
                }

                // Main node circle
                drawCircle(
                    color = baseColor,
                    radius = radius,
                    center = center
                )

                // White inner dot for root & level 1
                if (node.level <= 1) {
                    drawCircle(
                        color = Color.White,
                        radius = if (node.level == 0) 12f else 8f,
                        center = center
                    )
                }

                // Node outline
                drawCircle(
                    color = if (isSelected) Color.White else Color.Black.copy(alpha = 0.4f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = if (isSelected) 3f else 1.5f)
                )

                // Node text label
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = if (node.level == 0) 34f else if (node.level == 1) 28f else 24f
                    isFakeBoldText = true
                    textAlign = android.graphics.Paint.Align.CENTER
                    setShadowLayer(6f, 0f, 2f, android.graphics.Color.BLACK)
                }

                val textY = center.y + radius + 22f
                val labelText = if (node.label.length > 18) node.label.take(16) + "..." else node.label
                drawContext.canvas.nativeCanvas.drawText(labelText, center.x, textY, textPaint)
            }
        }

        // Selected node detail card
        selectedNode?.let { node ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .testTag("node_detail_card")
                    .clickable { selectedNode = null },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = if (node.level == 0) ResearchCyan else Color(0xFF38BDF8),
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = node.label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!node.detail.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = node.detail,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Level ${node.level} Node • ${node.categoryKey ?: "Central Theme"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = ResearchCyan,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
