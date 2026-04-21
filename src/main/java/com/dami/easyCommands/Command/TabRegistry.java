package com.dami.easyCommands.Command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class TabRegistry {

    private static final Map<String, BiFunction<CommandSender, String[], List<String>>> registry = new HashMap<>();

    static {
        // Register default completers
        register("players", (sender, args) -> Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList()));

        register("worlds", (sender, args) -> Bukkit.getWorlds().stream()
                .map(world -> world.getName())
                .collect(Collectors.toList()));

        register("boolean", (sender, args) -> {
            List<String> list = new ArrayList<>();
            list.add("true");
            list.add("false");
            return list;
        });
    }

    public static void register(String name, BiFunction<CommandSender, String[], List<String>> provider) {
        registry.put(name.toLowerCase(), provider);
    }

    public static List<String> getCompletions(String name, CommandSender sender, String[] args) {
        BiFunction<CommandSender, String[], List<String>> provider = registry.get(name.toLowerCase());
        if (provider != null) {
            return provider.apply(sender, args);
        }
        return null;
    }
}
