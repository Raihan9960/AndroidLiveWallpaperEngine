package com.antigravity.livewallpaper.di

import android.content.Context
import com.antigravity.livewallpaper.data.local.MediaStorageManager
import com.antigravity.livewallpaper.data.local.WallpaperPreferences
import com.antigravity.livewallpaper.data.repository.WallpaperRepositoryImpl
import com.antigravity.livewallpaper.domain.repository.WallpaperRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindWallpaperRepository(
        impl: WallpaperRepositoryImpl
    ): WallpaperRepository

    companion object {
        @Provides
        @Singleton
        fun provideWallpaperPreferences(
            @ApplicationContext context: Context
        ): WallpaperPreferences {
            return WallpaperPreferences(context)
        }

        @Provides
        @Singleton
        fun provideMediaStorageManager(
            @ApplicationContext context: Context
        ): MediaStorageManager {
            return MediaStorageManager(context)
        }
    }
}
