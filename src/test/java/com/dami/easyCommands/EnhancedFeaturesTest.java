package com.dami.easyCommands;

import com.dami.easyCommands.commands.TestCommand;
import com.dami.easyCommands.mocks.MockCommand;
import com.dami.easyCommands.mocks.MockCommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnhancedFeaturesTest {

    private TestCommand command;
    private MockCommandSender sender;
    private MockCommand bukkitCommand;

    @BeforeEach
    public void setup() {
        command = new TestCommand();
        sender = new MockCommandSender();
        bukkitCommand = new MockCommand("root");
        
        com.dami.easyCommands.internal.ParameterResolver.registerConverter(org.bukkit.entity.Player.class, s -> {
            return (org.bukkit.entity.Player) java.lang.reflect.Proxy.newProxyInstance(
                    org.bukkit.entity.Player.class.getClassLoader(),
                    new Class[]{org.bukkit.entity.Player.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("getName")) return s;
                        if (method.getName().equals("toString")) return "MockPlayer[" + s + "]";
                        return null;
                    }
            );
        });
    }

    @Test
    public void testParameterInjection() {
        command.onCommand(sender, bukkitCommand, "root", new String[]{"give", "Dami", "64"});
        assertTrue(sender.getMessages().stream().anyMatch(m -> m.contains("Giving 64 to")), "Should have giving message. Messages: " + sender.getMessages());
    }

    @Test
    public void testAliases() {
        command.onCommand(sender, bukkitCommand, "root", new String[]{"tp", "Dami"});
        assertTrue(sender.getMessages().stream().anyMatch(m -> m.contains("Teleporting to")), "Should work with tp alias. Messages: " + sender.getMessages());
        
        sender.getMessages().clear();
        command.onCommand(sender, bukkitCommand, "root", new String[]{"goto", "Dami"});
        assertTrue(sender.getMessages().stream().anyMatch(m -> m.contains("Teleporting to")), "Should work with goto alias. Messages: " + sender.getMessages());
    }

    @Test
    public void testSenderRequirements() {
        command.onCommand(sender, bukkitCommand, "root", new String[]{"playeronly"});
        assertTrue(sender.getMessages().stream().anyMatch(m -> m.contains("Only players can use this command")), "Should enforce player requirement. Messages: " + sender.getMessages());
    }

    @Test
    public void testHelpGeneration() {
        command.onCommand(sender, bukkitCommand, "root", new String[]{"help"});
        assertTrue(sender.getMessages().stream().anyMatch(m -> m.contains("Give items")), "Should contain give description");
        assertTrue(sender.getMessages().stream().anyMatch(m -> m.contains("Teleport to player")), "Should contain teleport description");
        assertTrue(sender.getMessages().stream().anyMatch(m -> m.contains("EasyCommands Help: /root")), "Should contain help header. Messages: " + sender.getMessages());
    }
}



