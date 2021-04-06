package es.segsocial.prosa

class ConfiguracionSpm {
	String version
	//String codAplicacion
	
	String versionJarGeneral
	
	String carpetaScript
	String carpetaDestino
	
	//String urlCloneAplicaciones
	//String dirCheckoutAplicaciones
	
	String artifactId
	String artifactGroupId

	String versionwas
	String versionWAS
	
	def arrArtifacts = []

	DatosArtsGenerados zipSpm = null
	DatosArtsGenerados earSpm = null

	ConfiguracionSpm(carScript, carDestino) {
		carpetaScript = carScript
		carpetaDestino = carDestino

	}
	
	def initPom(confGlobal) {
		String file = "${confGlobal.dirCheckoutInfra}/SPMs/${carpetaScript}/pom.xml"

		PomUtil pu = new PomUtil()
		def dp = pu.consultaVersiones(file)

		version = dp.version
		artifactId=dp.artifactId
	    artifactGroupId=dp.groupId

		def doc = pu.getDocument(file)
		versionJarGeneral = doc.properties.getProperty("es.segsocial.jargeneral.version")
	    //artifactJarGeneralId=dp.artifactId

		versionwas = doc.properties.getProperty("was.version")
		versionWAS = versionwas.toUpperCase()

		if(versionJarGeneral.isEmpty()){
			throw new NullPointerException("Para compilar los SPMs es necesario definir en el pom la versión de jarGeneral y de WAS. En el pom.xml actual no está definida la propiedad 'es.segsocial.jargeneral.version'. Se debe parar la ejecución.")
		}

		if(versionwas.isEmpty()){
			throw new NullPointerException("Para compilar los SPMs es necesario definir en el pom la versión de jarGeneral y de WAS. En el pom.xml actual no está definida la propiedad 'was.version'. Se debe parar la ejecución.")
		}
		
	}
		
	def initArtefactos(confGlobal) {
		def rutaBaseVersion = "${confGlobal.dirResultado}/${versionWAS}/Empaquetado/${carpetaDestino}/${version}"
		
		def fu = new FileUtils()
		def datArt = fu.getArtefactosZipEar(rutaBaseVersion)
		
		if(datArt.zip != null) {
			zipSpm = new DatosArtsGenerados()
			zipSpm.directorio = "${rutaBaseVersion}"
			zipSpm.rutaGenerado = "${zipSpm.directorio}/${datArt.zip}"
			zipSpm.artifactId = "${artifactId}zip"
			zipSpm.groupId = "${artifactGroupId}"
			zipSpm.version=version
			zipSpm.classifier="zip"
			zipSpm.extension="zip"
			zipSpm.packaging="zip"
			arrArtifacts << zipSpm.getDatosSubidaNexus()
		}

		if(datArt.ear != null) {
			earSpm = new DatosArtsGenerados()
			earSpm.directorio = "${rutaBaseVersion}"
			earSpm.rutaGenerado = "${earSpm.directorio}/${datArt.ear}"
			earSpm.artifactId = "${artifactId}ear"
			earSpm.groupId = "${artifactGroupId}"
			earSpm.version=version
			earSpm.classifier="ear"
			earSpm.extension="ear"
			earSpm.packaging="ear"
			arrArtifacts << earSpm.getDatosSubidaNexus()
		}

	}
	
	def getArrAtifacts() {
			return arrArtifacts
	}
	
	def mostrar() {
		def val="ConfiguracionSpm"
		def resul = "###################################### ${val} ######################################\n" + 
			"----- ${val}.version: ${version}\n" +
			"----- ${val}.versionJarGeneral: ${versionJarGeneral}\n" +
			"----- ${val}.carpetaScript: ${carpetaScript}\n" +
			"----- ${val}.carpetaDestino: ${carpetaDestino}\n" +
			"----- ${val}.artifactId: ${artifactId}\n" +
			"----- ${val}.artifactGroupId: ${artifactGroupId}\n"
	
		return resul
	}
}

