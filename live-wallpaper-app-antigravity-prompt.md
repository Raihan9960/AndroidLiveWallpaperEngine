# Antigravity Build Prompt — Android Live Wallpaper Engine

## 1. Core Objectives & Role

You are acting as a **senior Android software architect and engineer**, responsible for scaffolding and building a production-grade, Play Store-ready application from scratch.

**App concept:** A live wallpaper engine for Android — functionally inspired by *Lively Wallpaper* (the Windows desktop app) but redesigned for mobile constraints, and executed to a higher standard of performance and polish. The app lets users:

1. Browse and apply a curated, built-in collection of live wallpapers.
2. Import **any video, GIF, or image file** from their device and set it as a live wallpaper.
3. Have the wallpaper apply to the **home screen only** — the lock screen must remain untouched.

**Non-negotiable product pillars, in priority order:**
1. **Battery/power efficiency** — the wallpaper service runs continuously; this is the top engineering constraint, not an afterthought.
2. **Visual quality** — full-resolution, smooth playback; power savings must never come from silently degrading video quality by default.
3. **Professional, launch-ready UI/UX** — this is not a prototype or student project; it should look and feel like a polished app already live on the Play Store.

Treat all three as co-equal hard requirements. Do not trade one off against another without surfacing the tradeoff explicitly.

---

## 2. Tech Stack & Architecture

**Language:** Kotlin (official, modern Android standard; interoperates cleanly with Java if any existing Java code needs integration).

**Minimum SDK:** API 26 (Android 8.0) — required for adaptive icons and reasonable `WallpaperService` behavior. Target/compile against the latest stable SDK.

**Core Android components:**
- `WallpaperService` + `WallpaperService.Engine` — foundation for the live wallpaper itself.
- `Jetpack Compose` — all in-app UI (picker, settings, preview, onboarding). Do not use legacy XML layouts/View system for new screens.
- `Material 3` (Material You) design system as the component/theming baseline.

**Key libraries:**
- **ExoPlayer (Media3)** — video playback engine, rendered onto the wallpaper's `SurfaceHolder`. Must use hardware-accelerated decoding.
- **Coil** — image loading/decoding (thumbnails, static image wallpapers).
- **A lightweight animated-GIF decoder** compatible with Compose/Canvas rendering (evaluate `Android GIF Drawable` or Coil's GIF support) — decode once, cache frames, do not re-decode per loop.
- **Media3 Transformer** (or equivalent) for optional transcoding of oversized user-imported videos down to a sane resolution/bitrate ceiling.
- **DataStore** (not SharedPreferences) for settings persistence.
- **Hilt** for dependency injection.
- **Coroutines + Flow** for async work and state management throughout.

**Architecture pattern:** MVVM with a clean separation between:
- `presentation/` — Compose UI + ViewModels
- `domain/` — use cases, business logic, wallpaper state management
- `data/` — repositories, local asset/collection source, user-imported media source, settings storage
- `service/` — the `WallpaperService` engine implementation, isolated from UI code

**Suggested module/folder structure:**
```
app/
├── presentation/
│   ├── gallery/        (built-in collection browser)
│   ├── preview/         (live preview before applying)
│   ├── settings/
│   ├── onboarding/
│   └── theme/           (Material 3 theme, typography, color schemes)
├── domain/
│   ├── model/
│   ├── usecase/
│   └── repository/      (interfaces)
├── data/
│   ├── repository/      (implementations)
│   ├── local/            (bundled assets, Room/DataStore)
│   └── media/            (import, transcode, cache logic)
├── service/
│   ├── LiveWallpaperService.kt
│   ├── engine/           (rendering engines: video, gif, static image)
│   └── power/            (visibility/battery-state-aware playback control)
└── di/                   (Hilt modules)
```

---

## 3. Feature Specifications

### 3.1 Built-in Wallpaper Collection
- Grid gallery of curated wallpapers, each with a **live-motion thumbnail** (short looping low-res preview), not a static icon.
- Tap → opens a full **live preview** screen (phone-frame mockup showing the wallpaper actually animating) before the user commits.
- "Apply" button sets the wallpaper via `WallpaperManager`, scoped to `FLAG_SYSTEM` only (home screen).

### 3.2 Custom Import Flow
- User picks a video/image/GIF via the system picker (`ACTION_OPEN_DOCUMENT`).
- App validates format/size; if the video exceeds a defined resolution/bitrate ceiling, offer a transcode step (with a progress state) rather than playing it raw.
- Same live-preview-before-apply flow as built-in wallpapers.
- Imported wallpapers are saved to a "My Wallpapers" section for reuse without re-importing.

### 3.3 Wallpaper Rendering Engine (Service Layer)
- Single `WallpaperService.Engine` with pluggable renderers per media type (video / GIF / static image), selected based on the active wallpaper's type.
- Video renderer: ExoPlayer output surface bound directly to the engine's `SurfaceHolder`; playback looped seamlessly.
- Frame rate capped at 24–30fps by default (configurable in advanced settings, not the main flow).
- GIF/static: decode once, cache bitmap(s), avoid redundant decode work per frame/loop.

### 3.4 Settings Screen
- Frame rate cap (with sane presets, not raw numeric input for average users)
- Battery-saver adaptive behavior toggle (on by default): switches to a static frame or reduced frame rate automatically when `PowerManager.isPowerSaveMode()` is true.
- Storage management (view/clear cached transcoded files)
- About/permissions info

### 3.5 Onboarding & Permissions
- First-launch flow explains *why* storage/media permissions are needed before requesting them (no blind permission dialogs).
- Clear empty state if no wallpaper is yet applied.

---

## 4. Implementation Constraints

**Power/performance (hard requirements):**
- `onVisibilityChanged(false)` must fully stop the active renderer (release/pause the player, stop the render loop) — not just mute or hide output. Resume cleanly on `true`.
- No wake locks held during normal playback.
- Hardware-accelerated video decode only; verify ExoPlayer is not silently falling back to software decode on target test devices.
- Respect Doze/App Standby; no background work outside what's required for the active wallpaper surface.

**Quality constraints:**
- Bundled default wallpapers: full device-resolution assets, clean loop points, reasonable file size (optimize compression without visible quality loss).
- User-imported video transcoding ceiling should target parity with typical phone display resolution (e.g. ~1080p), not an aggressive downscale.

**Code quality:**
- Kotlin idiomatic style, consistent with official Android Kotlin style guide.
- Full separation of concerns per the architecture above — no business logic inside Composables or the wallpaper `Engine` class directly.
- Unit tests for domain/use-case logic; instrumented tests for the wallpaper service lifecycle (visibility changes, surface creation/destruction).

**Error handling:**
- Graceful handling of: corrupted/unsupported media on import, transcode failure, insufficient storage, permission denial, surface creation failure.
- User-facing error states must be clear and actionable (not raw stack traces or silent failures).

**UI/UX bar:**
- Full Material 3 theming, light/dark mode support.
- Loading/empty/error states designed for every screen, not just the happy path.
- App icon (adaptive icon spec) and splash screen included.

---

## 5. Execution Steps (Incremental Build Phases)

Build and verify each phase before moving to the next. Do not skip ahead.

**Phase 1 — Project Scaffold**
Set up the project structure above, Hilt DI, base Compose theme (Material 3, light/dark), navigation graph with placeholder screens for Gallery, Preview, Settings, Onboarding.

**Phase 2 — Wallpaper Service Core**
Implement `LiveWallpaperService` with a minimal video renderer (ExoPlayer bound to `SurfaceHolder`), hardcoded to a single test video. Verify: sets correctly via `WallpaperManager` with `FLAG_SYSTEM` only, loops cleanly, and correctly stops rendering in `onVisibilityChanged(false)`.

**Phase 3 — Power Management Layer**
Add the visibility-aware pause/resume logic, frame-rate capping, and `PowerManager.isPowerSaveMode()` adaptive behavior. Verify with battery profiling (Android Studio Profiler) that CPU/GPU usage drops to near-zero when the wallpaper is not visible.

**Phase 4 — GIF & Static Image Renderers**
Add the remaining renderer types to the engine, selected dynamically by media type.

**Phase 5 — Built-in Collection & Gallery UI**
Implement the gallery grid with live-motion thumbnails, the live preview screen (phone-frame mockup), and the apply flow, wired to the Phase 2–4 engine.

**Phase 6 — Custom Import Flow**
Implement file picker integration, validation, optional transcoding with progress UI, and "My Wallpapers" persistence.

**Phase 7 — Settings & Onboarding**
Build out the settings screen and first-launch permission-explanation flow.

**Phase 8 — Polish & Hardening**
Error states across all screens, adaptive icon/splash, accessibility pass (content descriptions, touch targets), final performance profiling pass on at least one mid-range and one low-end test device profile.

**Phase 9 — Release Prep**
Signing config, ProGuard/R8 rules verified against no functionality breakage, Play Store listing assets checklist (screenshots, description, privacy policy for storage/media access).

---

*End of build prompt.*
