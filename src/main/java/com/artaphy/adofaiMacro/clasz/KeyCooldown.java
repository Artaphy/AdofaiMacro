package com.artaphy.adofaiMacro.clasz;

import java.util.*;

/**
 * Manages key cooldowns and provides key selection based on priority and cooldown status
 */
public class KeyCooldown {
    // Key priority order (from highest to lowest)
    private static final int[] PRIORITY_KEYS = {
            75,  // K
            70,  // F
            76,  // L
            68,  // D
            83,  // S
            59,  // ;
            65   // A
    };
    
    // Default cooldown time in milliseconds
    private static final long DEFAULT_COOLDOWN_MS = 120;
    
    // Current cooldown time in nanoseconds
    private long cooldownTimeNanos = DEFAULT_COOLDOWN_MS * 1_000_000;
    
    // Map to store the last press time for each key
    private final Map<Integer, Long> lastPressTime = new HashMap<>();
    
    /**
     * Set cooldown time in milliseconds
     * @param milliseconds The cooldown time in milliseconds
     */
    public void setCooldownTime(long milliseconds) {
        this.cooldownTimeNanos = milliseconds * 1_000_000;
    }
    
    /**
     * Get current cooldown time in milliseconds
     * @return Current cooldown time in milliseconds
     */
    public long getCooldownTimeMs() {
        return cooldownTimeNanos / 1_000_000;
    }
    
    /**
     * Get the next available key based on priority and cooldown
     * @param currentTime Current time in nanoseconds
     * @return The key code of the available key with highest priority, or -1 if no key is available
     */
    public int getNextAvailableKey(long currentTime) {
        for (int key : PRIORITY_KEYS) {
            if (isKeyAvailable(key, currentTime)) {
                lastPressTime.put(key, currentTime);
                return key;
            }
        }
        return -1;
    }
    
    /**
     * Check if a key is available (not in cooldown)
     * @param key The key to check
     * @param currentTime Current time in nanoseconds
     * @return true if the key is available, false otherwise
     */
    private boolean isKeyAvailable(int key, long currentTime) {
        Long lastPress = lastPressTime.get(key);
        if (lastPress == null) {
            return true;
        }
        return (currentTime - lastPress) >= cooldownTimeNanos;
    }
    
    /**
     * Reset the cooldown for all keys
     */
    public void reset() {
        lastPressTime.clear();
    }
} 