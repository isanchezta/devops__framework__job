Function Get-AZD-Rest-Headers
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
        #$authString = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes(("{0}:{1}" -f $User,$PAT)))
        $authString = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes([String]::Concat((":", $PATToken))))
        $restHeaders.Add("Authorization", [String]::Concat("Basic ", $authString))

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

Function Get-AZD-ListUsers
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$Organization,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$APIVersion,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $restHeaders
    )
    Try
    {
        Write-Host "Start of process to obtain list of users of the $Organization organization"

        $setUri = ("https://vssps.dev.azure.com/{0}/_apis/graph/users?api-version={1}" -f $Organization, $APIVersion)
        #$setUri = ("https://vssps.dev.azure.com/" + $Organization + "/_apis/graph/users?api-version=" + $APIVersion)

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

Function Get-AZD-UserEntitlements
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$Organization,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$userId,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$APIVersion,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $restHeaders
    )
    Try
    {
        $setUri = ("https://vsaex.dev.azure.com/{0}/_apis/userentitlements/{1}?api-version={2}" -f $Organization, $userId, $APIVersion)

        $requestResult = Invoke-RestMethod -Uri $setUri -Method Get -ContentType "application/json" -Headers $restHeaders

        return $requestResult
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }
}

Function Get-AZD-ListProject
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Organization,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$APIVersion,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $restHeaders
    )
    Try
    {
        Write-Host "Sending a REST call to list a project"

        $top='$top=1000'

        $setUri = ("https://dev.azure.com/{0}/_apis/projects?$top&api-version={1}" -f $Organization, "5.0")

        $list = Invoke-RestMethod -Uri $setUri -Method Get -ContentType "application/json" -Headers $restHeaders

        #$resultList = @{}
        #$resultList = $list.value.id
        #$resultList = $list.value.name

        return $list   
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }    
}

Function Get-AZD-ListRepositories
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Organization,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Project,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$APIVersion,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $restHeaders
    )
    Try
    {
        Write-Host "Sending a REST call to list a repositories"

        $setUri = ("https://dev.azure.com/{0}/{1}/_apis/git/repositories?api-version={2}" -f $Organization, $Project, "5.0")

        $list = Invoke-RestMethod -Uri $setUri -Method Get -ContentType "application/json" -Headers $restHeaders

        #$resultList = @{}
        #$resultList = $list.value.id
        #$resultList = $list.value.name

        return $list   
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }    
}

Function Get-AZD-ListBranch
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Organization,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Project,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Repository,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$APIVersion,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $restHeaders
    )
    Try
    {
        Write-Host "Sending a REST call to list a branches"

        $setUri = ("https://dev.azure.com/{0}/{1}/_apis/git/repositories/{2}/stats/branches?api-version={3}" -f $Organization, $Project, $Repository, "5.0")

        $list = Invoke-RestMethod -Uri $setUri -Method Get -ContentType "application/json" -Headers $restHeaders

        #$resultList = @{}
        #$resultList = $list.value.name

        return $list   
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }    
}

Function Get-AZD-ListBuild
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Organization,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Project,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$APIVersion,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $restHeaders
    )
    Try
    {
        Write-Host "Sending a REST call to list a build"

        $setUri = ("https://dev.azure.com/{0}/{1}/_apis/build/builds?api-version={2}" -f $Organization, $Project, "5.0")

        $list = Invoke-RestMethod -Uri $setUri -Method Get -ContentType "application/json" -Headers $restHeaders

        #$resultList = @{}
        #$resultList = $list.value.id
        #$resultList = $list.value.name

        return $list   
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }    
}

Function Get-AZD-ListRelease
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Organization,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Project,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$APIVersion,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $restHeaders
    )
    Try
    {
        Write-Host "Sending a REST call to list a release"

        $setUri = ("https://vsrm.dev.azure.com/{0}/{1}/_apis/release/deployments?api-version={2}" -f $Organization, $Project, "5.0")

        $list = Invoke-RestMethod -Uri $setUri -Method Get -ContentType "application/json" -Headers $restHeaders

        #$resultList = @{}
        #$resultList = $list.value.id
        #$resultList = $list.value.name

        return $list   
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }    
}

Function Get-AZD-ListBuild-Table
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
        $ListBuild
    )
    Try
    {
        $tableBuild = New-Object System.Data.DataTable

        $tableBuild.Columns.Add("id","int32") | Out-Null
        $tableBuild.Columns.Add("result","string") | Out-Null
        #$tableBuild.Columns.Add("startTime","datetime") | Out-Null
        $tableBuild.Columns.Add("definitionId","int32") | Out-Null
        $tableBuild.Columns.add("definitionName","string") | Out-Null
        $tableBuild.Columns.add("sourceBranch","string") | Out-Null
        $tableBuild.Columns.add("repositoryName","string") | Out-Null

        ForEach ($LBuild in $ListBuild)
        {
            $r = $tableBuild.NewRow()

            $r.id = $LBuild.id
            $r.result = $LBuild.result
            #$r.startTime = $LBuild.startTime
            $r.definitionId = $LBuild.definition.id
            $r.definitionName = $LBuild.definition.name
            $r.sourceBranch = $LBuild.sourceBranch -replace ("refs/heads/","")
            $r.repositoryName = $LBuild.repository.name

            $tableBuild.Rows.Add($r)
        } 
        
        return  $tableBuild
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }     
}

Function Get-AZD-ListRelease-Table
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
        $ListRelease
    )
    Try
    {
        $tableRelease = New-Object System.Data.DataTable

        $tableRelease.Columns.Add("id","int32") | Out-Null
        $tableRelease.Columns.add("status","string") | Out-Null
        #$tableRelease.Columns.add("startTime","datetime") | Out-Null
        $tableRelease.Columns.Add("definitionId","int32") | Out-Null
        $tableRelease.Columns.Add("definitionName","string") | Out-Null
        $tableRelease.Columns.Add("sourceBranch","string") | Out-Null
        $tableRelease.Columns.Add("repositoryName","string") | Out-Null
        $tableRelease.Columns.add("releaseDefinition","string") | Out-Null

        ForEach ($LRelease in $ListRelease)
        {
            $r = $tableRelease.NewRow()

            $r.id = $LRelease.id
            $r.status = $LRelease.deploymentStatus
            #$r.startTime = $LRelease.startedOn
            $r.definitionId = $LRelease.release.artifacts.definitionReference.definition.id
            $r.definitionName = $LRelease.release.artifacts.definitionReference.definition.name
            $r.sourceBranch = $LRelease.release.artifacts.definitionReference.branch.name -replace ("refs/heads/","")        
            $r.repositoryName = $LRelease.release.artifacts.definitionReference.repository.name
            $r.releaseDefinition = $LRelease.releaseDefinition.name    

            $tableRelease.Rows.Add($r)
        }
        
        return  $tableRelease
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }     
}

Function Get-AZD-ValidatePath
{ 
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
        $Branch,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $restHeaders
    )
    Try
    {
        $bodyJson = @{
            "includeContentMetadata" = "$true"
            "itemDescriptors" = @(@{
                "path" = "/pipes"
                "recursionLevel" = "none"
                "version" = $Branch
                "versionOptions" = "none"
                "versionType" = "branch"
            })
    
        }

        $bodyString=($bodyJson | ConvertTo-Json -Depth 5)

        $Uri = "https://dev.azure.com/ITACCIONA/f10cecb1-2de7-48ec-b7e2-ea7f65b3e9a0/_apis/git/repositories/d1661cfe-e2a2-458e-b585-1848339c5c9e/itemsbatch?api-version=5.1"
        $buildresponse = Invoke-RestMethod -Uri $Uri -Method Post -ContentType "application/json" -Headers $restHeaders -Body $bodyString
        
        return $buildresponse.value.isFolder
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage -ErrorAction SilentlyContinue
        return "$false"
        #Exit 1
    }
}

Function Get-AZD-ListsCommit
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Organization,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Project,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$Repositories,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$fromDate,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$APIVersion,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $restHeaders
    )
    Try
    {
        Write-Host "Sending a REST call to list a commit"
        
        $top = '$top=1'

        $setUri = ("https://dev.azure.com/{0}/{1}/_apis/git/repositories/{2}/commits?&searchCriteria.fromDate={3}&searchCriteria.$top&api-version={4}"  -f $Organization, $Project, $Repositories, $fromDate, "5.0")
        $list = Invoke-RestMethod -Uri $setUri -Method Get -ContentType "application/json" -Headers $restHeaders
        return $list
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }
}

Function Get-AZD-InfoRepositories
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Organization,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Repositories,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$APIVersion,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $restHeaders
    )
    Try
    {
        Write-Host "Sending a REST call to info repositories"

        $setUri = ("https://dev.azure.com/{0}/_apis/git/repositories/{1}?api-version={2}" -f $Organization, $Repositories, "5.0")

        $list = Invoke-RestMethod -Uri $setUri -Method Get -ContentType "application/json" -Headers $restHeaders

        return $list   
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }    
}

Function Get-AZD-ListServiceEndpoint
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Organization,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Project,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$APIVersion,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $restHeaders
    )
    Try
    {
        Write-Host "Sending a REST call to info List Service Endpoint All"

        $setUri = ("https://dev.azure.com/{0}/{1}/_apis/serviceendpoint/endpoints?api-version={2}" -f $Organization, $Project, "6.0-preview.3")

        $list = Invoke-RestMethod -Uri $setUri -Method Get -ContentType "application/json" -Headers $restHeaders

        return $list   
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }    
}

Function Get-AZD-SpecificServiceEndpoint
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Organization,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Project,
    [Parameter(Mandatory, ValueFromPipeline)]
    [ValidateSet("artifactoryService","jenkins")]
        [string]$Type,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$APIVersion,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $restHeaders
    )
    Try
    {
        Write-Host "Sending a REST call to info Specific Service Endpoint"

        $setUri = ("https://dev.azure.com/{0}/{1}/_apis/serviceendpoint/endpoints?type={2}&api-version={3}" -f $Organization, $Project, $Type, "6.0-preview.3")

        $list = Invoke-RestMethod -Uri $setUri -Method Get -ContentType "application/json" -Headers $restHeaders

        return $list.value   
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }    
}

Function Get-AZD-SearchEndpointId
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Organization,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Project,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$endpointsId,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$APIVersion,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $restHeaders
    )
    Try
    {
        Write-Host "Sending a REST call to info Search Endpoint"

        $setUri = ("https://dev.azure.com/{0}/{1}/_apis/serviceendpoint/endpoints/{2}?api-version={3}" -f $Organization, $Project, $endpointsId, "6.0-preview.3")

        $list = Invoke-RestMethod -Uri $setUri -Method Get -ContentType "application/json" -Headers $restHeaders

        return $list.value   
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }    
}

Function Get-AZD-UpdateEndpointId
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$Organization,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$endpointsId,
    [Parameter(Mandatory,ValueFromPipeline)]
    [ValidateNotNullOrEmpty()]
        [string]$APIVersion,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        $restHeaders
    )
    Try
    {
        Write-Host "Sending a REST call to info Search Endpoint"

        $setUri = ("https://dev.azure.com/{0}/_apis/serviceendpoint/endpoints/{1}?api-version={2}" -f $Organization, $endpointsId, "6.0-preview.3")
        <#
        $setBody = @{
                        "id" = $endpointsId
                        "name" = "ARTIFACTORY-TEST"
                        "type" = "artifactoryService"
                        "url" = "http://articloudpro.jfrog.io/"
                        "description" = ""
                        "authorization" = @{
                            "parameters"= @{
                                "username" = "app_energia_devops@acciona.com"
                                "password" = "AKCp5ek8K4ZfuQcTKUuoJxTURu5y318UZJyLLa8bYvH8fr2EhPLj6GSvovsc1LJgvKmNTLD3n"
                            }
                            "scheme" = "UsernamePassword"
                        }
                        "isShared" = $false
                        "isReady" = $true
                        "owner" = "Library"
                    }
                    #>
        $setBody = "{
                        ""id"":$endpointsId
                        ""name"":""ARTIFACTORY-TEST""
                        ""type"":""artifactoryService""
                        ""url"":""http://articloudpro.jfrog.io/""
                        ""authorization"":{
                            ""parameters"":{
                                ""username"":""app_energia_devops@acciona.com""
                                ""password"":""AKCp5ek8K4ZfuQcTKUuoJxTURu5y318UZJyLLa8bYvH8fr2EhPLj6GSvovsc1LJgvKmNTLD3n""
                            }
                            ""scheme"":""UsernamePassword""
                        }
                        ""isShared"":false
                        ""isReady"":true
                        ""owner"":""Library""
                    }"

        #$setBodyJson = ($setBody | ConvertTo-Json -Depth 5)

        $list = Invoke-RestMethod -Uri $setUri -Method Patch -ContentType "application/json" -Headers $restHeaders -Body $setBodyJson

        return $list   
    }
    Catch
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
        #Exit 1
    }    
}