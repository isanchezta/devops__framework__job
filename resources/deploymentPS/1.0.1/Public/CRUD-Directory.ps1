#
# Author: Wilmer Andrade
# Date: 13/11/2019
# Este script:
# 1  -  Create Directory                   = Get-CreateDirectory
# 2  -  Update Directory                   = Get-UpdateDirectory
# 3  -  Delete Directory                   = Get-DeleteDirectory
# 4  -  Validate Directory                 = Get-ValidateDirectory
# 5  -  Delete File from Directories       = Get-DeleteFileDirectory
# 6  -  Info Directory                     = Get-InfoDirectory
# 7  -  Close Directories                  = Get-CloseDirectory
# 8  -  View Directories Open              = Get-ViewOpenDirectory
# 9  -  Delete and exclude files           = Get-DeleteExcludeFileDirectory
# 10 -  Create Directory Temporary         = Get-NewTemporaryDirectory
# 11 -  Delete and include files           = Get-DeleteIncludeFileDirectory
#

# 1 Create Directory
Function Get-CreateDirectory
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$directory
    ) 	
    Try
    {
        $Folder = $directory

        Write-Host "Directory Validation" 
        if (Test-Path -Path $Folder) 
        {                        
            Write-Host "Directory $Folder Already Exists!" -ForegroundColor Yellow 
            return $false          
        }
        else
        {
            New-Item -Path $Folder -ItemType Directory
            Write-Host "Directory $Folder Created Successfully" -ForegroundColor Green 
            return $true 
        }   
    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        Break
    }
}

# 2 Update Directory
Function Get-UpdateDirectory
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$directory1, 
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$directory2
    ) 	
    Try
    {
        $Folder1 = $directory1
        $Folder2 = $directory2

        Write-Host "Directory Validation" 
        if (Test-Path -Path $Folder1) 
        {
            Rename-Item -Path $Folder1 -newName $Folder2
            Write-Host "Directory $Folder1 Rename Successfully to $Folder2" -ForegroundColor Green 
            return $true
        }
        else
        {           
            Write-Host "Directory does not exist: $Folder1" -ForegroundColor Yellow;  
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

# 3 Delete Directory
Function Get-DeleteDirectory
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$directory
    ) 	
    Try
    {
        $Folder = $directory

        Write-Host "Directory Validation" 
        if (Test-Path -Path $Folder) 
        {
            Remove-Item -Recurse -Force $Folder
            #Get-ChildItem $directory -Recurse | Remove-Item -Force
            Write-Host "Delete Directory: $Folder" -ForegroundColor Green 
            return $true       
        }   
        else
        {
            Write-Host "Directory does not exist: $Folder" -ForegroundColor Yellow 
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

# 4 Validate Folder
Function Get-ValidateDirectory
{
    [CmdletBinding()]
    Param
    (
        [Parameter(Mandatory)]
        [ValidateNotNullOrEmpty()]
            [string]$directory             
    )

    Try
    {
        $Folder = $directory    

        if (Test-Path -Path $Folder)
        {
            Write-Host "The path $Folder Already Exists!" -ForegroundColor Green
            return $true
        }
         else
        {
            Write-Host "The path $Folder not Exists!" -ForegroundColor Yellow
            return $false
        }
    }
    Catch 
    {
        $ExceptionMessage = "Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        Break
    }
}

# 5 Delete Directory File
Function Get-DeleteFileDirectory
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$directory,
    [Parameter(Mandatory=$false)]
     $exclude,
    [Parameter(Mandatory=$false)]
     $include
    ) 	
    Try
    {  
        $Folder = $directory

        Write-Host "Directory Validation" 
        if (Test-Path -Path $Folder) 
        {
            Remove-Item "$Folder/*" -Force -Recurse
            Write-Host "Delete Directory Files $Folder" -ForegroundColor Green
             
            return $true         
        }
        else
        {
            Write-Host "Directory does not exist: $Folder" -ForegroundColor Yellow
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

# 6 Info Info Directory
Function Get-InfoDirectory
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$directory
    )

    Get-ChildItem -Path $directory | Format-Table -AutoSize
}

# 7 Close Directories
Function Get-CloseDirectory
{
    Try
    {
        <#
        $shell = New-Object -ComObject Shell.Application
        $window = $shell.Windows() | Where-Object { $_.LocationURL -like "$(([uri]"D:\").AbsoluteUri)*" }
        $window | ForEach-Object { $_.Quit() }
        #>

        $shell = New-Object -ComObject Shell.Application
        $directories = $shell.Windows()
        $window = $shell.Windows() | Where-Object { $_.LocationURL -like "$(([uri]"$directories").AbsoluteUri)*" }
        $window | ForEach-Object { $_.Quit() }

        Write-Host "Close directories" -ForegroundColor Green 
    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        Break
    }
}

# 8 View Directories Open
Function Get-ViewOpenDirectory
{
    Try
    {
        Write-Host "Open directories" -ForegroundColor Green
        $shell = New-Object -ComObject Shell.Application
        $shell.Windows() | Format-Table Name, LocationName, LocationURL
    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        Break
    }
}

# 9 Deletes files and folders from the directory and excludes those that are declared
Function Get-DeleteExcludeFileDirectory
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$directory,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $directoryExclude
    ) 	
    Try
    {  
        $Folder = $directory
        $Exclude = $directoryExclude

        Write-Host "Directory Validation" 
        if (Test-Path -Path $Folder) 
        {
            Get-ChildItem $Folder -Exclude $Exclude | Remove-Item -Recurse -Force
            Write-Host "Delete Directory Files $Folder except $Exclude" -ForegroundColor Green  
            return $true         
        }
        else
        {
            Write-Host "Directory does not exist: $Folder" -ForegroundColor Yellow
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

# 10 Create directory temporary
Function Get-NewTemporaryDirectory 
{ 
    try 
    {
        $parent = [System.IO.Path]::GetTempPath()
        [string]$name = [System.Guid]::NewGuid()
        $path_ = New-Item -ItemType Directory -Path (Join-Path $parent $name)

        Write-Host "Create directory temp $path_ in server $directory"

        return $path_
    }
    catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
    }    
}

# 11 Deletes files and folders from the directory and include those that are declared
Function Get-DeleteIncludeFileDirectory
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$directory,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $directoryInclude
    ) 	
    Try
    {  
        $Folder = $directory
        $Include = $directoryInclude

        Write-Host "Directory Validation" 
        if (Test-Path -Path $Folder) 
        {
            Get-ChildItem $Folder -Include $Include -Recurse | Remove-Item -Recurse -Force
            Write-Host "Delete Directory Files $Folder $Include" -ForegroundColor Green  
            return $true         
        }
        else
        {
            Write-Host "Directory does not exist: $Folder" -ForegroundColor Yellow
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

Function Get-Guid 
{ 
    try 
    {
        [string]$name = [System.Guid]::NewGuid()

        Write-Host "Create Guid $name"

        return $name
    }
    catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
    }    
}