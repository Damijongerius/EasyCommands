
$files = Get-ChildItem -Path src -Filter *.java -Recurse

foreach ($file in $files) {
    $content = Get-Content $file.FullName
    $newContent = @()
    
    foreach ($line in $content) {
        $updatedLine = $line
        
        # 1. Update package declarations and global package imports
        $updatedLine = $updatedLine -replace "package com.dami.easyCommands.Annotations", "package com.dami.easyCommands.annotations"
        $updatedLine = $updatedLine -replace "import com.dami.easyCommands.Annotations", "import com.dami.easyCommands.annotations"
        $updatedLine = $updatedLine -replace "package com.dami.easyCommands.Util", "package com.dami.easyCommands.util"
        $updatedLine = $updatedLine -replace "import com.dami.easyCommands.Util", "import com.dami.easyCommands.util"
        
        # 2. Update specific classes from Command package (being split)
        
        # command
        $updatedLine = $updatedLine -replace "com.dami.easyCommands.Command.BaseCommand", "com.dami.easyCommands.command.BaseCommand"
        $updatedLine = $updatedLine -replace "com.dami.easyCommands.Command.ShardableCommand", "com.dami.easyCommands.command.ShardableCommand"
        $updatedLine = $updatedLine -replace "com.dami.easyCommands.Command.ICommand", "com.dami.easyCommands.command.ICommand"
        $updatedLine = $updatedLine -replace "com.dami.easyCommands.Command.MessageHandler", "com.dami.easyCommands.command.MessageHandler"
        
        # internal
        $updatedLine = $updatedLine -replace "com.dami.easyCommands.Command.CommandNode", "com.dami.easyCommands.internal.CommandNode"
        $updatedLine = $updatedLine -replace "com.dami.easyCommands.Command.SubCommandInfo", "com.dami.easyCommands.internal.SubCommandInfo"
        $updatedLine = $updatedLine -replace "com.dami.easyCommands.Command.TabCompleteInfo", "com.dami.easyCommands.internal.TabCompleteInfo"
        $updatedLine = $updatedLine -replace "com.dami.easyCommands.Command.ParameterResolver", "com.dami.easyCommands.internal.ParameterResolver"
        $updatedLine = $updatedLine -replace "com.dami.easyCommands.Command.CompletionResolver", "com.dami.easyCommands.internal.CompletionResolver"
        
        # model
        $updatedLine = $updatedLine -replace "com.dami.easyCommands.Command.MessageKey", "com.dami.easyCommands.model.MessageKey"
        $updatedLine = $updatedLine -replace "com.dami.easyCommands.Command.SenderType", "com.dami.easyCommands.model.SenderType"
        $updatedLine = $updatedLine -replace "com.dami.easyCommands.Command.ValidationException", "com.dami.easyCommands.model.ValidationException"
        $updatedLine = $updatedLine -replace "com.dami.easyCommands.Command.TypeConverter", "com.dami.easyCommands.model.TypeConverter"
        $updatedLine = $updatedLine -replace "com.dami.easyCommands.Command.CompletionProvider", "com.dami.easyCommands.model.CompletionProvider"
        
        # Also need to handle package declaration in the moved files themselves
        # If the file is being moved to command, its package line should be updated
        # This is already partially handled by the -replace for full paths if they were used, 
        # but the files being moved have "package com.dami.easyCommands.Command;"
        
        $fileName = [System.IO.Path]::GetFileName($file.FullName)
        if ($line -match "^package com.dami.easyCommands.Command;") {
            if ("BaseCommand.java", "ShardableCommand.java", "ICommand.java", "MessageHandler.java" -contains $fileName) {
                $updatedLine = "package com.dami.easyCommands.command;"
            }
            elseif ("CommandNode.java", "SubCommandInfo.java", "TabCompleteInfo.java", "ParameterResolver.java", "CompletionResolver.java" -contains $fileName) {
                $updatedLine = "package com.dami.easyCommands.internal;"
            }
            elseif ("MessageKey.java", "SenderType.java", "ValidationException.java", "TypeConverter.java", "CompletionProvider.java" -contains $fileName) {
                $updatedLine = "package com.dami.easyCommands.model;"
            }
        }
        
        $newContent += $updatedLine
    }
    
    $newContent | Set-Content $file.FullName -NoNewline -Encoding UTF8
    # Add trailing newline that Set-Content -NoNewline might have removed if it wasn't there, 
    # but actually Set-Content without -NoNewline adds one. Let's just use regular Set-Content.
    $newContent | Set-Content $file.FullName -Encoding UTF8
}
