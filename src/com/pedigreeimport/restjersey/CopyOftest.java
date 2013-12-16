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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.pedigreeimport.backend.Tokenize;

public class CopyOftest {

	/**
	 * @param args
	 * @throws ParseException 
	 * @throws MiddlewareQueryException 
	 * @throws IOException 
	 */

	static int GID;
	static String global_id;
	static int locationID;

	public static void main(String[] args) throws IOException, MiddlewareQueryException, ParseException {
		//	bulk_createGID();
		//	copy_corrected();
		//updateFile_createdGID();
		//single_createdGID();
		sortList();
	}
	
	
	public static void sortList() throws IOException{
		String csv="E:/xampp/htdocs/PedigreeImport/output.csv";
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


	public static void single_createGID() throws MiddlewareQueryException, IOException, ParseException, InterruptedException{
		ManagerFactory factory = new Config().configDB();
		GermplasmDataManager manager = factory.getGermplasmDataManager();

		//reads json file with the details of the chosen germplasm
		JSONParser parser = new JSONParser();
		FileReader json= new FileReader("E:/xampp/htdocs/PedigreeImport/term.json");
		Object obj = parser.parse(json);
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

		updateFile_createdGID(gid, parent1ID, parent1, manager, "false");	//update the createdGID file

		parse(manager, parent1,gid, parent1ID,gpid1,gpid2);	//parse the germplasm name of the chosen GID 

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

				updateFile_createdGID(""+germplasm1.getGid(), fid + "/" + mid, cross, manager,"new");

				updateFile_corrected(germplasm1, fid, cross, manager);
				System.out.println("\t id: "+fid + "/" + mid);
				System.out.println("\t id: "+ cross);
				
				germplasm1=null;
			}else{
				System.out.println(" or HERE"+ germplasm.getGid());		
				List<Name> name = new ArrayList<Name>();
				name=manager.getNamesByGID(germplasm.getGid(), 0, null);

				updateFile_createdGID(""+germplasm.getGid(), fid + "/" + mid, name.get(0).getNval(), manager,"false");
				updateFile_corrected(germplasm, fid , name.get(0).getNval(), manager);
				name=null;
			}
		}
		String csvFile = "E:/xampp/htdocs/PedigreeImport/createdGID2.csv";
		File file = new File(csvFile);
		
		if(file.exists()){
			String root_cross, female, male;
			int index=0;
			//parse(manager, parent1,gid, parent1ID,gpid1,gpid2);
			// file to be written with the created GID(s)

			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";
			int i=0;

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] column = line.split(cvsSplitBy);
				column[0] = column[0].replaceAll("\"", "");
				column[1] = column[1].replaceAll("\"", "");
				column[3] = column[3].replaceAll("\"", "");
				if(i==0){
					female=column[3];
					fgid=Integer.valueOf(root_id);
					mgid= (int) fgid + 1;
				}
				if(column[0].equals(""+fgid)){
					index=i;
				}
				if(i== index+1){
					male=column[3];
					//echo "<br>".$male_c."<br>";
				}

				if(root_id.equals(column[1])){
					//root_cross=column[1];
				}
				i++;
			}
		}
		
		 

		json.close();
		factory.close();
		
		//clear all object, free memory
		details.clear();
		jsonObject.clear();
		germplasm=null;

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
				updateFile_createdGID(gid, id, pedigree, manager,"false");	//update the createdGID file
			}else{
				gpid2=germplasm.getGpid2();
				gpid1=germplasm.getGpid1();
				gid=""+germplasm.getGid();
				error=false; //set the flag to false
				updateFile_createdGID(gid, id, pedigree, manager,"false");	//update the createdGID file

			}
		}
		
		//clearing objects
		germplasm=null;
		
	}

	public static void bulk_createGID()throws IOException,
	MiddlewareQueryException, ParseException, InterruptedException {
		System.out.println(" ###Starting..BULK CREATION of GID");

		ManagerFactory factory = new Config().configDB();
		GermplasmDataManager manager = factory.getGermplasmDataManager();

		// file written with created GID's
		FileReader json= new FileReader("E:/xampp/htdocs/PedigreeImport/checked.json");

		Object json_obj1 = JSONValue.parse(json);

		// read json file that contains the GID of the chosen GID
		JSONObject json_array1 = (JSONObject) json_obj1;
		JSONArray obj_terms = (JSONArray) json_array1.get("checked");

		//int locationID= getLocationID(); //get locationID from json file

		// file to be read with the standardized germplasm names
		String csvFile = "E:/xampp/htdocs/PedigreeImport/corrected.csv";
		// file to be written with the created GID(s)
		String csv = "E:/xampp/htdocs/PedigreeImport/createdGID.csv";

		FileOutputStream fw;
		OutputStreamWriter osw;
		Writer pw;

		locationID=getLocationID();

		for (int i = 0; i < obj_terms.size(); i++) {

			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";

			FileReader fr= new FileReader(csvFile);
			br = new BufferedReader(fr);

			while ((line = br.readLine()) != null) {
				String[] column = line.split(cvsSplitBy);
				//System.out.println(">>>>"+column.length+" ::"+column[2]);


				column[3] = column[3].replaceAll("\"", "");
				column[7] = column[7].replaceAll("\"", "");
				column[5] = column[5].replaceAll("\"", "");
				column[9] = column[9].replaceAll("\"", "");

				String female_id=column[2];
				String female_remarks=column[3];
				String female_nval=column[5];
				//male
				String male_id=column[6];
				String male_remarks=column[7];
				String male_nval=column[9];
				//cross name
				String cross=column[1];


				//System.out.println("<<<<<HERE");
				//System.out.println("female_id: "+female_id+"\t obj_terms.get(i): "+obj_terms.get(i));
				//System.out.println("male_id: "+male_id+"\t obj_terms.get(i): "+obj_terms.get(i));
				if (female_id.equals(obj_terms.get(i)) || male_id.equals(obj_terms.get(i)) ) {
					//System.out.println("female_remarks: "+female_remarks);
					//System.out.println("male_remarks: "+male_remarks);
					br.close();
					fr.close();
					if (female_remarks.equals("in standardized format") && male_remarks.equals("in standardized format")) {
						
						System.out.println("\t" + "  ***Process parents");
						String csv_createdGID2 = null;
						
						Boolean result=processParents(manager, female_nval, female_id, male_nval, male_id, "", cross,"",csv, csv_createdGID2);
						if(!result){
							printNotSet(cross, female_nval+"/"+male_nval, female_nval+"/"+male_nval, female_id + "/" + male_id,csv);
						}
						System.out.println("\t" + "  ***END Process parents");
						/*pw.flush();
						pw.close();
						osw.close();
						fw.close();
						*/
					}
					break;
				}

			}
		}
		String createdGID2 = "E:/xampp/htdocs/PedigreeImport/createdGID2.csv";

		File file = new File(createdGID2);
		File file2 = new File(csv);
		if(file.exists() && file2.exists()){
			updateFile_createdGID();	//updates createdGID.csv with rows that are in createdGID2.csv
		}
		file=null;
		file2=null;
		json.close();
		// close the database connection\
		factory.close();
		
		
		//clearing objects
		json_obj1=null;
		json_array1.clear();
		obj_terms.clear();
		new FileProperties().setFilePermission(csv);
		new FileProperties().setFilePermission(createdGID2);
		System.out.println(" ###ENDING..BULK CREATION of GID \n");

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

	private static void updateFile_createdGID() throws IOException {
		
		System.out.println(" ###STARTING..Updating createdGID.csv \n ** this is updating the createdGID.csv with germplasm created that is not yet in the csv");
		
		String csv_createdGID="E:/xampp/htdocs/PedigreeImport/createdGID.csv";
		String csv_createdGID2="E:/xampp/htdocs/PedigreeImport/createdGID2.csv";
		String csv_checked="E:/xampp/htdocs/PedigreeImport/checked.csv";

		BufferedReader br = null;
		String line = "";
		FileReader fr=new FileReader(csv_checked);
		br = new BufferedReader(fr);
		
		String array[] = null;
		while ((line = br.readLine()) != null) {
			array=line.split(",");
			System.out.println("A: "+StringUtils.join(line, ","));
		}
		br.close();
		fr.close();


		line = "";
		fr=new FileReader(csv_createdGID2);
		br = new BufferedReader(fr);
		

		FileOutputStream fw = new FileOutputStream(csv_checked, true);
		OutputStreamWriter osw = new OutputStreamWriter(fw, "UTF-8");
		BufferedWriter pw = new BufferedWriter(osw);

		FileOutputStream fw2 = new FileOutputStream(csv_createdGID, true);
		OutputStreamWriter osw2 = new OutputStreamWriter(fw2, "UTF-8");
		BufferedWriter pw_createdGID = new BufferedWriter(osw2);

		String[] cells;
		while ((line = br.readLine()) != null) {
			System.out.println(":: "+line);
			cells=line.split(",");
			Boolean flag=false;
			//int j=-1;
			String yes="";
			for(int i=0; i<array.length; i++){
				System.out.println(array[i]);
				cells[1] = cells[1].replaceAll("\"", "");

				if(!cells[1].equals(array[i]) ){
					if(!cells[1].contains("/")){
						if(Integer.valueOf(cells[1])%2==0){

							yes=cells[1];
							System.out.println("here: "+yes);
							pw.append(yes+",");

						}
					}
					//System.out.println("here: "+cells[1]);
					pw_createdGID.append(cells[1]+","+cells[2]+","+cells[3]+","+cells[4]+","+cells[5]+
							","+cells[6]+","+cells[7]+","+cells[8]+","+cells[9]+","+cells[10]+","+cells[11]+","+"\n");
				}
			}

			//System.out.println("A: "+StringUtils.join(line, ","));
		}
		br.close();
		fr.close();


		pw_createdGID.close();
		osw2.close();
		fw2.close();

		pw.close();
		osw.close();
		fw.close();
		
		//clearing memory
		
		array = null;
		cells=null;
		
		System.out.println(" ###END..Updating createdGID.csv \n");
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
		String csv = "E:/xampp/htdocs/PedigreeImport/existingTerm.csv";

		FileWriter fw = new FileWriter(csv, true);
		BufferedWriter pw = new BufferedWriter(fw);

		List<Name> name=null;
		String nval_gpid1, nval_gpid2;
		for (int i = 0; i < germplasm.size(); i++) {
			// System.out.println(germplasm.get(i).getGid());
			pw.append(id + ",");
			pw.append(root + ",");
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

			pw.append(germplasm.get(i).getGpid1() + "," + nval_gpid1 + ",");

			pw.append(germplasm.get(i).getGpid2() + "," + nval_gpid2 + ",");

			pw.append(germplasm.get(i).getGid() + ","); // gid

			String meth=method.getMname().replace(",", "#");
			pw.append(germplasm.get(i).getMethodId() + "," + meth + ","); // method
			String loc=location.getLname().replace(",", "#");
			pw.append(germplasm.get(i).getLocationId() + "," + loc + ","); // location

			pw.newLine();
			
			//clearing memory
			location=null;
			method=null;
		}

		pw.flush();
		pw.close();
		fw.close();
		
		new FileProperties().setFilePermission(csv);
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
		String csvFile = "E:/xampp/htdocs/PedigreeImport/corrected.csv";
		String csv_createdGID2 = "E:/xampp/htdocs/PedigreeImport/createdGID2.csv";
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
		germplasm1.setUserId(1);
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
		name1.setUserId(0);
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
		FileReader json= new FileReader("E:/xampp/htdocs/PedigreeImport/location.json");
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
		String csv = "E:/xampp/htdocs/PedigreeImport/createdGID.csv";

		BufferedReader br = null;
		String line = "";
		int gid = 0;

		System.out.println("**getGID_fromFile");

		FileReader fr= new FileReader(csv);
		br = new BufferedReader(fr);
		
		String[] cells;
		while ((line = br.readLine()) != null) {
			cells = line.split(",");
			cells[0] = cells[0].replaceAll("\"", "");
			cells[1] = cells[1].replaceAll("\"", "");
			cells[2] = cells[2].replaceAll("\"", "");
			cells[3] = cells[3].replaceAll("\"", "");

			System.out.println("cells[0]: "+cells[0]+" id:"+id);
			System.out.println("cells[1]: "+cells[1]+" pedigree:"+pedigree);
			System.out.println("cells[2]: "+cells[2]+" pedigree:"+pedigree);

			if (cells[0].equals(id) && ((cells[2].equals(pedigree) || cells[1].equals(pedigree)))) {

				if(cells[3].equals("CHOOSE GID") || cells[3].equals("NOT SET")){
					gid= 0;

				}else{
					gid= Integer.valueOf(cells[3]);
					break;
				}
			}
		}
		
		
		br.close();
		fr.close();

		System.out.println("get GID from file: "+gid);

		System.out.println("  ###END getGID_fromFile \n");
		
		//clearing memory
		cells=null;
		return gid;
	}

	public static boolean has_GID(String id, String parent) throws IOException {

		String csvFile = "E:/xampp/htdocs/PedigreeImport/createdGID.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		FileReader fr= new FileReader(csvFile);
		br = new BufferedReader(fr);
		
		String[] row;
		while ((line = br.readLine()) != null) {
			// use comma as separator
			row = line.split(cvsSplitBy);
			if (row[0] == id && row[1] == parent) {
				if (row[3] == "NOT SET" || row[3] == "CHOOSE GID") {
					br.close();
					return false;
				}
			}
		}
		fr.close();
		br.close();
		
		//clearing memory
		row=null;
		return true;
	}

	/* END GET/SEARCH FROM FILE METHODS */

	/*UPDATE FILES METHODS*/

	public static void updateFile_createdGID(String gid, String id,
			String pedigree, GermplasmDataManager manager,String newGID) throws IOException,
			MiddlewareQueryException, InterruptedException {
		
		System.out.println("  ###STARTING.. updating createdGID.csv");
		/* this updates the file with created GIDs
		 * first, create a temporary file, it copies the content of the original file
		 * then  incorporate the modifications.
		 * then the original file is deleted. The temporary file will be transferred to 
		 * a newly created file with the name of the original file.
		 * 
		 * */

		// updated file, file to be written
		String temp = "E:/xampp/htdocs/PedigreeImport/updatedCreatedGID.csv";

		// file to be updatedcret, file to be read
		String original = "E:/xampp/htdocs/PedigreeImport/createdGID.csv";

		File file = new File(temp);

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fw=new FileOutputStream(file, true);
		OutputStreamWriter osw=new OutputStreamWriter(fw, "UTF-8");
		Writer bw = new BufferedWriter(osw);

		BufferedReader br = null;
		String line = "";
		FileReader fr=new FileReader(original);
		br = new BufferedReader(fr);
		System.out.println("*** Starting Updating createdGID.csv");
		
		while ((line = br.readLine()) != null) {
			System.out.println("A: "+StringUtils.join(line, ","));
			
			String[] processedLine = processLine(line, gid, id, pedigree,
					manager,newGID);
			System.out.println("B: "+StringUtils.join(processedLine, ","));
			
			bw.write(StringUtils.join(processedLine, ",")); // writing to the
			// new file with
			// updated germplasm
			bw.write("\n");
		}
		
		bw.close();
		osw.close();
		fw.close();
		br.close();
		fr.close();

		new FileProperties().setFilePermission(temp);
		// delete createdGID.csv
		file = new File(original);
		file.delete();
		//Path path=file.toPath();
		//java.nio.file.Files.delete(path);
		//Thread.sleep(10000);
		file = new File(original);
		File file2= new File(temp);
		boolean y=file2.renameTo(file);
		if(y){
			System.out.println("renaming file Succesfully!");
		}else {
			System.out.println("renaming file Failed!");
		}
		
		new FileProperties().setFilePermission(original);
		
		System.out.println("*** END Updating createdGID.csv");
	}

	private static String[] processLine(String line, String gid, String id,
			String pedigree, GermplasmDataManager manager, String newGID)
	throws MiddlewareQueryException, IOException {


		String[] cells = line.split(","); 
		cells[2] = cells[2].replaceAll("\"", "");

		Germplasm germplasm= manager.getGermplasmByGID(Integer.valueOf(gid));

		//System.out.println("\t id "+ id+ " cells[0]: "+ cells[0]);
		//System.out.println("\t pedigree "+ pedigree+ " cells[2]: "+
		//		cells[2]); System.out.println("\t here" +
		//				gid);


		if (cells[0].equals(id) && cells[2].equals(pedigree)) {

			Location location = manager.getLocationByID(germplasm
					.getLocationId());

			Method method = manager.getMethodByID(germplasm.getMethodId());

			cells[3] = germplasm.getGid().toString();
			cells[4] = germplasm.getMethodId().toString();					
			//cells[4] = ""+methodID;
			cells[5] = method.getMname().toString().replaceAll(",", "#");
			cells[6] = germplasm.getLocationId().toString();
			cells[7] = location.getLname().toString().replaceAll(",", "#");
			cells[8] = germplasm.getGpid1().toString();
			cells[9] = germplasm.getGpid2().toString();
			cells[10]=newGID;

			cells[3] = cells[3].replaceAll("\"", "");
			cells[4] = cells[4].replaceAll("\"", "");
			//cells[5] = cells[5].replaceAll("\"", "");
			cells[6] = cells[6].replaceAll("\"", "");
			cells[7] = cells[7].replaceAll("\"", "");
			cells[8] = cells[8].replaceAll("\"", "");
			cells[9] = cells[9].replaceAll("\"", "");
			cells[10] = cells[10].replaceAll("\"", "");
			
//clearing memeory
			
			location=null;
			method=null;
			
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
		String temp = "E:/xampp/htdocs/PedigreeImport/updatedCorrected.csv";

		// file to be updatedcret, file to be read
		String original = "E:/xampp/htdocs/PedigreeImport/corrected.csv";

		String copy = "E:/xampp/htdocs/PedigreeImport/corrected2.csv";

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
	private static void updateFile_corrected(Germplasm germplasm, String id,
			String pedigree, GermplasmDataManager manager) throws IOException,
			MiddlewareQueryException {

		// updated file, file to be written
		String temp = "E:/xampp/htdocs/PedigreeImport/updatedCorrected.csv";

		// file to be updatedcret, file to be read
		String original = "E:/xampp/htdocs/PedigreeImport/corrected.csv";

		String copy = "E:/xampp/htdocs/PedigreeImport/corrected2.csv";

		File file = new File(temp);

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fw=new FileOutputStream(file, true);
		OutputStreamWriter osw=new OutputStreamWriter(fw, "UTF-8");
		Writer bw = new BufferedWriter(osw);


		BufferedReader br = null;
		String line_original = "";
		FileReader fr= new FileReader(original);
		br = new BufferedReader(fr);
		System.out.println("*** Starting Updating corrected.csv");

		String line="";
		File file_copy = new File(copy);

		if(file_copy.exists()){
			BufferedReader br2 = null;
			String line_copy = "";
			FileReader fr2;

			fr2= new FileReader(copy);
			br2 = new BufferedReader(fr2);


			while ((line_original = br.readLine()) != null) {	//reads coorected.csv
				System.out.println("A: "+StringUtils.join(line_original, ","));
				String[] array_original=line_original.split(",");

				line_copy = "";

				line_copy = br2.readLine();	//reads coorected2.csv

				String[] array_copy=line_copy.split(","); 
				if(array_copy[0].equals(array_original[0])){
					line=line_original;

					String[] processedLine = processLine_corrected(line, germplasm, id, pedigree,
							manager);
					System.out.println("B: "+StringUtils.join(processedLine, ","));
					bw.write(StringUtils.join(processedLine, ",")); // writing to the
					// new file with
					// updated germplasm
					bw.write("\n");
				}else{
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
			}
			fr2.close();
			br2.close();


			bw.close();
			osw.close();
			fw.close();
			br.close();
			fr.close();

			file_copy.delete();
			if(file_copy.exists()){
				System.out.println(file_copy.getName()+" NOT DELETED!");
			}else{
				System.out.println(file_copy.getName()+" is successfully DELETED!");
			}

			file = new File(original);
			file.delete();

			if(file.exists()){
				System.out.println(file.getName()+" NOT DELETED!");
			}else{
				System.out.println(file.getName()+" is successfully DELETED!");
			}

			file = new File(original);
			File file2= new File(temp);
			file2.renameTo(file);
			//java.nio.file.Files.delete(file_copy.toPath());
		}else{

			fw=new FileOutputStream(file, true);
			osw=new OutputStreamWriter(fw, "UTF-8");
			bw = new BufferedWriter(osw);


			br = null;
			line = "";
			fr= new FileReader(original);
			br = new BufferedReader(fr);
			System.out.println("*** Starting Updating corrected.csv");
			while ((line = br.readLine()) != null) {
				System.out.println("A: "+StringUtils.join(line, ","));
				String[] processedLine = processLine_corrected(line, germplasm, id, pedigree,
						manager);
				System.out.println("B: "+StringUtils.join(processedLine, ","));
				bw.write(StringUtils.join(processedLine, ",")); // writing to the
				// new file with
				// updated germplasm
				bw.write("\n");
			}


			bw.close();
			osw.close();
			fw.close();
			br.close();
			fr.close();

			new FileProperties().setFilePermission(temp);
			// delete createdGID.csv
			System.out.println("Deleting corrected.csv @ "+original);

			file = new File(original);
			file.delete();

			if(file.exists()){
				System.out.println(file.getName()+" NOT DELETED!");
			}else{
				System.out.println(file.getName()+" is successfully DELETED!");
			}

			file = new File(original);
			File file2= new File(temp);
			file2.renameTo(file);
			
			new FileProperties().setFilePermission(original);
		}

		System.out.println("*** END Updating corrected.csv");
	}


	private static void copy_corrected() throws IOException,
	MiddlewareQueryException {

		// file to be updatedcret, file to be read
		String original = "E:/xampp/htdocs/PedigreeImport/corrected.csv";
		String copy = "E:/xampp/htdocs/PedigreeImport/corrected2.csv";

		File file_copy = new File(copy);
		File file_original = new File(original);

		Files.copy(file_original.toPath(), file_copy.toPath());

		System.out.println("*** END Coppying corrected.csv to corrected2.csv");
	}

	/*END OF UPDATE FILES METHODS*/

	/*PRINT To FILE METHODS */

	public static void printSuccess(String pedigree, String parent, String id, Germplasm germplasm, GermplasmDataManager manager, String root,String csv, String newGID) throws IOException, MiddlewareQueryException{

		//		String csv = "E:/xampp/htdocs/PedigreeImport/createdGID.csv";
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

		//		String csv = "E:/xampp/htdocs/PedigreeImport/createdGID.csv";
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

		//		String csv = "E:/xampp/htdocs/PedigreeImport/createdGID.csv";

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

		//		String csv = "E:/xampp/htdocs/PedigreeImport/createdGID.csv";
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
