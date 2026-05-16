// Copyright (C) 2026 Nicholas Doglio
// SPDX-License-Identifier: MIT

package dev.whosnickdoglio.animals

import android.app.Application
import dev.whosnickdoglio.animals.di.ApplicationComponent
import dev.whosnickdoglio.animals.di.ComponentOwner
import dev.whosnickdoglio.animals.di.create

public class AnimalsApplication: Application(), ComponentOwner {
    override val component: ApplicationComponent by lazy { ApplicationComponent::class.create() }
}
