package Commands;

import com.dami.easyCommands.Annotations.SubCommand;
import com.dami.easyCommands.Annotations.SubCommandTab;
import com.dami.easyCommands.Command.BaseCommand;
import com.dami.easyCommands.Command.ShardableCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class AmazingBaseCommand extends ShardableCommand {

    public AmazingBaseCommand(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void mainCommand(CommandSender sender, String[] args) {
        System.out.println("Amazing Command Executed");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of("null");
    }

    @Override
    public void showUsage(CommandSender sender) {

    }

    @Override
    public String getName() {
        return "amazing";
    }

    @Override
    public int maxArgs() {
        return 0;
    }

    @SubCommand(commandPath = {}, name = "subexample")
    public void subCommandExample(CommandSender sender, String[] args) {
        System.out.println("Subexample Command Executed");
    }

    @SubCommandTab(commandPath = {}, name = "subexample")
    public List<String> subCommandExampleTab(CommandSender sender, String[] args){

        System.out.println("TAB: SUBEXAMPLE has ben ran");

        return List.of(new String[]{"one", "two", "three"});
    }

    @SubCommand(commandPath = {}, name = "subexample2")
    public void subCommandExample2(CommandSender sender, String[] args) {
        System.out.println("Subexample2 Command Executed");
    }

    @SubCommandTab(commandPath = {}, name = "subexample2")
    public List<String> subCommandExampleTab2(CommandSender sender, String[] args){

        return List.of(new String[]{"one", "two", "three", "four"});
    }

    @SubCommand(commandPath = {"subexample"}, name = "one", maxArgs = 2)
    public void subCommandExample3(CommandSender sender, String[] args) {
        System.out.println("Subexample One Command Executed");
    }

    @SubCommandTab(commandPath = {"subexample"}, name = "one")
    public List<String> subCommandExampleTab3(CommandSender sender, String[] args){

        return List.of(new String[]{"four", "five", "six"});
    }
}
