# ⚡ EasyCommands Power User Guide

Welcome to the full potential of EasyCommands. This guide showcases how to use the framework's most powerful features to build clean, robust, and user-friendly command systems with minimal code.

## 🚀 The "Mega Example"

This single class demonstrates **Smart Parameters**, **Validation**, **Optional Values**, and **Automatic Tab Completion**.

```java
public class MagicCommand extends BaseCommand {

    public MagicCommand() {
        super();
        initialize(); // Required to register sub-commands
    }

    @Override
    public String getName() {
        return "magic";
    }

    // 1. Smart Parameters & Optional Values
    // /magic gift <player> <material> [amount=64]
    @SubCommand(name = "gift", description = "Gift items to players")
    public void giftItem(
        Player sender, 
        Player target, 
        Material item, 
        @Min(1) @Max(64) @Optional("64") int amount
    ) {
        target.getInventory().addItem(new ItemStack(item, amount));
        sender.sendMessage("§aGifted " + amount + " " + item + " to " + target.getName());
    }

    // 2. Automatic Tab Completion
    // EasyCommands knows how to complete: Player, World, Material, Sound, EntityType, Boolean
    @SubCommand(name = "play", description = "Play a sound to a player")
    public void playSound(Player sender, Player target, Sound sound, @Optional("1.0") float pitch) {
        target.playSound(target.getLocation(), sound, 1.0f, pitch);
    }

    // 3. Automated Help & Descriptions
    // Simply running /magic will show a beautiful help menu with these descriptions.
}
```

---

## 🛠️ Key Features

### 1. Smart Parameter Validation
Stop writing `if (args.length < 2)` or `try { Integer.parseInt(args[0]) }`.

*   **`@Min(value)` / `@Max(value)`**: Automatically validates numbers and sends a message to the user if they are out of range.
*   **`@Optional("default")`**: Makes a parameter optional. If the user doesn't provide it, the default value is automatically parsed and injected.

### 2. Automatic Type Conversion
Parameters are automatically converted from strings to:
*   `Player` / `OfflinePlayer`
*   `World`
*   `Material`
*   `Sound`
*   `EntityType`
*   `int`, `double`, `float`, `boolean`, `long`

### 3. "Did you mean...?" Suggestions
If a user mistypes a command (e.g., `/magic gfit`), EasyCommands calculates the Levenshtein distance and suggests the closest match:
> §cCommand not found. Did you mean /magic gift?

### 4. Permission-Aware Everything
*   **Help Filtering**: Users only see commands in the help menu that they have permission to use.
*   **Tab Completion Filtering**: Suggestions are only shown for commands the user can execute.

### 5. Modular Commands (`ShardableCommand`)
For large plugins, don't put everything in one class. Use `ShardableCommand` to register sub-commands from other classes.

**Main Class:**
```java
public class AdminCommand extends ShardableCommand {
    @Override public String getName() { return "admin"; }

    public AdminCommand() {
        RegisterSubCommandClass(new ReloadSubCommand());
        RegisterSubCommandClass(new MaintenanceSubCommand());
    }
}
```

**Sub-Class:**
```java
@SubCommand(name = "reload", permission = "admin.reload", description = "Reload the plugin")
public class ReloadSubCommand implements ICommand {
    // Implement required methods...
    
    @SubCommand(name = "config") // This becomes /admin reload config
    public void reloadConfig(CommandSender sender) {
        sender.sendMessage("§aConfig reloaded!");
    }
}
```

---

## 🎨 Customizing Messages
Want to change how the help menu or error messages look? Override the `MessageHandler`.

```java
public class MyCommand extends BaseCommand {
    public MyCommand() {
        getMessageHandler().setMessage(MessageKey.DID_YOU_MEAN, "§6[!] §7Unknown command. Try §e/%command% %suggestion%?");
        getMessageHandler().setMessage(MessageKey.HELP_HEADER, "§b--- Custom Help for /%command% ---");
        initialize();
    }
    // ...
}
```

## 💡 Best Practices
1.  **Always use `@Optional`** for trailing parameters that have sensible defaults.
2.  **Provide `description`** in your `@SubCommand` annotations to populate the automated help menu.
3.  **Use `ShardableCommand`** if your command has more than 5-10 sub-commands to keep your code clean.
4.  **Target `Player`** directly as the first parameter if the command is player-only; the framework will handle the "Only players can use this" check automatically if you set `senderType = SenderType.PLAYER`.

---

**Happy Coding!** 🚀
