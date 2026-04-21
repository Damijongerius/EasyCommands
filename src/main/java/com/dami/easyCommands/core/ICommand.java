package com.dami.easyCommands.core;

import org.bukkit.command.CommandSender;
import java.util.List;

public interface ICommand {
    String getName();
    @Deprecated(since = "2.0.0", forRemoval = true)
    int maxArgs();
    default String getDescription() { return ""; }
    default void mainCommand(CommandSender sender, String[] args) {}
    default List<String> tabComplete(CommandSender sender, String[] args) { return null; }
    @Deprecated(since = "2.1.0", forRemoval = true)
    void showUsage(CommandSender sender);
}
