

/**
 * Log para sacar mensajes informativos
 *
 * @param message Cadena de mensaje
 */
void info(message) {
	echo( "INFO: " + message)
}
	
/**
 * Log para sacar mensajes tipo warning
 *
 * @param message Cadena de mensaje
 */
void warning(message) {
	echo( "WARNING: " + message)
}

def defaultIfInexistent(varNameExpr, defaultValue) {
	try {
		varNameExpr()
	} catch (exc) {
		defaultValue
	}
}

/**
 * Log para indicar que ha existido un error
 * @param message
 */
void err(message) {
		error(message)
}
return this
	
