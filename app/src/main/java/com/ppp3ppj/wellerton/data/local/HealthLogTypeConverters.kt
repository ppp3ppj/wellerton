package com.ppp3ppj.wellerton.data.local

import androidx.room.TypeConverter
import com.ppp3ppj.wellerton.data.local.entity.HealthLogActivityType
import com.ppp3ppj.wellerton.data.local.entity.HealthLogStatus

class HealthLogTypeConverters {
    @TypeConverter
    fun fromStatus(status: HealthLogStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): HealthLogStatus = HealthLogStatus.valueOf(value)

    @TypeConverter
    fun fromActivityType(type: HealthLogActivityType): String = type.name

    @TypeConverter
    fun toActivityType(value: String): HealthLogActivityType =
        HealthLogActivityType.entries.find { it.name == value } ?: HealthLogActivityType.OTHER
}
