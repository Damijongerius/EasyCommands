package com.dami.easyCommands.commands;

import com.dami.easyCommands.Annotations.SubCommand;
import com.dami.easyCommands.Annotations.SubCommandTab;
import com.dami.easyCommands.Command.ShardableCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AmazingBaseCommand extends ShardableCommand {

    public AmazingBaseCommand() {
        super();
    }

    @Override
    public void mainCommand(CommandSender sender, String[] args) {
        sender.sendMessage("Amazing Command Executed");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of("base1", "base2");
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
        sender.sendMessage("Subexample Command Executed");
    }

    @SubCommandTab(commandPath = {}, name = "subexample")
    public List<String> subCommandExampleTab(CommandSender sender, String[] args){
        return List.of("one", "two", "three");
    }

    @SubCommand(commandPath = {}, name = "subexample2")
    public void subCommandExample2(CommandSender sender, String[] args) {
        sender.sendMessage("Subexample2 Command Executed");
    }

    @SubCommandTab(commandPath = {}, name = "subexample2")
    public List<String> subCommandExampleTab2(CommandSender sender, String[] args){
        return List.of("one", "two", "three", "four");
    }

    @SubCommand(commandPath = {"subexample"}, name = "one", maxArgs = 2)
    public void subCommandExample3(CommandSender sender, String[] args) {
        sender.sendMessage("Subexample One Command Executed");
    }

    @SubCommandTab(commandPath = {"subexample"}, name = "one")
    public List<String> subCommandExampleTab3(CommandSender sender, String[] args){
        return List.of("four", "five", "six");
    }
}
