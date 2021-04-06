#
# Author: Wilmer Andrade
# Date: 13/11/2019
# Este script:
# 1  - Move Files from Directories        = Get-MoveFileDirectory
# 2 -  Move File from Directorie          = Get-MoveFile
# 3 -  Move File Specific from Directorie = Get-MoveFileSpecific
# 4 -  Move Files from Directories exclude extension =  
#

# 1 Move Files from Directories
Function Get-MoveFileDirectory
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$source, 
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$destination
    ) 	
    Try
    {
        $source_ = $source
        $destination_ = $destination

        Write-Host "Copy Directory" 
        #if ((Test-Path -Path $source_) -and (Test-Path -Path $destination_) )
        if (Test-Path -Path $source_)
        {                        
            Copy-Item "$source_/*" -Destination "$destination_" -Recurse
            Write-Host "Directory $source_ copy to $destination_" -ForegroundColor Green;  
            return $true          
        }
        else
        {
            Write-Host "Directory $source_ or $destination_ Not Exists!" -ForegroundColor Yellow 
            return $false 
        }   
    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        Break
    }
}

# 2 Move File from Directorie
Function Get-MoveFile
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$source, 
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$destination
    ) 	
    Try
    {
        $source_ = $source
        $destination_ = $destination

        Write-Host "Copy Directory" 
        #if ((Test-Path -Path $source_) -and (Test-Path -Path $destination_) )
        if (Test-Path -Path $source_)
        {                        
            Copy-Item "$source_" -Destination "$destination_" -Recurse
            Write-Host "Directory $source_ copy to $destination_" -ForegroundColor Green;  
            return $true          
        }
        else
        {
            Write-Host "Directory $source_ or $destination_ Not Exists!" -ForegroundColor Yellow 
            return $false 
        }   
    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        Break
    }
}

# 3 Move File specific from Directorie
Function Get-MoveFileSpecific
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $source, 
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$destination
    ) 	
    Try
    {
        $source_ = $source
        $destination_ = $destination

        Write-Host "Copy Files" 
        if (Test-Path -Path $source_)
        {                        
            Copy-Item $source_ -Destination $destination_
            Write-Host "Directory $source_ copy to $destination_" -ForegroundColor Green;  
            return $true          
        }
        else
        {
            Write-Host "Directory $source_ or $destination_ Not Exists!" -ForegroundColor Yellow 
            return $false 
        }   
    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        Break
    }
}

# 4 Move Files from Directories exclude extension
Function Get-MoveFileDirectoryExclude
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$source, 
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$destination,
    [ValidateNotNullOrEmpty()]
        $exclude
    ) 	
    Try
    {
        $source_ = $source
        $destination_ = $destination

        Write-Host "Copy Directory" 
        #if ((Test-Path -Path $source_) -and (Test-Path -Path $destination_) )
        if (Test-Path -Path $source_)
        {                        
            Copy-Item "$source_/*" -Destination "$destination_" -Exclude $exclude -Recurse
            Write-Host "Directory $source_ copy to $destination_" -ForegroundColor Green;  
            return $true          
        }
        else
        {
            Write-Host "Directory $source_ or $destination_ Not Exists!" -ForegroundColor Yellow 
            return $false 
        }   
    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        Break
    }
}