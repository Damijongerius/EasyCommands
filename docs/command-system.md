# ⚡ Command System (0.3.1)

The `EasyCommands` framework operates through a highly intelligent `BaseCommand` which intercepts standard Bukkit commands and routes them to your annotated Java methods. 

Say goodbye to massive `if-else` blocks and manually casting arguments!

## 1. Creating a Base Command

Your root command (e.g. `/myplugin`) should extend `BaseCommand`.

```java
import com.dami.easyCommands.core.BaseCommand;
import org.bukkit.command.CommandSender;

public class MyPluginCommand extends BaseCommand {

    @Override
    public String getName() {
        return "myplugin"; // Matches the plugin.yml command
    }

    // Executed when a player simply types /myplugin
    @Override
    public void mainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§aWelcome to MyPlugin!");
    }
}
```

## 2. Registering Sub-Commands

Instead of nesting logic, you use the `@SubCommand` annotation. The framework automatically parses everything!

```java
@SubCommand(
    commandPath = {}, 
    name = "give", 
    permission = "myplugin.give", 
    description = "Give yourself an item."
)
public void giveCommand(Player player, Material material, int amount) {
    player.getInventory().addItem(new ItemStack(material, amount));
    player.sendMessage("You received " + amount + " " + material.name());
}
```

### Auto-Resolving Parameters 🧩
Notice how we didn't use `String[] args`? The framework does the heavy lifting:
* It ensures the sender is a `Player`. If console runs it, they get a nicely formatted error message!
* It ensures the `Material` is valid. If the user types `/myplugin give APPLEx 5`, they get a "Invalid argument" message.
* It ensures the `amount` is an integer.

## 3. Command Modifiers

EasyCommands 0.3.1 comes packed with powerful modifiers to protect and enhance your commands.

### Cooldowns (`@Cooldown`) ⏳
```java
@SubCommand(commandPath = {}, name = "heal")
@Cooldown(seconds = 60, bypassPermission = "myplugin.bypass.heal")
public void healPlayer(Player player) {
    player.setHealth(20.0);
    player.sendMessage("You have been healed!");
}
```

### Flags (`@Flag`) 🚩
Extract flags (like `-force` or `-silent`) from *anywhere* in the command string!
```java
@SubCommand(commandPath = {}, name = "ban")
public void banPlayer(CommandSender sender, Player target, @Flag("-silent") boolean silent) {
    target.kickPlayer("You have been banned!");
    if (!silent) {
        Bukkit.broadcastMessage(target.getName() + " was banned!");
    }
}
```

### Asynchronous Execution (`@Async`) 🚀
Never block the main thread when hitting a database!
```java
@SubCommand(commandPath = {}, name = "stats")
@Async
public void checkStats(Player player) {
    // This runs on a separate worker thread automatically!
    int kills = database.getKills(player.getUniqueId());
    player.sendMessage("Kills: " + kills);
}
```

### Confirmations (`@Confirm`) 🔒
Protect dangerous commands from accidental misclicks. The player is required to run the exact same command again to confirm.
```java
@SubCommand(commandPath = {}, name = "disband")
@Confirm(timeout = 10)
public void disbandFaction(Player player) {
    player.sendMessage("Your faction is gone!");
}
```

### Requirements (`@Require`) 🛡️
Safely intercept commands before they ever execute.
```java
@SubCommand(commandPath = {}, name = "fly")
@Require("has_vip")
public void flyCommand(Player player) {
    player.setAllowFlight(true);
}
```
*(See the API Reference for how to register your Custom Conditions!)*

## 4. Paginated Help Menus 📚

If you don't define a `help` sub-command, `EasyCommands` automatically creates one for you! 

Typing `/myplugin help` generates an interactive, clickable Kyori MiniMessage menu showing the descriptions of all your commands. It even paginates automatically if you have more than 7 commands!
