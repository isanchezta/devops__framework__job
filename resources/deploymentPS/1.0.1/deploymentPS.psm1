#region import everything we need
<#Add-Type -Path 
. $PSScriptRoot\Artifactory-API-Commans.ps1
. $PSScriptRoot\AzureDevOps-API-Commans.ps1
. $PSScriptRoot\Credentials.ps1
. $PSScriptRoot\CRUD-Directory.ps1
. $PSScriptRoot\Manifest.ps1
. $PSScriptRoot\ModifyXML.ps1
. $PSScriptRoot\MoveFiles.ps1
. $PSScriptRoot\Recycle-AppPool.ps1
. $PSScriptRoot\Recycle-WebSite.ps1
. $PSScriptRoot\SharePoint-Commans.ps1
. $PSScriptRoot\ZipUnzip.ps1#>
#endregion


foreach ($directory in @('Public')) 
{ 
    Get-ChildItem -Path "$PSScriptRoot\$directory\*.ps1" | ForEach-Object { . $_.FullName }
}