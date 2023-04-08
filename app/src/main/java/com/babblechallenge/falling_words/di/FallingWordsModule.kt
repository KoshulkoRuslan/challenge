package com.babblechallenge.falling_words.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.babblechallenge.falling_words.domain.WordsInteractor
import com.babblechallenge.falling_words.domain.WordsInteractorImpl
import com.babblechallenge.falling_words.presentation.FallingWordsViewModel
import com.babblechallenge.falling_words.presentation.FallingWordsViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface FallingWordsModule {

    @Binds
    @Singleton
    fun bindInteractor(impl: WordsInteractorImpl): WordsInteractor

    companion object {

        @Provides
        @Singleton
        fun provideViewModel(
            factory: FallingWordsViewModelFactory,
            viewModelStoreOwner: ViewModelStoreOwner
        ): FallingWordsViewModel {
            return ViewModelProvider(
                viewModelStoreOwner,
                factory
            )[FallingWordsViewModel::class.java]
        }
    }
}