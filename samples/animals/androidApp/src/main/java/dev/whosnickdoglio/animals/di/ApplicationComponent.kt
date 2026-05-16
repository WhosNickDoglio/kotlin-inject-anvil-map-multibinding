// Copyright (C) 2026 Nicholas Doglio
// SPDX-License-Identifier: MIT

package dev.whosnickdoglio.animals.di

import android.content.Context
import com.slack.circuit.foundation.Circuit
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@SingleIn(AppScope::class)
@MergeComponent(AppScope::class)
public interface ApplicationComponent {
    public val circuit: Circuit
}


internal interface ComponentOwner {
    val component: ApplicationComponent
}

internal val Context.injector
    get()= (applicationContext as ComponentOwner).component
