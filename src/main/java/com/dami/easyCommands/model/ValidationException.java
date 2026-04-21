package com.dami.easyCommands.model;

import lombok.Getter;
import java.util.Map;

@Getter
public class ValidationException extends Exception {
    private final MessageKey messageKey;
    private final Map<String, String> placeholders;

    public ValidationException(MessageKey messageKey, Map<String, String> placeholders) {
        this.messageKey = messageKey;
        this.placeholders = placeholders;
    }
}
