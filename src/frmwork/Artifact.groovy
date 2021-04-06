package frmwork


//@Grapes([
//	@Grab(group='com.cloudbees', module='groovy-cps', version='1.31'),
//	@Grab('ch.qos.logback:logback-classic:1.2.1')
//])

import com.cloudbees.groovy.cps.NonCPS

import org.jenkinsci.plugins.*
import groovy.util.logging.Slf4j


@Slf4j
class Artifact {
	
	def artifactory
	
	Artifact(artifactory) {
		this.artifactory = artifactory
	}
	@NonCPS
	String deployArtefacto(REPO, PRJ, VERSION, PATTERN) { // a/*.zip
		
		//def serverUpld = Artifactory.newServer(url: URL, username: USERNAME, password: PWD)
		
		// Se despliega el zip generado en artifactory en el repo de deploy subiendolo
		String res = ""
		try
		{
			def uploadSpec = """{
						  "files": [
						    {
						      "pattern": "$PATTERN",
				"target": "$REPO/$PRJ/$VERSION/"
			  }
		   ]
		  }"""
		  getArtifactory().upload spec: uploadSpec
		} catch (Exception e) {
			res = e.getMessage()
		}
		
		return res
	//  steps.sh "rm -rf a"
	}
	
	@NonCPS
	String downloadArtefacto(REPO, PRJ, VERSION) {
		def res = ""
		try
		{
			def downloadSpec = """{
			 			"files": [
			  			{
			      				"pattern": "$REPO/$PRJ/$VERSION/",
			      				"target": "d/"
			    			}
			 			]
					}"""
		 	getArtifactory().download spec: downloadSpec, failNoOp: true
		} catch (Exception e) {
			res = e.getMessage()
		}
		
		return res
	}
   
   
}



