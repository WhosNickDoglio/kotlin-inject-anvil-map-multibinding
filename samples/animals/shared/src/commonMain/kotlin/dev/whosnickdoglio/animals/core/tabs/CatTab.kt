// Copyright (C) 2026 Nicholas Doglio
// SPDX-License-Identifier: MIT

package dev.whosnickdoglio.animals.core.tabs

import dev.whosnickdoglio.animals.core.data.CatApiService
import dev.whosnickdoglio.inject.multibinding.ContributesMultibinding
import dev.whosnickdoglio.inject.multibinding.StringKey
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

@StringKey("cats")
@ContributesMultibinding(AppScope::class)
@Inject
public class CatTab(private val catApiService: CatApiService) : Tab {
    override suspend fun content(): List<AnimalTabModel> {
        return List(100) { AnimalDto("hello") }
    }
}

// @MapKey
// public annotation class TabKey(val index: Int, val name: String)
