package stages

//@Grapes(
//	@Grab(group='org.codehaus.groovy', module='groovy-xml', version='2.4.18')
//)

import frmwork.ModelDB
import frmwork.StagesTypes
import groovy.util.XmlSlurper

import hudson.model.Action
import org.jenkinsci.plugins.workflow.graph.FlowNode
import org.jenkinsci.plugins.workflow.cps.nodes.StepStartNode
import org.jenkinsci.plugins.workflow.actions.LabelAction


abstract  class Stage {
	
	
	protected Script script;
	StagesTypes.Value stageType
	
	Stage(Script scp) {
		this.script = scp;
		
		
		
		
	}
	
	Script getScript() {
		return script
	}
 
	
	def getDbConnect() {
		script.log.info "getDbConnect()"
		def dbXml = script.libraryResource 'databaseDeploy.xml'
		def xml = new XmlSlurper().parseText(dbXml)
		String _hostname = "${xml.hostname}"
		int _port = "${xml.port}" as Integer
		String _database = "${xml.database}"
		String _username= "${xml.username}"
		String _password= "${xml.password}"
		script.log.info "DB->${xml.hostname}:${xml.port}/${xml.database}"
		return new ModelDB(hostname: _hostname, port: _port, database: _database, username: _username, password:_password, script:script  )
	}

	abstract protected void execute() 
	
	/**
	 * Se tendr� un control centralizado que podr� revisar si una fase se tiene que ejecutar o no
	 * Si una fase siempre se tiene que ejecutar se podr� reescribir este metodo en el hijo para devolver siempre  true
	 * y no evaluar la del padre.
	 * 
	 * @return
	 */
	boolean isExecute() {
		// Si este job tiene que saltarse las fases de despliegue es porque tiene habilitado
		// el parametro de BUILD_ONLY
		if(script.BUILD_ONLY=="true") {
			return stageType < StagesTypes.Value.DEPLOY;
		} 
		
		return true;
	}
	
	void executeStage() {
		// Actualizamos que la ultima fase que se ejecuto fue estra
		String currentStage = getStage(script.currentBuild)
		script.env.setProperty("ACTUAL_STAGE",  currentStage)
		execute()
		String oks = script.env.getProperty("STAGES_OK")
		if(oks==null)
			oks="";
		script.env.setProperty("STAGES_OK", oks +"\n" + currentStage)
		script.env.setProperty("LAST_STAGE",  currentStage)
	}
	void exec() {
		if(isExecute()) 
			executeStage()
		else
			script.log.info "****** FASE TIPO ${stageType} NO SE TIENE QUE EJECUTAR ******"
	}
	
	def getStage(currentBuild){
		def build = currentBuild.getRawBuild()
		def execution = build.getExecution()
		def executionHeads = execution.getCurrentHeads()
		def stepStartNode = getStepStartNode(executionHeads)
	
		if(stepStartNode){
			return stepStartNode.getDisplayName()
		}
	}
	
	def getStepStartNode(List<FlowNode> flowNodes){
		def currentFlowNode = null
		def labelAction = null
	
		for (FlowNode flowNode: flowNodes){
			currentFlowNode = flowNode
			labelAction = false
	
			if (flowNode instanceof StepStartNode){
				labelAction = hasLabelAction(flowNode)
			}
	
			if (labelAction){
				return flowNode
			}
		}
	
		if (currentFlowNode == null) {
			return null
		}
	
		return getStepStartNode(currentFlowNode.getParents())
	}
	
	def hasLabelAction(FlowNode flowNode){
		def actions = flowNode.getActions()
	
		for (Action action: actions){
			if (action instanceof LabelAction) {
				return true
			}
		}
	
		return false
	}
}
