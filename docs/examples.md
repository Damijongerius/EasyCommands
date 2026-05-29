# 💡 Real-World Examples (0.3.1)

This page provides fully complete, real-world examples of how `EasyCommands` can be used to build complex systems instantly.

## Example 1: Economy `/pay` Command
Demonstrating `@Min`, primitives, and `@Confirm`.

```java
public class EconomyCommand extends BaseCommand {

    @Override
    public String getName() {
        return "pay";
    }

    @Override
    public void mainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§cUsage: /pay <player> <amount>");
    }

    @SubCommand(commandPath = {}, name = "send", permission = "economy.pay")
    @Confirm(timeout = 10)
    public void payPlayer(Player sender, Player target, @Min(1) double amount) {
        double balance = EconomyAPI.getBalance(sender);
        if (balance < amount) {
            sender.sendMessage("§cYou don't have enough money!");
            return;
        }

        EconomyAPI.withdraw(sender, amount);
        EconomyAPI.deposit(target, amount);

        sender.sendMessage("§aSent $" + amount + " to " + target.getName());
        target.sendMessage("§aReceived $" + amount + " from " + sender.getName());
    }
}
```

## Example 2: Administration Control Panel
Demonstrating Class-Based Sharding, `@Flag`, and `@Optional` with separated classes.

**AdminCommand.java**
```java
public class AdminCommand extends BaseCommand {

    public AdminCommand() {
        super();
        // Register the separated ServerAdmin command class
        RegisterSubCommandClass(new ServerAdmin());
    }

    @Override
    public String getName() { return "admin"; }

    @Override
    public void mainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§b--- Admin Control Panel ---");
    }
}
```

**ServerAdmin.java**
```java
@SubCommand(commandPath = {}, name = "server", permission = "admin.server")
public class ServerAdmin implements ICommand {

    @Override
    public String getName() { return "server"; }

    @Override
    public void mainCommand(CommandSender sender, String[] args) { }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return List.of("restart", "maintenance");
        return null;
    }

    // Sub-commands of /admin server
    
    @SubCommand(commandPath = {}, name = "restart")
    @Confirm(timeout = 15) // Extremely dangerous!
    public void restart(CommandSender sender, @Optional("10") int countdownSeconds) {
        Bukkit.broadcastMessage("§cServer restarting in " + countdownSeconds + " seconds!");
        // ... restart logic
    }

    @SubCommand(commandPath = {}, name = "maintenance")
    public void toggleMaintenance(CommandSender sender, @Flag("-silent") boolean silent) {
        boolean status = !MaintenanceAPI.isEnabled();
        MaintenanceAPI.setEnabled(status);
        
        if (!silent) {
            Bukkit.broadcastMessage("§eMaintenance mode is now: " + status);
        }
    }
}
```

## Example 3: Magic Wizard (Interactive Prompt)
Demonstrating `EasyCommands.prompt()` and `@Cooldown`.

```java
public class MagicCommand extends BaseCommand {

    @Override
    public String getName() { return "magic"; }

    @Override
    public void mainCommand(CommandSender sender, String[] args) { }

    @SubCommand(commandPath = {}, name = "cast")
    @Cooldown(seconds = 30) // Wait 30s between casts
    public void castSpell(Player player) {
        player.sendMessage("§dWhat spell do you wish to cast?");
        
        // Wait for them to type in chat
        EasyCommands.prompt(plugin, player, response -> {
            if (response.equalsIgnoreCase("fireball")) {
                player.launchProjectile(Fireball.class);
            } else {
                player.sendMessage("§cUnknown spell: " + response);
            }
        });
    }
}
```
