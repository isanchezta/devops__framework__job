#Install-Module -Name PowerShellForGitHub -Force -Verbose -Scope CurrentUser -Confirm:$False
#Import-Module -Name PowerShellForGitHub -ErrorAction SilentlyContinue

#Get-Module -Name PowerShellForGitHub -ListAvailable
#Get-Command -Module PowerShellForGitHub -Name “Get-*”
#Get-Command -Module PowerShellForGitHub -Name “Get-GitHubOrganizationMember” | fl

Function Get-GitHub-Rest-Headers
{
    [CmdletBinding()]
    Param
    ([Parameter(Mandatory=$False)]
        [string]$User,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$PATToken
    )
    Try
    {
        Write-Host "Creating header for PAT"

        $restHeaders = New-Object -TypeName "System.Collections.Generic.Dictionary[[String],[String]]"
        #$authString = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes(("{0}:{1}" -f $User,$PATToken)))
        $authString = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes([String]::Concat((":", $PATToken))))
        $restHeaders.Add("Authorization", [String]::Concat("Basic ", $authString))
        #$restHeaders.Add("Accept", "application/vnd.github.v3+json")
        #$restHeaders.Add("x-access-token", $PATToken)

        Write-Host "Created header for PAT"

        return $restHeaders
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }    
}

Function Get-GitHub-ListWebHooks
{
    [CmdletBinding()]
    param(
        [Parameter(Mandatory)]
        [ValidateNotNullOrEmpty()]
        [string] $OwnerName,
        [Parameter(Mandatory)]
        [ValidateNotNullOrEmpty()]
        [string] $RepositoryName,
        [Parameter(Mandatory)]
        [ValidateNotNullOrEmpty()]
        $restHeaders
    )
    Try
    {
        Write-Host "Start of process to obtain list of webhooks $RepositoryName  of the $OwnerName organization"

        $setUri = ("https://api.github.com/repos/{0}/{1}/hooks" -f $OwnerName, $RepositoryName)

        $requestResult = Invoke-RestMethod -Uri $setUri -Method Get -ContentType "application/json" -Headers $restHeaders

        Write-Host "End process to obtain list of webhooks $RepositoryName  of the $OwnerName organization"

        return $requestResult
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }    
}

Function Get-GitHub-CreateWebHooks
{
    [CmdletBinding()]
    param(
        [Parameter(Mandatory)]
        [ValidateNotNullOrEmpty()]
        [string] $OwnerName,
        [Parameter(Mandatory)]
        [ValidateNotNullOrEmpty()]
        [string] $RepositoryName,
        [Parameter(Mandatory)]
        [ValidateNotNullOrEmpty()]
        [string] $url,
        [Parameter(Mandatory)]
        [ValidateNotNullOrEmpty()]
        $restHeaders
    )
    Try
    {
        $bodyJson = @{
            "name"= "web"
            "active"= $true
            "events"= @(
              "push"
            )
            "config"= @{
              "url"= "$url"
              "content_type"= "json"
              "insecure_ssl"= "0"
            }
        }

        $bodyString=($bodyJson | ConvertTo-Json -Depth 5) 
        
        $setUri = ("https://api.github.com/repos/{0}/{1}/hooks" -f $OwnerName, $RepositoryName)

        $requestResult = Invoke-RestMethod -Uri $setUri -Headers $restHeaders -Body $bodyString -Method Post -ContentType "application/json"

        Write-Host "Create webhooks in $RepositoryName repository"

        return $requestResult
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }    
}

Function Get-GitHub-ActiveWebHooks
{
    [CmdletBinding()]
    param(
        [Parameter(Mandatory)]
        [ValidateNotNullOrEmpty()]
        [string] $OwnerName,
        [Parameter(Mandatory)]
        [ValidateNotNullOrEmpty()]
        [string] $RepositoryName,
        [Parameter(Mandatory)]
        [ValidateNotNullOrEmpty()]
        [string] $HookId,
        [Parameter(Mandatory, ValueFromPipeline)]
        [ValidateSet($true,$false)]
        $status,
        [Parameter(Mandatory)]
        [ValidateNotNullOrEmpty()]
        $restHeaders
    )
    Try
    {
        $bodyJson = @{
            "active"= $status
        }

        $bodyString=($bodyJson | ConvertTo-Json -Depth 5) 
        
        $setUri = ("https://api.github.com/repos/{0}/{1}/hooks/{2}" -f $OwnerName, $RepositoryName, $HookId)

        $requestResult = Invoke-RestMethod -Uri $setUri -Headers $restHeaders -Body $bodyString -Method Post -ContentType "application/json"

        Write-Host "Update state $status webhook in $RepositoryName repository"

        return $requestResult
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }    
}