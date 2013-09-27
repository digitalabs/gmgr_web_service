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
import java.io.Writer;
import java.util.ArrayList;
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

public class AssignGID {
	static int gpid1;
	static int gpid2;

	static boolean male, flag = true;
	static boolean female = male = true;


	public Germplasm is_crossExisting(int fgid, int mgid, int locationID, GermplasmDataManager manager) throws MiddlewareQueryException{
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

	private int getLocation_json() throws FileNotFoundException, IOException,
	ParseException {
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader(
		"E:/xampp/htdocs/GMGR/protected/modules/location.json"));
		JSONObject jsonObject = (JSONObject) obj;
		return Integer.valueOf((String) jsonObject.get("locationID"));
	}

	public boolean has_GID(String id, String parent) throws IOException {

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

	private String[] processLine(String line, Germplasm germplasm, String id,
			String pedigree, GermplasmDataManager manager)
	throws MiddlewareQueryException, IOException {

		
		String[] cells = line.split(","); 
		cells[2] = cells[2].replaceAll("\"", "");

		System.out.println("\t id "+ id+ " cells[0]: "+ cells[0]);
		System.out.println("\t pedigree "+ pedigree+ " cells[2]: "+
				cells[2]); System.out.println("\t here" +
						germplasm.getGid().toString());


				if (cells[0].equals(id) && cells[2].equals(pedigree)) {

					Location location = manager.getLocationByID(germplasm
							.getLocationId());
					Name f=manager.getGermplasmNameByID(germplasm.getGpid1());
					Name m=manager.getGermplasmNameByID(germplasm.getGpid2());
					int methodID=0;
					if (germplasm.getGpid2() == 0||germplasm.getGpid1() == 0) {
						methodID=33;
					}else{
						methodID=selectMethodType(manager,germplasm.getGpid1(),germplasm.getGpid2(),f.getNval(),m.getNval());
					}
					
					Method method = manager.getMethodByID(methodID);

					cells[3] = germplasm.getGid().toString();
					//cells[4] = germplasm.getMethodId().toString();
					//cells[5] = method.getMname().toString();
					cells[4] = ""+methodID;
					cells[5] = method.getMname();
					cells[6] = germplasm.getLocationId().toString();
					cells[7] = location.getLname().toString();
					cells[8] = germplasm.getGpid1().toString();
					cells[9] = germplasm.getGpid2().toString();

					cells[3] = cells[3].replaceAll("\"", "");
					cells[4] = cells[4].replaceAll("\"", "");
					cells[5] = cells[5].replaceAll("\"", "");
					cells[6] = cells[6].replaceAll("\"", "");
					cells[7] = cells[7].replaceAll("\"", "");
					cells[8] = cells[8].replaceAll("\"", "");
					cells[9] = cells[9].replaceAll("\"", "");
					return cells;
				}
				else if(cells[0].equals(id) && cells[0].contains("/")){
					cells[3] = "DUPLICATE";
					return cells;
				} 
				return cells;

	}
	private String[] processLine_corrected(String line, Germplasm germplasm, String id,
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
	private void updateFile_corrected(Germplasm germplasm, String id,
			String pedigree, GermplasmDataManager manager) throws IOException,
			MiddlewareQueryException {

		// updated file, file to be written
		String updatedFile = "E:/xampp/htdocs/GMGR/protected/modules/updatedCorrected.csv";

		// file to be updatedcret, file to be read
		String createdFile = "E:/xampp/htdocs/GMGR/protected/modules/corrected.csv";

		File file = new File(updatedFile);

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		Writer bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));

		BufferedReader br = null;
		String line = "";

		br = new BufferedReader(new FileReader(createdFile));
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

		new FileProperties().setFilePermission(updatedFile);
		// delete createdGID.csv
		file = new File(createdFile);
		file.delete();

		file = new File(createdFile);
		bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));

		br = null;
		line = "";

		br = new BufferedReader(new FileReader(updatedFile));
		while ((line = br.readLine()) != null) {
			bw.write(line); // writing to the new file with updated germplasm
			bw.write("\n");
		}
		bw.close();
		br.close();
		new FileProperties().setFilePermission(createdFile);
	}

	private void updateFile_createdGID(Germplasm germplasm, String id,
			String pedigree, GermplasmDataManager manager) throws IOException,
			MiddlewareQueryException {

		// updated file, file to be written
		String updatedFile = "E:/xampp/htdocs/GMGR/protected/modules/updatedCreatedGID.csv";

		// file to be updatedcret, file to be read
		String createdFile = "E:/xampp/htdocs/GMGR/protected/modules/createdGID.csv";

		File file = new File(updatedFile);

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		Writer bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));

		BufferedReader br = null;
		String line = "";

		br = new BufferedReader(new FileReader(createdFile));
		while ((line = br.readLine()) != null) {
			//System.out.println("A: "+StringUtils.join(line, ","));
			String[] processedLine = processLine(line, germplasm, id, pedigree,
					manager);
			// System.out.println("B: "+StringUtils.join(processedLine, ","));
			bw.write(StringUtils.join(processedLine, ",")); // writing to the
			// new file with
			// updated germplasm
			bw.write("\n");
		}
		bw.close();
		br.close();

		new FileProperties().setFilePermission(updatedFile);
		// delete createdGID.csv
		file = new File(createdFile);
		file.delete();

		file = new File(createdFile);
		bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));

		br = null;
		line = "";

		br = new BufferedReader(new FileReader(updatedFile));
		while ((line = br.readLine()) != null) {
			bw.write(line); // writing to the new file with updated germplasm
			bw.write("\n");
		}
		bw.close();
		br.close();
		new FileProperties().setFilePermission(createdFile);
	}

	public void chooseGID() throws IOException, ParseException,
	MiddlewareQueryException {
		ManagerFactory factory = new Config().configDB();
		GermplasmDataManager manager = factory.getGermplasmDataManager();
		// reads json file that contains the details of the chosen GID
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

		String maleParent, femaleParent, fid, mid;

		Boolean flag = false;

		String[] tokens = new Tokenize().tokenize(parent1);
		ArrayList<String> pedigreeList = new ArrayList<String>();
		String s = "";
		int index = 0;
		System.out.println("token length " + tokens.length);
		if(tokens.length==1){
			Germplasm germplasm = manager.getGermplasmByGID(Integer.valueOf((String) details.get(3)));
			updateFile_createdGID(germplasm, (String) details.get(0),
					term, manager);

		}else{
			for (int i = 0; i < tokens.length; i++) {
				if (i == 0) {
					s = s + tokens[i];
				} else {
					s = s + "-" + tokens[i];
				}
				if (s.equals(term)) {
					System.out.println("HERE at index: " + i);
					index = i;
				}
				pedigreeList.add(s);
			}

			System.out.println("term :" + term + " parent1: " + parent1 + " ID: "
					+ parent1ID);
			if (index > 0) {
				int count_LOCAL = countGermplasmByName_LOCAL(manager,
						pedigreeList.get(index - 1));
				int count_CENTRAL= countGermplasmByName_CENTRAL(manager,
						pedigreeList.get(index - 1));
				int count=count_LOCAL+ count_CENTRAL;
	

				System.out.print("pedigree: " + pedigreeList.get(index - 1) + "\t");

				if (count > 0) {
					System.out.print("\t count>1 ");

					List<Germplasm> germplasm = getGermplasmPOJOByName(manager,
							pedigreeList.get(index - 1), count_LOCAL,count_CENTRAL);

					for (int j = 0; j < germplasm.size(); j++) {
						if (germplasm.get(j).getLocationId() == locationID
								&& (gpid1 == germplasm.get(j).getGpid1() || gpid1 == germplasm
										.get(j).getGid())) {
							System.out.print("chosen gid: "
									+ germplasm.get(j).getGid());
							System.out.println("\t location ID: "
									+ germplasm.get(j).getLocationId() + " gpid1: "
									+ germplasm.get(j).getGpid1() + " gpid2: "
									+ germplasm.get(j).getGpid2());
							gpid2 = germplasm.get(j).getGid();
							System.out.println("gpid2: " + gpid2);

							updateFile_createdGID(germplasm.get(j), parent1ID,
									pedigreeList.get(index - 1), manager);
							break;
						}
					}
					// printToFile(manager, pw, germplasm.get(0));
					flag = assignGID_i(manager, pedigreeList, gpid1, locationID,
							index - 2, parent1ID, term);

					// assign GID's to the pedigree line

				} else {
					System.out.print("\t count==0 ");
					System.out.println("NOT SET");
					/*
					 * create GID using the location chose in uploading the file
					 * then check if there previous derivative already exist in that
					 * location if does not, create, if existing, use that GID *
					 */
					flag = pedigreeNotExisting(manager, pedigreeList, index - 2,
							parent1ID, term);
				}
			}
		}

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
		if (has_GID(parent2ID, parent2) && flag) {
			if(is_crossExisting(getGID_fromFile(femaleParent, fid), getGID_fromFile(maleParent, mid), getLocation_json(), manager).getGid()==null){
				int gid = (int) createGID(manager, cross,
						getGID_fromFile(femaleParent, fid),
						getGID_fromFile(maleParent, mid), getLocation_json());

				Germplasm germplasm1 = manager.getGermplasmByGID(gid);
				updateFile_createdGID(germplasm1, fid + "/" + mid, cross, manager);
				updateFile_corrected(germplasm1, fid + "/" + mid, cross, manager);
				System.out.println("\t id: "+fid + "/" + mid);
				System.out.println("\t id: "+ cross);
			}else{
				Germplasm germplasm=is_crossExisting(getGID_fromFile(femaleParent, fid), getGID_fromFile(maleParent, mid), getLocation_json(), manager);
				//Name name=manager.getGermplasmNameByID(germplasm.getGid());
				List<Name> name = new ArrayList<Name>();
					name=manager.getNamesByGID(germplasm.getGid(), 0, null);
					
				updateFile_createdGID(germplasm, fid + "/" + mid, name.get(0).getNval(), manager);
				updateFile_corrected(germplasm, fid + "/" + mid, name.get(0).getNval(), manager);
				System.out.println("\t **id: "+fid + "/" + mid);
				System.out.println("\t **id: "+ name.get(0).getNval());
			}
		}
		factory.close();
	}

	private int getGID_fromFile(String pedigree, String id) throws IOException {
		String csv = "E:/xampp/htdocs/GMGR/protected/modules/createdGID.csv";

		BufferedReader br = null;
		String line = "";

		br = new BufferedReader(new FileReader(csv));
		while ((line = br.readLine()) != null) {
			String[] cells = line.split(",");
			if (cells[0].equals(id) && cells[2].equals(pedigree)) {
				br.close();
				if(cells[3].equals("CHOOSE GID")){
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

	private Boolean pedigreeNotExisting(GermplasmDataManager manager,
			ArrayList<String> pedigreeList, int index, String id,
			String pedigree) throws MiddlewareQueryException, IOException {

		boolean flag = false;
		for (int i = pedigreeList.size() - 2; i >= 0; i--) {
			if (i == pedigreeList.size() - 1) {

				System.out.println("create GID");
				assignGID_fromRoot(manager, pedigreeList, 33, id, pedigree);
				flag = true;

			} else {
				flag = false;
			}
		}
		return flag;
	}

	private void assignGID_fromRoot(GermplasmDataManager manager,
			ArrayList<String> pedigreeList, int locationID, String id,
			String pedigree) throws MiddlewareQueryException, IOException {

		int gpid2 = 0, gpid1 = 0, gid;
		ArrayList<Integer> pedigreeList_GID = new ArrayList<Integer>();

		for (int i = 0; i < pedigreeList.size(); i++) {

			gid = (int) createGID(manager, pedigreeList.get(i), gpid1, gpid2,
					locationID);

			if (i == 0) {
				gpid2 = gid;
				gpid1 = gid;
			} else {
				gpid2 = gid;
			}
			pedigreeList_GID.add(gid);
			System.out.println(pedigreeList.get(i) + " gpid1: " + gpid1
					+ " gpid2: " + gpid2);
		}

		for (int i = pedigreeList_GID.size() - 1; i >= 0; i--) {

			Germplasm germplasm = manager.getGermplasmByGID(pedigreeList_GID
					.get(i));
			System.out.print("pedigree: " + pedigreeList.get(i));
			System.out.println("\t gid: " + germplasm.getGid() + " gpid1: "
					+ germplasm.getGpid1() + " gpid2: " + germplasm.getGpid2());

		}

	}

	// with updatedFile_createdGID
	private Boolean assignGID_i(GermplasmDataManager manager,
			ArrayList<String> pedigreeList, Integer gpid1, int locationID,
			int index, String id, String term) throws MiddlewareQueryException,
			IOException {

		Boolean flag = false;
		// Assign GID from ith index, forward search from root to most recent
		// pedigree
		for (int i = index; i >= 0; i--){
			int count_LOCAL = countGermplasmByName_LOCAL(manager,
					pedigreeList.get(i));
			int count_CENTRAL= countGermplasmByName_CENTRAL(manager,
					pedigreeList.get(i));
			int count=count_LOCAL+ count_CENTRAL;

			System.out.println("\npedigree: " + pedigreeList.get(i));
			System.out.println("number of rows: " + count);
			getGermplasmByName(manager, pedigreeList.get(i), count_LOCAL, count_CENTRAL, id);

			if (count > 0) {
				List<Germplasm> germplasm = getGermplasmPOJOByName(manager,
						pedigreeList.get(i), count_LOCAL, count_CENTRAL);
				System.out.println("gpid1: " + gpid1);
				
				Germplasm germplasm1 = getGermplasm_among_existing(manager,
						germplasm, pedigreeList.get(i).toString(), gpid1,
						locationID);

				if (germplasm1.getGid() == null) {
					System.out.println("\t " + pedigreeList.get(i)
							+ " is unknown");
					System.out.println("germplasm1.getGid() == null");
					flag = false;

				} else {
					System.out.print("\t gpid1: " + germplasm1.getGpid1());
					System.out.print("\t gpid2: " + germplasm1.getGpid2());
					System.out.println("\t location: "
							+ germplasm1.getLocationId());
					updateFile_createdGID(germplasm1, id, pedigreeList.get(i),
							manager);
					flag = true;
				}
			} else {
				System.out.println("\t" + pedigreeList.get(i) + " is unknown");

				flag = false;
			}
		}
		return flag;
	}

	public void createGID() throws FileNotFoundException, IOException,
	MiddlewareQueryException, ParseException {

		ManagerFactory factory = new Config().configDB();
		GermplasmDataManager manager = factory.getGermplasmDataManager();

		// file written with created GID's
		String csv = "E:/xampp/htdocs/GMGR/protected/modules/createdGID.csv";

		Writer pw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(csv,true), "UTF-8"));

		Object json_obj1 = JSONValue.parse(new FileReader(
		"E:/xampp/htdocs/GMGR/protected/modules/checked.json"));

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

				if (column[2].equals(obj_terms.get(i)) || column[6].equals(obj_terms.get(i)) ) {
					System.out.println("\t" + column[5] + " is " + column[3]);
					if (column[3].equals("in standardized format") && column[7].equals("in standardized format")) {

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

					}
				}
			}
			br.close();
			i++;
		}
		pw.close();
		// close the database connection
		factory.close();
		new FileProperties().setFilePermission(csv);
	}

	public void tokenize(GermplasmDataManager manager, Writer pw,
			String id, String pedigree) throws IOException,
			MiddlewareQueryException, ParseException {

		// String pedigree="IR 88888-UBN 3-4";

		String[] tokens = new Tokenize().tokenize(pedigree);
		ArrayList<String> pedigreeList = new ArrayList<String>();

		pedigreeList = saveToArray(pedigreeList, tokens);


		int count_LOCAL = countGermplasmByName_LOCAL(manager,
				pedigreeList.get(pedigreeList.size() - 1));
		int count_CENTRAL= countGermplasmByName_CENTRAL(manager,
				pedigreeList.get(pedigreeList.size() - 1));
		int count=count_LOCAL+ count_CENTRAL;
		System.out.print("pedigree: "
				+ pedigreeList.get(pedigreeList.size() - 1) + "\t");

		if (count == 1) {
			pw.write(id + ",");
			pw.write(pedigree + ","); // pedigree
			pw.write(pedigreeList.get(pedigreeList.size() - 1) + ","); // pedigree
			// name
			System.out.print("\t count==1 ");

			List<Germplasm> germplasm = getGermplasmPOJOByName(manager,
					pedigreeList.get(pedigreeList.size() - 1), count_LOCAL, count_CENTRAL);

			System.out.print("gid: " + germplasm.get(0).getGid());
			System.out.println("\t location ID: "
					+ germplasm.get(0).getLocationId() + " gpid1: "
					+ germplasm.get(0).getGpid1() + " gpid2: "
					+ germplasm.get(0).getGpid2());
			gpid2 = germplasm.get(0).getGid();
			System.out.println("gpid2: " + gpid2);

			printToFile(manager, pw, germplasm.get(0));
			if(pedigreeList.size()==1){
				flag=true;
			}else{
				// assign GID's to the pedigree line
				flag = assignGID(pw, manager, pedigreeList, germplasm.get(0)
						.getGpid1(), germplasm.get(0).getLocationId(),
						pedigreeList.size() - 1, id, pedigree);
			}
		} else if (count > 1) {
			// multiple=true;
			pw.write(id + ",");
			pw.write(pedigree + ","); // pedigree
			pw.write(pedigreeList.get(pedigreeList.size() - 1) + ","); // pedigree
			// name
			System.out.print("\t count>1 ");
			flag = false; // false, a flag to catch the pedigree with
			// existing GID's that sets the pedigree line
			System.out.println("choose GID");
			pw.write("CHOOSE GID" + ",");
			writeFile(pw);
			getGermplasmByName(manager,
					pedigreeList.get(pedigreeList.size() - 1), count_LOCAL, count_CENTRAL, id);

			multiplePedigree(pw, manager, pedigreeList,
					pedigreeList.size() - 1, id, pedigree);

		} else if (count == 0) {

			System.out.print("\t count==0 ");

			//System.out.println("NOT SET");

			//pw.write("NOT SET" + ",");
			//writeFile(pw);
			//flag = pedigreeNotExisting(pw, manager, pedigreeList,
			//		pedigreeList.size() - 1, id, pedigree);
			/*
			 * create GID using the location chosen in uploading the file then
			 * check if there previous derivative already exist in that location
			 * if does not, create, if existing and is multiple, set CHOOSE GID
			 * if existing and only 1 instance, use that GID *
			 */
			fromRoot2(pw, manager, pedigreeList, getLocation_json(), id, pedigree);
			flag=true;
			/*
			 assignGID( Writer pw, GermplasmDataManager manager,
			ArrayList<String> pedigreeList, int gpid1, int locationID,
			int index, String id, String pedigree)
			 */
		}

	}
	public void fromRoot2(Writer pw,GermplasmDataManager manager,ArrayList<String> pedigreeList,int locationID,String id, String pedigree) throws MiddlewareQueryException, IOException, ParseException{

		int count_LOCAL = countGermplasmByName_LOCAL(manager,
				pedigreeList.get(pedigreeList.size() - 1));
		int count_CENTRAL= countGermplasmByName_CENTRAL(manager,
				pedigreeList.get(pedigreeList.size() - 1));
		int count=count_LOCAL+ count_CENTRAL;

		if(count==0){
			int x = 0;
			for (int i = pedigreeList.size() - 1; i >= 0; i--) {
				count_LOCAL = countGermplasmByName_LOCAL(manager,
						pedigreeList.get(i));
				count_CENTRAL= countGermplasmByName_CENTRAL(manager,
						pedigreeList.get(i));
				count=count_LOCAL+ count_CENTRAL;

				if(count>0){
					break;
				}
				x=i;
			}
			System.out.println("##pedigree: "+pedigreeList.get(x));
			for (int i = x-1; i >= 0; i--) {
				System.out.println("#####pedigree: "+pedigreeList.get(i));
				count_LOCAL = countGermplasmByName_LOCAL(manager,
						pedigreeList.get(i));
				count_CENTRAL= countGermplasmByName_CENTRAL(manager,
						pedigreeList.get(i));
				count=count_LOCAL+ count_CENTRAL;
				if(count==1){
					List <Germplasm> germplasm = getGermplasmPOJOByName(manager,
							pedigreeList.get(pedigreeList.size() - 1), count_LOCAL, count_CENTRAL);
					gpid2=germplasm.get(0).getGid();
					gpid1=germplasm.get(0).getGpid1();
					break;
				}
			}
			ArrayList<Integer> list= new ArrayList<Integer>();
			for (int i = x; i <pedigreeList.size(); i++) {
				System.out.println("**["+i+"]: "+pedigreeList.get(i));
				int gid=createGID(manager, pedigreeList.get(i), gpid1, gpid2, getLocation_json());
				list.add(gid);

				gpid2=gid;
			}
			for (int i=pedigreeList.size()-1,j=list.size()-1; i>=x ; i--){
				pw.write(id + ",");
				pw.write(pedigree + ","); // pedigree name
				pw.write(pedigreeList.get(i) + ","); // pedigree name
				System.out.println("[1]"+pedigreeList.get(i) + ", "+ list.get(j)+ ","); // pedigree name

				Germplasm germplasm=manager.getGermplasmByGID(list.get(j));
				j++;
				printToFile(manager, pw, germplasm);

			}
			for (int i = x-1; i >= 0; i--) {
				count_LOCAL = countGermplasmByName_LOCAL(manager,
						pedigreeList.get(i));
				count_CENTRAL= countGermplasmByName_CENTRAL(manager,
						pedigreeList.get(i));
				count=count_LOCAL+ count_CENTRAL;
				//if(count==1){
				List <Germplasm> germplasm = getGermplasmPOJOByName(manager,
						pedigreeList.get(i), count_LOCAL, count_CENTRAL);
				gpid2=germplasm.get(0).getGid();
				gpid1=germplasm.get(0).getGpid1();
				pw.write(id + ",");
				pw.write(pedigree + ","); // pedigree name
				pw.write(pedigreeList.get(i) + ","); // pedigree name
				System.out.println("[2]"+pedigreeList.get(i) + ", "+ germplasm.get(0).getGid()+ ","); // pedigree name
				printToFile(manager, pw, germplasm.get(0));
				//	}
			}

		}
	}
	public void fromRoot(Writer pw,GermplasmDataManager manager,ArrayList<String> pedigreeList,int locationID,String id, String pedigree) throws MiddlewareQueryException, IOException{
		boolean loop=true;
		for(int i=0; i<pedigreeList.size(); i++){
			System.out.println("["+i+"]: "+pedigreeList.get(i));

			pw.write(id + ",");
			pw.write(pedigree + ","); // pedigree name
			pw.write(pedigreeList.get(i) + ","); // pedigree name

			int count_LOCAL = countGermplasmByName_LOCAL(manager,
					pedigreeList.get(i));
			int count_CENTRAL= countGermplasmByName_CENTRAL(manager,
					pedigreeList.get(i));
			int count=count_LOCAL+ count_CENTRAL;

			if(loop){
				if(count==1){
					List<Germplasm> germplasm = getGermplasmPOJOByName(manager,
							pedigreeList.get(pedigreeList.size() - 1), count_LOCAL, count_CENTRAL);

					System.out.print("gid: " + germplasm.get(0).getGid());
					System.out.println("\t location ID: "
							+ germplasm.get(0).getLocationId() + " gpid1: "
							+ germplasm.get(0).getGpid1() + " gpid2: "
							+ germplasm.get(0).getGpid2());
					gpid2 = germplasm.get(0).getGid();
					if (i == 0) {
						gpid2 = germplasm.get(0).getGid();
						gpid1 = germplasm.get(0).getGid();
					} else {
						gpid2 = germplasm.get(0).getGid();
					}
					System.out.println("gpid2: " + gpid2);

					pw.write(germplasm.get(0).getGid() + ",");
					printToFile(manager, pw, germplasm.get(0));
					loop=true;
				}else if(count==0){
					if(i==0){

						assignGID_fromRoot(pw, manager, pedigreeList, locationID, id, pedigree);
						break;
					}else{
						int gid=createGID(manager, pedigreeList.get(i), gpid2, gpid2, locationID);
						Germplasm germplasm = manager.getGermplasmByGID(gid);

						printToFile(manager, pw, germplasm);
						gpid2 = germplasm.getGid();					
					}

				}else{					
					System.out.println("herrre");
					pw.write("CHOOSE GID" + ","); // pedigree name
					writeFile(pw);
					loop=false;
				}
			}else{
				pw.write("NOT SET" + ","); // pedigree name
				writeFile(pw);
			}

		}

	}


	public ArrayList<String> saveToArray(ArrayList<String> pedigreeList,
			String[] tokens) {
		String s = "";
		for (int i = 0; i < tokens.length; i++) {
			if (i == 0) {
				s = s + tokens[i];
			} else {
				s = s + "-" + tokens[i];
			}
			pedigreeList.add(s);
		}
		return pedigreeList;
	}

	public void printToFile(GermplasmDataManager manager, Writer pw,
			Germplasm germplasm) throws MiddlewareQueryException, IOException {
		System.out.println("methodID: " + germplasm.getMethodId());

		Location location = manager.getLocationByID(germplasm.getLocationId());
		Method method = manager.getMethodByID(germplasm.getMethodId());

		pw.write(germplasm.getGid() + ","); // gid
		pw.write(germplasm.getMethodId() + "," + method.getMname() + ","); // method
		pw.write(germplasm.getLocationId() + "," + location.getLname() + ","); // location
		pw.write(germplasm.getGpid1() /*  */
				+ ","); // gpid1
		pw.write(germplasm.getGpid2() /*  */
				+ ",\n"); // gpid2

	}

	public void writeFile(Writer pw) throws IOException {
		pw.write("N/A" + "," + "N/A" + ","); // method
		pw.write("N/A" + "," + "N/A" + ","); // location
		pw.write("N/A" + ","); // gpid1
		pw.write("N/A" + "\n"); // gpid2
	}

	public boolean pedigreeNotExisting( Writer pw,
			GermplasmDataManager manager, ArrayList<String> pedigreeList,
			int index, String id, String pedigree) throws IOException,
			MiddlewareQueryException, ParseException {
		boolean flag = false;
		for (int i = pedigreeList.size() - 2; i >= 0; i--) {
			if (i == pedigreeList.size() - 1) {

				System.out.println("create GID");
				assignGID_fromRoot(pw, manager, pedigreeList, getLocation_json(), id, pedigree);
				flag = true;

			} else {
				pw.write(id + ",");
				pw.write(pedigree + ","); // pedigree name
				pw.write(pedigreeList.get(i) + ","); // pedigree name
				pw.write("NOT SET" + ","); // pedigree name
				writeFile(pw);
				flag = false;
			}
		}
		return flag;

	}

	public void multiplePedigree( Writer pw,
			GermplasmDataManager manager, ArrayList<String> pedigreeList,
			int index, String id, String pedigree) throws IOException {

		for (int i = pedigreeList.size() - 2; i >= 0; i--) {
			pw.write(id + ",");
			pw.write(pedigree + ","); // pedigree name
			pw.write(pedigreeList.get(i) + ","); // pedigree name
			pw.write("NOT SET" + ","); // pedigree name
			writeFile(pw);
		}
	}

	public void assignGID_fromRoot(Writer pw,
			GermplasmDataManager manager, ArrayList<String> pedigreeList,
			int locationID, String id, String pedigree)
	throws MiddlewareQueryException, IOException {
		int gpid2 = 0, gpid1 = 0, gid;
		ArrayList<Integer> pedigreeList_GID = new ArrayList<Integer>();

		for (int i = 0; i < pedigreeList.size(); i++) {

			gid = (int) createGID(manager, pedigreeList.get(i), gpid1, gpid2,
					locationID);

			if (i == 0) {
				gpid2 = gid;
				gpid1 = gid;
			} else {
				gpid2 = gid;
			}
			pedigreeList_GID.add(gid);
			System.out.println(pedigreeList.get(i) + " gpid1: " + gpid1
					+ " gpid2: " + gpid2);
		}

		for (int i = pedigreeList_GID.size() - 1; i >= 0; i--) {

			Germplasm germplasm = manager.getGermplasmByGID(pedigreeList_GID
					.get(i));
			System.out.print("pedigree: " + pedigreeList.get(i));
			System.out.println("\t gid: " + germplasm.getGid() + " gpid1: "
					+ germplasm.getGpid1() + " gpid2: " + germplasm.getGpid2());

			pw.write(id + ",");
			pw.write(pedigree + ","); // pedigree name
			pw.write(pedigreeList.get(i) + ","); // pedigree name
			printToFile(manager, pw, germplasm);
		}

	}

	public boolean assignGID( Writer pw, GermplasmDataManager manager,
			ArrayList<String> pedigreeList, int gpid1, int locationID,
			int index, String id, String pedigree)
	throws MiddlewareQueryException, IOException {

		Boolean flag = false;

		// Assign GID from ith index, forward search from root to most recent
		// pedigree
		for (int i = index - 1; i >= 0; i--) {
			int count_LOCAL = countGermplasmByName_LOCAL(manager,
					pedigreeList.get(i));
			int count_CENTRAL= countGermplasmByName_CENTRAL(manager,
					pedigreeList.get(i));
			int count=count_LOCAL+ count_CENTRAL;

			System.out.println("\npedigree: " + pedigreeList.get(i));
			System.out.println("number of rows: " + count);
			getGermplasmByName(manager, pedigreeList.get(i), count_LOCAL, count_CENTRAL, id);

			pw.write(id + ",");
			pw.write(pedigree + ","); // pedigree name
			pw.write(pedigreeList.get(i) + ","); // pedigree name

			if (count > 0) {
				List<Germplasm> germplasm = getGermplasmPOJOByName(manager,
						pedigreeList.get(i), count_LOCAL, count_CENTRAL);
				Germplasm germplasm1 = getGermplasm_among_existing(manager,
						germplasm, pedigreeList.get(i).toString(), gpid1,
						locationID);

				if (germplasm1.getGid() == null) {
					System.out.println("\t " + pedigreeList.get(i)
							+ " is unknown");

					pw.write("NOT SET" + ",");
					writeFile(pw);
					flag = false;

				} else {
					// pedigreeList_GID.add(germplasm1.getGid());
					System.out.print("\t gpid1: " + germplasm1.getGpid1());
					System.out.print("\t gpid2: " + germplasm1.getGpid2());
					System.out.println("\t location: "
							+ germplasm1.getLocationId());
					printToFile(manager, pw, germplasm1);
					flag = true;
				}
			} else {
				System.out.println("\t" + pedigreeList.get(i) + " is unknown");

				pw.write("NOT SET" + ",");
				writeFile(pw);
				flag = false;
			}
		}

		// Assign GID from ith index, backtrack from most recent pedigree to
		// root
		for (int i = index + 1; i < pedigreeList.size(); i++) {
			System.out.print("\npedigree: " + pedigreeList.get(i));
			int count_LOCAL = countGermplasmByName_LOCAL(manager,
					pedigreeList.get(i));
			int count_CENTRAL= countGermplasmByName_CENTRAL(manager,
					pedigreeList.get(i));
			int count=count_LOCAL+ count_CENTRAL;

			pw.write(id + ",");
			pw.write(pedigree + ","); // pedigree name
			pw.write(pedigreeList.get(i) + ","); // pedigree name
			if (count > 0) {
				List<Germplasm> germplasm = getGermplasmPOJOByName(manager,
						pedigreeList.get(i), count_LOCAL, count_CENTRAL);
				Germplasm germplasm1 = getGermplasm_among_existing(manager,
						germplasm, pedigreeList.get(i).toString(), gpid1,
						locationID);
				System.out.println("from pedigree: " + pedigreeList.get(i)
						+ " gpid1: " + germplasm1.getGpid1() + " gpid2: "
						+ germplasm1.getGpid2() + " location: "
						+ germplasm.get(0).getLocationId());
				flag = true;
			} else {
				// create GID for the pedigree with gpid1='gpid1'
				System.out.println("create GID for " + pedigreeList.get(i));
				int gid = (int) createGID(manager, pedigreeList.get(i), gpid1,
						gpid2, locationID);
				gpid2 = gid;
				Germplasm germplasm1 = manager.getGermplasmByGID(gid);
				System.out.print("\t gpid1: " + germplasm1.getGpid1());
				System.out.print("\t gpid2: " + germplasm1.getGpid2());
				System.out
				.println("\t location: " + germplasm1.getLocationId());

				printToFile(manager, pw, germplasm1);
				flag = true;
			}
		}
		return flag;
	}

	public Germplasm getGermplasm_among_existing(GermplasmDataManager manager,
			List<Germplasm> germplasm, String pedigree, int gpid1,
			int locationID) throws MiddlewareQueryException {
		Germplasm germplasm1 = new Germplasm();
		for (int i = 0; i < germplasm.size(); i++) {
			/*
			 * finding the germplasm with same gpid1(root GID) as the proceeding
			 * pedigree's gpid1 or same gpid1 to the root's gid, and same
			 * locationID as the preceeding's location ID
			 */
			System.out.println("\t germplasm gpid1: "
					+ germplasm.get(i).getGpid1() + " gpid1: " + gpid1);
			System.out.println("\t germplasm gid: " + germplasm.get(i).getGid()
					+ " gid: " + gpid1);
			if ((germplasm.get(i).getGpid1() == gpid1 || germplasm.get(i)
					.getGid() == gpid1)
					&& germplasm.get(i).getLocationId() == locationID) {
				System.out.print("chosen germplasm: "
						+ germplasm.get(i).getGid());
				return germplasm1 = germplasm.get(i);
			}
		}
		return germplasm1;
	}

	public List<Germplasm> getGermplasmPOJOByName(GermplasmDataManager manager,
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

	public void getGermplasmByName(GermplasmDataManager manager, String s,
			int count_LOCAL, int count_CENTRAL, String id) throws MiddlewareQueryException, IOException {

		// file written with existing GID
		String csv = "E:/xampp/htdocs/GMGR/protected/modules/existingTerm.csv";

		FileWriter fw = new FileWriter(csv, true);
		BufferedWriter pw = new BufferedWriter(fw);

		List<Germplasm> germplasm = new ArrayList<Germplasm>();
		germplasm = manager.getGermplasmByName(s, 0, count_LOCAL,
				GetGermplasmByNameModes.NORMAL, Operation.EQUAL, 0, null,
				Database.LOCAL);
				
		List<Germplasm> germplasm2 = new ArrayList<Germplasm>();
		germplasm2 = manager.getGermplasmByName(s, 0, count_CENTRAL,
				GetGermplasmByNameModes.NORMAL, Operation.EQUAL, 0, null,
				Database.CENTRAL);
		for(int i=0; i<germplasm2.size();i++){
			germplasm.add(germplasm2.get(i));

		}
		String nval_gpid1, nval_gpid2;
		for (int i = 0; i < germplasm.size(); i++) {
			// System.out.println(germplasm.get(i).getGid());
			pw.write(id + ",");
			pw.write(s + ",");
			System.out.println("\n string: " + s);
			System.out.println("GID: " + germplasm.get(i).getGid());
			System.out.println("gpid1: " + germplasm.get(i).getGpid1());
			System.out.println("gpid2: " + germplasm.get(i).getGpid2());
			List<Name> name = new ArrayList<Name>();
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

			pw.write(germplasm.get(i).getGpid1() + "," + nval_gpid1 + ",");

			pw.write(germplasm.get(i).getGpid2() + "," + nval_gpid2 + ",");

			pw.write(germplasm.get(i).getGid() + ","); // gid
			pw.write(germplasm.get(i).getMethodId() + "," + method.getMname()
					+ ","); // method
			pw.write(germplasm.get(i).getLocationId() + ","
					+ location.getLname() + ","); // method

			pw.newLine();
		}

		pw.flush();
		pw.close();
		fw.close();

	}

	public int createGID(GermplasmDataManager manager, String term, int gpid1,
			int gpid2, int location) throws MiddlewareQueryException {

		int gid;
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

		Name name1 = new Name();
		name1.setNdate(0);
		name1.setNstat(0);
		name1.setReferenceId(0);
		name1.setUserId(0);
		name1.setLocationId(location);
		name1.setNval(term);
		name1.setTypeId(0);

		gid = manager.addGermplasm(germplasm1, name1);
		System.out.println("Germplasm" + gid);

		return gid;
	}
	public int selectMethodType(GermplasmDataManager manager,int femaleGID, int maleGID, String female_nval, String male_nval)throws MiddlewareQueryException, IOException{
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
	public void updateMethod(GermplasmDataManager manager) throws FileNotFoundException, IOException,
	ParseException, MiddlewareQueryException {
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader(
		"E:/xampp/htdocs/GMGR/protected/modules/changeMethod.json"));
		JSONObject jsonObject = (JSONObject) obj;
		int mid=Integer.valueOf((String) jsonObject.get("mid"));
		int gid=Integer.valueOf((String) jsonObject.get("gid"));
		String id=(String) jsonObject.get("id");
		
		Germplasm g= manager.getGermplasmByGID(gid);
		g.setMethodId(mid);
		manager.updateGermplasm(g);
		
		Method m=manager.getMethodByID(mid);
		
		/*UPDATE CORRECTED.CSV*/
		
		// updated file, file to be written
		String updatedFile = "E:/xampp/htdocs/GMGR/protected/modules/updatedCreatedGID.csv";

		// file to be updatedcret, file to be read
		String createdFile = "E:/xampp/htdocs/GMGR/protected/modules/createdGID.csv";

		File file = new File(updatedFile);

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}else{
			file.delete();
			file.createNewFile();
		}

		Writer bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));

		BufferedReader br = null;
		String line = "";

		br = new BufferedReader(new FileReader(createdFile));
		while ((line = br.readLine()) != null) {
			System.out.println("A: "+StringUtils.join(line, ","));
			String[] processedLine = processLine_createdGID(line, mid,m.getMname(),id,
					manager);
			System.out.println("B: "+StringUtils.join(processedLine, ","));
			bw.write(StringUtils.join(processedLine, ",")); // writing to the
			// new file with
			// updated germplasm
			bw.write("\n");
		}
		bw.close();
		br.close();

		new FileProperties().setFilePermission(updatedFile);
		// delete createdGID.csv
		file = new File(createdFile);
		file.delete();

		file = new File(createdFile);
		bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));

		br = null;
		line = "";

		br = new BufferedReader(new FileReader(updatedFile));
		while ((line = br.readLine()) != null) {
			bw.write(line); // writing to the new file with updated germplasm
			bw.write("\n");
		}
		bw.close();
		br.close();
		new FileProperties().setFilePermission(createdFile);
		/*END UPDATE CORRECTED.CSV*/
	}

	private String[] processLine_createdGID(String line, int mid,String method, String id,
			GermplasmDataManager manager) {
		
		String[] cells = line.split(","); 
		cells[2] = cells[2].replaceAll("\"", "");

		//System.out.println("\t id "+ id+ " cells[2]: "+ cells[2]+ " GID: "+germplasm.getGid().toString());;

		//if (cells[0].equals(id) && cells[2].equals(pedigree)) {
		if (cells[0].equals(id) ) {
			System.out.println("HERE");
			cells[4] = ""+mid;
			cells[5] = method;

			cells[4] = cells[4].replaceAll("\"", "");
			cells[5] = cells[5].replaceAll("\"", "");
			return cells;
		}
		return cells;
	}
}
