package com.dami.easyCommands.core;

import com.dami.easyCommands.annotations.SubCommand;
import com.dami.easyCommands.annotations.SubCommandTab;
import com.dami.easyCommands.internal.CommandNode;
import com.dami.easyCommands.internal.TabCompleteInfo;
import com.dami.easyCommands.internal.SubCommandInfo;
import com.dami.easyCommands.model.MessageKey;
import com.dami.easyCommands.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.System.arraycopy;

public abstract class BaseCommand implements TabExecutor, ICommand {

    protected final Map<String, CommandNode> root = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    @Getter @Setter
    protected String description = "";

    @Getter @Setter
    protected MessageHandler messageHandler = new MessageHandler.DefaultMessageHandler();

    protected BaseCommand() {
        // Automatically initialize if name is already available (from subclass)
        // Note: For some subclasses, they might need to call initialize manually 
        // if getName() depends on fields initialized after super().
        try {
            if (getName() != null) {
                initialize();
            }
        } catch (Exception ignored) {
            // If getName() throws or is null, we wait for manual initialize()
        }
    }

    public void initialize() {
        if(getName() == null){
            throw new IllegalArgumentException("Command name cannot be null");
        }
        root.clear();
        collectSubCommands(this);
        collectTabCompleteMethods(this);
    }

    @Override
    public void mainCommand(CommandSender sender, String[] args) {
        showHelp(sender);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    public abstract String getName();

    protected void collectSubCommands(ICommand commandClass) {
        for (Method method : commandClass.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(SubCommand.class)) {
                SubCommand sub = method.getAnnotation(SubCommand.class);
                String[] path = Stream.concat(
                        Arrays.stream(sub.commandPath()),
                        Stream.of(sub.name())
                ).filter(s -> s != null && !s.isEmpty()).toArray(String[]::new);

                if (path.length > 0) {
                    insertCommand(path, method, sub, commandClass);
                }
            }
        }
    }

    protected void collectTabCompleteMethods(ICommand commandClass) {
        for (Method method : commandClass.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(SubCommandTab.class)) {
                SubCommandTab tab = method.getAnnotation(SubCommandTab.class);
                String[] path = Stream.concat(
                        Arrays.stream(tab.commandPath()),
                        Stream.of(tab.name())
                ).filter(s -> s != null && !s.isEmpty()).toArray(String[]::new);

                if (path.length > 0) {
                    insertTabComplete(path, method, tab, commandClass);
                }
            }
        }
    }

    protected void insertCommand(String[] path,Method method, SubCommand sub, Object owner){
        CommandNode commandNode = root.computeIfAbsent(path[0], k -> new CommandNode());
        SubCommandInfo command = new SubCommandInfo(method, owner, sub.weight(), sub.permission(), sub.maxArgs(), 
                sub.aliases(), sub.description(), sub.usage(), sub.senderType(), sub.completions());
        String[] newPath = new String[path.length - 1];
        arraycopy(path, 1, newPath, 0, path.length - 1);
        commandNode.insertCommand(newPath, command);
        for (String alias : sub.aliases()) {
            String[] aliasPath = new String[path.length];
            arraycopy(path, 0, aliasPath, 0, path.length - 1);
            aliasPath[path.length - 1] = alias;
            CommandNode aliasCommandNode = root.computeIfAbsent(aliasPath[0], k -> new CommandNode());
            String[] newAliasPath = new String[aliasPath.length - 1];
            arraycopy(aliasPath, 1, newAliasPath, 0, aliasPath.length - 1);
            aliasCommandNode.insertCommand(newAliasPath, command);
        }
    }

    protected void insertTabComplete(String[] path, Method method, SubCommandTab tab, Object owner) {
        CommandNode commandNode = root.computeIfAbsent(path[0], k -> new CommandNode());
        TabCompleteInfo tabComplete = new TabCompleteInfo(method, owner, tab.permission(), tab.priority());
        String[] newPath = new String[path.length - 1];
        arraycopy(path, 1, newPath, 0, path.length - 1);
        commandNode.insertTabComplete(newPath, tabComplete);
    }

    protected void insertTabComplete(String[] path, Method method, Object owner, String tabPermission, int tabPriority) {
        CommandNode commandNode = root.computeIfAbsent(path[0], k -> new CommandNode());
        TabCompleteInfo tabComplete = new TabCompleteInfo(method, owner, tabPermission, tabPriority);
        String[] newPath = new String[path.length - 1];
        arraycopy(path, 1, newPath, 0, path.length - 1);
        commandNode.insertTabComplete(newPath, tabComplete);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String arg, @NotNull String[] args) {
        if (args.length > 0) {
            String firstArg = args[0];
            if (firstArg.equalsIgnoreCase("help")) {
                int page = 1;
                if (args.length > 1) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {}
                }
                showHelp(sender, page);
                return true;
            }
            CommandNode commandNode = root.get(firstArg);
            List<String> wildcards = new ArrayList<>();
            if (commandNode != null) {
                String[] newArgs = new String[args.length - 1];
                arraycopy(args, 1, newArgs, 0, args.length - 1);
                if (commandNode.runSubCommand(newArgs, sender, new ArrayList<>(wildcards), messageHandler, getName(), firstArg)) {
                    return true;
                }
            } else {
                commandNode = root.get("*");
                if (commandNode != null) {
                    List<String> capturedWildcards = new ArrayList<>(wildcards);
                    capturedWildcards.add(firstArg);
                    String[] newArgs = new String[args.length - 1];
                    arraycopy(args, 1, newArgs, 0, args.length - 1);
                    if (commandNode.runSubCommand(newArgs, sender, capturedWildcards, messageHandler, getName(), firstArg)) {
                        return true;
                    }
                } else {
                    String suggestion = findSuggestion(firstArg, root.keySet());
                    if (suggestion != null) {
                        showDidYouMean(sender, suggestion);
                        return true;
                    }
                }
            }
        }
        if (args.length == 0) {
            mainCommand(sender,args);
            return true;
        }
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("command", getName());
        messageHandler.sendMessage(sender, MessageKey.COMMAND_NOT_FOUND, placeholders);
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length == 1) {
            List<String> tabComplete = new ArrayList<>();
            for (Map.Entry<String, CommandNode> entry : root.entrySet()) {
                if (entry.getKey().equals("*")) continue;
                if (entry.getValue().isAccessible(commandSender)) {
                    tabComplete.add(entry.getKey());
                }
            }
            tabComplete.addAll(tabComplete(commandSender, strings));
            return tabComplete;
        }
        String firstArg = strings[0];
        CommandNode commandNode = root.get(firstArg);
        List<String> wildcards = new ArrayList<>();
        if(commandNode != null){
            String[] newPath = new String[strings.length - 1];
            arraycopy(strings, 1, newPath, 0, strings.length - 1);
            return commandNode.getTabComplete(newPath, commandSender, new ArrayList<>(wildcards));
        }
        commandNode = root.get("*");
        if (commandNode != null) {
            wildcards.add(firstArg);
            String[] newPath = new String[strings.length - 1];
            arraycopy(strings, 1, newPath, 0, strings.length - 1);
            return commandNode.getTabComplete(newPath, commandSender, wildcards);
        }
        return null;
    }

    public void showHelp(CommandSender sender) {
        showHelp(sender, 1);
    }

    public void showHelp(CommandSender sender, int page) {
        List<CommandNode.HelpEntry> entries = new ArrayList<>();
        for (Map.Entry<String, CommandNode> entry : root.entrySet()) {
            if (entry.getKey().equals("*")) continue;
            if (entry.getValue().isAccessible(sender)) {
                entry.getValue().collectHelp(sender, entry.getKey(), entries);
            }
        }
        
        int itemsPerPage = 7;
        int maxPage = (int) Math.ceil(entries.size() / (double) itemsPerPage);
        if (maxPage == 0) maxPage = 1;
        
        if (page < 1) page = 1;
        if (page > maxPage) page = maxPage;

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("command", getName());
        
        messageHandler.sendMessage(sender, MessageKey.HELP_HEADER, placeholders);

        int start = (page - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, entries.size());

        for (int i = start; i < end; i++) {
            CommandNode.HelpEntry entry = entries.get(i);
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

    protected String findSuggestion(String input, Set<String> options) {
        String bestMatch = null;
        int bestDistance = Integer.MAX_VALUE;
        for (String option : options) {
            if (option.equals("*")) continue;
            int distance = StringUtil.levenshteinDistance(input, option);
            if (distance < bestDistance && distance <= 2) {
                bestDistance = distance;
                bestMatch = option;
            }
        }
        return bestMatch;
    }

    protected void showDidYouMean(CommandSender sender, String suggestion) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("command", getName());
        placeholders.put("suggestion", suggestion);
        messageHandler.sendMessage(sender, MessageKey.DID_YOU_MEAN, placeholders);
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
