package com.ppp3ppj.wellerton.presentation.healthlog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ppp3ppj.wellerton.data.local.entity.HealthLogActivityType
import com.ppp3ppj.wellerton.data.local.entity.HealthLogEntity
import com.ppp3ppj.wellerton.data.local.entity.HealthLogStatus
import com.ppp3ppj.wellerton.data.repository.HealthLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class HealthLogFormUiState(
    val isLoading: Boolean = true,
    val draftTimeHour: Int = 6,
    val draftTimeMinute: Int = 0,
    val draftActivity: String = "",
    val draftStatus: HealthLogStatus = HealthLogStatus.UNRATED,
    val draftType: HealthLogActivityType = HealthLogActivityType.OTHER,
    val isSaved: Boolean = false
)

@HiltViewModel
class HealthLogFormViewModel @Inject constructor(
    private val repository: HealthLogRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val logId: Int = savedStateHandle.get<Int>("logId") ?: -1
    private val date: String = savedStateHandle.get<String>("date") ?: LocalDate.now().toString()
    private var editingLog: HealthLogEntity? = null

    private val _uiState = MutableStateFlow(HealthLogFormUiState())
    val uiState: StateFlow<HealthLogFormUiState> = _uiState

    init {
        if (logId != -1) {
            viewModelScope.launch {
                val log = repository.getLogById(logId)
                editingLog = log
                if (log != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            draftTimeHour = log.timeMinutes / 60,
                            draftTimeMinute = log.timeMinutes % 60,
                            draftActivity = log.activity,
                            draftStatus = log.status,
                            draftType = log.type
                        )
                    }
                }
            }
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onTypeChange(type: HealthLogActivityType) {
        _uiState.update { state ->
            val shouldAutoFill = state.draftActivity.isBlank() ||
                HealthLogActivityType.entries.any { it.label == state.draftActivity }
            state.copy(
                draftType = type,
                draftActivity = if (shouldAutoFill && type != HealthLogActivityType.OTHER) type.label else state.draftActivity
            )
        }
    }

    fun onActivityChange(text: String) {
        _uiState.update { it.copy(draftActivity = text) }
    }

    fun onStatusChange(status: HealthLogStatus) {
        _uiState.update { it.copy(draftStatus = status) }
    }

    fun onSave(hour: Int, minute: Int) {
        val state = _uiState.value
        if (state.draftActivity.isBlank()) return
        viewModelScope.launch {
            val timeMinutes = hour * 60 + minute
            if (editingLog != null) {
                repository.updateLog(
                    editingLog!!.copy(
                        timeMinutes = timeMinutes,
                        activity = state.draftActivity.trim(),
                        status = state.draftStatus,
                        type = state.draftType
                    )
                )
            } else {
                repository.addLog(
                    HealthLogEntity(
                        date = date,
                        timeMinutes = timeMinutes,
                        activity = state.draftActivity.trim(),
                        status = state.draftStatus,
                        type = state.draftType
                    )
                )
            }
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
