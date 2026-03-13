package com.ppp3ppj.wellerton.presentation.healthlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ppp3ppj.wellerton.data.local.entity.HealthLogEntity
import com.ppp3ppj.wellerton.data.repository.HealthLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class HealthLogUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val logs: List<HealthLogEntity> = emptyList()
)

@HiltViewModel
class HealthLogViewModel @Inject constructor(
    private val repository: HealthLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthLogUiState())
    val uiState: StateFlow<HealthLogUiState> = _uiState

    private var logsJob: Job? = null

    init {
        loadLogsForDate(_uiState.value.selectedDate)
    }

    fun onDateChange(delta: Int) {
        val newDate = _uiState.value.selectedDate.plusDays(delta.toLong())
        _uiState.update { it.copy(selectedDate = newDate) }
        loadLogsForDate(newDate)
    }

    fun onDeleteLog(log: HealthLogEntity) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }

    private fun loadLogsForDate(date: LocalDate) {
        logsJob?.cancel()
        logsJob = viewModelScope.launch {
            repository.getLogsForDate(date.toString()).collect { logs ->
                _uiState.update { it.copy(logs = logs) }
            }
        }
    }
}
