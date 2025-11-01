# Consumer ProGuard Rules for RASP SDK
# These rules are applied to projects that use this library

# Keep all public API methods
-keep class com.example.raspsdk.RASP {
    public static <methods>;
}

# Keep data classes that might be used by consumers
-keep class com.example.raspsdk.SecurityReport { *; }
-keep class com.example.raspsdk.MonitoringStatistics { *; }
-keep class com.example.raspsdk.SecurityCheckResult { *; }
-keep class com.example.raspsdk.ThreatInfo { *; }

# Keep enums
-keep enum com.example.raspsdk.ThreatType { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Don't obfuscate the public API
-keep class com.example.raspsdk.RASP {
    public static void init(android.content.Context, boolean);
    public static void init(android.content.Context);
    public static boolean isDebuggerAttached();
    public static boolean isDeviceRooted();
    public static boolean isRunningOnEmulator();
    public static boolean isApplicationTampered();
    public static boolean areHooksDetected();
    public static boolean isSuspiciousBehavior();
    public static com.example.raspsdk.SecurityReport performSecurityCheck();
    public static com.example.raspsdk.DataProtection getDataProtection();
    public static void configureResponse(com.example.raspsdk.ResponseHandler$ResponseType);
    public static void handleThreat(com.example.raspsdk.ThreatType);
    public static void startContinuousMonitoring();
    public static void stopMonitoring();
    public static com.example.raspsdk.MonitoringStatistics getMonitoringStatistics();
    public static com.example.raspsdk.SecurityCheckResult performImmediateSecurityCheck();
    public static void pauseMonitoring();
    public static void resumeMonitoring();
    public static void cleanup();
}

# Keep response handler methods
-keep class com.example.raspsdk.ResponseHandler {
    public <methods>;
}

# Keep data protection methods
-keep class com.example.raspsdk.DataProtection {
    public <methods>;
}

# Keep continuous monitoring methods
-keep class com.example.raspsdk.ContinuousMonitoring {
    public <methods>;
}

