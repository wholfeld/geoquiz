package com.warrickholfeld.geoquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.warrickholfeld.geoquiz.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private val questionBank = listOf(
        Question(R.string.question_australia,true),
        Question(R.string.question_oceans,true),
        Question(R.string.question_mideast,false),
        Question(R.string.question_africa,false),
        Question(R.string.question_americas,true),
        Question(R.string.question_asia,true))

    private val seen = mutableSetOf<Int>()

    private var currentIndex = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.trueButton.setOnClickListener {
            checkAnswer(true)
        }

        binding.falseButton.setOnClickListener {
            checkAnswer(false)
        }
        val questionTextResId = questionBank[currentIndex].textResId
        binding.questionTextView.setText(questionTextResId)
        binding.questionTextView.setOnClickListener {
            nextQuestion()
        }

        binding.nextButton.setOnClickListener {
            nextQuestion()
        }
        updateQuestion()
        binding.previousButton.setOnClickListener {
            currentIndex = if (currentIndex == 0) {
                questionBank.size - 1
            } else {
                currentIndex - 1
            }
            Log.d(TAG, "Current question index: $currentIndex")

            try {
                val question = questionBank[currentIndex]
            } catch (ex: ArrayIndexOutOfBoundsException) {
                // Log a message at ERROR log level along with an exception stack trace
                Log.e(TAG, "Index was out of bounds I made this error message", ex)
            }
            updateQuestion()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun nextQuestion() {
        currentIndex = (currentIndex + 1) % questionBank.size
        updateQuestion()
    }

    private fun updateQuestion() {
        val questionTextResId = questionBank[currentIndex].textResId
        binding.questionTextView.setText(questionTextResId)
        if (seen.contains(currentIndex)) {
            disableButtons(true)
        } else {
            disableButtons(false)
        }
    }

    private fun disableButtons(disable: Boolean) {
        if (disable) {
            binding.trueButton.isEnabled = false
            binding.falseButton.isEnabled = false
        } else {
            binding.trueButton.isEnabled = true
            binding.falseButton.isEnabled = true
        }
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = questionBank[currentIndex].answer
        if (userAnswer == correctAnswer) {
            score += 1
        }
        seen.add(currentIndex)
        updateQuestion()
        nextQuestion()

        val messageResId = if (userAnswer == correctAnswer) {
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }

//        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
        if (seen.size == questionBank.size) {
            val finalScore = (score.toDouble() / questionBank.size) * 100
            val finalPercentage = String.format("%.0f%%", finalScore)
            println(finalPercentage)
            Toast.makeText(this, "You got $score right. That's $finalPercentage", Toast.LENGTH_SHORT).show()
        }
    }
}