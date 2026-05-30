package com.dami.easyCommands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define a custom GUI icon for an "auto gui" fallback menu.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface GuiIcon {
    /**
     * The Material name as a string (e.g., "STONE", "DIAMOND_SWORD").
     * Leave empty for a random icon to be generated.
     */
    String material() default "";
}
