package com.piplog.app.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piplog.app.data.repository.AuthRepository
import com.piplog.app.data.repository.TradeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalendarViewModel(
    private val tradeRepository: TradeRepository = TradeRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadCalendar()
    }

    fun loadCalendar() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val userId = authRepository.currentUserId
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            tradeRepository.getAllTrades(userId).fold(
                onSuccess = { trades ->
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val displayFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                    val dayMap = mutableMapOf<String, Pair<Double, Int>>()

                    trades.forEach { trade ->
                        val dateKey = try {
                            val date = displayFormat.parse(trade.openedAt) ?: return@forEach
                            dateFormat.format(date)
                        } catch (e: Exception) {
                            trade.openedAt.take(10)
                        }
                        val existing = dayMap[dateKey] ?: (0.0 to 0)
                        dayMap[dateKey] = (existing.first + (trade.pnl ?: 0.0)) to (existing.second + 1)
                    }

                    val calendarDays = dayMap.map { (dateKey, data) ->
                        val date = try { dateFormat.parse(dateKey) ?: Date() } catch (e: Exception) { Date() }
                        CalendarDay(date = date, pnl = data.first, tradeCount = data.second, dateKey = dateKey)
                    }

                    _uiState.update {
                        it.copy(isLoading = false, days = calendarDays, allTrades = trades)
                    }
                },
                onFailure = {
                    _uiState.update { it.copy(isLoading = false) }
                }
            )
        }
    }

    fun selectDate(dateKey: String) {
        val dayData = _uiState.value.days.find { it.dateKey == dateKey }
        val dayTrades = _uiState.value.allTrades.filter { it.openedAt.take(10) == dateKey }
        _uiState.update {
            it.copy(
                selectedDate = dayData ?: CalendarDay(
                    date = try { SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateKey) ?: Date() }
                           catch (e: Exception) { Date() },
                    pnl = 0.0, tradeCount = 0, dateKey = dateKey
                ),
                selectedDayTrades = dayTrades
            )
        }
    }
}
