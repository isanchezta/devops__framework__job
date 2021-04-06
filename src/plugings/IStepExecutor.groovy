package plugings

/**
 * Interfaz: plugings de jenkins accesible desde la libreria JK
 * 
 *  Uso: 
 *  
 *  	1) Desde el Jenkinsfile: 
 *  		registry.init()
 *  
 *  	2) Invocacion desde las clases una vez registrado
 *  
 *  	StepExecutor plugings = DefaultContext.getStepExecutor()
 *  	plugings.sh ("ls -al")
 *   
 * @author jpalmero
 *
 */
interface IStepExecutor {
	/**
	 * Invocar a bash 
	 * 
	 * @param command
	 * @return
	 */
    int sh(String command)
	
	/**
	 * Invocar a la rutina de error de JK
	 * 
	 * @param message
	 */
    void error(String message)
	
	/**
	 * Invocacion de las operaciones de creacion de ZIP
	 *  
	 * @param zipFile
	 * @param dir
	 */
	void zip(String zipFile, String dir)
	
	/**
	 * Mostrar un mesaje en el log de JK
	 * 
	 * @param command
	 */
    void echo(String command)
	
	/**
	 * Cambiar el directorio actual de ejecucion desde del esclavo JK
	 * @param command
	 */
	void dir(String command)

}
