# RASP SDK ProGuard Rules
# This file contains ProGuard rules for the RASP SDK

# Keep all native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep all classes in the anti-debug package
-keep class com.example.raspsdk.** { *; }

# Keep all public methods and fields
-keepclassmembers class com.example.raspsdk.** {
    public *;
}

# Keep all data classes
-keep class com.example.raspsdk.**$* { *; }

# Keep enums
-keepclassmembers enum com.example.raspsdk.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep security-related classes
-keep class com.example.raspsdk.RASP { *; }
-keep class com.example.raspsdk.DebuggerDetection { *; }
-keep class com.example.raspsdk.RootDetection { *; }
-keep class com.example.raspsdk.EmulatorDetection { *; }
-keep class com.example.raspsdk.TamperDetection { *; }
-keep class com.example.raspsdk.HookDetection { *; }
-keep class com.example.raspsdk.BehavioralAnalysis { *; }
-keep class com.example.raspsdk.DataProtection { *; }
-keep class com.example.raspsdk.ResponseHandler { *; }
-keep class com.example.raspsdk.ContinuousMonitoring { *; }

# Keep data classes
-keep class com.example.raspsdk.SecurityReport { *; }
-keep class com.example.raspsdk.MonitoringStatistics { *; }
-keep class com.example.raspsdk.SecurityCheckResult { *; }
-keep class com.example.raspsdk.ThreatInfo { *; }
-keep class com.example.raspsdk.SecurityEvent { *; }
-keep class com.example.raspsdk.AuditEvent { *; }

# Keep enums
-keep enum com.example.raspsdk.ThreatType { *; }

# Obfuscate internal implementation details
-keep class com.example.raspsdk.** {
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
-dontwarn com.example.raspsdk.**
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
-keep class com.example.raspsdk.RASP {
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

