package com.pedigreeimport.restjersey;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.GetGermplasmByNameModes;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.Operation;
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
	public static void main(String[] args) throws IOException, MiddlewareQueryException, ParseException {
		//bulk_createdGID();
		single_createdGID();
	}	

	public static void single_createdGID() throws MiddlewareQueryException, IOException, ParseException{
		ManagerFactory factory = new Config().configDB();
		GermplasmDataManager manager = factory.getGermplasmDataManager();

		//reads json file with the details of the chosen germplasm
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader(
		"E:/xampp/htdocs/GMGR/protected/modules/term.json"));
		JSONObject jsonObject = (JSONObject) obj;

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

		JSONArray details = (JSONArray) jsonObject.get("germplasm");
		System.out.println("json string:location ID: "
				+ (String) details.get(6));
		int locationID = Integer.valueOf((String) details.get(6));
		int gpid1 = Integer.valueOf((String) details.get(8));
		int gpid2 = Integer.valueOf((String) details.get(9));
		String parent1ID = (String) details.get(0);
		// parent1ID, ID of parent1
		String gid= (String) details.get(3);
		String maleParent, femaleParent, fid, mid;
		boolean male,female;
		
		if (!is_female) {
			fid = parent2ID;
			mid = parent1ID;
			femaleParent = parent2;
			female = has_GID(parent2ID, parent2);
			maleParent = parent1;
		} else {
			fid = parent1ID;
			mid = parent2ID;
			femaleParent = parent1;
			maleParent = parent2;
			male = has_GID(parent2ID, parent2);
		}
		String temp;
		mid=mid.replaceAll("\"", "");
		fid=fid.replaceAll("\"", "");
		if (Integer.valueOf(mid) < Integer.valueOf(fid)) {
			temp = mid;
			mid = fid;
			fid = temp;
		}
		
		updateFile_createdGID(gid, parent1ID, parent1, manager);	//update the createdGID file
		parse(manager, parent1,gid, parent1ID,gpid1,gpid2);
		
		int cross_locationID=getLocationID();
		int mgid=getGID_fromFile(maleParent, mid);
		int fgid=getGID_fromFile(femaleParent, fid);
		
		Germplasm germplasm=is_crossExisting(fgid, mgid, cross_locationID, manager);
		
		if (has_GID(parent2ID, parent2)) {
			if(germplasm.getGid()==null){
				
				int methodID=selectMethodType(manager,fgid,mgid,femaleParent,maleParent);
				
				int cross_gid = (int) addGID(manager, cross,fgid,mgid, cross_locationID, methodID);

				Germplasm germplasm1 = manager.getGermplasmByGID(cross_gid);
				
				updateFile_createdGID(""+germplasm1.getGid(), fid + "/" + mid, cross, manager);
				
				updateFile_corrected(germplasm1, fid, cross, manager);
				System.out.println("\t id: "+fid + "/" + mid);
				System.out.println("\t id: "+ cross);
			}else{
				
				//Name name=manager.getGermplasmNameByID(germplasm.getGid());
				List<Name> name = new ArrayList<Name>();
					name=manager.getNamesByGID(germplasm.getGid(), 0, null);
					
				updateFile_createdGID(""+germplasm.getGid(), fid + "/" + mid, name.get(0).getNval(), manager);
				updateFile_corrected(germplasm, fid , name.get(0).getNval(), manager);
				
			}
		}
		
		
		

		factory.close();

	}

	public static void parse(GermplasmDataManager manager, String parent, String gid, String id, int gpid1, int gpid2) throws MiddlewareQueryException, IOException {
		Boolean result=false;
		
		String[] tokens = new Tokenize().tokenize(parent);
		ArrayList<String> pedigreeList = new ArrayList<String>();

		pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0] is the most recent pedigree, pedigreeList[size] is the root

		String pedigree=pedigreeList.get(0);	// first element is the parent
		pedigreeList.remove(0); // remove the pedigree already processed

		System.out.print(">> "+ pedigreeList.get(0) + "\t");

		

		//print to file
		//printSuccess(pedigree, parent,id, germplasm.get(0),manager, root, csv);

		if(pedigreeList.size()>1){

			//int gpid1= germplasm.get(0).getGpid1();	//get gpid1/root
			//int gpid2= germplasm.get(0).getGpid2();	//get gpid1/root
			
			//call function to assign GID to the pedigree line
			assignGID(manager, id, parent, gpid1, gpid2, pedigreeList);
		}else{
			updateFile_createdGID(gid, id, pedigree, manager);	//update the createdGID file
		}

		result=true;	//set flag to true

	}
	
	public static void assignGID(GermplasmDataManager manager,String id, String parent, int gpid1, int gpid2, ArrayList<String> pedigreeList) throws MiddlewareQueryException, IOException{
		Boolean error=false;
		String gid;	//set the gid to be string 
		
		for(int i=0; i<pedigreeList.size(); i++){
			String pedigree=pedigreeList.get(i);
			System.out.print(">> "+ pedigree + "\t");
			Germplasm germplasm= new Germplasm();
			
			if(error){	//if the precedent pedigree does not exist

				List<Germplasm> germplasmList = new ArrayList<Germplasm>();

				int count_LOCAL = countGermplasmByName(manager,pedigree, Database.LOCAL);
				int count_CENTRAL= countGermplasmByName(manager,pedigree,Database.CENTRAL);
				germplasmList=getGermplasmList( manager,pedigree, count_LOCAL, count_CENTRAL); //gets lists of germplasm with that name
				germplasm= getGermplasmByGpid(manager, gpid1, germplasmList);	// get the germplasm of the same gpid1, for derivative line, or gid equals to the gpid1
			}else{
				germplasm=manager.getGermplasmByGID(gpid2);
			}

			if(germplasm==null){ // this is an ERROR, the gid should exist
				error=true;
				gid="Does not exist";
				updateFile_createdGID(gid, id, pedigree, manager);	//update the createdGID file
			}else{
				gpid2=germplasm.getGpid2();
				gpid1=germplasm.getGpid1();
				gid=""+germplasm.getGid();
				error=false; //set the flag to false
				updateFile_createdGID(gid, id, pedigree, manager);	//update the createdGID file
				
			}
		}
	}

	public static void bulk_createGID()throws IOException,
	MiddlewareQueryException, ParseException {

		ManagerFactory factory = new Config().configDB();
		GermplasmDataManager manager = factory.getGermplasmDataManager();

		// file written with created GID's
		FileReader json= new FileReader("E:/xampp/htdocs/GMGR/protected/modules/checked.json");

		Object json_obj1 = JSONValue.parse(json);

		// read json file that contains the GID of the chosen GID
		JSONObject json_array1 = (JSONObject) json_obj1;
		JSONArray obj_terms = (JSONArray) json_array1.get("checked");

		int fgid = 0, mgid = 0;

		//int locationID= getLocationID(); //get locationID from json file

		for (int i = 0; i < obj_terms.size(); i++) {

			// file to be read with the standardized germplasm names
			String csvFile = "E:/xampp/htdocs/GMGR/protected/modules/corrected.csv";
			// file to be written with the created GID(s)
			String csv = "E:/xampp/htdocs/GMGR/protected/modules/createdGID3.csv";

			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] column = line.split(cvsSplitBy);

				column[3] = column[3].replaceAll("\"", "");
				column[7] = column[7].replaceAll("\"", "");
				column[5] = column[5].replaceAll("\"", "");
				column[9] = column[9].replaceAll("\"", "");
				//female
				String female_id=column[2];
				String female_remarks=column[3];
				String female_nval=column[5];
				//male
				String male_id=column[6];
				String male_remarks=column[7];
				String male_nval=column[9];
				//cross name
				String cross=column[1];
				if (female_id.equals(obj_terms.get(i)) || male_id.equals(obj_terms.get(i)) ) {
					if (female_remarks.equals("in standardized format") && female_remarks.equals("in standardized format")) {
						processParents(manager, female_nval, female_id, male_nval, male_id, "", csv);
					}
				}

			}
			br.close();
			
		}

		json.close();
		// close the database connection
		factory.close();

	}
	public static boolean processParents(GermplasmDataManager manager, String female_nval, String female_id, String male_nval, String male_id, String root, String csv) throws MiddlewareQueryException, IOException{
		boolean female=false, male=false;
		//System.out.println("\t" + column[5] + " is " + column[3]);
		if(root.equals("")){
			System.out.println("\nfemale:");
			female=parse(manager,female_nval,female_nval, female_id, csv);
			System.out.println("\nmale:");
			male=parse(manager,male_nval,male_nval, male_id, csv);
		}else{
			System.out.println("\nfemale:");
			female=parse(manager,female_nval,root, female_id,csv);
			System.out.println("\nmale:");
			male=parse(manager,male_nval,root, male_id,csv);
		}
		if(male && female){
			return true;
		}else{
			return false;
		}

		/*
				//female
				tokenize(manager, pw, column[2], column[5]);
				fgid = gpid2;
				female = flag;

				//male
				tokenize(manager, pw, column[6], column[9]);
				male = flag;
				mgid = gpid2;
				pw.write(column[2] + "/" + column[6] + ",");
				pw.write(column[5] + "/" + column[9] + ",");
				pw.write(column[1] + ",");
				System.out.println("female: "+female+ " male: "+male);
				if (female && male) {
					Germplasm g=is_crossExisting(fgid, mgid, getLocation_json(), manager);
					if(g.getGid()==null){
						int gid = 0;
						gid = (int) createGID(manager, column[1], fgid,
								mgid, getLocation_json());
						Germplasm germplasm1 = manager
						.getGermplasmByGID(gid);
						printToFile(manager, pw, germplasm1);
						//updateFile_createdGID(germplasm1,column[2]+"/"+column[6], column[1], manager);
						updateFile_corrected(germplasm1,column[2], column[1], manager);
					}else{
						Germplasm germplasm=is_crossExisting(fgid, mgid, getLocation_json(), manager);
						printToFile(manager, pw, germplasm);
						//updateFile_createdGID(germplasm,column[2]+"/"+column[6], column[1], manager);
						updateFile_corrected(germplasm,column[2], column[1], manager);
					}

				} else {
					pw.append("NOT SET" + ","); // gid
					writeFile(pw);
				}
		 */


	}

	private static boolean parse(GermplasmDataManager manager, String parent, String root, String id, String csv) throws MiddlewareQueryException, IOException {

		Boolean result=false;

		String[] tokens = new Tokenize().tokenize(parent);
		ArrayList<String> pedigreeList = new ArrayList<String>();

		pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0] is the most recent pedigree, pedigreeList[size] is the root

		int count_LOCAL = countGermplasmByName(manager,pedigreeList.get(0), Database.LOCAL);
		int count_CENTRAL= countGermplasmByName(manager,pedigreeList.get(0),Database.CENTRAL);

		int count=count_LOCAL+ count_CENTRAL;

		System.out.print(">> "+ pedigreeList.get(0) + "\t");

		String pedigree=pedigreeList.get(0);	// first element is the parent

		if(count==1){
			//single_hit
			System.out.println("Single Hit");

			// get germplasm pojo
			List<Germplasm> germplasm = new ArrayList<Germplasm>();
			if(count_LOCAL==1){
				germplasm=getGermplasm(Database.LOCAL, manager,pedigree, count_LOCAL);
			}else{
				germplasm=getGermplasm(Database.CENTRAL,manager,pedigree, count_CENTRAL);
			}

			//print to file
			printSuccess(pedigree, parent,id, germplasm.get(0),manager, root, csv);

			if(pedigreeList.size()>1){

				int gpid1= germplasm.get(0).getGpid1();	//get gpid1/root
				int gpid2= germplasm.get(0).getGpid2();	//get gpid1/root

				pedigreeList.remove(0); // remove the pedigree already processed

				//call function to search for the pedigree line
				single_Hit(manager, id, parent, gpid1, gpid2, pedigreeList,root,csv);
			}

			result=true;	//set flag to true

		}else if(count>1){
			//multiple_hit
			System.out.println("Multiple Hit");

			//print to file "CHOOSE GID" for all the pedigree line
			printChooseGID(pedigree, parent,root, id, csv);

			pedigreeList.remove(0);
			for(int i=0; i<pedigreeList.size(); i++){

				pedigree=pedigreeList.get(i);
				System.out.print(">> "+ pedigree + "\t");
				printNotSet(pedigree, parent,root, id, csv);
			}
			result=false;	//set flag to false

		}else{// count==0
			System.out.println("No hit in local and central");

			//search_file
			//print NOT SET
			System.out.println("Search Input File");
			result=search_List(manager, id, parent,parent, pedigree);
			if(result){
				printNotSet(pedigree, parent, root, id, csv);
			}else{
				//create GID
			}

		}
		return result;
	}

	public static boolean search_List(GermplasmDataManager manager, String id, String parent,String cross, String pedigree) throws IOException, MiddlewareQueryException{

		boolean result=false;
		String csvFile = "E:/xampp/htdocs/GMGR/protected/modules/corrected.csv";
		String csv = "E:/xampp/htdocs/GMGR/protected/modules/createdGID2.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		br = new BufferedReader(new FileReader(csvFile));

		System.out.println(""+pedigree);

		while ((line = br.readLine()) != null) {
			// use comma as separator
			//System.out.println(""+line);
			String[] row = line.split(cvsSplitBy);

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

				processParents(manager, female_nval_l, female_id_l, male_nval_l, male_id_l, cross, csv);

				result=true;
				break;// first to search, exit the loop 
			}
		}
		br.close();

		return result;
	}


	public static void single_Hit(GermplasmDataManager manager,String id, String parent, int gpid1, int gpid2, ArrayList<String> pedigreeList, String root, String csv) throws MiddlewareQueryException, IOException{
		Boolean error=false;

		for(int i=0; i<pedigreeList.size(); i++){
			String pedigree=pedigreeList.get(i);
			System.out.print(">> "+ pedigree + "\t");
			Germplasm germplasm= new Germplasm();
			if(error){	//if the precedent pedigree does not exist

				List<Germplasm> germplasmList = new ArrayList<Germplasm>();

				int count_LOCAL = countGermplasmByName(manager,pedigree, Database.LOCAL);
				int count_CENTRAL= countGermplasmByName(manager,pedigree,Database.CENTRAL);
				germplasmList=getGermplasmList( manager,pedigree, count_LOCAL, count_CENTRAL); //gets lists of germplasm with that name
				germplasm= getGermplasmByGpid(manager, gpid1, germplasmList);	// get the germplasm of the same gpid1, for derivative line, or gid equals to the gpid1
			}else{
				germplasm=manager.getGermplasmByGID(gpid2);
			}

			if(germplasm==null){ // this is an ERROR, the gid should exist
				error=true;
				printError(pedigree, parent, root,id,csv); //prints ERROR to file

			}else{
				gpid2=germplasm.getGpid2();
				gpid1=germplasm.getGpid1();
				error=false; //set the flag to false

				printSuccess(pedigree, parent ,id, germplasm,manager,root,csv);	//print to file
			}
		}
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
			int gpid2, int location, int methodID) throws MiddlewareQueryException {

		int gid;
		//Germplasm Object
		Germplasm germplasm1 = new Germplasm();
		germplasm1.setMethodId(methodID);
		germplasm1.setGnpgs(0);
		germplasm1.setGpid1(gpid1);
		// int setGpid2=
		germplasm1.setGpid2(gpid2);
		germplasm1.setUserId(1);
		germplasm1.setLgid(-1);
		germplasm1.setLocationId(location);
		germplasm1.setGdate(0);
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
		name1.setUserId(0);
		name1.setLocationId(location);
		name1.setNval(pedigree);
		name1.setTypeId(0);

		gid = manager.addGermplasm(germplasm1, name1);
		System.out.println("Germplasm" + gid);

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
		
		int ntype=name.getTypeId();
		if(ntype==4 || ntype==6 || ntype==13 || ntype==20 || ntype==23){
			female_fixed=true;
		}
		
		name= manager.getNameByGIDAndNval(maleGID, male_nval,GetGermplasmByNameModes.NORMAL);
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
		/*pw.write("N/A" + "," ); // gid
		pw.write(methodType + "," + methodDesc + ","); // method
		pw.write("N/A" + "," + "N/A" + ","); // location
		pw.write("N/A" + ","); // gpid1
		pw.write("N/A" + "\n"); // gpid2
		*/
		System.out.println("****METHOD="+methodType);
		//List<String> cross_method = new ArrayList<String>();
		//cross_method.add(methodType);
		return methodType;
		

	}
	
	/* GET/SEARCH FROM FILE METHODS */

	public static int getLocationID() throws FileNotFoundException, IOException,
	ParseException {
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader(
		"E:/xampp/htdocs/GMGR/protected/modules/location.json"));
		JSONObject jsonObject = (JSONObject) obj;
		return Integer.valueOf((String) jsonObject.get("locationID"));
	}
	
	public static Germplasm is_crossExisting(int fgid, int mgid, int locationID, GermplasmDataManager manager) throws MiddlewareQueryException{
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

		for(int i=0; i< germplasm.size(); i++){
			if(germplasm.get(i).getGpid1()==fgid && germplasm.get(i).getGpid2()==mgid){
				//Name name=manager.getGermplasmNameByID(germplasm.get(i).getGid());
				//System.out.println(germplasm.get(i).getGid()+" "+ name.getNval());
				return germplasm.get(i);
			}
		}
		return g;
	}
	
	private static int getGID_fromFile(String pedigree, String id) throws IOException {
		String csv = "E:/xampp/htdocs/GMGR/protected/modules/createdGID.csv";

		BufferedReader br = null;
		String line = "";

		br = new BufferedReader(new FileReader(csv));
		while ((line = br.readLine()) != null) {
			String[] cells = line.split(",");
			if (cells[0].equals(id) && cells[2].equals(pedigree)) {
				br.close();
				if(cells[3].equals("CHOOSE GID") || cells[3].equals("NOT SET")){
					return 0;
				}else{
					return Integer.valueOf(cells[3]);
				}
			}
		}
		br.close();
		int gid = 0;

		return gid;
	}
	
	public static boolean has_GID(String id, String parent) throws IOException {

		String csvFile = "E:/xampp/htdocs/GMGR/protected/modules/createdGID.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		br = new BufferedReader(new FileReader(csvFile));
		while ((line = br.readLine()) != null) {
			// use comma as separator
			String[] row = line.split(cvsSplitBy);
			if (row[0] == id && row[1] == parent) {
				if (row[3] == "NOT SET" || row[3] == "CHOOSE GID") {
					br.close();
					return false;
				}
			}
		}
		br.close();
		return true;
	}
	
	/* END GET/SEARCH FROM FILE METHODS */
	
	/*UPDATE FILES METHODS*/
	
	public static void updateFile_createdGID(String gid, String id,
			String pedigree, GermplasmDataManager manager) throws IOException,
			MiddlewareQueryException {

		/* this updates the file with created GIDs
		 * first, create a temporary file, it copies the content of the original file
		 * then  incorporate the modifications.
		 * then the original file is deleted. The temporary file will be transferred to 
		 * a newly created file with the name of the original file.
		 * 
		 * */
		
		// updated file, file to be written
		String temp = "E:/xampp/htdocs/GMGR/protected/modules/updatedCreatedGID.csv";

		// file to be updatedcret, file to be read
		String original = "E:/xampp/htdocs/GMGR/protected/modules/createdGID.csv";

		File file = new File(temp);

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		Writer bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));

		BufferedReader br = null;
		String line = "";

		br = new BufferedReader(new FileReader(original));
		while ((line = br.readLine()) != null) {
			//System.out.println("A: "+StringUtils.join(line, ","));
			String[] processedLine = processLine(line, gid, id, pedigree,
					manager);
			// System.out.println("B: "+StringUtils.join(processedLine, ","));
			bw.write(StringUtils.join(processedLine, ",")); // writing to the
			// new file with
			// updated germplasm
			bw.write("\n");
		}
		bw.close();
		br.close();

		new FileProperties().setFilePermission(temp);
		// delete createdGID.csv
		file = new File(original);
		file.delete();

		file = new File(original);
		File file2= new File(temp);
		file2.renameTo(file);
	}
	
	private static String[] processLine(String line, String gid, String id,
			String pedigree, GermplasmDataManager manager)
	throws MiddlewareQueryException, IOException {

		
		String[] cells = line.split(","); 
		cells[2] = cells[2].replaceAll("\"", "");
		
		Germplasm germplasm= manager.getGermplasmByGID(Integer.valueOf(gid));
		
		System.out.println("\t id "+ id+ " cells[0]: "+ cells[0]);
		System.out.println("\t pedigree "+ pedigree+ " cells[2]: "+
				cells[2]); System.out.println("\t here" +
						gid);


				if (cells[0].equals(id) && cells[2].equals(pedigree)) {

					Location location = manager.getLocationByID(germplasm
							.getLocationId());
					
					Method method = manager.getMethodByID(germplasm.getMethodId());

					cells[3] = germplasm.getGid().toString();
					cells[4] = germplasm.getMethodId().toString();					
					//cells[4] = ""+methodID;
					cells[5] = ""+method.getMname();
					cells[6] = germplasm.getLocationId().toString();
					cells[7] = location.getLname().toString();
					cells[8] = germplasm.getGpid1().toString();
					cells[9] = germplasm.getGpid2().toString();

					cells[3] = cells[3].replaceAll("\"", "");
					cells[4] = cells[4].replaceAll("\"", "");
					//cells[5] = cells[5].replaceAll("\"", "");
					cells[6] = cells[6].replaceAll("\"", "");
					cells[7] = cells[7].replaceAll("\"", "");
					cells[8] = cells[8].replaceAll("\"", "");
					cells[9] = cells[9].replaceAll("\"", "");
					return cells;
				}
				/*else if(cells[0].equals(id) && cells[0].contains("/")){
					cells[3] = "DUPLICATE";
					return cells;
				} 
				*/
				return cells;

	}
	
	private static String[] processLine_corrected(String line, Germplasm germplasm, String id,
			String pedigree, GermplasmDataManager manager)
	throws MiddlewareQueryException {

		String[] cells = line.split(","); 
		cells[2] = cells[2].replaceAll("\"", "");

		System.out.println("\t id "+ id+ " cells[2]: "+ cells[2]+ " GID: "+germplasm.getGid().toString());;

		//if (cells[0].equals(id) && cells[2].equals(pedigree)) {
		if (cells[2].equals(id) ) {

			cells[0] = germplasm.getGid().toString();

			cells[0] = cells[0].replaceAll("\"", "");
			return cells;
		}
		return cells;

	}
	private static void updateFile_corrected(Germplasm germplasm, String id,
			String pedigree, GermplasmDataManager manager) throws IOException,
			MiddlewareQueryException {

		// updated file, file to be written
		String temp = "E:/xampp/htdocs/GMGR/protected/modules/updatedCorrected.csv";

		// file to be updatedcret, file to be read
		String original = "E:/xampp/htdocs/GMGR/protected/modules/corrected.csv";

		File file = new File(temp);

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		Writer bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));

		BufferedReader br = null;
		String line = "";

		br = new BufferedReader(new FileReader(original));
		while ((line = br.readLine()) != null) {
			//System.out.println("A: "+StringUtils.join(line, ","));
			String[] processedLine = processLine_corrected(line, germplasm, id, pedigree,
					manager);
			//System.out.println("B: "+StringUtils.join(processedLine, ","));
			bw.write(StringUtils.join(processedLine, ",")); // writing to the
			// new file with
			// updated germplasm
			bw.write("\n");
		}
		bw.close();
		br.close();

		new FileProperties().setFilePermission(temp);
		// delete createdGID.csv
		file = new File(original);
		file.delete();

		file = new File(original);
		File file2= new File(temp);
		file2.renameTo(file);
	}
	
	/*END OF UPDATE FILES METHODS*/
	
	/*PRINT To FILE METHODS */

	public static void printSuccess(String pedigree, String parent, String id, Germplasm germplasm, GermplasmDataManager manager, String root, String csv) throws IOException, MiddlewareQueryException{

		//		String csv = "E:/xampp/htdocs/GMGR/protected/modules/createdGID.csv";

		Writer pw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csv, true), "UTF-8"));
		pw.write(id + ",");
		pw.write(root + ","); // parent
		pw.write(pedigree + ","); // pedigree

		System.out.println("gid: " + germplasm.getGid());

		Location location = manager.getLocationByID(germplasm.getLocationId());
		Method method = manager.getMethodByID(germplasm.getMethodId());

		pw.write(germplasm.getGid() + ","); // gid
		pw.write(germplasm.getMethodId() + "," + method.getMname() + ","); // method
		pw.write(germplasm.getLocationId() + "," + location.getLname() + ","); // location
		pw.write(germplasm.getGpid1() + ","); // gpid1
		pw.write(germplasm.getGpid2() + ",\n"); // gpid2

		pw.close();
		new FileProperties().setFilePermission(csv);
	}

	public static void printError(String pedigree, String parent, String root, String id, String csv) throws IOException, MiddlewareQueryException{

		//		String csv = "E:/xampp/htdocs/GMGR/protected/modules/createdGID.csv";

		Writer pw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csv, true), "UTF-8"));
		pw.write(id + ",");
		pw.write(root + ","); // parent
		pw.write(pedigree + ","); // pedigree

		System.out.println("Dooes not exist" );

		pw.write("Does not exist" + ","); // gid
		pw.write("N/A" + "," + "N/A" + ","); // method
		pw.write("N/A" + "," + "N/A" + ","); // location
		pw.write("N/A" + ","); // gpid1
		pw.write("N/A" + ",\n"); // gpid2

		pw.close();
		new FileProperties().setFilePermission(csv);
	}

	public static void printNotSet(String pedigree, String parent,String root, String id, String csv) throws IOException, MiddlewareQueryException{

		//		String csv = "E:/xampp/htdocs/GMGR/protected/modules/createdGID.csv";

		Writer pw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csv, true), "UTF-8"));
		pw.write(id + ",");
		pw.write(root + ","); // parent
		pw.write(pedigree + ","); // pedigree

		System.out.println("NOT SET" );

		pw.write("NOT SET" + ","); // gid
		pw.write("N/A" + "," + "N/A" + ","); // method
		pw.write("N/A" + "," + "N/A" + ","); // location
		pw.write("N/A" + ","); // gpid1
		pw.write("N/A" + ",\n"); // gpid2

		pw.close();
		new FileProperties().setFilePermission(csv);
	}

	public static void printChooseGID(String pedigree, String parent,String root, String id, String csv) throws IOException, MiddlewareQueryException{

		//		String csv = "E:/xampp/htdocs/GMGR/protected/modules/createdGID.csv";

		Writer pw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csv, true), "UTF-8"));
		pw.write(id + ",");
		pw.write(root + ","); // parent
		pw.write(pedigree + ","); // pedigree

		System.out.println("CHOOSE GID" );

		pw.write("CHOOSE GID" + ","); // gid
		pw.write("N/A" + "," + "N/A" + ","); // method
		pw.write("N/A" + "," + "N/A" + ","); // location
		pw.write("N/A" + ","); // gpid1
		pw.write("N/A" + ",\n"); // gpid2

		pw.close();
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
