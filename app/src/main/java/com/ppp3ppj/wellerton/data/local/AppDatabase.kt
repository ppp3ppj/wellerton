package com.ppp3ppj.wellerton.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ppp3ppj.wellerton.data.local.dao.UserDao
import com.ppp3ppj.wellerton.data.local.entity.UserEntity
import java.security.MessageDigest

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

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
