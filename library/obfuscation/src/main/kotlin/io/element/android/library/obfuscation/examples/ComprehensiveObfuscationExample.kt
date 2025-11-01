package io.element.android.library.obfuscation.examples

import android.content.Context
import io.element.android.library.obfuscation.ObfuscationManager
import io.element.android.library.obfuscation.ObfuscationConfig

/**
 * Comprehensive Obfuscation Example
 * 
 * This class demonstrates how to use all the integrated obfuscation techniques
 * through the ObfuscationManager interface. This serves as both documentation
 * and a practical example for developers.
 */
class ComprehensiveObfuscationExample(private val context: Context) {

    private val obfuscationManager: ObfuscationManager by lazy {
        ObfuscationManager.getInstance()
    }

    /**
     * Example: advanced-Level String Obfuscation
     * Demonstrates multi-layer string encryption with hardware-based keys
     */
    fun demonstrateStringObfuscation() {
        val sensitiveData = "API_KEY_12345_SECRET"
        
        // advanced-style string obfuscation (a0, b2, c3 naming)
        val encryptedData = obfuscationManager.staticObfuscation.obfuscateStringAdvancedStyle(
            sensitiveData, 
            "api_context"
        )
        
        // Decrypt when needed
        val decryptedData = obfuscationManager.staticObfuscation.deobfuscateStringAdvancedStyle(
            encryptedData, 
            "api_context"
        )
        
        println("Original: $sensitiveData")
        println("Encrypted: ${encryptedData.joinToString("") { "%02x".format(it) }}")
        println("Decrypted: $decryptedData")
    }

    /**
     * Example: Advanced Control Flow Obfuscation
     * Demonstrates opaque predicates and control flow flattening
     */
    fun demonstrateControlFlowObfuscation() {
        val userInput = 42
        
        // Obfuscated branch with mathematical opaque predicates
        val result = obfuscationManager.staticObfuscation.obfuscatedBranch {
            if (userInput > 0) {
                "Positive number processed"
            } else {
                "Negative number processed"
            }
        }
        
        // Flattened execution with state machine
        val flattenedResult = obfuscationManager.staticObfuscation.flattenedExecution(
            { "Step 1: Initialize" },
            { "Step 2: Process data" },
            { "Step 3: Finalize" }
        )
        
        println("Obfuscated result: $result")
        println("Flattened result: $flattenedResult")
    }

    /**
     * Example: Method Inlining and Outlining
     * Demonstrates dynamic method structure modification
     */
    fun demonstrateMethodObfuscation() {
        val methodName = "calculateTotal"
        val methodBody = { 
            val price = 100.0
            val tax = price * 0.1
            price + tax
        }
        
        // Inline method (replace call with body)
        val inlinedBytecode = obfuscationManager.staticObfuscation.inlineMethod(
            methodName, 
            methodBody
        )
        
        // Outline method (extract body to separate methods)
        val outlinedMethods = obfuscationManager.staticObfuscation.outlineMethod(
            methodName, 
            methodBody
        )
        
        // Generate dynamic method
        val dynamicMethod = obfuscationManager.staticObfuscation.generateDynamicMethod(
            "dynamicMethod", 
            arrayOf(String::class.java, Int::class.java)
        )
        
        println("Inlined bytecode size: ${inlinedBytecode.size}")
        println("Outlined methods count: ${outlinedMethods.size}")
        println("Dynamic method generated: ${dynamicMethod.isNotEmpty()}")
    }

    /**
     * Example: Metadata Stripping and Debug Removal
     * Demonstrates removal of sensitive build-time information
     */
    fun demonstrateMetadataStripping() {
        val testBytecode = "test bytecode data".toByteArray()
        
        // Remove debug information
        val strippedBytecode = obfuscationManager.staticObfuscation.removeDebugInfo(testBytecode)
        
        // Strip annotations
        val annotationStripped = obfuscationManager.staticObfuscation.stripAnnotations(strippedBytecode)
        
        // Obfuscate types
        val typeObfuscated = obfuscationManager.staticObfuscation.obfuscateTypes(annotationStripped)
        
        // Obfuscate stack trace
        val exception = RuntimeException("Test exception")
        val obfuscatedStackTrace = obfuscationManager.staticObfuscation.obfuscateStackTrace(exception)
        
        println("Original bytecode size: ${testBytecode.size}")
        println("Stripped bytecode size: ${strippedBytecode.size}")
        println("Obfuscated stack trace: $obfuscatedStackTrace")
    }

    /**
     * Example: Dead Code Insertion and Junk Code Generation
     * Demonstrates realistic-looking fake code insertion
     */
    fun demonstrateDeadCodeInsertion() {
        val originalCode = { "Real functionality" }
        
        // Insert dead code
        val obfuscatedCode = obfuscationManager.staticObfuscation.insertDeadCode(
            "critical_function", 
            originalCode
        )
        
        // Generate junk code blocks
        val junkCodeBlocks = obfuscationManager.staticObfuscation.generateJunkCode("test_section")
        
        // Generate anti-analysis noise
        val antiAnalysisNoise = obfuscationManager.staticObfuscation.generateAntiAnalysisNoise("security_check")
        
        println("Junk code blocks generated: ${junkCodeBlocks.size}")
        println("Anti-analysis noise generated: ${antiAnalysisNoise != null}")
    }

    /**
     * Example: Anti-Analysis Detection
     * Demonstrates detection of reverse engineering tools
     */
    fun demonstrateAntiAnalysis() {
        // Perform comprehensive anti-analysis check
        val isAnalysisDetected = obfuscationManager.staticObfuscation.performAntiAnalysisCheck()
        
        // Get anti-analysis statistics
        val stats = obfuscationManager.staticObfuscation.getAntiAnalysisStats()
        
        println("Analysis detected: $isAnalysisDetected")
        println("Anti-analysis stats: $stats")
    }

    /**
     * Example: Encrypted Class Loading
     * Demonstrates runtime class decryption and loading
     */
    fun demonstrateEncryptedClassLoading() {
        val encryptedClassData = "encrypted class data".toByteArray()
        val className = "SecretClass"
        
        // Load encrypted class
        val loadedClass = obfuscationManager.runtimeObfuscation.loadEncryptedClassAdvanced(
            encryptedClassData, 
            className
        )
        
        // Generate runtime class
        val runtimeClass = obfuscationManager.runtimeObfuscation.generateRuntimeClass(
            "DynamicClass", 
            "class bytecode".toByteArray()
        )
        
        // Obfuscate class loading
        val obfuscatedClassName = obfuscationManager.runtimeObfuscation.obfuscateClassLoading(className)
        
        println("Class loaded: ${loadedClass != null}")
        println("Runtime class generated: ${runtimeClass != null}")
        println("Obfuscated class name: $obfuscatedClassName")
    }

    /**
     * Example: Code Virtualization
     * Demonstrates custom virtual machine execution
     */
    fun demonstrateCodeVirtualization() {
        val testBytecode = "virtual bytecode".toByteArray()
        
        // Execute virtual code
        val virtualResult = obfuscationManager.runtimeObfuscation.executeVirtualCode(testBytecode)
        
        // Obfuscate bytecode
        val obfuscatedBytecode = obfuscationManager.runtimeObfuscation.obfuscateBytecode(testBytecode)
        
        // Virtualize method call
        val virtualMethodResult = obfuscationManager.runtimeObfuscation.virtualizeMethodCall(
            "testMethod", 
            arrayOf("param1", 123)
        )
        
        println("Virtual execution result: $virtualResult")
        println("Bytecode obfuscated: ${obfuscatedBytecode.size != testBytecode.size}")
        println("Method virtualized: ${virtualMethodResult != null}")
    }

    /**
     * Example: Polymorphic Code Generation
     * Demonstrates self-modifying and metamorphic code
     */
    fun demonstratePolymorphicCode() {
        val originalCode = { "Original functionality" }
        
        // Generate polymorphic code
        val polymorphicCode = obfuscationManager.runtimeObfuscation.generatePolymorphicCode(
            "polymorphic_function", 
            originalCode
        )
        
        // Apply self-modifying code
        val selfModifyingCode = obfuscationManager.runtimeObfuscation.applySelfModifyingCode(
            "self_modifying_function", 
            originalCode
        )
        
        // Generate metamorphic code
        val metamorphicCode = obfuscationManager.runtimeObfuscation.generateMetamorphicCode(
            "metamorphic_function", 
            originalCode
        )
        
        println("Polymorphic code generated: ${polymorphicCode != originalCode}")
        println("Self-modifying code applied: ${selfModifyingCode != originalCode}")
        println("Metamorphic code generated: ${metamorphicCode != originalCode}")
    }

    /**
     * Example: Timing Obfuscation
     * Demonstrates environment-dependent behavior and timing attacks
     */
    fun demonstrateTimingObfuscation() {
        val originalCode = { "Timing-sensitive operation" }
        
        // Apply timing obfuscation
        val timingObfuscatedCode = obfuscationManager.runtimeObfuscation.applyTimingObfuscation(originalCode)
        
        // Apply environment-dependent behavior
        val environmentDependentCode = obfuscationManager.runtimeObfuscation.applyEnvironmentDependentBehavior(originalCode)
        
        // Apply timing attack
        val timingAttackCode = obfuscationManager.runtimeObfuscation.applyTimingAttack(originalCode)
        
        println("Timing obfuscation applied: ${timingObfuscatedCode != originalCode}")
        println("Environment-dependent behavior applied: ${environmentDependentCode != originalCode}")
        println("Timing attack applied: ${timingAttackCode != originalCode}")
    }

    /**
     * Example: Data Masking
     * Demonstrates various data privacy techniques
     */
    fun demonstrateDataMasking() {
        // Name masking
        val originalName = "John Doe"
        val maskedName = obfuscationManager.dataMasking.maskName(originalName)
        
        // Email masking
        val originalEmail = "john.doe@example.com"
        val maskedEmail = obfuscationManager.dataMasking.maskEmail(originalEmail)
        
        // Phone masking
        val originalPhone = "123-456-7890"
        val maskedPhone = obfuscationManager.dataMasking.maskPhone(originalPhone)
        
        // Credit card masking
        val originalCard = "1234-5678-9012-3456"
        val maskedCard = obfuscationManager.dataMasking.maskCreditCard(originalCard)
        
        // Numeric value masking
        val originalValue = 123.45
        val maskedValue = obfuscationManager.dataMasking.maskNumericValue(originalValue, "price")
        
        println("Name: $originalName -> $maskedName")
        println("Email: $originalEmail -> $maskedEmail")
        println("Phone: $originalPhone -> $maskedPhone")
        println("Card: $originalCard -> $maskedCard")
        println("Value: $originalValue -> $maskedValue")
    }

    /**
     * Example: Native Code Obfuscation
     * Demonstrates native library protection
     */
    fun demonstrateNativeObfuscation() {
        val libraryPath = "/path/to/library.so"
        val libraryData = "native library data".toByteArray()
        
        // Obfuscate native library
        val obfuscatedPath = obfuscationManager.nativeObfuscation.obfuscateNativeLibrary(libraryPath)
        
        // Strip symbols
        val strippedData = obfuscationManager.nativeObfuscation.stripSymbols(libraryData)
        
        // Add anti-debug checks
        val RASPData = obfuscationManager.nativeObfuscation.addRASPChecks(libraryData)
        
        // Verify library integrity
        val integrityResult = obfuscationManager.nativeObfuscation.verifyLibraryIntegrity(libraryPath)
        
        println("Library obfuscated: $obfuscatedPath")
        println("Symbols stripped: ${strippedData.size != libraryData.size}")
        println("Anti-debug added: ${RASPData.size != libraryData.size}")
        println("Integrity verified: $integrityResult")
    }

    /**
     * Example: Resource Obfuscation
     * Demonstrates comprehensive resource protection
     */
    fun demonstrateResourceObfuscation() {
        // Resource name obfuscation
        val originalResourceName = "main_activity_layout"
        val obfuscatedResourceName = io.element.android.library.obfuscation.static.ResourceObfuscator.ResourceNameMangler.obfuscateResourceName(
            originalResourceName, 
            "layout"
        )
        
        // Asset encryption
        val assetData = "asset content".toByteArray()
        val encryptedAsset = obfuscationManager.resourceObfuscation.encryptAsset(
            context, 
            "test_asset", 
            assetData
        )
        
        // Image obfuscation
        val imageData = "image data".toByteArray()
        val obfuscatedImage = obfuscationManager.resourceObfuscation.obfuscateImage(imageData)
        
        // Manifest obfuscation
        val manifestData = "<manifest>test</manifest>"
        val obfuscatedManifest = obfuscationManager.resourceObfuscation.obfuscateManifestComponents(manifestData)
        
        println("Resource name: $originalResourceName -> $obfuscatedResourceName")
        println("Asset encrypted: ${encryptedAsset.isNotEmpty()}")
        println("Image obfuscated: ${obfuscatedImage.size != imageData.size}")
        println("Manifest obfuscated: ${obfuscatedManifest != manifestData}")
    }

    /**
     * Example: Comprehensive Obfuscation Statistics
     * Demonstrates the full scope of implemented techniques
     */
    fun demonstrateObfuscationStatistics() {
        val status = obfuscationManager.getObfuscationStatus()
        val stats = obfuscationManager.getObfuscationStats()
        
        println("=== Obfuscation Status ===")
        status.forEach { (key, value) ->
            println("$key: $value")
        }
        
        println("\n=== Obfuscation Statistics ===")
        stats.forEach { (key, value) ->
            println("$key: $value")
        }
    }

    /**
     * Example: Complete Obfuscation Workflow
     * Demonstrates a real-world scenario using multiple techniques
     */
    fun demonstrateCompleteWorkflow() {
        println("=== Complete Obfuscation Workflow ===")
        
        // 1. Initialize obfuscation
        println("1. Initializing obfuscation...")
        obfuscationManager.resourceObfuscation.initializeResourceObfuscation(context)
        
        // 2. Obfuscate sensitive data
        println("2. Obfuscating sensitive data...")
        demonstrateStringObfuscation()
        
        // 3. Apply control flow obfuscation
        println("3. Applying control flow obfuscation...")
        demonstrateControlFlowObfuscation()
        
        // 4. Insert dead code
        println("4. Inserting dead code...")
        demonstrateDeadCodeInsertion()
        
        // 5. Perform anti-analysis checks
        println("5. Performing anti-analysis checks...")
        demonstrateAntiAnalysis()
        
        // 6. Apply runtime obfuscation
        println("6. Applying runtime obfuscation...")
        demonstrateCodeVirtualization()
        
        // 7. Mask sensitive data
        println("7. Masking sensitive data...")
        demonstrateDataMasking()
        
        // 8. Obfuscate resources
        println("8. Obfuscating resources...")
        demonstrateResourceObfuscation()
        
        // 9. Show final statistics
        println("9. Final obfuscation statistics...")
        demonstrateObfuscationStatistics()
        
        println("=== Workflow Complete ===")
    }
}

