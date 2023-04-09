package com.babblechallenge.falling_words.presentation

sealed interface ScreenState
data class TimerInProgress(
    val originalWord: String,
    val translation: String,
    val progress: Float = 0.0f,
    val score: String = "Score: 0"
): ScreenState

object Loading : ScreenState
object Error : ScreenState
data class GameFinished(val score: String) : ScreenState
