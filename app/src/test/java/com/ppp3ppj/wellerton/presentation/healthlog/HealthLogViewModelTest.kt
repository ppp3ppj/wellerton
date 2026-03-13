package com.ppp3ppj.wellerton.presentation.healthlog

import com.ppp3ppj.wellerton.data.local.entity.HealthLogEntity
import com.ppp3ppj.wellerton.data.local.entity.HealthLogStatus
import com.ppp3ppj.wellerton.data.repository.HealthLogRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HealthLogViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository: HealthLogRepository = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { repository.getLogsForDate(any()) } returns flowOf(emptyList())
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state shows today's date with empty logs`() = runTest {
        val viewModel = HealthLogViewModel(repository)
        assertEquals(LocalDate.now(), viewModel.uiState.value.selectedDate)
        assertTrue(viewModel.uiState.value.logs.isEmpty())
    }

    @Test
    fun `onDateChange advances date by delta`() = runTest {
        val viewModel = HealthLogViewModel(repository)
        viewModel.onDateChange(1)
        assertEquals(LocalDate.now().plusDays(1), viewModel.uiState.value.selectedDate)
    }

    @Test
    fun `onDateChange backwards moves date back`() = runTest {
        val viewModel = HealthLogViewModel(repository)
        viewModel.onDateChange(-1)
        assertEquals(LocalDate.now().minusDays(1), viewModel.uiState.value.selectedDate)
    }

    @Test
    fun `onShowAddSheet sets isAddSheetVisible true`() = runTest {
        val viewModel = HealthLogViewModel(repository)
        viewModel.onShowAddSheet()
        assertTrue(viewModel.uiState.value.isAddSheetVisible)
    }

    @Test
    fun `onDismissSheet sets isAddSheetVisible false`() = runTest {
        val viewModel = HealthLogViewModel(repository)
        viewModel.onShowAddSheet()
        viewModel.onDismissSheet()
        assertFalse(viewModel.uiState.value.isAddSheetVisible)
    }

    @Test
    fun `onDraftActivityChange updates draftActivity`() = runTest {
        val viewModel = HealthLogViewModel(repository)
        viewModel.onDraftActivityChange("Wakeup")
        assertEquals("Wakeup", viewModel.uiState.value.draftActivity)
    }

    @Test
    fun `onDraftTimeChange updates hour and minute`() = runTest {
        val viewModel = HealthLogViewModel(repository)
        viewModel.onDraftTimeChange(9, 20)
        assertEquals(9, viewModel.uiState.value.draftTimeHour)
        assertEquals(20, viewModel.uiState.value.draftTimeMinute)
    }

    @Test
    fun `onDraftStatusChange updates draftStatus`() = runTest {
        val viewModel = HealthLogViewModel(repository)
        viewModel.onDraftStatusChange(HealthLogStatus.GOOD)
        assertEquals(HealthLogStatus.GOOD, viewModel.uiState.value.draftStatus)
    }

    @Test
    fun `onSaveLog with blank activity does not call repository`() = runTest {
        val viewModel = HealthLogViewModel(repository)
        viewModel.onSaveLog()
        coVerify(exactly = 0) { repository.addLog(any()) }
    }

    @Test
    fun `onSaveLog with activity saves and dismisses sheet`() = runTest {
        coEvery { repository.addLog(any()) } returns Unit
        val viewModel = HealthLogViewModel(repository)
        viewModel.onShowAddSheet()
        viewModel.onDraftActivityChange("Wakeup")
        viewModel.onDraftTimeChange(6, 0)
        viewModel.onSaveLog()
        coVerify { repository.addLog(match { it.activity == "Wakeup" && it.timeMinutes == 360 }) }
        assertFalse(viewModel.uiState.value.isAddSheetVisible)
    }

    @Test
    fun `onToggleStatus calls updateLog with new status`() = runTest {
        coEvery { repository.updateLog(any()) } returns Unit
        val viewModel = HealthLogViewModel(repository)
        val log = HealthLogEntity(id = 1, date = "2026-03-13", timeMinutes = 360, activity = "Wakeup")
        viewModel.onToggleStatus(log, HealthLogStatus.GOOD)
        coVerify { repository.updateLog(match { it.status == HealthLogStatus.GOOD }) }
    }

    @Test
    fun `onDeleteLog calls deleteLog on repository`() = runTest {
        coEvery { repository.deleteLog(any()) } returns Unit
        val viewModel = HealthLogViewModel(repository)
        val log = HealthLogEntity(id = 1, date = "2026-03-13", timeMinutes = 360, activity = "Wakeup")
        viewModel.onDeleteLog(log)
        coVerify { repository.deleteLog(log) }
    }

    @Test
    fun `logs from repository are reflected in uiState`() = runTest {
        val logs = listOf(
            HealthLogEntity(id = 1, date = "2026-03-13", timeMinutes = 360, activity = "Wakeup"),
            HealthLogEntity(id = 2, date = "2026-03-13", timeMinutes = 560, activity = "Toilet")
        )
        every { repository.getLogsForDate(LocalDate.now().toString()) } returns flowOf(logs)
        val viewModel = HealthLogViewModel(repository)
        assertEquals(2, viewModel.uiState.value.logs.size)
        assertEquals("Wakeup", viewModel.uiState.value.logs[0].activity)
    }
}
