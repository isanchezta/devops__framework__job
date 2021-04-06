package frmwork

// https://mvnrepository.com/artifact/com.cloudbees/groovy-cps
//@Grapes(
//	@Grab(group='com.cloudbees', module='groovy-cps', version='1.31')
//)

import com.cloudbees.groovy.cps.NonCPS

import frmwork.StatusTypes
import frmwork.StatusTypes.Value
import frmwork.EnvironmentsTypes
import frmwork.Aplicativo

import groovy.util.logging.Slf4j


/**
 * Clase que gestiona el motor de despliegues
 * 
 * Necesita asociar la rama de trabajo y la aplicaci�n
 * Dentro de la aplicaci�n se tiene que registrar la versi�n y su estado
 * 
 * AccionaFwDevOps(aplication: app, branch: BRANCH)
 * 
 * @author javier
 *
 */
class AccionaFwDevOps {
	private String RAMA_DEVELOP = "DEVELOP"
	private String RAMA_RELEASE = "RELEASE"
	private String RAMA_MASTER 	= "MASTER"
	private String RAMA_HOTFIX 	= "HOTFIX"
	  
	EnvironmentsTypes.Value environment;
	Aplicativo aplication
	String branch;

	
	@NonCPS
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return aplication.toString()+ " branch: " + branch + " ("+ environment +")";
	}
	
	/**
	 * Dependiendo del estado del aplicativo integrado en el FW
	 * y de la rama de integracion se devolvera el entorno al cual desplegar
	 * 
	 * Las reglas son las definidas en el documento SIMPLE_GIT
	 * Se eval�a qeu cada etiqueta est� dado de alta en el commit integrante de la rama correcta
	 * 
	 * Adem�s de devolver el entorno, tambi�n actualiza el entorno de la propia clase getEnvironment
	 * 
	 * Etiquetas qeu se soportan:
	 * 
	 * x.y.z-KO	& developer -> DES
	 * x.y.z-KO	& hotfix    -> HOT 
	 * x.y.z-RC & release	-> PRE
	 * x.y.z-RFS & master	-> PRO
	 * 
	 * Si no se localiza algun entorno correcto se fija el entorno con ERROR
	 * 
	 * @return Retorna el entorno 
	 */
	public EnvironmentsTypes.Value queryEnvironmentDeploy() {
		
		setEnvironment ( EnvironmentsTypes.Value.ERROR )
		
		if(getAplication().getState() == StatusTypes.Value.KO && branch.toUpperCase().contains(RAMA_DEVELOP)) {
			setEnvironment ( EnvironmentsTypes.Value.DES )
		} else if(getAplication().getState() == StatusTypes.Value.RT && branch.toUpperCase().contains(RAMA_DEVELOP)) {
			setEnvironment ( EnvironmentsTypes.Value.INT )
		} else if(getAplication().getState() == StatusTypes.Value.RC && branch.toUpperCase().contains(RAMA_RELEASE)){
			setEnvironment ( EnvironmentsTypes.Value.PRE )
		} else if(getAplication().getState() == StatusTypes.Value.RFS && branch.toUpperCase().contains(RAMA_MASTER)){
			setEnvironment ( EnvironmentsTypes.Value.PRO )
		} else if(getAplication().getState() == StatusTypes.Value.KO && branch.toUpperCase().contains(RAMA_HOTFIX)) {
			setEnvironment ( EnvironmentsTypes.Value.HOT )
		} 
		
		return getEnvironment()
	}
	
	
}
