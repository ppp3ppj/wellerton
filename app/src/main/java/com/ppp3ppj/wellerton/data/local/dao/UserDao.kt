package com.ppp3ppj.wellerton.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ppp3ppj.wellerton.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM users WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getFirst(): UserEntity?
}
