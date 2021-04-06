/**
 * Proceso 1
 * Proceso que crea una sesion en la maquina remota con las credenciales indicadas y ejecuta el proces con los parametros
 * 
 * @param credentialId 	 Credencial en JK para que se ejecuten en las maquina remota
 * @param hostname	 	 Host de maquina remota
 * @param agentdirectory Directorio del workspace
 * @param artifact 	     Nombre del proyecto
 * @param fileZip        Nombre del archivo zip en artifactory
 * @param dest			 Ruta principal donde va a quedar el artefacto
 * @param backup		 Ruta del backup
 * @param appNamePool    Nombre del pool en el iis
 * @param auth			 Tipo de autenticacion para conectarse con el server
 * @param useSSLCet      Para uso de Certificado
 * @return
 */
int projectNetXML(String credentialId, String hostname, String agentdirectory, String artifact, String fileZip, String dest, String backupDir, String appNamePool, String auth, String useSSLCet)
{
	log.info("projectNet")
	
	Date date = new Date()
	String datePart = date.format("yyyyMMdd") // ampliar a hora minuto

    log.info("credentialId: " +  credentialId + " - hostname: " + hostname + " - agentdirectory: " + agentdirectory + " - auth: " + auth + " - useSSLCet: " + useSSLCet)
    log.info("artifact: " + artifact + " - fileZip: " + fileZip + " - dest: " + dest + " - backupDir: " + backupDir + " - appNamePool: " + appNamePool)
	
	def creds = new frmwork.Credentials()
	def username2 = creds.getUsername(credentialId)
	def password2 = creds.getPassword(credentialId)
    def version = agentdirectory.split('/')[-1]
    def backupServer = "${backupDir}/${datePart}"
	
    log.info("user: " + username2 + " - backupServer: " + backupServer + " - version: " + version)

    def status = 0

	withEnv(["USER=$username2", "PWD=$password2", "SERVER=$hostname", "ADirectory=$agentdirectory", "ART=$artifact", "FZIP=$fileZip", "PSERVER=$dest", "BSERVER=$backupServer", "VERS=$version", "NPOOL=$appNamePool", "AUTHE=$auth", "USSLCET=$useSSLCet"]) {
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

            $session = Invoke-Command -ScriptBlock ${function:Get-Credentials} -ArgumentList "$env:USER", "$env:PWD", $env:SERVER, $env:AUTHE, $env:USSLCET

            Write-Host "#############################################################################################################"

            Write-Host "Inicio de proceso de cierre de carpetas"

            #Invoke-Command -Session $session -ScriptBlock ${function:Get-ViewOpenDirectory}
            #Invoke-Command -Session $session -ScriptBlock ${function:Get-CloseDirectory}
            #Invoke-Command -Session $session -ScriptBlock ${function:Get-ViewOpenDirectory}

            Write-Host "Fin de proceso de cierre de carpetas"

            Write-Host "#############################################################################################################"

            Write-Host "Inicio de proceso de Backup"

            $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList "$env:PSERVER"

            if($value)
            {
                $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList "$env:BSERVER"

                if($value)
                {
                    $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList "$env:BSERVER/Backup.zip"

                    if($value)
                    {
                        Write-Host "1.1"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteDirectory} -ArgumentList "$env:BSERVER/Backup.zip"
                        Write-Host "1.2"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-ZipDirectory} -ArgumentList "$env:PSERVER", "$env:BSERVER/Backup.zip"
                    }
                    else
                    {
                        Write-Host "1.3"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-ZipDirectory} -ArgumentList "$env:PSERVER", "$env:BSERVER/Backup.zip"
                    }            
                }
                else
                {
                    Write-Host "2.1"
                    Invoke-Command -Session $session -ScriptBlock ${function:Get-CreateDirectory} -ArgumentList "$env:BSERVER"
                    Write-Host "2.2"
                    Write-Host "Backup ruta $env:PSERVER en $env:BSERVER/Backup.zip"
                    Invoke-Command -Session $session -ScriptBlock ${function:Get-ZipDirectory} -ArgumentList "$env:PSERVER", "$env:BSERVER/Backup.zip"
                }
            }
            else
            {
                Write-Host "No existe directorio para hacer Backup"
            }        

            Write-Host "Fin de proceso de Backup"

            Write-Host "#############################################################################################################"

            Write-Host "Inicio de proceso de Site"

            Write-Host "Validacion de directorio de site $env:PSERVER"
            $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList "$env:PSERVER" 
            
            if($value)
            {       
                Write-Host "1.1"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-RecycleAppPool} -ArgumentList "$env:NPOOL", Stopped
                Write-Host "1.2"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteFileDirectory} -ArgumentList "$env:PSERVER"
            }
            else
            {
                Write-Host "2"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-CreateDirectory} -ArgumentList "$env:PSERVER"
            }

            Write-Host "#############################################################################################################"

            Write-Host "Inicio de proceso de decarga de artefactos"
            
            $guidName = Invoke-Command -Session $session -ScriptBlock ${function:Get-Guid}
            $directorytemp = "C:/AzureTemp/$guidName"

            Write-Host "Validacion de directorio $directorytemp"    
            $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList $directorytemp

            if($value)
            {  
                Write-Host "1"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteFileDirectory} -ArgumentList $directorytemp               
            }
            else
            {
                Write-Host "2"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-CreateDirectory} -ArgumentList $directorytemp
            }            

            Write-Host "3"
            Write-Host "Descarga de artefactos de $env:ADirectory a $directorytemp"
            Copy-Item -Path "$env:ADirectory" -Destination "$directorytemp" -Recurse -Force -ToSession $session

            Write-Host "4"
            Write-Host "Unzip de archivo $directorytemp/$env:VERS/$env:FZIP"            
            Invoke-Command -Session $session -ScriptBlock ${function:Get-UpZipDirectory} -ArgumentList "$directorytemp/$env:VERS/$env:FZIP", "$directorytemp"

            Write-Host "5"
            Write-Host "Obtener ruta absoluta de PackageTmp"
            $source = Invoke-Command -Session $session -ScriptBlock ${function:Get-ModifyXML} -ArgumentList "$directorytemp", "archive.xml"

            Write-Host "6"
            Write-Host "Moviendo archivos de $source a $env:PSERVER"
            Invoke-Command -Session $session -ScriptBlock ${function:Get-MoveFileDirectory} -ArgumentList "$source", "$env:PSERVER"
            
            Write-Host "7"
            Write-Host "Borrado de directorio $directorytemp"
            $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteDirectory} -ArgumentList $directorytemp

            Write-Host "Fin de proceso de decarga de artefactos"

            Write-Host "#############################################################################################################"

            Write-Host "Validacion de appPool de app"
            Invoke-Command -Session $session -ScriptBlock ${function:Get-RecycleAppPool} -ArgumentList "$env:NPOOL", Started

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
 * Proceso 2
 * Proceso que crea una sesion en la maquina remota con las credenciales indicadas y ejecuta el proces con los parametros
 * 
 * @param credentialId 	 Credencial en JK para que se ejecuten en las maquina remota
 * @param hostname	 	 Host de maquina remota
 * @param agentdirectory Directorio del workspace
 * @param artifact 	     Nombre del proyecto
 * @param fileZip        Nombre del archivo zip en artifactory
 * @param dest			 Ruta principal donde va a quedar el artefacto
 * @param backup		 Ruta del backup
 * @param appNamePool    Nombre del pool en el iis
 * @param auth			 Tipo de autenticacion para conectarse con el server
 * @param useSSLCet      Para uso de Certificado
 * @return
 */
int projectNetFiles(String credentialId, String hostname, String agentdirectory, String artifact, String fileZip, String dest, String backupDir, String appNamePool, String auth, String useSSLCet)
{
	log.info("projectNetFiles")
	
	Date date = new Date()
	String datePart = date.format("yyyyMMdd")

    log.info("credentialId: " +  credentialId + " - hostname: " + hostname + " - agentdirectory: " + agentdirectory + " - auth: " + auth + " - useSSLCet: " + useSSLCet)
    log.info("artifact: " + artifact + " - fileZip: " + fileZip + " - dest: " + dest + " - backupDir: " + backupDir + " - appNamePool: " + appNamePool)
	
	def creds = new frmwork.Credentials()
	def username2 = creds.getUsername(credentialId)
	def password2 = creds.getPassword(credentialId)
    def version = agentdirectory.split('/')[-1]
    def backupServer = "${backupDir}/${datePart}"
	
    log.info("user: " + username2 + " - backupServer: " + backupServer + " - version: " + version)

    def status = 0

	withEnv(["USER=$username2", "PWD=$password2", "SERVER=$hostname", "ADirectory=$agentdirectory", "ART=$artifact", "FZIP=$fileZip", "PSERVER=$dest", "BSERVER=$backupServer", "VERS=$version", "NPOOL=$appNamePool", "AUTHE=$auth", "USSLCET=$useSSLCet"]) {
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

            $session = Invoke-Command -ScriptBlock ${function:Get-Credentials} -ArgumentList "$env:USER", "$env:PWD", $env:SERVER, $env:AUTHE, $env:USSLCET

            Write-Host "#############################################################################################################"

            Write-Host "Inicio de proceso de cierre de carpetas"

            #Invoke-Command -Session $session -ScriptBlock ${function:Get-ViewOpenDirectory}
            #Invoke-Command -Session $session -ScriptBlock ${function:Get-CloseDirectory}
            #Invoke-Command -Session $session -ScriptBlock ${function:Get-ViewOpenDirectory}

            Write-Host "Fin de proceso de cierre de carpetas"

            Write-Host "#############################################################################################################"

            Write-Host "Inicio de proceso de Backup"

            $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList "$env:PSERVER"

            if($value)
            {
                $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList "$env:BSERVER"

                if($value)
                {
                    $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList "$env:BSERVER/Backup.zip"

                    if($value)
                    {
                        Write-Host "1.1"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteDirectory} -ArgumentList "$env:BSERVER/Backup.zip"
                        Write-Host "1.2"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-ZipDirectory} -ArgumentList "$env:PSERVER", "$env:BSERVER/Backup.zip"
                    }
                    else
                    {
                        Write-Host "1.3"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-ZipDirectory} -ArgumentList "$env:PSERVER", "$env:BSERVER/Backup.zip"
                    }            
                }
                else
                {
                    Write-Host "2.1"
                    Invoke-Command -Session $session -ScriptBlock ${function:Get-CreateDirectory} -ArgumentList "$env:BSERVER"
                    Write-Host "2.2"
                    Invoke-Command -Session $session -ScriptBlock ${function:Get-ZipDirectory} -ArgumentList "$env:PSERVER", "$env:BSERVER/Backup.zip"
                }
            }
            else
            {
                Write-Host "No existe directorio para hacer Backup"
            }        

            Write-Host "Fin de proceso de Backup"

            Write-Host "#############################################################################################################"

            Write-Host "Inicio de proceso de Site"

            Write-Host "Validacion de directorio de site $env:PSERVER"
            $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList "$env:PSERVER" 
            
            if($value)
            {       
                Write-Host "1.1"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-RecycleAppPool} -ArgumentList "$env:NPOOL", Stopped
                Write-Host "1.2"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteFileDirectory} -ArgumentList "$env:PSERVER"
            }
            else
            {
                Write-Host "2"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-CreateDirectory} -ArgumentList "$env:PSERVER"
            }

            Write-Host "#############################################################################################################"

            Write-Host "Inicio de proceso de decarga de artefactos"         

            Write-Host "3"
            Write-Host "Descarga de artefactos de $env:ADirectory/$env:FZIP/* a $env:PSERVER"
            Copy-Item -Path "$env:ADirectory/$env:FZIP/*" -Destination "$env:PSERVER" -Verbose -Recurse -Force -ToSession $session

            Write-Host "Fin de proceso de decarga de artefactos"

            Write-Host "#############################################################################################################"

            Write-Host "Validacion de appPool de app"
            Invoke-Command -Session $session -ScriptBlock ${function:Get-RecycleAppPool} -ArgumentList "$env:NPOOL", Started

            Write-Host "#############################################################################################################"

            Remove-PSSession -Session $session

            Write-Host "Desconectando del servidor $env:SERVER"

            Write-Host "#############################################################################################################"

            Remove-Module -Name "deploymentPS"
                
            Write-Host "Modulos Removidos"

            Get-Module | Format-Table

            Write-Host "#############################################################################################################"
            
            Write-Host "#############################"
            Write-Host "## Fin de proceso global   ##"  
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

/** Corporativo **/

/**
 * Proceso 3
 * Proceso que crea una sesion en la maquina remota con las credenciales indicadas y ejecuta el proces con los parametros
 * 
 * @param credentialId 	 Credencial en JK para que se ejecuten en las maquina remota
 * @param hostname	 	 Host de maquina remota
 * @param agentdirectory Directorio del workspace
 * @param artifact 	     Nombre del proyecto
 * @param fileZip        Nombre del archivo zip en artifactory
 * @param dest			 Ruta principal donde va a quedar el artefacto
 * @param backup		 Ruta del backup
 * @param appNamePool    Nombre del pool en el iis
 * @param auth			 Tipo de autenticacion para conectarse con el server
 * @param useSSLCet      Para uso de Certificado
 * @return
 */
int projectNetFilesXMLCorp(String credentialId, String hostname, String agentdirectory, String artifact, String fileZip, String dest, String backupDir, String appNamePool, String auth, String useSSLCet)
{
	log.info("projectNetFilesXMLCorp")
	
	Date date = new Date()
	String datePart = date.format("yyyyMMdd")

    log.info("credentialId: " +  credentialId + " - hostname: " + hostname + " - agentdirectory: " + agentdirectory + " - auth: " + auth + " - useSSLCet: " + useSSLCet)
    log.info("artifact: " + artifact + " - fileZip: " + fileZip + " - dest: " + dest + " - backupDir: " + backupDir + " - appNamePool: " + appNamePool)
	
	def creds = new frmwork.Credentials()
	def username2 = creds.getUsername(credentialId)
	def password2 = creds.getPassword(credentialId)
    def version = agentdirectory.split('/')[-1]
    def backupServer = "${backupDir}/${datePart}"
	
    log.info("user: " + username2 + " - backupServer: " + backupServer + " - version: " + version)

    def status = 0

	withEnv(["USER=$username2", "PWD=$password2", "SERVER=$hostname", "ADirectory=$agentdirectory", "ART=$artifact", "FZIP=$fileZip", "PSERVER=$dest", "BSERVER=$backupServer", "VERS=$version", "NPOOL=$appNamePool", "AUTHE=$auth", "USSLCET=$useSSLCet"]) {
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

            $session = Invoke-Command -ScriptBlock ${function:Get-Credentials} -ArgumentList "$env:USER", "$env:PWD", $env:SERVER, $env:AUTHE, $env:USSLCET

            Write-Host "#############################################################################################################"

            Write-Host "Inicio de proceso de cierre de carpetas"

            #Invoke-Command -Session $session -ScriptBlock ${function:Get-ViewOpenDirectory}
            #Invoke-Command -Session $session -ScriptBlock ${function:Get-CloseDirectory}
            #Invoke-Command -Session $session -ScriptBlock ${function:Get-ViewOpenDirectory}

            Write-Host "Fin de proceso de cierre de carpetas"

            Write-Host "#############################################################################################################"

            Write-Host "Inicio de proceso de Backup"

            $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList "$env:PSERVER"
            $directorytemp = Invoke-Command -Session $session -ScriptBlock ${function:Get-NewTemporaryDirectory}
            $include = @("*.txt","Ficheros Eurest","Logs","SAP_Emulator")
            $exclude = @("*.txt","*.xml","*.config","Ficheros Eurest","Logs","SAP_Emulator","AppConfiguration.xml","*.json")

            if($value)
            {
                $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList "$env:BSERVER"

                if($value)
                {
                    $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList "$env:BSERVER/Backup.zip"

                    if($value)
                    {
                        Write-Host "1.1"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteDirectory} -ArgumentList "$env:BSERVER/Backup.zip"
                        Write-Host "1.2"
                        Write-Host "Moviendo archivos a directorio $directorytemp"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-MoveFileDirectory} -ArgumentList "$env:PSERVER", "$directorytemp"

                        Write-Host "Borrado de archivos"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteIncludeFileDirectory} -ArgumentList "$directorytemp", $include

                        Invoke-Command -Session $session -ScriptBlock ${function:Get-ZipDirectory} -ArgumentList "$directorytemp", "$env:BSERVER/Backup.zip"

                        Write-Host "Borrado de directorio $directorytemp"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteDirectory} -ArgumentList $directorytemp
                    }
                    else
                    {
                        Write-Host "1.3"
                        Write-Host "Moviendo archivos a directorio $directorytemp"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-MoveFileDirectory} -ArgumentList "$env:PSERVER", "$directorytemp"

                        Write-Host "Borrado de archivos"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteIncludeFileDirectory} -ArgumentList "$directorytemp", $include

                        Invoke-Command -Session $session -ScriptBlock ${function:Get-ZipDirectory} -ArgumentList "$directorytemp", "$env:BSERVER/Backup.zip"

                        Write-Host "Borrado de directorio $directorytemp"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteDirectory} -ArgumentList $directorytemp
                    }            
                }
                else
                {
                    Write-Host "2.1"
                    Invoke-Command -Session $session -ScriptBlock ${function:Get-CreateDirectory} -ArgumentList "$env:BSERVER"
                    Write-Host "2.2"
                    Write-Host "Moviendo archivos a directorio $directorytemp"
                    Invoke-Command -Session $session -ScriptBlock ${function:Get-MoveFileDirectory} -ArgumentList "$env:PSERVER", "$directorytemp"

                    Write-Host "Borrado de archivos"
                    Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteIncludeFileDirectory} -ArgumentList "$directorytemp", $include

                    Invoke-Command -Session $session -ScriptBlock ${function:Get-ZipDirectory} -ArgumentList "$directorytemp", "$env:BSERVER/Backup.zip"

                    Write-Host "Borrado de directorio $directorytemp"
                    Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteDirectory} -ArgumentList $directorytemp
                }
            }
            else
            {
                Write-Host "No existe directorio para hacer Backup"
            }        

            Write-Host "Fin de proceso de Backup"

            Write-Host "#############################################################################################################"

            Write-Host "Inicio de proceso de Site"

            Write-Host "Validacion de directorio de site $env:PSERVER"
            $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList "$env:PSERVER" 
            
            if($value)
            {       
                Write-Host "1.1"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-RecycleAppPool} -ArgumentList "$env:NPOOL", Stopped
                Write-Host "1.2"
                #Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteFileDirectory} -ArgumentList "$env:PSERVER"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteExcludeFileDirectory} -ArgumentList "$env:PSERVER", $exclude
            }
            else
            {
                Write-Host "2"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-CreateDirectory} -ArgumentList "$env:PSERVER"
            }

            Write-Host "#############################################################################################################"

            Write-Host "Inicio de proceso de decarga de artefactos"   

            $guidName = Invoke-Command -Session $session -ScriptBlock ${function:Get-Guid}
            $directorytemp = "C:/AzureTemp/$guidName"

            Write-Host "Validacion de directorio $directorytemp"
            $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList $directorytemp

            if($value)
            {
                Write-Host "1"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteFileDirectory} -ArgumentList $directorytemp
            }
            else
            {
                Write-Host "2"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-CreateDirectory} -ArgumentList $directorytemp
            }

            Write-Host "3"
            Write-Host "Descarga de artefactos de $env:ADirectory a $directorytemp"
            Copy-Item -Path "$env:ADirectory" -Destination "$directorytemp" -Recurse -Force -ToSession $session

            Write-Host "4"
            Write-Host "Unzip de archivo $directorytemp/$env:VERS/$env:FZIP"
            Invoke-Command -Session $session -ScriptBlock ${function:Get-UpZipDirectory} -ArgumentList "$directorytemp/$env:VERS/$env:FZIP", "$directorytemp"

            Write-Host "5"
            Write-Host "Obtener ruta absoluta de PackageTmp"
            $source = Invoke-Command -Session $session -ScriptBlock ${function:Get-ModifyXML} -ArgumentList "$directorytemp", "archive.xml"

            Write-Host "6"
            $excludeFile=@("*.config","appsettings*.json")
            Write-Host "Moviendo archivos de $source a $env:PSERVER ($exclude_)"            
            Invoke-Command -Session $session -ScriptBlock ${function:Get-MoveFileDirectoryExclude} -ArgumentList "$source", "$env:PSERVER", $excludeFile

            Write-Host "7"
            Write-Host "Borrado de directorio $directorytemp"
            $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteDirectory} -ArgumentList $directorytemp

            Write-Host "Fin de proceso de decarga de artefactos"

            Write-Host "#############################################################################################################"

            Write-Host "Validacion de appPool de app"
            Invoke-Command -Session $session -ScriptBlock ${function:Get-RecycleAppPool} -ArgumentList "$env:NPOOL", Started

            Write-Host "#############################################################################################################"

            Remove-PSSession -Session $session

            Write-Host "Desconectando del servidor $env:SERVER"

            Write-Host "#############################################################################################################"

            Remove-Module -Name "deploymentPS"
                
            Write-Host "Modulos Removidos"

            Get-Module | Format-Table

            Write-Host "#############################################################################################################"
            
            Write-Host "#############################"
            Write-Host "## Fin de proceso global   ##"  
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
 * Proceso 4
 * Proceso que crea una sesion en la maquina remota con las credenciales indicadas y ejecuta el proces con los parametros
 * 
 * @param credentialId 	 Credencial en JK para que se ejecuten en las maquina remota
 * @param hostname	 	 Host de maquina remota
 * @param agentdirectory Directorio del workspace
 * @param artifact 	     Nombre del proyecto
 * @param fileZip        Nombre del archivo zip en artifactory
 * @param dest			 Ruta principal donde va a quedar el artefacto
 * @param backup		 Ruta del backup
 * @param appNamePool    Nombre del pool en el iis
 * @param auth			 Tipo de autenticacion para conectarse con el server
 * @param useSSLCet      Para uso de Certificado
 * @return
 */
int projectNetFilesCorp(String credentialId, String hostname, String agentdirectory, String artifact, String fileZip, String dest, String backupDir, String appNamePool, String auth, String useSSLCet)
{
	log.info("projectNetFilesCorp")
	
	Date date = new Date()
	String datePart = date.format("yyyyMMdd")

    log.info("credentialId: " +  credentialId + " - hostname: " + hostname + " - agentdirectory: " + agentdirectory + " - auth: " + auth + " - useSSLCet: " + useSSLCet)
    log.info("artifact: " + artifact + " - fileZip: " + fileZip + " - dest: " + dest + " - backupDir: " + backupDir + " - appNamePool: " + appNamePool)
	
	def creds = new frmwork.Credentials()
	def username2 = creds.getUsername(credentialId)
	def password2 = creds.getPassword(credentialId)
    def version = agentdirectory.split('/')[-1]
    def backupServer = "${backupDir}/${datePart}"
	
    log.info("user: " + username2 + " - backupServer: " + backupServer + " - version: " + version)

    def status = 0

	withEnv(["USER=$username2", "PWD=$password2", "SERVER=$hostname", "ADirectory=$agentdirectory", "ART=$artifact", "FZIP=$fileZip", "PSERVER=$dest", "BSERVER=$backupServer", "VERS=$version", "NPOOL=$appNamePool", "AUTHE=$auth", "USSLCET=$useSSLCet"]) {
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

            $session = Invoke-Command -ScriptBlock ${function:Get-Credentials} -ArgumentList "$env:USER", "$env:PWD", $env:SERVER, $env:AUTHE, $env:USSLCET

            Write-Host "#############################################################################################################"

            Write-Host "Inicio de proceso de cierre de carpetas"

            #Invoke-Command -Session $session -ScriptBlock ${function:Get-ViewOpenDirectory}
            #Invoke-Command -Session $session -ScriptBlock ${function:Get-CloseDirectory}
            #Invoke-Command -Session $session -ScriptBlock ${function:Get-ViewOpenDirectory}

            Write-Host "Fin de proceso de cierre de carpetas"

            Write-Host "#############################################################################################################"

            Write-Host "Inicio de proceso de Backup"

            $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList "$env:PSERVER"
            $directorytemp = Invoke-Command -Session $session -ScriptBlock ${function:Get-NewTemporaryDirectory}
            $include = @("*.txt","Ficheros Eurest","Logs","SAP_Emulator")
            $exclude = @("*.txt","*.xml","*.config","Ficheros Eurest","Logs","SAP_Emulator","AppConfiguration.xml","*.json")

            if($value)
            {
                $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList "$env:BSERVER"

                if($value)
                {
                    $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList "$env:BSERVER/Backup.zip"

                    if($value)
                    {
                        Write-Host "1.1"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteDirectory} -ArgumentList "$env:BSERVER/Backup.zip"
                        Write-Host "1.2"
                        Write-Host "Moviendo archivos a directorio $directorytemp"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-MoveFileDirectory} -ArgumentList "$env:PSERVER", "$directorytemp"

                        Write-Host "Borrado de archivos"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteIncludeFileDirectory} -ArgumentList "$directorytemp", $include

                        Invoke-Command -Session $session -ScriptBlock ${function:Get-ZipDirectory} -ArgumentList "$directorytemp", "$env:BSERVER/Backup.zip"

                        Write-Host "Borrado de directorio $directorytemp"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteDirectory} -ArgumentList $directorytemp
                    }
                    else
                    {
                        Write-Host "1.3"
                        Write-Host "Moviendo archivos a directorio $directorytemp"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-MoveFileDirectory} -ArgumentList "$env:PSERVER", "$directorytemp"

                        Write-Host "Borrado de archivos"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteIncludeFileDirectory} -ArgumentList "$directorytemp", $include

                        Invoke-Command -Session $session -ScriptBlock ${function:Get-ZipDirectory} -ArgumentList "$directorytemp", "$env:BSERVER/Backup.zip"

                        Write-Host "Borrado de directorio $directorytemp"
                        Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteDirectory} -ArgumentList $directorytemp
                    }            
                }
                else
                {
                    Write-Host "2.1"
                    Invoke-Command -Session $session -ScriptBlock ${function:Get-CreateDirectory} -ArgumentList "$env:BSERVER"
                    Write-Host "2.2"
                    Write-Host "Moviendo archivos a directorio $directorytemp"
                    Invoke-Command -Session $session -ScriptBlock ${function:Get-MoveFileDirectory} -ArgumentList "$env:PSERVER", "$directorytemp"

                    Write-Host "Borrado de archivos"
                    Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteIncludeFileDirectory} -ArgumentList "$directorytemp", $include

                    Invoke-Command -Session $session -ScriptBlock ${function:Get-ZipDirectory} -ArgumentList "$directorytemp", "$env:BSERVER/Backup.zip"

                    Write-Host "Borrado de directorio $directorytemp"
                    Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteDirectory} -ArgumentList $directorytemp
                }
            }
            else
            {
                Write-Host "No existe directorio para hacer Backup"
            }        

            Write-Host "Fin de proceso de Backup"

            Write-Host "#############################################################################################################"

            Write-Host "Inicio de proceso de Site"

            Write-Host "Validacion de directorio de site $env:PSERVER"
            $value = Invoke-Command -Session $session -ScriptBlock ${function:Get-ValidateDirectory} -ArgumentList "$env:PSERVER" 
            
            if($value)
            {       
                Write-Host "1.1"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-RecycleAppPool} -ArgumentList "$env:NPOOL", Stopped
                Write-Host "1.2"
                #Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteFileDirectory} -ArgumentList "$env:PSERVER"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-DeleteExcludeFileDirectory} -ArgumentList "$env:PSERVER", $exclude
            }
            else
            {
                Write-Host "2"
                Invoke-Command -Session $session -ScriptBlock ${function:Get-CreateDirectory} -ArgumentList "$env:PSERVER"
            }

            Write-Host "#############################################################################################################"

            Write-Host "Inicio de proceso de decarga de artefactos"         

            Write-Host "3"
            $excludeFile=@("*.config","appsettings*.json")
            Write-Host "Descarga de artefactos de $env:ADirectory/$env:FZIP/* a $env:PSERVER"
            Copy-Item -Path "$env:ADirectory/$env:FZIP/*" -Destination "$env:PSERVER" -Exclude $excludeFile -Verbose -Recurse -Force -ToSession $session

            Write-Host "Fin de proceso de decarga de artefactos"

            Write-Host "#############################################################################################################"

            Write-Host "Validacion de appPool de app"
            Invoke-Command -Session $session -ScriptBlock ${function:Get-RecycleAppPool} -ArgumentList "$env:NPOOL", Started

            Write-Host "#############################################################################################################"

            Remove-PSSession -Session $session

            Write-Host "Desconectando del servidor $env:SERVER"

            Write-Host "#############################################################################################################"

            Remove-Module -Name "deploymentPS"
                
            Write-Host "Modulos Removidos"

            Get-Module | Format-Table

            Write-Host "#############################################################################################################"
            
            Write-Host "#############################"
            Write-Host "## Fin de proceso global   ##"  
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