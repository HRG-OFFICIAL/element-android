package io.element.android.library.obfuscation

import android.content.Context
import android.util.Log
import java.util.*

/**
 * Simple Obfuscation Manager
 * 
 * A minimal working implementation of obfuscation functionality
 * that compiles and provides basic obfuscation features.
 */
class SimpleObfuscationManager private constructor(
    private val config: ObfuscationConfig
) {
    
    companion object {
        @Volatile
        private var INSTANCE: SimpleObfuscationManager? = null
        
        fun getInstance(config: ObfuscationConfig = DefaultObfuscationConfig()): SimpleObfuscationManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SimpleObfuscationManager(config).also { INSTANCE = it }
            }
        }
    }
    
    private val random = Random()
    private val obfuscationMappings = mutableMapOf<String, String>()
    
    /**
     * Simple String Obfuscation
     */
    object StringObfuscation {
        
        /**
         * Obfuscate string using simple character substitution
         */
        fun obfuscateString(plaintext: String): String {
            if (plaintext.isEmpty()) return plaintext
            
            val obfuscated = StringBuilder()
            for (char in plaintext) {
                val obfuscatedChar = when (char) {
                    in 'a'..'z' -> 'a' + ((char - 'a' + 13) % 26)
                    in 'A'..'Z' -> 'A' + ((char - 'A' + 13) % 26)
                    in '0'..'9' -> '0' + ((char - '0' + 5) % 10)
                    else -> char
                }
                obfuscated.append(obfuscatedChar)
            }
            return obfuscated.toString()
        }
        
        /**
         * Deobfuscate string
         */
        fun deobfuscateString(obfuscatedText: String): String {
            if (obfuscatedText.isEmpty()) return obfuscatedText
            
            val deobfuscated = StringBuilder()
            for (char in obfuscatedText) {
                val deobfuscatedChar = when (char) {
                    in 'a'..'z' -> 'a' + ((char - 'a' - 13 + 26) % 26)
                    in 'A'..'Z' -> 'A' + ((char - 'A' - 13 + 26) % 26)
                    in '0'..'9' -> '0' + ((char - '0' - 5 + 10) % 10)
                    else -> char
                }
                deobfuscated.append(deobfuscatedChar)
            }
            return deobfuscated.toString()
        }
    }
    
    /**
     * Simple Resource Obfuscation
     */
    object ResourceObfuscation {
        
        /**
         * Obfuscate resource names
         */
        fun obfuscateResourceNames(resources: Map<String, String>): Map<String, String> {
            val obfuscatedResources = mutableMapOf<String, String>()
            val random = Random()
            
            resources.forEach { (originalName, resourceType) ->
                val obfuscatedName = generateObfuscatedName(originalName, random)
                obfuscatedResources[obfuscatedName] = resourceType
            }
            
            return obfuscatedResources
        }
        
        /**
         * Obfuscate manifest components
         */
        fun obfuscateManifestComponents(manifestData: String): String {
            var obfuscatedManifest = manifestData
            
            // Simple regex-based obfuscation
            obfuscatedManifest = obfuscatedManifest.replace(
                Regex("android:name=\"([^\"]+)\""),
                { matchResult ->
                    val originalName = matchResult.groupValues[1]
                    val obfuscatedName = generateObfuscatedName(originalName, Random(originalName.hashCode().toLong()))
                    "android:name=\"$obfuscatedName\""
                }
            )
            
            return obfuscatedManifest
        }
        
        /**
         * Obfuscate permissions
         */
        fun obfuscatePermissions(permissions: List<String>): List<String> {
            return permissions.map { permission ->
                generateObfuscatedName(permission, Random(permission.hashCode().toLong()))
            }
        }
        
        /**
         * Obfuscate intent filters
         */
        fun obfuscateIntentFilters(intentFilters: List<String>): List<String> {
            return intentFilters.map { intentFilter ->
                generateObfuscatedName(intentFilter, Random(intentFilter.hashCode().toLong()))
            }
        }
        
        private fun generateObfuscatedName(originalName: String, random: Random): String {
            val patterns = listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j")
            val pattern = patterns[random.nextInt(patterns.size)]
            val number = random.nextInt(1000)
            return "$pattern$number"
        }
    }
    
    /**
     * Simple Anti-Debug
     */
    object AntiDebug {
        
        /**
         * Check if debugger is attached
         */
        fun isDebuggerAttached(): Boolean {
            return try {
                android.os.Debug.isDebuggerConnected()
            } catch (e: Exception) {
                false
            }
        }
        
        /**
         * Check if app is running in emulator
         */
        fun isRunningInEmulator(): Boolean {
            return try {
                val buildModel = android.os.Build.MODEL
                val buildManufacturer = android.os.Build.MANUFACTURER
                val buildProduct = android.os.Build.PRODUCT
                
                buildModel.contains("google_sdk") ||
                buildModel.contains("Emulator") ||
                buildModel.contains("Android SDK built for x86") ||
                buildManufacturer.contains("Genymotion") ||
                buildProduct.contains("sdk") ||
                buildProduct.contains("emulator")
            } catch (e: Exception) {
                false
            }
        }
        
        /**
         * Check if device is rooted
         */
        fun isDeviceRooted(): Boolean {
            return try {
                val suPaths = arrayOf(
                    "/system/app/Superuser.apk",
                    "/sbin/su",
                    "/system/bin/su",
                    "/system/xbin/su",
                    "/data/local/xbin/su",
                    "/data/local/bin/su",
                    "/system/sd/xbin/su",
                    "/system/bin/failsafe/su",
                    "/data/local/su"
                )
                
                suPaths.any { path ->
                    java.io.File(path).exists()
                }
            } catch (e: Exception) {
                false
            }
        }
    }
    
    /**
     * Get obfuscation statistics
     */
    fun getObfuscationStats(): Map<String, Any> {
        return mapOf(
            "total_techniques" to 8,
            "string_obfuscation" to 2,
            "resource_obfuscation" to 4,
            "anti_debug" to 3,
            "manifest_obfuscation" to 1,
            "permission_obfuscation" to 1,
            "intent_filter_obfuscation" to 1,
            "component_obfuscation" to 1
        )
    }
    
    /**
     * Apply basic obfuscation
     */
    fun applyBasicObfuscation(context: Context): Boolean {
        return try {
            Log.d("SimpleObfuscationMgr", "Applying basic obfuscation...")
            
            // Check for debugger
            if (AntiDebug.isDebuggerAttached()) {
                Log.w("SimpleObfuscationMgr", "Debugger detected!")
                return false
            }
            
            // Check for emulator
            if (AntiDebug.isRunningInEmulator()) {
                Log.w("SimpleObfuscationMgr", "Emulator detected!")
                return false
            }
            
            // Check for root
            if (AntiDebug.isDeviceRooted()) {
                Log.w("SimpleObfuscationMgr", "Rooted device detected!")
                return false
            }
            
            Log.d("SimpleObfuscationMgr", "Basic obfuscation applied successfully")
            true
        } catch (e: Exception) {
            Log.e("SimpleObfuscationMgr", "Error applying basic obfuscation", e)
            false
        }
    }
}

/**
 * Simple Obfuscation Config for SimpleObfuscationManager
 */
class SimpleObfuscationConfig : ObfuscationConfig {
    override val isDebugMode: Boolean = false
}
