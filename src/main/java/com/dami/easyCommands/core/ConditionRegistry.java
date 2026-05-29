package com.dami.easyCommands.core;

import com.dami.easyCommands.model.Condition;
import java.util.HashMap;
import java.util.Map;

public class ConditionRegistry {
    private static final Map<String, Condition> conditions = new HashMap<>();

    public static void register(String id, Condition condition) {
        conditions.put(id.toLowerCase(), condition);
    }

    public static Condition get(String id) {
        return conditions.get(id.toLowerCase());
    }
}
