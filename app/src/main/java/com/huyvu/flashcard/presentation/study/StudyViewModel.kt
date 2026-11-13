package com.huyvu.flashcard.presentation.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.huyvu.flashcard.FlashcardApplication
import com.huyvu.flashcard.domain.repository.FlashcardRepository
import com.huyvu.flashcard.domain.usecase.AnswerCardUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * "Chị quản lý ca": nhận sự kiện từ View, gọi nghiệp vụ, cập nhật StudyUiState.
 * Không giữ tham chiếu tới View, nên xoay màn hình vẫn sống.
 */
class StudyViewModel(
    repository: FlashcardRepository,
    private val answerCard: AnswerCardUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudyUiState())
    val uiState: StateFlow<StudyUiState> = _uiState.asStateFlow()

    init {
        // Nghe dữ liệu từ repository: kho đổi gì thì màn hình tự cập nhật.
        viewModelScope.launch {
            repository.getCards().collect { cards ->
                _uiState.update { it.copy(cards = cards) }
            }
        }
    }

    fun onFlip() = _uiState.update { it.copy(isFlipped = !it.isFlipped) }

    fun onAnswer(isCorrect: Boolean) {
        val card = _uiState.value.currentCard ?: return
        viewModelScope.launch { answerCard(card, isCorrect) }
        _uiState.update { it.copy(currentIndex = it.currentIndex + 1, isFlipped = false) }
    }

    fun onRestart() = _uiState.update { it.copy(currentIndex = 0, isFlipped = false) }

    companion object {
        /** Cách tạo ViewModel khi chưa có Hilt: lấy đồ từ AppContainer. */
        val Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as FlashcardApplication).container
                StudyViewModel(container.flashcardRepository, container.answerCardUseCase)
            }
        }
    }
}
