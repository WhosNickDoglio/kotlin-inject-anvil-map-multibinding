// Copyright (C) 2026 Nicholas Doglio
// SPDX-License-Identifier: MIT

package dev.whosnickdoglio.animals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.CircuitContent
import dev.whosnickdoglio.animals.core.tabs.TabScreen
import dev.whosnickdoglio.animals.di.injector
import dev.whosnickdoglio.animals.ui.theme.AnimalsTheme

public class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimalsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CircuitCompositionLocals(injector.circuit) {
                        CircuitContent(
                            TabScreen,
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                }
            }
        }
    }
}
