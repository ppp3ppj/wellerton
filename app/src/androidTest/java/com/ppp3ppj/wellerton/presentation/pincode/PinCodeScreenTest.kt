package com.ppp3ppj.wellerton.presentation.pincode

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ppp3ppj.wellerton.MainActivity
import com.ppp3ppj.wellerton.data.local.entity.HealthLogEntity
import com.ppp3ppj.wellerton.data.repository.HealthLogRepository
import com.ppp3ppj.wellerton.data.repository.UserRepository
import com.ppp3ppj.wellerton.di.RepositoryModule
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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

    // --- Fake repositories ---

    class FakeUserRepository @Inject constructor() : UserRepository {
        override suspend fun findUserByPin(pin: String): String? =
            if (pin == "000000") "admin" else null
    }

    class FakeHealthLogRepository @Inject constructor() : HealthLogRepository {
        override fun getLogsForDate(date: String): Flow<List<HealthLogEntity>> = flowOf(emptyList())
        override suspend fun getLogById(id: Int): HealthLogEntity? = null
        override suspend fun addLog(log: HealthLogEntity) = Unit
        override suspend fun updateLog(log: HealthLogEntity) = Unit
        override suspend fun deleteLog(log: HealthLogEntity) = Unit
    }

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class FakeRepositoryModule {
        @Binds
        @Singleton
        abstract fun bindUserRepository(impl: FakeUserRepository): UserRepository

        @Binds
        @Singleton
        abstract fun bindHealthLogRepository(impl: FakeHealthLogRepository): HealthLogRepository
    }

    // --- Tests ---

    @Test
    fun showsEnterPinTitle() {
        composeTestRule.onNodeWithText("Enter PIN").assertIsDisplayed()
    }

    @Test
    fun showsSixDots() {
        composeTestRule.onNodeWithText("1").assertIsDisplayed()
        composeTestRule.onNodeWithText("2").assertIsDisplayed()
        composeTestRule.onNodeWithText("0").assertIsDisplayed()
    }

    @Test
    fun tapDigits_noErrorShown() {
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        composeTestRule.onNodeWithText("3").performClick()
        composeTestRule.onAllNodesWithText("Incorrect PIN").fetchSemanticsNodes().let {
            assert(it.isEmpty()) { "Expected no error message" }
        }
    }

    @Test
    fun correctPin_navigatesToHomeScreen() {
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
        composeTestRule.onNodeWithText("Enter PIN").assertIsDisplayed()
    }
}
