#
# Author: Wilmer Andrade
# Date: 16/11/2019
# Este script: Se encarga de buscar un string en un nodo y lo reemplaza por otro string
#

Function Get-ModifyXML
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$path,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$file
    )

    Try
    {
        $path_ = $path
        $file_ = $file        

        Write-Host "Ruta a leer path $path_" -ForegroundColor Green

        $value = Get-ChildItem $path_ -recurse | Where-Object {$_.PSIsContainer -eq $true -and $_.Name -match "PackageTmp"} | Select-Object FullName
        $value = $value -replace "@{FullName=", "" -replace "}", ""
        
        Write-Host "Ruta donde estan los archivos $value" -ForegroundColor Green

        return $value

    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        Break
    }
}
