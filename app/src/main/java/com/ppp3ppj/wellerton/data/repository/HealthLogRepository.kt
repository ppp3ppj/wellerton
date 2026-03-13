package com.ppp3ppj.wellerton.data.repository

import com.ppp3ppj.wellerton.data.local.entity.HealthLogEntity
import kotlinx.coroutines.flow.Flow

interface HealthLogRepository {
    fun getLogsForDate(date: String): Flow<List<HealthLogEntity>>
    suspend fun getLogById(id: Int): HealthLogEntity?
    suspend fun addLog(log: HealthLogEntity)
    suspend fun updateLog(log: HealthLogEntity)
    suspend fun deleteLog(log: HealthLogEntity)
}
