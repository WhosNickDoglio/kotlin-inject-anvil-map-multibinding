plugins {
    alias(libs.plugins.convention.app)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
}

ksp {
    arg("circuit.codegen.mode", "kotlin_inject_anvil")
    arg(
        "kotlin-inject-anvil-contributing-annotations",
        "com.slack.circuit.codegen.annotations.CircuitInject",
    )
}

android {
    namespace = "dev.whosnickdoglio.animals"
    defaultConfig {
        applicationId = "dev.whosnickdoglio.animals"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes { release { optimization { enable = false } } }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core)
    implementation(libs.bundles.inject)
    implementation(libs.circuit.codegen.annotations)
    implementation(libs.circuit.foundation)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(projects.runtime)
    implementation(projects.samples.animals.shared)

    debugImplementation(libs.compose.ui.tooling)

    coreLibraryDesugaring(libs.desugar)

    ksp(libs.bundles.inject.ksp)
    ksp(libs.circuit.codegen)
    ksp(projects.compiler)
}
