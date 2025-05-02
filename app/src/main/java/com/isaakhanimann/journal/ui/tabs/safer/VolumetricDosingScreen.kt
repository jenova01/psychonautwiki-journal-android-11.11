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

package com.isaakhanimann.journal.ui.tabs.safer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.isaakhanimann.journal.ui.VOLUMETRIC_DOSE_ARTICLE_URL
import com.isaakhanimann.journal.ui.theme.horizontalPadding

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun VolumetricDosingScreen() {
    val uriHandler = LocalUriHandler.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Volumetric liquid dosing") },
                actions = {
                    TextButton(
                        onClick = { uriHandler.openUri(VOLUMETRIC_DOSE_ARTICLE_URL) },
                    ) {
                        Text("Article")
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = horizontalPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = """Volumetric dosing is the process of dissolving a compound in a liquid to make it easier to measure. In the interest of harm reduction, it is important to use volumetric dosing with certain compounds that are too potent to measure with traditional weighing scales.
Many psychoactive substances, including benzodiazepines and certain psychedelics, are active at less than a single milligram. Such small quantities cannot be accurately measured with common digital scales, so the substance must instead be dosed volumetrically by weighing out larger amounts of the compound and dissolving it in a calculated volume of a suitable liquid.

Search the internet to determine what solvent to use. All substances should dissolve in alcohol, but many substances will not dissolve in water.""",
                textAlign = TextAlign.Left
            )
        }
    }
}