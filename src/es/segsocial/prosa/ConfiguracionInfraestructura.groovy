package es.segsocial.prosa

class ConfiguracionInfraestructura {
	String version

	String versionJarGeneral
	String versionwas
	String versionWAS
	
	String rutaBaseVersion

	// Nombres de los artefactos generados tras la compilacion
	String artInfraProsaGen="PROS00InfraProsa"
	String artInfraProsaUtil="${artInfraProsaGen}Util"
	String artInfraProsaPrivado="${artInfraProsaUtil}Privado"
	String artInfraProsaEJB="${artInfraProsaGen}EJB"
	String artInfraProsaWeb="${artInfraProsaGen}Web"
	
	// Artifacts ID para nexus
	String artifactInfraProsaUtilId=artInfraProsaUtil.toLowerCase()
	String artifactInfraProsaUtilPrivadoId=artInfraProsaPrivado.toLowerCase()
	String artifactInfraProsaEjb=artInfraProsaEJB.toLowerCase()
	String artifactInfraProsaWeb=artInfraProsaWeb.toLowerCase()
	
	// Groups ID para nexus
	String artifactGroupIdInfra
	String artifactGroupIdInfraWs
	String artifactGroupIdInfraOnline
	String artifactGroupIdInfraBatch
	
	String artifactGroupIdInfraDesa
	String artifactGroupIdInfraWsDesa
	String artifactGroupIdInfraOnlineDesa
	String artifactGroupIdInfraBatchDesa

	// Artefacto al fichero pom
	DatosArtsGenerados pomInfraProsa = new DatosArtsGenerados()
	
	// Datos a los ficheros generados
	// Empaquetados
	DatosArtsGenerados jarInfraProsaUtil = new DatosArtsGenerados()
	DatosArtsGenerados jarInfraProsaWsUtil = new DatosArtsGenerados()
	DatosArtsGenerados jarInfraProsaPrivado = new DatosArtsGenerados()
	DatosArtsGenerados jarInfraProsaOnlineEjb = new DatosArtsGenerados()
	DatosArtsGenerados jarInfraProsaBatchEjb = new DatosArtsGenerados()
	DatosArtsGenerados jarInfraProsaWsEjb = new DatosArtsGenerados()
	DatosArtsGenerados warInfraProsaOnlineWeb = new DatosArtsGenerados()
	DatosArtsGenerados warInfraProsaBatchWeb = new DatosArtsGenerados()
	DatosArtsGenerados warInfraProsaWsWeb = new DatosArtsGenerados()
	// Desarrollo
	DatosArtsGenerados jarInfraProsaUtilDesarrollo = new DatosArtsGenerados()
	DatosArtsGenerados jarInfraProsaPrivadoDesarrollo = new DatosArtsGenerados()
	DatosArtsGenerados jarInfraProsaEjbDesarrollo = new DatosArtsGenerados()
	DatosArtsGenerados warInfraProsaOnlineWebDesarrollo = new DatosArtsGenerados()
	DatosArtsGenerados warInfraProsaBatchWebDesarrollo = new DatosArtsGenerados()
	DatosArtsGenerados warInfraProsaWsWebDesarrollo = new DatosArtsGenerados()
	
	// XML generados empaquetados
	DatosArtsGenerados xmlAppnegOnlineWarIbmWebExt = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegOnlineWarWeb = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegOnlineWarIbmWebBnd = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegOnlineOnlineEjbEjbJar = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegOnlineOnlineEjbIbmEjbJarExt = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegWsWarHandlerChain = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegWsWarIbmWebExt = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegWsWarWeb = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegWsWarIbmWebBnd = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegWsWarBeans = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegWsEjbIbmEjbJarBnd = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegWsEjbEjbJar = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegWsEjbIbmEjbJarExt = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegDescriptoresEarDeployment = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegBatchWarIbmEjbJarExt = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegBatchWarWeb = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegBatchWarIbmEjbJarBnd = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegBatchEjbIbmEjbJarExt = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegBatchEjbWeb = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegBatchEjbIbmEjbJarBnd = new DatosArtsGenerados()
	DatosArtsGenerados xmlVersionesLibreriasJarsExternos = new DatosArtsGenerados()
	DatosArtsGenerados xmlApplication = new DatosArtsGenerados()
	DatosArtsGenerados xmlFicherosRenombrarDesa = new DatosArtsGenerados()
	// XML generados desarrollo
	DatosArtsGenerados xmlAppnegDesarrolloIbmWebExt = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegDesarrolloWeb = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegDesarrolloPortal8Web = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegDesarrolloPortalWeb = new DatosArtsGenerados()
	DatosArtsGenerados xmlAppnegDesarrolloVipWeb = new DatosArtsGenerados()

	def initPom(confGlobal) {
		String file = "${confGlobal.dirCheckoutInfra}/PROS00InfraestructuraProsa/pom.xml"

		PomUtil pu = new PomUtil()
		def dp = pu.consultaVersiones(file)

		version = dp.version
	    artifactGroupIdInfra=dp.groupId

		def doc = pu.getDocument(file)
		versionJarGeneral = doc.properties.getProperty("es.segsocial.jargeneral.version")
		versionwas = doc.properties.getProperty("was.version")
		versionWAS = versionwas.toUpperCase()

		if(versionJarGeneral.isEmpty()){
			throw new NullPointerException("Para compilar infraestructura es necesario definir en el pom la versión de jarGeneral y de WAS. En el pom.xml actual no está definida la propiedad 'es.segsocial.jargeneral.version'. Se debe parar la ejecución.")
		}

		if(versionwas.isEmpty()){
			throw new NullPointerException("Para compilar infraestructura es necesario definir en el pom la versión de jarGeneral y de WAS. En el pom.xml actual no está definida la propiedad 'was.version'. Se debe parar la ejecución.")
		}

		if(!artifactGroupIdInfra.contains(versionwas)){
			throw new NullPointerException("La versión de was debe coincidir con la que contiene el groupId.")
		}

	    //artifactJarGeneralId=dp.artifactId
		
		initArtefactos(confGlobal)
	}
	
	def initArtefactos(confGlobal) {
		artifactGroupIdInfraWs="${artifactGroupIdInfra}.ws"
		artifactGroupIdInfraOnline="${artifactGroupIdInfra}.online"
		artifactGroupIdInfraBatch="${artifactGroupIdInfra}.batch"
		
		artifactGroupIdInfraDesa="${artifactGroupIdInfra}.desa"
		artifactGroupIdInfraWsDesa="${artifactGroupIdInfraDesa}.ws"
		artifactGroupIdInfraOnlineDesa="${artifactGroupIdInfraDesa}.online"
		artifactGroupIdInfraBatchDesa="${artifactGroupIdInfraDesa}.batch"
		
		def groupIdDesc = "${artifactGroupIdInfra}.desc"
		def groupIdAppneg = "${artifactGroupIdInfra}.appneg"

		//montarDatosGenerados(confGlobal)
		rutaBaseVersion = "${confGlobal.dirResultado}/${versionWAS}/Empaquetado/Infraestructura/${version}"
		def rutaWs = "${rutaBaseVersion}/WS"
		def rutaOnline = "${rutaBaseVersion}/Online"
		def rutaBatch = "${rutaBaseVersion}/Batch"
		
		def rutaBaseDesa = "${confGlobal.dirResultado}/${versionWAS}/Desarrollo/Infraestructura/${version}"
		def rutaWsDesa = "${rutaBaseDesa}/WS"
		def rutaOnlineDesa = "${rutaBaseDesa}/Online"
		def rutaBatchDesa = "${rutaBaseDesa}/Batch"
		
		pomInfraProsa.directorio = confGlobal.dirCheckoutInfra
		pomInfraProsa.rutaGenerado = "${pomInfraProsa.directorio}/PROS00InfraestructuraProsa/pom-infra.xml"
		PomUtil pu = new PomUtil()
		def dp = pu.consultaVersiones(pomInfraProsa.rutaGenerado)
		pomInfraProsa.artifactId = "${dp.artifactId}"
		pomInfraProsa.groupId = "${dp.groupId}"
		pomInfraProsa.extension = "pom"
	
		jarInfraProsaUtil.directorio = rutaBaseVersion
		jarInfraProsaUtil.rutaGenerado = "${jarInfraProsaUtil.directorio}/${artInfraProsaUtil}.jar"
		jarInfraProsaUtil.artifactId = "${artifactInfraProsaUtilId}"
		jarInfraProsaUtil.groupId = "${artifactGroupIdInfra}"
		
		jarInfraProsaWsUtil.directorio = rutaWs
		jarInfraProsaWsUtil.rutaGenerado = "${jarInfraProsaWsUtil.directorio}/${artInfraProsaUtil}.jar"
		jarInfraProsaWsUtil.artifactId = "${artifactInfraProsaUtilId}"
		jarInfraProsaWsUtil.groupId = "${artifactGroupIdInfraWs}"
		
		jarInfraProsaPrivado.directorio = rutaBaseVersion
		jarInfraProsaPrivado.rutaGenerado = "${jarInfraProsaPrivado.directorio}/${artInfraProsaPrivado}.jar"
		jarInfraProsaPrivado.artifactId = "${artifactInfraProsaUtilPrivadoId}"
		jarInfraProsaPrivado.groupId = "${artifactGroupIdInfra}"
		
		jarInfraProsaOnlineEjb.directorio = rutaOnline
		jarInfraProsaOnlineEjb.rutaGenerado = "${jarInfraProsaOnlineEjb.directorio}/${artInfraProsaEJB}.jar"
		jarInfraProsaOnlineEjb.artifactId = "${artifactInfraProsaEjb}"
		jarInfraProsaOnlineEjb.groupId = "${artifactGroupIdInfraOnline}"
		
		jarInfraProsaBatchEjb.directorio = rutaBatch
		jarInfraProsaBatchEjb.rutaGenerado = "${jarInfraProsaBatchEjb.directorio}/${artInfraProsaEJB}.jar"
		jarInfraProsaBatchEjb.artifactId = "${artifactInfraProsaEjb}"
		jarInfraProsaBatchEjb.groupId = "${artifactGroupIdInfraBatch}"
		
		jarInfraProsaWsEjb.directorio = rutaWs
		jarInfraProsaWsEjb.rutaGenerado = "${jarInfraProsaWsEjb.directorio}/${artInfraProsaEJB}.jar"
		jarInfraProsaWsEjb.artifactId = "${artifactInfraProsaEjb}"
		jarInfraProsaWsEjb.groupId = "${artifactGroupIdInfraWs}"
		
		warInfraProsaOnlineWeb.directorio = rutaOnline
		warInfraProsaOnlineWeb.rutaGenerado = "${warInfraProsaOnlineWeb.directorio}/${artInfraProsaWeb}.war"
		warInfraProsaOnlineWeb.artifactId = "${artifactInfraProsaWeb}"
		warInfraProsaOnlineWeb.groupId = "${artifactGroupIdInfraOnline}"
		
		warInfraProsaBatchWeb.directorio = rutaBatch
		warInfraProsaBatchWeb.rutaGenerado = "${warInfraProsaBatchWeb.directorio}/${artInfraProsaWeb}.war"
		warInfraProsaBatchWeb.artifactId = "${artifactInfraProsaWeb}"
		warInfraProsaBatchWeb.groupId = "${artifactGroupIdInfraBatch}"
		
		warInfraProsaWsWeb.directorio = rutaWs
		warInfraProsaWsWeb.rutaGenerado = "${warInfraProsaWsWeb.directorio}/${artInfraProsaWeb}.war"
		warInfraProsaWsWeb.artifactId = "${artifactInfraProsaWeb}"
		warInfraProsaWsWeb.groupId = "${artifactGroupIdInfraWs}"
		// Desarrollo
		jarInfraProsaUtilDesarrollo.directorio = rutaBaseDesa
		jarInfraProsaUtilDesarrollo.rutaGenerado = "${jarInfraProsaUtilDesarrollo.directorio}/${artInfraProsaUtil}.jar"
		jarInfraProsaUtilDesarrollo.artifactId = "${artifactInfraProsaUtilId}"
		jarInfraProsaUtilDesarrollo.groupId = "${artifactGroupIdInfraDesa}"

		jarInfraProsaPrivadoDesarrollo.directorio = rutaBaseDesa
		jarInfraProsaPrivadoDesarrollo.rutaGenerado = "${jarInfraProsaPrivadoDesarrollo.directorio}/${artInfraProsaPrivado}.jar"
		jarInfraProsaPrivadoDesarrollo.artifactId = "${artifactInfraProsaUtilPrivadoId}"
		jarInfraProsaPrivadoDesarrollo.groupId = "${artifactGroupIdInfraDesa}"
		
		jarInfraProsaEjbDesarrollo.directorio = rutaBaseDesa
		jarInfraProsaEjbDesarrollo.rutaGenerado = "${jarInfraProsaEjbDesarrollo.directorio}/${artInfraProsaEJB}.jar"
		jarInfraProsaEjbDesarrollo.artifactId = "${artifactInfraProsaEjb}"
		jarInfraProsaEjbDesarrollo.groupId = "${artifactGroupIdInfraDesa}"
		
		warInfraProsaOnlineWebDesarrollo.directorio = rutaOnlineDesa
		warInfraProsaOnlineWebDesarrollo.rutaGenerado = "${warInfraProsaOnlineWebDesarrollo.directorio}/${artInfraProsaWeb}.war"
		warInfraProsaOnlineWebDesarrollo.artifactId = "${artifactInfraProsaWeb}"
		warInfraProsaOnlineWebDesarrollo.groupId = "${artifactGroupIdInfraOnlineDesa}"
		
		warInfraProsaBatchWebDesarrollo.directorio = rutaBatchDesa
		warInfraProsaBatchWebDesarrollo.rutaGenerado = "${warInfraProsaBatchWebDesarrollo.directorio}/${artInfraProsaWeb}.war"
		warInfraProsaBatchWebDesarrollo.artifactId = "${artifactInfraProsaWeb}"
		warInfraProsaBatchWebDesarrollo.groupId = "${artifactGroupIdInfraBatchDesa}"
		
		warInfraProsaWsWebDesarrollo.directorio = rutaWsDesa
		warInfraProsaWsWebDesarrollo.rutaGenerado = "${warInfraProsaWsWebDesarrollo.directorio}/${artInfraProsaWeb}.war"
		warInfraProsaWsWebDesarrollo.artifactId = "${artifactInfraProsaWeb}"
		warInfraProsaWsWebDesarrollo.groupId = "${artifactGroupIdInfraWsDesa}"
		
		// XML generados empaquetados
		xmlAppnegOnlineWarIbmWebExt.directorio = "${rutaOnline}/war"
		xmlAppnegOnlineWarIbmWebExt.rutaGenerado = "${xmlAppnegOnlineWarIbmWebExt.directorio}/ibm-web-ext.xml"
		xmlAppnegOnlineWarIbmWebExt.artifactId = "ibm-web-ext"
		xmlAppnegOnlineWarIbmWebExt.groupId = "${groupIdAppneg}.online.war"
		xmlAppnegOnlineWarWeb.directorio = "${rutaOnline}/war"
		xmlAppnegOnlineWarWeb.rutaGenerado = "${xmlAppnegOnlineWarWeb.directorio}/web.xml"
		xmlAppnegOnlineWarWeb.artifactId = "web"
		xmlAppnegOnlineWarWeb.groupId = "${groupIdAppneg}.online.war"
		xmlAppnegOnlineWarIbmWebBnd.directorio = "${rutaOnline}/war"
		xmlAppnegOnlineWarIbmWebBnd.rutaGenerado = "${xmlAppnegOnlineWarIbmWebBnd.directorio}/ibm-web-bnd.xml"
		xmlAppnegOnlineWarIbmWebBnd.artifactId = "ibm-web-bnd"
		xmlAppnegOnlineWarIbmWebBnd.groupId = "${groupIdAppneg}.online.war"

		xmlAppnegOnlineOnlineEjbEjbJar.directorio = "${rutaOnline}/ejb"
		xmlAppnegOnlineOnlineEjbEjbJar.rutaGenerado = "${xmlAppnegOnlineOnlineEjbEjbJar.directorio}/ejb-jar.xml"
		xmlAppnegOnlineOnlineEjbEjbJar.artifactId = "ejb-jar"
		xmlAppnegOnlineOnlineEjbEjbJar.groupId = "${groupIdAppneg}.online.ejb"
		xmlAppnegOnlineOnlineEjbIbmEjbJarExt.directorio = "${rutaOnline}/ejb"
		xmlAppnegOnlineOnlineEjbIbmEjbJarExt.rutaGenerado = "${xmlAppnegOnlineOnlineEjbIbmEjbJarExt.directorio}/ibm-ejb-jar-ext.xml"
		xmlAppnegOnlineOnlineEjbIbmEjbJarExt.artifactId = "ibm-ejb-jar-ext"
		xmlAppnegOnlineOnlineEjbIbmEjbJarExt.groupId = "${groupIdAppneg}.online.ejb"

		xmlAppnegWsWarHandlerChain.directorio = "${rutaWs}/war"
		xmlAppnegWsWarHandlerChain.rutaGenerado = "${xmlAppnegWsWarHandlerChain.directorio}/handler-chain.xml"
		xmlAppnegWsWarHandlerChain.artifactId = "handler-chain"
		xmlAppnegWsWarHandlerChain.groupId = "${groupIdAppneg}.ws.war"
		xmlAppnegWsWarIbmWebExt.directorio = "${rutaWs}/war"
		xmlAppnegWsWarIbmWebExt.rutaGenerado = "${xmlAppnegWsWarIbmWebExt.directorio}/ibm-web-ext.xml"
		xmlAppnegWsWarIbmWebExt.artifactId = "ibm-web-ext"
		xmlAppnegWsWarIbmWebExt.groupId = "${groupIdAppneg}.ws.war"
		xmlAppnegWsWarWeb.directorio = "${rutaWs}/war"
		xmlAppnegWsWarWeb.rutaGenerado = "${xmlAppnegWsWarWeb.directorio}/web.xml"
		xmlAppnegWsWarWeb.artifactId = "web"
		xmlAppnegWsWarWeb.groupId = "${groupIdAppneg}.ws.war"
		xmlAppnegWsWarIbmWebBnd.directorio = "${rutaWs}/war"
		xmlAppnegWsWarIbmWebBnd.rutaGenerado = "${xmlAppnegWsWarIbmWebBnd.directorio}/ibm-web-bnd.xml"
		xmlAppnegWsWarIbmWebBnd.artifactId = "ibm-web-bnd"
		xmlAppnegWsWarIbmWebBnd.groupId = "${groupIdAppneg}.ws.war"
		xmlAppnegWsWarBeans.directorio = "${rutaWs}/war"
		xmlAppnegWsWarBeans.rutaGenerado = "${xmlAppnegWsWarBeans.directorio}/beans.xml"
		xmlAppnegWsWarBeans.artifactId = "beans"
		xmlAppnegWsWarBeans.groupId = "${groupIdAppneg}.ws.war"

		xmlAppnegWsEjbIbmEjbJarBnd.directorio = "${rutaWs}/ejb"
		xmlAppnegWsEjbIbmEjbJarBnd.rutaGenerado = "${xmlAppnegWsEjbIbmEjbJarBnd.directorio}/ibm-ejb-jar-bnd.xml"
		xmlAppnegWsEjbIbmEjbJarBnd.artifactId = "ibm-ejb-jar-bnd"
		xmlAppnegWsEjbIbmEjbJarBnd.groupId = "${groupIdAppneg}.ws.ejb"
		xmlAppnegWsEjbEjbJar.directorio = "${rutaWs}/ejb"
		xmlAppnegWsEjbEjbJar.rutaGenerado = "${xmlAppnegWsEjbEjbJar.directorio}/ejb-jar.xml"
		xmlAppnegWsEjbEjbJar.artifactId = "ejb-jar"
		xmlAppnegWsEjbEjbJar.groupId = "${groupIdAppneg}.ws.ejb"
		xmlAppnegWsEjbIbmEjbJarExt.directorio = "${rutaWs}/ejb"
		xmlAppnegWsEjbIbmEjbJarExt.rutaGenerado = "${xmlAppnegWsEjbIbmEjbJarExt.directorio}/ibm-ejb-jar-ext.xml"
		xmlAppnegWsEjbIbmEjbJarExt.artifactId = "ibm-ejb-jar-ext"
		xmlAppnegWsEjbIbmEjbJarExt.groupId = "${groupIdAppneg}.ws.ejb"

		xmlAppnegDescriptoresEarDeployment.directorio = "${rutaBaseVersion}/DescriptoresEar"
		xmlAppnegDescriptoresEarDeployment.rutaGenerado = "${xmlAppnegDescriptoresEarDeployment.directorio}/deployment.xml"
		xmlAppnegDescriptoresEarDeployment.artifactId = "deployment"
		xmlAppnegDescriptoresEarDeployment.groupId = "${groupIdAppneg}.descriptoresear"

		xmlAppnegBatchWarIbmEjbJarExt.directorio = "${rutaBatch}/war"
		xmlAppnegBatchWarIbmEjbJarExt.rutaGenerado = "${xmlAppnegBatchWarIbmEjbJarExt.directorio}/ibm-web-ext.xml"
		xmlAppnegBatchWarIbmEjbJarExt.artifactId = "ibm-web-ext"
		xmlAppnegBatchWarIbmEjbJarExt.groupId = "${groupIdAppneg}.batch.war"
		xmlAppnegBatchWarWeb.directorio = "${rutaBatch}/war"
		xmlAppnegBatchWarWeb.rutaGenerado = "${xmlAppnegBatchWarWeb.directorio}/web.xml"
		xmlAppnegBatchWarWeb.artifactId = "web"
		xmlAppnegBatchWarWeb.groupId = "${groupIdAppneg}.batch.war"
		xmlAppnegBatchWarIbmEjbJarBnd.directorio = "${rutaBatch}/war"
		xmlAppnegBatchWarIbmEjbJarBnd.rutaGenerado = "${xmlAppnegBatchWarIbmEjbJarBnd.directorio}/ibm-web-bnd.xml"
		xmlAppnegBatchWarIbmEjbJarBnd.artifactId = "ibm-web-bnd"
		xmlAppnegBatchWarIbmEjbJarBnd.groupId = "${groupIdAppneg}.batch.war"

		xmlAppnegBatchEjbIbmEjbJarExt.directorio = "${rutaBatch}/ejb"
		xmlAppnegBatchEjbIbmEjbJarExt.rutaGenerado = "${xmlAppnegBatchEjbIbmEjbJarExt.directorio}/ibm-ejb-jar-bnd.xml"
		xmlAppnegBatchEjbIbmEjbJarExt.artifactId = "ibm-ejb-jar-bnd"
		xmlAppnegBatchEjbIbmEjbJarExt.groupId = "${groupIdAppneg}.batch.ejb"
		xmlAppnegBatchEjbWeb.directorio = "${rutaBatch}/ejb"
		xmlAppnegBatchEjbWeb.rutaGenerado = "${xmlAppnegBatchEjbWeb.directorio}/ejb-jar.xml"
		xmlAppnegBatchEjbWeb.artifactId = "ejb-jar"
		xmlAppnegBatchEjbWeb.groupId = "${groupIdAppneg}.batch.ejb"
		xmlAppnegBatchEjbIbmEjbJarBnd.directorio = "${rutaBatch}/ejb"
		xmlAppnegBatchEjbIbmEjbJarBnd.rutaGenerado = "${xmlAppnegBatchEjbIbmEjbJarBnd.directorio}/ibm-ejb-jar-ext.xml"
		xmlAppnegBatchEjbIbmEjbJarBnd.artifactId = "ibm-ejb-jar-ext"
		xmlAppnegBatchEjbIbmEjbJarBnd.groupId = "${groupIdAppneg}.batch.ejb"

		xmlVersionesLibreriasJarsExternos.directorio = "${rutaBaseVersion}"
		xmlVersionesLibreriasJarsExternos.rutaGenerado = "${xmlVersionesLibreriasJarsExternos.directorio}/versiones_librerias_JarsExternos.xml"
		xmlVersionesLibreriasJarsExternos.artifactId = "versiones_librerias_jarsexternos"
		xmlVersionesLibreriasJarsExternos.groupId = "${groupIdDesc}.ear"
		xmlApplication.directorio = "${rutaBaseVersion}"
		xmlApplication.rutaGenerado = "${xmlApplication.directorio}/application.xml"
		xmlApplication.artifactId = "application"
		xmlApplication.groupId = "${groupIdDesc}.ear"
		xmlFicherosRenombrarDesa.directorio = "${rutaBaseVersion}"
		xmlFicherosRenombrarDesa.rutaGenerado = "${xmlFicherosRenombrarDesa.directorio}/ficherosRenombrarDesa.xml"
		xmlFicherosRenombrarDesa.artifactId = "ficherosrenombrardesa"
		xmlFicherosRenombrarDesa.groupId = "${groupIdDesc}.ear"

		// XML generados desarrollo
		xmlAppnegDesarrolloIbmWebExt.directorio = "${rutaBaseVersion}/DescriptoresWarAppNegDesarrollo"
		xmlAppnegDesarrolloIbmWebExt.rutaGenerado = "${xmlAppnegDesarrolloIbmWebExt.directorio}/ibm-web-ext.xml"
		xmlAppnegDesarrolloIbmWebExt.artifactId = "ibm-web-ext"
		xmlAppnegDesarrolloIbmWebExt.groupId = "${groupIdDesc}.ear.desa"
		xmlAppnegDesarrolloWeb.directorio = "${rutaBaseVersion}/DescriptoresWarAppNegDesarrollo"
		xmlAppnegDesarrolloWeb.rutaGenerado = "${xmlAppnegDesarrolloWeb.directorio}/web.xml"
		xmlAppnegDesarrolloWeb.artifactId = "web"
		xmlAppnegDesarrolloWeb.groupId = "${groupIdDesc}.ear.desa"
		xmlAppnegDesarrolloPortal8Web.directorio = "${rutaBaseVersion}/DescriptoresWarAppNegDesarrollo/Portal8"
		xmlAppnegDesarrolloPortal8Web.rutaGenerado = "${xmlAppnegDesarrolloPortal8Web.directorio}/web.xml"
		xmlAppnegDesarrolloPortal8Web.artifactId = "web"
		xmlAppnegDesarrolloPortal8Web.groupId = "${groupIdDesc}.ear.portal8.desa"
		xmlAppnegDesarrolloPortalWeb.directorio = "${rutaBaseVersion}/DescriptoresWarAppNegDesarrollo/portal"
		xmlAppnegDesarrolloPortalWeb.rutaGenerado = "${xmlAppnegDesarrolloPortalWeb.directorio}/web.xml"
		xmlAppnegDesarrolloPortalWeb.artifactId = "web"
		xmlAppnegDesarrolloPortalWeb.groupId = "${groupIdDesc}.ear.portal.desa"
		xmlAppnegDesarrolloVipWeb.directorio = "${rutaBaseVersion}/DescriptoresWarAppNegDesarrollo/Vip"
		xmlAppnegDesarrolloVipWeb.rutaGenerado = "${xmlAppnegDesarrolloVipWeb.directorio}/web.xml"
		xmlAppnegDesarrolloVipWeb.artifactId = "web"
		xmlAppnegDesarrolloVipWeb.groupId = "${groupIdDesc}.ear.vip.desa"

	}

	def mostrar() {
		def resul = "###################################### ConfiguracionInfraestructura ######################################\n" + 
			"----- ConfiguracionInfraestructura.version: ${version}\n" +
			"----- ConfiguracionInfraestructura.artInfraProsaGen: ${artInfraProsaGen}\n" +
			"----- ConfiguracionInfraestructura.artInfraProsaUtil: ${artInfraProsaUtil}\n" +
			"----- ConfiguracionInfraestructura.artInfraProsaPrivado: ${artInfraProsaPrivado}\n" +
			"----- ConfiguracionInfraestructura.artInfraProsaEJB: ${artInfraProsaEJB}\n" +
			"----- ConfiguracionInfraestructura.artInfraProsaWeb: ${artInfraProsaWeb}\n" +
			"----- ConfiguracionInfraestructura.artifactInfraProsaUtilId: ${artifactInfraProsaUtilId}\n" +
			"----- ConfiguracionInfraestructura.artifactInfraProsaUtilPrivadoId: ${artifactInfraProsaUtilPrivadoId}\n" +
			"----- ConfiguracionInfraestructura.artifactInfraProsaEjb: ${artifactInfraProsaEjb}\n" +
			"----- ConfiguracionInfraestructura.artifactInfraProsaWeb: ${artifactInfraProsaWeb}\n" +
			"----- ConfiguracionInfraestructura.artifactGroupIdInfra: ${artifactGroupIdInfra}\n" +
			"----- ConfiguracionInfraestructura.artifactGroupIdInfraWs: ${artifactGroupIdInfraWs}\n" +
			"----- ConfiguracionInfraestructura.artifactGroupIdInfraOnline: ${artifactGroupIdInfraOnline}\n" +
			"----- ConfiguracionInfraestructura.artifactGroupIdInfraBatch: ${artifactGroupIdInfraBatch}\n" +
			"----- ConfiguracionInfraestructura.jarInfraProsaUtil.directorio: ${jarInfraProsaUtil.directorio}\n" +
			"----- ConfiguracionInfraestructura.jarInfraProsaUtil.rutaGenerado: ${jarInfraProsaUtil.rutaGenerado}\n" +
			"----- ConfiguracionInfraestructura.jarInfraProsaWsUtil.directorio: ${jarInfraProsaWsUtil.directorio}\n" +
			"----- ConfiguracionInfraestructura.jarInfraProsaWsUtil.rutaGenerado: ${jarInfraProsaWsUtil.rutaGenerado}\n" +
			"----- ConfiguracionInfraestructura.jarInfraProsaPrivado.directorio: ${jarInfraProsaPrivado.directorio}\n" +
			"----- ConfiguracionInfraestructura.jarInfraProsaPrivado.rutaGenerado: ${jarInfraProsaPrivado.rutaGenerado}\n" +
			"----- ConfiguracionInfraestructura.jarInfraProsaOnlineEjb.directorio: ${jarInfraProsaOnlineEjb.directorio}\n" +
			"----- ConfiguracionInfraestructura.jarInfraProsaOnlineEjb.rutaGenerado: ${jarInfraProsaOnlineEjb.rutaGenerado}\n" +
			"----- ConfiguracionInfraestructura.jarInfraProsaBatchEjb.directorio: ${jarInfraProsaBatchEjb.directorio}\n" +
			"----- ConfiguracionInfraestructura.jarInfraProsaBatchEjb.rutaGenerado: ${jarInfraProsaBatchEjb.rutaGenerado}\n" +
			"----- ConfiguracionInfraestructura.jarInfraProsaWsEjb.directorio: ${jarInfraProsaWsEjb.directorio}\n" +
			"----- ConfiguracionInfraestructura.jarInfraProsaWsEjb.rutaGenerado: ${jarInfraProsaWsEjb.rutaGenerado}\n" +
			"----- ConfiguracionInfraestructura.warInfraProsaOnlineWeb.directorio: ${warInfraProsaOnlineWeb.directorio}\n" +
			"----- ConfiguracionInfraestructura.warInfraProsaOnlineWeb.rutaGenerado: ${warInfraProsaOnlineWeb.rutaGenerado}\n" +
			"----- ConfiguracionInfraestructura.warInfraProsaBatchWeb.directorio: ${warInfraProsaBatchWeb.directorio}\n" +
			"----- ConfiguracionInfraestructura.warInfraProsaBatchWeb.rutaGenerado: ${warInfraProsaBatchWeb.rutaGenerado}\n" +
			"----- ConfiguracionInfraestructura.jarInfraProsaUtilDesarrollo.directorio: ${jarInfraProsaUtilDesarrollo.directorio}\n" +
			"----- ConfiguracionInfraestructura.jarInfraProsaUtilDesarrollo.rutaGenerado: ${jarInfraProsaUtilDesarrollo.rutaGenerado}\n" +
			"----- ConfiguracionInfraestructura.jarInfraProsaPrivadoDesarrollo.directorio: ${jarInfraProsaPrivadoDesarrollo.directorio}\n" +
			"----- ConfiguracionInfraestructura.jarInfraProsaPrivadoDesarrollo.rutaGenerado: ${jarInfraProsaPrivadoDesarrollo.rutaGenerado}\n" +
			"----- ConfiguracionInfraestructura.jarInfraProsaEjbDesarrollo.directorio: ${jarInfraProsaEjbDesarrollo.directorio}\n" +
			"----- ConfiguracionInfraestructura.jarInfraProsaEjbDesarrollo.rutaGenerado: ${jarInfraProsaEjbDesarrollo.rutaGenerado}\n" +
			"----- ConfiguracionInfraestructura.warInfraProsaOnlineWebDesarrollo.directorio: ${warInfraProsaOnlineWebDesarrollo.directorio}\n" +
			"----- ConfiguracionInfraestructura.warInfraProsaOnlineWebDesarrollo.rutaGenerado: ${warInfraProsaOnlineWebDesarrollo.rutaGenerado}\n" +
			"----- ConfiguracionInfraestructura.warInfraProsaBatchWebDesarrollo.directorio: ${warInfraProsaBatchWebDesarrollo.directorio}\n" +
			"----- ConfiguracionInfraestructura.warInfraProsaBatchWebDesarrollo.rutaGenerado: ${warInfraProsaBatchWebDesarrollo.rutaGenerado}\n" +
			"----- ConfiguracionInfraestructura.warInfraProsaWsWebDesarrollo.directorio: ${warInfraProsaWsWebDesarrollo.directorio}\n" +
			"----- ConfiguracionInfraestructura.warInfraProsaWsWebDesarrollo.rutaGenerado: ${warInfraProsaWsWebDesarrollo.rutaGenerado}\n"
		
		return resul
	}
	
	// Generacion de rutas para empaquetados
	def genUrlNexusArtifactInfraProsaUtilEmpa(confNexus) {
		confNexus.genUrlNexusArtifactJar(
			confNexus.nexusRepositoryInfraestructuraId,
			artifactGroupIdInfra, artifactInfraProsaUtilId, version)
	}

	def genUrlNexusArtifactInfraProsaWsUtilEmpa(confNexus) {
		confNexus.genUrlNexusArtifactJar(
			confNexus.nexusRepositoryInfraestructuraId,
			artifactGroupIdInfraWs, artifactInfraProsaUtilId, version)
	}

	def genUrlNexusArtifactInfraProsaPrivadoEmpa(confNexus) {
		confNexus.genUrlNexusArtifactJar(
			confNexus.nexusRepositoryInfraestructuraId,
			artifactGroupIdInfra, artifactInfraProsaUtilPrivadoId, version)
	}

	// *********************************** EJB
	def genUrlNexusArtifactInfraProsaOnlineEjbEmpa(confNexus) {
		confNexus.genUrlNexusArtifactJar(
			confNexus.nexusRepositoryInfraestructuraId,
			artifactGroupIdInfraOnline, artifactInfraProsaEjb, version)
	}
	def genUrlNexusArtifactInfraProsaBatchEjbEmpa(confNexus) {
		confNexus.genUrlNexusArtifactJar(
			confNexus.nexusRepositoryInfraestructuraId,
			artifactGroupIdInfraBatch, artifactInfraProsaEjb, version)
	}
	def genUrlNexusArtifactInfraProsaWsEjbEmpa(confNexus) {
		confNexus.genUrlNexusArtifactJar(
			confNexus.nexusRepositoryInfraestructuraId,
			artifactGroupIdInfraWs, artifactInfraProsaEjb, version)
	}

	// *********************************** WEB
	def genUrlNexusArtifactInfraProsaOnlineWebEmpa(confNexus) {
		confNexus.genUrlNexusArtifactWar(
			confNexus.nexusRepositoryInfraestructuraId,
			artifactGroupIdInfraOnline, artifactInfraProsaWeb, version)
	}
	def genUrlNexusArtifactInfraProsaBatchWebEmpa(confNexus) {
		confNexus.genUrlNexusArtifactWar(
			confNexus.nexusRepositoryInfraestructuraId,
			artifactGroupIdInfraBatch, artifactInfraProsaWeb, version)
	}
	def genUrlNexusArtifactInfraProsaWsWebEmpa(confNexus) {
		confNexus.genUrlNexusArtifactWar(
			confNexus.nexusRepositoryInfraestructuraId,
			artifactGroupIdInfraWs, artifactInfraProsaWeb, version)
	}

	// Generacion de rutas para desarrollo
	def genUrlNexusArtifactInfraProsaUtilDesa(confNexus) {
		confNexus.genUrlNexusArtifactJar(
			confNexus.nexusRepositoryDesarrolloId,
			artifactGroupIdInfra, artifactInfraProsaUtilId, version)
	}

	def genUrlNexusArtifactInfraProsaPrivadoDesa(confNexus) {
		confNexus.genUrlNexusArtifactJar(
			confNexus.nexusRepositoryDesarrolloId,
			artifactGroupIdInfra, artifactInfraProsaUtilPrivadoId, version)
	}	

	def genUrlNexusArtifactInfraProsaEjbDesa(confNexus) {
		confNexus.genUrlNexusArtifactJar(
			confNexus.nexusRepositoryDesarrolloId,
			artifactGroupIdInfra, artifactInfraProsaEjb, version)
	}
	
	// *********************************** WEB
	def genUrlNexusArtifactInfraProsaOnlineWebDesa(confNexus) {
		confNexus.genUrlNexusArtifactWar(
			confNexus.nexusRepositoryDesarrolloId,
			artifactGroupIdInfraOnline, artifactInfraProsaWeb, version)
	}
	def genUrlNexusArtifactInfraProsaBatchWebDesa(confNexus) {
		confNexus.genUrlNexusArtifactWar(
			confNexus.nexusRepositoryDesarrolloId,
			artifactGroupIdInfraBatch, artifactInfraProsaWeb, version)
	}
	def genUrlNexusArtifactInfraProsaWsWebDesa(confNexus) {
		confNexus.genUrlNexusArtifactWar(
			confNexus.nexusRepositoryDesarrolloId,
			artifactGroupIdInfraWs, artifactInfraProsaWeb, version)
	}
	
}

