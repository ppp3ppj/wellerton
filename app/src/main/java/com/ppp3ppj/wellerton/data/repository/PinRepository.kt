package com.ppp3ppj.wellerton.data.repository

interface PinRepository {
    suspend fun savePin(pin: String)
    suspend fun isPinSet(): Boolean
    suspend fun verifyPin(pin: String): Boolean
}
