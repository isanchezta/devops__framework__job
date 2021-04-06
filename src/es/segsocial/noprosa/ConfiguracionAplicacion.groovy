package es.segsocial.noprosa

class ConfiguracionAplicacion {
	String codAplicacion
	String urlClone

	/*
	String version
	
	String rutaBaseVersion
	
	String versionJarGeneral
	String versionInfraestructura
	String appComunesVersiones
	String versionwas
	String versionWAS

	
	String artifactId
	String artifactGroupId
	
	DatosArtsGenerados zipApp = new DatosArtsGenerados()
	DatosArtsGenerados earApp = new DatosArtsGenerados()
	*/
	
	ConfiguracionAplicacion(codApp, confGit, rutaRepoGit) {
		codAplicacion = codApp
		
		urlClone="${confGit.urlGitRemote}/${confGit.usuarioGit}/${rutaRepoGit}"
		
	}

	/*
	def initPom(confGlobal) {
		String file = "${dirCheckoutAplicaciones}/pom.xml"

		PomUtil pu = new PomUtil()
		def dp = pu.consultaVersiones(file)

		version = dp.version
	    artifactGroupId=dp.groupId
		artifactId=dp.artifactId

		def doc = pu.getDocument(file)
		versionJarGeneral = doc.properties.getProperty("es.segsocial.jargeneral.version")
		versionInfraestructura = doc.properties.getProperty("es.segsocial.infra.version")
		appComunesVersiones = doc.properties.getProperty("prosa.apps.comunes")
		versionwas = doc.properties.getProperty("was.version")
		versionWAS = versionwas.toUpperCase()

		if(versionJarGeneral.isEmpty()){
			throw new NullPointerException("Para compilar una aplicación es necesario definir en el pom la versión de jarGeneral, de infraestructura y de WAS. En el pom.xml actual no está definida la propiedad 'es.segsocial.jargeneral.version'. Se debe parar la ejecución.")
		}

		if(versionInfraestructura.isEmpty()){
			throw new NullPointerException("Para compilar una aplicación es necesario definir en el pom la versión de jarGeneral, de infraestructura y de WAS. En el pom.xml actual no está definida la propiedad 'es.segsocial.infra.version'. Se debe parar la ejecución.")
		}

		if(versionwas.isEmpty()){
			throw new NullPointerException("Para compilar una aplicación es necesario definir en el pom la versión de jarGeneral, de infraestructura y de WAS.En el pom.xml actual no está definida la propiedad 'was.version'. Se debe parar la ejecución.")
		}

	}
	
	def initArtefactos(confGlobal) {
		def rutaBaseVersion = "${confGlobal.dirResultado}/${versionWAS}/Ejecucion/Aplicaciones/${codAplicacion}/${version}"
		
		def fu = new FileUtils()
		def datArt = fu.getArtefactosZipEar(rutaBaseVersion)
		
		zipApp.directorio = "${rutaBaseVersion}"
		zipApp.rutaGenerado = "${zipApp.directorio}/${datArt.zip}"
		zipApp.artifactId = "${artifactId}zip"
		zipApp.groupId = "${artifactGroupId}"		

		earApp.directorio = "${rutaBaseVersion}"
		earApp.rutaGenerado = "${earApp.directorio}/${datArt.ear}"
		earApp.artifactId = "${artifactId}ear"
		earApp.groupId = "${artifactGroupId}"

	}

	def mostrar() {
		def val="ConfiguracionAplicacion"
		def resul = "###################################### ${val} ######################################\n" + 
			"----- ${val}.version: ${version}\n" +
			"----- ${val}.codAplicacion: ${codAplicacion}\n" +
			"----- ${val}.urlClone: ${urlClone}\n" +
			"----- ${val}.dirCheckoutAplicaciones: ${dirCheckoutAplicaciones}\n" +
			"----- ${val}.versionJarGeneral: ${versionJarGeneral}\n" +
			"----- ${val}.versionInfraestructura: ${versionInfraestructura}\n" +
			"----- ${val}.groupId: ${artifactGroupId}\n" +
			"----- ${val}.artifactId: ${artifactId}\n" +
			"----- ${val}.version: ${version}\n"

		return resul
	}
	*/

	def mostrar() {
		def val="ConfiguracionAplicacion"
		def resul = "###################################### ${val} ######################################\n" + 
			"----- ${val}.codAplicacion: ${codAplicacion}\n" +
			"----- ${val}.urlClone: ${urlClone}\n"

		return resul
	}
}

