package com.dami.easyCommands.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
/**
 * Annotation to define tab completion for a command path.
 * This annotation is used to mark methods as tab completion handlers
 * for specific command paths in the command framework.
 */
public @interface SubCommandTab {

    /**
     * The path to get to the command (same format as SubCommand)
     */
    String[] commandPath();

    /**
     * The command name for tab completion
     */
    String name();

    /**
     * Permission required to see this tab completion
     */
    String permission() default "";

    /**
     * Priority for this tab completion (higher = more priority)
     */
    int priority() default 0;
}
