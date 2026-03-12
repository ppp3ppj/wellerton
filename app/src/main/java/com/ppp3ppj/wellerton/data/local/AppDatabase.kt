package com.ppp3ppj.wellerton.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ppp3ppj.wellerton.data.local.dao.PinDao
import com.ppp3ppj.wellerton.data.local.entity.PinEntity

@Database(entities = [PinEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pinDao(): PinDao
}
