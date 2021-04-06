package es.segsocial.prosa

class DatosPom {
	String groupId
    String artifactId
    String version
    String packaging
}

class PomUtil {
	def DatosPom consultaVersiones(String pomFile){
		def pomFileParsed = getDocument(pomFile)
		DatosPom dp = new DatosPom()
		dp.groupId = pomFileParsed.groupId
		dp.artifactId = pomFileParsed.artifactId
		dp.version = pomFileParsed.version
		dp.packaging = pomFileParsed.packaging
		
		return dp

    }
	
	def getDocument(String pomFile) {
		def pomFileParsed = new XmlSlurper().parse(new File(pomFile))
		return pomFileParsed
	}
}


