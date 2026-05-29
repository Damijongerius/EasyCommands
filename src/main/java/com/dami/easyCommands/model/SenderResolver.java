package com.dami.easyCommands.model;

import org.bukkit.command.CommandSender;

/**
 * Resolves a CommandSender into a custom object.
 */
@FunctionalInterface
public interface SenderResolver<T> {
    T resolve(CommandSender sender) throws ValidationException;
}
