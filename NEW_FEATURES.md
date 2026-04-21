# New Features in EasyCommands (v0.2.1+)

This document summarizes the new features and improvements added to the EasyCommands framework.

---

## 1. Advanced Parameter Injection
Subcommand methods no longer require the strict `(CommandSender sender, String[] args)` signature. You can now use specific types, and the framework will automatically parse the arguments.

### Supported Types:
- `CommandSender` (The sender)
- `Player` (The sender if they are a player, or a target player if it's an argument)
- `World` (Automatically looks up a world by name)
- `String`, `int`, `long`, `double`, `boolean` (Primitive parsing)
- `String[]` (Remaining arguments)

### Example:
```java
@SubCommand(name = "give")
public void giveItem(Player sender, Player target, int amount, String itemName) {
    sender.sendMessage("Giving " + amount + " " + itemName + " to " + target.getName());
}
```

---

## 2. Command Aliases & Metadata
The `@SubCommand` annotation now supports aliases, descriptions, and usage instructions for better command discovery.

- **`aliases`**: Alternative triggers for the command.
- **`description`**: What the command does (used in auto-help).
- **`usage`**: How to use the command (used in error messages).

### Example:
```java
@SubCommand(
    name = "teleport",
    aliases = {"tp", "goto"},
    description = "Teleport to another player",
    usage = "/root tp <player>"
)
public void tp(Player sender, Player target) {
    sender.teleport(target);
}
```

---

## 3. Automatic Help Generation
A built-in `help` system is now available for every `BaseCommand`. Users can type `/<command> help` to see a formatted list of all registered subcommands, their descriptions, and usage.

- **Customization**: You can customize the look of the help menu using the `MessageHandler`.

---

## 4. Smart Tab Completion
You can now specify tab completion providers directly on method parameters using the `@Tab` annotation.

### Built-in Providers:
- `@Tab("players")`: Suggets online players.
- `@Tab("worlds")`: Suggests loaded worlds.
- `@Tab("boolean")`: Suggests `true`/`false`.

### Example:
```java
@SubCommand(name = "setworld")
public void setWorld(Player sender, @Tab("worlds") World world, @Tab("boolean") boolean force) {
    // ...
}
```
*Note: If no @Tab is present, the framework tries to guess based on the type (e.g., Player defaults to "players").*

---

## 5. Customizable Message System
You can now override every system message (Permission denied, Command not found, etc.) by implementing a custom `MessageHandler`.

### How to use:
```java
BaseCommand myCommand = new MyCommand();
MessageHandler.DefaultMessageHandler handler = new MessageHandler.DefaultMessageHandler();

handler.setMessage(MessageKey.NO_PERMISSION, "§cStop! You can't do that!");
handler.setMessage(MessageKey.COMMAND_NOT_FOUND, "§7Unknown command. Try §e/help");

myCommand.setMessageHandler(handler);
```

---

## 6. Requirement-Based Execution
Easily restrict who can run a command using the `senderType` field.

- **`SenderType.ANY`**: Default.
- **`SenderType.PLAYER`**: Only players can execute.
- **`SenderType.CONSOLE`**: Only the console can execute.

### Example:
```java
@SubCommand(name = "spawn", senderType = SenderType.PLAYER)
public void spawn(Player player) {
    player.teleport(player.getWorld().getSpawnLocation());
}
```

---

## 7. Configuration Defaults
Annotations now have sensible defaults, making them less verbose:
- `commandPath` now defaults to `{}`.
- `name` now defaults to `""`.
- `senderType` defaults to `ANY`.

---

### Testing your new commands tomorrow:
1. Ensure your subcommands use the new parameter types.
2. Add some aliases and descriptions.
3. Try typing `/<command> help` in-game.
4. Try typing `/<command> <tab>` to see the new completions!
