package com.huyvu.flashcard.presentation.study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huyvu.flashcard.domain.model.Flashcard
import com.huyvu.flashcard.presentation.theme.FlashcardTheme

/** Bản "có não": lấy ViewModel, đọc state, chuyển sự kiện. */
@Composable
fun StudyScreen(
    modifier: Modifier = Modifier,
    viewModel: StudyViewModel = viewModel(factory = StudyViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    StudyContent(
        state = state,
        onFlip = viewModel::onFlip,
        onAnswer = viewModel::onAnswer,
        onRestart = viewModel::onRestart,
        modifier = modifier,
    )
}

/** Bản "anh phục vụ": chỉ vẽ theo state và báo sự kiện lên. Dễ preview, dễ test. */
@Composable
fun StudyContent(
    state: StudyUiState,
    onFlip: () -> Unit,
    onAnswer: (isCorrect: Boolean) -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val card = state.currentCard
        when {
            state.isFinished -> {
                Text("Xong bộ thẻ rồi! 🎉", style = MaterialTheme.typography.headlineSmall)
                Button(onClick = onRestart, modifier = Modifier.padding(top = 16.dp)) {
                    Text("Học lại")
                }
            }

            card == null -> Text("Chưa có thẻ nào")

            else -> {
                Text("Thẻ ${state.currentIndex + 1}/${state.cards.size} · chuỗi đúng: ${card.streak}")

                Card(
                    onClick = onFlip,
                    modifier = Modifier.fillMaxWidth().height(220.dp).padding(vertical = 16.dp),
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (state.isFlipped) card.back else card.front,
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }
                }

                if (state.isFlipped) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedButton(onClick = { onAnswer(false) }) { Text("Sai rồi") }
                        Button(onClick = { onAnswer(true) }) { Text("Đúng rồi!") }
                    }
                } else {
                    Text("Chạm vào thẻ để lật")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StudyContentPreview() {
    FlashcardTheme {
        StudyContent(
            state = StudyUiState(cards = listOf(Flashcard(1, "Hello", "Xin chào"))),
            onFlip = {}, onAnswer = {}, onRestart = {},
        )
    }
}
