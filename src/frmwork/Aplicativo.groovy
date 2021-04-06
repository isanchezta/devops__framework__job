/**
 *
 */
package frmwork

//@Grapes(
//	@Grab(group='com.cloudbees', module='groovy-cps', version='1.31')
//)

import com.cloudbees.groovy.cps.NonCPS

import frmwork.StatusTypes
import groovy.util.logging.Slf4j


/**
 * Representacion del aplicativo a desplegar
 * 
 * Un aplicativo tiene una version, y un estado
 * @author javier
 *
 */
public class Aplicativo {
	// Variables de entorno y de version
	String version
	def state // StatusTypes.Value state
	String name
	
	/**
	 * Se toma la salida del git describe, y se localiza el - como separador
	 * El primer campo es la version en formato X.Y.Z 
	 * El segundo campo es el estado
	 * 	KO -> Kick Off
	 * 	CC -> Code complete
	 * 	RC -> Release Candidate
	 * 	RFS -> Ready form service
	 * 
	 * Si viene una versi�n sin estado , se entiende que es la version definitiva 
	 * y que se ha desplegado en explotacion
	 * 
	 * @param val Se le indica la cadena que se obtiene del git describe X.Y.Z-NN
	 */
	public void setGitDescribe(String val) {
		String[] res = val.split("-")
		version = res[0]
		if(res.size()==1)
			state = StatusTypes.Value.PRO
		else if(res[1].toUpperCase().contains("SNAPSHOT"))
			state = StatusTypes.Value.SNAPSHOT
		else if(res[1].toUpperCase().contains("KO"))
			state = StatusTypes.Value.KO
		else if(res[1].toUpperCase().contains("CC"))
			state = StatusTypes.Value.CC
		else if(res[1].toUpperCase().contains("RC"))
			state = StatusTypes.Value.RC
		else if(res[1].toUpperCase().contains("RFS"))
			state = StatusTypes.Value.RFS
		else if(res[1].toUpperCase().contains("RT"))
			state = StatusTypes.Value.RT
		else state =StatusTypes.Value.ERROR
	}
	
	@NonCPS
	@Override
	public String toString() {
		return getName() +":" + getVersion() + "/" + getState() 
	}
}
