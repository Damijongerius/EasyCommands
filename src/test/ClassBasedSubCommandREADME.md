# Class-Based Subcommand System

This document explains how to use the new class-based subcommand system in your EasyCommands framework.

## Overview

The class-based subcommand system allows you to create modular command structures by registering entire classes as subcommands. This is perfect for organizing complex command systems where different classes handle different parts of your command tree.

## Features

- **Modular Design**: Each subcommand can be its own class
- **Automatic Registration**: Classes are automatically registered when annotated
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
    permission = "mycommand.admin",
    weight = 10,
    maxArgs = 2
)
public class AdminSubCommand extends SubCommandHandler {

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Command execution logic
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
                    sender.sendMessage("§cUsage: /mycommand admin kick <player>");
                } else {
                    String playerName = args[1];
                    sender.sendMessage("§cKicking " + playerName + "...");
                }
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
        return sender.hasPermission("mycommand.admin");
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
    
    // ... rest of your command implementation
}
```

## Annotation Parameters

### @SubCommandClass

- `commandPath`: The path to the command (same format as `@SubCommand`)
- `name`: The command name for this subcommand class
- `permission`: Permission required to use this subcommand (optional)
- `weight`: Priority for this subcommand (optional, default 0)
- `maxArgs`: Maximum number of arguments (optional, default 0)

## SubCommandHandler Methods

### Required Methods

- `execute(CommandSender sender, String[] args)`: Main command execution logic

### Optional Methods

- `getTabComplete(CommandSender sender, String[] args)`: Tab completion suggestions
- `hasPermission(CommandSender sender)`: Custom permission checking
- `getMaxArgs()`: Custom argument limits
- `getWeight()`: Custom priority/weight

## Examples

### Simple Subcommand Class

```java
@SubCommandClass(
    commandPath = {},
    name = "help",
    permission = "",
    weight = 0,
    maxArgs = 0
)
public class HelpSubCommand extends SubCommandHandler {

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("§e=== Help ===");
        sender.sendMessage("§7/mycommand help - Show this help");
        sender.sendMessage("§7/mycommand admin - Admin commands");
        sender.sendMessage("§7/mycommand player - Player commands");
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        return Arrays.asList("general", "commands", "permissions");
    }
}
```

### Nested Subcommand Class

```java
@SubCommandClass(
    commandPath = {"admin"},
    name = "moderation",
    permission = "mycommand.admin.moderation",
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
                    sender.sendMessage("§cUsage: /mycommand admin moderation warn <player> <reason>");
                } else {
                    String playerName = args[1];
                    String reason = args[2];
                    sender.sendMessage("§cWarning " + playerName + " for: " + reason);
                }
                break;
            case "mute":
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /mycommand admin moderation mute <player> <duration>");
                } else {
                    String playerName = args[1];
                    String duration = args[2];
                    sender.sendMessage("§cMuting " + playerName + " for: " + duration);
                }
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
        return sender.hasPermission("mycommand.admin.moderation");
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
            new String[]{"custom"}, "command", "mycommand.custom", 5, 2);
    }
}
```

## Integration with Existing System

The class-based subcommand system integrates seamlessly with your existing command framework:

1. **Same Path System**: Uses the same `commandPath` and `name` system
2. **Automatic Discovery**: Classes are automatically discovered and registered
3. **Permission Integration**: Respects the same permission system
4. **Tab Completion**: Works with the existing tab completion system
5. **Command Node Integration**: Uses the same `CommandNode` structure

## Best Practices

1. **Keep Classes Focused**: Each subcommand class should handle a specific set of related commands
2. **Use Descriptive Names**: Choose clear, descriptive names for your subcommand classes
3. **Handle Errors Gracefully**: Always check argument counts and provide helpful error messages
4. **Use Permissions**: Implement proper permission checking for security
5. **Provide Tab Completion**: Always provide tab completion for better user experience
6. **Document Your Commands**: Include help text and usage examples

## Testing

You can test class-based subcommands using the provided test classes:

```java
@Test
public void testSubCommandClass() {
    // Test subcommand class logic
    String[] args = {"reload"};
    String subCommand = args[0];
    assertEquals("reload", subCommand);
}
```

## File Structure

- `SubCommandClass.java` - The annotation definition
- `SubCommandHandler.java` - Base class for subcommand handlers
- `SubCommandRegistry.java` - Registry for managing subcommand classes
- `Command.java` - Updated to support class-based subcommands
- `AdminSubCommand.java` - Example admin subcommand class
- `PlayerSubCommand.java` - Example player subcommand class
- `ModerationSubCommand.java` - Example moderation subcommand class
- `MainCommandExample.java` - Example main command class
- `ClassBasedSubCommandTest.java` - Test cases for the system

## Notes

- Subcommand classes are automatically discovered and registered
- The system respects permissions and only executes commands for authorized users
- Multiple subcommand classes can exist for the same command path
- The system uses the same command node structure as regular commands
- Class-based subcommands work alongside method-based subcommands
- Tab completion is automatically handled for class-based subcommands
