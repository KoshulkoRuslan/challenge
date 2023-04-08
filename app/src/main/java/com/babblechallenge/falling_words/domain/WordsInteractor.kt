package com.babblechallenge.falling_words.domain

import com.babblechallenge.falling_words.data.FallingWordsApi
import com.babblechallenge.falling_words.data.Word
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import kotlin.random.Random

private const val COUNT = 15

interface WordsInteractor {

    /**
     * Return list of words with [count] size for current round.
     */
    fun getRoundData(count: Int = COUNT): Single<List<RoundWord>>
}

class WordsInteractorImpl @Inject constructor(
    private val api: FallingWordsApi
) : WordsInteractor {

    override fun getRoundData(count: Int): Single<List<RoundWord>> = api.getDictionary()
        .map { dictionary -> createRoundData(dictionary, count) }


    /**
     * Return words [COUNT] for round created from [dictionary]
     */
    private fun createRoundData(dictionary: List<Word>, count: Int): List<RoundWord> {
        val resultRoundData = mutableListOf<RoundWord>()

        for (i in 0..count) {
            val addCorrectWord = Random.Default.nextBoolean()
            if (addCorrectWord) {
                val randomWordIndex = Random.Default.nextInt(until = dictionary.size)
                val correctWord = dictionary[randomWordIndex]
                val roundWord = RoundWord(
                    original = correctWord.original,
                    translation = correctWord.translation,
                    isCorrect = true
                )
                resultRoundData.add(roundWord)
            } else {
                val indexes = getTwoDifferentInt(until = dictionary.size)
                val firstWord = dictionary[indexes.first]
                val secondWord = dictionary[indexes.second]
                val roundWord = RoundWord(
                    original = firstWord.original,
                    translation = secondWord.translation,
                    isCorrect = false
                )
                resultRoundData.add(roundWord)
            }
        }
        return resultRoundData
    }

    /**
     * Return two different [Int] in range from 0 to [until]
     */
    private fun getTwoDifferentInt(until: Int): Pair<Int, Int> {
        val firstIndex = Random.Default.nextInt(until = until)
        var secondIndex: Int = Random.Default.nextInt(until = until)
        while (firstIndex == secondIndex) {
            secondIndex = Random.Default.nextInt(until = until)
        }
        return firstIndex to secondIndex
    }
}