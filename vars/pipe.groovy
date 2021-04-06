import es.segsocial.prosa.DatosArtsGenerados
import es.segsocial.prosa.FileUtils
import es.segsocial.prosa.ConfiguracionGlobal
import es.segsocial.prosa.ConfiguracionGit
import es.segsocial.prosa.ConfiguracionNexus
import es.segsocial.prosa.ConfiguracionJarGeneral
import es.segsocial.prosa.ConfiguracionInfraestructura
import es.segsocial.prosa.ConfiguracionAplicacion
import es.segsocial.prosa.ConfiguracionAppsComunes
import es.segsocial.prosa.ConfiguracionSpm
import es.segsocial.prosa.ConfiguracionKiuwan
import es.segsocial.prosa.ConfiguracionJacoco
import es.segsocial.prosa.ParamsScripts

def traza() {
	def text = '''
		<list>
			<technology>
				<name>Groovy</name>
				<as.es>Saldra 1 </as.es>
			</technology>
			<otro.as.es>Saldra 2</otro.as.es>
		</list>
	'''

	def list = new XmlSlurper().parseText(text)
	echo "TECHNOLOGY: " + list.technology.name
	def c = list.childNodes()
	while (c.hasNext()) {
		echo "Encontrado children"
		echo " ------------------- " + c.next().name
	}
	echo "Prueba getProperty(otro.as.es): " + list.getProperty("otro.as.es")
	echo "Prueba getProperty(as.es): " + list.technology.getProperty("as.es")

}

def getArrAtifacts() {
	def dag1 = new DatosArtsGenerados()
	dag1.directorio="/tmp"
	dag1.rutaGenerado="${dag1.directorio}/winstone1682065441120365555.jar"
	dag1.groupId="test.g1"
	dag1.artifactId="artefacto1"
	dag1.version="1.0.0"

	def dag2 = new DatosArtsGenerados()
	dag2.directorio="/tmp"
	dag2.rutaGenerado="${dag2.directorio}/borrar.xml"
	dag2.groupId="test.g2"
	dag2.artifactId="artefacto2"
	dag2.version="1.0.1"
	dag2.classifier="xml"
	dag2.extension="xml"
	dag2.packaging="xml"
	
	def resul = [dag1.getDatosSubidaNexus(), dag2.getDatosSubidaNexus()]
	
	return resul
	
}

def pruebaDir(dir) {
	def fu = new FileUtils()
	def r = fu.getArtefactosZipEar(dir)
	echo "RESULTADO.zip: ${r.zip}"
	echo "RESULTADO.ear: ${r.ear}"
}

def confGlobal
def paramsScripts

def pruebaPom(dca) {
	def codApp = "COD_APP"
	def rutaRepoGit = "rutaRepoGit"
	def cg = new ConfiguracionGit()
	paramsScripts = new ParamsScripts()
	confGlobal = new ConfiguracionGlobal(codApp, cg)
	
	confApp = new ConfiguracionAplicacion(codApp, confGlobal, cg, rutaRepoGit)
	confApp.dirCheckoutAplicaciones=dca
	confApp.initPom(confGlobal)
	
	paramsScripts.initParamsApp(confApp.versionJarGeneral, confApp.versionInfraestructura, confGlobal.codAplicacion,
		confApp.version, confApp.appComunesVersiones)
}



