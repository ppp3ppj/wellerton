package com.ppp3ppj.wellerton.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ppp3ppj.wellerton.data.local.AppDatabase
import com.ppp3ppj.wellerton.data.local.entity.UserEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.security.MessageDigest

@RunWith(AndroidJUnit4::class)
class UserRepositoryImplTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = UserRepositoryImpl(db.userDao())
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun findUserByPin_emptyDb_returnsNull() = runTest {
        assertNull(repository.findUserByPin("000000"))
    }

    @Test
    fun findUserByPin_correctPin_returnsUsername() = runTest {
        db.userDao().insert(UserEntity(name = "admin", pinHash = sha256("000000")))
        assertEquals("admin", repository.findUserByPin("000000"))
    }

    @Test
    fun findUserByPin_wrongPin_returnsNull() = runTest {
        db.userDao().insert(UserEntity(name = "admin", pinHash = sha256("000000")))
        assertNull(repository.findUserByPin("123456"))
    }

    @Test
    fun findUserByPin_multipleUsers_returnsCorrectOne() = runTest {
        db.userDao().insert(UserEntity(name = "admin", pinHash = sha256("000000")))
        db.userDao().insert(UserEntity(name = "alice", pinHash = sha256("111111")))
        assertEquals("alice", repository.findUserByPin("111111"))
        assertEquals("admin", repository.findUserByPin("000000"))
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
