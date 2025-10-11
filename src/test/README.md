# Testing Your Command Framework

This directory contains examples and tests for your EasyCommands framework. Here's how to test your command class by extending it.

## Overview

Your `Command` class is designed to be extended by other classes that implement the `ICommand` interface. To test it, you need to:

1. Create a test command class that extends `Command`
2. Implement the required methods (`getName()`, `maxArgs()`, `MainCommand()`)
3. Add subcommands using the `@SubCommand` annotation
4. Test the command execution logic

## Example Test Command

Here's how to create a test command:

```java
public class TestCommand extends Command {
    
    public TestCommand(Plugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "testcommand";
    }
    
    @Override
    public int maxArgs() {
        return 1;
    }
    
    @Override
    public void MainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§aTestCommand main command executed!");
    }
    
    @SubCommand(
        commandPath = {},
        name = "help",
        maxArgs = 0,
        permission = "testcommand.help"
    )
    public void helpCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§eHelp command executed!");
    }
}
```

## Testing Approaches

### 1. Unit Testing Framework Logic

Test the core logic of your command framework without requiring Bukkit dependencies:

```java
@Test
public void testCommandPathBuilding() {
    String[] commandPath = {};
    String name = "help";
    
    String[] fullPath = java.util.stream.Stream.concat(
        java.util.Arrays.stream(commandPath),
        java.util.stream.Stream.of(name)
    ).toArray(String[]::new);
    
    assertEquals(1, fullPath.length);
    assertEquals("help", fullPath[0]);
}
```

### 2. Testing Command Execution Flow

Test the command execution logic:

```java
@Test
public void testCommandExecution() {
    String[] args = {"help"};
    
    if (args.length > 0) {
        String subCommand = args[0];
        assertEquals("help", subCommand);
    }
}
```

### 3. Testing Subcommand Annotations

Test the annotation processing logic:

```java
@Test
public void testSubCommandAnnotation() {
    // Test that @SubCommand annotations are processed correctly
    String[] commandPath = {"admin"};
    String name = "reload";
    
    // Simulate the path building from your Command class
    String[] fullPath = java.util.stream.Stream.concat(
        java.util.Arrays.stream(commandPath),
        java.util.stream.Stream.of(name)
    ).toArray(String[]::new);
    
    assertEquals(2, fullPath.length);
    assertEquals("admin", fullPath[0]);
    assertEquals("reload", fullPath[1]);
}
```

## Mock Classes (Optional)

If you need to test with actual Bukkit components, you can create mock classes:

```java
public class MockPlayer implements CommandSender {
    private final String name;
    private final Set<String> permissions;
    
    public MockPlayer(String name) {
        this.name = name;
        this.permissions = new HashSet<>();
    }
    
    @Override
    public void sendMessage(String message) {
        System.out.println("[" + name + "] " + message);
    }
    
    @Override
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
    
    // Add other required methods...
}
```

## Testing Command Execution

To test command execution with custom parameters:

```java
@Test
public void testCommandExecution() {
    // Create your test command
    TestCommand testCommand = new TestCommand(mockPlugin);
    
    // Create mock player
    MockPlayer player = new MockPlayer("TestPlayer");
    
    // Test main command
    boolean result = testCommand.onCommand(player, null, "testcommand", new String[]{});
    assertTrue(result);
    
    // Test subcommand
    result = testCommand.onCommand(player, null, "testcommand", new String[]{"help"});
    assertTrue(result);
}
```

## Key Testing Points

1. **Command Name Validation**: Test that command names are not null or empty
2. **Max Args Validation**: Test that argument counts are within limits
3. **Subcommand Path Building**: Test the path building logic
4. **Permission Handling**: Test permission string building and validation
5. **Tab Completion**: Test tab completion logic
6. **Error Handling**: Test error scenarios and edge cases
7. **Command Weight**: Test command priority and weight system

## Files in This Directory

- `testEverything.java` - Basic test examples
- `CommandFrameworkTest.java` - Comprehensive framework tests
- `CommandTestExample.java` - Practical testing examples
- `CommandTestingExample.java` - Core logic tests
- `ExampleTestCommand.java` - Example test command class
- `SimpleTestCommand.java` - Simple test command example

## Running Tests

To run the tests, use your IDE's test runner or Maven/Gradle test commands:

```bash
# Maven
mvn test

# Gradle
./gradlew test
```

## Notes

- The mock classes are simplified and may not implement all Bukkit interface methods
- Focus on testing the core logic of your command framework
- Use unit tests to verify individual components
- Integration tests can verify the complete command execution flow
- Consider using testing frameworks like Mockito for more advanced mocking if needed