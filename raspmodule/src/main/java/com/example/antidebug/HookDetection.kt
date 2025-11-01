package com.example.raspsdk

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/**
 * HookDetection - Comprehensive hooking framework detection
 * 
 * This class implements multiple techniques to detect hooking frameworks:
 * - Frida detection
 * - Xposed framework detection
 * - Substrate detection
 * - Library monitoring
 * - Process monitoring
 * - System call monitoring
 * - API hooking detection
 */
class HookDetection(private val context: Context) {
    
    companion object {
        private const val TAG = "HookDetection"
        
        // Known hooking framework packages
        private val HOOK_PACKAGES = setOf(
            "de.robv.android.xposed.installer",
            "de.robv.android.xposed",
            "com.saurik.substrate",
            "com.cydiasubstrate",
            "com.saurik.cydia",
            "com.topjohnwu.magisk",
            "com.elitehacker.cydia",
            "com.saurik.cydia.updates",
            "com.saurik.cydia.updates.essential",
            "com.saurik.cydia.updates.essential.system",
            "com.saurik.cydia.updates.essential.system.cydia",
            "com.saurik.cydia.updates.essential.system.cydia.essential",
            "com.saurik.cydia.updates.essential.system.cydia.essential.system"
        )
        
        // Known hooking framework libraries
        private val HOOK_LIBRARIES = setOf(
            "libxposed_art.so",
            "libxposed_art.so.1",
            "libxposed_art.so.2",
            "libsubstrate.so",
            "libsubstrate.so.1",
            "libsubstrate.so.2",
            "libcydia.so",
            "libcydia.so.1",
            "libcydia.so.2",
            "libfrida.so",
            "libfrida.so.1",
            "libfrida.so.2",
            "libfrida-gadget.so",
            "libfrida-gadget.so.1",
            "libfrida-gadget.so.2",
            "libhook.so",
            "libhook.so.1",
            "libhook.so.2",
            "libinject.so",
            "libinject.so.1",
            "libinject.so.2",
            "libinjector.so",
            "libinjector.so.1",
            "libinjector.so.2"
        )
        
        // Known hooking framework processes
        private val HOOK_PROCESSES = setOf(
            "frida-server",
            "frida-gadget",
            "xposed",
            "substrate",
            "cydia",
            "magisk",
            "su",
            "daemonsu",
            "supersu",
            "kingroot",
            "kinguser",
            "busybox"
        )
        
        // Known hooking framework files
        private val HOOK_FILES = setOf(
            "/system/framework/XposedBridge.jar",
            "/system/framework/XposedBridge.jar.1",
            "/system/framework/XposedBridge.jar.2",
            "/system/lib/libxposed_art.so",
            "/system/lib/libxposed_art.so.1",
            "/system/lib/libxposed_art.so.2",
            "/system/lib/libsubstrate.so",
            "/system/lib/libsubstrate.so.1",
            "/system/lib/libsubstrate.so.2",
            "/system/lib/libcydia.so",
            "/system/lib/libcydia.so.1",
            "/system/lib/libcydia.so.2",
            "/data/local/tmp/frida-server",
            "/data/local/tmp/frida-gadget",
            "/data/local/tmp/xposed",
            "/data/local/tmp/substrate",
            "/data/local/tmp/cydia",
            "/data/local/tmp/magisk",
            "/data/local/tmp/su",
            "/data/local/tmp/daemonsu",
            "/data/local/tmp/supersu",
            "/data/local/tmp/kingroot",
            "/data/local/tmp/kinguser",
            "/data/local/tmp/busybox"
        )
        
        // JNI native methods
        external fun nativeHookCheck(): Boolean
        external fun nativeFridaCheck(): Boolean
        external fun nativeInlineHookCheck(): Boolean
    }
    
    private val hookCache = ConcurrentHashMap<String, Boolean>()
    
    /**
     * Main method to check if hooking frameworks are detected
     * Combines multiple detection techniques
     */
    fun isHookingDetected(): Boolean {
        return try {
            val checks = listOf(
                ::checkFridaDetection,
                ::checkXposedDetection,
                ::checkSubstrateDetection,
                ::checkLibraryMonitoring,
                ::checkProcessMonitoring,
                ::checkSystemCallMonitoring,
                ::checkApiHookingDetection,
                ::checkHookFiles,
                ::checkHookEnvironment,
                ::checkNativeHookCheck,
                ::checkNativeFridaCheck,
                ::checkNativeInlineHookCheck
            )
            
            // Return true if any check detects hooking
            checks.any { check ->
                try {
                    check.invoke()
                } catch (e: Exception) {
                    Log.w(TAG, "Hook check failed: ${e.message}")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in hook detection", e)
            false
        }
    }
    
    /**
     * Check for Frida framework
     */
    private fun checkFridaDetection(): Boolean {
        return try {
            // Check for Frida processes
            val process = Runtime.getRuntime().exec("ps -A")
            val reader = BufferedReader(process.inputStream.reader())
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val processLine = line!!.lowercase()
                if (processLine.contains("frida") || processLine.contains("frida-server") || 
                    processLine.contains("frida-gadget")) {
                    Log.d(TAG, "Frida process detected: $line")
                    reader.close()
                    return true
                }
            }
            reader.close()
            
            // Check for Frida libraries in memory
            val mapsFile = File("/proc/self/maps")
            if (mapsFile.exists()) {
                BufferedReader(FileReader(mapsFile)).use { mapsReader ->
                    var mapsLine: String?
                    while (mapsReader.readLine().also { mapsLine = it } != null) {
                        val currentLine = mapsLine!!
                        if (currentLine.lowercase().contains("frida")) {
                            Log.d(TAG, "Frida library detected: $currentLine")
                            return true
                        }
                    }
                }
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Frida detection check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check for Xposed framework
     */
    private fun checkXposedDetection(): Boolean {
        return try {
            // Check for Xposed packages
            val packageManager = context.packageManager
            for (packageName in HOOK_PACKAGES) {
                try {
                    packageManager.getPackageInfo(packageName, 0)
                    Log.d(TAG, "Xposed package found: $packageName")
                    return true
                } catch (e: Exception) {
                    // Package not found, continue
                }
            }
            
            // Check for Xposed files
            for (filePath in HOOK_FILES) {
                if (filePath.contains("xposed") && File(filePath).exists()) {
                    Log.d(TAG, "Xposed file found: $filePath")
                    return true
                }
            }
            
            // Check for Xposed libraries in memory
            val mapsFile = File("/proc/self/maps")
            if (mapsFile.exists()) {
                BufferedReader(FileReader(mapsFile)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        val currentLine = line!!
                        if (currentLine.lowercase().contains("xposed")) {
                            Log.d(TAG, "Xposed library detected: $currentLine")
                            return true
                        }
                    }
                }
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Xposed detection check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check for Substrate framework
     */
    private fun checkSubstrateDetection(): Boolean {
        return try {
            // Check for Substrate packages
            val packageManager = context.packageManager
            for (packageName in HOOK_PACKAGES) {
                if (packageName.contains("substrate")) {
                    try {
                        packageManager.getPackageInfo(packageName, 0)
                        Log.d(TAG, "Substrate package found: $packageName")
                        return true
                    } catch (e: Exception) {
                        // Package not found, continue
                    }
                }
            }
            
            // Check for Substrate files
            for (filePath in HOOK_FILES) {
                if (filePath.contains("substrate") && File(filePath).exists()) {
                    Log.d(TAG, "Substrate file found: $filePath")
                    return true
                }
            }
            
            // Check for Substrate libraries in memory
            val mapsFile = File("/proc/self/maps")
            if (mapsFile.exists()) {
                BufferedReader(FileReader(mapsFile)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        val currentLine = line!!
                        if (currentLine.lowercase().contains("substrate")) {
                            Log.d(TAG, "Substrate library detected: $currentLine")
                            return true
                        }
                    }
                }
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Substrate detection check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Monitor loaded libraries for suspicious ones
     */
    private fun checkLibraryMonitoring(): Boolean {
        return try {
            val mapsFile = File("/proc/self/maps")
            if (!mapsFile.exists()) return false
            
            BufferedReader(FileReader(mapsFile)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val currentLine = line!!
                    for (hookLib in HOOK_LIBRARIES) {
                        if (currentLine.lowercase().contains(hookLib.lowercase())) {
                            Log.d(TAG, "Suspicious library detected: $currentLine")
                            return true
                        }
                    }
                }
            }
            false
        } catch (e: Exception) {
            Log.w(TAG, "Library monitoring check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Monitor running processes for hooking tools
     */
    private fun checkProcessMonitoring(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("ps -A")
            val reader = BufferedReader(process.inputStream.reader())
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val processLine = line!!.lowercase()
                for (hookProcess in HOOK_PROCESSES) {
                    if (processLine.contains(hookProcess)) {
                        Log.d(TAG, "Suspicious process detected: $line")
                        reader.close()
                        return true
                    }
                }
            }
            reader.close()
            false
        } catch (e: Exception) {
            Log.w(TAG, "Process monitoring check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check for system call monitoring
     */
    private fun checkSystemCallMonitoring(): Boolean {
        return try {
            // Check for strace/ltrace processes
            val process = Runtime.getRuntime().exec("ps -A")
            val reader = BufferedReader(process.inputStream.reader())
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val processLine = line!!.lowercase()
                if (processLine.contains("strace") || processLine.contains("ltrace")) {
                    Log.d(TAG, "System call monitoring detected: $line")
                    reader.close()
                    return true
                }
            }
            reader.close()
            false
        } catch (e: Exception) {
            Log.w(TAG, "System call monitoring check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check for API hooking detection
     */
    private fun checkApiHookingDetection(): Boolean {
        return try {
            // Check for suspicious method modifications
            val suspiciousMethods = listOf(
                "android.app.Activity.onCreate",
                "android.app.Activity.onResume",
                "android.app.Activity.onPause",
                "android.content.Context.getSystemService",
                "android.content.pm.PackageManager.getPackageInfo",
                "android.content.pm.PackageManager.getInstalledPackages"
            )
            
            for (methodName in suspiciousMethods) {
                try {
                    val parts = methodName.split(".")
                    val className = parts.dropLast(1).joinToString(".")
                    val methodNameOnly = parts.last()
                    
                    val clazz = Class.forName(className)
                    val method = clazz.getDeclaredMethod(methodNameOnly)
                    
                    // Check if method is accessible (might indicate hooking)
                    if (method.isAccessible) {
                        Log.d(TAG, "Suspicious method accessibility: $methodName")
                        return true
                    }
                } catch (e: Exception) {
                    // Method not found or other error, continue
                }
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "API hooking detection check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check for hook-related files
     */
    private fun checkHookFiles(): Boolean {
        return try {
            for (filePath in HOOK_FILES) {
                if (File(filePath).exists()) {
                    Log.d(TAG, "Hook file found: $filePath")
                    return true
                }
            }
            false
        } catch (e: Exception) {
            Log.w(TAG, "Hook files check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check for hook-related environment variables
     */
    private fun checkHookEnvironment(): Boolean {
        return try {
            val hookEnvVars = listOf(
                "FRIDA_DEBUG", "XPOSED_DEBUG", "SUBSTRATE_DEBUG",
                "CYDIASUBSTRATE_DEBUG", "HOOK_DEBUG", "INJECT_DEBUG"
            )
            
            for (envVar in hookEnvVars) {
                val value = System.getenv(envVar)
                if (value != null && value.isNotEmpty()) {
                    Log.d(TAG, "Hook environment variable found: $envVar = $value")
                    return true
                }
            }
            false
        } catch (e: Exception) {
            Log.w(TAG, "Hook environment check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Native hook check via JNI
     */
    private fun checkNativeHookCheck(): Boolean {
        return try {
            nativeHookCheck()
        } catch (e: UnsatisfiedLinkError) {
            Log.w(TAG, "Native hook check unavailable")
            false
        } catch (e: Exception) {
            Log.w(TAG, "Native hook check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Native Frida check via JNI
     */
    private fun checkNativeFridaCheck(): Boolean {
        return try {
            nativeFridaCheck()
        } catch (e: UnsatisfiedLinkError) {
            Log.w(TAG, "Native Frida check unavailable")
            false
        } catch (e: Exception) {
            Log.w(TAG, "Native Frida check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Native inline hook check via JNI
     */
    private fun checkNativeInlineHookCheck(): Boolean {
        return try {
            nativeInlineHookCheck()
        } catch (e: UnsatisfiedLinkError) {
            Log.w(TAG, "Native inline hook check unavailable")
            false
        } catch (e: Exception) {
            Log.w(TAG, "Native inline hook check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Advanced hook detection using scoring system
     */
    fun performAdvancedHookDetection(): Boolean {
        return try {
            var hookScore = 0
            
            // High weight checks
            if (checkFridaDetection()) hookScore += 5
            if (checkXposedDetection()) hookScore += 5
            if (checkSubstrateDetection()) hookScore += 4
            if (checkLibraryMonitoring()) hookScore += 3
            
            // Medium weight checks
            if (checkProcessMonitoring()) hookScore += 3
            if (checkSystemCallMonitoring()) hookScore += 2
            if (checkApiHookingDetection()) hookScore += 2
            if (checkHookFiles()) hookScore += 2
            
            // Low weight checks
            if (checkHookEnvironment()) hookScore += 1
            if (checkNativeHookCheck()) hookScore += 1
            if (checkNativeFridaCheck()) hookScore += 1
            if (checkNativeInlineHookCheck()) hookScore += 1
            
            val isHookingDetected = hookScore >= 3
            Log.d(TAG, "Advanced hook detection score: $hookScore, detected: $isHookingDetected")
            
            isHookingDetected
        } catch (e: Exception) {
            Log.w(TAG, "Advanced hook detection failed: ${e.message}")
            false
        }
    }
    
    /**
     * Get specific hooking framework type
     */
    fun getHookingFrameworkType(): String {
        return try {
            when {
                checkFridaDetection() -> "Frida"
                checkXposedDetection() -> "Xposed"
                checkSubstrateDetection() -> "Substrate"
                checkLibraryMonitoring() -> "Unknown Library Hook"
                checkProcessMonitoring() -> "Unknown Process Hook"
                else -> "None"
            }
        } catch (e: Exception) {
            Log.w(TAG, "Hooking framework type check failed: ${e.message}")
            "Unknown"
        }
    }
    
    /**
     * Clear hook detection cache
     */
    fun clearHookCache() {
        hookCache.clear()
        Log.d(TAG, "Hook detection cache cleared")
    }
}
