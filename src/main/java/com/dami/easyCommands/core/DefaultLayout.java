package com.dami.easyCommands.core;

import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;

import java.util.List;

public class DefaultLayout implements IGuiLayout {
    @Override
    public void apply(PaginatedGui gui, List<GuiItem> items) {
        for (GuiItem item : items) {
            gui.addItem(item);
        }
    }
}
