package com.piplog.app.ui.screens.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piplog.app.data.model.JournalEntry
import com.piplog.app.data.repository.AuthRepository
import com.piplog.app.data.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class JournalViewModel(
    private val journalRepository: JournalRepository = JournalRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    init {
        loadEntries()
    }

    fun loadEntries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val userId = authRepository.currentUserId
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, error = "User not logged in") }
                return@launch
            }

            journalRepository.getAllEntries(userId).fold(
                onSuccess = { entries ->
                    _uiState.update { it.copy(isLoading = false, entries = entries) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    fun saveEntry(title: String, content: String, type: String, existingEntry: JournalEntry? = null) {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch

            if (existingEntry != null) {
                journalRepository.updateEntry(existingEntry.id, title, content)
            } else {
                val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(Date())
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                val entry = JournalEntry(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    title = title,
                    content = content,
                    entryType = type,
                    entryDate = today,
                    createdAt = now,
                    updatedAt = now
                )
                journalRepository.insertEntry(entry)
            }

            loadEntries()
        }
    }

    fun deleteEntry(entryId: String) {
        viewModelScope.launch {
            journalRepository.deleteEntry(entryId)
            loadEntries()
        }
    }
}
