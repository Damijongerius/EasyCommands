package com.dami.easyCommands.Command;

import java.util.List;

public interface ICommand {

    /**
     * @return The command name
     */
    String getName();

    /**
     * @return The maximum number of arguments for the MainCommand
     */
    int maxArgs();

}
