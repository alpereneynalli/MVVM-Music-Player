package com.player

import android.content.Context
import com.google.firebase.storage.FirebaseStorage
import com.player.repository.MusicRepository
import com.player.roomdb.AppDatabase
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
    fun provideMusicRepository(
        appDatabase: AppDatabase,
        firebaseStorage: FirebaseStorage,
        @ApplicationContext context: Context
    ): MusicRepository {
        return MusicRepository(appDatabase, firebaseStorage, context)
    }
}