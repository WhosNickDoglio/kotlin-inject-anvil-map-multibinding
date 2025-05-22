/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.inject.multibinding

import com.google.auto.service.AutoService
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import kotlin.reflect.KClass
import software.amazon.lastmile.kotlin.inject.anvil.ContextAware
import software.amazon.lastmile.kotlin.inject.anvil.requireQualifiedName

/**
 * TODO write lots of docs on this.
 *
 * Given the following
 *
 * ```
 * ```
 *
 * Will generate a file that looks like this
 *
 * ```
 * ```
 *
 * @property codeGenerator
 * @property logger
 */
internal class ContributesMapMultibindingProcessor(
    private val codeGenerator: CodeGenerator,
    override val logger: KSPLogger,
) : SymbolProcessor, ContextAware {

    @AutoService(SymbolProcessorProvider::class)
    internal class Provider : SymbolProcessorProvider {
        override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
            ContributesMapMultibindingProcessor(environment.codeGenerator, environment.logger)
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver
            .getSymbolsWithAnnotation(ContributesMapMultibinding::class)
            // TODO error handling
            .filterIsInstance<KSClassDeclaration>()
            .onEach { clazz -> checkIsPublic(clazz) }
            .map { clazz -> clazz.toMultibinding() }
            .forEach { binding -> binding.generate(codeGenerator) }

        return emptyList()
    }

    @OptIn(KspExperimental::class)
    private fun KSClassDeclaration.toMultibinding(): Multibinding =
        Multibinding(
            origin = this,
            className =
                ClassName(
                    packageName = packageName.asString(),
                    simpleNames = listOf(safeClassName),
                ),
            scope = scope().type.toClassName(),
            keyValue = mapKey().mapKeyValue(),
            keyType = mapKey().mapKeyType(),
            value = this.toClassName(),
            boundType = this.superTypes.first().toTypeName(),
            qualifiers =
                annotations
                    .filter { it.isKotlinInjectQualifierAnnotation() }
                    .map { it.toAnnotationSpec() }
                    .toList(),
        )

    @OptIn(KspExperimental::class)
    private fun KSClassDeclaration.mapKey(): KSAnnotation =
        annotations.firstOrNull { annotation ->
            annotation.annotationType.resolve().declaration.isAnnotationPresent(MapKey::class)
        } ?: error("No MapKey annotation found on ${qualifiedName?.asString()}")

    //    private fun KSClassDeclaration.getContributesMapAnnotation(): KSAnnotation =
    // annotations.filter

    private fun KSAnnotation.mapKeyType(): TypeName =
        when {
            isAnnotation(StringKey::class.requireQualifiedName()) -> String::class.asTypeName()
            isAnnotation(IntKey::class.requireQualifiedName()) -> Int::class.asTypeName()
            isAnnotation(LongKey::class.requireQualifiedName()) -> Long::class.asTypeName()
            isAnnotation(ClassKey::class.requireQualifiedName()) ->
                KClass::class.asTypeName() // TOOD
            // custom mapKey
            else -> (arguments.first().value as KSType).toTypeName()
        }

    private fun KSAnnotation.mapKeyValue(): String {
        arguments.first()

        return arguments.first().value?.toString() ?: error("No value found for MapKey annotation")
    }
}
