package stages

//@Grapes(
//	@Grab(group='org.codehaus.groovy', module='groovy-json', version='2.4.18')
//)


import frmwork.Credentials
import frmwork.StagesTypes
import groovy.json.JsonSlurper


class CompileProcess extends Compilacion {
	
	CompileProcess(Script scp) {
		super(scp)
		stageType = StagesTypes.Value.BUILD
	}
	
	void execute() {
		// Si se tiene que compilar en azure se invoca al JOB indicado
		if(this.script.BUILD_AZURE=="true") {
			this.script.log.info "COMPILACION EN AZURE ${this.script.AZURE_PROJECT}-${this.script.AZURE_BUILDID}"
			int res = compileAzure("ITACCIONA", this.script.AZURE_PROJECT, this.script.AZURE_BUILDID, this.script.GIT_CREDENTIALSID_AZURE) // script.GIT_CREDENTIALSID)
			if(res!=0)
				this.script.error("ERROR BUILD AZURE DEVOPS")
			return
		}
		def rootDir = this.script.pwd()
		this.script.log.info( 'INVOCAR BUILD')
		this.script.log.info "Si existe el directorio 'a' de artefactos se borra"
		def res = this.script.fileExists('a')
		if (res){
			this.script.log.info "directorio a existe se borrar"
			this.script.sh("rm -rf a") // sh "rm ${fileNameZip}"
		}
				
		this.script.sh("mkdir a")
		def build = this.script.load "${rootDir}/pipes/${this.script.DIR_BUILD}/start.groovy"
		def result = build.start();
		if(result>0) {
			this.script.error( "Error en BUILD(${this.script.ENTORNO}) ->  ${result}" )
			// se produce error terminar la pipe de jenkins con estado de error
			return;
		}
	}
}
