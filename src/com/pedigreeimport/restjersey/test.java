package com.pedigreeimport.restjersey;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
		bulk_createGID();
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
		
		int locationID= getLocationID(); //get locationID from json file

		for (int i = 0; i < (obj_terms.size());) {

			// file to be read with the standardized germplasm names
			String csvFile = "E:/xampp/htdocs/GMGR/protected/modules/corrected.csv";

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
				if (female_id.equals(obj_terms.get(i)) || male_id.equals(obj_terms.get(i)) ) {
					if (female_remarks.equals("in standardized format") && female_remarks.equals("in standardized format")) {
						processParents(manager, female_nval, female_id);
					}
				}
				
			}
			br.close();
			i++;
		}
		
		json.close();
		// close the database connection
		factory.close();
		
	}
	public static void processParents(GermplasmDataManager manager, String female_nval, String female_id) throws MiddlewareQueryException, IOException{
		
			//System.out.println("\t" + column[5] + " is " + column[3]);

				parse(manager,female_nval, female_id);

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

	private static boolean parse(GermplasmDataManager manager, String parent, String id) throws MiddlewareQueryException, IOException {
		
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
			printSuccess(pedigree, parent,id, germplasm.get(0),manager);
			
			if(pedigreeList.size()>1){
				
				int gpid1= germplasm.get(0).getGpid1();	//get gpid1/root
				int gpid2= germplasm.get(0).getGpid2();	//get gpid1/root
				
				pedigreeList.remove(0); // remove the pedigree already processed
				
				//call function to search for the pedigree line
				single_Hit(manager, id, parent, gpid1, gpid2, pedigreeList);
			}
			
			result=true;	//set flag to true
			
		}else if(count>1){
			//multiple_hit
			System.out.println("Multiple Hit");
			
			//print to file "CHOOSE GID" for all the pedigree line
			for(int i=0; i<pedigreeList.size(); i++){
				
				pedigree=pedigreeList.get(i);
				System.out.print(">> "+ pedigree + "\t");
				printChooseGID(pedigree, parent, id);
			}
			
		}else{// count==0
			System.out.println("No hit in local and central");
			
			//search_file
			//print N/A
			System.out.println("Search Input File");
			search_List(id,parent, pedigree);
			
		}
		return result;
	}
	
	public static void search_List(String id, String parent, String pedigree) throws IOException{
		String csvFile = "E:/xampp/htdocs/GMGR/protected/modules/corrected.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		br = new BufferedReader(new FileReader(csvFile));
		
		while ((line = br.readLine()) != null) {
			// use comma as separator
			System.out.println(""+line);
			String[] row = line.split(cvsSplitBy);
			
			if (row[1] == pedigree) {
				
			}
		}
		br.close();
	}
	
	
	public static void single_Hit(GermplasmDataManager manager,String id, String parent, int gpid1, int gpid2, ArrayList<String> pedigreeList) throws MiddlewareQueryException, IOException{
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
				printError(pedigree, parent, id); //prints ERROR to file
				
			}else{
				gpid2=germplasm.getGpid2();
				gpid1=germplasm.getGpid1();
				error=false; //set the flag to false
				
				printSuccess(pedigree, parent ,id, germplasm,manager);	//print to file
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
	
	public static int getLocationID() throws FileNotFoundException, IOException,
	ParseException {
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader(
		"E:/xampp/htdocs/GMGR/protected/modules/location.json"));
		JSONObject jsonObject = (JSONObject) obj;
		return Integer.valueOf((String) jsonObject.get("locationID"));
	}
	
	public static void printSuccess(String pedigree, String parent, String id, Germplasm germplasm, GermplasmDataManager manager) throws IOException, MiddlewareQueryException{
		
		String csv = "E:/xampp/htdocs/GMGR/protected/modules/createdGID.csv";

		Writer pw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csv, true), "UTF-8"));
		pw.write(id + ",");
		pw.write(parent + ","); // parent
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
	
public static void printError(String pedigree, String parent, String id) throws IOException, MiddlewareQueryException{
		
		String csv = "E:/xampp/htdocs/GMGR/protected/modules/createdGID.csv";

		Writer pw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csv, true), "UTF-8"));
		pw.write(id + ",");
		pw.write(parent + ","); // parent
		pw.write(pedigree + ","); // pedigree
		
		System.out.println("ERROR" );

		pw.write("ERROR" + ","); // gid
		pw.write("N/A" + "," + "N/A" + ","); // method
		pw.write("N/A" + "," + "N/A" + ","); // location
		pw.write("N/A" + ","); // gpid1
		pw.write("N/A" + ",\n"); // gpid2
		
		pw.close();
		new FileProperties().setFilePermission(csv);
	}

public static void printChooseGID(String pedigree, String parent, String id) throws IOException, MiddlewareQueryException{
	
	String csv = "E:/xampp/htdocs/GMGR/protected/modules/createdGID.csv";

	Writer pw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csv, true), "UTF-8"));
	pw.write(id + ",");
	pw.write(parent + ","); // parent
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
	
	public int addGID(GermplasmDataManager manager, String pedigree, int gpid1,
			int gpid2, int location) throws MiddlewareQueryException {

		int gid;
		//Germplasm Object
		Germplasm germplasm1 = new Germplasm();
		germplasm1.setMethodId(33);
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
