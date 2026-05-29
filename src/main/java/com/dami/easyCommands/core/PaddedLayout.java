package com.dami.easyCommands.core;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.Material;

import java.util.List;

public class PaddedLayout implements IGuiLayout {
    @Override
    public void apply(PaginatedGui gui, List<GuiItem> items) {
        GuiItem air = ItemBuilder.from(Material.AIR).asGuiItem();
        for (GuiItem item : items) {
            gui.addItem(item);
            gui.addItem(air);
        }
    }
}
