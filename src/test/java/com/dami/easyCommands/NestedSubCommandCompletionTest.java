package com.dami.easyCommands;

import com.dami.easyCommands.annotations.SubCommand;
import com.dami.easyCommands.core.ICommand;
import com.dami.easyCommands.core.ShardableCommand;
import com.dami.easyCommands.mocks.MockCommand;
import com.dami.easyCommands.mocks.MockCommandSender;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NestedSubCommandCompletionTest {

    private ShardableCommand rootCommand;
    private MockCommandSender sender;
    private MockCommand bukkitCommand;

    @BeforeEach
    public void setup() {
        rootCommand = new ShardableCommand() {
            @Override
            public String getName() {
                return "test";
            }


            @Override
            public void mainCommand(CommandSender sender, String[] args) {}

            @Override
            public List<String> tabComplete(CommandSender sender, String[] args) {
                return List.of();
            }

            @Override
            public void showUsage(CommandSender sender) {}

            @Override
            public int maxArgs() {
                return 0;
            }
        };
        sender = new MockCommandSender();
        bukkitCommand = new MockCommand("root");
    }

    @SubCommand(commandPath = {}, name = "hello")
    public static class HelloCommand implements ICommand {
        @Override
        public String getName() {
            return "hello";
        }

        @Override
        public void mainCommand(CommandSender sender, String[] args) {}

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            return List.of("h1", "h2");
        }

        @Override
        public void showUsage(CommandSender sender) {}

        @Override
        public int maxArgs() { return 0; }

        @SubCommand(commandPath = {}, name = "world")
        public void world(CommandSender sender, String[] args) {}
    }

    @SubCommand(commandPath = {"hello", "world"}, name = "country")
    public static class CountryCommand implements ICommand {
        @Override
        public String getName() {
            return "country";
        }

        @Override
        public void mainCommand(CommandSender sender, String[] args) {}

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            return List.of("c1", "c2");
        }

        @Override
        public void showUsage(CommandSender sender) {}

        @Override
        public int maxArgs() { return 0; }
    }

    @Test
    public void testNestedCompletionsMerged() {
        rootCommand.RegisterSubCommandClass(new HelloCommand());
        rootCommand.RegisterSubCommandClass(new CountryCommand());

        // /root hello <tab>
        // Should show "h1", "h2" AND "world"
        List<String> helloSuggestions = rootCommand.onTabComplete(sender, bukkitCommand, "root", new String[]{"hello", ""});
        assertNotNull(helloSuggestions);
        assertTrue(helloSuggestions.contains("h1"), "Should contain h1");
        assertTrue(helloSuggestions.contains("h2"), "Should contain h2");
        assertTrue(helloSuggestions.contains("world"), "Should contain world");

        // /root hello world <tab>
        // Should show "country"
        List<String> worldSuggestions = rootCommand.onTabComplete(sender, bukkitCommand, "root", new String[]{"hello", "world", ""});
        assertNotNull(worldSuggestions);
        assertTrue(worldSuggestions.contains("country"), "Should contain country");
    }
}



