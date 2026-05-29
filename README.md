# EasyCommands Framework 0.3.1

A powerful, cutting-edge command framework for Bukkit/Spigot/Paper plugins that provides annotation-based command handling, automatic type resolution, interactive chat prompts, and modular subcommand systems.

## 🌟 Features

- 🎯 **Annotation-Based**: Simple `@SubCommand` annotations. Say goodbye to giant `if-else` blocks!
- 🧩 **Auto-Dependency Injection**: Pass custom objects like `@Sender GamePlayer`, integers, or `OfflinePlayer` straight into your methods!
- ⏳ **Built-in Cooldowns**: `@Cooldown(seconds = 5)` handles everything out of the box.
- 🛡️ **Global Conditionals**: `@Require("has_money")` safely intercepts commands before execution.
- 🔒 **Built-in Confirmations**: `@Confirm(timeout = 10)` intercepts dangerous commands and forces the player to confirm.
- 🚀 **Asynchronous Execution**: Add `@Async` to offload heavy database/API logic to a worker thread instantly.
- 🚩 **Command Flags**: Use `@Flag("-force") boolean force` to extract flags from any point in the argument string!
- 💬 **Interactive Prompts**: `EasyCommands.prompt(plugin, player, response -> ...)` safely intercepts chat and bounces execution back to the main thread.
- ⚠️ **Global Exception Handlers**: Intercept exceptions globally and gracefully format errors for the player.
- 📚 **Paginated Help Menus**: Automatically generates interactive, clickable help menus via the Adventure Kyori API!
- 🖼️ **Auto-GUI Menus**: Turn your base command into an interactive Chest GUI with zero extra code.
- 🧩 **Class-Based Subcommands**: Build massive, modular command systems without massive files.

---

## 🚀 Quick Start

### 1. Basic Command Setup

Creating a command is as simple as defining a method!

```java
public class MyCommand extends BaseCommand {
    
    @Override
    public String getName() {
        return "mycommand"; // Registers as /mycommand
    }
    
    // The base command execution (/mycommand)
    @Override
    public void mainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§aMyCommand executed!");
    }
    
    // Sub command (/mycommand give <amount>)
    @SubCommand(
        commandPath = {}, 
        name = "give", 
        permission = "mycommand.give"
    )
    public void giveCommand(Player player, int amount) {
        player.sendMessage("§eGiving " + amount + " items!");
    }
}
```

### 2. Auto-Resolving Parameters
EasyCommands automatically resolves strings into primitives, `Player`, `OfflinePlayer`, `Material`, etc.
If an argument is missing, or invalid, it automatically sends a localized error message to the player!

```java
@SubCommand(commandPath = {}, name = "teleport")
public void teleportCommand(Player sender, Player target, @Optional("world") String environment) {
    sender.teleport(target);
    sender.sendMessage("Teleported in " + environment);
}
```

### 3. Command Flags
Flags can be placed anywhere in the command string (e.g. `/admin ban Dami -silent`).
```java
@SubCommand(commandPath = {}, name = "ban")
public void banCommand(CommandSender sender, String target, @Flag("-silent") boolean silent) {
    if (silent) {
        sender.sendMessage("Banned silently.");
    }
}
```

---

## ⚡ Advanced Interactivity

### Command Confirmations (`@Confirm`)
Prevent players from accidentally running destructive commands. If annotated, the framework will intercept the command, warn the user, and require them to run the exact same command again within the timeout.

```java
@SubCommand(commandPath = {}, name = "disband")
@Confirm(timeout = 10)
public void disbandFaction(Player player) {
    player.sendMessage("Your faction has been destroyed!");
}
```

### Interactive Chat Prompts
Launch conversational wizards directly from commands. The framework intercepts their next chat message, cancels the event, and securely routes the logic back to the **Main Server Thread**!

```java
@SubCommand(commandPath = {}, name = "create")
public void createArena(Player player) {
    player.sendMessage("What should we name the arena?");
    
    EasyCommands.prompt(plugin, player, response -> {
        player.sendMessage("Creating arena named: " + response);
    });
}
```

---

## 🛡️ Architecture & Registries

Registering advanced interceptors via the `EasyCommands` facade.

### Global Conditionals (`@Require`)
Define global conditions that can be applied to any command.

```java
// Register the condition
EasyCommands.registerCondition("has_money", sender -> {
    if (playerHasNoMoney) {
        throw new ValidationException(MessageKey.NO_PERMISSION, placeholders);
    }
});

// Use it anywhere
@SubCommand(commandPath = {}, name = "buy")
@Require("has_money")
public void buyItem(Player player) { ... }
```

### Custom Context Injection (`@Sender`)
Inject custom wrapper objects straight into your command signatures.

```java
// Register the resolver
EasyCommands.registerSenderResolver(GamePlayer.class, sender -> database.get(sender.getUniqueId()));

// Use it anywhere
@SubCommand(commandPath = {}, name = "stats")
public void viewStats(@Sender GamePlayer player) {
    player.sendMessage("Your kills: " + player.getKills());
}
```

### Global Exception Handlers
Stop writing `try-catch` blocks in every command. Catch business logic exceptions globally.

```java
EasyCommands.registerExceptionHandler(NotEnoughMoneyException.class, (sender, ex) -> {
    sender.sendMessage("§cYou cannot afford this!");
});
```

---

## Installation

### Maven

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.github.Damijongerius</groupId>
    <artifactId>easycommands</artifactId>
    <version>0.3.1</version>
</dependency>
```

## Contributing
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new features (`mvn test`)
5. Submit a pull request

**EasyCommands Framework** - Making Bukkit command development simple, beautiful, and incredibly powerful! 🚀
