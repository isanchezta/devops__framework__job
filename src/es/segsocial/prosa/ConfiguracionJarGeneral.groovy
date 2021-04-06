package es.segsocial.prosa

class ConfiguracionJarGeneral {
	public static final String artJarGeneral="APIPROSProsaGeneral"
	
	String version
	String versionwas
	String versionWAS
	// Artifacts ID para nexus
	String artifactJarGeneralId=artJarGeneral.toLowerCase()
    String artifactGroupIdJarGeneral
	String artifactGroupIdJarGeneralDesa
	
	// Datos de los ficheros generados
	DatosArtsGenerados jarGeneralEmpaquetado = new DatosArtsGenerados()
	DatosArtsGenerados jarGeneralDesarrollo = new DatosArtsGenerados()
	
	ConfiguracionJarGeneral() {
	}
	
	def initPom(confGlobal) {
		String file = "${confGlobal.dirCheckoutInfra}/APIPROSProsaGeneral/pom.xml"
		
		PomUtil pu = new PomUtil()
		def dp = pu.consultaVersiones(file)

		version = dp.version
	    artifactGroupIdJarGeneral=dp.groupId
		artifactGroupIdJarGeneralDesa=artifactGroupIdJarGeneral + ".desa"

		def doc = pu.getDocument(file)
		versionwas = doc.properties.getProperty("was.version")
		versionWAS = versionwas.toUpperCase()

		if(versionwas.isEmpty()){
			throw new NullPointerException("Para compilar jarGeneral es necesario definir en el pom la versión de WAS.En el pom.xml actual no está definida la propiedad 'was.version'. Se debe parar la ejecución.")
		}

		if(!artifactGroupIdJarGeneral.contains(versionwas)){
			throw new NullPointerException("La versión de was debe coincidir con la que contiene el groupId.")
		}
		
		initArtefactos(confGlobal)
	}
	
	def initArtefactos(confGlobal) {
		jarGeneralEmpaquetado.directorio="${confGlobal.dirResultado}/${versionWAS}/Empaquetado/JarGeneral/${version}"
		jarGeneralEmpaquetado.rutaGenerado="${jarGeneralEmpaquetado.directorio}/${artJarGeneral}.jar"
		jarGeneralEmpaquetado.artifactId="${artifactJarGeneralId}"
		jarGeneralEmpaquetado.groupId="${artifactGroupIdJarGeneral}"

		jarGeneralDesarrollo.directorio="${confGlobal.dirResultado}/${versionWAS}/Desarrollo/JarGeneral/${version}"
		jarGeneralDesarrollo.rutaGenerado="${jarGeneralDesarrollo.directorio}/${artJarGeneral}.jar"
		jarGeneralDesarrollo.artifactId="${artifactJarGeneralId}"
		jarGeneralDesarrollo.groupId="${artifactGroupIdJarGeneralDesa}"
	}
	
	def mostrar() {
		def resul = "###################################### ConfiguracionJarGeneral ######################################\n" + 
			"----- ConfiguracionJarGeneral.version: ${version}\n" +
			"----- ConfiguracionJarGeneral.artJarGeneral: ${artJarGeneral}\n" +
			"----- ConfiguracionJarGeneral.artifactJarGeneralId: ${artifactJarGeneralId}\n" +
			"----- ConfiguracionJarGeneral.artifactGroupIdJarGeneral: ${artifactGroupIdJarGeneral}\n" +
			"----- ConfiguracionJarGeneral.jarGeneralEmpaquetado.directorio: ${jarGeneralEmpaquetado.directorio}\n" +
			"----- ConfiguracionJarGeneral.jarGeneralEmpaquetado.rutaGenerado: ${jarGeneralEmpaquetado.rutaGenerado}\n" +
			"----- ConfiguracionJarGeneral.jarGeneralDesarrollo.directorio: ${jarGeneralDesarrollo.directorio}\n" +
			"----- ConfiguracionJarGeneral.jarGeneralDesarrollo.rutaGenerado: ${jarGeneralDesarrollo.rutaGenerado}\n"
		return resul
	}
	
	def descargaArtefactos() {
		
		def confNexus = new ConfiguracionNexus()
		// Configuracion NEXUS para descarga de jar general
		String urlNexusDescargaJarGeneralEmpaquetado=confNexus.genUrlNexusArtifactJar(
			confNexus.nexusRepositoryJarGeneralId, artifactGroupIdJarGeneral, artifactJarGeneralId, version)
		String urlNexusDescargaJarGeneralDesarrollo=confNexus.genUrlNexusArtifactJar(
			confNexus.nexusRepositoryDesarrolloId, artifactGroupIdJarGeneral, artifactJarGeneralId, version)

		sh "mkdir -p ${jarGeneralEmpaquetado.directorio}"
		sh "mkdir -p ${jarGeneralDesarrollo.directorio}"
		sh "curl ${urlNexusDescargaJarGeneralEmpaquetado} -o ${jarGeneralEmpaquetado.rutaGenerado}"
		sh "curl ${urlNexusDescargaJarGeneralDesarrollo} -o ${jarGeneralDesarrollo.rutaGenerado}"
	}
}

