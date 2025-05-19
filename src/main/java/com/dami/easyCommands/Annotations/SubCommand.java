package com.dami.easyCommands.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SubCommand {

    /**
     * The path to get to the command
     */
    String[] commandPath() default {};

    /**
     * The command values
     */
    int expectedValues() default 0;

    /**
     * The command name
     */
    String name() default "";

    /**
     * The command Permission
     */
    String permission() default "";

    /**
     * Weight to override the default command
     */
    int weight() default 0;
}
