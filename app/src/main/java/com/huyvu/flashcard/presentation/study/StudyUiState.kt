package com.huyvu.flashcard.presentation.study

import com.huyvu.flashcard.domain.model.Flashcard

/** "Bảng trạng thái" của màn học — View chỉ việc nhìn vào đây mà vẽ. */
data class StudyUiState(
    val cards: List<Flashcard> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
) {
    val currentCard: Flashcard? get() = cards.getOrNull(currentIndex)
    val isFinished: Boolean get() = cards.isNotEmpty() && currentIndex >= cards.size
}
