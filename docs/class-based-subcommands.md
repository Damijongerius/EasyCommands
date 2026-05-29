# 🧩 Class-Based Subcommands (0.3.1)

If your plugin is large, keeping all your command logic inside a single `BaseCommand` class will turn it into a disorganized mess. 

`EasyCommands` solves this by allowing you to shard your commands into completely separated classes! 

## 1. Creating the Main Command
First, create your root command (e.g., `/myplugin`). This class extends `BaseCommand` and serves as the entry point. Inside its constructor, you will register your external sub-command classes.

```java
import com.dami.easyCommands.core.BaseCommand;
import org.bukkit.command.CommandSender;

public class MyPluginCommand extends BaseCommand {

    public MyPluginCommand() {
        super();
        
        // Register your completely separated sub-command classes here!
        RegisterSubCommandClass(new AdminCommand());
        RegisterSubCommandClass(new EconomyCommand());
    }

    @Override
    public String getName() {
        return "myplugin"; // This is your root command: /myplugin
    }

    // Executed when a player types exactly: /myplugin
    @Override
    public void mainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("Welcome to MyPlugin!");
    }
}
```

## 2. Creating Separated Sub-Command Classes
Now, in a completely separate file, you can define your sub-command logic. Instead of extending `BaseCommand`, this separated class must implement `ICommand` and be annotated with `@SubCommand`.

```java
import com.dami.easyCommands.annotations.SubCommand;
import com.dami.easyCommands.core.ICommand;
import org.bukkit.command.CommandSender;
import java.util.List;

// This maps the entire separated class to the /myplugin admin path
@SubCommand(commandPath = {}, name = "admin", permission = "myplugin.admin")
public class AdminCommand implements ICommand {

    @Override
    public String getName() {
        return "admin"; 
    }

    // Executed when a player types exactly: /myplugin admin
    @Override
    public void mainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("Admin control panel.");
    }

    // Tab completions specifically for /myplugin admin
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return List.of("reload", "kick");
        return null;
    }

    // --- SUB-COMMANDS FOR /myplugin admin ---

    // Executed when a player types: /myplugin admin reload
    // Notice how commandPath is empty because the class itself is already mapped to "admin"
    @SubCommand(commandPath = {}, name = "reload")
    public void reloadPlugin(CommandSender sender) {
        sender.sendMessage("Plugin reloaded successfully!");
    }
    
    // Executed when a player types: /myplugin admin kick <player>
    @SubCommand(commandPath = {}, name = "kick")
    public void kickPlayer(CommandSender sender, org.bukkit.entity.Player target) {
        target.kickPlayer("You have been kicked by an admin!");
    }
}
```

## 3. Summary
By splitting your commands into separated classes, you keep your project organized. 
1. `MyPluginCommand` handles the root `/myplugin`.
2. `AdminCommand` (in a separate file) handles `/myplugin admin` and all of its methods.
3. `EconomyCommand` (in a separate file) handles `/myplugin economy` and all of its methods.

You can create as many separated `ICommand` classes as you need, keeping your command system perfectly modular and easy to read!
