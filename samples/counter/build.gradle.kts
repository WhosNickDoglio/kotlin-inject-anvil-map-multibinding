// Copyright (C) 2025 Nicholas Doglio
// SPDX-License-Identifier: MIT
plugins {
    alias(libs.plugins.convention.jvm)
    alias(libs.plugins.ksp)
    // TODO multiplatform
}

dependencies {
    implementation(libs.kotlin.inject.anvil.runtime)
    implementation(libs.kotlin.inject.anvil.runtime.optional)
    implementation(libs.kotlin.inject.runtime)
    implementation(projects.runtime)

    ksp(libs.kotlin.inject.anvil.compiler)
    ksp(libs.kotlin.inject.compiler)
    ksp(projects.compiler)
}
