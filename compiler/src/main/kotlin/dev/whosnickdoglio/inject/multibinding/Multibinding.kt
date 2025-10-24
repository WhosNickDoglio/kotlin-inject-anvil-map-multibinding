// Copyright (C) 2025 Nicholas Doglio
// SPDX-License-Identifier: MIT
package dev.whosnickdoglio.inject.multibinding

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

internal data class Multibinding(
    val originFile: KSFile,
    val originClass: ClassName,
    val scope: ClassName,
    val className: ClassName,
    val boundType: TypeName,
    // This need to be a list?
    val qualifiers: List<AnnotationSpec>,

    // TODO
    // https://github.com/evant/kotlin-inject/issues/409
    val keyValue: String, // TODO type
    val keyType: TypeName,
)

/**
 * TODO.
 *
 * @param codeGenerator
 */
internal fun Multibinding.generate(codeGenerator: CodeGenerator) {
    FileSpec.builder(className)
        .addType(
            TypeSpec.interfaceBuilder(className)
                .addOriginatingKSFile(originFile)
                .addAnnotation(
                    AnnotationSpec.builder(ContributesTo::class)
                        .addMember("scope = %T::class", scope)
                        .build()
                )
                .addFunction(
                    FunSpec.builder("provide${className.simpleName}")
                        .apply { qualifiers.forEach { addAnnotation(it) } }
                        .addAnnotation(IntoMap::class)
                        .addParameter("impl", originClass)
                        .addAnnotation(Provides::class)
                        // TODO need to be able to handle all kinds of types here
                        .returns(Pair::class.asTypeName().parameterizedBy(keyType, boundType))
                        .addStatement("return (%S to impl)", keyValue)
                        .build()
                )
                .build()
        )
        .build()
        .writeTo(codeGenerator = codeGenerator, aggregating = false)
}
