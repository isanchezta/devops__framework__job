import model.FtpData
import plugings.PublishSettings

/**
 * Sube via FTP el contendio del directorio incluyendo las subcarpetas
 * 
 * Ejemplo del fichero de settings:
 * 
 * -u "ene-hsol-dev\\$ene-hsol-dev:xJagLo2TPvRxvCs472oDG4Jqz8apqL5lqho09cXWqelpSuXoTsnceENmjQHC"
 * --url ftp://waws-prod-db3-107.ftp.azurewebsites.windows.net/site/wwwroot/
 * 
 * u 	-> usuario:passwd
 * url	-> direcci�n del FTP y directorio destino
 * 
 * @param directory		Directorio para subir
 * @param fileSettings	Fichero con las propiedades de acceso al FTP
 */
void uploadDir(String directory, String fileSettings) {
	sh "find ${directory}/ -exec curl -T {} -K ${fileSettings} \\;"
}


/**
 * Ejecuta un FTp utilizando las credenciales tipo FTP del publishSettings de Azure
 * 
 * @param directory Directorio desde donde enviar los ficheros 
 * @param publishSettingsFile fichero en donde se indican las PublishSettings de Azure
 */
void uploadDirPublishSettings(String directory, String publishSettingsFile) {
	log.info "uploadDirPublishSettings ${directory}, ${publishSettingsFile}"
	PublishSettings ps = new PublishSettings(script: this)
	def content = readFile "${publishSettingsFile}"
	FtpData data = ps.GetFtpData(content)
	log.info "FTP URL -> ${data.url} user: ${data.username}"
	log.info "CREACION DIRECTORIOS"
	sh ("cd ${directory} ; find . -type d -exec curl  --ftp-create-dirs  -T {} --url ${data.url}/{}/ -u  ${data.username}:${data.passwd}  \\;")
	log.info "SUBIDA ARCHIVOS"
	sh ("cd ${directory} ; find . -type f -exec curl  -T {} --url ${data.url}/{} -u  ${data.username}:${data.passwd}  \\;")
}
return this

/**
 * Ejecuta un FTp utilizando las credenciales tipo FTP del publishSettings de Azure
 * 
 * @param file desde donde enviar los ficheros 
 * @param publishSettingsFile fichero en donde se indican las PublishSettings de Azure
 */
void uploadFilePublishSettings(String directory, String publishSettingsFile) {
	log.info "uploadFilePublishSettings ${directory}, ${publishSettingsFile}"
	PublishSettings ps = new PublishSettings(script: this)
	def content = readFile "${publishSettingsFile}"
	FtpData data = ps.GetFtpData(content)
	log.info "FTP URL -> ${data.url} user: ${data.username}"
	log.info "SUBIDA DE ARCHIVO"
	sh ("find ${directory} -type f -exec curl  -T {} --url ${data.url}/{} -u  ${data.username}:${data.passwd}  \\;")
}
return this