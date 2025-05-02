/*
 * Copyright (c) 2022-2023. Isaak Hanimann.
 * This file is part of PsychonautWiki Journal.
 *
 * PsychonautWiki Journal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * PsychonautWiki Journal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PsychonautWiki Journal.  If not, see https://www.gnu.org/licenses/gpl-3.0.en.html.
 */

package com.isaakhanimann.journal.ui.tabs.search.substance.roa

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isaakhanimann.journal.data.substances.classes.Tolerance
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

// --- Tolerance graph ---
@Composable
fun ToleranceGraph(
    zeroDays: Int,
    halfDays: Int,
    currentDays: Int,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val totalDays = maxOf(zeroDays, halfDays, currentDays, 30)
    val zeroColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
    val halfColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    val cornerRadius = with(density) { 4.dp.toPx() }
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.height(100.dp)) {
        val width = size.width
        val dayWidth = width / totalDays
        val barHeight = with(density) { 26.dp.toPx() }
        val spacing = with(density) { 8.dp.toPx() }
        val textStyle = TextStyle(
            color = Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
        val leftMargin = with(density) { 40.dp.toPx() } // Reduzindo a margem esquerda de 60dp para 40dp

        // Function to draw rounded bars
        fun DrawScope.drawRoundedBar(
            days: Int,
            yPos: Float,
            color: Color,
            text: String
        ) {
            val timeText = when {
                days >= 30 -> "${days / 30} ${if (days / 30 == 1) "month" else "months"}"
                days >= 7 -> "${days / 7} ${if (days / 7 == 1) "week" else "weeks"}"
                else -> "$days ${if (days == 1) "day" else "days"}"
            }

            val textLayout = textMeasurer.measure(timeText, textStyle)
            drawText(
                textLayout,
                topLeft = Offset(
                    x = leftMargin + days * dayWidth + 8.dp.toPx(),
                    y = yPos + barHeight / 2 - textLayout.size.height / 2
                )
            )

            drawRoundRect(
                color = color,
                topLeft = Offset(leftMargin, yPos),
                size = Size(days * dayWidth, barHeight),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                style = Fill
            )

            // Adicionar texto "Zero" ou "Half" à esquerda da barra
            val statusText = when (text) {
                "$zeroDays" -> "Zero"
                "$halfDays" -> "Half"
                else -> ""
            }
            if (statusText.isNotEmpty()) {
                val statusTextLayout = textMeasurer.measure(statusText, textStyle)
                drawText(
                    statusTextLayout,
                    topLeft = Offset(
                        x = leftMargin - statusTextLayout.size.width - 8.dp.toPx(),
                        y = yPos + barHeight / 2 - statusTextLayout.size.height / 2
                    )
                )
            }
        }

        drawRoundedBar(
            days = zeroDays,
            yPos = 0f,
            color = zeroColor,
            text = "$zeroDays"
        )

        drawRoundedBar(
            days = halfDays,
            yPos = barHeight + spacing,
            color = halfColor,
            text = "$halfDays"
        )

        if (currentDays in 0 until zeroDays) {
            val currentX = leftMargin + currentDays * dayWidth
            drawLine(
                color = Color.White,
                start = Offset(currentX, 0f),
                end = Offset(currentX, barHeight * 2 + spacing * 2),
                strokeWidth = with(density) { 3.dp.toPx() }
            )

            // Adicionar texto "You" embaixo da linha
            val youTextLayout = textMeasurer.measure("You", textStyle)
            drawText(
                youTextLayout,
                topLeft = Offset(
                    x = currentX - youTextLayout.size.width / 2,
                    y = barHeight * 2 + spacing * 2 + 2.dp.toPx()
                )
            )
        }
    }
}

// --- Tolerance section (with graph and texts) ---
@Composable
fun ToleranceSection(
    tolerance: Tolerance?,
    crossTolerances: List<String> = emptyList(),
    lastUseDate: LocalDateTime?,
    modifier: Modifier = Modifier
) {
    var currentDays by remember { mutableStateOf(0) }
    val zeroDays = tolerance?.zero?.parseToDays() ?: 30
    val halfDays = tolerance?.half?.parseToDays() ?: 14

    // Atualizar currentDays a cada minuto
    LaunchedEffect(lastUseDate) {
        while (true) {
            currentDays = if (lastUseDate != null) {
                ChronoUnit.DAYS.between(lastUseDate, LocalDateTime.now()).toInt()
            } else {
                0
            }
            delay(60000) // Atualizar a cada minuto
        }
    }

    Column(modifier) {
        Text(
            text = "Tolerance Timeline",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 28.dp)
        )
        ToleranceGraph(
            zeroDays = zeroDays,
            halfDays = halfDays,
            currentDays = currentDays,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Adicionar indicador de redução de efeitos
        val effectReduction = when {
            currentDays >= zeroDays -> 0f
            currentDays >= halfDays -> 0.5f
            else -> 1f - (currentDays.toFloat() / halfDays)
        }
        
        Text(
            text = "Efeitos reduzidos em ${(effectReduction * 100).toInt()}%",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
            color = when {
                effectReduction > 0.7f -> Color.Red
                effectReduction > 0.3f -> Color.Yellow
                else -> Color.Green
            }
        )
        
        if (crossTolerances.isNotEmpty()) {
            Text(
                text = "Cross tolerance with: ${crossTolerances.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// --- Extension to convert string like "2 weeks" to days ---
private fun String?.parseToDays(): Int {
    if (this == null) return 0
    return when {
        contains("month", ignoreCase = true) -> (split(" ").first().toIntOrNull() ?: 1) * 30
        contains("week", ignoreCase = true) -> (split(" ").first().toIntOrNull() ?: 1) * 7
        contains("day", ignoreCase = true) -> split(" ").first().toIntOrNull() ?: 1
        else -> 0
    }
}
