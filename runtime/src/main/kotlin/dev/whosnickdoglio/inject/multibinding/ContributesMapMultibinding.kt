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
public annotation class ContributesMapMultibinding(
    /** The scope in which to include this contributed binding. */
    val scope: KClass<*>,
    /**
     * The type that this class is bound to. When injecting [boundType] the concrete class will be
     * this annotated class.
     */
    val boundType: KClass<*> = Unit::class,
    /**
     * This contributed binding will replace these contributed classes. The array is allowed to
     * include other contributed bindings and contributed component interfaces. All replaced classes
     * must use the same scope.
     */
    val replaces: Array<KClass<*>> = [],
    // ignoreQualifier?
    // https://github.com/square/anvil/commit/2e22b27cbbab25a6d96b7540037d741ccf5316de
)
