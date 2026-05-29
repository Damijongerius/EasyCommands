package com.dami.easyCommands.core;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.Material;

import java.util.List;

public class GridLayout implements IGuiLayout {
    private final int spacingX;
    private final int spacingY;

    public GridLayout(int spacingX, int spacingY) {
        this.spacingX = Math.max(0, spacingX);
        this.spacingY = Math.max(0, spacingY);
    }

    @Override
    public void apply(PaginatedGui gui, List<GuiItem> items) {
        GuiItem air = ItemBuilder.from(Material.AIR).asGuiItem();
        int colCounter = 0;
        
        for (GuiItem item : items) {
            gui.addItem(item);
            colCounter++;
            
            // Apply horizontal spacing
            for (int i = 0; i < spacingX; i++) {
                if (colCounter < 9) {
                    gui.addItem(air);
                    colCounter++;
                }
            }
            
            // Apply vertical spacing when we hit a new row
            if (colCounter >= 9) {
                colCounter = 0;
                for (int i = 0; i < spacingY * 9; i++) {
                    gui.addItem(air);
                }
            }
        }
    }
}
