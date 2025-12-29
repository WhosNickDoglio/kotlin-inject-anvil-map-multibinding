// Copyright (C) 2025 Nicholas Doglio
// SPDX-License-Identifier: MIT
plugins {
    alias(libs.plugins.convention.kmp)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
}

kotlin { jvm() }

dependencies { commonMainImplementation(libs.kotlin.inject.anvil.runtime) }
