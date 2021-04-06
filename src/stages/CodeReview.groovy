package stages

@Grapes(
	@Grab(group='org.codehaus.groovy', module='groovy-json', version='2.4.18')
)


import frmwork.Credentials
import frmwork.StagesTypes
import groovy.json.JsonSlurper


class CodeReview extends Compilacion {
	
	CodeReview(Script scp) {
		super(scp)
		stageType = StagesTypes.Value.CODE_REVIEW
	}
	
	void execute() {
		def rootDir = script.pwd()
		if(script.BUILD_AZURE=="true") {
			script.log.info "COMPILACION EN AZURE ${script.AZURE_PROJECT}-${script.AZURE_RT_BUILDID}"
			int res = compileAzure("ITACCIONA", script.AZURE_PROJECT, script.AZURE_RT_BUILDID, script.GIT_CREDENTIALSID_AZURE)
			if(res!=0)
				script.error("ERROR BUILD AZURE DEVOPS")
			return
		}
		
		def build = script.load "${rootDir}/pipes/${script.DIR_BUILD}/TDD.groovy"
		
		def result = build.start();
		if(result>0)
		{
			script.error( "Error en Cobertura Codigo(${script.ENTORNO}) ->  ${result}" )
			// se produce error terminar la pipe de jenkins con estado de error
			return;
		}
	}
}
