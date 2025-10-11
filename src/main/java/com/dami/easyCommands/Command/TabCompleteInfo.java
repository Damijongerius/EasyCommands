package com.dami.easyCommands.Command;

import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Information about a tab completion method.
 * This class stores the method, owner, permission, and priority for tab completion.
 */
public class TabCompleteInfo {

    private final Method method;
    private final Object owner;

    @Getter
    private final String permission;

    @Getter
    private final int priority;

    public TabCompleteInfo(Method method, Object owner, String permission, int priority) {
        this.method = method;
        this.owner = owner;
        this.permission = permission;
        this.priority = priority;
    }

    /**
     * Execute the tab completion method and return the results.
     * 
     * @param sender The command sender
     * @param args The command arguments
     * @return List of tab completion suggestions
     */
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        // Check permission if specified
        if (!permission.isEmpty() && !sender.hasPermission(permission)) {
            return null;
        }

        try {
            // Invoke the tab completion method
            Object result = method.invoke(owner, sender, args);
            
            if (result instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> completions = (List<String>) result;
                return completions;
            }
            
            return null;
        } catch (Exception e) {
            // Log error but don't crash
            System.err.println("Error executing tab completion method: " + e.getMessage());
            return null;
        }
    }
}
