package com.dami.easyCommands;

import com.dami.easyCommands.core.TabRegistry;
import com.dami.easyCommands.internal.ParameterResolver;
import com.dami.easyCommands.model.TypeConverter;

/**
 * Central API facade for EasyCommands framework.
 */
public class EasyCommands {
    
    /**
     * Registers a custom parameter type converter for dependency injection.
     */
    public static <T> void registerParameterType(Class<T> type, TypeConverter<T> converter) {
        ParameterResolver.registerConverter(type, converter);
    }

    /**
     * Registers a reusable tab completion source.
     */
    public static void registerCompletion(String key, TabRegistry.TabCompleter completer) {
        TabRegistry.register(key, completer);
    }

    /**
     * Registers a custom condition required by @Require.
     */
    public static void registerCondition(String id, com.dami.easyCommands.model.Condition condition) {
        com.dami.easyCommands.core.ConditionRegistry.register(id, condition);
    }

    /**
     * Registers a global exception handler.
     */
    public static <T extends Throwable> void registerExceptionHandler(Class<T> exceptionClass, com.dami.easyCommands.model.ExceptionHandler<T> handler) {
        com.dami.easyCommands.core.ExceptionRegistry.register(exceptionClass, handler);
    }

    /**
     * Registers a custom sender resolver for @Sender context injection.
     */
    public static <T> void registerSenderResolver(Class<T> clazz, com.dami.easyCommands.model.SenderResolver<T> resolver) {
        ParameterResolver.registerSenderResolver(clazz, resolver);
    }

    /**
     * Halts a command and prompts the user for chat input.
     */
    public static void prompt(org.bukkit.plugin.Plugin plugin, org.bukkit.entity.Player player, java.util.function.Consumer<String> onResponse) {
        com.dami.easyCommands.internal.ChatPromptManager.prompt(plugin, player, onResponse);
    }
}
