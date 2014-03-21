package backend.pedigreeviewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.util.Debug;
import org.json.simple.JSONObject;

public class SearchGid {
	private static int counter = 0;
	private int cnt;
	private static List<Integer> counterArray = new ArrayList<Integer>();
	
	public JSONObject main(ManagerFactory factory,JSONObject json_array, JSONObject outputTree) throws NumberFormatException, MiddlewareQueryException, IOException{
		GermplasmDataManager man = factory.getGermplasmDataManager();
		List<Name> names = new ArrayList<Name>();// null;//man.getNamesByGID(node.getGermplasm().getGid(),
													// null, null);
		Location loc2 = new Location();
		List<Attribute> attributes = new ArrayList<Attribute>();// man.getAttributesByGID(node.getGermplasm().getGid());
		Method meth = new Method();// man.getMethodByID(node.getGermplasm().getMethodId());
		Location loc = new Location();// man.getLocationByID(node.getGermplasm().getLocationId());
		Bibref bibref = new Bibref();// man.getBibliographicReferenceByID(node.getGermplasm().getReferenceId());

		Integer cid = 0;// loc.getCntryid();
		Country cnty = new Country();// man.getCountryById(cid);a

		// JSONObject json_array = (JSONObject) data;
		String gid = (String) json_array.get("GID");
		String level = (String) json_array.get("LEVEL");
		String sel = (String) json_array.get("SEL");
		

		String outputString = "";

		// ManagerFactory factory = new Config().configDB();
		System.out.println("gid: " + gid);
		System.out.println("level: " + level);
		System.out.println("sel: " + sel);
		cnt = counter++;
		Boolean bool;
		if (Integer.parseInt(sel) == 1) {
			bool = true;
		} else
			bool = false;

		if (cnt % 2 == 1) {

			PedigreeDataManager pedigreeManager = factory
					.getPedigreeDataManager();

			Debug.println(10, "GID = " + Integer.parseInt(gid) + ", level = "
					+ Integer.parseInt(level) + ":");
			GermplasmPedigreeTree tree = pedigreeManager.generatePedigreeTree(
					Integer.parseInt(gid), Integer.parseInt(level), bool);

			for (int i = 0; i < Integer.parseInt(level); i++) {
				counterArray.add(0);
			}

			if (tree != null) {
				outputString = outputString + "{";
				System.out.println("{");
				outputString = new SearchGid().printNode(outputString,
						tree.getRoot(), 1, names, loc2, attributes, cid, cnty,
						man, meth, bibref, loc);
				outputString = outputString + "\n}";
				System.out.println("}");
			}

			tree = null;
			pedigreeManager = null;

		}
		String found = "0";
		if (outputString.equals("")) {
			found = "1";
		}

		outputTree.put("tree", outputString);
		outputTree.put("found", found);

		System.out.println("Output: " + outputTree.get("tree"));

		counterArray.clear();
		names = null;
		loc2 = null;
		attributes = null;
		cid = null;
		cnty = null;
		meth = null;
		bibref = null;
		loc = null;
		
		return outputTree;
	}

	public String printNode(String outputString,GermplasmPedigreeTreeNode node, int level, List <Name> names, Location loc2, List <Attribute> attributes, int cid, Country cnty, GermplasmDataManager man, Method meth, Bibref bibref, Location loc) throws IOException, MiddlewareQueryException {
		
		StringBuffer tabs = new StringBuffer();
		String descBibref = "N/A";
		
		meth = man.getMethodByID(node.getGermplasm().getMethodId());
		loc = man.getLocationByID(node.getGermplasm().getLocationId());
		
		if(node.getGermplasm().getReferenceId()!=0){
			bibref = man.getBibliographicReferenceByID(node.getGermplasm().getReferenceId());
			descBibref = bibref.getAnalyt();
		}
		
		names = man.getNamesByGID(node.getGermplasm().getGid(), null, null);
		loc2 = null;
		attributes = man.getAttributesByGID(node.getGermplasm().getGid());
		
		cid = loc.getCntryid();
		cnty = man.getCountryById(cid);
		 
        for (int ctr = 1; ctr < level; ctr++) {
            tabs.append("\t");
        }
        
        counterArray.set(level-1, 0);
        String name = node.getGermplasm().getPreferredName() != null ? node.getGermplasm().getPreferredName().getNval() : null;
        
        String name2 = node.getGermplasm().getPreferredName() != null ? node.getGermplasm().getPreferredName().getNval() : null;
        
        if(!node.getLinkedNodes().isEmpty()){
        	Debug.println(0, tabs.toString() +"\"gid\" : \""+ node.getGermplasm().getGid() + "\",\n" + tabs.toString()+ "\"name\" : \""+ name +"\",\n" +tabs.toString()+ "\"layer\": \""+ (level-1) + "\",");
        	
        	if(name != null){
        		if(name.trim().toString().length() > 5)
	            {
	            	name2 = name.substring(0, Math.min(name.length(),6)) + "...";
	            }
        	}
        	
			String cn = "---";
			if(cnty != null)
        	{
        		cn = cnty.getIsoabbr();
        	}
			
        	outputString = outputString + "\n "+ tabs.toString() + tabs.toString() +"\"gid\" : \"" + node.getGermplasm().getGid() +"\",\n"
                    + tabs.toString() +"    \"name\" : \"" + name +"\",\n"
                    + tabs.toString() +"    \"name2\" : \"" + name2 +"\",\n"
                    + tabs.toString() +"    \"date\" : \"" + node.getGermplasm().getGdate() +"\",\n"
                    + tabs.toString() +"    \"id\" : \"" + node.getGermplasm().getGid() +"\",\n"
                    + tabs.toString() +"    \"layer\" : \"" + (level-1) +"\",\n"
                    + tabs.toString() +"    \"methodname\" : \"" + meth.getMname() +"\",\n"
                    + tabs.toString() +"    \"methodtype\" : \"" + meth.getMtype() +"\",\n"
                    + tabs.toString() +"    \"location\" : \"" + loc.getLname() +"\",\n"
                    + tabs.toString() +"    \"country\" : \"" + cn +"\",\n"
                    + tabs.toString() +"    \"ref\" : \"" + descBibref +"\",\n";
        		
        		if(meth.getMtype().equals("GEN"))
        		{
        			outputString = outputString + tabs.toString() +"    \"warning\" : \"" + "true" +"\",\n";
        		}
            
            for(int cntr=0;cntr<names.size();cntr++)
     		{
     			  loc2 = man.getLocationByID(names.get(cntr).getLocationId());
     			  UserDefinedField result = null;
     			  String res = "---";
     			  System.out.println("Nametype: " + names.get(cntr).getTypeId());
     			  if(names.get(cntr).getTypeId()!= 0)
     			  {
     				  result = man.getUserDefinedFieldByID(names.get(cntr).getTypeId());
     				  res = result.getFname();
     			  }
     			  
                  outputString = outputString 
                  + tabs.toString() +"    \"dates"+cntr+"\" : \"" + names.get(cntr).getNdate() +"\",\n"
                  + tabs.toString() +"    \"name"+cntr+"\" : \"" + names.get(cntr).getNval() +"\",\n"
                  + tabs.toString() +"    \"ntype"+cntr+"\" : \"" + res +"\",\n"
                  + tabs.toString() +"    \"nstat"+cntr+"\" : \"" + names.get(cntr).getNstat() +"\",\n"
                  + tabs.toString() +"    \"loc"+cntr+"\" : \"" + loc2.getLname() +"\",\n";
                 
    		}
            
            for(int cntr=0;cntr<attributes.size();cntr++)
     		{
     			  loc2 = man.getLocationByID(attributes.get(cntr).getLocationId());	
     			  UserDefinedField result = null;
     			  String res = "---";
     			  String des = "---";
     			  String val = "---";
     			  String date = "---";
     			  
     			  System.out.println("Atype: " + attributes.get(cntr).getAid());
     			  if(attributes.get(cntr).getAval()!=null || attributes.get(cntr).getAdate() != null )
     			  {
     				  val = attributes.get(cntr).getAval();
     				  date = attributes.get(cntr).getAdate().toString();
     			  }
     			  
     			  if(attributes.get(cntr).getAid()!= 0)
     			  {
     				  result = man.getUserDefinedFieldByID(attributes.get(cntr).getTypeId());
     				  if(result!=null){
     					  res = result.getFcode();
     					  des = result.getFname();
     				  }
     			  }
                  outputString = outputString 
                  + tabs.toString() +"    \"aid"+cntr+"\" : \"" + attributes.get(cntr).getAid() +"\",\n"
                  + tabs.toString() +"    \"atype"+cntr+"\" : \"" + attributes.get(cntr).getTypeId() +"\",\n"
                  + tabs.toString() +"    \"aval"+cntr+"\" : \"" + val +"\",\n"
                  + tabs.toString() +"    \"aloc"+cntr+"\" : \"" + loc2.getLname() +"\",\n"
                  + tabs.toString() +"    \"aname"+cntr+"\" : \"" + res +"\",\n"
                  + tabs.toString() +"    \"ades"+cntr+"\" : \"" + des +"\",\n"
                  + tabs.toString() +"    \"adate"+cntr+"\" : \"" + date +"\",\n";
                 
    		}
            
     		outputString = outputString + tabs.toString() +"    \"gpid1\" : \"" + node.getGermplasm().getGpid1() +"\",\n";
     		outputString = outputString + tabs.toString() +"    \""+"gpid2"+"\""+": \"" + node.getGermplasm().getGpid2() + "\",\n";
        }
        	
        else{
        
        	Debug.println(0, tabs.toString() +"\"gid\" : \""+ node.getGermplasm().getGid() + "\",\n" + tabs.toString()+ "\"name\" : \""+ name +"\",\n" +tabs.toString()+ "\"layer\": \""+ (level-1) + "\"");
        	
        	if(name != null){
        		if(name.trim().toString().length() > 5)
	            {
	            	name2 = name.substring(0, Math.min(name.length(),6)) + "...";
	            }
        	}
        	
        	
			String cn = "---";
        	if(cnty != null)
        	{
        		cn = cnty.getIsoabbr();
        	}
			
        	outputString = outputString + "\n "+ tabs.toString() + tabs.toString() + "\"gid\" : \"" + node.getGermplasm().getGid() +"\",\n"
                    + tabs.toString() +"    \"name\" : \"" + name +"\",\n"
                    + tabs.toString() +"    \"name2\" : \"" + name2 +"\",\n"
                    + tabs.toString() +"    \"date\" : \"" + node.getGermplasm().getGdate() +"\",\n"
                    + tabs.toString() +"    \"id\" : \"" + node.getGermplasm().getGid() +"\",\n"
                    + tabs.toString() +"    \"layer\" : \"" + (level-1) +"\",\n"
                    + tabs.toString() +"    \"methodname\" : \"" + meth.getMname() +"\",\n"
                    + tabs.toString() +"    \"methodtype\" : \"" + meth.getMtype() +"\",\n"
                    + tabs.toString() +"    \"location\" : \"" + loc.getLname() +"\",\n"
                    + tabs.toString() +"    \"country\" : \"" + cn +"\",\n"
                    + tabs.toString() +"    \"ref\" : \"" + descBibref +"\",\n";
            
        	if(meth.getMtype().equals("GEN"))
    		{
    			outputString = outputString + tabs.toString() +"    \"warning\" : \"" + "true" +"\",\n";
    		}
        	
            for(int cntr=0;cntr<names.size();cntr++)
     		{
     			  loc2 = man.getLocationByID(names.get(cntr).getLocationId());	
     			  UserDefinedField result = null;
    			  String res = "---";
    			  System.out.println("Nametype: " + names.get(cntr).getTypeId());
    			  if(names.get(cntr).getTypeId()!= 0)
    			  {
    				  result = man.getUserDefinedFieldByID(names.get(cntr).getTypeId());
    				  res = result.getFname();
    			  }
                  outputString = outputString 
                  + tabs.toString() +"    \"dates"+cntr+"\" : \"" + names.get(cntr).getNdate() +"\",\n"
                  + tabs.toString() +"    \"name"+cntr+"\" : \"" + names.get(cntr).getNval() +"\",\n"
                  + tabs.toString() +"    \"ntype"+cntr+"\" : \"" + res +"\",\n"
                  + tabs.toString() +"    \"nstat"+cntr+"\" : \"" + names.get(cntr).getNstat() +"\",\n"
                  + tabs.toString() +"    \"loc"+cntr+"\" : \"" + loc2.getLname() +"\",\n";
                 
    		}
            
            for(int cntr=0;cntr<attributes.size();cntr++)
     		{
     			  loc2 = man.getLocationByID(attributes.get(cntr).getLocationId());	
     			  UserDefinedField result = null;
     			  String res = "---";
     			  String des = "---";
     			  String val = "---";
    			  String date = "---";
    			  
     			  System.out.println("Atype: " + attributes.get(cntr).getAid());
     			  if(attributes.get(cntr).getAval()!=null || attributes.get(cntr).getAdate() != null )
    			  {
    				  val = attributes.get(cntr).getAval();
    				  date = attributes.get(cntr).getAdate().toString();
    			  }
     			 
     			  if(attributes.get(cntr).getAid()!= 0)
     			  {
     				  result = man.getUserDefinedFieldByID(attributes.get(cntr).getTypeId());
     				  if(result!=null){
     					  res = result.getFcode();
     					  des = result.getFname();
     				  }
     			  }
     			  
                  outputString = outputString 
                  + tabs.toString() +"    \"aid"+cntr+"\" : \"" + attributes.get(cntr).getAid() +"\",\n"
                  + tabs.toString() +"    \"atype"+cntr+"\" : \"" + attributes.get(cntr).getTypeId() +"\",\n"
                  + tabs.toString() +"    \"aval"+cntr+"\" : \"" + val +"\",\n"
                  + tabs.toString() +"    \"aloc"+cntr+"\" : \"" + loc2.getLname() +"\",\n"
                  + tabs.toString() +"    \"aname"+cntr+"\" : \"" + res +"\",\n"
                  + tabs.toString() +"    \"ades"+cntr+"\" : \"" + des +"\",\n"
                  + tabs.toString() +"    \"adate"+cntr+"\" : \"" + date +"\",\n";
                 
    		}
            
     		outputString = outputString + tabs.toString() +"    \"gpid1\" : \"" + node.getGermplasm().getGpid1() +"\",\n";
     		outputString = outputString + tabs.toString() +"    \""+"gpid2"+"\""+": \"" + node.getGermplasm().getGpid2() + "\"\n";
        }
        
        System.out.println(tabs.toString() + "size : " + node.getLinkedNodes().size());
        
		if(node.getLinkedNodes().size()==0 && (level-1) == 0)
        {
        	outputString = outputString + tabs.toString() + ",\"children\" : \n" + tabs.toString() +"[{}]";
        	System.out.println(tabs.toString() + "\"children\" : \n" + tabs.toString() +"[");
        }
        
        if(!node.getLinkedNodes().isEmpty()){
        	outputString = outputString + tabs.toString() + "\"children\" : \n" + tabs.toString() +"[";
        	System.out.println(tabs.toString() + "\"children\" : \n" + tabs.toString() +"[");
        }
        
        for (GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {
        	
        	counterArray.set(level-1,counterArray.get(level-1)+1);
        	
        	System.out.println(tabs.toString()+"{");
        	outputString = outputString + "\n" + tabs.toString()+ "{" ;
        	outputString = outputString + "\n";
        	outputString=printNode(outputString,parent,level+1,names,loc2,attributes,cid,cnty,man,meth,bibref,loc);
        	outputString = outputString + "\n" + tabs.toString()+ "}";
            System.out.println(tabs.toString()+"}");
           
            if(counterArray.get(level-1) < node.getLinkedNodes().size() ) 
            {
             		outputString = outputString + tabs.toString()+ ","; //check if no sibling
             		System.out.println(tabs.toString()+",");
            }
            
        }
        
        if(!node.getLinkedNodes().isEmpty()){
        	outputString = outputString + "\n" + tabs.toString()+ "]";
        	System.out.println(tabs.toString()+"]");
        }
        return outputString;
    }
	
}
