package com.pedigreeimport.restjersey;

import java.util.ArrayList;

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;

import antlr.collections.List;

public class Config {

	/*
	 * @param db_details format: 
	 *                   local_db_name,local_db_port,local_db_username,
	 * 					 central_db_name, central_db_port,central_db_username
	 * 
	 * Note: please change the database parameters according to what is available in your workstation,
			 * especially the db_details.get(2) which refers to the database username, i.e. 'root'
			 *  db_details.get(1) & db_details.get(4) are port numbers of local and central, respectively
			 *  db_details.get(0): local database
			 *  db_details.get(3): central database
			 *  db_details.get(2): local database username
			 *  db_details.get(5): central database username
			
	*/
	public ManagerFactory configDB(java.util.List<String> db_details){
		
		System.out.println("db_details:"+db_details);
	
	    if(db_details.contains( "undefined") || db_details.get(0)==""){
	    	db_details.clear();
	    }
		if(db_details.size() != 0){
			
			
			
			DatabaseConnectionParameters local = new DatabaseConnectionParameters(
					"localhost", db_details.get(1), db_details.get(0), db_details.get(2), "");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters(
					"localhost", db_details.get(4), db_details.get(3), db_details.get(5), "");
			ManagerFactory factory = new ManagerFactory(local, central);
            
			/*
			DatabaseConnectionParameters local = new DatabaseConnectionParameters(
					"localhost", db_details.get(1), "local2", "root", "");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters(
					"localhost", db_details.get(4), "central6", "root", "");
			ManagerFactory factory = new ManagerFactory(local, central);
            */
			return factory;
			
		}else{
			DatabaseConnectionParameters local = new DatabaseConnectionParameters(
					"localhost", "3306", "local2", "root", "");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters(
					"localhost", "3306", "central6", "root", "");
			ManagerFactory factory = new ManagerFactory(local, central);

			return factory;
		}
		
	}

	
/*
	public ManagerFactory configDB(){
		DatabaseConnectionParameters local = new DatabaseConnectionParameters(
				"127.0.0.1", "3306", "local", "datasourceuser", "ici$rule$");
		DatabaseConnectionParameters central = new DatabaseConnectionParameters(
				"127.0.0.1", "3306", "iris_mysiam_20121002", "datasourceuser", "ici$rule$");
		ManagerFactory factory = new ManagerFactory(local, central);
		
		return factory;
	}
	


	public ManagerFactory configDB(){
		DatabaseConnectionParameters local = new DatabaseConnectionParameters(
				"127.0.0.1", "5528", "local", "datasourceuser", "ici$rule$");
		DatabaseConnectionParameters central = new DatabaseConnectionParameters(
				"127.0.0.1", "5528", "iris_myisam_20121002", "datasourceuser", "ici$rule$");
		ManagerFactory factory = new ManagerFactory(local, central);
		
		return factory;
	}
	*/
	
}
