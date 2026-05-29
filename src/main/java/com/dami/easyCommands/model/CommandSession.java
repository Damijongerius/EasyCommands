package com.dami.easyCommands.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a session context that persists data across multiple command executions.
 */
public class CommandSession {
    private final Map<String, Object> data = new HashMap<>();

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public Object get(String key) {
        return data.get(key);
    }
    
    public <T> T get(String key, Class<T> clazz) {
        Object obj = data.get(key);
        if (clazz.isInstance(obj)) {
            return clazz.cast(obj);
        }
        return null;
    }

    public void clear() {
        data.clear();
    }
}
