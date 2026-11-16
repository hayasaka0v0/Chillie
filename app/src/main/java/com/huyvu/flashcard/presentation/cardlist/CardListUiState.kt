package com.huyvu.flashcard.presentation.cardlist

import com.huyvu.flashcard.domain.model.Flashcard

data class CardListUiState(
    val cards: List<Flashcard> = emptyList()
){

}
