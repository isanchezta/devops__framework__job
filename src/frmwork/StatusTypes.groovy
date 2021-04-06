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
public class StatusTypes {
	public enum Value {
		ERROR, 	// No se conoce el estado 
		KO, 		// Kick Off: arranque de la version
		RT,			// indica que se tienen que hacer pruebas de cobertura de codigo
		CC, 		// Code Complete: fin de desarrollos para la versi�n indicada
		RC, 		// Release Candidate: versi�n candidata para pasar a pruebas QA, UAT
		RFS, 		// Ready for Service: versi�n candidata para desplegarse en explotaci�n
		PRO,		// Versi�n en explotaci�n
		SNAPSHOT //Versión en integración
	}
			
	public final StatusTypes.Value ERROR = StatusTypes.Value.ERROR
	public final StatusTypes.Value KO = StatusTypes.Value.KO
	public final StatusTypes.Value CC = StatusTypes.Value.CC
	public final StatusTypes.Value RC = StatusTypes.Value.RC
	public final StatusTypes.Value RFS = StatusTypes.Value.RFS
	public final StatusTypes.Value PRO = StatusTypes.Value.PRO
	public final StatusTypes.Value RT = StatusTypes.Value.RT
}


		

