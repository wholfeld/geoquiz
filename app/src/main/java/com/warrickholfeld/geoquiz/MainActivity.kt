package com.warrickholfeld.geoquiz

import android.app.Activity
import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.warrickholfeld.geoquiz.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val quizViewModel: QuizViewModel by viewModels()

    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
       // Handle the result
       if (result.resultCode == Activity.RESULT_OK) {
           quizViewModel.isCheater =
               result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
       }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")

        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.trueButton.setOnClickListener {
            checkAnswer(true)
        }

        binding.falseButton.setOnClickListener {
            checkAnswer(false)
        }
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)
        binding.questionTextView.setOnClickListener {
            nextQuestion()
        }

        binding.nextButton.setOnClickListener {
            nextQuestion()
        }

        binding.cheatButton.setOnClickListener {
            //val intent = Intent(this, CheatActivity::class.java)
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            //startActivity(intent)
            cheatLauncher.launch(intent)
        }


        updateQuestion()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurCheatButton()
        }
        binding.previousButton.setOnClickListener {
            quizViewModel.moveToPrevious()
            updateQuestion()
        }

        binding.sdkTextView.setText("API Level " + Build.VERSION.SDK_INT.toString())
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
        quizViewModel.moveToNext()
        updateQuestion()
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)
        if (quizViewModel.isDisabled) {
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
        val successfulAnswer = quizViewModel.checkAnswer(userAnswer)
        val correctAnswer: Boolean = quizViewModel.currentQuestionAnswer
        updateQuestion()
        nextQuestion()

        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgement_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
        if (quizViewModel.finished) {
            val finalPercentage = quizViewModel.getCorrectPercentageString
            val score = quizViewModel.getScore
            println(finalPercentage)
            Toast.makeText(this, "You got $score right. That's $finalPercentage", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun blurCheatButton() {
        val effect = RenderEffect.createBlurEffect(
            10.0f,
            10.0f,
            Shader.TileMode.CLAMP
        )
        binding.cheatButton.setRenderEffect(effect)
    }

}