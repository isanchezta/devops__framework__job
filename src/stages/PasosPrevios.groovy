package stages

//@Grapes(
//	@Grab(group='org.codehaus.groovy', module='groovy-xml', version='2.4.18')
//)

import hudson.model.*
import groovy.util.XmlSlurper
import java.io.File
import frmwork.ModelDB
import frmwork.StagesTypes

/**
 * Fase que ejecuta la operativa previa al despliegue
 * 
 * @author jpalm
 *
 */
class PasosPrevios extends Stage{

	
	PasosPrevios(Script scp) {
		super(scp)
	}
	
	void updateVar(def t) {
		def s = script
		s.log.info  "Variable entorno -> ${t.name()}: ${t.text()}"
		def projectVal = s.env.getProperty("${t.name()}")
		if (projectVal == null ) // urilizamos siempre el valor definido en el pipeline
			s.env.setProperty("${t.name()}", "${t.text()}")
		else
			s.log.info "*** Valor en jenkisfile proyecto -> ${t.name()}: ${projectVal}"
	}

	void globalVars() {
		script.log.info "globalVars"
		
		def dbXml = script.libraryResource 'environment.xml'
		def xml = new XmlParser().parseText(dbXml)
		
		xml.each { thing ->updateVar(thing)	}
	}
	
	void agentConfig(String agentLabels) {
		script.log.info "agenConfig(${agentLabels})"
		
		// Se procesa el fichero de configruacion de los agentes y si alguno coincide con algun label se procesa dichas variables
		def dbXml = script.libraryResource 'agents.xml'
		def xml = new XmlParser().parseText(dbXml)
		def s = script
		xml.each { thing ->
			if(agentLabels.contains(thing.@label)) {
				s.log.info "Agente ejecucion: ${thing.@label}" 
				thing.each { t ->updateVar(t) }
			}	
		}
		
	}

	boolean isRepoGitLab(String enviorementVar ) {
		return enviorementVar.contains("gitlab.pro.portal.ss")
	}
	void updateGitRepoCred() {
		// Si ya existe dada de alta la variables de entorno GIT_CREDENTIALSID
		// se usa el contenido de dicha variables como credencial para acceder a GIT_URL
		// si no existe dependiendo de d�nde sea el origen del repo 
		// se usan las credenciales de  GIT_CREDENTIALSID_AZURE, GIT_CREDENTIALSID_GITHUB
		
		def projectVal = script.env.getProperty("GIT_CREDENTIALSID")
		
		if (!projectVal?.trim()) {
			script.log.info "CREDENCIAL GIT NO INDICADA SE USA LA DE POR DEFECTO"
			
			if(isRepoGitLab(script.env.GIT_URL)) 
				script.env.setProperty("GIT_CREDENTIALSID",  script.env.GIT_CREDENTIALSID_GITLAB)
			else
				script.env.setProperty("GIT_CREDENTIALSID",  script.env.GIT_CREDENTIALSID_AZURE)
		}
		
		script.log.info "GIT_CREDENTIALSID: ${script.GIT_CREDENTIALSID}"

		//Se añade para prueba en local
		script.env.setProperty("GIT_CREDENTIALSID",  "GissGithubDevops")
		
	}

	def setTimestampPipe() {
		if(script.LOGGING_ENABLE.equals("true")) {
			// Actualizar BD de depliegues
			def dbXml = script.libraryResource 'databaseDeploy.xml'
			def xml = new XmlSlurper().parseText(dbXml)
			String _hostname = "${xml.hostname}"
			int _port = "${xml.port}" as Integer
			String _database = "${xml.database}"
			String _username= "${xml.username}"
			String _password= "${xml.password}"
			def db = new ModelDB(hostname: _hostname, port: _port, database: _database, username: _username, password:_password, script: script  )
			def f_start = db.getTimestamp()
			
			script.env.setProperty("START_TIMESTAMP", f_start)
		
		}
	}

	
	void execute() {
		script.step([$class: 'WsCleanup']) // limpieza del workspace, por si quedao algo de ejecucion anteriores
		// si estamos en github existir� la variable de entorno BRANCH_NAME
		// si no estamos en github al no existir la variabla dará error
		// y se utilizará lo que venga por parametro en BRANCH
		// Devuelve las variables de entorno. Esto es util cuando se quiere depurar el pipe

		String[] str
		String modulo

		script.log.info "PASOS PREVIOS"
		globalVars()
		agentConfig(script.NODE_LABELS)

		updateGitRepoCred()
			
		if(isRepoGitLab(script.env.GIT_URL))
			script.env.setProperty("REPO_ORIGEN", "GITLAB")
		else 
			script.env.setProperty("REPO_ORIGEN", "AZURE")
		
		script.log.info "***** REPOSITORIO EN ${script.REPO_ORIGEN} *****"
		
		def envVal = script.sh(returnStdout: true, script: 'env')
			
		List lines = envVal.split("\n").findAll { it.startsWith( 'library.AccionaFwDevOps' ) }
		def scp = script
		lines.each {
			def s = it.split('=')
			scp.env.setProperty("FW_VERSION", s[1])
		}
			
		// "https://gitlab.pro.portal.ss/devops/framework/devops__framework__job.git"
		def rutas = "${script.env.GIT_URL}".split("/")
		script.log.info "GITLAB_PROJECT_NAME: ${rutas[5]}"
		
		script.log.info "JOBNAME: ${script.env.JOB_NAME}"
		def jobsDefinition = rutas[5].split("__")
		script.env.setProperty("DIVISION", jobsDefinition[0])
		script.env.setProperty("APLICACION", jobsDefinition[1])

		str = jobsDefinition[2].split("\\.")
		modulo = str [0]

		script.env.setProperty("MODULO", modulo)
		
		script.env.setProperty("JOBNAME", script.env.getProperty("JOB_NAME"))
		script.env.setProperty("BRANCH", script.env.getProperty("BRANCH_NAME")) 

		//script.env.setProperty("TRIGGER", script.env.getProperty("GITLAB_OBJECT_KIND")) 
	
			
		script.log.info "DIVISION: ${script.DIVISION}"
		script.log.info "APLICACION: ${script.APLICACION}"
		script.log.info "MODULO: ${script.MODULO}"
		script.log.info "JOB: ${script.JOBNAME}"
		script.log.info "RAMA: ${script.BRANCH}"
			
		setTimestampPipe() // arranque del pipe
		
		script.log.info script.sh(returnStdout: true, script: 'env')
	}
}
