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

package com.isaakhanimann.journal.ui.tabs.search.substance

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.isaakhanimann.journal.data.room.experiences.entities.CustomUnit
import com.isaakhanimann.journal.data.substances.AdministrationRoute
import com.isaakhanimann.journal.data.substances.classes.Category
import com.isaakhanimann.journal.data.substances.classes.SubstanceWithCategories
import com.isaakhanimann.journal.ui.DOSE_DISCLAIMER
import com.isaakhanimann.journal.ui.FULL_STOMACH_DISCLAIMER
import com.isaakhanimann.journal.ui.tabs.journal.addingestion.dose.ChasingTheDragonText
import com.isaakhanimann.journal.ui.tabs.journal.addingestion.dose.OptionalDosageUnitDisclaimer
import com.isaakhanimann.journal.ui.tabs.journal.addingestion.dose.customunit.CustomUnitRoaDoseView
import com.isaakhanimann.journal.ui.tabs.journal.addingestion.time.TimePickerButton
import com.isaakhanimann.journal.ui.tabs.journal.experience.TimelineDisplayOption
import com.isaakhanimann.journal.ui.tabs.journal.experience.components.TimeDisplayOption
import com.isaakhanimann.journal.ui.tabs.journal.experience.timeline.AllTimelines
import com.isaakhanimann.journal.ui.tabs.search.substance.roa.ToleranceSection
import com.isaakhanimann.journal.ui.tabs.search.substance.roa.dose.RoaDoseView
import com.isaakhanimann.journal.ui.tabs.search.substance.roa.duration.RoaDurationView
import com.isaakhanimann.journal.ui.tabs.search.substance.roa.toReadableString
import com.isaakhanimann.journal.ui.theme.JournalTheme
import com.isaakhanimann.journal.ui.theme.horizontalPadding
import com.isaakhanimann.journal.ui.theme.verticalPaddingCards
import com.isaakhanimann.journal.ui.utils.getShortTimeText
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

@Composable
fun SubstanceScreenWithViewModel(
    navigateToDosageExplanationScreen: () -> Unit,
    navigateToSaferHallucinogensScreen: () -> Unit,
    navigateToSaferStimulantsScreen: () -> Unit,
    navigateToVolumetricDosingScreen: () -> Unit,
    navigateToExplainTimeline: () -> Unit,
    navigateToCategoryScreen: (categoryName: String) -> Unit,
    viewModel: SubstanceViewModel = hiltViewModel()
) {
    val lastUseDate by viewModel.lastUseDate.collectAsState()
    SubstanceScreen(
        timelineDisplayOption = viewModel.timelineDisplayOptionFlow.collectAsState().value,
        ingestionTime = viewModel.ingestionTimeFlow.collectAsState().value,
        onChangeIngestionTime = viewModel::changeIngestionTime,
        navigateToDosageExplanationScreen = navigateToDosageExplanationScreen,
        navigateToSaferHallucinogensScreen = navigateToSaferHallucinogensScreen,
        navigateToSaferStimulantsScreen = navigateToSaferStimulantsScreen,
        navigateToVolumetricDosingScreen = navigateToVolumetricDosingScreen,
        navigateToExplainTimeline = navigateToExplainTimeline,
        navigateToCategoryScreen = navigateToCategoryScreen,
        substanceWithCategories = viewModel.substanceWithCategories,
        customUnits = viewModel.customUnitsFlow.collectAsState().value,
        lastUseDate = lastUseDate
    )
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SubstanceScreenPreview(
    @PreviewParameter(SubstanceWithCategoriesPreviewProvider::class) substanceWithCategories: SubstanceWithCategories
) {
    JournalTheme {
        SubstanceScreen(
            timelineDisplayOption = TimelineDisplayOption.Loading,
            ingestionTime = LocalDateTime.now(),
            onChangeIngestionTime = {},
            navigateToDosageExplanationScreen = {},
            navigateToSaferHallucinogensScreen = {},
            navigateToSaferStimulantsScreen = {},
            navigateToVolumetricDosingScreen = {},
            navigateToExplainTimeline = {},
            navigateToCategoryScreen = {},
            substanceWithCategories = substanceWithCategories,
            customUnits = listOf(
                CustomUnit.mdmaSample
            ),
            lastUseDate = null
        )
    }
}

// Função de extensão para parseToDays
private fun String?.parseToDays(): Int {
    if (this == null) return 0
    return when {
        contains("month", ignoreCase = true) -> (split(" ").first().toIntOrNull() ?: 1) * 30
        contains("week", ignoreCase = true) -> (split(" ").first().toIntOrNull() ?: 1) * 7
        contains("day", ignoreCase = true) -> split(" ").first().toIntOrNull() ?: 1
        else -> 0
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SubstanceScreen(
    timelineDisplayOption: TimelineDisplayOption,
    ingestionTime: LocalDateTime,
    onChangeIngestionTime: (LocalDateTime) -> Unit,
    navigateToDosageExplanationScreen: () -> Unit,
    navigateToSaferHallucinogensScreen: () -> Unit,
    navigateToSaferStimulantsScreen: () -> Unit,
    navigateToVolumetricDosingScreen: () -> Unit,
    navigateToExplainTimeline: () -> Unit,
    navigateToCategoryScreen: (categoryName: String) -> Unit,
    substanceWithCategories: SubstanceWithCategories,
    customUnits: List<CustomUnit>,
    lastUseDate: LocalDateTime?
) {
    val substance = substanceWithCategories.substance
    val uriHandler = LocalUriHandler.current
    
    // Usar remember para manter os valores entre recomposições
    var currentDays by remember { mutableStateOf(0) }
    var zeroDays by remember { mutableStateOf(30) }
    var halfDays by remember { mutableStateOf(14) }
    
    // Atualizar valores baseados na tolerância da substância
    LaunchedEffect(substance.tolerance, lastUseDate) {
        zeroDays = substance.tolerance?.zero?.parseToDays() ?: 30
        halfDays = substance.tolerance?.half?.parseToDays() ?: 14
        
        while (true) {
            currentDays = if (lastUseDate != null) {
                ChronoUnit.DAYS.between(lastUseDate, LocalDateTime.now()).toInt()
            } else {
                0
            }
            delay(60000) // Atualizar a cada minuto
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(substance.name) },
                actions = {
                    TextButton(
                        onClick = { uriHandler.openUri(substance.url) },
                    ) {
                        Text("Article")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            if (!substance.isApproved) {
                ElevatedCard(
                    modifier = Modifier
                        .padding(
                            horizontal = horizontalPadding,
                            vertical = verticalPaddingCards
                        )
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(
                            vertical = 5.dp,
                            horizontal = horizontalPadding
                        )
                    ) {
                        Icon(imageVector = Icons.Default.GppBad, contentDescription = "Verified")
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = "Info is not approved")
                    }
                }
            }
            val categories = substanceWithCategories.categories
            if (substance.summary != null || categories.isNotEmpty()) {
                VerticalSpace()
                ElevatedCard(
                    modifier = Modifier.padding(
                        horizontal = horizontalPadding,
                        vertical = verticalPaddingCards
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(
                                horizontal = horizontalPadding,
                                vertical = 10.dp
                            )
                            .fillMaxWidth()
                    ) {
                        if (substance.summary != null) {
                            Text(text = substance.summary)
                            VerticalSpace()
                        }
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            categories.forEach { category ->
                                CategoryChipFromSubstanceScreen(category, navigateToCategoryScreen)
                            }
                        }
                    }
                }
            }
            val roasWithDosesDefined = substance.roas.filter { roa ->
                val roaDose = roa.roaDose
                val isEveryDoseNull =
                    roaDose?.lightMin == null && roaDose?.commonMin == null && roaDose?.strongMin == null && roaDose?.heavyMin == null
                return@filter !isEveryDoseNull
            }
            if (substance.dosageRemark != null || roasWithDosesDefined.isNotEmpty()) {
                SectionWithTitle(title = "Dosage") {
                    Column(Modifier.padding(horizontal = horizontalPadding)) {
                        if (substance.dosageRemark != null) {
                            Text(text = substance.dosageRemark)
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider()
                        }
                        roasWithDosesDefined.forEach { roa ->
                            Column(
                                modifier = Modifier.padding(vertical = 5.dp)
                            ) {
                                Text(
                                    text = roa.route.displayText,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                if (roa.roaDose == null) {
                                    Text(text = "No dosage info")
                                } else {
                                    RoaDoseView(roaDose = roa.roaDose)
                                }
                                roa.roaDose?.let { roaDose ->
                                    val customUnitsForRoute =
                                        customUnits.filter { it.administrationRoute == roa.route && it.dose != null }
                                    customUnitsForRoute.forEach { customUnit ->
                                        Text(
                                            text = customUnit.name,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        CustomUnitRoaDoseView(
                                            roaDose = roaDose,
                                            customUnit = customUnit
                                        )
                                    }
                                }
                                val bio = roa.bioavailability
                                if (bio != null) {
                                    Text(text = "Bioavailability: ${bio.min?.toReadableString() ?: ".."}-${bio.max?.toReadableString() ?: ".."}%")
                                }
                                if (roa.route == AdministrationRoute.SMOKED && substance.name != "Cannabis") {
                                    Spacer(modifier = Modifier.height(5.dp))
                                    ChasingTheDragonText(
                                        titleStyle = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                            HorizontalDivider()
                        }
                        VerticalSpace()
                        OptionalDosageUnitDisclaimer(substance.name)
                        Text(text = DOSE_DISCLAIMER)
                        VerticalSpace()
                        if (substance.roas.any { it.roaDose?.shouldUseVolumetricDosing == true }) {
                            HorizontalDivider()
                            TextButton(onClick = navigateToVolumetricDosingScreen) {
                                Icon(
                                    Icons.Outlined.Info,
                                    contentDescription = "Info",
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Volumetric dosing")
                            }
                        }
                        HorizontalDivider()
                        TextButton(onClick = navigateToDosageExplanationScreen) {
                            Icon(
                                Icons.Outlined.Info,
                                contentDescription = "Info",
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text("Dosage classification")
                        }

                    }
                }
            }
            if (substance.tolerance != null || substance.crossTolerances.isNotEmpty()) {
                SectionWithTitle(title = "Tolerance") {
                    Column {
                        VerticalSpace()
                        ToleranceSection(
                            tolerance = substance.tolerance,
                            crossTolerances = substance.crossTolerances,
                            lastUseDate = lastUseDate,
                            modifier = Modifier.padding(horizontal = horizontalPadding)
                        )
                        VerticalSpace()
                    }
                }
            }
            if (substance.toxicities.isNotEmpty()) {
                SectionWithTitle(title = "Toxicity") {
                    Column {
                        VerticalSpace()
                        if (substance.toxicities.size == 1) {
                            Text(
                                substance.toxicities.firstOrNull() ?: "",
                                modifier = Modifier.padding(horizontal = horizontalPadding)
                            )
                        } else {
                            BulletPoints(
                                points = substance.toxicities,
                                modifier = Modifier.padding(horizontal = horizontalPadding)
                            )
                        }
                        VerticalSpace()
                    }
                }
            }
            val roasWithDurationsDefined = substance.roas.filter { roa ->
                val roaDuration = roa.roaDuration
                val isEveryDurationNull =
                    roaDuration?.onset == null && roaDuration?.comeup == null && roaDuration?.peak == null && roaDuration?.offset == null && roaDuration?.total == null
                return@filter !isEveryDurationNull
            }
            if (roasWithDurationsDefined.isNotEmpty()) {
                SectionWithTitle(title = "Duration") {
                    Column(Modifier.padding(horizontal = horizontalPadding)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Start:")
                            Spacer(modifier = Modifier.width(5.dp))
                            TimePickerButton(
                                localDateTime = ingestionTime,
                                onChange = onChangeIngestionTime,
                                timeString = ingestionTime.getShortTimeText(),
                                hasOutline = false,
                            )
                            val isTimeALotDifferentToNow = ChronoUnit.MINUTES.between(
                                ingestionTime,
                                LocalDateTime.now()
                            ).absoluteValue > 5
                            Spacer(modifier = Modifier.width(5.dp))
                            AnimatedVisibility(visible = isTimeALotDifferentToNow) {
                                IconButton(onClick = { onChangeIngestionTime(LocalDateTime.now()) }) {
                                    Icon(
                                        imageVector = Icons.Default.Update,
                                        contentDescription = "Reset to now"
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = navigateToExplainTimeline) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "Timeline disclaimer"
                                )
                            }
                        }
                        VerticalSpace()
                        when (timelineDisplayOption) {
                            TimelineDisplayOption.Hidden -> {}
                            TimelineDisplayOption.Loading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            TimelineDisplayOption.NotWorthDrawing -> {}
                            is TimelineDisplayOption.Shown -> {
                                val timelineModel = timelineDisplayOption.allTimelinesModel
                                Column {
                                    val effectReduction = when {
                                        currentDays >= zeroDays -> 0f
                                        currentDays >= halfDays -> 0.5f
                                        else -> 1f - (currentDays.toFloat() / halfDays)
                                    }
                                    
                                    Text(
                                        text = "Efeitos reduzidos em ${(effectReduction * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(bottom = 8.dp),
                                        color = when {
                                            effectReduction > 0.7f -> Color.Red
                                            effectReduction > 0.3f -> Color.Yellow
                                            else -> Color.Green
                                        }
                                    )
                                    
                                    AllTimelines(
                                        model = timelineModel,
                                        isShowingCurrentTime = false,
                                        timeDisplayOption = TimeDisplayOption.RELATIVE_TO_NOW,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        roasWithDurationsDefined.forEachIndexed { index, roa ->
                            Column(
                                modifier = Modifier.padding(
                                    vertical = 5.dp,
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    RouteColorCircle(roa.route)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = roa.route.displayText,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                val roaDuration = roa.roaDuration
                                if (roaDuration == null) {
                                    Text(text = "No duration info")
                                } else {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    RoaDurationView(roaDuration = roaDuration)
                                    if (roa.route == AdministrationRoute.ORAL) {
                                        Text(
                                            text = FULL_STOMACH_DISCLAIMER,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                            if (index < roasWithDurationsDefined.size - 1) {
                                HorizontalDivider()
                            }
                        }
                        VerticalSpace()
                    }
                }
            }
            val interactions = substance.interactions
            if (interactions != null) {
                if (interactions.dangerous.isNotEmpty() || interactions.unsafe.isNotEmpty() || interactions.uncertain.isNotEmpty()) {
                    SectionWithTitle(title = "Interactions") {
                        InteractionsView(
                            interactions = substance.interactions,
                            substanceURL = substance.url,
                        )
                    }
                }
            }
            if (substance.effectsSummary != null) {
                SectionWithTitle(title = "Effects") {
                    Column {
                        Text(
                            text = substance.effectsSummary,
                            modifier = Modifier.padding(horizontal = horizontalPadding)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            if (substance.generalRisks != null && substance.longtermRisks != null) {
                SectionWithTitle(title = "Risks") {
                    Column {
                        Text(
                            text = substance.generalRisks,
                            modifier = Modifier.padding(horizontal = horizontalPadding)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                SectionWithTitle(title = "Long-term") {
                    Column {
                        Text(
                            text = substance.longtermRisks,
                            modifier = Modifier.padding(horizontal = horizontalPadding)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            if (substance.saferUse.isNotEmpty()) {
                SectionWithTitle(title = "Safer use") {
                    Column {
                        BulletPoints(
                            points = substance.saferUse,
                            modifier = Modifier.padding(horizontal = horizontalPadding)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            if (substance.addictionPotential != null) {
                SectionWithTitle(title = "Addiction potential") {
                    Column {
                        Text(
                            substance.addictionPotential,
                            modifier = Modifier.padding(horizontal = horizontalPadding)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            val firstRoa = substance.roas.firstOrNull()
            val useVolumetric = firstRoa?.roaDose?.shouldUseVolumetricDosing == true
            if (substance.isHallucinogen || substance.isStimulant || useVolumetric) {
                SectionWithTitle(title = "See also") {
                    Column {
                        if (substance.isHallucinogen) {
                            TextButton(onClick = navigateToSaferHallucinogensScreen) {
                                Text(
                                    text = "Safer hallucinogen use",
                                    modifier = Modifier.padding(horizontal = horizontalPadding)
                                )
                            }
                            HorizontalDivider()
                        }
                        if (substance.isStimulant) {
                            TextButton(onClick = navigateToSaferStimulantsScreen) {
                                Text(
                                    text = "Safer stimulant use",
                                    modifier = Modifier.padding(horizontal = horizontalPadding)
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
fun RouteColorCircle(administrationRoute: AdministrationRoute) {
    val isDarkTheme = isSystemInDarkTheme()
    Surface(
        shape = CircleShape,
        color = administrationRoute.color.getComposeColor(isDarkTheme),
        modifier = Modifier
            .size(20.dp)
    ) {}
}

@Composable
fun BulletPoints(points: List<String>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        points.forEach {
            Row(verticalAlignment = Alignment.Top) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(top = 7.dp)
                        .size(7.dp)
                ) {}
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = it)
            }
        }
    }
}

@Composable
fun VerticalSpace() {
    Spacer(modifier = Modifier.height(5.dp))
}


@Composable
fun CategoryChipFromSubstanceScreen(
    category: Category,
    navigateToCategoryScreen: (categoryName: String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(shape = CircleShape)
            .clickable {
                navigateToCategoryScreen(category.name)
            }
            .background(color = category.color.copy(alpha = 0.2f))
            .height(48.dp)
            .padding(horizontal = 12.dp)

    ) {
        Text(text = category.name)
        Spacer(modifier = Modifier.width(3.dp))
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Go to",
            modifier = Modifier.size(20.dp)
        )
    }
}