package builds

//@Grapes(
//	@Grab(group='org.codehaus.groovy', module='groovy-ant', version='2.0.1')
//)

import plugings.IStepExecutor
import plugings.ContextRegistry
import groovy.util.logging.Slf4j


/**
 * Clase que gestiona el empaquetado y desempaquetado de los desplegables
 * 
 * @author jpalmero
 *
 */
//@Grab('ch.qos.logback:logback-classic:1.2.1')
@Slf4j
class Artefacto  {

	/**
	 * Crea el ZIP del directorio indicado generando un archivo cuyo nombre es el c�digo de proyecto y la versi�n: ${PRJ}-${VERSION}.zip
	 * 
	 * @param directory Directorio el cual se va empaquetar
	 * @param PRJ Denominaci�n del proyecto
	 * @param VERSION Versi�n del proyecto
	 */
	public void packing(directory, PRJ, VERSION) {
		// Si la compilacion tiene exito se tiene que comprimir el resultado
		// El zip lo deja en el raiz del workspace lo renombramos a PRG-VERSION.zip, eliminamos una version previa
		IStepExecutor steps = ContextRegistry.getContext().getStepExecutor()
	
		log.info("Crear ZIP versionado del directorio")
		
		def fileNameZip = "${PRJ}-${VERSION}.zip"
		log.info("${fileNameZip}")
		String dirA = "a"
		def dirFileA = new File(dirA)
		if (dirFileA.exists()) {
			log.info "directorio a existe"
			steps.sh("rm -rf a") // sh "rm ${fileNameZip}"
			
		}
		def fileZip = new File(fileNameZip)
		if (fileZip.exists()) {
			log.info "Fichero existe de una ejecucion anterior: ${fileNameZip}"
			steps.sh ("rm ${fileNameZip}") // sh "rm ${fileNameZip}"
			
		}
		def srcDir = new File(directory)
		def ant = new AntBuilder()
		ant.echo('hello from Ant!')
		ant.zip(destfile: fileZip, basedir: srcDir)
		
		// movemos todos los atefactos al directorio a
		steps.sh( "mkdir a; mv ${fileNameZip} a")
		steps.sh( 'ls -al a')
	}
	/**
	 * Descomprime del directorio indicado todos los zip que se encuentra en dicho directorio
	 * 
	 * @param directory Directorio el cual se va a proceder a desempaquetar los zips
	 */
	public void unpacking(String directory ) {
		
		IStepExecutor steps = ContextRegistry.getContext().getStepExecutor()
		
		steps.dir (directory) {
		def  FILES_LIST = steps.sh (script: "ls *.zip", returnStdout: true).trim()
		for(String ele : FILES_LIST.split("\\r?\\n")){
		   fileOperations([
			 fileUnZipOperation(
				 filePath: "${ele}",
				 targetLocation: '.')
			 ])
		 steps.sh "rm ${ele}"
		}
	 }
	}
}
