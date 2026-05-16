// Copyright (C) 2026 Nicholas Doglio
// SPDX-License-Identifier: MIT

package dev.whosnickdoglio.animals.core.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun TabItem(animals: List<AnimalTabModel>, modifier: Modifier = Modifier) {
    LazyColumn(modifier.fillMaxSize()) { items(animals) { AnimalItem(it) } }
}

@Composable
internal fun AnimalItem(animal: AnimalTabModel, modifier: Modifier = Modifier) {
    Column(modifier.wrapContentHeight().fillMaxWidth()) {
        Text("Testing")
        Text(animal.name)
    }
}
