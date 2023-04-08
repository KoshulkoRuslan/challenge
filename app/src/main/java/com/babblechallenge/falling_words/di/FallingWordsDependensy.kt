package com.babblechallenge.falling_words.di

import com.babblechallenge.app.ComponentDependencies
import retrofit2.Retrofit

interface FallingWordsDependency : ComponentDependencies {

    val retrofit: Retrofit
}