package com.babblechallenge.falling_words.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.babblechallenge.falling_words.domain.RoundWord
import com.babblechallenge.falling_words.domain.WordsInteractor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val DECISION_TIME = 15_000L

class FallingWordsViewModel(
    private val interactor: WordsInteractor
) : ViewModel() {

    val state: LiveData<ScreenState>
        get() = _state
    private val _state: MutableLiveData<ScreenState> = MutableLiveData(Loading)
    val eventsBus: Subject<Boolean>
        get() = _events
    private val _events = PublishSubject.create<Boolean>()
    private var timerDisposable: Disposable? = null
    private var loadingDisposable: Disposable? = null
    private var data: List<RoundWord>? = null
    private var currentStep = 0
    private var score = 0

    fun handleAction(action: Action) {
        when (action) {
            is ScreenCreated -> {
                handleSavedState(action.state)
            }
            is RetryButtonClicked -> {
                if (data == null) loadData()
            }
            is PositiveButtonClicked -> {
                handlePositiveButtonAction()
            }
            is NegativeButtonClicked -> {
                handleNegativeButton()
            }
            is OnScreenPaused -> {
                timerDisposable?.dispose()
            }
            is OnScreenResumed -> {
                continueGame()
            }
            is NewRoundButtonClicked -> {
                timerDisposable?.dispose()
                loadingDisposable?.dispose()
                score = 0
                currentStep = 0
                data = null
                loadData()
            }
        }
    }

    private fun continueGame() {
        val data = this.data ?: return
        if (timerDisposable?.isDisposed == false) return
        val isGameFinished = _state.value is GameFinished
        if (isGameFinished) return
        val lastProgress = (_state.value as? InProgress)?.progress
        startWithTimer(currentStep, data, score, lastProgress)
    }

    private fun handlePositiveButtonAction() {
        timerDisposable?.dispose()
        val data = data ?: return
        val isRightAnswer = data[currentStep].isCorrect
        _events.onNext(isRightAnswer)
        if (isRightAnswer) increaseScore()
        tryToStartNextStep()
    }

    private fun handleNegativeButton() {
        timerDisposable?.dispose()
        val data = data ?: return
        val isRightAnswer = !data[currentStep].isCorrect
        _events.onNext(isRightAnswer)
        if (isRightAnswer) increaseScore()
        tryToStartNextStep()
    }

    private fun onTimeExpired() {
        timerDisposable?.dispose()
        _events.onNext(false)
        tryToStartNextStep()
    }

    private fun tryToStartNextStep() {
        val data = this.data ?: return
        if (hasNextStep()) {
            moveToNextStep()
            startWithTimer(currentStep, data, score)
        } else {
            val finishedState = GameFinished("Score: $score")
            _state.postValue(finishedState)
        }
    }

    private fun hasNextStep(): Boolean {
        val dataSize = data?.size ?: return false
        return (currentStep < dataSize - 1)
    }

    private fun loadData() {
        loadingDisposable = interactor.getRoundData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _state.postValue(Loading) }
            .subscribe(::onDataLoaded, ::onError)
    }

    private fun handleSavedState(state: GameState?) {
        when {
            state == null -> loadData()
            state.data == null -> loadData()
            state.isFinished -> _state.value = GameFinished("Score: ${state.score}")
            else -> {
                this.data = state.data
                this.score = state.score
                this.currentStep = state.currentStep
                startWithTimer(state.currentStep, state.data, state.score, state.currentProgress)
            }
        }
    }

    private fun onDataLoaded(data: List<RoundWord>) {
        this.data = data
        startWithTimer(currentStep, data, score)
    }

    private fun onError(throwable: Throwable) = _state.postValue(Error)

    private fun startWithTimer(
        roundCount: Int,
        data: List<RoundWord>,
        score: Int,
        progress: Float? = null
    ) {
        val wordForStep = data[roundCount]
        val initialProgressState = InProgress(
            originalWord = wordForStep.original,
            translation = wordForStep.translation,
            isCorrect = wordForStep.isCorrect,
            progress = progress ?: 62L.toProgress(),
            score = "Score: $score"
        )
        _state.value = initialProgressState
        timerDisposable = Observable.interval(16, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val lastState = (_state.value as? InProgress) ?: return@subscribe
                val currentTime = lastState.progress.toTime()
                if (currentTime >= DECISION_TIME) {
                    onTimeExpired()
                } else {
                    val updatedTime = currentTime + 62
                    val updatedProgress = updatedTime.toProgress()
                    val updatedState = InProgress(
                        originalWord = wordForStep.original,
                        translation = wordForStep.translation,
                        isCorrect = wordForStep.isCorrect,
                        progress = updatedProgress,
                        score = "Score: $score"
                    )
                    _state.postValue(updatedState)
                }
            }
    }

    private fun increaseScore() {
        score += 1
    }

    private fun moveToNextStep() {
        currentStep += 1
    }

    fun getGameState(): GameState {
        val state = _state.value
        val data = this.data
        val progress = (state as? InProgress)?.progress
        val isFinished = state is GameFinished
        return GameState(
            score,
            isFinished = isFinished,
            data,
            currentStep,
            progress
        )
    }


    private fun Float.toTime(): Long {
        if (this >= 1.0f) return DECISION_TIME
        if (this <= 0.0f) return 0L
        return (this * DECISION_TIME).toLong()
    }

    private fun Long.toProgress(): Float {
        return (this.toFloat() / DECISION_TIME.toFloat())
    }

}

class FallingWordsViewModelFactory @Inject constructor(
    private val interactor: WordsInteractor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FallingWordsViewModel(interactor) as T
    }
}