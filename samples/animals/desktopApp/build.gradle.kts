plugins {
    alias(libs.plugins.convention.jvm)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.ksp)
}

ksp {
    arg("circuit.codegen.mode", "kotlin_inject_anvil")
    arg(
        "kotlin-inject-anvil-contributing-annotations",
        "com.slack.circuit.codegen.annotations.CircuitInject",
    )
}

compose.desktop { application.mainClass = "dev.whosnickdoglio.animals.Main.kt" }

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(libs.bundles.inject)
    implementation(libs.circuit.codegen.annotations)
    implementation(libs.circuit.foundation)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.backhandler)
    implementation(projects.runtime)
    implementation(projects.samples.animals.shared)

    ksp(libs.bundles.inject.ksp)
    ksp(libs.circuit.codegen)
    ksp(projects.compiler)
}
