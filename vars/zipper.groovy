

import plugings.IStepExecutor
import plugings.ContextRegistry
import groovy.util.logging.Slf4j


/**
 * Clase que gestiona el empaquetado y desempaquetado de los desplegables
 * 
 * @author jpalmero
 *
 */



	/**
	 * Crea el ZIP del directorio indicado generando un archivo cuyo nombre es el codigo de proyecto 
	 * y la version, y mueve el fichero ZIP resultante al directorio 'a' 
	 * 
	 * ${PRJ}-${VERSION}.zip
	 * 
	 * @param directory Directorio el cual se va empaquetar
	 * @param PRJ Denominacion del proyecto
	 * @param VERSION Version del proyecto
	 */
	public void packing(String directory, String PRJ, String VERSION) {
		// Si la compilacion tiene exito se tiene que comprimir el resultado
		// El zip lo deja en el raiz del workspace lo renombramos a PRG-VERSION.zip, eliminamos una version previa
		log.info("Crear ZIP versionado del directorio ${directory} para ${PRJ}-${VERSION}")
		
		def fileNameZip = "${PRJ}-${VERSION}.zip"
		log.info("Fichero a generara: ${fileNameZip}")
		
		res = fileExists("${fileNameZip}")
		if (res) {
			log.info "Fichero existe de una ejecucion anterior: ${fileNameZip}"
			sh ("rm ${fileNameZip}") // sh "rm ${fileNameZip}"
		}
	
		log.info('Comprimimos el directorio')
		zip zipFile: fileNameZip, dir: directory
		
		// movemos todos los atefactos al directorio a
		sh( "mv ${fileNameZip} a")
		sh( 'ls -al a')
	}
	/**
	 * Descomprime del directorio indicado todos los zip que se encuentra en dicho directorio
	 * 
	 * @param directory Directorio el cual se va a proceder a desempaquetar los zips
	 */
	public void unpacking(String directory ) {
		log.info("Descomprime los todos los archivos ZIP localizados en ${directory}")
		dir ("${directory}") {
			def FILES_LIST = ""
			try{
				FILES_LIST = sh (script: "ls *.zip", returnStdout: true).trim()
				for(String ele : FILES_LIST.split("\\r?\\n")){
					fileOperations([
						fileUnZipOperation(
						filePath: "${ele}",
						targetLocation: '.')
					])
					sh "rm ${ele}"
				}
			}catch(Exception e) {
				log.warning( e.getMessage())
			}
		}
	}
	
	/**
	 * Descomprime un archivo ZIP indicado, en la carpeta especificada
	 * 
	 * @param fileZip	Fichero a descomprimir
	 * @param target	Directorio destino
	 */
	public void unzipper (String fileZip, String target ) { 
		log.info("Se descomprime el archivo ${fileZip} en ${target}")
		fileOperations([
			fileUnZipOperation(
				filePath: "${fileZip}",
				targetLocation: "${target}/")
		])
	}
	
	/**
	 * Comprime la carpeta indicada y sus subcarpetas en el zip indicado
	 * 
	 * @param fileZip 
	 * @param source
	 */
	public void zipper (String fileZip, String source) {
		log.info("Comprimimos el directorio ${source} en ${fileZip}")
		zip zipFile: fileZip, dir: source
		
	}
	
return this