# 📚 API Reference (0.3.1)

## 📌 Annotations

| Annotation | Target | Description |
|---|---|---|
| `@SubCommand` | Method, Class | Marks a method or static class as a sub-command. Contains attributes for `name`, `permission`, `description`, `usage`, `maxArgs`, and `senderType`. |
| `@Flag` | Parameter | Extracts a flag from anywhere in the command (e.g. `@Flag("-force")`). |
| `@Cooldown` | Method, Class | Automatically applies a cooldown (in seconds) to the command, throwing a localized warning if triggered too fast. |
| `@Require` | Method, Class | Evaluates a registered custom `Condition` before executing the command. Halts execution if failed. |
| `@Confirm` | Method, Class | Intercepts the command execution, sends a warning, and forces the user to run the exact same command again to confirm. |
| `@AutoComplete` | Parameter | Injects a globally registered completion list directly into a specific parameter. <br><br> ```java @SubCommand(commandPath = {}, name = "join") public void joinGuild(Player player, @AutoComplete("guilds") String guildName) { // Tab completes from the "guilds" registry } ``` |
| `@Sender` | Parameter | Overrides standard parameter resolution to inject a custom context wrapper (e.g. `@Sender GamePlayer`). |
| `@Async` | Method | Automatically offloads the method execution from the Main Server Thread to a background worker thread. |
| `@Optional` | Parameter | Provides a default value if the user omits the trailing parameter (e.g. `@Optional("64") int amount`). |
| `@Min` | Parameter | Ensures a numeric parameter is at least this value. |
| `@Max` | Parameter | Ensures a numeric parameter is at most this value. |

---

## 🛠️ The `EasyCommands` Facade

The `com.dami.easyCommands.EasyCommands` class provides static methods to register global integrations.

### Custom Interactivity
```java
// Intercepts the next chat message, cancels it, and runs the consumer safely on the main thread.
EasyCommands.prompt(Plugin plugin, Player player, Consumer<String> onResponse);
```

### Registries
```java
// Register custom types for Dependency Injection (e.g. mapping "admin" to an Enum)
EasyCommands.registerParameterType(Class<T> type, TypeConverter<T> converter);

// Register a custom condition for the @Require annotation
EasyCommands.registerCondition(String id, Condition condition);

// Register a global Exception handler to catch logic errors
EasyCommands.registerExceptionHandler(Class<T> exceptionClass, ExceptionHandler<T> handler);

// Register a context wrapper for the @Sender annotation
EasyCommands.registerSenderResolver(Class<T> clazz, SenderResolver<T> resolver);
```
