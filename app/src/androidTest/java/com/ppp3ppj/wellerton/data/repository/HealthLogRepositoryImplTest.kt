package com.ppp3ppj.wellerton.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ppp3ppj.wellerton.data.local.AppDatabase
import com.ppp3ppj.wellerton.data.local.entity.HealthLogEntity
import com.ppp3ppj.wellerton.data.local.entity.HealthLogStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HealthLogRepositoryImplTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: HealthLogRepositoryImpl

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = HealthLogRepositoryImpl(db.healthLogDao())
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun getLogsForDate_emptyDb_returnsEmptyList() = runTest {
        val logs = repository.getLogsForDate("2026-03-13").first()
        assertTrue(logs.isEmpty())
    }

    @Test
    fun addLog_thenGetLogsForDate_returnsInsertedLog() = runTest {
        val log = HealthLogEntity(date = "2026-03-13", timeMinutes = 360, activity = "Wakeup")
        repository.addLog(log)
        val logs = repository.getLogsForDate("2026-03-13").first()
        assertEquals(1, logs.size)
        assertEquals("Wakeup", logs[0].activity)
        assertEquals(360, logs[0].timeMinutes)
    }

    @Test
    fun updateLog_changesStatus() = runTest {
        val log = HealthLogEntity(date = "2026-03-13", timeMinutes = 360, activity = "Wakeup")
        repository.addLog(log)
        val inserted = repository.getLogsForDate("2026-03-13").first()[0]
        repository.updateLog(inserted.copy(status = HealthLogStatus.GOOD))
        val updated = repository.getLogsForDate("2026-03-13").first()[0]
        assertEquals(HealthLogStatus.GOOD, updated.status)
    }

    @Test
    fun deleteLog_removesEntry() = runTest {
        val log = HealthLogEntity(date = "2026-03-13", timeMinutes = 360, activity = "Wakeup")
        repository.addLog(log)
        val inserted = repository.getLogsForDate("2026-03-13").first()[0]
        repository.deleteLog(inserted)
        val logs = repository.getLogsForDate("2026-03-13").first()
        assertTrue(logs.isEmpty())
    }

    @Test
    fun getLogsForDate_sortedByTimeMinutes() = runTest {
        repository.addLog(HealthLogEntity(date = "2026-03-13", timeMinutes = 560, activity = "Toilet"))
        repository.addLog(HealthLogEntity(date = "2026-03-13", timeMinutes = 360, activity = "Wakeup"))
        repository.addLog(HealthLogEntity(date = "2026-03-13", timeMinutes = 1380, activity = "Sleep"))
        val logs = repository.getLogsForDate("2026-03-13").first()
        assertEquals(listOf(360, 560, 1380), logs.map { it.timeMinutes })
    }

    @Test
    fun getLogsForDate_onlyReturnsMatchingDate() = runTest {
        repository.addLog(HealthLogEntity(date = "2026-03-13", timeMinutes = 360, activity = "Wakeup"))
        repository.addLog(HealthLogEntity(date = "2026-03-14", timeMinutes = 360, activity = "Wakeup Tomorrow"))
        val logs = repository.getLogsForDate("2026-03-13").first()
        assertEquals(1, logs.size)
        assertEquals("Wakeup", logs[0].activity)
    }
}
