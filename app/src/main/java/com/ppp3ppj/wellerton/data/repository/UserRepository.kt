package com.ppp3ppj.wellerton.data.repository

interface UserRepository {
    suspend fun getCurrentUsername(): String?
    suspend fun verifyPin(username: String, pin: String): Boolean
}
