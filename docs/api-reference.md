# API Reference

This document provides a complete reference for all classes, methods, and annotations in the EasyCommands framework.

## Table of Contents

- [Annotations](#annotations)
- [Core Classes](#core-classes)
- [Command System](#command-system)
- [Tab Completion](#tab-completion)
- [Class-Based Subcommands](#class-based-subcommands)

## Annotations

### @SubCommand

Marks a method as a subcommand handler.

```java
@SubCommand(
    commandPath = {},
    name = "help",
    maxArgs = 0,
    permission = "",
    weight = 0
)
public void helpCommand(CommandSender sender, String[] args) {
    // Command logic
}
```

**Parameters:**
- `commandPath` (String[]): The path to the command
- `name` (String): The command name
- `maxArgs` (int): Maximum number of arguments (default: 0)
- `permission` (String): Required permission (default: "")
- `weight` (int): Command priority (default: 0)

### @SubCommandTab

Marks a method as a tab completion handler.

```java
@SubCommandTab(
    commandPath = {},
    name = "help",
    permission = "",
    priority = 0
)
public List<String> helpTabComplete(CommandSender sender, String[] args) {
    return Arrays.asList("general", "commands");
}
```

**Parameters:**
- `commandPath` (String[]): The path to the command
- `name` (String): The command name
- `permission` (String): Required permission (default: "")
- `priority` (int): Tab completion priority (default: 0)

### @SubCommandClass

Marks a class as a subcommand handler.

```java
@SubCommandClass(
    commandPath = {},
    name = "admin",
    permission = "myplugin.admin",
    weight = 10,
    maxArgs = 2
)
public class AdminSubCommand extends SubCommandHandler {
    // Implementation
}
```

**Parameters:**
- `commandPath` (String[]): The path to the command
- `name` (String): The command name
- `permission` (String): Required permission (default: "")
- `weight` (int): Command priority (default: 0)
- `maxArgs` (int): Maximum number of arguments (default: 0)

## Core Classes

### Command

Base class for all commands in the EasyCommands framework.

```java
public abstract class Command implements TabExecutor, ICommand {
    protected final Plugin plugin;
    protected final HashMap<String, CommandNode> root;
    protected final SubCommandRegistry subCommandRegistry;
    
    protected Command(Plugin plugin);
    
    // Abstract methods
    public abstract String getName();
    public abstract int maxArgs();
    public abstract void MainCommand(CommandSender sender, String[] args);
    
    // Command execution
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args);
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args);
    
    // Subcommand class registration
    public void registerSubCommandClass(Class<? extends SubCommandHandler> handlerClass);
    public void registerSubCommandClass(Class<? extends SubCommandHandler> handlerClass, 
                                       String[] path, String name, String permission, 
                                       int weight, int maxArgs);
    public boolean isSubCommandClassRegistered(String[] path);
    public SubCommandRegistry getSubCommandRegistry();
}
```

### ICommand

Interface that defines the basic contract for commands.

```java
public interface ICommand {
    String getName();
    int maxArgs();
}
```

### CommandNode

Represents a node in the command tree structure.

```java
public class CommandNode {
    public Map<String, CommandNode> nodes;
    private SubCommandInfo subCommandInfo;
    private TabCompleteInfo tabCompleteInfo;
    
    // Command management
    public void insertCommand(String[] path, SubCommandInfo command);
    public void runSubCommand(String[] path, CommandSender sender);
    
    // Tab completion management
    public void insertTabComplete(String[] path, TabCompleteInfo tabComplete);
    public List<String> getTabComplete(String[] path, CommandSender sender);
    
    // Getters and setters
    public void setSubCommand(SubCommandInfo subCommandInfo);
    public void setTabComplete(TabCompleteInfo tabCompleteInfo);
    public boolean hasSubCommand();
    public boolean hasTabComplete();
    public int getSubCommandWeight();
    public int getTabCompletePriority();
}
```

### SubCommandInfo

Contains information about a subcommand method.

```java
public class SubCommandInfo {
    private final Method method;
    private final Object owner;
    private final int weight;
    private final String permission;
    private final int maxArgs;
    
    public SubCommandInfo(Method method, Object owner, int weight, String permission, int maxArgs);
    public void run(CommandSender commandSender, String[] args);
    
    // Getters
    public int getWeight();
    public String getPermission();
    public int getMaxArgs();
}
```

### TabCompleteInfo

Contains information about a tab completion method.

```java
public class TabCompleteInfo {
    private final Method method;
    private final Object owner;
    private final String permission;
    private final int priority;
    
    public TabCompleteInfo(Method method, Object owner, String permission, int priority);
    public List<String> getTabComplete(CommandSender sender, String[] args);
    
    // Getters
    public String getPermission();
    public int getPriority();
}
```

## Command System

### SubCommandHandler

Base class for class-based subcommands.

```java
public abstract class SubCommandHandler {
    // Abstract methods
    public abstract void execute(CommandSender sender, String[] args);
    
    // Optional methods
    public List<String> getTabComplete(CommandSender sender, String[] args);
    public boolean hasPermission(CommandSender sender);
    public int getMaxArgs();
    public int getWeight();
}
```

### SubCommandRegistry

Registry for managing class-based subcommands.

```java
public class SubCommandRegistry {
    // Registration
    public void registerSubCommandClass(Class<? extends SubCommandHandler> handlerClass, 
                                       String[] path, String name, String permission, 
                                       int weight, int maxArgs);
    public void registerSubCommandClass(Class<? extends SubCommandHandler> handlerClass);
    
    // Execution
    public boolean executeSubCommand(String[] path, CommandSender sender, String[] args);
    public List<String> getTabComplete(String[] path, CommandSender sender, String[] args);
    
    // Management
    public boolean isSubCommandRegistered(String[] path);
    public Set<String> getRegisteredPaths();
    public void clear();
}
```

## Tab Completion

### Basic Tab Completion

```java
@SubCommandTab(
    commandPath = {},
    name = "help",
    permission = ""
)
public List<String> helpTabComplete(CommandSender sender, String[] args) {
    return Arrays.asList("general", "commands", "permissions");
}
```

### Context-Aware Tab Completion

```java
@SubCommandTab(
    commandPath = {},
    name = "kick",
    permission = "myplugin.kick"
)
public List<String> kickTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
        // First argument - suggest player names
        return Bukkit.getOnlinePlayers().stream()
            .map(Player::getName)
            .collect(Collectors.toList());
    } else if (args.length == 2) {
        // Second argument - suggest kick reasons
        return Arrays.asList("griefing", "spamming", "cheating", "inappropriate");
    }
    return null;
}
```

### Permission-Based Tab Completion

```java
@SubCommandTab(
    commandPath = {},
    name = "admin",
    permission = "myplugin.admin"
)
public List<String> adminTabComplete(CommandSender sender, String[] args) {
    List<String> suggestions = new ArrayList<>();
    
    // Basic admin commands
    suggestions.add("reload");
    suggestions.add("config");
    
    // Advanced admin commands for senior staff
    if (sender.hasPermission("myplugin.admin.advanced")) {
        suggestions.add("kick");
        suggestions.add("ban");
    }
    
    return suggestions;
}
```

## Class-Based Subcommands

### Basic Subcommand Class

```java
@SubCommandClass(
    commandPath = {},
    name = "admin",
    permission = "myplugin.admin",
    weight = 10,
    maxArgs = 2
)
public class AdminSubCommand extends SubCommandHandler {
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§e=== Admin Commands ===");
            sender.sendMessage("§7/admin reload - Reload configuration");
            sender.sendMessage("§7/admin kick <player> - Kick a player");
            return;
        }

        String subCommand = args[0];
        switch (subCommand.toLowerCase()) {
            case "reload":
                sender.sendMessage("§aReloading configuration...");
                break;
            case "kick":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /myplugin admin kick <player>");
                } else {
                    String playerName = args[1];
                    sender.sendMessage("§cKicking " + playerName + "...");
                }
                break;
            default:
                sender.sendMessage("§cUnknown admin command: " + subCommand);
                break;
        }
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "kick", "ban", "mute");
        } else if (args.length == 2) {
            return Arrays.asList("Player1", "Player2", "Player3");
        }
        return null;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("myplugin.admin");
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public int getWeight() {
        return 10;
    }
}
```

### Nested Subcommand Class

```java
@SubCommandClass(
    commandPath = {"admin"},
    name = "moderation",
    permission = "myplugin.admin.moderation",
    weight = 15,
    maxArgs = 3
)
public class ModerationSubCommand extends SubCommandHandler {
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§e=== Moderation Commands ===");
            sender.sendMessage("§7/admin moderation warn <player> <reason>");
            sender.sendMessage("§7/admin moderation mute <player> <duration>");
            return;
        }

        String subCommand = args[0];
        switch (subCommand.toLowerCase()) {
            case "warn":
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /myplugin admin moderation warn <player> <reason>");
                } else {
                    String playerName = args[1];
                    String reason = args[2];
                    sender.sendMessage("§cWarning " + playerName + " for: " + reason);
                }
                break;
            case "mute":
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /myplugin admin moderation mute <player> <duration>");
                } else {
                    String playerName = args[1];
                    String duration = args[2];
                    sender.sendMessage("§cMuting " + playerName + " for: " + duration);
                }
                break;
            default:
                sender.sendMessage("§cUnknown moderation command: " + subCommand);
                break;
        }
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("warn", "mute", "unmute");
        } else if (args.length == 2) {
            return Arrays.asList("Player1", "Player2", "Player3");
        } else if (args.length == 3) {
            if ("warn".equals(args[0])) {
                return Arrays.asList("griefing", "spamming", "inappropriate", "cheating");
            } else if ("mute".equals(args[0])) {
                return Arrays.asList("1m", "5m", "10m", "1h", "1d");
            }
        }
        return null;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("myplugin.admin.moderation");
    }

    @Override
    public int getMaxArgs() {
        return 3;
    }

    @Override
    public int getWeight() {
        return 15;
    }
}
```

### Registration

```java
public class MainCommand extends Command {
    
    public MainCommand(Plugin plugin) {
        super(plugin);
        
        // Register subcommand classes
        registerSubCommandClass(AdminSubCommand.class);
        registerSubCommandClass(PlayerSubCommand.class);
        registerSubCommandClass(ModerationSubCommand.class);
        
        // Register with custom parameters
        registerSubCommandClass(CustomSubCommand.class, 
            new String[]{"custom"}, "command", "myplugin.custom", 5, 2);
    }
}
```

## Error Handling

### Common Exceptions

- `IllegalArgumentException`: Thrown when command name is null or empty
- `IllegalStateException`: Thrown when command is not found in plugin.yml
- `SecurityException`: Thrown when permission is denied

### Error Handling Best Practices

```java
@SubCommand(
    commandPath = {},
    name = "teleport",
    maxArgs = 1,
    permission = "myplugin.teleport"
)
public void teleportCommand(CommandSender sender, String[] args) {
    if (args.length == 0) {
        sender.sendMessage("§cUsage: /myplugin teleport <player>");
        return;
    }
    
    String playerName = args[0];
    Player player = Bukkit.getPlayer(playerName);
    
    if (player == null) {
        sender.sendMessage("§cPlayer not found: " + playerName);
        return;
    }
    
    if (!(sender instanceof Player)) {
        sender.sendMessage("§cConsole cannot teleport to players");
        return;
    }
    
    try {
        ((Player) sender).teleport(player);
        sender.sendMessage("§aTeleported to " + playerName);
    } catch (Exception e) {
        sender.sendMessage("§cAn error occurred while teleporting");
        Bukkit.getLogger().severe("Teleport error: " + e.getMessage());
    }
}
```

## Performance Considerations

### Command Registration

- Commands are registered once during plugin initialization
- Subcommand methods are discovered using reflection
- Tab completion methods are cached for performance

### Memory Usage

- Command nodes are created as needed
- Subcommand classes are instantiated on first use
- Tab completion results are not cached by default

### Best Practices

1. **Limit subcommand depth**: Avoid deeply nested command structures
2. **Use appropriate permissions**: Don't over-permission commands
3. **Implement efficient tab completion**: Avoid expensive operations in tab completion
4. **Handle errors gracefully**: Always provide helpful error messages

---

**This API reference provides complete information about all aspects of the EasyCommands framework!** 🚀
