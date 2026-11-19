package com.huyvu.flashcard.domain.model

/**
 * Một thẻ học. Kotlin thuần, không biết gì về Android hay database.
 *
 * Chỉ `deckId`, `front`, `back` là bắt buộc; các ô còn lại có giá trị mặc định.
 * Chuỗi rỗng `""` nghĩa là "không có", màn hình sẽ ẩn ô đó.
 *
 * Ví dụ tạo thẻ (truyền theo tên, bỏ qua các ô không cần):
 *     Flashcard(deckId = 1, front = "水", back = "water", reading = "みず")
 *
 * @property id 0 = thẻ chưa lưu vào database (database sẽ tự cấp id thật).
 * @property label nhãn nhỏ dưới mặt trước, vd. "Noun".
 * @property reading cách đọc, hiện phía trên nghĩa ở mặt sau, vd. "みず".
 * @property pronunciation phiên âm, vd. "mizu".
 * @property example câu ví dụ; @property exampleTranslation bản dịch câu ví dụ.
 */
data class Flashcard(
    val id: Long = 0,
    val deckId: Long,
    val front: String,
    val back: String,
    val label: String = "",
    val reading: String = "",
    val pronunciation: String = "",
    val example: String = "",
    val exampleTranslation: String = "",
    val schedule: ReviewSchedule = ReviewSchedule(),
)
