# Consumer ProGuard Rules for Anti-Debug SDK
# These rules are applied to projects that use this library

# Keep all public API methods
-keep class com.example.antidebug.AntiDebug {
    public static <methods>;
}

# Keep data classes that might be used by consumers
-keep class com.example.antidebug.SecurityReport { *; }
-keep class com.example.antidebug.MonitoringStatistics { *; }
-keep class com.example.antidebug.SecurityCheckResult { *; }
-keep class com.example.antidebug.ThreatInfo { *; }

# Keep enums
-keep enum com.example.antidebug.ThreatType { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Don't obfuscate the public API
-keep class com.example.antidebug.AntiDebug {
    public static void init(android.content.Context, boolean);
    public static void init(android.content.Context);
    public static boolean isDebuggerAttached();
    public static boolean isDeviceRooted();
    public static boolean isRunningOnEmulator();
    public static boolean isApplicationTampered();
    public static boolean areHooksDetected();
    public static boolean isSuspiciousBehavior();
    public static com.example.antidebug.SecurityReport performSecurityCheck();
    public static com.example.antidebug.DataProtection getDataProtection();
    public static void configureResponse(com.example.antidebug.ResponseHandler$ResponseType);
    public static void handleThreat(com.example.antidebug.ThreatType);
    public static void startContinuousMonitoring();
    public static void stopMonitoring();
    public static com.example.antidebug.MonitoringStatistics getMonitoringStatistics();
    public static com.example.antidebug.SecurityCheckResult performImmediateSecurityCheck();
    public static void pauseMonitoring();
    public static void resumeMonitoring();
    public static void cleanup();
}

# Keep response handler methods
-keep class com.example.antidebug.ResponseHandler {
    public <methods>;
}

# Keep data protection methods
-keep class com.example.antidebug.DataProtection {
    public <methods>;
}

# Keep continuous monitoring methods
-keep class com.example.antidebug.ContinuousMonitoring {
    public <methods>;
}
