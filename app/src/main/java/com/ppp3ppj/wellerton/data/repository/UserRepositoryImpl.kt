package com.ppp3ppj.wellerton.data.repository

import com.ppp3ppj.wellerton.data.local.dao.UserDao
import java.security.MessageDigest
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun findUserByPin(pin: String): String? =
        userDao.findByPinHash(pin.sha256())?.name

    private fun String.sha256(): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
