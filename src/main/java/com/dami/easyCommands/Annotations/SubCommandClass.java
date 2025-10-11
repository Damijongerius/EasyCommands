package com.dami.easyCommands.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
/**
 * Annotation to mark a class as a subcommand handler.
 * Classes marked with this annotation can be registered as subcommands
 * and will handle command execution for their assigned path.
 */
public @interface SubCommandClass {

    /**
     * The path to get to the command (same format as SubCommand)
     */
    String[] commandPath();

    /**
     * The command name for this subcommand class
     */
    String name();

    /**
     * Permission required to use this subcommand class
     */
    String permission() default "";

    /**
     * Weight to override the default command
     */
    int weight() default 0;

    /**
     * Maximum number of arguments for the main command of this class
     */
    int maxArgs() default 0;
}
