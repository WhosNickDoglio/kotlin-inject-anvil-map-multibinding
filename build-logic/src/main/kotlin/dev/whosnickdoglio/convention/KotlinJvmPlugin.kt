// Copyright (C) 2025 Nicholas Doglio
// SPDX-License-Identifier: MIT

package dev.whosnickdoglio.convention

import dev.whosnickdoglio.convention.configuration.applyLintingPlugins
import dev.whosnickdoglio.convention.configuration.configureJvm
import dev.whosnickdoglio.convention.configuration.configureLint
import dev.whosnickdoglio.convention.configuration.configureTests
import dev.whosnickdoglio.convention.configuration.getVersionOrError
import dev.whosnickdoglio.convention.configuration.versionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class KotlinJvmPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit =
        with(target) {
            val libs = versionCatalog()
            pluginManager.apply("org.jetbrains.kotlin.jvm")

            val jvmTargetVersion = libs.getVersionOrError("jdkTarget")

            applyLintingPlugins(jvmTargetVersion)

            configureJvm(
                toolchainVersion = libs.getVersionOrError("jdk").toInt(),
                jvmTargetVersion = jvmTargetVersion.toInt(),
            )
            configureLint()
            configureTests()
        }
}
