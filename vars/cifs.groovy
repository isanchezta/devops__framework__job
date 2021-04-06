/**
 * Copia los ficheros a una unidad compartida CIFS y carpeta 
 * Instalar: Jenkins Publish over CIFS plugin https://wiki.jenkins.io/display/JENKINS/Publish+Over+CIFS+Plugin
 * @param prefix Carpeta y patron de busqueda de archivos a transferir
 * @cifsConfig configuracion del cifs se tiene que crear en runtime
 * @destination directorio destino para alojar el archivo
 */
def deployToCIFS(prefix = "", cifsConfig, destination, credentials) {

	cifsConfig = 'myShare' // Define a share named "myShare" in the Jenkins Publish over CIFS system configuration
	  srcFiles = "${prefix}/**/**" // Copy everything after prefix
	  cifsPublisher alwaysPublishFromMaster: false, continueOnError: false, failOnError: true, publishers: [
		  [configName: cifsConfig,
			  transfers: [
				  [cleanRemote: true, excludes: '', flatten: false, makeEmptyDirs: true, noDefaultExcludes: false, patternSeparator: '[,]+', remoteDirectory: destination, remoteDirectorySDF: false, removePrefix: prefix, sourceFiles: srcFiles],
			  ],
			  usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false
		  ]
	  ]
  }


return this