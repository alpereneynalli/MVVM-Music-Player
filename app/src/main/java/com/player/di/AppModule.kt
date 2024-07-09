package com.player.di

import android.content.Context
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.player.data.repository.MusicRepositoryImpl
import com.player.data.roomdb.AppDatabase
import com.player.data.util.FirebaseDataSource
import com.player.domain.MusicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideFirebaseDataSource(
        firebaseStorage: FirebaseStorage,
        @ApplicationContext context: Context
    ): FirebaseDataSource {
        return FirebaseDataSource(firebaseStorage, context)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideMusicRepository(
        appDatabase: AppDatabase,
        firebaseDataSource: FirebaseDataSource,
        gson: Gson
    ): MusicRepository {
        return MusicRepositoryImpl(appDatabase, firebaseDataSource, gson)
    }
}