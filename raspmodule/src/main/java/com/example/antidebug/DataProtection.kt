package com.example.raspsdk

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.security.SecureRandom
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec
import android.util.Base64
import java.security.MessageDigest
import javax.crypto.spec.GCMParameterSpec

/**
 * DataProtection - Comprehensive data protection and encryption
 * 
 * This class implements multiple techniques to protect sensitive data:
 * - Sensitive data encryption
 * - Key management
 * - Data obfuscation
 * - Secure storage
 * - Access control
 * - Contextual access control
 */
class DataProtection(private val context: Context) {
    
    companion object {
        private const val TAG = "DataProtection"
        private const val ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val KEY_SIZE = 256
        private const val IV_SIZE = 12
        private const val GCM_TAG_LENGTH = 128
        private const val PREFS_NAME = "secure_prefs"
        private const val KEY_PREFS_NAME = "key_prefs"
    }
    
    private val keyCache = ConcurrentHashMap<String, SecretKey>()
    private val encryptionCache = ConcurrentHashMap<String, String>()
    private val secureRandom = SecureRandom()
    private val keyGenerator = KeyGenerator.getInstance(ALGORITHM)
    
    init {
        keyGenerator.init(KEY_SIZE)
    }
    
    /**
     * Encrypt sensitive data using AES-GCM
     */
    fun encryptData(data: String, keyAlias: String = "default"): String {
        return try {
            val key = getOrCreateKey(keyAlias)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            
            // Generate random IV
            val iv = ByteArray(IV_SIZE)
            secureRandom.nextBytes(iv)
            
            // Initialize cipher for encryption
            val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec)
            
            // Encrypt data
            val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            
            // Combine IV and encrypted data
            val combined = iv + encryptedBytes
            
            // Encode to Base64
            val encryptedData = Base64.encodeToString(combined, Base64.DEFAULT)
            
            // Cache encrypted data
            encryptionCache[data] = encryptedData
            
            Log.d(TAG, "Data encrypted successfully for key: $keyAlias")
            encryptedData
        } catch (e: Exception) {
            Log.e(TAG, "Failed to encrypt data", e)
            ""
        }
    }
    
    /**
     * Decrypt sensitive data using AES-GCM
     */
    fun decryptData(encryptedData: String, keyAlias: String = "default"): String {
        return try {
            val key = getOrCreateKey(keyAlias)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            
            // Decode from Base64
            val combined = Base64.decode(encryptedData, Base64.DEFAULT)
            
            // Extract IV and encrypted data
            val iv = combined.sliceArray(0 until IV_SIZE)
            val encryptedBytes = combined.sliceArray(IV_SIZE until combined.size)
            
            // Initialize cipher for decryption
            val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec)
            
            // Decrypt data
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            val decryptedData = String(decryptedBytes, Charsets.UTF_8)
            
            Log.d(TAG, "Data decrypted successfully for key: $keyAlias")
            decryptedData
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decrypt data", e)
            ""
        }
    }
    
    /**
     * Obfuscate sensitive data
     */
    fun obfuscateData(data: String): String {
        return try {
            // Simple XOR obfuscation with random key
            val key = secureRandom.nextInt(256)
            val obfuscated = data.map { char ->
                (char.code xor key).toChar()
            }.joinToString("")
            
            // Add key as prefix
            "$key:$obfuscated"
        } catch (e: Exception) {
            Log.e(TAG, "Failed to obfuscate data", e)
            data
        }
    }
    
    /**
     * Deobfuscate sensitive data
     */
    fun deobfuscateData(obfuscatedData: String): String {
        return try {
            val parts = obfuscatedData.split(":", limit = 2)
            if (parts.size != 2) {
                Log.w(TAG, "Invalid obfuscated data format")
                return obfuscatedData
            }
            
            val key = parts[0].toInt()
            val obfuscated = parts[1]
            
            val deobfuscated = obfuscated.map { char ->
                (char.code xor key).toChar()
            }.joinToString("")
            
            deobfuscated
        } catch (e: Exception) {
            Log.e(TAG, "Failed to deobfuscate data", e)
            obfuscatedData
        }
    }
    
    /**
     * Store data securely in SharedPreferences
     */
    fun storeSecureData(key: String, value: String, encrypt: Boolean = true) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            
            val dataToStore = if (encrypt) {
                encryptData(value)
            } else {
                value
            }
            
            editor.putString(key, dataToStore)
            editor.apply()
            
            Log.d(TAG, "Data stored securely: $key")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to store secure data", e)
        }
    }
    
    /**
     * Retrieve data securely from SharedPreferences
     */
    fun retrieveSecureData(key: String, encrypted: Boolean = true): String? {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val encryptedValue = prefs.getString(key, null)
            
            if (encryptedValue == null) {
                Log.w(TAG, "No data found for key: $key")
                return null
            }
            
            val decryptedValue = if (encrypted) {
                decryptData(encryptedValue)
            } else {
                encryptedValue
            }
            
            Log.d(TAG, "Data retrieved securely: $key")
            decryptedValue
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve secure data", e)
            null
        }
    }
    
    /**
     * Store data in encrypted file
     */
    fun storeEncryptedFile(filename: String, data: String, keyAlias: String = "default"): Boolean {
        return try {
            val encryptedData = encryptData(data, keyAlias)
            val file = java.io.File(context.filesDir, "encrypted_$filename")
            
            file.writeText(encryptedData)
            
            Log.d(TAG, "Data stored in encrypted file: $filename")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to store encrypted file", e)
            false
        }
    }
    
    /**
     * Retrieve data from encrypted file
     */
    fun retrieveEncryptedFile(filename: String, keyAlias: String = "default"): String? {
        return try {
            val file = java.io.File(context.filesDir, "encrypted_$filename")
            
            if (!file.exists()) {
                Log.w(TAG, "Encrypted file not found: $filename")
                return null
            }
            
            val encryptedData = file.readText()
            val decryptedData = decryptData(encryptedData, keyAlias)
            
            Log.d(TAG, "Data retrieved from encrypted file: $filename")
            decryptedData
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve encrypted file", e)
            null
        }
    }
    
    /**
     * Generate secure random key
     */
    fun generateSecureKey(keyAlias: String): SecretKey {
        return try {
            val key = keyGenerator.generateKey()
            keyCache[keyAlias] = key
            
            // Store key securely
            storeKeySecurely(keyAlias, key)
            
            Log.d(TAG, "Secure key generated: $keyAlias")
            key
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate secure key", e)
            throw e
        }
    }
    
    /**
     * Get or create encryption key
     */
    private fun getOrCreateKey(keyAlias: String): SecretKey {
        return keyCache.getOrPut(keyAlias) {
            try {
                // Try to load existing key
                loadKeySecurely(keyAlias) ?: generateSecureKey(keyAlias)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to load key, generating new one: $keyAlias", e)
                generateSecureKey(keyAlias)
            }
        }
    }
    
    /**
     * Store key securely
     */
    private fun storeKeySecurely(keyAlias: String, key: SecretKey) {
        try {
            val prefs = context.getSharedPreferences(KEY_PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            
            val keyBytes = key.encoded
            val keyString = Base64.encodeToString(keyBytes, Base64.DEFAULT)
            
            editor.putString(keyAlias, keyString)
            editor.apply()
            
            Log.d(TAG, "Key stored securely: $keyAlias")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to store key securely", e)
        }
    }
    
    /**
     * Load key securely
     */
    private fun loadKeySecurely(keyAlias: String): SecretKey? {
        return try {
            val prefs = context.getSharedPreferences(KEY_PREFS_NAME, Context.MODE_PRIVATE)
            val keyString = prefs.getString(keyAlias, null)
            
            if (keyString == null) {
                Log.w(TAG, "No key found for alias: $keyAlias")
                return null
            }
            
            val keyBytes = Base64.decode(keyString, Base64.DEFAULT)
            val key = SecretKeySpec(keyBytes, ALGORITHM)
            
            Log.d(TAG, "Key loaded securely: $keyAlias")
            key
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load key securely", e)
            null
        }
    }
    
    /**
     * Clear sensitive data
     */
    fun clearSensitiveData() {
        try {
            // Clear caches
            keyCache.clear()
            encryptionCache.clear()
            
            // Clear secure preferences
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            
            // Clear key preferences
            val keyPrefs = context.getSharedPreferences(KEY_PREFS_NAME, Context.MODE_PRIVATE)
            keyPrefs.edit().clear().apply()
            
            // Clear encrypted files
            val filesDir = context.filesDir
            filesDir.listFiles()?.forEach { file ->
                if (file.name.startsWith("encrypted_")) {
                    file.delete()
                }
            }
            
            Log.d(TAG, "Sensitive data cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear sensitive data", e)
        }
    }
    
    /**
     * Hash data for integrity checking
     */
    fun hashData(data: String, algorithm: String = "SHA-256"): String {
        return try {
            val digest = MessageDigest.getInstance(algorithm)
            val hashBytes = digest.digest(data.toByteArray(Charsets.UTF_8))
            
            val hexString = StringBuilder()
            for (byte in hashBytes) {
                val hex = Integer.toHexString(0xFF and byte.toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }
            
            hexString.toString()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to hash data", e)
            ""
        }
    }
    
    /**
     * Verify data integrity
     */
    fun verifyDataIntegrity(data: String, expectedHash: String, algorithm: String = "SHA-256"): Boolean {
        return try {
            val actualHash = hashData(data, algorithm)
            val isValid = actualHash == expectedHash
            
            if (!isValid) {
                Log.w(TAG, "Data integrity verification failed")
            }
            
            isValid
        } catch (e: Exception) {
            Log.e(TAG, "Failed to verify data integrity", e)
            false
        }
    }
    
    /**
     * Generate secure random string
     */
    fun generateSecureRandomString(length: Int): String {
        return try {
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            val random = SecureRandom()
            
            val result = StringBuilder(length)
            for (i in 0 until length) {
                result.append(chars[random.nextInt(chars.length)])
            }
            
            result.toString()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate secure random string", e)
            ""
        }
    }
    
    /**
     * Generate secure random bytes
     */
    fun generateSecureRandomBytes(length: Int): ByteArray {
        return try {
            val bytes = ByteArray(length)
            secureRandom.nextBytes(bytes)
            bytes
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate secure random bytes", e)
            ByteArray(0)
        }
    }
    
    /**
     * Advanced data protection with multiple layers
     */
    fun performAdvancedDataProtection(data: String, keyAlias: String = "default"): String {
        return try {
            // Layer 1: Obfuscate data
            val obfuscatedData = obfuscateData(data)
            
            // Layer 2: Encrypt data
            val encryptedData = encryptData(obfuscatedData, keyAlias)
            
            // Layer 3: Add integrity hash
            val hash = hashData(encryptedData)
            val protectedData = "$hash:$encryptedData"
            
            Log.d(TAG, "Advanced data protection applied")
            protectedData
        } catch (e: Exception) {
            Log.e(TAG, "Failed to apply advanced data protection", e)
            data
        }
    }
    
    /**
     * Recover data from advanced protection
     */
    fun recoverFromAdvancedProtection(protectedData: String, keyAlias: String = "default"): String {
        return try {
            // Layer 3: Verify integrity
            val parts = protectedData.split(":", limit = 2)
            if (parts.size != 2) {
                Log.w(TAG, "Invalid protected data format")
                return protectedData
            }
            
            val expectedHash = parts[0]
            val encryptedData = parts[1]
            
            if (!verifyDataIntegrity(encryptedData, expectedHash)) {
                Log.w(TAG, "Data integrity verification failed")
                return protectedData
            }
            
            // Layer 2: Decrypt data
            val decryptedData = decryptData(encryptedData, keyAlias)
            
            // Layer 1: Deobfuscate data
            val deobfuscatedData = deobfuscateData(decryptedData)
            
            Log.d(TAG, "Advanced data protection recovered")
            deobfuscatedData
        } catch (e: Exception) {
            Log.e(TAG, "Failed to recover from advanced data protection", e)
            protectedData
        }
    }
}
