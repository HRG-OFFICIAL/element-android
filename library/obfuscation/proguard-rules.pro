# enterprise-level ProGuard Rules for Maximum Obfuscation
# These rules implement industry-leading obfuscation techniques

# ===== CORE PROTECTION =====
# Keep obfuscation manager and core classes
-keep class io.element.android.library.obfuscation.ObfuscationManager { *; }
-keep class io.element.android.library.obfuscation.ObfuscationManager$* { *; }

# Keep configuration interface
-keep interface io.element.android.library.obfuscation.ObfuscationConfig { *; }

# ===== AGGRESSIVE OBFUSCATION SETTINGS =====
# enterprise-level-style aggressive obfuscation
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 10
-allowaccessmodification
-repackageclasses ''
-printmapping mapping.txt
-verbose

# Rename classes to single characters (enterprise-level-style: a0, b2, c3, etc.)
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Maximum obfuscation settings
-dontpreverify
-dontwarn **
-ignorewarnings
-overloadaggressively
-useuniqueclassmembernames
-flattenpackagehierarchy 'a'
-adaptclassstrings

# ===== IDENTIFIER OBFUSCATION =====
# enterprise-level-style identifier renaming
-adaptresourcefilenames **.properties,**.gif,**.jpg
-adaptresourcefilecontents **.properties,META-INF/MANIFEST.MF

# Rename all identifiers to single characters
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# ===== CONTROL FLOW OBFUSCATION =====
# Enable control flow obfuscation
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5

# ===== STRING OBFUSCATION =====
# Obfuscate string constants
-adaptclassstrings
-adaptresourcefilenames **.properties,**.gif,**.jpg
-adaptresourcefilecontents **.properties,META-INF/MANIFEST.MF

# ===== ANTI-ANALYSIS PROTECTION =====
# Remove debugging information
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Obfuscate exception handling
-keepattributes Exceptions

# ===== NATIVE CODE PROTECTION =====
# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep JNI methods
-keepclasseswithmembernames class * {
    @dalvik.annotation.optimization.CriticalNative <methods>;
}

# ===== REFLECTION PROTECTION =====
# Keep reflection calls
-keepclassmembers class * {
    @io.element.android.library.obfuscation.** *;
}

# Keep classes used in reflection
-keepclassmembers class * {
    @java.lang.reflect.** *;
}

# ===== SERIALIZATION PROTECTION =====
# Keep serialization
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ===== ENUM PROTECTION =====
# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ===== ANDROID SPECIFIC PROTECTION =====
# Keep Android components
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep Android annotations
-keep class android.support.annotation.** { *; }
-keep class androidx.annotation.** { *; }

# ===== OBFUSCATION UTILITY CLASSES =====
# Keep obfuscation utility classes (but obfuscate their internals)
-keep class io.element.android.library.obfuscation.static.** { *; }
-keep class io.element.android.library.obfuscation.runtime.** { *; }
-keep class io.element.android.library.obfuscation.data.** { *; }
-keep class io.element.android.library.obfuscation.native.** { *; }

# ===== DEMO CLASSES (OPTIONAL) =====
# Keep demo classes (optional)
-keep class io.element.android.library.obfuscation.demo.** { *; }

# ===== ANTI-DEBUG PROTECTION =====
# Keep anti-debug classes
-keep class com.example.antidebug.** { *; }

# ===== ADVANCED OBFUSCATION TECHNIQUES =====
# enterprise-level-style class renaming
-flattenpackagehierarchy 'a'

# Obfuscate method names
-useuniqueclassmembernames

# Obfuscate field names
-allowaccessmodification

# ===== RESOURCE OBFUSCATION =====
# Obfuscate resource files
-adaptresourcefilenames **.properties,**.gif,**.jpg,**.png
-adaptresourcefilecontents **.properties,META-INF/MANIFEST.MF

# ===== PACKAGE OBFUSCATION =====
# Flatten package hierarchy
-flattenpackagehierarchy 'a'

# ===== FINAL OBFUSCATION SETTINGS =====
# Maximum obfuscation
-dontpreverify
-dontwarn **
-ignorewarnings
-overloadaggressively
-useuniqueclassmembernames
-adaptclassstrings
-adaptresourcefilenames **.properties,**.gif,**.jpg
-adaptresourcefilecontents **.properties,META-INF/MANIFEST.MF

# ===== OBFUSCATION UTILITY METHODS =====
# Keep obfuscation utility methods
-keepclassmembers class * {
    public static * obfuscate*(...);
    public static * deobfuscate*(...);
    public static * mask*(...);
    public static * encrypt*(...);
    public static * decrypt*(...);
}

# Keep classes that might be dynamically loaded
-keep class io.element.android.library.obfuscation.** {
    public <init>(...);
}

# Keep obfuscation configuration
-keep class io.element.android.library.obfuscation.** {
    public static final *;
}

# Keep obfuscation statistics and status methods
-keepclassmembers class io.element.android.library.obfuscation.ObfuscationManager {
    public static * getObfuscationStatus();
    public static * getObfuscationStats();
}