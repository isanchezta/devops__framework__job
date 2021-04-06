package es.segsocial.prosa

import groovy.io.FileType


class FicherosGenerados {
	String zip = null
    String ear = null
}

class FicheroGeneradoPorExtension {
	String nombre = null
    String extension = null
}

class FileUtils {

	@NonCPS
	def getArtefactosZipEar(rutaDir) {
		println "[FileUtils] getArtefactosZipEar INICIO"

		def dir = new File(rutaDir)
		def ficheros = new FicherosGenerados()
		dir.eachFile() { file ->
			
			def nombre = file.name
			def partes = nombre.split("\\.")
			def extension = partes[partes.length -1]
			if(extension.compareToIgnoreCase( "zip" ) == 0) {
				ficheros.zip = nombre
			} else if(extension.compareToIgnoreCase( "ear" ) == 0) {
				ficheros.ear = nombre
			}
		}

		println "[FileUtils] getArtefactosZipEar FIN"
		
		return ficheros
	}

	@NonCPS
	/**
	Las extensiones, deben estar en minusculas
	*/
	def getArtefactosByExtension(rutaDir, extensiones) {
		println "[FileUtils] getArtefactosByExtension INICIO"
		println "[FileUtils] Ruta: ${rutaDir}"
		println "[FileUtils] Extensiones: ${extensiones}"
		
		def resul = []

		def dir = new File(rutaDir)
		
		dir.eachFile() { file ->
			
			def nombre = file.name
			def partes = nombre.split("\\.")
			def extension = partes[partes.length -1]
			if(extensiones.contains(extension.toLowerCase())) {
				def fichero = new FicheroGeneradoPorExtension()
				fichero.nombre = nombre
				fichero.extension = extension
				resul << fichero
			}

		}

		println "[FileUtils] getArtefactosByExtension FIN"
		
		return resul
	}
	
	@NonCPS
	def leerFicheroPropiedades(rutaFich) {
		Properties properties = new Properties()
		File propertiesFile = new File(rutaFich)
		propertiesFile.withInputStream {
			properties.load(it)
		}
		return properties
	}

}

