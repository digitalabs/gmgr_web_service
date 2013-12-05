package com.pedigreeimport.restjersey;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Location;
import org.json.simple.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import com.pedigreeimport.backend.*;

@Path("/term")
public class Model {

	int gpid1 = 0;
	int gpid2 = 0;

	@Path("/welcome")
	@GET
	@Produces("text/html")
	public Response welcome() {

		return Response.status(200).entity("Genealogy Manager/Pedigree Import")
				.build();
	}
    
	@Path("sortList1")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response sortList1() throws FileNotFoundException, IOException{
		new joanieTest();
		joanieTest.sortList();
		return Response.status(200).entity("Ok!").build();
	}
	
	@Path("/parse")
	@GET
	@Consumes()
	@Produces("application/json")
	public Response parsePedigree() throws JSONException,
			FileNotFoundException, IOException {
System.out.println("HREEEEE!!!");
		String csv = "/var/www/GMGR/protected/modules/output.csv";
		try {
			FileWriter fw = new FileWriter(csv);
			BufferedWriter pw = new BufferedWriter(fw);
			FileReader docinfo = new FileReader("/var/www/GMGR/protected/modules/docinfo.json");
			Object json_obj = JSONValue.parse(docinfo);
			JSONObject json_array = (JSONObject) json_obj;
			JSONArray gu_obj = (JSONArray) json_array.get("list");
			int k = 0;
			for (int j = 0; j < gu_obj.size();) {
				// System.out.println(gu_obj.get(0));
				String error, gid;
				pw.write("N/A,");
				pw.write(gu_obj.get(j) + ",");
				j++;
				// System.out.print("NULL ,");

				for (int i = 1; i <= 2; i++) {
					// System.out.print(gu_obj.get(j).toString() + "\t");

					pw.write(gu_obj.get(j).toString() + ","); // pedigree term
					error = new Main().checkString(gu_obj.get(j).toString());
					pw.write(k + ",");
					k++;
					if (error.equals("")) {
						pw.write("in standardized format,"); // remarks
						// System.out.print(" in standardized format,");
						// //remarks
						String[] tokens = new Tokenize().tokenize(gu_obj.get(j)
								.toString());
						gid = new Tokenize().stringTokens(tokens);

						if (i == 1) {
							pw.write("\"" + gid + "\"" + ","); // GID
							
						}
						if (i == 2) {
							if (j == 1) {
								pw.write("\"" + gid + "\n"
										+ gu_obj.get(j - 1).toString() + "\"");
							} else {
								pw.write("\"" + gid + "\n"
										+ gu_obj.get(j - 2).toString() + "\"");
							}
						}

						 

					} else {
						pw.write("\"" + error + "\"" + ","); // remarks
						if (i == 2) {
							pw.write("N/A"); // GID
						}
						if (i == 1) {
							pw.write("N/A,"); // GID
						}
					}
					j++;
				}
				pw.newLine();
			}

			// Flush the output to the file
			pw.flush();
			// Close the Print Writer
			pw.close();
			// Close the File Writer
			fw.close();
			docinfo.close();
			
		} catch (Exception e) {
		}
		new FileProperties().setFilePermission(csv);
		return Response.status(200).entity("OK!").build();
	}

	@Path("/standardize")
	@GET
	@Consumes()
	@Produces("application/json")
	public Response standardizePedigree() throws JSONException,
			FileNotFoundException, IOException {
		System.out.println("corrected.csv performed!!!");
		String csv = "/var/www/GMGR/protected/modules/corrected.csv";

		try {
			FileWriter fw = new FileWriter(csv);
			BufferedWriter pw = new BufferedWriter(fw);
			FileReader docinfo = new FileReader("/var/www/GMGR/protected/modules/docinfo.json");
			Object json_obj1 = JSONValue.parse(docinfo);
			JSONObject json_array1 = (JSONObject) json_obj1;
			JSONArray obj_terms = (JSONArray) json_array1.get("list");
			int k = 0;
			String correctedTerm, error, gid;
			for (int i = 0; i < (obj_terms.size());) {

				pw.write("N/A,");
				pw.write(obj_terms.get(i) + ",");
				// System.out.println("["+i+"]"+obj_terms.get(i));
				i++;
				// System.out.println("i: "+i);
				for (int j = 1; j < 3; j++) {
					// obj_terms.get(count);
					// count++;
					correctedTerm = obj_terms.get(i).toString();
					// System.out.println(""+correctedTerm);
					pw.write(k + ",");
					k++;
					error = new Main().checkString(obj_terms.get(i).toString());
					if (error.equals("")) {
						// System.out.print("in standardized format");
						pw.write("in standardized format,"); // remarks
						String[] tokens = new Tokenize().tokenize(obj_terms
								.get(i).toString());
						gid = new Tokenize().stringTokens(tokens);
						pw.write("\"" + gid + "\"" + ","); // GID

					} else {
						correctedTerm = new FixString()
								.checkString(correctedTerm);
						error = new Main().checkString(correctedTerm);
						if (error.equals("")) {
							pw.write("in standardized format,"); // remarks
							String[] tokens = new Tokenize()
									.tokenize(correctedTerm);
							gid = new Tokenize().stringTokens(tokens);
							pw.write("\"" + gid + "\"" + ","); // GID
							// System.out.print("gid"+gid);
						} else {
							// System.out.print("not in standardized format");
							pw.write("\"" + error + "\"" + ","); // remarks
							pw.write("N/A,"); // GID
						}
					}
					if (j == 1) {
						pw.write(correctedTerm + ","); // pedigree term
					}
					if (j == 2) {
						pw.write(correctedTerm + ","); // pedigree term
					}
					// System.out.print("count: "+count);
					i++;
				}
				pw.newLine();
			}

			// Flush the output to the file
			pw.flush();
			// Close the Print Writer
			pw.close();
			// Close the File Writer
			fw.close();
			docinfo.close();
		
		} catch (Exception e) {
		}
		new FileProperties().setFilePermission(csv);
		return Response.status(200).entity("OK standardize!").build();
	}

	@Path("/checkEditedString")
	@GET
	@Consumes()
	@Produces("application/json")
	public Response checkPedigree() throws JSONException,
			FileNotFoundException, IOException, MiddlewareQueryException {
		System.out.println("newString.csv performed!!!");
		String csv = "/var/www/GMGR/protected/modules/newString.csv";
		FileWriter fw = new FileWriter(csv);
		BufferedWriter pw = new BufferedWriter(fw);
		FileReader docinfo = new FileReader("/var/www/GMGR/protected/modules/docinfo.json");
		Object json_obj = JSONValue.parse(docinfo);

		JSONObject json_array = (JSONObject) json_obj;
		String newString = (String) json_array.get("new");
		 System.out.println(newString);
		 
		// System.out.println(gu_obj.get(0));
		String error, gid;
		pw.write("N/A,");
		// pw.write(+",");

		pw.write(newString + ","); // pedigree term
		error = new Main().checkString(newString);

		if (error.equals("")) {
			pw.write("in standardized format,"); // remarks
			// System.out.print(" in standardized format,"); //remarks
			String[] tokens = new Tokenize().tokenize(newString);
			gid = new Tokenize().stringTokens(tokens);
			pw.write("\"" + gid + "\"");
		} else {
			pw.write("\"" + error + "\"" + ","); // remarks
			pw.write("N/A"); // GID
		}
		pw.newLine();
		// Flush the output to the file
		pw.flush();
		// Close the Print Writer
		pw.close();
		// Close the File Writer
		fw.close();
		docinfo.close();
		
		new FileProperties().setFilePermission(csv);
		return Response.status(200).entity("OK!").build();
	}

	@Path("/createGID")
	@GET
	@Consumes()
	@Produces("application/json")
	public Response createGID() throws FileNotFoundException, IOException,
			MiddlewareQueryException, ParseException {
		
		new AssignGID().createGID();
	new AssignGID().print_checkedBox();
		
		return Response.status(200).entity("OK!").build();
	}
	
	@Path("/chooseGID")
	@GET
	@Consumes()
	@Produces("application/json")
	public Response chooseGID() throws FileNotFoundException, IOException,
			MiddlewareQueryException, ParseException {
		new AssignGID().chooseGID();
		return Response.status(200).entity("OK!").build();
	}

	@Path("/fetchLocation")
	@GET
	@Consumes()
	@Produces("application/json")
	public Response fetchLocation() throws JSONException, FileNotFoundException,
			IOException, MiddlewareQueryException, ParseException {
		
		ManagerFactory factory = new Config().configDB();
		GermplasmDataManager manager = factory.getGermplasmDataManager();
		
		Writer out = new BufferedWriter(new OutputStreamWriter(
			    new FileOutputStream("/var/www/GMGR/protected/modules/location.csv"), "UTF-8"));
		long count=manager.countAllLocations();
		List<Location> location=manager.getAllLocations(0,(int) count);
		for(int i=0; i< location.size(); i++){
			out.write(location.get(i).getLocid()+"#");
			out.write(/*location.get(i).getLabbr()+":"+*/location.get(i).getLname()+"\n");
		}
		
		out.close();
		factory.close();
		
		return Response.status(200).entity("OK!").build();
	}
}
