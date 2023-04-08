package com.babblechallenge.falling_words.di

import androidx.lifecycle.ViewModelStoreOwner
import com.babblechallenge.falling_words.presentation.FallingWorldsActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [NetworkModule::class, FallingWordsModule::class],
    dependencies = [FallingWordsDependency::class]
)
interface FallingWordsComponent {

    fun inject(activity: FallingWorldsActivity)

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance viewModelStoreOwner: ViewModelStoreOwner,
            dependency: FallingWordsDependency
        ): FallingWordsComponent
    }
}