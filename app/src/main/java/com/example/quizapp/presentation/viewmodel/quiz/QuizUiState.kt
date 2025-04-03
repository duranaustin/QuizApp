package com.example.quizapp.presentation.viewmodel.quiz

import com.example.quizapp.domain.models.Answer
import com.example.quizapp.domain.models.Question

data class QuizUiState(
    val currentQuestionInt: Int,
    val currentQuestion: Question?,
    val currentAnswer: Answer?,
    val questions: List<Question>,
    val answers: MutableMap<Int, Answer>
) {
    companion object {
        val Empty = QuizUiState(
            currentQuestionInt = 0,
            currentQuestion = null,
            currentAnswer = null,
            questions = listOf(),
            answers = mutableMapOf()
        )
    }
}
