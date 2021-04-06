package stages

import frmwork.StagesTypes

/**
 * Fase que localiza la version del aplicativo a desplegar
 * @author jpalm
 *
 */
class VersionDeploy extends Stage {

	VersionDeploy(Script scp) {
		super(scp)
	}
	
	
	void execute() {
		// En esta fase se obtiene de la rama descargada el ultimo TAG generado
		// para con el obtener la version y el estado del proyecto
		script.APP = new frmwork.Aplicativo()
		def utils = new frmwork.Utils()
		
		script.registry.init()
		script.log.info "RAMA DESCARGADA: ${script.BRANCH}"
		def gitDescribe = utils.gitDescribe()
		script.log.info "GIT-TAG commit last: ${gitDescribe} "
		        	
		script.APP.setName(script.PRJ)
		script.log.info "Obtener version y estado"
		script.APP.setGitDescribe(gitDescribe)
		script.VERSION = script.APP.getVersion()
		script.ESTADO = script.APP.getState()

		//Se suma uno al cuarto dígito de la versión
		String[] str = script.VERSION.split("\\.")

		def digito = str [3]
		def temporal = Integer.parseInt(digito)
		temporal = temporal + 1
		digito = temporal + ""

		script.VERSION = str [0] + "." + str [1] + "." + str [2] + "." + digito

		        	
		script.log.info "v->${script.VERSION} e->${script.ESTADO} r->${script.BRANCH}"
		script.log.info "DESPLIEGUE DEL ARTEFACTO: v->${script.VERSION} e->${script.ESTADO}"
		//script.log.info "DESPLIEGUE DEL ARTEFACTO ${script.PRJ}: v->${script.VERSION} e->${script.ESTADO}"
	
	}
}
