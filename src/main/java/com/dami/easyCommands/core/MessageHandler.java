package com.dami.easyCommands.core;

import com.dami.easyCommands.model.MessageKey;
import org.bukkit.command.CommandSender;
import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public interface MessageHandler {
    void sendMessage(CommandSender sender, MessageKey key, Map<String, String> placeholders);

    class DefaultMessageHandler implements MessageHandler {
        protected final Map<MessageKey, String> messages = new HashMap<>();

        public DefaultMessageHandler() {
            messages.put(MessageKey.NO_PERMISSION, "<red>You don't have permission to use this command.</red>");
            messages.put(MessageKey.PLAYER_ONLY, "<red>Only players can use this command.</red>");
            messages.put(MessageKey.CONSOLE_ONLY, "<red>Only the console can use this command.</red>");
            messages.put(MessageKey.COMMAND_NOT_FOUND, "<red>No command found. Try /<command> help</red>");
            messages.put(MessageKey.INVALID_USAGE, "<red>Invalid usage. Use: <usage></red>");
            messages.put(MessageKey.HELP_HEADER, "<gold>--- <yellow>EasyCommands Help: /<command> <gold>---</gold>");
            messages.put(MessageKey.HELP_FOOTER, "<gold>--- Page <page>/<max_page> ---</gold> <click:run_command:/<command> help <next_page>><yellow>[Next ></yellow></click>");
            messages.put(MessageKey.HELP_COMMAND_FORMAT, "<yellow>/<command> <sub> <gray>- <description></gray>");
            messages.put(MessageKey.INVALID_ARGUMENT, "<red>Invalid argument '<arg>' for parameter '<param>'.</red>");
            messages.put(MessageKey.MIN_VALUE, "<red>Parameter '<param>' must be at least <min>.</red>");
            messages.put(MessageKey.MAX_VALUE, "<red>Parameter '<param>' must be at most <max>.</red>");
            messages.put(MessageKey.DID_YOU_MEAN, "<red>Command not found. Did you mean /<command> <suggestion>?</red>");
            messages.put(MessageKey.COOLDOWN_ACTIVE, "<red>You must wait <time> seconds before using this again.</red>");
            messages.put(MessageKey.CONFIRMATION_REQUIRED, "<red>Are you sure? Run the exact same command again within <time> seconds to confirm.</red>");
        }

        public String getRawMessage(MessageKey key) {
            return messages.get(key);
        }

        @Override
        public void sendMessage(CommandSender sender, MessageKey key, Map<String, String> placeholders) {
            String message = messages.getOrDefault(key, "Message not found: " + key);
            
            TagResolver.Builder resolverBuilder = TagResolver.builder();
            if (placeholders != null) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    resolverBuilder.resolver(Placeholder.parsed(entry.getKey(), entry.getValue()));
                }
            }
            
            sender.sendMessage(MiniMessage.miniMessage().deserialize(message, resolverBuilder.build()));
        }

        public void setMessage(MessageKey key, String message) {
            messages.put(key, message);
        }
    }
}
