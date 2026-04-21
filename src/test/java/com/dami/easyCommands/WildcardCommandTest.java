package com.dami.easyCommands;

import com.dami.easyCommands.annotations.SubCommand;
import com.dami.easyCommands.core.ShardableCommand;
import com.dami.easyCommands.mocks.MockCommand;
import com.dami.easyCommands.mocks.MockCommandSender;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WildcardCommandTest {

    private ShardableCommand command;
    private MockCommandSender sender;
    private MockCommand bukkitCommand;

    @BeforeEach
    public void setUp() {
        sender = new MockCommandSender();
        bukkitCommand = new MockCommand("wild");
        command = new ShardableCommand() {
            @Override
            public String getName() {
                return "wild";
            }

            @Override
            public void mainCommand(CommandSender sender, String[] args) {
                sender.sendMessage("Main command");
            }

            @SubCommand(name = "static")
            public void staticCommand(CommandSender sender, String[] args) {
                sender.sendMessage("Static command");
            }

            @SubCommand(name = "*")
            public void wildcardRoot(CommandSender sender, String captured) {
                sender.sendMessage("Wildcard root: " + captured);
            }

            @SubCommand(commandPath = {"*"}, name = "sub")
            public void wildcardNested(CommandSender sender, String captured, String arg) {
                sender.sendMessage("Wildcard nested: " + captured + " - " + arg);
            }

            @SubCommand(commandPath = {"static", "*"}, name = "deep")
            public void deepWildcard(CommandSender sender, String captured) {
                sender.sendMessage("Deep wildcard: " + captured);
            }

            @Override
            public void showUsage(CommandSender sender) {
                sender.sendMessage("Usage: /wild <args>");
            }

            @Override
            public List<String> tabComplete(CommandSender sender, String[] args) {
                return new ArrayList<>();
            }

            @Override
            public int maxArgs() {
                return 0;
            }
        };
    }

    @Test
    public void testExactMatchPriority() {
        command.onCommand(sender, bukkitCommand, "wild", new String[]{"static"});
        assertTrue(sender.getMessages().contains("Static command"));
    }

    @Test
    public void testRootWildcard() {
        command.onCommand(sender, bukkitCommand, "wild", new String[]{"anything"});
        assertTrue(sender.getMessages().contains("Wildcard root: anything"));
    }

    @Test
    public void testNestedWildcard() {
        command.onCommand(sender, bukkitCommand, "wild", new String[]{"val1", "sub", "val2"});
        assertTrue(sender.getMessages().contains("Wildcard nested: val1 - val2"));
    }

    @Test
    public void testDeepWildcard() {
        command.onCommand(sender, bukkitCommand, "wild", new String[]{"static", "random", "deep"});
        assertTrue(sender.getMessages().contains("Deep wildcard: random"));
    }

    @Test
    public void testWildcardTabCompletion() {
        // /wild <tab> should NOT show *
        List<String> suggestions = command.onTabComplete(sender, bukkitCommand, "wild", new String[]{""});
        assertTrue(suggestions.contains("static"));
        assertTrue(!suggestions.contains("*"));

        // /wild anything <tab> should show "sub"
        List<String> suggestions2 = command.onTabComplete(sender, bukkitCommand, "wild", new String[]{"anything", ""});
        assertTrue(suggestions2.contains("sub"));
    }
}



