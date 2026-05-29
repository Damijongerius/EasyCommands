package com.dami.easyCommands.core;

import com.dami.easyCommands.model.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;

public class ExceptionRegistry {
    private static final Map<Class<? extends Throwable>, ExceptionHandler<?>> handlers = new HashMap<>();

    public static <T extends Throwable> void register(Class<T> exceptionClass, ExceptionHandler<T> handler) {
        handlers.put(exceptionClass, handler);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> ExceptionHandler<T> getHandler(Class<? extends Throwable> exceptionClass) {
        Class<?> current = exceptionClass;
        while (current != null && current != Throwable.class) {
            ExceptionHandler<?> handler = handlers.get(current);
            if (handler != null) {
                return (ExceptionHandler<T>) handler;
            }
            current = current.getSuperclass();
        }
        return null;
    }
}
