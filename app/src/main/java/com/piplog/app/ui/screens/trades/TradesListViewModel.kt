package com.piplog.app.ui.screens.trades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piplog.app.data.model.Trade
import com.piplog.app.data.repository.TradeRepository
import com.piplog.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TradesListUiState(
    val isLoading: Boolean = true,
    val trades: List<Trade> = emptyList(),
    val error: String? = null
)

class TradesListViewModel(
    private val tradeRepository: TradeRepository = TradeRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TradesListUiState())
    val uiState: StateFlow<TradesListUiState> = _uiState.asStateFlow()

    init {
        loadTrades()
    }

    fun loadTrades() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val userId = authRepository.currentUserId
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, error = "User not logged in") }
                return@launch
            }
            tradeRepository.getAllTrades(userId).fold(
                onSuccess = { trades ->
                    _uiState.update { it.copy(isLoading = false, trades = trades) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }
}
