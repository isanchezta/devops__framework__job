import es.segsocial.prosa.ConfiguracionGlobal
import es.segsocial.prosa.ConfiguracionGit
import es.segsocial.prosa.ConfiguracionNexus
import es.segsocial.prosa.ConfiguracionJarGeneral
import es.segsocial.prosa.ConfiguracionInfraestructura
import es.segsocial.noprosa.ConfiguracionAplicacion
import es.segsocial.prosa.ConfiguracionAppsComunes
import es.segsocial.prosa.ConfiguracionSpm
import es.segsocial.prosa.ConfiguracionKiuwan
import es.segsocial.prosa.ConfiguracionJacoco
import es.segsocial.prosa.ParamsScripts

// Clase para configuraciones globales
def confGlobal

// Configuracion para GIT
def confGit

// Configuracion para Nexus
def confNexus

// Configuracion para JarGeneral
def confJarGeneral

// Configuracion para SPM
def confSpm

// Configuracion para Infraestructura
def confInfra

// Configuracion para la aplicacion
def confApp

// Configuracion para las aplicaciones comunes
def confComun

// Configuracion para kiuwan
def confKiuwan

// Configuracion para jacoco
def confJacoco

def initConfiguracionesGlobales(codApp) {
	if(codApp == null) {
		throw new NullPointerException("Es necesario indicar un codigo de aplicacion para la configuracion global")
	}
	confGit = new ConfiguracionGit()
	confGlobal = new ConfiguracionGlobal(codApp, confGit)
	confNexus = new ConfiguracionNexus()
	paramsScripts = new ParamsScripts()
	confKiuwan = new ConfiguracionKiuwan()
	confKiuwan.kiuwanAplicacion = confGlobal.codAplicacion
	confJacoco = new ConfiguracionJacoco()
}


def initApp(codAplicacion, rutaRepoGit) {
	initConfiguracionesGlobales(codAplicacion)
	
	echo "Se llama a initApp terminado"
	confApp = new ConfiguracionAplicacion(codAplicacion, confGit, rutaRepoGit)
	echo "initApp terminado"
}


def mostrarConfiguracionGlobal() {
	echo "[mostrarConfiguracionGlobal] Mostrar valores configurados:"
	echo "[mostrarConfiguracionGlobal] ##################### confGlobal #####################"
	echo confGlobal.mostrar()
	echo "[mostrarConfiguracionGlobal] ##################### confGit #####################"
	echo confGit.mostrar()
	echo "[mostrarConfiguracionGlobal] ##################### confNexus #####################"
	echo confNexus.mostrar()
	echo "[mostrarConfiguracionGlobal] ##################### confJarGeneral #####################"
	echo confApp.mostrar()
	echo "[mostrarConfiguracionGlobal] Terminado:"
}


/*
def initJarGeneral(codApp) {
	confJarGeneral = new ConfiguracionJarGeneral()
	initConfiguracionesGlobales(codApp)
}

def initPomJarGeneral() {
	echo "[initPomJarGeneral] Se llama a initJarGeneral ahora"
	confJarGeneral.initPom(confGlobal)
	paramsScripts.initParamsJarGeneral(confJarGeneral.version)
	paramsScripts.initVersionWAS(confJarGeneral.versionWAS)
	echo "[initPomJarGeneral] initJarGeneral terminado"
	
	echo "[initPomJarGeneral] ##################### confJarGeneral tras leer el POM #####################"
	confJarGeneral.mostrar()
	echo "[initPomJarGeneral] " + paramsScripts.mostrar()
}


def initSpm(codApp) {
	confJarGeneral = new ConfiguracionJarGeneral()
	initConfiguracionesGlobales(codApp)
	echo "[initSpm] Se llama a confGlobal.inicializacionesPosterioresInfra"
	confGlobal.initLocalizacionesInfra()
	echo "[initSpm] Llamada a confGlobal.inicializacionesPosterioresInfra terminada"
}

def initInfraestructura(codApp) {
	echo "[initInfraestructura] Se instancia ConfiguracionJarGeneral"
	initJarGeneral(codApp)
	echo "[initInfraestructura] ConfiguracionJarGeneral instanciado"

	echo "[initInfraestructura] Se llama a instancia ConfiguracionInfraestructura"
	confInfra = new ConfiguracionInfraestructura()
	echo "[initInfraestructura] initInfraestructura terminado"
	
	echo "Se llama a confGlobal.inicializacionesPosterioresInfra"
	confGlobal.initLocalizacionesInfra()
	echo "[initInfraestructura] Llamada a confGlobal.inicializacionesPosterioresInfra terminada"

}

def initPomInfraestructura() {
	echo "[initPomInfraestructura] Se llama a initPom de infraestructura ahora, se necesita confInfra"
	confInfra.initPom(confGlobal)
	paramsScripts.initParamsGeneralInfra(confInfra.versionJarGeneral, confInfra.version)
	paramsScripts.initVersionWAS(confInfra.versionWAS)
	echo "[initPomInfraestructura] initPomInfraestructura terminado"
	
	echo "[initPomInfraestructura] ##################### confJarGeneral tras leer el POM #####################"
	echo "[initPomInfraestructura] " + confInfra.mostrar()
	echo "[initPomInfraestructura] " + paramsScripts.mostrar()
}

def initPomSpm(codApp, carpetaScript, carpetaDestino) {
	echo "[initPomSpm] Se llama a initPom de SPM ahora, se necesita confInfra"
	confSpm = new ConfiguracionSpm(carpetaScript, carpetaDestino)
	confSpm.initPom(confGlobal)

	paramsScripts.initParamsSpm(confSpm)
	paramsScripts.initVersionWAS(confSpm.versionWAS)

	echo "[initPomSpm] ##################### Configuracion SPM tras leer el POM #####################"
	echo "[initPomSpm] " + confSpm.mostrar()
	echo "[initPomSpm] " + paramsScripts.mostrar()

	echo "[initPomSpm] Terminado"
}

def initAplicacion(codApp, rutaRepoGit) {

	echo "[initAplicacion] Se instancia ConfiguracionJarGeneral"
	initJarGeneral(codApp)
	echo "[initAplicacion] ConfiguracionJarGeneral instanciado"

	echo "Se llama a initApp terminado"
	confApp = new ConfiguracionAplicacion(codApp, confGlobal, confGit, rutaRepoGit)
	echo "initApp terminado"
	
	echo "[initAplicacion] Se llama a confGlobal.initLocalizacionesApp"
	confGlobal.initLocalizacionesApp(confApp)
	echo "[initAplicacion] Llamada a confGlobal.initLocalizacionesApp terminada"

}

def initComunes(codApp, rutaRepoGit) {

	echo "[initComunes] Se instancia ConfiguracionJarGeneral"
	initJarGeneral(codApp)
	echo "[initComunes] ConfiguracionJarGeneral instanciado"

	echo "Se instancia ConfiguracionAppsComunes"
	confComun = new ConfiguracionAppsComunes(codApp, confGlobal, confGit, rutaRepoGit)
	echo "ConfiguracionAppsComunes terminado"
	
	echo "[initComunes] Se llama a confGlobal.initLocalizacionesApp"
	confGlobal.initLocalizacionesApp(confComun)
	echo "[initComunes] Llamada a confGlobal.initLocalizacionesApp terminada"

}

def initPomApp() {
	echo "Se llama a initPom de aplicacion ahora, se necesita confInfra"
	confApp.initPom(confGlobal)
	paramsScripts.initParamsApp(confApp.versionJarGeneral, confApp.versionInfraestructura, confGlobal.codAplicacion,
		confApp.version, confApp.appComunesVersiones)
	paramsScripts.initVersionWAS(confApp.versionWAS)
	echo "initPomApp terminado"
	
	echo "##################### confJarGeneral tras leer el POM #####################"
	echo confApp.mostrar()
	echo paramsScripts.mostrar()
}

def initPomComun() {
	echo "Se llama a initPom de aplicacion ahora, se necesita confInfra"
	confComun.initPom(confGlobal)
	paramsScripts.initParamsApp(confComun.versionJarGeneral, confComun.versionInfraestructura, confGlobal.codAplicacion,
		confComun.version, confComun.appComunesVersiones)
	paramsScripts.initVersionWAS(confComun.versionWAS)
	echo "initPomComun terminado"
	
	echo "##################### confJarGeneral tras leer el POM #####################"
	echo confComun.mostrar()
	echo paramsScripts.mostrar()
}

def mostrarConfiguracionGlobal() {
	echo "[mostrarConfiguracionGlobal] Mostrar valores configurados:"
	echo "[mostrarConfiguracionGlobal] ##################### confGlobal #####################"
	echo confGlobal.mostrar()
	echo "[mostrarConfiguracionGlobal] ##################### confGit #####################"
	echo confGit.mostrar()
	echo "[mostrarConfiguracionGlobal] ##################### confNexus #####################"
	echo confNexus.mostrar()
	echo "[mostrarConfiguracionGlobal] ##################### confJarGeneral #####################"
	echo confJarGeneral.mostrar()
	echo "[mostrarConfiguracionGlobal] Terminado:"
}

def borrarDirectorios() {
	echo "[borrarDirectorios] Se eliminan los directorios para ejecucion limpia"
	// Se eliminan los directorios fuentes correspondientes sa ejecuciones anteriores
	sh "rm -rf ${confGlobal.dirCheckout}"

	sh "rm -rf ${confGlobal.baseTrabajo}/ProsaAnttmpWas*"
	
	sh "rm -rf ${confGlobal.baseTrabajo}/local-repo*"
	
	sh "rm -rf ${confGlobal.baseTrabajo}/pocgissResultados*"
}

def leerFicheroSalidaCompilacion() {
	confGlobal.leerFicheroSalidaCompilacion()
}

def comprobarParametros(cod_app, ruta_repo) {

	if(cod_app.equals("null")){
			throw new NullPointerException("Se debe definir el parámetro COD_APLICACION que indica el código de la aplicación a compilar")
	}

	if(ruta_repo.equals("null")){
			throw new NullPointerException("Se debe definir el parámetro RUTA_REPO_GIT que indica la ruta donde se encuentra el código de la aplicación a compilar")
	}
}
*/





