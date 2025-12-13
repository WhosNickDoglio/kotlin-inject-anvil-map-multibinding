// Copyright (C) 2025 Nicholas Doglio
// SPDX-License-Identifier: MIT

buildscript { dependencies { classpath(libs.burst) } }

plugins {
    alias(libs.plugins.dependencyAnalysis)
    alias(libs.plugins.doctor)
    alias(libs.plugins.convention.jvm) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.lint) apply false
    alias(libs.plugins.sortDependencies) apply false
    alias(libs.plugins.ktfmt) apply false
    alias(libs.plugins.publish) apply false
}

// https://docs.gradle.org/8.9/userguide/gradle_daemon.html#daemon_jvm_criteria
tasks.updateDaemonJvm.configure {
    languageVersion = JavaLanguageVersion.of(libs.versions.jdk.get())
    vendor = JvmVendorSpec.AZUL
}
