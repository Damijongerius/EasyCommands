package com.dami.easyCommands.Command;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

public class SubCommandInfo {
    private final Method method;
    private final Object owner;
    private final int weight;
    private final String permission;
    private final int expectedValues;

    public SubCommandInfo(Method method, Object owner, int weight, String permission, int expectedValues) {
        this.method = method;
        this.owner = owner;
        this.weight = weight;
        this.permission = permission;
        this.expectedValues = expectedValues;
    }

    public void run(CommandSender commandSender, String[] args){

        if(!commandSender.hasPermission(permission)){
            commandSender.sendMessage("§cYou don't have permission to use this command.");
            return;
        }

        try {
            method.invoke(owner, commandSender, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getWeight() {
        return weight;
    }

    public int getExpectedValues() {
        return expectedValues;
    }
}
