import com.dami.easyCommands.Annotations.SubCommand;
import com.dami.easyCommands.Command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * A simple test command that demonstrates how to extend your Command class.
 * This can be used for testing without requiring complex mock implementations.
 */
public class SimpleTestCommand extends Command {
    
    public SimpleTestCommand(Plugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "simpletest";
    }
    
    @Override
    public int maxArgs() {
        return 1;
    }
    
    @Override
    public void MainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§aSimpleTest main command executed!");
        if (args.length > 0) {
            sender.sendMessage("§7Arguments: " + String.join(" ", args));
        }
    }
    
    @SubCommand(
        commandPath = {},
        name = "hello",
        maxArgs = 0,
        permission = ""
    )
    public void helloCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§eHello from SimpleTest command!");
    }
    
    @SubCommand(
        commandPath = {},
        name = "echo",
        maxArgs = 5,
        permission = ""
    )
    public void echoCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /simpletest echo <message>");
            return;
        }
        
        String message = String.join(" ", args);
        sender.sendMessage("§aEcho: §f" + message);
    }
    
    @SubCommand(
        commandPath = {"admin"},
        name = "test",
        maxArgs = 0,
        permission = "simpletest.admin.test"
    )
    public void adminTestCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§cAdmin test command executed!");
    }
}
