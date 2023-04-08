package com.babblechallenge.falling_words.data

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET


interface FallingWordsApi {

    @GET("/DroidCoder/7ac6cdb4bf5e032f4c737aaafe659b33/raw/baa9fe0d586082d85db71f346e2b039c580c5804/words.json")
    fun getDictionary(): Single<List<Word>>
}