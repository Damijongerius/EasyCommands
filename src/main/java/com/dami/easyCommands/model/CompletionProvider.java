package com.dami.easyCommands.model;

import org.bukkit.command.CommandSender;
import java.util.List;

@FunctionalInterface
public interface CompletionProvider {
    List<String> getSuggestions(CommandSender sender, String[] args);
}
