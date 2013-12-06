package com.pedigreeimport.restjersey;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.GetGermplasmByNameModes;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.pedigreeimport.backend.Tokenize;

public class test {

	/**
	 * @param args
	 * @throws ParseException 
	 * @throws MiddlewareQueryException 
	 * @throws IOException 
	 */

	static int GID;
	static String global_id;
	static int locationID;
	//static List<List<String>> output = new ArrayList<List<String>>();
	static List<List<String>> list_local = new ArrayList<List<String>>();
	static List<List<String>> existingTerm_local = new ArrayList<List<String>>();
	static List<List<String>> createdGID_local = new ArrayList<List<String>>();
	static List<String> checked_local = new ArrayList<String>();
	static String userID_local;
	

	public static void main(String[] args) throws IOException, MiddlewareQueryException, ParseException {
		//	bulk_createGID();
		//	copy_corrected();
		//updateFile_createdGID();
		//single_createdGID();
		sortList();
	}


	public static void sortList() throws IOException{
		String csv="E:/xampp/htdocs/GMGR/csv_files/output.csv";
		List<String> list= new ArrayList<String>();
		List<String> sortedList= new ArrayList<String>();

		FileReader fr= new FileReader(csv);
		BufferedReader br = new BufferedReader(fr);
		String line;
		String[] cells;

		while ((line = br.readLine()) != null) {
			System.out.println("**"+line);
			list.add(line);
		}

		for(int i=0; i<list.size(); i++){
			System.out.println("$$ "+list.get(i));

			String[] column=list.get(i).split(",");

			System.out.println("size: "+column.length+column[2]);

			String female=column[2];
			String male=column[5];
			String f_id=column[3];
			String m_id=column[7];
			System.out.println("Starting female");
			sortedList=recurse(female, f_id, list.get(i), list, i, sortedList);
			System.out.println(sortedList+"-"+sortedList.size());

			System.out.println("Starting male");

			sortedList=recurse(male,m_id,  list.get(i),list,i, sortedList);
			System.out.println(sortedList+"-"+sortedList.size());
		}


	}

	public static List<String> recurse(String pedigree, String id, String line,List<String> list, int i, List<String> sortedList){

		String[] column=line.split(",");
		String female= column[2];
		String male= column[6];

		String fid_l= column[3];
		String mid_l=column[7];

		if(i-1==list.size() && list.get(i)!=pedigree){
			System.out.println("wala jud");
			return sortedList;
		}else if(i-1==list.size() && list.get(i)==pedigree){
			System.out.println("naa, sa last");
			sortedList.add(id);
			return sortedList;
		}else if(list.get(i)==pedigree){
			System.out.println("naa, sa last");



			if(!sortedList.contains(line)){
				sortedList.add(id);	
				//return sortedList;
			}else{
				if(fid_l==id){
					id=fid_l;
					pedigree=female;
				}else{
					id=mid_l;
					pedigree=male;
				}
				recurse(pedigree,id,list.get(i-1), list, i, sortedList);
			}

		}
		return sortedList;


	}


	public static JSONObject single_createGID(JSONObject obj) throws MiddlewareQueryException, IOException, ParseException, InterruptedException{
		ManagerFactory factory = new Config().configDB();
		GermplasmDataManager manager = factory.getGermplasmDataManager();

		
		JSONObject jsonObject = (JSONObject) obj;
		List<List<String>> createdGID = (List<List<String>>) jsonObject.get("createdGID");
		createdGID_local.clear();
		createdGID_local=createdGID;
		createdGID=createdGID_local;
		List<List<String>> list = (List<List<String>>) jsonObject.get("list");
		System.out.println("\t list: "+list.size());
		list_local=list;
		System.out.println("\t list: "+list_local.size());
		List<List<String>> existingTerm = (List<List<String>>) jsonObject.get("existingTerm");
		existingTerm_local=existingTerm;
		
		
		String userID = (String) jsonObject.get("userID");
		userID_local=userID;

		String term = (String) jsonObject.get("term");
		// term refers to the last derivative of parent1 that had already exists
		String parent1 = (String) jsonObject.get("parent1");
		// the parent that had chosen a GID from existing GID's

		String parent2 = (String) jsonObject.get("parent2");
		// the other parent that also need to be re-evaluate
		String parent2ID = (String) jsonObject.get("parent2ID");
		// parent2ID, ID of parent2
		String cross = (String) jsonObject.get("cross");
		// the name of the cross of parent1 and parent2
		Boolean is_female = (Boolean) jsonObject.get("is_female");
		// flag to determine if parent2 is female or male

				List<String> details =  (List<String>) jsonObject.get("germplasm");
		System.out.println("json string:location ID: " + (String) details.get(6));
		locationID = Integer.valueOf((String) details.get(6));
		int gpid1 = Integer.valueOf((String) details.get(8));
		int gpid2 = Integer.valueOf((String) details.get(9));
		String root_id = (String) jsonObject.get("root_id");

		String parent1ID = (String) details.get(0);
		// parent1ID, ID of parent1
		String gid= (String) details.get(3);
		String maleParent, femaleParent, fid, mid;


		if (!is_female) {
			fid = parent2ID;
			mid = parent1ID;
			femaleParent = parent2;
			maleParent = parent1;

		} else {
			fid = parent1ID;
			mid = parent2ID;
			femaleParent = parent1;
			maleParent = parent2;
		}
		System.out.println("[X] fid: "+fid);
		System.out.println("[X] mid: "+mid);
		String temp;
		mid=mid.replaceAll("\"", "");
		fid=fid.replaceAll("\"", "");
		int mid2=Integer.valueOf(mid),fid2=Integer.valueOf(fid);
		//System.out.println("fid: "+fid2);
		//System.out.println("mid: "+mid2);
		if (Integer.valueOf(mid) < Integer.valueOf(fid)) {
			System.out.println("here: ");
			temp = mid;
			mid = fid;
			fid = temp;

			temp = maleParent;
			maleParent = femaleParent;
			femaleParent = temp;
		}
		System.out.println("parent1: "+parent1);
		System.out.println("parent1ID: "+parent1ID);
		//System.out.println("mid: "+mid);

		createdGID_local = updateCreatedGID(gid, parent1ID, parent1, manager, "false", createdGID);	//update the createdGID file
		createdGID=createdGID_local;

		parse(manager, parent1,gid, parent1ID,gpid1,gpid2);	//parse the germplasm name of the chosen GID
		createdGID=createdGID_local;

		locationID=getLocationID();
		int mgid=getGID_fromFile(maleParent, mid);
		int fgid=getGID_fromFile(femaleParent, fid);


		Germplasm germplasm=isExisting(fgid, mgid,  manager);

		if (has_GID(parent2ID, parent2)) {
			System.out.println("PROCESSING CROSS");

			if(germplasm.getGid()==null){
				System.out.println("fgid: "+ fgid);
				System.out.println("mgid: "+ mgid);

				int methodID=selectMethodType(manager,fgid,mgid,femaleParent,maleParent);

				int cross_gid = (int) addGID(manager, cross,fgid,mgid,  methodID);

				Germplasm germplasm1 = manager.getGermplasmByGID(cross_gid);

				createdGID_local=updateCreatedGID(""+germplasm1.getGid(), fid + "/" + mid, cross, manager, "new", createdGID);
				createdGID=createdGID_local;

				list_local=updateFile_corrected(germplasm1, fid, cross, manager);
				System.out.println("\t id: "+fid + "/" + mid);
				System.out.println("\t id: "+ cross);

				germplasm1=null;
			}else{
				System.out.println(" or HERE"+ germplasm.getGid());		
				List<Name> name = new ArrayList<Name>();
				name=manager.getNamesByGID(germplasm.getGid(), 0, null);

				createdGID_local=updateCreatedGID(""+germplasm.getGid(), fid + "/" + mid, name.get(0).getNval(), manager, "false", createdGID);
				createdGID=createdGID_local;
				
				list_local=updateFile_corrected(germplasm, fid , name.get(0).getNval(), manager);
				name=null;
			}
		}
		
		factory.close();

		//clear all object, free memory
		details.clear();
		jsonObject.clear();
		germplasm=null;
		
		//createdGID_local.clear();
		
		JSONObject data_output= new JSONObject();
		data_output.put("list",list_local);
		data_output.put("createdGID",createdGID);
		data_output.put("existingTerm",existingTerm_local);
		
		//System.out.println("createdGID: "+createdGID_local);
		//System.out.println("\t existing: "+existingTerm_local);
		System.out.println("\t list: "+list_local);
		
		System.out.println("END SINGLE CREATED @ test.java");
		
		return data_output;

	}

	public static void parse(GermplasmDataManager manager, String parent, String gid, String id, int gpid1, int gpid2) throws MiddlewareQueryException, IOException, InterruptedException {

		System.out.println("### STARTING parsing in sInlge creation of GID");
		String[] tokens = new Tokenize().tokenize(parent);
		ArrayList<String> pedigreeList = new ArrayList<String>();

		pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0] is the most recent pedigree, pedigreeList[size] is the root
		String pedigree;

		if(pedigreeList.size()>1){
			pedigreeList.remove(0); // remove the pedigree already processed

			pedigree=pedigreeList.get(0);	// first element is the parent


			System.out.print(">> "+ pedigreeList.get(0) + "\t");
			//call function to assign GID to the pedigree line
			assignGID(manager, id, parent, gpid1, gpid2, pedigreeList);
		}/*else{
			pedigree=pedigreeList.get(0);
			updateFile_createdGID(gid, id, pedigree, manager);	//update the createdGID file
		}
		 */


		//clearing objects 
		pedigreeList.clear();
		tokens=null;

		System.out.println("### END parsing in sInlge creation of GID");
	}

	public static void assignGID(GermplasmDataManager manager,String id, String parent, int gpid1, int gpid2, ArrayList<String> pedigreeList) throws MiddlewareQueryException, IOException, InterruptedException{
		Boolean error=false;
		String gid;	//set the gid to be string 
		Germplasm germplasm;
		for(int i=0; i<pedigreeList.size(); i++){
			String pedigree=pedigreeList.get(i);
			System.out.print(">> "+ pedigree + "\t");
			germplasm= new Germplasm();

			if(error){	//if the precedent pedigree does not exist

				List<Germplasm> germplasmList = new ArrayList<Germplasm>();

				int count_LOCAL = countGermplasmByName(manager,pedigree, Database.LOCAL);
				int count_CENTRAL= countGermplasmByName(manager,pedigree,Database.CENTRAL);
				germplasmList=getGermplasmList( manager,pedigree, count_LOCAL, count_CENTRAL); //gets lists of germplasm with that name
				germplasm= getGermplasmByGpid(manager, gpid1, germplasmList);	// get the germplasm of the same gpid1, for derivative line, or gid equals to the gpid1

				germplasmList.clear();	//clearing object
			}else{
				germplasm=manager.getGermplasmByGID(gpid2);
			}

			if(germplasm==null){ // this is an ERROR, the gid should exist
				error=true;
				gid="Does not exist";
				//createdGID_local=updateFile_createdGID(gid, id, pedigree, manager,"false");	//update the createdGID file
				
			}else{
				gpid2=germplasm.getGpid2();
				gpid1=germplasm.getGpid1();
				gid=""+germplasm.getGid();
				error=false; //set the flag to false
			}
			createdGID_local = updateCreatedGID(gid, id, pedigree, manager,"false", createdGID_local);	//update the createdGID file
			
		}

		//clearing objects
		germplasm=null;

	}

	public static JSONObject bulk_createGID2(List<List<String>> createdGID,List<List<String>> list, List<String> checked, int locationID_l, List<List<String>> existingTerm, String userID)throws IOException,
	MiddlewareQueryException, ParseException, InterruptedException {
		
		System.out.println(" ###Starting..BULK CREATION of GID");

		createdGID_local = new ArrayList<List<String>>();
		createdGID_local=createdGID;
		list_local = new ArrayList<List<String>>();
		list_local=list;
		checked_local= new ArrayList<String>();
		
		existingTerm_local=existingTerm;
		checked_local=checked;
		
		userID_local=userID;
		
		
		ManagerFactory factory = new Config().configDB();
		GermplasmDataManager manager = factory.getGermplasmDataManager();

		locationID=locationID_l;
		List<String> row_output=new ArrayList<String>();
		
		//System.out.println("HERE: "+list);
		//System.out.println("HERE: "+checked.size());
		for (int i = 0; i < checked.size(); i++) {
			//System.out.println("\n CHECK:: "+ checked.get(i));
			
			for(int j=0; j<list.size();j++){

				row_output= list.get(j);
				//System.out.println("row_output: "+row_output);
				String female_id=row_output.get(2);
				String female_remarks=row_output.get(3);
				String female_nval=row_output.get(5);
				//male
				String male_id=row_output.get(6);
				String male_remarks=row_output.get(7);
				String male_nval=row_output.get(9);
				//cross name
				String cross=row_output.get(1);

				if (female_id.equals(checked.get(i)) || male_id.equals(checked.get(i)) ) {
					//System.out.println("female_remarks: "+female_remarks);
					//System.out.println("male_remarks: "+male_remarks);

					if (female_remarks.equals("in standardized format") && male_remarks.equals("in standardized format")) {

						System.out.println("\t" + "  ***Process parents");
						
						/*System.out.println("locationID: "+locationID);
						System.out.println("checked: "+ checked);
						System.out.println("list: "+ list);
						System.out.println("existingTerm: " + existingTerm);
						*/

						Boolean result=processParents2(manager, female_nval, female_id, male_nval, male_id, cross,list);
						if(!result){
							printNotSet2(cross, female_nval+"/"+male_nval, female_id + "/" + male_id);
						}
						System.out.println("\t" + "  ***END Process parents");
					}
					break;
				}
			}
		}
		//row_output.clear();
		//list.clear();
		//existingTerm.clear();
		
		//System.out.println("output: "+createdGID_local);
		//System.out.println("list: "+list_local);
		existingTerm= existingTerm_local;
		JSONObject data_output= new JSONObject();
		data_output.put("existingTerm",existingTerm);
		data_output.put("list",list_local);
		data_output.put("createdGID",createdGID_local);
		
		
		
		System.out.println("\n createdGID: "+createdGID_local.size()+"\t"+createdGID_local);
		System.out.println("\n list: "+list_local.size()+"\t"+list_local);
		
		System.out.println(" ###ENDING..BULK CREATION of GID \n");

		return data_output;
	}

	public static JSONObject bulk_createGID(List<List<String>> list, List<String> checked, int locationID_l, List<List<String>> existingTerm, String userID)throws IOException,
	MiddlewareQueryException, ParseException, InterruptedException {
		
		System.out.println(" ###Starting..BULK CREATION of GID");

		createdGID_local = new ArrayList<List<String>>();
		list_local = new ArrayList<List<String>>();
		checked_local= new ArrayList<String>();
		
		existingTerm_local=existingTerm;
		checked_local=checked;
		
		userID_local=userID;
		
		
		ManagerFactory factory = new Config().configDB();
		GermplasmDataManager manager = factory.getGermplasmDataManager();

		locationID=locationID_l;
		List<String> row_output=new ArrayList<String>();
		
		//System.out.println("HERE: "+list);
		//System.out.println("HERE: "+checked.size());
		for (int i = 0; i < checked.size(); i++) {
			//System.out.println("\n CHECK:: "+ checked.get(i));
			
			for(int j=0; j<list.size();j++){

				row_output= list.get(j);
				//System.out.println("row_output: "+row_output);
				String female_id=row_output.get(2);
				String female_remarks=row_output.get(3);
				String female_nval=row_output.get(5);
				//male
				String male_id=row_output.get(6);
				String male_remarks=row_output.get(7);
				String male_nval=row_output.get(9);
				//cross name
				String cross=row_output.get(1);

				if (female_id.equals(checked.get(i)) || male_id.equals(checked.get(i)) ) {
					//System.out.println("female_remarks: "+female_remarks);
					//System.out.println("male_remarks: "+male_remarks);

					if (female_remarks.equals("in standardized format") && male_remarks.equals("in standardized format")) {

						System.out.println("\t" + "  ***Process parents");
						
						/*System.out.println("locationID: "+locationID);
						System.out.println("checked: "+ checked);
						System.out.println("list: "+ list);
						System.out.println("existingTerm: " + existingTerm);
						*/

						Boolean result=processParents2(manager, female_nval, female_id, male_nval, male_id, cross,list);
						if(!result){
							printNotSet2(cross, female_nval+"/"+male_nval, female_id + "/" + male_id);
						}
						System.out.println("\t" + "  ***END Process parents");
					}
					break;
				}
			}
		}
		//row_output.clear();
		//list.clear();
		//existingTerm.clear();
		
		//System.out.println("output: "+createdGID_local);
		//System.out.println("list: "+list_local);
		existingTerm= existingTerm_local;
		JSONObject data_output= new JSONObject();
		data_output.put("existingTerm",existingTerm);
		data_output.put("list",list_local);
		data_output.put("createdGID",createdGID_local);
		
		
		System.out.println("\t existing: "+existingTerm_local.size()+"\t"+existingTerm_local);
		System.out.println("\t existing: "+existingTerm.size()+"\t"+existingTerm);
		
		System.out.println("\t existing: "+createdGID_local.size()+"\t"+createdGID_local);
		
		System.out.println(" ###ENDING..BULK CREATION of GID \n");

		return data_output;
	}
	private static Boolean processParents2(GermplasmDataManager manager,
			String female_nval, String female_id, String male_nval,
			String male_id, String cross, List<List<String>> list) throws MiddlewareQueryException, IOException {

		boolean female=false, male=false;
		int fgid = 0, mgid = 0;

		female=parse2(manager,female_nval,female_nval, female_id);
		fgid=GID;
		System.out.println("\nmale:");
		male=parse2(manager,male_nval,male_nval, male_id);
		mgid=GID;

		Germplasm germplasm=isExisting(fgid, mgid, manager);

		if(male && female){
			if(germplasm.getGid()==null){

				System.out.println("female_id: "+fgid);
				System.out.println("male_id: "+mgid);

				int methodID=selectMethodType(manager,fgid,mgid,female_nval,male_nval);

				int cross_gid = (int) addGID(manager, cross,fgid,mgid,  methodID);

				Germplasm germplasm1 = manager.getGermplasmByGID(cross_gid);
				System.out.println("YEAH");
				printSuccess2(cross,female_nval + "/" + male_nval, female_id + "/" + male_id, germplasm1, manager,  "new");
				//updateFile_createdGID(germplasm1.getGid().toString(), female_id + "/" + male_id, cross, manager, "new");

				list_local=updateFile_corrected2(germplasm1, female_id, cross, manager,list);
				System.out.println("\t id: "+female_id + "/" + male_id);
				System.out.println("\t id: "+ cross);

				germplasm1=null;
			}else{

				//Name name=manager.getGermplasmNameByID(germplasm.getGid());
				List<Name> name = new ArrayList<Name>();
				name=manager.getNamesByGID(germplasm.getGid(), 0, null);
				System.out.println("\t ====gid: "+ germplasm.getGid());	
				printSuccess2(cross,female_nval + "/" + male_nval, female_id + "/" + male_id, germplasm, manager,  "new");
				//updateFile_createdGID(""+germplasm.getGid().toString(), female_id + "/" + male_id, name.get(0).getNval(), manager, "false");

				list_local=updateFile_corrected2(germplasm, female_id , name.get(0).getNval(), manager,list);

				name=null;
			}
		}else{
			list_local=list;
		}
		germplasm=null;
		
		//System.out.println("list_local: "+list_local);
		System.out.println(" ###END..Processing of Parents \n");

		if(male && female){
			return true;
		}else{
			return false;
		}
	}


	private static List<List<String>> updateFile_corrected2(Germplasm germplasm,
			String id, String nval, GermplasmDataManager manager,
			List<List<String>> list) {
		List<List<String>> output=new ArrayList<List<String>>();

		for (int i = 0; i < list.size();i++) {

			List<String> row_object= list.get(i);
			List<String> output_object= new ArrayList<String>();

			for(int j=0; j< row_object.size();j++){

				if (row_object.get(j).equals(id) && j==2 ) {
					output_object.add(germplasm.getGid().toString()); 
				}else{
					output_object.add(row_object.get(j));
				}
			}
			output.add(output_object);
		}


		System.out.println("CORRECTED**: "+output);
		
		System.out.println("*** END Updating corrected.csv");
		return output;

	}


	private static boolean parse2(GermplasmDataManager manager, String parent, String root, String id) throws MiddlewareQueryException, IOException {
		System.out.println(" ###STARTING..Parsing");

		Boolean result=false;

		String[] tokens = new Tokenize().tokenize(parent);
		ArrayList<String> pedigreeList = new ArrayList<String>();

		pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0] is the most recent pedigree, pedigreeList[size] is the root

		int count_LOCAL = countGermplasmByName(manager,pedigreeList.get(0), Database.LOCAL);
		int count_CENTRAL= countGermplasmByName(manager,pedigreeList.get(0),Database.CENTRAL);

		int count=count_LOCAL+ count_CENTRAL;

		System.out.print(">> "+ pedigreeList.get(0) + "\t");

		String pedigree=pedigreeList.get(0);	// first element is the parent
		List<Germplasm> germplasm = new ArrayList<Germplasm>();

		if(count==1){
			//single_hit
			System.out.println("Single Hit");

			// get germplasm pojo

			if(count_LOCAL==1){
				germplasm=getGermplasm(Database.LOCAL, manager,pedigree, count_LOCAL);
			}else{
				germplasm=getGermplasm(Database.CENTRAL,manager,pedigree, count_CENTRAL);
			}

			//print to file
			printSuccess2(pedigree,parent,id, germplasm.get(0),manager, "false");
			GID=germplasm.get(0).getGid();


			if(pedigreeList.size()>1){

				int gpid1= germplasm.get(0).getGpid1();	//get gpid1/root
				int gpid2= germplasm.get(0).getGpid2();	//get gpid1/root

				pedigreeList.remove(0); // remove the pedigree already processed

				//call function to search for the pedigree line
				single_Hit2(manager, id, parent, gpid1, gpid2, pedigreeList,root);
			}

			result=true;	//set flag to true

		}else if(count>1){
			//multiple_hit
			System.out.println("Multiple Hit");

			//print to file "CHOOSE GID" for all the pedigree line
			printChooseGID2(pedigree, parent, id);

			germplasm=getGermplasmList(manager, pedigree, count_LOCAL, count_CENTRAL);
			printMultipleHits(germplasm, manager, pedigree, id, pedigree);

			pedigreeList.remove(0);
			for(int i=0; i<pedigreeList.size(); i++){

				pedigree=pedigreeList.get(i);
				System.out.print(">> "+ pedigree + "\t");

				printNotSet2(pedigree, parent, id);
			}
			result=false;	//set flag to false

		}else{// count==0
			global_id=id;

			System.out.println("No hit in local and central");
			int event;

			System.out.println("Search Input File");

			//search_file	


			//event=search_List(manager, id, parent,parent, pedigree,root_id,csv);

			//if(event==0){
			//add GID
			int lastIndex=pedigreeList.size();
			String root_pedigree=pedigreeList.get(lastIndex-1);
			Germplasm g=isExisting(root_pedigree, manager);

			if(g.getGid()==null){

			}else{
				//					createPedigreeLine(GermplasmDataManager manager, ArrayList<String> pedigreeList, String id, String root, String csv) throws MiddlewareQueryException, IOException{
				createPedigreeLine2(manager, pedigreeList,id,parent);
			}
			result=true;

			/*}else if(event==2){
				System.out.print("()event 2");

				printNotSet(pedigree, parent, root, id, csv);

				result=false;

			}
			else{
				result=true;
			}
			 */

		}
		//clearing memory

		tokens=null;
		pedigreeList.clear();
		germplasm.clear();

		System.out.println(" ###ENDING..Parsing \n");

		return result;
	}


	private static void createPedigreeLine2(GermplasmDataManager manager,
			ArrayList<String> pedigreeList, String id, String parent) throws MiddlewareQueryException {
		int gpid2 = 0, gpid1 = 0, gid;

		Collections.reverse(pedigreeList);

		ArrayList<Integer> pedigreeList_GID = new ArrayList<Integer>();
		List<Germplasm> list= new ArrayList<Germplasm>();
		List<Name> name=null;
		List<Name> name1=null;
		Germplasm g;
		for (int i = 0; i < pedigreeList.size(); i++) {

			name=manager.getNamesByGID(gpid1,0 , GermplasmNameType.DERIVATIVE_NAME);
			name1=manager.getNamesByGID(gpid2,0 , GermplasmNameType.DERIVATIVE_NAME);

			//int methodID=selectMethodType(manager,gpid1,gpid2,name.get(0).getNval(),name1.get(0).getNval());
			int methodID=33;
			gid = (int) addGID(manager, pedigreeList.get(i), gpid1, gpid2,
					methodID);
			g=new Germplasm();
			g=manager.getGermplasmByGID(gid);
			if (i == 0) {
				gpid2 = gid;
				gpid1 = gid;
			} else {
				gpid2 = gid;
			}
			//pedigreeList_GID.add(gid);
			list.add(g);
			//System.out.println(pedigreeList.get(i) + " gpid1: " + gpid1
			//		+ " gpid2: " + gpid2);
		}

		for (int i = list.size() - 1; i >= 0; i--) {
			System.out.println("YEAAAAAH id: " +id);
			printSuccess2(pedigreeList.get(i), parent, id, list.get(i), manager, "new");
		}

		//clearing memory
		pedigreeList.clear();
		list.clear();
		name.clear();
		name1.clear();
		g=null;

	}


	private static void single_Hit2(GermplasmDataManager manager, String id,
			String parent, int gpid1, int gpid2,
			ArrayList<String> pedigreeList, String root) throws MiddlewareQueryException, IOException {
		Boolean error=false;
		Germplasm germplasm;
		List<Germplasm> germplasmList=null;

		for(int i=0; i<pedigreeList.size(); i++){
			String pedigree=pedigreeList.get(i);
			System.out.print(">> "+ pedigree + "\t");
			
			germplasm= new Germplasm();
			if(error){	//if the precedent pedigree does not exist

				germplasmList = new ArrayList<Germplasm>();

				int count_LOCAL = countGermplasmByName(manager,pedigree, Database.LOCAL);
				int count_CENTRAL= countGermplasmByName(manager,pedigree,Database.CENTRAL);
				germplasmList=getGermplasmList( manager,pedigree, count_LOCAL, count_CENTRAL); //gets lists of germplasm with that name
				germplasm= getGermplasmByGpid(manager, gpid1, germplasmList);	// get the germplasm of the same gpid1, for derivative line, or gid equals to the gpid1
			}else{
				germplasm=manager.getGermplasmByGID(gpid2);
			}

			if(germplasm==null){ // this is an ERROR, the gid should exist
				error=true;

				printError2(pedigree, parent,id); //prints ERROR to file

			}else{
				gpid2=germplasm.getGpid2();
				gpid1=germplasm.getGpid1();
				error=false; //set the flag to false

				printSuccess2(pedigree,parent,id, germplasm,manager, "false");	//print to file
			}
			System.out.println("gpid1: "+gpid1 +" gpid2: "+ gpid2);
		}

		germplasm=null;
		//germplasmList.clear();
		pedigreeList.clear();

	}


	private static void printError2(String pedigree, String parent, String id) {
		List<String> row= new ArrayList<String>();

		row.add(id);
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		System.out.println("Does not Exist");

		row.add("Does not Exist" ); // gid
		row.add("N/A"); // method
		row.add("N/A"); // method
		row.add("N/A"); // location
		row.add("N/A"); // location
		row.add("N/A" ); // gpid1
		row.add("N/A" ); // gpid2
		row.add(""+false ); // gpid2

		createdGID_local.add(row);
		System.out.println("row: "+row);
		System.out.println("output: "+createdGID_local);
		//row.clear();

	}


	private static void printNotSet2(String pedigree, String parent, String id) {
		List<String> row= new ArrayList<String>();
		row.add(id);
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		System.out.println("NOT SET");

		row.add("NOT SET" ); // gid
		row.add("N/A"); // method
		row.add("N/A"); // method
		row.add("N/A"); // location
		row.add("N/A"); // location
		row.add("N/A" ); // gpid1
		row.add("N/A" ); // gpid2
		row.add(""+false ); // gpid2

		createdGID_local.add(row);
		System.out.println("row: "+row);
		System.out.println("output: "+createdGID_local);
		//row.clear();

	}


	private static void printChooseGID2(String pedigree, String parent,
			String id) {
		List<String> row= new ArrayList<String>();
		row.add(id);
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		System.out.println("CHOOSE GID" );

		row.add("CHOOSE GID" ); // gid
		row.add("N/A"); // method
		row.add("N/A"); // method
		row.add("N/A"); // location
		row.add("N/A"); // location
		row.add("N/A" ); // gpid1
		row.add("N/A" ); // gpid2
		row.add(""+false ); // gpid2

		createdGID_local.add(row);

		System.out.println("row: "+row);
		System.out.println("output: "+createdGID_local);
		//row.clear();

	}


	private static void printSuccess2(String pedigree,String parent, String id,
			Germplasm germplasm, GermplasmDataManager manager,
			String tag) throws MiddlewareQueryException {
		List<String> row= new ArrayList<String>();
		row.add(id);
		//System.out.println(id + ",");
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		//System.out.println("gid: " + germplasm.getGid());

		Location location = manager.getLocationByID(germplasm.getLocationId());
		Method method = manager.getMethodByID(germplasm.getMethodId());

		row.add(""+germplasm.getGid()); // gid
		String meth=method.getMname().replaceAll(",", "#");


		row.add(""+germplasm.getMethodId()); // method
		row.add( meth ); // method
		
		String loc=location.getLname().replaceAll("," , "#");

		//System.out.print("loc "+loc);
		row.add(""+germplasm.getLocationId()); // location
		row.add(loc); // location
		row.add(""+germplasm.getGpid1()); // gpid1
		row.add(""+germplasm.getGpid2()); // gpid2
		row.add(tag ); // gpid2

		/*System.out.print(id + ",");
		System.out.print(parent + ","); // parent
		System.out.print(pedigree + ","); // pedigree
		System.out.print(germplasm.getGid() + ","); // gid
		System.out.print(germplasm.getMethodId() + "," + meth + ","); // method
		System.out.print(germplasm.getLocationId() + "," + loc + ","); // location
		System.out.print(germplasm.getGpid1() + ","); // gpid1
		System.out.print(germplasm.getGpid2() + ","); // gpid2
		System.out.println(tag + ",\n"); // gpid2
		 */
		//clearing memory
		germplasm=null;


		method=null;
		location=null;
		germplasm=null;

		createdGID_local.add(row);
		System.out.println("row: "+row);
		System.out.println("output: "+createdGID_local);
		//row.clear();
	}


	public static boolean processParents(GermplasmDataManager manager, String female_nval, String female_id, String male_nval, String male_id, String root, String cross, String id, String csv, String csv_createdGID2) throws MiddlewareQueryException, IOException, ParseException, InterruptedException{
		boolean female=false, male=false;
		int fgid = 0, mgid = 0;

		System.out.println(" ###Starting..Processing of Parents");

		if(root.equals("")){
			System.out.println("\nfemale:");
			female=parse(manager,female_nval,female_nval, female_id,"",csv);
			fgid=GID;
			System.out.println("\nmale:");
			male=parse(manager,male_nval,male_nval, male_id,"",csv);
			mgid=GID;

			Germplasm germplasm=isExisting(fgid, mgid, manager);

			if(male && female){
				if(germplasm.getGid()==null){

					System.out.println("female_id: "+fgid);
					System.out.println("male_id: "+mgid);

					int methodID=selectMethodType(manager,fgid,mgid,female_nval,male_nval);

					int cross_gid = (int) addGID(manager, cross,fgid,mgid,  methodID);

					Germplasm germplasm1 = manager.getGermplasmByGID(cross_gid);
					System.out.println("YEAH");
					printSuccess(cross, "", female_id + "/" + male_id, germplasm1, manager, female_nval + "/" + male_nval,csv, "new");
					updateFile_createdGID(germplasm1.getGid().toString(), female_id + "/" + male_id, cross, manager, "new");

					updateFile_corrected(germplasm1, female_id, cross, manager);
					System.out.println("\t id: "+female_id + "/" + male_id);
					System.out.println("\t id: "+ cross);

					germplasm1=null;
				}else{

					//Name name=manager.getGermplasmNameByID(germplasm.getGid());
					List<Name> name = new ArrayList<Name>();
					name=manager.getNamesByGID(germplasm.getGid(), 0, null);
					System.out.println("\t ====gid: "+ germplasm.getGid());	
					printSuccess(cross, "", female_id + "/" + male_id, germplasm, manager, female_nval + "/" + male_nval,csv, "false");
					updateFile_createdGID(""+germplasm.getGid().toString(), female_id + "/" + male_id, name.get(0).getNval(), manager, "false");

					updateFile_corrected(germplasm, female_id , name.get(0).getNval(), manager);

					name=null;
				}
			}
			germplasm=null;


		}else{
			System.out.println("\nfemale:");
			System.out.println("\n"+csv_createdGID2);
			female=parse(manager,female_nval,root, female_id,id,csv_createdGID2);
			fgid=GID;
			System.out.println("\nmale:");
			System.out.println("\n"+csv_createdGID2);
			male=parse(manager,male_nval,root, male_id,id,csv_createdGID2);
			mgid=GID;

			if(male && female){			

				int methodID=selectMethodType(manager,fgid,mgid,female_nval,male_nval);

				int cross_gid = (int) addGID(manager, cross,fgid,mgid,  methodID);
				GID=cross_gid;

				Germplasm germplasm1 = manager.getGermplasmByGID(cross_gid);
				System.out.println("cross ID: "+female_id + "/" + male_id);
				System.out.println("rootID: "+id);



				printSuccess(cross, "", id, germplasm1, manager, cross,csv, "new");

				FileOutputStream fw = new FileOutputStream(csv_createdGID2, true);
				OutputStreamWriter osw = new OutputStreamWriter(fw, "UTF-8");
				BufferedWriter pw_createdGID2 = new BufferedWriter(osw);

				pw_createdGID2.append(global_id+",");

				pw_createdGID2.flush();
				pw_createdGID2.close();
				osw.close();
				fw.close();

				printSuccess(cross, root, female_id + "/" + male_id, germplasm1, manager, female_nval + "/" + male_nval,csv_createdGID2, "new");

				updateFile_corrected_2(germplasm1, female_id , cross, manager);

				System.out.println("\t id: "+female_id + "/" + male_id);
				System.out.println("\t id: "+ cross);

				germplasm1=null;
			}
		}

		System.out.println(" ###END..Processing of Parents \n");

		if(male && female){
			return true;
		}else{
			return false;
		}
	}



	private static boolean parse(GermplasmDataManager manager, String parent, String root, String id, String root_id, String csv) throws MiddlewareQueryException, IOException, ParseException, InterruptedException {

		System.out.println(" ###STARTING..Parsing");

		Boolean result=false;

		String[] tokens = new Tokenize().tokenize(parent);
		ArrayList<String> pedigreeList = new ArrayList<String>();

		pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0] is the most recent pedigree, pedigreeList[size] is the root

		int count_LOCAL = countGermplasmByName(manager,pedigreeList.get(0), Database.LOCAL);
		int count_CENTRAL= countGermplasmByName(manager,pedigreeList.get(0),Database.CENTRAL);

		int count=count_LOCAL+ count_CENTRAL;

		System.out.print(">> "+ pedigreeList.get(0) + "\t");

		String pedigree=pedigreeList.get(0);	// first element is the parent
		List<Germplasm> germplasm = new ArrayList<Germplasm>();

		if(count==1){
			//single_hit
			System.out.println("Single Hit");

			// get germplasm pojo

			if(count_LOCAL==1){
				germplasm=getGermplasm(Database.LOCAL, manager,pedigree, count_LOCAL);
			}else{
				germplasm=getGermplasm(Database.CENTRAL,manager,pedigree, count_CENTRAL);
			}

			if(!root_id.equals("")){	
				FileOutputStream fw = new FileOutputStream(csv, true);
				OutputStreamWriter osw = new OutputStreamWriter(fw, "UTF-8");
				BufferedWriter pw= new BufferedWriter(osw);

				pw.append(global_id+",");

				pw.flush();
				pw.close();
				osw.close();
				fw.close();

			}
			//print to file
			printSuccess(pedigree, parent,id, germplasm.get(0),manager, root,csv, "false");
			GID=germplasm.get(0).getGid();


			if(pedigreeList.size()>1){

				int gpid1= germplasm.get(0).getGpid1();	//get gpid1/root
				int gpid2= germplasm.get(0).getGpid2();	//get gpid1/root

				pedigreeList.remove(0); // remove the pedigree already processed

				//call function to search for the pedigree line
				single_Hit(manager, id, parent, gpid1, gpid2, pedigreeList,root,root_id,csv);
			}

			result=true;	//set flag to true

		}else if(count>1){
			//multiple_hit
			System.out.println("Multiple Hit");

			//print to file "CHOOSE GID" for all the pedigree line
			if(!root_id.equals("")){	// if searching the file				
				FileOutputStream fw = new FileOutputStream(csv, true);
				OutputStreamWriter osw = new OutputStreamWriter(fw, "UTF-8");
				BufferedWriter pw= new BufferedWriter(osw);

				pw.append(global_id+",");

				pw.flush();
				pw.close();
				osw.close();
				fw.close();
			}
			printChooseGID(pedigree, parent,root, id,csv);

			germplasm=getGermplasmList(manager, pedigree, count_LOCAL, count_CENTRAL);
			printMultipleHits(germplasm, manager, pedigree, id, pedigree);

			pedigreeList.remove(0);
			for(int i=0; i<pedigreeList.size(); i++){

				pedigree=pedigreeList.get(i);
				System.out.print(">> "+ pedigree + "\t");
				if(!root_id.equals("")){	// if searching the file				
					FileOutputStream fw = new FileOutputStream(csv, true);
					OutputStreamWriter osw = new OutputStreamWriter(fw, "UTF-8");
					BufferedWriter pw= new BufferedWriter(osw);

					pw.append(global_id+",");

					pw.flush();
					pw.close();
					osw.close();
					fw.close();
				}
				printNotSet(pedigree, parent,root, id,csv);
			}
			result=false;	//set flag to false

		}else{// count==0
			global_id=id;

			System.out.println("No hit in local and central");
			int event;

			System.out.println("Search Input File");

			//search_file	


			//event=search_List(manager, id, parent,parent, pedigree,root_id,csv);

			//if(event==0){
			//add GID
			int lastIndex=pedigreeList.size();
			String root_pedigree=pedigreeList.get(lastIndex-1);
			Germplasm g=isExisting(root_pedigree, manager);

			if(g.getGid()==null){

			}else{
				//					createPedigreeLine(GermplasmDataManager manager, ArrayList<String> pedigreeList, String id, String root, String csv) throws MiddlewareQueryException, IOException{
				createPedigreeLine(manager, pedigreeList,id,parent,csv);
			}
			result=true;

			/*}else if(event==2){
				System.out.print("()event 2");

				printNotSet(pedigree, parent, root, id, csv);

				result=false;

			}
			else{
				result=true;
			}
			 */

		}
		//clearing memory

		tokens=null;
		pedigreeList.clear();
		germplasm.clear();

		System.out.println(" ###ENDING..Parsing \n");

		return result;
	}

	public static void printMultipleHits(List<Germplasm> germplasm, GermplasmDataManager manager, String pedigree, String id, String root) throws IOException, MiddlewareQueryException{
		
		List<Name> name=null;
		String nval_gpid1, nval_gpid2;
		List<String> row=new ArrayList<String>();
		
		System.out.println("1 existingTerm:"+existingTerm_local);
		
		for (int i = 0; i < germplasm.size(); i++) {
			
			row= new ArrayList<String>();
			 System.out.println(germplasm.get(i).getGid());
			row.add(id);
			row.add(root);
			/*System.out.println("\n string: " + root);
			System.out.println("GID: " + germplasm.get(i).getGid());
			System.out.println("gpid1: " + germplasm.get(i).getGpid1());
			System.out.println("gpid2: " + germplasm.get(i).getGpid2());
			 */
			name = new ArrayList<Name>();
			if (germplasm.get(i).getGpid1() != 0
					&& germplasm.get(i).getGpid2() != 0) {

				name=manager.getNamesByGID(germplasm.get(i)
						.getGpid1(), 0, null);
				nval_gpid1 = name.get(0).getNval();

				System.out.println("nval_gpid1: " + nval_gpid1);				
				name=manager.getNamesByGID(germplasm.get(i)
						.getGpid2(), 0, null);
				nval_gpid2 = name.get(0).getNval();

				System.out.println("nval_gpid2: " + nval_gpid2);
			} else {
				if(germplasm.get(i).getGpid1() == 0 && germplasm.get(i).getGpid2() != 0){
					nval_gpid1 = "Source unknown";	
					name=manager.getNamesByGID(germplasm.get(i)
							.getGpid2(), 0, null);
					nval_gpid2 = name.get(0).getNval();

				}else if(germplasm.get(i).getGpid2() == 0 && germplasm.get(i).getGpid1() != 0){
					name=manager.getNamesByGID(germplasm.get(i)
							.getGpid1(), 0, null);
					nval_gpid1 = name.get(0).getNval();
					nval_gpid2 = "Source unknown";
				}else{
					nval_gpid1 = "Source unknown";
					nval_gpid2 = "Source unknown";
				}
			}
			Location location = manager.getLocationByID(germplasm.get(i)
					.getLocationId());
			Method method = manager.getMethodByID(germplasm.get(i)
					.getMethodId());

			row.add(""+germplasm.get(i).getGpid1());
			row.add( nval_gpid1);

			row.add(""+germplasm.get(i).getGpid2());
			row.add(nval_gpid2);

			row.add(""+germplasm.get(i).getGid()); // gid

			String meth=method.getMname().replace(",", "#");
			row.add(""+germplasm.get(i).getMethodId());
			row.add(meth ); // method
			String loc=location.getLname().replace(",", "#");
			row.add(""+germplasm.get(i).getLocationId());
			row.add(loc); // location

			//clearing memory
			location=null;
			method=null;
			
			existingTerm_local.add(row);
			
		}
		//existingTerm_local = existingTerm;
		
		System.out.println("2 existingTerm:"+existingTerm_local);
		//
		germplasm.clear();
		name.clear();
	}

	public static void createPedigreeLine(GermplasmDataManager manager, ArrayList<String> pedigreeList, String id, String root, String csv) throws MiddlewareQueryException, IOException{
		int gpid2 = 0, gpid1 = 0, gid;

		Collections.reverse(pedigreeList);

		ArrayList<Integer> pedigreeList_GID = new ArrayList<Integer>();
		List<Germplasm> list= new ArrayList<Germplasm>();
		List<Name> name=null;
		List<Name> name1=null;
		Germplasm g;
		for (int i = 0; i < pedigreeList.size(); i++) {

			name=manager.getNamesByGID(gpid1,0 , GermplasmNameType.DERIVATIVE_NAME);
			name1=manager.getNamesByGID(gpid2,0 , GermplasmNameType.DERIVATIVE_NAME);

			//int methodID=selectMethodType(manager,gpid1,gpid2,name.get(0).getNval(),name1.get(0).getNval());
			int methodID=33;
			gid = (int) addGID(manager, pedigreeList.get(i), gpid1, gpid2,
					methodID);
			g=new Germplasm();
			g=manager.getGermplasmByGID(gid);
			if (i == 0) {
				gpid2 = gid;
				gpid1 = gid;
			} else {
				gpid2 = gid;
			}
			//pedigreeList_GID.add(gid);
			list.add(g);
			//System.out.println(pedigreeList.get(i) + " gpid1: " + gpid1
			//		+ " gpid2: " + gpid2);
		}

		for (int i = list.size() - 1; i >= 0; i--) {
			System.out.println("YEAAAAAH id: " +id);
			printSuccess(pedigreeList.get(i), id, id, list.get(i), manager, root,csv, "new");
		}

		//clearing memory
		pedigreeList.clear();
		list.clear();
		name.clear();
		name1.clear();
		g=null;
	}

	public static int search_List(GermplasmDataManager manager, String id, String parent,String cross, String pedigree, String root_id, String csv_createdGID) throws IOException, MiddlewareQueryException, ParseException, InterruptedException{

		System.out.println(" ###STARTING..Searching the list");

		int result=0;
		String csvFile = "E:/xampp/htdocs/GMGR/csv_files/corrected.csv";
		String csv_createdGID2 = "E:/xampp/htdocs/GMGR/csv_files/createdGID2.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		FileReader fr= new FileReader(csvFile);
		br = new BufferedReader(fr);

		FileOutputStream fw;
		OutputStreamWriter osw;
		Writer pw_createdGID2;


		System.out.println(""+pedigree);
		String[] row;
		while ((line = br.readLine()) != null) {
			// use comma as separator
			//System.out.println(""+line);
			row = line.split(cvsSplitBy);
			System.out.println("row1: "+row[1]+"\t pedigree: "+pedigree);
			row[1] = row[1].replaceAll("\"", "");
			if (row[1].equals(pedigree)) {

				row[3] = row[3].replaceAll("\"", "");
				row[7] = row[7].replaceAll("\"", "");
				row[5] = row[5].replaceAll("\"", "");
				row[9] = row[9].replaceAll("\"", "");
				//female
				String female_id_l=row[2];
				String female_nval_l=row[5];
				//male
				String male_id_l=row[6];
				String male_nval_l=row[9];

				int id_l= Integer.valueOf(id);
				String id2_l= ""+(id_l+1);

				copy_corrected();
				Boolean event=processParents(manager, female_nval_l, female_id_l, male_nval_l, male_id_l, cross, cross, id, csv_createdGID,csv_createdGID2);


				System.out.println("event: "+event);
				if(event){
					result=1;	//success
					// update the createdGID file, update the row processed and the related pedigree in the list

					// update the corrected File

					/*int fgid= getGID_fromFile(female_nval_l, female_id_l);
					int mgid= getGID_fromFile(male_nval_l, male_id_l);

					int methodID=selectMethodType(manager,fgid,mgid,female_nval_l,male_nval_l);

					int cross_gid = (int) addGID(manager, cross,fgid,mgid,  methodID);

					Germplasm germplasm1 = manager.getGermplasmByGID(cross_gid);

					System.out.println("cross ID: "+female_id + "/" + male_id);
					System.out.println("rootID: "+id);

					printSuccess(cross, "", id, germplasm1, manager, cross,pw, "new");
					printSuccess(cross, id, female_id_l + "/" + male_id_l, germplasm1, manager, female_nval_l + "/" + male_nval_l,pw_createdGID2, "new");
					/*int a,b;
					if(Integer.valueOf(global_id)%2==0){
						a=Integer.valueOf(global_id);
						b=a+1;
					}else{
						b=Integer.valueOf(global_id);
						a=b-1;
					}
					updateFile_createdGID(""+germplasm1.getGid(), a + "/" + b, cross, manager, "new");
					 */
					//printSuccess(pedigree, parent, male_id, germplasm, manager, root, pw, newGID)

					/*
					 * updateFile_corrected(germplasm1, female_id, cross, manager);
					System.out.println("\t id: "+female_id + "/" + male_id);
					System.out.println("\t id: "+ cross);
					 */

				}else{
					result=2;	// print NOT SET
				}

				//result=true;
				break;// first to search, exit the loop 
			}
		}
		br.close();
		fr.close();

		new FileProperties().setFilePermission(csv_createdGID2);

		//clearing memonry

		row=null;

		System.out.println(" ###END..Searching the list \n");
		return result;
	}


	public static void single_Hit(GermplasmDataManager manager,String id, String parent, int gpid1, int gpid2, ArrayList<String> pedigreeList, String root,String root_id, String csv) throws MiddlewareQueryException, IOException{
		Boolean error=false;
		Germplasm germplasm;
		List<Germplasm> germplasmList=null;

		for(int i=0; i<pedigreeList.size(); i++){
			String pedigree=pedigreeList.get(i);
			System.out.print(">> "+ pedigree + "\t");
			germplasm= new Germplasm();
			if(error){	//if the precedent pedigree does not exist

				germplasmList = new ArrayList<Germplasm>();

				int count_LOCAL = countGermplasmByName(manager,pedigree, Database.LOCAL);
				int count_CENTRAL= countGermplasmByName(manager,pedigree,Database.CENTRAL);
				germplasmList=getGermplasmList( manager,pedigree, count_LOCAL, count_CENTRAL); //gets lists of germplasm with that name
				germplasm= getGermplasmByGpid(manager, gpid1, germplasmList);	// get the germplasm of the same gpid1, for derivative line, or gid equals to the gpid1
			}else{
				germplasm=manager.getGermplasmByGID(gpid2);
			}

			if(germplasm==null){ // this is an ERROR, the gid should exist
				error=true;
				if(!root_id.equals("")){
					FileOutputStream fw = new FileOutputStream(csv, true);
					OutputStreamWriter osw = new OutputStreamWriter(fw, "UTF-8");
					BufferedWriter pw= new BufferedWriter(osw);

					pw.append(global_id+",");
					pw.flush();
					pw.close();
					osw.close();
					fw.close();

				}
				printError(pedigree, parent, root,id,csv); //prints ERROR to file

			}else{
				gpid2=germplasm.getGpid2();
				gpid1=germplasm.getGpid1();
				error=false; //set the flag to false
				if(!root_id.equals("")){
					FileOutputStream fw = new FileOutputStream(csv, true);
					OutputStreamWriter osw = new OutputStreamWriter(fw, "UTF-8");
					BufferedWriter pw= new BufferedWriter(osw);

					pw.append(global_id+",");
					pw.flush();
					pw.close();
					osw.close();
					fw.close();

				}
				printSuccess(pedigree, parent ,id, germplasm,manager,root,csv, "false");	//print to file
			}
		}

		germplasm=null;
		//germplasmList.clear();
		pedigreeList.clear();
	}

	public static Germplasm getGermplasmByGpid(GermplasmDataManager manager, int gpid1, List<Germplasm> germplasmList){
		for(int i=0; i< germplasmList.size();i++){
			if(germplasmList.get(i).getGpid1()==gpid1 || germplasmList.get(i).getGid()==gpid1 ){
				return germplasmList.get(i);
			}
		}
		return null;
	} 

	public static List<Germplasm> getGermplasm(Database db, GermplasmDataManager manager,
			String pedigree, int count) throws MiddlewareQueryException,
			IOException {

		List<Germplasm> germplasm = new ArrayList<Germplasm>();

		germplasm = manager.getGermplasmByName(pedigree, 0, count,
				GetGermplasmByNameModes.NORMAL, Operation.EQUAL, 0, null,
				db);

		return germplasm;
	}

	public static List<Germplasm> getGermplasmList(GermplasmDataManager manager,
			String pedigree, int count_LOCAL, int count_CENTRAL) throws MiddlewareQueryException,
			IOException {

		List<Germplasm> germplasm = new ArrayList<Germplasm>();

		germplasm = manager.getGermplasmByName(pedigree, 0, count_LOCAL,
				GetGermplasmByNameModes.NORMAL, Operation.EQUAL, 0, null,
				Database.LOCAL);
		List<Germplasm> germplasm2 = new ArrayList<Germplasm>();
		germplasm2 = manager.getGermplasmByName(pedigree, 0, count_CENTRAL,
				GetGermplasmByNameModes.NORMAL, Operation.EQUAL, 0, null,
				Database.CENTRAL);

		for(int i=0; i<germplasm2.size();i++){
			germplasm.add(germplasm2.get(i));

		}
		germplasm2.clear();
		return germplasm;
	}
	public static ArrayList<String> saveToArray(ArrayList<String> array,
			String[] tokens) {

		String s = "";
		for (int i = 0; i < tokens.length; i++) {
			if (i == 0) {
				s = s + tokens[i];
			} else {
				s = s + "-" + tokens[i];
			}
			//System.out.println(""+s);
			array.add(s);
		}
		Collections.reverse(array);
		tokens=null;
		return array;
	}
	public static int countGermplasmByName(GermplasmDataManager manager,
			String s, Database db) throws MiddlewareQueryException {

		int count = (int) manager.countGermplasmByName(s,
				GetGermplasmByNameModes.NORMAL, Operation.EQUAL, 0, null,
				db);

		return count;
	}
	public static int addGID(GermplasmDataManager manager, String pedigree, int gpid1,
			int gpid2, int methodID) throws MiddlewareQueryException {
			
		int gid;
		//Germplasm Object
		Germplasm germplasm1 = new Germplasm();
		germplasm1.setMethodId(methodID);
		germplasm1.setGnpgs(0);
		germplasm1.setGpid1(gpid1);
		// int setGpid2=
		germplasm1.setGpid2(gpid2);
		germplasm1.setUserId(Integer.valueOf(userID_local));
		germplasm1.setLgid(-1);
		germplasm1.setLocationId(locationID);
		Date date = new Date();
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		//System.out.println(sdf.format(date));
		Integer gdate=Integer.valueOf(sdf.format(date));
		germplasm1.setGdate(gdate);
		germplasm1.setGrplce(0);
		germplasm1.setMgid(0);
		germplasm1.setReferenceId(1);
		germplasm1.setPreferredAbbreviation("N/A");
		germplasm1.setPreferredAbbreviation("N/A");

		//Name object
		Name name1 = new Name();
		name1.setNdate(0);
		name1.setNstat(0);
		name1.setReferenceId(0);
		name1.setUserId(Integer.valueOf(userID_local));
		name1.setLocationId(locationID);
		name1.setNval(pedigree);
		name1.setTypeId(0);

		gid = manager.addGermplasm(germplasm1, name1);
		System.out.println("Germplasm" + gid);

		germplasm1=null;
		name1=null;
		return gid;
	}

	public static int selectMethodType(GermplasmDataManager manager,int femaleGID, int maleGID, String female_nval, String male_nval)throws MiddlewareQueryException, IOException{
		System.out.println("****SELECT METHOD TYPE");
		Germplasm female_germplasm=manager.getGermplasmByGID(femaleGID);
		Germplasm male_germplasm=manager.getGermplasmByGID(maleGID);

		Boolean male_fixed,female_fixed=male_fixed=false;
		int methodType=0;
		String methodDesc="";

		Name name= manager.getNameByGIDAndNval(femaleGID, female_nval,GetGermplasmByNameModes.NORMAL);
		System.out.print("fgid: "+femaleGID);
		System.out.println("\t female: "+female_nval);

		if(name.getTypeId().equals(null)){
			System.out.println("NULL NAMETYPE");
		}
		int ntype=name.getTypeId();
		if(ntype==4 || ntype==6 || ntype==13 || ntype==20 || ntype==23){
			female_fixed=true;
		}

		name= manager.getNameByGIDAndNval(maleGID, male_nval,GetGermplasmByNameModes.NORMAL);
		System.out.print("mgid: "+maleGID);
		System.out.println("\t male: "+male_nval);
		ntype=name.getTypeId();
		if(ntype==4 || ntype==6 || ntype==13 || ntype==20 || ntype==23){
			male_fixed=true;
		}else{
			methodType=205;	//if AXB; single cross
			methodDesc="Single plant selection";
		}
		if(male_fixed && female_fixed){

			if(female_germplasm.getMethodId()==101 && male_germplasm.getMethodId()==101){ 
				methodType=103;	//if (AXB)X(CXD); double cross
				methodDesc="";
			}else if((female_germplasm.getMethodId()==101 && male_germplasm.getMethodId()==103) 
					|| (female_germplasm.getMethodId()==103 && male_germplasm.getMethodId()==101) ){
				methodType=102;	//if [(AXB)X(CXD)] X (EXF); 3 way cross
				methodDesc="Three-way cross";
			}else if((female_germplasm.getMethodId()==103 && male_germplasm.getMethodId()==103) 
					|| (female_germplasm.getMethodId()==106 && male_germplasm.getMethodId()==103)
					|| (female_germplasm.getMethodId()==103 && male_germplasm.getMethodId()==106)
					|| (female_germplasm.getMethodId()==106 && male_germplasm.getMethodId()==106)){
				methodType=106; //Cross between two three-way or more complex crosses
				methodDesc="Complex Cross";
			}else{
				methodType=101;	//if AXB; single cross
				methodDesc="Single cross";
			}
		}


		//clearing memory
		name=null;
		female_germplasm=null;
		male_germplasm=null;

		return methodType;


	}

	/* GET/SEARCH FROM FILE METHODS */

	public static int getLocationID() throws FileNotFoundException, IOException,
	ParseException {
		JSONParser parser = new JSONParser();
		FileReader json= new FileReader("E:/xampp/htdocs/GMGR/json_files/location.json");
		Object obj = parser.parse(json);
		JSONObject jsonObject = (JSONObject) obj;
		json.close();

		return Integer.valueOf((String) jsonObject.get("locationID"));

	}
	public static Germplasm isExisting(String pedigree, GermplasmDataManager manager) throws MiddlewareQueryException{
		Germplasm g= new Germplasm();
		Location location = manager.getLocationByID(locationID);

		long count= manager.countGermplasmByLocationName(location.getLname(), Operation.EQUAL, Database.LOCAL);
		count+=manager.countGermplasmByLocationName(location.getLname(), Operation.EQUAL, Database.CENTRAL);

		List<Germplasm> germplasm=manager.getGermplasmByLocationName(location.getLname(), 0, (int)count, Operation.EQUAL, Database.LOCAL);
		List<Germplasm> germplasm2 = new ArrayList<Germplasm>();
		germplasm2 = manager.getGermplasmByLocationName(location.getLname(), 0, (int)count, Operation.EQUAL, Database.CENTRAL);
		for(int i=0; i<germplasm2.size();i++){
			germplasm.add(germplasm2.get(i));
		}

		//clearing memory
		germplasm2.clear();

		System.out.println("size: "+germplasm.size());
		if(germplasm.isEmpty()){
			return g;
		}else{
			return germplasm.get(0);
		}
	}
	public static Germplasm isExisting(int fgid, int mgid, GermplasmDataManager manager) throws MiddlewareQueryException{
		Germplasm g= new Germplasm();
		Location location = manager.getLocationByID(locationID);

		long count= manager.countGermplasmByLocationName(location.getLname(), Operation.EQUAL, Database.LOCAL);
		count+=manager.countGermplasmByLocationName(location.getLname(), Operation.EQUAL, Database.CENTRAL);

		List<Germplasm> germplasm=manager.getGermplasmByLocationName(location.getLname(), 0, (int)count, Operation.EQUAL, Database.LOCAL);
		List<Germplasm> germplasm2 = new ArrayList<Germplasm>();
		germplasm2 = manager.getGermplasmByLocationName(location.getLname(), 0, (int)count, Operation.EQUAL, Database.CENTRAL);
		for(int i=0; i<germplasm2.size();i++){
			germplasm.add(germplasm2.get(i));

		}

		System.out.println("fgid: "+ fgid);
		System.out.println("mgid: "+ mgid);

		for(int i=0; i< germplasm.size(); i++){
			if((germplasm.get(i).getGpid1().equals(fgid) && germplasm.get(i).getGpid2().equals(mgid)) && (mgid!=0 && fgid!=0)){
				//Name name=manager.getGermplasmNameByID(germplasm.get(i).getGid());
				//System.out.println(germplasm.get(i).getGid()+" "+ name.getNval());
				g=germplasm.get(i);
			}
		}

		//clearing memory
		germplasm2.clear();
		germplasm.clear();
		return g;
	}

	private static int getGID_fromFile(String pedigree, String id) throws IOException {
		int gid = 0;
		
		for(int i=0; i< createdGID_local.size(); i++){
			List<String> row_object= createdGID_local.get(i);
					
			if (row_object.get(0).equals(id) && ((row_object.get(2).equals(pedigree) || row_object.get(1).equals(pedigree)))) {

				if(row_object.get(3).equals("CHOOSE GID") || row_object.get(3).equals("NOT SET")){
					gid= 0;

				}else{
					gid= Integer.valueOf(row_object.get(3));
					break;
				}
			}
		}
		
		System.out.println("get GID from file: "+gid);

		System.out.println("  ###END getGID_fromFile \n");

		//clearing memory

		return gid;
	}

	public static boolean has_GID(String id, String parent) throws IOException {

		for(int i=0; i< createdGID_local.size(); i++){
			List<String> row_object= createdGID_local.get(i);
			
			if (row_object.get(0) == id && row_object.get(1) == parent) {
				if (row_object.get(3) == "NOT SET" || row_object.get(3) == "CHOOSE GID") {
					return false;
				}
			}
		}
		
		return true;
	}

	/* END GET/SEARCH FROM FILE METHODS */

	/*UPDATE FILES METHODS*/
	private static List<List<String>> updateCreatedGID (String gid, String id,
			String pedigree, GermplasmDataManager manager,String newGID,
			List<List<String>> createdGID) throws NumberFormatException, MiddlewareQueryException {

		List<List<String>> output=new ArrayList<List<String>>();
		
		
		System.out.println("SIZE:"+ createdGID.size());
		System.out.println("CREATED**: "+createdGID);
		
		for (int i = 0; i < createdGID.size();i++) {
			List<String> row_object= createdGID.get(i);
			List<String> output_object= new ArrayList<String>();

			Germplasm germplasm= manager.getGermplasmByGID(Integer.valueOf(gid));

			System.out.println("\t id "+ id+ " row_object.get(0: "+ row_object.get(0));
			System.out.println("\t pedigree "+ pedigree+ " row_object.get(2: "+
					row_object.get(2)); 

			if (row_object.get(0).equals(id) && row_object.get(2).equals(pedigree)) {

				Location location = manager.getLocationByID(germplasm
						.getLocationId());

				Method method = manager.getMethodByID(germplasm.getMethodId());
				output_object.add(id);
				output_object.add(row_object.get(1));
				output_object.add(pedigree);
				output_object.add(germplasm.getGid().toString());
				output_object.add(germplasm.getMethodId().toString());					
				output_object.add(method.getMname().toString().replaceAll(",", "#"));
				output_object.add(germplasm.getLocationId().toString());
				output_object.add(location.getLname().toString().replaceAll(",", "#"));
				output_object.add(germplasm.getGpid1().toString());
				output_object.add(germplasm.getGpid2().toString());
				output_object.add(newGID);

				//clearing memory

				location=null;
				method=null;
				
			}else{
				//System.out.println("HERE");
				for(int j=0; j< row_object.size();j++){
					output_object.add(row_object.get(j));
				}
			}
			
			System.out.println("\t\t RESULT: "+output_object);
			output.add(output_object);

		}
		System.out.println("CREATED**: "+output);

		System.out.println("*** END Updating createdGID");
		createdGID_local=output;
		return output;

	}

	public static List<List<String>> updateFile_createdGID(String gid, String id,
			String pedigree, GermplasmDataManager manager,String newGID) throws IOException,
			MiddlewareQueryException, InterruptedException {

		List<List<String>> output=new ArrayList<List<String>>();

		for (int i = 0; i < createdGID_local.size();i++) {

			List<String> row_object= createdGID_local.get(i);
			List<String> output_object= new ArrayList<String>();

			if (row_object.get(0).equals(id) && row_object.get(1).equals(pedigree)) {
				Germplasm germplasm= manager.getGermplasmByGID(Integer.valueOf(gid));

				Location location = manager.getLocationByID(germplasm
						.getLocationId());

				Method method = manager.getMethodByID(germplasm.getMethodId());

				output_object.add(id);
				output_object.add(pedigree);
					output_object.add(germplasm.getGid().toString());
					output_object.add(germplasm.getMethodId().toString());					
					//cells[4] = ""+methodID;
					output_object.add(method.getMname().toString().replaceAll(",", "#"));
					output_object.add(germplasm.getLocationId().toString());
					output_object.add(location.getLname().toString().replaceAll(",", "#"));
					output_object.add(germplasm.getGpid1().toString());
					output_object.add(germplasm.getGpid2().toString());
					output_object.add(newGID);

					//clearing memeory

					location=null;
					method=null;
				}
				else{
					for(int j=0; j< row_object.size(); j++){
						output_object.add(row_object.get(j));
					}
				}
			
			output.add(output_object);
		}
		
		return output;
	}


	private static String[] processLine_corrected(String line, Germplasm germplasm, String id,
			String pedigree, GermplasmDataManager manager)
	throws MiddlewareQueryException {

		String[] cells = line.split(","); 
		cells[2] = cells[2].replaceAll("\"", "");

		//System.out.println("\t id "+ id+ " cells[2]: "+ cells[2]+ " GID: "+germplasm.getGid().toString());;

		//if (cells[0].equals(id) && cells[2].equals(pedigree)) {
		if (cells[2].equals(id) ) {

			cells[0] = germplasm.getGid().toString();

			cells[0] = cells[0].replaceAll("\"", "");
			return cells;
		}
		return cells;

	}
	private static void updateFile_corrected_2(Germplasm germplasm, String id,
			String pedigree, GermplasmDataManager manager) throws IOException,
			MiddlewareQueryException {

		// updated file, file to be written
		String temp = "E:/xampp/htdocs/GMGR/csv_files/updatedCorrected.csv";

		// file to be updatedcret, file to be read
		String original = "E:/xampp/htdocs/GMGR/csv_files/corrected.csv";

		String copy = "E:/xampp/htdocs/GMGR/csv_files/corrected2.csv";

		File file = new File(temp);

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fw=new FileOutputStream(file, true);
		OutputStreamWriter osw=new OutputStreamWriter(fw, "UTF-8");
		Writer bw = new BufferedWriter(osw);


		System.out.println("*** Starting Updating corrected2.csv");

		String line="";
		File file_copy = new File(copy);

		if(file_copy.exists()){
			BufferedReader br2 = null;
			String line_copy = "";
			FileReader fr2;

			fr2= new FileReader(copy);
			br2 = new BufferedReader(fr2);


			while ((line_copy = br2.readLine()) != null) {	//reads coorected.csv

				line=line_copy;
				System.out.println("A: "+StringUtils.join(line, ","));
				String[] processedLine = processLine_corrected(line, germplasm, id, pedigree,
						manager);
				System.out.println("B: "+StringUtils.join(processedLine, ","));
				bw.write(StringUtils.join(processedLine, ",")); // writing to the
				// new file with
				// updated germplasm
				bw.write("\n");
			}
			fr2.close();
			br2.close();


			bw.close();
			osw.close();
			fw.close();

			file_copy.delete();

			if(file_copy.exists()){
				System.out.println(file_copy.getName()+" NOT DELETED!");
			}else{
				System.out.println(file_copy.getName()+" is successfully DELETED!");
			}

			file = new File(copy);
			File file2= new File(temp);
			file2.renameTo(file);

			new FileProperties().setFilePermission(original);

		}
		System.out.println("*** END Updating corrected2.csv");
	}
	private static List<List<String>> updateFile_corrected(Germplasm germplasm, String id,
			String pedigree, GermplasmDataManager manager ) throws IOException,
			MiddlewareQueryException {


			System.out.println("*** Starting Updating corrected.csv");
			List<List<String>> output=new ArrayList<List<String>>();
			System.out.println("\t list: "+list_local);
			for (int i = 0; i < list_local.size();i++) {

				List<String> row_object= list_local.get(i);
				List<String> output_object= new ArrayList<String>();
				
				if (row_object.get(2).equals(id) ) {
					output_object.add(germplasm.getGid().toString());
					for(int j=1; j< row_object.size(); j++){
						output_object.add(row_object.get(j));
					}
					
				}else{
					for(int j=0; j< row_object.size(); j++){
						output_object.add(row_object.get(j));
					}
					
				}
				output.add(output_object);
			}
			System.out.println("\n\t list: "+output);
		System.out.println("*** END Updating corrected.csv");
		
		return output;
	}


	private static void copy_corrected() throws IOException,
	MiddlewareQueryException {

		// file to be updatedcret, file to be read
		String original = "E:/xampp/htdocs/GMGR/csv_files/corrected.csv";
		String copy = "E:/xampp/htdocs/GMGR/csv_files/corrected2.csv";

		File file_copy = new File(copy);
		File file_original = new File(original);

		Files.copy(file_original.toPath(), file_copy.toPath());

		System.out.println("*** END Coppying corrected.csv to corrected2.csv");
	}

	/*END OF UPDATE FILES METHODS*/

	/*PRINT To FILE METHODS */

	public static void printSuccess(String pedigree, String parent, String id, Germplasm germplasm, GermplasmDataManager manager, String root,String csv, String newGID) throws IOException, MiddlewareQueryException{

		//		String csv = "E:/xampp/htdocs/GMGR/csv_files/createdGID.csv";
		FileOutputStream fw = new FileOutputStream(csv, true);
		OutputStreamWriter osw = new OutputStreamWriter(fw, "UTF-8");
		BufferedWriter pw= new BufferedWriter(osw);

		pw.append(id + ",");
		System.out.println(id + ",");
		pw.append(root + ","); // parent
		pw.append(pedigree + ","); // pedigree

		System.out.println("gid: " + germplasm.getGid());

		Location location = manager.getLocationByID(germplasm.getLocationId());
		Method method = manager.getMethodByID(germplasm.getMethodId());

		pw.append(germplasm.getGid() + ","); // gid
		String meth=method.getMname().replaceAll(",", "#");


		pw.append(germplasm.getMethodId() + "," + meth + ","); // method
		String loc=location.getLname().replaceAll("," , "#");

		//System.out.print("loc "+loc);
		pw.append(germplasm.getLocationId() + "," + loc + ","); // location
		pw.append(germplasm.getGpid1() + ","); // gpid1
		pw.append(germplasm.getGpid2() + ","); // gpid2
		pw.append(newGID + ",\n"); // gpid2

		System.out.print(id + ",");
		System.out.print(root + ","); // parent
		System.out.print(pedigree + ","); // pedigree
		System.out.print(germplasm.getGid() + ","); // gid
		System.out.print(germplasm.getMethodId() + "," + meth + ","); // method
		System.out.print(germplasm.getLocationId() + "," + loc + ","); // location
		System.out.print(germplasm.getGpid1() + ","); // gpid1
		System.out.print(germplasm.getGpid2() + ","); // gpid2
		System.out.println(newGID + ",\n"); // gpid2


		//clearing memory
		germplasm=null;

		pw.flush();
		pw.close();
		osw.close();
		fw.close();

		method=null;
		location=null;
		germplasm=null;
		new FileProperties().setFilePermission(csv);

	}

	public static void printError(String pedigree, String parent, String root, String id,String csv) throws IOException, MiddlewareQueryException{

		//		String csv = "E:/xampp/htdocs/GMGR/csv_files/createdGID.csv";
		FileOutputStream fw = new FileOutputStream(csv, true);
		OutputStreamWriter osw = new OutputStreamWriter(fw, "UTF-8");
		BufferedWriter pw= new BufferedWriter(osw);

		pw.append(id + ",");
		pw.append(root + ","); // parent
		pw.append(pedigree + ","); // pedigree

		System.out.println("Dooes not exist" );

		pw.append("Does not exist" + ","); // gid
		pw.append("N/A" + "," + "N/A" + ","); // method
		pw.append("N/A" + "," + "N/A" + ","); // location
		pw.append("N/A" + ","); // gpid1
		pw.append("N/A" + ","); // gpid2
		pw.append(false + ",\n"); // gpid2

		pw.flush();
		pw.close();
		osw.close();
		fw.close();

		new FileProperties().setFilePermission(csv);


	}

	public static void printNotSet(String pedigree, String parent,String root, String id, String csv) throws IOException, MiddlewareQueryException{

		//		String csv = "E:/xampp/htdocs/GMGR/csv_files/createdGID.csv";

		FileOutputStream fw = new FileOutputStream(csv, true);
		OutputStreamWriter osw = new OutputStreamWriter(fw, "UTF-8");
		BufferedWriter pw= new BufferedWriter(osw);


		pw.append(id + ",");
		pw.append(root + ","); // parent
		pw.append(pedigree + ","); // pedigree

		System.out.println("NOT SET" );

		pw.append("NOT SET" + ","); // gid
		pw.append("N/A" + "," + "N/A" + ","); // method
		pw.append("N/A" + "," + "N/A" + ","); // location
		pw.append("N/A" + ","); // gpid1
		pw.append("N/A" + ","); // gpid2
		pw.append(false + ",\n"); // gpid2

		pw.flush();
		pw.close();
		osw.close();
		fw.close();

		new FileProperties().setFilePermission(csv);

	}

	public static void printChooseGID(String pedigree, String parent,String root, String id, String csv) throws IOException, MiddlewareQueryException{

		//		String csv = "E:/xampp/htdocs/GMGR/csv_files/createdGID.csv";
		FileOutputStream fw = new FileOutputStream(csv, true);
		OutputStreamWriter osw = new OutputStreamWriter(fw, "UTF-8");
		BufferedWriter pw= new BufferedWriter(osw);

		pw.append(id + ",");
		pw.append(root + ","); // parent
		pw.append(pedigree + ","); // pedigree

		System.out.println("CHOOSE GID" );

		pw.append("CHOOSE GID" + ","); // gid
		pw.append("N/A" + "," + "N/A" + ","); // method
		pw.append("N/A" + "," + "N/A" + ","); // location
		pw.append("N/A" + ","); // gpid1
		pw.append("N/A" + ","); // gpid2
		pw.append(false + ",\n"); // gpid2

		pw.flush();
		pw.close();
		osw.close();
		fw.close();

		new FileProperties().setFilePermission(csv);
	}

	/* END PRINT To FILE METHODS */


}

/*List<Germplasm> germplasmList = new ArrayList<Germplasm>();

int count_LOCAL = countGermplasmByName(manager,pedigree, Database.LOCAL);
int count_CENTRAL= countGermplasmByName(manager,pedigree,Database.CENTRAL);

if(count_LOCAL>0 && count_CENTRAL>0){	// germplasm name has at least one hit in local and central
	germplasmList=getGermplasmList( manager,pedigree, count_LOCAL, count_CENTRAL);

}else if(count_LOCAL>0 && count_CENTRAL==0){	//germplasm name has only at least one hit in the local
	germplasmList=getGermplasm(Database.LOCAL, manager,pedigree, count_LOCAL);

}else if(count_LOCAL==0 && count_CENTRAL>0){	//germplasm name has only at least one hit in the central
	germplasmList=getGermplasm(Database.CENTRAL,manager,pedigree, count_CENTRAL);

}

Germplasm germplasm= getGermplasmByGpid(manager, gpid1, gpid2, germplasmList);
if(germplasm!=null){ //if there is a germplasm given the gpid1 and gpid2 of the child pedigree
	//print to file
	printToFile(pedigree, parent ,id, germplasm,manager);

}else{// the gid of the pedigree, should create that gid
	//create GID with that gpid1 and location and gpid2 is the gid of the pedigree

}

if(count_LOCAL==0 && count_CENTRAL==0){	// no hits in local and central
	//search in the file
	//if does not exist in the file
	//create GID


}
 */
