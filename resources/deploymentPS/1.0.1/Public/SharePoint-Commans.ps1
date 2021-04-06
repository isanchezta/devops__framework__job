Function Get-SP-Update
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$Project, 
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$literalPath
    ) 	
    Try
    {
        Add-PSSnapin Microsoft.SharePoint.PowerShell -EA 0

        Write-Host "Project: " $Project "Path: " $literalPath

        Update-SPSolution -Identity $Project -literalPath $literalPath -GACDeployment -FullTrustBinDeployment

        Write-Host "The $project project is updated" -ForegroundColor Green

        return $true                   
    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        return $false
    }
}

Function Get-SP-Backup
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$Project,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$Path
    ) 	
    Try
    {
        Write-Host "Project: " $Project

        Add-PSSnapin Microsoft.SharePoint.PowerShell -ErrorAction SilentlyContinue

        $bakname = "bak_{0}_hms" -f (Get-Date).tostring("hhmmss")
        #$ruta = (Join-Path ($pwd) $bakname)
        $ruta = (Join-Path ($Path) $bakname)
        if( (Test-Path $ruta) -eq $false){
            New-Item -Path $ruta -ItemType Directory -Force | out-null
        }
        $farm = Get-SPFarm
        $wspnames = [string]::Join('|', $Project)	

        Trap{
            Write-Host $_
            exit -1
        }

        foreach ($solution in $farm.Solutions | ? {$_.Name -match "($($wspnames))"})
        {
            $rutaWsp = (Join-Path $ruta $solution.Name)
            Write-Host "`t -Copyiing solution $rutaWsp..."
            $solution.SolutionFile.SaveAs($rutaWsp)   
            Write-Host "`t   [OK]" -ForegroundColor green 
        }        

        Write-Host "$Project project backup was created" -ForegroundColor Green

        return $true                   
    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        return $false
    }
}

Function Get-SP-WaitForJobToFinish
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$Project
    ) 	
    Try
    {
        Add-PSSnapin Microsoft.SharePoint.PowerShell -ErrorAction SilentlyContinue

        # Get the number of retries
        $retries = "60"
        # Get the number of attempts in seconds
        $delay = "1" 
        # Initial counter
        $counter = 1
        $solution = Get-SPSolution -Identity $Project

        # Wait for the project status JobExists equal $False
        do
        {
            $solution = Get-SPSolution -Identity $Project
            Write-Host "$counter/$retries Waiting for $Project. Deployed: " $solution.Deployed " JobExists: " $solution.JobExists
            $counter++
            Start-Sleep -Seconds $delay
        }
        while($solution.JobExists -ne $False -and $counter -le $retries)

        # Throw an error
        if($counter -gt $retries) 
        {
            Write-Host $solution.LastOperationResult
            Write-Host "Could not deploy the project $Project. `nTry to increase the number of retries ($retries) or delay between attempts ($delay seconds)." -ForegroundColor Red
            return $false
        } 
        else
        {
            Write-Host $solution.LastOperationResult
            Write-Host "The $project project is deployed, values Deployed: " $solution.Deployed " JobExists: " $solution.JobExists -ForegroundColor Green
            return $true
        }                            
    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        return $false
    }
}