package com.babblechallenge.falling_words.presentation

import androidx.annotation.FloatRange

sealed interface ScreenState

data class InProgress(
    val originalWord: String,
    val translation: String,
    val isCorrect: Boolean,
    @FloatRange(from = 0.0, to = 1.0) var progress: Float,
    val score: String = "Score: 0"
) : ScreenState

object Loading : ScreenState
object Error : ScreenState
data class GameFinished(val score: String) : ScreenState
