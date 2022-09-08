package com.warrickholfeld.geoquiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"

class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val questionBank = listOf(
        Question(R.string.question_australia,true),
        Question(R.string.question_oceans,true),
        Question(R.string.question_mideast,false),
        Question(R.string.question_africa,false),
        Question(R.string.question_americas,true),
        Question(R.string.question_asia,true))

    private val seen = mutableSetOf<Int>()

    private var currentIndex: Int
        get() = savedStateHandle[CURRENT_INDEX_KEY] ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)

    private var score = 0

    private val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    val finished: Boolean
        get() = seen.size == questionBank.size

    val getScore: Int
        get() = score

    private val getCorrectPercentage: Double
        get() = (score.toDouble() / questionBank.size) * 100

    val getCorrectPercentageString: String
        get() = String.format("%.0f%%", getCorrectPercentage)

    val isDisabled: Boolean
        get() = seen.contains(currentIndex)

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }
    fun moveToPrevious() {
        currentIndex = if (currentIndex == 0) {
            questionBank.size - 1
        } else {
            currentIndex - 1
        }
    }

    fun checkAnswer(userAnswer: Boolean): Boolean {
        val correctAnswer = currentQuestionAnswer
        seen.add(currentIndex)
        if (userAnswer == correctAnswer) {
            score += 1
            return true
        }
        return false
    }

}