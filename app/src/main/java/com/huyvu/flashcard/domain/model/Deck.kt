package com.huyvu.flashcard.domain.model

import java.time.Instant

/** Deck do người dùng tự tạo hay tải từ kho online. Dùng cho bộ lọc Created / Imported. */
enum class DeckSource { CREATED, IMPORTED }

/**
 * Một bộ thẻ.
 *
 * `Instant` = một thời điểm chính xác (tới mili giây), không phụ thuộc múi giờ.
 *
 * @property id 0 = chưa lưu vào database.
 * @property colorIndex 0, 1 hoặc 2: chọn 1 trong 3 màu nền của thẻ deck trong thiết kế.
 * @property remoteId id trong kho deck online (null nếu tự tạo), để biết deck đã được tải chưa.
 * @property lastStudiedAt lần học gần nhất; `null` = chưa học lần nào.
 */
data class Deck(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "",
    val emblem: Emblem,
    val colorIndex: Int = 0,
    val source: DeckSource = DeckSource.CREATED,
    val remoteId: String? = null,
    val createdAt: Instant,
    val lastStudiedAt: Instant? = null,
)
