		
	/**
	 * Construye la imagen de Docker basado en el Dockerfile que se encuentra en la raiz del proyecto. 
	 * El tag por defecto será "latest"
	 *
	 * @param credentialId 
	 * @param registry 
	 */
	def login(credentialId = "${env.ARTIFACTORY_CREDENTIALSID}", registry = "articloudpro-${env.ARTIFACTORY_REGISTRY}.jfrog.io"){
		withCredentials([usernamePassword(credentialsId: "${credentialId}", passwordVariable: 'pass', usernameVariable: 'user')]) {
			sh "docker login ${registry} -u ${user} -p ${pass}"
		}
	}
	
	/**
	 * Construye la imagen de Docker basado en el Dockerfile que se encuentra en la raiz del proyecto. 
	 * El tag por defecto será "latest"
	 */
	def logout(){
		sh "docker logout"
	}
	
	/**
	 * Construye la imagen de Docker basado en el Dockerfile que se encuentra en la raiz del proyecto. 
	 * El tag por defecto será "latest"
	 *
	 * @param args Posibles argumentos necesarios para la construccion.
	 *		  Deben estar en el formato --build-arg foo="bar"
	 * @return image Imagen resultante de la construcción
	 */
    def buildImage(String args = "", String imageName = "${env.PRJ}"){
		log.info("buildImage: " + imageName.toLowerCase() + " - " + args);
		image = docker.build(imageName.toLowerCase(), args + " .")
        return image
    }

	/**
	 * Pushea la imagen recibida al registry definido en la variable de Jenkinsfile ARTIFACTORY_REGISTRY
	 *  
	 * @param image Imagen que hay que pushear
	 * @param tag Tag que recibirá la imagen a subir al registry
	 */
	void pushImage(image, String tag, String imageName = "${env.PRJ}") {
		def protocol = "https://"
        def registry_url = "articloudpro-${env.ARTIFACTORY_REGISTRY}.jfrog.io"
   		log.info("pushImage: " + registry_url + " - " + tag );
		docker.withRegistry(protocol + registry_url, "${env.ARTIFACTORY_CREDENTIALSID}") {
            image.push(tag)
        }
		try{
			deleteImage(registry_url + "/" + imageName + ":" + tag)
			deleteImage(imageName + ":latest")
		}catch(Exception e){
			
		}
	}

	/**
	 * Aúna los dos métodos anteriores
	 *
	 * @param name nombre de la imagen a borrar
	 */
	void deleteImage(String name){
		sh "docker rmi ${name.toLowerCase()}"
	}

	/**
	 * Aúna los dos métodos anteriores
	 *
	 * @param args Posibles argumentos necesarios para la construccion.
	 *		  Deben estar en el formato --build-arg foo="bar"	 * @param idBuild
	 * @param tag Tag que recibirá la imagen a subir al registry. Por defecto es el tag de la rama
	 */
    def buildAndPushImage(String args = "", String tag = new frmwork.Utils().gitDescribe().split("-")[0], String imageName = "${env.PRJ}"){
        image = buildImage(args, imageName)
        pushImage(image, tag, imageName)
    }