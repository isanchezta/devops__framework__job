package stages

import frmwork.StagesTypes

/**
 * Clase que ejecuta la descompresion de los zips en el directorio de descarga
 * 
 * @author jpalm
 *
 */
class UnzipArtefactos extends Stage{
	
	UnzipArtefactos(Script scp) {
		super(scp)
		stageType = StagesTypes.Value.DEPLOY
		
	}
	
	void execute() {
		// En esta fase se descomprime todo aquello descargado
		// en d
		script.registry.init()
		script.zipper.unpacking("d/$script.PRJ/$script.VERSION" )
	}
}
