package com.piplog.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piplog.app.data.model.Profile
import com.piplog.app.data.repository.AuthRepository
import com.piplog.app.data.repository.TradeRepository
import com.piplog.app.utils.DashboardMetrics
import com.piplog.app.utils.calculateDashboardMetrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: Profile? = null,
    val metrics: DashboardMetrics? = null,
    val email: String = "",
    val error: String? = null
)

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val tradeRepository: TradeRepository = TradeRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val userId = authRepository.currentUserId
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, error = "User not logged in") }
                return@launch
            }

            val session = authRepository.getCurrentSession()
            val profileResult = authRepository.getProfile(userId)
            val tradesResult = tradeRepository.getAllTrades(userId)

            val trades = tradesResult.getOrNull() ?: emptyList()
            val metrics = calculateDashboardMetrics(trades)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    profile = profileResult.getOrNull(),
                    metrics = metrics,
                    email = session.email ?: ""
                )
            }
        }
    }

    fun updateProfile(displayName: String) {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch
            authRepository.updateProfile(userId, displayName, null)
            loadData()
        }
    }
}
