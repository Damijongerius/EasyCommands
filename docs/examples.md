# Examples

This document provides comprehensive examples of how to use the EasyCommands framework in real-world scenarios.

## Table of Contents

- [Basic Examples](#basic-examples)
- [Advanced Examples](#advanced-examples)
- [Real-World Examples](#real-world-examples)
- [Best Practice Examples](#best-practice-examples)

## Basic Examples

### Simple Help Command

```java
public class HelpCommand extends Command {
    
    public HelpCommand(Plugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "help";
    }
    
    @Override
    public int maxArgs() {
        return 1;
    }
    
    @Override
    public void MainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§e=== Help ===");
        sender.sendMessage("§7/help - Show this help");
        sender.sendMessage("§7/help <topic> - Show specific help");
    }
    
    @SubCommand(
        commandPath = {},
        name = "general",
        maxArgs = 0,
        permission = ""
    )
    public void generalHelpCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§e=== General Help ===");
        sender.sendMessage("§7This is general help information");
    }
    
    @SubCommand(
        commandPath = {},
        name = "commands",
        maxArgs = 0,
        permission = ""
    )
    public void commandsHelpCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§e=== Commands Help ===");
        sender.sendMessage("§7Available commands:");
        sender.sendMessage("§7- /help - Show help");
        sender.sendMessage("§7- /info - Get server info");
    }
    
    @SubCommandTab(
        commandPath = {},
        name = "general",
        permission = ""
    )
    public List<String> generalHelpTabComplete(CommandSender sender, String[] args) {
        return Arrays.asList("general", "commands", "permissions");
    }
}
```

### Player Information Command

```java
public class PlayerInfoCommand extends Command {
    
    public PlayerInfoCommand(Plugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "playerinfo";
    }
    
    @Override
    public int maxArgs() {
        return 1;
    }
    
    @Override
    public void MainCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /playerinfo <player>");
            return;
        }
        
        String playerName = args[0];
        Player player = Bukkit.getPlayer(playerName);
        
        if (player == null) {
            sender.sendMessage("§cPlayer not found: " + playerName);
            return;
        }
        
        sender.sendMessage("§a=== Player Info ===");
        sender.sendMessage("§7Name: §f" + player.getName());
        sender.sendMessage("§7Health: §f" + player.getHealth() + "/" + player.getMaxHealth());
        sender.sendMessage("§7Location: §f" + player.getLocation().getWorld().getName());
    }
    
    @SubCommandTab(
        commandPath = {},
        name = "playerinfo",
        permission = ""
    )
    public List<String> playerInfoTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        }
        return null;
    }
}
```

## Advanced Examples

### Admin Command System

```java
public class AdminCommand extends Command {
    
    public AdminCommand(Plugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "admin";
    }
    
    @Override
    public int maxArgs() {
        return 2;
    }
    
    @Override
    public void MainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§e=== Admin Commands ===");
        sender.sendMessage("§7/admin reload - Reload configuration");
        sender.sendMessage("§7/admin kick <player> - Kick a player");
        sender.sendMessage("§7/admin ban <player> - Ban a player");
        sender.sendMessage("§7/admin moderation - Moderation commands");
    }
    
    @SubCommand(
        commandPath = {},
        name = "reload",
        maxArgs = 0,
        permission = "admin.reload"
    )
    public void reloadCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§aReloading configuration...");
        // Your reload logic here
        sender.sendMessage("§aConfiguration reloaded successfully!");
    }
    
    @SubCommand(
        commandPath = {},
        name = "kick",
        maxArgs = 2,
        permission = "admin.kick"
    )
    public void kickCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /admin kick <player> <reason>");
            return;
        }
        
        String playerName = args[0];
        String reason = args[1];
        
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage("§cPlayer not found: " + playerName);
            return;
        }
        
        player.kickPlayer("§cYou have been kicked by " + sender.getName() + "\n§7Reason: " + reason);
        sender.sendMessage("§aKicked " + playerName + " for: " + reason);
    }
    
    @SubCommand(
        commandPath = {},
        name = "ban",
        maxArgs = 2,
        permission = "admin.ban"
    )
    public void banCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /admin ban <player> <reason>");
            return;
        }
        
        String playerName = args[0];
        String reason = args[1];
        
        // Your ban logic here
        sender.sendMessage("§aBanned " + playerName + " for: " + reason);
    }
    
    @SubCommand(
        commandPath = {},
        name = "moderation",
        maxArgs = 0,
        permission = "admin.moderation"
    )
    public void moderationCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§e=== Moderation Commands ===");
        sender.sendMessage("§7/admin moderation warn <player> <reason>");
        sender.sendMessage("§7/admin moderation mute <player> <duration>");
        sender.sendMessage("§7/admin moderation unmute <player>");
    }
    
    @SubCommandTab(
        commandPath = {},
        name = "kick",
        permission = "admin.kick"
    )
    public List<String> kickTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        } else if (args.length == 2) {
            return Arrays.asList("griefing", "spamming", "cheating", "inappropriate");
        }
        return null;
    }
    
    @SubCommandTab(
        commandPath = {},
        name = "ban",
        permission = "admin.ban"
    )
    public List<String> banTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        } else if (args.length == 2) {
            return Arrays.asList("griefing", "spamming", "cheating", "inappropriate");
        }
        return null;
    }
}
```

### Teleport Command System

```java
public class TeleportCommand extends Command {
    
    public TeleportCommand(Plugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "tp";
    }
    
    @Override
    public int maxArgs() {
        return 2;
    }
    
    @Override
    public void MainCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /tp <player> [target]");
            return;
        }
        
        String playerName = args[0];
        String targetName = args.length > 1 ? args[1] : null;
        
        if (targetName == null) {
            // Teleport sender to player
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cConsole cannot teleport to players");
                return;
            }
            
            Player target = Bukkit.getPlayer(playerName);
            if (target == null) {
                sender.sendMessage("§cPlayer not found: " + playerName);
                return;
            }
            
            ((Player) sender).teleport(target);
            sender.sendMessage("§aTeleported to " + playerName);
        } else {
            // Teleport player to target
            Player player = Bukkit.getPlayer(playerName);
            Player target = Bukkit.getPlayer(targetName);
            
            if (player == null) {
                sender.sendMessage("§cPlayer not found: " + playerName);
                return;
            }
            
            if (target == null) {
                sender.sendMessage("§cTarget player not found: " + targetName);
                return;
            }
            
            player.teleport(target);
            sender.sendMessage("§aTeleported " + playerName + " to " + targetName);
        }
    }
    
    @SubCommand(
        commandPath = {},
        name = "here",
        maxArgs = 1,
        permission = "tp.here"
    )
    public void teleportHereCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cConsole cannot use this command");
            return;
        }
        
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /tp here <player>");
            return;
        }
        
        String playerName = args[0];
        Player player = Bukkit.getPlayer(playerName);
        
        if (player == null) {
            sender.sendMessage("§cPlayer not found: " + playerName);
            return;
        }
        
        player.teleport(((Player) sender).getLocation());
        sender.sendMessage("§aTeleported " + playerName + " to you");
    }
    
    @SubCommandTab(
        commandPath = {},
        name = "tp",
        permission = "tp"
    )
    public List<String> teleportTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        } else if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        }
        return null;
    }
    
    @SubCommandTab(
        commandPath = {},
        name = "here",
        permission = "tp.here"
    )
    public List<String> teleportHereTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        }
        return null;
    }
}
```

## Real-World Examples

### Plugin Management System

```java
public class PluginManagerCommand extends Command {
    
    public PluginManagerCommand(Plugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "pluginmanager";
    }
    
    @Override
    public int maxArgs() {
        return 2;
    }
    
    @Override
    public void MainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§e=== Plugin Manager ===");
        sender.sendMessage("§7/pluginmanager list - List all plugins");
        sender.sendMessage("§7/pluginmanager enable <plugin> - Enable a plugin");
        sender.sendMessage("§7/pluginmanager disable <plugin> - Disable a plugin");
        sender.sendMessage("§7/pluginmanager reload <plugin> - Reload a plugin");
    }
    
    @SubCommand(
        commandPath = {},
        name = "list",
        maxArgs = 0,
        permission = "pluginmanager.list"
    )
    public void listPluginsCommand(CommandSender sender, String[] args) {
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        
        sender.sendMessage("§e=== Installed Plugins ===");
        for (Plugin plugin : plugins) {
            String status = plugin.isEnabled() ? "§aEnabled" : "§cDisabled";
            sender.sendMessage("§7" + plugin.getName() + " - " + status);
        }
    }
    
    @SubCommand(
        commandPath = {},
        name = "enable",
        maxArgs = 1,
        permission = "pluginmanager.enable"
    )
    public void enablePluginCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /pluginmanager enable <plugin>");
            return;
        }
        
        String pluginName = args[0];
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        
        if (plugin == null) {
            sender.sendMessage("§cPlugin not found: " + pluginName);
            return;
        }
        
        if (plugin.isEnabled()) {
            sender.sendMessage("§cPlugin is already enabled: " + pluginName);
            return;
        }
        
        // Your enable logic here
        sender.sendMessage("§aEnabled plugin: " + pluginName);
    }
    
    @SubCommand(
        commandPath = {},
        name = "disable",
        maxArgs = 1,
        permission = "pluginmanager.disable"
    )
    public void disablePluginCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /pluginmanager disable <plugin>");
            return;
        }
        
        String pluginName = args[0];
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        
        if (plugin == null) {
            sender.sendMessage("§cPlugin not found: " + pluginName);
            return;
        }
        
        if (!plugin.isEnabled()) {
            sender.sendMessage("§cPlugin is already disabled: " + pluginName);
            return;
        }
        
        // Your disable logic here
        sender.sendMessage("§aDisabled plugin: " + pluginName);
    }
    
    @SubCommandTab(
        commandPath = {},
        name = "enable",
        permission = "pluginmanager.enable"
    )
    public List<String> enablePluginTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(plugin -> !plugin.isEnabled())
                .map(Plugin::getName)
                .collect(Collectors.toList());
        }
        return null;
    }
    
    @SubCommandTab(
        commandPath = {},
        name = "disable",
        permission = "pluginmanager.disable"
    )
    public List<String> disablePluginTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(Plugin::isEnabled)
                .map(Plugin::getName)
                .collect(Collectors.toList());
        }
        return null;
    }
}
```

### World Management System

```java
public class WorldManagerCommand extends Command {
    
    public WorldManagerCommand(Plugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "worldmanager";
    }
    
    @Override
    public int maxArgs() {
        return 2;
    }
    
    @Override
    public void MainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§e=== World Manager ===");
        sender.sendMessage("§7/worldmanager list - List all worlds");
        sender.sendMessage("§7/worldmanager create <name> - Create a new world");
        sender.sendMessage("§7/worldmanager delete <name> - Delete a world");
        sender.sendMessage("§7/worldmanager teleport <name> - Teleport to a world");
    }
    
    @SubCommand(
        commandPath = {},
        name = "list",
        maxArgs = 0,
        permission = "worldmanager.list"
    )
    public void listWorldsCommand(CommandSender sender, String[] args) {
        List<World> worlds = Bukkit.getWorlds();
        
        sender.sendMessage("§e=== Available Worlds ===");
        for (World world : worlds) {
            int playerCount = world.getPlayers().size();
            sender.sendMessage("§7" + world.getName() + " - §f" + playerCount + " players");
        }
    }
    
    @SubCommand(
        commandPath = {},
        name = "create",
        maxArgs = 1,
        permission = "worldmanager.create"
    )
    public void createWorldCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /worldmanager create <name>");
            return;
        }
        
        String worldName = args[0];
        
        if (Bukkit.getWorld(worldName) != null) {
            sender.sendMessage("§cWorld already exists: " + worldName);
            return;
        }
        
        // Your world creation logic here
        sender.sendMessage("§aCreated world: " + worldName);
    }
    
    @SubCommand(
        commandPath = {},
        name = "delete",
        maxArgs = 1,
        permission = "worldmanager.delete"
    )
    public void deleteWorldCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /worldmanager delete <name>");
            return;
        }
        
        String worldName = args[0];
        World world = Bukkit.getWorld(worldName);
        
        if (world == null) {
            sender.sendMessage("§cWorld not found: " + worldName);
            return;
        }
        
        // Your world deletion logic here
        sender.sendMessage("§aDeleted world: " + worldName);
    }
    
    @SubCommand(
        commandPath = {},
        name = "teleport",
        maxArgs = 1,
        permission = "worldmanager.teleport"
    )
    public void teleportWorldCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cConsole cannot teleport to worlds");
            return;
        }
        
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /worldmanager teleport <name>");
            return;
        }
        
        String worldName = args[0];
        World world = Bukkit.getWorld(worldName);
        
        if (world == null) {
            sender.sendMessage("§cWorld not found: " + worldName);
            return;
        }
        
        ((Player) sender).teleport(world.getSpawnLocation());
        sender.sendMessage("§aTeleported to world: " + worldName);
    }
    
    @SubCommandTab(
        commandPath = {},
        name = "create",
        permission = "worldmanager.create"
    )
    public List<String> createWorldTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("world1", "world2", "world3");
        }
        return null;
    }
    
    @SubCommandTab(
        commandPath = {},
        name = "delete",
        permission = "worldmanager.delete"
    )
    public List<String> deleteWorldTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getWorlds().stream()
                .map(World::getName)
                .collect(Collectors.toList());
        }
        return null;
    }
    
    @SubCommandTab(
        commandPath = {},
        name = "teleport",
        permission = "worldmanager.teleport"
    )
    public List<String> teleportWorldTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getWorlds().stream()
                .map(World::getName)
                .collect(Collectors.toList());
        }
        return null;
    }
}
```

## Best Practice Examples

### Configuration Management

```java
public class ConfigCommand extends Command {
    
    public ConfigCommand(Plugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "config";
    }
    
    @Override
    public int maxArgs() {
        return 3;
    }
    
    @Override
    public void MainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§e=== Configuration Manager ===");
        sender.sendMessage("§7/config get <key> - Get a configuration value");
        sender.sendMessage("§7/config set <key> <value> - Set a configuration value");
        sender.sendMessage("§7/config reload - Reload configuration");
    }
    
    @SubCommand(
        commandPath = {},
        name = "get",
        maxArgs = 1,
        permission = "config.get"
    )
    public void getConfigCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /config get <key>");
            return;
        }
        
        String key = args[0];
        Object value = getConfig().get(key);
        
        if (value == null) {
            sender.sendMessage("§cConfiguration key not found: " + key);
            return;
        }
        
        sender.sendMessage("§a" + key + " = " + value);
    }
    
    @SubCommand(
        commandPath = {},
        name = "set",
        maxArgs = 2,
        permission = "config.set"
    )
    public void setConfigCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /config set <key> <value>");
            return;
        }
        
        String key = args[0];
        String value = args[1];
        
        getConfig().set(key, value);
        saveConfig();
        
        sender.sendMessage("§aSet " + key + " = " + value);
    }
    
    @SubCommand(
        commandPath = {},
        name = "reload",
        maxArgs = 0,
        permission = "config.reload"
    )
    public void reloadConfigCommand(CommandSender sender, String[] args) {
        reloadConfig();
        sender.sendMessage("§aConfiguration reloaded!");
    }
    
    @SubCommandTab(
        commandPath = {},
        name = "get",
        permission = "config.get"
    )
    public List<String> getConfigTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return getConfig().getKeys(false).stream()
                .collect(Collectors.toList());
        }
        return null;
    }
    
    @SubCommandTab(
        commandPath = {},
        name = "set",
        permission = "config.set"
    )
    public List<String> setConfigTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return getConfig().getKeys(false).stream()
                .collect(Collectors.toList());
        } else if (args.length == 2) {
            String key = args[0];
            Object value = getConfig().get(key);
            if (value != null) {
                return Arrays.asList(value.toString());
            }
        }
        return null;
    }
}
```

### Permission Management

```java
public class PermissionCommand extends Command {
    
    public PermissionCommand(Plugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "permission";
    }
    
    @Override
    public int maxArgs() {
        return 3;
    }
    
    @Override
    public void MainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§e=== Permission Manager ===");
        sender.sendMessage("§7/permission set <player> <permission> - Set a permission");
        sender.sendMessage("§7/permission remove <player> <permission> - Remove a permission");
        sender.sendMessage("§7/permission check <player> <permission> - Check a permission");
    }
    
    @SubCommand(
        commandPath = {},
        name = "set",
        maxArgs = 2,
        permission = "permission.set"
    )
    public void setPermissionCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /permission set <player> <permission>");
            return;
        }
        
        String playerName = args[0];
        String permission = args[1];
        
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage("§cPlayer not found: " + playerName);
            return;
        }
        
        // Your permission setting logic here
        sender.sendMessage("§aSet permission " + permission + " for " + playerName);
    }
    
    @SubCommand(
        commandPath = {},
        name = "remove",
        maxArgs = 2,
        permission = "permission.remove"
    )
    public void removePermissionCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /permission remove <player> <permission>");
            return;
        }
        
        String playerName = args[0];
        String permission = args[1];
        
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage("§cPlayer not found: " + playerName);
            return;
        }
        
        // Your permission removal logic here
        sender.sendMessage("§aRemoved permission " + permission + " from " + playerName);
    }
    
    @SubCommand(
        commandPath = {},
        name = "check",
        maxArgs = 2,
        permission = "permission.check"
    )
    public void checkPermissionCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /permission check <player> <permission>");
            return;
        }
        
        String playerName = args[0];
        String permission = args[1];
        
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage("§cPlayer not found: " + playerName);
            return;
        }
        
        boolean hasPermission = player.hasPermission(permission);
        String status = hasPermission ? "§aYes" : "§cNo";
        sender.sendMessage("§7" + playerName + " has " + permission + ": " + status);
    }
    
    @SubCommandTab(
        commandPath = {},
        name = "set",
        permission = "permission.set"
    )
    public List<String> setPermissionTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        } else if (args.length == 2) {
            return Arrays.asList("myplugin.admin", "myplugin.player", "myplugin.moderation");
        }
        return null;
    }
    
    @SubCommandTab(
        commandPath = {},
        name = "remove",
        permission = "permission.remove"
    )
    public List<String> removePermissionTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        } else if (args.length == 2) {
            return Arrays.asList("myplugin.admin", "myplugin.player", "myplugin.moderation");
        }
        return null;
    }
    
    @SubCommandTab(
        commandPath = {},
        name = "check",
        permission = "permission.check"
    )
    public List<String> checkPermissionTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        } else if (args.length == 2) {
            return Arrays.asList("myplugin.admin", "myplugin.player", "myplugin.moderation");
        }
        return null;
    }
}
```

---

**These examples demonstrate the power and flexibility of the EasyCommands framework for creating professional-grade command systems!** 🚀
