package com.babblechallenge.falling_words.data

import com.google.gson.annotations.SerializedName

data class Word(
    @SerializedName("text_eng") val original: String,
    @SerializedName("text_spa") val translation: String
)