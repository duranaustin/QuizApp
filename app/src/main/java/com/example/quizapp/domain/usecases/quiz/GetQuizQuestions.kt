package com.example.quizapp.domain.usecases.quiz

import com.example.quizapp.data.repositories.quiz.IQuizRepository
import com.example.quizapp.domain.models.Question

class GetQuizQuestions(
    private val repository: IQuizRepository
) {
    suspend operator fun invoke(): List<Question> {
        return repository.getQuizQuestions()
    }
}
