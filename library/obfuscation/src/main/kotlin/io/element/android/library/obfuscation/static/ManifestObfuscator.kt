package io.element.android.library.obfuscation.static

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import java.io.File
import java.util.*

/**
 * Manifest Obfuscator
 * 
 * Provides comprehensive manifest obfuscation for Android applications including:
 * - Component name obfuscation (Activities, Services, Receivers, Providers)
 * - Permission obfuscation and filtering
 * - Intent filter obfuscation
 * - Metadata obfuscation
 * - Application attribute obfuscation
 */
object ManifestObfuscator {

    private const val TAG = "ManifestObfuscator"
    
    // Obfuscated component name mappings
    private val componentMappings = mutableMapOf<String, String>()
    private val permissionMappings = mutableMapOf<String, String>()
    private val intentFilterMappings = mutableMapOf<String, String>()
    
    /**
     * Component Name Obfuscator
     * Obfuscates Android manifest components (Activities, Services, Receivers, Providers)
     */
    object ComponentNameObfuscator {
        
        /**
         * Obfuscate activity names in manifest
         */
        fun obfuscateActivityNames(manifestContent: String): String {
            var obfuscatedContent = manifestContent
            
            // Find all activity declarations
            val activityPattern = """<activity\s+android:name="([^"]+)"""".toRegex()
            val activities = activityPattern.findAll(manifestContent).map { it.groupValues[1] }.toList()
            
            activities.forEach { activityName ->
                val obfuscatedName = generateObfuscatedComponentName(activityName, "activity")
                componentMappings[activityName] = obfuscatedName
                obfuscatedContent = obfuscatedContent.replace(activityName, obfuscatedName)
            }
            
            return obfuscatedContent
        }
        
        /**
         * Obfuscate service names in manifest
         */
        fun obfuscateServiceNames(manifestContent: String): String {
            var obfuscatedContent = manifestContent
            
            // Find all service declarations
            val servicePattern = """<service\s+android:name="([^"]+)"""".toRegex()
            val services = servicePattern.findAll(manifestContent).map { it.groupValues[1] }.toList()
            
            services.forEach { serviceName ->
                val obfuscatedName = generateObfuscatedComponentName(serviceName, "service")
                componentMappings[serviceName] = obfuscatedName
                obfuscatedContent = obfuscatedContent.replace(serviceName, obfuscatedName)
            }
            
            return obfuscatedContent
        }
        
        /**
         * Obfuscate receiver names in manifest
         */
        fun obfuscateReceiverNames(manifestContent: String): String {
            var obfuscatedContent = manifestContent
            
            // Find all receiver declarations
            val receiverPattern = """<receiver\s+android:name="([^"]+)"""".toRegex()
            val receivers = receiverPattern.findAll(manifestContent).map { it.groupValues[1] }.toList()
            
            receivers.forEach { receiverName ->
                val obfuscatedName = generateObfuscatedComponentName(receiverName, "receiver")
                componentMappings[receiverName] = obfuscatedName
                obfuscatedContent = obfuscatedContent.replace(receiverName, obfuscatedName)
            }
            
            return obfuscatedContent
        }
        
        /**
         * Obfuscate provider names in manifest
         */
        fun obfuscateProviderNames(manifestContent: String): String {
            var obfuscatedContent = manifestContent
            
            // Find all provider declarations
            val providerPattern = """<provider\s+android:name="([^"]+)"""".toRegex()
            val providers = providerPattern.findAll(manifestContent).map { it.groupValues[1] }.toList()
            
            providers.forEach { providerName ->
                val obfuscatedName = generateObfuscatedComponentName(providerName, "provider")
                componentMappings[providerName] = obfuscatedName
                obfuscatedContent = obfuscatedContent.replace(providerName, obfuscatedName)
            }
            
            return obfuscatedContent
        }
        
        /**
         * Generate obfuscated component name using advanced naming
         */
        private fun generateObfuscatedComponentName(originalName: String, componentType: String): String {
            val hash = originalName.hashCode().toString().replace("-", "")
            val prefix = when (componentType) {
                "activity" -> "a"
                "service" -> "s"
                "receiver" -> "r"
                "provider" -> "p"
                else -> "c"
            }
            return "${prefix}${hash.takeLast(3)}"
        }
    }
    
    /**
     * Permission Obfuscator
     * Obfuscates and filters Android permissions
     */
    object PermissionObfuscator {
        
        /**
         * Obfuscate permission names in manifest
         */
        fun obfuscatePermissions(manifestContent: String): String {
            var obfuscatedContent = manifestContent
            
            // Find all permission declarations
            val permissionPattern = """<uses-permission\s+android:name="([^"]+)"""".toRegex()
            val permissions = permissionPattern.findAll(manifestContent).map { it.groupValues[1] }.toList()
            
            permissions.forEach { permission ->
                val obfuscatedPermission = generateObfuscatedPermissionName(permission)
                permissionMappings[permission] = obfuscatedPermission
                obfuscatedContent = obfuscatedContent.replace(permission, obfuscatedPermission)
            }
            
            return obfuscatedContent
        }
        
        /**
         * Filter sensitive permissions from manifest
         */
        fun filterSensitivePermissions(manifestContent: String): String {
            var filteredContent = manifestContent
            
            val sensitivePermissions = listOf(
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.CAMERA",
                "android.permission.RECORD_AUDIO",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.READ_CONTACTS",
                "android.permission.WRITE_CONTACTS",
                "android.permission.READ_CALL_LOG",
                "android.permission.WRITE_CALL_LOG",
                "android.permission.READ_SMS",
                "android.permission.SEND_SMS",
                "android.permission.RECEIVE_SMS"
            )
            
            sensitivePermissions.forEach { permission ->
                val permissionLine = """<uses-permission\s+android:name="$permission"[^>]*/>""".toRegex()
                filteredContent = filteredContent.replace(permissionLine, "")
            }
            
            return filteredContent
        }
        
        /**
         * Generate obfuscated permission name
         */
        private fun generateObfuscatedPermissionName(originalPermission: String): String {
            val hash = originalPermission.hashCode().toString().replace("-", "")
            return "android.permission.OBF_${hash.takeLast(6)}"
        }
    }
    
    /**
     * Intent Filter Obfuscator
     * Obfuscates intent filters and actions
     */
    object IntentFilterObfuscator {
        
        /**
         * Obfuscate intent filter actions
         */
        fun obfuscateIntentFilters(manifestContent: String): String {
            var obfuscatedContent = manifestContent
            
            // Find all intent filter actions
            val actionPattern = """<action\s+android:name="([^"]+)"""".toRegex()
            val actions = actionPattern.findAll(manifestContent).map { it.groupValues[1] }.toList()
            
            actions.forEach { action ->
                val obfuscatedAction = generateObfuscatedActionName(action)
                intentFilterMappings[action] = obfuscatedAction
                obfuscatedContent = obfuscatedContent.replace(action, obfuscatedAction)
            }
            
            return obfuscatedContent
        }
        
        /**
         * Generate obfuscated action name
         */
        private fun generateObfuscatedActionName(originalAction: String): String {
            val hash = originalAction.hashCode().toString().replace("-", "")
            return "android.intent.action.OBF_${hash.takeLast(6)}"
        }
    }
    
    /**
     * Application Attribute Obfuscator
     * Obfuscates application-level attributes
     */
    object ApplicationAttributeObfuscator {
        
        /**
         * Obfuscate application attributes
         */
        fun obfuscateApplicationAttributes(manifestContent: String): String {
            var obfuscatedContent = manifestContent
            
            // Obfuscate application label
            obfuscatedContent = obfuscateApplicationLabel(obfuscatedContent)
            
            // Obfuscate application description
            obfuscatedContent = obfuscateApplicationDescription(obfuscatedContent)
            
            // Obfuscate application icon
            obfuscatedContent = obfuscateApplicationIcon(obfuscatedContent)
            
            return obfuscatedContent
        }
        
        private fun obfuscateApplicationLabel(manifestContent: String): String {
            val labelPattern = """android:label="([^"]+)"""".toRegex()
            return manifestContent.replace(labelPattern) { matchResult ->
                val originalLabel = matchResult.groupValues[1]
                val obfuscatedLabel = generateObfuscatedLabel(originalLabel)
                "android:label=\"$obfuscatedLabel\""
            }
        }
        
        private fun obfuscateApplicationDescription(manifestContent: String): String {
            val descriptionPattern = """android:description="([^"]+)"""".toRegex()
            return manifestContent.replace(descriptionPattern) { matchResult ->
                val originalDescription = matchResult.groupValues[1]
                val obfuscatedDescription = generateObfuscatedDescription(originalDescription)
                "android:description=\"$obfuscatedDescription\""
            }
        }
        
        private fun obfuscateApplicationIcon(manifestContent: String): String {
            val iconPattern = """android:icon="@drawable/([^"]+)"""".toRegex()
            return manifestContent.replace(iconPattern) { matchResult ->
                val originalIcon = matchResult.groupValues[1]
                val obfuscatedIcon = generateObfuscatedIconName(originalIcon)
                "android:icon=\"@drawable/$obfuscatedIcon\""
            }
        }
        
        private fun generateObfuscatedLabel(originalLabel: String): String {
            val hash = originalLabel.hashCode().toString().replace("-", "")
            return "App_${hash.takeLast(4)}"
        }
        
        private fun generateObfuscatedDescription(originalDescription: String): String {
            val hash = originalDescription.hashCode().toString().replace("-", "")
            return "Desc_${hash.takeLast(4)}"
        }
        
        private fun generateObfuscatedIconName(originalIcon: String): String {
            val hash = originalIcon.hashCode().toString().replace("-", "")
            return "ic_${hash.takeLast(4)}"
        }
    }
    
    /**
     * Metadata Obfuscator
     * Obfuscates metadata and custom attributes
     */
    object MetadataObfuscator {
        
        /**
         * Obfuscate metadata entries
         */
        fun obfuscateMetadata(manifestContent: String): String {
            var obfuscatedContent = manifestContent
            
            // Find all metadata entries
            val metadataPattern = """<meta-data\s+android:name="([^"]+)"[^>]*/>""".toRegex()
            val metadataEntries = metadataPattern.findAll(manifestContent).toList()
            
            metadataEntries.forEach { matchResult ->
                val originalName = matchResult.groupValues[1]
                val obfuscatedName = generateObfuscatedMetadataName(originalName)
                obfuscatedContent = obfuscatedContent.replace(originalName, obfuscatedName)
            }
            
            return obfuscatedContent
        }
        
        private fun generateObfuscatedMetadataName(originalName: String): String {
            val hash = originalName.hashCode().toString().replace("-", "")
            return "meta_${hash.takeLast(6)}"
        }
    }
    
    /**
     * Comprehensive manifest obfuscation
     */
    fun obfuscateManifest(manifestContent: String): String {
        Log.d(TAG, "Starting comprehensive manifest obfuscation")
        
        var obfuscatedContent = manifestContent
        
        try {
            // 1. Obfuscate component names
            obfuscatedContent = ComponentNameObfuscator.obfuscateActivityNames(obfuscatedContent)
            obfuscatedContent = ComponentNameObfuscator.obfuscateServiceNames(obfuscatedContent)
            obfuscatedContent = ComponentNameObfuscator.obfuscateReceiverNames(obfuscatedContent)
            obfuscatedContent = ComponentNameObfuscator.obfuscateProviderNames(obfuscatedContent)
            
            // 2. Obfuscate permissions
            obfuscatedContent = PermissionObfuscator.obfuscatePermissions(obfuscatedContent)
            obfuscatedContent = PermissionObfuscator.filterSensitivePermissions(obfuscatedContent)
            
            // 3. Obfuscate intent filters
            obfuscatedContent = IntentFilterObfuscator.obfuscateIntentFilters(obfuscatedContent)
            
            // 4. Obfuscate application attributes
            obfuscatedContent = ApplicationAttributeObfuscator.obfuscateApplicationAttributes(obfuscatedContent)
            
            // 5. Obfuscate metadata
            obfuscatedContent = MetadataObfuscator.obfuscateMetadata(obfuscatedContent)
            
            Log.d(TAG, "Manifest obfuscation completed successfully")
            Log.d(TAG, "Components obfuscated: ${componentMappings.size}")
            Log.d(TAG, "Permissions obfuscated: ${permissionMappings.size}")
            Log.d(TAG, "Intent filters obfuscated: ${intentFilterMappings.size}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error during manifest obfuscation", e)
        }
        
        return obfuscatedContent
    }
    
    /**
     * Get obfuscation mappings for debugging
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
}