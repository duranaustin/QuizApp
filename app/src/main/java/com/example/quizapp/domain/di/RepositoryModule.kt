package com.example.quizapp.domain.di

import com.example.quizapp.data.repositories.quiz.IQuizRepository
import com.example.quizapp.data.repositories.quiz.QuizRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provieQuizRepository(
    ): IQuizRepository {
        return QuizRepository()
    }
}
