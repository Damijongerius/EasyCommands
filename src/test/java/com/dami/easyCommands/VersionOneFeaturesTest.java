package com.dami.easyCommands;

import com.dami.easyCommands.annotations.Require;
import com.dami.easyCommands.annotations.Sender;
import com.dami.easyCommands.annotations.SubCommand;
import com.dami.easyCommands.core.BaseCommand;
import com.dami.easyCommands.mocks.MockCommand;
import com.dami.easyCommands.mocks.MockCommandSender;
import com.dami.easyCommands.model.MessageKey;
import com.dami.easyCommands.model.ValidationException;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VersionOneFeaturesTest {

    public static class CustomSender {
        public String name;
        public CustomSender(String name) { this.name = name; }
    }

    public static class CustomException extends RuntimeException {
        public CustomException(String msg) { super(msg); }
    }

    public static class V1Command extends BaseCommand {
        @Override
        public String getName() { return "v1"; }

        @SubCommand(commandPath = {}, name = "customsender")
        public void customSenderCommand(CommandSender bukkitSender, @Sender CustomSender customSender) {
            bukkitSender.sendMessage("Custom sender name: " + customSender.name);
        }

        @SubCommand(commandPath = {}, name = "condition")
        @Require("test_condition")
        public void conditionCommand(CommandSender sender) {
            sender.sendMessage("Condition passed!");
        }

        @SubCommand(commandPath = {}, name = "error")
        public void errorCommand(CommandSender sender) {
            throw new CustomException("This is a custom error!");
        }

        @SubCommand(commandPath = {}, name = "dangerous")
        @com.dami.easyCommands.annotations.Confirm(timeout = 5)
        public void dangerousCommand(CommandSender sender) {
            sender.sendMessage("Dangerous action executed!");
        }
    }

    private V1Command command;
    private MockCommandSender sender;
    private MockCommand bukkitCommand;

    @BeforeEach
    public void setup() {
        command = new V1Command();
        sender = new MockCommandSender();
        sender.setName("TestUser");
        bukkitCommand = new MockCommand("v1");

        EasyCommands.registerSenderResolver(CustomSender.class, s -> new CustomSender(s.getName()));
        
        EasyCommands.registerCondition("test_condition", s -> {
            if (s.getName().equals("reject_me")) {
                java.util.Map<String, String> placeholders = new HashMap<>();
                throw new ValidationException(MessageKey.NO_PERMISSION, placeholders);
            }
        });

        EasyCommands.registerExceptionHandler(CustomException.class, (s, ex) -> {
            s.sendMessage("Caught custom: " + ex.getMessage());
        });
    }

    @Test
    public void testSenderInjection() {
        command.onCommand(sender, bukkitCommand, "v1", new String[]{"customsender"});
        assertTrue(sender.getMessages().stream().anyMatch(m -> m.contains("Custom sender name: TestUser")), "Should inject custom sender");
    }

    @Test
    public void testConditionPassed() {
        command.onCommand(sender, bukkitCommand, "v1", new String[]{"condition"});
        assertTrue(sender.getMessages().stream().anyMatch(m -> m.contains("Condition passed!")), "Should pass condition");
    }

    @Test
    public void testConditionRejected() {
        sender.setName("reject_me");
        command.onCommand(sender, bukkitCommand, "v1", new String[]{"condition"});
        assertTrue(sender.getMessages().stream().anyMatch(m -> m.contains("You don't have permission")), "Should be rejected by condition. Messages: " + sender.getMessages());
    }

    @Test
    public void testGlobalExceptionHandler() {
        command.onCommand(sender, bukkitCommand, "v1", new String[]{"error"});
        assertTrue(sender.getMessages().stream().anyMatch(m -> m.contains("Caught custom: This is a custom error!")), "Should catch custom exception");
    }

    @Test
    public void testConfirmAnnotation() {
        java.util.UUID mockUUID = java.util.UUID.randomUUID();
        org.bukkit.entity.Player mockPlayer = (org.bukkit.entity.Player) java.lang.reflect.Proxy.newProxyInstance(
                org.bukkit.entity.Player.class.getClassLoader(),
                new Class[]{org.bukkit.entity.Player.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getUniqueId")) return mockUUID;
                    if (method.getName().equals("sendMessage")) {
                        return method.invoke(sender, args);
                    }
                    if (method.getName().equals("hasPermission")) return true;
                    if (method.getName().equals("getName")) return "TestPlayer";
                    return null;
                }
        );

        command.onCommand(mockPlayer, bukkitCommand, "v1", new String[]{"dangerous"});
        assertTrue(sender.getMessages().stream().anyMatch(m -> m.contains("Are you sure?")), "Should prompt for confirmation");

        sender.getMessages().clear();
        command.onCommand(mockPlayer, bukkitCommand, "v1", new String[]{"dangerous"});
        assertTrue(sender.getMessages().stream().anyMatch(m -> m.contains("Dangerous action executed!")), "Should execute on second run");
    }
}
