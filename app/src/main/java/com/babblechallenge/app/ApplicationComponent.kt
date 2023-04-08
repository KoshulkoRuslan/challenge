package com.babblechallenge.app

import com.babblechallenge.falling_words.di.FallingWordsDependency
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [BaseNetworkModule::class]
)
interface ApplicationComponent: FallingWordsDependency {

    @Component.Factory
    interface Factory {
        fun create(): ApplicationComponent
    }
}