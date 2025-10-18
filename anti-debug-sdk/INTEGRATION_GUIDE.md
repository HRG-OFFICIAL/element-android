# Anti-Debug SDK Integration Guide

## Overview

This guide provides comprehensive instructions for integrating the Anti-Debug SDK into Android applications. The SDK provides multiple layers of security protection including debugger detection, root detection, emulator detection, tamper detection, hook detection, behavioral analysis, and data protection.

## Quick Start

### 1. Add Dependency

Add the anti-debug-sdk to your app's `build.gradle`:

```gradle
dependencies {
    implementation project(':anti-debug-sdk')
}
```

### 2. Initialize the SDK

Initialize the SDK in your Application class:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize AntiDebug SDK
        AntiDebug.init(this, enableContinuousMonitoring = true)
    }
}
```

### 3. Basic Usage

```kotlin
// Check for security threats
val securityReport = AntiDebug.performSecurityCheck()

if (securityReport.hasThreats()) {
    // Handle security threats
    when {
        securityReport.debuggerDetected -> {
            // Handle debugger detection
        }
        securityReport.rootDetected -> {
            // Handle root detection
        }
        securityReport.emulatorDetected -> {
            // Handle emulator detection
        }
        // ... other threat types
    }
}
```

## API Reference

### Core Methods

#### `AntiDebug.init(context: Context, enableContinuousMonitoring: Boolean = false)`

Initializes the Anti-Debug SDK.

**Parameters:**
- `context`: Application context
- `enableContinuousMonitoring`: Whether to enable continuous background monitoring

#### `AntiDebug.performSecurityCheck(): SecurityReport`

Performs a comprehensive security check and returns a detailed report.

**Returns:** `SecurityReport` object containing threat detection results

#### `AntiDebug.isDebuggerAttached(): Boolean`

Checks if a debugger is currently attached to the application.

**Returns:** `true` if debugger is detected, `false` otherwise

#### `AntiDebug.isDeviceRooted(): Boolean`

Checks if the device is rooted.

**Returns:** `true` if root is detected, `false` otherwise

#### `AntiDebug.isRunningOnEmulator(): Boolean`

Checks if the application is running on an emulator.

**Returns:** `true` if emulator is detected, `false` otherwise

#### `AntiDebug.isApplicationTampered(): Boolean`

Checks if the application has been tampered with.

**Returns:** `true` if tampering is detected, `false` otherwise

#### `AntiDebug.areHooksDetected(): Boolean`

Checks if hooking frameworks are detected.

**Returns:** `true` if hooks are detected, `false` otherwise

#### `AntiDebug.isSuspiciousBehavior(): Boolean`

Checks for suspicious behavioral patterns.

**Returns:** `true` if suspicious behavior is detected, `false` otherwise

### Data Protection

#### `AntiDebug.getDataProtection(): DataProtection`

Gets the data protection instance for secure storage operations.

**Returns:** `DataProtection` object for encryption/decryption operations

**Example:**
```kotlin
val dataProtection = AntiDebug.getDataProtection()
val encrypted = dataProtection.encryptData("sensitive data")
val decrypted = dataProtection.decryptData(encrypted)
```

### Monitoring and Statistics

#### `AntiDebug.getMonitoringStatistics(): MonitoringStatistics`

Gets current monitoring statistics.

**Returns:** `MonitoringStatistics` object with monitoring information

#### `AntiDebug.performImmediateSecurityCheck(): SecurityCheckResult`

Performs an immediate security check with detailed timing information.

**Returns:** `SecurityCheckResult` object with check results and timing

#### `AntiDebug.pauseMonitoring()`

Pauses continuous monitoring.

#### `AntiDebug.resumeMonitoring()`

Resumes continuous monitoring.

#### `AntiDebug.stopMonitoring()`

Stops continuous monitoring.

#### `AntiDebug.startContinuousMonitoring()`

Starts continuous monitoring.

### Cleanup

#### `AntiDebug.cleanup()`

Cleans up resources and stops all monitoring.

## Data Classes

### SecurityReport

Contains the results of a security check.

```kotlin
data class SecurityReport(
    val debuggerDetected: Boolean,
    val rootDetected: Boolean,
    val emulatorDetected: Boolean,
    val tamperingDetected: Boolean,
    val hooksDetected: Boolean,
    val suspiciousBehavior: Boolean,
    val timestamp: Long,
    val threats: List<ThreatInfo>
) {
    fun hasThreats(): Boolean
    fun getThreatCount(): Int
}
```

### ThreatInfo

Contains information about a specific threat.

```kotlin
data class ThreatInfo(
    val type: ThreatType,
    val severity: Int,
    val description: String,
    val timestamp: Long
)
```

### ThreatType

Enumeration of threat types.

```kotlin
enum class ThreatType {
    DEBUGGER_DETECTED,
    ROOT_DETECTED,
    EMULATOR_DETECTED,
    TAMPER_DETECTED,
    HOOK_DETECTED,
    BEHAVIORAL_ANOMALY,
    DATA_BREACH,
    UNKNOWN
}
```

### MonitoringStatistics

Contains monitoring statistics.

```kotlin
data class MonitoringStatistics(
    val isMonitoring: Boolean,
    val monitoringDuration: Long,
    val totalThreats: Int,
    val lastThreatTime: Long,
    val averageCheckTime: Long
)
```

### SecurityCheckResult

Contains detailed security check results.

```kotlin
data class SecurityCheckResult(
    val timestamp: Long,
    val duration: Long,
    val threats: List<ThreatInfo>,
    val debuggerDetected: Boolean,
    val rootDetected: Boolean,
    val emulatorDetected: Boolean,
    val tamperingDetected: Boolean,
    val hooksDetected: Boolean,
    val suspiciousBehavior: Boolean
)
```

## Integration Examples

### Basic Integration

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Perform security check
        val securityReport = AntiDebug.performSecurityCheck()
        
        if (securityReport.hasThreats()) {
            // Handle security threats
            handleSecurityThreats(securityReport)
        }
    }
    
    private fun handleSecurityThreats(report: SecurityReport) {
        when {
            report.debuggerDetected -> {
                // Show warning or exit app
                showSecurityWarning("Debugger detected")
            }
            report.rootDetected -> {
                // Show warning or limit functionality
                showSecurityWarning("Root detected")
            }
            // ... handle other threats
        }
    }
}
```

### Advanced Integration with Continuous Monitoring

```kotlin
class SecurityManager {
    private var isMonitoring = false
    
    fun startSecurityMonitoring() {
        if (!isMonitoring) {
            AntiDebug.startContinuousMonitoring()
            isMonitoring = true
        }
    }
    
    fun stopSecurityMonitoring() {
        if (isMonitoring) {
            AntiDebug.stopMonitoring()
            isMonitoring = false
        }
    }
    
    fun getSecurityStatus(): SecurityStatus {
        val statistics = AntiDebug.getMonitoringStatistics()
        val report = AntiDebug.performSecurityCheck()
        
        return SecurityStatus(
            isMonitoring = statistics.isMonitoring,
            totalThreats = statistics.totalThreats,
            hasActiveThreats = report.hasThreats()
        )
    }
}
```

### Data Protection Integration

```kotlin
class SecureDataManager {
    private val dataProtection = AntiDebug.getDataProtection()
    
    fun storeSecureData(key: String, data: String) {
        val encrypted = dataProtection.encryptData(data)
        // Store encrypted data securely
        storeEncryptedData(key, encrypted)
    }
    
    fun retrieveSecureData(key: String): String? {
        val encrypted = getEncryptedData(key)
        return if (encrypted != null) {
            dataProtection.decryptData(encrypted)
        } else {
            null
        }
    }
}
```

## Configuration

### ProGuard Rules

Add the following ProGuard rules to your `proguard-rules.pro`:

```proguard
# Anti-Debug SDK Rules
-keep class com.example.antidebug.** { *; }
-keepclassmembers class com.example.antidebug.** {
    public <methods>;
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep data classes
-keep class com.example.antidebug.SecurityReport { *; }
-keep class com.example.antidebug.MonitoringStatistics { *; }
-keep class com.example.antidebug.SecurityCheckResult { *; }
-keep class com.example.antidebug.ThreatInfo { *; }
-keep enum com.example.antidebug.ThreatType { *; }
```

### Build Configuration

Ensure your app's `build.gradle` includes the anti-debug-sdk dependency:

```gradle
dependencies {
    implementation project(':anti-debug-sdk')
}
```

## Best Practices

### 1. Initialize Early

Initialize the Anti-Debug SDK as early as possible in your application lifecycle, preferably in the `Application.onCreate()` method.

### 2. Handle Security Threats Appropriately

Implement appropriate responses to detected security threats based on your application's security requirements:

- **Debugger Detection**: Consider terminating the app or showing warnings
- **Root Detection**: Limit functionality or show warnings
- **Emulator Detection**: Block access or show warnings
- **Tamper Detection**: Terminate the app or clear sensitive data
- **Hook Detection**: Terminate the app or show warnings
- **Suspicious Behavior**: Log events and consider limiting functionality

### 3. Use Continuous Monitoring Wisely

Enable continuous monitoring for production builds to detect threats in real-time, but be aware of the performance impact.

### 4. Secure Data Storage

Use the provided data protection methods for storing sensitive information:

```kotlin
val dataProtection = AntiDebug.getDataProtection()
val encrypted = dataProtection.encryptData("sensitive data")
// Store encrypted data securely
```

### 5. Monitor Performance

Monitor the performance impact of security checks and adjust the monitoring frequency as needed.

### 6. Log Security Events

Log security events for analysis and monitoring:

```kotlin
val statistics = AntiDebug.getMonitoringStatistics()
Log.d("Security", "Total threats detected: ${statistics.totalThreats}")
```

## Troubleshooting

### Common Issues

1. **Native Library Loading Errors**
   - Ensure the native library is properly built
   - Check that the library is included in the APK

2. **ProGuard Obfuscation Issues**
   - Ensure all necessary ProGuard rules are included
   - Check that native methods are not obfuscated

3. **Performance Issues**
   - Consider reducing monitoring frequency
   - Use selective monitoring for specific threat types

4. **False Positives**
   - Review detection thresholds
   - Implement whitelisting for known safe environments

### Debug Mode

For debugging purposes, you can disable continuous monitoring and use manual security checks:

```kotlin
AntiDebug.init(context, enableContinuousMonitoring = false)
```

## Security Considerations

### 1. Threat Response

Implement appropriate responses to detected threats based on your security requirements. Consider the following response strategies:

- **Immediate Response**: Terminate the app for critical threats
- **Graduated Response**: Show warnings and limit functionality
- **Logging Response**: Log events for analysis

### 2. Data Protection

Use the provided encryption methods for sensitive data:

```kotlin
val dataProtection = AntiDebug.getDataProtection()
val encrypted = dataProtection.encryptData("sensitive data")
```

### 3. Continuous Monitoring

Enable continuous monitoring for production builds to detect threats in real-time.

### 4. Resource Management

Monitor resource usage and adjust monitoring frequency as needed.

## Support

For support and questions about the Anti-Debug SDK, please refer to the documentation or contact the development team.

## License

This SDK is provided under the project's license terms. Please refer to the LICENSE file for details.
