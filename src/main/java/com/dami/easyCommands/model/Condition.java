package com.dami.easyCommands.model;

import org.bukkit.command.CommandSender;

/**
 * A condition that must be met before a command executes.
 */
@FunctionalInterface
public interface Condition {
    /**
     * Checks if the condition is met.
     * @param sender The command sender.
     * @throws ValidationException If the condition is not met, a ValidationException should be thrown.
     */
    void check(CommandSender sender) throws ValidationException;
}
