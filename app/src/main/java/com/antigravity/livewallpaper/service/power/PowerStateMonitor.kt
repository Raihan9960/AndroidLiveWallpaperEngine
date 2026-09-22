package com.antigravity.livewallpaper.service.power

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PowerStateMonitor(
    private val context: Context,
    private val onPowerStateChanged: (Boolean) -> Unit
) {
    companion object {
        private const val TAG = "PowerStateMonitor"
    }

    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
    private val _isPowerSaveMode = MutableStateFlow(checkPowerSaveMode())
    val isPowerSaveMode: StateFlow<Boolean> = _isPowerSaveMode.asStateFlow()

    private var isReceiverRegistered = false

    private val powerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val isSaveMode = checkPowerSaveMode()
            Log.d(TAG, "Power save state changed: $isSaveMode")
            _isPowerSaveMode.value = isSaveMode
            onPowerStateChanged(isSaveMode)
        }
    }

    private fun checkPowerSaveMode(): Boolean {
        return powerManager?.isPowerSaveMode ?: false
    }

    fun start() {
        if (isReceiverRegistered) return
        try {
            val filter = IntentFilter().apply {
                addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
                addAction(Intent.ACTION_BATTERY_LOW)
                addAction(Intent.ACTION_BATTERY_OKAY)
            }
            context.registerReceiver(powerReceiver, filter)
            isReceiverRegistered = true

            // Trigger initial state
            val initialState = checkPowerSaveMode()
            _isPowerSaveMode.value = initialState
            onPowerStateChanged(initialState)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register power receiver", e)
        }
    }

    fun stop() {
        if (!isReceiverRegistered) return
        try {
            context.unregisterReceiver(powerReceiver)
            isReceiverRegistered = false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unregister power receiver", e)
        }
    }
}
