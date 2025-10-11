import com.dami.easyCommands.Annotations.SubCommand;
import com.dami.easyCommands.Annotations.SubCommandTab;
import com.dami.easyCommands.Command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

/**
 * Example command class showing how to use the new @SubCommandTab annotation
 * for tab completion in your command framework.
 */
public class TabCompleteExample extends Command {
    
    public TabCompleteExample(Plugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "tabexample";
    }
    
    @Override
    public int maxArgs() {
        return 1;
    }
    
    @Override
    public void MainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§aTabExample main command executed!");
        if (args.length > 0) {
            sender.sendMessage("§7Arguments: " + String.join(" ", args));
        }
    }
    
    // SubCommand with basic tab completion
    @SubCommand(
        commandPath = {},
        name = "help",
        maxArgs = 0,
        permission = ""
    )
    public void helpCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§eHelp command executed!");
    }
    
    // Tab completion for the help command
    @SubCommandTab(
        commandPath = {},
        name = "help",
        permission = ""
    )
    public List<String> helpTabComplete(CommandSender sender, String[] args) {
        return Arrays.asList("general", "commands", "permissions");
    }
    
    // SubCommand with player name tab completion
    @SubCommand(
        commandPath = {},
        name = "info",
        maxArgs = 1,
        permission = "tabexample.info"
    )
    public void infoCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /tabexample info <player>");
            return;
        }
        
        String playerName = args[0];
        sender.sendMessage("§aPlayer info for: §f" + playerName);
    }
    
    // Tab completion for player names
    @SubCommandTab(
        commandPath = {},
        name = "info",
        permission = "tabexample.info"
    )
    public List<String> infoTabComplete(CommandSender sender, String[] args) {
        // In a real implementation, you would get online players
        return Arrays.asList("Player1", "Player2", "Player3", "AdminPlayer");
    }
    
    // SubCommand with message tab completion
    @SubCommand(
        commandPath = {},
        name = "broadcast",
        maxArgs = 10,
        permission = "tabexample.broadcast"
    )
    public void broadcastCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /tabexample broadcast <message>");
            return;
        }
        
        String message = String.join(" ", args);
        sender.sendMessage("§aBroadcasting: §f" + message);
    }
    
    // Tab completion for broadcast messages
    @SubCommandTab(
        commandPath = {},
        name = "broadcast",
        permission = "tabexample.broadcast"
    )
    public List<String> broadcastTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            // First argument - suggest common broadcast messages
            return Arrays.asList("Hello", "Welcome", "Server", "Maintenance", "Update");
        } else if (args.length == 2) {
            // Second argument - suggest additional words
            return Arrays.asList("to", "all", "players", "now", "soon");
        }
        
        return null; // No more suggestions
    }
    
    // Nested admin commands
    @SubCommand(
        commandPath = {"admin"},
        name = "reload",
        maxArgs = 0,
        permission = "tabexample.admin.reload"
    )
    public void adminReloadCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§aReloading configuration...");
    }
    
    // Tab completion for admin reload
    @SubCommandTab(
        commandPath = {"admin"},
        name = "reload",
        permission = "tabexample.admin.reload"
    )
    public List<String> adminReloadTabComplete(CommandSender sender, String[] args) {
        return Arrays.asList("config", "permissions", "all");
    }
    
    // Another admin command
    @SubCommand(
        commandPath = {"admin"},
        name = "kick",
        maxArgs = 2,
        permission = "tabexample.admin.kick"
    )
    public void adminKickCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /tabexample admin kick <player> <reason>");
            return;
        }
        
        String playerName = args[0];
        String reason = args[1];
        sender.sendMessage("§cKicking " + playerName + " for: " + reason);
    }
    
    // Tab completion for admin kick
    @SubCommandTab(
        commandPath = {"admin"},
        name = "kick",
        permission = "tabexample.admin.kick"
    )
    public List<String> adminKickTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            // First argument - player names
            return Arrays.asList("Player1", "Player2", "Player3");
        } else if (args.length == 2) {
            // Second argument - common kick reasons
            return Arrays.asList("griefing", "spamming", "cheating", "inappropriate", "other");
        }
        
        return null;
    }
    
    // Tab completion for the main admin command
    @SubCommandTab(
        commandPath = {},
        name = "admin",
        permission = "tabexample.admin"
    )
    public List<String> adminTabComplete(CommandSender sender, String[] args) {
        return Arrays.asList("reload", "kick", "ban", "mute");
    }
}
