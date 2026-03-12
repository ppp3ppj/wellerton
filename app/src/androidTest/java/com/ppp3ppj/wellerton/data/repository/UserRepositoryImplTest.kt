package com.ppp3ppj.wellerton.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ppp3ppj.wellerton.data.local.AppDatabase
import com.ppp3ppj.wellerton.data.local.entity.UserEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
    fun getCurrentUsername_emptyDb_returnsNull() = runTest {
        assertNull(repository.getCurrentUsername())
    }

    @Test
    fun getCurrentUsername_returnsFirstUserName() = runTest {
        db.userDao().insert(UserEntity(name = "admin", pinHash = sha256("000000")))
        assertEquals("admin", repository.getCurrentUsername())
    }

    @Test
    fun verifyPin_correctPin_returnsTrue() = runTest {
        db.userDao().insert(UserEntity(name = "admin", pinHash = sha256("000000")))
        assertTrue(repository.verifyPin("admin", "000000"))
    }

    @Test
    fun verifyPin_wrongPin_returnsFalse() = runTest {
        db.userDao().insert(UserEntity(name = "admin", pinHash = sha256("000000")))
        assertFalse(repository.verifyPin("admin", "123456"))
    }

    @Test
    fun verifyPin_unknownUser_returnsFalse() = runTest {
        assertFalse(repository.verifyPin("nobody", "000000"))
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
