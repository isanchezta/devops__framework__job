package es.segsocial.prosa

class ConfiguracionAppsComunes {
	String version
	String codAplicacion
	
	String rutaBaseVersion
	
	String versionJarGeneral
	String versionInfraestructura
	String appComunesVersiones

	String urlCloneAplicaciones
	String dirCheckoutAplicaciones
	
	String artifactId
	String artifactGroupId

	String versionwas
	String versionWAS

	def arrArtifacts = []
	
	ConfiguracionAppsComunes(codApp, confGlobal, confGit, rutaRepoGit) {
		codAplicacion = codApp
		
		urlCloneAplicaciones="${confGit.urlGitRemote}/${confGit.usuarioGit}/${rutaRepoGit}" //giss-ifca.git, giss-frpr.git
		dirCheckoutAplicaciones="${confGlobal.dirCheckout}/aplicaciones"
		
	}

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
			throw new NullPointerException("Para compilar una aplicación común es necesario definir en el pom la versión de jarGeneral, de infraestructura y de WAS. En el pom.xml actual no está definida la propiedad 'es.segsocial.jargeneral.version'. Se debe parar la ejecución.")
		}

		if(versionInfraestructura.isEmpty()){
			throw new NullPointerException("Para compilar una aplicación común es necesario definir en el pom la versión de jarGeneral, de infraestructura y de WAS. En el pom.xml actual no está definida la propiedad 'es.segsocial.infra.version'. Se debe parar la ejecución.")
		}

		if(versionwas.isEmpty()){
			throw new NullPointerException("Para compilar una aplicación común es necesario definir en el pom la versión de jarGeneral, de infraestructura y de WAS.En el pom.xml actual no está definida la propiedad 'was.version'. Se debe parar la ejecución.")
		}
		
		//initArtefactos(confGlobal)
	}
	
	def trataListaGenerados(generados, rutaBase, sufijoGroupId="") {
		for(fich in generados) {
			def artefacto = new DatosArtsGenerados()
			artefacto.directorio = "${rutaBase}"
			artefacto.rutaGenerado = "${artefacto.directorio}/${fich.nombre}"
			artefacto.artifactId = "${artifactId}-${fich.extension}"
			artefacto.groupId = "${artifactGroupId}${sufijoGroupId}"
			artefacto.version=version
			artefacto.classifier="${fich.extension}"
			artefacto.extension="${fich.extension}"
			artefacto.packaging="${fich.extension}"
			arrArtifacts << artefacto.getDatosSubidaNexus()
		}
	}
	
	def initArtefactos(confGlobal) {
		def rutaBaseVersionEmpa = "${confGlobal.dirResultado}/${versionWAS}/Empaquetado/Aplicaciones/${codAplicacion}/${version}"
		def rutaBaseVersionDesa = "${confGlobal.dirResultado}/${versionWAS}/Desarrollo/Aplicaciones/${codAplicacion}/${version}"
		
		def extensiones = ["jar", "war"]
		
		def fu = new FileUtils()
		def generadosEmpa = fu.getArtefactosByExtension(rutaBaseVersionEmpa, extensiones)
		def generadosDesa = fu.getArtefactosByExtension(rutaBaseVersionDesa, extensiones)
		
		trataListaGenerados(generadosEmpa, rutaBaseVersionEmpa)
		trataListaGenerados(generadosDesa, rutaBaseVersionDesa, ".desa")

	}
	
	def getArrAtifacts() {
		return arrArtifacts
	}

	def mostrar() {
		def val="ConfiguracionAplicacion"
		def resul = "###################################### ${val} ######################################\n" + 
			"----- ${val}.version: ${version}\n" +
			"----- ${val}.codAplicacion: ${codAplicacion}\n" +
			"----- ${val}.urlCloneAplicaciones: ${urlCloneAplicaciones}\n" +
			"----- ${val}.dirCheckoutAplicaciones: ${dirCheckoutAplicaciones}\n" +
			"----- ${val}.versionJarGeneral: ${versionJarGeneral}\n" +
			"----- ${val}.versionInfraestructura: ${versionInfraestructura}\n" +
			"----- ${val}.groupId: ${artifactGroupId}\n" +
			"----- ${val}.artifactId: ${artifactId}\n" +
			"----- ${val}.version: ${version}\n"

		return resul
	}
}

