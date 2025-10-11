package com.dami.easyCommands.Command;

import com.dami.easyCommands.Annotations.SubCommand;
import com.dami.easyCommands.Annotations.SubCommandTab;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.System.arraycopy;

public abstract class Command implements TabExecutor, ICommand {

    /**
     * The plugin instance
     */
    protected final Plugin plugin;

    //this hashmap contains all the subcommands wit the possible executes
    protected final HashMap<String, CommandNode> root = new HashMap<>();
    
    //registry for class-based subcommands
    protected final SubCommandRegistry subCommandRegistry = new SubCommandRegistry();

    protected Command(Plugin plugin) {
        this.plugin = plugin;

        //check if the name is set
        if(getName() == null){
            throw new IllegalArgumentException("Command name cannot be null");
        }

        PluginCommand pluginCommand = plugin.getServer().getPluginCommand(getName());

        if (pluginCommand == null) {
            throw new IllegalArgumentException("No command found for name: " + getName() + " in plugin.yml");
        }

        //set the executor
        pluginCommand.setExecutor(this);

        // get the SubCommand Methods in the class
        collectSubCommands();
        
        // get the TabComplete Methods in the class
        collectTabCompleteMethods();
    }

    private void collectSubCommands(){
        //get all methods in the class
        for (Method method : this.getClass().getDeclaredMethods()) {
            //the method must have the SubCommand annotation
            if (method.isAnnotationPresent(SubCommand.class)) {

                SubCommand sub = method.getAnnotation(SubCommand.class);

                //the path of the subcommand is now being computed an array to give it the position it needs to have in the hash
                String[] path = java.util.stream.Stream.concat(
                        java.util.Arrays.stream(sub.commandPath()),
                        java.util.stream.Stream.of(sub.name())
                ).toArray(String[]::new);

                insertCommand(path, method, sub, this);
            }
        }

        root.forEach((s, commandNode) -> {
            //check if the command node has a subcommand
            if(commandNode.hasSubCommand()){
                Bukkit.getLogger().info("Command: " + s + " has subcommand: " + commandNode.hasSubCommand());
            }
        });

    }

    private void collectTabCompleteMethods() {
        //get all methods in the class
        for (Method method : this.getClass().getDeclaredMethods()) {
            //the method must have the SubCommandTab annotation
            if (method.isAnnotationPresent(SubCommandTab.class)) {

                SubCommandTab tab = method.getAnnotation(SubCommandTab.class);

                //the path of the tab complete is now being computed an array to give it the position it needs to have in the hash
                String[] path = java.util.stream.Stream.concat(
                        java.util.Arrays.stream(tab.commandPath()),
                        java.util.stream.Stream.of(tab.name())
                ).toArray(String[]::new);

                insertTabComplete(path, method, tab, this);
            }
        }

        root.forEach((s, commandNode) -> {
            //check if the command node has tab complete
            if(commandNode.hasTabComplete()){
                Bukkit.getLogger().info("Command: " + s + " has tab complete: " + commandNode.hasTabComplete());
            }
        });
    }

    private void insertCommand(String[] path,Method method, SubCommand sub, Object owner){

        String fullPath = String.join(".", path);
        Bukkit.getLogger().info("Inserting command: " + fullPath);

        //get the command node
        CommandNode commandNode = root.computeIfAbsent(path[0], k -> new CommandNode());

        SubCommandInfo command = new SubCommandInfo(method, owner, sub.weight(), sub.permission(), sub.maxArgs());

        String[] newPath = new String[path.length - 1];
        arraycopy(path, 1, newPath, 0, path.length - 1);

        commandNode.insertCommand(newPath, command);
    }

    private void insertTabComplete(String[] path, Method method, SubCommandTab tab, Object owner) {

        String fullPath = String.join(".", path);
        Bukkit.getLogger().info("Inserting tab complete: " + fullPath);

        //get the command node
        CommandNode commandNode = root.computeIfAbsent(path[0], k -> new CommandNode());

        TabCompleteInfo tabComplete = new TabCompleteInfo(method, owner, tab.permission(), tab.priority());

        String[] newPath = new String[path.length - 1];
        arraycopy(path, 1, newPath, 0, path.length - 1);

        commandNode.insertTabComplete(newPath, tabComplete);
    }

    public abstract void MainCommand(CommandSender sender, String[] args);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String arg, @NotNull String[] args) {
        //check if the command is empty

        if(args.length <= maxArgs()){
            MainCommand(sender,args);
            return true;
        }

        if(args.length == 0){
            sender.sendMessage("Please provide a command:" + root.keySet());
            return false;
        }

        //get the command node
        CommandNode commandNode = root.get(args[0]);

        //check if the command node is empty
        if(commandNode == null){
            // Check if it's a class-based subcommand
            String[] path = new String[args.length];
            System.arraycopy(args, 0, path, 0, args.length);
            
            if (subCommandRegistry.isSubCommandRegistered(path)) {
                String[] subArgs = new String[args.length - 1];
                arraycopy(args, 1, subArgs, 0, args.length - 1);
                return subCommandRegistry.executeSubCommand(path, sender, subArgs);
            }
            
            sender.sendMessage("Command not found");
            return false;
        }

        String[] newPath = new String[args.length - 1];
        arraycopy(args, 1, newPath, 0, args.length - 1);
        commandNode.runSubCommand(newPath, sender);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        //check if the command is empty

        if(strings.length == 1){
            return new ArrayList<>(root.keySet());
        }

        //get the command node
        CommandNode commandNode = root.get(strings[0]);

        //check if the command node is empty
        if(commandNode == null){
            // Check if it's a class-based subcommand
            String[] path = new String[strings.length];
            System.arraycopy(strings, 0, path, 0, strings.length);
            
            if (subCommandRegistry.isSubCommandRegistered(path)) {
                String[] subArgs = new String[strings.length - 1];
                arraycopy(strings, 1, subArgs, 0, strings.length - 1);
                return subCommandRegistry.getTabComplete(path, commandSender, subArgs);
            }
            
            return null;
        }

        //get the tab complete
        String[] newPath = new String[strings.length - 1];
        arraycopy(strings, 1, newPath, 0, strings.length - 1);
        List<String> tabComplete = commandNode.getTabComplete(newPath, commandSender);

        //check if the tab complete is empty
        if(tabComplete == null){
            return null;
        }

        return tabComplete;
    }

    /**
     * Register a subcommand class.
     * 
     * @param handlerClass The class to register as a subcommand
     */
    public void registerSubCommandClass(Class<? extends SubCommandHandler> handlerClass) {
        subCommandRegistry.registerSubCommandClass(handlerClass);
    }

    /**
     * Register a subcommand class with custom parameters.
     * 
     * @param handlerClass The class to register
     * @param path The command path
     * @param name The command name
     * @param permission The permission required
     * @param weight The weight/priority
     * @param maxArgs The maximum number of arguments
     */
    public void registerSubCommandClass(Class<? extends SubCommandHandler> handlerClass, 
                                       String[] path, String name, String permission, 
                                       int weight, int maxArgs) {
        subCommandRegistry.registerSubCommandClass(handlerClass, path, name, permission, weight, maxArgs);
    }

    /**
     * Check if a subcommand class is registered.
     * 
     * @param path The command path
     * @return true if the subcommand is registered, false otherwise
     */
    public boolean isSubCommandClassRegistered(String[] path) {
        return subCommandRegistry.isSubCommandRegistered(path);
    }

    /**
     * Get the subcommand registry.
     * 
     * @return The subcommand registry
     */
    public SubCommandRegistry getSubCommandRegistry() {
        return subCommandRegistry;
    }
}
