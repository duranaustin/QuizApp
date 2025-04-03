package com.example.quizapp.presentation.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.quizapp.domain.models.Answer
import com.example.quizapp.domain.models.Question
import com.example.quizapp.domain.models.QuestionType
import com.example.quizapp.presentation.viewmodel.quiz.QuizViewModel
import com.example.quizapp.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen() {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val quizViewModel = hiltViewModel<QuizViewModel>()
    val quizUiState by quizViewModel.quizUiState.collectAsStateWithLifecycle()

    val previousQuestionIndex = remember { mutableIntStateOf(quizUiState.currentQuestionInt) }
    previousQuestionIndex.intValue = quizUiState.currentQuestionInt

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val pagerState = rememberPagerState(
        initialPage = quizUiState.currentQuestionInt,
        pageCount = { quizUiState.questions.size }
    )

    var previousPage by remember { mutableIntStateOf(pagerState.currentPage) }

    LaunchedEffect(pagerState.currentPage) {
        if (previousPage != pagerState.currentPage) {
            focusManager.clearFocus()
            if (pagerState.currentPage > previousPage) {
                quizViewModel.onNext()
            } else {
                quizViewModel.onBack()
            }
            previousPage = pagerState.currentPage
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.quiz),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier.width(56.dp)
                    ) {
                        AnimatedVisibility(
                            visible = quizUiState.currentQuestionInt > 0,
                            enter = slideInHorizontally { fullWidth -> -fullWidth },
                            exit = slideOutHorizontally { fullWidth -> -fullWidth },
                        ) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier.width(56.dp)
                    ) {
                        Row {
                            AnimatedVisibility(
                                visible = quizUiState.currentQuestionInt < quizUiState.questions.size - 1,
                                enter = slideInHorizontally { fullWidth -> fullWidth },
                                exit = slideOutHorizontally { fullWidth -> fullWidth },
                            ) {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Next"
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) { page ->
            QuestionDisplay(
                question = quizUiState.questions[page],
                answer = quizUiState.currentAnswer,
                onTrueOrFalse = { quizViewModel.onTrueOrFalse(it) },
                onMultipleChoice = { id -> quizViewModel.onMultipleChoice(id) },
                onMultipleAnswer = { id, result ->
                    quizViewModel.onMultipleAnswer(
                        id,
                        result
                    )
                },
                onShortAnswer = { quizViewModel.onShortAnswer(it) }
            )
        }
    }
}

@Composable
fun QuestionDisplay(
    question: Question,
    answer: Answer?,
    onTrueOrFalse: (Boolean) -> Unit,
    onMultipleChoice: (Int) -> Unit,
    onMultipleAnswer: (Int, Boolean) -> Unit,
    onShortAnswer: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = question.question,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        when (question.questionType) {
            QuestionType.TRUE_FALSE -> {
                TrueFalseQuestion(
                    answer = answer?.trueOrFalseAnswer ?: false,
                    onAnswerSelected = onTrueOrFalse
                )
            }

            QuestionType.MULTIPLE_CHOICE -> {
                MultipleChoiceQuestion(
                    options = question.options ?: emptyMap(),
                    selectedAnswer = answer?.multipleAnswers?.entries?.firstOrNull { it.value == true }?.key,
                    onAnswerSelected = onMultipleChoice
                )
            }

            QuestionType.MULTIPLE_ANSWER -> {
                MultipleAnswerQuestion(
                    options = question.options ?: emptyMap(),
                    selectedAnswers = answer?.multipleAnswers ?: emptyMap(),
                    onAnswerSelected = onMultipleAnswer
                )
            }

            QuestionType.SHORT_ANSWER -> {
                ShortAnswerQuestion(
                    answerText = answer?.shortAnswer.orEmpty(),
                    onTextChanged = onShortAnswer
                )
            }
        }
    }
}

@Composable
fun TrueFalseQuestion(answer: Boolean, onAnswerSelected: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.true_or_false), style = MaterialTheme.typography.bodyLarge)
        Switch(
            checked = answer,
            onCheckedChange = { onAnswerSelected(it) }
        )
    }
}

@Composable
fun MultipleChoiceQuestion(
    options: Map<Int, String>,
    selectedAnswer: Int?,
    onAnswerSelected: (Int) -> Unit
) {
    Column {
        options.forEach { (id, text) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onAnswerSelected(id)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedAnswer == id,
                    onClick = { onAnswerSelected(id) }
                )
                Text(
                    text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun MultipleAnswerQuestion(
    options: Map<Int, String>,
    selectedAnswers: Map<Int, Boolean?>,
    onAnswerSelected: (Int, Boolean) -> Unit
) {
    Column {
        options.forEach { (id, text) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onAnswerSelected(id, !(selectedAnswers[id] ?: false))
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedAnswers[id] ?: false,
                    onCheckedChange = { onAnswerSelected(id, it) }
                )
                Text(
                    text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ShortAnswerQuestion(answerText: String, onTextChanged: (String) -> Unit) {
    OutlinedTextField(
        value = answerText,
        onValueChange = onTextChanged,
        label = { Text(stringResource(R.string.your_answer)) },
        modifier = Modifier.fillMaxWidth()
    )
}





