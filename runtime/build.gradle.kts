/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
plugins {
    alias(libs.plugins.convention.jvm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
    // TODO multiplatform
}

dependencies { implementation(libs.kotlin.inject.anvil.runtime) }
