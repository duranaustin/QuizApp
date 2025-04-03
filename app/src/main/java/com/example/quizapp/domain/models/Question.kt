package com.example.quizapp.domain.models

data class Question(
    val id: Int,
    val question: String,
    val questionType: QuestionType,
    val options: MutableMap<Int, String>? = null,
)

enum class QuestionType {
    TRUE_FALSE,
    MULTIPLE_CHOICE,
    MULTIPLE_ANSWER,
    SHORT_ANSWER
}
