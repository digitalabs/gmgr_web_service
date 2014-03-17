package com.pedigreeimport.restjersey;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;


public class Config {

	/*
	 * @param db_details format: 
	 *                   local_db_host,local_db_name,local_db_port,local_db_username,local_db_password,
	 * 					 central_db_host,central_db_name, central_db_port,central_db_username, central_db_password
	 * 
	 * Note: please change the database parameters according to what is available in your workstation,
		 
			 *  db_details.get(0): local host
			 *  db_details.get(1): local database name
			 *  db_details.get(2): local database port
			 *  db_details.get(3): local database username
			 *  db_details.get(4): local database password
			 
			 *  db_details.get(5): central database host
			 *  db_details.get(6): central database name
			 *  db_details.get(7): central database port
			 *  db_details.get(8): central database username
			 *  db_details.get(9): central database password
			
	*/
	public ManagerFactory configDB(List<String> db_details){
		
		System.out.println("db_details:"+db_details);
		
		System.out.println("local password:"+db_details.get(4));
		System.out.println("central password:"+db_details.get(9));
		System.out.println("central host: "+db_details.get(5));

	    if(db_details.contains( "undefined") || db_details.get(0)=="" || db_details.get(0) == null){
	    	db_details.clear();
	    }
		if(db_details.size() != 0){
			
			if( (db_details.get(4).equals(null) || db_details.get(4).equals("")) ){
				System.out.println("enter here");
				db_details.set(4, "");
			
			}

			if((db_details.get(9).equals(null) || db_details.get(9).equals(""))){
				db_details.set(9, "");
			}
			DatabaseConnectionParameters local = new DatabaseConnectionParameters(
					db_details.get(0), db_details.get(2), db_details.get(1), db_details.get(3), db_details.get(4));
			DatabaseConnectionParameters central = new DatabaseConnectionParameters(
					db_details.get(5), db_details.get(7), db_details.get(6), db_details.get(8), db_details.get(9));

		
			ManagerFactory factory = new ManagerFactory(local, central);
            /*
			DatabaseConnectionParameters local = new DatabaseConnectionParameters(
					"localhost", "3306", "local3", "root", "");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters(
					"localhost", "3306", "central", "root", "");
			ManagerFactory factory = new ManagerFactory(local, central);
			*/
			return factory;
			
		}else{

            /*
			DatabaseConnectionParameters local = new DatabaseConnectionParameters(
					"localhost", "3306", "local3", "root","");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters(
					"localhost", "3306", "central", "root","");
			ManagerFactory factory = new ManagerFactory(local, central);
            */
			DatabaseConnectionParameters local = new DatabaseConnectionParameters(
					"127.0.0.1", "3306", "local", "datasourceuser", "ici$rule$");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters(
					"127.0.0.1", "3306", "iris_mysiam_20121002", "datasourceuser", "ici$rule$");
			ManagerFactory factory = new ManagerFactory(local, central);

            
			/*DatabaseConnectionParameters local = new DatabaseConnectionParameters(

					"localhost", "3306", "gmgr_local", "gmgruser", "gmgrpass");
			DatabaseConnectionParameters central = new DatabaseConnectionParameters(
					"localhost", "3306", "gmgr_local", "gmgruser", "gmgrpass");
			
			ManagerFactory factory = new ManagerFactory(local, central);
		
			*/
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
