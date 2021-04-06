package stages
/**
 * Funciones que encapsulan las fases que ejecuta el pipeline
 * Cada funcion se corresponde con una fase del JK, 
 * de esta manera mantemos la mayor parte de la logica del framework de Jk oculta al desarrollador 
 * y m�s f�cilmente mantenible  
 * 
 * @author jpalm
 *
 */
import frmwork.StagesTypes
/**
 * Utilidades de las distintas fases
 * 
 * @author jpalm
 *
 */
class StgFw extends  Stage {
	
	
	public StgFw(Script scp) {
		super(scp)
		stageType = StagesTypes.Value.DEPLOY
	}

 /**
  * 
  * Fase: OBTENER A TRAVES DEL ESTADO Y LA RAMA EL ENTORNO 
  *
  * @param app		Aplicativo a desplegar
  * @param envType	Entonrno de despliegue
  * @param deploy	Framework de JK
  * @param script 	Acceso al pipeline
  * @return			Devuelve el directorio de entorno
  */
 public def queryEnvironmentDeploy(frmwork.Aplicativo app, frmwork.EnvironmentsTypes envType, frmwork.AccionaFwDevOps deploy ) {
	 try
	 {
		// Una vez obtenido la version se localiza el entorno de despliegue
		// si no se localiza eun posible entorno de despleigue
		// se considera la ejecuciÃ³n como errÃ³nea
		// revisar la documentaciÃ³n de SIMPLE_GIT para
		// conocer el ciclo de vida
		script.registry.init()
	
		String appStr = app.toString()
		script.log.info  "FROM GROOVY APP ${appStr} -> ${script.BRANCH} "
		
		
		script.ENTORNO = deploy.queryEnvironmentDeploy()
		
		script.log.info( "Seleccionar directorio de trabajo dependiendo del entorno ${script.ENTORNO}")
		def DIR_ENTORNO = ""
		
		switch(deploy.getEnvironment()){
			case envType.DES:
				DIR_ENTORNO = "${script.DIR_DES}"
				break;
			case envType.INT:
				DIR_ENTORNO = "${script.DIR_INT}"
				break;			
			case envType.PRE:
				DIR_ENTORNO = "${script.DIR_PRE}"
				break;
			case envType.PRO:
				DIR_ENTORNO = "${script.DIR_PRO}"
				break;
			default:
				script.error("Error al obtener el entorno(${script.ENTORNO}): ESTADO->${script.ESTADO} RAMA->${script.BRANCH}")
		}
					
		script.log.info "Estado ${script.ESTADO}/${script.BRANCH} -> Entorno ${script.ENTORNO} -> Directorio ${DIR_ENTORNO} "
		
		return DIR_ENTORNO
	}
	catch(e)
	{
		script.log.info "ERROR EN METODO queryEnvironmentDeploy: " + e.getMessage()
		script.error(e.getMessage())
	}
 }

 
 /**
  * Retorna la cadena encriptada 
  * @param text			cadena a encriptar
  * @param credManager	credencial del gestor de proyecto
  * @param credDevOps	credencial de la oficina devops
  * @return				devuelve el texto con la cadena encriptada
  */
 public def encryptText(String text, String credManager, String credDevOps) {
	 script.log.info "encryptText: " + credManager + " " + credDevOps
	 
	 def cred = new frmwork.Credentials();
	 String publicKey = cred.getText(credManager);
	 String privateKey = cred.getText(credDevOps);
	 
	 if(credManager.length() ==0 || credDevOps.length()==0)
		 return "";
		 
	 def crypt = new frmwork.CryptoUtils()
	 return crypt.encrypt( text, publicKey, privateKey)
}
 
 
 public def execScriptEnviorement(String entorno, String dirEntorno, String project, String versionDeploy) {
	try
	{
		script.registry.init()
		// en esta fase se carga dinÃ¡micamente el fichero por entorno
		// para ejecutar el despliegue
		def rootDir = script.pwd()
		script.log.info( "INVOCAR ENTORNO -> ${entorno}, pipes/${dirEntorno}")
		script.log.info( "${rootDir}/pipes/${dirEntorno}/start.groovy -> ${versionDeploy}")
		def deploy = script.load "${rootDir}/pipes/${dirEntorno}/start.groovy"
		def result = deploy.start("d/${project}/${versionDeploy}","pipes/${dirEntorno}" )
		return result
	}
	catch(e)
	{
		script.log.info "ERROR EN METODO execScriptEnviorement: " + e.getMessage()
		script.error(e.getMessage())
	}	 
 }
 
 public def execScriptPostEnviorement(String entorno, String dirEntorno, String project, String versionDeploy) {
	 try
	 {
		script.registry.init()
		// en esta fase se carga dinÃ¡micamente el fichero por entorno
		// para ejecutar el despliegue
		def rootDir = script.pwd()
		script.log.info( "INVOCAR ENTORNO -> ${entorno}, pipes/${dirEntorno}")
		script.log.info( "${rootDir}/pipes/${dirEntorno}/post_deploy.groovy -> ${versionDeploy}")
		def deploy = script.load "${rootDir}/pipes/${dirEntorno}/post_deploy.groovy"
		def result = deploy.start("d/${project}/${versionDeploy}","pipes/${dirEntorno}" )
		return result
	}
	catch(e)
	{
		script.log.info "ERROR EN METODO execScriptPostEnviorement: " + e.getMessage()
		script.error(e.getMessage())
	}
 }
 
 void execute() {
	 
 }
 
}