package com.dami.easyCommands.Command;

import com.dami.easyCommands.Annotations.SubCommand;
import com.dami.easyCommands.Annotations.SubCommandTab;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.System.arraycopy;

public abstract class BaseCommand implements TabExecutor, ICommand {

    /**
     * The plugin instance
     */
    protected final Plugin plugin;

    //this hashmap contains all the subcommands wit the possible executes
    protected final HashMap<String, CommandNode> root = new HashMap<>();

    protected BaseCommand(Plugin plugin) {
        this.plugin = plugin;

        //check if the name is set
        if(getName() == null){
            throw new IllegalArgumentException("Command name cannot be null");
        }

        // get the SubCommand Methods in the class
        collectSubCommands(this);

        // get the TabComplete Methods in the class
        collectTabCompleteMethods(this);

        if(plugin == null){
            System.out.println("Plugin is null for command: " + getName());
            System.out.println("This class can now only be used for unit testing otherwise not");
            return;
        }

        PluginCommand pluginCommand = plugin.getServer().getPluginCommand(getName());

        if (pluginCommand == null) {
            throw new IllegalArgumentException("No command found for name: " + getName() + " in plugin.yml");
        }

        //set the executor
        pluginCommand.setExecutor(this);

    }

    protected abstract String getName();

    protected void collectSubCommands(ICommand commandClass) {
        //get all methods in the class
        for (Method method : commandClass.getClass().getDeclaredMethods()) {
            //the method must have the SubCommand annotation
            if (method.isAnnotationPresent(SubCommand.class)) {

                SubCommand sub = method.getAnnotation(SubCommand.class);

                //the path of the subcommand is now being computed an array to give it the position it needs to have in the hash
                String[] path = Stream.concat(
                        Arrays.stream(sub.commandPath()),
                        Stream.of(sub.name())
                ).toArray(String[]::new);

                insertCommand(path, method, sub, commandClass);
            }
        }
    }

    protected void collectTabCompleteMethods(ICommand commandClass) {
        //get all methods in the class
        for (Method method : commandClass.getClass().getDeclaredMethods()) {
            //the method must have the SubCommandTab annotation
            if (method.isAnnotationPresent(SubCommandTab.class)) {

                SubCommandTab tab = method.getAnnotation(SubCommandTab.class);

                //the path of the tab complete is now being computed an array to give it the position it needs to have in the hash
                String[] path = Stream.concat(
                        Arrays.stream(tab.commandPath()),
                        Stream.of(tab.name())
                ).toArray(String[]::new);

                insertTabComplete(path, method, tab, commandClass);
            }
        }
    }

    protected void insertCommand(String[] path,Method method, SubCommand sub, Object owner){

        //get the command node
        CommandNode commandNode = root.computeIfAbsent(path[0], k -> new CommandNode());

        SubCommandInfo command = new SubCommandInfo(method, owner, sub.weight(), sub.permission(), sub.maxArgs());

        String[] newPath = new String[path.length - 1];
        arraycopy(path, 1, newPath, 0, path.length - 1);

        commandNode.insertCommand(newPath, command);
    }

    protected void insertTabComplete(String[] path, Method method, SubCommandTab tab, Object owner) {

        //get the command node
        CommandNode commandNode = root.computeIfAbsent(path[0], k -> new CommandNode());

        TabCompleteInfo tabComplete = new TabCompleteInfo(method, owner, tab.permission(), tab.priority());

        String[] newPath = new String[path.length - 1];
        arraycopy(path, 1, newPath, 0, path.length - 1);

        commandNode.insertTabComplete(newPath, tabComplete);
    }

    protected void insertTabComplete(String[] path, Method method, Object owner, String tabPermission, int tabPriority) {

        //get the command node
        CommandNode commandNode = root.computeIfAbsent(path[0], k -> new CommandNode());

        TabCompleteInfo tabComplete = new TabCompleteInfo(method, owner, tabPermission, tabPriority);

        String[] newPath = new String[path.length - 1];
        arraycopy(path, 1, newPath, 0, path.length - 1);

        commandNode.insertTabComplete(newPath, tabComplete);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String arg, @NotNull String[] args) {
        //check if the command is empty

        if(args.length <= maxArgs()){
            mainCommand(sender,args);
            return true;
        }

        if(args.length == 0){
            sender.sendMessage("Please provide a command:" + root.keySet());
            return false;
        }

        //get the command node
        CommandNode commandNode = root.get(args[0]);

        if(commandNode == null){
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

        if(strings.length == 1) {
            List<String> tabComplete = new ArrayList<>();
            tabComplete.addAll(root.keySet());
            tabComplete.addAll(tabComplete(commandSender, strings));

            return tabComplete;
        }

        //get the command node
        CommandNode commandNode = root.get(strings[0]);

        //check if the command node is empty
        if(commandNode == null){
            return null;
        }

        //get the tab complete
        String[] newPath = new String[strings.length - 1];
        arraycopy(strings, 1, newPath, 0, strings.length - 1);

        return commandNode.getTabComplete(newPath, commandSender);
    }

    public String ConvertToObject(){
        Map<String,Object> result = new HashMap<>();

        root.forEach((s, commandNode) -> {
            result.put(s, commandNode.ConvertToObject());
        });

        Yaml yaml = new Yaml();

        return yaml.dump(result);
    }
}
