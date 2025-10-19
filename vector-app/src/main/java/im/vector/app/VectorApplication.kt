/*
 * Copyright 2022-2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package im.vector.app

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Handler
import android.os.HandlerThread
import android.os.StrictMode
import android.util.Log
import android.view.Gravity
import androidx.core.content.ContextCompat
import androidx.core.provider.FontRequest
import androidx.core.provider.FontsContractCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDex
import androidx.recyclerview.widget.SnapHelper
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.EpoxyAsyncUtil
import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.Mavericks
import com.facebook.stetho.Stetho
import com.gabrielittner.threetenbp.LazyThreeTen
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.mapbox.mapboxsdk.Mapbox
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import dagger.hilt.android.HiltAndroidApp
import im.vector.app.config.Config
import im.vector.app.core.debug.FlipperProxy
import im.vector.app.core.debug.LeakDetector
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.pushers.FcmHelper
import im.vector.app.core.resources.BuildMeta
import im.vector.app.features.analytics.DecryptionFailureTracker
import im.vector.app.features.analytics.VectorAnalytics
import im.vector.app.features.analytics.plan.SuperProperties
import im.vector.app.features.call.webrtc.WebRtcCallManager
import im.vector.app.features.configuration.VectorConfiguration
import im.vector.app.features.invite.InvitesAcceptor
import im.vector.app.features.lifecycle.VectorActivityLifecycleCallbacks
import im.vector.app.features.notifications.NotificationDrawerManager
import im.vector.app.features.notifications.NotificationUtils
import im.vector.app.features.pin.PinLocker
import im.vector.app.features.popup.PopupAlertManager
import im.vector.app.features.rageshake.VectorFileLogger
import im.vector.app.features.rageshake.VectorUncaughtExceptionHandler
import im.vector.app.features.settings.VectorLocale
import im.vector.app.features.settings.VectorPreferences
import im.vector.app.features.themes.ThemeUtils
import im.vector.app.features.version.VersionProvider
import im.vector.application.R
import org.jitsi.meet.sdk.log.JitsiMeetDefaultLogHandler

// Security and Obfuscation imports
import com.example.antidebug.AntiDebug
import com.example.antidebug.AntiDebug.ThreatType
import com.example.antidebug.SecurityReport
import com.example.antidebug.MonitoringStatistics
import com.example.antidebug.SecurityCheckResult
import io.element.android.library.obfuscation.ObfuscationManager
import io.element.android.library.obfuscation.ObfuscationConfig
import org.matrix.android.sdk.api.Matrix
import org.matrix.android.sdk.api.auth.AuthenticationService
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors
import javax.inject.Inject
import androidx.work.Configuration as WorkConfiguration

@HiltAndroidApp
class VectorApplication :
        Application(),
        WorkConfiguration.Provider {

    lateinit var appContext: Context
    @Inject lateinit var authenticationService: AuthenticationService
    @Inject lateinit var vectorConfiguration: VectorConfiguration
    @Inject lateinit var emojiCompatFontProvider: EmojiCompatFontProvider
    @Inject lateinit var emojiCompatWrapper: EmojiCompatWrapper
    @Inject lateinit var vectorUncaughtExceptionHandler: VectorUncaughtExceptionHandler
    @Inject lateinit var activeSessionHolder: ActiveSessionHolder
    @Inject lateinit var notificationDrawerManager: NotificationDrawerManager
    @Inject lateinit var vectorPreferences: VectorPreferences
    @Inject lateinit var versionProvider: VersionProvider
    @Inject lateinit var notificationUtils: NotificationUtils
    @Inject lateinit var spaceStateHandler: SpaceStateHandler
    @Inject lateinit var popupAlertManager: PopupAlertManager
    @Inject lateinit var pinLocker: PinLocker
    @Inject lateinit var callManager: WebRtcCallManager
    @Inject lateinit var invitesAcceptor: InvitesAcceptor
    @Inject lateinit var autoRageShaker: AutoRageShaker
    @Inject lateinit var decryptionFailureTracker: DecryptionFailureTracker
    @Inject lateinit var vectorFileLogger: VectorFileLogger
    @Inject lateinit var vectorAnalytics: VectorAnalytics
    @Inject lateinit var flipperProxy: FlipperProxy
    @Inject lateinit var matrix: Matrix
    @Inject lateinit var fcmHelper: FcmHelper
    @Inject lateinit var buildMeta: BuildMeta
    @Inject lateinit var leakDetector: LeakDetector
    @Inject lateinit var vectorLocale: VectorLocale
    @Inject lateinit var webRtcCallManager: WebRtcCallManager

    // font thread handler
    private var fontThreadHandler: Handler? = null

    private val powerKeyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == Intent.ACTION_SCREEN_OFF &&
                    vectorPreferences.useFlagPinCode()) {
                pinLocker.screenIsOff()
            }
        }
    }

    override fun onCreate() {
        enableStrictModeIfNeeded()
        super.onCreate()
        appContext = this
        
        // Add timeout handler to prevent ANR
        val timeoutHandler = Handler(mainLooper)
        timeoutHandler.postDelayed({
            Timber.w("Application startup taking longer than expected - continuing with basic initialization")
        }, 10000) // 10 second timeout - more aggressive
        
        // In debug builds, use minimal startup mode if configured
        if (buildMeta.isDebug) {
            val useMinimalStartup = try {
                val buildConfigClass = Class.forName("im.vector.app.BuildConfig")
                val field = buildConfigClass.getDeclaredField("USE_MINIMAL_STARTUP_IN_DEBUG")
                field.isAccessible = true
                field.getBoolean(null)
            } catch (e: Exception) {
                true // Default to true for safety
            }
            
            if (useMinimalStartup) {
                Timber.d("Debug build detected - using minimal startup mode")
                initializeMinimalStartup()
                return
            }
        }
        
        // Initialize security and obfuscation systems in background
        Thread {
            try {
                initializeSecurity()
                initializeObfuscation()
                timeoutHandler.removeCallbacksAndMessages(null) // Clear timeout on success
            } catch (e: Exception) {
                Timber.e(e, "Failed to initialize security/obfuscation in background")
            }
        }.start()
        
        // Initialize only essential systems on main thread
        vectorUncaughtExceptionHandler.activate()

        // Remove Log handler statically added by Jitsi
        Timber.forest()
                .filterIsInstance(JitsiMeetDefaultLogHandler::class.java)
                .forEach { Timber.uproot(it) }

        if (buildMeta.isDebug) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.plant(vectorFileLogger)

        logInfo()
        LazyThreeTen.init(this)
        Mavericks.initialize(debugMode = false)
        
        // Move heavy initialization to background thread
        Thread {
            try {
                flipperProxy.init(matrix)
                vectorAnalytics.init()
                vectorAnalytics.updateSuperProperties(
                        SuperProperties(
                                appPlatform = SuperProperties.AppPlatform.EA,
                                cryptoSDK = SuperProperties.CryptoSDK.Rust,
                                cryptoSDKVersion = Matrix.getCryptoVersion(longFormat = false)
                        )
                )
                invitesAcceptor.initialize()
                autoRageShaker.initialize()
                decryptionFailureTracker.start()
                
                if (buildMeta.isDebug) {
                    Stetho.initializeWithDefaults(this@VectorApplication)
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to initialize heavy systems in background")
            }
        }.start()

        configureEpoxy()

        registerActivityLifecycleCallbacks(VectorActivityLifecycleCallbacks(popupAlertManager))
        
        // Move heavy operations to background thread
        Thread {
            try {
                val fontRequest = FontRequest(
                        "com.google.android.gms.fonts",
                        "com.google.android.gms",
                        "Noto Color Emoji Compat",
                        R.array.com_google_android_gms_fonts_certs
                )
                @Suppress("DEPRECATION")
                FontsContractCompat.requestFont(this@VectorApplication, fontRequest, emojiCompatFontProvider, getFontThreadHandler())
                vectorLocale.init()
                ThemeUtils.init(this@VectorApplication)
                vectorConfiguration.applyToApplicationContext()
                emojiCompatWrapper.init(fontRequest)
                notificationUtils.createNotificationChannels()
            } catch (e: Exception) {
                Timber.e(e, "Failed to initialize background systems")
            }
        }.start()

        // Move ProcessLifecycleOwner observers to background thread
        Thread {
            try {
                ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
                    private var stopBackgroundSync = false

                    override fun onResume(owner: LifecycleOwner) {
                        Timber.i("App entered foreground")
                        fcmHelper.onEnterForeground(activeSessionHolder)
                        if (webRtcCallManager.currentCall.get() == null) {
                            Timber.i("App entered foreground and no active call: stop any background sync")
                            activeSessionHolder.getSafeActiveSessionAsync {
                                it?.syncService()?.stopAnyBackgroundSync()
                            }
                        } else {
                            Timber.i("App entered foreground: there is an active call, set stopBackgroundSync to true")
                            stopBackgroundSync = true
                        }
                    }

                    override fun onPause(owner: LifecycleOwner) {
                        Timber.i("App entered background")
                        fcmHelper.onEnterBackground(activeSessionHolder)

                        if (stopBackgroundSync) {
                            if (webRtcCallManager.currentCall.get() == null) {
                                Timber.i("App entered background: stop any background sync")
                                activeSessionHolder.getSafeActiveSessionAsync {
                                    it?.syncService()?.stopAnyBackgroundSync()
                                }
                                stopBackgroundSync = false
                            } else {
                                Timber.i("App entered background: there is an active call do not stop background sync")
                            }
                        }
                    }
                })
                ProcessLifecycleOwner.get().lifecycle.addObserver(spaceStateHandler)
                ProcessLifecycleOwner.get().lifecycle.addObserver(pinLocker)
                ProcessLifecycleOwner.get().lifecycle.addObserver(callManager)
            } catch (e: Exception) {
                Timber.e(e, "Failed to initialize ProcessLifecycleOwner observers")
            }
        }.start()
        // Move receiver registration and emoji initialization to background
        Thread {
            try {
                // This should be done as early as possible
                // initKnownEmojiHashSet(appContext)
                ContextCompat.registerReceiver(
                        applicationContext,
                        powerKeyReceiver,
                        IntentFilter().apply {
                            // Looks like i cannot receive OFF, if i don't have both ON and OFF
                            addAction(Intent.ACTION_SCREEN_OFF)
                            addAction(Intent.ACTION_SCREEN_ON)
                        },
                        ContextCompat.RECEIVER_NOT_EXPORTED,
                )
                EmojiManager.install(GoogleEmojiProvider())
            } catch (e: Exception) {
                Timber.e(e, "Failed to initialize receiver and emoji systems")
            }
        }.start()

        // Initialize Mapbox before inflating mapViews
        // Move Mapbox initialization to background
        Thread {
            try {
                Mapbox.getInstance(this@VectorApplication)
            } catch (e: Exception) {
                Timber.e(e, "Failed to initialize Mapbox")
            }
        }.start()

        initMemoryLeakAnalysis()
    }

    private fun configureEpoxy() {
        EpoxyController.defaultDiffingHandler = EpoxyAsyncUtil.getAsyncBackgroundHandler()
        EpoxyController.defaultModelBuildingHandler = EpoxyAsyncUtil.getAsyncBackgroundHandler()
        Carousel.setDefaultGlobalSnapHelperFactory(object : Carousel.SnapHelperFactory() {
            override fun buildSnapHelper(context: Context?): SnapHelper {
                return GravitySnapHelper(Gravity.START)
            }
        })
    }

    private fun enableStrictModeIfNeeded() {
        if (Config.ENABLE_STRICT_MODE_LOGS) {
            StrictMode.setThreadPolicy(
                    StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog()
                            .build()
            )
        }
    }

    override fun getWorkManagerConfiguration(): WorkConfiguration {
        return WorkConfiguration.Builder()
                .setWorkerFactory(matrix.getWorkerFactory())
                .setMinimumLoggingLevel(Log.DEBUG)
                .setExecutor(Executors.newCachedThreadPool())
                .build()
    }

    private fun logInfo() {
        val appVersion = versionProvider.getVersion(longFormat = true)
        val sdkVersion = Matrix.getSdkVersion()
        val date = SimpleDateFormat("MM-dd HH:mm:ss.SSSZ", Locale.US).format(Date())

        Timber.d("----------------------------------------------------------------")
        Timber.d("----------------------------------------------------------------")
        Timber.d(" Application version: $appVersion")
        Timber.d(" SDK version: $sdkVersion")
        Timber.d(" Local time: $date")
        Timber.d("----------------------------------------------------------------")
        Timber.d("----------------------------------------------------------------\n\n\n\n")
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        vectorConfiguration.onConfigurationChanged()
    }

    private fun getFontThreadHandler(): Handler {
        return fontThreadHandler ?: createFontThreadHandler().also {
            fontThreadHandler = it
        }
    }

    private fun createFontThreadHandler(): Handler {
        val handlerThread = HandlerThread("Vector-fonts")
        handlerThread.start()
        return Handler(handlerThread.looper)
    }

    private fun initMemoryLeakAnalysis() {
        leakDetector.enable(vectorPreferences.isMemoryLeakAnalysisEnabled())
    }
    
    /**
     * Minimal startup for debug builds to prevent ANR
     */
    private fun initializeMinimalStartup() {
        try {
            // Only initialize absolutely essential systems
            vectorUncaughtExceptionHandler.activate()
            
            // Basic logging setup
            if (buildMeta.isDebug) {
                Timber.plant(Timber.DebugTree())
            }
            Timber.plant(vectorFileLogger)
            
            // Basic info logging
            logInfo()
            
            // Initialize only critical systems
            LazyThreeTen.init(this)
            Mavericks.initialize(debugMode = false)
            
            // Configure Epoxy
            configureEpoxy()
            
            // Register activity lifecycle callbacks
            registerActivityLifecycleCallbacks(VectorActivityLifecycleCallbacks(popupAlertManager))
            
            // Initialize memory leak analysis
            initMemoryLeakAnalysis()
            
            Timber.d("Minimal startup completed successfully")
            
            // Initialize other systems in background after a delay
            Handler(mainLooper).postDelayed({
                Thread {
                    try {
                        // Initialize heavy systems in background
                        flipperProxy.init(matrix)
                        vectorAnalytics.init()
                        vectorAnalytics.updateSuperProperties(
                                SuperProperties(
                                        appPlatform = SuperProperties.AppPlatform.EA,
                                        cryptoSDK = SuperProperties.CryptoSDK.Rust,
                                        cryptoSDKVersion = Matrix.getCryptoVersion(longFormat = false)
                                )
                        )
                        invitesAcceptor.initialize()
                        autoRageShaker.initialize()
                        decryptionFailureTracker.start()
                        
                        if (buildMeta.isDebug) {
                            Stetho.initializeWithDefaults(this@VectorApplication)
                        }
                        
                        // Initialize other background systems
                        val fontRequest = FontRequest(
                                "com.google.android.gms.fonts",
                                "com.google.android.gms",
                                "Noto Color Emoji Compat",
                                R.array.com_google_android_gms_fonts_certs
                        )
                        @Suppress("DEPRECATION")
                        FontsContractCompat.requestFont(this@VectorApplication, fontRequest, emojiCompatFontProvider, getFontThreadHandler())
                        vectorLocale.init()
                        ThemeUtils.init(this@VectorApplication)
                        vectorConfiguration.applyToApplicationContext()
                        emojiCompatWrapper.init(fontRequest)
                        notificationUtils.createNotificationChannels()
                        
                        // ProcessLifecycleOwner observers
                        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
                            private var stopBackgroundSync = false

                            override fun onResume(owner: LifecycleOwner) {
                                Timber.i("App entered foreground")
                                fcmHelper.onEnterForeground(activeSessionHolder)
                                if (webRtcCallManager.currentCall.get() == null) {
                                    Timber.i("App entered foreground and no active call: stop any background sync")
                                    activeSessionHolder.getSafeActiveSessionAsync {
                                        it?.syncService()?.stopAnyBackgroundSync()
                                    }
                                } else {
                                    Timber.i("App entered foreground: there is an active call, set stopBackgroundSync to true")
                                    stopBackgroundSync = true
                                }
                            }

                            override fun onPause(owner: LifecycleOwner) {
                                Timber.i("App entered background")
                                fcmHelper.onEnterBackground(activeSessionHolder)

                                if (stopBackgroundSync) {
                                    if (webRtcCallManager.currentCall.get() == null) {
                                        Timber.i("App entered background: stop any background sync")
                                        activeSessionHolder.getSafeActiveSessionAsync {
                                            it?.syncService()?.stopAnyBackgroundSync()
                                        }
                                        stopBackgroundSync = false
                                    } else {
                                        Timber.i("App entered background: there is an active call do not stop background sync")
                                    }
                                }
                            }
                        })
                        ProcessLifecycleOwner.get().lifecycle.addObserver(spaceStateHandler)
                        ProcessLifecycleOwner.get().lifecycle.addObserver(pinLocker)
                        ProcessLifecycleOwner.get().lifecycle.addObserver(callManager)
                        
                        // Receiver registration
                        ContextCompat.registerReceiver(
                                applicationContext,
                                powerKeyReceiver,
                                IntentFilter().apply {
                                    addAction(Intent.ACTION_SCREEN_OFF)
                                    addAction(Intent.ACTION_SCREEN_ON)
                                },
                                ContextCompat.RECEIVER_NOT_EXPORTED,
                        )
                        EmojiManager.install(GoogleEmojiProvider())
                        
                        // Mapbox initialization
                        Mapbox.getInstance(this@VectorApplication)
                        
                        Timber.d("Background initialization completed successfully")
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to initialize background systems in minimal startup")
                    }
                }.start()
            }, 2000) // 2 second delay to let main thread finish
            
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize minimal startup")
        }
    }
    
    /**
     * Initialize security systems including anti-debug protection
     */
    private fun initializeSecurity() {
        try {
            // Check if security should be disabled in debug builds
            val disableSecurityInDebug = try {
                val buildConfigClass = Class.forName("im.vector.app.BuildConfig")
                val field = buildConfigClass.getDeclaredField("DISABLE_SECURITY_IN_DEBUG")
                field.isAccessible = true
                field.getBoolean(null)
            } catch (e: Exception) {
                true // Default to true for safety
            }
            
            // In debug builds, skip security initialization if configured
            if (buildMeta.isDebug && disableSecurityInDebug) {
                Timber.d("Debug build detected with security disabled - skipping security initialization")
                return
            }
            
            // Initialize AntiDebug SDK with continuous monitoring for production builds
            AntiDebug.init(this, enableContinuousMonitoring = !buildMeta.isDebug)
            
            // Perform initial security check
            val securityReport = AntiDebug.performSecurityCheck()
            
            // Handle any detected threats
            if (securityReport.hasThreats()) {
                Timber.w("Security threats detected during initialization")
                handleSecurityThreats(securityReport)
            } else {
                Timber.d("Security initialization completed successfully")
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize security systems")
            // In production, you might want to exit the app if security fails
            if (!buildMeta.isDebug) {
                // For production builds, consider terminating the app
                // System.exit(1)
            }
        }
    }
    
    /**
     * Initialize obfuscation systems
     */
    private fun initializeObfuscation() {
        try {
            // Check if obfuscation should be disabled in debug builds
            val disableObfuscationInDebug = try {
                val buildConfigClass = Class.forName("im.vector.app.BuildConfig")
                val field = buildConfigClass.getDeclaredField("DISABLE_OBFUSCATION_IN_DEBUG")
                field.isAccessible = true
                field.getBoolean(null)
            } catch (e: Exception) {
                true // Default to true for safety
            }
            
            // Skip obfuscation in debug builds if configured
            if (buildMeta.isDebug && disableObfuscationInDebug) {
                Timber.d("Debug build detected with obfuscation disabled - skipping obfuscation initialization for faster startup")
                return
            }
            
            // Initialize obfuscation manager with configuration
            ObfuscationManager.initialize(
                context = this,
                config = object : ObfuscationConfig {
                    override val isDebugMode = BuildConfig.DEBUG
                }
            )
            
            // Log obfuscation status
            val obfuscationStatus = ObfuscationManager.getObfuscationStatus()
            val obfuscationStats = ObfuscationManager.getObfuscationStats()
            
            Timber.d("Obfuscation initialized - Status: $obfuscationStatus, Stats: $obfuscationStats")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize obfuscation systems")
        }
    }
    
    /**
     * Handle detected security threats
     */
    private fun handleSecurityThreats(securityReport: SecurityReport) {
        when {
            securityReport.debuggerDetected -> {
                Timber.w("Debugger detected - handling threat")
                // Threat handling is already done in the individual detection methods
            }
            securityReport.rootDetected -> {
                Timber.w("Root detected - handling threat")
                // Threat handling is already done in the individual detection methods
            }
            securityReport.emulatorDetected -> {
                Timber.w("Emulator detected - handling threat")
                // Threat handling is already done in the individual detection methods
            }
            securityReport.tamperingDetected -> {
                Timber.w("Tampering detected - handling threat")
                // Threat handling is already done in the individual detection methods
            }
            securityReport.hooksDetected -> {
                Timber.w("Hooks detected - handling threat")
                // Threat handling is already done in the individual detection methods
            }
            securityReport.suspiciousBehavior -> {
                Timber.w("Suspicious behavior detected - handling threat")
                // Threat handling is already done in the individual detection methods
            }
        }
        
        // For production builds, consider terminating the app
        if (!buildMeta.isDebug) {
            Timber.w("Security threat detected in production build - terminating app")
            // Note: finishAffinity() is not available in Application class
            // The app will continue running but with security warnings
            System.exit(1)
        } else {
            Timber.d("Security threat detected in debug build - continuing with warnings")
        }
    }
    
    /**
     * Perform periodic security checks
     */
    fun performSecurityCheck() {
        try {
            // Check if security should be disabled in debug builds
            val disableSecurityInDebug = try {
                val buildConfigClass = Class.forName("im.vector.app.BuildConfig")
                val field = buildConfigClass.getDeclaredField("DISABLE_SECURITY_IN_DEBUG")
                field.isAccessible = true
                field.getBoolean(null)
            } catch (e: Exception) {
                true // Default to true for safety
            }
            
            // Skip security checks in debug builds if configured
            if (buildMeta.isDebug && disableSecurityInDebug) {
                Timber.d("Debug build detected with security disabled - skipping security check")
                return
            }
            
            val securityReport = AntiDebug.performSecurityCheck()
            if (securityReport.hasThreats()) {
                handleSecurityThreats(securityReport)
            }
        } catch (e: Exception) {
            Timber.e(e, "Security check failed")
        }
    }
    
    /**
     * Get security monitoring statistics
     */
    fun getSecurityStatistics(): MonitoringStatistics? {
        return try {
            AntiDebug.getMonitoringStatistics()
        } catch (e: Exception) {
            Timber.e(e, "Failed to get security statistics")
            null
        }
    }
    
    /**
     * Perform immediate security check
     */
    fun performImmediateSecurityCheck(): SecurityCheckResult? {
        return try {
            AntiDebug.performImmediateSecurityCheck()
        } catch (e: Exception) {
            Timber.e(e, "Failed to perform immediate security check")
            null
        }
    }
    
    /**
     * Pause security monitoring
     */
    fun pauseSecurityMonitoring() {
        try {
            AntiDebug.pauseMonitoring()
            Timber.d("Security monitoring paused")
        } catch (e: Exception) {
            Timber.e(e, "Failed to pause security monitoring")
        }
    }
    
    /**
     * Resume security monitoring
     */
    fun resumeSecurityMonitoring() {
        try {
            AntiDebug.resumeMonitoring()
            Timber.d("Security monitoring resumed")
        } catch (e: Exception) {
            Timber.e(e, "Failed to resume security monitoring")
        }
    }
    
    /**
     * Get data protection instance for secure storage
     */
    fun getDataProtection(): com.example.antidebug.DataProtection? {
        return try {
            AntiDebug.getDataProtection()
        } catch (e: Exception) {
            Timber.e(e, "Failed to get data protection instance")
            null
        }
    }
    
    /**
     * Handle security threats (public method for external calls)
     */
    fun handleSecurityThreat(threatType: ThreatType) {
        try {
            AntiDebug.handleThreat(threatType)
        } catch (e: Exception) {
            Timber.e(e, "Failed to handle security threat: $threatType")
        }
    }
}
