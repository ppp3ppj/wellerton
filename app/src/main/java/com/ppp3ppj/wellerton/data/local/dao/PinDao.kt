package com.ppp3ppj.wellerton.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ppp3ppj.wellerton.data.local.entity.PinEntity

@Dao
interface PinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePin(pin: PinEntity)

    @Query("SELECT * FROM pin WHERE id = 1")
    suspend fun getPin(): PinEntity?
}
