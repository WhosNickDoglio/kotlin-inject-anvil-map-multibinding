import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.spotless)
    alias(libs.plugins.lint)
    alias(libs.plugins.sortDependencies)
    `java-gradle-plugin`
}

kotlin {
    explicitApi()
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.jdk.get())
        vendor = JvmVendorSpec.AZUL
    }

    compilerOptions {
        freeCompilerArgs.add("-Xjdk-release=${libs.versions.jdk.get()}")
        allWarningsAsErrors = true
        jvmTarget = JvmTarget.fromTarget(libs.versions.jdk.get())
    }
}

lint {
    htmlReport = false
    xmlReport = false
    textReport = true
    absolutePaths = false
    checkTestSources = true
    warningsAsErrors = true
    baseline = file("lint-baseline.xml")
    disable.add("GradleDependency")
}

gradlePlugin {
    plugins {
        register("convention.kotlin.jvm") {
            id = "dev.whosnickdoglio.convention.kotlin.jvm"
            implementationClass = "dev.whosnickdoglio.convention.KotlinJvmPlugin"
        }

        register("convention.kotlin.multiplatform") {
            id = "dev.whosnickdoglio.convention.kotlin.multiplatform"
            implementationClass = "dev.whosnickdoglio.convention.KotlinMultiplatformPlugin"
        }
    }
}

spotless {
    format("misc") {
        target("*.md", ".gitignore")
        trimTrailingWhitespace()
        endWithNewline()
    }

    kotlin {
        ktfmt(libs.versions.ktfmt.get()).kotlinlangStyle()
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        ktfmt(libs.versions.ktfmt.get()).kotlinlangStyle()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks.withType<JavaCompile>().configureEach { options.release = libs.versions.jdk.get().toInt() }

tasks.withType<Detekt>().configureEach { jvmTarget = libs.versions.jdkTarget.get() }

// https://docs.gradle.org/8.9/userguide/gradle_daemon.html#daemon_jvm_criteria
tasks.updateDaemonJvm.configure {
    languageVersion = JavaLanguageVersion.of(libs.versions.jdk.get())
    vendor = JvmVendorSpec.AZUL
}

dependencies {
    implementation(libs.android.gradle)
    implementation(libs.dependencyAnalysis.gradle)
    implementation(libs.detekt.gradle)
    implementation(libs.kotlin.gradle)
    implementation(libs.sortDependencies.gradle)
    implementation(libs.spotless.gradle)
}
