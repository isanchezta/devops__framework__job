import plugings.ContextRegistry


/**
 * Funcion de inicializacion del context para usar los plugings desde los groovy shared libraries
 * @return
 */
static void init() {
	ContextRegistry.registerDefaultContext(this)
}


return this



