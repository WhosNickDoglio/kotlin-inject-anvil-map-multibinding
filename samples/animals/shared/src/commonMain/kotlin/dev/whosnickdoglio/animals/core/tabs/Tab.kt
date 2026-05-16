// Copyright (C) 2026 Nicholas Doglio
// SPDX-License-Identifier: MIT

package dev.whosnickdoglio.animals.core.tabs

public fun interface Tab {
    public suspend fun content(): List<AnimalTabModel>
}

public interface AnimalTabModel {
    public val name: String
}

public data class AnimalDto(override val name: String) : AnimalTabModel
