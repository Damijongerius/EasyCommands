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

    public static class DirectoryGuiMetadata {
        private final String iconMaterial;
        private final String description;
        private final int row;
        private final int col;

        public DirectoryGuiMetadata(String iconMaterial, String description, int row, int col) {
            this.iconMaterial = iconMaterial;
            this.description = description;
            this.row = row;
            this.col = col;
        }

        public String getIconMaterial() { return iconMaterial; }
        public String getDescription() { return description; }
        public int getRow() { return row; }
        public int getCol() { return col; }
    }

    private final java.util.Map<String, DirectoryGuiMetadata> directoryMetadata = new java.util.HashMap<>();

    public void setDirectoryGuiMetadata(String path, String iconMaterial, String description) {
        directoryMetadata.put(path.toLowerCase(), new DirectoryGuiMetadata(iconMaterial, description, 0, 0));
    }

    public void setDirectoryGuiMetadata(String path, String iconMaterial, String description, int row, int col) {
        directoryMetadata.put(path.toLowerCase(), new DirectoryGuiMetadata(iconMaterial, description, row, col));
    }

    private org.bukkit.inventory.ItemStack createCustomIcon(String iconStr) {
        if (iconStr == null || iconStr.isEmpty()) {
            return null;
        }
        
        if (iconStr.startsWith("eyJ") || iconStr.length() > 50) {
            org.bukkit.inventory.ItemStack head = new org.bukkit.inventory.ItemStack(org.bukkit.Material.PLAYER_HEAD);
            try {
                org.bukkit.inventory.meta.SkullMeta skullMeta = (org.bukkit.inventory.meta.SkullMeta) head.getItemMeta();
                if (skullMeta != null) {
                    com.destroystokyo.paper.profile.PlayerProfile profile = org.bukkit.Bukkit.createProfile(java.util.UUID.randomUUID());
                    profile.getProperties().add(new com.destroystokyo.paper.profile.ProfileProperty("textures", iconStr));
                    skullMeta.setPlayerProfile(profile);
                    head.setItemMeta(skullMeta);
                }
            } catch (Exception ignored) {}
            return head;
        }
        
        try {
            org.bukkit.Material mat = org.bukkit.Material.valueOf(iconStr.toUpperCase());
            return new org.bukkit.inventory.ItemStack(mat);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void runCommandNextTick(org.bukkit.entity.Player player, String command) {
        org.bukkit.plugin.Plugin plugin = org.bukkit.Bukkit.getPluginManager().getPlugin("EpicKingdom");
        if (plugin != null && plugin.isEnabled()) {
            org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
                player.performCommand(command);
            });
        } else {
            player.performCommand(command);
        }
    }

    protected final List<String> registeredSubCommandClasses = new ArrayList<>();
    private boolean autoGuiEnabled = false;
    private String autoGuiTitle = null;
    private IGuiLayout autoGuiLayout = new DefaultLayout();

    public boolean isAutoGuiEnabled() {
        return autoGuiEnabled;
    }

    public String getAutoGuiTitle() {
        return autoGuiTitle;
    }

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
            openCommandNodeGui(player, "", title);
        } else {
            super.mainCommand(sender, args);
        }
    }

    public void openCommandNodeGui(org.bukkit.entity.Player player, String currentPath, String title) {
        java.util.Map<String, com.dami.easyCommands.internal.CommandNode> currentNodes = getNodesForPath(currentPath);
        if (currentNodes == null || currentNodes.isEmpty()) {
            player.sendMessage("§cNo commands found in this menu.");
            return;
        }

        java.util.List<java.util.Map.Entry<String, com.dami.easyCommands.internal.CommandNode>> sortedEntries = new java.util.ArrayList<>(currentNodes.entrySet());
        sortedEntries.sort(java.util.Map.Entry.comparingByKey());

        java.util.List<dev.triumphteam.gui.guis.GuiItem> layoutItems = new java.util.ArrayList<>();
        
        class PinnedItem {
            int row;
            int col;
            dev.triumphteam.gui.guis.GuiItem item;
            PinnedItem(int row, int col, dev.triumphteam.gui.guis.GuiItem item) {
                this.row = row;
                this.col = col;
                this.item = item;
            }
        }
        java.util.List<PinnedItem> pinnedItems = new java.util.ArrayList<>();
        int maxExplicitRow = 1;

        org.bukkit.Material[] randomMaterials = {
            org.bukkit.Material.DIAMOND, org.bukkit.Material.EMERALD, org.bukkit.Material.GOLD_INGOT, 
            org.bukkit.Material.IRON_INGOT, org.bukkit.Material.COPPER_INGOT, org.bukkit.Material.REDSTONE,
            org.bukkit.Material.LAPIS_LAZULI, org.bukkit.Material.COAL
        };

        final dev.triumphteam.gui.guis.PaginatedGui[] guiHolder = new dev.triumphteam.gui.guis.PaginatedGui[1];

        for (java.util.Map.Entry<String, com.dami.easyCommands.internal.CommandNode> entry : sortedEntries) {
            if (entry.getKey().equals("*")) continue;
            if (!entry.getValue().isAccessible(player)) continue;

            String subCommandName = entry.getKey();
            com.dami.easyCommands.internal.CommandNode node = entry.getValue();
            com.dami.easyCommands.internal.SubCommandInfo info = node.getSubCommandInfo();

            if (info != null && info.getAliases() != null) {
                boolean isAlias = false;
                for (String alias : info.getAliases()) {
                    if (subCommandName.equalsIgnoreCase(alias)) {
                        isAlias = true;
                        break;
                    }
                }
                if (isAlias) continue;
            }

            String fullCommandPath = currentPath.isEmpty() ? subCommandName : currentPath + " " + subCommandName;
            String fullExecutablePath = getName() + " " + fullCommandPath;

            org.bukkit.Material iconMaterial = null;
            org.bukkit.inventory.ItemStack customItemStack = null;
            boolean isLocked = false;
            String lockReason = "";
            boolean onCooldown = false;
            long remainingCooldown = 0;

            if (info != null) {
                // Leaf command logic
                if (info.getRequires().length > 0) {
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
                
                if (info.getCooldownSeconds() > 0) {
                    String bypass = info.getCooldownBypassPerm().isEmpty() ? fullExecutablePath.replace(" ", ".") + ".bypass.cooldown" : info.getCooldownBypassPerm();
                    if (!player.hasPermission(bypass)) {
                        if (com.dami.easyCommands.internal.CooldownManager.isOnCooldown(fullExecutablePath, player.getUniqueId())) {
                            onCooldown = true;
                            remainingCooldown = com.dami.easyCommands.internal.CooldownManager.getRemainingSeconds(fullExecutablePath, player.getUniqueId());
                        }
                    }
                }

                if (isLocked) {
                    iconMaterial = org.bukkit.Material.BARRIER;
                } else if (onCooldown) {
                    iconMaterial = org.bukkit.Material.CLOCK;
                } else if (info.getGuiIconMaterial() != null && !info.getGuiIconMaterial().isEmpty()) {
                    customItemStack = createCustomIcon(info.getGuiIconMaterial());
                    if (customItemStack == null) {
                        iconMaterial = randomMaterials[Math.abs(subCommandName.hashCode()) % randomMaterials.length];
                    }
                } else {
                    iconMaterial = randomMaterials[Math.abs(subCommandName.hashCode()) % randomMaterials.length];
                }
            } else {
                // Directory/Folder logic
                DirectoryGuiMetadata dirMeta = directoryMetadata.get(fullCommandPath.toLowerCase());
                if (dirMeta != null) {
                    customItemStack = createCustomIcon(dirMeta.getIconMaterial());
                }
                if (customItemStack == null) {
                    iconMaterial = org.bukkit.Material.CHEST; // Default for directories
                }
            }

            String displayName = subCommandName.substring(0, 1).toUpperCase() + subCommandName.substring(1);

            dev.triumphteam.gui.builder.item.ItemBuilder itemBuilder;
            if (customItemStack != null) {
                itemBuilder = dev.triumphteam.gui.builder.item.ItemBuilder.from(customItemStack)
                        .name(net.kyori.adventure.text.Component.text("§e/" + getName() + " " + (currentPath.isEmpty() ? "" : currentPath + " ") + displayName));
            } else {
                itemBuilder = dev.triumphteam.gui.builder.item.ItemBuilder.from(iconMaterial)
                        .name(net.kyori.adventure.text.Component.text("§e/" + getName() + " " + (currentPath.isEmpty() ? "" : currentPath + " ") + displayName));
            }

            if (info != null) {
                if (isLocked) {
                    itemBuilder.lore(net.kyori.adventure.text.Component.text("§c🔒 Locked: " + lockReason));
                } else if (onCooldown) {
                    itemBuilder.lore(net.kyori.adventure.text.Component.text("§c⏳ On Cooldown (" + remainingCooldown + "s)"));
                }
                if (info.getDescription() != null && !info.getDescription().isEmpty()) {
                    itemBuilder.lore(net.kyori.adventure.text.Component.text("§7" + info.getDescription()));
                }
                if (info.getAliases() != null && info.getAliases().length > 0) {
                    itemBuilder.lore(net.kyori.adventure.text.Component.text("§8Aliases: " + String.join(", ", info.getAliases())));
                }
                if (info.getRequiredArgs() > 0) {
                    String usageStr = info.getUsage() != null && !info.getUsage().isEmpty() ? info.getUsage() : "<arguments...>";
                    itemBuilder.lore(net.kyori.adventure.text.Component.text("§cRequires: " + usageStr));
                    itemBuilder.lore(net.kyori.adventure.text.Component.text("§8(Click to enter arguments)"));
                }
                itemBuilder.lore(net.kyori.adventure.text.Component.text("§8(Right-Click/Shift-Click to edit in chat)"));
            } else {
                DirectoryGuiMetadata dirMeta = directoryMetadata.get(fullCommandPath.toLowerCase());
                if (dirMeta != null && dirMeta.getDescription() != null && !dirMeta.getDescription().isEmpty()) {
                    itemBuilder.lore(net.kyori.adventure.text.Component.text("§7" + dirMeta.getDescription()));
                } else {
                    itemBuilder.lore(net.kyori.adventure.text.Component.text("§7Sub-menu: View options for /" + getName() + " " + fullCommandPath));
                }
                java.util.List<String> childNames = new java.util.ArrayList<>();
                for (java.util.Map.Entry<String, com.dami.easyCommands.internal.CommandNode> childEntry : node.nodes.entrySet()) {
                    String childName = childEntry.getKey();
                    if (childName.equals("*")) continue;
                    
                    com.dami.easyCommands.internal.SubCommandInfo childInfo = childEntry.getValue().getSubCommandInfo();
                    if (childInfo != null && childInfo.getAliases() != null) {
                        boolean isChildAlias = false;
                        for (String alias : childInfo.getAliases()) {
                            if (childName.equalsIgnoreCase(alias)) {
                                isChildAlias = true;
                                break;
                            }
                        }
                        if (isChildAlias) continue;
                    }
                    childNames.add(childName);
                }
                if (!childNames.isEmpty()) {
                    itemBuilder.lore(net.kyori.adventure.text.Component.text("§8Options: " + String.join(", ", childNames)));
                }
            }

            final com.dami.easyCommands.internal.SubCommandInfo finalInfo = info;
            final String finalSubCommandName = subCommandName;
            final boolean finalIsLocked = isLocked;
            final String finalFullCommandPath = fullCommandPath;
            final String finalFullExecutablePath = fullExecutablePath;

            dev.triumphteam.gui.guis.GuiItem guiItem = itemBuilder.asGuiItem(event -> {
                event.setCancelled(true);

                // Check for Right-Click / Shift-Click to suggest in chat
                if (event.isRightClick() || event.isShiftClick()) {
                    if (guiHolder[0] != null) {
                        guiHolder[0].close(player);
                    }
                    net.kyori.adventure.text.Component component = net.kyori.adventure.text.Component.text("§e[EasyCommands] Click to edit command: §a/" + finalFullExecutablePath)
                            .clickEvent(net.kyori.adventure.text.event.ClickEvent.suggestCommand("/" + finalFullExecutablePath + " "));
                    player.sendMessage(component);
                    return;
                }

                if (finalInfo == null) {
                    // Open sub-menu for directory
                    openCommandNodeGui(player, finalFullCommandPath, title);
                    return;
                }

                if (finalIsLocked) {
                    if (guiHolder[0] != null) {
                        guiHolder[0].close(player);
                    }
                    runCommandNextTick(player, finalFullExecutablePath);
                    return;
                }

                if (finalInfo.getRequiredArgs() > 0) {
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
                                             runCommandNextTick(player, finalFullExecutablePath + " " + suggestion);
                                         });
                                subGui.addItem(subItem);
                            }
                            subGui.open(player);
                            return;
                        }
                    } catch (Exception ex) {}

                    if (guiHolder[0] != null) {
                        guiHolder[0].close(player);
                    }
                    String usageStr = finalInfo.getUsage() != null && !finalInfo.getUsage().isEmpty() ? finalInfo.getUsage() : null;
                    com.dami.easyCommands.internal.ChatCaptureManager.startCapture(player, finalFullExecutablePath, usageStr);
                } else {
                    if (guiHolder[0] != null) {
                        guiHolder[0].close(player);
                    }
                    runCommandNextTick(player, finalFullExecutablePath);
                }
            });

            int explicitRow = 0;
            int explicitCol = 0;
            if (info != null) {
                explicitRow = info.getGuiRow();
                explicitCol = info.getGuiCol();
            } else {
                DirectoryGuiMetadata dirMeta = directoryMetadata.get(fullCommandPath.toLowerCase());
                if (dirMeta != null) {
                    explicitRow = dirMeta.getRow();
                    explicitCol = dirMeta.getCol();
                }
            }

            if (explicitRow > 0 && explicitCol > 0) {
                pinnedItems.add(new PinnedItem(explicitRow, explicitCol, guiItem));
                if (explicitRow > maxExplicitRow) {
                    maxExplicitRow = explicitRow;
                }
            } else {
                layoutItems.add(guiItem);
            }
        }

        int itemCount = layoutItems.size() + pinnedItems.size();
        int requiredRows = (int) Math.ceil(itemCount / 9.0);
        if (maxExplicitRow > requiredRows) {
            requiredRows = maxExplicitRow;
        }
        
        boolean needsPagination = false;
        if (itemCount > 54) {
            needsPagination = true;
            requiredRows = 6;
        }
        
        if (!currentPath.isEmpty() && requiredRows < 6 && (itemCount % 9 == 0 || (itemCount + 1) / 9.0 > requiredRows)) {
            requiredRows++;
        }
        
        if (requiredRows < 1) requiredRows = 1;
        if (requiredRows > 6) requiredRows = 6;

        dev.triumphteam.gui.guis.PaginatedGui gui = dev.triumphteam.gui.guis.Gui.paginated()
                .title(net.kyori.adventure.text.Component.text(title))
                .rows(requiredRows)
                .create();
        guiHolder[0] = gui;

        if (needsPagination || requiredRows == 6) {
            gui.setItem(requiredRows, 3, dev.triumphteam.gui.builder.item.ItemBuilder.from(org.bukkit.Material.ARROW).name(net.kyori.adventure.text.Component.text("§7Previous Page")).asGuiItem(event -> {
                event.setCancelled(true);
                gui.previous();
            }));

            gui.setItem(requiredRows, 7, dev.triumphteam.gui.builder.item.ItemBuilder.from(org.bukkit.Material.ARROW).name(net.kyori.adventure.text.Component.text("§7Next Page")).asGuiItem(event -> {
                event.setCancelled(true);
                gui.next();
            }));
        }

        if (!currentPath.isEmpty()) {
            String parentPath;
            int lastSpace = currentPath.lastIndexOf(' ');
            if (lastSpace != -1) {
                parentPath = currentPath.substring(0, lastSpace);
            } else {
                parentPath = "";
            }
            gui.setItem(requiredRows, 1, dev.triumphteam.gui.builder.item.ItemBuilder.from(org.bukkit.Material.REDSTONE)
                    .name(net.kyori.adventure.text.Component.text("§cGo Back"))
                    .lore(net.kyori.adventure.text.Component.text("§7Return to the previous menu."))
                    .asGuiItem(event -> {
                        event.setCancelled(true);
                        openCommandNodeGui(player, parentPath, title);
                    }));
        }

        for (PinnedItem pi : pinnedItems) {
            int actualRow = pi.row;
            if (actualRow > requiredRows) actualRow = requiredRows;
            gui.setItem(actualRow, pi.col, pi.item);
        }
        
        if (autoGuiLayout != null) {
            autoGuiLayout.apply(gui, layoutItems);
        }
        gui.open(player);
    }

    private java.util.Map<String, com.dami.easyCommands.internal.CommandNode> getNodesForPath(String path) {
        if (path.isEmpty()) return root;
        String[] parts = path.split(" ");
        java.util.Map<String, com.dami.easyCommands.internal.CommandNode> current = root;
        for (String part : parts) {
            com.dami.easyCommands.internal.CommandNode nextNode = current.get(part);
            if (nextNode == null) return null;
            current = nextNode.nodes;
        }
        return current;
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
