package com.antigravity.livewallpaper.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.antigravity.livewallpaper.domain.model.AppSettings
import com.antigravity.livewallpaper.domain.model.Wallpaper
import com.antigravity.livewallpaper.domain.model.WallpaperType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "live_wallpaper_prefs")

@Singleton
class WallpaperPreferences @Inject constructor(
    private val context: Context
) {
    companion object {
        val KEY_ACTIVE_WALLPAPER_ID = stringPreferencesKey("active_wallpaper_id")
        val KEY_ACTIVE_WALLPAPER_TITLE = stringPreferencesKey("active_wallpaper_title")
        val KEY_ACTIVE_WALLPAPER_TYPE = stringPreferencesKey("active_wallpaper_type")
        val KEY_ACTIVE_WALLPAPER_URI = stringPreferencesKey("active_wallpaper_uri")
        val KEY_ACTIVE_WALLPAPER_THUMB = stringPreferencesKey("active_wallpaper_thumb")

        val KEY_FRAME_RATE_CAP = intPreferencesKey("frame_rate_cap")
        val KEY_BATTERY_SAVER_ADAPTIVE = booleanPreferencesKey("battery_saver_adaptive")
        val KEY_PARALLAX_ENABLED = booleanPreferencesKey("parallax_enabled")
        val KEY_TRANSCODE_CEILING = booleanPreferencesKey("transcode_ceiling_1080p")
        val KEY_ONBOARDING_DONE = booleanPreferencesKey("has_completed_onboarding")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            frameRateCap = prefs[KEY_FRAME_RATE_CAP] ?: 30,
            batterySaverAdaptive = prefs[KEY_BATTERY_SAVER_ADAPTIVE] ?: true,
            parallaxEnabled = prefs[KEY_PARALLAX_ENABLED] ?: true,
            transcodeCeiling1080p = prefs[KEY_TRANSCODE_CEILING] ?: true,
            hasCompletedOnboarding = prefs[KEY_ONBOARDING_DONE] ?: false,
            activeWallpaperId = prefs[KEY_ACTIVE_WALLPAPER_ID]
        )
    }

    val activeWallpaperFlow: Flow<Wallpaper?> = context.dataStore.data.map { prefs ->
        val id = prefs[KEY_ACTIVE_WALLPAPER_ID] ?: return@map null
        val title = prefs[KEY_ACTIVE_WALLPAPER_TITLE] ?: "Default Wallpaper"
        val typeStr = prefs[KEY_ACTIVE_WALLPAPER_TYPE] ?: WallpaperType.VIDEO.name
        val uri = prefs[KEY_ACTIVE_WALLPAPER_URI] ?: ""
        val thumb = prefs[KEY_ACTIVE_WALLPAPER_THUMB] ?: uri

        val type = try {
            WallpaperType.valueOf(typeStr)
        } catch (e: Exception) {
            WallpaperType.VIDEO
        }

        Wallpaper(
            id = id,
            title = title,
            type = type,
            sourceUri = uri,
            thumbnailUri = thumb,
            isBuiltIn = uri.startsWith("asset:///")
        )
    }

    suspend fun setActiveWallpaper(wallpaper: Wallpaper) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACTIVE_WALLPAPER_ID] = wallpaper.id
            prefs[KEY_ACTIVE_WALLPAPER_TITLE] = wallpaper.title
            prefs[KEY_ACTIVE_WALLPAPER_TYPE] = wallpaper.type.name
            prefs[KEY_ACTIVE_WALLPAPER_URI] = wallpaper.sourceUri
            prefs[KEY_ACTIVE_WALLPAPER_THUMB] = wallpaper.thumbnailUri
        }
    }

    suspend fun updateFrameRateCap(fps: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_FRAME_RATE_CAP] = fps
        }
    }

    suspend fun updateBatterySaverAdaptive(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_BATTERY_SAVER_ADAPTIVE] = enabled
        }
    }

    suspend fun updateParallaxEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_PARALLAX_ENABLED] = enabled
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_DONE] = completed
        }
    }
}
