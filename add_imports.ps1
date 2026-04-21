
$basePath = "src/main/java/com/dami/easyCommands"

function Add-Imports {
    param($dir, $imports)
    $files = Get-ChildItem -Path "$basePath/$dir/*.java"
    foreach ($file in $files) {
        $content = Get-Content $file.FullName
        $newContent = @()
        $inserted = $false
        foreach ($line in $content) {
            $newContent += $line
            if (!$inserted -and $line -match "^package ") {
                $newContent += ""
                foreach ($import in $imports) {
                    $newContent += "import $import;"
                }
                $inserted = $true
            }
        }
        [System.IO.File]::WriteAllLines($file.FullName, $newContent)
    }
}

# Add imports for the split packages
Add-Imports "command" @("com.dami.easyCommands.internal.*", "com.dami.easyCommands.model.*", "com.dami.easyCommands.annotations.*", "com.dami.easyCommands.util.*")
Add-Imports "internal" @("com.dami.easyCommands.command.*", "com.dami.easyCommands.model.*", "com.dami.easyCommands.annotations.*", "com.dami.easyCommands.util.*")
Add-Imports "model" @("com.dami.easyCommands.command.*", "com.dami.easyCommands.internal.*", "com.dami.easyCommands.annotations.*", "com.dami.easyCommands.util.*")
Add-Imports "annotations" @("com.dami.easyCommands.command.*", "com.dami.easyCommands.internal.*", "com.dami.easyCommands.model.*", "com.dami.easyCommands.util.*")
Add-Imports "util" @("com.dami.easyCommands.command.*", "com.dami.easyCommands.internal.*", "com.dami.easyCommands.model.*", "com.dami.easyCommands.annotations.*")
