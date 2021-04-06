package frmwork

import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

/***
 * Devuelve las credenciales de acceso 
 */
class Credentials {
	
	def jenkinsCredentials
	
	/**
	 * Construye el objeto dando de alta las credenciales
	 */
	Credentials(){
		jenkinsCredentials = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
			com.cloudbees.plugins.credentials.Credentials.class,
			Jenkins.instance,
			null,
			null
		);
	}
	
	/**
	 * Devuelve el usuario de la credencial
	 * @param cred
	 * @return
	 */
	String getUsername(String cred) {
		
		
		for (creds in jenkinsCredentials) {
			println(creds.id)
			if(creds.id == cred){
			  return creds.username
			  }
		  }
		  
		return ""
	}
	
	/**
	 * Devuelve la contrasena de la credencial
	 * @param cred
	 * @return
	 */
	String getPassword(String cred) {
		for (creds in jenkinsCredentials) {
			if(creds.id == cred){
			  return creds.password
			  }
		  }
		  
		return ""
	}
	
	/**
	 * Deveulve el texto asociado a la credencial tipo 'Secret text'
	 * @param cred
	 * @return
	 */
	String getText(String cred	) {
		System.out.println("Credentials.getText:" + cred);
		
		for (creds in jenkinsCredentials) {
			if(creds.id == cred){
			  return creds.secret
			  }
		  }
		  return ""
	}
}
