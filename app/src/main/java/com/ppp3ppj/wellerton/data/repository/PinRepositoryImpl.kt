package com.ppp3ppj.wellerton.data.repository

import com.ppp3ppj.wellerton.data.local.dao.PinDao
import com.ppp3ppj.wellerton.data.local.entity.PinEntity
import java.security.MessageDigest
import javax.inject.Inject

class PinRepositoryImpl @Inject constructor(
    private val pinDao: PinDao
) : PinRepository {

    override suspend fun savePin(pin: String) {
        pinDao.savePin(PinEntity(hash = pin.sha256()))
    }

    override suspend fun isPinSet(): Boolean {
        return pinDao.getPin() != null
    }

    override suspend fun verifyPin(pin: String): Boolean {
        val stored = pinDao.getPin() ?: return false
        return stored.hash == pin.sha256()
    }

    private fun String.sha256(): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
