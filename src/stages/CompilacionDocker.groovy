package stages

import frmwork.StagesTypes

/**
 * Fase que seleccciona el entorno de despliegue
 * 
 * @author jpalm
 *
 */
class CompilacionDocker extends Stage {
	
	CompilacionDocker(Script scp) {
		super(scp)
	}
	
	void execute() {
		// La fase de BUILD se lanzara si se ha soliciutado por paramtro la compialcion
		// y si esetamso en el entorno de DES
		// la carga del script de BUILD es dinamica
		// si se produce un error en al compilacion no se continua el job
		// compialr significa que se tiene que dejar en el directorio a
		// los artefactos que se quieran desplegar con zip y versionados
		script.registry.init()
		// Si la fase es DES es opcional invocar a la fase de BUILD
	
		String entorno = "${script.ENTORNO}"
		String dir_des = "${script.DIR_DES}"

		if(!entorno.equals(dir_des)){
			script.log.info "NO HAY QUE CONSTRUIR IMAGEN DOCKER, INDICADO POR PARAMETRO"
			return
		}else{

			def rootDir = script.pwd()
			script.log.info( 'INVOCAR BUILD_DOCKER')

			if (script.BUILD_DOCKER=="true")
			{
				if(script.fileExists('Dockerfile') == "false")
				{
					script.log.info "No se ha encontrado un Dockerfile en la raíz del proyecto."
				}

				if(script.ARTIFACTORY_REGISTRY=="")
				{
					script.error( "No se ha definido REGISTRY_ARTIFACTORY" )
					return
				}

				script.log.info "stgFw.execScriptEnviorement( ${script.ENTORNO}, ${script.DIR_DOCKER}, ${script.PRJ}, ${script.VERSION})"
				def st = new StgFw(script)
				def result =  st.execScriptEnviorement( "${script.ENTORNO}", "${script.DIR_DOCKER}", "${script.PRJ}", "${script.VERSION}")			

				if(result>0)
				{
					script.error( "Error en BUILD_DOCKER(${script.ENTORNO}) ->  ${result}" )
					// se produce error terminar la pipe de jenkins con estado de error
					return;
				}
			}
		}
	}
}
