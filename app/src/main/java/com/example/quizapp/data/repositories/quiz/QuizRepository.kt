package com.example.quizapp.data.repositories.quiz

import com.example.quizapp.domain.models.Question
import com.example.quizapp.domain.models.QuestionType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(): IQuizRepository {
    override suspend fun getQuizQuestions(): List<Question> {
        return listOf(
            Question(
                114,
                "True or False: An Activity is destroyed during recomposition?",
                QuestionType.TRUE_FALSE
            ),
            Question(
                543,
                "What is the primary purpose of Jetpack Compose?",
                QuestionType.MULTIPLE_CHOICE,
                mutableMapOf(
                    1 to "To build UI with a declarative approach",
                    2 to "To manage background tasks",
                    3 to "To access device sensors",
                    4 to "To handle network requests"
                )
            ),
            Question(
                23,
                "What does the acronym 'MVVM' stand for in Android development?",
                QuestionType.SHORT_ANSWER
            ),
            Question(
                1,
                question = "Which of the following are benefits of using Jetpack Compose?",
                questionType = QuestionType.MULTIPLE_ANSWER,
                options = mutableMapOf(
                    1 to "Declarative UI",
                    2 to "Faster Development",
                    3 to "XML-based layouts",
                    4 to "Easy to integrate with existing code",
                    5 to "Imperative UI"
                )
            ),
            Question(
                333,
                "Which of these are UI components in Jetpack Compose? (Select all that apply)",
                QuestionType.MULTIPLE_ANSWER,
                mutableMapOf(
                    1 to "Text()",
                    2 to "Button()",
                    3 to "RecyclerView",
                    4 to "Card()",
                    5 to "EditText",
                    6 to "Switch()",
                    7 to "LazyColumn()",
                    8 to "ScrollView",
                    9 to "Slider()",
                    10 to "TabRow()",
                    11 to "Snackbar()",
                    12 to "Divider()",
                    13 to "Checkbox()",
                    14 to "FloatingActionButton()",
                    15 to "Scaffold()",
                    16 to "ProgressIndicator()",
                    17 to "BottomSheet()",
                    18 to "NavigationRail()",
                    19 to "Pager()",
                    20 to "ModalDrawer()"
                )
            )
        )
    }

    override suspend fun submitQuiz(): Boolean {
        return true
    }
}
