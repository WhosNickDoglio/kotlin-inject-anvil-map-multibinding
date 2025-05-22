/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

package dev.whosnickdoglio.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

internal class KotlinMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) { pluginManager.apply("org.jetbrains.kotlin.multiplatform") }
}
