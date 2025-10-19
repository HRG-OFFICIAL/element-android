package io.element.android.library.obfuscation.static

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import java.io.File
import java.io.StringWriter
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList

/**
 * Manifest Obfuscation
 * 
 * Provides comprehensive obfuscation for Android manifest components and permissions.
 * This includes obfuscating activity names, service names, receiver names, provider names,
 * permission names, and intent filters to make reverse engineering more difficult.
 */
object ManifestObfuscator {

    private const val TAG = "ManifestObfuscator"
    
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
            val document = parseManifest(manifestData)
            val manifest = document.documentElement
            
            // Obfuscate activities
            obfuscateActivities(document, manifest)
            
            // Obfuscate services
            obfuscateServices(document, manifest)
            
            // Obfuscate receivers
            obfuscateReceivers(document, manifest)
            
            // Obfuscate providers
            obfuscateProviders(document, manifest)
            
            // Obfuscate permissions
            obfuscatePermissions(document, manifest)
            
            // Obfuscate intent filters
            obfuscateIntentFilters(document, manifest)
            
            return serializeManifest(document)
        } catch (e: Exception) {
            Log.e(TAG, "Error obfuscating manifest components", e)
            return manifestData
        }
    }

    /**
     * Obfuscate activity names
     */
    private fun obfuscateActivities(document: Document, manifest: Element) {
        val activities = manifest.getElementsByTagName("activity")
        for (i in 0 until activities.length) {
            val activity = activities.item(i) as Element
            val originalName = activity.getAttribute("android:name")
            if (originalName.isNotEmpty()) {
                val obfuscatedName = generateObfuscatedName(originalName, componentMappings, componentPatterns)
                activity.setAttribute("android:name", obfuscatedName)
                Log.d(TAG, "Obfuscated activity: $originalName -> $obfuscatedName")
            }
        }
    }

    /**
     * Obfuscate service names
     */
    private fun obfuscateServices(document: Document, manifest: Element) {
        val services = manifest.getElementsByTagName("service")
        for (i in 0 until services.length) {
            val service = services.item(i) as Element
            val originalName = service.getAttribute("android:name")
            if (originalName.isNotEmpty()) {
                val obfuscatedName = generateObfuscatedName(originalName, componentMappings, componentPatterns)
                service.setAttribute("android:name", obfuscatedName)
                Log.d(TAG, "Obfuscated service: $originalName -> $obfuscatedName")
            }
        }
    }

    /**
     * Obfuscate receiver names
     */
    private fun obfuscateReceivers(document: Document, manifest: Element) {
        val receivers = manifest.getElementsByTagName("receiver")
        for (i in 0 until receivers.length) {
            val receiver = receivers.item(i) as Element
            val originalName = receiver.getAttribute("android:name")
            if (originalName.isNotEmpty()) {
                val obfuscatedName = generateObfuscatedName(originalName, componentMappings, componentPatterns)
                receiver.setAttribute("android:name", obfuscatedName)
                Log.d(TAG, "Obfuscated receiver: $originalName -> $obfuscatedName")
            }
        }
    }

    /**
     * Obfuscate provider names
     */
    private fun obfuscateProviders(document: Document, manifest: Element) {
        val providers = manifest.getElementsByTagName("provider")
        for (i in 0 until providers.length) {
            val provider = providers.item(i) as Element
            val originalName = provider.getAttribute("android:name")
            if (originalName.isNotEmpty()) {
                val obfuscatedName = generateObfuscatedName(originalName, componentMappings, componentPatterns)
                provider.setAttribute("android:name", obfuscatedName)
                Log.d(TAG, "Obfuscated provider: $originalName -> $obfuscatedName")
            }
        }
    }

    /**
     * Obfuscate permission names
     */
    private fun obfuscatePermissions(document: Document, manifest: Element) {
        val permissions = manifest.getElementsByTagName("uses-permission")
        for (i in 0 until permissions.length) {
            val permission = permissions.item(i) as Element
            val originalName = permission.getAttribute("android:name")
            if (originalName.isNotEmpty()) {
                val obfuscatedName = generateObfuscatedName(originalName, permissionMappings, permissionPatterns)
                permission.setAttribute("android:name", obfuscatedName)
                Log.d(TAG, "Obfuscated permission: $originalName -> $obfuscatedName")
            }
        }
    }

    /**
     * Obfuscate intent filters
     */
    private fun obfuscateIntentFilters(document: Document, manifest: Element) {
        val intentFilters = manifest.getElementsByTagName("intent-filter")
        for (i in 0 until intentFilters.length) {
            val intentFilter = intentFilters.item(i) as Element
            val actions = intentFilter.getElementsByTagName("action")
            for (j in 0 until actions.length) {
                val action = actions.item(j) as Element
                val originalName = action.getAttribute("android:name")
                if (originalName.isNotEmpty()) {
                    val obfuscatedName = generateObfuscatedName(originalName, intentFilterMappings, intentFilterPatterns)
                    action.setAttribute("android:name", obfuscatedName)
                    Log.d(TAG, "Obfuscated intent filter: $originalName -> $obfuscatedName")
                }
            }
        }
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
     * Parse manifest XML
     */
    private fun parseManifest(manifestData: String): Document {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        return builder.parse(manifestData.byteInputStream())
    }

    /**
     * Serialize manifest XML
     */
    private fun serializeManifest(document: Document): String {
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        val source = DOMSource(document)
        val result = StreamResult(StringWriter())
        transformer.transform(source, result)
        return result.writer.toString()
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

    /**
     * Restore original manifest
     * 
     * @param context Android context
     * @param manifestFile Manifest file path
     * @return Success status
     */
    fun restoreManifest(context: Context, manifestFile: String): Boolean {
        return try {
            val file = File(manifestFile)
            if (file.exists()) {
                // This would require storing original mappings
                // For now, just clear the mappings
                clearMappings()
                Log.d(TAG, "Manifest mappings cleared")
                true
            } else {
                Log.e(TAG, "Manifest file not found: $manifestFile")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error restoring manifest", e)
            false
        }
    }
}
