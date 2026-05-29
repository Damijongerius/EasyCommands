package com.dami.easyCommands.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    
    // CommandPath -> (PlayerUUID -> ExpiryTimeMillis)
    private static final Map<String, Map<UUID, Long>> cooldowns = new HashMap<>();

    public static boolean isOnCooldown(String commandPath, UUID playerId) {
        Map<UUID, Long> commandCooldowns = cooldowns.get(commandPath);
        if (commandCooldowns == null) {
            return false;
        }
        Long expiry = commandCooldowns.get(playerId);
        return expiry != null && System.currentTimeMillis() < expiry;
    }

    public static long getRemainingSeconds(String commandPath, UUID playerId) {
        Map<UUID, Long> commandCooldowns = cooldowns.get(commandPath);
        if (commandCooldowns == null) {
            return 0;
        }
        Long expiry = commandCooldowns.get(playerId);
        if (expiry == null || System.currentTimeMillis() >= expiry) {
            return 0;
        }
        return (expiry - System.currentTimeMillis()) / 1000 + 1; // +1 to prevent 0s display
    }

    public static void setCooldown(String commandPath, UUID playerId, int seconds) {
        cooldowns.computeIfAbsent(commandPath, k -> new HashMap<>())
                 .put(playerId, System.currentTimeMillis() + (seconds * 1000L));
    }
}
