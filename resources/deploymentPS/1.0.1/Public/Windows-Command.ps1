Function Get-StatusServices
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$services
    ) 
    Try
    {
        Write-Host "Get Status $services"

        Get-Service $services
    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        Break
    }
}

Function Get-RecycleService 
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$services,
    [ValidateSet("Started","Stopped","Restart")]
        [String]$status
    ) 
    Try
    {
        switch($status)
        {
            Started 
            {
                $status_ = (Get-Service $services).Status
                If ($status_.Status -ne "Running")
                {
                    write-host "Stopping $services"
                    Start-Service $services
                } 
                Else 
                {
                    write-host "$services already running"
                }
            }
            Stopped 
            {
                $status_ = (Get-Service $services).Status
                If ($status_.Status -ne "Stopped")
                {
                    write-host "Stopping $services"
                    Stop-Service $services
                } 
                Else 
                {
                    write-host "$services already stopped"
                }
            }
            Restart 
            {
                $status_ = (Get-Service $services).Status
                If ($status_.Status -ne "Stopped")
                {
                    write-host "Restarting $services"
                    Restart-Service $services
                } 
                Else 
                {
                    "$services is already stopped. Starting $services"
                    Start-Service $services
                }
            }
        }
        
        Get-Service $services 
    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        Break
    }
}

Function Get-KillServices
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$services
    ) 
    Try
    {
        Write-Host "Stop Services $services"

        Get-Process -Name $services

        Stop-Process -Name $services -Force

        Get-Process -Name $services
    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        Break
    }
}