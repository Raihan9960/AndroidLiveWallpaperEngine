package com.antigravity.livewallpaper

import com.antigravity.livewallpaper.domain.model.BatteryImpact
import com.antigravity.livewallpaper.domain.model.Wallpaper
import com.antigravity.livewallpaper.domain.model.WallpaperType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WallpaperModelTest {

    @Test
    fun testWallpaperCreation() {
        val wallpaper = Wallpaper(
            id = "test_wp_1",
            title = "Cyber Wave",
            type = WallpaperType.VIDEO,
            sourceUri = "asset:///wallpapers/test.mp4",
            thumbnailUri = "asset:///wallpapers/test.jpg",
            isBuiltIn = true,
            resolution = "1080x1920",
            batteryImpact = BatteryImpact.LOW
        )

        assertEquals("test_wp_1", wallpaper.id)
        assertEquals("Cyber Wave", wallpaper.title)
        assertEquals(WallpaperType.VIDEO, wallpaper.type)
        assertTrue(wallpaper.isBuiltIn)
        assertEquals(BatteryImpact.LOW, wallpaper.batteryImpact)
    }

    @Test
    fun testBatteryImpactGrades() {
        assertEquals("A+", BatteryImpact.ULTRA_LOW.grade)
        assertEquals("A", BatteryImpact.LOW.grade)
        assertEquals("B+", BatteryImpact.BALANCED.grade)
        assertEquals("B", BatteryImpact.PERFORMANCE.grade)
    }
}
