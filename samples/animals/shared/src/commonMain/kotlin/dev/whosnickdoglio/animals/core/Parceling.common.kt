// Copyright (C) 2026 Nicholas Doglio
// SPDX-License-Identifier: MIT

package dev.whosnickdoglio.animals.core

// For Android @Parcelize
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
public annotation class CommonParcelize

// For Android Parcelable
public expect interface CommonParcelable
