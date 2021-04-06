package stages

import frmwork.StagesTypes

/**
 * Fase que seleccciona el entorno de despliegue
 * 
 * @author jpalm
 *
 */
class EntornoDeploy extends Stage {
	
	EntornoDeploy(Script scp) {
		super(scp)
	}
	
	void execute() {
		// Se invoca a un unico script por cada fase dentro de las clases stgFw
		script.DEPLOY = new frmwork.AccionaFwDevOps(aplication: script.APP , branch: script.BRANCH)
		script.ENTORNO = script.DEPLOY.queryEnvironmentDeploy().toString()
		script.ENVTYPE = new frmwork.EnvironmentsTypes()
		script.log.info "ENTORNO: " + script.ENTORNO
		
		def st = new StgFw(script)
		script.DIR_ENTORNO = st.queryEnvironmentDeploy(script.APP, script.ENVTYPE, script.DEPLOY)
	}
	
}
