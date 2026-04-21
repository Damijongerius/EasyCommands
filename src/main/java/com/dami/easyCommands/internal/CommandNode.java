package com.dami.easyCommands.internal;

import com.dami.easyCommands.core.MessageHandler;
import com.dami.easyCommands.model.MessageKey;
import com.dami.easyCommands.util.StringUtil;
import org.bukkit.command.CommandSender;

import java.util.*;

public class CommandNode {
    public Map<String, CommandNode> nodes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

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

    public boolean runSubCommand(String[] path, CommandSender sender, List<String> wildcards, MessageHandler messageHandler, String baseCommandName, String fullPath){
        // Help check
        if (path.length > 0 && path[0].equalsIgnoreCase("help")) {
            showHelp(sender, messageHandler, baseCommandName, "");
            return true;
        }

        // Priority 1: Child node matching (exact or wildcard)
        if (path.length > 0) {
            String node = path[0];
            CommandNode commandNode = nodes.get(node);
            
            if(commandNode != null){
                String[] newPath = new String[path.length - 1];
                System.arraycopy(path, 1, newPath, 0, path.length - 1);
                if (commandNode.runSubCommand(newPath, sender, new ArrayList<>(wildcards), messageHandler, baseCommandName, fullPath + " " + node)) {
                    return true;
                }
            } else {
                commandNode = nodes.get("*");
                if (commandNode != null) {
                    List<String> capturedWildcards = new ArrayList<>(wildcards);
                    capturedWildcards.add(node);
                    String[] newPath = new String[path.length - 1];
                    System.arraycopy(path, 1, newPath, 0, path.length - 1);
                    if (commandNode.runSubCommand(newPath, sender, capturedWildcards, messageHandler, baseCommandName, fullPath + " " + node)) {
                        return true;
                    }
                } else {
                    // No match, try to find a suggestion
                    String suggestion = findSuggestion(node, sender);
                    if (suggestion != null) {
                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("command", baseCommandName);
                        placeholders.put("suggestion", fullPath + " " + suggestion);
                        messageHandler.sendMessage(sender, MessageKey.DID_YOU_MEAN, placeholders);
                        return true;
                    }
                }
            }
        }

        // Priority 2: Leaf match or Fallback match (this node has a command)
        if(subCommandInfo != null){
            subCommandInfo.run(sender, path, wildcards, messageHandler, baseCommandName);
            return true;
        }

        return false;
    }

    private String findSuggestion(String input, CommandSender sender) {
        String bestMatch = null;
        int bestDistance = Integer.MAX_VALUE;

        for (Map.Entry<String, CommandNode> entry : nodes.entrySet()) {
            String option = entry.getKey();
            if (option.equals("*")) continue;
            if (!entry.getValue().isAccessible(sender)) continue;

            int distance = StringUtil.levenshteinDistance(input, option);
            if (distance < bestDistance && distance <= 2) {
                bestDistance = distance;
                bestMatch = option;
            }
        }

        return bestMatch;
    }

    public void showHelp(CommandSender sender, MessageHandler messageHandler, String baseCommandName, String currentPath) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("command", baseCommandName);
        
        if (currentPath.isEmpty()) {
            messageHandler.sendMessage(sender, MessageKey.HELP_HEADER, placeholders);
        }

        if (subCommandInfo != null) {
            String permission = subCommandInfo.getPermission();
            if (permission.isEmpty() || sender.hasPermission(permission)) {
                Map<String, String> cmdPlaceholders = new HashMap<>(placeholders);
                cmdPlaceholders.put("sub", currentPath);
                cmdPlaceholders.put("description", subCommandInfo.getDescription());
                cmdPlaceholders.put("usage", subCommandInfo.getUsage().isEmpty() ? currentPath : subCommandInfo.getUsage());
                messageHandler.sendMessage(sender, MessageKey.HELP_COMMAND_FORMAT, cmdPlaceholders);
            }
        }

        for (Map.Entry<String, CommandNode> entry : nodes.entrySet()) {
            if (entry.getValue().isAccessible(sender)) {
                String nextPath = currentPath.isEmpty() ? entry.getKey() : currentPath + " " + entry.getKey();
                entry.getValue().showHelp(sender, messageHandler, baseCommandName, nextPath);
            }
        }

        if (currentPath.isEmpty()) {
            messageHandler.sendMessage(sender, MessageKey.HELP_FOOTER, placeholders);
        }
    }

    public boolean isAccessible(CommandSender sender) {
        if (subCommandInfo != null) {
            String permission = subCommandInfo.getPermission();
            if (permission.isEmpty() || sender.hasPermission(permission)) {
                return true;
            }
        }

        for (CommandNode child : nodes.values()) {
            if (child.isAccessible(sender)) {
                return true;
            }
        }
        return false;
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

    public List<String> getTabComplete(String[] path, CommandSender sender, List<String> wildcards) {
        if (path.length == 1) {
            Set<String> suggestions = new HashSet<>();
            for (Map.Entry<String, CommandNode> entry : nodes.entrySet()) {
                if (entry.getKey().equals("*")) continue;
                if (entry.getValue().isAccessible(sender)) {
                    suggestions.add(entry.getKey());
                }
            }
            
            if (tabCompleteInfo != null) {
                List<String> customSuggestions = tabCompleteInfo.getTabComplete(sender, path, wildcards);
                if (customSuggestions != null) {
                    suggestions.addAll(customSuggestions);
                }
            }

            if (subCommandInfo != null) {
                List<String> autoSuggestions = subCommandInfo.getTabComplete(sender, path, wildcards);
                if (autoSuggestions != null) {
                    suggestions.addAll(autoSuggestions);
                }
            }

            return new ArrayList<>(suggestions);
        }

        String node = path[0];
        CommandNode commandNode = nodes.get(node);

        if (commandNode != null) {
            String[] newPath = new String[path.length - 1];
            System.arraycopy(path, 1, newPath, 0, path.length - 1);
            return commandNode.getTabComplete(newPath, sender, new ArrayList<>(wildcards));
        }
        
        commandNode = nodes.get("*");
        if (commandNode != null) {
            List<String> capturedWildcards = new ArrayList<>(wildcards);
            capturedWildcards.add(node);
            String[] newPath = new String[path.length - 1];
            System.arraycopy(path, 1, newPath, 0, path.length - 1);
            return commandNode.getTabComplete(newPath, sender, capturedWildcards);
        }

        return null;
    }

    public Map<String,Object> ConvertToObject(){
        Map<String,Object> result = new HashMap<>();

        if(subCommandInfo != null){
            Map<String,Object> subCommandMap = new HashMap<>();
            subCommandMap.put("weight", subCommandInfo.getWeight());
            subCommandMap.put("permission", subCommandInfo.getPermission());
            subCommandMap.put("maxArgs", subCommandInfo.getMaxArgs());
            result.put("subCommand", subCommandMap);
        }

        if(tabCompleteInfo != null){
            Map<String,Object> tabCompleteMap = new HashMap<>();
            tabCompleteMap.put("priority", tabCompleteInfo.getPriority());
            tabCompleteMap.put("permission", tabCompleteInfo.getPermission());
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
