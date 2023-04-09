package com.babblechallenge.falling_words.domain

import com.babblechallenge.falling_words.data.FallingWordsApi
import com.babblechallenge.falling_words.data.Word
import io.reactivex.rxjava3.core.Single
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class WordsInteractorTest {

    private val apiMock: FallingWordsApi = mock()
    private val interactor = WordsInteractorImpl(apiMock)

    @Test
    fun output_size_equals_input_size() {
        whenever(apiMock.getDictionary()).thenReturn(Single.just(stubData))
        val actual = interactor.getRoundData().blockingGet()
        verify(apiMock).getDictionary()
        assert(actual.size == stubData.size)
    }

    @Test
    fun output_size_min_of_response_and_default() {
        whenever(apiMock.getDictionary()).thenReturn(Single.just(stubData))
        val actual = interactor.getRoundData(count = 2).blockingGet()
        verify(apiMock).getDictionary()
        assert(actual.size == 2)
    }

    @Test
    fun verify_that_all_words_at_the_right_fields() {
        whenever(apiMock.getDictionary()).thenReturn(Single.just(stubData))
        val actual = interactor.getRoundData(count = 2).blockingGet()
        verify(apiMock).getDictionary()
        assert(actual.all { originalSet.contains(it.original) })
        assert(actual.all { translatedSet.contains(it.translation) })
    }

    companion object {
        private val stubData = listOf<Word>(
            Word(original = "1", translation = "2"),
            Word(original = "3", translation = "4"),
            Word(original = "5", translation = "6"),
            Word(original = "7", translation = "8"),
            Word(original = "9", translation = "10")
        )

        private val originalSet = setOf<String>("1", "3", "5", "7", "9")
        private val translatedSet = setOf<String>("2", "4", "6", "8", "10")
    }
}