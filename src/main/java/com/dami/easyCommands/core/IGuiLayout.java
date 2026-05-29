package com.dami.easyCommands.core;

import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;

import java.util.List;

/**
 * Interface for designing custom layout strategies for Auto-GUIs.
 */
public interface IGuiLayout {
    
    /**
     * Applies the layout to the given GUI with the provided items.
     * @param gui The paginated GUI
     * @param items The items to place
     */
    void apply(PaginatedGui gui, List<GuiItem> items);
}
