package com.ppp3ppj.wellerton.presentation.pincode

import com.ppp3ppj.wellerton.data.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
class PinCodeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository: UserRepository = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getCurrentUsername() } returns "admin"
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads username from repository`() = runTest {
        val viewModel = PinCodeViewModel(repository)
        assertEquals("admin", viewModel.uiState.value.username)
    }

    @Test
    fun `onPinDigit accumulates up to 6 digits`() = runTest {
        coEvery { repository.verifyPin(any(), any()) } returns false
        val viewModel = PinCodeViewModel(repository)

        viewModel.onPinDigit("1")
        viewModel.onPinDigit("2")
        viewModel.onPinDigit("3")
        assertEquals("123", viewModel.uiState.value.pin)
    }

    @Test
    fun `onPinDigit caps at 6 digits`() = runTest {
        coEvery { repository.verifyPin(any(), any()) } returns false
        val viewModel = PinCodeViewModel(repository)

        repeat(8) { viewModel.onPinDigit("1") }
        assertEquals(6, viewModel.uiState.value.pin.length)
    }

    @Test
    fun `onDelete removes last digit`() = runTest {
        coEvery { repository.verifyPin(any(), any()) } returns false
        val viewModel = PinCodeViewModel(repository)

        viewModel.onPinDigit("1")
        viewModel.onPinDigit("2")
        viewModel.onDelete()
        assertEquals("1", viewModel.uiState.value.pin)
    }

    @Test
    fun `onDelete on empty pin does nothing`() = runTest {
        val viewModel = PinCodeViewModel(repository)
        viewModel.onDelete()
        assertEquals("", viewModel.uiState.value.pin)
    }

    @Test
    fun `correct 6-digit pin sets isSuccess true`() = runTest {
        coEvery { repository.verifyPin("admin", "000000") } returns true
        val viewModel = PinCodeViewModel(repository)

        "000000".forEach { viewModel.onPinDigit(it.toString()) }
        assertTrue(viewModel.uiState.value.isSuccess)
    }

    @Test
    fun `wrong 6-digit pin sets error and clears pin`() = runTest {
        coEvery { repository.verifyPin("admin", "111111") } returns false
        val viewModel = PinCodeViewModel(repository)

        "111111".forEach { viewModel.onPinDigit(it.toString()) }
        assertFalse(viewModel.uiState.value.isSuccess)
        assertEquals("", viewModel.uiState.value.pin)
        assertEquals("Incorrect PIN", viewModel.uiState.value.error)
    }

    @Test
    fun `new digit after error clears error message`() = runTest {
        coEvery { repository.verifyPin(any(), any()) } returns false
        val viewModel = PinCodeViewModel(repository)

        "111111".forEach { viewModel.onPinDigit(it.toString()) }
        assertEquals("Incorrect PIN", viewModel.uiState.value.error)

        viewModel.onPinDigit("5")
        assertEquals(null, viewModel.uiState.value.error)
    }
}
