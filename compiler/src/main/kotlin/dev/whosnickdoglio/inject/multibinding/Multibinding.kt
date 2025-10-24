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
import com.squareup.kotlinpoet.asClassName
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
    // https://github.com/evant/kotlin-inject/issues/409
    val mapKey: MapKey,
    val boundType: TypeName,
    // This need to be a list?
    val qualifiers: List<AnnotationSpec>,
) {
    internal data class MapKey(val type: TypeName, val value: Any)
}

// TODO need to be able to handle all kinds of types here
private fun Multibinding.MapKey.returns(binding: Multibinding): TypeName {
    return Pair::class.asTypeName().parameterizedBy(type, binding.boundType)
}

private fun Multibinding.MapKey.statement(): String {
    val poet =
        when (this.type) {
            String::class.asClassName() -> "%S"
            Int::class.asClassName() -> "%L"
            Long::class.asClassName() -> "%LL"
            else -> error("oops!")
        }

    return "return ($poet to `impl`)"
}

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
                        .returns(mapKey.returns(this))
                        .addStatement(mapKey.statement(), mapKey.value)
                        .build()
                )
                .build()
        )
        .build()
        .writeTo(codeGenerator = codeGenerator, aggregating = false)
}
