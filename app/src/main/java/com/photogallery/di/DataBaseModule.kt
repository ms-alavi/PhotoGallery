package com.photogallery.di

import android.app.Application
import androidx.room.Room
import com.photogallery.model.local.AppDataBase
import com.photogallery.model.local.PhotoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataBaseModule {

    @Provides
    @Singleton
    internal fun provideAppDatabase(application: Application): AppDataBase {
        return Room.databaseBuilder(
            application,
            AppDataBase::class.java,
            AppDataBase.DB_NAME
        ).build()
    }

    @Provides
    internal fun providePhotoDao(appDatabase: AppDataBase): PhotoDao {
        return appDatabase.photoDao
    }
}