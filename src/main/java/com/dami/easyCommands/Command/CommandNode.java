package com.dami.easyCommands.Command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandNode {
    public Map<String, CommandNode> nodes = new HashMap<>();

    private SubCommandInfo subCommandInfo;
    private TabCompleteInfo tabCompleteInfo;

    public void insertCommand(String[] path, SubCommandInfo command){

        if(path.length == 0){
            this.subCommandInfo = command;
            return;
        }

        String node = path[0];
        CommandNode commandNode = nodes.computeIfAbsent(node, k -> new CommandNode());

        String[] newPath = new String[path.length - 1];
        System.arraycopy(path, 1, newPath, 0, path.length - 1);
        commandNode.insertCommand(newPath, command);
    }

    public void runSubCommand(String[] path, CommandSender sender){

        if(subCommandInfo != null && path.length <= subCommandInfo.getMaxArgs()){
            subCommandInfo.run(sender,path);
            return;
        }

        String node = path[0];
        CommandNode commandNode = nodes.get(node);

        if(commandNode != null){
            String[] newPath = new String[path.length - 1];
            System.arraycopy(path, 1, newPath, 0, path.length - 1);
            commandNode.runSubCommand(newPath, sender);
        } else {
            sender.sendMessage("No command found");
        }
    }

    public void insertTabComplete(String[] path, TabCompleteInfo tabComplete) {

        if (path.length == 0) {
            this.tabCompleteInfo = tabComplete;
            return;
        }

        String node = path[0];
        CommandNode commandNode = nodes.computeIfAbsent(node, k -> new CommandNode());

        String[] newPath = new String[path.length - 1];
        System.arraycopy(path, 1, newPath, 0, path.length - 1);
        commandNode.insertTabComplete(newPath, tabComplete);
    }

    public List<String> getTabComplete(String[] path, CommandSender sender) {
        System.out.println("Getting tab complete: " + String.join(" ", path));

        if (path.length == 1) {
            if (tabCompleteInfo != null) {
                return tabCompleteInfo.getTabComplete(sender, path);
            }
            return new ArrayList<>(nodes.keySet());
        }

        String node = path[0];
        CommandNode commandNode = nodes.get(node);

        if (commandNode != null) {
            String[] newPath = new String[path.length - 1];
            System.arraycopy(path, 1, newPath, 0, path.length - 1);
            return commandNode.getTabComplete(newPath, sender);
        }

        return null;
    }

    public void setSubCommand(SubCommandInfo subCommandInfo) {
        this.subCommandInfo = subCommandInfo;
    }

    public void setTabComplete(TabCompleteInfo tabCompleteInfo) {
        this.tabCompleteInfo = tabCompleteInfo;
    }

    public boolean hasSubCommand() {
        return subCommandInfo != null;
    }

    public boolean hasTabComplete() {
        return tabCompleteInfo != null;
    }

    public int getSubCommandWeight() {
        return subCommandInfo != null ? subCommandInfo.getWeight() : 0;
    }

    public int getTabCompletePriority() {
        return tabCompleteInfo != null ? tabCompleteInfo.getPriority() : 0;
    }

    public Map<String,Object> ConvertToObject(){
        Map<String,Object> result = new HashMap<>();

        if(hasSubCommand()){
            Map<String,Object> subCommandMap = new HashMap<>();
            subCommandMap.put("weight", subCommandInfo.getWeight());
            subCommandMap.put("permission", subCommandInfo.getPermission());
            subCommandMap.put("maxArgs", subCommandInfo.getMaxArgs());
            result.put("subCommand", subCommandMap);
        }

        if(hasTabComplete()){
            Map<String,Object> tabCompleteMap = new HashMap<>();
            tabCompleteMap.put("priority", tabCompleteInfo.getPriority());
            tabCompleteMap.put("permission", tabCompleteInfo.getPermission());
            tabCompleteMap.put("output", tabCompleteInfo.getTabComplete(null,new String[]{}));
            result.put("tabComplete", tabCompleteMap);
        }

        Map<String,Object> children = new HashMap<>();
        for(Map.Entry<String, CommandNode> entry : nodes.entrySet()){
            children.put(entry.getKey(), entry.getValue().ConvertToObject());
        }

        if(!children.isEmpty()){
            result.put("children", children);
        }

        return result;
    }

}
