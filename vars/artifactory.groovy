// https://mvnrepository.com/artifact/com.cloudbees/groovy-cps
//@Grapes(
//	@Grab(group='com.cloudbees', module='groovy-cps', version='1.31')
//)

import com.cloudbees.groovy.cps.NonCPS

/**
 * Acceso al servicio de artifactory por usuario y contrase�a
 * Este acceso no tendr�a que utilizarse , ya que para acceder al servicio se tieen que utilizar un usuario 
 * de servicio
 * @param url		URL de acceso a artifactory artifactory.acciona.com
 * @param username	Usuario con permiso de acceso
 * @param password	Contraseña de acceso
 * @return			Retorna el objeto 
 */
def getArtifactory(url, username, password) {
  return  Artifactory.newServer (url: url, username: username, password: password)
}

/**
 * Acceso al servicio de artifactrory utilizando las credenciales almacenadas en jenkins
 * 
 * @param url			URL de acceso a artifactory
 * @param credentialsId	ID de credencial en Jenkins
 * @return				Retorna el objeto conexion
 */
def getArtifactoryByCred(url, credentialsId) {
	return  Artifactory.newServer (url: url, credentialsId: credentialsId)
}

/**
 * Se despliega un artefacto dentro del REPO indicado en el directorio  indicado por PRJ y versionado 
 * Esto es válido para repositorios que admitan subidas genéricas
 * Si existe el elemento se actualiza
 * Se permiten pasar en los PRJ y VERSION cadenas vacias
 * 
 * @param REPO		Repositorio dónde se alojará el artefacto
 * @param PRJ		Directorio que se creará dentro del REPO para alojar el elemento
 * @param VERSION	Subdirectorio partiendo de PRJ en dónde se alojará el componente  
 * @param PATTERN	Patron de busqueda de los artefactos a subir, puede ser un archivo o varios
 * @param script	Acceso al pipeline
 */
void deployArtefacto(String REPO, String PRJ, String VERSION, String PATTERN, Script script) { // a/*.zip
	// Se despliega el zip generado en artifactory en el repo de deploy subiendolo
	assert REPO?.trim()
	
	def uploadSpec = """{
			"files": [
			{
				"pattern": "$PATTERN",
				"target": "$REPO/$PRJ/$VERSION/"
		  	}
	   ]
	}"""
	log.info "Servidor artf: ${script.ARTIFACTORY_URL} - ${script.ARTIFACTORY_CREDENTIALSID}"
	def art = script.Artifactory.newServer(url: "${script.ARTIFACTORY_URL}", credentialsId: "${script.ARTIFACTORY_CREDENTIALSID}")
	art.upload spec: uploadSpec
}	

/**
 * Descarga de un repositorio indicado y de un directorio y subdirectorio el contenido completo
 * Se permiten pasar en los PRJ y VERSION cadenas vacias
 * 	
 * @param REPO 		Repositorio donde localizar los elementos a descargar
 * @param PRJ		Directorio principal 
 * @param VERSION	Subdirectorio
 */
void downloadArtefacto(String REPO, String PRJ, String VERSION, Script script) {
	assert REPO?.trim()
	
	def downloadSpec = """{
		 	"files": [
		  	{
		      	"pattern": "$REPO/$PRJ/$VERSION/",
		    	"target": "d/"
			}
		]
	}"""
	log.info "Servidor artf: ${script.ARTIFACTORY_URL} - ${script.ARTIFACTORY_CREDENTIALSID}"
	def art = script.Artifactory.newServer(url: "${script.ARTIFACTORY_URL}", credentialsId: "${script.ARTIFACTORY_CREDENTIALSID}")
	art.download spec: downloadSpec, failNoOp: true
}
   
/**
 * Compilacion via maven, con esta funcion no se hacen despliegues solo es compilacion no valida para 'mvn insall'
 *
 * @param repoDownload repositorio en artifactory en donde descargarse los complementos plugings y liberias
 * @param pomPath repositorio al fichero pom.xml
 * @param goals que es lo que se tiene que ejecutar 'package, site, validate ...'
 */
@NonCPS
void mavenBuild (String repoDownload, String pomPath, String goals) {
	// Compilamos con maven y genera el package
	assert repoDownload?.trim()
	assert pomPath?.trim()
	assert goals?.trim()
	
	def rtMaven = Artifactory.newMavenBuild()
	def art = Artifactory.newServer(url: "${ARTIFACTORY_URL}", credentialsId: "${ARTIFACTORY_CREDENTIALSID}")
	rtMaven.resolver( server: art, releaseRepo: repoDownload , snapshotRepo: repoDownload)
	rtMaven.run (pom: pomPath, goals: goals)

}
	

/**
 * 
 * @param repoDownload repositorio en artifactory en donde descargarse los complementos plugings y liberias
 * @param repoDeploy repositorio en d�nde alojar el componente maven generado
 * @param pomPath repositorio al fichero pom.xml
 * @param goals que es lo que se tiene que ejecutar 'package, site, validate ...'
 */
@NonCPS
void mavenBuildDepoy(String repoDownload, String repoDeploy, String pomPath, String goals) {
	// Compilamos con maven y genera el package
	assert repoDownload?.trim()
	assert pomPath?.trim()
	assert goals?.trim()
	
	def rtMaven = Artifactory.newMavenBuild()
	def art = Artifactory.newServer(url: "${ARTIFACTORY_URL}", credentialsId: "${ARTIFACTORY_CREDENTIALSID}")
	rtMaven.resolver( server: art, releaseRepo: repoDownload , snapshotRepo: repoDownload)
	rtMaven.deployer( server: art, releaseRepo: repoDeploy , snapshotRepo: repoDeploy)
	rtMaven.run (pom: pomPath, goals: goals)
	
}


/**
 * Compilacion via Gradle, se especifica que comando de Gradle se quiere que se use.
 *
 * @param repoDownload repositorio en artifactory en donde descargarse los complementos plugings y liberias
 * @param gradlePath directorio en el que se encuentra el dichero build.gradle
 * @param goals que es lo que se tiene que ejecutar 'package, site, validate ...'
 */
@NonCPS
void gradleTask (String repoDownload, String gradlePath, String task) {
	// Compilamos con Gradle y genera el package
	assert repoDownload?.trim()
	assert gradlePath?.trim()
	assert task?.trim()
	
	def rtGradle = Artifactory.newGradleBuild()
	def art = Artifactory.newServer(url: "${ARTIFACTORY_URL}", credentialsId: "${ARTIFACTORY_CREDENTIALSID}")
	rtGradle.resolver( server: art, repo: repoDownload)
	rtGradle.usesPlugin = true
	rtGradle.run (run: gradlePath, buildFile: 'build.gradle', tasks: task)
}
	
/**
 * Compilacion v�a Gradle en repositorio que soporte Gradle, por lo tanto se puede utilizar el 'mvn install'
 * 
 * @param repoDownload repositorio en artifactory en donde descargarse los complementos plugins y liberias
 * @param repoDeploy repositorio en d�nde alojar el componente Gradle generado
 * @param gradlePath directorio en el que se encuentra el dichero build.gradle
 */
@NonCPS
void gradleBuild(String repoDownload, String gradlePath) {
	// Compilamos con Gradle y genera el package
	artifactory.gradleTask(repoDownload, gradlePath, "clean buildEnvironment")
}

/**
 * Compilacion v�a Gradle y posterior despliegue en repositorio que soporte Gradle, por lo tanto se puede utilizar el 'mvn install'
 * 
 * @param repoDownload repositorio en artifactory en donde descargarse los complementos plugins y liberias
 * @param repoDeploy repositorio en d�nde alojar el componente Gradle generado
 * @param gradlePath directorio en el que se encuentra el dichero build.gradle
 */
@NonCPS
void gradleBuildDeploy(String repoDownload, String gradlePath) {
	// Compilamos con Gradle y genera el package
	artifactory.gradleTask(repoDownload, gradlePath, "clean buildEnvironment assembleRelease artifactoryPublish")
}


return this