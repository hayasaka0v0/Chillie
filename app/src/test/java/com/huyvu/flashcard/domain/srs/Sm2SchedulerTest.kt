package com.huyvu.flashcard.domain.srs

import com.huyvu.flashcard.domain.model.Answer
import com.huyvu.flashcard.domain.model.ReviewSchedule
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/**
 * Unit test cho luật SM-2: chạy trên máy tính (JVM), không cần emulator.
 *
 * Mỗi hàm có nhãn `@Test` là một bài kiểm tra; JUnit tự tìm và chạy chúng.
 * `assertEquals(mongDoi, thucTe)`: nếu hai giá trị khác nhau thì test FAIL.
 * Tên hàm đặt trong dấu `` ` `` để viết được dấu cách, đọc như một câu.
 */
class Sm2SchedulerTest {

    private val today = LocalDate.of(2026, 11, 19)

    @Test
    fun `got it on a new card - due tomorrow`() {
        val result = Sm2Scheduler.next(ReviewSchedule(), Answer.GOT_IT, today)

        assertEquals(1, result.repetitions)
        assertEquals(1, result.intervalDays)
        assertEquals(LocalDate.of(2026, 11, 20), result.dueDate)
        assertEquals(2.5, result.easeFactor, 1e-9) // số thực: cho phép lệch 1e-9 vì sai số làm tròn
    }

    @Test
    fun `got it the second time - interval 6 days`() {
        val current = ReviewSchedule(repetitions = 1, intervalDays = 1, dueDate = today)

        val result = Sm2Scheduler.next(current, Answer.GOT_IT, today)

        assertEquals(2, result.repetitions)
        assertEquals(6, result.intervalDays)
        assertEquals(LocalDate.of(2026, 11, 25), result.dueDate)
    }

    @Test
    fun `got it the third time - interval multiplied by ease`() {
        val current = ReviewSchedule(repetitions = 2, intervalDays = 6, easeFactor = 2.5, dueDate = today)

        val result = Sm2Scheduler.next(current, Answer.GOT_IT, today)

        assertEquals(15, result.intervalDays) // 6 × 2.5 = 15
    }

    @Test
    fun `got it rounds half up`() {
        val current = ReviewSchedule(repetitions = 3, intervalDays = 15, easeFactor = 2.5, dueDate = today)

        val result = Sm2Scheduler.next(current, Answer.GOT_IT, today)

        assertEquals(38, result.intervalDays) // 15 × 2.5 = 37.5 → 38
    }

    @Test
    fun `again resets the card and makes it due today`() {
        val current = ReviewSchedule(repetitions = 3, intervalDays = 15, easeFactor = 2.5, dueDate = today, lapses = 0)

        val result = Sm2Scheduler.next(current, Answer.AGAIN, today)

        assertEquals(0, result.repetitions)
        assertEquals(0, result.intervalDays)
        assertEquals(today, result.dueDate)
        assertEquals(1, result.lapses)
        assertEquals(1.96, result.easeFactor, 1e-9) // 2.5 - 0.54
    }

    @Test
    fun `again never drops ease below 1_3`() {
        val current = ReviewSchedule(repetitions = 1, intervalDays = 1, easeFactor = 1.5, dueDate = today)

        val result = Sm2Scheduler.next(current, Answer.AGAIN, today)

        assertEquals(1.3, result.easeFactor, 1e-9)
    }
}
