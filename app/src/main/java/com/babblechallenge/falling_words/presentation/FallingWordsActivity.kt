package com.babblechallenge.falling_words.presentation

import android.animation.ValueAnimator
import android.animation.ValueAnimator.RESTART
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnRepeat
import com.babblechallenge.app.findComponentDependencies
import com.babblechallenge.databinding.ActivityMainBinding
import com.babblechallenge.falling_words.di.DaggerFallingWordsComponent
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

private const val DECISION_TIME = 5_000L

class FallingWordsActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: FallingWordsViewModel
    lateinit var binding: ActivityMainBinding
    private var eventDisposable: Disposable? = null

    private val animator = ValueAnimator.ofFloat(0.0f, 1.0f).apply {
        duration = DECISION_TIME
        repeatMode = RESTART
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener { animator ->
            val value = animator.animatedValue as Float
            binding.motionGroup.progress = value
        }
        doOnRepeat {
            viewModel.handleAction(TimerFinished)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpComponent()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.positiveButton.setOnClickListener {
            viewModel.handleAction(PositiveButtonClicked)
        }
        binding.negativeButton.setOnClickListener {
            viewModel.handleAction(NegativeButtonClicked)
        }
        binding.retryButton.setOnClickListener {
            viewModel.handleAction(RetryButtonClicked)
        }
        binding.newRoundButton.setOnClickListener {
            viewModel.handleAction(NewRoundButtonClicked)
        }
        val state = savedInstanceState?.parcelable<GameState>(GAME_STATE_KEY)
        viewModel.handleAction(ScreenCreated(state))
    }

    private fun setUpComponent() {
        DaggerFallingWordsComponent.factory().create(
            viewModelStoreOwner = this,
            dependency = findComponentDependencies()
        ).inject(this)
    }

    override fun onStart() {
        super.onStart()
        viewModel.state.observe(this) { state ->
            when (state) {
                is Loading -> {
                    showLoading()
                }
                is Error -> {
                    showError()
                }
                is TimerInProgress -> {
                    showControlGroup()
                    binding.originalWord.text = state.originalWord
                    binding.translationWord.text = state.translation
                    binding.score.text = state.score
                    animator.cancel()
                    animator.setCurrentFraction(state.progress)
                    animator.start()
                }
                is GameFinished -> {
                    showScore()
                    animator.cancel()
                    binding.roundScore.text = state.score
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        eventDisposable = viewModel.eventsBus.subscribe(::showReaction)
        viewModel.handleAction(OnScreenResumed)
    }

    override fun onPause() {
        super.onPause()
        eventDisposable?.dispose()
        val progress = animator.animatedValue as Float
        animator.cancel()
        viewModel.handleAction(OnScreenPaused(progress))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val progress = if (animator.isRunning) animator.animatedValue as? Float else null
        val gameState = viewModel.getGameState().copy(currentProgress = progress)
        outState.putParcelable(GAME_STATE_KEY, gameState)
    }

    private fun showReaction(isRightAnswer: Boolean) {
        binding.root.apply {
            val color = if (isRightAnswer) Color.GREEN else Color.RED
            (background as? RippleDrawable)?.setColor(ColorStateList.valueOf(color))
            isPressed = true
            isPressed = false
            performClick()
        }
    }

    private fun showControlGroup() {
        binding.originalWord.visibility = View.VISIBLE
        binding.group.visibility = View.VISIBLE
        binding.progressCircular.visibility = View.GONE
        binding.retryButton.visibility = View.GONE
        binding.roundScore.visibility = View.GONE
        binding.newRoundButton.visibility = View.GONE
    }

    private fun showLoading() {
        binding.originalWord.visibility = View.GONE
        binding.group.visibility = View.GONE
        binding.progressCircular.visibility = View.VISIBLE
        binding.retryButton.visibility = View.GONE
        binding.roundScore.visibility = View.GONE
        binding.newRoundButton.visibility = View.GONE
    }

    private fun showError() {
        binding.originalWord.visibility = View.GONE
        binding.group.visibility = View.GONE
        binding.progressCircular.visibility = View.GONE
        binding.retryButton.visibility = View.VISIBLE
        binding.roundScore.visibility = View.GONE
        binding.newRoundButton.visibility = View.GONE
    }

    private fun showScore() {
        binding.originalWord.visibility = View.GONE
        binding.group.visibility = View.GONE
        binding.progressCircular.visibility = View.GONE
        binding.retryButton.visibility = View.GONE
        binding.roundScore.visibility = View.VISIBLE
        binding.newRoundButton.visibility = View.VISIBLE
    }

    companion object {
        private const val GAME_STATE_KEY = "game_state"
    }
}