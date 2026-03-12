package com.ppp3ppj.wellerton.data.repository

import com.ppp3ppj.wellerton.data.local.dao.UserDao
import java.security.MessageDigest
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun getCurrentUsername(): String? = userDao.getFirst()?.name

    override suspend fun verifyPin(username: String, pin: String): Boolean {
        val user = userDao.findByName(username) ?: return false
        return user.pinHash == pin.sha256()
    }

    private fun String.sha256(): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
