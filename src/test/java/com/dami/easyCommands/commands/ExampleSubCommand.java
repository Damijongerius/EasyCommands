package com.dami.easyCommands.commands;

import com.dami.easyCommands.Annotations.SubCommand;
import com.dami.easyCommands.Command.ICommand;
import org.bukkit.command.CommandSender;

import java.util.List;

@SubCommand(commandPath = {}, name = "admin")
public class ExampleSubCommand implements ICommand {

    @SubCommand(commandPath = {}, name = "unalive")
    public void uninstallLife(CommandSender sender, String[] args) {
        sender.sendMessage("Example Sub Command Executed");
    }

    @Override
    public int maxArgs() {
        return 0;
    }

    @Override
    public void mainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("ExampleSubCommand mainCommand executed");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of("unalive");
    }

    @Override
    public void showUsage(CommandSender sender) {
    }
}
