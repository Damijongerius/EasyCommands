# Tab Completion System

This document explains how to use the tab completion system in EasyCommands to provide intelligent command suggestions.

## Table of Contents

- [Overview](#overview)
- [Basic Tab Completion](#basic-tab-completion)
- [Advanced Tab Completion](#advanced-tab-completion)
- [Permission-Based Tab Completion](#permission-based-tab-completion)
- [Dynamic Tab Completion](#dynamic-tab-completion)
- [Best Practices](#best-practices)

## Overview

The tab completion system in EasyCommands allows you to provide intelligent command suggestions using the `@SubCommandTab` annotation. This enhances the user experience by showing available options as players type.

### Key Features

- **Automatic Registration**: Tab completion methods are automatically discovered
- **Permission Support**: Tab completion respects user permissions
- **Dynamic Suggestions**: Different suggestions based on context
- **Priority System**: Multiple tab completion methods can have different priorities
- **Nested Commands**: Support for complex command structures

## Basic Tab Completion

### Simple Tab Completion

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

### Method Requirements

Tab completion methods must:

- Be annotated with `@SubCommandTab`
- Return `List<String>` (the suggestions)
- Take `CommandSender sender` and `String[] args` as parameters
- Be public

### Annotation Parameters

#### commandPath
- **Type**: `String[]`
- **Purpose**: The path to the command (same as `@SubCommand`)
- **Example**: `{}` for root level, `{"admin"}` for nested

#### name
- **Type**: `String`
- **Purpose**: The command name for tab completion
- **Example**: `"help"`, `"kick"`, `"reload"`

#### permission
- **Type**: `String`
- **Purpose**: Permission required to see these suggestions
- **Default**: `""` (no permission required)
- **Example**: `"myplugin.admin"`

#### priority
- **Type**: `int`
- **Purpose**: Priority for this tab completion (higher = more priority)
- **Default**: `0`
- **Example**: `10` (high priority)

## Advanced Tab Completion

### Context-Aware Suggestions

```java
@SubCommandTab(
    commandPath = {},
    name = "kick",
    permission = "myplugin.kick"
)
public List<String> kickTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
        // First argument - suggest player names
        return getOnlinePlayers();
    } else if (args.length == 2) {
        // Second argument - suggest kick reasons
        return Arrays.asList("griefing", "spamming", "cheating", "inappropriate");
    }
    
    return null; // No more suggestions
}
```

### Nested Command Tab Completion

```java
// Tab completion for admin commands
@SubCommandTab(
    commandPath = {},
    name = "admin",
    permission = "myplugin.admin"
)
public List<String> adminTabComplete(CommandSender sender, String[] args) {
    return Arrays.asList("kick", "ban", "mute", "reload");
}

// Tab completion for admin kick command
@SubCommandTab(
    commandPath = {"admin"},
    name = "kick",
    permission = "myplugin.admin.kick"
)
public List<String> adminKickTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
        return getOnlinePlayers();
    } else if (args.length == 2) {
        return Arrays.asList("griefing", "spamming", "cheating");
    }
    return null;
}
```

### Multi-Argument Tab Completion

```java
@SubCommandTab(
    commandPath = {},
    name = "broadcast",
    permission = "myplugin.broadcast"
)
public List<String> broadcastTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
        // First argument - suggest common words
        return Arrays.asList("Hello", "Welcome", "Server", "Maintenance", "Update");
    } else if (args.length == 2) {
        // Second argument - suggest additional words
        return Arrays.asList("to", "all", "players", "now", "soon");
    } else if (args.length == 3) {
        // Third argument - suggest more words
        return Arrays.asList("server", "world", "everyone", "here");
    }
    
    return null; // No more suggestions
}
```

## Permission-Based Tab Completion

### Basic Permission Checking

```java
@SubCommandTab(
    commandPath = {},
    name = "admin",
    permission = "myplugin.admin"
)
public List<String> adminTabComplete(CommandSender sender, String[] args) {
    // Only players with "myplugin.admin" permission will see these suggestions
    return Arrays.asList("kick", "ban", "mute", "reload");
}
```

### Conditional Tab Completion

```java
@SubCommandTab(
    commandPath = {},
    name = "help",
    permission = ""
)
public List<String> helpTabComplete(CommandSender sender, String[] args) {
    List<String> suggestions = new ArrayList<>();
    
    // Basic help topics for everyone
    suggestions.add("general");
    suggestions.add("commands");
    
    // Admin help topics for admins
    if (sender.hasPermission("myplugin.admin")) {
        suggestions.add("admin");
        suggestions.add("permissions");
    }
    
    // Player help topics for players
    if (sender.hasPermission("myplugin.player")) {
        suggestions.add("player");
        suggestions.add("teleport");
    }
    
    return suggestions;
}
```

### Role-Based Suggestions

```java
@SubCommandTab(
    commandPath = {},
    name = "moderation",
    permission = "myplugin.moderation"
)
public List<String> moderationTabComplete(CommandSender sender, String[] args) {
    List<String> suggestions = new ArrayList<>();
    
    // Basic moderation commands
    suggestions.add("warn");
    suggestions.add("mute");
    
    // Advanced moderation commands for senior staff
    if (sender.hasPermission("myplugin.moderation.advanced")) {
        suggestions.add("ban");
        suggestions.add("kick");
    }
    
    // Super admin commands
    if (sender.hasPermission("myplugin.superadmin")) {
        suggestions.add("ipban");
        suggestions.add("unban");
    }
    
    return suggestions;
}
```

## Dynamic Tab Completion

### Player Name Suggestions

```java
@SubCommandTab(
    commandPath = {},
    name = "info",
    permission = "myplugin.info"
)
public List<String> infoTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
        // Get online players
        return Bukkit.getOnlinePlayers().stream()
            .map(Player::getName)
            .collect(Collectors.toList());
    }
    return null;
}
```

### World Name Suggestions

```java
@SubCommandTab(
    commandPath = {},
    name = "world",
    permission = "myplugin.world"
)
public List<String> worldTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
        // Get world names
        return Bukkit.getWorlds().stream()
            .map(World::getName)
            .collect(Collectors.toList());
    }
    return null;
}
```

### Configuration-Based Suggestions

```java
@SubCommandTab(
    commandPath = {},
    name = "config",
    permission = "myplugin.config"
)
public List<String> configTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
        // Get configuration sections
        return getConfig().getKeys(false).stream()
            .collect(Collectors.toList());
    } else if (args.length == 2) {
        // Get configuration keys for the selected section
        String section = args[0];
        return getConfig().getConfigurationSection(section).getKeys(false).stream()
            .collect(Collectors.toList());
    }
    return null;
}
```

### Time-Based Suggestions

```java
@SubCommandTab(
    commandPath = {},
    name = "schedule",
    permission = "myplugin.schedule"
)
public List<String> scheduleTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
        // Suggest time formats
        return Arrays.asList("1m", "5m", "10m", "1h", "1d", "1w");
    } else if (args.length == 2) {
        // Suggest actions
        return Arrays.asList("restart", "backup", "maintenance", "update");
    }
    return null;
}
```

## Best Practices

### 1. Keep Suggestions Relevant

```java
@SubCommandTab(
    commandPath = {},
    name = "teleport",
    permission = "myplugin.teleport"
)
public List<String> teleportTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
        // Only suggest players that are online and visible
        return Bukkit.getOnlinePlayers().stream()
            .filter(player -> !player.isHidden())
            .map(Player::getName)
            .collect(Collectors.toList());
    }
    return null;
}
```

### 2. Handle Edge Cases

```java
@SubCommandTab(
    commandPath = {},
    name = "kick",
    permission = "myplugin.kick"
)
public List<String> kickTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
        // Don't suggest the sender's own name
        return Bukkit.getOnlinePlayers().stream()
            .filter(player -> !player.equals(sender))
            .map(Player::getName)
            .collect(Collectors.toList());
    } else if (args.length == 2) {
        // Suggest kick reasons
        return Arrays.asList("griefing", "spamming", "cheating", "inappropriate");
    }
    return null;
}
```

### 3. Use Priority Wisely

```java
// High priority tab completion
@SubCommandTab(
    commandPath = {},
    name = "help",
    permission = "",
    priority = 10
)
public List<String> highPriorityHelpTabComplete(CommandSender sender, String[] args) {
    return Arrays.asList("general", "commands", "permissions");
}

// Lower priority tab completion
@SubCommandTab(
    commandPath = {},
    name = "help",
    permission = "",
    priority = 5
)
public List<String> lowPriorityHelpTabComplete(CommandSender sender, String[] args) {
    return Arrays.asList("advanced", "technical", "debug");
}
```

### 4. Provide Helpful Suggestions

```java
@SubCommandTab(
    commandPath = {},
    name = "permission",
    permission = "myplugin.permission"
)
public List<String> permissionTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
        return Arrays.asList("set", "remove", "check", "list");
    } else if (args.length == 2) {
        String action = args[0];
        if ("set".equals(action) || "remove".equals(action)) {
            // Suggest permission nodes
            return Arrays.asList(
                "myplugin.admin",
                "myplugin.player",
                "myplugin.moderation"
            );
        } else if ("check".equals(action)) {
            // Suggest player names
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        }
    }
    return null;
}
```

### 5. Filter Based on Context

```java
@SubCommandTab(
    commandPath = {},
    name = "admin",
    permission = "myplugin.admin"
)
public List<String> adminTabComplete(CommandSender sender, String[] args) {
    List<String> suggestions = new ArrayList<>();
    
    // Always available admin commands
    suggestions.add("reload");
    suggestions.add("config");
    
    // Player management commands
    if (sender.hasPermission("myplugin.admin.players")) {
        suggestions.add("kick");
        suggestions.add("ban");
        suggestions.add("mute");
    }
    
    // Server management commands
    if (sender.hasPermission("myplugin.admin.server")) {
        suggestions.add("restart");
        suggestions.add("backup");
        suggestions.add("maintenance");
    }
    
    return suggestions;
}
```

## Troubleshooting

### Common Issues

1. **Tab completion not working**: Ensure method returns `List<String>`
2. **Permission denied**: Check permission strings
3. **No suggestions**: Return `null` when no suggestions available
4. **Too many suggestions**: Limit suggestions to reasonable numbers

### Debug Tips

1. **Add logging**: Use `Bukkit.getLogger().info()` to debug
2. **Test permissions**: Verify permission strings
3. **Check method signatures**: Ensure correct parameters
4. **Validate suggestions**: Test with different argument counts

---

**Next**: Learn about [Class-Based Subcommands](class-based-subcommands.md) for modular command design! 🚀
