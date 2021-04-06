#
# Author: Wilmer Andrade
# Date: 13/11/2019
# Este script:
# 1 - Validate AppPool                  = Get-ValidateAppPool
# 2 - Create AppPool                    = Get-CreateAppPool
# 3.1 - Properties AppPool              = Get-WebSitePropertiesAppPool
# 3.2 - Properties AppPool              = Get-WebAppPropertiesAppPool
# 4 - Info de AppPool                   = Get-InfoAppPools
# 5 - Change AppPool to Site or AppWeb  = Get-ChangeAppPool
# 6 - Delete AppPool to Site or AppWeb  = Get-DeleteAppPool
# 7 - Recycle AppPool                   = Get-RecycleAppPool
# 8 - List AppPool                      = Get-ListAppPoolsState
#

# 1 Validate AppPool
Function Get-ValidateAppPool
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$Name              
    )

    Try
    {
        Import-Module WebAdministration

        $Name_ = $Name

        if(Test-Path IIS:\AppPools\$Name_)
        {
            Write-Host "The AppPool $Name_ Already Exists!" -ForegroundColor Green
            return $true
        }
        else
        {
            Write-Host "The AppPool $Name_ not Exists!" -ForegroundColor Yellow
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

# 2 Create AppPool
Function Get-CreateAppPool 
{
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string] $Name
    )

    Try
    {
        $Name_ = $Name

        Import-module WebAdministration
 
        if(Test-Path IIS:\AppPools\$Name_) 
        {
            Write-Host "The AppPool $Name_ already exists" -ForegroundColor Yellow
            return $false
        }
        else
        {
            New-WebAppPool -Name $Name_
            Write-Host "The AppPool $Name_ created Successfully" -ForegroundColor Green
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

# 3.1 Parametros para determinar configuracion de AppPool
Function Get-WebSitePropertiesAppPool 
{
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$WebSiteNamePool,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$managedRuntimeVersion,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [int]$managedPipelineMode,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [bool]$enable32BitAppOnWin64
    )

    Try
    {
        Import-module WebAdministration

        $WebSite_ = $Using:WebSiteNamePool
        $managedRuntimeVersions = $managedRuntimeVersion
        $managedPipelineModes = $managedPipelineMode
        $enable32BitAppOnWins64 = $enable32BitAppOnWin64     

        Set-ItemProperty IIS:\AppPools\$WebSite_ managedRuntimeVersion $managedRuntimeVersions -Force
        Write-Host "The AppPool $WebSite_ was updated to $managedRuntimeVersions managed runtime versions!" -ForegroundColor Green

        if($managedPipelineModes -ine $null)
        {
            Set-ItemProperty IIS:\AppPools\$WebSite_ managedPipelineMode $managedPipelineModes -Force
            Write-Host "The AppPool $WebSite_ was updated to $managedPipelineModes managed pipeline mode!" -ForegroundColor Green
        }
        if($enable32BitAppOnWins64)
        {
            Set-ItemProperty IIS:\AppPools\$WebSite_ enable32BitAppOnWin64 $enable32BitAppOnWins64 -Force
            Write-Host "The AppPool $WebSite_ was updated to $enable32BitAppOnWins64 enable 32 Bit!" -ForegroundColor Green
        }        
    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        Break
    }    
}

# 3.2 Parametros para determinar configuracion de AppPool
Function Get-WebAppPropertiesAppPool 
{
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$WebAppNamePool,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$managedRuntimeVersion,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [int]$managedPipelineMode,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [bool]$enable32BitAppOnWin64,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [int]$maxProcesses,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [int]$identityType,
    [Parameter(Mandatory=$False)]
    [ValidateNotNullOrEmpty()]
        [string]$useridentityType,
    [Parameter(Mandatory=$False)]
    [ValidateNotNullOrEmpty()]
        [string]$passwordidentityType
    )

    Try
    {
        Import-module WebAdministration

        $WebApp_ = $Using:WebAppNamePool
        $managedRuntimeVersions = $managedRuntimeVersion
        $managedPipelineModes = $managedPipelineMode
        $enable32BitAppOnWins64 = $enable32BitAppOnWin64
        $maxProcesses_ = $maxProcesses
        $identityTypes = $identityType
        $users = $useridentityType
        $passwords = $passwordidentityType  
        
        Set-ItemProperty IIS:\AppPools\$($WebApp_) managedRuntimeVersion $managedRuntimeVersions -Force
        Write-Host "The AppPool $WebApp_ was updated to $managedRuntimeVersions managed runtime versions!" -ForegroundColor Green

        if($managedPipelineModes -ine $null)
        {
            Set-ItemProperty IIS:\AppPools\$($WebApp_) managedPipelineMode $managedPipelineModes -Force
            Write-Host "The AppPool $WebApp_ was updated to $managedPipelineModes managed pipeline mode!" -ForegroundColor Green
        }
        if($enable32BitAppOnWins64)
        {
            Set-ItemProperty IIS:\AppPools\$($WebApp_) enable32BitAppOnWin64 $enable32BitAppOnWins64
            Write-Host "The AppPool $($WebApp_) was updated to $enable32BitAppOnWins64 enable 32 Bit!" -ForegroundColor Green
        }
        if($maxProcesses_ -ine $null)
        {
            Set-ItemProperty IIS:\AppPools\$($WebApp_) maxProcesses $($maxProcesses_)
            Write-Host "The AppPool $($WebApp_) was updated to $($maxProcesses_) max processes!" -ForegroundColor Green
        }

        if($identityTypes -ine $null)
        {
            switch($identityTypes)
            {
                1
                {
                    Set-ItemProperty -Path IIS:\AppPools\$($WebApp_) -Name processmodel.identityType -Value 1
                    Write-Host "The AppPool $($WebApp_) was updated identityTypes to $identityTypes! 1" -ForegroundColor Green
                }
                2
                {
                    Set-ItemProperty -Path IIS:\AppPools\$($WebApp_) -Name processmodel.identityType -Value 2
                    Write-Host "The AppPool $($WebApp_) was updated identityTypes to $identityTypes! 2" -ForegroundColor Green
                }
                3
                {
                    Set-ItemProperty -Path IIS:\AppPools\$($WebApp_) -Name processmodel -Value @{username=$users;password=$passwords;identityType=3}
                    Write-Host "The AppPool $($WebApp_) was updated identityTypes to $identityTypes, username and password! 3" -ForegroundColor Green
                }
                4
                {
                    Set-ItemProperty -Path IIS:\AppPools\$($WebApp_) -Name processmodel.identityType -Value 4
                    Write-Host "The AppPool $($WebApp_) was updated identityTypes to $identityTypes! 4" -ForegroundColor Green
                }
                default 
                { 
                    Set-ItemProperty -Path IIS:\AppPools\$($WebApp_) -Name processmodel.identityType -Value 4
                    Write-Host "The AppPool $($WebApp_) was updated identityTypes to $identityTypes! 5" -ForegroundColor Green
                }
            }
            
        }
        
    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Break
    }    
}

# 4 Info AppPools
Function Get-InfoAppPools
{
    Import-Module WebAdministration

    $APPArray = @()

    foreach ($webapp in get-childitem IIS:\AppPools\)
    {
        $name = "IIS:\AppPools\" + $webapp.name
        $item = @{}
        $item.WebAppName = $webapp.name
        $item.Version = (Get-ItemProperty $name managedRuntimeVersion).Value
        $item.State = (Get-WebAppPoolState -Name $webapp.name).Value
        $item.UserIdentityType = $webapp.processModel.identityType

        $obj = New-Object PSObject -Property $item
        $APPArray += $obj
    }

    $APPArray | Format-Table -a -Property "WebAppName", "Version", "State", "UserIdentityType"
}

# 5 Change AppPool to Site or AppWeb
Function Get-ChangeAppPool
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$WebSiteName,
    [Parameter()]
        [string]$WebAppName,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$NewAppPool                      
    )

    Try
    {
        Import-Module WebAdministration

        $Root = @()
        $SiteName = $Using:WebSiteName
        $AppName  = $Using:WebAppName

        if($AppName -ne $Null -and $AppName -ne "")
        {
            $Root = $SiteName + "\" + $AppName
        }
        else
        {
            $Root = $SiteName
        }

        Set-ItemProperty "IIS:\Sites\$Root" -Name ApplicationPool -Value $NewAppPool
    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        Break
    }    
}

# 6 Delete AppPool
Function Get-DeleteAppPool
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$WebSiteName,
    [Parameter()]
        [string]$WebAppName                      
    )
    Try
    {
        Import-Module WebAdministration

        $Root = @()
        $SiteName = $Using:WebSiteName
        $AppName  = $Using:WebAppName

        if($AppName -ne $Null -and $AppName -ne "")
        {
            $Root = $SiteName + "\" + $AppName
        }
        else
        {
            $Root = $SiteName
        }
        
        [int]$count = (Get-Item "IIS:\Sites\$Root" | select -Property “ApplicationPool” | ForEach-Object {$_.applicationPool}).Count

        if($count -gt 0)  
        {
            Write-Host "AppPool has 1 or more associated applications" -ForegroundColor Yellow
            return $false
        }  
        else
        {
            Write-Host $count
            #Get-ValidateAppPool -WebSiteName "trainmar"
            Remove-WebAppPool -Name $AppName
            #Remove-Item "IIS:\AppPools\$Root" -Recurse
            Write-Host "AppPool was delete" -ForegroundColor Green
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

# 7 Recycle Appool
Function Get-RecycleAppPool
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $appPoolNames,
    [Parameter(Mandatory, ValueFromPipeline)]
    [ValidateSet("Started","Stopped","Recycle")]
        [String]$status
    )
    Try
    {
        Import-Module WebAdministration

        $appPoolNames_ = $appPoolNames
        $status_ = $status

        # Array for final results
        $APPArray = @()

        # Get the number of retries
        $retries = "60"
        # Get the number of attempts in milliseconds (6000 = 1 Seg)
        $delay = "1" 

        foreach ($appPoolName in $appPoolNames_)
        {
            Write-Host "Validate exist $appPoolName"
            if(Test-Path "IIS:\AppPools\$appPoolName")
            {            
                Write-Host "Execute $status_ to $appPoolName"
                switch($status_)
                {
                    Started 
                    {
                        if((Get-WebAppPoolState -Name $appPoolName).Value -eq "Started")
                        {
                            Write-Host ("AppPool is already started: {0}" -f $appPoolName) -ForegroundColor Yellow
                        }
                        else
                        {
                            Write-Host ("Starting AppPool: {0}" -f $appPoolName) -ForegroundColor Green
                            Start-WebAppPool -Name $appPoolName

                            $state = (Get-WebAppPoolState $appPoolName).Value
                            $counter = 1

                            # Wait for the app pool to the "Started" before proceeding
                            do
                            {
                                $state = (Get-WebAppPoolState $appPoolName).Value
                                Write-Host "$counter/$retries Waiting for IIS app pool $appPoolName to turn on completely. Current status: $state"
                                $counter++
                                Start-Sleep -Seconds $delay
                            }
                            while($state -ne "Started" -and $counter -le $retries)

                            # Throw an error if the app pool is not Started
                            if($counter -gt $retries) 
                            {
                                throw "Could not turn on IIS app pool $appPoolName. `nTry to increase the number of retries ($retries) or delay between attempts ($delay seconds)." 
                            }
                        }
                    }
                    Stopped 
                    { 
                        if((Get-WebAppPoolState -Name $appPoolName).Value -eq "Stopped")
                        {
                            Write-Host ("AppPool is already stopped: {0}" -f $appPoolName) -ForegroundColor Yellow
                        } 
                        else
                        {
                            Write-Host ("Stopping AppPool: {0}" -f $appPoolName) -ForegroundColor Green
                            Stop-WebAppPool -Name $appPoolName

                            $state = (Get-WebAppPoolState $appPoolName).Value
                            $counter = 1

                            # Wait for the app pool to the "Stopped" before proceeding
                            do
                            {
                                $state = (Get-WebAppPoolState $appPoolName).Value
                                Write-Host "$counter/$retries Waiting for IIS app pool $appPoolName to shut down completely. Current status: $state"
                                $counter++
                                Start-Sleep -Seconds $delay
                            }
                            while($state -ne "Stopped" -and $counter -le $retries)

                            # Throw an error if the app pool is not stopped
                            if($counter -gt $retries) 
                            {
                                throw "Could not shut down IIS app pool $appPoolName. `nTry to increase the number of retries ($retries) or delay between attempts ($delay milliseconds)." 
                            }
                        }
                    }
                    Recycle 
                    {
                        if((Get-WebAppPoolState -Name $appPoolName).Value -eq "Stopped")
                        {
                            Write-Host ("You can not recycle the app, the app is stopped, it will be executed Started: {0}" -f $appPoolName) -ForegroundColor Yellow
                            Start-WebAppPool -Name $appPoolName
                        }
                        else
                        {
                            Write-Host ("Recycling AppPool: {0}" -f $appPoolName) -ForegroundColor Green
                            Restart-WebAppPool -Name $appPoolName
                        }                           
                    }
                    default 
                    { 
                        'Unknown' 
                    }
                }
            }
            else
            {
                Write-Host ("AppPool does not exist: {0}" -f $appPoolName) -ForegroundColor Red
            }
        }

        Write-Host "Display results"
        Write-Host "`nFinal results:" -ForegroundColor Green
        #$appPool = Get-IISAppPool 
        $appPool = Get-WebApplication

        foreach ($webapp in get-childitem IIS:\AppPools\)
        {
            $name = "IIS:\AppPools\" + $webapp.name
            $item = @{}
            $item.WebAppName = $webapp.name
            $item.Version = (Get-ItemProperty $name managedRuntimeVersion).Value
            $item.State = (Get-WebAppPoolState -Name $webapp.name).Value
            $item.UserIdentityType = $webapp.processModel.identityType

            $obj = New-Object PSObject -Property $item
            $APPArray += $obj
        }

        $APPArray | Format-Table -a -Property "WebAppName", "Version", "State", "UserIdentityType"

    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        Break
    }
}

Function Get-PropertiesAppPool_v2
{
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$AppPoolName,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$managedRuntimeVersion,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [int]$managedPipelineMode,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [bool]$enable32BitAppOnWin64,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [int]$identityType,
    [Parameter(Mandatory=$False)]
    [ValidateNotNullOrEmpty()]
        [string]$useridentityType,
    [Parameter(Mandatory=$False)]
    [ValidateNotNullOrEmpty()]
        [string]$passwordidentityType
    )

    Try
    {
        Import-module WebAdministration

        $Name_ = $AppPoolName
        $managedRuntimeVersions = $managedRuntimeVersion
        $managedPipelineModes = $managedPipelineMode
        $enable32BitAppOnWins64 = $enable32BitAppOnWin64
        $maxProcesses = $maxProcesses_
        $identityTypes = $identityType
        $users = $useridentityType
        $passwords = $passwordidentityType

        Write-Host "$identityTypes $users $passwords"        

        New-Item IIS:\AppPools\$Name_
        $NewPool = Get-Item IIS:\AppPools\$Name_
        $NewPool.ManagedRuntimeVersion = $managedRuntimeVersions

        Write-Host "The AppPool $Name_ was updated to $managedRuntimeVersions managed runtime versions!" -ForegroundColor Green
        

        if($managedPipelineModes)
        {
            $NewPool.ManagedPipelineMode = $managedPipelineModes
            Write-Host "The AppPool $Name_ was updated to $managedPipelineModes manage pipeline modes!" -ForegroundColor Green
        }
        if($enable32BitAppOnWins64)
        {
            $NewPool.Enable32BitAppOnWin64 = $enable32BitAppOnWins64
            Write-Host "The AppPool $Name_ was updated to $enable32BitAppOnWins64 enable 32 bit!" -ForegroundColor Green
        }
        if($maxProcesses_)
        {
            $NewPool.ProcessModel.MaxProcesses = $maxProcesses_
            Write-Host "The AppPool $Name_ was updated to $maxProcesses_ max processes!" -ForegroundColor Green
        }
        if($identityTypes)
        {
            switch($identityTypes)
            {
                1
                {
                    $NewPool.ProcessModel.IdentityType = 1
                    Write-Host "The AppPool $Name_ was updated identityTypes to $identityTypes!" -ForegroundColor Green
                }
                2
                {
                    $NewPool.ProcessModel.IdentityType = 2
                    Write-Host "The AppPool $Name_ was updated identityTypes to $identityTypes!" -ForegroundColor Green
                }
                3
                {                    
                    $NewPool.ProcessModel.Username = $users
                    $NewPool.ProcessModel.Password = $passwords
                    $NewPool.ProcessModel.IdentityType = 3
                    Write-Host "The AppPool $Name_ was updated identityTypes to $identityTypes, username and password!" -ForegroundColor Green
                }
                4
                {
                    $NewPool.ProcessModel.IdentityType = 4
                    Write-Host "The AppPool $Name_ was updated identityTypes to $identityTypes!" -ForegroundColor Green
                }
                default 
                { 
                    $NewPool.ProcessModel.IdentityType = 4
                    Write-Host "The AppPool $Name_ was updated identityTypes to $identityTypes!" -ForegroundColor Green
                }
            }
            
        }

        $NewPool | Set-Item        
    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        Break
    }    
}

# 8 List Appool
Function Get-ListAppPoolsState
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory, ValueFromPipeline)]
    [ValidateSet("Started","Stopped")]
        [String]$status
    )
    Try
    {
        Import-Module WebAdministration

        $status_ = $status

        $AppPools=Get-ChildItem IIS:\AppPools | where {$_.State -eq $status_} | select Name

        return $AppPools
    }
    Catch 
    {

        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        Break
    }
}