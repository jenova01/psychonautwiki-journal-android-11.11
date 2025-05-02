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

package com.isaakhanimann.journal.ui.tabs.journal.addingestion.interactions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.isaakhanimann.journal.data.room.experiences.ExperienceRepository
import com.isaakhanimann.journal.data.room.experiences.entities.Ingestion
import com.isaakhanimann.journal.data.substances.classes.InteractionType
import com.isaakhanimann.journal.data.substances.repositories.SubstanceRepository
import com.isaakhanimann.journal.ui.main.navigation.graphs.CheckInteractionsRoute
import com.isaakhanimann.journal.ui.tabs.settings.combinations.CombinationSettingsStorage
import com.isaakhanimann.journal.ui.utils.getTimeDifferenceText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class CheckInteractionsViewModel @Inject constructor(
    substanceRepo: SubstanceRepository,
    private val experienceRepo: ExperienceRepository,
    private val combinationSettingsStorage: CombinationSettingsStorage,
    private val interactionChecker: InteractionChecker,
    state: SavedStateHandle,
) : ViewModel() {
    private val checkInteractionsRoute = state.toRoute<CheckInteractionsRoute>()
    val substanceName = checkInteractionsRoute.substanceName
    val substance = substanceRepo.getSubstance(substanceName)!!
    val dangerousInteractions = substance.interactions?.dangerous ?: emptyList()
    val unsafeInteractions = substance.interactions?.unsafe ?: emptyList()
    val uncertainInteractions = substance.interactions?.uncertain ?: emptyList()

    var isSearchingForInteractions by mutableStateOf(true)
    var isShowingAlert by mutableStateOf(false)
    var alertInteractionType by mutableStateOf<InteractionType?>(null)
    var alertText by mutableStateOf("")
    private var latestIngestionsOfEverySubstanceSinceTwoDays: List<Ingestion> = emptyList()

    init {
        viewModelScope.launch {
            val twoDaysAgo = Instant.now().minus(2, ChronoUnit.DAYS)
            latestIngestionsOfEverySubstanceSinceTwoDays =
                experienceRepo.getLatestIngestionOfEverySubstanceSinceDate(twoDaysAgo)
            checkInteractionsAndMaybeShowAlert()
            isSearchingForInteractions = false
        }
    }

    private suspend fun checkInteractionsAndMaybeShowAlert() {
        val filteredIngestions = getIngestionsWithInteraction()
        val dangerousIngestions =
            filteredIngestions.filter { it.interactionType == InteractionType.DANGEROUS }
                .map { it.ingestion }.sortedByDescending { it.time }
        val unsafeIngestions =
            filteredIngestions.filter { it.interactionType == InteractionType.UNSAFE }
                .map { it.ingestion }.sortedByDescending { it.time }
        val uncertainIngestions =
            filteredIngestions.filter { it.interactionType == InteractionType.UNCERTAIN }
                .map { it.ingestion }.sortedByDescending { it.time }
        val enabledExtraInteractions =
            combinationSettingsStorage.enabledInteractionsFlow.first()
        val dangerousExtras =
            enabledExtraInteractions.filter { substance.interactions?.dangerous?.contains(it) == true }
        val unsafeExtras =
            enabledExtraInteractions.filter { substance.interactions?.unsafe?.contains(it) == true }
        val uncertainExtras =
            enabledExtraInteractions.filter { substance.interactions?.uncertain?.contains(it) == true }
        alertInteractionType =
            if (dangerousIngestions.isNotEmpty() || dangerousExtras.isNotEmpty()) {
                InteractionType.DANGEROUS
            } else if (unsafeIngestions.isNotEmpty() || unsafeExtras.isNotEmpty()) {
                InteractionType.UNSAFE
            } else if (uncertainIngestions.isNotEmpty() || uncertainExtras.isNotEmpty()) {
                InteractionType.UNCERTAIN
            } else {
                return
            }
        val now = Instant.now()
        val messages = dangerousIngestions.map { ingestion ->
            "Dangerous interaction with ${ingestion.substanceName} (taken ${
                getTimeDifferenceText(
                    fromInstant = ingestion.time,
                    toInstant = now
                )
            } ago)."
        }.toMutableList()
        messages += dangerousExtras.map { extra ->
            "Dangerous interaction with $extra."
        }
        messages += unsafeIngestions.map { ingestion ->
            "Unsafe interaction with ${ingestion.substanceName} (taken ${
                getTimeDifferenceText(
                    fromInstant = ingestion.time,
                    toInstant = now
                )
            } ago)."
        }
        messages += unsafeExtras.map { extra ->
            "Unsafe interaction with $extra."
        }
        messages += uncertainIngestions.map { ingestion ->
            "Uncertain interaction with ${ingestion.substanceName} (taken ${
                getTimeDifferenceText(
                    fromInstant = ingestion.time,
                    toInstant = now
                )
            } ago)."
        }
        messages += uncertainExtras.map { extra ->
            "Uncertain interaction with $extra."
        }
        alertText = messages.distinct().joinToString(separator = "\n")
        isShowingAlert = true
    }

    private fun getIngestionsWithInteraction(): List<IngestionInteraction> {
        return latestIngestionsOfEverySubstanceSinceTwoDays.mapNotNull { ingestion ->
            val interaction = interactionChecker.getInteractionBetween(
                aName = substanceName,
                bName = ingestion.substanceName
            )
            if (interaction != null) {
                return@mapNotNull IngestionInteraction(
                    ingestion = ingestion,
                    interactionType = interaction.interactionType
                )
            } else {
                return@mapNotNull null
            }
        }
    }

    data class IngestionInteraction(
        val ingestion: Ingestion,
        val interactionType: InteractionType
    )


}