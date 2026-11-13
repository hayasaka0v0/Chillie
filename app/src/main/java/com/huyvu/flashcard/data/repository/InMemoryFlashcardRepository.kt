package com.huyvu.flashcard.data.repository

import com.huyvu.flashcard.domain.model.Flashcard
import com.huyvu.flashcard.domain.repository.FlashcardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * "Người đi chợ" tạm thời: lưu thẻ trong bộ nhớ (tắt app là mất).
 * Sau này thay bằng RoomFlashcardRepository — Domain và Presentation không phải sửa gì.
 */
class InMemoryFlashcardRepository : FlashcardRepository {

    private val cards = MutableStateFlow(
        listOf(
            Flashcard(id = 1, front = "Hello", back = "Xin chào"),
            Flashcard(id = 2, front = "Thank you", back = "Cảm ơn"),
            Flashcard(id = 3, front = "Goodbye", back = "Tạm biệt"),
            Flashcard(id = 4, front = "Friend", back = "Bạn bè"),
        )
    )

    override fun getCards(): Flow<List<Flashcard>> = cards.asStateFlow()

    override suspend fun updateCard(card: Flashcard) {
        cards.update { list -> list.map { if (it.id == card.id) card else it } }
    }
}
