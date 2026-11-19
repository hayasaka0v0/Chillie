package com.huyvu.flashcard.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ReviewScheduleTest {

    private val today = LocalDate.of(2026, 11, 19)

    @Test
    fun `status is NEW when never reviewed`() {
        assertEquals(CardStatus.NEW, ReviewSchedule().status())
    }

    @Test
    fun `status is MASTERED from 21 days interval`() {
        assertEquals(CardStatus.MASTERED, ReviewSchedule(intervalDays = 21, dueDate = today).status())
        assertEquals(CardStatus.LEARNING, ReviewSchedule(intervalDays = 20, dueDate = today).status())
    }

    @Test
    fun `status is LEARNING after again`() {
        // Đã ôn rồi (có dueDate) nhưng interval về 0 → không còn là thẻ mới
        assertEquals(CardStatus.LEARNING, ReviewSchedule(intervalDays = 0, dueDate = today).status())
    }

    @Test
    fun `new card is always due`() {
        assertTrue(ReviewSchedule().isDue(today))
    }

    @Test
    fun `due is decided by calendar day`() {
        assertTrue(ReviewSchedule(dueDate = today).isDue(today))
        assertTrue(ReviewSchedule(dueDate = today.minusDays(3)).isDue(today))
        assertFalse(ReviewSchedule(dueDate = today.plusDays(1)).isDue(today))
    }
}
