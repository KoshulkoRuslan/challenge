package com.babblechallenge.falling_words.presentation

sealed interface Action

data class ScreenCreated(val state: GameState? = null) : Action
object OnScreenPaused : Action
object OnScreenResumed : Action
object PositiveButtonClicked : Action
object NegativeButtonClicked : Action
object RetryButtonClicked : Action
object NewRoundButtonClicked : Action