// Copyright (C) 2026 Nicholas Doglio
// SPDX-License-Identifier: MIT

package dev.whosnickdoglio.animals

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.CircuitContent
import dev.whosnickdoglio.animals.core.tabs.TabScreen
import dev.whosnickdoglio.animals.di.DesktopComponent
import dev.whosnickdoglio.animals.di.create

public fun main(): Unit = application {
    val component = remember { DesktopComponent::class.create() }

    Window(onCloseRequest = ::exitApplication, title = "Animals") {
        CircuitCompositionLocals(component.circuit) { CircuitContent(TabScreen) }
    }
}
