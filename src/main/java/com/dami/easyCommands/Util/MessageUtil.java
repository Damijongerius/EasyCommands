package com.dami.easyCommands.Util;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public class MessageUtil {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static void sendMessage(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) return;
        sender.sendMessage(miniMessage.deserialize(message));
    }

    public static MiniMessage getMiniMessage() {
        return miniMessage;
    }
}
