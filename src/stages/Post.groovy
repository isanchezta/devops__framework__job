package stages
// https://mvnrepository.com/artifact/org.codehaus.groovy/groovy-xml
//@Grapes(
//	@Grab(group='org.codehaus.groovy', module='groovy-xml', version='2.4.18')
//)



import groovy.util.XmlSlurper
import java.sql.*
import java.io.File
import frmwork.ModelDB
import frmwork.StagesTypes

/**
 * Fase que se ejecuta tras haberse ejecutado el JOB
 * 
 * @author jpalm
 *
 */
class Post extends Stage {
	
	
	
	Post(Script scp) {
		super(scp)
	}
	
	/**
	 * Siempre se tiene que ejecutar esta fase
	 */
	boolean isExecute() {
		return true
	}
	
	/**
	 * Crea el registro en al tabla de LOG del JOB indicado
	 */
	void jobLogging(def script) {
		// Actualizar BD de depliegues
		script.log.info "Actualizar LOGGING Despliegue"
		
		def scp = script
		def fwVersion = "${script.FW_VERSION}"    // "${script.env.library.AccionaFwDevOps.version}"
		def jobName = "${script.env.JOB_NAME}"
		def jobsDefinition = jobName.split("/")
		def db = getDbConnect()
		def f_stop = db.getTimestamp()
		def stmt = "insert into \"LOGGING_JK\"(repo_url, jobname, version, entorno, status, division, url, f_alta, etiqueta, rama, version_fw, f_start, f_stop) values ('${script.GIT_URL}', '${jobsDefinition[1]}', '${script.VERSION}', '${script.ENTORNO}', '${script.currentBuild.currentResult}', '${jobsDefinition[0]}','${script.env.BUILD_URL}', CURRENT_TIMESTAMP, '${script.ESTADO}', '${script.BRANCH}', '${fwVersion}', '${script.START_TIMESTAMP}' ,'${f_stop}'  ) "
		script.log.info stmt
		
		db.executeSql(stmt)
	}
	
	
	
	/**
	 * Para el caso que sea un JOB de infraestructura que sea el generar de la misma
	 * Se tendr� que actualizar la informaci�n de la infraestreuctura de dicho entorno		
	 */
	void jobInfraestructure(def script) {
		script.log.info "jobInfraestructure()"
		
		def rootDir = script.pwd()
		def confXml = "${rootDir}/pipes/${script.INFRASTRUCTURE_XML}"
		String entorno = "${script.ENTORNO}"
		String content = script.readFile "${confXml}"
		def xml = new XmlParser().parseText(content)
		for(def node in xml.environment) {
			script.log.info "env:" + node.@env
			script.log.info "client:" + node.@client
			String env = node.@env
			String clie = node.@client
			
			// Si estamos en el entorno indicado y el modo es client=NO entonces se escribe el registro
			if(entorno.toUpperCase().equals(env.toUpperCase())) {
				if(clie.toUpperCase().equals("NO")) { // estamos en el caso del creador de la infraestructura
					// Ejecutamos el insert en al tabla
					def jobName = "${script.env.JOB_NAME}"
					def jobsDefinition = jobName.split("/")
					String name =  node.name.text()
					
					def stmt = "insert into \"INFRAESTRUCTURE_DEPLOY\"(division,job_name,rama,infraestructure,version,entorno,status,f_alta) values ('${jobsDefinition[0]}', '${jobsDefinition[1]}','${script.BRANCH}', '${name}', '${script.VERSION}', '${script.ENTORNO}', '${script.currentBuild.currentResult}', CURRENT_TIMESTAMP )"
					def db = getDbConnect()
					db.executeSql(stmt)
				}
			}
		}
	}

	void exec() {
		execute()
	}
	
	void execute() {
		try {
			def scp = this.script
			script.stage("****** SEND EMAILS ******") {
				def rootDir = scp.pwd()
				
				String resultado = "${scp.currentBuild.currentResult}"
				String stageTxt =  "${scp.env.ACTUAL_STAGE}"
				String stagesExec = "${scp.env.STAGES_OK}"
			
				if(resultado.contentEquals("SUCCESS"))
					stageTxt = "${scp.env.LAST_STAGE}"
			
				// El envio de email ser� dependiente de la variable de entorno
				if(scp.env.OFFICE365_SEND.equals("true"))
					scp.email.sendEmails365("${rootDir}/pipes/${scp.OFFICE365_XML}" , "", "", "${scp.currentBuild.currentResult}", "started ${scp.env.JOB_NAME} ${scp.env.BUILD_NUMBER} (<${scp.env.BUILD_URL}|Open>)", scp)
				if(scp.env.EMAIL_SEND.equals("true")) 
					scp.email.sendEmails("${rootDir}/pipes/${scp.EMAIL_XML}" , "", "", "JK: ${resultado} -> ${scp.env.JOB_NAME} #${scp.env.BUILD_NUMBER}" , resultado, "${scp.env.BUILD_URL}", scp,stageTxt, stagesExec )
			} 
			
			scp.stage("****** UPDATE DB LOGGING ******") {
			if(scp.env.LOGGING_ENABLE.equals("true")) 
				jobLogging(scp);
				
			if(scp.INFRASTRUCTURE_ENABLE.equals("true"))
				jobInfraestructure(scp);
			}
			// 	Se borra el workspace para no dejar datos en el esclavo
			
			scp.stage("****** END TASKS ******"){
			scp.step([$class: 'WsCleanup'])
			}
		} catch (error) {
			script.log.info "ERROR POSTPROCESO: " + error.getMessage() 
			throw error
		}
	}
}
