package com.huyvu.flashcard.domain.usecase

import com.huyvu.flashcard.domain.model.Flashcard
import com.huyvu.flashcard.domain.repository.FlashcardRepository

/**
 * Luật nghiệp vụ khi trả lời một thẻ:
 * - Đúng → streak + 1
 * - Sai  → streak về 0
 *
 * Có logic thật nên mới đáng tách thành UseCase.
 */
class AnswerCardUseCase(private val repository: FlashcardRepository) {
    suspend operator fun invoke(card: Flashcard, isCorrect: Boolean) {
        val newStreak = if (isCorrect) card.streak + 1 else 0
        repository.updateCard(card.copy(streak = newStreak))
    }
}
