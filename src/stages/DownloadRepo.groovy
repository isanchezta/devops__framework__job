package stages


import frmwork.StagesTypes

/**
 * Descarga el repositorio inicial
 * 
 * @author jpalm
 *
 */
class DownloadRepo extends Stage{

	
	DownloadRepo(Script scp) {
		super(scp)
		stageType = StagesTypes.Value.SCM
	}
	
	
	void execute() {
		// Se descarga el repositorio incluido los TAGS esto es muy importante
		// ya que por defecto no baja los TAGS
		// y como primer paso se borra el workspace
		script.sh("git config --global http.sslVerify false")
		script.checkout([
			$class: 'GitSCM',
			branches: [[name: master]], ,
			doGenerateSubmoduleConfigurations: script.scm.doGenerateSubmoduleConfigurations,
			extensions: [[$class: 'CloneOption', noTags: false, shallow: false, depth: 0, reference: '']],
			userRemoteConfigs: [[credentialsId: script.GIT_CREDENTIALSID, url: "https://github.com/isanchezta/devops__framework__job.git"]]
		])
	}
}
