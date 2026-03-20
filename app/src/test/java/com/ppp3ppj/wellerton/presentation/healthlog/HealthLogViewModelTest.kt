package com.ppp3ppj.wellerton.presentation.healthlog

import com.ppp3ppj.wellerton.data.local.entity.HealthLogEntity
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
    fun `initial state shows today date with empty logs`() = runTest {
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
    fun `onDeleteLog calls deleteLog on repository`() = runTest {
        coEvery { repository.deleteLog(any()) } returns Unit
        val viewModel = HealthLogViewModel(repository)
        val log = HealthLogEntity(id = 1, date = LocalDate.now().toString(), timeMinutes = 360, activity = "Wakeup")
        viewModel.onDeleteLog(log)
        coVerify { repository.deleteLog(log) }
    }

    @Test
    fun `logs from repository are reflected in uiState`() = runTest {
        val today = LocalDate.now().toString()
        val logs = listOf(
            HealthLogEntity(id = 1, date = today, timeMinutes = 360, activity = "Wakeup"),
            HealthLogEntity(id = 2, date = today, timeMinutes = 560, activity = "Toilet")
        )
        every { repository.getLogsForDate(today) } returns flowOf(logs)
        val viewModel = HealthLogViewModel(repository)
        assertEquals(2, viewModel.uiState.value.logs.size)
        assertEquals("Wakeup", viewModel.uiState.value.logs[0].activity)
    }
}
