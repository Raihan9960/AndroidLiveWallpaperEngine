package com.antigravity.livewallpaper.domain.usecase

import android.net.Uri
import com.antigravity.livewallpaper.domain.model.Wallpaper
import com.antigravity.livewallpaper.domain.repository.WallpaperRepository
import javax.inject.Inject

class ImportMediaUseCase @Inject constructor(
    private val repository: WallpaperRepository
) {
    suspend operator fun invoke(uri: Uri, title: String? = null): Result<Wallpaper> {
        return repository.importMedia(uri, title)
    }
}
