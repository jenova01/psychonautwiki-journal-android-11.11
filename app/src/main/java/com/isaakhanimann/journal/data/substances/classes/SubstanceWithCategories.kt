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

package com.isaakhanimann.journal.data.substances.classes

import com.isaakhanimann.journal.ui.tabs.search.CategoryModel
import com.isaakhanimann.journal.ui.tabs.search.SubstanceModel

data class SubstanceWithCategories(
    val substance: Substance, val categories: List<Category>


) {
    fun toSubstanceModel(): SubstanceModel {
        return SubstanceModel(
            name = substance.name,
            commonNames = substance.commonNames,
            categories = categories.map { category ->
                CategoryModel(
                    name = category.name, color = category.color
                )
            },
            hasSaferUse = substance.saferUse.isNotEmpty(),
            hasInteractions = substance.hasInteractions
        )
    }
}
