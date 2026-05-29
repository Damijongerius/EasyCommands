package com.dami.easyCommands.internal;

import com.dami.easyCommands.core.TabRegistry;

import com.dami.easyCommands.annotations.Tab;
import com.dami.easyCommands.model.CompletionProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class to resolve tab completions from method parameters.
 */
public class CompletionResolver {

    private static final Map<String, CompletionProvider> providers = new HashMap<>();

    static {
        registerProvider("players", (sender, args) -> 
            Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        
        registerProvider("worlds", (sender, args) -> 
            Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
            
        registerProvider("boolean", (sender, args) -> 
            List.of("true", "false"));

        registerProvider("materials", (sender, args) ->
            Arrays.stream(Material.values()).map(m -> m.name().toLowerCase()).collect(Collectors.toList()));

        registerProvider("sounds", (sender, args) ->
            Arrays.stream(Sound.values()).map(s -> s.name().toLowerCase()).collect(Collectors.toList()));

        registerProvider("entity_types", (sender, args) ->
            Arrays.stream(EntityType.values()).map(e -> e.name().toLowerCase()).collect(Collectors.toList()));
    }

    public static void registerProvider(String name, CompletionProvider provider) {
        providers.put(name.toLowerCase(), provider);
    }

    public static List<String> resolve(Method method, CommandSender sender, String[] args, List<String> wildcards, String[] completions) {
        Parameter[] parameters = method.getParameters();

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
        
        int argIndexToFind = fullArgs.length - 1; 
        int currentArgIndex = 0;

        for (Parameter param : parameters) {
            Class<?> type = param.getType();
            
            if (CommandSender.class.isAssignableFrom(type) && !Player.class.isAssignableFrom(type)) {
                continue;
            }
            if (Player.class.isAssignableFrom(type) && currentArgIndex == 0 && sender instanceof Player) {
                continue;
            }
            
            if (currentArgIndex == argIndexToFind) {
                java.util.List<String> rawCompletions = null;
                
                if (completions != null && currentArgIndex < completions.length) {
                    String providerName = completions[currentArgIndex];
                    if (providerName != null && !providerName.isEmpty()) {
                        rawCompletions = TabRegistry.getCompletions(providerName, sender);
                    }
                }

                if (rawCompletions == null && param.isAnnotationPresent(Tab.class)) {
                    String providerName = param.getAnnotation(Tab.class).value();
                    CompletionProvider provider = providers.get(providerName.toLowerCase());
                    if (provider != null) {
                        rawCompletions = provider.getSuggestions(sender, fullArgs);
                    }
                }
                
                if (rawCompletions == null) {
                    if (Player.class.isAssignableFrom(type)) {
                        rawCompletions = providers.get("players").getSuggestions(sender, fullArgs);
                    } else if (World.class.isAssignableFrom(type)) {
                        rawCompletions = providers.get("worlds").getSuggestions(sender, fullArgs);
                    } else if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)) {
                        rawCompletions = providers.get("boolean").getSuggestions(sender, fullArgs);
                    } else if (Material.class.isAssignableFrom(type)) {
                        rawCompletions = providers.get("materials").getSuggestions(sender, fullArgs);
                    } else if (Sound.class.isAssignableFrom(type)) {
                        rawCompletions = providers.get("sounds").getSuggestions(sender, fullArgs);
                    } else if (EntityType.class.isAssignableFrom(type)) {
                        rawCompletions = providers.get("entity_types").getSuggestions(sender, fullArgs);
                    }
                }
                
                if (rawCompletions != null) {
                    String token = fullArgs[fullArgs.length - 1].toLowerCase();
                    return rawCompletions.stream()
                        .filter(s -> s.toLowerCase().startsWith(token))
                        .collect(Collectors.toList());
                }
                
                return null;
            }
            
            currentArgIndex++;
        }

        return null;
    }
}
