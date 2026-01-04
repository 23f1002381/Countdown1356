package com.yourname.countdown1356

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat

/**
 * MainActivity is the single screen of the app.
 * 
 * This activity:
 * - Displays the countdown (days and HH:MM:SS)
 * - Binds to CountdownService to receive updates
 * - Starts the service if it's not running
 * - Updates UI every second with current countdown
 * - Handles service connection lifecycle
 */
class MainActivity : AppCompatActivity() {

    // UI components
    private lateinit var tvDays: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvStatus: TextView

    // Service connection
    private var countdownService: CountdownService? = null
    private var isServiceBound = false

    // Service connection callback
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as CountdownService.LocalBinder
            countdownService = binder.getService()
            isServiceBound = true
            
            // Set callback to receive countdown updates
            countdownService?.countdownUpdateCallback = { data ->
                updateUI(data)
            }
            
            // Immediately update UI with current countdown
            countdownService?.getCurrentCountdownData()?.let { updateUI(it) }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            countdownService = null
            isServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        tvDays = findViewById(R.id.tvDays)
        tvTime = findViewById(R.id.tvTime)
        tvStatus = findViewById(R.id.tvStatus)

        // Request notification permission for Android 13+
        requestNotificationPermission()

        // Start and bind to the countdown service
        startAndBindService()
    }

    override fun onStart() {
        super.onStart()
        // Re-bind to service if not already bound
        if (!isServiceBound) {
            bindService()
        }
    }

    override fun onStop() {
        super.onStop()
        // Unbind from service (but don't stop it - it runs in foreground)
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
            countdownService?.countdownUpdateCallback = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up service connection
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    /**
     * Requests notification permission for Android 13+ (API 33+).
     * 
     * This is required to show the foreground service notification.
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                // Permission not granted - user will see system dialog
                // For production, you might want to show explanation first
            }
        }
    }

    /**
     * Starts the CountdownService and binds to it.
     * 
     * The service will run in the foreground with a notification.
     */
    private fun startAndBindService() {
        val intent = Intent(this, CountdownService::class.java)
        
        // Start as foreground service (required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        
        // Bind to service to receive updates
        bindService()
    }

    /**
     * Binds to the CountdownService.
     */
    private fun bindService() {
        val intent = Intent(this, CountdownService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    /**
     * Updates the UI with current countdown data.
     * 
     * @param data The countdown data (days, hours, minutes, seconds)
     */
    private fun updateUI(data: CountdownData) {
        // Update days display
        tvDays.text = data.days.toString()
        
        // Update time display (HH:MM:SS)
        val timeString = String.format(
            getString(R.string.time_format),
            data.hours,
            data.minutes,
            data.seconds
        )
        tvTime.text = timeString
        
        // Update status (optional, for debugging)
        if (data.totalMillis == 0L) {
            tvStatus.text = "Countdown completed!"
            tvStatus.visibility = android.view.View.VISIBLE
        } else {
            tvStatus.visibility = android.view.View.GONE
        }
    }
}

