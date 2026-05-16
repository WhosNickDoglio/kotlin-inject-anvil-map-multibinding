// Copyright (C) 2026 Nicholas Doglio
// SPDX-License-Identifier: MIT

package dev.whosnickdoglio.animals.core.data

import me.tatarka.inject.annotations.Inject

@Inject
public class CatApiService : AnimalService {
    override suspend fun requestAnimals(): List<Animal> {
        TODO("Not yet implemented")
    }
}
