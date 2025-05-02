/*
 * Copyright (c) 2023. Isaak Hanimann.
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

package com.isaakhanimann.journal.ui.tabs.journal.experience.timeline.screen

import android.content.res.Configuration
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.isaakhanimann.journal.ui.tabs.journal.experience.TimelineDisplayOption
import com.isaakhanimann.journal.ui.tabs.journal.experience.components.TimeDisplayOption
import com.isaakhanimann.journal.ui.tabs.journal.experience.timeline.AllTimelines
import com.isaakhanimann.journal.ui.theme.horizontalPadding

@Composable
fun TimelineScreen(
    viewModel: TimelineScreenViewModel = hiltViewModel()
) {
    TimelineScreen(
        title = viewModel.consumerName,
        timelineDisplayOption = viewModel.timelineDisplayOptionFlow.collectAsState().value,
        timeDisplayOption = viewModel.timeDisplayOptionFlow.collectAsState().value,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
     title: String,
     timelineDisplayOption: TimelineDisplayOption,
     timeDisplayOption: TimeDisplayOption,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .safeDrawingPadding(),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.toFloat()
            var canvasWidth by remember { mutableFloatStateOf(screenWidth) }
            val isOrientationPortrait =
                LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
            Box(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) {
                when (timelineDisplayOption) {
                    TimelineDisplayOption.Hidden -> {}
                    TimelineDisplayOption.Loading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    TimelineDisplayOption.NotWorthDrawing -> {}
                    is TimelineDisplayOption.Shown -> {
                        AllTimelines(
                            model = timelineDisplayOption.allTimelinesModel,
                            timeDisplayOption = timeDisplayOption,
                            isShowingCurrentTime = true,
                            modifier = Modifier
                                .fillMaxHeight(if (isOrientationPortrait) 0.8f else 0.8f)
                                .width(canvasWidth.dp)
                                .padding(horizontal = horizontalPadding)
                        )
                    }
                }
            }
            Slider(
                value = canvasWidth,
                onValueChange = { value ->
                    canvasWidth = value
                },
                valueRange = screenWidth..5 * screenWidth,
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .padding(bottom = if (isOrientationPortrait) 30.dp else 10.dp)
            )
        }
    }
}
