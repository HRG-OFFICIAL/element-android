# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class im.vector.app.features.** { *; }

## print all the rules in a file
# -printconfiguration ../proguard_files/full-r8-config.txt

# WebRTC

-keep class org.webrtc.** { *; }
-dontwarn org.chromium.build.BuildHooksAndroid

# Jitsi (else callbacks are not called)

-keep class org.jitsi.meet.** { *; }
-keep class org.jitsi.meet.sdk.** { *; }

# React Native

# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.proguard.annotations.DoNotStrip
-keep,allowobfuscation @interface com.facebook.proguard.annotations.KeepGettersAndSetters
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.proguard.annotations.DoNotStrip class *
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.proguard.annotations.DoNotStrip *;
    @com.facebook.common.internal.DoNotStrip *;
}

-keepclassmembers @com.facebook.proguard.annotations.KeepGettersAndSetters class * {
  void set*(***);
  *** get*();
}

-keep class * extends com.facebook.react.bridge.JavaScriptModule { *; }
-keep class * extends com.facebook.react.bridge.NativeModule { *; }
-keepclassmembers,includedescriptorclasses class * { native <methods>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.UIProp <fields>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactProp <methods>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactPropGroup <methods>; }

-dontwarn com.facebook.react.**
-keep,includedescriptorclasses class com.facebook.react.bridge.** { *; }

-keepattributes InnerClasses

# JWT dependencies
-keep class io.jsonwebtoken.** { *; }
-keepnames class io.jsonwebtoken.* { *; }
-keepnames interface io.jsonwebtoken.* { *; }

-keep class org.bouncycastle.** { *; }
-keepnames class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

# JNA
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }

# New
-dontwarn com.google.appengine.api.urlfetch.**
-dontwarn com.google.common.io.LimitInputStream
-dontwarn com.google.firebase.analytics.connector.AnalyticsConnector
-dontwarn com.google.javascript.jscomp.**
-dontwarn com.likethesalad.android.templates.provider.api.TemplatesProvider
-dontwarn com.yahoo.platform.yui.compressor.**
-dontwarn java.awt.**
-dontwarn org.apache.velocity.**
-dontwarn org.commonmark.ext.gfm.strikethrough.Strikethrough
-dontwarn org.mozilla.javascript.**
-dontwarn org.slf4j.**
-dontwarn org.jspecify.annotations.NullMarked

# Security and Obfuscation Rules
# RASP SDK
-keep class im.vector.app.security.** { *; }
-keep class im.vector.app.security.RASP { *; }
-keep class im.vector.app.security.SecurityReport { *; }
-keep class im.vector.app.security.ThreatType { *; }
-keep class im.vector.app.security.ResponseHandler { *; }
-keep class im.vector.app.security.DataProtection { *; }

# Keep native methods for anti-debug
-keepclasseswithmembernames class im.vector.app.security.** {
    native <methods>;
}

# Obfuscation Manager
-keep class im.vector.app.obfuscation.ObfuscationManager { *; }
-keep class im.vector.app.obfuscation.ObfuscationManager$* { *; }

# Keep obfuscation utility classes
-keep class im.vector.app.obfuscation.static.** { *; }
-keep class im.vector.app.obfuscation.runtime.** { *; }
-keep class im.vector.app.obfuscation.data.** { *; }
-keep class im.vector.app.obfuscation.native.** { *; }

# Keep obfuscation utility methods
-keepclassmembers class im.vector.app.obfuscation.** {
    public static * mask*(...);
    public static * encrypt*(...);
    public static * decrypt*(...);
    public static * obfuscate*(...);
}

# Keep security initialization methods
-keepclassmembers class im.vector.app.VectorApplication {
    public void initializeSecurity();
    public void initializeObfuscation();
}

# Keep security monitoring methods
-keepclassmembers class im.vector.app.** {
    public void performSecurityCheck();
    public void handleSecurityThreat(...);
}

# Keep obfuscation status methods
-keepclassmembers class im.vector.app.obfuscation.ObfuscationManager {
    public static * getObfuscationStatus();
    public static * getObfuscationStats();
}

# Keep data masking methods
-keepclassmembers class im.vector.app.obfuscation.data.DataMasking {
    public static * mask*(...);
    public static * encrypt*(...);
    public static * decrypt*(...);
}

# Keep reflection-based security classes
-keepclassmembers class * {
    @im.vector.app.obfuscation.runtime.ReflectionIndirection *;
}

# Keep security-related serializable classes
-keep class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# RASP SDK Rules
-keep class com.example.raspsdk.** { *; }
-keepclassmembers class com.example.raspsdk.** {
    public <methods>;
}

# Keep native methods for anti-debug
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep anti-debug data classes
-keep class com.example.raspsdk.SecurityReport { *; }
-keep class com.example.raspsdk.MonitoringStatistics { *; }
-keep class com.example.raspsdk.SecurityCheckResult { *; }
-keep class com.example.raspsdk.ThreatInfo { *; }
-keep enum com.example.raspsdk.ThreatType { *; }

# Keep anti-debug public API
-keep class com.example.raspsdk.RASP {
    public static <methods>;
}

# Keep response handler
-keep class com.example.raspsdk.ResponseHandler {
    public <methods>;
}

# Keep data protection
-keep class com.example.raspsdk.DataProtection {
    public <methods>;
}

# Keep continuous monitoring
-keep class com.example.raspsdk.ContinuousMonitoring {
    public <methods>;
}

# R8-Specific Rules for Anti-Debug and Obfuscation Integration
# Optimized rules for better R8 integration and smaller APK size

# Keep only essential security classes (more aggressive obfuscation)
-keep class com.example.raspsdk.RASP { *; }
-keep class com.example.raspsdk.DebuggerDetection { *; }
-keep class com.example.raspsdk.RootDetection { *; }
-keep class com.example.raspsdk.ResponseHandler { *; }
-keep class com.example.raspsdk.DataProtection { *; }
-keep class com.example.raspsdk.ContinuousMonitoring { *; }

# Keep obfuscation manager but allow internal classes to be obfuscated
-keep class io.element.android.library.obfuscation.ObfuscationManager { *; }
-keepclassmembers class io.element.android.library.obfuscation.** {
    public static <methods>;
    public <methods>;
}

# Keep security initialization and monitoring methods
-keepclassmembers class im.vector.app.VectorApplication {
    public void initializeSecurity();
    public void initializeObfuscation();
    public void performSecurityCheck();
    public void handleSecurityThreat(...);
}

# Keep obfuscation manager and its methods
-keep class io.element.android.library.obfuscation.ObfuscationManager {
    public static <methods>;
    public <methods>;
}

# Optimized obfuscation rules - allow internal classes to be obfuscated
-keepclassmembers class io.element.android.library.obfuscation.** {
    public static * mask*(...);
    public static * encrypt*(...);
    public static * decrypt*(...);
    public static * obfuscate*(...);
    public static * getObfuscationStatus();
    public static * getObfuscationStats();
}

# Allow R8 to optimize internal obfuscation classes
-keepclassmembers class io.element.android.library.obfuscation.static.** {
    public <methods>;
}
-keepclassmembers class io.element.android.library.obfuscation.runtime.** {
    public <methods>;
}
-keepclassmembers class io.element.android.library.obfuscation.data.** {
    public <methods>;
}
-keepclassmembers class io.element.android.library.obfuscation.native.** {
    public <methods>;
}

# Keep anti-debug native methods and JNI functions
-keepclasseswithmembernames class com.example.raspsdk.** {
    native <methods>;
}

# Keep anti-debug detection methods
-keepclassmembers class com.example.raspsdk.DebuggerDetection {
    public static <methods>;
    public <methods>;
}

-keepclassmembers class com.example.raspsdk.RootDetection {
    public static <methods>;
    public <methods>;
}

# Keep security response handling
-keepclassmembers class com.example.raspsdk.ResponseHandler {
    public <methods>;
    private <methods>;
}

# Keep security data classes
-keep class com.example.raspsdk.SecurityReport { *; }
-keep class com.example.raspsdk.MonitoringStatistics { *; }
-keep class com.example.raspsdk.SecurityCheckResult { *; }
-keep class com.example.raspsdk.ThreatInfo { *; }
-keep enum com.example.raspsdk.ThreatType { *; }

# Keep reflection-based security classes
-keepclassmembers class * {
    @io.element.android.library.obfuscation.runtime.ReflectionIndirection *;
}

# Keep security-related serializable classes
-keep class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Google API Client - Missing classes during R8 minification
-dontwarn com.google.api.client.http.GenericUrl
-dontwarn com.google.api.client.http.HttpHeaders
-dontwarn com.google.api.client.http.HttpRequest
-dontwarn com.google.api.client.http.HttpRequestFactory
-dontwarn com.google.api.client.http.HttpResponse
-dontwarn com.google.api.client.http.HttpTransport
-dontwarn com.google.api.client.http.javanet.NetHttpTransport$Builder
-dontwarn com.google.api.client.http.javanet.NetHttpTransport
-dontwarn org.joda.time.Instant

# Exclude problematic Tink classes that require Google API Client
-assumenosideeffects class com.google.crypto.tink.util.KeysDownloader {
    public static java.lang.String fetchAndCacheData(...);
}

# Keep Google API Client classes if they exist
-keep class com.google.api.client.http.** { *; }
-keep class com.google.api.client.http.javanet.** { *; }
-keep class org.joda.time.** { *; }

# R8 Optimization Rules for Better Performance and Smaller APK
# Allow aggressive optimization while preserving security functionality

# Enable R8 optimizations
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# Aggressive shrinking for smaller APK (let R8 shrink and show warnings)
# -dontshrink  // removed to allow code shrinking
-dontwarn **
# -ignorewarnings  // removed to keep visibility on potential issues

# Keep only essential security methods, allow others to be optimized
-keepclassmembers class com.example.raspsdk.** {
    public static <methods>;
    public <methods>;
    native <methods>;
}

# Allow R8 to optimize unused security methods
-assumenosideeffects class com.example.raspsdk.** {
    public static void log*(...);
    public static void debug*(...);
}

# Optimize reflection usage
-keepclassmembers class * {
    @io.element.android.library.obfuscation.runtime.ReflectionIndirection *;
}

# Allow aggressive optimization of data classes
-keepclassmembers class com.example.raspsdk.SecurityReport {
    public <init>(...);
    public <methods>;
}
-keepclassmembers class com.example.raspsdk.MonitoringStatistics {
    public <init>(...);
    public <methods>;
}
