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
            val questionsMap = quizQuestions.associateBy { it.id }.toMutableMap()
            val answersMap = quizQuestions.associate { question ->
                question.id to Answer(
                    questionId = question.id,
                    multipleAnswers = question.options?.keys?.associateWith { false }
                        ?.toMutableMap()
                )
            }.toMutableMap()

            val firstQuestion = questionsMap.values.firstOrNull()
            val firstAnswer = firstQuestion?.id?.let { answersMap[it] }

            _quizUiState.update {
                it.copy(
                    questions = questionsMap,
                    answers = answersMap,
                    currentQuestionInt = firstQuestion?.id ?: 0,
                    currentQuestion = firstQuestion,
                    currentAnswer = firstAnswer
                )
            }
        }
    }

    fun onNext() {
        _quizUiState.update { currentState ->
            val nextQuestionInt = currentState.currentQuestionInt + 1
            val nextQuestion = currentState.questions[nextQuestionInt]
            val nextAnswer = nextQuestion?.id?.let { currentState.answers[it] }

            if (nextQuestion != null) {
                currentState.copy(
                    currentQuestionInt = nextQuestionInt,
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
            val prevQuestionInt = currentState.currentQuestionInt - 1
            val prevQuestion = currentState.questions[prevQuestionInt]
            val prevAnswer = prevQuestion?.id?.let { currentState.answers[it] }

            if (prevQuestion != null) {
                currentState.copy(
                    currentQuestionInt = prevQuestionInt,
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
