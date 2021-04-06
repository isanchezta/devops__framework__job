package stages

//@Grapes([
//	@Grab(group='org.codehaus.groovy', module='groovy-xml', version='2.4.18'),
//	@Grab(group='com.cloudbees', module='groovy-cps', version='1.31')
//])


import groovy.util.XmlSlurper
import com.cloudbees.groovy.cps.NonCPS
import frmwork.StagesTypes

/**
 * Clase que engloba el despliegue
 * 
 * @author jpalm
 *
 */
class RunDeploy extends Stage {
	
	RunDeploy(Script scp) {
		super(scp)
		stageType = StagesTypes.Value.DEPLOY
	}
	
	void callDeploy() {
		def scp = script
		try
		{
			script.log.info "stgFw.execScriptEnviorement( ${script.ENTORNO}, ${script.DIR_ENTORNO}, ${script.PRJ}, ${script.VERSION})"
			def st = new StgFw(script)
			def result =  st.execScriptEnviorement( "${script.ENTORNO}", "${script.DIR_ENTORNO}", "${script.PRJ}", "${script.VERSION}")
			if(result>0) 
			{
				script.error( "Error en DEPLOY(${script.ENTORNO}) ->   ${result}" )
				// se produce error terminar la pipe de jenkins con estado de error
				return;
			}
		}
		catch(e)
		{
			script.log.info "ERROR EN METODO callDeploy: " + e.getMessage()
			script.error(e.getMessage())
		}
	}
	
	void callPostDeploy() {
		script.log.info "stgFw.execScriptPostEnviorement( ${script.ENTORNO}, ${script.DIR_ENTORNO}, ${script.PRJ}, ${script.VERSION})"
		def st = new StgFw(script)
		def result =  st.execScriptPostEnviorement( "${script.ENTORNO}", "${script.DIR_ENTORNO}", "${script.PRJ}", "${script.VERSION}")
		if(result>0) {
			script.error( "Error en DEPLOY(${script.ENTORNO}) ->   ${result}" )
			// se produce error terminar la pipe de jenkins con estado de error
			return;
		}
	}
	
	boolean bLock
	String entorno 
	String resource
	boolean master 
	int timeout
	String unit
	String message
	def manual 
	
	def confLock() {
		bLock = false;
		
		if(script.LOCK_DEPLOY=="true") {
			script.log.info "SE BLOQUEA PROCESO DE DESPLIEGUE"
			entorno = "${script.ENTORNO}"
			master = false
			String content = script.readFile "${script.WORKSPACE}/pipes/${script.LOCK_DEPLOY_XML}"
			script.log.info "XML:" +  content
			def xml = new XmlSlurper().parseText(content)
			
			manual = false
			
			for(def node in xml.lock) {
				script.log.info "name:" + node.name.text()
				script.log.info "environment:" + node.@environment
				script.log.info "master:" + node.@master
				String env = node.@environment
							
				if( entorno.contentEquals(env)) {
					script.log.info "SE localiza bloqueo en el entorno de ${node.@environment} que ${node.@master} actua como MASTER"
					resource = node.name.text()
					master = "${node.@master}".contentEquals("YES")
					bLock = true
					
					try {
						boolean manualNode = node.manual.size()>0
						if(master && manualNode ) {
							unit = node.manual.@unit
							message = node.manual.message
							timeout = node.manual.@timeout.toInteger()
							manual = true
							script.log.info"Aprobacion manual: ${message} -> ${timeout}-${unit}"
						}
					} catch (e) {
						script.log.info "NO HAY APROBACION MANUAL"
						manual = false
					}
					
					break;
				}
			}
		}
	}
	
	@NonCPS
	def deleteLocks(lockNames) {
		def manager = org.jenkins.plugins.lockableresources.LockableResourcesManager.get()
		synchronized (manager) {
			manager.getResources().removeAll { r -> lockNames.contains(r.name) } //&& !r.locked && !r.reserved }
			manager.save()
		}
	}
	
	
	def searchLock(String resource, Script scp) {
		scp.log.info "searchLock(${resource})"
		def locked = false
		def manager = org.jenkins.plugins.lockableresources.LockableResourcesManager.get()
		def resources = manager.getResources()
		
		resources.each {
			scp.log.info "Recurso: ${it.name}"
			if(it.name.compareToIgnoreCase(resource)==0) {
				scp.log.info  "Recurso Padre localizado: ${resource}"
				locked = it.locked
			}
		}
		
		return locked
	}

	
	def lockMaster(Script script) {
		def scp = script
		try {
		
		scp.lock(resource: "${resource}"  ) {
			if(manual) {
				scp.stage('APROBACION MANUAL'){
					scp.timeout(time: timeout, unit:"${unit}") {
						scp.input (message: "${message}" )
					}
				}
			}
			scp.log.info "ARRANCAMOS DESPLIEGUE"
			callDeploy()
		}
		} catch (error) {
			// Se tiene que avisar a los JOBS encolados que tienen que abortar
			scp.log.info "Error: " + error.getMessage()
			deleteLocks(resource)
			scp.lock(resource: "${resource}_CANCEL"  ) {
				scp.log.info "SE ESPERA A LA CANCELACION DE LOS JOBS EN COLA"
				scp.sleep 300
			}
			deleteLocks(resource)
			deleteLocks("${resource}_CANCEL" )
			scp.error("DESPLIEGUE MASTER CANCELADO")
		}
		
		scp.log.info "MASTER: SE ESPERAN QUE LOS HIJOS TERMINEN"
		// ejecucion post despliegue
		// wait , exec -> postDeploy
		scp.sleep 60
		scp.log.info "ESPERANDO A QUE LOS HIJOS TERMINEN"
	
		scp.lock(resource: "${resource}") {
			scp.log.info "HIJOS TERMINARON SE INVOCA POST-DEPLOY"
			callPostDeploy()
		}
		deleteLocks(resource)
	}

	def lockNoMaster(Script script) {
		def scp = script
		scp.log.info "JOB no actua como MASTER, es necesario que el MASTER haya creado el lock: ${resource}"
		
				  
		if(!searchLock("${resource}", scp)) {
			scp.error("JOB Padre no creo el lock: ${resource}")
		}
		scp.log.info "HIJO ESPERA EN COLA DEL PADRE"
		try {
			scp.lock(resource: "${resource}" ) {
				scp.log.info "DENTRO SECCION: " + (master ? "MASTER" : "HIJO")
				
				def abortar = searchLock("${resource}_CANCEL", scp)
				if(abortar) {
					scp.error("JOB CANCELADO de la cola: ${resource}")
				}
				
				callDeploy()
			}
		} catch (e) {
			scp.log.info "ERROR AL DESPLEGAR EN EL HIJO: " + e.getMessage()
			scp.error(e.getMessage())
		}
	}

	boolean infraestructuraEnable () {
		boolean bRes = false
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
				if(clie.toUpperCase().equals("YES")) { // estamos en el caso del creador de la infraestructura
					// Ejecutamos la select sobre la tabla para verificar que la infraestructura
					// esta en la version correcta
					def versionNecesaria = node.version.text()

					def db = getDbConnect()
					String versionDeslegada = db.queryVersionInfraByEnvioremnt(node.name.text(), entorno) 
					script.log.info "${entorno}-${node.name.text()} version necesaria(${versionNecesaria}) ->  version desplegada (${versionDeslegada})"
					bRes =  versionDeslegada.contains(versionNecesaria)
					
				}
			}
		}
		return bRes
	}
	
	
	void execute() {
		
		script.log.info "EJECUTAMOS DESPLIEGUE Entorno: ${script.ENTORNO}"
		
		// Si estamos en un proceso que necesita el despliegue de una infraestructura en concreto
		// se tendr� que revisar que dicha infraestructura est� desplegada en el entorno indicado
		if(script.INFRASTRUCTURE_ENABLE.equals("true")) {
			if(!infraestructuraEnable ()) {
				script.error ("NO ESTA DESPLEGADA LA INFRAESTRUCTURA EN SU VERSION NECESARIA EN EL ENTORNO  ${script.ENTORNO}")
				return
			}
		}
		
		
		// Para el caso de intervencion manual del bloqueo procedemos a preguntar en este punto 
		confLock()
		
		if(bLock && master) {
			lockMaster(script)
		} else if(bLock && !master) {
			lockNoMaster( script)

		}
		
		if(!bLock) 
			callDeploy()
		
	}
}