# EasyCommands Improvement Report

## Current State Analysis
EasyCommands is a solid foundation for a Bukkit command framework, utilizing an annotation-based approach and a tree-like structure (`CommandNode`) to manage subcommands and tab completions. It supports both method-level subcommands and class-level subcommands (via `ShardableCommand`).

### Key Strengths:
- Modular design with `CommandNode`.
- Annotation-based, reducing boilerplate for simple commands.
- Support for weighted commands and priorities.
- Permission integration at both command and tab-completion levels.

### Identified Opportunities for Improvement:
1.  **Automatic Usage & Descriptions**: Currently, `showUsage` must be implemented manually. Adding `description` to `@SubCommand` will allow the framework to automatically generate help messages and usage guides tailored to the user's permissions.
2.  **Adventure API Integration**: Modern Paper plugins benefit from `Component` and `MiniMessage`. Integrating these will allow for richer, more maintainable messaging.
3.  **Enhanced Error Handling & Fuzzy Matching**: When a user types an incorrect subcommand, the framework should suggest the closest valid command or list available options instead of a generic "No command found" message.
4.  **Reusable Tab Completions**: Many commands need to suggest players, worlds, or custom lists. A registry for reusable tab completers would significantly reduce code duplication.
5.  **Smart Argument Handling**: Improving how arguments are passed and ensuring subcommands "take over" the execution context once matched, providing better support for deep command nesting.
6.  **Internal Code Cleanup**:
    *   Standardize logging (moving away from `System.out.println`).
    *   Improve the `ConvertToObject` (YAML export) functionality for debugging/documentation.
    *   Ensure consistent use of Paper APIs where applicable.

---

## Proposed Changes

### 1. Annotation Updates
Update `@SubCommand` and `@SubCommandTab` to include:
- `description`: A brief explanation of what the command does.
- `usage`: (Optional) A specific usage string if the default `/base subcommand <args>` isn't sufficient.

### 2. Messaging System
- Implement a `MessageUtil` or similar, utilizing `MiniMessage` for color and formatting.
- Update `SubCommandInfo` to use this for permission-denied messages.

### 3. Smart Command Execution
- **Fuzzy Matching**: Implement Levenshtein distance or similar to suggest subcommands.
- **Auto-Help**: When a command is called incorrectly (e.g., wrong argument count or invalid subcommand), display a formatted list of subcommands the user has access to, including their descriptions.

### 4. Tab Completion Registry
- Create a `TabRegistry` where developers can register common completion providers (e.g., `players`, `boolean`, `numbers`).
- Allow annotations to reference these providers by name.

### 5. Architectural Refinement
- Ensure `ShardableCommand` and `BaseCommand` handle argument slicing consistently.
- Refine the "take over" logic so subcommands can easily handle variable-length arguments once they are the active node.

## Conclusion
These improvements will transform EasyCommands from a basic utility into a high-end framework suitable for professional Paper plugin development, significantly improving both the developer experience and the end-user interaction.
