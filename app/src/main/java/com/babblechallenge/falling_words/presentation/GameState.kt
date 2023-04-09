package com.babblechallenge.falling_words.presentation

import android.os.Parcelable
import com.babblechallenge.falling_words.domain.RoundWord
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameState(
    val score: Int,
    val isFinished: Boolean,
    val data: List<RoundWord>?,
    val currentStep: Int,
    val currentProgress: Float? = 0.0f
): Parcelable