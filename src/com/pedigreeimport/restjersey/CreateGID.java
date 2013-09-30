package com.pedigreeimport.restjersey;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class CreateGID {
	
	public void bulk_create()throws FileNotFoundException, IOException,
	MiddlewareQueryException, ParseException {

		ManagerFactory factory = new Config().configDB();
		GermplasmDataManager manager = factory.getGermplasmDataManager();

		// file written with created GID's
		String csv = "E:/xampp/htdocs/GMGR/protected/modules/createdGID.csv";

		Writer pw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(csv,true), "UTF-8"));

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
		json.close();
		// close the database connection
		factory.close();
		new FileProperties().setFilePermission(csv);
	}

}
