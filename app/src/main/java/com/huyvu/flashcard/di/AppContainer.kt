package com.huyvu.flashcard.di

import com.huyvu.flashcard.data.repository.InMemoryFlashcardRepository
import com.huyvu.flashcard.domain.repository.FlashcardRepository
import com.huyvu.flashcard.domain.usecase.AnswerCardUseCase

/**
 * "Dây điện" nối các khu với nhau (DI thủ công).
 * Đây là chỗ DUY NHẤT biết Domain đang dùng bản Data nào.
 * Sau này chuyển sang Hilt thì file này được thay bằng các @Module.
 */
class AppContainer {
    val flashcardRepository: FlashcardRepository by lazy { InMemoryFlashcardRepository() }

    val answerCardUseCase: AnswerCardUseCase by lazy { AnswerCardUseCase(flashcardRepository) }
}
