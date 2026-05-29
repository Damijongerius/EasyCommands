package com.dami.easyCommands.internal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatPromptManager implements Listener {

    private static boolean isRegistered = false;
    private static Plugin providingPlugin = null;
    private static final Map<UUID, Consumer<String>> pendingPrompts = new HashMap<>();
    private static final Map<UUID, java.util.List<String>> withheldMessages = new HashMap<>();

    public static void prompt(Plugin plugin, Player player, Consumer<String> onResponse) {
        if (!isRegistered) {
            Bukkit.getPluginManager().registerEvents(new ChatPromptManager(), plugin);
            providingPlugin = plugin;
            isRegistered = true;
        }
        UUID playerId = player.getUniqueId();
        pendingPrompts.put(playerId, onResponse);
        withheldMessages.put(playerId, new java.util.ArrayList<>());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        if (pendingPrompts.containsKey(playerId)) {
            event.setCancelled(true);
            Consumer<String> responseConsumer = pendingPrompts.remove(playerId);
            java.util.List<String> queuedMessages = withheldMessages.remove(playerId);
            String message = event.getMessage();
            
            // Execute on main thread
            Bukkit.getScheduler().runTask(providingPlugin, () -> {
                responseConsumer.accept(message);
                
                if (queuedMessages != null && !queuedMessages.isEmpty() && player.isOnline()) {
                    player.sendMessage("§8[§7... while you were typing ...§8]");
                    for (String msg : queuedMessages) {
                        player.sendMessage(msg);
                    }
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onOthersChat(AsyncPlayerChatEvent event) {
        java.util.Iterator<Player> iterator = event.getRecipients().iterator();
        String formattedMessage = null;
        
        while (iterator.hasNext()) {
            Player recipient = iterator.next();
            UUID recipientId = recipient.getUniqueId();
            
            if (pendingPrompts.containsKey(recipientId) && !event.getPlayer().equals(recipient)) {
                // Remove the prompted player from receiving this chat message right now
                iterator.remove();
                
                // Format the message if we haven't already
                if (formattedMessage == null) {
                    formattedMessage = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
                }
                
                // Queue it for later
                withheldMessages.get(recipientId).add(formattedMessage);
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        pendingPrompts.remove(playerId);
        withheldMessages.remove(playerId);
    }
}
