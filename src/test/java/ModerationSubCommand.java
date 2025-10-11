import com.dami.easyCommands.Annotations.SubCommandClass;
import com.dami.easyCommands.Command.SubCommandHandler;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * Example moderation subcommand class.
 * This demonstrates how to create a class-based subcommand for moderation commands.
 */
@SubCommandClass(
    commandPath = {"admin"},
    name = "moderation",
    permission = "mycommand.admin.moderation",
    weight = 15,
    maxArgs = 3
)
public class ModerationSubCommand extends SubCommandHandler {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§e=== Moderation Commands ===");
            sender.sendMessage("§7/admin moderation warn <player> <reason> - Warn a player");
            sender.sendMessage("§7/admin moderation mute <player> <duration> - Mute a player");
            sender.sendMessage("§7/admin moderation unmute <player> - Unmute a player");
            return;
        }

        String subCommand = args[0];
        switch (subCommand.toLowerCase()) {
            case "warn":
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /mycommand admin moderation warn <player> <reason>");
                } else {
                    String playerName = args[1];
                    String reason = args[2];
                    sender.sendMessage("§cWarning " + playerName + " for: " + reason);
                }
                break;
            case "mute":
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /mycommand admin moderation mute <player> <duration>");
                } else {
                    String playerName = args[1];
                    String duration = args[2];
                    sender.sendMessage("§cMuting " + playerName + " for: " + duration);
                }
                break;
            case "unmute":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /mycommand admin moderation unmute <player>");
                } else {
                    String playerName = args[1];
                    sender.sendMessage("§aUnmuting " + playerName);
                }
                break;
            default:
                sender.sendMessage("§cUnknown moderation command: " + subCommand);
                break;
        }
    }

    @Override
    public List<String> getTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("warn", "mute", "unmute");
        } else if (args.length == 2) {
            return Arrays.asList("Player1", "Player2", "Player3");
        } else if (args.length == 3) {
            if ("warn".equals(args[0])) {
                return Arrays.asList("griefing", "spamming", "inappropriate", "cheating");
            } else if ("mute".equals(args[0])) {
                return Arrays.asList("1m", "5m", "10m", "1h", "1d");
            }
        }
        return null;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("mycommand.admin.moderation");
    }

    @Override
    public int getMaxArgs() {
        return 3; // Allow up to 3 arguments
    }

    @Override
    public int getWeight() {
        return 15; // High priority
    }
}
