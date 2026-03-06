package com.dami.easyCommands;

import com.dami.easyCommands.commands.AmazingBaseCommand;
import com.dami.easyCommands.commands.ExampleSubCommand;
import com.dami.easyCommands.mocks.MockCommand;
import com.dami.easyCommands.mocks.MockCommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubCommandClassTest {

    private AmazingBaseCommand command;
    private MockCommandSender sender;
    private MockCommand bukkitCommand;
    private ExampleSubCommand subCommandClass;

    @BeforeEach
    public void setup() {
        command = new AmazingBaseCommand();
        sender = new MockCommandSender();
        bukkitCommand = new MockCommand("amazing");
        subCommandClass = new ExampleSubCommand();
        
        command.RegisterSubCommandClass(subCommandClass);
    }

    @Test
    public void testSubCommandClassExecution() {
        command.onCommand(sender, bukkitCommand, "amazing", new String[]{"admin", "unalive"});
        assertTrue(sender.getMessages().contains("Example Sub Command Executed"));
    }

    @Test
    public void testSubCommandClassTabCompletion() {
        List<String> result = command.onTabComplete(sender, bukkitCommand, "amazing", new String[]{"admin", ""});
        assertNotNull(result);
        assertTrue(result.contains("unalive"));
    }
}
