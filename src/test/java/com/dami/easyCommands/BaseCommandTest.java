package com.dami.easyCommands;

import com.dami.easyCommands.commands.AmazingBaseCommand;
import com.dami.easyCommands.mocks.MockCommand;
import com.dami.easyCommands.mocks.MockCommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BaseCommandTest {

    private AmazingBaseCommand command;
    private MockCommandSender sender;
    private MockCommand bukkitCommand;

    @BeforeEach
    public void setup() {
        command = new AmazingBaseCommand();
        sender = new MockCommandSender();
        bukkitCommand = new MockCommand("amazing");
    }

    @Test
    public void testMainCommandExecution() {
        command.onCommand(sender, bukkitCommand, "amazing", new String[]{});
        System.out.printf("Messages: %s%n", sender.getMessages());
        assertTrue(sender.getMessages().contains("Amazing Command Executed"));
    }

    @Test
    public void testSubCommandExecution() {
        command.onCommand(sender, bukkitCommand, "amazing", new String[]{"subexample"});
        System.out.println("Messages: " + sender.getMessages());
        assertTrue(sender.getMessages().contains("Subexample Command Executed"));
    }

    @Test
    public void testNestedSubCommandExecution() {
        command.onCommand(sender, bukkitCommand, "amazing", new String[]{"subexample", "one"});
        System.out.println("Messages: " + sender.getMessages());
        assertTrue(sender.getMessages().contains("Subexample One Command Executed"));
    }

    @Test
    public void testInvalidSubCommand() {
        boolean result = command.onCommand(sender, bukkitCommand, "amazing", new String[]{"nonexistent"});
        System.out.println("Messages: " + sender.getMessages());
        assertFalse(result, "Should return false for non-existent subcommand");
    }

    @Test
    public void testSerialization() {
        String yaml = command.ConvertToObject();
        assertNotNull(yaml);
        assertTrue(yaml.contains("subexample"));
        assertTrue(yaml.contains("subexample2"));
    }
}
