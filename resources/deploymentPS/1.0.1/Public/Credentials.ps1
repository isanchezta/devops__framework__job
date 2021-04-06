Function Get-Credentials
{
    [CmdletBinding()]
    Param
    (
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$user, 
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$pword,
    [Parameter(Mandatory)]
    [ValidateNotNullOrEmpty()]
        [string]$server,
    [Parameter(Mandatory)]
    [ValidateSet("Basic","Credssp","Kerberos")]
        $auth,
    [Parameter(Mandatory=$false)]
        $useSSLCet
    ) 	
    Try
    {
        Write-Host "Creando la Session -> Password"
        $pass  = ConvertTo-SecureString -AsPlainText -Force -String $pword
        
        Write-Host "Creando la Session -> Credenciales"
        $credentials = New-Object -TypeName System.Management.Automation.PSCredential -ArgumentList $user,$pass        
		
		Write-Host "UseSSL $useSSLCet"
		
        if($useSSLCet -eq "False")
        {
            Write-Host "Creando la Session -> PSSession -> UseSSL False"
            $session = New-PSSession -ComputerName $server -Credential $credentials -Authentication $auth            
        }
        else
        {
            Write-Host "Creando la Session -> PSSession -> UseSSL True"
            #Se agrega -UseSSL -SessionOption (New-PSSessionOption -SkipCACheck -SkipCNCheck) por temas de certificado SSL en la maquina remota puerto habilitado 5986 (HTTPS)
            $session = New-PSSession -ComputerName $server -Credential $credentials -UseSSL -SessionOption (New-PSSessionOption -SkipCACheck -SkipCNCheck) -Authentication $auth
        }                
        
        return $session 
    }
    Catch 
    {
        $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
        Write-Error -Message $ExceptionMessage
    }
}