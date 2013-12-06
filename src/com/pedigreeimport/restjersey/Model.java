package com.pedigreeimport.restjersey;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Location;
import org.json.simple.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.pedigreeimport.backend.*;

import com.google.gson.Gson;

import javax.xml.bind.annotation.XmlRootElement;



@Path("/term")
public class Model {

	@Path("/updateMethod")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeMethod(JSONObject data) throws JSONException, FileNotFoundException,
	IOException, MiddlewareQueryException, ParseException {
		
		
		ManagerFactory factory = new Config().configDB();
		List<List<String>> createdGID =new ArrayList<List<String>>();
		createdGID= (List<List<String>>) data.get("createdGID");
		int mid=Integer.valueOf((String) data.get("mid"));
		int gid=Integer.valueOf((String) data.get("gid"));
		
		String id=(String) data.get("id");
		
		GermplasmDataManager manager = factory.getGermplasmDataManager();
		data=new AssignGID().updateMethod(manager, createdGID, mid, gid, id);
		
		factory.close();

		return Response.status(200).entity(data).build();
	}
	
	@Path("/chooseGID2")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response chooseGID2(JSONObject data) throws FileNotFoundException, IOException,
	MiddlewareQueryException, ParseException, InterruptedException {		
		
		new test();
		System.out.println("HERE!");
		JSONObject output=new JSONObject();
		output=test.single_createGID(data);
		return Response.status(200).entity(output).build();

	}
	@Path("/createGID3")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createGID3(JSONObject data) throws FileNotFoundException, IOException,
	MiddlewareQueryException, ParseException, InterruptedException {
		new test();
		//new AssignGID().createGID();
		//print_checkedBox();
		List<String> checked= new ArrayList<String>();
		List<List<String>> list= new ArrayList<List<String>>();
		List<List<String>> createdGID= new ArrayList<List<String>>();
		List<List<String>> existingTerm= new ArrayList<List<String>>();
		
		JSONObject json_array = (JSONObject) data;
		String locationID = (String) json_array.get("locationID");
		checked= (List<String>) json_array.get("checked");
		list = (List<List<String>>) json_array.get("list");
		createdGID = (List<List<String>>) json_array.get("createdGID");
		existingTerm = (List<List<String>>) json_array.get("existingTerm");
		String userID = (String) json_array.get("userID");
		
		System.out.println("\t createdGID @ Model: "+createdGID.size()+"\t"+createdGID);

		JSONObject output=new JSONObject();
		System.out.println();
		output=test.bulk_createGID2(createdGID,list, checked,Integer.parseInt(locationID),existingTerm, userID);
		
	
		//System.out.println("list: "+  json_array.get("list"));
		//System.out.println("createdGID: "+ json_array.get("createdGID"));
		
		//System.out.println("SINGLE CREATE GID ");
		
		
		
		return Response.status(200).entity(output).build();
	}
	
	@Path("/createGID2")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createGID2(JSONObject data) throws FileNotFoundException, IOException,
	MiddlewareQueryException, ParseException, InterruptedException {
		new test();
		//new AssignGID().createGID();
		//print_checkedBox();
		List<String> checked= new ArrayList<String>();
		List<List<String>> list= new ArrayList<List<String>>();
		List<List<String>> existingTerm= new ArrayList<List<String>>();
		
		JSONObject json_array = (JSONObject) data;
		String locationID = (String) json_array.get("locationID");
		checked= (List<String>) json_array.get("checked");
		list = (List<List<String>>) json_array.get("list");
		existingTerm = (List<List<String>>) json_array.get("existingTerm");
		String userID = (String) json_array.get("userID");
		

		JSONObject output=new JSONObject();
		System.out.println();
		output=test.bulk_createGID(list, checked,Integer.parseInt(locationID),existingTerm, userID);
		
		
		//System.out.println("list: "+  json_array.get("list"));
		//System.out.println("createdGID: "+ json_array.get("createdGID"));
		System.out.println("\t existing: "+output.get("existingTerm"));
		//System.out.println("SINGLE CREATE GID ");
		
		
		
		return Response.status(200).entity(output).build();
	}
	

	@Path("/updateGermplasmName")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateGermplasmName(JSONObject data ) throws MiddlewareQueryException, IOException {

		List<String> newString= new ArrayList<String>();
		
		JSONObject json_array = (JSONObject) data;
		String newName = (String) json_array.get("new");
		String old = (String) json_array.get("old");
		System.out.println("new: "+newName);
		String error = new Main().checkString(newName);

		List<List<String>> object = (List<List<String>>) json_array.get("list");
		newString.add("N/A");
		newString.add(newName);
		
		JSONObject data_output= new JSONObject();
		if (error.equals("")) {
			List<List<String>> output = new ArrayList<List<String>>() ;

			for(int i=0; i<object.size();i++){
				List<String> row_output= new ArrayList<String>();


				//String row=output.get(i) ;
				List<String> row=object.get(i);

				row_output.add(row.get(0));
				row_output.add(row.get(1));
				row_output.add(row.get(2));

				System.out.print(":: "+ row.get(3));
				System.out.println(" \t:: "+ row.get(9));

				if(row.get(5).equals(old)){
					
					System.out.println("female edited");
					//System.out.println("New:: "+ row.get(2));
					
					row_output.add("in standardized format");
					String[] tokens = new Tokenize().tokenize(newName);
					String gid = new Tokenize().stringTokens(tokens);
					row_output.add(gid);
					row_output.add(newName);
					
					newString.add("in standardized format");
					newString.add(gid);

				}else{
					
					row_output.add(row.get(3));
					row_output.add(row.get(4));
					row_output.add(row.get(5));
					
				}

				if(row.get(9).equals(old)){
					System.out.println("male edited");
					
					row_output.add(row.get(6));
					row_output.add("in standardized format");
					String[] tokens = new Tokenize().tokenize(newName);
					String gid = new Tokenize().stringTokens(tokens);
					row_output.add(gid);
					row_output.add(newName);
					
					//System.out.println(" \t New:: "+ row.get(6));
					
					newString.add("in standardized format");
					newString.add(gid);
				}else{
					row_output.add(row.get(6));
					row_output.add(row.get(7));
					row_output.add(row.get(8));
					row_output.add(row.get(9));

				}

				output.add(row_output);
			}
			data_output.put("list", output);
			data_output.put("new", newName);
			data_output.put("old", old);
			data_output.put("updated", true);
			
			data_output.put("newString", newString);

		}else{

			data_output.put("list", object);
			data_output.put("new", newName);
			data_output.put("old", error);
			data_output.put("updated", false);
			newString.add(error);
			newString.add("N/A");
			data_output.put("newString", newString);
		}
		System.out.println("list: "+data_output.get("list"));

		return Response.status(200).entity(data_output).build();
	}


	@Path("/standardize2")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response standardize(JSONObject list) throws MiddlewareQueryException, IOException  {

		JSONObject json_array = (JSONObject) list;

		//List<String> object=(List<String>) json_array.get("list");

		List<List<String>> object = (List<List<String>>) json_array.get("list");

		List<List<String>> output = new ArrayList<List<String>>();
		int k = 0;
		String correctedTerm, error, gid;
		List<String> row_object=new ArrayList<String>();
		List<String> row= new ArrayList<String>();
		
		System.out.println("size"+object.size());
		for (int m = 0; m < object.size();m++) {

			row_object= object.get(m);
			row= new ArrayList<String>();
			
			row.add("N/A");		//0
			row.add(row_object.get(1));		//1
			System.out.print("N/A,");
			System.out.print(row_object.get(1) + ",");


			for (int i = 2; i < row_object.size(); i++) {
				//for (int j = 1; j < 3; j++) {
				if(i==2 || i==6){


					// object.get(count);
					// count++;
					correctedTerm = row_object.get(i).toString();
					// System.out.println(""+correctedTerm);
					row.add(""+k);
					System.out.print(k+ ",");
					k++;
					error = new Main().checkString(row_object.get(i).toString());
					if (error.equals("")) {
						// System.out.print("in standardized format");
						row.add("in standardized format"); // remarks
						String[] tokens = new Tokenize().tokenize(row_object
								.get(i).toString());
						gid = new Tokenize().stringTokens(tokens);
						row.add(gid); // GID
						System.out.print(gid); // GID

					} else {
						correctedTerm = new FixString().checkString(correctedTerm);
						error = new Main().checkString(correctedTerm);
						System.out.print("ERROR:" + error + "|"); // remarks
						if (error.equals("")) {
							System.out.println("in standardized format,"); // remarks
							row.add("in standardized format"); // remarks
							String[] tokens = new Tokenize()
							.tokenize(correctedTerm);
							gid = new Tokenize().stringTokens(tokens);
							row.add(gid); // GID
							System.out.print("\"" + gid + "\"" + ","); // GID
						} else {
							// System.out.print("not in standardized format");

							row.add(error); // remarks
							row.add("N/A"); // GID
						}
					}
					//if (j == 1 || j==2) {
					row.add(correctedTerm); // pedigree term
					System.out.print(correctedTerm + ","); // pedigree term
					//}
					// System.out.print("count: "+count);

				}

				System.out.println();

			}
			output.add(row);
		}

		System.out.println("yeah: "+output);
		System.out.println("\t\t\t ***END Method Standardize***");

		return Response.status(201).entity(output).build();
	}

	@POST
	@Path("/post")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createTrackInJSON(JSONObject list) throws IOException, ParseException, MiddlewareQueryException {

		JSONObject json_array = (JSONObject) list;

		List<String> gu_obj=(List<String>) json_array.get("list");
		System.out.println("list: "+ gu_obj);
		
		gu_obj=new sortList().algo(gu_obj);

		List<List<String>> output = new ArrayList<List<String>>();
		int k=0;
		for (int j =0; j< gu_obj.size();){
			List<String> row= new ArrayList<String>();
			String error, gid;

			row.add("N/A");
			row.add(gu_obj.get(j));

			//System.out.print("[0] N/A,");
			//System.out.print("[1] "+gu_obj.get(j) + ",");
			j++;
			// System.out.print("NULL ,");
			
			for (int i = 1; i <= 2; i++) {
				// System.out.print(gu_obj.get(j).toString() + "\t");

				row.add(gu_obj.get(j).toString()); // pedigree term
				//System.out.print("[2] "+gu_obj.get(j).toString() + ","); // pedigree term

				error = new Main().checkString(gu_obj.get(j).toString());
				row.add(""+k);
				//System.out.print("[3] "+k + ",");
				//System.out.print("[ERROR] "+error + ",");

				k++;
				if (error.equals("")) {

					//System.out.print("[4] "+"in standardized format,");
					row.add("in standardized format"); // remarks
					// System.out.print(" in standardized format,");
					// //remarks
					String[] tokens = new Tokenize().tokenize(gu_obj.get(j)
							.toString());
					gid = new Tokenize().stringTokens(tokens);

					if (i == 1) {
						//System.out.print("\"" + gid + "\""); // GID
						row.add( gid  ); // GID

					}
					if (i == 2) {
						if (j == 1) {
							row.add(  gid);
							//System.out.print("\"" + gid + "\"");
						} else {
							row.add( gid );
							//System.out.print("\"" + gid +"\"");
						}
					}



				} else {
					//System.out.print("\"" + error + "\"" + ","); // remarks
					row.add(error); // remarks
					if (i == 2) {
						row.add("N/A"); // GID
						//System.out.print("N/A,");
					}
					if (i == 1) {
						row.add("N/A"); // GID
						//System.out.print("N/A,");
					}
				}
				j++;
			}
			//System.out.println();
			//System.out.println("line: "+line);

			//row.add(line);
			output.add(row);

			//System.out.println("\n \n **row: "+row);
			//System.out.println("\n \n **output: "+output);
		}
		//System.out.println("output: "+output);
		//System.out.println("output size: "+output.size());

		return Response.status(201).entity(output).build();

	}
	@Path("/welcome")
	@GET
	@Produces("text/html")
	public Response welcome() {

		return Response.status(200).entity("Genealogy Manager/Pedigree Import")
		.build();
	}

	@Path("/sort")
	@GET
	@Produces("text/html")
	public Response sortList() throws IOException{

		return Response.status(200).entity("sortedList")
		.build();
	}

	@Path("/parse")
	@GET
	@Consumes()
	@Produces("application/json")
	public Response parsePedigree() throws JSONException,
	FileNotFoundException, IOException, MiddlewareQueryException {
		System.out.println("\t\t\t ***Method Parse***");
		String csv = "E:/xampp/htdocs/GMGR/csv_files/output.csv";

		FileReader json= new FileReader("E:/xampp/htdocs/GMGR/json_files/docinfo.json");
		Object json_obj = JSONValue.parse(json);
		JSONObject json_array = (JSONObject) json_obj;
		JSONArray gu_obj = (JSONArray) json_array.get("list");
		int k = 0;

		System.out.println("# of germplasm in docinfo.json: "+ gu_obj.size()+"\n");
		FileWriter fw = new FileWriter(csv,true);
		BufferedWriter pw = new BufferedWriter(fw);

		for (int j = 0; j < gu_obj.size();) {
			// System.out.println(gu_obj.get(0));


			String error, gid;
			pw.append("N/A,");
			pw.append(gu_obj.get(j) + ",");

			System.out.print("[0] N/A,");
			System.out.print("[1] "+gu_obj.get(j) + ",");
			j++;
			// System.out.print("NULL ,");

			for (int i = 1; i <= 2; i++) {
				// System.out.print(gu_obj.get(j).toString() + "\t");

				pw.append(gu_obj.get(j).toString() + ","); // pedigree term
				System.out.print("[2] "+gu_obj.get(j).toString() + ","); // pedigree term

				error = new Main().checkString(gu_obj.get(j).toString());
				pw.append(k + ",");
				System.out.print("[3] "+k + ",");
				//System.out.print("[ERROR] "+error + ",");

				k++;
				if (error.equals("")) {

					System.out.print("[4] "+"in standardized format,");
					pw.write("in standardized format,"); // remarks
					// System.out.print(" in standardized format,");
					// //remarks
					String[] tokens = new Tokenize().tokenize(gu_obj.get(j)
							.toString());
					gid = new Tokenize().stringTokens(tokens);

					if (i == 1) {
						System.out.print("\"" + gid + "\"" + ","); // GID
						pw.append("\"" + gid + "\"" + ","); // GID

					}
					if (i == 2) {
						if (j == 1) {
							pw.append("\"" + gid + "\n"
									+ gu_obj.get(j - 1).toString() + "\"");
							System.out.print("\"" + gid + "\n"
									+ gu_obj.get(j - 1).toString() + "\"");
						} else {
							pw.append("\"" + gid + "\n"
									+ gu_obj.get(j - 2).toString() + "\"");
							System.out.print("\"" + gid + "\n"
									+ gu_obj.get(j - 2).toString() + "\"");
						}
					}



				} else {
					System.out.print("\"" + error + "\"" + ","); // remarks
					pw.append("\"" + error + "\"" + ","); // remarks
					if (i == 2) {
						pw.append("N/A"); // GID
						System.out.print("N/A,");
					}
					if (i == 1) {
						pw.append("N/A,"); // GID
						System.out.print("N/A,");
					}
				}
				j++;
			}
			System.out.println();
			pw.newLine();

		}
		// Flush the output to the file
		pw.flush();
		// Close the Print Writer
		pw.close();
		// Close the File Writer
		fw.close();

		json.close();

		System.out.println("\t\t\t ***END Method Parse***");
		new FileProperties().setFilePermission(csv);
		return Response.status(200).entity("OK!").build();
	}

	@Path("/standardize")
	@GET
	@Consumes()
	@Produces("application/json")
	public Response standardizePedigree() throws JSONException,
	FileNotFoundException, IOException, MiddlewareQueryException {

		String csv = "E:/xampp/htdocs/GMGR/csv_files/corrected.csv";
		System.out.println("CORRECTED");
		FileWriter fw = new FileWriter(csv);
		BufferedWriter pw = new BufferedWriter(fw);
		FileReader json= new FileReader("E:/xampp/htdocs/GMGR/json_files/docinfo.json");
		Object json_obj1 = JSONValue.parse(json);
		JSONObject json_array1 = (JSONObject) json_obj1;
		JSONArray obj_terms = (JSONArray) json_array1.get("list");
		int k = 0;
		String correctedTerm, error, gid;

		for (int i = 0; i < (obj_terms.size());) {

			pw.write("N/A,");
			pw.write(obj_terms.get(i) + ",");
			System.out.print("N/A,");
			System.out.print(obj_terms.get(i) + ",");

			i++;

			for (int j = 1; j < 3; j++) {
				// obj_terms.get(count);
				// count++;
				correctedTerm = obj_terms.get(i).toString();
				// System.out.println(""+correctedTerm);
				pw.write(k + ",");
				System.out.print(k+ ",");
				k++;
				error = new Main().checkString(obj_terms.get(i).toString());
				if (error.equals("")) {
					// System.out.print("in standardized format");
					pw.write("in standardized format,"); // remarks
					String[] tokens = new Tokenize().tokenize(obj_terms
							.get(i).toString());
					gid = new Tokenize().stringTokens(tokens);
					pw.write("\"" + gid + "\"" + ","); // GID
					System.out.print("\"" + gid + "\"" + ","); // GID

				} else {
					correctedTerm = new FixString()
					.checkString(correctedTerm);
					error = new Main().checkString(correctedTerm);
					System.out.print("ERROR:" + error + "|"); // remarks
					if (error.equals("")) {
						System.out.println("in standardized format,"); // remarks
						pw.write("in standardized format,"); // remarks
						String[] tokens = new Tokenize()
						.tokenize(correctedTerm);
						gid = new Tokenize().stringTokens(tokens);
						pw.write("\"" + gid + "\"" + ","); // GID
						System.out.print("\"" + gid + "\"" + ","); // GID
					} else {
						// System.out.print("not in standardized format");

						pw.write("\"" + error + "\"" + ","); // remarks
						pw.write("N/A,"); // GID
					}
				}
				if (j == 1 || j==2) {
					pw.write(correctedTerm + ","); // pedigree term
					System.out.print(correctedTerm + ","); // pedigree term
				}
				// System.out.print("count: "+count);
				i++;
			}
			System.out.println();
			pw.newLine();
		}

		// Flush the output to the file
		pw.flush();
		// Close the Print Writer
		pw.close();
		// Close the File Writer
		fw.close();
		json.close();
		System.out.println("\t\t\t ***END Method Standardize***");
		new FileProperties().setFilePermission(csv);
		return Response.status(200).entity("OK standardize!").build();
	}

	@Path("/checkEditedString")
	@GET
	@Consumes()
	@Produces("application/json")
	public Response checkPedigree() throws JSONException,
	FileNotFoundException, IOException, MiddlewareQueryException {

		String csv = "E:/xampp/htdocs/GMGR/csv_files/newString.csv";
		FileWriter fw = new FileWriter(csv);
		BufferedWriter pw = new BufferedWriter(fw);
		FileReader json= new FileReader("E:/xampp/htdocs/GMGR/json_files/docinfo.json");
		Object json_obj = JSONValue.parse(json);

		JSONObject json_array = (JSONObject) json_obj;
		String newString = (String) json_array.get("new");
		// System.out.println(newString);

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
		json.close();
		new FileProperties().setFilePermission(csv);
		return Response.status(200).entity("OK!").build();
	}

	@Path("/createGID")
	@GET
	@Consumes()
	@Produces("application/json")
	public Response createGID() throws FileNotFoundException, IOException,
	MiddlewareQueryException, ParseException, InterruptedException {

		new CopyOftest();
		//new AssignGID().createGID();
		print_checkedBox();
		CopyOftest.bulk_createGID();


		return Response.status(200).entity("OK!").build();
	}

	@Path("/chooseGID")
	@GET
	@Consumes()
	@Produces("application/json")
	public Response chooseGID() throws FileNotFoundException, IOException,
	MiddlewareQueryException, ParseException, InterruptedException {
		//new AssignGID().chooseGID();
		//new test();
		//.single_createGID();
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
				new FileOutputStream("E:/xampp/htdocs/GMGR/csv_files/location.csv"), "UTF-8"));
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

	public void print_checkedBox() throws IOException, ParseException{
		String csv = "E:/xampp/htdocs/GMGR/csv_files/checked.csv";
		FileWriter fw = new FileWriter(csv,true);
		FileReader json= new FileReader(
		"E:/xampp/htdocs/GMGR/json_files/checked.json");
		Object json_obj = JSONValue.parse(json);
		JSONObject json_array = (JSONObject) json_obj;
		JSONArray gu_obj = (JSONArray) json_array.get("checked");
		for (int i = 0; i < gu_obj.size(); i++) {
			System.out.println(""+gu_obj.get(i)+",");
			fw.append(""+gu_obj.get(i)+",");
		}
		new FileProperties().setFilePermission(csv);
		fw.close();
		json.close();


	}
	

}
