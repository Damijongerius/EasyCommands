# EasyCommands Framework

A powerful and flexible command framework for Bukkit/Spigot plugins that provides annotation-based command handling, tab completion, and modular subcommand systems.

## Features

- 🎯 **Annotation-Based Commands**: Simple `@SubCommand` annotations for method-based commands
- 🔄 **Tab Completion**: Built-in tab completion with `@SubCommandTab` annotations
- 🏗️ **Class-Based Subcommands**: Register entire classes as subcommands for modular design
- 🔐 **Permission System**: Integrated permission checking for commands and tab completion
- ⚡ **Weight System**: Priority-based command execution
- 🧪 **Comprehensive Testing**: Full test suite with examples and mock classes
- 📚 **Extensive Documentation**: Complete guides and examples

## Quick Start

### 1. Basic Command Setup

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
        sender.sendMessage("§aMyCommand executed!");
    }
    
    @SubCommand(
        commandPath = {},
        name = "help",
        maxArgs = 0,
        permission = ""
    )
    public void helpCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§eHelp command executed!");
    }
}
```

### 2. Tab Completion

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

### 3. Class-Based Subcommands

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
        sender.sendMessage("§aAdmin command executed!");
    }
    
    @Override
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        return Arrays.asList("reload", "kick", "ban", "mute");
    }
}
```

## Documentation

### 📖 Core Documentation
- [**Getting Started Guide**](docs/getting-started.md) - Complete setup and basic usage
- [**Command System**](docs/command-system.md) - Detailed command framework documentation
- [**Tab Completion**](docs/tab-completion.md) - Tab completion system guide
- [**Class-Based Subcommands**](docs/class-based-subcommands.md) - Modular subcommand system
- [**Permission System**](docs/permission-system.md) - Permission handling guide

### 🧪 Testing Documentation
- [**Testing Guide**](docs/testing.md) - How to test your commands
- [**Mock Classes**](docs/mock-classes.md) - Mock implementations for testing
- [**Test Examples**](docs/test-examples.md) - Comprehensive test examples

### 📚 API Reference
- [**Annotations**](docs/annotations.md) - Complete annotation reference
- [**Classes**](docs/classes.md) - Framework class documentation
- [**Examples**](docs/examples.md) - Real-world usage examples

## Installation

### Maven

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.dami</groupId>
    <artifactId>easycommands</artifactId>
    <version>0.0.9</version>
</dependency>
```

### Gradle

Add to your `build.gradle`:

```gradle
dependencies {
    implementation 'com.dami:easycommands:0.0.9'
}
```

## Basic Usage

### 1. Create Your Command Class

```java
public class MyPluginCommand extends Command {
    
    public MyPluginCommand(Plugin plugin) {
        super(plugin);
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

### 2. Register in Your Plugin

```java
public class MyPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        // Register your command
        new MyPluginCommand(this);
    }
}
```

### 3. Add to plugin.yml

```yaml
name: MyPlugin
version: 1.0.0
main: com.example.MyPlugin

commands:
  myplugin:
    description: My plugin command
    usage: /myplugin [args]
```

## Advanced Features

### Method-Based Subcommands

```java
@SubCommand(
    commandPath = {},
    name = "reload",
    maxArgs = 0,
    permission = "myplugin.reload"
)
public void reloadCommand(CommandSender sender, String[] args) {
    sender.sendMessage("§aReloading configuration...");
}

@SubCommand(
    commandPath = {"admin"},
    name = "kick",
    maxArgs = 2,
    permission = "myplugin.admin.kick"
)
public void adminKickCommand(CommandSender sender, String[] args) {
    if (args.length < 2) {
        sender.sendMessage("§cUsage: /myplugin admin kick <player> <reason>");
        return;
    }
    
    String playerName = args[0];
    String reason = args[1];
    sender.sendMessage("§cKicking " + playerName + " for: " + reason);
}
```

### Class-Based Subcommands

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
        // Admin command logic
    }
    
    @Override
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        return Arrays.asList("reload", "kick", "ban", "mute");
    }
}
```

### Tab Completion

```java
@SubCommandTab(
    commandPath = {},
    name = "help",
    permission = ""
)
public List<String> helpTabComplete(CommandSender sender, String[] args) {
    return Arrays.asList("general", "commands", "permissions");
}

@SubCommandTab(
    commandPath = {"admin"},
    name = "kick",
    permission = "myplugin.admin.kick"
)
public List<String> adminKickTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
        return Arrays.asList("Player1", "Player2", "Player3");
    } else if (args.length == 2) {
        return Arrays.asList("griefing", "spamming", "cheating");
    }
    return null;
}
```

## Testing

The framework includes comprehensive testing support:

```java
@Test
public void testCommandExecution() {
    // Test your command logic
    String[] args = {"help"};
    assertEquals("help", args[0]);
}
```

## Examples

Check the `src/test/java/` directory for complete examples:

- **Basic Commands**: Simple command implementations
- **Tab Completion**: Tab completion examples
- **Class-Based Subcommands**: Modular subcommand examples
- **Testing**: Comprehensive test cases

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new features
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- 📖 **Documentation**: Check the `docs/` directory for detailed guides
- 🐛 **Issues**: Report bugs on GitHub Issues
- 💬 **Discussions**: Join discussions on GitHub Discussions
- 📧 **Contact**: Contact the maintainer for support

## Changelog

### Version 0.0.9
- Initial release
- Annotation-based command system
- Tab completion support
- Class-based subcommand system
- Comprehensive testing framework
- Full documentation

---

**EasyCommands Framework** - Making Bukkit command development simple and powerful! 🚀
