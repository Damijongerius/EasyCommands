package com.dami.easyCommands.annotations;

import com.dami.easyCommands.model.SenderType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
    String[] commandPath() default {};

    /**
     * The command name
     */
    String name();

    /**
     * Aliases for this command
     */
    String[] aliases() default {};

    /**
     * The command description
     */
    String description() default "";

    /**
     * The command usage
     */
    String usage() default "";

    /**
     * The maximum number of arguments allowed
     */
    int maxArgs() default 0;

    /**
     * Reusable tab completions for parameters matching the index.
     * Example: completions = {"@players", "@kingdoms"}
     */
    String[] completions() default {};

    /**
     * The command Permission
     */
    String permission() default "";

    /**
     * Weight to override the default command
     */
    int weight() default 0;

    /**
     * The type of sender that can use this command
     */
    SenderType senderType() default SenderType.ANY;

    /**
     * The number of required arguments. Defaults to -1 (auto-detect or 0 for mainCommand).
     */
    int requiredArgs() default -1;
}
