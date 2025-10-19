package com.example.antidebug

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.ConcurrentHashMap

/**
 * TamperDetection - Comprehensive application tampering detection
 * 
 * This class implements multiple techniques to detect application tampering:
 * - APK signature verification
 * - DEX file integrity checks
 * - Native library verification
 * - Memory integrity monitoring
 * - Installation source verification
 * - Runtime environment monitoring
 */
class TamperDetection(private val context: Context) {
    
    companion object {
        private const val TAG = "TamperDetection"
        
        // Expected certificate fingerprints (to be set at runtime)
        private var expectedFingerprints = mutableSetOf<String>()
        
        // Known debug certificate fingerprints
        private val DEBUG_FINGERPRINTS = setOf(
            "SHA256: 14:6D:E9:83:C5:73:17:34:02:85:12:8F:32:37:4E:85:D3:ED:F3:AA:8C:0A:BC:10:24:02:1C:60:5D:BE:AB:A6",
            "SHA256: 5E:8F:16:06:2E:A3:28:DE:65:EE:64:1F:6B:25:88:1F:8C:2B:0F:9F:01:41:1D:09:8C:2A:0A:8B:6F:4A:3C:0F"
        )
        
        // Known test certificate fingerprints
        private val TEST_FINGERPRINTS = setOf(
            "SHA256: 00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00"
        )
        
        // JNI native methods
        external fun nativeMemoryCheck(): Boolean
        external fun nativeIntegrityCheck(): Boolean
        external fun nativeBreakpointScan(): Boolean
    }
    
    private val packageManager = context.packageManager
    private val packageName = context.packageName
    private val integrityCache = ConcurrentHashMap<String, String>()
    
    /**
     * Initialize expected certificate fingerprints
     */
    fun initializeFingerprints(fingerprints: Set<String>) {
        expectedFingerprints.clear()
        expectedFingerprints.addAll(fingerprints)
        Log.d(TAG, "Initialized ${fingerprints.size} expected fingerprints")
    }
    
    /**
     * Main method to check if application has been tampered with
     * Combines multiple detection techniques
     */
    fun isApplicationTampered(): Boolean {
        return try {
            val checks = listOf(
                ::checkApkSignature,
                ::checkDexIntegrity,
                ::checkNativeLibraryIntegrity,
                ::checkMemoryIntegrity,
                ::checkInstallationSource,
                ::checkRuntimeEnvironment,
                ::checkClassLoaderIntegrity,
                ::checkApplicationDirectory,
                ::checkNativeMemoryCheck,
                ::checkNativeIntegrityCheck,
                ::checkNativeBreakpointScan
            )
            
            // Return true if any check detects tampering
            checks.any { check ->
                try {
                    check.invoke()
                } catch (e: Exception) {
                    Log.w(TAG, "Tamper check failed: ${e.message}")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in tamper detection", e)
            false
        }
    }
    
    /**
     * Check APK signature for tampering
     */
    private fun checkApkSignature(): Boolean {
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            val signatures = packageInfo.signatures
            
            if (signatures?.isNotEmpty() == true) {
                val signature = signatures[0]
                val fingerprint = calculateFingerprint(signature)
                
                Log.d(TAG, "Current signature fingerprint: $fingerprint")
                
                // Check if signature is debug/test
                if (DEBUG_FINGERPRINTS.contains(fingerprint)) {
                    Log.w(TAG, "Debug signature detected")
                    return true
                }
                
                if (TEST_FINGERPRINTS.contains(fingerprint)) {
                    Log.w(TAG, "Test signature detected")
                    return true
                }
                
                // Check against expected fingerprints
                if (expectedFingerprints.isNotEmpty() && !expectedFingerprints.contains(fingerprint)) {
                    Log.w(TAG, "Unexpected signature fingerprint: $fingerprint")
                    return true
                }
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "APK signature check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check DEX file integrity
     */
    private fun checkDexIntegrity(): Boolean {
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val applicationInfo = packageInfo.applicationInfo
            val sourceDir = applicationInfo?.sourceDir
            
            val dexFile = File(sourceDir)
            if (!dexFile.exists()) {
                Log.w(TAG, "DEX file not found: $sourceDir")
                return true
            }
            
            // Calculate checksum
            val checksum = calculateFileChecksum(dexFile)
            val cachedChecksum = integrityCache["dex_$sourceDir"]
            
            if (cachedChecksum == null) {
                // First time - cache the checksum
                integrityCache["dex_$sourceDir"] = checksum
                Log.d(TAG, "Cached DEX checksum: $checksum")
                return false
            } else if (cachedChecksum != checksum) {
                Log.w(TAG, "DEX checksum mismatch - cached: $cachedChecksum, current: $checksum")
                return true
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "DEX integrity check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check native library integrity
     */
    private fun checkNativeLibraryIntegrity(): Boolean {
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val applicationInfo = packageInfo.applicationInfo
            val nativeLibraryDir = applicationInfo?.nativeLibraryDir
            
            if (nativeLibraryDir != null) {
                val libDir = File(nativeLibraryDir)
                if (libDir.exists()) {
                    val libFiles = libDir.listFiles { file -> file.extension == "so" }
                    
                    for (libFile in libFiles ?: emptyArray()) {
                        val checksum = calculateFileChecksum(libFile)
                        val cachedChecksum = integrityCache["lib_${libFile.name}"]
                        
                        if (cachedChecksum == null) {
                            integrityCache["lib_${libFile.name}"] = checksum
                        } else if (cachedChecksum != checksum) {
                            Log.w(TAG, "Native library checksum mismatch: ${libFile.name}")
                            return true
                        }
                    }
                }
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Native library integrity check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check memory integrity
     */
    private fun checkMemoryIntegrity(): Boolean {
        return try {
            // Check for suspicious memory modifications
            val mapsFile = File("/proc/self/maps")
            if (!mapsFile.exists()) return false
            
            var suspiciousRegions = 0
            mapsFile.readLines().forEach { line ->
                if (line.contains("rwxp")) {
                    suspiciousRegions++
                    Log.d(TAG, "Suspicious memory region: $line")
                }
            }
            
            // advanced threshold: more than 3 suspicious regions
            suspiciousRegions > 3
        } catch (e: Exception) {
            Log.w(TAG, "Memory integrity check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check installation source
     */
    private fun checkInstallationSource(): Boolean {
        return try {
            val installerPackage = packageManager.getInstallerPackageName(packageName)
            
            // Check for suspicious installers
            val suspiciousInstallers = listOf(
                "com.android.vending", // Google Play Store (legitimate)
                "com.amazon.venezia",  // Amazon Appstore (legitimate)
                "com.fdroid.fdroid",   // F-Droid (legitimate)
                null,                  // Sideloaded (suspicious)
                "com.unknown.installer" // Unknown installer (suspicious)
            )
            
            if (installerPackage == null) {
                Log.w(TAG, "Application was sideloaded (no installer package)")
                return true
            }
            
            if (installerPackage == "com.unknown.installer") {
                Log.w(TAG, "Unknown installer detected: $installerPackage")
                return true
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Installation source check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check runtime environment for tampering
     */
    private fun checkRuntimeEnvironment(): Boolean {
        return try {
            val tamperIndicators = listOf(
                // Check for debug build
                context.packageManager.getPackageInfo(packageName, 0).versionName?.contains("debug") == true,
                context.packageManager.getPackageInfo(packageName, 0).versionName?.contains("test") == true,
                
                // Check for debug VM
                System.getProperty("java.vm.name").contains("debug"),
                
                // Check for development mode
                System.getProperty("java.vm.version").contains("debug"),
                
                // Check for test keys
                android.os.Build.TAGS.contains("test-keys")
            )
            
            tamperIndicators.any { it }
        } catch (e: Exception) {
            Log.w(TAG, "Runtime environment check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check class loader integrity
     */
    private fun checkClassLoaderIntegrity(): Boolean {
        return try {
            val classLoader = javaClass.classLoader
            val classLoaderClass = classLoader.javaClass
            
            // Check for suspicious class loader modifications
            val suspiciousMethods = listOf(
                "loadClass", "findClass", "defineClass", "resolveClass"
            )
            
            for (methodName in suspiciousMethods) {
                try {
                    val method = classLoaderClass.getDeclaredMethod(methodName, String::class.java)
                    if (method.isAccessible) {
                        Log.w(TAG, "Suspicious class loader method accessible: $methodName")
                        return true
                    }
                } catch (e: NoSuchMethodException) {
                    // Method not found, continue
                }
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Class loader integrity check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check application directory for suspicious files
     */
    private fun checkApplicationDirectory(): Boolean {
        return try {
            val dataDir = context.filesDir.parentFile
            if (dataDir != null && dataDir.exists()) {
                val suspiciousFiles = listOf(
                    "frida", "xposed", "substrate", "cydia", "hook",
                    "inject", "patch", "mod", "crack", "hack"
                )
                
                val files = dataDir.listFiles()
                for (file in files ?: emptyArray()) {
                    val fileName = file.name.lowercase()
                    for (suspicious in suspiciousFiles) {
                        if (fileName.contains(suspicious)) {
                            Log.w(TAG, "Suspicious file found: ${file.name}")
                            return true
                        }
                    }
                }
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Application directory check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Native memory check via JNI
     */
    private fun checkNativeMemoryCheck(): Boolean {
        return try {
            nativeMemoryCheck()
        } catch (e: UnsatisfiedLinkError) {
            Log.w(TAG, "Native memory check unavailable")
            false
        } catch (e: Exception) {
            Log.w(TAG, "Native memory check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Native integrity check via JNI
     */
    private fun checkNativeIntegrityCheck(): Boolean {
        return try {
            nativeIntegrityCheck()
        } catch (e: UnsatisfiedLinkError) {
            Log.w(TAG, "Native integrity check unavailable")
            false
        } catch (e: Exception) {
            Log.w(TAG, "Native integrity check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Native breakpoint scan via JNI
     */
    private fun checkNativeBreakpointScan(): Boolean {
        return try {
            nativeBreakpointScan()
        } catch (e: UnsatisfiedLinkError) {
            Log.w(TAG, "Native breakpoint scan unavailable")
            false
        } catch (e: Exception) {
            Log.w(TAG, "Native breakpoint scan failed: ${e.message}")
            false
        }
    }
    
    /**
     * Calculate SHA-256 fingerprint of signature
     */
    private fun calculateFingerprint(signature: Signature): String {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val hash = md.digest(signature.toByteArray())
            val hexString = StringBuilder()
            
            for (byte in hash) {
                val hex = Integer.toHexString(0xFF and byte.toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }
            
            "SHA256: ${hexString.toString().chunked(2).joinToString(":")}"
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "SHA-256 algorithm not available", e)
            ""
        }
    }
    
    /**
     * Calculate MD5 checksum of file
     */
    private fun calculateFileChecksum(file: File): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val fis = FileInputStream(file)
            val buffer = ByteArray(8192)
            var bytesRead: Int
            
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                md.update(buffer, 0, bytesRead)
            }
            
            fis.close()
            
            val hash = md.digest()
            val hexString = StringBuilder()
            
            for (byte in hash) {
                val hex = Integer.toHexString(0xFF and byte.toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }
            
            hexString.toString()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to calculate file checksum", e)
            ""
        }
    }
    
    /**
     * Advanced tamper detection using scoring system
     */
    fun performAdvancedTamperDetection(): Boolean {
        return try {
            var tamperScore = 0
            
            // High weight checks
            if (checkApkSignature()) tamperScore += 5
            if (checkDexIntegrity()) tamperScore += 4
            if (checkNativeLibraryIntegrity()) tamperScore += 4
            if (checkMemoryIntegrity()) tamperScore += 3
            
            // Medium weight checks
            if (checkInstallationSource()) tamperScore += 2
            if (checkRuntimeEnvironment()) tamperScore += 2
            if (checkClassLoaderIntegrity()) tamperScore += 2
            if (checkApplicationDirectory()) tamperScore += 2
            
            // Low weight checks
            if (checkNativeMemoryCheck()) tamperScore += 1
            if (checkNativeIntegrityCheck()) tamperScore += 1
            if (checkNativeBreakpointScan()) tamperScore += 1
            
            val isTampered = tamperScore >= 3
            Log.d(TAG, "Advanced tamper detection score: $tamperScore, tampered: $isTampered")
            
            isTampered
        } catch (e: Exception) {
            Log.w(TAG, "Advanced tamper detection failed: ${e.message}")
            false
        }
    }
    
    /**
     * Clear integrity cache (useful for testing)
     */
    fun clearIntegrityCache() {
        integrityCache.clear()
        Log.d(TAG, "Integrity cache cleared")
    }
}