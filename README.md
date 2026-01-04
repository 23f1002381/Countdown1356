# 1356 – The Countdown

A persistent Android countdown app that tracks a 1356-day countdown timer.

## 📱 Overview

This app displays a countdown timer that:
- **Never resets** - The countdown starts once and continues forever
- **Runs continuously** - Works even when the app is closed, screen is locked, or device reboots
- **Play Store compliant** - Uses proper foreground service with notification
- **Battery efficient** - Updates notification every minute, UI every second

## 🏗️ Architecture

### Core Components

1. **CountdownManager.kt**
   - Manages countdown logic and persistence
   - Saves start timestamp to SharedPreferences
   - Calculates remaining time: `startTime + 1356 days - currentTime`

2. **CountdownService.kt**
   - Foreground service that runs continuously
   - Shows persistent notification
   - Updates notification every minute
   - Broadcasts countdown updates to UI

3. **BootReceiver.kt**
   - Listens for device reboot events
   - Automatically restarts CountdownService after reboot

4. **MainActivity.kt**
   - Single-screen UI
   - Displays days and HH:MM:SS
   - Binds to service to receive updates
   - Updates UI every second

## 🔧 Setup Instructions

### Prerequisites
- Android Studio (latest version)
- Android SDK with API 26+ (Android 8.0)
- Gradle 8.0+

### Build Steps

1. **Open Project**
   ```
   Open Android Studio → Open Project → Select this directory
   ```

2. **Sync Gradle**
   ```
   Android Studio will automatically sync, or click "Sync Now"
   ```

3. **Add App Icons** (Required)
   - Go to `app/src/main/res/`
   - Create `mipmap-hdpi`, `mipmap-mdpi`, `mipmap-xhdpi`, `mipmap-xxhdpi`, `mipmap-xxxhdpi` folders
   - Add `ic_launcher.png` and `ic_launcher_round.png` to each folder
   - Or use Android Studio's Image Asset Studio: Right-click `res` → New → Image Asset

4. **Build APK/AAB**
   ```
   Build → Build Bundle(s) / APK(s) → Build Bundle(s)
   ```

## 🚀 Running the App

### On Device/Emulator

1. **Connect device** or start emulator (API 26+)
2. **Run app**: Click the green "Run" button in Android Studio
3. **Grant permissions**: 
   - Allow notification permission (Android 13+)
   - The app will request this automatically

### What You Should See

1. **On First Launch:**
   - Countdown starts immediately
   - Shows "0000 days" and "00:00:00" initially
   - Notification appears in status bar
   - Countdown begins counting down

2. **After Closing App:**
   - Notification remains visible
   - Countdown continues in background
   - Reopening app shows current countdown

3. **After Reboot:**
   - Service automatically restarts
   - Notification reappears
   - Countdown continues from where it left off

## ✅ Verification Checklist

### Test Countdown Persistence

- [ ] **First Launch**: Countdown starts and begins counting
- [ ] **Close App**: Notification remains, countdown continues
- [ ] **Lock Screen**: Countdown continues
- [ ] **Switch Apps**: Countdown continues
- [ ] **Reboot Device**: Service restarts automatically, countdown resumes correctly
- [ ] **Force Stop App**: Service restarts when app is opened again

### Test UI

- [ ] Days display updates correctly
- [ ] Time (HH:MM:SS) updates every second
- [ ] Format is correct (e.g., "1234 days" and "12:34:56")

### Test Notification

- [ ] Notification appears when app starts
- [ ] Notification shows remaining days
- [ ] Notification is non-dismissible (can't swipe away)
- [ ] Tapping notification opens app
- [ ] Notification updates every minute

## 📋 Play Store Compliance

### Why Foreground Service?

The app uses a **foreground service** because:
- The countdown must run continuously, even when the app is closed
- Android restricts background work to prevent battery drain
- Foreground services are the Play Store-approved way to run long-running tasks
- The service is properly declared in AndroidManifest.xml

### Why Notification?

The notification is shown because:
- **Required by Android** - All foreground services must show a notification
- **User transparency** - Users can see the countdown is running
- **Play Store policy** - Foreground services must have visible notifications
- **Non-dismissible** - Ensures users always know the countdown is active

### Permissions Explained

- **RECEIVE_BOOT_COMPLETED**: Restarts service after device reboot
- **FOREGROUND_SERVICE**: Required for Android 9+ to use foreground services
- **POST_NOTIFICATIONS**: Required for Android 13+ to show notifications

## 🐛 Troubleshooting

### Service Not Starting
- Check AndroidManifest.xml has service declaration
- Verify FOREGROUND_SERVICE permission is declared
- Check logcat for errors

### Notification Not Showing
- Grant notification permission (Android 13+)
- Check notification channel is created
- Verify service is running in foreground

### Countdown Resets
- Check SharedPreferences is working
- Verify start timestamp is saved correctly
- Check CountdownManager.initializeCountdown() is called

### Service Stops After Reboot
- Verify BootReceiver is declared in manifest
- Check RECEIVE_BOOT_COMPLETED permission
- Some manufacturers require additional permissions (check device settings)

## 📦 Building for Play Store

1. **Generate Signed Bundle**
   ```
   Build → Generate Signed Bundle / APK → Android App Bundle
   ```

2. **Create Keystore** (if first time)
   - Follow Android Studio wizard
   - Save keystore securely (you'll need it for updates)

3. **Upload to Play Console**
   - Go to Google Play Console
   - Create new app
   - Upload the generated .aab file
   - Fill in store listing, content rating, etc.

## 📝 Code Structure

```
app/
├── src/main/
│   ├── java/com/yourname/countdown1356/
│   │   ├── MainActivity.kt          # UI screen
│   │   ├── CountdownService.kt      # Foreground service
│   │   ├── BootReceiver.kt          # Reboot handler
│   │   └── CountdownManager.kt      # Countdown logic
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml    # UI layout
│   │   └── values/
│   │       ├── strings.xml          # String resources
│   │       ├── colors.xml           # Color resources
│   │       └── themes.xml           # App theme
│   └── AndroidManifest.xml          # App configuration
├── build.gradle                     # App dependencies
└── proguard-rules.pro              # ProGuard rules
```

## 🔢 Countdown Logic

The countdown uses this formula:
```
remaining = (startTime + 1356 days) - currentTime
```

- **startTime**: Saved on first app launch (never changes)
- **1356 days**: Fixed duration (1356 × 24 × 60 × 60 × 1000 ms)
- **currentTime**: System current time
- **remaining**: Time left until countdown reaches zero

This ensures:
- ✅ No drift (uses absolute timestamps)
- ✅ No reset (startTime is saved permanently)
- ✅ Accurate across reboots (uses system time)

## 📄 License

This project is provided as-is for educational purposes.

## 🆘 Support

If you encounter issues:
1. Check logcat for error messages
2. Verify all permissions are granted
3. Ensure device meets minimum requirements (API 26+)
4. Test on different Android versions if possible

---

**Built with ❤️ for Android**

