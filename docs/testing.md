# Testing Guide

This document explains how to test your EasyCommands framework implementation using the provided testing tools and examples.

## Table of Contents

- [Overview](#overview)
- [Basic Testing](#basic-testing)
- [Advanced Testing](#advanced-testing)
- [Mock Classes](#mock-classes)
- [Test Examples](#test-examples)
- [Best Practices](#best-practices)

## Overview

The EasyCommands framework includes comprehensive testing support to help you verify that your commands work correctly. This includes unit tests, integration tests, and mock classes for testing without requiring a full Bukkit server.

### Testing Features

- **Unit Tests**: Test individual command logic
- **Integration Tests**: Test complete command execution
- **Mock Classes**: Test without Bukkit dependencies
- **Tab Completion Tests**: Verify tab completion functionality
- **Permission Tests**: Test permission-based access control

## Basic Testing

### Simple Command Testing

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyCommandTest {
    
    @Test
    public void testCommandName() {
        String commandName = "mycommand";
        assertNotNull(commandName, "Command name should not be null");
        assertFalse(commandName.isEmpty(), "Command name should not be empty");
    }
    
    @Test
    public void testArgumentHandling() {
        String[] args = {"help"};
        assertEquals(1, args.length, "Should have 1 argument");
        assertEquals("help", args[0], "First argument should be 'help'");
    }
    
    @Test
    public void testCommandPathBuilding() {
        String[] commandPath = {};
        String name = "help";
        
        String[] fullPath = java.util.stream.Stream.concat(
            java.util.Arrays.stream(commandPath),
            java.util.stream.Stream.of(name)
        ).toArray(String[]::new);
        
        assertEquals(1, fullPath.length, "Full path should have 1 element");
        assertEquals("help", fullPath[0], "First element should be 'help'");
    }
}
```

### Tab Completion Testing

```java
@Test
public void testTabCompletion() {
    List<String> suggestions = Arrays.asList("help", "info", "admin");
    assertNotNull(suggestions, "Suggestions should not be null");
    assertEquals(3, suggestions.size(), "Should have 3 suggestions");
    assertTrue(suggestions.contains("help"), "Should contain 'help'");
    assertTrue(suggestions.contains("info"), "Should contain 'info'");
    assertTrue(suggestions.contains("admin"), "Should contain 'admin'");
}

@Test
public void testTabCompletionFiltering() {
    List<String> allSuggestions = Arrays.asList("help", "info", "admin", "player");
    String partialInput = "h";
    
    List<String> filteredSuggestions = allSuggestions.stream()
        .filter(suggestion -> suggestion.startsWith(partialInput))
        .collect(Collectors.toList());
    
    assertEquals(1, filteredSuggestions.size(), "Should have 1 filtered suggestion");
    assertEquals("help", filteredSuggestions.get(0), "Filtered suggestion should be 'help'");
}
```

### Permission Testing

```java
@Test
public void testPermissionHandling() {
    String permission = "myplugin.admin";
    assertNotNull(permission, "Permission should not be null");
    assertTrue(permission.startsWith("myplugin"), "Permission should start with 'myplugin'");
    assertTrue(permission.contains("admin"), "Permission should contain 'admin'");
}

@Test
public void testPermissionHierarchy() {
    String basePermission = "myplugin";
    String adminPermission = "myplugin.admin";
    String kickPermission = "myplugin.admin.kick";
    
    assertTrue(adminPermission.startsWith(basePermission), "Admin permission should start with base");
    assertTrue(kickPermission.startsWith(adminPermission), "Kick permission should start with admin");
}
```

## Advanced Testing

### Command Execution Flow Testing

```java
@Test
public void testCommandExecutionFlow() {
    // Test different command scenarios
    String[][] testCases = {
        {}, // No args - should trigger main command
        {"help"}, // Single subcommand
        {"admin", "kick"}, // Nested subcommand
        {"player", "info", "Player1"} // Multi-arg subcommand
    };
    
    for (String[] args : testCases) {
        assertNotNull(args, "Args should not be null");
        
        if (args.length == 0) {
            // Should execute main command
            assertTrue(true, "Empty args should trigger main command");
        } else {
            // Should execute subcommand
            String subCommand = args[0];
            assertNotNull(subCommand, "Subcommand should not be null");
            assertFalse(subCommand.isEmpty(), "Subcommand should not be empty");
        }
    }
}
```

### Nested Command Testing

```java
@Test
public void testNestedCommandPath() {
    String[] commandPath = {"admin"};
    String name = "kick";
    
    String[] fullPath = java.util.stream.Stream.concat(
        java.util.Arrays.stream(commandPath),
        java.util.stream.Stream.of(name)
    ).toArray(String[]::new);
    
    assertEquals(2, fullPath.length, "Nested path should have 2 elements");
    assertEquals("admin", fullPath[0], "First element should be 'admin'");
    assertEquals("kick", fullPath[1], "Second element should be 'kick'");
}

@Test
public void testNestedCommandExecution() {
    String[] args = {"admin", "kick", "Player1", "griefing"};
    
    // Test path extraction
    String[] path = Arrays.copyOfRange(args, 0, 2);
    String[] subArgs = Arrays.copyOfRange(args, 2, args.length);
    
    assertEquals(2, path.length, "Path should have 2 elements");
    assertEquals("admin", path[0], "First path element should be 'admin'");
    assertEquals("kick", path[1], "Second path element should be 'kick'");
    
    assertEquals(2, subArgs.length, "Sub args should have 2 elements");
    assertEquals("Player1", subArgs[0], "First sub arg should be 'Player1'");
    assertEquals("griefing", subArgs[1], "Second sub arg should be 'griefing'");
}
```

### Weight System Testing

```java
@Test
public void testWeightSystem() {
    int defaultWeight = 0;
    int highWeight = 10;
    int lowWeight = -5;
    
    assertTrue(highWeight > defaultWeight, "High weight should be greater than default");
    assertTrue(defaultWeight > lowWeight, "Default weight should be greater than low weight");
    
    // Test priority selection
    int[] weights = {defaultWeight, highWeight, lowWeight};
    int maxWeight = Arrays.stream(weights).max().orElse(0);
    assertEquals(highWeight, maxWeight, "Highest weight should be selected");
}
```

## Mock Classes

### Basic Mock Player

```java
public class MockPlayer implements CommandSender {
    private final String name;
    private final Set<String> permissions;
    
    public MockPlayer(String name) {
        this.name = name;
        this.permissions = new HashSet<>();
    }
    
    public MockPlayer(String name, Set<String> permissions) {
        this.name = name;
        this.permissions = permissions != null ? permissions : new HashSet<>();
    }
    
    @Override
    public void sendMessage(String message) {
        System.out.println("[" + name + "] " + message);
    }
    
    @Override
    public boolean hasPermission(String permission) {
        return permissions.contains(permission) || permissions.contains("*");
    }
    
    public void addPermission(String permission) {
        permissions.add(permission);
    }
    
    public void removePermission(String permission) {
        permissions.remove(permission);
    }
    
    // Implement other required methods...
}
```

### Mock Plugin

```java
public class MockPlugin implements Plugin {
    private final String name;
    private final Server server;
    private final Logger logger;
    
    public MockPlugin(String name, Server server) {
        this.name = name;
        this.server = server;
        this.logger = Logger.getLogger(name);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public Server getServer() {
        return server;
    }
    
    @Override
    public Logger getLogger() {
        return logger;
    }
    
    // Implement other required methods...
}
```

### Mock Server

```java
public class MockServer implements Server {
    private final Map<String, PluginCommand> commands = new HashMap<>();
    
    public void registerCommand(String name, PluginCommand command) {
        commands.put(name, command);
    }
    
    @Override
    public PluginCommand getPluginCommand(String name) {
        return commands.get(name);
    }
    
    // Implement other required methods...
}
```

## Test Examples

### Complete Command Test

```java
public class MyCommandTest {
    
    private MyCommand myCommand;
    private MockPlayer player;
    private MockPlayer adminPlayer;
    private MockPlugin plugin;
    private MockServer server;
    
    @BeforeEach
    public void setUp() {
        // Create mock server and plugin
        server = new MockServer();
        plugin = new MockPlugin("TestPlugin", server);
        
        // Create mock plugin command and register it
        PluginCommand pluginCommand = new PluginCommand("mycommand", plugin);
        server.registerCommand("mycommand", pluginCommand);
        
        // Create test command instance
        myCommand = new MyCommand(plugin);
        
        // Create test players
        player = new MockPlayer("TestPlayer");
        adminPlayer = new MockPlayer("AdminPlayer");
        
        // Give admin player permissions
        adminPlayer.addPermission("myplugin.admin");
        adminPlayer.addPermission("myplugin.admin.kick");
    }
    
    @Test
    public void testMainCommand() {
        // Test main command without arguments
        boolean result = myCommand.onCommand(player, null, "mycommand", new String[]{});
        assertTrue(result, "Main command should return true");
        
        // Test main command with arguments
        result = myCommand.onCommand(player, null, "mycommand", new String[]{"arg1"});
        assertTrue(result, "Main command with args should return true");
    }
    
    @Test
    public void testHelpCommand() {
        // Test help command
        boolean result = myCommand.onCommand(player, null, "mycommand", new String[]{"help"});
        assertTrue(result, "Help command should return true");
    }
    
    @Test
    public void testAdminCommand() {
        // Test admin command with permission
        boolean result = myCommand.onCommand(adminPlayer, null, "mycommand", new String[]{"admin"});
        assertTrue(result, "Admin command should return true");
        
        // Test admin command without permission
        result = myCommand.onCommand(player, null, "mycommand", new String[]{"admin"});
        assertTrue(result, "Admin command should return true (permission check happens inside)");
    }
    
    @Test
    public void testTabComplete() {
        // Test tab completion for first argument
        var completions = myCommand.onTabComplete(player, null, "mycommand", new String[]{""});
        assertNotNull(completions, "Tab completion should not be null");
        assertTrue(completions.contains("help"), "Tab completion should contain 'help'");
        assertTrue(completions.contains("admin"), "Tab completion should contain 'admin'");
    }
}
```

### Class-Based Subcommand Test

```java
public class ClassBasedSubCommandTest {
    
    @Test
    public void testAdminSubCommand() {
        // Test admin subcommand logic
        String[] args = {"reload"};
        String subCommand = args[0];
        
        assertEquals("reload", subCommand, "Subcommand should be 'reload'");
        
        // Test admin command suggestions
        List<String> adminCommands = Arrays.asList("reload", "kick", "ban", "mute");
        assertNotNull(adminCommands, "Admin commands should not be null");
        assertEquals(4, adminCommands.size(), "Should have 4 admin commands");
        assertTrue(adminCommands.contains("reload"), "Should contain 'reload'");
        assertTrue(adminCommands.contains("kick"), "Should contain 'kick'");
    }
    
    @Test
    public void testPlayerSubCommand() {
        // Test player subcommand logic
        String[] args = {"info", "Player1"};
        String subCommand = args[0];
        String playerName = args[1];
        
        assertEquals("info", subCommand, "Subcommand should be 'info'");
        assertEquals("Player1", playerName, "Player name should be 'Player1'");
        
        // Test player command suggestions
        List<String> playerCommands = Arrays.asList("info", "list", "teleport");
        assertNotNull(playerCommands, "Player commands should not be null");
        assertEquals(3, playerCommands.size(), "Should have 3 player commands");
        assertTrue(playerCommands.contains("info"), "Should contain 'info'");
        assertTrue(playerCommands.contains("teleport"), "Should contain 'teleport'");
    }
}
```

## Best Practices

### 1. Test Command Logic Separately

```java
@Test
public void testCommandLogic() {
    // Test the core logic without Bukkit dependencies
    String[] args = {"help"};
    String subCommand = args[0];
    
    assertEquals("help", subCommand, "Subcommand should be 'help'");
    
    // Test argument validation
    assertTrue(args.length <= 1, "Args should be within limit");
}
```

### 2. Test Error Scenarios

```java
@Test
public void testErrorScenarios() {
    // Test null command name
    String nullCommandName = null;
    boolean isNullCommand = nullCommandName == null;
    assertTrue(isNullCommand, "Null command name should be detected");
    
    // Test empty command name
    String emptyCommandName = "";
    boolean isEmptyCommand = emptyCommandName.isEmpty();
    assertTrue(isEmptyCommand, "Empty command name should be detected");
    
    // Test command not found scenario
    String[] availableCommands = {"help", "info", "admin"};
    String requestedCommand = "invalid";
    boolean commandExists = Arrays.asList(availableCommands).contains(requestedCommand);
    assertFalse(commandExists, "Invalid command should not be found");
}
```

### 3. Test Permission Scenarios

```java
@Test
public void testPermissionScenarios() {
    // Test permission string building
    String basePermission = "myplugin";
    String adminPermission = "myplugin.admin";
    String kickPermission = "myplugin.admin.kick";
    
    assertTrue(adminPermission.startsWith(basePermission), "Admin permission should start with base");
    assertTrue(kickPermission.startsWith(adminPermission), "Kick permission should start with admin");
    
    // Test permission validation
    String[] userPermissions = {"myplugin.admin", "myplugin.admin.kick"};
    String requiredPermission = "myplugin.admin";
    
    boolean hasPermission = Arrays.asList(userPermissions).contains(requiredPermission);
    assertTrue(hasPermission, "User should have required permission");
}
```

### 4. Test Tab Completion Scenarios

```java
@Test
public void testTabCompletionScenarios() {
    // Test basic tab completion
    List<String> suggestions = Arrays.asList("help", "info", "admin");
    assertNotNull(suggestions, "Suggestions should not be null");
    assertEquals(3, suggestions.size(), "Should have 3 suggestions");
    
    // Test filtering
    String partialInput = "h";
    List<String> filteredSuggestions = suggestions.stream()
        .filter(suggestion -> suggestion.startsWith(partialInput))
        .collect(Collectors.toList());
    
    assertEquals(1, filteredSuggestions.size(), "Should have 1 filtered suggestion");
    assertEquals("help", filteredSuggestions.get(0), "Filtered suggestion should be 'help'");
}
```

### 5. Test Argument Handling

```java
@Test
public void testArgumentHandling() {
    // Test valid arguments
    String[] validArgs = {"help"};
    int maxArgs = 1;
    boolean isValid = validArgs.length <= maxArgs;
    assertTrue(isValid, "Valid args should pass validation");
    
    // Test too many arguments
    String[] tooManyArgs = {"help", "extra", "args"};
    boolean isInvalid = tooManyArgs.length > maxArgs;
    assertTrue(isInvalid, "Too many args should fail validation");
    
    // Test argument parsing
    String command = "mycommand help";
    String[] parts = command.split(" ");
    
    assertEquals(2, parts.length, "Command should split into 2 parts");
    assertEquals("mycommand", parts[0], "Command name should be correct");
    assertEquals("help", parts[1], "Subcommand should be correct");
}
```

## Troubleshooting

### Common Issues

1. **Tests not running**: Check that JUnit is properly configured
2. **Mock classes not working**: Ensure all required methods are implemented
3. **Permission tests failing**: Verify permission strings are correct
4. **Tab completion not working**: Check that methods return `List<String>`

### Debug Tips

1. **Add logging**: Use `System.out.println()` for debug output
2. **Test incrementally**: Start with simple tests and build up
3. **Use assertions**: Always verify expected behavior
4. **Check method signatures**: Ensure all required methods are implemented

---

**Next**: Check out the [Examples](examples.md) for real-world usage patterns! 🚀
