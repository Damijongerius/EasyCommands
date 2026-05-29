package com.dami.easyCommands.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConfirmationManager {
    
    // UUID -> (CommandSignature -> ExpiryTimeMillis)
    private static final Map<UUID, Map<String, Long>> pendingConfirmations = new HashMap<>();

    public static boolean isConfirmed(UUID playerId, String cmdSignature) {
        Map<String, Long> playerConfirmations = pendingConfirmations.get(playerId);
        if (playerConfirmations == null) {
            return false;
        }
        Long expiry = playerConfirmations.get(cmdSignature);
        if (expiry != null && System.currentTimeMillis() <= expiry) {
            return true;
        }
        if (expiry != null) {
            playerConfirmations.remove(cmdSignature); // clean up expired
        }
        return false;
    }

    public static void requestConfirmation(UUID playerId, String cmdSignature, int timeoutSeconds) {
        pendingConfirmations.computeIfAbsent(playerId, k -> new HashMap<>())
                            .put(cmdSignature, System.currentTimeMillis() + (timeoutSeconds * 1000L));
    }

    public static void removeConfirmation(UUID playerId, String cmdSignature) {
        Map<String, Long> playerConfirmations = pendingConfirmations.get(playerId);
        if (playerConfirmations != null) {
            playerConfirmations.remove(cmdSignature);
        }
    }
}
