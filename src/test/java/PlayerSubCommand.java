import com.dami.easyCommands.Annotations.SubCommandClass;
import com.dami.easyCommands.Command.SubCommandHandler;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * Example player subcommand class.
 * This demonstrates how to create a class-based subcommand for player-related commands.
 */
@SubCommandClass(
    commandPath = {},
    name = "player",
    permission = "mycommand.player",
    weight = 5,
    maxArgs = 2
)
public class PlayerSubCommand extends SubCommandHandler {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§e=== Player Commands ===");
            sender.sendMessage("§7/player info <player> - Get player information");
            sender.sendMessage("§7/player list - List online players");
            sender.sendMessage("§7/player teleport <player> - Teleport to player");
            return;
        }

        String subCommand = args[0];
        switch (subCommand.toLowerCase()) {
            case "info":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /mycommand player info <player>");
                } else {
                    String playerName = args[1];
                    sender.sendMessage("§aPlayer info for: §f" + playerName);
                    sender.sendMessage("§7This is a mock implementation");
                }
                break;
            case "list":
                sender.sendMessage("§aOnline players: §fPlayer1, Player2, Player3");
                break;
            case "teleport":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /mycommand player teleport <player>");
                } else {
                    String playerName = args[1];
                    sender.sendMessage("§aTeleporting to: §f" + playerName);
                }
                break;
            default:
                sender.sendMessage("§cUnknown player command: " + subCommand);
                break;
        }
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("info", "list", "teleport");
        } else if (args.length == 2) {
            if ("info".equals(args[0]) || "teleport".equals(args[0])) {
                return Arrays.asList("Player1", "Player2", "Player3", "AdminPlayer");
            }
        }
        return null;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("mycommand.player");
    }

    @Override
    public int getMaxArgs() {
        return 2; // Allow up to 2 arguments
    }

    @Override
    public int getWeight() {
        return 5; // Medium priority
    }
}
