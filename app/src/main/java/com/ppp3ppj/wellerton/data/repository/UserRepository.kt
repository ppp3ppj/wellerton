package com.ppp3ppj.wellerton.data.repository

interface UserRepository {
    /** Returns the username of the user whose PIN matches, or null if no match. */
    suspend fun findUserByPin(pin: String): String?
}
