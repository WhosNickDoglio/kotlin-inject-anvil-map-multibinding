// Copyright (C) 2025 Nicholas Doglio
// SPDX-License-Identifier: MIT
package dev.whosnickdoglio.inject.multibinding.counter

import dev.whosnickdoglio.inject.multibinding.ContributesIntoMap
import dev.whosnickdoglio.inject.multibinding.StringKey
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@SingleIn(AppScope::class)
@MergeComponent(AppScope::class)
public interface AppComponent {
    public val greetersMap: Map<String, Greeter>
}

public fun main() {
    val appComponent = AppComponent::class.create()

    appComponent.greetersMap.forEach { (key, greeter) -> println(" $key: ${greeter.greet()}") }
}

public interface Greeter {
    public fun greet(): String
}

@StringKey("greeter1")
@ContributesIntoMap(AppScope::class)
@Inject
public class GreeterImpl : Greeter {
    override fun greet(): String = "Hello, World!"
}

@StringKey("greeter2")
@ContributesIntoMap(AppScope::class)
@Inject
public class GreeterImpl2 : Greeter {
    override fun greet(): String = "Hello, Friends!"
}

@StringKey("greeter3")
@ContributesIntoMap(AppScope::class)
@Inject
public class GreeterImpl3 : Greeter {
    override fun greet(): String = "Hello, World!"
}
