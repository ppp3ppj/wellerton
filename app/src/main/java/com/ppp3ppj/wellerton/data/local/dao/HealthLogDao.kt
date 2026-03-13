package com.ppp3ppj.wellerton.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ppp3ppj.wellerton.data.local.entity.HealthLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthLogDao {
    @Query("SELECT * FROM health_logs WHERE date = :date ORDER BY timeMinutes ASC")
    fun getLogsForDate(date: String): Flow<List<HealthLogEntity>>

    @Query("SELECT * FROM health_logs WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): HealthLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: HealthLogEntity)

    @Update
    suspend fun update(log: HealthLogEntity)

    @Delete
    suspend fun delete(log: HealthLogEntity)
}
