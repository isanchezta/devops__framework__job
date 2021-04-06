// Funcion de arranque del proceso de despliegue de desarrollo
// devuelve 0 si OK  y <>0 si ERROR
def start(pathVersion , def pathConf="") {
	log.info "FASE DES: ${pathVersion} , ${pathConf}"
	
	sh "ls -al ${WORKSPACE}"
	sh "ls -al ${pathConf}"
	sh "ls -al ${pathVersion}"

	return 0; 
}

return this;