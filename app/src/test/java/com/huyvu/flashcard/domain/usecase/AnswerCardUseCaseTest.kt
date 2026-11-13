package com.huyvu.flashcard.domain.usecase

import com.huyvu.flashcard.data.repository.InMemoryFlashcardRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class AnswerCardUseCaseTest {

    private val repository = InMemoryFlashcardRepository()
    private val answerCard = AnswerCardUseCase(repository)

    private suspend fun firstCard() = repository.getCards().first().first()

    @Test
    fun correctAnswer_increasesStreak() = runBlocking {
        answerCard(firstCard(), isCorrect = true)
        answerCard(firstCard(), isCorrect = true)

        assertEquals(2, firstCard().streak)
    }

    @Test
    fun wrongAnswer_resetsStreak() = runBlocking {
        answerCard(firstCard(), isCorrect = true)
        answerCard(firstCard(), isCorrect = false)

        assertEquals(0, firstCard().streak)
    }
}
