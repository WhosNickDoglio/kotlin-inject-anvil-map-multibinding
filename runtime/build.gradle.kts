// Copyright (C) 2025 Nicholas Doglio
// SPDX-License-Identifier: MIT
plugins {
    alias(libs.plugins.convention.kmp)
    alias(libs.plugins.android.library)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
}

kotlin {
    android {
        namespace = "dev.whosnickdoglio.inject.multibinding"
        compileSdk { version = release(37) }
    }

    iosArm64()
    iosSimulatorArm64()
    iosX64()

    js { browser() }

    jvm()

    linuxArm64()
    linuxX64()

    macosArm64()

    mingwX64()

    tvosArm64()
    tvosSimulatorArm64()

    // https://youtrack.jetbrains.com/issue/KT-84582/coreLibrariesVersion-isnt-really-compatible-with-JS-or-Wasm
    //    wasmJs { browser() }

    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()

    applyDefaultHierarchyTemplate()
}

dependencies { commonMainImplementation(libs.kotlin.inject.anvil.runtime) }
