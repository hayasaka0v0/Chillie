package com.huyvu.flashcard.domain.model

/**
 * Entity: một thẻ học. Kotlin thuần, không biết gì về Android hay database.
 *
 * @property streak số lần trả lời đúng liên tiếp.
 */
data class Flashcard(
    val id: Long,
    val front: String,
    val back: String,
    val streak: Int = 0,
)
