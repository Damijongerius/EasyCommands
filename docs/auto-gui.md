# 🖼️ Auto-GUI Menus (0.3.1)

If your plugin has many subcommands, typing `/command help` to see a wall of text can become overwhelming. 

`EasyCommands` integrates seamlessly with **Triumph GUI** to automatically generate interactive, paginated, chest GUIs for your base command!

## 1. Enabling Auto-GUI

To enable this feature, your main command class must extend `ShardableCommand`. In your constructor, simply call `enableAutoGui()`.

```java
import com.dami.easyCommands.core.ShardableCommand;

public class MyPluginCommand extends ShardableCommand {

    public MyPluginCommand() {
        super();
        this.enableAutoGui(); // Replaces the default text fallback with a Chest GUI!
    }
    
    // ... register subcommands
}
```

Now, if a player types your base command (e.g., `/myplugin`) without any arguments, a Chest GUI opens automatically, displaying an item for every single registered subcommand they have permission to see.

### Layout Strategies
You can customize the layout of the GUI by passing a title and a `GuiLayout` strategy:
```java
this.enableAutoGui("§8⚙️ Admin Panel", GuiLayout.PADDED); 
// PADDED leaves an empty slot between every item! 
// Other options: DEFAULT, BORDER
```

## 2. Hardcoding Positions (`@GuiSlot`)
While layouts handle dynamic placing, you can force specific commands to always appear in exact slots using the `@GuiSlot` annotation!
```java
@SubCommand(commandPath = {}, name = "reload")
@GuiSlot(row = 1, col = 5) // Pins the item directly in the top-middle slot!
public void reloadCommand(Player player) { ... }
```

## 3. Customizing Icons (`@GuiIcon`)

By default, the framework assigns a consistent random ore or ingot material based on the command's name. To explicitly set the material icon for a specific subcommand, use the `@GuiIcon` annotation.

```java
import com.dami.easyCommands.annotations.GuiIcon;
import com.dami.easyCommands.annotations.SubCommand;
import org.bukkit.entity.Player;

@SubCommand(
    commandPath = {}, 
    name = "stats", 
    description = "View your player statistics" // The description becomes the item's Lore!
)
@GuiIcon(material = "DIAMOND_SWORD") // Sets the item in the GUI to a Diamond Sword
public void viewStats(Player player) {
    player.sendMessage("Your stats...");
}
```

## 4. Visual Locks (`@Require`)
If a command is protected by a `@Require` annotation, the GUI will test the condition before rendering. If the player fails the requirement, the item won't be hidden—instead, it transforms into a **BARRIER** block with a lore tag: `§c🔒 Locked: [Condition]`. This is a fantastic way to tease premium commands!

## 5. Interactive Argument Sub-Menus
What happens if a player clicks a command in the GUI that requires arguments (e.g. `/ban <player>`)? 
Instead of instantly throwing an error, the Auto-GUI engine automatically queries the `CompletionResolver`! 
If tab completions exist for that argument, it **seamlessly opens a brand new Sub-Menu GUI** filled with the completion options (like a list of online players) for the user to click.
If no completions exist, it falls back to the **Interactive Chat Prompt**, allowing them to type the missing arguments directly in chat!
