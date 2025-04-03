package com.example.quizapp.domain.usecases.quiz

import com.example.quizapp.data.repositories.quiz.IQuizRepository

class SubmitQuiz(
    private val repository: IQuizRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.submitQuiz()
    }
}
