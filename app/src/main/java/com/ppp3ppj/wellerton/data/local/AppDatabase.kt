package com.ppp3ppj.wellerton.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ppp3ppj.wellerton.data.local.dao.HealthLogDao
import com.ppp3ppj.wellerton.data.local.dao.UserDao
import com.ppp3ppj.wellerton.data.local.entity.HealthLogEntity
import com.ppp3ppj.wellerton.data.local.entity.UserEntity
import java.security.MessageDigest

@Database(entities = [UserEntity::class, HealthLogEntity::class], version = 3, exportSchema = false)
@TypeConverters(HealthLogTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun healthLogDao(): HealthLogDao

    companion object {
        val seedCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                val hash = sha256("000000")
                db.execSQL("INSERT INTO users (name, pin_hash) VALUES ('admin', '$hash')")
            }

            private fun sha256(input: String): String {
                val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
                return bytes.joinToString("") { "%02x".format(it) }
            }
        }
    }
}
