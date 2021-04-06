package es.segsocial.prosa

class ConfiguracionGit {
	String idCredencialGit="GissGithubDevops"
	
	//String urlGitRemote="http://gitlab-devops-giss.poc-devops-817965-e618beb03cdc8d5b32fda3ba78243b48-0000.eu-de.containers.appdomain.cloud"
	//String urlGitRemote="http://giss-gitlab"
	String urlGithttps="https://"
	String urlGitgithub="github.com"
	String urlGitRemote= urlGithttps+urlGitgithub
	String usuarioGit="ragovi"
	String urlCloneAnstscripts="${urlGitRemote}/${usuarioGit}/giss-ant.git"
	String urlCloneInfraestructura="${urlGitRemote}/${usuarioGit}"
	String urlCloneAplicaciones="${urlGitRemote}/${usuarioGit}/giss-app.git"
	String urlCloneSql="${urlGitRemote}/${usuarioGit}/sqlflyway.git"

	String urlPushTagAplicaciones="${urlGitgithub}/${usuarioGit}/giss-app.git"

	def mostrar() {
		def val="ConfiguracionGit"
		def resul = "###################################### ${val} ######################################\n" + 
			"----- ${val}.idCredencialGit: ${idCredencialGit}\n" +
			"----- ${val}.urlGitRemote: ${urlGitRemote}\n" +
			"----- ${val}.usuarioGit: ${usuarioGit}\n" +
			"----- ${val}.urlCloneAnstscripts: ${urlCloneAnstscripts}\n" +
			"----- ${val}.urlCloneInfraestructura: ${urlCloneInfraestructura}\n" +
			"----- ${val}.urlCloneAplicaciones: ${urlCloneAplicaciones}\n" +
			"----- ${val}.urlCloneSql: ${urlCloneSql}\n"
		return resul
	}
}

