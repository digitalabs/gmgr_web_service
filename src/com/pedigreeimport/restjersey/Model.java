private void printNode(GermplasmPedigreeTreeNode node, int level, List <Name> names, Location loc2, List <Attribute> attributes, int cid, Country cnty, GermplasmDataManager man, Method meth, Bibref bibref, Location loc) throws IOException, MiddlewareQueryException {
	       
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
        
        if(!node.getLinkedNodes().isEmpty()){
        	Debug.println(0, tabs.toString() +"\"gid\" : \""+ node.getGermplasm().getGid() + "\",\n" + tabs.toString()+ "\"name\" : \""+ name +"\",\n" +tabs.toString()+ "\"layer\": \""+ (level-1) + "\",");
        	
        	outputString = outputString + "\n "+ tabs.toString() + tabs.toString() +"\"gid\" : \"" + node.getGermplasm().getGid() +"\",\n"
                    + tabs.toString() +"    \"name\" : \"" + name +"\",\n"
                    + tabs.toString() +"    \"date\" : \"" + node.getGermplasm().getGdate() +"\",\n"
                    + tabs.toString() +"    \"id\" : \"" + node.getGermplasm().getGid() +"\",\n"
                    + tabs.toString() +"    \"layer\" : \"" + (level-1) +"\",\n"
                    + tabs.toString() +"    \"methodname\" : \"" + meth.getMname() +"\",\n"
                    + tabs.toString() +"    \"methodtype\" : \"" + meth.getMtype() +"\",\n"
                    + tabs.toString() +"    \"location\" : \"" + loc.getLname() +"\",\n"
                    + tabs.toString() +"    \"country\" : \"" + cnty.getIsoabbr() +"\",\n"
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
        
        	outputString = outputString + "\n "+ tabs.toString() + tabs.toString() + "\"gid\" : \"" + node.getGermplasm().getGid() +"\",\n"
                    + tabs.toString() +"    \"name\" : \"" + name +"\",\n"
                    + tabs.toString() +"    \"date\" : \"" + node.getGermplasm().getGdate() +"\",\n"
                    + tabs.toString() +"    \"id\" : \"" + node.getGermplasm().getGid() +"\",\n"
                    + tabs.toString() +"    \"layer\" : \"" + (level-1) +"\",\n"
                    + tabs.toString() +"    \"methodname\" : \"" + meth.getMname() +"\",\n"
                    + tabs.toString() +"    \"methodtype\" : \"" + meth.getMtype() +"\",\n"
                    + tabs.toString() +"    \"location\" : \"" + loc.getLname() +"\",\n"
                    + tabs.toString() +"    \"country\" : \"" + cnty.getIsoabbr() +"\",\n"
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
        	printNode(parent,level+1,names,loc2,attributes,cid,cnty,man,meth,bibref,loc);
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
    }