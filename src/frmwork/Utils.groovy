package frmwork

// https://mvnrepository.com/artifact/org.codehaus.groovy/groovy-json

//@Grapes(
//	@Grab(group='org.codehaus.groovy', module='groovy-json', version='2.4.18')
//)


import groovy.json.JsonSlurper

/**
 * Utilidad que devuelve la ejecuciï¿½n del Git Describe sobre la rama bajada
 * 
 * @return String con el resultado X.Y.Z-NN
 */
def gitDescribe() {
	
  return sh ( returnStdout: true, script: 
	  '''version=$(git describe --abbrev=0 --tags);set $version;echo $version''').trim()  
}

/**
 * Utilidad que devuelve la version del tag mas actual
 * @return
 */
def getVersionRepo() {
	String tag = gitDescribe()
	String[] res = tag.split("-")
	return  res[0]
}

/**
 * Utilidad que devuelve la version del tag mas actual de un repo diferente en GitHub
 * @return
 */
def getRepoLatestTag(String repository) {
	withCredentials([usernamePassword(credentialsId: 'GITLAB_PRO_USER_PASSWD', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
		repository = repository.replaceFirst(/https:\/\//, "https://$USERNAME:$PASSWORD@")
		return sh ( returnStdout: true, script: 
	  		"tag=\$(git ls-remote --tags --exit-code --refs \"${repository}\" \
				| sed -E 's/^[[:xdigit:]]+[[:space:]]+refs\\/tags\\/(.+)/\\1/g' | tail -n1);set \$tag;echo \$tag").trim()  
	}
}

/**
 * Crea el team del proyecto en GitHub conforme a la division y al nombre del proyecto
 * Devuelve el nombre del team generado
 * 
 * @param teamSlugNameDiv
 * @param strProjectName
 * @return
 */
def  updateTeamProject(String teamSlugNameDiv, String strProjectName) {
	
	// para crear el team es necesario obtener el ID del project padre
	def creds = new  Credentials()
	def token = creds.getPassword('GITLAB_PRO_USER_PASSWD')
	int idTeamDiv = getIdTeam(teamSlugNameDiv, 'GITLAB_PRO_USER_PASSWD') 
	
	// POST para crear el team /orgs/:org/teams
	def post = new URL("https://api.github.com/orgs/ITACCIONA/teams").openConnection();
	def message = "{\"name\":\"${strProjectName}\", \"parent_team_id\":${idTeamDiv} }"
	println(message)
	post.setRequestMethod("POST")
	post.setDoOutput(true)
	post.setRequestProperty("Content-Type", "application/json")
	post.setRequestProperty ("Authorization", "token "+ token);
	post.getOutputStream().write(message.getBytes("UTF-8"));
	def postRC = post.getResponseCode();
	println("RESPUESTA CREACION TEAM 200/201 creado y 422 ya existente: " + postRC);
	
	return strProjectName
}

/**
 * Utilidad que ejecuta el mirror de Azure Repo a GitHub
 * 
 * Se utilizara unas credenciales propias para acceder a GITHUB internas para realizar la
 * replicacion
 * 
 * @param gitAzure url de conexion al repo de azure
 * @param credAzure credencial de conexion al repo de azure
 * 
 * @return
 */
def gitAzure2GitHub(String gitAzure, String credAzure) {
	// Creacion del repostorio en github: formato -> nombre_proyecto__nombre_repo
	// El formato de la URL en azure repo es: https://dev.azure.com/ITACCIONA/con_SEO/_git/Caja
	// el cual se descompone en : https://dev.azure.com/ITACCIONA/nombre_proyecto/_git/nombre_repo
	
	// Obtener el proyecto y repositorio
	def values = gitAzure.split('/')
	
	if (!values[2].contains("dev.azure.com")) {
		println("REPO NO ES DE AZURE-> NO SE REPLICA EN GITHUB: " + gitAzure)
		return ;
	}
	
	assert( "URL de AZURE no valido ${gitAzure} " && values.length>6 )
	
	String strProjectName = values[4]
	String strRepoName = values[6]
	
	echo( "Proyecto __ Repositorio: " + strProjectName + "__" + strRepoName)
	
	// Creacion del repositorio en GitHub
	// Uso de credencial de admintrador : usuario accionador
	// credencial APP_OFI_DEVOPS_GIT
	
	def creds = new  Credentials()
	def username = creds.getUsername('GITLAB_PRO_USER_PASSWD')
	def token = creds.getPassword('GITLAB_PRO_USER_PASSWD')
	def usernameAzure = creds.getUsername(credAzure)
	def tokenAzure = creds.getPassword(credAzure)
	
		
	String strRepoNameGitHub = strProjectName +"__"+ strRepoName
	if(strProjectName.compareToIgnoreCase(strRepoName)==0)
		strRepoNameGitHub = strProjectName +"__default";
	
	String strUrlAzure = "https://${tokenAzure}@dev.azure.com/ITACCIONA/${strProjectName}/_git/${strRepoName}"
	String strUrlGitHub = "https://${token}@github.com/ITACCIONA/${strRepoNameGitHub}.git"
	
	// si no existe el repo se crea y acto seguido re realiza el mirror
	// crear el repositorio y verifica que existe conlleva una operacion al API asi que el gasto es el mismo
	def res = createRepoGitHub(strRepoNameGitHub, 'TOKEN_ACCESO_GITHUB')
	// Seleccionar team dependiendo de la division
	def division = strProjectName.split("_")
	String teamSlugNameDiv = "" 
	/*
	 * ene -> energia-team
	 * con -> construccion-team
	 * cor -> corporativo-team
	 * ser -> servicios-team
	 * agu -> agua-team
	 */
	if (division[0].contains("ene")) {
		teamSlugNameDiv =  "energia-team" 
	} else if (division[0].contains("con")) {
		teamSlugNameDiv =  "construccion-team" 
	} else if (division[0].contains("cor")) {
		teamSlugNameDiv =  "corporativo-team" 
	} else if (division[0].contains("ser")) {
		teamSlugNameDiv =  "servicios-team" 
	} else if (division[0].contains("agu")) {
		teamSlugNameDiv =  "agua-team" 
	} else // ERROR proyecto de azure mal denominado o division no soportada
		throw new Exception("EL PREFIJO DEL NOMBRE DEL PROYECTO NO ES RECONOCIDO ${strProjectName}")
	
	// FW: 1.3.5 el team al que tiene que ir asignado el repo es:
	// division-team -> project-team
	// si no existe el project-team se creará uno con el nombre del proyecto del repositorio
	def teamSlugName = updateTeamProject(teamSlugNameDiv, strProjectName)
	
	println "prefijo/division: "+ division[0] + "/" + teamSlugName
	if(teamSlugName.length() > 0)
		assingRepo2Team( strRepoNameGitHub, 'TOKEN_ACCESO_GITHUB', teamSlugName)

	sh """
		mkdir mirror
		git init --bare mirror
		
		cd mirror
		git config remote.origin.url ${strUrlAzure}
		git config --add remote.origin.fetch '+refs/heads/*:refs/heads/*'
		git config --add remote.origin.fetch '+refs/tags/*:refs/tags/*'
		git config --add remote.origin.fetch '+refs/notes/*:refs/notes/*'
		git config remote.origin.mirror true
		git -c http.sslVerify=true fetch --all

		git -c http.sslVerify=true push --mirror ${strUrlGitHub}
	"""
}

/*
def isRepoGitHub(String strRepoName, String cred ) {
	boolean bres = false;
	def creds = new  Credentials()
	def username = creds.getUsername(cred)
	def token = creds.getPassword(cred)
	
	// POST
	def url = "https://${token}@github.com/ITACCIONA/${strRepoName}";
	echo url
	def post = new URL(url).openConnection();
	//def message = '{\"name\":\"' +strRepoName+ '\", \"private\":true }'
	//echo message
	
	post.setRequestMethod("HEAD")
	//post.setDoOutput(true)
	//post.setRequestProperty("Content-Type", "application/json")
	post.setRequestProperty ("Authorization", "token "+ token);
	//post.getOutputStream().write(message.getBytes("UTF-8"));
	def postRC = post.getResponseCode();
	println("EXISTE REPO: " + postRC);
	
	return postRC.equals(200);
}
*/

/**
 * Función que genera un repo en GITHUB
 * 
 * @param strRepoName 	Nombre del repositorio a generar
 * @param cred	Credencial de administrador que puede crear repositorios
 * @return
 */
def createRepoGitHub(String strRepoName, String cred) {
	
	def creds = new  Credentials()
	def username = creds.getUsername(cred)
	def token = creds.getPassword(cred)
	
	// POST
	def post = new URL("https://api.github.com/orgs/ITACCIONA/repos").openConnection();
	def message = '{\"name\":\"' +strRepoName+ '\", \"private\":true }'
	
	
	post.setRequestMethod("POST")
	post.setDoOutput(true)
	post.setRequestProperty("Content-Type", "application/json")
	post.setRequestProperty ("Authorization", "token "+ token);
	post.getOutputStream().write(message.getBytes("UTF-8"));
	def postRC = post.getResponseCode();
	println("RESPUESTA CREACION REPO 200/201 creado y 422 ya existente: " + postRC);
	
	return postRC
}

/**
 * Devuelve el ID del team de GitHub indicando por el parametro
 * 
 * @param teamName
 * @param cred
 * @return
 */
def getIdTeam(String teamName, String cred) {
	def creds = new  Credentials()
	def username = creds.getUsername(cred)
	def token = creds.getPassword(cred)
	
	int idteam
	// 1- Localizar el ID del team
	def post = new URL("https://api.github.com/orgs/ITACCIONA/teams/${teamName}").openConnection();
	post.setRequestMethod("GET")
	post.setDoOutput(true)
	post.setRequestProperty("Content-Type", "application/json")
	post.setRequestProperty ("Authorization", "token "+ token);

	def postRC = post.getResponseCode();
	println "GET INFO TEAM: " + postRC
	if(postRC.equals(200)) {
		def jsonSlurper = new JsonSlurper()
		String strRes = post.getInputStream().getText()
		println strRes
		def object = jsonSlurper.parseText( strRes );
		println object
		idteam = object.id
		println teamName +"->" + idteam
	}
	
	return idteam
}
 
/**
 * Asigna un repo a un team en concreto 
 * /teams/:team_id/repos/:owner/:repo
 * @param strRepoName
 * @param cred
 * @param teamName
 * @return
 */
def assingRepo2Team(String strRepoName, String cred, String teamName) {
	
	def creds = new  Credentials()
	def username = creds.getUsername(cred)
	def token = creds.getPassword(cred)
	
	// 1- Localizar el ID del team
	int idteam = getIdTeam(teamName, cred) 

	// 2- Con el ID del team realizamos la asignacion del repo al team
	String command  ="https://api.github.com/teams/${idteam}/repos/ITACCIONA/${strRepoName}"
	println "API REST: " + command
	def post2 = new URL(command).openConnection();
	post2.setRequestMethod("PUT")
	post2.setDoOutput(true)
	post2.setRequestProperty("Content-Type", "application/json")
	post2.setRequestProperty ("Authorization", "token "+ token);
	post2.setRequestProperty ("Content-Length", "0");
	
	def postRC2 = post2.getResponseCode()
	println "ASIGNAR REPO TO TEAM: " + postRC2
	
	return postRC2
}

return this
