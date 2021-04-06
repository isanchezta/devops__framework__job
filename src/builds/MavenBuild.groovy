package builds

//@Grapes(
//	@Grab(group='com.cloudbees', module='groovy-cps', version='1.31')
//)

import com.cloudbees.groovy.cps.NonCPS
import groovy.util.logging.Slf4j


/**
 * Clase para poder construir por maven el aplicativo
 */
@Grab('ch.qos.logback:logback-classic:1.2.1')
@Slf4j
class MavenBuild  {
	
	def rtMaven // rtMaven = Artifactory.newMavenBuild()
	def artifactory
	MavenBuild(artifactory, rtMaven) {
		 this.artifactory = artifactory
		 this.rtMaven = rtMaven
	}
	
	// Todos los elementos a subir a artifactory tienen que alojarse en el directorio a
	/**
	 * Compilacion via maven, con esta funcion no se hacen despliegues solo es compilacion no valida para 'mvn insall'
	 * 
	 * @param repoDownload repositorio en artifactory en d�nde descargarse los complementos plugings y liberias
	 * @param pomPath repositorio al fichero pom.xml
	 * @param goals que es lo que se tiene que ejecutar 'package, site, validate ...'
	 */
	@NonCPS
	def compile (String repoDownload, String pomPath, String goals) {
		// Compilamos con maven y genera el package
		String res = ""
		
		assert artifactory != null
		rtMaven.resolver( server: artifactory, releaseRepo: repoDownload , snapshotRepo: repoDownload)
		rtMaven.run (pom: pomPath, goals: goals)
		return res
	}
	
}
