/**
 * Proceso que crea una sesion en la maquina remota con las credenciales indicadas y ejecuta el proces con los parametros
 * 
 * @param credentialId 	 Credencial en JK para que se ejecuten en las maquina remota
 * @param hostname	 	 Host de maquina remota
 * @param wsp	 	     Nombre del proyecto
 * @param dest			 Ruta principal donde va a quedar el artefacto
 * @param agentdirectory Ruta del workspace
 * @param repository     Nombre del repositorio
 * @param auth			 Tipo de autenticacion para conectarse con el server
 * @return
 */
int projectSharePoint(String credentialId, String hostname, String wsp,  String dest, String agentdirectory, String repository, String auth)
{
	log.info("projectSharePoint")
	Date date = new Date()
	String datePart = date.format("yyyyMMdd")

	log.info "credentialId: " +  credentialId + " - hostname: " + hostname + " - wsp: " + wsp + " - dest: " + dest + " - agentdirectory: " + agentdirectory + " - auth: " + auth

	def creds = new frmwork.Credentials()
	def username2 = creds.getUsername(credentialId)
	def password2 = creds.getPassword(credentialId)
    def version = agentdirectory.split('/')[-1]
	def source = "${agentdirectory}"    
    def workspace = "${env.WORKSPACE}"
    def pathServer = "${dest}/${datePart}/${repository}"

    log.info "user: " + username2 + " - version: " + version + " - source: " + source + " - pathServer: " + pathServer + " - workspace: " + workspace
	
	def status = 0
    
	withEnv(["USER=$username2", "PWD=$password2", "SERVER=$hostname", "SRC=$source", "WSP=$wsp", "PSERVER=$pathServer", "AUTHE=$auth", "WSPACE=$workspace"]) {
	status = powershell(returnStatus: true, script: '''		
        Try
        {
            Write-Host "##############################"
            Write-Host "## Inicio de proceso global ##"
            Write-Host "##############################"
            Write-Host "Importando Modulos"
            
            Import-Module -Name "deploymentPS"
            Get-Module | Format-Table 
            Write-Host "Modulos Importados"
            Write-Host "#############################################################################################################"

            $session = Invoke-Command -ScriptBlock ${function:Get-Credentials} -ArgumentList "$env:USER", "$env:PWD", $env:SERVER, $env:AUTHE
            
            Write-Host "#############################################################################################################"

            [collections.generic.list[object]]$wsps = @()
            $wspSplits = $null        

            if((Select-String -Path "$env:SRC/proyectos_*.txt" -Pattern "Acciona.InterAcciona") -and !$env:WSP)
            {
                Write-Host "Archivo proyectos_*.txt encontrado"

                $file = Get-ChildItem -Path "$env:SRC" -Filter "proyectos_*.txt" | Where-Object {-not $_.PsIsContainer} | Sort-Object LastWriteTime -Descending | Select-Object -First 1

                if($file)
                {
                    Write-Host "Archivo a procesar: " $file

                    ForEach($line in [System.IO.File]::ReadLines("$env:SRC/$file"))
                    {
                        if($line -match "Acciona.InterAcciona")
                        {
                            $wsps.Add($line + ".wsp")
                        }                        
                    }
                    $wspSplits = $wsps
                    Write-Host "Se a encontrado un archivo con listado de aplicaciones a desplegar: $wspSplits"
                }                
            }
            else
            {
                Write-Host "Archivo proyectos_*.txt no encontrado, se tomara la variable del archivo start.groovy: $env:WSP"
                $wspSplits = $env:WSP.Split(",").Trim()
            }

            ForEach ($wspSplit in $wspSplits)
            {
                Write-Host "Artefacto a desplegar: $wspSplit"
                $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList "$env:PSERVER"
                if($value)
                {
                    Write-Host "The directory exist"
                }
                else
                {
                    Invoke-Command -Session $session -ScriptBlock ${function:Get-CreateDirectory} -ArgumentList "$env:PSERVER"
                    Invoke-Command -Session $session -ScriptBlock ${function:Get-CreateDirectory} -ArgumentList "$env:PSERVER/Backup"                
                }

                Write-Host "#############################################################################################################"

                Write-Host "Copy files $env:SRC/$wspSplit to $env:PSERVER/$wspSplit"
                Copy-Item -Path "$env:SRC/$wspSplit" -Destination "$env:PSERVER/$wspSplit" -Force -ToSession $session
                
                Write-Host "#############################################################################################################"

                Write-Host "Backup $wspSplit"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-SP-Backup} -ArgumentList "$wspSplit", "$env:PSERVER/Backup"
                
                Write-Host "#############################################################################################################"

                Write-Host "Update $wspSplit"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-SP-Update} -ArgumentList "$wspSplit", "$env:PSERVER/$wspSplit"

                Write-Host "#############################################################################################################"

                Write-Host "Wait For Job To Finish $wspSplit"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-SP-WaitForJobToFinish} -ArgumentList "$wspSplit"
            }            
            
            Write-Host "#############################################################################################################"

            Remove-PSSession -Session $session
            Write-Host "Desconectando del servidor $env:SERVER"

            Write-Host "#############################################################################################################"

            Remove-Module -Name "deploymentPS"
                
            Write-Host "Modulos Removidos"
            Get-Module | Format-Table

            Write-Host "#############################################################################################################"
            
            Write-Host "#############################"
            Write-Host "##  Fin de proceso global  ##"  
            Write-Host "#############################"
            exit 0
        }
        Catch 
        {
            $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
            Write-Error -Message $ExceptionMessage
            exit 55
            #Break
        }		
	''' )
	}

	return status; 
}

/**
 * Proceso que crea una sesion en la maquina remota con las credenciales indicadas y ejecuta el proces con los parametros
 * 
 * @param credentialId 	 Credencial en JK para que se ejecuten en las maquina remota
 * @param hostname	 	 Host de maquina remota
 * @param agentdirectory Directorio del workspace
 * @param artifact 	     Nombre del proyecto
 * @param dest			 Ruta principal donde va a quedar el artefacto
 * @param backup		 Ruta del backup
 * @param auth			 Tipo de autenticacion para conectarse con el server
 * @return
 */
int noprojectSharePoint(String credentialId, String hostname, String agentdirectory, String artifact, String dest, String backupDir, String auth)
{
	log.info("noprojectSharePoint")
	
	Date date = new Date()
	String datePart = date.format("yyyyMMdd") // ampliar a hora minuto

    log.info("credentialId: " +  credentialId + " - hostname: " + hostname + " - agentdirectory: " + agentdirectory)
    log.info("artifact: " + artifact + " - dest: " + dest + " - backupDir: " + backupDir)
	
	def creds = new frmwork.Credentials()
	def username2 = creds.getUsername(credentialId)
	def password2 = creds.getPassword(credentialId)
    //def dest = "${dest}/${artifact}"
    def backupServer = "${backupDir}/${datePart}"
	
    log.info("user: " + username2 + " - backupServer: " + backupServer)
	//log.info("user: " + username2 + " - dest: " + dest + " - backupServer: " + backupServer)

    def status = 0

	withEnv(["USER=$username2", "PWD=$password2", "SERVER=$hostname", "ADirectory=$agentdirectory", "ART=$artifact", "PSERVER=$dest", "BSERVER=$backupServer", "AUTHE=$auth"]) {
	status = powershell(returnStatus: true, script: '''
		Try 
		{
            Write-Host "##############################"
            Write-Host "## Inicio de proceso global ##"
            Write-Host "##############################"
            Write-Host "Importando Modulos"
            
            Import-Module -Name "deploymentPS"
            Get-Module | Format-Table 
            Write-Host "Modulos Importados"

            Write-Host "#############################################################################################################"

            $session = Invoke-Command -ScriptBlock ${function:Get-Credentials} -ArgumentList "$env:USER", "$env:PWD", $env:SERVER, $env:AUTHE

            Write-Host "#############################################################################################################"

            $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList "$env:PSERVER"
            if($value)
            {
                Write-Host "The $env:PSERVER directory exist"
                $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList "$env:BSERVER"
                if($value)
                {
                    Write-Host "The $env:BSERVER directory exist"
                }
                else
                {
                    Invoke-Command -Session $session -ScriptBlock ${function:Get-CreateDirectory} -ArgumentList "$env:BSERVER"
                }

                Write-Host "#############################################################################################################"

                Write-Host "Creating backup"

                Write-Host "Creacion de directorio temp"
                $directorytemp = Invoke-Command -Session $session -ScriptBlock ${function:Get-NewTemporaryDirectory}
                Write-Host "Moviendo archivos a directorio $directorytemp"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-MoveFileDirectory} -ArgumentList "$env:PSERVER", "$directorytemp"
                Write-Host "Borrado de archivos @(_logs,Logs,*.log)"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteIncludeFileDirectory} -ArgumentList "$directorytemp", @("_logs","Logs","*.log")
                
                Invoke-Command -Session $session -ScriptBlock ${function:Get-ZipDirectory} -ArgumentList "$directorytemp", "$env:BSERVER/Backup.zip"

                Write-Host "Borrado de directorio $directorytemp"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteDirectory} -ArgumentList $directorytemp

                Write-Host "#############################################################################################################"

                Write-Host "Deleted files"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteExcludeFileDirectory} -ArgumentList "$env:PSERVER", @("*.config","*.csv","*.log","*.txt","*.json","*.zip","_logs","Logs","jsonSampleFiles","assets","PS","versiones","versions","RJLFiles","_keep","DOC")

                Write-Host "#############################################################################################################"

                Write-Host "Copy files $env:ADirectory/* to $env:PSERVER"
                Copy-Item -Path "$env:ADirectory/*" -Destination "$env:PSERVER" -Exclude @("*.config","*.manifest") -Force -Recurse -ToSession $session            
            }
            else
            {
                Write-Host "The directory not exist!"
            }

            Write-Host "#############################################################################################################"

            Remove-PSSession -Session $session
            Write-Host "Desconectando del servidor $env:SERVER"

            Write-Host "#############################################################################################################"

            Remove-Module -Name "deploymentPS"
                
            Write-Host "Modulos Removidos"
            Get-Module | Format-Table

            Write-Host "#############################################################################################################"
            
            Write-Host "#############################"
            Write-Host "##  Fin de proceso global  ##"  
            Write-Host "#############################"
            exit 0
        }
        Catch 
        {
            $ExceptionMessage="Error in Line: " + $_.Exception.Line + ". " + $_.Exception.GetType().FullName + ": " + $_.Exception.Message + " Stacktrace: " + $_.Exception.StackTrace
            Write-Error -Message $ExceptionMessage
            exit 55
            #Break
        }
	''' )
	}

	return status; 
}

return this