package com.dami.easyCommands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define a dynamic tab completion key for a specific parameter.
 * This directly replaces the need for the method-level completions array.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface AutoComplete {
    /**
     * The registry key for the completion (e.g. "guilds")
     */
    String value();
}
