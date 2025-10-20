# Security Modules Documentation

This fork of Element Android includes custom security modules for testing and research purposes.

## Overview

This repository has been forked from the official [Element Android](https://github.com/element-hq/element-android) to integrate and test advanced security features including anti-debugging and code obfuscation capabilities.

## Modules

### 1. Anti-Debug SDK (`anti-debug-sdk/`)

A comprehensive anti-debugging solution that provides multiple layers of protection:

#### Features
- **Debugger Detection**: Detects various debugging tools and environments
- **Root Detection**: Identifies rooted devices and potential security risks
- **Emulator Detection**: Prevents execution in emulated environments
- **Runtime Monitoring**: Continuous security threat assessment
- **Native Implementation**: C++ code for enhanced security and performance

#### Integration
- Seamlessly integrated into the main Android application
- Configurable security levels for debug vs release builds
- R8/ProGuard rules for code protection

### 2. Obfuscation Manager (`library/obfuscation/`)

Advanced code obfuscation and data protection system:

#### Features
- **Static Obfuscation**: Compile-time code transformation
- **Runtime Data Masking**: Dynamic data protection
- **String Encryption**: Sensitive string obfuscation
- **Reflection Indirection**: Method call obfuscation
- **R8 Integration**: Works with Android's R8 minifier

#### Capabilities
- Class and method name obfuscation
- Control flow obfuscation
- Data encryption and decryption
- Anti-tampering measures

## Build Configuration

### Debug Builds
- Security features are disabled for easier development
- Full debugging capabilities available
- Faster build times

### Release Builds
- Full security protection enabled
- R8 minification and obfuscation active
- Optimized for production use

## Usage

### Building the Project
```bash
# Debug build (security disabled)
./gradlew assembleGplayDebug

# Release build (full security enabled)
./gradlew assembleGplayRelease
```

### Configuration
Security settings can be configured in `gradle.properties`:
```properties
# Disable security in debug builds
vector.disableSecurityInDebug=true
vector.disableObfuscationInDebug=true
vector.useMinimalStartupInDebug=true
```

## Important Notes

⚠️ **This is a research/testing fork**
- Not intended for production use
- For official Element Android, visit: [element-hq/element-android](https://github.com/element-hq/element-android)
- Security modules are experimental and may not be fully tested
- Use at your own risk

## License

This fork maintains the same dual licensing as the original Element Android project:
- GNU Affero General Public License v3
- Element Commercial License

The security modules are provided as-is for research and testing purposes.
