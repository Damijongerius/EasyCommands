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

    public void runSubCommand(String[] path, CommandSender sender, String fullPath) {

        if (subCommandInfo != null && (nodes.isEmpty() || path.length <= subCommandInfo.getMaxArgs())) {
            subCommandInfo.run(sender, path);
            return;
        }

        if (path.length == 0) {
            showHelp(sender, fullPath);
            return;
        }

        String node = path[0];
        CommandNode commandNode = nodes.get(node);

        if (commandNode != null) {
            String[] newPath = new String[path.length - 1];
            System.arraycopy(path, 1, newPath, 0, path.length - 1);
            commandNode.runSubCommand(newPath, sender, fullPath + " " + node);
        } else {
            String closest = com.dami.easyCommands.Util.StringUtil.findClosestMatch(node, nodes.keySet());
            if (closest != null) {
                com.dami.easyCommands.Util.MessageUtil.sendMessage(sender, "<red>Unknown subcommand '<yellow>" + node + "</yellow>'. Did you mean '<yellow>" + closest + "</yellow>'?");
            } else {
                com.dami.easyCommands.Util.MessageUtil.sendMessage(sender, "<red>Unknown subcommand '<yellow>" + node + "</yellow>'.");
                showHelp(sender, fullPath);
            }
        }
    }

    public void showHelp(CommandSender sender, String currentPath) {
        if (nodes.isEmpty()) return;

        com.dami.easyCommands.Util.MessageUtil.sendMessage(sender, "<gray>Available subcommands for <gold>" + currentPath + "</gold>:");
        for (Map.Entry<String, CommandNode> entry : nodes.entrySet()) {
            CommandNode node = entry.getValue();
            if (node.subCommandInfo != null) {
                String perm = node.subCommandInfo.getPermission();
                if (!perm.isEmpty() && !sender.hasPermission(perm)) continue;

                String desc = node.subCommandInfo.getDescription();
                String usage = node.subCommandInfo.getUsage();

                String helpMsg = "<yellow> - " + entry.getKey() + "</yellow>";
                if (!desc.isEmpty()) helpMsg += " <gray>(" + desc + ")</gray>";
                if (!usage.isEmpty()) helpMsg += " <dark_gray>Usage: " + usage + "</dark_gray>";

                com.dami.easyCommands.Util.MessageUtil.sendMessage(sender, helpMsg);
            } else {
                // If it's just a branch node without its own command info
                com.dami.easyCommands.Util.MessageUtil.sendMessage(sender, "<yellow> - " + entry.getKey() + " ...</yellow>");
            }
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

    public String getSubCommandPermission() {
        return subCommandInfo != null ? subCommandInfo.getPermission() : "";
    }

    public String getSubCommandDescription() {
        return subCommandInfo != null ? subCommandInfo.getDescription() : "";
    }

    public String getSubCommandUsage() {
        return subCommandInfo != null ? subCommandInfo.getUsage() : "";
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
            try {
                tabCompleteMap.put("output", tabCompleteInfo.getTabComplete(null, new String[]{}));
            } catch (Exception e) {
                tabCompleteMap.put("output", "Error: " + e.getMessage());
            }
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
