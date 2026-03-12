package com.ppp3ppj.wellerton.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pin")
data class PinEntity(
    @PrimaryKey val id: Int = 1,
    val hash: String
)
