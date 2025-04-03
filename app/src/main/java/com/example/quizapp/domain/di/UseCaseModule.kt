package com.example.quizapp.domain.di

import com.example.quizapp.data.repositories.quiz.QuizRepository
import com.example.quizapp.domain.usecases.quiz.GetQuizQuestions
import com.example.quizapp.domain.usecases.quiz.QuizUseCases
import com.example.quizapp.domain.usecases.quiz.SubmitQuiz
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideQuizUseCases(quizRepository: QuizRepository): QuizUseCases {
        return QuizUseCases(
            getQuizQuestions = GetQuizQuestions(quizRepository),
            submitQuiz = SubmitQuiz(quizRepository)
        )
    }
}
