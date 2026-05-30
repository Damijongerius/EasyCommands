package com.dami.easyCommands.model;

import java.util.Map;

public class ValidationException extends Exception {
    private final MessageKey messageKey;
    private final Map<String, String> placeholders;
    private final String customMessage;
    private String hoverText;
    private String clickActionCommand;

    public ValidationException(MessageKey messageKey, Map<String, String> placeholders) {
        this.messageKey = messageKey;
        this.placeholders = placeholders;
        this.customMessage = null;
    }

    public ValidationException(String customMessage) {
        this.messageKey = null;
        this.placeholders = null;
        this.customMessage = customMessage;
    }
    
    public ValidationException withHover(String hoverText) {
        this.hoverText = hoverText;
        return this;
    }
    
    public ValidationException withClickCommand(String command) {
        this.clickActionCommand = command;
        return this;
    }

    public MessageKey getMessageKey() {
        return messageKey;
    }

    public Map<String, String> getPlaceholders() {
        return placeholders;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public String getHoverText() {
        return hoverText;
    }

    public String getClickActionCommand() {
        return clickActionCommand;
    }
}
