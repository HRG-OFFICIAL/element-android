package com.example.antidebug

import android.content.Context
import kotlinx.coroutines.*

/**
 * AntiDebugSDK - Main entry point for the security SDK
 * 
 * This class provides a unified interface to access all security detection features
 * including debugger detection, root detection, emulator detection, tamper detection,
 * hook detection, behavioral analysis, and data protection.
 * 
 * Usage:
 * ```kotlin
 * AntiDebug.init(context)
 * if (AntiDebug.isDebuggerAttached()) {
 *     // Handle threat
 * }
 * ```
 */
object AntiDebug {
    
    // Constants
    const val DEBUGGER_DETECTED = "DEBUGGER_DETECTED"
    const val ROOT_DETECTED = "ROOT_DETECTED"
    const val EMULATOR_DETECTED = "EMULATOR_DETECTED"
    const val TAMPER_DETECTED = "TAMPER_DETECTED"
    const val HOOK_DETECTED = "HOOK_DETECTED"
    const val BEHAVIORAL_ANOMALY = "BEHAVIORAL_ANOMALY"
    const val DATA_BREACH = "DATA_BREACH"
    const val UNKNOWN = "UNKNOWN"
    
    // Enums
    enum class ThreatType {
        DEBUGGER, ROOT, EMULATOR, TAMPERING, HOOKS, SUSPICIOUS_BEHAVIOR, DATA_BREACH, UNKNOWN
    }
    
    enum class ResponseType {
        BLOCK, LOG, NOTIFY, TERMINATE
    }
    
    private var initialized = false
    private lateinit var context: Context
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Detection modules
    private lateinit var debuggerDetection: DebuggerDetection
    private lateinit var rootDetection: RootDetection
    private lateinit var emulatorDetection: EmulatorDetection
    private lateinit var tamperDetection: TamperDetection
    private lateinit var hookDetection: HookDetection
    private lateinit var behavioralDetection: BehavioralAnalysis
    private lateinit var responseHandler: ResponseHandler
    private lateinit var dataProtection: DataProtection
    private lateinit var continuousMonitoring: ContinuousMonitoring
    
    // Native library loading
    init {
        try {
            System.loadLibrary("anti-debug-native")
        } catch (e: UnsatisfiedLinkError) {
            // Log error but continue - some features will be limited
            android.util.Log.e("AntiDebug", "Failed to load native library: ${e.message}")
        }
    }
    
    /**
     * Initialize the AntiDebug SDK with application context
     * 
     * @param context Application context
     * @param enableContinuousMonitoring Enable background monitoring (optional)
     */
    @JvmStatic
    @JvmOverloads
    fun init(context: Context, enableContinuousMonitoring: Boolean = false) {
        if (initialized) return
        
        this.context = context.applicationContext
        
        // Initialize detection modules
        debuggerDetection = DebuggerDetection(this.context)
        rootDetection = RootDetection(this.context)
        emulatorDetection = EmulatorDetection(this.context)
        tamperDetection = TamperDetection(this.context)
        hookDetection = HookDetection(this.context)
        behavioralDetection = BehavioralAnalysis(this.context)
        responseHandler = ResponseHandler(this.context)
        dataProtection = DataProtection(this.context)
        continuousMonitoring = ContinuousMonitoring(this.context)
        
        // Initialize certificate fingerprints for tamper detection
        // Note: Certificate fingerprints will be loaded at runtime from the main app
        val debugFingerprints = setOf(
            "SHA256: 14:6D:E9:83:C5:73:17:34:02:85:12:8F:32:37:4E:85:D3:ED:F3:AA:8C:0A:BC:10:24:02:1C:60:5D:BE:AB:A6"
        )
        val releaseFingerprints = setOf(
            "TODO: Add production certificate fingerprint here"
        )
        val allFingerprints = debugFingerprints + releaseFingerprints
        // Note: Certificate fingerprints will be loaded at runtime from the main app
        
        initialized = true
        
        // Initialize continuous monitoring
        continuousMonitoring.initialize(
            debuggerDetection,
            rootDetection,
            emulatorDetection,
            tamperDetection,
            hookDetection,
            behavioralDetection,
            responseHandler
        )
        
        // Start continuous monitoring if enabled
        if (enableContinuousMonitoring) {
            startContinuousMonitoring()
        }
    }
    
    /**
     * Initialize device fingerprints for detection
     */
    private fun initializeFingerprints() {
        // Initialize device-specific fingerprints for detection
        // This is a placeholder for fingerprint initialization
    }
    
    /**
     * Check if a debugger is currently attached to the process
     * 
     * @return true if debugger is detected
     */
    @JvmStatic
    fun isDebuggerAttached(): Boolean {
        ensureInitialized()
        val isDetected = debuggerDetection.isDebuggerAttached()
        
        if (isDetected) {
            responseHandler.handleSecurityThreat(
                ThreatType.DEBUGGER,
                5,
                "Debugger detected using advanced detection methods"
            )
        }
        
        return isDetected
    }
    
    /**
     * Check if the device is rooted
     * 
     * @return true if root is detected
     */
    @JvmStatic
    fun isDeviceRooted(): Boolean {
        ensureInitialized()
        val isDetected = rootDetection.isDeviceRooted()
        
        if (isDetected) {
            responseHandler.handleSecurityThreat(
                ThreatType.ROOT,
                6,
                "Root access detected on device"
            )
        }
        
        return isDetected
    }
    
    /**
     * Check if running on an emulator
     * 
     * @return true if emulator is detected
     */
    @JvmStatic
    fun isRunningOnEmulator(): Boolean {
        ensureInitialized()
        val isDetected = emulatorDetection.isEmulator()
        
        if (isDetected) {
            responseHandler.handleSecurityThreat(
                ThreatType.EMULATOR,
                4,
                "Emulator environment detected"
            )
        }
        
        return isDetected
    }
    
    /**
     * Check if the application has been tampered with
     * 
     * @return true if tampering is detected
     */
    @JvmStatic
    fun isApplicationTampered(): Boolean {
        ensureInitialized()
        val isDetected = tamperDetection.isApplicationTampered()
        
        if (isDetected) {
            responseHandler.handleSecurityThreat(
                ThreatType.TAMPERING,
                7,
                "Application tampering detected"
            )
        }
        
        return isDetected
    }
    
    /**
     * Check if hooking frameworks are present
     * 
     * @return true if hooks are detected
     */
    @JvmStatic
    fun areHooksDetected(): Boolean {
        ensureInitialized()
        val isDetected = hookDetection.isHookingDetected()
        
        if (isDetected) {
            responseHandler.handleSecurityThreat(
                ThreatType.HOOKS,
                5,
                "Hooking framework detected"
            )
        }
        
        return isDetected
    }
    
    /**
     * Perform behavioral analysis for suspicious activity
     * 
     * @return true if suspicious behavior is detected
     */
    @JvmStatic
    fun isSuspiciousBehavior(): Boolean {
        ensureInitialized()
        val isDetected = behavioralDetection.isSuspiciousBehaviorDetected()
        
        if (isDetected) {
            responseHandler.handleSecurityThreat(
                ThreatType.SUSPICIOUS_BEHAVIOR,
                3,
                "Suspicious behavior detected"
            )
        }
        
        return isDetected
    }
    
    /**
     * Perform comprehensive security check
     * 
     * @return SecurityReport containing all detection results
     */
    @JvmStatic
    fun performSecurityCheck(): SecurityReport {
        ensureInitialized()
        
        return SecurityReport(
            debuggerDetected = isDebuggerAttached(),
            rootDetected = isDeviceRooted(),
            emulatorDetected = isRunningOnEmulator(),
            tamperingDetected = isApplicationTampered(),
            hooksDetected = areHooksDetected(),
            suspiciousBehavior = isSuspiciousBehavior(),
            timestamp = System.currentTimeMillis()
        )
    }
    
    /**
     * Get data protection instance for secure storage
     * 
     * @return DataProtection instance
     */
    @JvmStatic
    fun getDataProtection(): DataProtection {
        ensureInitialized()
        return dataProtection
    }
    
    /**
     * Configure response handler behavior
     * 
     * @param responseType Type of response when threats are detected
     */
    @JvmStatic
    fun configureResponse(responseType: ResponseType) {
        ensureInitialized()
        responseHandler.setResponseType(responseType)
    }
    
    /**
     * Manually trigger response for detected threat
     * 
     * @param threatType Type of threat detected
     */
    @JvmStatic
    fun handleThreat(threatType: ThreatType) {
        ensureInitialized()
        responseHandler.handleThreat(threatType)
    }
    
    /**
     * Start continuous monitoring in background
     */
    private fun startContinuousMonitoring() {
        continuousMonitoring.startMonitoring()
        android.util.Log.i("AntiDebug", "Continuous monitoring started")
    }
    
    /**
     * Stop continuous monitoring
     */
    @JvmStatic
    fun stopMonitoring() {
        continuousMonitoring.stopMonitoring()
        android.util.Log.i("AntiDebug", "Continuous monitoring stopped")
    }
    
    /**
     * Ensure SDK is initialized before use
     */
    private fun ensureInitialized() {
        if (!initialized) {
            throw IllegalStateException("AntiDebug SDK not initialized. Call AntiDebug.init(context) first.")
        }
    }
    
    /**
     * Get monitoring statistics
     */
    @JvmStatic
    fun getMonitoringStatistics(): MonitoringStatistics {
        ensureInitialized()
        return continuousMonitoring.getMonitoringStatistics()
    }
    
    /**
     * Perform immediate security check
     */
    @JvmStatic
    fun performImmediateSecurityCheck(): SecurityCheckResult {
        ensureInitialized()
        return continuousMonitoring.performImmediateSecurityCheck()
    }
    
    /**
     * Pause monitoring temporarily
     */
    @JvmStatic
    fun pauseMonitoring() {
        ensureInitialized()
        continuousMonitoring.pauseMonitoring()
    }
    
    /**
     * Resume monitoring
     */
    @JvmStatic
    fun resumeMonitoring() {
        ensureInitialized()
        continuousMonitoring.resumeMonitoring()
    }
    
    /**
     * Clean up resources when no longer needed
     */
    @JvmStatic
    fun cleanup() {
        if (initialized) {
            continuousMonitoring.cleanup()
            stopMonitoring()
            initialized = false
        }
    }
}

/**
 * Security report containing all detection results
 */
data class SecurityReport(
    val debuggerDetected: Boolean,
    val rootDetected: Boolean,
    val emulatorDetected: Boolean,
    val tamperingDetected: Boolean,
    val hooksDetected: Boolean,
    val suspiciousBehavior: Boolean,
    val timestamp: Long
) {
    fun hasThreats(): Boolean {
        return debuggerDetected || rootDetected || emulatorDetected || 
               tamperingDetected || hooksDetected || suspiciousBehavior
    }
    
    fun getThreatCount(): Int {
        return listOf(
            debuggerDetected, rootDetected, emulatorDetected,
            tamperingDetected, hooksDetected, suspiciousBehavior
        ).count { it }
    }
}

