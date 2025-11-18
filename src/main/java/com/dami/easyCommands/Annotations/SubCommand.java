package com.dami.easyCommands.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
/**
 * Annotation to define a sub-command for a command framework.
 * This annotation is used to mark methods as sub-commands
 * with specific properties such as command path, name, permission, etc.
 * BTW name is being added to the path automatically <3
 */
public @interface SubCommand {

    /**
     * The path to get to the command
     */
    String[] commandPath();

    /**
     * The command name
     */
    String name();

    int maxArgs() default 0;

    /**
     * The command Permission
     */
    String permission() default "";

    /**
     * Weight to override the default command
     */
    int weight() default 0;
}
