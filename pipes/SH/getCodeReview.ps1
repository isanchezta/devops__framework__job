# SCRIPT PARA Validar si se ejecuta Sonar
$Folder_JF = "$env:System_DefaultWorkingDirectory\pipes\Jenkinsfile" 
$Valor = "True"
$tag=$(git tag -l "*$env:Build_BuildNumber-KO*")

if (Test-Path -Path $Folder_JF)
{
    $condition=Get-ChildItem -Path $Folder_JF -Recurse | Get-Content | Select-String -pattern "CODE_REVIEW=true"

    if($condition)
    {        
        Write-Output "##vso[task.setvariable variable=ShouldRun_Sonar]$Valor"
        Write-Host "Valor: $Valor"
    }
}

Write-Host "Directorio: $Folder_JF"
Write-Host "Condicion:"$condition.ToString().Trim()
Write-Host "Tag: $tag"
Write-Host "Version: $env:Build_BuildNumber"
Write-Host "Branch: $env:Build_SourceBranchName"