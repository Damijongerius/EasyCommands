package com.dami.easyCommands.commands;

import com.dami.easyCommands.annotations.SubCommand;
import com.dami.easyCommands.core.BaseCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class PermissionTestCommand extends BaseCommand {

    public PermissionTestCommand() {
        super();
        initialize();
    }

    @Override
    public void mainCommand(CommandSender sender, String[] args) {
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public void showUsage(CommandSender sender) {
    }

    @Override
    public String getName() {
        return "permissiontest";
    }

    @Override
    public int maxArgs() {
        return 0;
    }

    @SubCommand(name = "public")
    public void publicCommand(CommandSender sender, String[] args) {
    }

    @SubCommand(name = "secret", permission = "test.secret")
    public void secretCommand(CommandSender sender, String[] args) {
    }

    @SubCommand(commandPath = {"secret"}, name = "deep", permission = "test.deep")
    public void deepCommand(CommandSender sender, String[] args) {
    }
}



