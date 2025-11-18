package com.dami.easyCommands.Command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface ICommand {

    /**
     * @return The maximum number of arguments for the MainCommand
     */
    int maxArgs();

    /**
     * @param sender the object that ran the command
     * @param args the arguments passed to the main command
     */
    void mainCommand(CommandSender sender, String[] args);

    /**
     * @param sender the object that ran the command
     * @param args the arguments passed to the main command (could be less based on if it is the main command or a subcommand)
     * @return returns a list of tab completions for the main command just note these are going to be in addition to the @SubCommand
     */
    List<String> tabComplete(CommandSender sender, String[] args);

    /**
     * @param sender the object that ran the command
     * Show the usage of the command to the sender
     */
    void showUsage(CommandSender sender);

}
