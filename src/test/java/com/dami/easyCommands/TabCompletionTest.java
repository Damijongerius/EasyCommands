package com.dami.easyCommands;

import com.dami.easyCommands.commands.AmazingBaseCommand;
import com.dami.easyCommands.mocks.MockCommand;
import com.dami.easyCommands.mocks.MockCommandSender;
import com.dami.easyCommands.EasyCommands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TabCompletionTest {

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
    public void testRootTabComplete() {
        // When typing /amazing <tab>
        List<String> suggestions = command.onTabComplete(sender, bukkitCommand, "amazing", new String[]{""});

        System.out.println("Tab Suggestions: " + suggestions);
        assertNotNull(suggestions);
        assertTrue(suggestions.contains("subexample"));
        assertTrue(suggestions.contains("subexample2"));
        // BaseCommand.onTabComplete also includes results from the overridden tabComplete method
        assertTrue(suggestions.contains("base1"));
        assertTrue(suggestions.contains("base2"));
    }

    @Test
    public void testSubCommandTabComplete() {
        // When typing /amazing subexample <tab>
        List<String> suggestions = command.onTabComplete(sender, bukkitCommand, "amazing", new String[]{"subexample", ""});

        System.out.println("Tab Suggestions for subexample: " + suggestions);
        assertNotNull(suggestions);
        assertTrue(suggestions.contains("one"));
        // It should also contain results from subCommandExampleTab if it was registered for the node itself?
        // Wait, how does BaseCommand handle this?
        // If args.length == 2 ("subexample", ""), it gets CommandNode for "subexample"
        // and calls commandNode.getTabComplete([""], sender)
    }

    @Test
    public void testNestedSubCommandTabComplete() {
        // When typing /amazing subexample one <tab>
        List<String> suggestions = command.onTabComplete(sender, bukkitCommand, "amazing", new String[]{"subexample", "one", ""});

        System.out.println("Tab Suggestions for subexample one: " + suggestions);
        assertNotNull(suggestions);
        assertTrue(suggestions.contains("four"));
        assertTrue(suggestions.contains("five"));
        assertTrue(suggestions.contains("six"));
    }

    @Test
    public void testInvalidSubCommandTabComplete() {
        List<String> suggestions = command.onTabComplete(sender, bukkitCommand, "amazing", new String[]{"invalid", ""});
        assertNull(suggestions);
    }

    @Test
    public void testCustomCompletionsArray() {
        EasyCommands.registerCompletion("@test", s -> List.of("custom1", "custom2"));
        
        List<String> suggestions = command.onTabComplete(sender, bukkitCommand, "amazing", new String[]{"testcompletions", ""});
        
        assertNotNull(suggestions);
        assertTrue(suggestions.contains("custom1"));
        assertTrue(suggestions.contains("custom2"));
    }
}
