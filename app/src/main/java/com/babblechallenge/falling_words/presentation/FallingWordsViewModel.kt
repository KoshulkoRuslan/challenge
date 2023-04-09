package com.babblechallenge.falling_words.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.babblechallenge.falling_words.domain.RoundWord
import com.babblechallenge.falling_words.domain.WordsInteractor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject


class FallingWordsViewModel(
    private val interactor: WordsInteractor
) : ViewModel() {

    val state: LiveData<ScreenState>
        get() = _state
    private val _state: MutableLiveData<ScreenState> = MutableLiveData()
    val eventsBus: Subject<Boolean>
        get() = _events
    private val _events = PublishSubject.create<Boolean>()
    private var loadingDisposable: Disposable? = null
    private var data: List<RoundWord>? = null
    private var currentStep = 0
    private var score = 0
    private var progress: Float = 0.0f

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
                progress = action.progress
            }
            is OnScreenResumed -> {
                startTimerForCurrentStep(progress)
            }
            is TimerFinished -> {
                onTimeExpired()
            }
            is NewRoundButtonClicked -> {
                data = null
                score = 0
                currentStep = 0
                progress = 0.0f
                loadData()
            }
        }
    }

    private fun handlePositiveButtonAction() {
        val data = data ?: return
        val isRightAnswer = data[currentStep].isCorrect
        _events.onNext(isRightAnswer)
        if (isRightAnswer) increaseScore()
        tryToStartNextStep()
    }

    private fun handleNegativeButton() {
        val data = data ?: return
        val isRightAnswer = !data[currentStep].isCorrect
        _events.onNext(isRightAnswer)
        if (isRightAnswer) increaseScore()
        tryToStartNextStep()
    }

    private fun onTimeExpired() {
        _events.onNext(false)
        tryToStartNextStep()
    }

    private fun tryToStartNextStep() {
        if (hasNextStep()) {
            moveToNextStep()
            startTimerForCurrentStep()
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

    private fun onError(throwable: Throwable) = _state.postValue(Error)

    private fun onDataLoaded(data: List<RoundWord>) {
        this.data = data
        startTimerForCurrentStep()
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
                startTimerForCurrentStep(state.currentProgress)
            }
        }
    }

    private fun startTimerForCurrentStep(progress: Float? = null) {
        val data = this.data ?: return
        val stepWord = data[currentStep]
        _state.value = TimerInProgress(
            originalWord = stepWord.original,
            translation = stepWord.translation,
            progress = progress ?: 0.0f,
            score = "Score: $score"
        )
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
        val isFinished = state is GameFinished
        return GameState(
            score,
            isFinished = isFinished,
            data,
            currentStep
        )
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