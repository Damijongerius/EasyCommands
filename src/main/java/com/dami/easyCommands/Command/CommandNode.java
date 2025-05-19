package com.dami.easyCommands.Command;

import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandNode {
    public Map<String, CommandNode> nodes = new HashMap<>();

    private SubCommandInfo subCommandInfo;

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

        if(path.length == 0 || subCommandInfo != null && subCommandInfo.getExpectedValues() == path.length){
            if(subCommandInfo != null){
                subCommandInfo.run(sender, path);
            } else {
                sender.sendMessage("No command found");
            }
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

    public void setSubCommand(SubCommandInfo subCommandInfo){
        this.subCommandInfo = subCommandInfo;
    }

    public boolean hasSubCommand(){
        return subCommandInfo != null;
    }

    public int getSubCommandWeight(){
        return subCommandInfo.getWeight();
    }
}
