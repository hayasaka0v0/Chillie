package com.huyvu.flashcard.domain.srs

import com.huyvu.flashcard.domain.model.Answer
import com.huyvu.flashcard.domain.model.ReviewSchedule
import java.time.LocalDate
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Luật ôn tập ngắt quãng SM-2 (bản rút gọn cho 2 nút Again / Got it).
 *
 * `object`: chỉ có một bản duy nhất, gọi thẳng `Sm2Scheduler.next(...)`.
 * Không cần tạo object vì luật này không cần nhớ gì giữa các lần gọi:
 * cùng đầu vào thì luôn ra cùng kết quả (hàm thuần).
 */
object Sm2Scheduler {

    private const val MIN_EASE = 1.3

    // Điểm chất lượng của SM-2 (thang 0–5) ứng với 2 nút của app.
    private const val QUALITY_GOT_IT = 4
    private const val QUALITY_AGAIN = 1

    /**
     * Tính lịch ôn MỚI sau khi trả lời. Không sửa `current` (nó là val),
     * mà trả về một ReviewSchedule mới tạo bằng `copy(...)`.
     */
    fun next(current: ReviewSchedule, answer: Answer, today: LocalDate): ReviewSchedule {
        return when (answer) {
            Answer.GOT_IT -> {
                val newEase = nextEase(current.easeFactor, QUALITY_GOT_IT)
                val newRepetitions = current.repetitions + 1
                val newInterval = when (newRepetitions) {
                    1 -> 1
                    2 -> 6
                    else -> (current.intervalDays * newEase).roundToInt() // 37.5 → 38
                }
                current.copy(
                    repetitions = newRepetitions,
                    easeFactor = newEase,
                    intervalDays = newInterval,
                    dueDate = today.plusDays(newInterval.toLong()),
                )
            }

            Answer.AGAIN -> current.copy(
                repetitions = 0,
                easeFactor = nextEase(current.easeFactor, QUALITY_AGAIN),
                intervalDays = 0,
                dueDate = today,            // đến hạn lại ngay hôm nay
                lapses = current.lapses + 1,
            )
        }
    }

    /** Công thức độ dễ của SM-2, không cho thấp hơn 1.3. q = 4 → giữ nguyên; q = 1 → giảm 0.54. */
    private fun nextEase(ease: Double, quality: Int): Double {
        val q = 5 - quality
        val changed = ease + (0.1 - q * (0.08 + q * 0.02))
        return max(MIN_EASE, changed)
    }
}
