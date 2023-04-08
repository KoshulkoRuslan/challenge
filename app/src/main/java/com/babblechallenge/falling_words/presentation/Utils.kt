package com.babblechallenge.falling_words.presentation

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable

inline fun <reified T : Parcelable> Bundle?.parcelable(key: String): T? = when {
    this == null -> null
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable<T>(key)
}