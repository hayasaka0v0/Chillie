package com.huyvu.flashcard.domain.model

import java.time.LocalDate

/** Trạng thái của một thẻ, dùng cho vòng tròn Mastered / Learning / New ở màn Deck. */
enum class CardStatus { NEW, LEARNING, MASTERED }

/**
 * Lịch ôn của MỘT thẻ (thuật toán SM-2).
 *
 * `data class`: Kotlin tự viết sẵn `==` (so từng ô), `copy(...)`, `toString()`.
 * Mọi ô là `val` → không sửa được; muốn đổi thì `copy` ra bản mới.
 * Các ô có `= ...` là giá trị mặc định: `ReviewSchedule()` tạo một lịch của thẻ mới tinh.
 *
 * @property repetitions số lần "Got it" liên tiếp (Again đưa về 0).
 * @property easeFactor độ dễ của thẻ; thẻ càng dễ thì khoảng cách ôn càng giãn nhanh.
 * @property intervalDays khoảng cách (ngày) tới lần ôn tiếp theo.
 * @property dueDate ngày đến hạn ôn. `LocalDate?` có dấu `?` → được phép `null`;
 *                   `null` nghĩa là thẻ CHƯA ÔN LẦN NÀO (thẻ New).
 * @property lapses số lần bấm Again (quên).
 */
data class ReviewSchedule(
    val repetitions: Int = 0,
    val easeFactor: Double = 2.5,
    val intervalDays: Int = 0,
    val dueDate: LocalDate? = null,
    val lapses: Int = 0,
) {
    /**
     * Hàm thành viên (viết bên trong class): gọi bằng `schedule.status()`,
     * bên trong `dueDate`, `intervalDays` nghĩa là `this.dueDate`, `this.intervalDays`.
     *
     * `when { điều kiện -> kết quả ... }`: thử từng điều kiện từ trên xuống,
     * gặp điều kiện đúng đầu tiên thì lấy kết quả của nó. `else` = không điều kiện nào đúng.
     */
    fun status(): CardStatus {
        return when {
            dueDate == null -> CardStatus.NEW
            intervalDays >= MASTERED_INTERVAL_DAYS -> CardStatus.MASTERED
            else -> CardStatus.LEARNING
        }
    }

    /** Thẻ có cần ôn vào ngày `today` không. Tính theo ngày lịch, không theo giờ. */
    fun isDue(today: LocalDate): Boolean {
        val due = dueDate ?: return true // `?:` = "nếu bên trái là null thì làm bên phải": thẻ New luôn đến hạn
        return !due.isAfter(today)       // due <= today
    }

    /**
     * `companion object`: chỗ đặt những thứ thuộc về CẢ class, không thuộc riêng object nào.
     * Dùng: `ReviewSchedule.MASTERED_INTERVAL_DAYS`.
     * `const val`: hằng số biết giá trị ngay lúc biên dịch.
     */
    companion object {
        const val MASTERED_INTERVAL_DAYS = 21
    }
}
