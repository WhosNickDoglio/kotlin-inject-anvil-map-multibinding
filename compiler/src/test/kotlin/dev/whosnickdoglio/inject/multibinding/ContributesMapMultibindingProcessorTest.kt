/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */
@file:Suppress("JUnitMalformedDeclaration")

package dev.whosnickdoglio.inject.multibinding

import app.cash.burst.Burst
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.tschuchort.compiletesting.KotlinCompilation
import java.lang.String
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.valueParameters
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.Provides
import org.junit.Test
import software.amazon.lastmile.kotlin.inject.anvil.compile
import software.amazon.lastmile.kotlin.inject.anvil.isOk

class ContributesMapMultibindingProcessorTest {

    // https://github.com/cashapp/burst/issues/76
    enum class MapKeyValue(val key: KClass<*>, val value: Any) {
        STRING(StringKey::class, "\"greeter3\"")
        //        INT(IntKey::class, 1),
        //        LONG(LongKey::class, 1L),
        //        CLASS(ClassKey::class, String::class),
        // TODO custom
    }

    @Burst
    @Test
    fun `given MapKey annotation applied with implicit boundType when compiling then generates expected code`(
        mapKeyAndValue: MapKeyValue
    ) {
        compile(
            """
            package $TEST_LOOKUP_PACKAGE

            import dev.whosnickdoglio.inject.multibinding.ContributesMapMultibinding
            import dev.whosnickdoglio.inject.multibinding.${mapKeyAndValue.key.simpleName}
            import me.tatarka.inject.annotations.Inject
            import software.amazon.lastmile.kotlin.inject.anvil.AppScope

            interface Greeter

            @${mapKeyAndValue.key.simpleName}(${mapKeyAndValue.value})
            @ContributesMapMultibinding(AppScope::class)
            @Inject
            class GreeterImpl3 : Greeter
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

    @Test
    fun `given annotation applied when there is no MapKey then compilation fails`() {
        compile(
            """
            package $TEST_LOOKUP_PACKAGE

            import dev.whosnickdoglio.inject.multibinding.ContributesMapMultibinding
            import me.tatarka.inject.annotations.Inject
            import software.amazon.lastmile.kotlin.inject.anvil.AppScope

            interface Greeter

            @ContributesMapMultibinding(AppScope::class)
            @Inject
            class GreeterImpl3 : Greeter
            """
                .trimIndent(),
            exitCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        ) {
            assertThat(messages)
                .contains(
                    "dev.whosnickdoglio.inject.GreeterImpl3 must be annotated with a " +
                        "MapKey annotation to be annotated with ContributesMapMultibinding."
                )
        }
    }

    @Test
    fun `given annotation applied when there is no Inject annotation then compilation fails`() {
        compile(
            """
            package $TEST_LOOKUP_PACKAGE

            import dev.whosnickdoglio.inject.multibinding.ContributesMapMultibinding
            import dev.whosnickdoglio.inject.multibinding.StringKey
            import software.amazon.lastmile.kotlin.inject.anvil.AppScope

            interface Greeter

            @StringKey("greeter3")
            @ContributesMapMultibinding(AppScope::class)
            class GreeterImpl3 : Greeter
            """
                .trimIndent(),
            exitCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        ) {
            assertThat(messages)
                .contains(
                    "dev.whosnickdoglio.inject.GreeterImpl3 must be" +
                        " annotated with Inject to be annotated with ContributesMapMultibinding."
                )
        }
    }

    @Test
    fun `given annotation applied when class is not public then compilation fails`() {
        compile(
            """
            package $TEST_LOOKUP_PACKAGE

            import dev.whosnickdoglio.inject.multibinding.ContributesMapMultibinding
            import dev.whosnickdoglio.inject.multibinding.StringKey
            import me.tatarka.inject.annotations.Inject
            import software.amazon.lastmile.kotlin.inject.anvil.AppScope

            interface Greeter

            @StringKey("greeter3")
            @ContributesMapMultibinding(AppScope::class)
            @Inject
            private class GreeterImpl3 : Greeter
            """
                .trimIndent(),
            exitCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        ) {
            assertThat(messages)
                .contains(
                    "dev.whosnickdoglio.inject.GreeterImpl3 must be public " +
                        "to be annotated with ContributesMapMultibinding"
                )
        }
    }

    @Test
    fun `given annotation applied when class is abstract class then compilation fails`() {
        compile(
            """
            package $TEST_LOOKUP_PACKAGE

            import dev.whosnickdoglio.inject.multibinding.ContributesMapMultibinding
            import dev.whosnickdoglio.inject.multibinding.StringKey
            import me.tatarka.inject.annotations.Inject
            import software.amazon.lastmile.kotlin.inject.anvil.AppScope

            interface Greeter

            @StringKey("greeter3")
            @ContributesMapMultibinding(AppScope::class)
            @Inject
            abstract class GreeterImpl3 : Greeter
            """
                .trimIndent(),
            exitCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        ) {
            assertThat(messages)
                .contains(
                    "dev.whosnickdoglio.inject.GreeterImpl3 must be " +
                        "concrete class to be annotated with ContributesMapMultibinding."
                )
        }
    }

    @Test
    fun `given annotation applied when class is an interface then compilation fails`() {
        compile(
            """
            package $TEST_LOOKUP_PACKAGE

            import dev.whosnickdoglio.inject.multibinding.ContributesMapMultibinding
            import dev.whosnickdoglio.inject.multibinding.StringKey
            import me.tatarka.inject.annotations.Inject
            import software.amazon.lastmile.kotlin.inject.anvil.AppScope

            interface Greeter

            @StringKey("greeter3")
            @ContributesMapMultibinding(AppScope::class)
            @Inject
            interface GreeterImpl3 : Greeter
            """
                .trimIndent(),
            exitCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        ) {
            assertThat(messages)
                .contains(
                    "dev.whosnickdoglio.inject.GreeterImpl3 must be " +
                        "concrete class to be annotated with ContributesMapMultibinding."
                )
        }
    }

    @Test
    fun `given annotation applied when multiple supers without an explicit boundType then compilation fails`() {
        compile(
            """
            package $TEST_LOOKUP_PACKAGE

            import dev.whosnickdoglio.inject.multibinding.ContributesMapMultibinding
            import dev.whosnickdoglio.inject.multibinding.StringKey
            import me.tatarka.inject.annotations.Inject
            import software.amazon.lastmile.kotlin.inject.anvil.AppScope

            interface Greeter
            interface Super

            @StringKey("greeter3")
            @ContributesMapMultibinding(AppScope::class)
            @Inject
            class GreeterImpl3 : Greeter, Super
            """
                .trimIndent(),
            exitCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        ) {
            assertThat(messages)
                .contains(
                    "Multiple super types found for " +
                        "dev.whosnickdoglio.inject.GreeterImpl3 but no bound type specified"
                )
        }
    }

    @Test
    fun `given annotation applied with wrong explicit bondType then compilation fails`() {
        compile(
            """
            package $TEST_LOOKUP_PACKAGE

            import dev.whosnickdoglio.inject.multibinding.ContributesMapMultibinding
            import dev.whosnickdoglio.inject.multibinding.StringKey
            import me.tatarka.inject.annotations.Inject
            import software.amazon.lastmile.kotlin.inject.anvil.AppScope

            interface Greeter
            interface Super

            @StringKey("greeter3")
            @ContributesMapMultibinding(AppScope::class, boundType = Super::class)
            @Inject
            class GreeterImpl3 : Greeter
            """
                .trimIndent(),
            exitCode = KotlinCompilation.ExitCode.COMPILATION_ERROR,
        ) {
            assertThat(messages)
                .contains(
                    "Bound type dev.whosnickdoglio.inject.Super " +
                        "not found in super types of dev.whosnickdoglio.inject.GreeterImpl3"
                )
        }
    }

    @Test
    fun `given annotation applied when explicit boundType is passed then generate expected code`() {
        compile(
            """
            package $TEST_LOOKUP_PACKAGE

            import dev.whosnickdoglio.inject.multibinding.ContributesMapMultibinding
            import dev.whosnickdoglio.inject.multibinding.StringKey
            import me.tatarka.inject.annotations.Inject
            import software.amazon.lastmile.kotlin.inject.anvil.AppScope

            interface Greeter
            interface Super

            @StringKey("greeter3")
            @ContributesMapMultibinding(AppScope::class, boundType = Greeter::class)
            @Inject
            class GreeterImpl3 : Greeter, Super
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

    @Test
    fun `given annotation applied when single super and explicit boundType then code is generated as expected`() {
        compile(
            """
            package $TEST_LOOKUP_PACKAGE

            import dev.whosnickdoglio.inject.multibinding.ContributesMapMultibinding
            import dev.whosnickdoglio.inject.multibinding.StringKey
            import me.tatarka.inject.annotations.Inject
            import software.amazon.lastmile.kotlin.inject.anvil.AppScope

            interface Greeter

            @StringKey("greeter3")
            @ContributesMapMultibinding(AppScope::class, boundType = Greeter::class)
            @Inject
            class GreeterImpl3 : Greeter
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
