/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
package dev.whosnickdoglio.inject.multibinding

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import java.lang.String
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.valueParameters
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.Provides
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test
import software.amazon.lastmile.kotlin.inject.anvil.compile
import software.amazon.lastmile.kotlin.inject.anvil.isOk

@OptIn(ExperimentalCompilerApi::class)
class ContributesMapMultibindingProcessorTest {

    @Test
    fun `StringKey with implicit boundType should generate the correct code`() {
        compile(
            """
            package $TEST_LOOKUP_PACKAGE

            import dev.whosnickdoglio.inject.multibinding.ContributesMapMultibinding
            import dev.whosnickdoglio.inject.multibinding.StringKey
            import me.tatarka.inject.annotations.Inject
            import software.amazon.lastmile.kotlin.inject.anvil.AppScope

            fun interface Greeter {
                fun greet(): String
            }

            @StringKey("greeter3")
            @ContributesMapMultibinding(AppScope::class)
            @Inject
            class GreeterImpl3 : Greeter {
                override fun greet(): String = "Hello, World!"
            }

            """
                .trimIndent()
        ) {
            val greeter3 = clazz("GreeterImpl3")
            val greeterSuper = clazz("Greeter")
            val component = greeter3.generatedComponent

            assertThat(component.packageName).isEqualTo(TEST_LOOKUP_PACKAGE)

            val function = component.kotlin.declaredMemberFunctions.single()
            assertThat(function.name).isEqualTo(greeter3.multibindingMethodName())
            // TODO do I need to use TypeName for all of this?
            assertThat(function.valueParameters.single().type.asTypeName())
                .isEqualTo(greeter3.asTypeName())
            assertThat(function.returnType.asTypeName())
                .isEqualTo(
                    Pair::class.asClassName()
                        .parameterizedBy(
                            String::class.asTypeName(),
                            greeterSuper.kotlin.asTypeName(),
                        )
                )
            assertThat(function).isAnnotatedWith(Provides::class)
            assertThat(function).isAnnotatedWith(IntoMap::class)

            // todo method body
            assertThat(exitCode).isOk()
        }
    }
}
