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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PinCodeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository: UserRepository = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onPinDigit accumulates up to 6 digits`() = runTest {
        coEvery { repository.findUserByPin(any()) } returns null
        val viewModel = PinCodeViewModel(repository)

        viewModel.onPinDigit("1")
        viewModel.onPinDigit("2")
        viewModel.onPinDigit("3")
        assertEquals("123", viewModel.uiState.value.pin)
    }

    @Test
    fun `onPinDigit triggers verify on 6th digit and resets pin on failure`() = runTest {
        coEvery { repository.findUserByPin(any()) } returns null
        val viewModel = PinCodeViewModel(repository)

        repeat(5) { viewModel.onPinDigit("1") }
        assertEquals(5, viewModel.uiState.value.pin.length)

        // 6th digit completes the PIN → verify fires → wrong PIN → pin cleared
        viewModel.onPinDigit("1")
        assertEquals("", viewModel.uiState.value.pin)
        assertEquals("Incorrect PIN", viewModel.uiState.value.error)
    }

    @Test
    fun `onDelete removes last digit`() = runTest {
        coEvery { repository.findUserByPin(any()) } returns null
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
    fun `correct 6-digit pin sets loggedInUsername`() = runTest {
        coEvery { repository.findUserByPin("000000") } returns "admin"
        val viewModel = PinCodeViewModel(repository)

        "000000".forEach { viewModel.onPinDigit(it.toString()) }
        assertEquals("admin", viewModel.uiState.value.loggedInUsername)
    }

    @Test
    fun `wrong 6-digit pin sets error and clears pin`() = runTest {
        coEvery { repository.findUserByPin("111111") } returns null
        val viewModel = PinCodeViewModel(repository)

        "111111".forEach { viewModel.onPinDigit(it.toString()) }
        assertNull(viewModel.uiState.value.loggedInUsername)
        assertEquals("", viewModel.uiState.value.pin)
        assertEquals("Incorrect PIN", viewModel.uiState.value.error)
    }

    @Test
    fun `new digit after error clears error message`() = runTest {
        coEvery { repository.findUserByPin(any()) } returns null
        val viewModel = PinCodeViewModel(repository)

        "111111".forEach { viewModel.onPinDigit(it.toString()) }
        assertEquals("Incorrect PIN", viewModel.uiState.value.error)

        viewModel.onPinDigit("5")
        assertNull(viewModel.uiState.value.error)
    }
}
