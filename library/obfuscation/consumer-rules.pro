# Consumer ProGuard rules for obfuscation library
# These rules are applied to projects that consume this library

# Keep obfuscation manager and core classes
-keep class io.element.android.library.obfuscation.ObfuscationManager { *; }
-keep class io.element.android.library.obfuscation.ObfuscationManager$* { *; }

# Keep configuration interface
-keep interface io.element.android.library.obfuscation.ObfuscationConfig { *; }

# Keep static obfuscation classes
-keep class io.element.android.library.obfuscation.static.** { *; }

# Keep runtime obfuscation classes
-keep class io.element.android.library.obfuscation.runtime.** { *; }

# Keep data masking classes
-keep class io.element.android.library.obfuscation.data.** { *; }

# Keep native obfuscation classes
-keep class io.element.android.library.obfuscation.native.** { *; }

# Keep demo classes (for testing)
-keep class io.element.android.library.obfuscation.demo.** { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep reflection-based classes
-keepclassmembers class * {
    @io.element.android.library.obfuscation.runtime.ReflectionIndirection *;
}

# Keep encryption/decryption methods
-keepclassmembers class * {
    public * encrypt*(...);
    public * decrypt*(...);
    public * mask*(...);
    public * obfuscate*(...);
}

# Keep obfuscation utility methods
-keepclassmembers class * {
    public static * obfuscate*(...);
    public static * deobfuscate*(...);
    public static * mask*(...);
    public static * encrypt*(...);
    public static * decrypt*(...);
}
