package com.babblechallenge.falling_words.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoundWord(
    val original: String,
    val translation: String,
    val isCorrect: Boolean
): Parcelable