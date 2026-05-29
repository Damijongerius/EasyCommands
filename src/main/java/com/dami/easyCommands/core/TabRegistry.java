package com.dami.easyCommands.core;

import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabRegistry {
    private static final Map<String, TabCompleter> registry = new HashMap<>();

    public static void register(String key, TabCompleter completer) {
        registry.put(key, completer);
    }

    public static List<String> getCompletions(String key, CommandSender sender) {
        TabCompleter completer = registry.get(key);
        if (completer != null) {
            return completer.complete(sender);
        }
        return null;
    }

    @FunctionalInterface
    public interface TabCompleter {
        List<String> complete(CommandSender sender);
    }
}
