/*
 * Copyright (c) 2024. Isaak Hanimann.
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

package com.isaakhanimann.journal.ui.tabs.settings.customunits.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.isaakhanimann.journal.data.room.experiences.entities.CustomUnit
import com.isaakhanimann.journal.data.room.experiences.entities.Ingestion
import com.isaakhanimann.journal.data.room.experiences.entities.PluralizableUnit
import com.isaakhanimann.journal.data.room.experiences.relations.IngestionWithCompanionAndCustomUnit
import com.isaakhanimann.journal.data.substances.AdministrationRoute
import com.isaakhanimann.journal.data.substances.classes.roa.DoseClass
import com.isaakhanimann.journal.data.substances.classes.roa.RoaDose
import com.isaakhanimann.journal.ui.tabs.journal.addingestion.dose.CurrentDoseClassInfo
import com.isaakhanimann.journal.ui.tabs.journal.addingestion.dose.StandardDeviationExplanation
import com.isaakhanimann.journal.ui.tabs.journal.addingestion.search.suggestion.models.toStringWith
import com.isaakhanimann.journal.ui.tabs.journal.experience.components.ingestion.IngestionRow
import com.isaakhanimann.journal.ui.tabs.journal.experience.models.IngestionElement
import com.isaakhanimann.journal.ui.tabs.journal.experience.rating.FloatingDoneButton
import com.isaakhanimann.journal.ui.tabs.search.substance.roa.dose.RoaDosePreviewProvider
import com.isaakhanimann.journal.ui.tabs.search.substance.roa.dose.RoaDoseView
import com.isaakhanimann.journal.ui.theme.horizontalPadding
import com.isaakhanimann.journal.ui.utils.getShortTimeWithWeekdayText
import java.time.Instant

@Composable
fun FinishAddCustomUnitScreen(
    dismissAddCustomUnit: (customUnitId: Int) -> Unit,
    viewModel: FinishAddCustomUnitViewModel = hiltViewModel()
) {
    FinishAddCustomUnitScreenContent(
        substanceName = viewModel.substanceName,
        administrationRoute = viewModel.administrationRoute,
        roaDose = viewModel.roaDose,
        dismiss = {
            viewModel.createSaveAndDismissAfter(dismiss = dismissAddCustomUnit)
        },
        name = viewModel.name,
        onChangeOfName = viewModel::onChangeOfName,
        doseText = viewModel.doseText,
        onChangeDoseText = viewModel::onChangeOfDose,
        estimatedDoseStandardDeviationText = viewModel.estimatedDoseDeviationText,
        onChangeEstimatedDoseDeviationText = viewModel::onChangeOfEstimatedDoseDeviation,
        isEstimate = viewModel.isEstimate,
        onChangeIsEstimate = viewModel::onChangeOfIsEstimate,
        currentDoseClass = viewModel.currentDoseClass,
        isUnitsFieldShown = viewModel.isUnitsFieldShown,
        unit = viewModel.unit,
        onChangeOfUnits = viewModel::onChangeOfUnit,
        unitPlural = viewModel.unitPlural,
        onChangeOfUnitPlural = viewModel::onChangeOfUnitPlural,
        originalUnit = viewModel.originalUnit,
        onChangeOfOriginalUnit = viewModel::onChangeOfOriginalUnit,
        note = viewModel.note,
        onChangeOfNote = viewModel::onChangeOfNote,
        isArchived = viewModel.isArchived,
        onChangeOfIsArchived = viewModel::onChangeOfIsArchived
    )
}

@Preview
@Composable
private fun FinishAddCustomUnitScreenPreview(
    @PreviewParameter(RoaDosePreviewProvider::class) roaDose: RoaDose,
) {
    FinishAddCustomUnitScreenContent(
        substanceName = "Example",
        administrationRoute = AdministrationRoute.ORAL,
        roaDose = roaDose,
        dismiss = {},
        name = "Pink rocket",
        onChangeOfName = {},
        doseText = "10",
        onChangeDoseText = {},
        estimatedDoseStandardDeviationText = "",
        onChangeEstimatedDoseDeviationText = {},
        isEstimate = true,
        onChangeIsEstimate = {},
        currentDoseClass = DoseClass.LIGHT,
        isUnitsFieldShown = false,
        unit = "pill",
        onChangeOfUnits = {},
        unitPlural = "pills",
        onChangeOfUnitPlural = {},
        originalUnit = "mg",
        onChangeOfOriginalUnit = {},
        note = "",
        onChangeOfNote = {},
        isArchived = false,
        onChangeOfIsArchived = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FinishAddCustomUnitScreenContent(
    substanceName: String,
    administrationRoute: AdministrationRoute,
    roaDose: RoaDose?,
    dismiss: () -> Unit,
    name: String,
    onChangeOfName: (String) -> Unit,
    doseText: String,
    onChangeDoseText: (String) -> Unit,
    estimatedDoseStandardDeviationText: String,
    onChangeEstimatedDoseDeviationText: (String) -> Unit,
    isEstimate: Boolean,
    onChangeIsEstimate: (Boolean) -> Unit,
    currentDoseClass: DoseClass?,
    isUnitsFieldShown: Boolean,
    unit: String,
    onChangeOfUnits: (units: String) -> Unit,
    unitPlural: String,
    onChangeOfUnitPlural: (unitPlural: String) -> Unit,
    originalUnit: String,
    onChangeOfOriginalUnit: (String) -> Unit,
    note: String,
    onChangeOfNote: (String) -> Unit,
    isArchived: Boolean,
    onChangeOfIsArchived: (Boolean) -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("$substanceName unit") }) },
        floatingActionButton = {
            FloatingDoneButton(
                onDone = dismiss,
                modifier = Modifier.imePadding()
            )
        }
    ) { padding ->
        EditCustomUnitSections(
            padding = padding,
            substanceName = substanceName,
            administrationRoute = administrationRoute,
            numberOfIngestionsWithThisCustomUnit = null,
            roaDose = roaDose,
            name = name,
            onChangeOfName = onChangeOfName,
            doseText = doseText,
            onChangeDoseText = onChangeDoseText,
            estimatedDoseStandardDeviationText = estimatedDoseStandardDeviationText,
            onChangeEstimatedDoseStandardDeviationText = onChangeEstimatedDoseDeviationText,
            isEstimate = isEstimate,
            onChangeIsEstimate = onChangeIsEstimate,
            currentDoseClass = currentDoseClass,
            isShowingUnitsField = isUnitsFieldShown,
            unit = unit,
            onChangeOfUnits = onChangeOfUnits,
            unitPlural = unitPlural,
            onChangeOfUnitPlural = onChangeOfUnitPlural,
            originalUnit = originalUnit,
            onChangeOfOriginalUnit = onChangeOfOriginalUnit,
            note = note,
            onChangeOfNote = onChangeOfNote,
            isArchived = isArchived,
            onChangeOfIsArchived = onChangeOfIsArchived
        )
    }
}

@Composable
fun EditCustomUnitSections(
    padding: PaddingValues,
    substanceName: String,
    administrationRoute: AdministrationRoute,
    numberOfIngestionsWithThisCustomUnit: Int?,
    roaDose: RoaDose?,
    name: String,
    onChangeOfName: (String) -> Unit,
    doseText: String,
    onChangeDoseText: (String) -> Unit,
    estimatedDoseStandardDeviationText: String,
    onChangeEstimatedDoseStandardDeviationText: (String) -> Unit,
    isEstimate: Boolean,
    onChangeIsEstimate: (Boolean) -> Unit,
    currentDoseClass: DoseClass?,
    isShowingUnitsField: Boolean,
    unit: String,
    onChangeOfUnits: (units: String) -> Unit,
    unitPlural: String,
    onChangeOfUnitPlural: (unitPlural: String) -> Unit,
    originalUnit: String,
    onChangeOfOriginalUnit: (String) -> Unit,
    note: String,
    onChangeOfNote: (String) -> Unit,
    isArchived: Boolean,
    onChangeOfIsArchived: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(padding)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        val textStyle = MaterialTheme.typography.titleMedium
        val focusRequesterName = remember { FocusRequester() }
        val focusRequesterUnit = remember { FocusRequester() }
        val focusRequesterNote = remember { FocusRequester() }
        val focusRequesterDose = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        LaunchedEffect(Unit) {
            focusRequesterName.requestFocus()
        }
        AnimatedVisibility(visible = numberOfIngestionsWithThisCustomUnit != null) {
            if (numberOfIngestionsWithThisCustomUnit != null) {
                ElevatedCard(
                    modifier = Modifier
                        .padding(
                            horizontal = horizontalPadding,
                        )
                        .padding(bottom = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(
                            horizontal = horizontalPadding,
                            vertical = 10.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        if (numberOfIngestionsWithThisCustomUnit > 0) {
                            val pluralizableUnit =
                                PluralizableUnit(singular = "ingestion", plural = "ingestions")
                            Text(
                                "${
                                    numberOfIngestionsWithThisCustomUnit.toStringWith(
                                        pluralizableUnit
                                    )
                                } are affected by this edit"
                            )
                        } else {
                            Text("No ingestions are using this unit yet")
                        }
                    }
                }
            }
        }
        if (substanceName == "Cannabis" && administrationRoute == AdministrationRoute.SMOKED) {
            ElevatedCard(
                modifier = Modifier
                    .padding(
                        horizontal = horizontalPadding,
                    )
                    .padding(bottom = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = horizontalPadding,
                        vertical = 10.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text("When smoking a joint about 23% of the THC in the bud is inhaled. So if you smoke a joint with 300mg of a bud that has 20% THC then you inhale 300mg * 20/100 * 23/100 = 13.8mg THC.")
                    Text("When smoking with a bong about 40% of the THC in the bud is inhaled. So if you smoke 300mg of a bud that has 20% THC then you inhale 300mg * 20/100 * 40/100 = 24mg THC.")
                    Text("When smoking with a vaporizer about 70% of the THC in the bud is inhaled. So if you smoke 300mg of a bud that has 20% THC then you inhale 300mg * 20/100 * 70/100 = 42mg THC.")
                }
            }
        } else if (substanceName == "Psilocybin mushrooms") {
            ElevatedCard(
                modifier = Modifier.padding(
                    horizontal = horizontalPadding,
                    vertical = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = horizontalPadding,
                        vertical = 10.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text("Dried Psilocybe cubensis contain around 1% of Psilocybin.")
                    Text("Fresh Psilocybe cubensis contain around 0.1% of Psilocybin.")
                    Text("Research the strain of mushroom you have to be able to estimate the amount of Psilocybin it contains.")
                }
            }
        } else if (substanceName == "Alcohol") {
            ElevatedCard(
                modifier = Modifier.padding(
                    horizontal = horizontalPadding,
                    vertical = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = horizontalPadding,
                        vertical = 10.dp
                    )
                ) {
                    Text("1 ml of Ethanol is 0.8g. So if you are e.g. consuming 200ml of a spirit with 40% of Alcohol you are consuming 200ml * 40/100 * 0.8 = 64g Ethanol.")
                }
            }
        }
        ElevatedCard(modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 4.dp)) {
            val prompt = when (substanceName) {
                "Cannabis" -> Prompt(
                    name = "e.g. Joint weed 20%, Bong weed 15%, Vaporizer",
                    unit = "mg, joint, hit",
                    unitPlural = "mg, joints, hits"
                )

                "Psilocybin mushrooms" ->
                    Prompt(name = "Mushroom strain", unit = "g", unitPlural = "g")

                "Alcohol" ->
                    Prompt(
                        name = "e.g. beer, wine, spirit",
                        unit = "e.g. ml, cup",
                        unitPlural = "ml, cups"
                    )

                "Caffeine" ->
                    Prompt(
                        name = "e.g. coffee, tea, energy drink",
                        unit = "e.g. cup, can",
                        unitPlural = "cups, cans"
                    )

                else ->
                    when (administrationRoute) {
                        AdministrationRoute.ORAL ->
                            Prompt(
                                name = "e.g. Blue rocket, 85% powder",
                                unit = "e.g. pill, capsule, mg",
                                unitPlural = "e.g. pills, capsules, mg"
                            )

                        AdministrationRoute.SMOKED ->
                            Prompt(
                                name = "e.g. 85% powder",
                                unit = "e.g. mg, hit",
                                unitPlural = "e.g. mg, hits"
                            )

                        AdministrationRoute.INSUFFLATED ->
                            Prompt(
                                name = "e.g. Nasal solution, Blue dispenser",
                                unit = "e.g. spray, spoon, scoop, line",
                                unitPlural = "e.g. sprays, spoons, scoops, lines"
                            )

                        AdministrationRoute.BUCCAL ->
                            Prompt(
                                name = "e.g. Brand name",
                                unit = "e.g. pouch",
                                unitPlural = "pouches"
                            )

                        AdministrationRoute.TRANSDERMAL ->
                            Prompt(
                                name = "e.g. brand name",
                                unit = "e.g. patch",
                                unitPlural = "patches"
                            )

                        else ->
                            Prompt(
                                name = "e.g. 85% powder, blue rocket",
                                unit = "e.g. pill, spray, spoon",
                                unitPlural = "e.g. pills, sprays, spoons"
                            )
                    }
            }
            Column(
                modifier = Modifier.padding(
                    horizontal = horizontalPadding,
                    vertical = 10.dp
                )
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onChangeOfName,
                    textStyle = textStyle,
                    singleLine = true,
                    label = { Text(text = "Name") },
                    placeholder = {
                        Text(prompt.name)
                    },
                    keyboardActions = KeyboardActions(onNext = { focusRequesterUnit.requestFocus() }),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesterName)

                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = onChangeOfUnits,
                    textStyle = textStyle,
                    singleLine = true,
                    label = { Text(text = "Unit singular") },
                    placeholder = {
                        Text(prompt.unit)
                    },
                    keyboardActions = KeyboardActions(onNext = { focusRequesterNote.requestFocus() }),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.None
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesterUnit)
                )
                OutlinedTextField(
                    value = unitPlural,
                    onValueChange = onChangeOfUnitPlural,
                    textStyle = textStyle,
                    singleLine = true,
                    label = { Text(text = "Unit plural") },
                    placeholder = {
                        Text(prompt.unitPlural)
                    },
                    keyboardActions = KeyboardActions(onNext = { focusRequesterNote.requestFocus() }),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.None
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesterUnit)
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = onChangeOfNote,
                    label = { Text(text = "Note") },
                    keyboardActions = KeyboardActions(onNext = { focusRequesterDose.requestFocus() }),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesterNote)
                )
            }
        }
        ElevatedCard(modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 4.dp)) {
            Column(
                modifier = Modifier.padding(
                    horizontal = horizontalPadding,
                    vertical = 10.dp
                )
            ) {
                if (roaDose != null) {
                    RoaDoseView(roaDose = roaDose)
                    AnimatedVisibility(visible = currentDoseClass != null) {
                        if (currentDoseClass != null) {
                            CurrentDoseClassInfo(currentDoseClass, roaDose)
                        }
                    }
                }
                OutlinedTextField(
                    value = doseText,
                    onValueChange = {
                        onChangeDoseText(
                            it.replace(
                                oldChar = ',',
                                newChar = '.'
                            )
                        )
                    },
                    textStyle = textStyle,
                    label = { Text("Dose per $unit", style = textStyle) },
                    trailingIcon = {
                        Text(
                            text = originalUnit,
                            style = textStyle,
                            modifier = Modifier.padding(horizontal = horizontalPadding)
                        )
                    },
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    isError = doseText.toDoubleOrNull() == null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesterDose)
                )
                if (isShowingUnitsField) {
                    OutlinedTextField(
                        value = originalUnit,
                        onValueChange = onChangeOfOriginalUnit,
                        label = { Text("Units") },
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(onClick = { onChangeOfOriginalUnit("µg") }) {
                            Text(text = "µg")
                        }
                        OutlinedButton(onClick = { onChangeOfOriginalUnit("mg") }) {
                            Text(text = "mg")
                        }
                        OutlinedButton(onClick = { onChangeOfOriginalUnit("g") }) {
                            Text(text = "g")
                        }
                        OutlinedButton(onClick = { onChangeOfOriginalUnit("mL") }) {
                            Text(text = "mL")
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Switch(
                        checked = isEstimate,
                        onCheckedChange = onChangeIsEstimate,
                        modifier = Modifier.semantics { contentDescription = "Is an estimate" })
                    Text("Estimate", style = MaterialTheme.typography.titleMedium)
                }
                AnimatedVisibility(visible = isEstimate) {
                    Column {
                        OutlinedTextField(
                            value = estimatedDoseStandardDeviationText,
                            onValueChange = {
                                onChangeEstimatedDoseStandardDeviationText(
                                    it.replace(
                                        oldChar = ',',
                                        newChar = '.'
                                    )
                                )
                            },
                            textStyle = textStyle,
                            label = {
                                Text(
                                    "Estimated standard deviation per $unit",
                                    style = textStyle
                                )
                            },
                            trailingIcon = {
                                Text(
                                    text = originalUnit,
                                    style = textStyle,
                                    modifier = Modifier.padding(horizontal = horizontalPadding)
                                )
                            },
                            keyboardActions = KeyboardActions(onDone = {
                                focusManager.clearFocus()
                            }),
                            isError = estimatedDoseStandardDeviationText.toDoubleOrNull() == null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        val mean = doseText.toDoubleOrNull()
                        val standardDeviation = estimatedDoseStandardDeviationText.toDoubleOrNull()
                        val isExplanationShown = mean != null && standardDeviation != null
                        AnimatedVisibility(isExplanationShown) {
                            if (mean != null && standardDeviation != null) {
                                StandardDeviationExplanation(
                                    mean = mean,
                                    standardDeviation = standardDeviation,
                                    unit = originalUnit
                                )
                            }
                        }
                    }
                }
            }
        }
        if (name.isNotBlank() && unit.isNotBlank()) {
            ElevatedCard(
                modifier = Modifier
                    .padding(horizontal = horizontalPadding, vertical = 4.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        "Ingestion sample preview:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = horizontalPadding)
                    )
                    HorizontalDivider()
                    val customUnit = CustomUnit(
                        id = 123,
                        substanceName = substanceName,
                        name = name,
                        administrationRoute = administrationRoute,
                        unit = unit,
                        unitPlural = unitPlural,
                        originalUnit = originalUnit,
                        dose = doseText.toDoubleOrNull(),
                        estimatedDoseStandardDeviation = estimatedDoseStandardDeviationText.toDoubleOrNull(),
                        isEstimate = isEstimate,
                        isArchived = isArchived,
                        note = "",
                    )
                    val ingestionElement = IngestionElement(
                        ingestionWithCompanionAndCustomUnit = IngestionWithCompanionAndCustomUnit(
                            ingestion = Ingestion(
                                substanceName = substanceName,
                                notes = null,
                                experienceId = 1,
                                consumerName = null,
                                stomachFullness = null,
                                dose = 3.0,
                                isDoseAnEstimate = false,
                                time = Instant.now(),
                                endTime = null,
                                customUnitId = customUnit.id,
                                administrationRoute = administrationRoute,
                                estimatedDoseStandardDeviation = null,
                                units = unit,
                            ),
                            substanceCompanion = null,
                            customUnit = customUnit
                        ),
                        roaDuration = null,
                        numDots = null
                    )
                    IngestionRow(
                        ingestionElement = ingestionElement,
                        areDosageDotsHidden = true,
                        modifier = Modifier.padding(horizontal = horizontalPadding)
                    ) {
                        val timeString = Instant.now().getShortTimeWithWeekdayText()
                        Text(
                            text = timeString,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }
        }
        ElevatedCard(
            modifier = Modifier
                .padding(horizontal = horizontalPadding, vertical = 4.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = horizontalPadding,
                    vertical = 10.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Switch(
                        checked = isArchived,
                        onCheckedChange = onChangeOfIsArchived,
                        modifier = Modifier.semantics { contentDescription = "Archive this unit" })
                    Text("Archive", style = MaterialTheme.typography.titleMedium)
                }
                Text("Archived custom units don't show up when adding ingestions")
            }
        }
    }
}

data class Prompt(
    val name: String,
    val unit: String,
    val unitPlural: String
)
