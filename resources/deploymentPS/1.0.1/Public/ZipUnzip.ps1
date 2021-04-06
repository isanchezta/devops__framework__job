#
# Date: 14/11/2019
# Este script:
# 1 Zip Directory     = Get-ZipDirectory
# 2 UpZip Directory   = Get-UpZipDirectory
#

Function Get-ZipDirectory 
{
    [CmdletBinding()]
    param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
     [String]$source, 
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
     [String]$destination
    ) 
    Try
    {
        # Add Type 
	    Add-Type -AssemblyName System.IO.Compression.FileSystem
	    #Zip File        
	    [System.IO.Compression.ZipFile]::CreateFromDirectory($source, $destination)
        Write-Host "Zip File complete" -ForegroundColor Green
    }
    Catch 
    {
        #$PSCmdlet.ThrowTerminatingError($_)
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        $ExceptionMessage
        Break
    }
}

Function Get-UpZipDirectory
{
    [CmdletBinding()]
    param
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
        # Add Type
        Add-Type -AssemblyName System.IO.Compression.FileSystem

        #Unzip File
        #[System.IO.Compression.ZipFile]::ExtractToDirectory($source, $destination, [System.Text.Encoding]::entryNameEncoding)
		[System.IO.Compression.ZipFile]::ExtractToDirectory($source, $destination)
        Write-Host "Unzip File complete" -ForegroundColor Green
    }
    Catch 
    {
        #$PSCmdlet.ThrowTerminatingError($_)
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        $ExceptionMessage
        Break
    }
}