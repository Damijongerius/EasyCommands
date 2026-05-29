package com.dami.easyCommands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applies a cooldown to a command.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Cooldown {
    /**
     * The cooldown duration in seconds.
     */
    int seconds();

    /**
     * The permission required to bypass this cooldown. 
     * Leave empty for auto-generated permission.
     */
    String bypassPermission() default "";
}
