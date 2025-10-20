# Android Obfuscation Library

A **industry-leading**, standalone Android obfuscation library that implements **enterprise-level-level** code protection techniques. This library provides multiple layers of obfuscation, anti-debugging, and security techniques that match the sophistication of enterprise-grade applications.

## Enterprise-level Features

### **Identifier Obfuscation (enterprise-level-Style)**
- **Single-character naming**: `a0`, `b2`, `c3`, `d1` (exactly like enterprise-level PDF)
- **Dynamic renaming**: Changes names at runtime to confuse analysis
- **Class/Method/Field obfuscation**: All identifiers become meaningless characters
- **Package flattening**: Reduces package hierarchy to single characters

### **Advanced Control Flow Obfuscation**
- **Opaque predicates**: Mathematically proven true/false conditions
- **Control flow flattening**: Converts linear code into state machines
- **Bogus loops**: Creates fake loops to confuse analysis
- **Anti-disassembly**: Inserts invalid opcodes and padding

### **Multi-Layer String Encryption**
- **AES-256 + ChaCha20 + Custom XOR**: Triple-layer encryption
- **Hardware-based keys**: Uses device characteristics for key generation
- **Anti-debugging**: Returns fake strings when debugger detected
- **Dynamic decryption**: Runtime string reconstruction

### **Comprehensive Anti-Debugging**
- **18 detection methods**: Including timing attacks, ptrace checks, memory analysis
- **Statistical analysis**: Advanced timing attack detection
- **Emulator detection**: Identifies virtual environments
- **Root detection**: Detects rooted devices
- **Hook detection**: Identifies Xposed, LSPosed, etc.

## Package Structure

```
io.element.android.library.obfuscation/
 ObfuscationManager.kt          # Central coordinator for all obfuscation
 ObfuscationConfig.kt           # Configuration interface
 static/                        # Static code obfuscation (compile-time)
    AdvancedObfuscator.kt      # enterprise-level-level static obfuscation
    StringObfuscator.kt        # Multi-layer string encryption
    ControlFlowObfuscator.kt   # Control flow obfuscation
    FlowObfuscator.kt          # Advanced flow techniques
    StringCrypto.kt            # String encryption utilities
    ResourceObfuscator.kt      # Resource and manifest obfuscation
 runtime/                       # Runtime/dynamic obfuscation
    ReflectionIndirection.kt   # Reflection-based indirection
 data/                          # Data masking and privacy
    DataMasking.kt             # Comprehensive data masking
 native/                        # Native code obfuscation
    NativeObfuscator.kt        # Native library protection
 demo/                          # Obfuscation demonstration
     ObfuscationDemo.kt         # Demo code showing obfuscation
     EncryptedClassLoader.kt    # Dynamic class loading demo
     SecureUtility.kt           # Security utility demo
```

##  Quick Start

### 1. Add Dependency

In your app's `build.gradle`:

```gradle
dependencies {
    implementation project(':library:obfuscation')
    // or if using as external library:
    // implementation 'io.element.android:obfuscation:1.0.0'
}
```

### 2. Initialize in Application

```kotlin
import io.element.android.library.obfuscation.ObfuscationManager
import io.element.android.library.obfuscation.ObfuscationConfig

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize with custom configuration
        ObfuscationManager.initialize(
            context = this,
            config = object : ObfuscationConfig {
                override val isDebugMode = BuildConfig.DEBUG
            }
        )
    }
}
```

### 3. Use Obfuscation Features

```kotlin
// Get the manager instance
val obfuscationManager = ObfuscationManager.getInstance()

// Static obfuscation
obfuscationManager.staticObfuscation.applyComprehensiveObfuscation {
    // Your sensitive code here
}

// Data masking
val maskedEmail = obfuscationManager.dataMasking.maskEmail("user@example.com")

// Runtime obfuscation
val dynamicMethod = obfuscationManager.runtimeObfuscation.generateDynamicMethod(12345L)
```

##  Obfuscation Categories

### 1. Static Code Obfuscation
- **Identifier Renaming**: Classes, methods, fields  meaningless names
- **Control Flow Obfuscation**: Opaque predicates, fake branches
- **Advanced Flow Obfuscation**: State machines, timing checks, flattened execution
- **String Encryption**: Runtime string decryption
- **Junk Code Insertion**: Dead code and misleading methods
- **Method Inlining/Outlining**: Code structure obfuscation
- **Metadata Stripping**: Debug information removal

### 2. Runtime/Dynamic Obfuscation
- **Dynamic Class Loading**: Runtime class decryption
- **Code Virtualization**: VM-based obfuscation
- **Dynamic Code Generation**: Runtime code creation
- **Reflection Indirection**: Method call obfuscation

### 3. Data Masking
- **Static Data Masking (SDM)**: Pre-processing data sanitization
- **Dynamic Data Masking (DDM)**: Runtime data masking
- **Format-Preserving Encryption (FPE)**: Maintains data format
- **Data Redaction**: Selective information removal

### 4. Native Code Obfuscation
- **Symbol Stripping**: Remove debugging symbols
- **Function Obfuscation**: Inline/outline functions
- **Anti-debugging**: Runtime protection checks
- **Anti-tampering**: Integrity verification

### 5. Resource Obfuscation
- **Resource Name Mangling**: Obfuscate resource names
- **Asset Encryption**: Encrypt sensitive assets
- **Manifest Obfuscation**: Hide component information

##  Obfuscation Statistics

- **Total Techniques**: 35+ different obfuscation methods
- **Static Techniques**: 15 (identifier renaming, control flow, string encryption, method inlining/outlining, metadata stripping, dead code insertion, anti-analysis)
- **Runtime Techniques**: 10 (encrypted class loading, code virtualization, polymorphic code generation, timing obfuscation, reflection indirection)
- **Data Techniques**: 8 (SDM, DDM, FPE, redaction, numeric masking, substitution, encryption)
- **Native Techniques**: 6 (symbol stripping, function obfuscation, anti-debugging, anti-tampering, code encryption, integrity verification)
- **Resource Techniques**: 25 (comprehensive resource obfuscation including layouts, drawables, strings, manifests, etc.)
- **enterprise-level Features**: 12 (single-character naming, multi-layer encryption, mathematical opaque predicates, custom VM)
- **Anti-Analysis Methods**: 18 (tool detection, timing attacks, environment detection, memory analysis)

##  Configuration

### ObfuscationConfig Interface

```kotlin
interface ObfuscationConfig {
    val isDebugMode: Boolean
}
```

### Custom Configuration

```kotlin
val customConfig = object : ObfuscationConfig {
    override val isDebugMode = BuildConfig.DEBUG
}

ObfuscationManager.initialize(context, customConfig)
```

##  Build Integration

### ProGuard/R8 Rules

The library includes comprehensive ProGuard rules in `proguard-rules.pro`:

```proguard
# Keep obfuscation manager and core classes
-keep class io.element.android.library.obfuscation.ObfuscationManager { *; }
-keep class io.element.android.library.obfuscation.ObfuscationManager$* { *; }

# Keep configuration interface
-keep interface io.element.android.library.obfuscation.ObfuscationConfig { *; }

# Keep all obfuscation utility classes
-keep class io.element.android.library.obfuscation.** { *; }
```

### Gradle Configuration

```gradle
android {
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 
                         'proguard-rules.pro'
        }
    }
}
```

##  Performance Impact

- **Build Time**: Increased due to obfuscation processing
- **APK Size**: Slightly increased due to obfuscation overhead
- **Runtime Performance**: Minimal impact with optimized implementation
- **Memory Usage**: Slightly increased due to runtime decryption

##  Security Benefits

- **Reverse Engineering**: Extremely difficult due to multiple obfuscation layers
- **Static Analysis**: Significantly reduced effectiveness
- **Code Theft**: Protected intellectual property
- **Data Privacy**: Comprehensive data masking and encryption
- **Tampering**: Anti-tampering and integrity verification

##  Testing

### Unit Tests

```kotlin
@Test
fun testObfuscationManager() {
    val config = object : ObfuscationConfig {
        override val isDebugMode = true
    }
    
    ObfuscationManager.initialize(context, config)
    val manager = ObfuscationManager.getInstance()
    
    assertNotNull(manager)
    assertTrue(manager.getObfuscationStatus().isNotEmpty())
}
```

### Integration Tests

```kotlin
@Test
fun testDataMasking() {
    val manager = ObfuscationManager.getInstance()
    val masked = manager.dataMasking.maskEmail("test@example.com")
    
    assertNotEquals("test@example.com", masked)
    assertTrue(masked.contains("*"))
}
```

##  Migration from App Module

If migrating from an app-specific obfuscation module:

1. **Update imports**:
   ```kotlin
   // Old
   import im.vector.app.obfuscation.ObfuscationManager
   
   // New
   import io.element.android.library.obfuscation.ObfuscationManager
   import io.element.android.library.obfuscation.ObfuscationConfig
   ```

2. **Update initialization**:
   ```kotlin
   // Old
   ObfuscationManager.initialize(context)
   
   // New
   ObfuscationManager.initialize(
       context = context,
       config = object : ObfuscationConfig {
           override val isDebugMode = BuildConfig.DEBUG
       }
   )
   ```

3. **Update usage**:
   ```kotlin
   // Old
   ObfuscationManager.StaticObfuscation.applyComprehensiveObfuscation { }
   
   // New
   val manager = ObfuscationManager.getInstance()
   manager.staticObfuscation.applyComprehensiveObfuscation { }
   ```

##  Advanced Usage

### Comprehensive Integration Example

```kotlin
class ComprehensiveObfuscationExample(private val context: Context) {
    private val obfuscationManager = ObfuscationManager.getInstance()

    fun demonstrateAllTechniques() {
        // 1. enterprise-level String Obfuscation
        val encrypted = obfuscationManager.staticObfuscation.obfuscateStringenterprise-levelStyle(
            "API_KEY_12345_SECRET", 
            "api_context"
        )
        
        // 2. Advanced Control Flow Obfuscation
        val result = obfuscationManager.staticObfuscation.obfuscatedBranch {
            if (userInput > 0) "Positive" else "Negative"
        }
        
        // 3. Method Obfuscation
        val inlinedBytecode = obfuscationManager.staticObfuscation.inlineMethod(
            "calculateTotal", 
            { 100.0 + (100.0 * 0.1) }
        )
        
        // 4. Code Virtualization
        val virtualResult = obfuscationManager.runtimeObfuscation.executeVirtualCode(bytecode)
        
        // 5. Polymorphic Code Generation
        val polymorphicCode = obfuscationManager.runtimeObfuscation.generatePolymorphicCode(
            "polymorphic_function", 
            originalCode
        )
        
        // 6. Data Masking
        val maskedEmail = obfuscationManager.dataMasking.maskEmail("user@example.com")
        
        // 7. Resource Obfuscation
        val obfuscatedResource = obfuscationManager.resourceObfuscation.obfuscateResourceName(
            "main_activity", 
            "layout"
        )
        
        // 8. Native Protection
        val obfuscatedLibrary = obfuscationManager.nativeObfuscation.obfuscateNativeLibrary(
            "/path/to/library.so"
        )
        
        // 9. Anti-Analysis
        val isAnalysisDetected = obfuscationManager.staticObfuscation.performAntiAnalysisCheck()
        
        // 10. Get Statistics
        val stats = obfuscationManager.getObfuscationStats()
        println("Total techniques: ${stats["total_techniques"]}")
    }
}
```

### Custom Obfuscation Techniques

```kotlin
// Create custom obfuscation configuration
val advancedConfig = object : ObfuscationConfig {
    override val isDebugMode = false // Force obfuscation even in debug
}

// Use with specific techniques
val manager = ObfuscationManager.getInstance()
manager.staticObfuscation.obfuscatedBranch {
    // This will always be obfuscated
    performSensitiveOperation()
}
```

### Integration with Dependency Injection

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object ObfuscationModule {
    
    @Provides
    @Singleton
    fun provideObfuscationConfig(): ObfuscationConfig {
        return object : ObfuscationConfig {
            override val isDebugMode = BuildConfig.DEBUG
        }
    }
    
    @Provides
    @Singleton
    fun provideObfuscationManager(
        context: Context,
        config: ObfuscationConfig
    ): ObfuscationManager {
        ObfuscationManager.initialize(context, config)
        return ObfuscationManager.getInstance()
    }
}
```

##  Important Notes

- **Debugging**: Obfuscated code is harder to debug (keep mapping files)
- **Reflection**: Some reflection-based code may need special ProGuard rules
- **Third-party Libraries**: Ensure compatibility with obfuscation
- **Testing**: Always test obfuscated builds thoroughly
- **Performance**: Monitor app performance after obfuscation

##  License

This library is part of Element Android and follows the same licensing terms:
- AGPL-3.0-only OR LicenseRef-Element-Commercial

##  Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

##  Support

For issues and questions:
- Create an issue in the Element Android repository
- Check the documentation in the main project
- Review the demo code in the `demo/` package

---

**Built for Element Android** - A secure, privacy-focused Matrix client
