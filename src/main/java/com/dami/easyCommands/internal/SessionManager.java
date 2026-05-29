package com.dami.easyCommands.internal;

import com.dami.easyCommands.model.CommandSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    private static final Map<UUID, CommandSession> sessions = new HashMap<>();

    public static CommandSession getSession(UUID uuid) {
        return sessions.computeIfAbsent(uuid, k -> new CommandSession());
    }
    
    public static void clearSession(UUID uuid) {
        sessions.remove(uuid);
    }
}
