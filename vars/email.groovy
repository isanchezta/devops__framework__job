import frmwork.Credentials
//@Grapes(
//	@Grab(group='org.codehaus.groovy', module='groovy-xml', version='2.4.18')
//)

import groovy.util.XmlParser
import plugings.Smtp

/**
 * 
 * Envio de email via office 365 acciona
 * @param to		emails destino
 * @param from		cuenta origen
 * @param subject	asunto del correo
 * @param text		texto del email
 * @param webhook 	URL para el envio del msg
 */
void sendEmailWebhook(String to, String from, String subject, String text, String webhook , Script script) {
	
	script.office365ConnectorSend message: text, status: subject, webhookUrl: webhook
}


/**
 * Funcion que envian las notificaciones a las cuentas definidas en office365.xml
 * Para el crear el conector acceda a la documentacion
 * 
 * https://wiki.jenkins.io/display/JENKINS/Office+365+Connector+Plugin
 * 
 * El fichero xml a generar tendra las siguiente sintaxis:
 * 
 * <accounts>
 *	<!--  Para enviar las notificaciones a los usuarios de la organizacion Acciona u a otras que est�n en office 365
 *	Se utiliza el pluging de conexion de JK a office 365 enviando mensajes
 *	Para crear estos webhook acceder a la guia: 
 *	 -->	
 *	<account>
 *		<username>cuenta_email</username>
 *		<webhook>url que crea office</webhook>
 *	</account>
 * </accounts>
 * @param fileConfig fichero office365.xml de configuracion 
 */
void sendEmails365(String fileConfig, String to, String from, String subject, String text, Script script) {
	script.log.info "sendEmail365 ${fileConfig}"
	String content = script.readFile "${fileConfig}"
	def xml = new XmlParser().parseText(content)
	for(def node in xml.account) {
		script.log.info "username:" + node.username.text()
		script.log.info "webhook:" + node.webhook.text()
		String hookUrl = node.webhook.text()
		sendEmailWebhook( to, from, subject, text, hookUrl, script)
	}
	script.log.info "XML:" +  content
}

/**
 * Envio de email via protoclo smtp
 * 
 * @param fileConfig
 * @param to
 * @param from
 * @param subject
 * @param text
 * @param script
 */
void sendEmails(String fileConfig, String to, String from, String jobname, String result,  String text, Script script, String summary, String summaryOks) {
	script.log.info "sendEmail ${fileConfig}"
	
	String content = script.readFile "${fileConfig}"
	def xml = new XmlParser().parseText(content)
	// leer fichero de notificaciones para obtener los datos del correo a enviar y formato
	def notfXmlFile = script.libraryResource 'notificaciones.xml'
	def notfXml = new XmlParser().parseText(notfXmlFile)
	script.log.info notfXmlFile
	String strText = notfXml.text.text()
	String strSubject = notfXml.subject.text()
	script.log.info strText
	script.log.info strSubject
	
	if(result.compareToIgnoreCase("SUCCESS")!=0) {
		// Para el caso que estemos ante un error del job siempre se notifica a la oficina devops

		String emailAddr = script.env.EMAIL_ERROR;
	
		String subjct = String.format(strSubject, jobname)
		String txt = String.format(strText, result, text,summary, summaryOks)
		sendMailBySmtp( from, subjct, emailAddr, txt, script)
	}
	
	for(def node in xml.account) {
		String emailAddr = node.email.text()
		String subjct = String.format(strSubject, jobname)
		String txt = String.format(strText, result, text,summary, summaryOks)
		script.log.info "DATOS CORREO"
		script.log.info emailAddr
		script.log.info from 
		script.log.info subjct
		script.log.info txt
		
		sendMailBySmtp( from, subjct, emailAddr, txt, script)
	}
	script.log.info "XML:" +  content
}

void sendMailBySmtp(String from, String subject, String to, String content,  Script script) {
	Smtp smtp = new plugings.Smtp(script: script)
	def smtpXmlFile = script.libraryResource 'smtp.xml'
	smtp.executeNotificacion(from, subject, to, content, smtpXmlFile)
}

/**
 * Utilidad deprecated se usa smtp
 * 
 * @param from
 * @param subject
 * @param to
 * @param content
 * @param smtpXmlFile
 * @param script
 */
void sendMailByMailx(String from, String subject, String to, String content, smtpXmlFile, Script script) {
	sendMailBySmtp( from,  subject,  to,  content,   script)
	/*
	def smtpXml = new XmlParser().parseText(smtpXmlFile)
	
	//
	String credential = smtpXml.credential.text()
	String host = smtpXml.host.text()
	String port = smtpXml.port.text()
	String auth = smtpXml.auth.text()
	String starttls= smtpXml.starttls.text()
	String nss_config_dir = smtpXml.nss_config_dir.text()
	String from2 = smtpXml.from.text()
	String ssl_verify = smtpXml.ssl_verify.text()
	
	def creds = new  Credentials()
	String username = creds.getUsername(credential)
	String password = creds.getPassword(credential)
	
	String command = """echo '${content}' | mailx -v -s '${subject}' -S smtp-auth=login -S smtp-use-starttls=${starttls} -S nss-config-dir=~//.certs -S smtp=smtp://${host}:${port} -S from=${from2}  -S smtp-auth-user=${username} -S smtp-auth-password=${password} -S ssl-verify=ignore ${to} """
		
	script.log.info command
	
	sh( returnStdout: true, script: command)
	*/
}
/**
 * Envio de un email por medio del smtp
 * @param to
 * @param from
 * @param subject
 * @param text
 * @param webhook
 * @param script
 */
void sendEmail(String to, String from, String subject, String text,  Script script) {
	sendMailBySmtp( from,  subject,  to,  text,   script)
}



return this;
