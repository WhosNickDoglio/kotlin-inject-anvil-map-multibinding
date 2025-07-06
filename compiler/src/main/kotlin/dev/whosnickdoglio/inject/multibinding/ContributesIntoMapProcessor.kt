/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.inject.multibinding

import com.google.auto.service.AutoService
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import kotlin.sequences.onEach
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContextAware
import software.amazon.lastmile.kotlin.inject.anvil.argumentAt
import software.amazon.lastmile.kotlin.inject.anvil.requireQualifiedName

/**
 * TODO write lots of docs on this.
 *
 * Given the following
 *
 * ```
 * @StringKey("greeter1")
 * @ContributesIntoMap(AppScope::class)
 * @Inject
 * public class GreeterImpl : Greeter {
 *     override fun greet(): String = "Hello, World!"
 * }
 * ```
 *
 * Will generate a file that looks like this
 *
 * ```
 * @ContributesTo(scope = AppScope::class)
 * public interface DevWhosnickdoglioInjectMultibindingCounterGreeterImpl {
 *     @IntoMap
 *     @Provides
 *     public fun provideDevWhosnickdoglioInjectMultibindingCounterGreeterImpl(
 *         `impl`: GreeterImpl
 *     ): Pair<String, Greeter> = ("greeter1" to `impl`)
 * }
 * ```
 *
 * @property codeGenerator
 * @property logger
 */
internal class ContributesIntoMapProcessor(
    private val codeGenerator: CodeGenerator,
    override val logger: KSPLogger,
) : SymbolProcessor, ContextAware {

    @AutoService(SymbolProcessorProvider::class)
    internal class Provider : SymbolProcessorProvider {
        override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
            ContributesIntoMapProcessor(environment.codeGenerator, environment.logger)
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver
            .getSymbolsWithAnnotation(ContributesIntoMap::class)
            .filterIsInstance<KSClassDeclaration>()
            .onEach { clazz ->
                checkIsPublic(
                    declaration = clazz,
                    lazyMessage = {
                        "${clazz.requireQualifiedName()} must be public to " +
                            "be annotated with ContributesIntoMap."
                    },
                )
                checkIsConcreteClass(clazz)
                checkHasInjectAnnotation(clazz)
                checkHasMapKeyAnnotation(clazz)
            }
            .map { clazz -> clazz.toMultibinding() }
            .forEach { binding -> binding.generate(codeGenerator) }

        return emptyList()
    }

    private fun KSClassDeclaration.toMultibinding(): Multibinding =
        Multibinding(
            originFile = requireContainingFile(),
            originClass = toClassName(),
            scope = scope().type.toClassName(),
            className =
                ClassName(
                    packageName = packageName.asString(),
                    simpleNames = listOf(safeClassName),
                ),
            boundType = getBoundType(),
            qualifiers = qualifierSpecs(),
            keyValue = mapKey().mapKeyValue(),
            keyType = mapKey().mapKeyType(),
        )

    private fun KSClassDeclaration.getBoundType(): TypeName {
        val supers =
            superTypes
                .map { typeRef -> typeRef.toTypeName() }
                .filterNot { typeName -> typeName == UNIT }
                .toList()
        val boundType =
            (contributesMultibindingAnnotation().argumentAt(name = "boundType")?.value as? KSType)
                ?.toTypeName()

        if (boundType != null) {
            check(
                supers.contains(boundType),
                lazyMessage = {
                    "Bound type $boundType not found in super types of ${this.qualifiedName?.asString()}"
                },
            )
        }

        val hasImplicitBoundType = boundType == null || boundType == UNIT
        val hasSingleSuper = supers.size == 1

        return when {
            // has explicitly set boundType so we're using that
            !hasImplicitBoundType -> boundType
            // Single super with no explicit boundType means we just use the single super
            hasImplicitBoundType && hasSingleSuper -> supers.first()
            else -> {
                error(
                    "Multiple super types found for ${this.qualifiedName?.asString()} but no bound type specified"
                )
            }
        }
    }

    private fun KSClassDeclaration.contributesMultibindingAnnotation(): KSAnnotation =
        annotations.first { annotation ->
            annotation.isAnnotation(ContributesIntoMap::class.requireQualifiedName())
        }

    private fun KSClassDeclaration.qualifierSpecs(): List<AnnotationSpec> =
        annotations
            .filter { annotation -> annotation.isKotlinInjectQualifierAnnotation() }
            .map { annotation -> annotation.toAnnotationSpec() }
            .toList()

    private fun checkIsConcreteClass(
        clazz: KSClassDeclaration,
        lazyMessage: () -> String = {
            "${clazz.requireQualifiedName()} must be concrete class to be annotated with ContributesIntoMap."
        },
    ) {
        check(clazz.classKind == ClassKind.CLASS && !clazz.isAbstract(), clazz, lazyMessage)
    }

    private fun checkHasInjectAnnotation(
        clazz: KSClassDeclaration,
        lazyMessage: () -> String = {
            "${clazz.requireQualifiedName()} must be annotated " +
                "with Inject to be annotated with ContributesIntoMap."
        },
    ) {
        fun KSAnnotation.isInjectAnnotation(): Boolean =
            annotationType.resolve().declaration.requireQualifiedName() ==
                Inject::class.requireQualifiedName()

        check(clazz.annotations.any { annotation -> annotation.isInjectAnnotation() }, lazyMessage)
    }

    private fun checkHasMapKeyAnnotation(
        clazz: KSClassDeclaration,
        lazyMessage: () -> String = {
            "${clazz.requireQualifiedName()} must be annotated with a single MapKey " +
                "annotation to be annotated with ContributesIntoMap."
        },
    ) {
        fun KSAnnotation.isMapKeyAnnotation(): Boolean =
            annotationType.resolve().declaration.isAnnotationPresent(MapKey::class)

        checkNotNull(
            clazz.annotations.singleOrNull { annotation -> annotation.isMapKeyAnnotation() },
            lazyMessage,
        )
    }

    private fun KSAnnotation.mapKeyType(): TypeName =
        when {
            isAnnotation(StringKey::class.requireQualifiedName()) -> String::class.asTypeName()
            isAnnotation(IntKey::class.requireQualifiedName()) -> Int::class.asTypeName()
            isAnnotation(LongKey::class.requireQualifiedName()) -> Long::class.asTypeName()
            isAnnotation(ClassKey::class.requireQualifiedName()) ->
                (arguments.first().value as KSType).toTypeName()
            // custom mapKey
            else -> (arguments.first().value as KSType).toTypeName()
        }
}

private fun KSClassDeclaration.mapKey(): KSAnnotation =
    annotations.firstOrNull { annotation ->
        annotation.annotationType.resolve().declaration.isAnnotationPresent(MapKey::class)
    } ?: error("No MapKey annotation found on ${qualifiedName?.asString()}")

private fun KSAnnotation.mapKeyValue(): String {
    arguments.first()

    return arguments.first().value?.toString() ?: error("No value found for MapKey annotation")
}
