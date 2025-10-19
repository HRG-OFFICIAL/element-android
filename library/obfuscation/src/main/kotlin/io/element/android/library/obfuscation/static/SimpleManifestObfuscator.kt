package io.element.android.library.obfuscation.static

import android.content.Context
import android.util.Log
import java.io.File
import java.util.*

/**
 * Simple Manifest Obfuscation
 * 
 * Provides basic obfuscation for Android manifest components and permissions.
 */
object SimpleManifestObfuscator {

    private const val TAG = "SimpleManifestObf"
    
    // Obfuscation mappings
    private val componentMappings = mutableMapOf<String, String>()
    private val permissionMappings = mutableMapOf<String, String>()
    private val intentFilterMappings = mutableMapOf<String, String>()
    
    // Obfuscation patterns
    private val componentPatterns = listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j")
    private val permissionPatterns = listOf("p0", "p1", "p2", "p3", "p4", "p5", "p6", "p7", "p8", "p9")
    private val intentFilterPatterns = listOf("if0", "if1", "if2", "if3", "if4", "if5", "if6", "if7", "if8", "if9")

    /**
     * Obfuscate manifest components
     * 
     * @param manifestData The original manifest XML content
     * @return Obfuscated manifest XML content
     */
    fun obfuscateManifestComponents(manifestData: String): String {
        try {
            var obfuscatedManifest = manifestData
            
            // Simple regex-based obfuscation
            obfuscatedManifest = obfuscateActivityNames(obfuscatedManifest)
            obfuscatedManifest = obfuscateServiceNames(obfuscatedManifest)
            obfuscatedManifest = obfuscateReceiverNames(obfuscatedManifest)
            obfuscatedManifest = obfuscateProviderNames(obfuscatedManifest)
            obfuscatedManifest = obfuscatePermissionNames(obfuscatedManifest)
            
            return obfuscatedManifest
        } catch (e: Exception) {
            Log.e(TAG, "Error obfuscating manifest components", e)
            return manifestData
        }
    }

    /**
     * Obfuscate activity names
     */
    private fun obfuscateActivityNames(manifestContent: String): String {
        return manifestContent.replace(
            Regex("android:name=\"([^\"]+)\""),
            { matchResult ->
                val originalName = matchResult.groupValues[1]
                val obfuscatedName = generateObfuscatedName(originalName, componentMappings, componentPatterns)
                "android:name=\"$obfuscatedName\""
            }
        )
    }

    /**
     * Obfuscate service names
     */
    private fun obfuscateServiceNames(manifestContent: String): String {
        return manifestContent.replace(
            Regex("android:name=\"([^\"]+)\""),
            { matchResult ->
                val originalName = matchResult.groupValues[1]
                val obfuscatedName = generateObfuscatedName(originalName, componentMappings, componentPatterns)
                "android:name=\"$obfuscatedName\""
            }
        )
    }

    /**
     * Obfuscate receiver names
     */
    private fun obfuscateReceiverNames(manifestContent: String): String {
        return manifestContent.replace(
            Regex("android:name=\"([^\"]+)\""),
            { matchResult ->
                val originalName = matchResult.groupValues[1]
                val obfuscatedName = generateObfuscatedName(originalName, componentMappings, componentPatterns)
                "android:name=\"$obfuscatedName\""
            }
        )
    }

    /**
     * Obfuscate provider names
     */
    private fun obfuscateProviderNames(manifestContent: String): String {
        return manifestContent.replace(
            Regex("android:name=\"([^\"]+)\""),
            { matchResult ->
                val originalName = matchResult.groupValues[1]
                val obfuscatedName = generateObfuscatedName(originalName, componentMappings, componentPatterns)
                "android:name=\"$obfuscatedName\""
            }
        )
    }

    /**
     * Obfuscate permission names
     */
    private fun obfuscatePermissionNames(manifestContent: String): String {
        return manifestContent.replace(
            Regex("android:name=\"([^\"]+)\""),
            { matchResult ->
                val originalName = matchResult.groupValues[1]
                val obfuscatedName = generateObfuscatedName(originalName, permissionMappings, permissionPatterns)
                "android:name=\"$obfuscatedName\""
            }
        )
    }

    /**
     * Generate obfuscated name
     */
    private fun generateObfuscatedName(
        originalName: String,
        mappings: MutableMap<String, String>,
        patterns: List<String>
    ): String {
        return mappings.getOrPut(originalName) {
            val random = Random(originalName.hashCode().toLong())
            val pattern = patterns[random.nextInt(patterns.size)]
            val number = random.nextInt(1000)
            "$pattern$number"
        }
    }

    /**
     * Obfuscate permissions list
     * 
     * @param permissions List of permission strings
     * @return Obfuscated permissions list
     */
    fun obfuscatePermissions(permissions: List<String>): List<String> {
        return permissions.map { permission ->
            generateObfuscatedName(permission, permissionMappings, permissionPatterns)
        }
    }

    /**
     * Obfuscate intent filters list
     * 
     * @param intentFilters List of intent filter strings
     * @return Obfuscated intent filters list
     */
    fun obfuscateIntentFilters(intentFilters: List<String>): List<String> {
        return intentFilters.map { intentFilter ->
            generateObfuscatedName(intentFilter, intentFilterMappings, intentFilterPatterns)
        }
    }

    /**
     * Get obfuscation mappings
     */
    fun getObfuscationMappings(): Map<String, Map<String, String>> {
        return mapOf(
            "components" to componentMappings,
            "permissions" to permissionMappings,
            "intent_filters" to intentFilterMappings
        )
    }

    /**
     * Clear obfuscation mappings
     */
    fun clearMappings() {
        componentMappings.clear()
        permissionMappings.clear()
        intentFilterMappings.clear()
    }

    /**
     * Apply manifest obfuscation to file
     * 
     * @param context Android context
     * @param manifestFile Manifest file path
     * @return Success status
     */
    fun applyManifestObfuscation(context: Context, manifestFile: String): Boolean {
        return try {
            val file = File(manifestFile)
            if (file.exists()) {
                val originalContent = file.readText()
                val obfuscatedContent = obfuscateManifestComponents(originalContent)
                file.writeText(obfuscatedContent)
                Log.d(TAG, "Manifest obfuscation applied successfully")
                true
            } else {
                Log.e(TAG, "Manifest file not found: $manifestFile")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error applying manifest obfuscation", e)
            false
        }
    }
}
