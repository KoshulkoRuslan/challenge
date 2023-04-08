package com.babblechallenge.falling_words.di

import com.babblechallenge.falling_words.data.FallingWordsApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideFallingWordsApi(retrofit: Retrofit): FallingWordsApi =
        retrofit.create(FallingWordsApi::class.java)
}