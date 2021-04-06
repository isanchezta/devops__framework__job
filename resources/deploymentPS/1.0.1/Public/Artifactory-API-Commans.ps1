Function Get-ART-Rest-Headers
{
    [CmdletBinding()]
    Param
    ([Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$PATToken
    )
    Try
    {
        Write-Host "Creating header for PAT"
        $restHeaders = New-Object -TypeName "System.Collections.Generic.Dictionary[[String],[String]]"
        #$authString = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes([String]::Concat((":", $PATToken))))
        $restHeaders.Add("X-JFrog-Art-Api", $PATToken)

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

Function Get-ART-Repositories
{
    [CmdletBinding()]
    Param
    ([Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$Organization,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $restHeaders
    )
    Try
    {
        Write-Host "Start of process to obtain list of Repositories of the $Organization organization"

        $setUri = ('https://artifactory.{0}.com/artifactory/api/storageinfo' -f $Organization)  
        
        $requestResult = Invoke-RestMethod -Uri $setUri -Method Get -ContentType "application/json" -Headers $restHeaders

        Write-Host "End process to obtain list of Repositories of the $Organization organization"

        return $requestResult
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }
}

Function Get-ART-ListUsers
{
    [CmdletBinding()]
    Param
    ([Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$Organization,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $restHeaders
    )
    Try
    {
        Write-Host "Start of process to obtain list of users of the $Organization organization"

        $setUri = ('https://artifactory.{0}.com/artifactory/ui/users' -f $Organization)       

        $requestResult = Invoke-RestMethod -Uri $setUri -Method Get -ContentType "application/json" -Headers $restHeaders

        Write-Host "Records found: " $requestResult.count

        Write-Host "End process to obtain list of users of the $Organization organization"

        return $requestResult
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }
}

Function Get-ART-SearchInfoRepositories
{
    [CmdletBinding()]
    Param
    ([Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$Organization,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $restHeaders,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$Repository
    )
    Try
    {
        Write-Host "Obtaining information from the $Repository repository"

        $setJson = 'items.find({"repo":"' + $Repository + '"})'

        $setUri = ('https://artifactory.{0}.com/artifactory/api/search/aql' -f $Organization)       

        $requestResult = Invoke-RestMethod -Uri $setUri -Method Post -ContentType "text/plain" -Headers $restHeaders -Body $setJson -TimeoutSec 60

        #$requestResult = ($requestResult | ConvertTo-Json -Depth 5)

        return $requestResult
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }
}