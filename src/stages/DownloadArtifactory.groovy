package stages

import frmwork.StagesTypes
/**
 * Fase que descarga los elementos de despliegue de artifactory
 * 
 * @author jpalm
 *
 */
class DownloadArtifactory extends Stage {

	
	DownloadArtifactory(Script scp) {
		super(scp)
		stageType = StagesTypes.Value.DEPLOY
	}
	
	void execute() {
		// En esta fase se localiza en el repositorio de despliegue
		// de artifactory por proeycto y version los zips que contienen
		// los artefactos a desplegar
		// la descarga es en directorio d
		// si no se descargan artefactos se producirá un error
		script.registry.init()

		if (script.fileExists('d')) {
			script.sh 'rm -rf d'
		}
		
		try {
			script.log.info( "Descarga Artifactory-> $script.ARTIFACTORY_REPO/$script.PRJ/$script.VERSION/")
			script.artifactory.downloadArtefacto("${script.ARTIFACTORY_REPO}", "${script.PRJ}", "${script.VERSION}", script)
		} catch (Exception e) {
			script.log.warning( e.getMessage())
		}
		
		if (script.BUILD == "true" && !script.fileExists("d/$script.PRJ/$script.VERSION")) {
			script.error("NO SE DESCARGARON ARTEFACTOS: d/$script.PRJ/$script.VERSION")
			return
		}
	}
}
