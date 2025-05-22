/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

package dev.whosnickdoglio.convention.configuration

import com.android.build.api.dsl.Lint
import com.diffplug.gradle.spotless.SpotlessExtension
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.Project

internal fun Project.applyLintingPlugins(jvmTarget: String) {
    pluginManager.apply("io.gitlab.arturbosch.detekt")
    dependOnBuildLogicTask("detektMain")
    dependOnBuildLogicTask("detektTest")
    tasks.withType(Detekt::class.java).configureEach { detekt ->
        detekt.jvmTarget = jvmTarget
        detekt.exclude { tree -> tree.file.path.contains("/build/") }
    }
    pluginManager.apply("com.autonomousapps.dependency-analysis")
    pluginManager.apply("com.squareup.sort-dependencies")
    dependOnBuildLogicTask("sortDependencies")
    dependOnBuildLogicTask("checkSortDependencies")
    val libs = versionCatalog()
    configureSpotless(libs.findVersion("ktfmt").get().requiredVersion)
}

internal fun Project.configureLint() {
    pluginManager.apply("com.android.lint")
    extensions.getByType(Lint::class.java).apply {
        htmlReport = false
        xmlReport = false
        textReport = true
        absolutePaths = false
        checkTestSources = true
        warningsAsErrors = true
        baseline = file("lint-baseline.xml")
        disable.add("GradleDependency")
    }
    dependOnBuildLogicTask("lint")
}

internal fun Project.configureSpotless(ktfmtVersion: String) {
    pluginManager.apply("com.diffplug.spotless")
    dependOnBuildLogicTask("spotlessApply")
    dependOnBuildLogicTask("spotlessCheck")
    extensions.getByType(SpotlessExtension::class.java).apply {
        format("misc") { formatExtension ->
            with(formatExtension) {
                target("*.md", ".gitignore")
                trimTrailingWhitespace()
                endWithNewline()
            }
        }

        kotlin { kotlinExtension ->
            with(kotlinExtension) {
                ktfmt(ktfmtVersion).kotlinlangStyle()
                trimTrailingWhitespace()
                endWithNewline()
                targetExclude("**/build/**/*")
                licenseHeaderFile(file("$rootDir/spotless/spotless.kt"))
            }
        }
        kotlinGradle { kotlinGradleExtension ->
            with(kotlinGradleExtension) {
                ktfmt(ktfmtVersion).kotlinlangStyle()
                trimTrailingWhitespace()
                endWithNewline()
                licenseHeaderFile(
                    file("$rootDir/spotless/spotless.kt"),
                    "(import|plugins|buildscript|dependencies|pluginManagement)",
                )
            }
        }
    }
}
