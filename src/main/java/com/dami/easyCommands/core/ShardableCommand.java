package com.dami.easyCommands.core;

import com.dami.easyCommands.annotations.SubCommand;
import com.dami.easyCommands.annotations.SubCommandTab;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public abstract class ShardableCommand extends BaseCommand {

    protected final List<String> registeredSubCommandClasses = new ArrayList<>();
    private boolean autoGuiEnabled = false;

    protected ShardableCommand() {
        super();
    }

    public void enableAutoGui() {
        this.autoGuiEnabled = true;
    }

    public void disableAutoGui() {
        this.autoGuiEnabled = false;
    }

    @Override
    public void mainCommand(CommandSender sender, String[] args) {
        if (autoGuiEnabled && sender instanceof org.bukkit.entity.Player) {
            org.bukkit.entity.Player player = (org.bukkit.entity.Player) sender;
            dev.triumphteam.gui.guis.PaginatedGui gui = dev.triumphteam.gui.guis.Gui.paginated()
                    .title(net.kyori.adventure.text.Component.text(getName() + " Commands"))
                    .rows(6)
                    .create();

            gui.setItem(6, 3, dev.triumphteam.gui.builder.item.ItemBuilder.from(org.bukkit.Material.ARROW).name(net.kyori.adventure.text.Component.text("§7Previous Page")).asGuiItem(event -> {
                event.setCancelled(true);
                gui.previous();
            }));

            gui.setItem(6, 7, dev.triumphteam.gui.builder.item.ItemBuilder.from(org.bukkit.Material.ARROW).name(net.kyori.adventure.text.Component.text("§7Next Page")).asGuiItem(event -> {
                event.setCancelled(true);
                gui.next();
            }));
            
            org.bukkit.Material[] randomMaterials = {
                org.bukkit.Material.DIAMOND, org.bukkit.Material.EMERALD, org.bukkit.Material.GOLD_INGOT, 
                org.bukkit.Material.IRON_INGOT, org.bukkit.Material.COPPER_INGOT, org.bukkit.Material.REDSTONE,
                org.bukkit.Material.LAPIS_LAZULI, org.bukkit.Material.COAL
            };
            java.util.Random random = new java.util.Random();

            for (java.util.Map.Entry<String, com.dami.easyCommands.internal.CommandNode> entry : root.entrySet()) {
                if (entry.getKey().equals("*")) continue;
                if (!entry.getValue().isAccessible(sender)) continue;
                
                String subCommandName = entry.getKey();
                // Get the leaf subCommandInfo (might be nested or direct)
                com.dami.easyCommands.internal.SubCommandInfo info = entry.getValue().getSubCommandInfo();
                if (info == null && !entry.getValue().nodes.isEmpty()) {
                     // Try to get info from the first child if current is just a path node
                     info = entry.getValue().nodes.values().iterator().next().getSubCommandInfo();
                }

                org.bukkit.Material iconMaterial;
                if (info != null && info.getGuiIconMaterial() != null && !info.getGuiIconMaterial().isEmpty()) {
                    try {
                        iconMaterial = org.bukkit.Material.valueOf(info.getGuiIconMaterial().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        iconMaterial = randomMaterials[random.nextInt(randomMaterials.length)];
                    }
                } else {
                    iconMaterial = randomMaterials[random.nextInt(randomMaterials.length)];
                }

                dev.triumphteam.gui.builder.item.ItemBuilder itemBuilder = dev.triumphteam.gui.builder.item.ItemBuilder.from(iconMaterial)
                        .name(net.kyori.adventure.text.Component.text("§e/" + getName() + " " + subCommandName));
                        
                if (info != null && info.getDescription() != null && !info.getDescription().isEmpty()) {
                    itemBuilder.lore(net.kyori.adventure.text.Component.text("§7" + info.getDescription()));
                }

                dev.triumphteam.gui.guis.GuiItem guiItem = itemBuilder.asGuiItem(event -> {
                    event.setCancelled(true);
                    player.performCommand(getName() + " " + subCommandName);
                    gui.close(player);
                });
                gui.addItem(guiItem);
            }
            gui.open(player);
        } else {
            super.mainCommand(sender, args);
        }
    }

    public void RegisterSubCommandClass(ICommand command){
        if(registeredSubCommandClasses.contains(command.getClass().getName())){
            return;
        }
        registeredSubCommandClasses.add(command.getClass().getName());
        if(command.getClass().isAnnotationPresent(SubCommand.class)){
            collectSubClassCommands(command);
            collectSubClassTabCompleteMethods(command);
        } else {
            throw new IllegalArgumentException("The class " + command.getClass().getName() + " is not annotated with @SubCommand");
        }
    }

    protected void collectSubClassCommands(ICommand commandClass) {
        SubCommand subCommandClassContext = commandClass.getClass().getAnnotation(SubCommand.class);
        String[] basePath = Stream.concat(
                Arrays.stream(subCommandClassContext.commandPath()),
                Stream.of(subCommandClassContext.name())
        ).filter(s -> s != null && !s.isEmpty()).toArray(String[]::new);

        try {
            Method mainMethod = commandClass.getClass().getMethod("mainCommand", CommandSender.class, String[].class);
            insertCommand(basePath, mainMethod, subCommandClassContext, commandClass);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The class " + commandClass.getClass().getName() + " has no mainCommand method", e);
        }

        for (Method method : commandClass.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(SubCommand.class)) {
                SubCommand sub = method.getAnnotation(SubCommand.class);
                String[] path = Stream.concat(
                        Stream.concat(Arrays.stream(basePath), Arrays.stream(sub.commandPath())),
                        Stream.of(sub.name())
                ).filter(s -> s != null && !s.isEmpty()).toArray(String[]::new);
                if (path.length > 0) {
                    insertCommand(path, method, sub, commandClass);
                }
            }
        }
    }

    protected void collectSubClassTabCompleteMethods(ICommand commandClass) {
        SubCommand subCommandClassContext = commandClass.getClass().getAnnotation(SubCommand.class);
        String[] basePath = Stream.concat(
                Arrays.stream(subCommandClassContext.commandPath()),
                Stream.of(subCommandClassContext.name())
        ).filter(s -> s != null && !s.isEmpty()).toArray(String[]::new);

        try {
            Method tabComplete = commandClass.getClass().getMethod("tabComplete", CommandSender.class, String[].class);
            insertTabComplete(basePath, tabComplete, commandClass, subCommandClassContext.permission(), 100);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The class " + commandClass.getClass().getName() + " has no tabComplete method", e);
        }

        for (Method method : commandClass.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(SubCommandTab.class)) {
                SubCommandTab tab = method.getAnnotation(SubCommandTab.class);
                String[] path = Stream.concat(
                        Stream.concat(Arrays.stream(basePath), Arrays.stream(tab.commandPath())),
                        Stream.of(tab.name())
                ).filter(s -> s != null && !s.isEmpty()).toArray(String[]::new);
                insertTabComplete(path, method, tab, commandClass);
            }
        }
    }
}
