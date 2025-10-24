// Copyright (C) 2025 Nicholas Doglio
// SPDX-License-Identifier: MIT

rootProject.name = "build-logic"

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    versionCatalogs { create("libs") { from(files("../gradle/libs.versions.toml")) } }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }
