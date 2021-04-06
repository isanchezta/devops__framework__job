package es.segsocial.prosa

class ConfiguracionGlobal {

	String urlCloneAntscripts
	
	String urlCloneInfraestructura
	String dirCheckoutInfra

    String codAplicacion
	String baseTrabajo="/tmp"
	String dirCheckout
	String dirCheckoutAntScripts
	String dirResultado
	
	String localizaciones
	
	// ######## Estan definidos en dos lugares, en los ficheros XML para las tareas ANT y aqui. ########
	String baseTemporalSufijo = "tmpWas_liberacion_periodica_parche"
	String temporalSufijo = baseTemporalSufijo
	String dirInformes
	String dirTrabajo
	
	// Variables para definir el directorio de los resultados de los tests y el fichero en si
	String dirTestJenkins = "target/reportsTests"
	String fichTestJenkins
	String fichSalidaCompilacion
	String liquibase = "/pocgiss/Liquibase/liquibase-4.2.2"
	
	def propsSalidaCompilacion
	
	def confGit
	
	ConfiguracionGlobal(codApp, confGit) {
		codAplicacion = codApp
		this.confGit = confGit

		urlCloneAntscripts="${confGit.urlCloneAnstscripts}"

	    dirCheckout="${baseTrabajo}/WorkspaceGiss/ws"
	    dirCheckoutAntScripts="${dirCheckout}/antscripts"
	    dirResultado="${baseTrabajo}/pocgissResultados"
		
		fichTestJenkins = "${dirTestJenkins}/ResulTest_${codAplicacion}.xml"
		
		urlCloneInfraestructura="${confGit.urlCloneInfraestructura}"
		dirCheckoutInfra="${dirCheckout}/infraestructura"

		dirInformes = "${baseTrabajo}/ProsaAnt${temporalSufijo}_${codAplicacion}_test/informes"
		dirTrabajo = "${baseTrabajo}/ProsaAnt${temporalSufijo}_${codAplicacion}/Trabajo"
		fichSalidaCompilacion = "${dirTrabajo}/salidaCompilacion.properties"
	}
	
	def mostrar() {
		def resul = "###################################### ConfiguracionGlobal ######################################\n" + 
			"----- ConfiguracionGlobal.codAplicacion: ${codAplicacion}\n" +
			"----- ConfiguracionGlobal.urlCloneAntscripts: ${urlCloneAntscripts}\n" +
			"----- ConfiguracionGlobal.baseTrabajo: ${baseTrabajo}\n" +
			"----- ConfiguracionGlobal.dirCheckout: ${dirCheckout}\n" +
			"----- ConfiguracionGlobal.dirCheckoutAntScripts: ${dirCheckoutAntScripts}\n" +
			"----- ConfiguracionGlobal.dirResultado: ${dirResultado}\n" +
			"----- ConfiguracionGlobal.temporalSufijo: ${temporalSufijo}\n" +
			"----- ConfiguracionGlobal.dirInformes: ${dirInformes}\n" +
			"----- ConfiguracionGlobal.dirTestJenkins: ${dirTestJenkins}\n" +
			"----- ConfiguracionGlobal.fichTestJenkins: ${fichTestJenkins}\n"
		return resul
	}
	
	def initLocalizacionesInfra() {
		localizaciones=" -w ${dirCheckoutInfra} -s ${dirCheckoutAntScripts}" 
	}
	
	def initLocalizacionesApp(conf) {
		localizaciones=" -w ${conf.dirCheckoutAplicaciones} -s ${dirCheckoutAntScripts}"
	}
	
	def leerFicheroSalidaCompilacion() {
		def fu = new FileUtils()
		propsSalidaCompilacion = fu.leerFicheroPropiedades(fichSalidaCompilacion)
	}
	
	def leerPropiedadFicheroSalidaCompilacion(prop) {
		return propsSalidaCompilacion.getProperty(prop)
	}

}

