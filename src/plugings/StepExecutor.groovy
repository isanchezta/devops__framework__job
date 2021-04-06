package plugings

/**
 * Implantaci�n de los plugings accesibles desde la liberia
 * @author jpalmero
 *
 */
class StepExecutor implements IStepExecutor {
    // this will be provided by the vars script and 
    // let's us access Jenkins steps
    private _steps 

    StepExecutor(steps) {
        this._steps = steps
    }

    @Override
    int sh(String command) {
        this._steps.sh returnStatus: true, script: "${command}"
    }

    @Override
    void error(String message) {
        this._steps.error(message)
    }
	
	@Override
	void echo(String message) {
		this._steps.echo(message)
	}
	
	
	@Override
	void dir(String command) {
		this._steps.dir(command)
	}
	

	
	
	@Override
	void zip(String zipFile, String dir) {
		this._steps.zip zipFile: zipFile, dir: dir
	}
}
