package stages

// https://mvnrepository.com/artifact/org.codehaus.groovy/groovy-json
//@Grapes(
//    @Grab(group='org.codehaus.groovy', module='groovy-json', version='2.4.18')
//)


import frmwork.Credentials
import frmwork.StagesTypes
import groovy.json.JsonSlurper
import frmwork.StagesTypes
import frmwork.StatusTypes


/**
 * Clase que engloba el proceso de compilacion
 * 
 * @author jpalm
 *
 */
class Compilacion extends Stage{
	
	Compilacion(Script scp) {
		super(scp)
		stageType = StagesTypes.Value.BUILD
	}
	
	/**
	 * Invoca a pipe de BUILDE de Azure de la organizacon ITACCION
	 * @param prj
	 * @param idBuild
	 * @return int 0 succeeded sino error
	 */
	def compileAzure(String org, String prj, String idBuild, String credentialAzure) {
		String result = ""
		String urlLog = ""
		String status = "running"
		script.log.info("compileAzure: ${org} - ${prj} -> pipe:${idBuild} - ${credentialAzure}");
		def creds = new  Credentials()
		def username = creds.getUsername(credentialAzure)
		def token = creds.getPassword(credentialAzure)
		
		String pat = Base64.getEncoder().encodeToString( ("${username}:${token}").getBytes())
		def post = new URL("https://dev.azure.com/${org}/${prj}/_apis/build/builds?api-version=5.0").openConnection();
		def message = "{\"definition\": {\"id\": ${idBuild}}}"
		
		post.setRequestMethod("POST")
		post.setDoOutput(true)
		post.setRequestProperty("Content-Type", "application/json")
		post.setRequestProperty ("Authorization", "Basic " + pat);
		post.getOutputStream().write(message.getBytes("UTF-8"));
		def postRC = post.getResponseCode();
		script.log.info("RESPUESTA: " + postRC);
		
		if(postRC.equals(200)) {
			def jsonSlurper = new JsonSlurper()
			String strRes = post.getInputStream().getText()
			def object = jsonSlurper.parseText( strRes );
			script.log.info object
			
			// Esperamos hasta que termina con un periodo de gracia de 10 minutos
			// obtner id compilacion
			
			urlLog =  object._links.web.href
			String urlResp = object._links.self.href
			script.log.info "DATOS EJECUCION AZURE: " + urlResp
			script.log.info "BUILD EJECUCION AZURE: " + urlLog
			
			def i = 0
			
			// ampliamos timeout a REPETICION / 2 = minutos espera
			int REPETICION = 60
			while(status.compareToIgnoreCase("running") == 0 && i < REPETICION )
			{
				Thread.sleep( 30000 )    
				def resp = new URL(urlResp).openConnection();
				resp.setRequestMethod("GET")
				resp.setRequestProperty ("Authorization", "Basic " + pat);
				def postRC2 = resp.getResponseCode();
				script.log.info "Respuesta build: " + postRC2
				if(postRC2.equals(200)) {
					String strRes2 = resp.getInputStream().getText()
					def object2 = jsonSlurper.parseText( strRes2 );
					//script.log.info object2
					script.log.info "Estado ejecucion JOB: ${object2.status}"
			
					if(object2.status.compareToIgnoreCase("completed")==0) {
						result = object2.result
						status = object2.status
					}
				}
				i++
			}
			
			if(i>=REPETICION)
				script.error("TIMEOUT AZURE DEVOPS")
			
			script.log.info "JOB COMPLETADO ${status}-${result}"
			script.log.info "URL LOG:  ${urlLog}"
			
			
		}
		return result.compareToIgnoreCase("succeeded")
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
		if(script.BUILD=="false")
		{
			script.log.info "NO HAY QUE COMPILAR, INDICADO POR PARAMETRO"
			return
		}
		if(script.DEPLOY.getEnvironment()==script.ENVTYPE.DES)
		{
			// Antes de invocar a la fase de compilacion se tiene que ver si se han de pasar las pruebas
			if(script.CODE_REVIEW=="true") {
				script.stage("****** CODE REVIEW ******") {
					CodeReview cr = new CodeReview(this.script)
					cr.exec()
				}
			}
			
			script.stage("****** COMPILE PROCESS ******") {
				CompileProcess cr = new CompileProcess(this.script)
				cr.exec()
			}
			
			if(this.script.BUILD_AZURE=="false") {
				script.stage("****** UPLOAD ARTEFACTOS ******") {
				// Si la compilacion fue correcta subimos los artefactos alojados en directorio a
					this.script.log.info( "Subida Artifactory y contenido de directorio de subida: ${this.script.ARTIFACTORY_REPO}/${this.script.PRJ}/${this.script.VERSION}/")
					this.script.sh "ls -al a"
					this.script.artifactory.deployArtefacto("${this.script.ARTIFACTORY_REPO}","${this.script.PRJ}","${this.script.VERSION}" , "a/*.zip", this.script )
					this.script.sh "rm -rf a"
				}
			}
			
		}
	}
}
