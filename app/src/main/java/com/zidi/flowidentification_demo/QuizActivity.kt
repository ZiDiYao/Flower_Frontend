package com.zidi.flowidentification_demo

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class QuizActivity : AppCompatActivity() {
    
    private val quizMap: Map<String, String> = mapOf(
        "Which flower is known as the symbol of love?" to "Rose",
        "Which flower typically has thorns on its stems?" to "Rose",
        "Which flower is commonly used to make herbal tea?" to "Hibiscus",
        "Which tropical flower is often associated with Hawaii?" to "Hibiscus",
        "Which flower is commonly used in Day of the Dead celebrations?" to "Marigold",
        "Which flower is often used as a natural dye?" to "Marigold",
        "Which flower is also known as Ixora?" to "Jungle Geranium",
        "Which flower has medicinal properties used in traditional Asian medicine?" to "Jungle Geranium"
    )
    
    private lateinit var currentQuestion: String
    private lateinit var currentAnswer: String
    private lateinit var questionTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        questionTextView = findViewById(R.id.question_text)

        // Initialize button listeners
        findViewById<ImageButton>(R.id.option_1).setOnClickListener { checkAnswer("Rose") }
        findViewById<ImageButton>(R.id.option_2).setOnClickListener { checkAnswer("Hibiscus") }
        findViewById<ImageButton>(R.id.option_3).setOnClickListener { checkAnswer("Marigold") }
        findViewById<ImageButton>(R.id.option_4).setOnClickListener { checkAnswer("Jungle Geranium") }

        // Load the first question
        loadQuestion()
    }

    private fun loadQuestion() {
        // Get a random question from the map
        currentQuestion = quizMap.keys.random()
        currentAnswer = quizMap[currentQuestion]!!
        
        // Set the question text
        questionTextView.text = currentQuestion
    }

    private fun checkAnswer(selectedAnswer: String) {
        if (selectedAnswer == currentAnswer) {
            // Correct answer
            Toast.makeText(this, "Correct! That's a $selectedAnswer", Toast.LENGTH_SHORT).show()

            // Wait a bit and then load the next question
            questionTextView.postDelayed({
                loadQuestion()
            }, 1500)
        } else {
            // Wrong answer
            Toast.makeText(this, "Incorrect. Try again!", Toast.LENGTH_SHORT).show()
        }
    }
}
