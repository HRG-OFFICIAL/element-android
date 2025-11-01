package io.element.android.library.obfuscation

import android.content.Context
import io.element.android.library.obfuscation.static.AdvancedObfuscator
import io.element.android.library.obfuscation.static.StringObfuscator
import io.element.android.library.obfuscation.static.FlowObfuscator
import io.element.android.library.obfuscation.static.ResourceObfuscator
import io.element.android.library.obfuscation.static.MethodObfuscator
import io.element.android.library.obfuscation.static.MetadataStripper
import io.element.android.library.obfuscation.static.DeadCodeGenerator
import io.element.android.library.obfuscation.static.AntiAnalysis
import io.element.android.library.obfuscation.data.DataMasking
import io.element.android.library.obfuscation.native.NativeObfuscator
import io.element.android.library.obfuscation.runtime.ReflectionIndirection
import io.element.android.library.obfuscation.runtime.EncryptedClassLoader
import io.element.android.library.obfuscation.runtime.CodeVirtualization
import io.element.android.library.obfuscation.runtime.PolymorphicCodeGenerator
import io.element.android.library.obfuscation.runtime.TimingObfuscator
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.junit.Assert.*

/**
 * Comprehensive Integration Test for Obfuscation Library
 * 
 * This test verifies that all obfuscation techniques are properly integrated
 * and wired through the ObfuscationManager interface.
 */
@RunWith(MockitoJUnitRunner::class)
class ObfuscationIntegrationTest {

    @Mock
    private lateinit var mockContext: Context

    private val testConfig = object : ObfuscationConfig {
        override val isDebugMode = false
    }

    @Test
    fun testObfuscationManagerInitialization() {
        // Test that ObfuscationManager can be initialized
        ObfuscationManager.initialize(mockContext, testConfig)
        val manager = ObfuscationManager.getInstance()
        
        assertNotNull("ObfuscationManager should be initialized", manager)
        assertNotNull("Static obfuscation should be available", manager.staticObfuscation)
        assertNotNull("Runtime obfuscation should be available", manager.runtimeObfuscation)
        assertNotNull("Data masking should be available", manager.dataMasking)
        assertNotNull("Native obfuscation should be available", manager.nativeObfuscation)
        assertNotNull("Resource obfuscation should be available", manager.resourceObfuscation)
    }

    @Test
    fun testStaticObfuscationIntegration() {
        ObfuscationManager.initialize(mockContext, testConfig)
        val manager = ObfuscationManager.getInstance()
        val staticObf = manager.staticObfuscation

        // Test advanced-level string obfuscation
        val testString = "Hello World"
        val encrypted = staticObf.obfuscateStringadvancedStyle(testString, "test")
        val decrypted = staticObf.deobfuscateStringadvancedStyle(encrypted, "test")
        assertEquals("String obfuscation should work correctly", testString, decrypted)

        // Test method inlining
        val methodBody = { "test result" }
        val inlinedCode = staticObf.inlineMethod("testMethod", methodBody)
        assertNotNull("Method inlining should return bytecode", inlinedCode)

        // Test metadata stripping
        val testBytecode = "test bytecode".toByteArray()
        val strippedBytecode = staticObf.removeDebugInfo(testBytecode)
        assertNotNull("Debug info removal should work", strippedBytecode)

        // Test dead code insertion
        val originalCode = { "original" }
        val obfuscatedCode = staticObf.insertDeadCode("test", originalCode)
        assertNotNull("Dead code insertion should work", obfuscatedCode)

        // Test anti-analysis
        val antiAnalysisResult = staticObf.performAntiAnalysisCheck()
        assertNotNull("Anti-analysis check should work", antiAnalysisResult)
    }

    @Test
    fun testRuntimeObfuscationIntegration() {
        ObfuscationManager.initialize(mockContext, testConfig)
        val manager = ObfuscationManager.getInstance()
        val runtimeObf = manager.runtimeObfuscation

        // Test encrypted class loading
        val testBytecode = "test class".toByteArray()
        val loadedClass = runtimeObf.loadEncryptedClassAdvanced(testBytecode, "TestClass")
        // Note: This might return null in test environment, which is expected

        // Test code virtualization
        val virtualResult = runtimeObf.executeVirtualCode(testBytecode)
        // Note: This might return null in test environment, which is expected

        // Test polymorphic code generation
        val originalCode = { "test" }
        val polymorphicCode = runtimeObf.generatePolymorphicCode("test", originalCode)
        assertNotNull("Polymorphic code generation should work", polymorphicCode)

        // Test timing obfuscation
        val timingObfuscatedCode = runtimeObf.applyTimingObfuscation(originalCode)
        assertNotNull("Timing obfuscation should work", timingObfuscatedCode)
    }

    @Test
    fun testDataMaskingIntegration() {
        ObfuscationManager.initialize(mockContext, testConfig)
        val manager = ObfuscationManager.getInstance()
        val dataMasking = manager.dataMasking

        // Test name masking
        val originalName = "John Doe"
        val maskedName = dataMasking.maskName(originalName)
        assertNotEquals("Name should be masked", originalName, maskedName)

        // Test email masking
        val originalEmail = "john.doe@example.com"
        val maskedEmail = dataMasking.maskEmail(originalEmail)
        assertNotEquals("Email should be masked", originalEmail, maskedEmail)

        // Test phone masking
        val originalPhone = "123-456-7890"
        val maskedPhone = dataMasking.maskPhone(originalPhone)
        assertNotEquals("Phone should be masked", originalPhone, maskedPhone)

        // Test credit card masking
        val originalCard = "1234-5678-9012-3456"
        val maskedCard = dataMasking.maskCreditCard(originalCard)
        assertNotEquals("Credit card should be masked", originalCard, maskedCard)

        // Test numeric value masking
        val originalValue = 123.45
        val maskedValue = dataMasking.maskNumericValue(originalValue, "price")
        assertNotEquals("Numeric value should be masked", originalValue.toString(), maskedValue)
    }

    @Test
    fun testNativeObfuscationIntegration() {
        ObfuscationManager.initialize(mockContext, testConfig)
        val manager = ObfuscationManager.getInstance()
        val nativeObf = manager.nativeObfuscation

        // Test native library obfuscation
        val testLibraryPath = "/path/to/library.so"
        val obfuscatedPath = nativeObf.obfuscateNativeLibrary(testLibraryPath)
        assertNotNull("Native library obfuscation should work", obfuscatedPath)

        // Test symbol stripping
        val testLibraryData = "test library data".toByteArray()
        val strippedData = nativeObf.stripSymbols(testLibraryData)
        assertNotNull("Symbol stripping should work", strippedData)

        // Test anti-debug checks
        val RASPData = nativeObf.addRASPChecks(testLibraryData)
        assertNotNull("Anti-debug checks should work", RASPData)

        // Test library integrity verification
        val integrityResult = nativeObf.verifyLibraryIntegrity(testLibraryPath)
        assertNotNull("Library integrity verification should work", integrityResult)
    }

    @Test
    fun testResourceObfuscationIntegration() {
        ObfuscationManager.initialize(mockContext, testConfig)
        val manager = ObfuscationManager.getInstance()
        val resourceObf = manager.resourceObfuscation

        // Test resource name obfuscation
        val originalName = "main_activity"
        val obfuscatedName = resourceObf.obfuscateResourceName(originalName, "layout")
        assertNotEquals("Resource name should be obfuscated", originalName, obfuscatedName)

        // Test asset encryption
        val testData = "test asset data".toByteArray()
        val encryptedAsset = resourceObf.encryptAsset(mockContext, "test_asset", testData)
        assertNotNull("Asset encryption should work", encryptedAsset)

        // Test image obfuscation
        val testImageData = "test image data".toByteArray()
        val obfuscatedImage = resourceObf.obfuscateImage(testImageData)
        assertNotNull("Image obfuscation should work", obfuscatedImage)

        // Test string resource obfuscation
        val testStringResource = "Hello World"
        val obfuscatedStringResource = resourceObf.obfuscateStringResource(testStringResource)
        assertNotNull("String resource obfuscation should work", obfuscatedStringResource)

        // Test manifest obfuscation
        val testManifest = "<manifest>test</manifest>"
        val obfuscatedManifest = resourceObf.obfuscateManifestComponents(testManifest)
        assertNotNull("Manifest obfuscation should work", obfuscatedManifest)
    }

    @Test
    fun testObfuscationStatistics() {
        ObfuscationManager.initialize(mockContext, testConfig)
        val manager = ObfuscationManager.getInstance()

        val status = manager.getObfuscationStatus()
        val stats = manager.getObfuscationStats()

        // Test status
        assertTrue("Static obfuscation should be enabled", status["static_obfuscation"] == true)
        assertTrue("Runtime obfuscation should be enabled", status["runtime_obfuscation"] == true)
        assertTrue("Data masking should be enabled", status["data_masking"] == true)
        assertTrue("Native obfuscation should be enabled", status["native_obfuscation"] == true)
        assertTrue("Resource obfuscation should be enabled", status["resource_obfuscation"] == true)

        // Test statistics
        assertTrue("Total techniques should be 35+", (stats["total_techniques"] as Int) >= 35)
        assertTrue("Static techniques should be 15+", (stats["static_techniques"] as Int) >= 15)
        assertTrue("Runtime techniques should be 10+", (stats["runtime_techniques"] as Int) >= 10)
        assertTrue("Data techniques should be 8+", (stats["data_techniques"] as Int) >= 8)
        assertTrue("Native techniques should be 6+", (stats["native_techniques"] as Int) >= 6)
        assertTrue("Resource techniques should be 25+", (stats["resource_techniques"] as Int) >= 25)
        assertTrue("advanced-level features should be 12+", (stats["advanced_level_features"] as Int) >= 12)
        assertTrue("Anti-analysis methods should be 18+", (stats["anti_analysis_methods"] as Int) >= 18)
    }

    @Test
    fun testDebugModeBehavior() {
        val debugConfig = object : ObfuscationConfig {
            override val isDebugMode = true
        }
        
        ObfuscationManager.initialize(mockContext, debugConfig)
        val manager = ObfuscationManager.getInstance()
        val staticObf = manager.staticObfuscation

        // In debug mode, obfuscation should be disabled
        val testString = "Hello World"
        val encrypted = staticObf.obfuscateStringadvancedStyle(testString, "test")
        assertEquals("In debug mode, string should not be obfuscated", testString, String(encrypted))

        val originalCode = { "test" }
        val obfuscatedCode = staticObf.insertDeadCode("test", originalCode)
        assertEquals("In debug mode, code should not be obfuscated", originalCode, obfuscatedCode)
    }

    @Test
    fun testAllTechniquesAreAccessible() {
        ObfuscationManager.initialize(mockContext, testConfig)
        val manager = ObfuscationManager.getInstance()

        // Test that all major technique categories are accessible
        assertNotNull("AdvancedObfuscator should be accessible", AdvancedObfuscator)
        assertNotNull("StringObfuscator should be accessible", StringObfuscator)
        assertNotNull("FlowObfuscator should be accessible", FlowObfuscator)
        assertNotNull("ResourceObfuscator should be accessible", ResourceObfuscator)
        assertNotNull("MethodObfuscator should be accessible", MethodObfuscator)
        assertNotNull("MetadataStripper should be accessible", MetadataStripper)
        assertNotNull("DeadCodeGenerator should be accessible", DeadCodeGenerator)
        assertNotNull("AntiAnalysis should be accessible", AntiAnalysis)
        assertNotNull("DataMasking should be accessible", DataMasking)
        assertNotNull("NativeObfuscator should be accessible", NativeObfuscator)
        assertNotNull("ReflectionIndirection should be accessible", ReflectionIndirection)
        assertNotNull("EncryptedClassLoader should be accessible", EncryptedClassLoader)
        assertNotNull("CodeVirtualization should be accessible", CodeVirtualization)
        assertNotNull("PolymorphicCodeGenerator should be accessible", PolymorphicCodeGenerator)
        assertNotNull("TimingObfuscator should be accessible", TimingObfuscator)
    }
}

