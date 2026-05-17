// Copyright (C) 2025 Nicholas Doglio
// SPDX-License-Identifier: MIT

buildscript { dependencies { classpath(libs.burst) } }

plugins {
    alias(libs.plugins.dependencyAnalysis)
    alias(libs.plugins.doctor)
    alias(libs.plugins.android.app) apply false
    alias(libs.plugins.android.cacheFix) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.autoservice) apply false
    alias(libs.plugins.buildConfig) apply false
    alias(libs.plugins.convention.app) apply false
    alias(libs.plugins.convention.jvm) apply false
    alias(libs.plugins.convention.kmp) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.serialization) apply false
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
