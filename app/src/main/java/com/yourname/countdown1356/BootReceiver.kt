package com.yourname.countdown1356

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

/**
 * BootReceiver handles device reboot events.
 * 
 * WHY THIS RECEIVER?
 * - When device reboots, all services are stopped
 * - We need to restart CountdownService automatically
 * - This ensures countdown continues after reboot
 * - Play Store compliant when properly declared in manifest
 * 
 * This receiver:
 * - Listens for BOOT_COMPLETED broadcast
 * - Restarts CountdownService when device boots
 * - Ensures countdown never stops, even after reboot
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Only handle BOOT_COMPLETED action
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            
            // Start the countdown service
            val serviceIntent = Intent(context, CountdownService::class.java)
            
            // Use startForegroundService for Android 8.0+ to ensure foreground service starts
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                // For older Android versions, use startService
                context.startService(serviceIntent)
            }
        }
    }
}

