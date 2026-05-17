import java.util.Locale
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

// Copyright (C) 2026 Nicholas Doglio
// SPDX-License-Identifier: MIT

plugins {
    alias(libs.plugins.convention.kmp)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

ksp {
    arg("circuit.codegen.mode", "kotlin_inject_anvil")
    arg(
        "kotlin-inject-anvil-contributing-annotations",
        "com.slack.circuit.codegen.annotations.CircuitInject",
    )
}

kotlin {
    targets.configureEach {
        val isAndroidJvm = platformType == KotlinPlatformType.androidJvm
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    with(freeCompilerArgs) {
                        if (isAndroidJvm) {
                            addAll(
                                "-P",
                                "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=dev.whosnickdoglio.animals.core.CommonParcelize",
                            )
                        }
                        // https://youtrack.jetbrains.com/issue/KT-61573
                        add("-Xexpect-actual-classes")
                    }
                }
            }
        }
    }

    android {
        namespace = "dev.whosnickdoglio.animals.core"
        compileSdk { version = release(37) }
    }

    iosArm64()
    iosSimulatorArm64()

    js { browser() }

    jvm()

    // https://youtrack.jetbrains.com/issue/KT-84582/coreLibrariesVersion-isnt-really-compatible-with-JS-or-Wasm
    //    wasmJs { browser() }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.bundles.inject)
            implementation(libs.circuit.foundation)
            implementation(libs.circuit.codegen.annotations)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.ktor)
            implementation(libs.ktor.cio)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.serialization)
            implementation(libs.kotlin.serialization.json)
            implementation(projects.runtime)
        }
    }
}

dependencies {
    kotlin.targets.forEach { target ->
        if (target.name != "metadata") {
            dependencies.addKspDependencies(target.name)
        }
    }
}

fun DependencyHandler.addKspDependencies(configuration: String) {
    fun capitalizeConfiguration(): String = configuration.replaceFirstChar { char ->
        if (char.isLowerCase()) {
            char.titlecase(Locale.getDefault())
        } else {
            char.toString()
        }
    }

    add("ksp${capitalizeConfiguration()}", libs.bundles.inject.ksp)
    add("ksp${capitalizeConfiguration()}", libs.circuit.codegen)
    add("ksp${capitalizeConfiguration()}", projects.compiler)
}
