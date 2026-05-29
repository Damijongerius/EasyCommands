package com.dami.easyCommands.internal;

import com.dami.easyCommands.core.MessageHandler;
import com.dami.easyCommands.internal.CooldownManager;
import com.dami.easyCommands.annotations.GuiIcon;

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

    @Getter
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

    @Getter
    private final String guiIconMaterial;

    @Getter
    private final int guiRow;

    @Getter
    private final int guiCol;

    @Getter
    private final String[] completions;

    @Getter
    private final boolean isAsync;

    @Getter
    private final int cooldownSeconds;

    @Getter
    private final String cooldownBypassPerm;

    @Getter
    private final String[] requires;

    @Getter
    private final int confirmTimeout;

    public SubCommandInfo(Method method, Object owner, int weight, String permission, int maxArgs, 
                          String[] aliases, String description, String usage, SenderType senderType, String[] completions) {
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
        
        if (method.isAnnotationPresent(GuiIcon.class)) {
            this.guiIconMaterial = method.getAnnotation(GuiIcon.class).material();
        } else {
            this.guiIconMaterial = "";
        }
        
        if (method.isAnnotationPresent(com.dami.easyCommands.annotations.GuiSlot.class)) {
            com.dami.easyCommands.annotations.GuiSlot guiSlot = method.getAnnotation(com.dami.easyCommands.annotations.GuiSlot.class);
            this.guiRow = guiSlot.row();
            this.guiCol = guiSlot.col();
        } else {
            this.guiRow = -1;
            this.guiCol = -1;
        }
        
        String[] combinedCompletions = completions != null ? completions : new String[0];
        java.util.List<String> finalCompletions = new java.util.ArrayList<>();
        int compIndex = 0;
        for (java.lang.reflect.Parameter param : method.getParameters()) {
            if (CommandSender.class.isAssignableFrom(param.getType()) || param.isAnnotationPresent(com.dami.easyCommands.annotations.Sender.class)) {
                continue;
            }
            if (param.isAnnotationPresent(com.dami.easyCommands.annotations.Flag.class)) {
                continue;
            }
            String comp = "";
            if (compIndex < combinedCompletions.length && combinedCompletions[compIndex] != null && !combinedCompletions[compIndex].isEmpty()) {
                comp = combinedCompletions[compIndex];
            } else if (param.isAnnotationPresent(com.dami.easyCommands.annotations.AutoComplete.class)) {
                comp = param.getAnnotation(com.dami.easyCommands.annotations.AutoComplete.class).value();
            }
            finalCompletions.add(comp);
            compIndex++;
        }
        this.completions = finalCompletions.toArray(new String[0]);
        this.isAsync = method.isAnnotationPresent(com.dami.easyCommands.annotations.Async.class);
        
        if (method.isAnnotationPresent(com.dami.easyCommands.annotations.Cooldown.class)) {
            com.dami.easyCommands.annotations.Cooldown cd = method.getAnnotation(com.dami.easyCommands.annotations.Cooldown.class);
            this.cooldownSeconds = cd.seconds();
            this.cooldownBypassPerm = cd.bypassPermission();
        } else {
            this.cooldownSeconds = 0;
            this.cooldownBypassPerm = "";
        }

        if (method.isAnnotationPresent(com.dami.easyCommands.annotations.Require.class)) {
            this.requires = method.getAnnotation(com.dami.easyCommands.annotations.Require.class).value();
        } else {
            this.requires = new String[0];
        }

        if (method.isAnnotationPresent(com.dami.easyCommands.annotations.Confirm.class)) {
            this.confirmTimeout = method.getAnnotation(com.dami.easyCommands.annotations.Confirm.class).timeout();
        } else {
            this.confirmTimeout = 0;
        }
    }

    public void run(CommandSender commandSender, String[] args, List<String> wildcards, MessageHandler messageHandler, String baseCommandName, String fullPath){
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

        if (cooldownSeconds > 0 && commandSender instanceof Player) {
            Player player = (Player) commandSender;
            String bypass = cooldownBypassPerm.isEmpty() ? fullPath.replace(" ", ".") + ".bypass.cooldown" : cooldownBypassPerm;
            if (!player.hasPermission(bypass)) {
                if (CooldownManager.isOnCooldown(fullPath, player.getUniqueId())) {
                    long remaining = CooldownManager.getRemainingSeconds(fullPath, player.getUniqueId());
                    Map<String, String> cdPlaceholders = new HashMap<>(placeholders);
                    cdPlaceholders.put("time", String.valueOf(remaining));
                    messageHandler.sendMessage(commandSender, MessageKey.COOLDOWN_ACTIVE, cdPlaceholders);
                    return;
                } else {
                    CooldownManager.setCooldown(fullPath, player.getUniqueId(), cooldownSeconds);
                }
            }
        }

        if (confirmTimeout > 0 && commandSender instanceof Player) {
            Player p = (Player) commandSender;
            String cmdSignature = fullPath + " " + String.join(" ", args);
            if (!com.dami.easyCommands.internal.ConfirmationManager.isConfirmed(p.getUniqueId(), cmdSignature)) {
                com.dami.easyCommands.internal.ConfirmationManager.requestConfirmation(p.getUniqueId(), cmdSignature, confirmTimeout);
                Map<String, String> confPlaceholders = new HashMap<>(placeholders);
                confPlaceholders.put("time", String.valueOf(confirmTimeout));
                messageHandler.sendMessage(commandSender, MessageKey.CONFIRMATION_REQUIRED, confPlaceholders);
                return;
            }
        }

        try {
            for (String req : requires) {
                com.dami.easyCommands.model.Condition condition = com.dami.easyCommands.core.ConditionRegistry.get(req);
                if (condition != null) {
                    condition.check(commandSender);
                } else {
                    commandSender.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize("<red>Warning: Condition '" + req + "' is not registered.</red>"));
                }
            }
        } catch (ValidationException e) {
            Map<String, String> mergedPlaceholders = new HashMap<>(placeholders);
            if (e.getPlaceholders() != null) {
                mergedPlaceholders.putAll(e.getPlaceholders());
            }
            if (messageHandler instanceof com.dami.easyCommands.core.MessageHandler.DefaultMessageHandler) {
                com.dami.easyCommands.core.MessageHandler.DefaultMessageHandler defaultHandler = (com.dami.easyCommands.core.MessageHandler.DefaultMessageHandler) messageHandler;
                String rawMsg = defaultHandler.getRawMessage(e.getMessageKey());
                if (rawMsg == null) rawMsg = "Message not found: " + e.getMessageKey();
                
                net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.Builder resolverBuilder = net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.builder();
                for (Map.Entry<String, String> entry : mergedPlaceholders.entrySet()) {
                    resolverBuilder.resolver(net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.parsed(entry.getKey(), entry.getValue()));
                }
                
                net.kyori.adventure.text.Component component = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(rawMsg, resolverBuilder.build());
                
                if (e.getHoverText() != null && !e.getHoverText().isEmpty()) {
                    component = component.hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(e.getHoverText())));
                }
                if (e.getClickActionCommand() != null && !e.getClickActionCommand().isEmpty()) {
                    component = component.clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand(e.getClickActionCommand()));
                }
                
                commandSender.sendMessage(component);
            } else {
                messageHandler.sendMessage(commandSender, e.getMessageKey(), mergedPlaceholders);
            }
            return;
        } catch (Exception e) {
            e.printStackTrace();
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
            
            Runnable execution = () -> {
                try {
                    method.setAccessible(true);
                    method.invoke(owner, resolvedParams);
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    com.dami.easyCommands.model.ExceptionHandler handler = com.dami.easyCommands.core.ExceptionRegistry.getHandler(cause.getClass());
                    if (handler != null) {
                        @SuppressWarnings("unchecked")
                        com.dami.easyCommands.model.ExceptionHandler<Throwable> castedHandler = (com.dami.easyCommands.model.ExceptionHandler<Throwable>) handler;
                        castedHandler.handle(commandSender, cause);
                    } else {
                        System.err.println("Error while trying to run command " + method.getName() + " in " + owner.getClass().getSimpleName());
                        cause.printStackTrace();
                        commandSender.sendMessage("§cAn internal error occurred while executing this command.");
                    }
                }
            };

            if (isAsync) {
                org.bukkit.plugin.Plugin plugin = org.bukkit.plugin.java.JavaPlugin.getProvidingPlugin(owner.getClass());
                org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(plugin, execution);
            } else {
                execution.run();
            }
        } catch (ValidationException e) {
            if (messageHandler instanceof com.dami.easyCommands.core.MessageHandler.DefaultMessageHandler) {
                com.dami.easyCommands.core.MessageHandler.DefaultMessageHandler defaultHandler = (com.dami.easyCommands.core.MessageHandler.DefaultMessageHandler) messageHandler;
                String rawMsg = defaultHandler.getRawMessage(e.getMessageKey());
                if (rawMsg == null) rawMsg = "Message not found: " + e.getMessageKey();
                
                net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.Builder resolverBuilder = net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.builder();
                if (e.getPlaceholders() != null) {
                    for (Map.Entry<String, String> entry : e.getPlaceholders().entrySet()) {
                        resolverBuilder.resolver(net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.parsed(entry.getKey(), entry.getValue()));
                    }
                }
                
                net.kyori.adventure.text.Component component = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(rawMsg, resolverBuilder.build());
                
                if (e.getHoverText() != null && !e.getHoverText().isEmpty()) {
                    component = component.hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(e.getHoverText())));
                }
                if (e.getClickActionCommand() != null && !e.getClickActionCommand().isEmpty()) {
                    component = component.clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand(e.getClickActionCommand()));
                }
                
                commandSender.sendMessage(component);
            } else {
                messageHandler.sendMessage(commandSender, e.getMessageKey(), e.getPlaceholders());
            }
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
        return CompletionResolver.resolve(method, sender, args, wildcards, completions);
    }
}
