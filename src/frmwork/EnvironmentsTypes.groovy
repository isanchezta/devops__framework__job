package frmwork

/**
 * Clase que encapsula los tipos de entornos de despliegue:
 * 
 * 	ERROR: entorno desconocido
 * 	BUILD: entorno de construcci�n
 * 	DES: entorno de integracion - desarrollo
 * 	PRE: entorno previos de explotaci�n (ejecuci�n de pruebas funcionales, QS, etc...)
 * 	PRO: entorno de explotaci�n
 *  HOT: entorno especial en el cual se compila pero no se despliega en el entorno de integracion, por lo tanto no se invoca a ningun script
 * 
 *  Desde el punto de vista de las clases se tendr� que usar el tipo enumerado, pero en los scripts
 *  del pipe se tiene que utilizar los valores concretos de la clase, como miembros de la misma
 *  
 * @author jpalmero
 *
 */
class EnvironmentsTypes {
	public enum Value {
		ERROR, BUILD, DES, HOT, INT, PRE, PRO
	}
	
	// Validos para se llamados por el script
	public final EnvironmentsTypes.Value ERROR = EnvironmentsTypes.Value.ERROR
	public final EnvironmentsTypes.Value BUILD = EnvironmentsTypes.Value.BUILD
	public final EnvironmentsTypes.Value DES = EnvironmentsTypes.Value.DES
	public final EnvironmentsTypes.Value INT = EnvironmentsTypes.Value.INT
	public final EnvironmentsTypes.Value PRE = EnvironmentsTypes.Value.PRE
	public final EnvironmentsTypes.Value PRO = EnvironmentsTypes.Value.PRO
	public final EnvironmentsTypes.Value HOT = EnvironmentsTypes.Value.HOT

}
 
