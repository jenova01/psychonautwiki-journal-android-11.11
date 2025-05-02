/*
 * Copyright (c) 2022. Isaak Hanimann.
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

package com.isaakhanimann.journal.ui.tabs.search.substance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.isaakhanimann.journal.data.room.experiences.ExperienceRepository
import com.isaakhanimann.journal.data.substances.repositories.SubstanceRepository
import com.isaakhanimann.journal.ui.main.navigation.graphs.SubstanceRoute
import com.isaakhanimann.journal.ui.tabs.journal.experience.TimelineDisplayOption
import com.isaakhanimann.journal.ui.tabs.journal.experience.components.DataForOneEffectLine
import com.isaakhanimann.journal.ui.tabs.journal.experience.timeline.AllTimelinesModel
import com.isaakhanimann.journal.ui.utils.getInstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class SubstanceViewModel @Inject constructor(
    private val experienceRepo: ExperienceRepository,
    private val substanceRepo: SubstanceRepository,
    state: SavedStateHandle
) : ViewModel() {

    private val substanceName = state.get<String>("substanceName")!!
    val substanceWithCategories = substanceRepo.getSubstanceWithCategories(substanceName)!!
    private val _lastUseDate = MutableStateFlow<LocalDateTime?>(null)
    val lastUseDate = _lastUseDate.asStateFlow()
    private var currentDays by mutableStateOf(0)
    private var zeroDays by mutableStateOf(30)
    private var halfDays by mutableStateOf(14)

    init {
        viewModelScope.launch {
            val lastUseDate = experienceRepo.getLastUseDate(substanceName)
            if (lastUseDate != null) {
                currentDays = ChronoUnit.DAYS.between(lastUseDate, LocalDateTime.now()).toInt()
            }
            
            // Calcular tolerância personalizada
            val (zero, half) = experienceRepo.calculatePersonalizedTolerance(substanceName)
            zeroDays = zero
            halfDays = half
            
            while (true) {
                delay(60000) // Atualizar a cada minuto
                if (lastUseDate != null) {
                    currentDays = ChronoUnit.DAYS.between(lastUseDate, LocalDateTime.now()).toInt()
                }
            }
        }
    }

    val customUnitsFlow = experienceRepo.getUnArchivedCustomUnitsFlow(substanceName).stateIn(
        initialValue = emptyList(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000)
    )

    val ingestionTimeFlow = MutableStateFlow(LocalDateTime.now())

    fun changeIngestionTime(newTime: LocalDateTime) {
        viewModelScope.launch {
            ingestionTimeFlow.emit(newTime)
        }
    }

    val timelineDisplayOptionFlow = ingestionTimeFlow.map { ingestionTime ->
        val substance = substanceWithCategories.substance
        val roasWithDurationsDefined = substance.roas.filter { roa ->
            val roaDuration = roa.roaDuration
            val isEveryDurationNull =
                roaDuration?.onset == null && roaDuration?.comeup == null && roaDuration?.peak == null && roaDuration?.offset == null && roaDuration?.total == null
            return@filter !isEveryDurationNull
        }
        val roasWithDosesDefined = substance.roas.filter { roa ->
            val roaDose = roa.roaDose
            val isEveryDoseNull =
                roaDose?.lightMin == null && roaDose?.commonMin == null && roaDose?.strongMin == null && roaDose?.heavyMin == null
            return@filter !isEveryDoseNull
        }
        val firstAverageCommonDose =
            roasWithDosesDefined.firstNotNullOfOrNull { it.roaDose?.averageCommonDose } ?: 100.0
            
        // Calcular a redução de efeitos baseada na tolerância
        val effectReduction = when {
            currentDays >= zeroDays -> 0f
            currentDays >= halfDays -> 0.5f
            else -> 1f - (currentDays.toFloat() / halfDays)
        }
            
        val dataForEffectLines = roasWithDurationsDefined.mapIndexed { index, roa ->
            val baseHeight = roa.roaDose?.getStrengthRelativeToCommonDose(firstAverageCommonDose)
                ?.toFloat() ?: 1f
            val adjustedHeight = baseHeight * (1f - effectReduction)
            
            DataForOneEffectLine(
                substanceName = "name$index",
                route = roa.route,
                roaDuration = roa.roaDuration,
                height = adjustedHeight,
                horizontalWeight = 0.5f,
                color = roa.route.color,
                startTime = ingestionTime.getInstant(),
                endTime = null,
            )
        }
        if (dataForEffectLines.isEmpty()) {
            return@map TimelineDisplayOption.NotWorthDrawing
        } else {
            val model = AllTimelinesModel(
                dataForLines = dataForEffectLines,
                dataForRatings = emptyList(),
                timedNotes = emptyList(),
                areSubstanceHeightsIndependent = false
            )
            return@map TimelineDisplayOption.Shown(model)
        }
    }.flowOn(Dispatchers.Default)
        .stateIn(
            initialValue = TimelineDisplayOption.Loading,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )
}