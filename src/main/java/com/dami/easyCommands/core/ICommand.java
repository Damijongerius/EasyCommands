package com.dami.easyCommands.core;

import org.bukkit.command.CommandSender;
import java.util.List;

public interface ICommand {
    String getName();
    default String getDescription() { return ""; }
    default void mainCommand(CommandSender sender, String[] args) {}
    default List<String> tabComplete(CommandSender sender, String[] args) { return null; }
}
