package com.dami.easyCommands.core;

import com.dami.easyCommands.model.MessageKey;
import org.bukkit.command.CommandSender;
import java.util.HashMap;
import java.util.Map;

public interface MessageHandler {
    void sendMessage(CommandSender sender, MessageKey key, Map<String, String> placeholders);

    class DefaultMessageHandler implements MessageHandler {
        private final Map<MessageKey, String> messages = new HashMap<>();

        public DefaultMessageHandler() {
            messages.put(MessageKey.NO_PERMISSION, "§cYou don't have permission to use this command.");
            messages.put(MessageKey.PLAYER_ONLY, "§cOnly players can use this command.");
            messages.put(MessageKey.CONSOLE_ONLY, "§cOnly the console can use this command.");
            messages.put(MessageKey.COMMAND_NOT_FOUND, "§cNo command found. Try /%command% help");
            messages.put(MessageKey.INVALID_USAGE, "§cInvalid usage. Use: %usage%");
            messages.put(MessageKey.HELP_HEADER, "§6--- §eEasyCommands Help: /%command% §6---");
            messages.put(MessageKey.HELP_FOOTER, "§6-------------------------------------");
            messages.put(MessageKey.HELP_COMMAND_FORMAT, "§e/%command% %sub% §7- %description%");
            messages.put(MessageKey.INVALID_ARGUMENT, "§cInvalid argument '%arg%' for parameter '%param%'.");
            messages.put(MessageKey.MIN_VALUE, "§cParameter '%param%' must be at least %min%.");
            messages.put(MessageKey.MAX_VALUE, "§cParameter '%param%' must be at most %max%.");
            messages.put(MessageKey.DID_YOU_MEAN, "§cCommand not found. Did you mean /%command% %suggestion%?");
        }

        @Override
        public void sendMessage(CommandSender sender, MessageKey key, Map<String, String> placeholders) {
            String message = messages.getOrDefault(key, "Message not found: " + key);
            if (placeholders != null) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    message = message.replace("%" + entry.getKey() + "%", entry.getValue());
                }
            }
            sender.sendMessage(message);
        }

        public void setMessage(MessageKey key, String message) {
            messages.put(key, message);
        }
    }
}
