package com.pedigreeimport.restjersey;

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;

public class Config {

	public ManagerFactory configDB(){
		DatabaseConnectionParameters local = new DatabaseConnectionParameters(
				"localhost", "3306", "local", "root", "");
		DatabaseConnectionParameters central = new DatabaseConnectionParameters(
				"localhost", "3306", "central", "root", "");
		ManagerFactory factory = new ManagerFactory(local, central);
		
		return factory;
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
