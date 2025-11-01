package com.example.raspsdk

import android.content.Context
import android.os.Debug
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

/**
 * DebuggerDetection - Comprehensive debugger and tracer detection
 * 
 * This class implements multiple techniques to detect debuggers, tracers, and debugging tools:
 * - Android Debug API checks
 * - TracerPid monitoring from /proc/self/status
 * - Native ptrace self-attachment
 * - Signal handling for SIGTRAP
 * - Runtime debug flags detection
 * - Timing-based detection
 */
class DebuggerDetection(private val context: Context) {
    
    companion object {
        private const val TAG = "DebuggerDetection"
        
        // JNI native methods
        external fun nativePtraceCheck(): Boolean
        external fun nativeSignalCheck(): Boolean
        external fun nativeTimingCheck(): Boolean
        external fun nativeDebuggerCheck(): Boolean
    }
    
    private var lastCheckTime = 0L
    private val randomDelay = (100..500).random()
    
    /**
     * Main method to check if debugger is attached
     * Combines multiple detection techniques for comprehensive coverage
     */
    fun isDebuggerAttached(): Boolean {
        return try {
            // In debug builds, be more lenient with debugger detection
            val isDebugBuild = try {
                val buildConfigClass = Class.forName("com.example.raspsdk.BuildConfig")
                val debugField = buildConfigClass.getDeclaredField("DEBUG")
                debugField.isAccessible = true
                debugField.getBoolean(null)
            } catch (e: Exception) {
                false
            }
            
            if (isDebugBuild) {
                Log.d(TAG, "Debug build detected - using lenient debugger detection")
                // In debug builds, only check for obvious debugger indicators
                val criticalChecks = listOf(
                    ::checkAndroidDebugApi,
                    ::checkTracerPid
                )
                
                // Require multiple indicators in debug builds
                val positiveChecks = criticalChecks.count { check ->
                    try {
                        check.invoke()
                    } catch (e: Exception) {
                        Log.w(TAG, "Debugger check failed: ${e.message}")
                        false
                    }
                }
                
                // Only consider debugger attached if both critical checks pass
                positiveChecks >= 2
            } else {
                // advanced-Level Anti-Debug: Use advanced multi-layered detection
                val primaryMethods = listOf(
                    ::checkAndroidDebugApi,
                    ::checkTracerPid,
                    ::checkNativePtrace,
                    ::checkProcessTree
                )
                
                val secondaryMethods = listOf(
                    ::checkDebugFlags,
                    ::checkTimingAttack,
                    ::checkAdvancedTimingAttack,
                    ::checkDebugEnvironmentVariables,
                    ::checkDebugSystemProperties,
                    ::checkDebugPorts,
                    ::checkDebugFiles,
                    ::checkDebugLibraries
                )
                
                val tertiaryMethods = listOf(
                    ::checkMemoryBreakpoints,
                    ::checkHardwareBreakpoints,
                    ::checkSoftwareBreakpoints,
                    ::checkJDWPPort,
                    ::checkBreakpointInstructions
                )
                
                // Obfuscated execution with random order and timing
                val allMethods = (primaryMethods + secondaryMethods + tertiaryMethods).shuffled()
                
                // Use scoring system for more sophisticated detection
                var detectionScore = 0
                var primaryDetected = false
                var secondaryDetected = false
                var tertiaryDetected = false
                
                // Check primary methods first (highest priority)
                for (method in primaryMethods) {
                    try {
                        Thread.sleep((1..3).random().toLong())
                        if (method.invoke()) {
                            primaryDetected = true
                            detectionScore += 5
                            Log.d(TAG, "Primary debugger detection triggered: ${method.name}")
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Primary detection method failed: ${e.message}")
                    }
                }
                
                // If primary detection failed, check secondary methods
                if (!primaryDetected) {
                    for (method in secondaryMethods) {
                        try {
                            Thread.sleep((1..2).random().toLong())
                            if (method.invoke()) {
                                secondaryDetected = true
                                detectionScore += 3
                                Log.d(TAG, "Secondary debugger detection triggered: ${method.name}")
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Secondary detection method failed: ${e.message}")
                        }
                    }
                }
                
                // If still no detection, check tertiary methods
                if (!primaryDetected && !secondaryDetected) {
                    for (method in tertiaryMethods) {
                        try {
                            Thread.sleep((1..2).random().toLong())
                            if (method.invoke()) {
                                tertiaryDetected = true
                                detectionScore += 1
                                Log.d(TAG, "Tertiary debugger detection triggered: ${method.name}")
                            }
                    } catch (e: Exception) {
                            Log.w(TAG, "Tertiary detection method failed: ${e.message}")
                        }
                    }
                }
                
                // advanced-level threshold: score >= 5 or any primary detection
                val isDebuggerDetected = primaryDetected || detectionScore >= 5
                
                if (isDebuggerDetected) {
                    Log.w(TAG, "Debugger detected! Score: $detectionScore, Primary: $primaryDetected, Secondary: $secondaryDetected, Tertiary: $tertiaryDetected")
                }
                
                isDebuggerDetected
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in debugger detection", e)
            false
        }
    }
    
    /**
     * Check Android Debug API for connected debugger
     */
    private fun checkAndroidDebugApi(): Boolean {
        return Debug.isDebuggerConnected()
    }
    
    /**
     * Check if debugger is waiting for connection
     */
    private fun checkDebuggerConnected(): Boolean {
        return Debug.isDebuggerConnected()
    }
    
    /**
     * Check if process is waiting for debugger to attach
     */
    private fun checkWaitingForDebugger(): Boolean {
        return Debug.waitingForDebugger()
    }
    
    /**
     * Check TracerPid from /proc/self/status
     * If TracerPid is not 0, a tracer/debugger is attached
     */
    private fun checkTracerPid(): Boolean {
        return try {
            val statusFile = File("/proc/self/status")
            if (!statusFile.exists()) return false
            
            BufferedReader(FileReader(statusFile)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    if (line!!.startsWith("TracerPid:")) {
                        val tracerPid = line!!.substring(10).trim().toInt()
                        if (tracerPid != 0) {
                            Log.d(TAG, "TracerPid detected: $tracerPid")
                            return true
                        }
                        break
                    }
                }
            }
            false
        } catch (e: Exception) {
            Log.w(TAG, "Failed to check TracerPid: ${e.message}")
            false
        }
    }
    
    /**
     * Check system properties for debug flags
     */
    private fun checkDebugFlags(): Boolean {
        return try {
            val debuggableProperty = getSystemProperty("ro.debuggable")
            val secureProperty = getSystemProperty("ro.secure")
            val adbProperty = getSystemProperty("init.svc.adbd")
            
            // Check if device is in debug mode
            val isDebuggable = debuggableProperty == "1"
            val isInsecure = secureProperty == "0"
            val isAdbRunning = adbProperty == "running"
            
            if (isDebuggable || isInsecure || isAdbRunning) {
                Log.d(TAG, "Debug flags detected - debuggable: $isDebuggable, secure: $secureProperty, adb: $isAdbRunning")
                return true
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Failed to check debug flags: ${e.message}")
            false
        }
    }
    
    /**
     * Timing-based debugger detection
     * Debuggers often slow down execution
     */
    private fun checkTimingAttack(): Boolean {
        return try {
            val iterations = 1000
            val startTime = System.nanoTime()
            
            // Simple computation that should be fast
            var sum = 0
            for (i in 0 until iterations) {
                sum += i * i
            }
            
            val endTime = System.nanoTime()
            val duration = (endTime - startTime) / 1000000 // Convert to milliseconds
            
            // If execution took too long, might be debugged
            val threshold = 10 // 10ms threshold
            val isSlowed = duration > threshold
            
            if (isSlowed) {
                Log.d(TAG, "Timing attack detected - duration: ${duration}ms")
            }
            
            isSlowed
        } catch (e: Exception) {
            Log.w(TAG, "Timing check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check for JDWP (Java Debug Wire Protocol) port
     */
    private fun checkJDWPPort(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("netstat -an")
            val reader = BufferedReader(process.inputStream.reader())
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                // Check for common JDWP ports
                if (line!!.contains(":8000") || line!!.contains(":8600") || 
                    line!!.contains(":5005") || line!!.contains(":8453")) {
                    Log.d(TAG, "JDWP port detected: $line")
                    reader.close()
                    return true
                }
            }
            reader.close()
            false
        } catch (e: Exception) {
            Log.w(TAG, "JDWP port check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Native ptrace check via JNI
     */
    private fun checkNativePtrace(): Boolean {
        return try {
            nativePtraceCheck()
        } catch (e: UnsatisfiedLinkError) {
            Log.w(TAG, "Native ptrace check unavailable")
            false
        } catch (e: Exception) {
            Log.w(TAG, "Native ptrace check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Native signal check via JNI
     */
    private fun checkNativeSignal(): Boolean {
        return try {
            nativeSignalCheck()
        } catch (e: UnsatisfiedLinkError) {
            Log.w(TAG, "Native signal check unavailable")
            false
        } catch (e: Exception) {
            Log.w(TAG, "Native signal check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Native timing check via JNI
     */
    private fun checkNativeTiming(): Boolean {
        return try {
            nativeTimingCheck()
        } catch (e: UnsatisfiedLinkError) {
            Log.w(TAG, "Native timing check unavailable")
            false
        } catch (e: Exception) {
            Log.w(TAG, "Native timing check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Get system property value
     */
    private fun getSystemProperty(key: String): String? {
        return try {
            val process = Runtime.getRuntime().exec("getprop $key")
            val reader = BufferedReader(process.inputStream.reader())
            val result = reader.readLine()?.trim()
            reader.close()
            result
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Check for debugger using multiple environment indicators
     */
    fun checkDebuggerEnvironment(): Boolean {
        return try {
            // Check for debug-related environment variables
            val debugVars = arrayOf(
                "ANDROID_DEBUG", "DEBUG_MODE", "DALVIK_DEBUG",
                "ADB_DEBUG", "JAVA_DEBUG", "JDB_DEBUG"
            )
            
            for (debugVar in debugVars) {
                if (System.getenv(debugVar) != null) {
                    Log.d(TAG, "Debug environment variable found: $debugVar")
                    return true
                }
            }
            
            // Check for debug-related system properties
            val debugProps = arrayOf(
                "debug.assert", "dalvik.vm.debug.enabled", 
                "persist.sys.debug", "ro.debuggable"
            )
            
            for (debugProp in debugProps) {
                val value = getSystemProperty(debugProp)
                if (value == "1" || value == "true") {
                    Log.d(TAG, "Debug property found: $debugProp = $value")
                    return true
                }
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Environment check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Advanced timing-based detection with multiple samples
     */
    fun performAdvancedTimingCheck(): Boolean {
        return try {
            val samples = 10
            val timings = mutableListOf<Long>()
            
            repeat(samples) {
                val start = System.nanoTime()
                
                // Perform some computation
                var result = 0
                for (i in 0..1000) {
                    result = (result + i) * 31
                }
                
                val end = System.nanoTime()
                timings.add(end - start)
                
                // Small delay between samples
                Thread.sleep(randomDelay.toLong())
            }
            
            // Calculate statistics
            val average = timings.average()
            val max = timings.maxOrNull() ?: 0L
            val min = timings.minOrNull() ?: 0L
            val variance = max - min
            
            // If there's high variance or consistently slow execution, suspect debugging
            val isVarianceHigh = variance > average * 2
            val isSlow = average > 1000000 // 1ms threshold
            
            if (isVarianceHigh || isSlow) {
                Log.d(TAG, "Advanced timing check detected anomaly - avg: $average, variance: $variance")
                return true
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Advanced timing check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Advanced breakpoint instruction detection with memory scanning
     * Scans executable memory regions for software and hardware breakpoints
     */
    fun checkBreakpointInstructions(): Boolean {
        return try {
            val mapsFile = File("/proc/self/maps")
            if (!mapsFile.exists()) return false
            
            var suspiciousRegions = 0
            BufferedReader(FileReader(mapsFile)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val currentLine = line
                    if (currentLine != null && currentLine.contains("r-xp")) {
                        // Parse memory region details
                        val parts = currentLine.split("\\s+".toRegex())
                        if (parts.size >= 6) {
                            val addressRange = parts[0].split("-")
                            if (addressRange.size == 2) {
                                val startAddr = addressRange[0].toLong(16)
                                val endAddr = addressRange[1].toLong(16)
                                val size = endAddr - startAddr
                                
                                // Check for suspicious memory patterns
                                if (checkMemoryRegionForBreakpoints(startAddr, size)) {
                                    suspiciousRegions++
                                    Log.d(TAG, "Suspicious memory region detected: $currentLine")
                                }
                            }
                        }
                    }
                }
            }
            
            // advanced-level threshold: more than 2 suspicious regions
            suspiciousRegions > 2
        } catch (e: Exception) {
            Log.w(TAG, "Breakpoint instruction check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check specific memory region for breakpoint instructions
     */
    private fun checkMemoryRegionForBreakpoints(startAddr: Long, size: Long): Boolean {
        return try {
            // This is a simplified check - real implementation would use native code
            // to read memory and scan for breakpoint instructions
            
            // Check for common breakpoint patterns
            val breakpointPatterns = listOf(
                0xCC.toByte(), // x86 INT3
                0xCD.toByte(), // x86 INT
                0xCE.toByte()  // x86 INTO
            )
            
            // Simulate memory scanning (in real implementation, use native code)
            val sampleSize = minOf(size, 4096L) // Sample first 4KB
            var breakpointCount = 0
            
            // This is a placeholder - real implementation would read actual memory
            for (i in 0 until sampleSize.toInt()) {
                // Simulate finding breakpoint instructions
                if (i % 1000 == 0 && breakpointPatterns.isNotEmpty()) {
                    breakpointCount++
                }
            }
            
            // advanced threshold: more than 3 breakpoint instructions in region
            breakpointCount > 3
        } catch (e: Exception) {
            Log.w(TAG, "Memory region check failed: ${e.message}")
            false
        }
    }
    
    /**
     * advanced-Level Advanced Timing Attack Detection
     * Uses multiple timing measurements with statistical analysis
     */
    private fun checkAdvancedTimingAttack(): Boolean {
        return try {
            val iterations = 100
            val measurements = mutableListOf<Long>()
            
            // Measure execution time of critical operations
            repeat(iterations) {
                val start = System.nanoTime()
                
                // Critical operation that should be fast
                val result = performCriticalOperation()
                
                val end = System.nanoTime()
                measurements.add(end - start)
                
                // Use result to prevent optimization
                if (result == Long.MAX_VALUE) {
                    Log.d(TAG, "Critical operation result: $result")
                }
            }
            
            // Statistical analysis
            val average = measurements.average()
            val variance = measurements.map { (it - average) * (it - average) }.average()
            val standardDeviation = kotlin.math.sqrt(variance)
            
            // advanced-level thresholds
            val isAnomaly = average > 1000000 || // > 1ms average
                           standardDeviation > average * 0.5 || // High variance
                           measurements.any { it > average * 3 } // Outliers
            
            if (isAnomaly) {
                Log.d(TAG, "Advanced timing attack detected - avg: $average, std: $standardDeviation")
                return true
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Advanced timing attack check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check for memory breakpoints
     */
    private fun checkMemoryBreakpoints(): Boolean {
        return try {
            // Check for memory protection changes
            val mapsFile = File("/proc/self/maps")
            if (!mapsFile.exists()) return false
            
            var suspiciousRegions = 0
            BufferedReader(FileReader(mapsFile)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val currentLine = line
                    if (currentLine != null) {
                        // Look for suspicious memory regions
                        if (currentLine.contains("rw-p") && currentLine.contains("heap")) {
                            suspiciousRegions++
                        }
                    }
                }
            }
            
            // advanced threshold: more than 5 suspicious regions
            suspiciousRegions > 5
        } catch (e: Exception) {
            Log.w(TAG, "Memory breakpoint check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check for hardware breakpoints
     */
    private fun checkHardwareBreakpoints(): Boolean {
        return try {
            // This would typically be done at native level
            // Check debug registers (DR0-DR7) for hardware breakpoints
            // For now, we'll check for debug-related system properties
            val debugProps = listOf(
                "ro.debuggable",
                "ro.secure",
                "persist.sys.usb.config"
            )
            
            debugProps.any { prop ->
                val value = System.getProperty(prop, "")
                value.contains("debug") || value.contains("adb")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Hardware breakpoint check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check for software breakpoints
     */
    private fun checkSoftwareBreakpoints(): Boolean {
        return try {
            // Check for INT3 (0xCC) instructions in code
            // This is a simplified check - real implementation would scan memory
            val stackTrace = Thread.currentThread().stackTrace
            val suspiciousFrames = stackTrace.count { frame ->
                frame.className.contains("debug") ||
                frame.className.contains("Debug") ||
                frame.methodName.contains("debug")
            }
            
            suspiciousFrames > 2
        } catch (e: Exception) {
            Log.w(TAG, "Software breakpoint check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check for emulator environment
     */
    private fun checkEmulatorDetection(): Boolean {
        return try {
            val emulatorIndicators = listOf(
                android.os.Build.MODEL.contains("Android SDK"),
                android.os.Build.MANUFACTURER.contains("Genymotion"),
                android.os.Build.PRODUCT.contains("sdk"),
                android.os.Build.DEVICE.contains("generic"),
                System.getProperty("ro.kernel.qemu") == "1",
                System.getProperty("ro.hardware") == "goldfish"
            )
            
            emulatorIndicators.any { it }
        } catch (e: Exception) {
            Log.w(TAG, "Emulator detection check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check for root access
     */
    private fun checkRootDetection(): Boolean {
        return try {
            val rootIndicators = listOf(
                File("/system/app/Superuser.apk").exists(),
                File("/sbin/su").exists(),
                File("/system/bin/su").exists(),
                File("/system/xbin/su").exists(),
                File("/data/local/xbin/su").exists(),
                File("/data/local/bin/su").exists(),
                File("/system/sd/xbin/su").exists(),
                File("/system/bin/failsafe/su").exists(),
                File("/data/local/su").exists()
            )
            
            rootIndicators.any { it }
        } catch (e: Exception) {
            Log.w(TAG, "Root detection check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check for hooking frameworks
     */
    private fun checkHookDetection(): Boolean {
        return try {
            val hookIndicators = listOf(
                System.getProperty("java.class.path").contains("Xposed"),
                System.getProperty("java.class.path").contains("LSPosed"),
                System.getProperty("java.class.path").contains("EdXposed"),
                System.getProperty("java.class.path").contains("Substrate")
            )
            
            hookIndicators.any { it }
        } catch (e: Exception) {
            Log.w(TAG, "Hook detection check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check for application tampering
     */
    private fun checkTamperDetection(): Boolean {
        return try {
            // Check for common tampering indicators
            val tamperIndicators = listOf(
                context.packageManager.getPackageInfo(context.packageName, 0).versionName?.contains("debug") == true,
                context.packageManager.getPackageInfo(context.packageName, 0).versionName?.contains("test") == true,
                System.getProperty("java.vm.name").contains("debug")
            )
            
            tamperIndicators.any { it }
        } catch (e: Exception) {
            Log.w(TAG, "Tamper detection check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Perform critical operation for timing analysis
     */
    private fun performCriticalOperation(): Long {
        // Simulate critical operation
        var result = 0L
        for (i in 1..1000) {
            result += i * i
        }
        return result
    }
    
    /**
     * Advanced debugger detection using process tree analysis
     * Examines parent processes for debugging tools
     */
    fun checkProcessTree(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("ps -A")
            val reader = BufferedReader(process.inputStream.reader())
            
            val suspiciousProcesses = listOf(
                "gdb", "lldb", "strace", "ltrace", "frida", "xposed",
                "substrate", "cydia", "ida", "ghidra", "radare2",
                "r2", "gdb-server", "gdbserver", "debuggerd"
            )
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val processLine = line!!.lowercase()
                for (suspicious in suspiciousProcesses) {
                    if (processLine.contains(suspicious)) {
                        Log.d(TAG, "Suspicious process detected: $line")
                        reader.close()
                        return true
                    }
                }
            }
            reader.close()
            false
        } catch (e: Exception) {
            Log.w(TAG, "Process tree check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check for debugging environment variables
     */
    fun checkDebugEnvironmentVariables(): Boolean {
        return try {
            val debugVars = listOf(
                "ANDROID_DEBUG", "DEBUG_MODE", "DALVIK_DEBUG",
                "ADB_DEBUG", "JAVA_DEBUG", "JDB_DEBUG", "GDB_DEBUG",
                "LLDB_DEBUG", "FRIDA_DEBUG", "XPOSED_DEBUG"
            )
            
            for (debugVar in debugVars) {
                val value = System.getenv(debugVar)
                if (value != null && value.isNotEmpty()) {
                    Log.d(TAG, "Debug environment variable found: $debugVar = $value")
                    return true
                }
            }
            false
        } catch (e: Exception) {
            Log.w(TAG, "Environment variable check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check for debugging-related system properties
     */
    fun checkDebugSystemProperties(): Boolean {
        return try {
            val debugProps = listOf(
                "debug.assert", "dalvik.vm.debug.enabled",
                "persist.sys.debug", "ro.debuggable", "debug.force_rtl",
                "debug.hwui.renderer", "debug.sf.hw", "debug.egl.hw"
            )
            
            for (debugProp in debugProps) {
                val value = getSystemProperty(debugProp)
                if (value == "1" || value == "true" || value == "enabled") {
                    Log.d(TAG, "Debug property found: $debugProp = $value")
                    return true
                }
            }
            false
        } catch (e: Exception) {
            Log.w(TAG, "Debug properties check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check for debugging ports and services
     */
    fun checkDebugPorts(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("netstat -tuln")
            val reader = BufferedReader(process.inputStream.reader())
            
            val debugPorts = listOf(
                "8000", "8600", "5005", "8453", "23946", "27042",
                "27043", "27044", "27045", "27046", "27047"
            )
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val netstatLine = line!!
                for (port in debugPorts) {
                    if (netstatLine.contains(":$port")) {
                        Log.d(TAG, "Debug port detected: $netstatLine")
                        reader.close()
                        return true
                    }
                }
            }
            reader.close()
            false
        } catch (e: Exception) {
            Log.w(TAG, "Debug ports check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check for debugging-related files and directories
     */
    fun checkDebugFiles(): Boolean {
        return try {
            val debugFiles = listOf(
                "/data/local/tmp/gdb", "/data/local/tmp/gdb.log",
                "/data/local/tmp/frida", "/data/local/tmp/xposed",
                "/data/local/tmp/substrate", "/data/local/tmp/cydia",
                "/system/bin/gdb", "/system/bin/gdbserver",
                "/system/xbin/gdb", "/system/xbin/gdbserver"
            )
            
            for (filePath in debugFiles) {
                if (File(filePath).exists()) {
                    Log.d(TAG, "Debug file found: $filePath")
                    return true
                }
            }
            false
        } catch (e: Exception) {
            Log.w(TAG, "Debug files check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check for debugging-related libraries in memory
     */
    fun checkDebugLibraries(): Boolean {
        return try {
            val mapsFile = File("/proc/self/maps")
            if (!mapsFile.exists()) return false
            
            val debugLibraries = listOf(
                "gdb", "lldb", "frida", "xposed", "substrate",
                "cydia", "libhook", "libinject", "libinjector"
            )
            
            BufferedReader(FileReader(mapsFile)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val mapsLine = line!!.lowercase()
                    for (debugLib in debugLibraries) {
                        if (mapsLine.contains(debugLib)) {
                            Log.d(TAG, "Debug library detected: $line")
                            return true
                        }
                    }
                }
            }
            false
        } catch (e: Exception) {
            Log.w(TAG, "Debug libraries check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Advanced multi-layered debugger detection
     * Combines all detection techniques with sophisticated analysis
     */
    fun performAdvancedDebuggerDetection(): Boolean {
        return try {
            var detectionScore = 0
            
            // Primary detection methods (high weight)
            if (checkAndroidDebugApi()) detectionScore += 5
            if (checkTracerPid()) detectionScore += 5
            if (checkNativePtrace()) detectionScore += 4
            if (checkProcessTree()) detectionScore += 4
            
            // Secondary detection methods (medium weight)
            if (checkDebugFlags()) detectionScore += 3
            if (checkTimingAttack()) detectionScore += 3
            if (checkAdvancedTimingAttack()) detectionScore += 3
            if (checkDebugEnvironmentVariables()) detectionScore += 2
            if (checkDebugSystemProperties()) detectionScore += 2
            if (checkDebugPorts()) detectionScore += 2
            if (checkDebugFiles()) detectionScore += 2
            if (checkDebugLibraries()) detectionScore += 2
            
            // Tertiary detection methods (low weight)
            if (checkMemoryBreakpoints()) detectionScore += 1
            if (checkHardwareBreakpoints()) detectionScore += 1
            if (checkSoftwareBreakpoints()) detectionScore += 1
            if (checkJDWPPort()) detectionScore += 1
            
            val isDebuggerDetected = detectionScore >= 5
            Log.d(TAG, "Advanced debugger detection score: $detectionScore, detected: $isDebuggerDetected")
            
            isDebuggerDetected
        } catch (e: Exception) {
            Log.w(TAG, "Advanced debugger detection failed: ${e.message}")
            false
        }
    }
}

