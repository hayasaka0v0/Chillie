package com.huyvu.flashcard.domain.model

import java.time.Instant

/**
 * Một dòng nhật ký: thẻ nào được ôn, lúc nào, đúng hay sai.
 * Dùng để vẽ biểu đồ tuần, tính chuỗi ngày học và dòng "Studied yesterday".
 */
data class ReviewLog(
    val id: Long = 0,
    val cardId: Long,
    val deckId: Long,
    val reviewedAt: Instant,
    val correct: Boolean,
)
