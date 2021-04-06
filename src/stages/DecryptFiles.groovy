package stages

import frmwork.StagesTypes

/**
 * Fase que ejecuta la desencriptacion de los ficheros dentro de los directorios de despliegue
 * 
 * @author jpalm
 *
 */
class DecryptFiles extends Stage{

	DecryptFiles(Script scp) {
		super(scp)
		stageType = StagesTypes.Value.DEPLOY
	}
	
	void execute() {
		script.log.info "Desecriptar Archivos en pipes/${script.DIR_ENTORNO}"
		def result = decryptFiles( "**/*.crypt" , "${script.CRYPT_CREDENTIAL_MANAGER}", "${script.CRYPT_CREDENTIAL_OFICINA}","pipes/${script.DIR_ENTORNO}" )
		if(result>0) {
			script.error( "Error en DESENCRIPTACION ARCHIVOS (${script.ENTORNO}) -> ${result}" )       
			// se produce error terminar la pipe de jenkins con estado de error
			return;			                               
		}   
	}
	
	/**
	 * Desencripta los ficheros con la extension .crypt del directorio indicado de forma recursiva
	 * @param dir			Directorio a partir del cual buscara todos los ficheros crypt para proceder a su desencriptacion
	 * @param credentialId	Credencial utilizada para desencriptar los archivos �
	 *
	 * @return				0 pudo desencriptara o bien el codigo de error
	 */
	public def decryptFiles(String pattern, String credentialIdManager, String credentialIdOficina, String directory) {
		script.log.info "DecryptFiles: " + pattern + " " + credentialIdManager +"/" + credentialIdOficina
		
		def cred = new frmwork.Credentials();
		String publicKey = cred.getText(credentialIdManager);
		String privateKey = cred.getText(credentialIdOficina);
	   
		def crypt = new frmwork.CryptoUtils();
		def res = 0
		if(credentialIdManager.length() ==0)
			return res;
		def scp = script
		script.dir (directory) {
			def rootDir = scp.pwd()
			scp.log.info "Directory: ${rootDir}/*.crypt"
		   
			def files = scp.findFiles(glob: pattern)
			scp.log.info "Listado de archivos: "  + files
			for( def f: files) {
				def filePath = "${rootDir}/${f.toString()}"
				def str = scp.readFile filePath
				def claro =  crypt.decrypt( str, publicKey, privateKey)
				
				if(claro != null ) {
					scp.writeFile file: filePath.substring(0, filePath.length() - 6 ), text: claro
					scp.log.info "Fichero desencriptado OK"
				}
				else {
					scp.log.err "Error al desencriptar archivo"
					res += 1 // error al desencriptar
				}
			}
		}
	   
		return res
	}
}
