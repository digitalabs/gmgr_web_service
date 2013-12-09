package com.pedigreeimport.restjersey;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.util.Debug;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
*
* @author KMahipus
*/
public class Editor {
	
	private static PedigreeDataManager pedigreeManager;
	private static ManagerFactory factory;
	private static JSONObject obj = new JSONObject();
	
	public static void main(String args[]) throws IOException, MiddlewareQueryException, ConfigException, URISyntaxException, JSONException{
		//int inputGID=50533;
		//JSONObject data = null;
		//searchAllGermplasm();
		//searchAllGermplasmList();
		//JSONObject data = 
		//JSONObject json_array = (JSONObject) data;
		//String gid = (String) json_array.get("GID");
		showMore2(50533);
	}
	
	/**
	 * @throws IOException
	 * @throws MiddlewareQueryException
	 * @throws URISyntaxException 
	 * @throws ConfigException 
	 * @throws JSONException 
	 */
	public static void show_germplasm_details(JSONObject data) throws IOException, MiddlewareQueryException, ConfigException, URISyntaxException, JSONException {
		
		DatabaseConnectionParameters local = new DatabaseConnectionParameters("localhost", "3306", "local", "root", "");
	    DatabaseConnectionParameters central = new DatabaseConnectionParameters("localhost", "3306", "central", "root", "");
	    factory = new ManagerFactory(local, central);
	    pedigreeManager = factory.getPedigreeDataManager();
	    
	    ManagerFactory fac = new Config().configDB();
		GermplasmDataManager man = fac.getGermplasmDataManager();
	    
	    JSONObject json_array = (JSONObject) data;
	    String gid = (String) json_array.get("GID");
	    
	    Debug.println(0, gid);
	    System.out.print("Yes");
	    
	    Integer gidtmp = Integer.valueOf(50533);
	    List<Name> names = man.getNamesByGID(gidtmp, null, null);
        Debug.println(0, "testGetNamesByGID(" + gidtmp + ") RESULTS: " + names);
	    
	}
	
	public static void showMore2(int gidtmp) throws IOException, MiddlewareQueryException, ConfigException, URISyntaxException, JSONException {
		
		DatabaseConnectionParameters local = new DatabaseConnectionParameters("localhost", "3306", "local", "root", "");
	    DatabaseConnectionParameters central = new DatabaseConnectionParameters("localhost", "3306", "central", "root", "");
	    factory = new ManagerFactory(local, central);
	    pedigreeManager = factory.getPedigreeDataManager();
	    
	    ManagerFactory fac = new Config().configDB();
		GermplasmDataManager man = fac.getGermplasmDataManager();
	    
	    Integer gid = Integer.valueOf(50533);
        Integer status = Integer.valueOf(8);
        GermplasmNameType type = GermplasmNameType.INTERNATIONAL_TESTING_NUMBER;
        List<Name> names = man.getNamesByGID(gid, status, type);
        System.out.println("testGetNamesByGIDWithStatusAndType(gid=" + gid + ", status" + status + ", type=" + type + ") RESULTS: " + names);
        
        for(int c=0;c<names.size();c++)
        {
        	System.out.println(names.get(c).getTypeId());
        	
        	
        }
	    
	}
	
	/**
	 * @throws IOException
	 * @throws MiddlewareQueryException
	 * @throws URISyntaxException 
	 * @throws ConfigException 
	 * @throws JSONException 
	 */
	public static void searchAllGermplasm(JSONObject data) throws IOException, MiddlewareQueryException, ConfigException, URISyntaxException, JSONException {
		
		 
		 JSONObject json_array = (JSONObject) data;
		 String gid = (String) json_array.get("GID");
		 String level = (String) json_array.get("LEVEL");
		
		 DatabaseConnectionParameters local = new DatabaseConnectionParameters("localhost", "3306", "local", "root", "");
	     DatabaseConnectionParameters central = new DatabaseConnectionParameters("localhost", "3306", "central", "root", "");
	     factory = new ManagerFactory(local, central);
	     pedigreeManager = factory.getPedigreeDataManager();
	     
	     
	        Boolean includeDerivativeLines = false;
	        
	        GermplasmPedigreeTree germplasmPedigreeTree = pedigreeManager.generatePedigreeTree(Integer.parseInt(gid), Integer.parseInt(level), includeDerivativeLines);
	        Debug.println(0, "generatePedigreeTree(" + gid + ", " + Integer.parseInt(level) + ", " + includeDerivativeLines +")");
	        //Debug.println(0, renderNode(germplasmPedigreeTree.getRoot(),""));
	        renderNode(germplasmPedigreeTree.getRoot(),"");
            factory.close();
           
	 }
	
	public static void println(int indent, String s) 
	{
        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
            obj.put("gid",s);
        }
        System.out.println(s);
        
    }
		
	
	//
	private static void printNode(GermplasmPedigreeTreeNode node, int level) throws IOException {
		
		Writer out = new BufferedWriter(new OutputStreamWriter(
				  new FileOutputStream("E:/xampp/htdocs/GMGR/json_files/tree.json"),"UTF-8"));
		
        StringBuffer tabs = new StringBuffer();
        
        
        for (int ctr = 1; ctr < level; ctr++) {
            tabs.append("\t");
        }
        
        //if(level!=1)
        	//System.out.println(tabs.toString()+"\""+"children"+"\""+":[");
        Debug.println(0,tabs.toString()+"{");
        out.append(tabs.toString()+"{");
        
        String name = node.getGermplasm().getPreferredName() != null ? node.getGermplasm().getPreferredName().getNval() : null;
        //Debug.println(0, tabs.toString() + node.getGermplasm().getGid() + " : " + name);
        
        Debug.println(0, tabs.toString() + " \"name\" : " +" \""+ name +" \",");
        Debug.println(0, tabs.toString() + " \"gid\" : " +" \""+ node.getGermplasm().getGid()+" \",");
        Debug.println(0, tabs.toString() + " \"date\" : " +" \""+ node.getGermplasm().getGdate()+" \",");
        Debug.println(0, tabs.toString() + " \"gpid1\" : " +" \""+ node.getGermplasm().getGpid1()+" \",");
        Debug.println(0, tabs.toString() + " \"gpid2\" : " +" \""+ node.getGermplasm().getGpid2()+" \",");
        Debug.println(0, tabs.toString() + " \"id\" : " +" \""+ node.getGermplasm().getGid()+" \",");
        if(!node.getLinkedNodes().isEmpty()) Debug.println(0, tabs.toString() + " \"layer\" : " +" \""+ (level-1)+" \",");
        else Debug.println(0, tabs.toString() + " \"layer\" : " +" \""+ (level-1)+" \"");
        if(!node.getLinkedNodes().isEmpty()) System.out.println(tabs.toString()+" \""+"children"+"\""+":[");
        
        out.append(tabs.toString() + " \"name\" : " +" \""+ name +" \",");
        out.append(tabs.toString() + " \"gid\" : " +" \""+ node.getGermplasm().getGid()+" \",");
        out.append(tabs.toString() + " \"date\" : " +" \""+ node.getGermplasm().getGdate()+" \",");
        out.append(tabs.toString() + " \"gpid1\" : " +" \""+ node.getGermplasm().getGpid1()+" \",");
        out.append(tabs.toString() + " \"gpid2\" : " +" \""+ node.getGermplasm().getGpid2()+" \",");
        out.append(tabs.toString() + " \"id\" : " +" \""+ node.getGermplasm().getGid()+" \",");
        if(!node.getLinkedNodes().isEmpty()) out.append(tabs.toString() + " \"layer\" : " +" \""+ (level-1)+" \",");
        else out.append(tabs.toString() + " \"layer\" : " +" \""+ (level-1)+" \"");
        if(!node.getLinkedNodes().isEmpty()) out.append(tabs.toString()+" \""+"children"+"\""+":[");
        
        for (GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {
        	
            printNode(parent, level + 1);
           // System.out.println(tabs.toString()+"children:[");
        }
        
        if(!node.getLinkedNodes().isEmpty())
        { 
        	Debug.println(0,tabs.toString()+"]");
        	out.append(tabs.toString()+"]");
        }
        
        out.close();
        
    }
	
	
	//render the node - print
	@SuppressWarnings("unchecked")
	private static String renderNode(GermplasmPedigreeTreeNode node, String prefix) throws IOException, MiddlewareQueryException{
		
		JSONObject obj = new JSONObject();
		JSONArray jsonNodesArray = new JSONArray();
		
		Writer out = new BufferedWriter(new OutputStreamWriter(
				     new FileOutputStream("E:/xampp/htdocs/GMGR/json_files/tree.json"),"UTF-8"));
		
		ManagerFactory fac = new Config().configDB();
		GermplasmDataManager man = fac.getGermplasmDataManager();
		
		String name = node.getGermplasm().getPreferredName() != null ? node.getGermplasm().getPreferredName().getNval() : null;
		Method meth = man.getMethodByID(node.getGermplasm().getMethodId());
		Location loc = man.getLocationByID(node.getGermplasm().getLocationId());
		Bibref bibref = man.getBibliographicReferenceByID(node.getGermplasm().getReferenceId());
		Integer status = Integer.valueOf(8);
        GermplasmNameType type = GermplasmNameType.INTERNATIONAL_TESTING_NUMBER;
		
		List<Name> names = man.getNamesByGID(node.getGermplasm().getGid(), null, null);
		Location loc2 = null;
		
		Integer cid = loc.getCntryid();
		Country cnty = man.getCountryById(cid);
        String outputString = "";
        
        if(node != null)
        {
        	
             outputString = " " + prefix + "{\n   " + prefix + " \"gid\" : \" " + node.getGermplasm().getGid() +"\",\n"
                          + "   " + prefix + " \"name\" : \" " + name +"\",\n"
                          + "   " + prefix + " \"date\" : \" " + node.getGermplasm().getGdate() +"\",\n"
                          + "   " + prefix + " \"id\" : \" " + node.getGermplasm().getGid() +"\",\n"
                          + "   " + prefix + " \"methodname\" : \" " + meth.getMname() +"\",\n"
                          + "   " + prefix + " \"methodtype\" : \" " + meth.getMtype() +"\",\n"
                          + "   " + prefix + " \"location\" : \" " + loc.getLname() +"\",\n"
                          + "   " + prefix + " \"country\" : \" " + cnty.getIsoabbr() +"\",\n"
                          + "   " + prefix + " \"ref\" : \" " + bibref.getAnalyt() +"\",\n";
             
             		for(int cntr=0;cntr<names.size();cntr++)
             		{
             			  loc2 = man.getLocationByID(names.get(cntr).getLocationId());	
                          outputString = outputString 
                          + "   " + prefix + " \"dates"+cntr+"\" : \" " + names.get(cntr).getNdate() +"\",\n"
                          + "   " + prefix + " \"name"+cntr+"\" : \" " + names.get(cntr).getNval() +"\",\n"
                          + "   " + prefix + " \"ntype"+cntr+"\" : \" " + names.get(cntr).getTypeId() +"\",\n"
                          + "   " + prefix + " \"nstat"+cntr+"\" : \" " + names.get(cntr).getNstat() +"\",\n"
                          + "   " + prefix + " \"loc"+cntr+"\" : \" " + loc2.getLname() +"\",\n";
                         
        			}
             		      outputString = outputString + "   " + prefix + " \"gpid1\" : \" " + node.getGermplasm().getGpid1() +"\",\n";
             		      
             if(!node.getLinkedNodes().isEmpty())
            	 outputString = outputString + " " + prefix + "   \""+"gpid2"+"\""+": \"" + node.getGermplasm().getGpid2() + "\",\n";	
             else
            	 outputString = outputString + " " + prefix + "   \""+"gpid2"+"\""+": \"" + node.getGermplasm().getGpid2() + "\"\n";	
             
             if(!node.getLinkedNodes().isEmpty()) 
            	 outputString = outputString + " " + prefix + "   \""+"children"+"\""+":[";
             
             for (GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) 
             {
                 outputString = outputString + "\n"
                         + renderNode(parent, prefix + "   ");
             }
             
              if(!node.getLinkedNodes().isEmpty())
                	 outputString = outputString + prefix + "\n " + prefix +"]";
                 
              outputString = outputString + prefix + "\n " + prefix +"}";
                 
              if(node.getLinkedNodes().isEmpty() )	
    	                outputString = outputString +",";
              
             
	         out.write(outputString);
	         out.close();
	        
         }
        
		 return outputString;
     }
}
