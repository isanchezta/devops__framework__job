// Funciona de arranque del proceso de compilacion
// devuelve 0 si OK  y <>0 si ERROR
def start() {
	log.info "COMPILE"
	
	log.info "MavenBuild"

	artifactory.mavenBuild("documentum-maven", "pom.xml", "gplus:groovydoc")
	//artifactory.mavenBuildDepoy("documentum-maven", "documentum-release",  "AccionaFwDevOps/pom.xml", "validate gplus:groovydoc install")

	log.info "Creacion del ZIP pipes, ${PRJ}, ${VERSION}"
	sh "pwd"
	sh "ls -al"
	

	
	String dir = "${WORKSPACE}/pipes/"
	String prj = "${PRJ}"
	String version = "${VERSION}"
	zipper.packing( dir , prj , version )
	
	dir =  "${WORKSPACE}/target/gapidocs/"
	prj = "${PRJ}-DOC"
	zipper.packing( dir , prj , version )
	
	return 0; // devolveremos como error la fase de compilacion para que no continue el pipe
}

return this;