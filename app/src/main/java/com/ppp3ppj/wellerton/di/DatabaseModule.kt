package com.ppp3ppj.wellerton.di

import android.content.Context
import androidx.room.Room
import com.ppp3ppj.wellerton.data.local.AppDatabase
import com.ppp3ppj.wellerton.data.local.dao.HealthLogDao
import com.ppp3ppj.wellerton.data.local.dao.UserDao
import com.ppp3ppj.wellerton.data.repository.HealthLogRepository
import com.ppp3ppj.wellerton.data.repository.HealthLogRepositoryImpl
import com.ppp3ppj.wellerton.data.repository.UserRepository
import com.ppp3ppj.wellerton.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "wellerton.db")
            .addCallback(AppDatabase.seedCallback)
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideHealthLogDao(db: AppDatabase): HealthLogDao = db.healthLogDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindHealthLogRepository(impl: HealthLogRepositoryImpl): HealthLogRepository
}
