# EasyCommands Documentation

Welcome to the comprehensive documentation for the EasyCommands framework! This documentation covers everything you need to know to build powerful command systems for your Bukkit/Spigot plugins.

## 📚 Documentation Index

### 🚀 Getting Started
- [**Getting Started Guide**](getting-started.md) - Complete setup and basic usage
- [**Installation Guide**](installation.md) - How to add EasyCommands to your project
- [**Quick Start Tutorial**](quick-start.md) - Build your first command in 5 minutes

### 🎯 Core Features
- [**Command System**](command-system.md) - Complete command framework documentation
- [**Tab Completion**](tab-completion.md) - Intelligent command suggestions
- [**Class-Based Subcommands**](class-based-subcommands.md) - Modular command design
- [**Permission System**](permission-system.md) - Access control and security

### 🧪 Testing & Development
- [**Testing Guide**](testing.md) - How to test your commands
- [**Mock Classes**](mock-classes.md) - Mock implementations for testing
- [**Debug Guide**](debug.md) - Troubleshooting and debugging
- [**Performance Guide**](performance.md) - Optimization tips and best practices

### 📖 Reference
- [**API Reference**](api-reference.md) - Complete API documentation
- [**Annotations**](annotations.md) - All available annotations
- [**Examples**](examples.md) - Real-world usage examples
- [**Changelog**](changelog.md) - Version history and updates

### 🔧 Advanced Topics
- [**Custom Annotations**](custom-annotations.md) - Creating your own annotations
- [**Plugin Integration**](plugin-integration.md) - Integrating with other plugins
- [**Migration Guide**](migration.md) - Upgrading from other frameworks
- [**Contributing**](contributing.md) - How to contribute to the project

## 🎯 Quick Navigation

### By Experience Level

#### Beginner
- [Getting Started Guide](getting-started.md)
- [Basic Command Examples](examples.md#basic-examples)
- [Simple Tab Completion](tab-completion.md#basic-tab-completion)

#### Intermediate
- [Advanced Command Features](command-system.md#advanced-features)
- [Permission-Based Tab Completion](tab-completion.md#permission-based-tab-completion)
- [Class-Based Subcommands](class-based-subcommands.md)

#### Advanced
- [Custom Subcommand Classes](class-based-subcommands.md#advanced-features)
- [Performance Optimization](performance.md)
- [API Reference](api-reference.md)

### By Feature

#### Commands
- [Basic Commands](getting-started.md#your-first-command)
- [Subcommands](command-system.md#subcommand-annotation)
- [Nested Commands](command-system.md#nested-commands)
- [Class-Based Commands](class-based-subcommands.md)

#### Tab Completion
- [Simple Tab Completion](tab-completion.md#basic-tab-completion)
- [Context-Aware Suggestions](tab-completion.md#context-aware-suggestions)
- [Permission-Based Completion](tab-completion.md#permission-based-tab-completion)
- [Dynamic Suggestions](tab-completion.md#dynamic-tab-completion)

#### Testing
- [Unit Testing](testing.md#basic-testing)
- [Integration Testing](testing.md#advanced-testing)
- [Mock Classes](testing.md#mock-classes)
- [Test Examples](testing.md#test-examples)

## 🚀 Quick Start

### 1. Installation

Add EasyCommands to your project:

**Maven:**
```xml
<dependency>
    <groupId>com.dami</groupId>
    <artifactId>easycommands</artifactId>
    <version>0.0.9</version>
</dependency>
```

**Gradle:**
```gradle
dependencies {
    implementation 'com.dami:easycommands:0.0.9'
}
```

### 2. Create Your First Command

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
        sender.sendMessage("§aHello from MyCommand!");
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

### 3. Register in Your Plugin

```java
public class MyPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        new MyCommand(this);
    }
}
```

### 4. Add to plugin.yml

```yaml
commands:
  mycommand:
    description: My command
    usage: /mycommand [args]
```

## 📋 Documentation Structure

### Core Documentation
- **Getting Started**: Essential guides for new users
- **Command System**: Complete framework documentation
- **Tab Completion**: Intelligent suggestion system
- **Class-Based Subcommands**: Modular command design

### Development Documentation
- **Testing**: Comprehensive testing guides
- **Examples**: Real-world usage patterns
- **API Reference**: Complete technical reference
- **Performance**: Optimization and best practices

### Advanced Documentation
- **Custom Features**: Extending the framework
- **Integration**: Working with other plugins
- **Migration**: Upgrading from other systems
- **Contributing**: Contributing to the project

## 🔍 Finding What You Need

### Common Tasks

**"How do I create a basic command?"**
→ [Getting Started Guide](getting-started.md#your-first-command)

**"How do I add tab completion?"**
→ [Tab Completion Guide](tab-completion.md#basic-tab-completion)

**"How do I create nested commands?"**
→ [Command System Guide](command-system.md#nested-commands)

**"How do I test my commands?"**
→ [Testing Guide](testing.md#basic-testing)

**"How do I create class-based subcommands?"**
→ [Class-Based Subcommands Guide](class-based-subcommands.md)

### Problem Solving

**"My command isn't working"**
→ [Debug Guide](debug.md) → [Troubleshooting](debug.md#troubleshooting)

**"Tab completion isn't showing"**
→ [Tab Completion Guide](tab-completion.md) → [Common Issues](tab-completion.md#troubleshooting)

**"Permission denied errors"**
→ [Permission System Guide](permission-system.md) → [Permission Troubleshooting](permission-system.md#troubleshooting)

**"Performance issues"**
→ [Performance Guide](performance.md) → [Optimization Tips](performance.md#optimization-tips)

## 📞 Getting Help

### Documentation
- Check the relevant guide for your question
- Look at the [Examples](examples.md) for similar use cases
- Review the [API Reference](api-reference.md) for technical details

### Community
- **GitHub Issues**: Report bugs and request features
- **GitHub Discussions**: Ask questions and share ideas
- **Discord**: Join our community server for real-time help

### Support
- **Documentation**: Comprehensive guides and examples
- **Examples**: Real-world usage patterns
- **Test Cases**: Working examples in the test directory

## 🎯 Next Steps

1. **Start with the Basics**: Read the [Getting Started Guide](getting-started.md)
2. **Build Your First Command**: Follow the [Quick Start Tutorial](quick-start.md)
3. **Explore Advanced Features**: Check out [Tab Completion](tab-completion.md) and [Class-Based Subcommands](class-based-subcommands.md)
4. **Test Your Commands**: Use the [Testing Guide](testing.md)
5. **Optimize Performance**: Follow the [Performance Guide](performance.md)

## 📝 Contributing

We welcome contributions to the documentation! If you find an error, have a suggestion, or want to add new content:

1. **Fork the repository**
2. **Create a feature branch**
3. **Make your changes**
4. **Submit a pull request**

See the [Contributing Guide](contributing.md) for more details.

---

**Ready to build amazing commands?** Start with the [Getting Started Guide](getting-started.md) and explore the power of EasyCommands! 🚀
