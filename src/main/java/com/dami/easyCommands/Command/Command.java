package com.dami.easyCommands.Command;

import com.dami.easyCommands.Annotations.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
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
    protected final JavaPlugin plugin;

    //this hashmap contains all the subcommands wit the possible executes
    private final HashMap<String, CommandNode> root = new HashMap<>();

    protected Command(JavaPlugin plugin) {
        this.plugin = plugin;

        //check if the name is set
        if(getName() == null){
            throw new IllegalArgumentException("Command name cannot be null");
        }

        // get the SubCommand Methods in the class
        collectSubCommands();
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

    }

    private void insertCommand(String[] path,Method method, SubCommand sub, Object owner){

        //get the command node
        CommandNode commandNode = root.computeIfAbsent(path[0], k -> new CommandNode());

        SubCommandInfo command = new SubCommandInfo(method, owner, sub.weight(), sub.permission(), sub.expectedValues());

        if(sub.expectedValues() >= path.length){

            if(commandNode.hasSubCommand() && commandNode.getSubCommandWeight() > command.getWeight()){
                return;
            }

            commandNode.setSubCommand(command);
            return;
        }

        String[] newPath = new String[path.length - 1];
        arraycopy(path, 1, newPath, 0, path.length - 1);

        commandNode.insertCommand(newPath, command);
    }

    //the actual command name
    public abstract String getName();

    public abstract void MainCommand(CommandSender sender, String[] args);

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        //check if the command is empty
        if(strings.length == 0){
            MainCommand(commandSender, strings);
        }

        //get the command node
        CommandNode commandNode = root.get(strings[0]);

        //check if the command node is empty
        if(commandNode == null){
            return false;
        }

        //get the command node
        for (int i = 1; i < strings.length; i++) {
            commandNode = commandNode.nodes.get(strings[i]);
            if(commandNode == null){
                return false;
            }
        }

        commandNode.runSubCommand(strings, commandSender);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        //check if the command is empty
        if (strings.length == 0) {
            return null;
        }

        //get the command node
        CommandNode commandNode = root.get(strings[0]);

        //check if the command node is empty
        if (commandNode == null) {
            return null;
        }

        //traverse the command nodes
        for (int i = 1; i < strings.length; i++) {
            commandNode = commandNode.nodes.get(strings[i]);
            if (commandNode == null) {
                return null;
            }
        }

        //return the list of child node keys as suggestions
        return new ArrayList<>(commandNode.nodes.keySet());
    }
}
