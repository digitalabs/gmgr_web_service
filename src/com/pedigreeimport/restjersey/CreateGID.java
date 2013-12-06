package com.pedigreeimport.restjersey;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.GetGermplasmByNameModes;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import com.pedigreeimport.backend.Tokenize;

public class CreateGID {
	
	public void main(String[] args){
		String[] tokens = new Tokenize().tokenize("IR 888-9-3-4");
	}
	
	public void bulk_create()throws IOException,
	MiddlewareQueryException, ParseException {

		ManagerFactory factory = new Config().configDB();
		GermplasmDataManager manager = factory.getGermplasmDataManager();

		// file written with created GID's
		

		FileReader json= new FileReader("/var/www/GMGR/protected/modules/checked.json");

		Object json_obj1 = JSONValue.parse(json);

		// read json file that contains the GID of the chosen GID
		JSONObject json_array1 = (JSONObject) json_obj1;
		JSONArray obj_terms = (JSONArray) json_array1.get("checked");

		int fgid = 0, mgid = 0;

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

					System.out.println("\t" + column[5] + " is " + column[3]);

					if (female_remarks.equals("in standardized format") && female_remarks.equals("in standardized format")) {

						parse(manager,female_nval);

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
				}
			}
			br.close();
			i++;
		}
		
		json.close();
		// close the database connection
		factory.close();
		
	}

	private void parse(GermplasmDataManager manager, String nval) throws MiddlewareQueryException {
		String[] tokens = new Tokenize().tokenize(nval);
		ArrayList<String> pedigreeList = new ArrayList<String>();
		
		pedigreeList = saveToArray(pedigreeList, tokens);

		int count_LOCAL = countGermplasmByName_LOCAL(manager,
				pedigreeList.get(0));
		int count_CENTRAL= countGermplasmByName_CENTRAL(manager,
				pedigreeList.get(0));
		
		int count=count_LOCAL+ count_CENTRAL;
		
		System.out.print(">> "+ pedigreeList.get(0) + "\t");
		
		if(count==1){
			//single_hit
			//get gpid1/root GID
			if(pedigreeList.size()==1){
				
			}else{
				
			}
		}else if(count>1){
			//multiple_hit
		}else{// count==0
			//search_file
			//print N/A
		}

	}
	public void single_Hit(){
		
	}
	
	public ArrayList<String> saveToArray(ArrayList<String> array,
			String[] tokens) {
		
		String s = "";
		for (int i = tokens.length; i < 0; i--) {
			if (i == 0) {
				s = s + tokens[i];
			} else {
				s = s + "-" + tokens[i];
			}
			array.add(s);
		}
		Collections.reverse(array);
		return array;
	}
	public static int countGermplasmByName_LOCAL(GermplasmDataManager manager,
			String s) throws MiddlewareQueryException {

		int count = (int) manager.countGermplasmByName(s,
				GetGermplasmByNameModes.NORMAL, Operation.EQUAL, 0, null,
				Database.LOCAL);

		return count;
	}
	public static int countGermplasmByName_CENTRAL(GermplasmDataManager manager,
			String s) throws MiddlewareQueryException {

		int count = (int) manager.countGermplasmByName(s,
				GetGermplasmByNameModes.NORMAL, Operation.EQUAL, 0, null,
				Database.CENTRAL);
		return count;
	}
	
	public void printToFile(ArrayList<String> pedigreeList, int i, String pedigree, String id, Germplasm germplasm, GermplasmDataManager manager) throws IOException, MiddlewareQueryException{
		
		String csv = "E:/xampp/htdocs/GMGR/protected/modules/createdGID.csv";

		Writer pw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csv, true), "UTF-8"));
		pw.write(id + ",");
		pw.write(pedigree + ","); // parent
		pw.write(pedigreeList.get(i) + ","); // pedigree
		
		System.out.println("methodID: " + germplasm.getMethodId());

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
	
	

}
