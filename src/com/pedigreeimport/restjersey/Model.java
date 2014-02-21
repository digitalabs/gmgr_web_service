package com.pedigreeimport.restjersey;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateUtil;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.util.Debug;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.pedigreeimport.backend.*;
import com.pedigreeimport.restjersey.Config;

@Path("/term")
public class Model {

	private static GermplasmDataManager manager;
	private static HibernateUtil hibernateUtil;
	private static int counter = 0;
	private int cnt;
	private static List<Integer> counterArray = new ArrayList<Integer>();
	private String outputString = "";

	@Path("/welcome")
	@GET
	@Produces("text/html")
	public Response welcome() {

		return Response.status(200).entity("Genealogy Manager")
		.build();
	}
	@Path("/createNew")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response CreateNew(JSONObject data) throws FileNotFoundException, IOException,
	MiddlewareQueryException, ParseException, InterruptedException {		


		//System.out.println("HERE!");
		JSONObject output=new JSONObject();

		JSONObject json_array= (JSONObject)data;
		List<String> db_details = new ArrayList<String>();

		db_details.clear();

		//list = (List<String>) json_array.get("output");
		String local_db_name = (String) json_array.get("local_db_name");
		String local_db_port = (String) json_array.get("local_db_port");
		String local_db_username = (String) json_array.get("local_db_username");

		String central_db_name = (String) json_array.get("central_db_name");
		String central_db_port = (String) json_array.get("central_db_port");
		String central_db_username = (String) json_array.get("central_db_username");

		//add db details to array
		db_details.add(local_db_name);
		db_details.add(local_db_port);
		db_details.add(local_db_username);
		db_details.add(central_db_name);
		db_details.add(central_db_port);
		db_details.add(central_db_username);

		ManagerFactory factory = new Config().configDB(db_details);
		//output=test.chooseGID(data,factory);
		new AssignGID();
		//ManagerFactory factory = new Config().configDB();
		output=AssignGID.createNew(data,factory);
		System.out.println("existingTerm: "+output.get("existingTerm"));
		factory.close();
		return Response.status(200).entity(output).build();

	}


	/*@Path("/updateMethod")
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
		data=new TestAssign().updateMethod(manager, createdGID, mid, gid, id);
		manager=null;
		createdGID.clear();
		factory.close();
		return Response.status(200).entity(data).build();
	}*/

	@Path("/chooseGID2")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response chooseGID2(JSONObject data) throws FileNotFoundException, IOException,
	MiddlewareQueryException, ParseException, InterruptedException {		

		new AssignGID();
		//System.out.println("HERE!");
		JSONObject output=new JSONObject();
		JSONObject json_array= (JSONObject)data;
		List<String> db_details = new ArrayList<String>();

		db_details.clear();

		//list = (List<String>) json_array.get("output");
		String local_db_name = (String) json_array.get("local_db_name");
		String local_db_port = (String) json_array.get("local_db_port");
		String local_db_username = (String) json_array.get("local_db_username");

		String central_db_name = (String) json_array.get("central_db_name");
		String central_db_port = (String) json_array.get("central_db_port");
		String central_db_username = (String) json_array.get("central_db_username");

		//add db details to array
		db_details.add(local_db_name);
		db_details.add(local_db_port);
		db_details.add(local_db_username);
		db_details.add(central_db_name);
		db_details.add(central_db_port);
		db_details.add(central_db_username);

		ManagerFactory factory = new Config().configDB(db_details);
		output=AssignGID.chooseGID(data,factory);
		factory.close();
		return Response.status(200).entity(output).build();

	}
	@Path("/chooseGID_cross")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response chooseGID_cross(JSONObject data) throws FileNotFoundException, IOException,
	MiddlewareQueryException, ParseException, InterruptedException {		

		new AssignGID();
		//System.out.println("HERE!");
		JSONObject output=new JSONObject();

		JSONObject json_array= (JSONObject)data;
		List<String> db_details = new ArrayList<String>();

		db_details.clear();

		//list = (List<String>) json_array.get("output");
		String local_db_name = (String) json_array.get("local_db_name");
		String local_db_port = (String) json_array.get("local_db_port");
		String local_db_username = (String) json_array.get("local_db_username");

		String central_db_name = (String) json_array.get("central_db_name");
		String central_db_port = (String) json_array.get("central_db_port");
		String central_db_username = (String) json_array.get("central_db_username");

		//add db details to array
		db_details.add(local_db_name);
		db_details.add(local_db_port);
		db_details.add(local_db_username);
		db_details.add(central_db_name);
		db_details.add(central_db_port);
		db_details.add(central_db_username);

		ManagerFactory factory = new Config().configDB(db_details);

		output=AssignGid.chooseGID_cross(data,factory);

		factory.close();
		return Response.status(200).entity(output).build();

	}
	@Path("/createGID3")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createGID3(JSONObject data) throws FileNotFoundException, IOException,
	MiddlewareQueryException, ParseException, InterruptedException, NumberFormatException, java.text.ParseException {
		new AssignGID();
		//new AssignGID().createGID();
		//print_checkedBox();
		List<String> checked= new ArrayList<String>();
		List<List<String>> list= new ArrayList<List<String>>();
		List<List<String>> createdGID= new ArrayList<List<String>>();
		List<List<String>> existingTerm= new ArrayList<List<String>>();
		List<String> db_details = new ArrayList<String>();

		db_details.clear();

		JSONObject json_array = (JSONObject) data;
		String locationID = (String) json_array.get("locationID");
		checked= (List<String>) json_array.get("checked");
		list = (List<List<String>>) json_array.get("list");
		createdGID = (List<List<String>>) json_array.get("createdGID");
		existingTerm = (List<List<String>>) json_array.get("existing");
		String userID = (String) json_array.get("userID");

		String local_db_name = (String) json_array.get("local_db_name");
		String local_db_port = (String) json_array.get("local_db_port");
		String local_db_username = (String) json_array.get("local_db_username");

		String central_db_name = (String) json_array.get("central_db_name");
		String central_db_port = (String) json_array.get("central_db_port");
		String central_db_username = (String) json_array.get("central_db_username");

		//add db details to array
		db_details.add(local_db_name);
		db_details.add(local_db_port);
		db_details.add(local_db_username);
		db_details.add(central_db_name);
		db_details.add(central_db_port);
		db_details.add(central_db_username);

		//System.out.println("\t createdGID @ Model: "+createdGID.size()+"\t"+createdGID);
		//System.out.println("\t existing: \t"+existingTerm);
		//System.out.println("\t list: "+list.size()+"\t"+list);
		//System.out.println("\t checked: "+checked.size()+"\t"+checked);
		//System.out.println("\t locationID: \t"+locationID);
		//System.out.println("\t userID: \t"+userID);

		JSONObject output=new JSONObject();
		//System.out.println();

		ManagerFactory factory = new Config().configDB(db_details);
		output=AssignGID.bulk_createGID2(createdGID,list, checked,Integer.parseInt(locationID),existingTerm, userID,factory);

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
	MiddlewareQueryException, ParseException, InterruptedException, NumberFormatException, java.text.ParseException {
		new AssignGID();
		//new AssignGID().createGID();
		//print_checkedBox();
		List<String> checked= new ArrayList<String>();
		List<List<String>> list= new ArrayList<List<String>>();
		List<List<String>> existingTerm= new ArrayList<List<String>>();
		List<String> db_details = new ArrayList<String>();


		JSONObject json_array = (JSONObject) data;
		String locationID = (String) json_array.get("locationID");
		checked= (List<String>) json_array.get("checked");
		list = (List<List<String>>) json_array.get("list");
		existingTerm = (List<List<String>>) json_array.get("existingTerm");
		String userID = (String) json_array.get("userID");

		String local_db_name = (String) json_array.get("local_db_name");
		String local_db_port = (String) json_array.get("local_db_port");
		String local_db_username = (String) json_array.get("local_db_username");

		String central_db_name = (String) json_array.get("central_db_name");
		String central_db_port = (String) json_array.get("central_db_port");
		String central_db_username = (String) json_array.get("central_db_username");

		//add db details to array
		db_details.add(local_db_name);
		db_details.add(local_db_port);
		db_details.add(local_db_username);
		db_details.add(central_db_name);
		db_details.add(central_db_port);
		db_details.add(central_db_username);


		JSONObject output=new JSONObject();
		//System.out.println();
		ManagerFactory factory = new Config().configDB(db_details);
		output=AssignGID.bulk_createGID(list, checked,Integer.parseInt(locationID),existingTerm, userID,factory);
		db_details.clear();
		factory.close();

		////System.out.println("list: "+  json_array.get("list"));
		System.out.println("createdGID: "+ output.get("createdGID"));
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
		String error ="";
		JSONObject parse= new JSONObject();
		List<String> correctedList= new ArrayList<String>();
		if(newName.contains("/")||newName.contains("*")){
			parse=new CrossOp().main(newName.toString(), false);	// not to standardize the parent
			JSONObject parse_array = (JSONObject) parse;
			correctedList= new ArrayList<String>();
			correctedList=(List<String>) parse_array.get("correctedList");
			//System.out.println("***ERROR: "+ error);
			System.out.println("***JSON STRING: "+ parse_array.toJSONString());
			System.out.println("\n------ @ MODEL.java");
			for(int l=0; l<correctedList.size();l++){
				System.out.println("::"+correctedList.get(l));
			}
			System.out.println("------");
			error= (String) parse_array.get("error");


		}else{
			error = new Main().checkString(newName);
		}

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
					//String[] tokens = new Tokenize().tokenize(newName);
					//String gid = new Tokenize().stringTokens(tokens);
					String gid="";

					if(newName.contains("/")||newName.contains("*")){
						System.out.println("tokens: "+ gid);
						for(int n=1; n<correctedList.size(); n++){
							gid =  gid + "#"+ correctedList.get(n);

							System.out.println("tokens: "+ gid);
						}
						System.out.println("tokens: "+ gid);
					}else{

						String[] tokens = new Tokenize().tokenize(newName);
						gid = new Tokenize().stringTokens(tokens);

					}


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

					//String[] tokens = new Tokenize().tokenize(newName);
					//String gid = new Tokenize().stringTokens(tokens);
					String gid="";
					if(newName.contains("/")||newName.contains("*")){
						System.out.println("tokens: "+ gid);
						for(int n=1; n<correctedList.size(); n++){
							gid =  gid + "#"+ correctedList.get(n);

							//System.out.println("tokens: "+ gid);
						}
						//System.out.println("tokens: "+ gid);
					}else{

						String[] tokens = new Tokenize().tokenize(newName);
						gid = new Tokenize().stringTokens(tokens);

					}

					row_output.add(gid);
					row_output.add(newName);

					////System.out.println(" \t New:: "+ row.get(6));

					newString.add("in standardized format");
					newString.add(gid);
					row_output.add(row.get(10));
				}else{
					row_output.add(row.get(6));
					row_output.add(row.get(7));
					row_output.add(row.get(8));
					row_output.add(row.get(9));
					row_output.add(row.get(10));
				}

				output.add(row_output);
			}
			data_output.put("list", output);
			data_output.put("new", newName);
			data_output.put("old", old);
			data_output.put("updated", true);


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
		System.out.println(""+newString);

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
		String correctedTerm, error, gid="";
		List<String> row_object=new ArrayList<String>();
		List<String> row= new ArrayList<String>();
		List<String> correctedList= new ArrayList<String>();
		String line="";

		System.out.println("*******S T A R T I N G Standardization");
		for (int m = 0; m < object.size();m++) {

			row_object= object.get(m);
			row= new ArrayList<String>();

			row.add("N/A");		//0
			row.add(row_object.get(1));		//1
			//System.out.print("N/A,");
			//System.out.print(row_object.get(1) + ",");


			for (int i = 2; i < row_object.size(); i++) {
				//for (int j = 1; j < 3; j++) {
				if(i==5 || i==9){


					// object.get(count);
					// count++;
					correctedTerm = row_object.get(i).toString();
					System.out.println(""+correctedTerm);
					row.add(""+k);
					//System.out.print(k+ ",");
					k++;
					JSONObject parse=new JSONObject();

					if(row_object.get(i).toString().contains("/")||row_object.get(i).toString().contains("*")){
						parse=new CrossOp().main(row_object.get(i).toString(),true);	// false= not to standardize
						JSONObject parse_array = (JSONObject) parse;

						correctedList=(List<String>) parse_array.get("correctedList");
						error= (String) parse_array.get("error");
						correctedTerm =correctedList.get(0);

					}else{
						error = new Main().checkString(row_object.get(i).toString());
					}

					if (error.equals("")) {
						// //System.out.print("in standardized format");
						row.add("in standardized format"); // remarks

						if(row_object.get(i).toString().contains("/")||row_object.get(i).toString().contains("*")){
							//System.out.println("tokens: "+ gid);

							for(int n=1; n<correctedList.size(); n++){
								gid =  gid + "#"+ correctedList.get(n);

								//System.out.println("tokens: "+ gid);
							}
							//System.out.println("tokens: "+ gid);
						}else{
							Pattern p = Pattern.compile("IR");
							Matcher m1 = p.matcher(line);

							if (m1.lookingAt()) {
								String[] tokens = new Tokenize().tokenize(row_object.get(i).toString());
								gid = new Tokenize().stringTokens(tokens);
							}else{
								gid="";
							}

						}

						row.add(gid); // GID
						//System.out.print(gid); // GID

					} else {
						if(row_object.get(i).toString().contains("/")||row_object.get(i).toString().contains("*")){
							correctedTerm=correctedList.get(0);
							error=(String) parse.get("error");

						}else{
							correctedTerm = new FixString().checkString(correctedTerm);

							error = new Main().checkString(correctedTerm);
						}
						//System.out.print("ERROR:" + error + "|"); // remarks
						if (error.equals("")) {
							//System.out.println("in standardized format,"); // remarks
							if(row_object.get(i).toString().contains("/")||row_object.get(i).toString().contains("*")){
								System.out.println("tokens: "+ gid);
								for(int n=1; n<correctedList.size(); n++){
									gid =  gid + "#"+ correctedList.get(n);

									System.out.println("tokens: "+ gid);
								}
								System.out.println("tokens: "+ gid);
								correctedTerm=correctedList.get(0);
							}else{
								row.add("in standardized format"); // remarks

								Pattern p = Pattern.compile("IR");
								Matcher m1 = p.matcher(correctedTerm);

								if (m1.lookingAt()) {
									String[] tokens = new Tokenize().tokenize(correctedTerm);
									gid = new Tokenize().stringTokens(tokens);
								}else{
									gid="";
								}
							}
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
					System.out.print("#####"+correctedTerm); // pedigree term
					//}
					// //System.out.print("count: "+count);
					gid="";
				}
				//System.out.println(""+row_object.get(i));
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
	public Response displayFile(JSONObject list) throws IOException, ParseException, MiddlewareQueryException {

		JSONObject json_array = (JSONObject) list;

		List<String> gu_obj=(List<String>) json_array.get("list");
		System.out.println("list: "+ gu_obj);

		gu_obj=new SortList().algo(gu_obj);

		List<List<String>> output = new ArrayList<List<String>>();
		int k=0;
		String line = "";
		JSONObject parse;
		List<String> correctedList = new ArrayList<String>();;
		System.out.println("size:"+gu_obj.size());
		for (int j =0; j< gu_obj.size();){
			List<String> row= new ArrayList<String>();
			String error, gid = "";

			row.add("N/A");
			row.add(gu_obj.get(j));	// Cross name

			////System.out.print("[0] N/A,");
			//System.out.print(" "+gu_obj.get(j) + ",");

			// //System.out.print("NULL ,");
			//System.out.println(j+" :: "+gu_obj.get(j).toString());

			for (int i = 1; i <= 2; i++) {
				j++;
				if(j< gu_obj.size()){

					//System.out.println(j+" :: "+gu_obj.get(j).toString());
					// //System.out.print(gu_obj.get(j).toString() + "\t");


					System.out.print("[2] "+gu_obj.get(j).toString() + ","); // pedigree term
					parse=new JSONObject();

					if(gu_obj.get(j).toString().contains("/")||gu_obj.get(j).toString().contains("*")){
						parse=new CrossOp().main(gu_obj.get(j).toString(), false);	// not to standardize the parent
						JSONObject parse_array = (JSONObject) parse;
						correctedList= new ArrayList<String>();
						correctedList=(List<String>) parse_array.get("correctedList");
						//System.out.println("***ERROR: "+ error);
						System.out.println("***JSON STRING: "+ parse_array.toJSONString());
						System.out.println("\n------ @ MODEL.java");
						for(int l=0; l<correctedList.size();l++){
							System.out.println("::"+correctedList.get(l));
						}
						System.out.println("------");
						error= (String) parse_array.get("error");


					}else{
						error = new Main().checkString(gu_obj.get(j).toString());
					}
					row.add(""+k);	//id
					////System.out.print("[3] "+k + ",");
					////System.out.print("[ERROR] "+error + ",");

					k++;
					if (error.equals("")) {

						////System.out.print("[4] "+"in standardized format,");
						row.add("in standardized format"); // remarks
						// //System.out.print(" in standardized format,");
						// //remarks

						if(gu_obj.get(j).toString().contains("/")||gu_obj.get(j).toString().contains("*")){


							System.out.println("tokens: "+ gid);
							for(int n=1; n<correctedList.size(); n++){
								gid =  gid + "#"+ correctedList.get(n);

								System.out.println("tokens: "+ gid);
							}
							System.out.println("tokens: "+ gid);
						}else{
							Pattern p = Pattern.compile("IR");
							Matcher m = p.matcher(gu_obj.get(j));

							if (m.lookingAt()) {
								String[] tokens = new Tokenize().tokenize(gu_obj.get(j)
										.toString());
								gid = new Tokenize().stringTokens(tokens);
							}else{
								gid="";
							}

						}

						row.add( gid  ); // GID

					} else {
						////System.out.print("\"" + error + "\"" + ","); // remarks
						row.add(error); // remarks
						row.add("N/A"); // GID
						////System.out.print("N/A,");
					}
					gid="";
					System.out.println("#####"+gu_obj.get(j).toString());
					row.add(gu_obj.get(j).toString()); // pedigree term

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

			////System.out.println("\n \n **output: "+output);
		}
		//System.out.println("output: "+output);
		////System.out.println("output size: "+output.size());

		return Response.status(201).entity(output).build();

	}

	@Path("/searchGID")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchGID(JSONObject data) throws JSONException, FileNotFoundException,
	IOException, MiddlewareQueryException, ParseException, ConfigException, URISyntaxException {

		List<String> db_details = new ArrayList<String>();
		JSONObject json_array = (JSONObject) data;

		String local_db_name = (String) json_array.get("local_db_name");
		String local_db_port = (String) json_array.get("local_db_port");
		String local_db_username = (String) json_array.get("local_db_username");

		String central_db_name = (String) json_array.get("central_db_name");
		String central_db_port = (String) json_array.get("central_db_port");
		String central_db_username = (String) json_array.get("central_db_username");

		//add db details to array
		db_details.add(local_db_name);
		db_details.add(local_db_port);
		db_details.add(local_db_username);
		db_details.add(central_db_name);
		db_details.add(central_db_port);
		db_details.add(central_db_username);

		JSONObject output=new JSONObject();
		//System.out.println();

		ManagerFactory factory = new Config().configDB(db_details);
		GermplasmDataManager man = factory.getGermplasmDataManager();
		List<Name> names = new ArrayList<Name>();//null;//man.getNamesByGID(node.getGermplasm().getGid(), null, null);
		Location loc2 = new Location();
		List<Attribute> attributes = new ArrayList<Attribute>();//man.getAttributesByGID(node.getGermplasm().getGid());
		Method meth = new Method();//man.getMethodByID(node.getGermplasm().getMethodId());
		Location loc = new Location();//man.getLocationByID(node.getGermplasm().getLocationId());
		Bibref bibref = new Bibref();//man.getBibliographicReferenceByID(node.getGermplasm().getReferenceId());

		Integer cid = 0;//loc.getCntryid();
		Country cnty = new Country();//man.getCountryById(cid);

		//JSONObject json_array = (JSONObject) data;
		String gid = (String) json_array.get("GID");
		String level = (String) json_array.get("LEVEL");
		String sel = (String) json_array.get("SEL");
		JSONObject outputTree = new JSONObject();

		//ManagerFactory factory = new Config().configDB();
		System.out.println("gid: "+gid);
		System.out.println("level: "+level);
		System.out.println("sel: "+sel);
		cnt = counter++;
		Boolean bool;
		if(Integer.parseInt(sel)==1)
		{
			bool = true;
		}
		else
			bool = false;

		if(cnt % 2 == 1)
		{

			PedigreeDataManager pedigreeManager = factory.getPedigreeDataManager();

			Debug.println(10, "GID = " + Integer.parseInt(gid) + ", level = " + Integer.parseInt(level) +  ":");
			GermplasmPedigreeTree tree = pedigreeManager.generatePedigreeTree(Integer.parseInt(gid),Integer.parseInt(level), bool);

			for(int i=0;i<Integer.parseInt(level);i++)
			{
				counterArray.add(0);
			}

			if (tree != null) 
			{
				outputString = outputString +"{";
				System.out.println("{");
				printNode(tree.getRoot(), 1,names,loc2,attributes,cid,cnty,man,meth,bibref,loc);
				outputString = outputString +"\n}";
				System.out.println("}");
			}

			tree = null;
			pedigreeManager = null;

		}
		String found = "0";
		if(outputString.equals(""))
		{
			found = "1";
		}

		outputTree.put("tree", outputString);
		outputTree.put("found", found);

		System.out.println("Output: " + outputTree.get("tree"));

		counterArray.clear();
		names = null;
		loc2 = null;
		attributes = null;
		cid = null;
		cnty = null;
		meth = null;
		bibref = null;
		loc= null;

		factory.close();
		return Response.status(200).entity(outputTree).build();
	}

	private void printNode(GermplasmPedigreeTreeNode node, int level, List <Name> names, Location loc2, List <Attribute> attributes, int cid, Country cnty, GermplasmDataManager man, Method meth, Bibref bibref, Location loc) throws IOException, MiddlewareQueryException {

		StringBuffer tabs = new StringBuffer();
		String descBibref = "N/A";

		meth = man.getMethodByID(node.getGermplasm().getMethodId());
		loc = man.getLocationByID(node.getGermplasm().getLocationId());

		if(node.getGermplasm().getReferenceId()!=0){
			bibref = man.getBibliographicReferenceByID(node.getGermplasm().getReferenceId());
			descBibref = bibref.getAnalyt();
		}

		names = man.getNamesByGID(node.getGermplasm().getGid(), null, null);
		loc2 = null;
		attributes = man.getAttributesByGID(node.getGermplasm().getGid());

		cid = loc.getCntryid();
		cnty = man.getCountryById(cid);
<<<<<<< HEAD

		for (int ctr = 1; ctr < level; ctr++) {
			tabs.append("\t");
		}

		counterArray.set(level-1, 0);
		String name = node.getGermplasm().getPreferredName() != null ? node.getGermplasm().getPreferredName().getNval() : null;

		if(!node.getLinkedNodes().isEmpty()){
			Debug.println(0, tabs.toString() +"\"gid\" : \""+ node.getGermplasm().getGid() + "\",\n" + tabs.toString()+ "\"name\" : \""+ name +"\",\n" +tabs.toString()+ "\"layer\": \""+ (level-1) + "\",");

			outputString = outputString + "\n "+ tabs.toString() + tabs.toString() +"\"gid\" : \"" + node.getGermplasm().getGid() +"\",\n"
			+ tabs.toString() +"    \"name\" : \"" + name +"\",\n"
			+ tabs.toString() +"    \"date\" : \"" + node.getGermplasm().getGdate() +"\",\n"
			+ tabs.toString() +"    \"id\" : \"" + node.getGermplasm().getGid() +"\",\n"
			+ tabs.toString() +"    \"layer\" : \"" + (level-1) +"\",\n"
			+ tabs.toString() +"    \"methodname\" : \"" + meth.getMname() +"\",\n"
			+ tabs.toString() +"    \"methodtype\" : \"" + meth.getMtype() +"\",\n"
			+ tabs.toString() +"    \"location\" : \"" + loc.getLname() +"\",\n"
			+ tabs.toString() +"    \"country\" : \"" + cnty.getIsoabbr() +"\",\n"
			+ tabs.toString() +"    \"ref\" : \"" + descBibref +"\",\n";

			if(meth.getMtype().equals("GEN"))
			{
				outputString = outputString + tabs.toString() +"    \"warning\" : \"" + "true" +"\",\n";
			}

			for(int cntr=0;cntr<names.size();cntr++)
			{
				loc2 = man.getLocationByID(names.get(cntr).getLocationId());	
				outputString = outputString 
				+ tabs.toString() +"    \"dates"+cntr+"\" : \"" + names.get(cntr).getNdate() +"\",\n"
				+ tabs.toString() +"    \"name"+cntr+"\" : \"" + names.get(cntr).getNval() +"\",\n"
				+ tabs.toString() +"    \"ntype"+cntr+"\" : \"" + names.get(cntr).getTypeId() +"\",\n"
				+ tabs.toString() +"    \"nstat"+cntr+"\" : \"" + names.get(cntr).getNstat() +"\",\n"
				+ tabs.toString() +"    \"loc"+cntr+"\" : \"" + loc2.getLname() +"\",\n";

			}

			for(int cntr=0;cntr<attributes.size();cntr++)
			{
				loc2 = man.getLocationByID(attributes.get(cntr).getLocationId());	
				outputString = outputString 
				+ tabs.toString() +"    \"aid"+cntr+"\" : \"" + attributes.get(cntr).getAid() +"\",\n"
				+ tabs.toString() +"    \"atype"+cntr+"\" : \"" + attributes.get(cntr).getTypeId() +"\",\n"
				+ tabs.toString() +"    \"aval"+cntr+"\" : \"" + attributes.get(cntr).getAval() +"\",\n"
				+ tabs.toString() +"    \"aloc"+cntr+"\" : \"" + loc2.getLname() +"\",\n"
				+ tabs.toString() +"    \"adate"+cntr+"\" : \"" + attributes.get(cntr).getAdate() +"\",\n";

			}


			outputString = outputString + tabs.toString() +"    \"gpid1\" : \"" + node.getGermplasm().getGpid1() +"\",\n";
			outputString = outputString + tabs.toString() +"    \""+"gpid2"+"\""+": \"" + node.getGermplasm().getGpid2() + "\",\n";
		}

		else{

			Debug.println(0, tabs.toString() +"\"gid\" : \""+ node.getGermplasm().getGid() + "\",\n" + tabs.toString()+ "\"name\" : \""+ name +"\",\n" +tabs.toString()+ "\"layer\": \""+ (level-1) + "\"");

			outputString = outputString + "\n "+ tabs.toString() + tabs.toString() + "\"gid\" : \"" + node.getGermplasm().getGid() +"\",\n"
			+ tabs.toString() +"    \"name\" : \"" + name +"\",\n"
			+ tabs.toString() +"    \"date\" : \"" + node.getGermplasm().getGdate() +"\",\n"
			+ tabs.toString() +"    \"id\" : \"" + node.getGermplasm().getGid() +"\",\n"
			+ tabs.toString() +"    \"layer\" : \"" + (level-1) +"\",\n"
			+ tabs.toString() +"    \"methodname\" : \"" + meth.getMname() +"\",\n"
			+ tabs.toString() +"    \"methodtype\" : \"" + meth.getMtype() +"\",\n"
			+ tabs.toString() +"    \"location\" : \"" + loc.getLname() +"\",\n"
			+ tabs.toString() +"    \"country\" : \"" + cnty.getIsoabbr() +"\",\n"
			+ tabs.toString() +"    \"ref\" : \"" + descBibref +"\",\n";

			if(meth.getMtype().equals("GEN"))
			{
				outputString = outputString + tabs.toString() +"    \"warning\" : \"" + "true" +"\",\n";
			}

			for(int cntr=0;cntr<names.size();cntr++)
			{
				loc2 = man.getLocationByID(names.get(cntr).getLocationId());	
				outputString = outputString 
				+ tabs.toString() +"    \"dates"+cntr+"\" : \"" + names.get(cntr).getNdate() +"\",\n"
				+ tabs.toString() +"    \"name"+cntr+"\" : \"" + names.get(cntr).getNval() +"\",\n"
				+ tabs.toString() +"    \"ntype"+cntr+"\" : \"" + names.get(cntr).getTypeId() +"\",\n"
				+ tabs.toString() +"    \"nstat"+cntr+"\" : \"" + names.get(cntr).getNstat() +"\",\n"
				+ tabs.toString() +"    \"loc"+cntr+"\" : \"" + loc2.getLname() +"\",\n";

			}
			outputString = outputString + tabs.toString() +"    \"gpid1\" : \"" + node.getGermplasm().getGpid1() +"\",\n";
			outputString = outputString + tabs.toString() +"    \""+"gpid2"+"\""+": \"" + node.getGermplasm().getGpid2() + "\"\n";
		}

		if(!node.getLinkedNodes().isEmpty()){
			outputString = outputString + tabs.toString() + "\"children\" : \n" + tabs.toString() +"[";
			System.out.println(tabs.toString() + "\"children\" : \n" + tabs.toString() +"[");
		}

		for (GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {

			counterArray.set(level-1,counterArray.get(level-1)+1);

			System.out.println(tabs.toString()+"{");
			outputString = outputString + "\n" + tabs.toString()+ "{" ;
			outputString = outputString + "\n";
			printNode(parent,level+1,names,loc2,attributes,cid,cnty,man,meth,bibref,loc);
			outputString = outputString + "\n" + tabs.toString()+ "}";
			System.out.println(tabs.toString()+"}");

			if(counterArray.get(level-1) < node.getLinkedNodes().size() ) 
			{
				outputString = outputString + tabs.toString()+ ","; //check if no sibling
				System.out.println(tabs.toString()+",");
			}

		}

		if(!node.getLinkedNodes().isEmpty()){
			outputString = outputString + "\n" + tabs.toString()+ "]";
			System.out.println(tabs.toString()+"]");
		}
	}
		 
        for (int ctr = 1; ctr < level; ctr++) {
            tabs.append("\t");
        }
        
        counterArray.set(level-1, 0);
        String name = node.getGermplasm().getPreferredName() != null ? node.getGermplasm().getPreferredName().getNval() : null;
        
        if(!node.getLinkedNodes().isEmpty()){
        	Debug.println(0, tabs.toString() +"\"gid\" : \""+ node.getGermplasm().getGid() + "\",\n" + tabs.toString()+ "\"name\" : \""+ name +"\",\n" +tabs.toString()+ "\"layer\": \""+ (level-1) + "\",");
        	
        	outputString = outputString + "\n "+ tabs.toString() + tabs.toString() +"\"gid\" : \"" + node.getGermplasm().getGid() +"\",\n"
                    + tabs.toString() +"    \"name\" : \"" + name +"\",\n"
                    + tabs.toString() +"    \"date\" : \"" + node.getGermplasm().getGdate() +"\",\n"
                    + tabs.toString() +"    \"id\" : \"" + node.getGermplasm().getGid() +"\",\n"
                    + tabs.toString() +"    \"layer\" : \"" + (level-1) +"\",\n"
                    + tabs.toString() +"    \"methodname\" : \"" + meth.getMname() +"\",\n"
                    + tabs.toString() +"    \"methodtype\" : \"" + meth.getMtype() +"\",\n"
                    + tabs.toString() +"    \"location\" : \"" + loc.getLname() +"\",\n"
                    + tabs.toString() +"    \"country\" : \"" + cnty.getIsoabbr() +"\",\n"
                    + tabs.toString() +"    \"ref\" : \"" + descBibref +"\",\n";
        		
        		if(meth.getMtype().equals("GEN"))
        		{
        			outputString = outputString + tabs.toString() +"    \"warning\" : \"" + "true" +"\",\n";
        		}
            
            for(int cntr=0;cntr<names.size();cntr++)
     		{
     			  loc2 = man.getLocationByID(names.get(cntr).getLocationId());
     			  UserDefinedField result = null;
     			  String res = "---";
     			  System.out.println("Nametype: " + names.get(cntr).getTypeId());
     			  if(names.get(cntr).getTypeId()!= 0)
     			  {
     				  result = man.getUserDefinedFieldByID(names.get(cntr).getTypeId());
     				  res = result.getFname();
     			  }
     			  
                  outputString = outputString 
                  + tabs.toString() +"    \"dates"+cntr+"\" : \"" + names.get(cntr).getNdate() +"\",\n"
                  + tabs.toString() +"    \"name"+cntr+"\" : \"" + names.get(cntr).getNval() +"\",\n"
                  + tabs.toString() +"    \"ntype"+cntr+"\" : \"" + res +"\",\n"
                  + tabs.toString() +"    \"nstat"+cntr+"\" : \"" + names.get(cntr).getNstat() +"\",\n"
                  + tabs.toString() +"    \"loc"+cntr+"\" : \"" + loc2.getLname() +"\",\n";
                 
    		}
            
            for(int cntr=0;cntr<attributes.size();cntr++)
     		{
     			  loc2 = man.getLocationByID(attributes.get(cntr).getLocationId());	
     			  UserDefinedField result = null;
     			  String res = "---";
     			  String des = "---";
     			  System.out.println("Atype: " + attributes.get(cntr).getAid());
     			  if(attributes.get(cntr).getAid()!= 0)
     			  {
     				  result = man.getUserDefinedFieldByID(attributes.get(cntr).getAid());
     				  if(result!=null){
     					  res = result.getFname();
     					  des = result.getFdesc();
     				  }
     			  }
                  outputString = outputString 
                  + tabs.toString() +"    \"aid"+cntr+"\" : \"" + attributes.get(cntr).getAid() +"\",\n"
                  + tabs.toString() +"    \"atype"+cntr+"\" : \"" + attributes.get(cntr).getTypeId() +"\",\n"
                  + tabs.toString() +"    \"aval"+cntr+"\" : \"" + attributes.get(cntr).getAval() +"\",\n"
                  + tabs.toString() +"    \"aloc"+cntr+"\" : \"" + loc2.getLname() +"\",\n"
                  + tabs.toString() +"    \"aname"+cntr+"\" : \"" + res +"\",\n"
                  + tabs.toString() +"    \"ades"+cntr+"\" : \"" + des +"\",\n"
                  + tabs.toString() +"    \"adate"+cntr+"\" : \"" + attributes.get(cntr).getAdate() +"\",\n";
                 
    		}
            
     		outputString = outputString + tabs.toString() +"    \"gpid1\" : \"" + node.getGermplasm().getGpid1() +"\",\n";
     		outputString = outputString + tabs.toString() +"    \""+"gpid2"+"\""+": \"" + node.getGermplasm().getGpid2() + "\",\n";
        }
        	
        else{
        
        	Debug.println(0, tabs.toString() +"\"gid\" : \""+ node.getGermplasm().getGid() + "\",\n" + tabs.toString()+ "\"name\" : \""+ name +"\",\n" +tabs.toString()+ "\"layer\": \""+ (level-1) + "\"");
        
        	outputString = outputString + "\n "+ tabs.toString() + tabs.toString() + "\"gid\" : \"" + node.getGermplasm().getGid() +"\",\n"
                    + tabs.toString() +"    \"name\" : \"" + name +"\",\n"
                    + tabs.toString() +"    \"date\" : \"" + node.getGermplasm().getGdate() +"\",\n"
                    + tabs.toString() +"    \"id\" : \"" + node.getGermplasm().getGid() +"\",\n"
                    + tabs.toString() +"    \"layer\" : \"" + (level-1) +"\",\n"
                    + tabs.toString() +"    \"methodname\" : \"" + meth.getMname() +"\",\n"
                    + tabs.toString() +"    \"methodtype\" : \"" + meth.getMtype() +"\",\n"
                    + tabs.toString() +"    \"location\" : \"" + loc.getLname() +"\",\n"
                    + tabs.toString() +"    \"country\" : \"" + cnty.getIsoabbr() +"\",\n"
                    + tabs.toString() +"    \"ref\" : \"" + descBibref +"\",\n";
            
        	if(meth.getMtype().equals("GEN"))
    		{
    			outputString = outputString + tabs.toString() +"    \"warning\" : \"" + "true" +"\",\n";
    		}
        	
            for(int cntr=0;cntr<names.size();cntr++)
     		{
     			  loc2 = man.getLocationByID(names.get(cntr).getLocationId());	
     			  UserDefinedField result = null;
    			  String res = "---";
    			  System.out.println("Nametype: " + names.get(cntr).getTypeId());
    			  if(names.get(cntr).getTypeId()!= 0)
    			  {
    				  result = man.getUserDefinedFieldByID(names.get(cntr).getTypeId());
    				  res = result.getFname();
    			  }
                  outputString = outputString 
                  + tabs.toString() +"    \"dates"+cntr+"\" : \"" + names.get(cntr).getNdate() +"\",\n"
                  + tabs.toString() +"    \"name"+cntr+"\" : \"" + names.get(cntr).getNval() +"\",\n"
                  + tabs.toString() +"    \"ntype"+cntr+"\" : \"" + res +"\",\n"
                  + tabs.toString() +"    \"nstat"+cntr+"\" : \"" + names.get(cntr).getNstat() +"\",\n"
                  + tabs.toString() +"    \"loc"+cntr+"\" : \"" + loc2.getLname() +"\",\n";
                 
    		}
            
            for(int cntr=0;cntr<attributes.size();cntr++)
     		{
     			  loc2 = man.getLocationByID(attributes.get(cntr).getLocationId());	
     			  UserDefinedField result = null;
     			  String res = "---";
     			  String des = "---";
     			  System.out.println("Atype: " + attributes.get(cntr).getAid());
     			  if(attributes.get(cntr).getAid()!= 0)
     			  {
     				  result = man.getUserDefinedFieldByID(attributes.get(cntr).getAid());
     				  if(result!=null){
     					  res = result.getFname();
     					  des = result.getFdesc();
     				  }
     			  }
                  outputString = outputString 
                  + tabs.toString() +"    \"aid"+cntr+"\" : \"" + attributes.get(cntr).getAid() +"\",\n"
                  + tabs.toString() +"    \"atype"+cntr+"\" : \"" + attributes.get(cntr).getTypeId() +"\",\n"
                  + tabs.toString() +"    \"aval"+cntr+"\" : \"" + attributes.get(cntr).getAval() +"\",\n"
                  + tabs.toString() +"    \"aloc"+cntr+"\" : \"" + loc2.getLname() +"\",\n"
                  + tabs.toString() +"    \"aname"+cntr+"\" : \"" + res +"\",\n"
                  + tabs.toString() +"    \"ades"+cntr+"\" : \"" + des +"\",\n"
                  + tabs.toString() +"    \"adate"+cntr+"\" : \"" + attributes.get(cntr).getAdate() +"\",\n";
                 
    		}
            
     		outputString = outputString + tabs.toString() +"    \"gpid1\" : \"" + node.getGermplasm().getGpid1() +"\",\n";
     		outputString = outputString + tabs.toString() +"    \""+"gpid2"+"\""+": \"" + node.getGermplasm().getGpid2() + "\"\n";
        }
        
        System.out.println(tabs.toString() + "size : " + node.getLinkedNodes().size());
        
		if(node.getLinkedNodes().size()==0 && (level-1) == 0)
        {
        	outputString = outputString + tabs.toString() + ",\"children\" : \n" + tabs.toString() +"[{}]";
        	System.out.println(tabs.toString() + "\"children\" : \n" + tabs.toString() +"[");
        }

        if(!node.getLinkedNodes().isEmpty()){
        	outputString = outputString + tabs.toString() + "\"children\" : \n" + tabs.toString() +"[";
        	System.out.println(tabs.toString() + "\"children\" : \n" + tabs.toString() +"[");
        }
        
        for (GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {
        	
        	counterArray.set(level-1,counterArray.get(level-1)+1);
        	
        	System.out.println(tabs.toString()+"{");
        	outputString = outputString + "\n" + tabs.toString()+ "{" ;
        	outputString = outputString + "\n";
        	printNode(parent,level+1,names,loc2,attributes,cid,cnty,man,meth,bibref,loc);
        	outputString = outputString + "\n" + tabs.toString()+ "}";
            System.out.println(tabs.toString()+"}");
           
            if(counterArray.get(level-1) < node.getLinkedNodes().size() ) 
            {
             		outputString = outputString + tabs.toString()+ ","; //check if no sibling
             		System.out.println(tabs.toString()+",");
            }
            
        }
        
        if(!node.getLinkedNodes().isEmpty()){
        	outputString = outputString + "\n" + tabs.toString()+ "]";
        	System.out.println(tabs.toString()+"]");
        }
    }


	@Path("/show_germplasm_details")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response show_germplasm_details(JSONObject data) throws JSONException, FileNotFoundException,
	IOException, MiddlewareQueryException, ParseException, ConfigException, URISyntaxException {

		//new Editor();
		//Editor.show_germplasm_details((JSONObject)data);
		return Response.status(200).entity("OK!").build();

	}

	@Path("/editGermplasm")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editGermplasm(JSONObject data) throws JSONException, FileNotFoundException,
	IOException, MiddlewareQueryException, ParseException, ConfigException, URISyntaxException {

		List<String> db_details = new ArrayList<String>();
		JSONObject json_array = (JSONObject) data;


		String local_db_name = (String) json_array.get("local_db_name");
		String local_db_port = (String) json_array.get("local_db_port");
		String local_db_username = (String) json_array.get("local_db_username");

		String central_db_name = (String) json_array.get("central_db_name");
		String central_db_port = (String) json_array.get("central_db_port");
		String central_db_username = (String) json_array.get("central_db_username");

		//add db details to array
		db_details.add(local_db_name);
		db_details.add(local_db_port);
		db_details.add(local_db_username);
		db_details.add(central_db_name);
		db_details.add(central_db_port);
		db_details.add(central_db_username);

		ManagerFactory factory = new Config().configDB(db_details);
		GermplasmDataManager man = factory.getGermplasmDataManager();
		Germplasm germplasm = null;


		//g.setGid(50534);
		Integer nameId = 50533; //Assumption: id=-1 exists
		Name name = man.getGermplasmNameByID(nameId); 
		String nameBefore = name.toString();
		//name.setLocationId(man.getLocationByID(9000).getLocid()); //Assumption: location with id=1 exists
		//man.updateGermplasmName(name);
		name.setNval("IR64-1kel");
		man.updateGermplasmName(name);
		Debug.println(0, "testUpdateGermplasmName(" + nameId + ") RESULTS: " 
				+ "\n\tBEFORE: " + nameBefore
				+ "\n\tAFTER: " + name.toString());

		System.out.println("Edit success!");

		return Response.status(200).entity("OK!").build();
	}
}
