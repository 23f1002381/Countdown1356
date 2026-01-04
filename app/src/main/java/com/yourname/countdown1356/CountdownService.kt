package com.yourname.countdown1356

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * CountdownService is a ForegroundService that runs the countdown continuously.
 * 
 * WHY FOREGROUND SERVICE?
 * - Android restricts background work to prevent battery drain
 * - Foreground services can run indefinitely with a visible notification
 * - This is Play Store compliant when properly declared and justified
 * - Required for countdown that must continue when app is closed
 * 
 * This service:
 * - Runs in the foreground with a persistent notification
 * - Updates the notification every minute (battery efficient)
 * - Broadcasts countdown updates for UI components
 * - Survives app closure and process death
 * - Automatically restarts on device reboot (via BootReceiver)
 */
class CountdownService : Service() {

    private val binder = LocalBinder()
    private val countdownManager: CountdownManager by lazy { CountdownManager(this) }
    
    // Handler for updating notification (runs on main thread)
    private val handler = Handler(Looper.getMainLooper())
    
    // Runnable that updates the notification every minute
    private val notificationUpdateRunnable = object : Runnable {
        override fun run() {
            updateNotification()
            // Schedule next update in 60 seconds (1 minute)
            handler.postDelayed(this, 60_000L)
        }
    }

    // Callback for UI updates (set by MainActivity)
    var countdownUpdateCallback: ((CountdownData) -> Unit)? = null
    
    // Handler for UI updates (every second)
    private val uiUpdateHandler = Handler(Looper.getMainLooper())
    private val uiUpdateRunnable = object : Runnable {
        override fun run() {
            val data = countdownManager.getCountdownData()
            countdownUpdateCallback?.invoke(data)
            // Schedule next UI update in 1 second
            uiUpdateHandler.postDelayed(this, 1_000L)
        }
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "countdown_channel"
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_NAME = "Countdown Timer"
    }

    /**
     * Inner class for local binding (allows MainActivity to bind to this service)
     */
    inner class LocalBinder : Binder() {
        fun getService(): CountdownService = this@CountdownService
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize countdown on service creation
        countdownManager.initializeCountdown()
        // Create notification channel (required for Android 8.0+)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start as foreground service with notification
        startForeground(NOTIFICATION_ID, createNotification())
        
        // Start updating notification every minute
        handler.post(notificationUpdateRunnable)
        
        // Start updating UI every second (if callback is set)
        uiUpdateHandler.post(uiUpdateRunnable)
        
        // Return START_STICKY to restart service if killed by system
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop all handlers when service is destroyed
        handler.removeCallbacks(notificationUpdateRunnable)
        uiUpdateHandler.removeCallbacks(uiUpdateRunnable)
    }

    /**
     * Creates the notification channel (required for Android 8.0+).
     * 
     * Notification channels allow users to control notification behavior.
     * This is a Play Store requirement.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW // Low importance = no sound/vibration
            ).apply {
                description = getString(R.string.notification_channel_description)
                setShowBadge(false)
                // Make notification non-dismissible
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Creates the foreground notification.
     * 
     * WHY THIS NOTIFICATION?
     * - Required by Android for foreground services
     * - Informs users that countdown is running
     * - Play Store compliant when properly explained
     * - Non-dismissible to ensure countdown visibility
     */
    private fun createNotification(): Notification {
        val data = countdownManager.getCountdownData()
        
        // Intent to open app when notification is tapped
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationText = if (data.days > 0) {
            "${data.days} ${getString(R.string.days_label)} remaining"
        } else {
            getString(R.string.notification_text)
        }

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(notificationText)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // Makes notification non-dismissible
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    /**
     * Updates the notification with current countdown data.
     * 
     * Called every minute to keep notification current without excessive battery usage.
     */
    private fun updateNotification() {
        val notification = createNotification()
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    /**
     * Gets current countdown data (for UI binding).
     */
    fun getCurrentCountdownData(): CountdownData {
        return countdownManager.getCountdownData()
    }
}

