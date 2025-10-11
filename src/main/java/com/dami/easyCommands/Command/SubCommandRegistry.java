package com.dami.easyCommands.Command;

import com.dami.easyCommands.Annotations.SubCommandClass;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for managing subcommand classes.
 * This class handles registration and execution of class-based subcommands.
 */
public class SubCommandRegistry {

    private final Map<String, SubCommandHandler> registeredHandlers = new HashMap<>();
    private final Map<String, Class<? extends SubCommandHandler>> handlerClasses = new HashMap<>();

    /**
     * Register a subcommand class.
     * 
     * @param handlerClass The class to register
     * @param path The command path for this handler
     * @param name The command name
     * @param permission The permission required
     * @param weight The weight/priority
     * @param maxArgs The maximum number of arguments
     */
    public void registerSubCommandClass(Class<? extends SubCommandHandler> handlerClass, 
                                      String[] path, String name, String permission, 
                                      int weight, int maxArgs) {
        
        String fullPath = String.join(".", path) + "." + name;
        Bukkit.getLogger().info("Registering subcommand class: " + fullPath);
        
        handlerClasses.put(fullPath, handlerClass);
    }

    /**
     * Register a subcommand class using annotation information.
     * 
     * @param handlerClass The class to register
     */
    public void registerSubCommandClass(Class<? extends SubCommandHandler> handlerClass) {
        if (!handlerClass.isAnnotationPresent(SubCommandClass.class)) {
            throw new IllegalArgumentException("Class must be annotated with @SubCommandClass");
        }

        SubCommandClass annotation = handlerClass.getAnnotation(SubCommandClass.class);
        registerSubCommandClass(handlerClass, annotation.commandPath(), annotation.name(), 
                               annotation.permission(), annotation.weight(), annotation.maxArgs());
    }

    /**
     * Execute a subcommand.
     * 
     * @param path The command path
     * @param sender The command sender
     * @param args The command arguments
     * @return true if the command was executed successfully, false otherwise
     */
    public boolean executeSubCommand(String[] path, CommandSender sender, String[] args) {
        String fullPath = String.join(".", path);
        
        // Try to get existing handler instance
        SubCommandHandler handler = registeredHandlers.get(fullPath);
        
        if (handler == null) {
            // Try to create new instance from registered class
            Class<? extends SubCommandHandler> handlerClass = handlerClasses.get(fullPath);
            if (handlerClass != null) {
                try {
                    Constructor<? extends SubCommandHandler> constructor = handlerClass.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    handler = constructor.newInstance();
                    registeredHandlers.put(fullPath, handler);
                } catch (Exception e) {
                    Bukkit.getLogger().severe("Failed to create subcommand handler: " + e.getMessage());
                    return false;
                }
            }
        }

        if (handler == null) {
            return false;
        }

        // Check permission
        if (!handler.hasPermission(sender)) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return false;
        }

        // Check argument count
        if (args.length > handler.getMaxArgs()) {
            sender.sendMessage("§cToo many arguments. Maximum: " + handler.getMaxArgs());
            return false;
        }

        // Execute the command
        try {
            handler.execute(sender, args);
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().severe("Error executing subcommand: " + e.getMessage());
            sender.sendMessage("§cAn error occurred while executing the command.");
            return false;
        }
    }

    /**
     * Get tab completion for a subcommand.
     * 
     * @param path The command path
     * @param sender The command sender
     * @param args The command arguments
     * @return List of tab completion suggestions, or null for no suggestions
     */
    public List<String> getTabComplete(String[] path, CommandSender sender, String[] args) {
        String fullPath = String.join(".", path);
        
        SubCommandHandler handler = registeredHandlers.get(fullPath);
        if (handler == null) {
            return null;
        }

        // Check permission
        if (!handler.hasPermission(sender)) {
            return null;
        }

        try {
            return handler.getTabComplete(sender, args);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Error getting tab completion: " + e.getMessage());
            return null;
        }
    }

    /**
     * Check if a subcommand is registered.
     * 
     * @param path The command path
     * @return true if the subcommand is registered, false otherwise
     */
    public boolean isSubCommandRegistered(String[] path) {
        String fullPath = String.join(".", path);
        return registeredHandlers.containsKey(fullPath) || handlerClasses.containsKey(fullPath);
    }

    /**
     * Get all registered subcommand paths.
     * 
     * @return Set of all registered subcommand paths
     */
    public java.util.Set<String> getRegisteredPaths() {
        return handlerClasses.keySet();
    }

    /**
     * Clear all registered subcommands.
     */
    public void clear() {
        registeredHandlers.clear();
        handlerClasses.clear();
    }
}
