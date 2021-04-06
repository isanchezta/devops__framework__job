package frmwork

//@Grapes([
//	@Grab(group='org.postgresql', module='postgresql', version='42.2.5'),
//	@Grab(group='org.codehaus.groovy', module='groovy-sql', version='2.4.18')
//])


import groovy.sql.Sql
import java.util.ServiceLoader;
import java.sql.Driver;
import java.sql.DriverManager;
//import org.postgresql.ds.PGSimpleDataSource
import hudson.model.*


/**
 * Acceso al modelo de db
 * 
 * Se integra en este puento los acceso a la BD, con las diferentes acciones
 * 
 * @author jpalm
 *
 */
class ModelDB {
	String hostname
	int port
	String database
	String username
	String password
	def script
	
	def connect() {
		script.log.info "ModelDB:connect(${hostname}, ${port}, ${database})"
		//def ds = new PGSimpleDataSource()
		//	database: 'jdbc:mysql://svdevslvlin2.acciona.int:5432/jenkinsdb', user: 'postgres', password: 'docker')
		
		//ds.setServerName(hostname );  // The value `localhost` means the Postgres cluster running locally on the same machine.
		//ds.setDatabaseName( database);   // A connection to Postgres must be made to a specific database rather than to the server as a whole. You likely have an initial database created named `public`.
		//ds.setUser( username );         // Or use the super-user 'postgres' for user name if you installed Postgres with defaults and have not yet created user(s) for your application.
		//ds.setPassword( password );     //
		//ds.setPortNumber(port)
		
		//return new Sql(ds)
		return null
	}
	
	def executeSql(String stmt) {
		script.log.info  "executeSql(${stmt})"
		def sql = connect();
		sql.execute stmt
		script.log.info  "Ejecutada sql"
	}
	
	String getTimestamp() {
		String res
		script.log.info  "getTimestamp()"
		def sql = connect();
		//sql.rows("select cast(current_timestamp as varchar) as now").each { row -> res = row.now }
		
		return res
	}
	
	def queryVersionInfraByEnvioremnt(String infraName, String env) {
	
		script.log.info ("queryVersionInfraByEnvioremnt( ${infraName}, ${env})")
		
		String stmt = 
""" 
 SELECT i.infraestructure i, i.version v, i.entorno e FROM \"INFRAESTRUCTURE_DEPLOY\" i
 JOIN
 (
  SELECT infraestructure, max(f_Alta) f_alta FROM "INFRAESTRUCTURE_DEPLOY"
  GROUP BY infraestructure, f_Alta
 ) s ON i.infraestructure = s.infraestructure AND i.f_alta = s.f_alta
 WHERE i.status = 'SUCCESS' AND i.infraestructure = '${infraName}' and i.entorno = '${env}'
"""
		def sql = connect();
		String res = ""
		sql.rows(stmt).each { row -> res = row.v }
		return res
	}
}
