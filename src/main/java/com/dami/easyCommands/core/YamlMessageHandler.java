package com.dami.easyCommands.core;

import com.dami.easyCommands.model.MessageKey;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class YamlMessageHandler extends MessageHandler.DefaultMessageHandler {
    private final File file;
    private final YamlConfiguration config;

    public YamlMessageHandler(File file) {
        super();
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
        loadOrSave();
    }

    private void loadOrSave() {
        boolean saveNeeded = false;
        for (MessageKey key : MessageKey.values()) {
            if (!config.contains(key.name())) {
                config.set(key.name(), getRawMessage(key));
                saveNeeded = true;
            } else {
                setMessage(key, config.getString(key.name()));
            }
        }
        if (saveNeeded) {
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
