package com.ppp3ppj.wellerton.data.repository

import com.ppp3ppj.wellerton.data.local.dao.HealthLogDao
import com.ppp3ppj.wellerton.data.local.entity.HealthLogEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HealthLogRepositoryImpl @Inject constructor(
    private val dao: HealthLogDao
) : HealthLogRepository {
    override fun getLogsForDate(date: String): Flow<List<HealthLogEntity>> = dao.getLogsForDate(date)
    override suspend fun getLogById(id: Int): HealthLogEntity? = dao.findById(id)
    override suspend fun addLog(log: HealthLogEntity) = dao.insert(log)
    override suspend fun updateLog(log: HealthLogEntity) = dao.update(log)
    override suspend fun deleteLog(log: HealthLogEntity) = dao.delete(log)
}
