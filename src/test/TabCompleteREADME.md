# Tab Completion System

This document explains how to use the new `@SubCommandTab` annotation for tab completion in your EasyCommands framework.

## Overview

The tab completion system allows you to provide intelligent tab completion suggestions for your commands using the `@SubCommandTab` annotation. This works alongside your existing `@SubCommand` annotations and uses the same command registration system.

## Features

- **Automatic Registration**: Tab completion methods are automatically registered when your command class is initialized
- **Permission Support**: Tab completion can be restricted based on permissions
- **Priority System**: Multiple tab completion methods can have different priorities
- **Nested Commands**: Supports tab completion for nested command paths (e.g., `admin reload`)
- **Dynamic Suggestions**: Tab completion methods can return different suggestions based on arguments

## Basic Usage

### 1. Create a Tab Completion Method

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

### 2. Method Signature Requirements

Tab completion methods must:
- Be annotated with `@SubCommandTab`
- Return `List<String>` (the suggestions)
- Take `CommandSender sender` and `String[] args` as parameters
- Be public

### 3. Annotation Parameters

- `commandPath`: The path to the command (same as `@SubCommand`)
- `name`: The command name for tab completion
- `permission`: Permission required to see these suggestions (optional)
- `priority`: Priority for this tab completion (optional, default 0)

## Examples

### Simple Tab Completion

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

@SubCommandTab(
    commandPath = {},
    name = "help",
    permission = ""
)
public List<String> helpTabComplete(CommandSender sender, String[] args) {
    return Arrays.asList("general", "commands", "permissions");
}
```

### Player Name Tab Completion

```java
@SubCommand(
    commandPath = {},
    name = "info",
    maxArgs = 1,
    permission = "mycommand.info"
)
public void infoCommand(CommandSender sender, String[] args) {
    // Command implementation
}

@SubCommandTab(
    commandPath = {},
    name = "info",
    permission = "mycommand.info"
)
public List<String> infoTabComplete(CommandSender sender, String[] args) {
    // In a real implementation, get online players
    return Arrays.asList("Player1", "Player2", "Player3");
}
```

### Nested Command Tab Completion

```java
@SubCommand(
    commandPath = {"admin"},
    name = "kick",
    maxArgs = 2,
    permission = "mycommand.admin.kick"
)
public void adminKickCommand(CommandSender sender, String[] args) {
    // Command implementation
}

@SubCommandTab(
    commandPath = {"admin"},
    name = "kick",
    permission = "mycommand.admin.kick"
)
public List<String> adminKickTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
        // First argument - player names
        return Arrays.asList("Player1", "Player2", "Player3");
    } else if (args.length == 2) {
        // Second argument - kick reasons
        return Arrays.asList("griefing", "spamming", "cheating");
    }
    
    return null; // No more suggestions
}
```

### Dynamic Tab Completion

```java
@SubCommandTab(
    commandPath = {},
    name = "broadcast",
    permission = "mycommand.broadcast"
)
public List<String> broadcastTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
        // First argument - suggest common words
        return Arrays.asList("Hello", "Welcome", "Server", "Maintenance");
    } else if (args.length == 2) {
        // Second argument - suggest additional words
        return Arrays.asList("to", "all", "players", "now");
    }
    
    return null; // No more suggestions
}
```

## Advanced Features

### Permission-Based Tab Completion

```java
@SubCommandTab(
    commandPath = {"admin"},
    name = "reload",
    permission = "mycommand.admin.reload"
)
public List<String> adminReloadTabComplete(CommandSender sender, String[] args) {
    // Only players with the permission will see these suggestions
    return Arrays.asList("config", "permissions", "all");
}
```

### Priority System

```java
@SubCommandTab(
    commandPath = {},
    name = "help",
    permission = "",
    priority = 10  // Higher priority
)
public List<String> highPriorityTabComplete(CommandSender sender, String[] args) {
    return Arrays.asList("high", "priority", "suggestions");
}

@SubCommandTab(
    commandPath = {},
    name = "help",
    permission = "",
    priority = 5   // Lower priority
)
public List<String> lowPriorityTabComplete(CommandSender sender, String[] args) {
    return Arrays.asList("low", "priority", "suggestions");
}
```

## Integration with Existing Commands

The tab completion system automatically integrates with your existing command structure:

1. **Same Path System**: Uses the same `commandPath` and `name` system as `@SubCommand`
2. **Automatic Registration**: Tab completion methods are registered when the command class is initialized
3. **Permission Integration**: Respects the same permission system as commands
4. **Command Node Integration**: Uses the same `CommandNode` structure for organization

## Testing Tab Completion

You can test tab completion functionality using the provided test classes:

```java
@Test
public void testTabCompletion() {
    // Test tab completion logic
    List<String> suggestions = Arrays.asList("help", "info", "broadcast");
    assertNotNull(suggestions);
    assertTrue(suggestions.contains("help"));
}
```

## Best Practices

1. **Keep Suggestions Relevant**: Only suggest options that make sense for the current context
2. **Use Permissions**: Restrict tab completion based on user permissions
3. **Handle Argument Count**: Provide different suggestions based on argument count
4. **Return Null When Done**: Return `null` when no more suggestions are available
5. **Use Priority Wisely**: Use priority to control which suggestions appear first

## File Structure

- `SubCommandTab.java` - The annotation definition
- `TabCompleteInfo.java` - Information class for tab completion methods
- `CommandNode.java` - Updated to handle tab completion
- `Command.java` - Updated to collect and use tab completion methods
- `TabCompleteExample.java` - Example implementation
- `TabCompleteTest.java` - Test cases for tab completion

## Notes

- Tab completion methods are automatically discovered and registered
- The system respects permissions and only shows suggestions to authorized users
- Multiple tab completion methods can exist for the same command path
- The system uses the same command node structure as regular commands
- Tab completion works with both simple and nested command paths
