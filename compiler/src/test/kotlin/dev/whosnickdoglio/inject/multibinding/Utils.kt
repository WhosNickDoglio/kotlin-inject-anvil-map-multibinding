/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
@file:OptIn(ExperimentalCompilerApi::class)

/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

package dev.whosnickdoglio.inject.multibinding

import assertk.Assert
import assertk.assertions.contains
import com.tschuchort.compiletesting.JvmCompilationResult
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import software.amazon.lastmile.kotlin.inject.anvil.capitalize

internal const val TEST_LOOKUP_PACKAGE = "dev.whosnickdoglio.inject"

// TODO can I convert all of this to KClass?

val Class<*>.generatedComponent: Class<*>
    get() =
        classLoader.loadClass(
            "$TEST_LOOKUP_PACKAGE." +
                canonicalName.split(".").joinToString(separator = "") { it.capitalize() }
        )

internal fun JvmCompilationResult.clazz(name: String): Class<*> =
    classLoader.loadClass("$TEST_LOOKUP_PACKAGE.$name")

internal fun Class<*>.multibindingMethodName(): String =
    "provide${packageName.split(".").joinToString(separator = "") { it.capitalize() }}$simpleName"

internal fun Assert<KAnnotatedElement>.isAnnotatedWith(annotation: KClass<*>) {
    transform { element -> element.annotations.map { it.annotationClass } }.contains(annotation)
}
