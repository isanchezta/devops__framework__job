package stages

import frmwork.StagesTypes

/**
 * Fase que toma decisión según el estado y la rama
 * @author jpalm
 *
 */
class SelectOption extends Stage {

	SelectOption(Script scp) {
		super(scp)
	}
	
	
	void execute() {
		
		script.registry.init()
        script.log.info "EVENTO desencadenador: ${script.TRIGGER}"
        script.log.info "RAMA DESCARGADA: ${script.BRANCH}"
        script.log.info "ESTADO obtenido del TAG: ${script.ESTADO}"		

        //Opción 1: Llamada de webhook por push
        if (script.TRIGGER == "push"){
            script.log.info "Llamada de webhook por push"

            if((script.BRANCH == "develop") && (script.ESTADO == "SNAPSHOT")){
                script.log.info "Continúa la compilación"
            }
            else if((!(script.BRANCH.contains("release"))) || (script.BRANCH != "develop")){
                script.log.info "Error rama"
            }

        }

        //Opción 2: Llamada de webhook por tag
        if (script.TRIGGER == "tag_push"){
            script.log.info "Llamada de webhook por tag"

            if((script.BRANCH == "develop") && (script.ESTADO == "SNAPSHOT")){
                script.log.info "No hacer nada"
            }else if((script.BRANCH == "develop") && (script.ESTADO == "RC")){
                script.log.info "Crear rama de release"
            }else if((script.BRANCH.contains("release")) && (script.ESTADO == "SNAPSHOT")){
                script.log.info "Error relación rama/estado"
            }else if((!(script.BRANCH.contains("release"))) || (script.BRANCH != "develop")){
                script.log.info "Error rama"
            }
            
        }

        //Opción 3: Arranque manual del job
        if (script.TRIGGER == "none"){
            script.log.info "Arranque manual del job"
        }
        	        	
	
	}
}