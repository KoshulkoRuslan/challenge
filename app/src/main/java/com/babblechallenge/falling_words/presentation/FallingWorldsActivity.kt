package com.babblechallenge.falling_words.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.babblechallenge.app.findComponentDependencies
import com.babblechallenge.falling_words.di.DaggerFallingWordsComponent
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

class FallingWorldsActivity : ComponentActivity() {

    @Inject
    lateinit var viewModel: FallingWordsViewModel
    private var eventDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpComponent()
        setContent {
            FallingWords(viewModel)
        }
        val savedState = savedInstanceState?.parcelable<GameState>(GAME_STATE_KEY)
        viewModel.handleAction(ScreenCreated(savedState))
    }

    @Composable
    fun FallingWords(viewModel: FallingWordsViewModel) {
        MaterialTheme {
            val state by viewModel.state.observeAsState(initial = Loading)
            when (state) {
                is Loading -> LoadingScreen()
                is InProgress -> {
                    val progressState = state as InProgress
                    InProgressScreen(
                        progress = progressState.progress,
                        score = progressState.score,
                        originalWord = progressState.originalWord,
                        translatedWord = progressState.translation,
                        positiveButtonListener = { viewModel.handleAction(PositiveButtonClicked) },
                        negativeButtonListener = { viewModel.handleAction(NegativeButtonClicked) }
                    )
                }
                is GameFinished -> {
                    val finishedState = state as GameFinished
                    FinishScreen(
                        score = finishedState.score,
                        newRoundButtonListener = { viewModel.handleAction(NewRoundButtonClicked) }
                    )
                }
                is Error -> ErrorScreen(
                    retryButtonListener = { viewModel.handleAction(RetryButtonClicked) }
                )
            }
        }
    }

    private fun setUpComponent() {
        DaggerFallingWordsComponent.factory().create(
            viewModelStoreOwner = this,
            dependency = findComponentDependencies()
        ).inject(this)
    }

    override fun onResume() {
        super.onResume()
//        eventDisposable = viewModel.eventsBus.subscribe(::showReaction)
        viewModel.handleAction(OnScreenResumed)
    }

    override fun onPause() {
        super.onPause()
        eventDisposable?.dispose()
        viewModel.handleAction(OnScreenPaused)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(GAME_STATE_KEY, viewModel.getGameState())
    }

    companion object {
        private const val GAME_STATE_KEY = "game_state"
    }
}