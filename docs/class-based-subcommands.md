# Class-Based Subcommands

This document explains how to use the class-based subcommand system in EasyCommands for creating modular and organized command structures.

## Table of Contents

- [Overview](#overview)
- [Basic Usage](#basic-usage)
- [Advanced Features](#advanced-features)
- [Best Practices](#best-practices)
- [Examples](#examples)

## Overview

The class-based subcommand system allows you to create modular command structures by registering entire classes as subcommands. This is perfect for organizing complex command systems where different classes handle different parts of your command tree.

### Key Features

- **Modular Design**: Each subcommand can be its own class
- **Automatic Registration**: Classes are automatically discovered and registered
- **Permission Support**: Each subcommand class can have its own permissions
- **Weight System**: Priority system for multiple subcommands
- **Tab Completion**: Built-in tab completion support
- **Flexible Arguments**: Custom argument limits per subcommand class

## Basic Usage

### 1. Create a Subcommand Class

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
            sender.sendMessage("§7/admin ban <player> - Ban a player");
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
            case "ban":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /myplugin admin ban <player>");
                } else {
                    String playerName = args[1];
                    sender.sendMessage("§cBanning " + playerName + "...");
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

### 2. Register the Subcommand Class

```java
public class MainCommand extends Command {
    
    public MainCommand(Plugin plugin) {
        super(plugin);
        
        // Register subcommand classes
        registerSubCommandClass(AdminSubCommand.class);
        registerSubCommandClass(PlayerSubCommand.class);
        registerSubCommandClass(ModerationSubCommand.class);
    }
    
    @Override
    public String getName() {
        return "myplugin";
    }
    
    @Override
    public int maxArgs() {
        return 1;
    }
    
    @Override
    public void MainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§aMyPlugin main command!");
    }
}
```

## Advanced Features

### Nested Subcommand Classes

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
            sender.sendMessage("§7/admin moderation unmute <player>");
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
            case "unmute":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /myplugin admin moderation unmute <player>");
                } else {
                    String playerName = args[1];
                    sender.sendMessage("§aUnmuting " + playerName);
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

### Custom Registration

```java
public class MainCommand extends Command {
    
    public MainCommand(Plugin plugin) {
        super(plugin);
        
        // Register with custom parameters
        registerSubCommandClass(CustomSubCommand.class, 
            new String[]{"custom"}, "command", "myplugin.custom", 5, 2);
    }
}
```

### Dynamic Subcommand Classes

```java
public class DynamicSubCommand extends SubCommandHandler {
    
    private final String commandName;
    private final String permission;
    private final int maxArgs;
    
    public DynamicSubCommand(String commandName, String permission, int maxArgs) {
        this.commandName = commandName;
        this.permission = permission;
        this.maxArgs = maxArgs;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("§aDynamic command executed: " + commandName);
    }
    
    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(permission);
    }
    
    @Override
    public int getMaxArgs() {
        return maxArgs;
    }
}

// Register dynamically
public class MainCommand extends Command {
    
    public MainCommand(Plugin plugin) {
        super(plugin);
        
        // Register dynamic subcommands
        registerSubCommandClass(DynamicSubCommand.class, 
            new String[]{}, "dynamic1", "myplugin.dynamic1", 5, 1);
        registerSubCommandClass(DynamicSubCommand.class, 
            new String[]{}, "dynamic2", "myplugin.dynamic2", 10, 2);
    }
}
```

## Best Practices

### 1. Organize by Functionality

```java
// Admin-related commands
@SubCommandClass(
    commandPath = {},
    name = "admin",
    permission = "myplugin.admin",
    weight = 10,
    maxArgs = 2
)
public class AdminSubCommand extends SubCommandHandler {
    // Admin command logic
}

// Player-related commands
@SubCommandClass(
    commandPath = {},
    name = "player",
    permission = "myplugin.player",
    weight = 5,
    maxArgs = 2
)
public class PlayerSubCommand extends SubCommandHandler {
    // Player command logic
}

// Moderation-related commands
@SubCommandClass(
    commandPath = {"admin"},
    name = "moderation",
    permission = "myplugin.admin.moderation",
    weight = 15,
    maxArgs = 3
)
public class ModerationSubCommand extends SubCommandHandler {
    // Moderation command logic
}
```

### 2. Use Descriptive Names

```java
// Good
@SubCommandClass(
    commandPath = {},
    name = "admin",
    permission = "myplugin.admin"
)
public class AdminSubCommand extends SubCommandHandler {
    // Admin commands
}

// Bad
@SubCommandClass(
    commandPath = {},
    name = "a",
    permission = "myplugin.a"
)
public class ASubCommand extends SubCommandHandler {
    // Unclear what this does
}
```

### 3. Handle Errors Gracefully

```java
@SubCommandClass(
    commandPath = {},
    name = "teleport",
    permission = "myplugin.teleport",
    maxArgs = 2
)
public class TeleportSubCommand extends SubCommandHandler {
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /myplugin teleport <player> [target]");
            return;
        }
        
        String playerName = args[0];
        String targetName = args.length > 1 ? args[1] : null;
        
        // Validate player exists
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage("§cPlayer not found: " + playerName);
            return;
        }
        
        // Validate target if specified
        if (targetName != null) {
            Player target = Bukkit.getPlayer(targetName);
            if (target == null) {
                sender.sendMessage("§cTarget player not found: " + targetName);
                return;
            }
            
            // Teleport player to target
            player.teleport(target);
            sender.sendMessage("§aTeleported " + playerName + " to " + targetName);
        } else {
            // Teleport sender to player
            if (sender instanceof Player) {
                ((Player) sender).teleport(player);
                sender.sendMessage("§aTeleported to " + playerName);
            } else {
                sender.sendMessage("§cConsole cannot teleport to players");
            }
        }
    }
    
    @Override
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        } else if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        }
        return null;
    }
}
```

### 4. Provide Comprehensive Help

```java
@SubCommandClass(
    commandPath = {},
    name = "help",
    permission = "",
    maxArgs = 1
)
public class HelpSubCommand extends SubCommandHandler {
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            showGeneralHelp(sender);
        } else {
            String topic = args[0];
            showTopicHelp(sender, topic);
        }
    }
    
    private void showGeneralHelp(CommandSender sender) {
        sender.sendMessage("§e=== MyPlugin Help ===");
        sender.sendMessage("§7/myplugin - Main command");
        sender.sendMessage("§7/myplugin help - Show this help");
        
        if (sender.hasPermission("myplugin.admin")) {
            sender.sendMessage("§7/myplugin admin - Admin commands");
        }
        
        if (sender.hasPermission("myplugin.player")) {
            sender.sendMessage("§7/myplugin player - Player commands");
        }
    }
    
    private void showTopicHelp(CommandSender sender, String topic) {
        switch (topic.toLowerCase()) {
            case "admin":
                if (sender.hasPermission("myplugin.admin")) {
                    sender.sendMessage("§e=== Admin Commands ===");
                    sender.sendMessage("§7/myplugin admin - Admin command list");
                    sender.sendMessage("§7/myplugin admin kick <player> - Kick a player");
                    sender.sendMessage("§7/myplugin admin ban <player> - Ban a player");
                } else {
                    sender.sendMessage("§cYou don't have permission to view admin help");
                }
                break;
            case "player":
                if (sender.hasPermission("myplugin.player")) {
                    sender.sendMessage("§e=== Player Commands ===");
                    sender.sendMessage("§7/myplugin player info <player> - Get player info");
                    sender.sendMessage("§7/myplugin player teleport <player> - Teleport to player");
                } else {
                    sender.sendMessage("§cYou don't have permission to view player help");
                }
                break;
            default:
                sender.sendMessage("§cUnknown help topic: " + topic);
                break;
        }
    }
    
    @Override
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> topics = new ArrayList<>();
            topics.add("general");
            
            if (sender.hasPermission("myplugin.admin")) {
                topics.add("admin");
            }
            
            if (sender.hasPermission("myplugin.player")) {
                topics.add("player");
            }
            
            return topics;
        }
        return null;
    }
}
```

## Examples

### Complete Example

```java
// Main command class
public class MyPluginCommand extends Command {
    
    public MyPluginCommand(Plugin plugin) {
        super(plugin);
        
        // Register subcommand classes
        registerSubCommandClass(AdminSubCommand.class);
        registerSubCommandClass(PlayerSubCommand.class);
        registerSubCommandClass(HelpSubCommand.class);
    }
    
    @Override
    public String getName() {
        return "myplugin";
    }
    
    @Override
    public int maxArgs() {
        return 1;
    }
    
    @Override
    public void MainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§aMyPlugin main command!");
        sender.sendMessage("§7Use /myplugin help for more information");
    }
}

// Admin subcommand class
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
        // Admin command logic
    }
    
    @Override
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        // Tab completion logic
        return null;
    }
}

// Player subcommand class
@SubCommandClass(
    commandPath = {},
    name = "player",
    permission = "myplugin.player",
    weight = 5,
    maxArgs = 2
)
public class PlayerSubCommand extends SubCommandHandler {
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        // Player command logic
    }
    
    @Override
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        // Tab completion logic
        return null;
    }
}
```

## Troubleshooting

### Common Issues

1. **Class not registered**: Ensure the class is annotated with `@SubCommandClass`
2. **Permission denied**: Check permission strings
3. **Arguments not working**: Verify `getMaxArgs()` method
4. **Tab completion not working**: Ensure method returns `List<String>`

### Debug Tips

1. **Enable debug logging**: Check console for registration messages
2. **Test permissions**: Use permission plugins to verify
3. **Validate arguments**: Add debug output for argument validation
4. **Check method signatures**: Ensure all required methods are implemented

---

**Next**: Learn about [Testing](testing.md) to ensure your commands work correctly! 🚀
