package com.pedigreeimport.restjersey;

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;

public class Manager {

	
	DatabaseConnectionParameters local = new DatabaseConnectionParameters("localhost", "3306", "local", "root", "");
	ManagerFactory factory = new ManagerFactory(local, null);
}
