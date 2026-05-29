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

## 2. Customizing Icons (`@GuiIcon`)

By default, the framework assigns a random ore or ingot material (Diamond, Emerald, Redstone, etc.) to each subcommand. To explicitly set the material icon for a specific subcommand, use the `@GuiIcon` annotation.

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

### What Happens When Clicked?
When a player clicks the Diamond Sword in the chest menu, the framework instantly closes the GUI and executes `/myplugin stats` on behalf of the player!

*Note: The Auto-GUI engine automatically respects pagination. If you have more than 45 subcommands, "Next Page" and "Previous Page" arrows are automatically injected into the bottom corners of the chest.*
