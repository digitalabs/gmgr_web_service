package com.pedigreeimport.restjersey;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.pedigreeimport.backend.*;

@Path("/term")
public class Model {
	
	@Path("/welcome")
	@GET
	@Produces("text/html")
	public Response welcome() {
		
		return Response.status(200).entity("Genealogy Manager")
		.build();
	}
		
	@Path("/updateMethod")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeMethod(JSONObject data) throws JSONException, FileNotFoundException,
	IOException, MiddlewareQueryException, ParseException {

		List<List<String>> createdGID =new ArrayList<List<String>>();
		createdGID= (List<List<String>>) data.get("createdGID");
		int mid=Integer.valueOf((String) data.get("mid"));
		int gid=Integer.valueOf((String) data.get("gid"));

		String id=(String) data.get("id");
		ManagerFactory factory = new Config().configDB();
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
		//System.out.println("HERE!");
		JSONObject output=new JSONObject();
		ManagerFactory factory = new Config().configDB();
		output=test.single_createGID(data,factory);
		factory.close();
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
		existingTerm = (List<List<String>>) json_array.get("existing");
		String userID = (String) json_array.get("userID");

		//System.out.println("\t createdGID @ Model: "+createdGID.size()+"\t"+createdGID);
		//System.out.println("\t existing: \t"+existingTerm);
		//System.out.println("\t list: "+list.size()+"\t"+list);
		//System.out.println("\t checked: "+checked.size()+"\t"+checked);
		//System.out.println("\t locationID: \t"+locationID);
		//System.out.println("\t userID: \t"+userID);

		JSONObject output=new JSONObject();
		//System.out.println();
		ManagerFactory factory = new Config().configDB();
		output=test.bulk_createGID2(createdGID,list, checked,Integer.parseInt(locationID),existingTerm, userID,factory);
		factory.close();

		////System.out.println("list: "+  json_array.get("list"));
		////System.out.println("createdGID: "+ json_array.get("createdGID"));

		////System.out.println("SINGLE CREATE GID ");



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
		//System.out.println();
		ManagerFactory factory = new Config().configDB();
		output=test.bulk_createGID(list, checked,Integer.parseInt(locationID),existingTerm, userID,factory);
		factory.close();

		////System.out.println("list: "+  json_array.get("list"));
		////System.out.println("createdGID: "+ json_array.get("createdGID"));
		////System.out.println("\t existing: "+output.get("existingTerm"));
		////System.out.println("SINGLE CREATE GID ");

		return Response.status(200).entity(output).build();
	}


	@Path("/updateGermplasmName")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateGermplasmName(JSONObject data ) throws MiddlewareQueryException, IOException {

		System.out.println("UPDATING GERMPLASM NAME");
		List<String> newString= new ArrayList<String>();

		JSONObject json_array = (JSONObject) data;
		String newName = (String) json_array.get("new");
		String old = (String) json_array.get("old");
		//System.out.println("new: "+newName);
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

				//System.out.print(":: "+ row.get(3));
				//System.out.println(" \t:: "+ row.get(9));

				if(row.get(5).equals(old)){

					//System.out.println("female edited");
					////System.out.println("New:: "+ row.get(2));

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
					//System.out.println("male edited");

					row_output.add(row.get(6));
					row_output.add("in standardized format");
					String[] tokens = new Tokenize().tokenize(newName);
					String gid = new Tokenize().stringTokens(tokens);
					row_output.add(gid);
					row_output.add(newName);

					////System.out.println(" \t New:: "+ row.get(6));

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
		//System.out.println("list: "+data_output.get("list"));

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

		////System.out.println("size"+object.size());
		for (int m = 0; m < object.size();m++) {

			row_object= object.get(m);
			row= new ArrayList<String>();

			row.add("N/A");		//0
			row.add(row_object.get(1));		//1
			//System.out.print("N/A,");
			//System.out.print(row_object.get(1) + ",");


			for (int i = 2; i < row_object.size(); i++) {
				//for (int j = 1; j < 3; j++) {
				if(i==2 || i==6){


					// object.get(count);
					// count++;
					correctedTerm = row_object.get(i).toString();
					// //System.out.println(""+correctedTerm);
					row.add(""+k);
					//System.out.print(k+ ",");
					k++;
					error = new Main().checkString(row_object.get(i).toString());
					if (error.equals("")) {
						// //System.out.print("in standardized format");
						row.add("in standardized format"); // remarks
						String[] tokens = new Tokenize().tokenize(row_object
								.get(i).toString());
						gid = new Tokenize().stringTokens(tokens);
						row.add(gid); // GID
						//System.out.print(gid); // GID

					} else {
						correctedTerm = new FixString().checkString(correctedTerm);
						error = new Main().checkString(correctedTerm);
						//System.out.print("ERROR:" + error + "|"); // remarks
						if (error.equals("")) {
							//System.out.println("in standardized format,"); // remarks
							row.add("in standardized format"); // remarks
							String[] tokens = new Tokenize()
							.tokenize(correctedTerm);
							gid = new Tokenize().stringTokens(tokens);
							row.add(gid); // GID
							//System.out.print("\"" + gid + "\"" + ","); // GID
						} else {
							// //System.out.print("not in standardized format");

							row.add(error); // remarks
							row.add("N/A"); // GID
						}
					}
					//if (j == 1 || j==2) {
					row.add(correctedTerm); // pedigree term
					//System.out.print(correctedTerm + ","); // pedigree term
					//}
					// //System.out.print("count: "+count);

				}
				System.out.println(""+row_object.get(i));
				if(i==10){
					row.add(row_object.get(i).toString()); // cross' date of creation
				}

			}
			
			output.add(row);
		}

		System.out.println("yeah: "+output);
		//System.out.println("\t\t\t ***END Method Standardize***");

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
			row.add(gu_obj.get(j));	// Cross name

			////System.out.print("[0] N/A,");
			//System.out.print(" "+gu_obj.get(j) + ",");

			// //System.out.print("NULL ,");
			System.out.println(j+" :: "+gu_obj.get(j).toString());

			for (int i = 1; i <= 2; i++) {
				j++;
				if(j< gu_obj.size()){
					//System.out.println(j+" :: "+gu_obj.get(j).toString());
					// //System.out.print(gu_obj.get(j).toString() + "\t");

					row.add(gu_obj.get(j).toString()); // pedigree term
					////System.out.print("[2] "+gu_obj.get(j).toString() + ","); // pedigree term

					error = new Main().checkString(gu_obj.get(j).toString());
					row.add(""+k);	//id
					////System.out.print("[3] "+k + ",");
					////System.out.print("[ERROR] "+error + ",");

					k++;
					if (error.equals("")) {

						////System.out.print("[4] "+"in standardized format,");
						row.add("in standardized format"); // remarks
						// //System.out.print(" in standardized format,");
						// //remarks
						String[] tokens = new Tokenize().tokenize(gu_obj.get(j)
								.toString());
						gid = new Tokenize().stringTokens(tokens);

						row.add( gid  ); // GID

					} else {
						////System.out.print("\"" + error + "\"" + ","); // remarks
						row.add(error); // remarks
						row.add("N/A"); // GID
						////System.out.print("N/A,");
					}
				}
			}
			j++;
			//System.out.println(j+" :: "+gu_obj.get(j).toString());
			row.add(gu_obj.get(j).toString()); // cross' date of creation
			j++;
			//System.out.println(j+" :: "+gu_obj.get(j).toString());
			
			////System.out.println();
			////System.out.println("line: "+line);

			//row.add(line);
			output.add(row);

			////System.out.println("\n \n **row: "+row);
			////System.out.println("\n \n **output: "+output);
		}
		////System.out.println("output: "+output);
		////System.out.println("output size: "+output.size());

		return Response.status(201).entity(output).build();

	}
	
	@Path("/searchGID")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchGID(JSONObject data) throws JSONException, FileNotFoundException,
			IOException, MiddlewareQueryException, ParseException, ConfigException, URISyntaxException {
		
		new Editor();
		Editor.searchAllGermplasm((JSONObject)data);
		
		return Response.status(200).entity("OK!").build();
	}
	
	@Path("/show_germplasm_details")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response show_germplasm_details(JSONObject data) throws JSONException, FileNotFoundException,
			IOException, MiddlewareQueryException, ParseException, ConfigException, URISyntaxException {
		
		new Editor();
		Editor.show_germplasm_details((JSONObject)data);
		
		return Response.status(200).entity("OK!").build();
	}
}
