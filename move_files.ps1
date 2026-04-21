
$basePath = "src/main/java/com/dami/easyCommands"

# 1. Move from Annotations to annotations
Get-ChildItem -Path "$basePath/Annotations/*.java" | Move-Item -Destination "$basePath/annotations/"

# 2. Move from Util to util
Get-ChildItem -Path "$basePath/Util/*.java" | Move-Item -Destination "$basePath/util/"

# 3. Move from Command to command
"BaseCommand.java", "ShardableCommand.java", "ICommand.java", "MessageHandler.java" | ForEach-Object {
    Move-Item -Path "$basePath/Command/$_" -Destination "$basePath/command/"
}

# 4. Move from Command to internal
"CommandNode.java", "SubCommandInfo.java", "TabCompleteInfo.java", "ParameterResolver.java", "CompletionResolver.java" | ForEach-Object {
    Move-Item -Path "$basePath/Command/$_" -Destination "$basePath/internal/"
}

# 5. Move from Command to model
"MessageKey.java", "SenderType.java", "ValidationException.java", "TypeConverter.java", "CompletionProvider.java" | ForEach-Object {
    Move-Item -Path "$basePath/Command/$_" -Destination "$basePath/model/"
}
