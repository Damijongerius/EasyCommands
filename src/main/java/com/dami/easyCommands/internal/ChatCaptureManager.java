package com.dami.easyCommands.internal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatCaptureManager implements Listener {

    private static final Map<UUID, String> activeCaptures = new ConcurrentHashMap<>();
    private static boolean registered = false;

    public static void register(Plugin plugin) {
        if (registered) return;
        Bukkit.getPluginManager().registerEvents(new ChatCaptureManager(), plugin);
        registered = true;
    }

    public static void startCapture(Player player, String command) {
        startCapture(player, command, null);
    }

    public static void startCapture(Player player, String command, String usage) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("EpicKingdom");
        if (plugin != null) {
            register(plugin);
        }

        activeCaptures.put(player.getUniqueId(), command);
        player.sendMessage(" ");
        player.sendMessage("§e§l» §7Type the arguments for §a/" + command + " §7in chat:");
        if (usage != null && !usage.isEmpty()) {
            player.sendMessage("§e§l» §cUsage: §7/" + command + " " + usage);
        }
        player.sendMessage("§8Type §ccancel §8to abort.");
        player.sendMessage(" ");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String command = activeCaptures.remove(player.getUniqueId());
        if (command == null) return;

        event.setCancelled(true);
        String message = event.getMessage();

        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage("§cArguments entry cancelled.");
            return;
        }

        Plugin plugin = Bukkit.getPluginManager().getPlugin("EpicKingdom");
        if (plugin != null) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.performCommand(command + " " + message);
            });
        } else {
            player.performCommand(command + " " + message);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        activeCaptures.remove(event.getPlayer().getUniqueId());
    }
}
