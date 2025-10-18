package com.example.antidebug

import android.content.Context
import android.util.Log
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * BehavioralAnalysis - Comprehensive behavioral analysis for threat detection
 * 
 * This class implements multiple techniques to detect suspicious behavior:
 * - Execution pattern analysis
 * - User behavior analysis
 * - System behavior analysis
 * - Resource usage monitoring
 * - Network activity analysis
 * - Timing pattern analysis
 */
class BehavioralAnalysis(private val context: Context) {
    
    companion object {
        private const val TAG = "BehavioralAnalysis"
        
        // Behavioral thresholds
        private const val MAX_EXECUTION_TIME_MS = 1000L
        private const val MAX_CPU_USAGE_PERCENT = 80.0
        private const val MAX_MEMORY_USAGE_MB = 100
        private const val MAX_NETWORK_REQUESTS_PER_MINUTE = 100
        private const val MAX_SUSPICIOUS_ACTIVITIES = 5
        private const val TIMING_VARIANCE_THRESHOLD = 0.5
    }
    
    private val executionTimes = ConcurrentHashMap<String, MutableList<Long>>()
    private val resourceUsage = ConcurrentHashMap<String, MutableList<Double>>()
    private val suspiciousActivities = AtomicInteger(0)
    private val lastActivityTime = AtomicLong(System.currentTimeMillis())
    private val activityCount = AtomicInteger(0)
    
    /**
     * Main method to check for suspicious behavior
     * Combines multiple analysis techniques
     */
    fun isSuspiciousBehaviorDetected(): Boolean {
        return try {
            val checks = listOf(
                ::checkExecutionPatterns,
                ::checkUserBehavior,
                ::checkSystemBehavior,
                ::checkResourceUsage,
                ::checkNetworkActivity,
                ::checkTimingPatterns,
                ::checkInteractionPatterns,
                ::checkNavigationPatterns,
                ::checkDeviceStateChanges,
                ::checkEnvironmentChanges
            )
            
            // Return true if any check detects suspicious behavior
            checks.any { check ->
                try {
                    check.invoke()
                } catch (e: Exception) {
                    Log.w(TAG, "Behavioral check failed: ${e.message}")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in behavioral analysis", e)
            false
        }
    }
    
    /**
     * Check execution patterns for anomalies
     */
    private fun checkExecutionPatterns(): Boolean {
        return try {
            val currentTime = System.currentTimeMillis()
            val executionTime = measureExecutionTime {
                // Simulate critical operation
                performCriticalOperation()
            }
            
            // Store execution time for analysis
            val operationName = "critical_operation"
            executionTimes.getOrPut(operationName) { mutableListOf() }.add(executionTime)
            
            // Keep only last 100 measurements
            val times = executionTimes[operationName]!!
            if (times.size > 100) {
                times.removeAt(0)
            }
            
            // Check for execution time anomalies
            if (executionTime > MAX_EXECUTION_TIME_MS) {
                Log.d(TAG, "Execution time anomaly detected: ${executionTime}ms")
                suspiciousActivities.incrementAndGet()
                return true
            }
            
            // Check for timing variance
            if (times.size >= 10) {
                val variance = calculateVariance(times.map { it.toDouble() })
                val average = times.average()
                val coefficientOfVariation = sqrt(variance) / average
                
                if (coefficientOfVariation > TIMING_VARIANCE_THRESHOLD) {
                    Log.d(TAG, "High timing variance detected: $coefficientOfVariation")
                    suspiciousActivities.incrementAndGet()
                    return true
                }
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Execution pattern check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check user behavior patterns
     */
    private fun checkUserBehavior(): Boolean {
        return try {
            val currentTime = System.currentTimeMillis()
            val timeSinceLastActivity = currentTime - lastActivityTime.get()
            
            // Update activity count
            activityCount.incrementAndGet()
            lastActivityTime.set(currentTime)
            
            // Check for rapid, repetitive activities (bot-like behavior)
            if (timeSinceLastActivity < 100) { // Less than 100ms between activities
                Log.d(TAG, "Rapid activity detected: ${timeSinceLastActivity}ms")
                suspiciousActivities.incrementAndGet()
                return true
            }
            
            // Check for too many activities in short time
            val activitiesPerMinute = activityCount.get() * 60000 / (currentTime - lastActivityTime.get() + 1)
            if (activitiesPerMinute > 1000) { // More than 1000 activities per minute
                Log.d(TAG, "Excessive activity detected: $activitiesPerMinute per minute")
                suspiciousActivities.incrementAndGet()
                return true
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "User behavior check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check system behavior for anomalies
     */
    private fun checkSystemBehavior(): Boolean {
        return try {
            // Check for unusual system calls
            val systemCallCount = getSystemCallCount()
            if (systemCallCount > 10000) { // More than 10k system calls
                Log.d(TAG, "Excessive system calls detected: $systemCallCount")
                suspiciousActivities.incrementAndGet()
                return true
            }
            
            // Check for unusual file access patterns
            val fileAccessCount = getFileAccessCount()
            if (fileAccessCount > 1000) { // More than 1k file accesses
                Log.d(TAG, "Excessive file access detected: $fileAccessCount")
                suspiciousActivities.incrementAndGet()
                return true
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "System behavior check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check resource usage for anomalies
     */
    private fun checkResourceUsage(): Boolean {
        return try {
            val runtime = Runtime.getRuntime()
            val totalMemory = runtime.totalMemory()
            val freeMemory = runtime.freeMemory()
            val usedMemory = totalMemory - freeMemory
            val usedMemoryMB = usedMemory / (1024 * 1024)
            
            // Store memory usage for analysis
            resourceUsage.getOrPut("memory") { mutableListOf() }.add(usedMemoryMB.toDouble())
            
            // Check for excessive memory usage
            if (usedMemoryMB > MAX_MEMORY_USAGE_MB) {
                Log.d(TAG, "Excessive memory usage detected: ${usedMemoryMB}MB")
                suspiciousActivities.incrementAndGet()
                return true
            }
            
            // Check for memory usage spikes
            val memoryUsage = resourceUsage["memory"]!!
            if (memoryUsage.size >= 10) {
                val recentUsage = memoryUsage.takeLast(10)
                val averageUsage = recentUsage.average()
                val maxUsage = recentUsage.maxOrNull() ?: 0.0
                
                if (maxUsage > averageUsage * 2) {
                    Log.d(TAG, "Memory usage spike detected: max=${maxUsage}MB, avg=${averageUsage}MB")
                    suspiciousActivities.incrementAndGet()
                    return true
                }
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Resource usage check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check network activity for anomalies
     */
    private fun checkNetworkActivity(): Boolean {
        return try {
            // This is a simplified check - in real implementation, you would monitor actual network traffic
            val networkRequestCount = getNetworkRequestCount()
            
            if (networkRequestCount > MAX_NETWORK_REQUESTS_PER_MINUTE) {
                Log.d(TAG, "Excessive network activity detected: $networkRequestCount requests")
                suspiciousActivities.incrementAndGet()
                return true
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Network activity check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check timing patterns for anomalies
     */
    private fun checkTimingPatterns(): Boolean {
        return try {
            val currentTime = System.currentTimeMillis()
            val timeSinceLastCheck = currentTime - lastActivityTime.get()
            
            // Check for suspicious timing patterns
            if (timeSinceLastCheck < 50) { // Less than 50ms between checks
                Log.d(TAG, "Suspicious timing pattern detected: ${timeSinceLastCheck}ms")
                suspiciousActivities.incrementAndGet()
                return true
            }
            
            // Check for too regular timing patterns (might indicate automation)
            val timingPattern = getTimingPattern()
            if (timingPattern.isRegular()) {
                Log.d(TAG, "Regular timing pattern detected (possible automation)")
                suspiciousActivities.incrementAndGet()
                return true
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Timing pattern check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check interaction patterns for anomalies
     */
    private fun checkInteractionPatterns(): Boolean {
        return try {
            // Check for rapid, repetitive interactions
            val interactionTimes = getInteractionTimes()
            if (interactionTimes.size >= 10) {
                val recentInteractions = interactionTimes.takeLast(10)
                val intervals = recentInteractions.zipWithNext { a, b -> b - a }
                val averageInterval = intervals.average()
                val minInterval = intervals.minOrNull() ?: 0L
                
                // Check for too regular intervals (possible automation)
                if (minInterval < 100 && abs(averageInterval - minInterval) < 10) {
                    Log.d(TAG, "Regular interaction pattern detected (possible automation)")
                    suspiciousActivities.incrementAndGet()
                    return true
                }
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Interaction pattern check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check navigation patterns for anomalies
     */
    private fun checkNavigationPatterns(): Boolean {
        return try {
            // Check for systematic navigation patterns
            val navigationSequence = getNavigationSequence()
            if (navigationSequence.size >= 5) {
                // Check for repetitive navigation patterns
                val pattern = navigationSequence.takeLast(5)
                val isRepetitive = pattern.all { it == pattern[0] }
                
                if (isRepetitive) {
                    Log.d(TAG, "Repetitive navigation pattern detected")
                    suspiciousActivities.incrementAndGet()
                    return true
                }
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Navigation pattern check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check device state changes for anomalies
     */
    private fun checkDeviceStateChanges(): Boolean {
        return try {
            // Check for unusual device state changes
            val stateChanges = getDeviceStateChanges()
            if (stateChanges.size > 10) { // More than 10 state changes
                Log.d(TAG, "Excessive device state changes detected: ${stateChanges.size}")
                suspiciousActivities.incrementAndGet()
                return true
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Device state change check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Check environment changes for anomalies
     */
    private fun checkEnvironmentChanges(): Boolean {
        return try {
            // Check for unusual environment changes
            val environmentChanges = getEnvironmentChanges()
            if (environmentChanges.size > 5) { // More than 5 environment changes
                Log.d(TAG, "Excessive environment changes detected: ${environmentChanges.size}")
                suspiciousActivities.incrementAndGet()
                return true
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Environment change check failed: ${e.message}")
            false
        }
    }
    
    /**
     * Perform critical operation for timing analysis
     */
    private fun performCriticalOperation() {
        // Simulate critical operation
        var result = 0
        for (i in 1..1000) {
            result += i * i
        }
    }
    
    /**
     * Measure execution time of a block
     */
    private fun measureExecutionTime(block: () -> Unit): Long {
        val startTime = System.currentTimeMillis()
        block()
        val endTime = System.currentTimeMillis()
        return endTime - startTime
    }
    
    /**
     * Calculate variance of a list of values
     */
    private fun calculateVariance(values: List<Double>): Double {
        val mean = values.average()
        return values.map { (it - mean) * (it - mean) }.average()
    }
    
    /**
     * Get system call count (simplified)
     */
    private fun getSystemCallCount(): Int {
        // This is a simplified implementation
        // In real implementation, you would use native code to get actual system call count
        return (Math.random() * 1000).toInt()
    }
    
    /**
     * Get file access count (simplified)
     */
    private fun getFileAccessCount(): Int {
        // This is a simplified implementation
        // In real implementation, you would monitor actual file access
        return (Math.random() * 100).toInt()
    }
    
    /**
     * Get network request count (simplified)
     */
    private fun getNetworkRequestCount(): Int {
        // This is a simplified implementation
        // In real implementation, you would monitor actual network requests
        return (Math.random() * 50).toInt()
    }
    
    /**
     * Get timing pattern (simplified)
     */
    private fun getTimingPattern(): List<Long> {
        // This is a simplified implementation
        // In real implementation, you would track actual timing patterns
        return listOf(100L, 105L, 98L, 102L, 99L)
    }
    
    /**
     * Get interaction times (simplified)
     */
    private fun getInteractionTimes(): List<Long> {
        // This is a simplified implementation
        // In real implementation, you would track actual interaction times
        return listOf(
            System.currentTimeMillis() - 1000,
            System.currentTimeMillis() - 800,
            System.currentTimeMillis() - 600,
            System.currentTimeMillis() - 400,
            System.currentTimeMillis() - 200
        )
    }
    
    /**
     * Get navigation sequence (simplified)
     */
    private fun getNavigationSequence(): List<String> {
        // This is a simplified implementation
        // In real implementation, you would track actual navigation
        return listOf("A", "B", "C", "D", "E")
    }
    
    /**
     * Get device state changes (simplified)
     */
    private fun getDeviceStateChanges(): List<String> {
        // This is a simplified implementation
        // In real implementation, you would track actual state changes
        return listOf("screen_on", "screen_off", "wifi_on", "wifi_off")
    }
    
    /**
     * Get environment changes (simplified)
     */
    private fun getEnvironmentChanges(): List<String> {
        // This is a simplified implementation
        // In real implementation, you would track actual environment changes
        return listOf("locale_changed", "timezone_changed")
    }
    
    /**
     * Check if timing pattern is regular
     */
    private fun List<Long>.isRegular(): Boolean {
        if (size < 3) return false
        val intervals = zipWithNext { a, b -> b - a }
        val averageInterval = intervals.average()
        return intervals.all { abs(it - averageInterval) < 5 }
    }
    
    /**
     * Advanced behavioral analysis using scoring system
     */
    fun performAdvancedBehavioralAnalysis(): Boolean {
        return try {
            var behaviorScore = 0
            
            // High weight checks
            if (checkExecutionPatterns()) behaviorScore += 5
            if (checkUserBehavior()) behaviorScore += 4
            if (checkSystemBehavior()) behaviorScore += 4
            
            // Medium weight checks
            if (checkResourceUsage()) behaviorScore += 3
            if (checkNetworkActivity()) behaviorScore += 3
            if (checkTimingPatterns()) behaviorScore += 2
            
            // Low weight checks
            if (checkInteractionPatterns()) behaviorScore += 1
            if (checkNavigationPatterns()) behaviorScore += 1
            if (checkDeviceStateChanges()) behaviorScore += 1
            if (checkEnvironmentChanges()) behaviorScore += 1
            
            val isSuspicious = behaviorScore >= 3 || suspiciousActivities.get() >= MAX_SUSPICIOUS_ACTIVITIES
            Log.d(TAG, "Advanced behavioral analysis score: $behaviorScore, suspicious: $isSuspicious")
            
            isSuspicious
        } catch (e: Exception) {
            Log.w(TAG, "Advanced behavioral analysis failed: ${e.message}")
            false
        }
    }
    
    /**
     * Reset behavioral analysis data
     */
    fun resetBehavioralData() {
        executionTimes.clear()
        resourceUsage.clear()
        suspiciousActivities.set(0)
        activityCount.set(0)
        lastActivityTime.set(System.currentTimeMillis())
        Log.d(TAG, "Behavioral analysis data reset")
    }
}
