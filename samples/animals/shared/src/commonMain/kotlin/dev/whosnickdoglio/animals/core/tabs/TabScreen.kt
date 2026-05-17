// Copyright (C) 2026 Nicholas Doglio
// SPDX-License-Identifier: MIT

package dev.whosnickdoglio.animals.core.tabs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.whosnickdoglio.animals.core.CommonParcelize
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

@CommonParcelize
public object TabScreen : Screen {

    public data class State(
        val selected: SelectTabInfo,
        val tabs: Map<String, Tab>,
        val events: (Event) -> Unit,
    ) : CircuitUiState

    public sealed interface Event : CircuitUiEvent {
        public data class SelectTab(val name: String) : Event
    }
}

public data class SelectTabInfo(val name: String, val index: Int)

@CircuitInject(TabScreen::class, AppScope::class)
@Composable
public fun TabScreen(state: TabScreen.State, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) {
        PrimaryTabRow(selectedTabIndex = state.selected.index, modifier = Modifier.padding(it)) {
            state.tabs.forEach { (name, _) ->
                Tab(
                    selected = state.selected.name == name,
                    onClick = {
                        // TODO need to pass index
                        state.events(TabScreen.Event.SelectTab(name))
                    },
                    text = { Text(name) },
                )
            }
        }

        val animals by
            produceState(emptyList<AnimalTabModel>()) {
                state.tabs[state.selected.name]!!.content()
            }

        TabItem(animals)
    }
}

@CircuitInject(TabScreen::class, AppScope::class)
@Inject
public class TabScreenPresenter(
    private val tabs: Map<String, Tab>,
    @Assisted private val screen: TabScreen,
) : Presenter<TabScreen.State> {
    @Composable
    override fun present(): TabScreen.State {
        var select by remember { mutableStateOf(SelectTabInfo("cats", 0)) }

        return TabScreen.State(selected = select, tabs = tabs) { event ->
            when (event) {
                is TabScreen.Event.SelectTab -> {
                    select = SelectTabInfo(event.name, 0)
                }
            }
        }
    }
}
