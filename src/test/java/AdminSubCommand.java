import com.dami.easyCommands.Annotations.SubCommandClass;
import com.dami.easyCommands.Command.SubCommandHandler;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * Example admin subcommand class.
 * This demonstrates how to create a class-based subcommand.
 */
@SubCommandClass(
    commandPath = {},
    name = "admin",
    permission = "mycommand.admin",
    weight = 10,
    maxArgs = 1
)
public class AdminSubCommand extends SubCommandHandler {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§e=== Admin Commands ===");
            sender.sendMessage("§7/admin reload - Reload configuration");
            sender.sendMessage("§7/admin kick <player> - Kick a player");
            sender.sendMessage("§7/admin ban <player> - Ban a player");
            return;
        }

        String subCommand = args[0];
        switch (subCommand.toLowerCase()) {
            case "reload":
                sender.sendMessage("§aReloading configuration...");
                break;
            case "kick":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /mycommand admin kick <player>");
                } else {
                    String playerName = args[1];
                    sender.sendMessage("§cKicking " + playerName + "...");
                }
                break;
            case "ban":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /mycommand admin ban <player>");
                } else {
                    String playerName = args[1];
                    sender.sendMessage("§cBanning " + playerName + "...");
                }
                break;
            default:
                sender.sendMessage("§cUnknown admin command: " + subCommand);
                break;
        }
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "kick", "ban", "mute");
        } else if (args.length == 2) {
            if ("kick".equals(args[0]) || "ban".equals(args[0])) {
                return Arrays.asList("Player1", "Player2", "Player3");
            }
        }
        return null;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("mycommand.admin");
    }

    @Override
    public int getMaxArgs() {
        return 2; // Allow up to 2 arguments
    }

    @Override
    public int getWeight() {
        return 10; // High priority
    }
}
