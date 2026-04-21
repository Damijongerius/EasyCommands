package com.dami.easyCommands.internal;

import com.dami.easyCommands.core.MessageHandler;

import com.dami.easyCommands.model.MessageKey;
import com.dami.easyCommands.model.SenderType;
import com.dami.easyCommands.model.ValidationException;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubCommandInfo {

    private final Method method;
    private final Object owner;

    @Getter
    private final int weight;

    @Getter
    private final String permission;

    @Getter
    private final int maxArgs;

    @Getter
    private final String[] aliases;

    @Getter
    private final String description;

    @Getter
    private final String usage;

    @Getter
    private final SenderType senderType;

    public SubCommandInfo(Method method, Object owner, int weight, String permission, int maxArgs, 
                          String[] aliases, String description, String usage, SenderType senderType) {
        this.method = method;
        this.owner = owner;
        this.weight = weight;
        this.permission = permission;
        
        if (maxArgs == 0) {
            int argCount = 0;
            for (java.lang.reflect.Parameter param : method.getParameters()) {
                if (!CommandSender.class.isAssignableFrom(param.getType())) {
                    argCount++;
                }
            }
            this.maxArgs = argCount;
        } else {
            this.maxArgs = maxArgs;
        }
        
        this.aliases = aliases != null ? aliases : new String[0];
        this.description = description != null ? description : "";
        this.usage = usage != null ? usage : "";
        this.senderType = senderType != null ? senderType : SenderType.ANY;
    }

    public void run(CommandSender commandSender, String[] args, List<String> wildcards, MessageHandler messageHandler, String baseCommandName){
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("command", baseCommandName);
        placeholders.put("usage", usage.isEmpty() ? baseCommandName : usage);

        if(!commandSender.hasPermission(permission) && !permission.isEmpty()){
            messageHandler.sendMessage(commandSender, MessageKey.NO_PERMISSION, placeholders);
            return;
        }

        if(senderType == SenderType.PLAYER && !(commandSender instanceof Player)){
            messageHandler.sendMessage(commandSender, MessageKey.PLAYER_ONLY, placeholders);
            return;
        }

        if(senderType == SenderType.CONSOLE && (commandSender instanceof Player)){
            messageHandler.sendMessage(commandSender, MessageKey.CONSOLE_ONLY, placeholders);
            return;
        }

        try {
            String[] fullArgs;
            if (wildcards != null && !wildcards.isEmpty()) {
                fullArgs = new String[wildcards.size() + args.length];
                for (int i = 0; i < wildcards.size(); i++) {
                    fullArgs[i] = wildcards.get(i);
                }
                System.arraycopy(args, 0, fullArgs, wildcards.size(), args.length);
            } else {
                fullArgs = args;
            }

            Object[] resolvedParams = ParameterResolver.resolveParameters(method, commandSender, fullArgs);
            method.setAccessible(true);
            method.invoke(owner, resolvedParams);
        } catch (ValidationException e) {
            messageHandler.sendMessage(commandSender, e.getMessageKey(), e.getPlaceholders());
        } catch (Exception e) {
            System.err.println("Error while trying to run command " + method.getName() + " in " + owner.getClass().getSimpleName());
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            } else {
                e.printStackTrace();
            }
        }
    }

    public List<String> getTabComplete(CommandSender sender, String[] args, List<String> wildcards) {
        return CompletionResolver.resolve(method, sender, args, wildcards);
    }
}
