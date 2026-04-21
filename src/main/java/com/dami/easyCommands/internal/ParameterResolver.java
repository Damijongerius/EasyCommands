package com.dami.easyCommands.internal;

import com.dami.easyCommands.annotations.Max;
import com.dami.easyCommands.annotations.Min;
import com.dami.easyCommands.annotations.Optional;
import com.dami.easyCommands.model.MessageKey;
import com.dami.easyCommands.model.TypeConverter;
import com.dami.easyCommands.model.ValidationException;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to resolve method parameters from command arguments.
 */
public class ParameterResolver {

    private static final Map<Class<?>, TypeConverter<?>> converters = new HashMap<>();

    static {
        registerConverter(String.class, s -> s);
        registerConverter(Integer.class, Integer::parseInt);
        registerConverter(int.class, Integer::parseInt);
        registerConverter(Long.class, Long::parseLong);
        registerConverter(long.class, Long::parseLong);
        registerConverter(Double.class, Double::parseDouble);
        registerConverter(double.class, Double::parseDouble);
        registerConverter(Float.class, Float::parseFloat);
        registerConverter(float.class, Float::parseFloat);
        registerConverter(Boolean.class, s -> s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes") || s.equals("1"));
        registerConverter(boolean.class, s -> s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes") || s.equals("1"));
        
        registerConverter(Player.class, s -> {
            if (Bukkit.getServer() == null) return null;
            return Bukkit.getPlayer(s);
        });
        registerConverter(OfflinePlayer.class, s -> {
            if (Bukkit.getServer() == null) return null;
            return Bukkit.getOfflinePlayer(s);
        });
        registerConverter(World.class, s -> {
            if (Bukkit.getServer() == null) return null;
            return Bukkit.getWorld(s);
        });
        registerConverter(Material.class, s -> Material.matchMaterial(s.toUpperCase()));
        registerConverter(Sound.class, s -> {
            try {
                return Sound.valueOf(s.toUpperCase());
            } catch (Exception e) {
                return null;
            }
        });
        registerConverter(EntityType.class, s -> {
            try {
                return EntityType.valueOf(s.toUpperCase());
            } catch (Exception e) {
                return null;
            }
        });
    }

    public static <T> void registerConverter(Class<T> clazz, TypeConverter<T> converter) {
        converters.put(clazz, converter);
    }

    public static Object[] resolveParameters(Method method, CommandSender sender, String[] args) throws ValidationException {
        Parameter[] parameters = method.getParameters();
        Object[] resolvedArgs = new Object[parameters.length];

        int argIndex = 0;

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            Class<?> paramType = param.getType();

            if (CommandSender.class.isAssignableFrom(paramType) && !Player.class.isAssignableFrom(paramType)) {
                resolvedArgs[i] = sender;
            } else if (Player.class.isAssignableFrom(paramType) && i == 0 && sender instanceof Player) {
                resolvedArgs[i] = (Player) sender;
            } else if (paramType.isArray() && paramType.getComponentType() == String.class) {
                String[] remainingArgs = new String[Math.max(0, args.length - argIndex)];
                if (remainingArgs.length > 0) {
                    System.arraycopy(args, argIndex, remainingArgs, 0, args.length - argIndex);
                }
                resolvedArgs[i] = remainingArgs;
                argIndex = args.length;
            } else {
                String value = null;
                if (argIndex < args.length) {
                    value = args[argIndex++];
                } else if (param.isAnnotationPresent(Optional.class)) {
                    value = param.getAnnotation(Optional.class).value();
                }

                if (value == null) {
                    resolvedArgs[i] = null;
                } else {
                    try {
                        resolvedArgs[i] = convert(value, paramType);
                        if (resolvedArgs[i] == null && !paramType.isPrimitive() && !value.isEmpty()) {
                             Map<String, String> placeholders = new HashMap<>();
                             placeholders.put("arg", value);
                             placeholders.put("param", param.getName());
                             throw new ValidationException(MessageKey.INVALID_ARGUMENT, placeholders);
                        }
                        validate(param, resolvedArgs[i]);
                    } catch (ValidationException e) {
                        throw e;
                    } catch (Exception e) {
                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("arg", value);
                        placeholders.put("param", param.getName());
                        throw new ValidationException(MessageKey.INVALID_ARGUMENT, placeholders);
                    }
                }
            }
        }

        return resolvedArgs;
    }

    private static Object convert(String value, Class<?> type) throws Exception {
        TypeConverter<?> converter = converters.get(type);
        if (converter != null) {
            return converter.convert(value);
        }
        throw new IllegalArgumentException("No converter registered for type: " + type.getName());
    }

    private static void validate(Parameter param, Object value) throws ValidationException {
        if (value instanceof Number) {
            double doubleValue = ((Number) value).doubleValue();
            if (param.isAnnotationPresent(Min.class)) {
                double min = param.getAnnotation(Min.class).value();
                if (doubleValue < min) {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("param", param.getName());
                    placeholders.put("min", String.valueOf(min));
                    throw new ValidationException(MessageKey.MIN_VALUE, placeholders);
                }
            }
            if (param.isAnnotationPresent(Max.class)) {
                double max = param.getAnnotation(Max.class).value();
                if (doubleValue > max) {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("param", param.getName());
                    placeholders.put("max", String.valueOf(max));
                    throw new ValidationException(MessageKey.MAX_VALUE, placeholders);
                }
            }
        }
    }
}
