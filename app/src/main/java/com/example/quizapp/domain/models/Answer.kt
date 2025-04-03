package com.example.quizapp.domain.models

data class Answer(
    val questionId: Int,
    var trueOrFalseAnswer: Boolean? = null,
    var shortAnswer: String? = null,
    var multipleAnswers: Map<Int, Boolean?>? = null,
)
