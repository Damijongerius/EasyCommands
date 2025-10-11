import com.dami.easyCommands.Command.Command;
import com.dami.easyCommands.Command.SubCommandHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

/**
 * Example main command class that demonstrates how to register and use
 * class-based subcommands in your command framework.
 */
public class MainCommandExample extends Command {
    
    public MainCommandExample(Plugin plugin) {
        super(plugin);
        
        // Register subcommand classes
        registerSubCommandClass(AdminSubCommand.class);
        registerSubCommandClass(PlayerSubCommand.class);
        registerSubCommandClass(ModerationSubCommand.class);
        
        // You can also register with custom parameters
        // registerSubCommandClass(CustomSubCommand.class, 
        //     new String[]{"custom"}, "command", "mycommand.custom", 5, 2);
    }
    
    @Override
    public String getName() {
        return "mycommand";
    }
    
    @Override
    public int maxArgs() {
        return 1;
    }
    
    @Override
    public void MainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§aMyCommand main command executed!");
        sender.sendMessage("§7Available subcommands: admin, player");
        sender.sendMessage("§7Use /mycommand <subcommand> for more options");
        
        if (args.length > 0) {
            sender.sendMessage("§7Arguments: " + String.join(" ", args));
        }
    }
    
    // You can still use regular @SubCommand methods alongside class-based subcommands
    @com.dami.easyCommands.Annotations.SubCommand(
        commandPath = {},
        name = "help",
        maxArgs = 0,
        permission = ""
    )
    public void helpCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§e=== MyCommand Help ===");
        sender.sendMessage("§7/mycommand - Main command");
        sender.sendMessage("§7/mycommand help - Show this help");
        sender.sendMessage("§7/mycommand admin - Admin commands");
        sender.sendMessage("§7/mycommand player - Player commands");
        sender.sendMessage("§7/mycommand admin moderation - Moderation commands");
    }
    
    @com.dami.easyCommands.Annotations.SubCommand(
        commandPath = {},
        name = "info",
        maxArgs = 1,
        permission = "mycommand.info"
    )
    public void infoCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /mycommand info <player>");
            return;
        }
        
        String playerName = args[0];
        sender.sendMessage("§aPlayer info for: §f" + playerName);
    }
    
    // Tab completion for the main command
    @com.dami.easyCommands.Annotations.SubCommandTab(
        commandPath = {},
        name = "help",
        permission = ""
    )
    public List<String> helpTabComplete(CommandSender sender, String[] args) {
        return Arrays.asList("general", "commands", "permissions");
    }
    
    @com.dami.easyCommands.Annotations.SubCommandTab(
        commandPath = {},
        name = "info",
        permission = "mycommand.info"
    )
    public List<String> infoTabComplete(CommandSender sender, String[] args) {
        return Arrays.asList("Player1", "Player2", "Player3");
    }
}
