package com.dami.easyCommands.Command;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

public class SubCommandInfo {

    private final Method method;

    private final Object owner;

    @Getter
    private final int weight;

    @Getter
    private final String permission;

    @Getter
    private final int maxArgs; //max args

    //tab complete

    public SubCommandInfo(Method method, Object owner, int weight, String permission, int maxArgs) {
        this.method = method;
        this.owner = owner;
        this.weight = weight;
        this.permission = permission;
        this.maxArgs = maxArgs;
    }

    public void run(CommandSender commandSender, String[] args){

        if(!commandSender.hasPermission(permission) && !permission.isEmpty()){
            commandSender.sendMessage("§cYou don't have permission to use this command.");
            return;
        }

        try {
            method.invoke(owner, commandSender, args);
        } catch (Exception e) {
            System.out.println("Error while trying to run command make sure the args CommandSender sender, String[] args are the first two arguments");
        }
    }

}
