import com.dami.easyCommands.Annotations.SubCommand;
import com.dami.easyCommands.Command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * Example test command that extends your Command class.
 * This demonstrates how to create a test command for testing purposes.
 * 
 * Note: This class won't compile without proper Bukkit dependencies,
 * but it shows the structure you would use for testing.
 */
public class ExampleTestCommand extends Command {
    
    public ExampleTestCommand(Plugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "exampletest";
    }
    
    @Override
    public int maxArgs() {
        return 1;
    }
    
    @Override
    public void MainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§aExampleTest main command executed!");
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
        sender.sendMessage("§eHello from ExampleTest command!");
    }
    
    @SubCommand(
        commandPath = {},
        name = "echo",
        maxArgs = 5,
        permission = ""
    )
    public void echoCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /exampletest echo <message>");
            return;
        }
        
        String message = String.join(" ", args);
        sender.sendMessage("§aEcho: §f" + message);
    }
    
    @SubCommand(
        commandPath = {"admin"},
        name = "test",
        maxArgs = 0,
        permission = "exampletest.admin.test"
    )
    public void adminTestCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§cAdmin test command executed!");
    }
    
    @SubCommand(
        commandPath = {"admin"},
        name = "reload",
        maxArgs = 0,
        permission = "exampletest.admin.reload"
    )
    public void adminReloadCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§aReloading configuration...");
    }
}
