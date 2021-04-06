#
# Author: Wilmer Andrade
# Date: 14/11/2019
# 1 - Validate Web Site  = Get-ValidateWebSite
# 2 - Create Web Site    = Get-CreateWebSite
# 3 - Info Web Site      = Get-InfoWebSites
# 4 - Recycle Web Site   = Get-RecycleWebSite
# 5 - Create Web App     = Get-CreateWebApp
# 6 - Delete Web Site    = Get-DeleteWebSite
# 7 - Delete Web App     = Get-DeleteWebApp
#

# 1 Validate Web Site
Function Get-ValidateWebSite
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$WebSiteName              
    )

    Try
    {
        Import-Module WebAdministration

        $SiteName = $Using:WebSiteName

        if(Test-Path IIS:\\SITES\$SiteName)
        {
            Write-Host "The Web Site $SiteName Already Exists!" -ForegroundColor Yellow
            return $true
        }
        else
        {
            Write-Host "The Web Site $SiteName not Exists!" -ForegroundColor Green
            return $false
        }
    }
    Catch 
    {
        #$PSCmdlet.ThrowTerminatingError($_)
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        $ExceptionMessage
        Break
    }    
}

# 2 Create Web Site
Function Get-CreateWebSite 
{
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$WebAppName,
    [Parameter(Mandatory, ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        $Port, 
    [Parameter(Mandatory=$False)]
        [String]$HostName,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [String]$IPAddress,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [String]$RootFSFolder
    <#[Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [String]$Certificate#>
    )

    Try
    {
        $Folder     = $Using:RootFSFolder    
        $SiteName   = $Using:WebSiteName
        $Ports      = $Using:Port
        $HostNames  = $Using:HostName
        $IPAddress_ = $Using:IPAddress
        #$cert       = $Using:Certificate

        Import-module WebAdministration
 
        if($(Get-Website | where-object { $_.name -eq "$SiteName" }) -ne $null) 
        {
            Write-Host "The Website $($SiteName) Already Exists!" -ForegroundColor Yellow
            return $false
        }
        else
        {
            New-Website -Name $SiteName -PhysicalPath "$($Folder)" -ApplicationPool $SiteName #-Port "$Ports$($SiteId)" -HostHeader $HostNames

            Get-WebBinding -Name $SiteName -Port 80 | Remove-WebBinding
            
            foreach ($Port_ in $Ports)
            {
                $arr = $Port_ -split ':'     
                New-WebBinding -Name $SiteName -IPAddress $IPAddress_ -Port $arr[1] -HostHeader $HostNames -Protocol $arr[0]

                #$value = Get-ChildItem Cert:\LocalMachine\My | Where-Object {$_.subject -like "*$cert*"} | Select-Object -ExpandProperty Thumbprint
                ###$value = (Get-ChildItem Cert:\LocalMachine\My | where-object { $_.Subject -like "*CN=$cert*" } | Select-Object -First 1)
                <#
                if($value)
                {   
                    #(Get-WebBinding -Name $SiteName -Port $arr[1] -Protocol $arr[0] -HostHeader $HostNames).AddSslCertificate($value.Thumbprint, "my")
                    (Get-WebBinding -Name $SiteName -Port $arr[1] -Protocol $arr[0] -HostHeader $HostNames).AddSslCertificate($value.GetCertHashString(), "my")      
                    
                    $certificateSubject = $value.Subject

                    Write-Host "The $($arr[0]) binding on $($arr[1]) for $($HostNames) has been created and uses the certificate with subject $($certificateSubject)" -ForegroundColor Green              
                }
                else
                {
                    Write-Host "The $($SiteName) Website has no certificate to associate" -ForegroundColor Yellow
                } 
                #>               
            }
                        
            Write-Host "The $($SiteName) Website created Successfully" -ForegroundColor Green

            return $true
        }
    }
    Catch 
    {
        #$PSCmdlet.ThrowTerminatingError($_)
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        $ExceptionMessage
        Break
    }    
}

# 3 Info Web Sites
Function Get-InfoWebSites
{
    Import-Module WebAdministration
    $APPArray = @()

    foreach ($website in get-childitem IIS:\Sites\)
    {
        $name = "IIS:\\SITES\" + $website.name
        $item = @{}
        $item.WebSiteName = $website.name
        $item.ID = $website.id
        $item.State = (Get-WebsiteState -Name $website.name).Value
        $item.Bindings = (Get-WebBinding -Name $website.name)

        $obj = New-Object PSObject -Property $item
        $APPArray += $obj
    }

    $APPArray | Format-Table -a -Property "WebSiteName", "ID", "State", "Bindings"
}


# 4 Recycle Web Site
Function Get-RecycleWebSite
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory, ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        $WebSiteNamePool,
    [Parameter(Mandatory, ValueFromPipeline)]
    [ValidateSet("Started","Stopped","Recycle")]
        [String]$status
    )
    Try
    {
        Import-Module WebAdministration

        $webSiteNames_ = $WebSiteNamePool
        $status_ = $status

        # Array for final results
        $APPArray = @()

        foreach ($webSiteName in $webSiteNames_)
        {
            echo "Validate exist $webSiteName"
            if(Test-Path "IIS:\AppPools\$webSiteName")
            {            
                echo "Execute $status to $webSiteName"
                switch($status_)
                {
                    Started 
                    {
                        if((Get-WebsiteState -Name $webSiteName).Value -eq "Started")
                        {
                            Write-Host ("Web Site is already started: {0}" -f $webSiteName) -ForegroundColor Yellow
                        }
                        else
                        {
                            Write-Host ("Starting Web Site: {0}" -f $webSiteName) -ForegroundColor Green
                            Start-Website -Name $webSiteName
                        }
                    }
                    Stopped 
                    { 
                        if((Get-WebsiteState -Name $webSiteName).Value -eq "Stopped")
                        {
                            Write-Host ("Web Site is already stopped: {0}" -f $webSiteName) -ForegroundColor Yellow
                        } 
                        else
                        {
                            Write-Host ("Stopping Application Pool: {0}" -f $webSiteName) -ForegroundColor Green
                            Stop-Website -Name $webSiteName
                        }
                    }
                    Recycle 
                    {
                        if((Get-WebsiteState -Name $webSiteName).Value -eq "Stopped")
                        {
                            Write-Host ("You can not recycle the Web Site, the Web Site is stopped, it will be executed Started: {0}" -f $webSiteName) -ForegroundColor Yellow
                            Start-Website -Name $webSiteName
                        }
                        else
                        {
                            Write-Host ("Recycling Web Site: {0}" -f $webSiteName) -ForegroundColor Green
                            Restart-WebSitesSite -Name $webSiteName
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
                Write-Host ("Application Pool does not exist: {0}" -f $webSiteName) -ForegroundColor Red
            }
        }

        echo "Display results"
        Write-Host "`nFinal results:" -ForegroundColor Green
        #$appPool = Get-IISAppPool 
        $appPool = Get-Website

        foreach ($website in get-childitem IIS:\\SITES\)
        {
            $name = "IIS:\\SITES\" + $website.name
            $item = @{}
            $item.WebSiteName = $website.name
            $item.ID = $website.id
            $item.State = (Get-WebsiteState -Name $website.name).Value
            $item.Bindings = (Get-WebBinding -Name $website.name)

            $obj = New-Object PSObject -Property $item
            $APPArray += $obj
        }

        $APPArray | Format-Table -a -Property "WebSiteName", "ID", "State", "Bindings"

    }
    Catch 
    {
        #$PSCmdlet.ThrowTerminatingError($_)
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        $ExceptionMessage
        Break
    }
}

# 5 Create Web App
Function Get-CreateWebApp 
{
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string] $WebSiteName,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()] 
        [string] $WebAppName, 
        [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string] $WebAppNamePool,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string] $RootFSFolder
    )

    Try
    {
        Import-Module WebAdministration

        Write-Host $WebSiteName
        Write-Host $WebAppName
        Write-Host $RootFSFolder
        Write-Host $WebAppNamePool
        Write-Host $AppFolders + $WebAppNames

        $WebSiteNames    = $WebSiteName
        $WebAppNames     = $WebAppName
        $AppFolders      = $RootFSFolder
        $WebAppNamePools = $WebAppNamePool
        $Folders         = $AppFolders + $WebAppNames

        
        
        if (Test-Path "IIS:\Sites\$($WebSiteNames)\$($WebAppNames)")
        {
            New-WebApplication -Site $WebSiteNames -name $WebAppNames  -PhysicalPath $Folders -ApplicationPool $WebAppNamePools -Force
            Write-Host "Web App $($WebAppNames) created Successfully" -ForegroundColor Green
            return $true             
        }
        else 
        {
            Write-Host "Web App $($WebAppNames) no exists" -ForegroundColor Yellow

            return $false  
        }
    }
    Catch 
    {
        #$PSCmdlet.ThrowTerminatingError($_)
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        $ExceptionMessage
        Break
    }    
}

# 6 Delete Web Site
Function Get-DeleteWebSite
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$WebSiteName                   
    )
    Try
    {
        Import-Module WebAdministration

        $SiteName = $Using:WebSiteName

        if(Test-Path IIS:\\SITES\$SiteName)  
        {
            Write-Host "Web Site $SiteName deleted Successfully" -ForegroundColor Green
            Remove-Website -Name $WebSiteName
            return $true
        }  
        else
        {   
            Write-Host "Web Site $SiteName not Exists!" -ForegroundColor Yellow
            return $False
        }
    }
    Catch 
    {
        #$PSCmdlet.ThrowTerminatingError($_)
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        $ExceptionMessage
        Break
    }    
}

# 7 Delete Web App
Function Get-DeleteWebApp
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$WebSiteName,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$WebAppName                      
    )
    Try
    {
        Import-Module WebAdministration

        $SiteName = $Using:WebSiteName
        $AppName  = $Using:WebAppName

        if(Test-Path "IIS:\\SITES\$SiteName\$AppName")  
        {
            Write-Host "Web Site $SiteName deleted Successfully" -ForegroundColor Green
            Remove-WebApplication -Site $WebSiteName -Name $AppName 
            return $true
        }  
        else
        {
            Write-Host "App Site $SiteName\$AppName not Exists!" -ForegroundColor Yellow
            return $False
        }
    }
    Catch 
    {
        #$PSCmdlet.ThrowTerminatingError($_)
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        $ExceptionMessage
        Break
    }    
}