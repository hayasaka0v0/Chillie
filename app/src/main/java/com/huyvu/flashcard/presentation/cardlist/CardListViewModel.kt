package com.huyvu.flashcard.presentation.cardlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.huyvu.flashcard.FlashcardApplication
import com.huyvu.flashcard.domain.repository.FlashcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CardListViewModel(
    repository: FlashcardRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardListUiState())
    val uiState: StateFlow<CardListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getCards().collect { cards ->
                _uiState.update { it.copy(cards = cards) }
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as FlashcardApplication).container
                CardListViewModel(container.flashcardRepository)
            }
        }
    }
}