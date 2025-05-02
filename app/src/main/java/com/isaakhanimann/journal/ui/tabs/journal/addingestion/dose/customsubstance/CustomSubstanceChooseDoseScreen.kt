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

package com.isaakhanimann.journal.ui.tabs.journal.addingestion.dose.customsubstance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.isaakhanimann.journal.data.substances.AdministrationRoute
import com.isaakhanimann.journal.ui.tabs.journal.addingestion.dose.PurityCalculation
import com.isaakhanimann.journal.ui.tabs.journal.addingestion.dose.StandardDeviationExplanation
import com.isaakhanimann.journal.ui.tabs.journal.addingestion.dose.UnknownDoseDialog
import com.isaakhanimann.journal.ui.theme.horizontalPadding

@Composable
fun CustomSubstanceChooseDoseScreen(
    navigateToChooseTimeAndMaybeColor: (units: String?, isEstimate: Boolean, dose: Double?, estimatedDoseStandardDeviation: Double?) -> Unit,
    navigateToCreateCustomUnit: () -> Unit,
    navigateToSaferSniffingScreen: () -> Unit,
    viewModel: CustomSubstanceChooseDoseViewModel = hiltViewModel()
) {
    CustomSubstanceChooseDoseScreen(
        navigateToSaferSniffingScreen = navigateToSaferSniffingScreen,
        navigateToCreateCustomUnit = navigateToCreateCustomUnit,
        substanceName = viewModel.substanceName,
        administrationRoute = viewModel.administrationRoute,
        doseText = viewModel.doseText,
        onChangeDoseText = viewModel::onDoseTextChange,
        estimatedDoseStandardDeviationText = viewModel.estimatedDoseDeviationText,
        onChangeEstimatedStandardDeviationText = viewModel::onEstimatedDoseStandardDeviationTextChange,
        isValidDose = viewModel.isValidDose,
        isEstimate = viewModel.isEstimate,
        onChangeIsEstimate = {
            viewModel.isEstimate = it
        },
        navigateToNext = {
            navigateToChooseTimeAndMaybeColor(
                viewModel.units,
                viewModel.isEstimate,
                viewModel.dose,
                viewModel.estimatedDoseStandardDeviation
            )
        },
        useUnknownDoseAndNavigate = {
            navigateToChooseTimeAndMaybeColor(
                viewModel.units,
                false,
                null,
                null
            )
        },
        purityText = viewModel.purityText,
        onPurityChange = {
            viewModel.purityText = it
        },
        isValidPurity = viewModel.isPurityValid,
        convertedDoseAndUnitText = viewModel.impureDoseWithUnit,
        units = viewModel.units,
    )
}

@Preview
@Composable
fun CustomChooseDosePreview() {
    CustomSubstanceChooseDoseScreen(
        navigateToSaferSniffingScreen = {},
        navigateToCreateCustomUnit = {},
        substanceName = "Example Substance",
        administrationRoute = AdministrationRoute.INSUFFLATED,
        doseText = "5",
        onChangeDoseText = {},
        estimatedDoseStandardDeviationText = "",
        onChangeEstimatedStandardDeviationText = {},
        isValidDose = true,
        isEstimate = false,
        onChangeIsEstimate = {},
        navigateToNext = {},
        useUnknownDoseAndNavigate = {},
        purityText = "20",
        onPurityChange = {},
        isValidPurity = true,
        convertedDoseAndUnitText = "25 impure mg",
        units = "mg",
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSubstanceChooseDoseScreen(
    navigateToSaferSniffingScreen: () -> Unit,
    navigateToCreateCustomUnit: () -> Unit,
    substanceName: String,
    administrationRoute: AdministrationRoute,
    doseText: String,
    onChangeDoseText: (String) -> Unit,
    estimatedDoseStandardDeviationText: String,
    onChangeEstimatedStandardDeviationText: (String) -> Unit,
    isValidDose: Boolean,
    isEstimate: Boolean,
    onChangeIsEstimate: (Boolean) -> Unit,
    navigateToNext: () -> Unit,
    useUnknownDoseAndNavigate: () -> Unit,
    purityText: String,
    onPurityChange: (purity: String) -> Unit,
    isValidPurity: Boolean,
    convertedDoseAndUnitText: String?,
    units: String,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(administrationRoute.displayText + " " + substanceName + " Dosage") },
                actions = {
                    var isShowingUnknownDoseDialog by remember { mutableStateOf(false) }
                    IconButton(onClick = { isShowingUnknownDoseDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.QuestionMark,
                            contentDescription = "Log unknown dose"
                        )
                    }
                    AnimatedVisibility(visible = isShowingUnknownDoseDialog) {
                        UnknownDoseDialog(
                            useUnknownDoseAndNavigate = useUnknownDoseAndNavigate,
                            dismiss = { isShowingUnknownDoseDialog = false }
                        )
                    }
                })
        },
        floatingActionButton = {
            if (isValidDose) {
                ExtendedFloatingActionButton(
                    modifier = Modifier.imePadding(),
                    onClick = navigateToNext,
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.NavigateNext,
                            contentDescription = "Next"
                        )
                    },
                    text = { Text("Next") },
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            LinearProgressIndicator(
                progress = { 0.67f },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(4.dp))
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
                    val focusManager = LocalFocusManager.current
                    val textStyle = MaterialTheme.typography.titleMedium
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
                        label = { Text("Pure dose", style = textStyle) },
                        isError = !isValidDose,
                        trailingIcon = {
                            Text(
                                text = units,
                                style = textStyle,
                                modifier = Modifier.padding(horizontal = horizontalPadding)
                            )
                        },
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                        }),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Switch(checked = isEstimate, onCheckedChange = onChangeIsEstimate)
                        Text("Estimate", style = MaterialTheme.typography.titleMedium)
                    }
                    AnimatedVisibility(visible = isEstimate) {
                        Column {
                            OutlinedTextField(
                                value = estimatedDoseStandardDeviationText,
                                onValueChange = {
                                    onChangeEstimatedStandardDeviationText(
                                        it.replace(
                                            oldChar = ',',
                                            newChar = '.'
                                        )
                                    )
                                },
                                textStyle = textStyle,
                                label = { Text("Estimated standard deviation", style = textStyle) },
                                isError = !isValidDose,
                                trailingIcon = {
                                    Text(
                                        text = units,
                                        style = textStyle,
                                        modifier = Modifier.padding(horizontal = horizontalPadding)
                                    )
                                },
                                keyboardActions = KeyboardActions(onDone = {
                                    focusManager.clearFocus()
                                }),
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
                                        unit = units
                                    )
                                }
                            }
                        }
                    }
                    TextButton(onClick = navigateToCreateCustomUnit) {
                        Text(text = "Create custom unit")
                    }
                }
            }
            AnimatedVisibility(visible = isValidDose) {
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
                        PurityCalculation(
                            purityText = purityText,
                            onPurityChange = onPurityChange,
                            convertedDoseAndUnitText = convertedDoseAndUnitText,
                            isValidPurity = isValidPurity
                        )
                    }
                }
            }
            if (administrationRoute == AdministrationRoute.INSUFFLATED) {
                TextButton(onClick = navigateToSaferSniffingScreen) {
                    Text(text = "Safer sniffing")
                }
            } else if (administrationRoute == AdministrationRoute.RECTAL) {
                val uriHandler = LocalUriHandler.current
                TextButton(onClick = { uriHandler.openUri(AdministrationRoute.SAFER_PLUGGING_ARTICLE_URL) }) {
                    Icon(
                        Icons.Outlined.OpenInBrowser,
                        contentDescription = "Open link"
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Safer plugging")
                }
            }
        }
    }
}