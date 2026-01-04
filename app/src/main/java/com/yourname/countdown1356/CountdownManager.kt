package com.yourname.countdown1356

import android.content.Context
import android.content.SharedPreferences

/**
 * CountdownManager handles the core countdown logic and persistence.
 * 
 * This class is responsible for:
 * - Saving the start timestamp when the countdown begins (first launch)
 * - Calculating remaining time based on the saved start timestamp
 * - Ensuring the countdown never resets or drifts
 * 
 * The countdown duration is exactly 1356 days from the start timestamp.
 */
class CountdownManager(private val context: Context) {

    companion object {
        // SharedPreferences key for storing the start timestamp
        private const val PREFS_NAME = "Countdown1356Prefs"
        private const val KEY_START_TIME_MILLIS = "start_time_millis"
        
        // Countdown duration in milliseconds
        // 1356 days = 1356 * 24 * 60 * 60 * 1000 milliseconds
        private const val COUNTDOWN_DURATION_DAYS = 1356L
        private const val MILLIS_PER_DAY = 24L * 60 * 60 * 1000
        val COUNTDOWN_DURATION_MILLIS = COUNTDOWN_DURATION_DAYS * MILLIS_PER_DAY
    }

    private val prefs: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Initializes the countdown by saving the current timestamp if this is the first launch.
     * 
     * This method is idempotent - it only sets the start time once.
     * Subsequent calls will not change the start time.
     * 
     * @return true if this was the first launch (start time was just set)
     *         false if start time already existed
     */
    fun initializeCountdown(): Boolean {
        val startTime = getStartTimeMillis()
        return if (startTime == 0L) {
            // First launch - save current time as start time
            val currentTime = System.currentTimeMillis()
            prefs.edit().putLong(KEY_START_TIME_MILLIS, currentTime).apply()
            true
        } else {
            // Already initialized
            false
        }
    }

    /**
     * Gets the saved start timestamp.
     * 
     * @return The start timestamp in milliseconds, or 0 if not yet initialized
     */
    fun getStartTimeMillis(): Long {
        return prefs.getLong(KEY_START_TIME_MILLIS, 0L)
    }

    /**
     * Calculates the remaining time until the countdown reaches zero.
     * 
     * Formula: remaining = (startTime + 1356 days) - currentTime
     * 
     * @return Remaining time in milliseconds. Returns 0 if countdown has expired.
     */
    fun getRemainingTimeMillis(): Long {
        val startTime = getStartTimeMillis()
        if (startTime == 0L) {
            // Not initialized yet
            return COUNTDOWN_DURATION_MILLIS
        }
        
        val endTime = startTime + COUNTDOWN_DURATION_MILLIS
        val currentTime = System.currentTimeMillis()
        val remaining = endTime - currentTime
        
        // Return 0 if countdown has expired
        return maxOf(0L, remaining)
    }

    /**
     * Checks if the countdown has expired (reached zero).
     * 
     * @return true if countdown has expired, false otherwise
     */
    fun isExpired(): Boolean {
        return getRemainingTimeMillis() == 0L
    }

    /**
     * Gets the remaining time broken down into days, hours, minutes, and seconds.
     * 
     * @return CountdownData object containing the breakdown
     */
    fun getCountdownData(): CountdownData {
        val remainingMillis = getRemainingTimeMillis()
        
        val totalSeconds = remainingMillis / 1000
        val days = totalSeconds / (24 * 60 * 60)
        val hours = (totalSeconds % (24 * 60 * 60)) / (60 * 60)
        val minutes = (totalSeconds % (60 * 60)) / 60
        val seconds = totalSeconds % 60
        
        return CountdownData(
            days = days.toInt(),
            hours = hours.toInt(),
            minutes = minutes.toInt(),
            seconds = seconds.toInt(),
            totalMillis = remainingMillis
        )
    }
}

/**
 * Data class to hold countdown breakdown values.
 * 
 * This makes it easy to pass countdown information between components.
 */
data class CountdownData(
    val days: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val totalMillis: Long
)

