package frmwork

/**
 * Clase que encapsula los estados del aplicativo
 * 
 *	ERROR					: No se conoce el estado 
 *	KO	(Kick Off)			:   arranque de la version
 *  CC	(Code Complete) 	: fin de desarrollos para la version indicada
 *  RC	(Release Candidate) : version candidata para pasar a pruebas QA, UAT
 *  RFS	(Ready for Service) : version candidata para desplegarse en explotacion
 *	PRO						: Version en explotacion
 * @author jpalmero
 *
 */
public class StagesTypes {
	public enum Value {	// Es pereferible indicar los stages por orden de ejecucion FRAMEWORK < BUILD < DEPLOY
		FRAMEWORK, 		// Stage relativas a la gestion del Framework Acciona (evaluacion ramas, estados, versiones...)
		SCM,			// Stage descarga repositorio
		CODE_REVIEW,	// Stage de revision de codigo
		BUILD, 			// Stage relativas a compilacion
		DEPLOY 			// Stage relativas a tareas de despliegue
	}
			
	public final StagesTypes.Value BUILD = StagesTypes.Value.BUILD
	public final StagesTypes.Value SCM = StagesTypes.Value.SCM
	public final StagesTypes.Value DEPLOY = StagesTypes.Value.DEPLOY
	public final StagesTypes.Value FRAMEWORK = StagesTypes.Value.FRAMEWORK
	public final StagesTypes.Value CODE_REVIEW = StagesTypes.Value.CODE_REVIEW
}


		

