package com.dami.easyCommands;

import com.dami.easyCommands.commands.PermissionTestCommand;
import com.dami.easyCommands.mocks.MockCommand;
import com.dami.easyCommands.mocks.MockCommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PermissionTabCompletionTest {

    private PermissionTestCommand command;
    private MockCommandSender sender;
    private MockCommand bukkitCommand;

    @BeforeEach
    public void setup() {
        command = new PermissionTestCommand();
        sender = new MockCommandSender();
        bukkitCommand = new MockCommand("permissiontest");
    }

    @Test
    public void testRootTabCompleteNoPermission() {
        // Sender has no permissions
        List<String> suggestions = command.onTabComplete(sender, bukkitCommand, "permissiontest", new String[]{""});

        assertNotNull(suggestions);
        assertTrue(suggestions.contains("public"), "Should show public command");
        // THIS IS THE BUG: It should NOT contain 'secret' if the player doesn't have 'test.secret'
        assertFalse(suggestions.contains("secret"), "Should NOT show secret command without permission");
    }

    @Test
    public void testRootTabCompleteWithPermission() {
        // Give sender permission for 'secret'
        sender.addPermission("test.secret");
        
        List<String> suggestions = command.onTabComplete(sender, bukkitCommand, "permissiontest", new String[]{""});

        assertNotNull(suggestions);
        assertTrue(suggestions.contains("public"));
        assertTrue(suggestions.contains("secret"), "Should show secret command with permission");
    }

    @Test
    public void testNestedTabCompleteNoPermission() {
        // Give sender permission for 'secret' but not 'secret deep'
        sender.addPermission("test.secret");
        
        List<String> suggestions = command.onTabComplete(sender, bukkitCommand, "permissiontest", new String[]{"secret", ""});

        assertNotNull(suggestions);
        // THIS IS THE BUG: It should NOT contain 'deep' if the player doesn't have 'test.deep'
        assertFalse(suggestions.contains("deep"), "Should NOT show deep command without permission");
    }
}



