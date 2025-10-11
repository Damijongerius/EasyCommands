package com.dami.easyCommands.Command;

import org.bukkit.command.CommandSender;

/**
 * Base class for subcommand handlers.
 * Classes that want to be registered as subcommands should extend this class.
 */
public abstract class SubCommandHandler {

    /**
     * The main command method for this subcommand handler.
     * This is called when the subcommand is executed.
     * 
     * @param sender The command sender
     * @param args The command arguments
     */
    public abstract void execute(CommandSender sender, String[] args);

    /**
     * Get tab completion suggestions for this subcommand.
     * Override this method to provide custom tab completion.
     * 
     * @param sender The command sender
     * @param args The command arguments
     * @return List of tab completion suggestions, or null for no suggestions
     */
    public java.util.List<String> getTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    /**
     * Check if the sender has permission to use this subcommand.
     * Override this method to provide custom permission checking.
     * 
     * @param sender The command sender
     * @return true if the sender has permission, false otherwise
     */
    public boolean hasPermission(CommandSender sender) {
        return true;
    }

    /**
     * Get the maximum number of arguments for this subcommand.
     * Override this method to provide custom argument limits.
     * 
     * @return The maximum number of arguments
     */
    public int getMaxArgs() {
        return 0;
    }

    /**
     * Get the weight/priority of this subcommand.
     * Higher weights have higher priority.
     * 
     * @return The weight of this subcommand
     */
    public int getWeight() {
        return 0;
    }
}
