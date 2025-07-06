/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.inject.multibinding

import kotlin.reflect.KClass
import software.amazon.lastmile.kotlin.inject.anvil.extend.ContributingAnnotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ContributingAnnotation
public annotation class ContributesIntoMap(
    /** The scope in which to include this contributed binding. */
    val scope: KClass<*>,
    /**
     * The type that this class is bound to. When injecting [boundType] the concrete class will be
     * this annotated class.
     */
    val boundType: KClass<*> = Unit::class,
    // TODO not sure if I can support this
    // https://github.com/amzn/kotlin-inject-anvil/blob/404411644b27c9f8ecf63c3bba30fc1233b5b489/compiler/src/main/kotlin/software/amazon/lastmile/kotlin/inject/anvil/processor/MergeComponentProcessor.kt#L192
//    /**
//     * This contributed binding will replace these contributed classes. The array is allowed to
//     * include other contributed bindings and contributed component interfaces. All replaced classes
//     * must use the same scope.
//     */
//    val replaces: Array<KClass<*>> = [],
    /**
     * Whether the qualifier for this class should be included in the generated multibinding method.
     * This parameter is only necessary to use when
     * [ContributesBinding][software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding] and
     * [ContributesIntoMap] are used together for the same class. If not, simply remove the
     * qualifier from the class and don't use this parameter.
     */
    val ignoreQualifier: Boolean = false,
)
