package com.dami.easyCommands.model;

import org.bukkit.command.CommandSender;

/**
 * Handles exceptions thrown by command methods.
 */
@FunctionalInterface
public interface ExceptionHandler<T extends Throwable> {
    void handle(CommandSender sender, T exception);
}
