// Copyright (C) 2025 Nicholas Doglio
// SPDX-License-Identifier: MIT

package dev.whosnickdoglio.convention.configuration

import kotlin.jvm.optionals.getOrNull
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

internal fun Project.versionCatalog(catalogName: String = "libs"): VersionCatalog {
    val catalog = extensions.getByType(VersionCatalogsExtension::class.java)

    return catalog.named(catalogName)
}

internal fun VersionCatalog.getVersionOrError(key: String): String {
    val version =
        findVersion(key).getOrNull()
            ?: throw GradleException(
                "Please set a version in your version catalog with the key $key"
            )

    return version.requiredVersion
}

internal fun Project.dependOnBuildLogicTask(taskName: String) {
    tasks.named(taskName).configure {
        it.dependsOn(gradle.includedBuild("build-logic").task(":$taskName"))
    }
}
