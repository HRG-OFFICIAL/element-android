# Anti-Debug SDK ProGuard Rules
# This file contains ProGuard rules for the anti-debug SDK

# Keep all native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep all classes in the anti-debug package
-keep class com.example.antidebug.** { *; }

# Keep all public methods and fields
-keepclassmembers class com.example.antidebug.** {
    public *;
}

# Keep all data classes
-keep class com.example.antidebug.**$* { *; }

# Keep enums
-keepclassmembers enum com.example.antidebug.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep security-related classes
-keep class com.example.antidebug.AntiDebug { *; }
-keep class com.example.antidebug.DebuggerDetection { *; }
-keep class com.example.antidebug.RootDetection { *; }
-keep class com.example.antidebug.EmulatorDetection { *; }
-keep class com.example.antidebug.TamperDetection { *; }
-keep class com.example.antidebug.HookDetection { *; }
-keep class com.example.antidebug.BehavioralAnalysis { *; }
-keep class com.example.antidebug.DataProtection { *; }
-keep class com.example.antidebug.ResponseHandler { *; }
-keep class com.example.antidebug.ContinuousMonitoring { *; }

# Keep data classes
-keep class com.example.antidebug.SecurityReport { *; }
-keep class com.example.antidebug.MonitoringStatistics { *; }
-keep class com.example.antidebug.SecurityCheckResult { *; }
-keep class com.example.antidebug.ThreatInfo { *; }
-keep class com.example.antidebug.SecurityEvent { *; }
-keep class com.example.antidebug.AuditEvent { *; }

# Keep enums
-keep enum com.example.antidebug.ThreatType { *; }

# Obfuscate internal implementation details
-keep class com.example.antidebug.** {
    !public <methods>;
    !public <fields>;
}

# Remove debug information
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Remove Timber debug logs in release builds
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Keep only essential JNI methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Remove unused classes and methods
-dontwarn com.example.antidebug.**
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

# Keep reflection-based code
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Keep serialization
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Security: Remove stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Obfuscate package names
-repackageclasses 'a'
-flattenpackagehierarchy 'a'

# Remove debug information
-keepattributes !LocalVariableTable,!LocalVariableTypeTable

# Optimize for size
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

# Remove unused code
-dontwarn **
-ignorewarnings

# Keep native library interface
-keep class com.example.antidebug.AntiDebug {
    public static native boolean nativeHardenSystem();
    public static native boolean nativeProtectMemory(long, int);
    public static native void nativeRandomDelay();
    public static native boolean nativeAntiFork();
    public static native boolean nativeMemoryProtection();
    public static native boolean nativeBreakpointScan();
    public static native boolean nativeAdvancedTimingCheck();
    public static native boolean nativeProcessMonitoring();
    public static native boolean nativeMemoryIntegrity();
    public static native boolean nativeSignalHandling();
}
