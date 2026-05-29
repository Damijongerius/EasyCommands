# ⚡ EasyCommands Power User Guide (0.3.1)

Welcome to the full potential of EasyCommands. This guide showcases how to use the framework's most powerful features to build clean, robust, and user-friendly command systems with minimal code.

## 🚀 The "Mega Example"

This single class demonstrates **Smart Parameters**, **Command Modifiers**, **Global Exceptions**, and **Context Injection**.

```java
import com.dami.easyCommands.core.BaseCommand;

public class GuildCommand extends BaseCommand {

    @Override
    public String getName() {
        return "guild";
    }

    // 1. Dependency Injection (@Sender) & Modifiers
    // /guild delete
    @SubCommand(commandPath = {}, name = "delete", description = "Delete your guild")
    @Confirm(timeout = 10) // Forces player to run the command again within 10s
    @Require("not_in_combat") // Dynamic state check! (Cannot be done via simple permissions)
    @Async // Runs the logic off the main server thread
    public void deleteGuild(@Sender GuildPlayer player) {
        
        player.getGuild().delete();
        player.sendMessage("Your guild was disbanded.");
        
        // Throw business logic exceptions to be caught globally!
        throw new SuccessException("Guild deleted successfully.");
    }

    // 2. Flags & Optional Variables
    // /guild invite Dami -force
    @SubCommand(commandPath = {}, name = "invite")
    public void invitePlayer(Player sender, Player target, @Flag("-force") boolean force, @Optional("Member") String role) {
        if (force) {
            sender.sendMessage("Forcing invite for " + target.getName() + " as " + role);
        } else {
            sender.sendMessage("Inviting " + target.getName() + " as " + role);
        }
    }

    // 3. Interactive Prompts
    @SubCommand(commandPath = {}, name = "create")
    @Cooldown(seconds = 300) // 5-minute cooldown
    public void createGuild(Player sender) {
        sender.sendMessage("What do you want to name your guild?");
        
        EasyCommands.prompt(plugin, sender, response -> {
            sender.sendMessage("Guild '" + response + "' created!");
        });
    }
}
```

---

## 🛠️ Key Features Under the Hood

### 1. Smart Parameter Validation
Stop writing `if (args.length < 2)` or `try { Integer.parseInt(args[0]) }`.

*   **`@Min(value)` / `@Max(value)`**: Automatically validates numbers and sends an error to the user if they are out of range.
*   **`@Optional("default")`**: Makes a parameter optional. If the user doesn't provide it, the default value is automatically parsed and injected.

### 2. Automatic Type Conversion
Parameters are automatically converted from strings to:
*   `Player` / `OfflinePlayer`
*   `World`
*   `Material`
*   `Sound`
*   `EntityType`
*   Primitives: `int`, `double`, `float`, `boolean`, `long`

### 3. "Did you mean...?" Suggestions
If a user mistypes a command (e.g., `/guild creat`), EasyCommands calculates the Levenshtein distance and suggests the closest match!

### 4. Interactive Help Menus
With the Kyori Adventure integration, running `/guild help` dynamically computes all descriptions, validates permissions, and sends a clickable paginated menu to the player automatically!

---

## 🎨 Global Exception Handlers
Don't pollute your code with `try-catch` blocks! Register an ExceptionHandler globally!

```java
EasyCommands.registerExceptionHandler(SQLException.class, (sender, exception) -> {
    sender.sendMessage("§cDatabase error occurred. Please try again later.");
    exception.printStackTrace();
});
```

## 💡 Best Practices
1.  **Always use `@Optional`** for trailing parameters that have sensible defaults.
2.  **Provide `description`** in your `@SubCommand` annotations to populate the automated help menu.
3.  **Use Static Classes for Subcommands** if your command has more than 5 sub-commands to keep your code clean and manageable.
4.  **Target `Player`** directly as the first parameter if the command is player-only; the framework handles the restriction automatically.
