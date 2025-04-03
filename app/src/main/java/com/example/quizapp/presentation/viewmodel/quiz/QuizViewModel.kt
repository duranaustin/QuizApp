package com.example.quizapp.presentation.viewmodel.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.domain.models.Answer
import com.example.quizapp.domain.usecases.quiz.QuizUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizUseCases: QuizUseCases
) : ViewModel() {

    private val _quizUiState = MutableStateFlow(QuizUiState.Empty)
    val quizUiState: StateFlow<QuizUiState> = _quizUiState

    init {
        viewModelScope.launch {
            val quizQuestions = quizUseCases.getQuizQuestions()
            val answersMap = quizQuestions.associate { question ->
                question.id to Answer(
                    questionId = question.id,
                    multipleAnswers = question.options?.keys?.associateWith { false }
                        ?.toMutableMap()
                )
            }.toMutableMap()

            val firstQuestion = quizQuestions.firstOrNull()
            val firstAnswer = firstQuestion?.let { answersMap[it.id] }

            _quizUiState.update {
                it.copy(
                    questions = quizQuestions,
                    answers = answersMap,
                    currentQuestionInt = 0,
                    currentQuestion = firstQuestion,
                    currentAnswer = firstAnswer
                )
            }
        }
    }

    fun onNext() {
        _quizUiState.update { currentState ->
            val nextQuestionIndex = currentState.currentQuestionInt + 1
            val nextQuestion = if (nextQuestionIndex < currentState.questions.size) {
                currentState.questions[nextQuestionIndex]
            } else {
                null
            }

            val nextAnswer = nextQuestion?.let { currentState.answers[it.id] }

            if (nextQuestion != null) {
                currentState.copy(
                    currentQuestionInt = nextQuestionIndex,
                    currentQuestion = nextQuestion,
                    currentAnswer = nextAnswer
                )
            } else {
                currentState
            }
        }
    }

    fun onBack() {
        _quizUiState.update { currentState ->
            val prevQuestionIndex = currentState.currentQuestionInt - 1
            val prevQuestion = if (prevQuestionIndex >= 0) {
                currentState.questions[prevQuestionIndex]
            } else {
                null
            }

            val prevAnswer = prevQuestion?.let { currentState.answers[it.id] }

            if (prevQuestion != null) {
                currentState.copy(
                    currentQuestionInt = prevQuestionIndex,
                    currentQuestion = prevQuestion,
                    currentAnswer = prevAnswer
                )
            } else {
                currentState
            }
        }
    }

    fun onTrueOrFalse(result: Boolean) {
        _quizUiState.update { currentState ->
            currentState.currentAnswer?.let { answer ->
                val updatedAnswers = currentState.answers.toMutableMap()
                updatedAnswers[answer.questionId] = answer.copy(trueOrFalseAnswer = result)
                currentState.copy(
                    answers = updatedAnswers,
                    currentAnswer = updatedAnswers[answer.questionId]
                )
            } ?: currentState
        }
    }

    fun onMultipleChoice(answerId: Int) {
        _quizUiState.update { currentState ->
            currentState.currentAnswer?.let { answer ->
                val updatedMultipleChoice = answer.multipleAnswers?.toMutableMap() ?: mutableMapOf()
                updatedMultipleChoice.keys.forEach { updatedMultipleChoice[it] = false }
                updatedMultipleChoice[answerId] = true

                val updatedAnswers = currentState.answers.toMutableMap()
                updatedAnswers[answer.questionId] =
                    answer.copy(multipleAnswers = updatedMultipleChoice)

                currentState.copy(
                    answers = updatedAnswers,
                    currentAnswer = updatedAnswers[answer.questionId]
                )
            } ?: currentState
        }
    }

    fun onMultipleAnswer(answerId: Int, result: Boolean) {
        _quizUiState.update { currentState ->
            currentState.currentAnswer?.let { answer ->
                val updatedMultipleAnswers =
                    answer.multipleAnswers?.toMutableMap() ?: mutableMapOf()
                updatedMultipleAnswers[answerId] = result

                val updatedAnswers = currentState.answers.toMutableMap()
                updatedAnswers[answer.questionId] =
                    answer.copy(multipleAnswers = updatedMultipleAnswers)

                currentState.copy(
                    answers = updatedAnswers,
                    currentAnswer = updatedAnswers[answer.questionId]
                )
            } ?: currentState
        }
    }

    fun onShortAnswer(result: String) {
        _quizUiState.update { currentState ->
            currentState.currentAnswer?.let { answer ->
                val updatedAnswers = currentState.answers.toMutableMap()
                updatedAnswers[answer.questionId] = answer.copy(shortAnswer = result)
                currentState.copy(
                    answers = updatedAnswers,
                    currentAnswer = updatedAnswers[answer.questionId]
                )
            } ?: currentState
        }
    }

    fun submitQuiz() {
        viewModelScope.launch {
            quizUseCases.submitQuiz()
        }
    }
}
