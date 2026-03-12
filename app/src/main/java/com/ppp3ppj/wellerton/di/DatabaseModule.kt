package com.ppp3ppj.wellerton.di

import android.content.Context
import androidx.room.Room
import com.ppp3ppj.wellerton.data.local.AppDatabase
import com.ppp3ppj.wellerton.data.local.dao.PinDao
import com.ppp3ppj.wellerton.data.repository.PinRepository
import com.ppp3ppj.wellerton.data.repository.PinRepositoryImpl
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
        Room.databaseBuilder(context, AppDatabase::class.java, "wellerton.db").build()

    @Provides
    fun providePinDao(db: AppDatabase): PinDao = db.pinDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPinRepository(impl: PinRepositoryImpl): PinRepository
}
