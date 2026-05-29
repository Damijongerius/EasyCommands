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
    private String autoGuiTitle = null;
    private IGuiLayout autoGuiLayout = new DefaultLayout();

    protected ShardableCommand() {
        super();
    }

    public void enableAutoGui() {
        this.autoGuiEnabled = true;
    }

    public void enableAutoGui(String title) {
        this.autoGuiEnabled = true;
        this.autoGuiTitle = title;
    }
    
    public void enableAutoGui(String title, IGuiLayout layout) {
        this.autoGuiEnabled = true;
        this.autoGuiTitle = title;
        this.autoGuiLayout = layout;
    }

    public void disableAutoGui() {
        this.autoGuiEnabled = false;
        this.autoGuiTitle = null;
        this.autoGuiLayout = new DefaultLayout();
    }

    @Override
    public void mainCommand(CommandSender sender, String[] args) {
        if (autoGuiEnabled && sender instanceof org.bukkit.entity.Player) {
            org.bukkit.entity.Player player = (org.bukkit.entity.Player) sender;
            String title = autoGuiTitle != null ? autoGuiTitle : getName() + " Commands";
            dev.triumphteam.gui.guis.PaginatedGui gui = dev.triumphteam.gui.guis.Gui.paginated()
                    .title(net.kyori.adventure.text.Component.text(title))
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

            java.util.List<java.util.Map.Entry<String, com.dami.easyCommands.internal.CommandNode>> sortedEntries = new java.util.ArrayList<>(root.entrySet());
            sortedEntries.sort(java.util.Map.Entry.comparingByKey());

            java.util.List<dev.triumphteam.gui.guis.GuiItem> layoutItems = new java.util.ArrayList<>();

            for (java.util.Map.Entry<String, com.dami.easyCommands.internal.CommandNode> entry : sortedEntries) {
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
                boolean onCooldown = false;
                long remainingCooldown = 0;
                String fullPath = getName() + " " + subCommandName;
                
                boolean isLocked = false;
                String lockReason = "";
                
                if (info != null && info.getRequires().length > 0) {
                    for (String req : info.getRequires()) {
                        com.dami.easyCommands.model.Condition condition = com.dami.easyCommands.core.ConditionRegistry.get(req);
                        if (condition != null) {
                            try {
                                condition.check(player);
                            } catch (com.dami.easyCommands.model.ValidationException ex) {
                                isLocked = true;
                                lockReason = req;
                                break;
                            }
                        }
                    }
                }
                
                if (info != null && info.getCooldownSeconds() > 0) {
                    String bypass = info.getCooldownBypassPerm().isEmpty() ? fullPath.replace(" ", ".") + ".bypass.cooldown" : info.getCooldownBypassPerm();
                    if (!player.hasPermission(bypass)) {
                        if (com.dami.easyCommands.internal.CooldownManager.isOnCooldown(fullPath, player.getUniqueId())) {
                            onCooldown = true;
                            remainingCooldown = com.dami.easyCommands.internal.CooldownManager.getRemainingSeconds(fullPath, player.getUniqueId());
                        }
                    }
                }

                if (isLocked) {
                    iconMaterial = org.bukkit.Material.BARRIER;
                } else if (onCooldown) {
                    iconMaterial = org.bukkit.Material.CLOCK;
                } else if (info != null && info.getGuiIconMaterial() != null && !info.getGuiIconMaterial().isEmpty()) {
                    try {
                        iconMaterial = org.bukkit.Material.valueOf(info.getGuiIconMaterial().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        iconMaterial = randomMaterials[Math.abs(subCommandName.hashCode()) % randomMaterials.length];
                    }
                } else {
                    iconMaterial = randomMaterials[Math.abs(subCommandName.hashCode()) % randomMaterials.length];
                }

                // Capitalize the subcommand name for display
                String displayName = subCommandName.substring(0, 1).toUpperCase() + subCommandName.substring(1);

                dev.triumphteam.gui.builder.item.ItemBuilder itemBuilder = dev.triumphteam.gui.builder.item.ItemBuilder.from(iconMaterial)
                        .name(net.kyori.adventure.text.Component.text("§e/" + getName() + " " + displayName));
                        
                if (isLocked) {
                    itemBuilder.lore(net.kyori.adventure.text.Component.text("§c🔒 Locked: " + lockReason));
                } else if (onCooldown) {
                    itemBuilder.lore(net.kyori.adventure.text.Component.text("§c⏳ On Cooldown (" + remainingCooldown + "s)"));
                }
                        
                if (info != null && info.getDescription() != null && !info.getDescription().isEmpty()) {
                    itemBuilder.lore(net.kyori.adventure.text.Component.text("§7" + info.getDescription()));
                }
                
                if (info != null && info.getMaxArgs() > 0) {
                    String usageStr = info.getUsage() != null && !info.getUsage().isEmpty() ? info.getUsage() : "<arguments...>";
                    itemBuilder.lore(net.kyori.adventure.text.Component.text("§cRequires: " + usageStr));
                    itemBuilder.lore(net.kyori.adventure.text.Component.text("§8(Click to enter arguments)"));
                }

                final com.dami.easyCommands.internal.SubCommandInfo finalInfo = info;
                final String finalSubCommandName = subCommandName;
                final boolean finalIsLocked = isLocked;

                dev.triumphteam.gui.guis.GuiItem guiItem = itemBuilder.asGuiItem(event -> {
                    event.setCancelled(true);
                    gui.close(player);
                    
                    if (finalIsLocked) {
                        player.performCommand(getName() + " " + finalSubCommandName);
                        return;
                    }
                    
                    if (finalInfo != null && finalInfo.getMaxArgs() > 0) {
                        try {
                            java.lang.reflect.Method m = finalInfo.getClass().getDeclaredMethod("getMethod");
                            m.setAccessible(true);
                            java.lang.reflect.Method cmdMethod = (java.lang.reflect.Method) m.invoke(finalInfo);
                            
                            java.util.List<String> suggestions = com.dami.easyCommands.internal.CompletionResolver.resolve(
                                    cmdMethod, player, new String[]{""}, null, finalInfo.getCompletions());
                            
                            if (suggestions != null && !suggestions.isEmpty()) {
                                dev.triumphteam.gui.guis.PaginatedGui subGui = dev.triumphteam.gui.guis.Gui.paginated()
                                        .title(net.kyori.adventure.text.Component.text("Select Argument"))
                                        .rows(6)
                                        .create();
                                        
                                subGui.setItem(6, 3, dev.triumphteam.gui.builder.item.ItemBuilder.from(org.bukkit.Material.ARROW).name(net.kyori.adventure.text.Component.text("§7Previous Page")).asGuiItem(e -> { e.setCancelled(true); subGui.previous(); }));
                                subGui.setItem(6, 7, dev.triumphteam.gui.builder.item.ItemBuilder.from(org.bukkit.Material.ARROW).name(net.kyori.adventure.text.Component.text("§7Next Page")).asGuiItem(e -> { e.setCancelled(true); subGui.next(); }));
                                        
                                for (String suggestion : suggestions) {
                                    dev.triumphteam.gui.guis.GuiItem subItem = dev.triumphteam.gui.builder.item.ItemBuilder.from(org.bukkit.Material.PAPER)
                                            .name(net.kyori.adventure.text.Component.text("§a" + suggestion))
                                            .asGuiItem(e -> {
                                                e.setCancelled(true);
                                                subGui.close(player);
                                                player.performCommand(getName() + " " + finalSubCommandName + " " + suggestion);
                                            });
                                    subGui.addItem(subItem);
                                }
                                subGui.open(player);
                                return;
                            }
                        } catch (Exception ex) {}

                        player.sendMessage("§ePlease type the missing arguments in chat for: §a/" + getName() + " " + finalSubCommandName);
                        org.bukkit.plugin.Plugin plugin = org.bukkit.plugin.java.JavaPlugin.getProvidingPlugin(getClass());
                        com.dami.easyCommands.internal.ChatPromptManager.prompt(plugin, player, response -> {
                            player.performCommand(getName() + " " + finalSubCommandName + " " + response);
                        });
                    } else {
                        player.performCommand(getName() + " " + finalSubCommandName);
                    }
                });
                
                if (info != null && info.getGuiRow() > 0 && info.getGuiCol() > 0) {
                    gui.setItem(info.getGuiRow(), info.getGuiCol(), guiItem);
                } else {
                    layoutItems.add(guiItem);
                }
            }
            
            if (autoGuiLayout != null) {
                autoGuiLayout.apply(gui, layoutItems);
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
