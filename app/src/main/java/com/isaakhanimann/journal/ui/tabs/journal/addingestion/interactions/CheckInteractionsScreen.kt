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

package com.isaakhanimann.journal.ui.tabs.journal.addingestion.interactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.isaakhanimann.journal.data.substances.classes.InteractionType
import com.isaakhanimann.journal.data.substances.classes.SubstanceWithCategories
import com.isaakhanimann.journal.ui.tabs.journal.addingestion.NextFAB
import com.isaakhanimann.journal.ui.tabs.search.substance.InteractionExplanationButton
import com.isaakhanimann.journal.ui.tabs.search.substance.SubstanceWithCategoriesPreviewProvider
import com.isaakhanimann.journal.ui.theme.horizontalPadding


@Composable
fun CheckInteractionsScreen(
    navigateToNext: () -> Unit,
    viewModel: CheckInteractionsViewModel = hiltViewModel()
) {
    CheckInteractionsScreen(
        substanceName = viewModel.substanceName,
        substanceUrl = viewModel.substance.url,
        isSearchingForInteractions = viewModel.isSearchingForInteractions,
        dangerousInteractions = viewModel.dangerousInteractions,
        unsafeInteractions = viewModel.unsafeInteractions,
        uncertainInteractions = viewModel.uncertainInteractions,
        navigateToNext = navigateToNext,
        dismissAlert = {
            viewModel.isShowingAlert = false
        },
        isShowingAlert = viewModel.isShowingAlert,
        alertInteractionType = viewModel.alertInteractionType,
        alertText = viewModel.alertText
    )
}

@Preview
@Composable
fun CheckInteractionsScreenPreview(@PreviewParameter(SubstanceWithCategoriesPreviewProvider::class) substanceWithCategories: SubstanceWithCategories) {
    CheckInteractionsScreen(
        substanceName = "LSD",
        substanceUrl = "",
        isSearchingForInteractions = true,
        dangerousInteractions = substanceWithCategories.substance.interactions?.dangerous
            ?: emptyList(),
        unsafeInteractions = substanceWithCategories.substance.interactions?.unsafe ?: emptyList(),
        uncertainInteractions = substanceWithCategories.substance.interactions?.uncertain
            ?: emptyList(),
        navigateToNext = {},
        dismissAlert = {},
        isShowingAlert = false,
        alertInteractionType = InteractionType.DANGEROUS,
        alertText = "Dangerous interaction with Heroin taken 4h ago"
    )
}

@Preview
@Composable
fun CheckInteractionsScreenPreview2() {
    CheckInteractionsScreen(
        substanceName = "MDMA",
        substanceUrl = "",
        isSearchingForInteractions = true,
        dangerousInteractions = emptyList(),
        unsafeInteractions = emptyList(),
        uncertainInteractions = emptyList(),
        navigateToNext = {},
        dismissAlert = {},
        isShowingAlert = true,
        alertInteractionType = InteractionType.DANGEROUS,
        alertText = "Dangerous interaction with Heroin taken 4h ago."
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInteractionsScreen(
    substanceName: String,
    substanceUrl: String,
    isSearchingForInteractions: Boolean,
    isShowingAlert: Boolean,
    dismissAlert: () -> Unit,
    alertInteractionType: InteractionType?,
    alertText: String,
    dangerousInteractions: List<String>,
    unsafeInteractions: List<String>,
    uncertainInteractions: List<String>,
    navigateToNext: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("$substanceName interactions") }) },
        floatingActionButton = {
            NextFAB(navigateToNext)
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (isSearchingForInteractions) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else {
                LinearProgressIndicator(
                    progress = { 0.33f },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (dangerousInteractions.isEmpty() && unsafeInteractions.isEmpty() && uncertainInteractions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No interactions found... check other sources.")
                }
            } else {
                val verticalPaddingInside = 4.dp
                LazyColumn {
                    if (dangerousInteractions.isNotEmpty()) {
                        items(dangerousInteractions) {
                            InteractionRow(
                                text = it,
                                interactionType = InteractionType.DANGEROUS,
                                verticalPaddingInside = verticalPaddingInside,
                            )
                        }
                    }
                    if (unsafeInteractions.isNotEmpty()) {
                        items(unsafeInteractions) {
                            InteractionRow(
                                text = it, interactionType = InteractionType.UNSAFE,
                                verticalPaddingInside = verticalPaddingInside,
                            )
                        }
                    }
                    if (uncertainInteractions.isNotEmpty()) {
                        items(uncertainInteractions) {
                            InteractionRow(
                                text = it, interactionType = InteractionType.UNCERTAIN,
                                verticalPaddingInside = verticalPaddingInside,
                            )
                        }
                    }
                    item {
                        InteractionExplanationButton(substanceURL = substanceUrl)
                    }
                }
            }
            AnimatedVisibility(visible = isShowingAlert) {
                AlertDialog(
                    onDismissRequest = dismissAlert,
                    title = {
                        val title = when (alertInteractionType) {
                            InteractionType.DANGEROUS -> "Dangerous interaction!"
                            InteractionType.UNSAFE -> "Unsafe interaction"
                            InteractionType.UNCERTAIN -> "Uncertain interaction"
                            else -> "Interaction"
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Warning",
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(text = title, style = MaterialTheme.typography.titleLarge)
                        }
                    },
                    text = {
                        Text(text = alertText)
                    },
                    confirmButton = {
                        Row(
                            modifier = Modifier.padding(all = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TextButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = dismissAlert
                            ) {
                                Text("Dismiss")
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun InteractionRow(
    text: String,
    interactionType: InteractionType,
    verticalPaddingInside: Dp = 2.dp
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RectangleShape,
        color = interactionType.color
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = horizontalPadding,
                vertical = verticalPaddingInside
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.weight(1f))
            LazyRow {
                items(interactionType.dangerCount) {
                    Icon(
                        imageVector = Icons.Outlined.WarningAmber,
                        contentDescription = "Warning",
                        tint = Color.Black,
                    )
                }
            }
        }
    }
}