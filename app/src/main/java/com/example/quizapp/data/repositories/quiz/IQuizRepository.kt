package com.example.quizapp.data.repositories.quiz

import com.example.quizapp.domain.models.Question

interface IQuizRepository {
    suspend fun getQuizQuestions(): List<Question>
    suspend fun submitQuiz(): Boolean
}
