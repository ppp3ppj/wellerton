package com.ppp3ppj.wellerton.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_logs")
data class HealthLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val timeMinutes: Int,
    val activity: String,
    val status: HealthLogStatus = HealthLogStatus.UNRATED,
    val type: HealthLogActivityType = HealthLogActivityType.OTHER
)
