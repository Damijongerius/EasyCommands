package com.dami.easyCommands.commands;

import com.dami.easyCommands.annotations.SubCommand;
import com.dami.easyCommands.annotations.Tab;
import com.dami.easyCommands.core.BaseCommand;
import com.dami.easyCommands.model.SenderType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TestCommand extends BaseCommand {
    public TestCommand() {
        super();
        initialize();
    }
    @Override
    public String getName() { return "root"; }
    @Override
    public int maxArgs() { return 0; }
    @Override
    public void mainCommand(CommandSender sender, String[] args) {}
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) { return List.of(); }
    @Override
    public void showUsage(CommandSender sender) {}

    @SubCommand(name = "give", usage = "test give <player> <amount>", description = "Give items")
    public void giveCommand(CommandSender sender, Player target, int amount) {
        sender.sendMessage("Giving " + amount + " to " + (target != null ? target.getName() : "null"));
    }

    @SubCommand(name = "teleport", aliases = {"tp", "goto"}, description = "Teleport to player")
    public void tpCommand(CommandSender sender, Player target) {
        sender.sendMessage("Teleporting to " + (target != null ? target.getName() : "null"));
    }

    @SubCommand(name = "playeronly", senderType = SenderType.PLAYER)
    public void playerOnly(Player sender) {
        sender.sendMessage("Hello player!");
    }
    
    @SubCommand(name = "autotab")
    public void autoTab(@Tab("players") String player, @Tab("boolean") boolean force) {
        // Test method for tab completion
    }
}



