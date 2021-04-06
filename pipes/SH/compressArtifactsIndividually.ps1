Write-Host "List $env:build_binariesDirectory"

dir $env:Build.ArtifactStagingDirectory
Write-Host "List $env:build_binariesDirectory"

dir $env:Build.ArtifactStagingDirectory

$sourcePath = "$env:Build_ArtifactStagingDirectory"
$targetPath = "$env:Build_ArtifactStagingDirectory\zipped\"

echo "Directorio: " $targetPath
New-Item -Path $targetPath -ItemType Directory

# Delete files (if any) in the target folder
Get-ChildItem -Path $targetPath *.* -File -Recurse | foreach { $_.Delete()}

# To zip all WSP files in source folder and place it in target folder
Get-ChildItem $sourcePath *.wsp | ForEach-Object {
    $newFileName = $_.Name -replace ".wsp", "-$env:Build_BuildNumber.zip"
    Compress-Archive -LiteralPath $_.FullName -DestinationPath $targetPath$newFileName 
    Write-Host $newFileName 
    $newFileName = $null
    }

# To zip all TXT files in source folder and place it in target folder
Get-ChildItem $sourcePath *.txt | ForEach-Object {
    $newFileName = $_.Name -replace ".txt", ".zip"
    Compress-Archive -LiteralPath $_.FullName -DestinationPath $targetPath$newFileName 
    Write-Host $newFileName 
    $newFileName = $null
    }

Get-ChildItem $targetPath