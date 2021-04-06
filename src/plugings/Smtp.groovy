package plugings

// https://mvnrepository.com/artifact/javax.mail/mail
import frmwork.Credentials
//@Grapes([
//    @Grab(group='javax.mail', module='mail', version='1.4.7'),
//	@Grab(group='com.cloudbees', module='groovy-cps', version='1.31'),
//	@Grab(group='org.codehaus.groovy', module='groovy-xml', version='2.4.18')
//	])

import com.cloudbees.groovy.cps.NonCPS
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


class Smtp {

	String credential;
	String host;
	String port;
	String auth;
	String starttls;
	String nss_config_dir;
	String from;
	String ssl_verify;
	String password ;
	String username;
	String email;
	def script
	 
	public executeNotificacion(String addresser, String subject, String to, String content,  String smtpXmlFile) {
		// leer smtp.xml de los recursos para obtener los datos de conexion
		
		def smtpXml = new XmlParser().parseText(smtpXmlFile)
		
		//
		credential = smtpXml.credential.text()
		host = smtpXml.host.text()
		port = smtpXml.port.text()
		auth = smtpXml.auth.text()
		starttls= smtpXml.starttls.text()
		nss_config_dir = smtpXml.nss_config_dir.text()
		from = smtpXml.from.text()
		ssl_verify = smtpXml.ssl_verify.text()
		
		def creds = new  Credentials()
		username = creds.getUsername(credential)
		password = creds.getPassword(credential)
		email = username;
		
		script.log.info( from + "-" +   subject + "-" +  to + "-" +  content + "-" )
		send( from,  subject,  to,    content)
	}
	
	private class SMTPAuthenticator extends javax.mail.Authenticator {
		String user
		String passwd
		@NonCPS
		public PasswordAuthentication getPasswordAuthentication() {
		   
		   return new PasswordAuthentication(user, passwd);
		}
	}
/**
 * Send mail via SSL/TLS enabled SMTP server.
 * @param addresser Could be different with sender email.
 * @param password  Password of sender email.
 * @param subject   Subject of mail.
 * @param to        Addressee.
 * @param cc        Carbon copy.
 * @param bcc       Blind carbon copy.
 * @param content   Content of mail.
 * @Param mimeType  MIME type of {@param content}， e.g. -> 'text/plain', 'text/html;charset=utf-8'
 * @return
 */
public void send(String addresser, String subject, String to, String content ) {
	script.log.info("smtp.send")
	
	  Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", port.toInteger());
        props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");
		

		Authenticator auth = new SMTPAuthenticator(user: username, passwd: password);
        Session session = Session.getInstance(props, auth) 


        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO,
                    new InternetAddress(to));
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setText(content);
            Transport.send(msg);

        } catch (MessagingException e) {
            script.log.info e.getCause().getMessage()
        }

}
}
