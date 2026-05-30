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

    public SubCommandInfo getSubCommandInfo() {
        return subCommandInfo;
    }

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

    public boolean runSubCommand(String[] path, CommandSender sender, List<String> wildcards, MessageHandler messageHandler, String baseCommandName, String fullPath, com.dami.easyCommands.core.BaseCommand parentCommand){
        // Help check
        if (path.length > 0 && path[0].equalsIgnoreCase("help")) {
            int page = 1;
            if (path.length > 1) {
                try {
                    page = Integer.parseInt(path[1]);
                } catch (NumberFormatException ignored) {}
            }
            showHelp(sender, messageHandler, baseCommandName, "", page);
            return true;
        }

        // Priority 1: Child node matching (exact or wildcard)
        if (path.length > 0) {
            String node = path[0];
            CommandNode commandNode = nodes.get(node);
            
            if(commandNode != null){
                String[] newPath = new String[path.length - 1];
                System.arraycopy(path, 1, newPath, 0, path.length - 1);
                if (commandNode.runSubCommand(newPath, sender, new ArrayList<>(wildcards), messageHandler, baseCommandName, fullPath + " " + node, parentCommand)) {
                    return true;
                }
            } else {
                commandNode = nodes.get("*");
                if (commandNode != null) {
                    List<String> capturedWildcards = new ArrayList<>(wildcards);
                    capturedWildcards.add(node);
                    String[] newPath = new String[path.length - 1];
                    System.arraycopy(path, 1, newPath, 0, path.length - 1);
                    if (commandNode.runSubCommand(newPath, sender, capturedWildcards, messageHandler, baseCommandName, fullPath + " " + node, parentCommand)) {
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
            subCommandInfo.run(sender, path, wildcards, messageHandler, baseCommandName, fullPath);
            return true;
        }

        // Check if we executed a directory subcommand (path is empty, no leaf command, but has children)
        if (path.length == 0 && !nodes.isEmpty() && sender instanceof org.bukkit.entity.Player) {
            if (parentCommand instanceof com.dami.easyCommands.core.ShardableCommand) {
                com.dami.easyCommands.core.ShardableCommand shardable = (com.dami.easyCommands.core.ShardableCommand) parentCommand;
                if (shardable.isAutoGuiEnabled()) {
                    org.bukkit.entity.Player player = (org.bukkit.entity.Player) sender;
                    String title = shardable.getAutoGuiTitle() != null ? shardable.getAutoGuiTitle() : shardable.getName() + " Commands";
                    shardable.openCommandNodeGui(player, fullPath, title);
                    return true;
                }
            }
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

    public static class HelpEntry {
        public String path;
        public String description;
        public String usage;

        public HelpEntry(String path, String description, String usage) {
            this.path = path;
            this.description = description;
            this.usage = usage;
        }
    }

    public void collectHelp(CommandSender sender, String currentPath, List<HelpEntry> entries) {
        if (subCommandInfo != null) {
            String permission = subCommandInfo.getPermission();
            if (permission.isEmpty() || sender.hasPermission(permission)) {
                entries.add(new HelpEntry(
                    currentPath,
                    subCommandInfo.getDescription(),
                    subCommandInfo.getUsage().isEmpty() ? currentPath : subCommandInfo.getUsage()
                ));
            }
        }
        for (Map.Entry<String, CommandNode> entry : nodes.entrySet()) {
            if (entry.getValue().isAccessible(sender)) {
                String nextPath = currentPath.isEmpty() ? entry.getKey() : currentPath + " " + entry.getKey();
                entry.getValue().collectHelp(sender, nextPath, entries);
            }
        }
    }

    public void showHelp(CommandSender sender, MessageHandler messageHandler, String baseCommandName, String currentPath, int page) {
        List<HelpEntry> entries = new ArrayList<>();
        collectHelp(sender, currentPath, entries);

        int itemsPerPage = 7;
        int maxPage = (int) Math.ceil(entries.size() / (double) itemsPerPage);
        if (maxPage == 0) maxPage = 1;
        
        if (page < 1) page = 1;
        if (page > maxPage) page = maxPage;

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("command", baseCommandName);
        
        messageHandler.sendMessage(sender, MessageKey.HELP_HEADER, placeholders);

        int start = (page - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, entries.size());

        for (int i = start; i < end; i++) {
            HelpEntry entry = entries.get(i);
            Map<String, String> cmdPlaceholders = new HashMap<>(placeholders);
            cmdPlaceholders.put("sub", entry.path);
            cmdPlaceholders.put("description", entry.description);
            cmdPlaceholders.put("usage", entry.usage);
            messageHandler.sendMessage(sender, MessageKey.HELP_COMMAND_FORMAT, cmdPlaceholders);
        }

        placeholders.put("page", String.valueOf(page));
        placeholders.put("max_page", String.valueOf(maxPage));
        placeholders.put("next_page", String.valueOf(page < maxPage ? page + 1 : maxPage));
        placeholders.put("prev_page", String.valueOf(page > 1 ? page - 1 : 1));
        
        messageHandler.sendMessage(sender, MessageKey.HELP_FOOTER, placeholders);
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
