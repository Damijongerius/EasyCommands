package com.dami.easyCommands.model;

import lombok.Getter;
import java.util.Map;

@Getter
public class ValidationException extends Exception {
    private final MessageKey messageKey;
    private final Map<String, String> placeholders;
    private String hoverText;
    private String clickActionCommand;

    public ValidationException(MessageKey messageKey, Map<String, String> placeholders) {
        this.messageKey = messageKey;
        this.placeholders = placeholders;
    }
    
    public ValidationException withHover(String hoverText) {
        this.hoverText = hoverText;
        return this;
    }
    
    public ValidationException withClickCommand(String command) {
        this.clickActionCommand = command;
        return this;
    }
}
