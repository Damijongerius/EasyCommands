package com.dami.easyCommands.internal;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.List;

public class TabCompleteInfo {

    private final Method method;
    private final Object owner;

    private final String permission;

    private final int priority;

    public TabCompleteInfo(Method method, Object owner, String permission, int priority) {
        this.method = method;
        this.owner = owner;
        this.permission = permission;
        this.priority = priority;
    }

    public String getPermission() {
        return permission;
    }

    public int getPriority() {
        return priority;
    }

    public List<String> getTabComplete(CommandSender sender, String[] args, List<String> wildcards){
        if(!sender.hasPermission(permission) && !permission.isEmpty()){
            return null;
        }
        try {
            method.setAccessible(true);
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] params = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                if (CommandSender.class.isAssignableFrom(parameterTypes[i])) {
                    params[i] = sender;
                } else if (parameterTypes[i] == String[].class) {
                    params[i] = args;
                } else if (List.class.isAssignableFrom(parameterTypes[i])) {
                    params[i] = wildcards;
                } else {
                    params[i] = null;
                }
            }
            return (List<String>) method.invoke(owner, params);
        } catch (Exception e) {
            System.err.println("Error while trying to get tab completion for " + method.getName() + " in " + owner.getClass().getSimpleName());
            e.printStackTrace();
        }
        return null;
    }
}
