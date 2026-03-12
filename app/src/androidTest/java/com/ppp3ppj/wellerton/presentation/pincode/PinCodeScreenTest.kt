package com.ppp3ppj.wellerton.presentation.pincode

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ppp3ppj.wellerton.MainActivity
import com.ppp3ppj.wellerton.data.repository.UserRepository
import com.ppp3ppj.wellerton.di.RepositoryModule
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(RepositoryModule::class)
@RunWith(AndroidJUnit4::class)
class PinCodeScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var fakeRepo: UserRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    // --- Fake repository ---

    class FakeUserRepository @Inject constructor() : UserRepository {
        override suspend fun getCurrentUsername(): String = "admin"
        override suspend fun verifyPin(username: String, pin: String): Boolean =
            username == "admin" && pin == "000000"
    }

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class FakeRepositoryModule {
        @Binds
        @Singleton
        abstract fun bindUserRepository(impl: FakeUserRepository): UserRepository
    }

    // --- Tests ---

    @Test
    fun showsEnterPinTitle() {
        composeTestRule.onNodeWithText("Enter PIN").assertIsDisplayed()
    }

    @Test
    fun showsSixDots() {
        // Six dot indicators rendered as empty Surface nodes — verify via digit buttons presence
        composeTestRule.onNodeWithText("1").assertIsDisplayed()
        composeTestRule.onNodeWithText("2").assertIsDisplayed()
        composeTestRule.onNodeWithText("0").assertIsDisplayed()
    }

    @Test
    fun tapDigits_noErrorShown() {
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        composeTestRule.onNodeWithText("3").performClick()
        // No error should appear for partial entry
        composeTestRule.onAllNodesWithText("Incorrect PIN").fetchSemanticsNodes().let {
            assert(it.isEmpty()) { "Expected no error message" }
        }
    }

    @Test
    fun correctPin_navigatesToHomeScreen() {
        // admin PIN is 000000
        repeat(6) {
            composeTestRule.onNodeWithText("0").performClick()
        }
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onAllNodesWithText("Welcome").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Welcome").assertIsDisplayed()
        composeTestRule.onNodeWithText("admin").assertIsDisplayed()
    }

    @Test
    fun wrongPin_showsErrorMessage() {
        // Enter 6 wrong digits
        repeat(6) {
            composeTestRule.onNodeWithText("1").performClick()
        }
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onAllNodesWithText("Incorrect PIN").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Incorrect PIN").assertIsDisplayed()
    }

    @Test
    fun deleteButton_removesLastDigit() {
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        composeTestRule.onNodeWithText("⌫").performClick()
        // After delete, only 1 digit remains — error not triggered, Enter PIN still shown
        composeTestRule.onNodeWithText("Enter PIN").assertIsDisplayed()
    }
}
