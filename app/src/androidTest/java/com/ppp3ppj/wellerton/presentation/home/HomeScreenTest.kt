package com.ppp3ppj.wellerton.presentation.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysWelcomeAndUsername() {
        composeTestRule.setContent {
            HomeScreen(username = "admin", onLogout = {})
        }
        composeTestRule.onNodeWithText("Welcome").assertIsDisplayed()
        composeTestRule.onNodeWithText("admin").assertIsDisplayed()
    }

    @Test
    fun displaysLogoutButton() {
        composeTestRule.setContent {
            HomeScreen(username = "admin", onLogout = {})
        }
        composeTestRule.onNodeWithText("Logout").assertIsDisplayed()
    }

    @Test
    fun logoutButton_invokesCallback() {
        var logoutCalled = false
        composeTestRule.setContent {
            HomeScreen(username = "admin", onLogout = { logoutCalled = true })
        }
        composeTestRule.onNodeWithText("Logout").performClick()
        assertTrue(logoutCalled)
    }
}
