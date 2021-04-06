package stages

import frmwork.Utils
import frmwork.StagesTypes

/**
 * Fase que ejecuta la operacion de mirror de un repostiorio de Azure Repo a GitHUB
 * 
 * @author jpalm
 *
 */
class Azure2GitHub extends Stage {
	Azure2GitHub(Script scp) {
		super(scp)
	}
	
	
	void execute() {
		if(script.REPLY_GITHUB == "true") {
			def utils = new frmwork.Utils()
			utils.gitAzure2GitHub(script.GIT_URL, script.GIT_CREDENTIALSID)          
		}   
	}
}
