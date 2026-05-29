# 🔄 Tab Completion (0.3.1)

Nobody likes guessing commands. `EasyCommands` provides one of the most intelligent tab completion systems available for Bukkit.

## 1. Automatic Type Completion

You don't need to write any code for standard Bukkit objects! If your method parameter requires a specific type, `EasyCommands` will automatically provide tab suggestions for it dynamically based on the parameter's class type.

### Example: Spawning Mobs (Enums)
When using an enum like `EntityType`, the framework automatically lists all possible enum values.
```java
@SubCommand(commandPath = {}, name = "spawn")
public void spawnMob(Player player, EntityType type) {
    // Typing /myplugin spawn [tab] -> ZOMBIE, SKELETON, CREEPER, etc.
}
```

### Example: Finding Players & Offline Players
When resolving `Player`, it queries all currently online players. When resolving `OfflinePlayer`, it queries all historically known players!
```java
@SubCommand(commandPath = {}, name = "ban")
public void banPlayer(Player player, OfflinePlayer target) {
    // Typing /myplugin ban [tab] -> Suggests every player who has ever joined the server!
}

@SubCommand(commandPath = {}, name = "teleport")
public void teleportPlayer(Player player, Player target) {
    // Typing /myplugin teleport [tab] -> Suggests ONLY currently online players!
}
```

### Example: World, Material, and Sound
Bukkit registries are automatically hooked into the completion engine.
```java
@SubCommand(commandPath = {}, name = "give")
public void giveItem(Player player, Material material) {
    // Typing /myplugin give [tab] -> DIAMOND_SWORD, DIRT, STONE, etc.
}

@SubCommand(commandPath = {}, name = "goto")
public void gotoWorld(Player player, World world) {
    // Typing /myplugin goto [tab] -> world, world_nether, world_the_end
}

@SubCommand(commandPath = {}, name = "play")
public void playSound(Player player, Sound sound) {
    // Typing /myplugin play [tab] -> ENTITY_EXPERIENCE_ORB_PICKUP, etc.
}
```

### Example: Primitives
Even booleans automatically suggest `true` and `false`!
```java
@SubCommand(commandPath = {}, name = "god")
public void godMode(Player player, boolean enable) {
    // Typing /myplugin god [tab] -> true, false
}
```

## 2. Dynamic Wildcard Completions

Sometimes you have commands with dynamic arguments (e.g., `/myplugin info <player>`).
In this case, the `Player` is automatically suggested!

But what if you want to tab-complete a subcommand AFTER the wildcard?
e.g. `/myplugin info Notch stats` or `/myplugin info Notch inventory`

`EasyCommands` handles wildcard pathways naturally.
```java
@SubCommand(commandPath = {"info", "*"}, name = "stats")
public void viewStats(Player sender, Player target) {
    // target is automatically captured by the wildcard!
}
```

## 3. Global Reusable Completions (The Best Way)

If you have a dynamic list of things (like Guilds, Kits, or Custom Enchants) that you want to tab-complete across many different commands, you shouldn't have to write the same logic 100 times!

`EasyCommands` allows you to register a completion logic block **once** and reuse it everywhere via the `@SubCommand(completions = {...})` attribute.

### Registering the Completion List
In your plugin's `onEnable()` method (or wherever you initialize), register the logic with `EasyCommands.registerCompletion`:

```java
public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // Register a dynamic list of Guilds
        EasyCommands.registerCompletion("guilds", (sender, args) -> {
            return GuildManager.getAllGuildNames();
        });
        
        // Register a list of custom Kits
        EasyCommands.registerCompletion("kits", (sender, args) -> {
            return List.of("starter", "vip", "warrior");
        });
    }
}
```

### Using the Registered Completions

Now, you can automatically inject these lists into *any* command using the `@AutoComplete` annotation directly on the parameter! This is much cleaner than using the legacy `completions` array in `@SubCommand`.

```java
@SubCommand(commandPath = {}, name = "join")
public void joinGuild(Player player, @AutoComplete("guilds") String guildName) {
    player.sendMessage("Joining " + guildName);
}

@SubCommand(commandPath = {}, name = "invite")
public void inviteToGuild(
    Player player, 
    Player target, // Argument 1: Online Players (handled automatically!)
    @AutoComplete("guilds") String guildName // Argument 2: Custom "guilds" list
) {
    target.sendMessage("You were invited to " + guildName);
}
```
*(Notice how the framework intelligently maps the autocomplete registry key directly to the parameter position, bypassing `@Sender` and `@Flag` arguments automatically!)*

## 4. Manual Tab Completions

If you need a highly specific completion that you won't reuse anywhere else, you can define it directly in your command class.

### For the Root Command
```java
public class MyCommand extends BaseCommand {
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (args.length == 1) return List.of("custom_1", "custom_2");
        return null;
    }
}
```

### For Class-Based Sub-Commands
If you are using separated classes, implement the `tabComplete` method from the `ICommand` interface!

```java
@SubCommand(commandPath = {}, name = "admin", permission = "myplugin.admin")
public class AdminCommand implements ICommand {
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return List.of("reload", "restart", "stop");
        return null;
    }
}
```
