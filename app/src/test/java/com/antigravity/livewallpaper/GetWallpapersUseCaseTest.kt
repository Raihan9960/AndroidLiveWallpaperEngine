package com.antigravity.livewallpaper

import com.antigravity.livewallpaper.domain.model.Wallpaper
import com.antigravity.livewallpaper.domain.model.WallpaperType
import com.antigravity.livewallpaper.domain.repository.WallpaperRepository
import com.antigravity.livewallpaper.domain.usecase.GetWallpapersUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetWallpapersUseCaseTest {

    private val fakeWallpaper = Wallpaper(
        id = "mock_1",
        title = "Mock Wallpaper",
        type = WallpaperType.IMAGE,
        sourceUri = "mock://uri",
        thumbnailUri = "mock://thumb"
    )

    private val fakeRepository = object : WallpaperRepository {
        override fun getBuiltInWallpapers() = flowOf(listOf(fakeWallpaper))
        override fun getMyWallpapers() = flowOf(emptyList<Wallpaper>())
        override fun getActiveWallpaper() = flowOf(fakeWallpaper)
        override fun getSettings() = flowOf(com.antigravity.livewallpaper.domain.model.AppSettings())
        override suspend fun setActiveWallpaper(wallpaper: Wallpaper) {}
        override suspend fun importMedia(uri: android.net.Uri, title: String?) = Result.success(fakeWallpaper)
        override suspend fun deleteCustomWallpaper(id: String) = true
        override suspend fun updateFrameRateCap(fps: Int) {}
        override suspend fun updateBatterySaverAdaptive(enabled: Boolean) {}
        override suspend fun updateParallaxEnabled(enabled: Boolean) {}
        override suspend fun setOnboardingCompleted(completed: Boolean) {}
        override suspend fun getCacheSizeBytes() = 0L
        override suspend fun clearCache() = true
    }

    @Test
    fun testGetBuiltInReturnsList() = runTest {
        val useCase = GetWallpapersUseCase(fakeRepository)
        val wallpapers = useCase.getBuiltIn().first()

        assertEquals(1, wallpapers.size)
        assertEquals("mock_1", wallpapers[0].id)
    }

    @Test
    fun testGetActiveWallpaper() = runTest {
        val useCase = GetWallpapersUseCase(fakeRepository)
        val active = useCase.getActiveWallpaper().first()

        assertEquals("Mock Wallpaper", active?.title)
    }
}
