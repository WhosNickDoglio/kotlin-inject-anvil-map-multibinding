// Copyright (C) 2025 Nicholas Doglio
// SPDX-License-Identifier: MIT
plugins {
    alias(libs.plugins.convention.jvm)
    alias(libs.plugins.ksp)
    alias(libs.plugins.burst)
    alias(libs.plugins.publish)
}

kotlin { compilerOptions { optIn.addAll("com.google.devtools.ksp.KspExperimental") } }

tasks.compileTestKotlin.configure {
    compilerOptions { optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi") }
}

dependencies {
    implementation(libs.kotlin.inject.anvil.compiler.utils)
    implementation(libs.kotlin.inject.anvil.runtime)
    implementation(libs.kotlin.inject.anvil.runtime.optional)
    implementation(libs.kotlin.inject.runtime)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.ksp)
    implementation(projects.runtime)

    compileOnly(libs.auto.service.annotations)

    testImplementation(testFixtures(libs.kotlin.inject.anvil.compiler.utils))
    testImplementation(libs.assertk)
    testImplementation(libs.junit)
    testImplementation(libs.kctfork.ksp)

    ksp(libs.auto.service.ksp)
}
