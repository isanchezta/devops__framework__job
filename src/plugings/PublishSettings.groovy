package plugings

//@Grab(group='org.codehaus.groovy', module='groovy-xml', version='2.4.18')

import model.FtpData

/**
 * Clase que engloba las funcionalidades relativas a la gesion de los ficheros
 * app.PublishSettings de la plataforma de azure
 * @author Javier
 *
 */
class PublishSettings {
	
	Script script
	/**
	 * Funcion que devuelve las credencialas para el acceso a un servidor de FTP
	 * 
	 * @param xmlData
	 * @return
	 */
	public FtpData GetFtpData(def xmlFile ) {
		def xml = new XmlParser().parseText(xmlFile)
		
		FtpData ftpData = null
		
		for(def node in xml.publishProfile) {
			String method = node.@publishMethod
			if(method.compareToIgnoreCase("FTP")==0) {
				ftpData = new FtpData(url: node.@publishUrl, username: node.@userName, passwd: node.@userPWD, passive: node.@ftpPassiveMode )
				break;
			}
		}
		
		return ftpData
	}
}
