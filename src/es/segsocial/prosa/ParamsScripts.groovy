package es.segsocial.prosa

class ParamsScripts {
	// Variables para pasar a los scripts .sh, versiones generadas y utilizadas
	String genJarGeneral = ""
	String usoJarGeneral = ""
	
	String genCarpetasSpm = ""

	String genInfraestructura = ""
	String usoInfraestructura = ""

	String genAplicacion = ""
	String codAplicacion = ""

	String usoVersionWAS = ""
	
	String usoAppComunesVersiones = ""

	def initParamsJarGeneral(versJarGeneral) {
		genJarGeneral = "-v ${versJarGeneral}"
		usoJarGeneral = "-g ${versJarGeneral}"
	}

	def initParamsSpm(confSpm) {
		initParamsJarGeneral(confSpm.versionJarGeneral)
		
		genAplicacion = "-v ${confSpm.version}"
		genCarpetasSpm = " -k ${confSpm.carpetaScript}	-d ${confSpm.carpetaDestino}"
	}

	def initParamsInfra(versInfra) {
		genInfraestructura = "-v ${versInfra}"
		usoInfraestructura = "-i ${versInfra}"
	}

	def initParamsGeneralInfra(versJarGeneral, versInfra) {
		initParamsJarGeneral(versJarGeneral)
		
		initParamsInfra(versInfra)
	}

	def initParamsApp(versJarGeneral, versInfra, codApp, versApp, appComunesVersiones) {
		initParamsGeneralInfra(versJarGeneral, versInfra)
		
		genAplicacion = "-v ${versApp}"
		codAplicacion = "-c ${codApp}"
		
		if(appComunesVersiones?.trim()) {
			usoAppComunesVersiones = "-f ${appComunesVersiones}"
		}
	}
	
	def initVersionWAS(versionWAS) {
		this.usoVersionWAS = "-e ${versionWAS}"
	}

	def mostrar() {

		def val="ParamsScripts"
		def resul = "###################################### ${val} ######################################\n" + 
			"----- ${val}.genJarGeneral: ${genJarGeneral}\n" +
			"----- ${val}.usoJarGeneral: ${usoJarGeneral}\n" +
			"----- ${val}.genInfraestructura: ${genInfraestructura}\n" +
			"----- ${val}.usoInfraestructura: ${usoInfraestructura}\n" +
			"----- ${val}.genAplicacion: ${genAplicacion}\n" +
			"----- ${val}.codAplicacion: ${codAplicacion}\n" +
			"----- ${val}.genCarpetasSpm: ${genCarpetasSpm}\n"

		return resul
	}
}


