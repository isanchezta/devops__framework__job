package es.segsocial.prosa

/**
classifier NO ES NECESARIO
packaging NO ES NECESARIO

Classifier afecta de forma que despues de la version se añade como -classifier
El nombre es ARTIFACTID-VERSION-CLASSIFIER

En una subida con los siguientes valores:
packages:
		[$class: 'MavenPackage',
			mavenAssetList: [
				[classifier: 'jarcl',
				 extension: 'jarex',
				 filePath: "${fichero}"]
			],
			mavenCoordinate: [
				artifactId: "mariadb",
				groupId: "borrar.es.felix",
				packaging: 'zippac',
				version: "1.0.1"
			]
		]

Generaun un artefacto que necesita las siguientes coordenadas
		<dependency>
		  <groupId>borrar.es.felix</groupId>
		  <artifactId>mariadb</artifactId>
		  <version>1.0.1</version>
		  <classifier>jarcl</classifier>
		  <type>jarex</type>
		</dependency>



*/

//@ToString
class DatosArtsGenerados {
	String directorio
	String rutaGenerado
	String groupId
	String artifactId

	String version
	
	String classifier=""
	String extension="jar"
	String packaging="jar"
	
	def getDatosSubidaNexus() {
		
		def mal = [
			classifier: "${classifier}",
			extension: "${extension}",
			filePath: "${rutaGenerado}"]
		def mc = [
			artifactId: "${artifactId}",
			groupId: "${groupId}",
			packaging: "${packaging}",
			version: "${version}"]

		def resul = [$class: 'MavenPackage',
			mavenAssetList: [[
				classifier: "${classifier}",
				extension: "${extension}",
				filePath: "${rutaGenerado}"]],
			mavenCoordinate: [
				artifactId: "${artifactId}",
				groupId: "${groupId}",
				packaging: "${packaging}",
				version: "${version}"]
		]
		
		return resul
	}
	
	
	public String toString() {
		def resul="DatosArtsGenerados: [directorio: ${directorio}, rutaGenerado: ${rutaGenerado}, groupId: ${groupId}, artifactId: ${artifactId}, version: ${version}, classifier: ${classifier}, extension: ${extension}, packaging: ${packaging}]"

		return resul
	}
	
}


