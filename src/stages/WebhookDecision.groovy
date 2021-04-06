package stages

import frmwork.StagesTypes

/**
 * Fase que toma decisión según webhook y estado
 * @author jpalm
 *
 */
class WebhookDecision extends Stage {

	WebhookDecision(Script scp) {
		super(scp)
	}
	
	
	void execute() {
		
		script.registry.init()
        script.env.setProperty("TRIGGER", script.env.getProperty("GITLAB_OBJECT_KIND")) 
        script.log.info "EVENTO desencadenador: ${script.TRIGGER}"

        if (script.TRIGGER == "tag_push"){
            script.log.info "Llamada de webhook por tag"
            script.env.setProperty("TAG_NAME", script.env.getProperty("TAG_NAME"))

            String[] str = script.TAG_NAME.split("-")
            def estado = str [1]

            if(!estado.contains("RC")){
                script.log.info "El webhook ha sido por un tag y el estado no es RC. Parar ejecución."
            }
        }	
        	        	
	
	}
}