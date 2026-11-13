package com.huyvu.flashcard.domain.repository

import com.huyvu.flashcard.domain.model.Flashcard
import kotlinx.coroutines.flow.Flow

/**
 * "Cần 200g thịt bò" — Domain chỉ nói CẦN GÌ, không nói lấy Ở ĐÂU.
 * Khu Data sẽ implement interface này (bộ nhớ, Room, API...).
 */
interface FlashcardRepository {
    fun getCards(): Flow<List<Flashcard>>
    suspend fun updateCard(card: Flashcard)
}
