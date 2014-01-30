package com.pedigreeimport.restjersey;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.GetGermplasmByNameModes;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.pedigreeimport.backend.CrossOp;
import com.pedigreeimport.backend.ParseCrossOp;
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
	static String cross_date;


	public static void main(String[] args) throws IOException, MiddlewareQueryException, ParseException {
		//	bulk_createGID();
		//	copy_corrected();
		//updateFile_createdGID();
		//single_createdGID();

	}
	public static JSONObject chooseGID(JSONObject obj,ManagerFactory factory ) throws MiddlewareQueryException, IOException, ParseException, InterruptedException{

		GermplasmDataManager manager = factory.getGermplasmDataManager();


		JSONObject jsonObject = (JSONObject) obj;
		List<List<String>> createdGID = (List<List<String>>) jsonObject.get("createdGID");
		createdGID_local.clear();
		createdGID_local=createdGID;
		createdGID=createdGID_local;
		List<List<String>> list = (List<List<String>>) jsonObject.get("list");
		//////System.out.println("\t list: "+list.size());
		list_local=list;
		//////System.out.println("\t list: "+list_local.size());
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
		//////System.out.println("json string:location ID: " + (String) details.get(6));
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

		String temp;
		mid=mid.replaceAll("\"", "");
		fid=fid.replaceAll("\"", "");
		int mid2=Integer.valueOf(mid),fid2=Integer.valueOf(fid);
		//////System.out.println("fid: "+fid2);
		//////System.out.println("mid: "+mid2);
		if (Integer.valueOf(mid) < Integer.valueOf(fid)) {
			temp = mid;
			mid = fid;
			fid = temp;

			temp = maleParent;
			maleParent = femaleParent;
			femaleParent = temp;
		}

		String[] tokens = new Tokenize().tokenize(parent1);
		ArrayList<String> pedigreeList = new ArrayList<String>();
		pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0] is the most recent pedigree, pedigreeList[size] is the root

		//Collections.reverse(pedigreeList); // index 0 is the root

		Germplasm germplasm = new Germplasm();

		int gid_local=Integer.valueOf(gid);
		int gpid1_local=gpid1,gpid2_local=gpid2;

		Boolean error=false;
		int index=-1;
		for(int i=0; i<pedigreeList.size();i++){
			if(pedigreeList.get(i).equals(term)){
				index=i;
			}
		}
		for(int i=index+1;i<pedigreeList.size();i++){
			if(error){
				List<Germplasm> germplasmList = new ArrayList<Germplasm>();

				int count_LOCAL = countGermplasmByName(manager,pedigreeList.get(i), Database.LOCAL);
				int count_CENTRAL= countGermplasmByName(manager,pedigreeList.get(i),Database.CENTRAL);
				germplasmList=getGermplasmList( manager,pedigreeList.get(i), count_LOCAL, count_CENTRAL); //gets lists of germplasm with that name
				germplasm= getGermplasmByGpid(manager, gpid1_local, germplasmList);	// get the germplasm of the same gpid1, for derivative line, or gid equals to the gpid1
				gpid2_local=germplasm.getGpid2();
				gpid1_local=germplasm.getGpid1();
				gid_local=germplasm.getGid();
				createdGID_local=updateFile_createdGID(""+gid_local, parent1ID, pedigreeList.get(i), manager,"false",parent1);	//update the createdGID file
				createdGID=createdGID_local;
				germplasmList.clear();	//clearing object

			}else{
				germplasm=manager.getGermplasmByGID(gpid2);
				if(germplasm==null){ // this is an ERROR, the gid should exist
					error=true;
					String remarks="Does not exist";
					createdGID_local=updateFile_createdGID(remarks, parent1ID, pedigreeList.get(i), manager,"false", parent1);	//update the createdGID file
					createdGID=createdGID_local;
				}else{
					gpid2_local=germplasm.getGpid2();
					gpid1_local=germplasm.getGpid1();
					gid_local=germplasm.getGid();
					createdGID_local=updateFile_createdGID(""+gid_local, parent1ID, pedigreeList.get(i), manager,"false", parent1);	//update the createdGID file
					createdGID=createdGID_local;
					error=false; //set the flag to false
				}
			}
		}
		gid_local=Integer.valueOf(gid);
		gpid1_local=gpid1;
		for(int i=index-1; i>=0;i--){
			System.out.println("\t create GID: "+ pedigreeList.get(i));

			int methodID=selectMethodType_DER(pedigreeList.get(i), parent1);
			gid_local = (int) addGID(manager, pedigreeList.get(i), gpid1_local, gid_local,
					methodID);
			if(i==0){
				GID=gid_local;
			}
			createdGID_local=updateFile_createdGID(""+gid_local, parent1ID, pedigreeList.get(i), manager,"new", parent1);	//update the createdGID file
			createdGID=createdGID_local;
		}

		int mgid=getGID_fromFile(maleParent, mid);
		int fgid=getGID_fromFile(femaleParent, fid);

		if (has_GID(parent2ID, parent2)) {
			System.out.println("cross: "+ cross);
			germplasm=isCross_existing(manager, cross);
			if (germplasm==null){
				int methodID=selectMethodType(manager,fgid,mgid,femaleParent,maleParent);

				int cross_gid = (int) addGID(manager, cross,fgid,mgid,  methodID);

				Germplasm germplasm1 = manager.getGermplasmByGID(cross_gid);

				createdGID_local=updateCreatedGID(""+germplasm1.getGid(), fid + "/" + mid, cross, manager, "new", createdGID);
				createdGID=createdGID_local;

				list_local=updateFile_corrected(germplasm1, fid, cross, manager);
				////System.out.println("\t id: "+fid + "/" + mid);
				////System.out.println("\t id: "+ cross);

				germplasm1=null;

			}else{
				createdGID_local=updateCreatedGID(""+germplasm.getGid(), fid + "/" + mid, cross, manager, "false", createdGID);
				createdGID=createdGID_local;

				list_local=updateFile_corrected(germplasm, fid , cross, manager);
			}
			System.out.println("createdGID: "+list_local);
		}
		details.clear();
		jsonObject.clear();
		germplasm=null;

		JSONObject data_output= new JSONObject();
		data_output.put("list",list_local);
		data_output.put("createdGID",createdGID);
		data_output.put("existingTerm",existingTerm_local);

		return data_output;

	}
	public static Germplasm isCross_existing(GermplasmDataManager manager,String cross) throws MiddlewareQueryException, IOException{
		List<Germplasm> germplasm = new ArrayList<Germplasm>();
		List<Germplasm> germplasm_fin=new ArrayList<Germplasm>();

		int count_LOCAL = countGermplasmByName(manager,cross, Database.LOCAL);
		int count_CENTRAL= countGermplasmByName(manager,cross,Database.CENTRAL);

		germplasm=getGermplasmList(manager, cross, count_LOCAL, count_CENTRAL);
		System.out.println("gsize: "+germplasm.size());

		for(int j=0; j<germplasm.size();j++){
			if(germplasm.get(j).getLocationId().equals(locationID)){
				germplasm_fin.add(germplasm.get(j));
			}
		}
		System.out.println("gfin size: "+germplasm.size());
		if(germplasm_fin.size()!=0){
			return germplasm_fin.get(0);
		}else{
			return null;
		}
	}

	public static int selectMethodType_DER(String pedigree, String parent){
		int methodID = 33;
		String tokens[]=pedigree.split("-");
		Pattern p = Pattern.compile("(\\d+)|(IR\\s\\d+)|(B)|(\\d+B)|(\\d*R)|(\\d*AC)|(C\\d+)|(\\d+MP)|((UBN|AJY|SRN|CPA|KKN|PMI|SKN|SRN|SDO)\\s\\d+)");
		System.out.println("pedigree: "+tokens[tokens.length-1]);
		String gen;
		if(!pedigree.contains("-")){	// if F1
			gen=pedigree;
		}else{
			gen=tokens[tokens.length-1];
			Matcher m = p.matcher(gen);
			if(m.find()){
				//printGroup(m);

				if(m.group(1)!=null && m.group(1).equals(gen)){	
					methodID=205;// Single plant selection
				}else if(m.group(2)!=null && m.group(2).equals(gen)){
					methodID=33;	// root is unknown
				}else if(m.group(3)!=null && m.group(3).equals(gen)){
					methodID=207;	//random bulk
				}else if(m.group(4)!=null && m.group(4).equals(gen)){
					methodID=206;	//selected bulk
				}else{
					methodID=205;	// no support for C|R|MP yet
				}
			}

		}


		return methodID;
	}
	public static void printGroup(Matcher m) {
		System.out.println("Group count: " + m.groupCount());
		int i;
		for (i = 0; i <= m.groupCount(); i++) {
			System.out.println(i + " : " + m.group(i));
		}
	}

	public static JSONObject single_createGID(JSONObject obj,ManagerFactory factory ) throws MiddlewareQueryException, IOException, ParseException, InterruptedException{

		GermplasmDataManager manager = factory.getGermplasmDataManager();


		JSONObject jsonObject = (JSONObject) obj;
		List<List<String>> createdGID = (List<List<String>>) jsonObject.get("createdGID");
		createdGID_local.clear();
		createdGID_local=createdGID;
		createdGID=createdGID_local;
		List<List<String>> list = (List<List<String>>) jsonObject.get("list");
		//////System.out.println("\t list: "+list.size());
		list_local=list;
		//////System.out.println("\t list: "+list_local.size());
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
		//////System.out.println("json string:location ID: " + (String) details.get(6));
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

		String temp;
		mid=mid.replaceAll("\"", "");
		fid=fid.replaceAll("\"", "");
		int mid2=Integer.valueOf(mid),fid2=Integer.valueOf(fid);
		//////System.out.println("fid: "+fid2);
		//////System.out.println("mid: "+mid2);
		if (Integer.valueOf(mid) < Integer.valueOf(fid)) {
			temp = mid;
			mid = fid;
			fid = temp;

			temp = maleParent;
			maleParent = femaleParent;
			femaleParent = temp;
		}
		//////System.out.println("mid: "+mid);

		createdGID_local = updateCreatedGID(gid, parent1ID, parent1, manager, "false", createdGID);	//update the createdGID file
		createdGID=createdGID_local;

		parse(manager, parent1,gid, parent1ID,gpid1,gpid2);	//parse the germplasm name of the chosen GID
		createdGID=createdGID_local;

		//locationID=getLocationID();
		int mgid=getGID_fromFile(maleParent, mid);
		int fgid=getGID_fromFile(femaleParent, fid);


		Germplasm germplasm=isExisting(fgid, mgid,  manager);

		if (has_GID(parent2ID, parent2)) {
			////System.out.println("PROCESSING CROSS");

			if(germplasm.getGid()==null || germplasm.getGpid1()!=fgid || germplasm.getGpid2()!=mgid){
				////System.out.println("fgid: "+ fgid);
				////System.out.println("mgid: "+ mgid);

				int methodID=selectMethodType(manager,fgid,mgid,femaleParent,maleParent);

				int cross_gid = (int) addGID(manager, cross,fgid,mgid,  methodID);

				Germplasm germplasm1 = manager.getGermplasmByGID(cross_gid);

				createdGID_local=updateCreatedGID(""+germplasm1.getGid(), fid + "/" + mid, cross, manager, "new", createdGID);
				createdGID=createdGID_local;

				list_local=updateFile_corrected(germplasm1, fid, cross, manager);
				////System.out.println("\t id: "+fid + "/" + mid);
				////System.out.println("\t id: "+ cross);

				germplasm1=null;
			}else{
				////System.out.println(" or HERE"+ germplasm.getGid());		
				List<Name> name = new ArrayList<Name>();
				name=manager.getNamesByGID(germplasm.getGid(), 0, null);

				createdGID_local=updateCreatedGID(""+germplasm.getGid(), fid + "/" + mid, name.get(0).getNval(), manager, "false", createdGID);
				createdGID=createdGID_local;

				list_local=updateFile_corrected(germplasm, fid , name.get(0).getNval(), manager);
				name=null;
			}
		}

		//clear all object, free memory
		details.clear();
		jsonObject.clear();
		germplasm=null;

		//createdGID_local.clear();

		JSONObject data_output= new JSONObject();
		data_output.put("list",list_local);
		data_output.put("createdGID",createdGID);
		data_output.put("existingTerm",existingTerm_local);

		//////System.out.println("createdGID: "+createdGID_local);
		//////System.out.println("\t existing: "+existingTerm_local);
		////System.out.println("\t list: "+list_local);

		////System.out.println("END SINGLE CREATED @ test.java");

		return data_output;

	}

	public static void parse(GermplasmDataManager manager, String parent, String gid, String id, int gpid1, int gpid2) throws MiddlewareQueryException, IOException, InterruptedException {

		////System.out.println("### STARTING parsing in sInlge creation of GID");
		String[] tokens = new Tokenize().tokenize(parent);
		ArrayList<String> pedigreeList = new ArrayList<String>();

		pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0] is the most recent pedigree, pedigreeList[size] is the root
		String pedigree;

		if(pedigreeList.size()>1){
			pedigreeList.remove(0); // remove the pedigree already processed

			pedigree=pedigreeList.get(0);	// first element is the parent


			////System.out.print(">> "+ pedigreeList.get(0) + "\t");
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

		////System.out.println("### END parsing in sInlge creation of GID");
	}

	public static void assignGID(GermplasmDataManager manager,String id, String parent, int gpid1, int gpid2, ArrayList<String> pedigreeList) throws MiddlewareQueryException, IOException, InterruptedException{
		Boolean error=false;
		String gid;	//set the gid to be string 
		Germplasm germplasm;
		for(int i=0; i<pedigreeList.size(); i++){
			String pedigree=pedigreeList.get(i);
			////System.out.print(">> "+ pedigree + "\t");
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

	public static JSONObject bulk_createGID2(List<List<String>> createdGID,List<List<String>> list, List<String> checked, int locationID_l, List<List<String>> existingTerm, String userID, ManagerFactory factory)throws IOException,
	MiddlewareQueryException, ParseException, InterruptedException {

		////System.out.println(" ###Starting..BULK CREATION of GID");

		createdGID_local = new ArrayList<List<String>>();
		createdGID_local=createdGID;
		list_local = new ArrayList<List<String>>();
		list_local=list;
		checked_local= new ArrayList<String>();


		if (existingTerm==null){
			//System.out.println();
			existingTerm=new ArrayList<List<String>>();;
			existingTerm_local = new ArrayList<List<String>>();
		}
		existingTerm_local=existingTerm;
		checked_local=checked;

		userID_local=userID;

		GermplasmDataManager manager = factory.getGermplasmDataManager();

		locationID=locationID_l;
		List<String> row_output=new ArrayList<String>();

		//System.out.println("HERE BULK2 existing: "+existingTerm);
		//System.out.println("HERE BULK2 checked: "+checked);
		//System.out.println("HERE BULK2 createdGID: "+createdGID);
		//System.out.println("HERE BULK2 list: "+list);
		//////System.out.println("HERE: "+checked.size());
		for (int i = 0; i < checked.size(); i++) {
			//////System.out.println("\n CHECK:: "+ checked.get(i));

			for(int j=0; j<list.size();j++){

				row_output= list.get(j);
				//////System.out.println("row_output: "+row_output);
				String female_id=row_output.get(2);
				String female_remarks=row_output.get(3);
				String female_nval=row_output.get(5);
				//male
				String male_id=row_output.get(6);
				String male_remarks=row_output.get(7);
				String male_nval=row_output.get(9);
				cross_date=row_output.get(10);
				//cross name
				String cross=row_output.get(1);

				if (female_id.equals(checked.get(i)) || male_id.equals(checked.get(i)) ) {
					//////System.out.println("female_remarks: "+female_remarks);
					//////System.out.println("male_remarks: "+male_remarks);

					if (female_remarks.equals("in standardized format") && male_remarks.equals("in standardized format")) {

						////System.out.println("\t" + "  ***Process parents");

						/*////System.out.println("locationID: "+locationID);
						////System.out.println("checked: "+ checked);
						////System.out.println("list: "+ list);
						////System.out.println("existingTerm: " + existingTerm);
						 */

						Boolean result=processParents(manager, female_nval, female_id, male_nval, male_id, cross,list);
						if(!result){
							printNotSet(cross, female_nval+"/"+male_nval, female_id + "/" + male_id);
						}
						////System.out.println("\t" + "  ***END Process parents");
					}
					break;
				}
			}
		}
		//row_output.clear();
		//list.clear();
		//existingTerm.clear();

		//////System.out.println("output: "+createdGID_local);
		//////System.out.println("list: "+list_local);
		existingTerm= existingTerm_local;
		JSONObject data_output= new JSONObject();
		data_output.put("existingTerm",existingTerm);
		data_output.put("list",list_local);
		data_output.put("createdGID",createdGID_local);



		////System.out.println("\n createdGID: "+createdGID_local.size()+"\t"+createdGID_local);
		////System.out.println("\n list: "+list_local.size()+"\t"+list_local);
		//System.out.println("END existing: "+existingTerm);

		////System.out.println(" ###ENDING..BULK CREATION of GID \n");

		return data_output;
	}

	public static JSONObject bulk_createGID(List<List<String>> list, List<String> checked, int locationID_l, List<List<String>> existingTerm, String userID,ManagerFactory factory)throws IOException,
	MiddlewareQueryException, ParseException, InterruptedException {

		////System.out.println(" ###Starting..BULK CREATION of GID");

		createdGID_local = new ArrayList<List<String>>();
		list_local = new ArrayList<List<String>>();
		checked_local= new ArrayList<String>();

		existingTerm_local=existingTerm;
		checked_local=checked;
		list_local=list;
		userID_local=userID;


		GermplasmDataManager manager = factory.getGermplasmDataManager();

		locationID=locationID_l;
		List<String> row_output=new ArrayList<String>();

		//////System.out.println("HERE: "+list);
		//////System.out.println("HERE: "+checked.size());
		for (int i = 0; i < checked.size(); i++) {
			//////System.out.println("\n CHECK:: "+ checked.get(i));

			for(int j=0; j<list.size();j++){

				row_output= list.get(j);
				//////System.out.println("row_output: "+row_output);
				String female_id=row_output.get(2);
				String female_remarks=row_output.get(3);
				String female_nval=row_output.get(5);
				//male
				String male_id=row_output.get(6);
				String male_remarks=row_output.get(7);
				String male_nval=row_output.get(9);
				cross_date=row_output.get(10);
				//cross name
				String cross=row_output.get(1);

				if (female_id.equals(checked.get(i)) || male_id.equals(checked.get(i)) ) {
					//////System.out.println("female_remarks: "+female_remarks);
					//////System.out.println("male_remarks: "+male_remarks);

					if (female_remarks.equals("in standardized format") && male_remarks.equals("in standardized format")) {

						////System.out.println("\t" + "  ***Process parents");

						/*////System.out.println("locationID: "+locationID);
						////System.out.println("checked: "+ checked);
						////System.out.println("list: "+ list);
						////System.out.println("existingTerm: " + existingTerm);
						 */

						Boolean result=processParents(manager, female_nval, female_id, male_nval, male_id, cross,list);
						list=list_local;
						if(!result){
							printNotSet(cross, female_nval+"/"+male_nval, female_id + "/" + male_id);
						}
						////System.out.println("\t" + "  ***END Process parents");
					}
					break;
				}
			}
		}
		//row_output.clear();
		//list.clear();
		//existingTerm.clear();

		//////System.out.println("output: "+createdGID_local);
		//////System.out.println("list: "+list_local);
		existingTerm= existingTerm_local;
		JSONObject data_output= new JSONObject();
		data_output.put("existingTerm",existingTerm);
		data_output.put("list",list_local);
		data_output.put("createdGID",createdGID_local);
		System.out.println("existingTerm:"+existingTerm_local);

		////System.out.println("\t created: "+createdGID_local.size()+"\t"+createdGID_local);

		////System.out.println(" ###ENDING..BULK CREATION of GID \n");

		return data_output;
	}
	public static List<String> parse_crossOp(List<String> pedigreeList, String nval, String id){
		List<String> row_output=new ArrayList<String>();
		for(int i=0; i<list_local.size(); i++){
			row_output= list_local.get(i);
			//////System.out.println("row_output: "+row_output);
			String female_id=row_output.get(2);
			String female_remarks=row_output.get(3);
			String female_nval=row_output.get(4);
			//male
			String male_id=row_output.get(6);
			String male_remarks=row_output.get(7);
			String male_nval=row_output.get(8);
			cross_date=row_output.get(10);
			//cross name
			String cross=row_output.get(1);

			if(female_id.equals(id)){
				System.out.println(":::: "+female_nval);
				String tokens[]=female_nval.split("#");
				for(int j=0; j<tokens.length;j++){
					if(!tokens[j].equals("")){
						pedigreeList.add(tokens[j]);
					}
				}
			}

			if(male_id.equals(id)){
				System.out.println(":::: "+male_nval);
				String tokens[]=male_nval.split("#");
				for(int j=0; j<tokens.length;j++){
					if(!tokens[j].equals("")){
						pedigreeList.add(tokens[j]);
					}
				}

			}

		}

		return pedigreeList;
	}
	public static int maxCross(int max,String line) {
		int count = 0, start = 0, end = line.length();
		char currChar;
		while (start < end) {
			currChar = line.charAt(start);
			if (currChar == '/') {
				count++;
				if (max < count) {
					max = count;
				}
			} else {
				count = 0;
			}
			start++;
		}
		return max;
	}
	private static JSONObject getParse(String line) throws MiddlewareQueryException, IOException { // method of backCrossing
		String temp = line;
		List<String> list = new ArrayList<String>();
		List<String> correctedList = new ArrayList<String>();

		Pattern p = Pattern.compile("\\*\\d"); // backcross to female
		Matcher m = p.matcher(line);

		int max =0;
		max=maxCross(max,line);

		while (m.find()) {
			String[] tokens = temp.split("\\*\\d", 2);
			//print(tokens);

			String slash = "";
			max++;
			for (int j = max; j > 0;) {
				slash = slash + "/";
				j--;
			}
			//System.out.println("token: " + tokens[0]);
			tokens[0] = tokens[0].concat(slash).concat(tokens[0]);
			temp.replaceFirst("\\*\\d", tokens[0]);
			temp = tokens[0].concat(tokens[1]);
			//System.out.println("token: " + temp);
		}

		System.out.println("\nBackCross to male; ");
		Pattern p1 = Pattern.compile("\\d\\*\\D"); // backcross to male
		Matcher m1 = p1.matcher(line);

		while (m1.find()) {
			String[] tokens = temp.split("\\d\\*", 2);
			//print(tokens);

			String slash = "";
			System.out.println("slash: "+max);
			max++;
			for (int j = max; j > 0;) {
				slash = slash + "/";
				j--;
			}

			tokens[0] = tokens[0].concat(tokens[1]);
			temp.replaceFirst("\\*\\d", tokens[0]);
			temp = tokens[0].concat(slash.concat(tokens[1]));
			System.out.println("token: " + temp);
		}
		JSONObject output= new JSONObject();
		output.put("max",max);
		output.put("line",temp);
		return output;

	}
	public static List<List<String>> method2(int max, int familyCount,List<String> row,List<List<String>> twoDim, List<List<String>> parents, List<String> row_parents) throws MiddlewareQueryException, IOException {
		if (max > 0) {
			String slash = "";
			for (int i = max; i > 0;) {
				slash = slash + "/";
				i--;
			}

			for (int i = 0; i < twoDim.size(); i++) {
				for (int j = 0; j < row.size(); j++) {
					if ("0".equals(twoDim.get(i).get(1))) {
						//System.out.println("token: " + twoDim.get(i).get(0));

						Pattern p1 = Pattern.compile(slash);
						Matcher m = p1.matcher(twoDim.get(i).get(0));

						while (m.find()) {
							String[] temp2 = twoDim.get(i).get(0).split(slash + "|\\+");   // ncarumba used the character + just to flag where to split the string
							//System.out.println(Arrays.toString(temp2));
							familyCount++;
							row_parents = new ArrayList<String>();
							row_parents.add(twoDim.get(i).get(0));
							for (int k = 0; k < temp2.length; k++) {
								row = new ArrayList<String>();


								row.add(temp2[k]);
								row.add("0");
								twoDim.add(row);

								if (k % 2 == 0) {
									System.out.println("\n(family" + familyCount + ") female:   " + temp2[k]);
									row_parents.add(temp2[k]);
								} else {
									System.out.println("(family" + familyCount + ") male:     " + temp2[k]+"\n");
									row_parents.add(temp2[k]);
								}
							}

							parents.add(row_parents);

							twoDim.get(i).remove(1);
							twoDim.get(i).add("1");
							//System.out.println("end finding "+slash);
						}

						//System.out.println("list:" + twoDim);
					}

				}
			}
			method2(max - 1, familyCount,row,twoDim,parents,row_parents);
		} else {
			//

			//for(int m=0; m<correctedList.size(); m++){
			//System.out.println("tokens @ parseCross: "+ correctedList.get(m));
			//}

			return parents;
		}
		//for(int m=0; m<list.size(); m++){
		//System.out.println("@ return tokens @ parseCross: "+ list.get(m));
		//}

		return parents;
	}
	public static Boolean checkString(String id, String pedigree){
		for(int i=0; i<createdGID_local.size();i++){
			List<String> row=createdGID_local.get(i);
			if(row.get(0).equals(id) && row.get(1).equals(pedigree)){
				return true;
			}
		}

		return false;
	}
	public static Boolean checkParent_crossOp(GermplasmDataManager manager, String nval, String id) throws MiddlewareQueryException, IOException{
		String parent=nval;
		List<String> pedigreeList = new ArrayList<String>();
		List<List<String>> temp = new ArrayList<List<String>>();
		List<List<String>> parents = new ArrayList<List<String>>();
		pedigreeList.add(parent);
		List<String> row = new ArrayList<String>();
		new CrossOp();
		pedigreeList=CrossOp.method2(parent,pedigreeList);
		Boolean isParent=false;
		int notParent_index=-1;
		for(int j=0; j<pedigreeList.size();j++){
			row = new ArrayList<String>();
			//System.out.println("::"+pedigreeList.get(j));
			if(!pedigreeList.get(j).contains("/")){
				if(!isParent){
					notParent_index=j;
				}
				row.add(pedigreeList.get(j));
				row.add("0");
				row.add("0");
				parents.add(row);
				//System.out.println("row_parents; "+row);
				isParent=true;
			}
		}
		System.out.println("PARENTS: "+parents);
		System.out.println("last index of the crosses:"+notParent_index);

		System.out.println("------");
		//pedigreeList= parse_crossOp(pedigreeList, nval, id);

		Boolean flag = true;
		Boolean single_hit=false;
		Boolean multiple_hit=false;
		Boolean no_hit=false;
		Boolean no_hit_flag=false;
		Boolean exitLoop=false;
		Boolean result=false;

		List<Integer> check=new ArrayList<Integer>();
		int index=0;
		int gpid2=0;
		int gpid1=0;
		int gid=0;
		int count_LOCAL; 
		int count_CENTRAL;
		int count=-1;;

		for(int i=0; i< pedigreeList.size();i++){

			List<Germplasm> germplasm_fin = new ArrayList<Germplasm>();
			List<Germplasm> germplasm = new ArrayList<Germplasm>();

			String pedigree=pedigreeList.get(i);
			System.out.println("pedigree: "+pedigree);
			if(no_hit ){

				count_LOCAL = countGermplasmByName(manager,pedigree, Database.LOCAL);
				count_CENTRAL= countGermplasmByName(manager,pedigree,Database.CENTRAL);

				germplasm=getGermplasmList(manager, pedigree, count_LOCAL, count_CENTRAL);
				//System.out.println("gcount: "+count_LOCAL);
				for(int j=0; j<germplasm.size();j++){
					if(germplasm.get(j).getLocationId().equals(locationID)){
						germplasm_fin.add(germplasm.get(j));
					}
				}
				count=germplasm_fin.size();
			}
			//System.out.println("count: "+count);
			if(count==1 || single_hit){
				System.out.println("\t Single HIT");
				single_hit=true;

				if(i==0 || no_hit){
					if(i==0){
						result=true;
					}
					no_hit=false;

					temp=printSuccess_temp(pedigree, parent, id, germplasm_fin.get(0), manager, "false",temp);
					GID=germplasm_fin.get(0).getGid();
					gpid2=germplasm_fin.get(0).getGpid2();
					gpid1=germplasm_fin.get(0).getGpid1();
					System.out.println("gpid1: "+gpid1);
					System.out.println("gpid2: "+gpid2);
					gid=germplasm_fin.get(0).getGid();

					check.add(gid);
					if(gpid1!=0 || gpid2!=0){
						check.add(gpid1);
						check.add(gpid2);
					}
				}else{

					System.out.println("\t checked: "+check);
					for(int j=0; j< check.size();j++){
						Germplasm g1=manager.getGermplasmByGID(check.get(j));
						List<Name> name = new ArrayList<Name>();
						//System.out.println("check: "+check.get(j));
						//System.out.println("gid: "+g1.getGid());
						name=manager.getNamesByGID(g1.getGid(), 0, null);
						//System.out.println("name: "+name.get(0).getNval());
						//System.out.println("P: "+pedigreeList.get(i));
						if(name.get(0).getNval().equals(pedigreeList.get(i))){

							System.out.println("\t "+pedigreeList.get(i)+" is found with GID="+g1.getGid());
							temp=printSuccess_temp(pedigreeList.get(i), parent, id, g1, manager, "false",temp);
							if((!check.contains(g1.getGpid1()) )) {
								System.out.println("\t gpid1 is not in the check list");
								if( g1.getGpid1()!=0 )   {
									System.out.println("\t gpid1 is not 0");
									check.add(g1.getGpid1());
								}
							}

							if((!check.contains(g1.getGpid2()) )) {
								System.out.println("\t gpid2 is not in the check list");
								if(g1.getGpid2()!=0  ) {
									System.out.println("\t gpid2 is not 0");
									check.add(g1.getGpid2());
								}
							}

							gpid1=g1.getGpid1();
							gpid2=g1.getGpid2();
							System.out.println("\t gpid1: "+gpid1);
							System.out.println("\t gpid2: "+gpid2);

							if(pedigreeList.get(i).contains("-") && !pedigreeList.get(i).contains("/")){

								String[] tokens = new Tokenize().tokenize(pedigreeList.get(i));
								ArrayList<String> pedigreeList_der = new ArrayList<String>();

								new test();
								pedigreeList_der = test.saveToArray(pedigreeList_der, tokens);
								for(int n=1; n<pedigreeList_der.size();n++){
									System.out.println("add:: "+pedigreeList_der.get(n));
									Germplasm germplasm2=manager.getGermplasmByGID(gpid2);
									temp=printSuccess_temp(pedigreeList_der.get(n), parent, id, germplasm2, manager, "false",temp);
									gpid1=germplasm2.getGpid1();
									gpid2=germplasm2.getGpid2();
									if(i==pedigreeList_der.size()-2){
										if(gpid1!=gpid2){
											germplasm2=manager.getGermplasmByGID(gpid1);
											printSuccess(pedigreeList_der.get(n), parent, id, germplasm2, manager, "false");
										}
									}
								}
								pedigreeList_der.clear();
								tokens=null;
							}
						}
						name=null;
						g1=null;
					}
				}

			}else if(count>1 || multiple_hit){	//multiple hits
				System.out.println("\t Multiple HIT");
				multiple_hit=true;
				if(i==0){
					printChooseGID(pedigree, parent, id);
				}else{
					if(no_hit){
						for(int k=0;k<temp.size();k++){
							createdGID_local.add(temp.get(k));
						}temp.clear();
						printChooseGID(pedigree, parent, id);
					}else{
						printNotSet(pedigreeList.get(i), parent, id);
					}
				}
				no_hit=false;

			}
			else{	//not existing
				System.out.println("\t No Hit");
				//System.out.println("pedigree: "+pedigree);
				temp=printNotSet_temp(pedigree, parent, id, temp);
				index=i;
				no_hit=true;
				no_hit_flag=true;

				if(i==notParent_index-1){
					System.out.println("EXIT the LOOP");
					exitLoop=true;
					//should exit the loop, check if parents exist
					break;
				}

			}

			germplasm.clear();
			germplasm_fin.clear();
		}
		if(exitLoop){
			//check parents if existing
			// if all parents are existing then create GID for crosses with operators
			// if not existing the create GID for that parent
			// if existing and there is a mutliple hits the set CHOOSE GID for that parent
			int gid_parent=0;
			List<String> row_parents=new ArrayList<String>();

			for(int i=0; i<parents.size(); i++){
				row_parents= parents.get(i);

				//System.out.println("r_parent::"+row_parents);
				JSONObject output=getPedigreeLine(manager, row_parents.get(0), id, temp, parent, gid_parent);
				Boolean result_flag=(Boolean) output.get("result");
				temp=(List<List<String>>) output.get("temp_fin");
				gid_parent= (Integer) output.get("gid_parent");
				check.add(gid_parent);
				if(result_flag){
					System.out.println("result::"+result_flag);
					row_parents.set(1, "1");
					row_parents.set(2, ""+gid_parent);
				}
				
				parents.set(i, row_parents);
				output.clear();
			}
			System.out.println("PARENTS: "+parents);
			int allExisting=0;
			for(int i=0; i<parents.size(); i++){
				row_parents= parents.get(i);;
				if(!row_parents.get(2).equals("0")){
					allExisting++;
				}
			}
			if(allExisting==parents.size()){
				result=true;
				//all parents exists
				//created GID for the crosses
				// update the temp
				JSONObject output=getParse(pedigreeList.get(0));
				String line= (String) output.get("line");
				int max= (Integer) output.get("max");
				
				List<List<String>> temp_crossesGID=new ArrayList<List<String>>();
				
				temp_crossesGID=createGID_crossParents(manager,pedigreeList,index, temp_crossesGID, check, line, max, parent, id);
				
				for(int i=0; i< temp_crossesGID.size(); i++){
					for(int j=0; j< temp.size(); j++){
						if(temp_crossesGID.get(i).get(2).equals(temp.get(j).get(2))){
							temp.set(j, temp_crossesGID.get(i));
						}
					//	System.out.println(""+temp_crossesGID.get(i));

					}
				}
				//System.out.println("[2]***********");
				for(int i=0; i< temp.size(); i++){
					//System.out.println(""+temp.get(i));
					createdGID_local.add(temp.get(i));
				}
				//System.out.println("***********");


			}else{
				//
				for(int k=0;k<temp.size();k++){
					createdGID_local.add(temp.get(k));
				}temp.clear();
			}

		}else if(!exitLoop && no_hit_flag && !multiple_hit){	// if there is a no hit and then a single hit after
			result=true;
			// create GID for the pedigreeList NOT SET
			System.out.println("if there is a no hit and then a single hit after");
			JSONObject output=getParse(pedigreeList.get(0));
			String line= (String) output.get("line");
			int max= (Integer) output.get("max");
			
			List<List<String>> temp_crossesGID=new ArrayList<List<String>>();
			
			temp_crossesGID=createGID_crossParents(manager,pedigreeList,index, temp_crossesGID, check, line, max, parent, id);
			
			
			for(int i=0; i< temp_crossesGID.size(); i++){
				for(int j=0; j< temp.size(); j++){
					if(temp_crossesGID.get(i).get(2).equals(temp.get(j).get(2))){
						temp.set(j, temp_crossesGID.get(i));
					}
				//	System.out.println(""+temp_crossesGID.get(i));

				}
			}
			//System.out.println("[2]***********");
			for(int i=0; i< temp.size(); i++){
				//System.out.println(""+temp.get(i));
				createdGID_local.add(temp.get(i));
			}
			
		}
		temp.clear();
		check.clear();
		pedigreeList.clear();

		return result;
	}
	public static List<List<String>> createGID_crossParents(GermplasmDataManager manager, List<String> pedigreeList, int index,List<List<String>> temp_crossesGID, List<Integer> check, String line, int max, String parent, String id) throws MiddlewareQueryException, IOException{
		
		String female_nval = null,male_nval;
		
		int familyCount = 0;
		int methodID=0;
		int gid_cross;
		int gpid1=0,gpid2=0;
		
		List<List<String>> twoDim = new ArrayList<List<String>>();
		List<String> row2 = new ArrayList<String>();
		List<List<String>> crosses = new ArrayList<List<String>>();
		List<String> row_crosses = new ArrayList<String>();

		row2.add(line);
		row2.add("0");   // 0 for unexplored token
		twoDim.add(row2);
		crosses=method2(max, familyCount, row2, twoDim,crosses,row_crosses);
		crosses.get(0).set(0, parent);

		System.out.println("***"+crosses);
		System.out.println("***"+check);
		for(int i=crosses.size()-1; i>=0; i--){
			row_crosses=crosses.get(i);
			for(int a=index; a>=0; a--){
				System.out.println("pedigreelist: "+pedigreeList.get(a)+" crosses: "+row_crosses.get(0));
				if(pedigreeList.get(a).equals(row_crosses.get(0))){
					for(int k=1; k< 3; k++){
						for(int j=0; j< check.size(); j++){
							System.out.println("K: "+k);
							System.out.println(" crosses: "+row_crosses.get(k));
							Germplasm g1=manager.getGermplasmByGID(check.get(j));
							List<Name> name = new ArrayList<Name>();
							//System.out.println("check: "+check.get(j));
							//System.out.println("gid: "+g1.getGid());
							name=manager.getNamesByGID(g1.getGid(), 0, null);
							System.out.println("name: "+name.get(0).getNval());
							//System.out.println("P: "+pedigreeList.get(i));
							if(name.get(0).getNval().equals(row_crosses.get(k))){

								if(k==1){
									System.out.println("FEMALE: ");
									System.out.println("\t "+row_crosses.get(k)+" is found with GID="+g1.getGid());
									female_nval=row_crosses.get(k);
									gpid1=g1.getGid();	
								}else{
									System.out.println("MALE: ");
									System.out.println("\t "+row_crosses.get(k)+" is found with GID="+g1.getGid());

									gpid2=g1.getGid();
									male_nval=row_crosses.get(k);
									methodID=selectMethodType(manager, gpid1, gpid2, female_nval, male_nval);
									if(i==0){
										gid_cross=addGID(manager, parent, gpid1, gpid2, methodID);
										GID=gid_cross;
										g1=manager.getGermplasmByGID(gid_cross);
										temp_crossesGID=printSuccess_temp(parent, parent, id, g1, manager, "new", temp_crossesGID);
									}else{
										gid_cross=addGID(manager, row_crosses.get(0), gpid1, gpid2, methodID);
										g1=manager.getGermplasmByGID(gid_cross);
										temp_crossesGID=printSuccess_temp(row_crosses.get(0), parent, id, g1, manager, "new", temp_crossesGID);
									}

									check.add(gid_cross);

								}
								break;
							}
							name=null;
							g1=null;
						}
					}
				}
			}
		}
		//System.out.println("[1]**********");
		twoDim.clear();
		row_crosses.clear();
		crosses.clear();
		row2.clear();

		return temp_crossesGID;
	}
	
	public static JSONObject getPedigreeLine(GermplasmDataManager manager,String parent,String id, List<List<String>> temp_fin, String parent2, int GID) throws MiddlewareQueryException,
	IOException {

		String[] tokens = new Tokenize().tokenize(parent);
		ArrayList<String> pedigreeList = new ArrayList<String>();

		pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0] is the most recent pedigree, pedigreeList[size] is the root

		Boolean flag = true;
		Boolean single_hit=false;
		Boolean multiple_hit=false;
		Boolean error=false;
		Boolean result=false;

		List<Germplasm> germplasm_fin = new ArrayList<Germplasm>();
		List<Germplasm> germplasm = new ArrayList<Germplasm>();
		List<Germplasm> germplasmList=null;

		List<List<String>> temp = new ArrayList<List<String>>();
		int index=0;
		int gpid2=0;
		int gpid1=0;
		int gid=0;
		int gpid2_index=0;

		// index 0 is the parent, index pedigreeList.size()-1 is the root
		for(int i=0; i<pedigreeList.size(); i++){
			String pedigree=pedigreeList.get(i);
			System.out.println("pedigree: "+pedigree);
			System.out.println("result: "+result);
			if(flag){
				int count_LOCAL = countGermplasmByName(manager,pedigree, Database.LOCAL);
				int count_CENTRAL= countGermplasmByName(manager,pedigree,Database.CENTRAL);

				germplasm=getGermplasmList(manager, pedigree, count_LOCAL, count_CENTRAL);
				System.out.println("count in db: "+germplasm.size());
				for(int j=0; j<germplasm.size();j++){
					//System.out.println("\t ["+j+"]: "+germplasm.get(j).getLocationId());
					if(germplasm.get(j).getLocationId().equals(locationID)){
						germplasm_fin.add(germplasm.get(j));
					}
				}
				int count=germplasm_fin.size();

				if(count==1){	// only one germplasm with that name in the location
					System.out.print("count==1");
					System.out.println("\t "+pedigree);

					if(i==0){	// if it is the parent
						temp_fin=printSuccess_temp(pedigree, parent2, id, germplasm_fin.get(0), manager, "false",temp_fin);
						GID=germplasm_fin.get(0).getGid();
						gpid2=germplasm_fin.get(0).getGpid2();
						gpid1=germplasm_fin.get(0).getGpid1();
						gid=germplasm_fin.get(0).getGid();
					}else if(i==pedigreeList.size()-1){	// if root
						//germplasm_fin.get(0)
						List<Germplasm> list= new ArrayList<Germplasm>();
						gid=germplasm_fin.get(0).getGid();
						index=i;

						System.out.println("create GID from "+ pedigreeList.get(i-1));
						for(int k=i-1;k>=0;k--){
							System.out.println("\t create GID: "+ pedigreeList.get(k));
							if(k==i-1){
								gpid1=gid;
							}
							int methodID=selectMethodType_DER(pedigreeList.get(i), parent);

							int gid_single_hit = (int) addGID(manager, pedigreeList.get(k), gpid1, gid,
									methodID);
							if(k==0){
								GID=gid_single_hit;
							}
							gid=gid_single_hit;

							Germplasm g=manager.getGermplasmByGID(gid_single_hit);

							list.add(g);

						}
						int l=0;
						for (int m = list.size()-1 ; m >= 0; m--) {
							//printSuccess(pedigreeList.get(l), parent, id, list.get(m), manager, "new");
							temp_fin=printSuccess_temp(pedigreeList.get(l), parent2, id, list.get(m), manager, "new",temp_fin);
							l++;
						}
						list.clear();

						temp_fin=printSuccess_temp(pedigreeList.get(i), parent2, id, germplasm_fin.get(0), manager, "false",temp_fin);
					}
					else{	// not root and not the parent
						System.out.print("i: "+i);
						System.out.println("\t|  "+pedigree);
						temp=printSuccess_temp(pedigree, parent2, id, germplasm_fin.get(0), manager, "false", temp);
						gpid1=germplasm_fin.get(0).getGpid1();
						gpid2_index=germplasm_fin.get(0).getGpid2();
						gid=germplasm_fin.get(0).getGid();
					}


					flag=false;
					single_hit=true;
					multiple_hit=false;
					index=i;
					result=true;

					//System.out.println("TEMP: "+temp);

				}else if(count>1){	//multiple germplasm name in a location
					System.out.print("count>1");
					System.out.println("\t "+pedigree);

					multipleHits_inLocation(germplasm_fin, manager, pedigree, id, parent2,pedigreeList);
					if(i==0)	// if it is the parent
						temp_fin=printChooseGID_temp(pedigree, parent2, id,temp_fin);
					else if(i==pedigreeList.size()-1){	//if it is the root
						for(int k=0;k<temp.size();k++){
							temp_fin.add(temp.get(k));
						}
						temp_fin=printChooseGID_temp(pedigree, parent2, id,temp_fin);
					}
					else{	// if not the root and not the parent
						temp=printChooseGID_temp(pedigree, parent2, id, temp);
					}
					multiple_hit=true;
					flag=false;
					index=i;
					result=false;

				}else{
					System.out.print("count==0");
					System.out.println("\t "+pedigree);

					//no germplasm name in the list's location
					if(i==pedigreeList.size()-1){	//if root assign GID from the root
						JSONObject output2=createPedigreeLine_CrossOp(manager, pedigreeList, id, pedigreeList.get(i),parent2,temp_fin,GID);
						GID=(Integer) output2.get("GID");
						temp_fin=(List<List<String>>) output2.get("temp_fin");

						result=true;
					}else{	//else, not root, print NOT SET

						temp=printNotSet_temp(pedigree, parent2, id, temp);
						single_hit=false;
						multiple_hit=false;
						flag=true;
					}
				}

			}else{
				if(multiple_hit){
					System.out.println("\t multiple hit is true");
					if(i-1==index && (i-1)!=0){	 // if the previous is the 'index' and not the parent

						for(int k=0;k<temp.size();k++){
							temp_fin.add(temp.get(k));
						}
						temp_fin=printNotSet_temp(pedigree, parent2, id,temp_fin);
					}else{
						System.out.println("\t"+pedigree+"is NOT SET");
						temp_fin=printNotSet_temp(pedigree, parent2, id,temp_fin);
					}
					single_hit=false;
					flag=false;
					multiple_hit=true;
					result=false;
				}else if(single_hit){
					System.out.println("\t Single hit is true");
					if(i-1==index && (i-1)!=0){	// if the previous is not the parent and is the 'index'
						//create GID for the index onwards

						List<Germplasm> list= new ArrayList<Germplasm>();
						//gid=germplasm_fin.get(0).getGid();

						System.out.println("create GID from "+ pedigreeList.get(i-2));
						for(int k=i-2;k>=0;k--){
							System.out.println("\t create GID: "+ pedigreeList.get(k));

							int methodID=selectMethodType_DER(pedigreeList.get(i), parent);

							gpid2=gid;

							System.out.println("gpid1: "+gpid1);
							System.out.println("gpid2: "+gpid2);

							int gid_single_hit = (int) addGID(manager, pedigreeList.get(k), gpid1, gpid2,
									methodID);
							if(k==0){
								GID=gid_single_hit;
							}
							gid=gid_single_hit;

							Germplasm g=manager.getGermplasmByGID(gid_single_hit);

							//printSuccess(pedigreeList.get(k), parent, id, g, manager, "false");
							list.add(g);

						}
						int l=0;
						for (int m = list.size()-1 ; m >= 0; m--) {
							temp_fin=printSuccess_temp(pedigreeList.get(l), parent2, id, list.get(m), manager, "new",temp_fin);
							l++;
						}
						list.clear();

						//printSuccess(pedigreeList.get(i-1), parent, id, germplasm_fin.get(0), manager, "false");
						temp_fin.add(temp.get(index));

						Germplasm g=manager.getGermplasmByGID(gpid2_index);

						if(g==null){ // this is an ERROR, the gid should exist
							error=true;

							temp_fin=printError_temp(pedigree, parent2,id,temp); //prints ERROR to file

						}else{
							gpid2=g.getGpid2();
							gpid1=g.getGpid1();
							error=false; //set the flag to false

							temp_fin=printSuccess_temp(pedigree,parent2,id, g,manager, "false",temp_fin);	//print to file
							System.out.println("\t"+pedigree+" is found");
						}
					}else{
						Germplasm g;
						if(error){	//if the precedent pedigree does not exist

							germplasmList = new ArrayList<Germplasm>();

							int count_LOCAL = countGermplasmByName(manager,pedigree, Database.LOCAL);
							int count_CENTRAL= countGermplasmByName(manager,pedigree,Database.CENTRAL);
							germplasmList=getGermplasmList( manager,pedigree, count_LOCAL, count_CENTRAL); //gets lists of germplasm with that name
							g= getGermplasmByGpid(manager, gpid1, germplasmList);	// get the germplasm of the same gpid1, for derivative line, or gid equals to the gpid1
						}else{
							g=manager.getGermplasmByGID(gpid2);
							System.out.println("gpid2="+gpid2);
						}
						if(g==null){ // this is an ERROR, the gid should exist
							error=true;

							temp_fin=printError_temp(pedigree, parent2,id,temp_fin); //prints ERROR to file

						}else{
							gpid2=g.getGpid2();
							gpid1=g.getGpid1();
							error=false; //set the flag to false

							temp_fin=printSuccess_temp(pedigree,parent2,id, g,manager, "false",temp_fin);	//print to file
						}
					}
					single_hit=true;
					flag=false;
					multiple_hit=false;
					result=true;
				}
			}			
		}

		temp.clear();
		germplasm.clear();
		germplasm_fin.clear();
		JSONObject output= new JSONObject();
		System.out.println(parent+": "+result);
		output.put("result", result);
		output.put("temp_fin", temp_fin);
		output.put("gid_parent", GID);
		return output;
	}

	private static Boolean processParents(GermplasmDataManager manager,
			String female_nval, String female_id, String male_nval,
			String male_id, String cross, List<List<String>> list) throws MiddlewareQueryException, IOException {

		boolean female=false, male=false;
		int fgid = 0, mgid = 0;

		if(female_nval.contains("/") || female_nval.contains("*")){
			System.out.println("female has cross operators");
			female=checkParent_crossOp(manager, female_nval, female_id);
		}else{
			female=checkParent(manager,female_nval, female_id);
		}
		//System.out.println("female: "+female);
		fgid=GID;
		////System.out.println("\nmale:");
		male=checkParent(manager,male_nval, male_id);
		//System.out.println("male: "+male);
		mgid=GID;

		if(male && female){
			Germplasm germplasm=isCross_existing(manager, cross);
			if (germplasm==null || germplasm.getGpid1()!=fgid || germplasm.getGpid2()!=mgid){
				System.out.println("createdGID for cross "+ cross);
				int methodID=selectMethodType(manager,fgid,mgid,female_nval,male_nval);

				int cross_gid = (int) addGID(manager, cross,fgid,mgid,  methodID);

				Germplasm germplasm1 = manager.getGermplasmByGID(cross_gid);

				printSuccess(cross,female_nval + "/" + male_nval, female_id + "/" + male_id, germplasm1, manager,  "new");

				list_local=updateFile_corrected(germplasm1, female_id, cross, manager);
				////System.out.println("\t id: "+fid + "/" + mid);
				////System.out.println("\t id: "+ cross);

				germplasm1=null;

			}else{
				System.out.println("cross "+ cross+ " already exists");
				printSuccess(cross,female_nval + "/" + male_nval, female_id + "/" + male_id, germplasm, manager,  "old");

				list_local=updateFile_corrected(germplasm, female_id , cross, manager);
			}

			germplasm=null;
		}else{
			list_local=list;
		}
		System.out.println("table2: "+list_local);

		//////System.out.println("list_local: "+list_local);
		////System.out.println(" ###END..Processing of Parents \n");

		if(male && female){
			return true;
		}else{
			return false;
		}
	}
	public static Boolean checkParent(GermplasmDataManager manager,String parent,String id) throws MiddlewareQueryException,
	IOException {

		String[] tokens = new Tokenize().tokenize(parent);
		ArrayList<String> pedigreeList = new ArrayList<String>();

		pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0] is the most recent pedigree, pedigreeList[size] is the root

		Boolean flag = true;
		Boolean single_hit=false;
		Boolean multiple_hit=false;
		Boolean error=false;
		Boolean result=false;

		List<Germplasm> germplasm_fin = new ArrayList<Germplasm>();
		List<Germplasm> germplasm = new ArrayList<Germplasm>();
		List<Germplasm> germplasmList=null;

		List<List<String>> temp = new ArrayList<List<String>>();
		int index=0;
		int gpid2=0;
		int gpid1=0;
		int gid=0;
		int gpid2_index=0;

		// index 0 is the parent, index pedigreeList.size()-1 is the root
		for(int i=0; i<pedigreeList.size(); i++){
			String pedigree=pedigreeList.get(i);

			if(flag){
				int count_LOCAL = countGermplasmByName(manager,pedigree, Database.LOCAL);
				int count_CENTRAL= countGermplasmByName(manager,pedigree,Database.CENTRAL);

				germplasm=getGermplasmList(manager, pedigree, count_LOCAL, count_CENTRAL);
				System.out.println("count in db: "+germplasm.size());
				for(int j=0; j<germplasm.size();j++){
					//System.out.println("\t ["+j+"]: "+germplasm.get(j).getLocationId());
					if(germplasm.get(j).getLocationId().equals(locationID)){
						germplasm_fin.add(germplasm.get(j));
					}
				}
				int count=germplasm_fin.size();

				if(count==1){	// only one germplasm with that name in the location
					System.out.print("count==1");
					System.out.println("\t "+pedigree);

					if(i==0){	// if it is the parent
						printSuccess(pedigree, parent, id, germplasm_fin.get(0), manager, "false");
						GID=germplasm_fin.get(0).getGid();
						gpid2=germplasm_fin.get(0).getGpid2();
						gpid1=germplasm_fin.get(0).getGpid1();
						gid=germplasm_fin.get(0).getGid();
					}else if(i==pedigreeList.size()-1){	// if root
						//germplasm_fin.get(0)
						List<Germplasm> list= new ArrayList<Germplasm>();
						gid=germplasm_fin.get(0).getGid();
						index=i;

						System.out.println("create GID from "+ pedigreeList.get(i-1));
						for(int k=i-1;k>=0;k--){
							System.out.println("\t create GID: "+ pedigreeList.get(k));
							if(k==i-1){
								gpid1=gid;
							}
							int methodID=selectMethodType_DER(pedigreeList.get(i), parent);

							int gid_single_hit = (int) addGID(manager, pedigreeList.get(k), gpid1, gid,
									methodID);
							if(k==0){
								GID=gid_single_hit;
							}
							gid=gid_single_hit;

							Germplasm g=manager.getGermplasmByGID(gid_single_hit);

							list.add(g);

						}
						int l=0;
						for (int m = list.size()-1 ; m >= 0; m--) {
							printSuccess(pedigreeList.get(l), parent, id, list.get(m), manager, "new");
							l++;
						}
						list.clear();

						printSuccess(pedigreeList.get(i), parent, id, germplasm_fin.get(0), manager, "false");
					}
					else{	// not root and not the parent
						System.out.print("i: "+i);
						System.out.println("\t|  "+pedigree);
						temp=printSuccess_temp(pedigree, parent, id, germplasm_fin.get(0), manager, "false", temp);
						gpid1=germplasm_fin.get(0).getGpid1();
						gpid2_index=germplasm_fin.get(0).getGpid2();
						gid=germplasm_fin.get(0).getGid();
					}


					flag=false;
					single_hit=true;
					multiple_hit=false;
					index=i;
					result=true;

					//System.out.println("TEMP: "+temp);

				}else if(count>1){	//multiple germplasm name in a location
					System.out.print("count>1");
					System.out.println("\t "+pedigree);

					multipleHits_inLocation(germplasm_fin, manager, pedigree, id, parent,pedigreeList);
					if(i==0)	// if it is the parent
						printChooseGID(pedigree, parent, id);
					else if(i==pedigreeList.size()-1){	//if it is the root
						for(int k=0;k<temp.size();k++){
							createdGID_local.add(temp.get(k));
						}
						printChooseGID(pedigree, parent, id);
					}
					else{	// if not the root and not the parent
						temp=printChooseGID_temp(pedigree, parent, id, temp);
					}
					multiple_hit=true;
					flag=false;
					index=i;
					result=false;

				}else{
					System.out.print("count==0");
					System.out.println("\t "+pedigree);

					//no germplasm name in the list's location
					if(i==pedigreeList.size()-1){	//if root assign GID from the root
						createPedigreeLine2(manager, pedigreeList, id, parent);
						result=true;
					}else{	//else, not root, print NOT SET

						temp=printNotSet_temp(pedigree, parent, id, temp);
						single_hit=false;
						multiple_hit=false;
						flag=true;
					}
				}

			}else{
				if(multiple_hit){
					System.out.println("\t multiple hit is true");
					if(i-1==index && (i-1)!=0){	 // if the previous is the 'index' and not the parent

						for(int k=0;k<temp.size();k++){
							createdGID_local.add(temp.get(k));
						}
						printNotSet(pedigree, parent, id);
					}else{
						System.out.println("\t"+pedigree+"is NOT SET");
						printNotSet(pedigree, parent, id);
					}
					single_hit=false;
					flag=false;
					multiple_hit=true;
					result=false;
				}else if(single_hit){
					System.out.println("\t Single hit is true");
					if(i-1==index && (i-1)!=0){	// if the previous is not the parent and is the 'index'
						//create GID for the index onwards

						List<Germplasm> list= new ArrayList<Germplasm>();
						//gid=germplasm_fin.get(0).getGid();

						System.out.println("create GID from "+ pedigreeList.get(i-2));
						for(int k=i-2;k>=0;k--){
							System.out.println("\t create GID: "+ pedigreeList.get(k));

							int methodID=selectMethodType_DER(pedigreeList.get(i), parent);

							gpid2=gid;

							System.out.println("gpid1: "+gpid1);
							System.out.println("gpid2: "+gpid2);

							int gid_single_hit = (int) addGID(manager, pedigreeList.get(k), gpid1, gpid2,
									methodID);
							if(k==0){
								GID=gid_single_hit;
							}
							gid=gid_single_hit;

							Germplasm g=manager.getGermplasmByGID(gid_single_hit);

							//printSuccess(pedigreeList.get(k), parent, id, g, manager, "false");
							list.add(g);

						}
						int l=0;
						for (int m = list.size()-1 ; m >= 0; m--) {
							printSuccess(pedigreeList.get(l), parent, id, list.get(m), manager, "new");
							l++;
						}
						list.clear();

						//printSuccess(pedigreeList.get(i-1), parent, id, germplasm_fin.get(0), manager, "false");
						createdGID_local.add(temp.get(index));

						Germplasm g=manager.getGermplasmByGID(gpid2_index);

						if(g==null){ // this is an ERROR, the gid should exist
							error=true;

							printError(pedigree, parent,id); //prints ERROR to file

						}else{
							gpid2=g.getGpid2();
							gpid1=g.getGpid1();
							error=false; //set the flag to false

							printSuccess(pedigree,parent,id, g,manager, "false");	//print to file
							System.out.println("\t"+pedigree+" is found");
						}
					}else{
						Germplasm g;
						if(error){	//if the precedent pedigree does not exist

							germplasmList = new ArrayList<Germplasm>();

							int count_LOCAL = countGermplasmByName(manager,pedigree, Database.LOCAL);
							int count_CENTRAL= countGermplasmByName(manager,pedigree,Database.CENTRAL);
							germplasmList=getGermplasmList( manager,pedigree, count_LOCAL, count_CENTRAL); //gets lists of germplasm with that name
							g= getGermplasmByGpid(manager, gpid1, germplasmList);	// get the germplasm of the same gpid1, for derivative line, or gid equals to the gpid1
						}else{
							g=manager.getGermplasmByGID(gpid2);
						}
						if(g==null){ // this is an ERROR, the gid should exist
							error=true;

							printError(pedigree, parent,id); //prints ERROR to file

						}else{
							gpid2=g.getGpid2();
							gpid1=g.getGpid1();
							error=false; //set the flag to false

							printSuccess(pedigree,parent,id, g,manager, "false");	//print to file
						}
					}
					single_hit=true;
					flag=false;
					multiple_hit=false;
					result=true;
				}
			}			
		}

		temp.clear();
		germplasm.clear();
		germplasm_fin.clear();
		return result;
	}

	private static List<List<String>> printError_temp(String pedigree, String parent, String id, List<List<String>> temp) {
		List<String> row= new ArrayList<String>();

		row.add(id);
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		////System.out.println("Does not Exist");

		row.add("Does not Exist" ); // gid
		row.add("N/A"); // method
		row.add("N/A"); // method
		row.add("N/A"); // location
		row.add("N/A"); // location
		row.add("N/A" ); // gpid1
		row.add("N/A" ); // gpid2
		row.add(""+false ); // gpid2
		row.add(cross_date ); // cross' date of creation specified in the list

		createdGID_local.add(row);
		//////System.out.println("row: "+row);
		//////System.out.println("output: "+createdGID_local);
		//row.clear();
		temp.add(row);
		return temp;
	}

	private static List<List<String>> printSuccess_temp(String pedigree,String parent, String id,
			Germplasm germplasm, GermplasmDataManager manager,
			String tag, List<List<String>> temp) throws MiddlewareQueryException {
		List<String> row= new ArrayList<String>();
		row.add(id);
		//////System.out.println(id + ",");
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		//////System.out.println("gid: " + germplasm.getGid());

		Location location = manager.getLocationByID(germplasm.getLocationId());
		Method method = manager.getMethodByID(germplasm.getMethodId());

		row.add(""+germplasm.getGid()); // gid
		String meth=method.getMname().replaceAll(",", "#");


		row.add(""+germplasm.getMethodId()); // method
		row.add( meth ); // method

		String loc=location.getLname().replaceAll("," , "#");

		//////System.out.print("loc "+loc);
		row.add(""+germplasm.getLocationId()); // location
		row.add(loc); // location
		row.add(""+germplasm.getGpid1()); // gpid1
		row.add(""+germplasm.getGpid2()); // gpid2
		row.add(tag ); // gpid2
		row.add(cross_date ); // cross' date of creation specified in the list 

		/*////System.out.print(id + ",");
		////System.out.print(parent + ","); // parent
		////System.out.print(pedigree + ","); // pedigree
		////System.out.print(germplasm.getGid() + ","); // gid
		////System.out.print(germplasm.getMethodId() + "," + meth + ","); // method
		////System.out.print(germplasm.getLocationId() + "," + loc + ","); // location
		////System.out.print(germplasm.getGpid1() + ","); // gpid1
		////System.out.print(germplasm.getGpid2() + ","); // gpid2
		////System.out.println(tag + ",\n"); // gpid2
		 */
		//clearing memory
		germplasm=null;


		method=null;
		location=null;
		germplasm=null;

		temp.add(row);

		/*System.out.println("size:"+temp.size()+" temp: "+row);

		System.out.println("\n------------");
		for(int i=0; i<temp.size(); i++){
			System.out.println(""+temp.get(i));
		}
		System.out.println("------------");
		*/
		//////System.out.println("output: "+createdGID_local);
		//row.clear();
		return temp;
	}

	private static List<List<String>> printChooseGID_temp(String pedigree, String parent,
			String id,List<List<String>> temp) {
		List<String> row= new ArrayList<String>();
		row.add(id);
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		////System.out.println("CHOOSE GID" );

		row.add("CHOOSE GID" ); // gid
		row.add("N/A"); // method
		row.add("N/A"); // method
		row.add("N/A"); // location
		row.add("N/A"); // location
		row.add("N/A" ); // gpid1
		row.add("N/A" ); // gpid2
		row.add(""+false ); // gpid2
		row.add(cross_date ); // cross' date of creation specified in the list

		temp.add(row);
		//////System.out.println("row: "+row);
		//////System.out.println("output: "+createdGID_local);
		//row.clear();
		return temp;

	}

	public static List<List<String>> printNotSet_temp(String pedigree, String parent, String id, List<List<String>> temp) {
		List<String> row= new ArrayList<String>();
		row.add(id);
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		////System.out.println("NOT SET");

		row.add("NOT SET" ); // gid
		row.add("N/A"); // method
		row.add("N/A"); // method
		row.add("N/A"); // location
		row.add("N/A"); // location
		row.add("N/A" ); // gpid1
		row.add("N/A" ); // gpid2
		row.add(""+false ); // gpid2
		row.add(cross_date ); // cross' date of creation specified in the list

		temp.add(row);
		//////System.out.println("row: "+row);
		//////System.out.println("output: "+createdGID_local);
		//row.clear();
		return temp;

	}
	public static void multipleHits_inLocation(List<Germplasm> germplasm, GermplasmDataManager manager, String pedigree, String id, String root, ArrayList<String> pedigreeList) throws IOException, MiddlewareQueryException{

		List<Name> name=null;
		String nval_gpid1, nval_gpid2;
		List<String> row=new ArrayList<String>();

		//System.out.println("1 existingTerm:"+existingTerm_local);

		for (int i = 0; i < germplasm.size(); i++) {

			row= new ArrayList<String>();
			////System.out.println(germplasm.get(i).getGid());
			row.add(id);
			row.add(root);
			/*////System.out.println("\n string: " + root);
			////System.out.println("GID: " + germplasm.get(i).getGid());
			////System.out.println("gpid1: " + germplasm.get(i).getGpid1());
			////System.out.println("gpid2: " + germplasm.get(i).getGpid2());
			 */
			name = new ArrayList<Name>();
			if (germplasm.get(i).getGpid1() != 0
					&& germplasm.get(i).getGpid2() != 0) {

				name=manager.getNamesByGID(germplasm.get(i)
						.getGpid1(), 0, null);
				nval_gpid1 = name.get(0).getNval();

				////System.out.println("nval_gpid1: " + nval_gpid1);				
				name=manager.getNamesByGID(germplasm.get(i)
						.getGpid2(), 0, null);
				nval_gpid2 = name.get(0).getNval();

				////System.out.println("nval_gpid2: " + nval_gpid2);
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
			row.add(pedigree); // pedigree name

			String date=germplasm.get(i).getGdate().toString();

			String yr,day,mo;
			//System.out.println("date: "+date);
			if(date.equals("0")){
				yr="0000";
				day="00";
				mo="00";
			}else{
				//System.out.println(date.charAt(0));
				yr=date.charAt(0)+""+date.charAt(1)+""+date.charAt(2)+""+date.charAt(3)+"-";
				day=date.charAt(4)+""+date.charAt(5)+"-";
				mo=date.charAt(6)+""+date.charAt(7)+"";
			}

			//System.out.println("date: "+yr.concat(day).concat(mo));
			row.add(yr.concat(day).concat(mo));	//date of creation
			row.add(cross_date);	//date of creation

			//clearing memory
			location=null;
			method=null;

			existingTerm_local.add(row);
			//System.out.println("row: "+row);

		}
		//existingTerm_local = existingTerm;

		System.out.println("existingTerm:"+existingTerm_local);
		//
		germplasm.clear();
		name.clear();
	}

	private static void createPedigreeLine2(GermplasmDataManager manager,
			ArrayList<String> pedigreeList, String id, String parent) throws MiddlewareQueryException {
		int gpid2 = 0, gpid1 = 0, gid;

		Collections.reverse(pedigreeList);

		List<Germplasm> list= new ArrayList<Germplasm>();
		Germplasm g;
		for (int i = 0; i < pedigreeList.size(); i++) {

			g=new Germplasm();

			if (i == 0) {
				int methodID=selectMethodType_DER(pedigreeList.get(i), parent);
				gid = (int) addGID(manager, pedigreeList.get(i), gpid1, gpid2,
						methodID);
				g=manager.getGermplasmByGID(gid);
				gpid2 = gid;
				gpid1 = gid;
			} else {
				int methodID=selectMethodType_DER(pedigreeList.get(i), parent);
				gid = (int) addGID(manager, pedigreeList.get(i), gpid1, gpid2,
						methodID);
				g=manager.getGermplasmByGID(gid);
				gpid2 = gid;
			}
			//pedigreeList_GID.add(gid);
			list.add(g);
			//////System.out.println(pedigreeList.get(i) + " gpid1: " + gpid1
			//		+ " gpid2: " + gpid2);
			if(i==pedigreeList.size()-1){
				GID=gid;
			}
		}

		for (int i = list.size() - 1; i >= 0; i--) {
			System.out.println("pedigreeList: " +pedigreeList.get(i));
			printSuccess(pedigreeList.get(i), parent, id, list.get(i), manager, "new");
		}

		//clearing memory
		pedigreeList.clear();
		list.clear();

		g=null;

	}

	private static JSONObject createPedigreeLine_CrossOp(GermplasmDataManager manager,
			ArrayList<String> pedigreeList, String id, String parent,String parent2, List<List<String>> temp_fin, int GID) throws MiddlewareQueryException {
		int gpid2 = 0, gpid1 = 0, gid;

		Collections.reverse(pedigreeList);

		List<Germplasm> list= new ArrayList<Germplasm>();
		Germplasm g;
		for (int i = 0; i < pedigreeList.size(); i++) {

			g=new Germplasm();

			if (i == 0) {
				int methodID=selectMethodType_DER(pedigreeList.get(i), parent);
				gid = (int) addGID(manager, pedigreeList.get(i), gpid1, gpid2,
						methodID);
				g=manager.getGermplasmByGID(gid);
				gpid2 = gid;
				gpid1 = gid;
			} else {
				int methodID=selectMethodType_DER(pedigreeList.get(i), parent);
				gid = (int) addGID(manager, pedigreeList.get(i), gpid1, gpid2,
						methodID);
				g=manager.getGermplasmByGID(gid);
				gpid2 = gid;
			}
			//pedigreeList_GID.add(gid);
			list.add(g);
			//////System.out.println(pedigreeList.get(i) + " gpid1: " + gpid1
			//		+ " gpid2: " + gpid2);
			if(i==pedigreeList.size()-1){
				GID=gid;
			}
		}

		for (int i = list.size() - 1; i >= 0; i--) {
			System.out.println("pedigreeList: " +pedigreeList.get(i));
			temp_fin=printSuccess_temp(pedigreeList.get(i), parent2, id, list.get(i), manager, "new", temp_fin);
		}

		//clearing memory
		pedigreeList.clear();
		list.clear();

		g=null;

		JSONObject output=new JSONObject();
		output.put("GID", GID);
		output.put("temp_fin", temp_fin);
		return output;

	}


	private static void printError(String pedigree, String parent, String id) {
		List<String> row= new ArrayList<String>();

		row.add(id);
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		////System.out.println("Does not Exist");

		row.add("Does not Exist" ); // gid
		row.add("N/A"); // method
		row.add("N/A"); // method
		row.add("N/A"); // location
		row.add("N/A"); // location
		row.add("N/A" ); // gpid1
		row.add("N/A" ); // gpid2
		row.add(""+false ); // gpid2
		row.add(cross_date ); // cross' date of creation specified in the list

		createdGID_local.add(row);
		//////System.out.println("row: "+row);
		//////System.out.println("output: "+createdGID_local);
		//row.clear();

	}


	private static void printNotSet(String pedigree, String parent, String id) {
		List<String> row= new ArrayList<String>();
		row.add(id);
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		////System.out.println("NOT SET");

		row.add("NOT SET" ); // gid
		row.add("N/A"); // method
		row.add("N/A"); // method
		row.add("N/A"); // location
		row.add("N/A"); // location
		row.add("N/A" ); // gpid1
		row.add("N/A" ); // gpid2
		row.add(""+false ); // gpid2
		row.add(cross_date ); // cross' date of creation specified in the list

		createdGID_local.add(row);
		//////System.out.println("row: "+row);
		//////System.out.println("output: "+createdGID_local);
		//row.clear();

	}


	private static void printChooseGID(String pedigree, String parent,
			String id) {
		List<String> row= new ArrayList<String>();
		row.add(id);
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		////System.out.println("CHOOSE GID" );

		row.add("CHOOSE GID" ); // gid
		row.add("N/A"); // method
		row.add("N/A"); // method
		row.add("N/A"); // location
		row.add("N/A"); // location
		row.add("N/A" ); // gpid1
		row.add("N/A" ); // gpid2
		row.add(""+false ); // gpid2
		row.add(cross_date ); // cross' date of creation specified in the list

		createdGID_local.add(row);

		//////System.out.println("row: "+row);
		//////System.out.println("output: "+createdGID_local);
		//row.clear();

	}


	private static void printSuccess(String pedigree,String parent, String id,
			Germplasm germplasm, GermplasmDataManager manager,
			String tag) throws MiddlewareQueryException {
		List<String> row= new ArrayList<String>();
		row.add(id);
		//////System.out.println(id + ",");
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		//////System.out.println("gid: " + germplasm.getGid());
		//System.out.println("location: " + germplasm.getLocationId());
		Location location = manager.getLocationByID(germplasm.getLocationId());
		Method method = manager.getMethodByID(germplasm.getMethodId());

		row.add(""+germplasm.getGid()); // gid
		//System.out.println("GID: "+germplasm.getGid());
		//System.out.println("MID: "+germplasm.getMethodId());
		//System.out.println("MNAME:  "+method.getMname());
		String meth=method.getMname().replaceAll(",", "#");


		row.add(""+germplasm.getMethodId()); // method
		row.add( meth ); // method

		String loc=location.getLname().replaceAll("," , "#");

		//////System.out.print("loc "+loc);
		row.add(""+germplasm.getLocationId()); // location
		row.add(loc); // location
		row.add(""+germplasm.getGpid1()); // gpid1
		row.add(""+germplasm.getGpid2()); // gpid2
		row.add(tag ); // gpid2
		row.add(cross_date ); // cross' date of creation specified in the list 

		/*////System.out.print(id + ",");
		////System.out.print(parent + ","); // parent
		////System.out.print(pedigree + ","); // pedigree
		////System.out.print(germplasm.getGid() + ","); // gid
		////System.out.print(germplasm.getMethodId() + "," + meth + ","); // method
		////System.out.print(germplasm.getLocationId() + "," + loc + ","); // location
		////System.out.print(germplasm.getGpid1() + ","); // gpid1
		////System.out.print(germplasm.getGpid2() + ","); // gpid2
		////System.out.println(tag + ",\n"); // gpid2
		 */
		//clearing memory
		germplasm=null;


		method=null;
		location=null;
		germplasm=null;

		createdGID_local.add(row);
		//////System.out.println("row: "+row);
		//////System.out.println("output: "+createdGID_local);
		//row.clear();
	}


	public static void printMultipleHits(List<Germplasm> germplasm, GermplasmDataManager manager, String pedigree, String id, String root) throws IOException, MiddlewareQueryException{

		List<Name> name=null;
		String nval_gpid1, nval_gpid2;
		List<String> row=new ArrayList<String>();

		//System.out.println("1 existingTerm:"+existingTerm_local);

		for (int i = 0; i < germplasm.size(); i++) {

			row= new ArrayList<String>();
			////System.out.println(germplasm.get(i).getGid());
			row.add(id);
			row.add(root);
			/*////System.out.println("\n string: " + root);
			////System.out.println("GID: " + germplasm.get(i).getGid());
			////System.out.println("gpid1: " + germplasm.get(i).getGpid1());
			////System.out.println("gpid2: " + germplasm.get(i).getGpid2());
			 */
			name = new ArrayList<Name>();
			if (germplasm.get(i).getGpid1() != 0
					&& germplasm.get(i).getGpid2() != 0) {

				name=manager.getNamesByGID(germplasm.get(i)
						.getGpid1(), 0, null);
				nval_gpid1 = name.get(0).getNval();

				////System.out.println("nval_gpid1: " + nval_gpid1);				
				name=manager.getNamesByGID(germplasm.get(i)
						.getGpid2(), 0, null);
				nval_gpid2 = name.get(0).getNval();

				////System.out.println("nval_gpid2: " + nval_gpid2);
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

			System.out.println("date: "+germplasm.get(i).getGdate());
			row.add(""+germplasm.get(i).getGdate());	//date of creation

			//clearing memory
			location=null;
			method=null;

			existingTerm_local.add(row);
			//System.out.println("row: "+row);

		}
		//existingTerm_local = existingTerm;

		//System.out.println("2 existingTerm:"+existingTerm_local);
		//
		germplasm.clear();
		name.clear();
	}

	public static Germplasm getGermplasmByGpid(GermplasmDataManager manager, int gpid1, List<Germplasm> germplasmList){
		for(int i=0; i< germplasmList.size();i++){
			if(germplasmList.get(i).getGpid1()==gpid1 || germplasmList.get(i).getGid()==gpid1 && germplasmList.get(i).getLocationId()==locationID){
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
			//////System.out.println(""+s);
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
		//////System.out.println(sdf.format(date));
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
		////System.out.println("Germplasm" + gid);

		germplasm1=null;
		name1=null;
		return gid;
	}

	public static int selectMethodType(GermplasmDataManager manager,int femaleGID, int maleGID, String female_nval, String male_nval)throws MiddlewareQueryException, IOException{
		////System.out.println("****SELECT METHOD TYPE");
		Germplasm female_germplasm=manager.getGermplasmByGID(femaleGID);
		Germplasm male_germplasm=manager.getGermplasmByGID(maleGID);

		int methodType=0;
		String methodDesc="";

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
				|| (female_germplasm.getMethodId()==106 && male_germplasm.getMethodId()==106)
				|| (female_germplasm.getMethodId()==106 && male_germplasm.getMethodId()==101)){
			methodType=106; //Cross between two three-way or more complex crosses or 1 3-way and a single cross
			methodDesc="Complex Cross";
		}else{
			methodType=101;	//if AXB; single cross
			methodDesc="Single cross";
		}

		//clearing memory
		female_germplasm=null;
		male_germplasm=null;

		return methodType;


	}

	/* GET/SEARCH FROM FILE METHODS */

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

		////System.out.println("size: "+germplasm.size());
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

		////System.out.println("fgid: "+ fgid);
		////System.out.println("mgid: "+ mgid);

		for(int i=0; i< germplasm.size(); i++){
			if((germplasm.get(i).getGpid1().equals(fgid) && germplasm.get(i).getGpid2().equals(mgid)) && (mgid!=0 && fgid!=0) && germplasm.get(i).getLocationId()==locationID){
				//Name name=manager.getGermplasmNameByID(germplasm.get(i).getGid());
				//////System.out.println(germplasm.get(i).getGid()+" "+ name.getNval());
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

		////System.out.println("get GID from file: "+gid);

		////System.out.println("  ###END getGID_fromFile \n");

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


		////System.out.println("SIZE:"+ createdGID.size());
		////System.out.println("CREATED**: "+createdGID);

		for (int i = 0; i < createdGID.size();i++) {
			List<String> row_object= createdGID.get(i);
			List<String> output_object= new ArrayList<String>();

			Germplasm germplasm= manager.getGermplasmByGID(Integer.valueOf(gid));

			////System.out.println("\t id "+ id+ " row_object.get(0: "+ row_object.get(0));
			////System.out.println("\t pedigree "+ pedigree+ " row_object.get(2: "+
			//		row_object.get(2)); 

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
				//////System.out.println("HERE");
				for(int j=0; j< row_object.size();j++){
					output_object.add(row_object.get(j));
				}
			}

			////System.out.println("\t\t RESULT: "+output_object);
			output.add(output_object);

		}
		////System.out.println("CREATED**: "+output);

		////System.out.println("*** END Updating createdGID");
		createdGID_local=output;
		return output;

	}

	public static List<List<String>> updateFile_createdGID(String gid, String id,
			String pedigree, GermplasmDataManager manager,String newGID,String parent) throws IOException,
			MiddlewareQueryException, InterruptedException {

		List<List<String>> output=new ArrayList<List<String>>();

		for (int i = 0; i < createdGID_local.size();i++) {

			List<String> row_object= createdGID_local.get(i);
			List<String> output_object= new ArrayList<String>();

			if (row_object.get(0).equals(id) && row_object.get(2).equals(pedigree)) {
				Germplasm germplasm= manager.getGermplasmByGID(Integer.valueOf(gid));

				Location location = manager.getLocationByID(germplasm
						.getLocationId());

				Method method = manager.getMethodByID(germplasm.getMethodId());

				output_object.add(id);
				output_object.add(parent);
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

	private static List<List<String>> updateFile_corrected(Germplasm germplasm, String id,
			String pedigree, GermplasmDataManager manager ) throws IOException,
			MiddlewareQueryException {


		////System.out.println("*** Starting Updating corrected.csv");
		List<List<String>> output=new ArrayList<List<String>>();
		////System.out.println("\t list: "+list_local);
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
		////System.out.println("\n\t list: "+output);
		////System.out.println("*** END Updating corrected.csv");

		return output;
	}


	/*END OF UPDATE FILES METHODS*/

	/*PRINT To FILE METHODS */



	/* END PRINT To FILE METHODS */


}