


// Funcion de arranque del proceso de despliegue de desarrollo
// devuelve 0 si OK  y <>0 si ERROR
def start(pathVersion , def pathConf="") {
	log.info "FASE DES: ${pathVersion} , ${pathConf}"
	
	def status = 0
	Date date = new Date()
	String datePart = date.format("yyyyMMddhhmmss") // ampliar a hora minuto
	
	def creds = new frmwork.Credentials()
	def username2 = creds.getUsername('CORP_NET_DES')
	def password2 = creds.getPassword('CORP_NET_DES')
	def source = "download/"
	def dest   = "\\\\svappcorpdes01\\E\$\\inetpub\\RevisionSalarialPrueba"
	def backupDir = "\\\\svappcorpdes01\\E\$\\backup\\RevisionSalarial"
	def deployDir = "\\\\svappcorpdes01\\E\$\\backup\\RevisionSalarial\\deploy"
	def source2 ="${pathVersion}/a/"
	def fileConfig="RevisionSalarial.Web.SetParameters.xml"
	def configFilePath = "${WORKSPACE}/${pathConf}/${fileConfig}"

	sh "ls -al ${pathVersion}"
	sh "ls -al ${source2}"
	

	
	// No copiar los ficehros con ext .config 
	withEnv(["USER=$username2", "PWD=$password2", "DEST=$dest","SRC=$source","SRC2=$source2", "FECHA=$datePart", "DIR_BACKUP=$backupDir", 
	"DIR_DEPLOY=$deployDir", "FILE_CONFIG_PATH=$configFilePath", "FILE_CONFIG=$fileConfig"]) {
	status = powershell(returnStatus: true, script: '''
		$p = ConvertTo-SecureString "$env:PWD" -AsPlainText -Force
		$mycreds = New-Object System.Management.Automation.PSCredential("$env:USER", $p)
		$dirDep = "$env:DIR_DEPLOY"
		$fecha = "$env:FECHA"
		
		New-PSDrive -Name J -PSProvider FileSystem -Root "$env:DEST" -Credential $mycreds 
		New-PSDrive -Name K -PSProvider FileSystem -Root "$env:DIR_BACKUP" -Credential $mycreds 
		New-PSDrive -Name P -PSProvider FileSystem -Root "$env:DIR_DEPLOY" -Credential $mycreds 
		New-Item -Path "k:\" -Name "$env:FECHA" -ItemType "directory" -Force
		New-Item -Path "p:\" -Name "$env:FECHA" -ItemType "directory" -Force

		Copy-item -Force -Recurse  -Path J:/ -Destination k:/"$env:FECHA"
		Copy-item -Force -Recurse  -Path "$env:SRC2" -Destination p:/"$env:FECHA"
		Copy-item -Force -Recurse  -Path "$env:FILE_CONFIG_PATH" -Destination p:/"$env:FECHA"/a
		
		echo "NUEVA SESION EN MAQUINA REMOTA"
		$s = New-PSSession -ComputerName svappcorpdes01.acciona.int -credential $mycreds -Authentication Kerberos

		Invoke-Command -Session $s -ScriptBlock { 
			echo "LLAMADA REMOTA:" $Using:dirDep $Using:fecha
			get-location
			Get-ChildItem
			e:
			cd backup\\RevisionSalarial\\deploy
			cd $Using:fecha
			cd a
			Get-ChildItem
			cmd /c "RevisionSalarial.Web.deploy.cmd /Y"
		}
	''' )
	}

	return status; 
}

return this;