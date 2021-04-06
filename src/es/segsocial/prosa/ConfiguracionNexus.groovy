package es.segsocial.prosa

class ConfiguracionNexus {
	
	// Esto debería desaparecer de aqui. Creo que seria mejor cogerlo de la configuracion de jenkins, pero de momento no se como hacerlo
	String urlNexus="http://nexus:8081/repository"

	// Configuracion para NEXUS
	//def nexusInstanceId="NexusDocker_openshift"
	//String nexusInstanceId="APIPROSProsaGeneral"
	String nexusInstanceId="NexusDocker_local_docker"
	String nexusRepositoryJarGeneralId="giss_jargeneral"
	String nexusRepositoryInfraestructuraId="giss_infra"
	String nexusRepositoryAppsId="giss_apps"
	String nexusRepositoryDesarrolloId="giss_desarrollo"
	
	def mostrar() {
		def val="ConfiguracionNexus"
		def resul = "###################################### ${val} ######################################\n" + 
			"----- ${val}.nexusInstanceId: ${nexusInstanceId}\n" +
			"----- ${val}.nexusRepositoryJarGeneralId: ${nexusRepositoryJarGeneralId}\n" +
			"----- ${val}.nexusRepositoryInfraestructuraId: ${nexusRepositoryInfraestructuraId}\n" +
			"----- ${val}.nexusRepositoryDesarrolloId: ${nexusRepositoryDesarrolloId}\n" +
			"----- ${val}.nexusRepositoryAppsId: ${nexusRepositoryAppsId}\n"
		return resul
	}
	
	def genUrlNexusArtifactJar(nexusRepo, groupIdArtifact, artifactId, version) {
		def resul="${urlNexus}/${nexusRepo}/" + groupIdArtifact.replace(".", "/") + "/" +
			"${artifactId}/${version}/${artifactId}-${version}-jar.jar"
		return resul
	}
	
	def genUrlNexusArtifactWar(nexusRepo, groupIdArtifact, artifactId, version) {
		def resul="${urlNexus}/${nexusRepo}/" + groupIdArtifact.replace(".", "/") + "/" +
			"${artifactId}/${version}/${artifactId}-${version}-war.war"
		return resul
	}

}

