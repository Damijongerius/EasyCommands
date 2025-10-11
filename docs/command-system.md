# Command System Documentation

This document provides comprehensive information about the EasyCommands command system, including annotations, methods, and advanced features.

## Table of Contents

- [Core Concepts](#core-concepts)
- [Command Class Structure](#command-class-structure)
- [SubCommand Annotation](#subcommand-annotation)
- [Command Execution Flow](#command-execution-flow)
- [Advanced Features](#advanced-features)
- [Best Practices](#best-practices)

## Core Concepts

### Command Hierarchy

EasyCommands uses a hierarchical command structure:

```
Main Command
├── Subcommand 1
│   ├── Nested Subcommand 1.1
│   └── Nested Subcommand 1.2
├── Subcommand 2
└── Subcommand 3
```

### Command Paths

Commands are identified by their path:

- **Main Command**: `[]` (empty path)
- **Subcommand**: `["help"]`
- **Nested Subcommand**: `["admin", "kick"]`

## Command Class Structure

### Required Implementation

Every command class must extend `Command` and implement:

```java
public class MyCommand extends Command {
    
    public MyCommand(Plugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "mycommand";
    }
    
    @Override
    public int maxArgs() {
        return 1;
    }
    
    @Override
    public void MainCommand(CommandSender sender, String[] args) {
        // Main command logic
    }
}
```

### Method Signatures

#### getName()
- **Returns**: `String` - The command name
- **Purpose**: Defines the command name for registration
- **Example**: `return "mycommand";`

#### maxArgs()
- **Returns**: `int` - Maximum number of arguments for the main command
- **Purpose**: Limits argument count for the main command
- **Example**: `return 1;` (allows 0-1 arguments)

#### MainCommand(CommandSender sender, String[] args)
- **Parameters**: 
  - `sender`: The command sender
  - `args`: Command arguments
- **Purpose**: Executes the main command logic
- **Example**: Handle main command execution

## SubCommand Annotation

### Basic Usage

```java
@SubCommand(
    commandPath = {},
    name = "help",
    maxArgs = 0,
    permission = ""
)
public void helpCommand(CommandSender sender, String[] args) {
    sender.sendMessage("§eHelp command executed!");
}
```

### Annotation Parameters

#### commandPath
- **Type**: `String[]`
- **Purpose**: Defines the path to the command
- **Examples**:
  - `{}` - Root level command
  - `{"admin"}` - Nested under "admin"
  - `{"admin", "moderation"}` - Deeply nested

#### name
- **Type**: `String`
- **Purpose**: The command name
- **Example**: `"help"`, `"kick"`, `"reload"`

#### maxArgs
- **Type**: `int`
- **Purpose**: Maximum number of arguments
- **Default**: `0`
- **Example**: `1` (allows 0-1 arguments)

#### permission
- **Type**: `String`
- **Purpose**: Required permission to use the command
- **Default**: `""` (no permission required)
- **Example**: `"myplugin.admin"`

#### weight
- **Type**: `int`
- **Purpose**: Command priority (higher = more priority)
- **Default**: `0`
- **Example**: `10` (high priority)

### Nested Commands

```java
// Root level command
@SubCommand(
    commandPath = {},
    name = "admin",
    maxArgs = 0,
    permission = "myplugin.admin"
)
public void adminCommand(CommandSender sender, String[] args) {
    sender.sendMessage("§eAdmin commands available");
}

// Nested under admin
@SubCommand(
    commandPath = {"admin"},
    name = "kick",
    maxArgs = 2,
    permission = "myplugin.admin.kick"
)
public void adminKickCommand(CommandSender sender, String[] args) {
    if (args.length < 2) {
        sender.sendMessage("§cUsage: /mycommand admin kick <player> <reason>");
        return;
    }
    
    String playerName = args[0];
    String reason = args[1];
    sender.sendMessage("§cKicking " + playerName + " for: " + reason);
}
```

## Command Execution Flow

### 1. Command Registration

When a command class is instantiated:

1. **Validation**: Check if command name is set
2. **Plugin Command**: Get the command from plugin.yml
3. **Executor Setup**: Set the command executor
4. **Subcommand Collection**: Scan for `@SubCommand` methods
5. **Tab Completion Collection**: Scan for `@SubCommandTab` methods

### 2. Command Execution

When a command is executed:

1. **Argument Check**: Validate argument count against `maxArgs()`
2. **Main Command**: Execute `MainCommand()` if within limits
3. **Subcommand Lookup**: Find matching subcommand
4. **Permission Check**: Verify sender has required permission
5. **Method Execution**: Invoke the subcommand method

### 3. Tab Completion

When tab completion is requested:

1. **Path Resolution**: Determine the command path
2. **Tab Completion Lookup**: Find matching tab completion method
3. **Permission Check**: Verify sender has required permission
4. **Suggestion Generation**: Return tab completion suggestions

## Advanced Features

### Weight System

Commands can have different priorities:

```java
@SubCommand(
    commandPath = {},
    name = "help",
    weight = 10  // High priority
)
public void helpCommand(CommandSender sender, String[] args) {
    // This will have higher priority
}

@SubCommand(
    commandPath = {},
    name = "help",
    weight = 5   // Lower priority
)
public void helpCommand2(CommandSender sender, String[] args) {
    // This will have lower priority
}
```

### Permission System

```java
@SubCommand(
    commandPath = {},
    name = "admin",
    permission = "myplugin.admin"
)
public void adminCommand(CommandSender sender, String[] args) {
    // Only players with "myplugin.admin" permission can use this
}

@SubCommand(
    commandPath = {"admin"},
    name = "kick",
    permission = "myplugin.admin.kick"
)
public void adminKickCommand(CommandSender sender, String[] args) {
    // Requires "myplugin.admin.kick" permission
}
```

### Error Handling

```java
@SubCommand(
    commandPath = {},
    name = "teleport",
    maxArgs = 1,
    permission = "myplugin.teleport"
)
public void teleportCommand(CommandSender sender, String[] args) {
    if (args.length == 0) {
        sender.sendMessage("§cUsage: /mycommand teleport <player>");
        return;
    }
    
    String playerName = args[0];
    
    // Validate player exists
    if (!isValidPlayer(playerName)) {
        sender.sendMessage("§cPlayer not found: " + playerName);
        return;
    }
    
    // Execute teleport logic
    sender.sendMessage("§aTeleporting to: " + playerName);
}
```

## Best Practices

### 1. Command Organization

```java
public class MyPluginCommand extends Command {
    
    // Main command
    @Override
    public void MainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§aMyPlugin main command!");
    }
    
    // Help command
    @SubCommand(commandPath = {}, name = "help", maxArgs = 0, permission = "")
    public void helpCommand(CommandSender sender, String[] args) {
        // Help logic
    }
    
    // Admin commands
    @SubCommand(commandPath = {}, name = "admin", maxArgs = 0, permission = "myplugin.admin")
    public void adminCommand(CommandSender sender, String[] args) {
        // Admin logic
    }
    
    // Nested admin commands
    @SubCommand(commandPath = {"admin"}, name = "kick", maxArgs = 2, permission = "myplugin.admin.kick")
    public void adminKickCommand(CommandSender sender, String[] args) {
        // Kick logic
    }
}
```

### 2. Permission Structure

Organize permissions hierarchically:

```
myplugin.*                    # All permissions
myplugin.admin.*             # All admin permissions
myplugin.admin.kick          # Specific admin permission
myplugin.player.*            # All player permissions
myplugin.player.info         # Specific player permission
```

### 3. Error Messages

Use consistent error message formatting:

```java
// Good
sender.sendMessage("§cUsage: /mycommand <subcommand>");
sender.sendMessage("§cPlayer not found: " + playerName);
sender.sendMessage("§cYou don't have permission to use this command.");

// Bad
sender.sendMessage("Error: Invalid usage");
sender.sendMessage("Player " + playerName + " not found");
```

### 4. Argument Validation

Always validate arguments:

```java
@SubCommand(
    commandPath = {},
    name = "kick",
    maxArgs = 2,
    permission = "myplugin.kick"
)
public void kickCommand(CommandSender sender, String[] args) {
    if (args.length < 2) {
        sender.sendMessage("§cUsage: /mycommand kick <player> <reason>");
        return;
    }
    
    String playerName = args[0];
    String reason = args[1];
    
    // Validate player exists
    if (!isValidPlayer(playerName)) {
        sender.sendMessage("§cPlayer not found: " + playerName);
        return;
    }
    
    // Execute kick logic
    sender.sendMessage("§cKicking " + playerName + " for: " + reason);
}
```

### 5. Help Commands

Provide comprehensive help:

```java
@SubCommand(
    commandPath = {},
    name = "help",
    maxArgs = 0,
    permission = ""
)
public void helpCommand(CommandSender sender, String[] args) {
    sender.sendMessage("§e=== MyPlugin Commands ===");
    sender.sendMessage("§7/myplugin - Main command");
    sender.sendMessage("§7/myplugin help - Show this help");
    
    if (sender.hasPermission("myplugin.admin")) {
        sender.sendMessage("§7/myplugin admin - Admin commands");
    }
    
    if (sender.hasPermission("myplugin.player")) {
        sender.sendMessage("§7/myplugin player - Player commands");
    }
}
```

## Troubleshooting

### Common Issues

1. **Command not found**: Check plugin.yml and command name
2. **Permission denied**: Verify permission strings
3. **Arguments not working**: Check maxArgs() method
4. **Tab completion not working**: Ensure method returns List<String>

### Debug Tips

1. **Enable debug logging**: Check console for command registration messages
2. **Test permissions**: Use `/lp user <player> permission set <permission> true`
3. **Validate arguments**: Add debug output for argument validation
4. **Check method signatures**: Ensure all required methods are implemented

---

**Next**: Learn about [Tab Completion](tab-completion.md) to enhance your commands! 🚀
