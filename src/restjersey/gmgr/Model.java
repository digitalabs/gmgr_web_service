package restjersey.gmgr;

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
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.util.Debug;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import restjersey.gmgr.Config;

import backend.pedigreeimport.*;
import backend.pedigreeviewer.*;

/**Model is includes all entry points of the web service call
 * @author Nikki G. Carumba
 *
 */

@Path("/term")
public class Model {

	/**
	 * Displays String on web page
	 * 
	 * @return String "Genealogy Manager"
	 */
	@Path("/welcome")
	@GET
	@Produces("text/html")
	public Response welcome() {

		return Response.status(200).entity("Genealogy Manager").build();
	}

	/**
	 * Extract database configuration from the json Object and store it into a
	 * List<String>
	 * 
	 * @param db_details database configuration to be stored
	 * @param json_array object fetched from Apache
	 * @return db_details List<String>
	 */
	public List<String> getDbDetails(List<String> db_details,
			JSONObject json_array) {

		String local_db_host = (String) json_array.get("local_db_host");		// host
		String local_db_name = (String) json_array.get("local_db_name");	// local datase name
		String local_db_port = (String) json_array.get("local_db_port");	// local port	
		String local_db_username = (String) json_array.get("local_db_username");	// username of the local database
		String local_db_password = (String) json_array.get("local_db_password");	// password of the local database

		String central_db_host = (String) json_array.get("central_db_host");	// host of the central database
		String central_db_name = (String) json_array.get("central_db_name");	//central database name
		String central_db_port = (String) json_array.get("central_db_port");	// central port
		String central_db_username = (String) json_array
		.get("central_db_username");// username of the central database
		String central_db_password = (String) json_array	
		.get("central_db_password");	// password of the central database

		// add db details to array
		db_details.add(local_db_host);
		db_details.add(local_db_name);
		db_details.add(local_db_port);
		db_details.add(local_db_username);
		db_details.add(local_db_password);

		db_details.add(central_db_host);
		db_details.add(central_db_name);
		db_details.add(central_db_port);
		db_details.add(central_db_username);
		db_details.add(central_db_password);

		return db_details;
	}

	/**
	 * Entry point in creating New GID
	 * 
	 * @param data object fetched from apache server
	 * @return JSON object output that contains the Lists createdGID, list, and
	 *         existingTerm
	 * @throws IOException
	 * @throws MiddlewareQueryException
	 * @throws InterruptedException
	 */
	@Path("/createNew")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response CreateNew(JSONObject data) throws IOException,
	MiddlewareQueryException, InterruptedException {

		JSONObject output = new JSONObject();		// holds the output of the method
		List<String> db_details = new ArrayList<String>();

		JSONObject json_array = (JSONObject) data;
		
		db_details = getDbDetails(db_details, json_array); // get db configuraton

		ManagerFactory factory = new Config().configDB(db_details);	// connects to the database
		
		output = new AssignGid().createNew(data, factory);	// calls  the method in creating New GID

		db_details.clear();
		factory.close();
		return Response.status(200).entity(output).build();

	}

	/**
	 * Entry point in choosing a GID for the female or male parent JSON Object
	 * data will be passed to method 'chooseGID'.
	 * 
	 * @param data
	 * @return JSON object output that contains the Lists createdGID, list, and
	 *         existingTerm
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws MiddlewareQueryException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	@Path("/chooseGID2")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response chooseGID2(JSONObject data) throws IOException,
	MiddlewareQueryException, InterruptedException, ParseException {

		JSONObject output = new JSONObject();
		JSONObject json_array = (JSONObject) data;
		List<String> db_details = new ArrayList<String>();

		db_details = getDbDetails(db_details, json_array); // get db configuraton

		ManagerFactory factory = new Config().configDB(db_details);	// connects to database
		output = new AssignGid().chooseGID(data, factory);	// calls the method to assign the chosen GID to the pedigree line of the femlae/ male parent
		
		db_details.clear();
		factory.close();
		return Response.status(200).entity(output).build();

	}

	/**
	 * This method is for choosing a GID for the cross name JSON Object data
	 * will be passed to method 'chooseGID_cross'.
	 * 
	 * @param data object fetched from apache
	 * @return JSON object output that contains the Lists createdGID, list, and
	 *         existingTerm
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws MiddlewareQueryException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	@Path("/chooseGID_cross")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response chooseGID_cross(JSONObject data) throws IOException,
	MiddlewareQueryException, InterruptedException, ParseException {

		JSONObject output = new JSONObject();
		JSONObject json_array = (JSONObject) data;
		List<String> db_details = new ArrayList<String>();

		db_details = getDbDetails(db_details, json_array); // get db configuraton

		ManagerFactory factory = new Config().configDB(db_details);	// connects to database

		output = new AssignGid().chooseGID_cross(data, factory);	// calls method to assign the chosen GID to the pedigree line
		
		db_details.clear();
		factory.close();
		return Response.status(200).entity(output).build();

	}

	/**
	 * Entry point in assigning GID of unchecked rows from the list
	 * 
	 * @param data obejct feteched from apache
	 * @return output that contains the Lists createdGID,list and existingTerm
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws MiddlewareQueryException
	 * @throws ParseException
	 * @throws InterruptedException
	 * @throws NumberFormatException
	 * @throws java.text.ParseException
	 */
	@Path("/createGID3")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createGID3(JSONObject data) throws IOException,
	MiddlewareQueryException, ParseException, InterruptedException,
	java.text.ParseException {
		new AssignGid();

		List<String> checked = new ArrayList<String>();
		List<List<String>> list = new ArrayList<List<String>>();
		List<List<String>> createdGID = new ArrayList<List<String>>();
		List<List<String>> existingTerm = new ArrayList<List<String>>();
		List<String> db_details = new ArrayList<String>();
		JSONObject output = new JSONObject();

		JSONObject json_array = (JSONObject) data;
		
		// Start: extract Lists and user ID to the JSON Object
		String locationID = (String) json_array.get("locationID");
		checked = (List<String>) json_array.get("checked");
		list = (List<List<String>>) json_array.get("list");
		createdGID = (List<List<String>>) json_array.get("createdGID");
		existingTerm = (List<List<String>>) json_array.get("existing");
		String userID = (String) json_array.get("userID");
		// END extract Lists and user ID to the JSON Object

		db_details = getDbDetails(db_details, json_array); // get db configuraton

		ManagerFactory factory = new Config().configDB(db_details); // connects to database
		output = new AssignGid().bulk_createGID2(createdGID, list, checked,
				Integer.parseInt(locationID), existingTerm, userID, factory);	//calls method in searching the Germplasm Name in the database

		factory.close();

		return Response.status(200).entity(output).build();
	}

	/**
	 * Entry point in assigning of GID of checked rows
	 * 
	 * @param data object fetched from apache
	 * @return output that contains Lists of createdGID, existingTerm, and list
	 * @throws IOException
	 * @throws MiddlewareQueryException
	 * @throws ParseException
	 * @throws InterruptedException
	 * @throws java.text.ParseException
	 */
	@Path("/createGID2")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createGID2(JSONObject data) throws IOException,
	MiddlewareQueryException, ParseException, InterruptedException,
	java.text.ParseException {

		// new AssignGid().createGID();
		// print_checkedBox();
		List<String> checked = new ArrayList<String>();
		List<List<String>> list = new ArrayList<List<String>>();
		List<List<String>> existingTerm = new ArrayList<List<String>>();
		List<String> db_details = new ArrayList<String>();

		JSONObject json_array = (JSONObject) data;
		// START: extract Lists and user ID to the JSON Object
		String locationID = (String) json_array.get("locationID");
		checked = (List<String>) json_array.get("checked");
		list = (List<List<String>>) json_array.get("list");
		existingTerm = (List<List<String>>) json_array.get("existingTerm");
		String userID = (String) json_array.get("userID");
		// END: extract Lists and user ID to the JSON Object

		db_details = getDbDetails(db_details, json_array); // get db
		// configuraton

		JSONObject output = new JSONObject();
		ManagerFactory factory = new Config().configDB(db_details);	// connects to database
		output = new AssignGid().bulk_createGID(list, checked,
				Integer.parseInt(locationID), existingTerm, userID, factory);	//calls method in searching the Germplasm Name in the database
		db_details.clear();
		factory.close();

		return Response.status(200).entity(output).build();
	}

	/**
	 * Checks if the new germplasm name input is in standardized format and
	 * updates the list, if not in standard form, returns that the input
	 * germplasm name's pattern is unrecognized.
	 * 
	 * @param data object fetched from apache
	 * @return output that contains the old name, the new name, the list and a
	 *         boolean if it has been updated
	 * @throws MiddlewareQueryException
	 * @throws IOException
	 */
	@Path("/updateGermplasmName")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateGermplasmName(JSONObject data)
	throws MiddlewareQueryException, IOException {

		List<String> newString = new ArrayList<String>();	// contains the indices [0]GID  [1] new name 2. remarks 3. parsed new name (if correct)
		List<String> correctedList = new ArrayList<String>();	// holds the corrected parsed germplasm name
		JSONObject data_output = new JSONObject();	// holds the output of the method
		JSONObject parse_array = new JSONObject();	// holds the output of the method that checks germplasm names with cross operators if it conforms to the standard format or not
		String error = "";

		// start extracting the newname, old name, list 
		JSONObject json_array = (JSONObject) data;
		String newName = (String) json_array.get("new");
		String old = (String) json_array.get("old");
		
		List<List<String>> list = (List<List<String>>) json_array.get("list");
		// format of the list 
		//[0]GID, [1]nval, [2]fid, [3]fremarks, [4]fgid, [5]female, [6]mid, [7]mremarks, [8]mgid, [9]male, [10]crossdate
		// end extracting the newname, old name, list
		
		if (newName.contains("/") || newName.contains("*")) {	// if the input germplasm name has cross operators
			
			parse_array = new CrossOp().main(newName.toString(), true); // false= not to standardize the germplasm name, just return the remarks if it conforms to the standard format or not
			correctedList = (List<String>) parse_array.get("correctedList");
			error = (String) parse_array.get("error");

		} else {	// no cross operators
			error = new NomenclatureRules().checkString(newName);	
		}
		
		newString.add("N/A");	// GID of the cross
		newString.add(newName);	// new name of the germplasm name

		if (error.equals("")) {	// if the germplasm name conforms to the standard format
			
			// Start updating the list with the new name
			for (int i = 0; i < list.size(); i++) {	// loops through all rows in the list
				if (list.get(i).get(5).equals(old)) {	// if female equals the old name, update the female germplasm name

					String gid = "";

					if (newName.contains("/") || newName.contains("*")) { // if it has cross operators
						for (int n = 1; n < correctedList.size(); n++) {
							gid = gid + "#" + correctedList.get(n);
						}
						System.out.println("tokens: " + gid);
					} else {
						String[] tokens = new Tokenize().tokenize(newName);
						gid = new Tokenize().stringTokens(tokens);
					}

					//start update list
					list.get(i).set(3,"in standardized format");	//remarks
					list.get(i).set(4,gid);	// parsed germplasm name
					list.get(i).set(5,newName);	// female name
					// end update list
					
					newString.add("in standardized format");
					newString.add(gid);

				} 

				if (list.get(i).get(9).equals(old)) {

					String gid = "";
					if (newName.contains("/") || newName.contains("*")) {	// if male equals the old name, update the male germplasm name
						System.out.println("tokens: " + gid);
						for (int n = 1; n < correctedList.size(); n++) {
							gid = gid + "#" + correctedList.get(n);
						}
					} else {
						String[] tokens = new Tokenize().tokenize(newName);
						gid = new Tokenize().stringTokens(tokens);
					}
					
					// start update list
					list.get(i).set(7,"in standardized format");
					list.get(i).set(8,gid);
					list.get(i).set(9,newName);
					//end update list
					
					newString.add("in standardized format");
					newString.add(gid);
				}
			}
			//start storing to the output
			data_output.put("list", list);
			data_output.put("new", newName);
			data_output.put("old", old);
			data_output.put("updated", true);
			// end start storing to the output

		} else {	// if the germplasm name does not conform to the standard format it will return error remarks

			newString.add(error);	// remarks/errors on the newName
			newString.add("N/A");	// will not be parsed
			
			//start storing to the output
			data_output.put("list", list);
			data_output.put("new", newName);
			data_output.put("old", error);
			data_output.put("updated", false);
			data_output.put("newString", newString);
			//end storing to the output
		}
		
		parse_array.clear();
		return Response.status(200).entity(data_output).build();
	}

	/**
	 * Removes unexpected space(s) and add necessary space(s) in the germplasm name
	 * 
	 * @param list object that contains the entries of the uploaded list
	 * @return output the updated list with the assigned GID of the entries
	 * @throws MiddlewareQueryException
	 * @throws IOException
	 */
	@Path("/standardize2")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response standardize(JSONObject list)
	throws MiddlewareQueryException, IOException {
		
		JSONObject json_array = (JSONObject) list;
		List<List<String>> output = new ArrayList<List<String>>();
		List<String> row_object = new ArrayList<String>();
		List<String> row = new ArrayList<String>();
		List<String> correctedList = new ArrayList<String>();
		List<List<String>> object = (List<List<String>>) json_array.get("list");
		// format of the list 
		//[0]GID, [1]nval, [2]fid, [3]fremarks, [4]fgid, [5]female, [6]mid, [7]mremarks, [8]mgid, [9]male, [10]crossdate
		
		int k = 0;
		String correctedTerm, error, gid = "";
		String line = "";

		
		for (int m = 0; m < object.size(); m++) {

			row_object = object.get(m);
			row = new ArrayList<String>();

			row.add("N/A"); // 0
			row.add(row_object.get(1)); // 1

			for (int i = 2; i < row_object.size(); i++) {
				if (i == 5 || i == 9) {

					gid = "";
					correctedTerm = row_object.get(i).toString();
					
					row.add("" + k);	//2
					k++;
					JSONObject parse = new JSONObject();

					if (row_object.get(i).toString().contains("/")
							|| row_object.get(i).toString().contains("*")) {
						parse = new CrossOp().main(
								row_object.get(i).toString(), true); // false= not to standardize
						JSONObject parse_array = (JSONObject) parse;

						correctedList = (List<String>) parse_array.get("correctedList");
						error = (String) parse_array.get("error");
						correctedTerm = correctedList.get(0);

					} else {
						error = new NomenclatureRules().checkString(row_object.get(i)
								.toString());
					}

					if (error.equals("")) {
					
						if (row_object.get(i).toString().contains("/")
								|| row_object.get(i).toString().contains("*")) {
					
							for (int n = 1; n < correctedList.size(); n++) {
								gid = gid + "#" + correctedList.get(n);
							}
						} else {
							Pattern p = Pattern.compile("IR");
							Matcher m1 = p.matcher(line);

							if (m1.lookingAt()) {
								String[] tokens = new Tokenize().tokenize(row_object.get(i).toString());
								gid = new Tokenize().stringTokens(tokens);
							} else {
								gid = "";
							}

						}
						if (i == 5){
							//[0]GID, [1]nval, [2]fid, [3]fremarks, [4]fgid, [5]female, [6]mid, [7]mremarks, [8]mgid, [9]male, [10]crossdate
							object.get(m).set(3,"in standardized format");
							object.get(m).set(4,gid);
						}else{
							object.get(m).set(7,"in standardized format");
							object.get(m).set(8,gid);
						}
						row.add("in standardized format"); // remarks
						row.add(gid); // GID

					} else {
						if (row_object.get(i).toString().contains("/")
								|| row_object.get(i).toString().contains("*")) {
							correctedTerm = correctedList.get(0);
							error = (String) parse.get("error");

						} else {
							correctedTerm = new FixString()
							.checkString(correctedTerm);

							error = new NomenclatureRules().checkString(correctedTerm);
						}
						if (error.equals("")) {
							// remarks
							if (row_object.get(i).toString().contains("/")
									|| row_object.get(i).toString()
									.contains("*")) {
								System.out.println("tokens: " + gid);
								for (int n = 1; n < correctedList.size(); n++) {
									gid = gid + "#" + correctedList.get(n);
								}
								correctedTerm = correctedList.get(0);
							} else {
								if (i == 5){
									//[0]GID, [1]nval, [2]fid, [3]fremarks, [4]fgid, [5]female, [6]mid, [7]mremarks, [8]mgid, [9]male, [10]crossdate
									object.get(m).set(3,"in standardized format");
								}else{
									object.get(m).set(7,"in standardized format");
								}
								row.add("in standardized format"); // remarks

								Pattern p = Pattern.compile("IR");
								Matcher m1 = p.matcher(correctedTerm);

								if (m1.lookingAt()) {
									String[] tokens = new Tokenize()
									.tokenize(correctedTerm);
									gid = new Tokenize().stringTokens(tokens);
								} else {
									gid = "";
								}
							}
							if (i == 5){
								//[0]GID, [1]nval, [2]fid, [3]fremarks, [4]fgid, [5]female, [6]mid, [7]mremarks, [8]mgid, [9]male, [10]crossdate
								object.get(m).set(4,gid);
							}else{
								object.get(m).set(8,gid);
							}
							row.add(gid); // GID
						} else {
							row.add(error); // remarks
							row.add("N/A"); // GID
						}
					}
					if (i == 5){
						object.get(m).set(5,correctedTerm);
					}else{
						object.get(m).set(9,correctedTerm);
					}
					row.add(correctedTerm); // pedigree term
					gid = "";
				}
				if (i == 10) {
					row.add(row_object.get(i).toString()); // cross' date of creation
				}

			}

			output.add(row);
		}

		return Response.status(201).entity(output).build();
	}

	/**
	 * Sorts the entry of the upload list, and checks if the germplsm names are
	 * in standardized format and returns the error
	 * 
	 * @param list object that contains the entries of the uploaded list
	 * @return output that stores the sorted and evaluated list
	 * @throws IOException
	 * @throws ParseException
	 * @throws MiddlewareQueryException
	 */
	@POST
	@Path("/post")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response displayFile(JSONObject list) throws IOException,
	ParseException, MiddlewareQueryException {

		JSONObject json_array = (JSONObject) list;

		List<String> gu_obj = (List<String>) json_array.get("list");
		System.out.println("list: " + gu_obj);

		gu_obj = new SortList().algo(gu_obj);

		List<List<String>> output = new ArrayList<List<String>>();
		int k = 0;
		String line = "";
		JSONObject parse;
		List<String> correctedList = new ArrayList<String>();
		;
		System.out.println("size:" + gu_obj.size());
		for (int j = 0; j < gu_obj.size();) {
			List<String> row = new ArrayList<String>();
			String error, gid = "";

			row.add("N/A");
			row.add(gu_obj.get(j)); // Cross name

			// //System.out.print("[0] N/A,");
			// System.out.print(" "+gu_obj.get(j) + ",");

			// //System.out.print("NULL ,");
			// System.out.println(j+" :: "+gu_obj.get(j).toString());

			for (int i = 1; i <= 2; i++) {
				j++;

				if (j < gu_obj.size()) {

					// System.out.println(j+" :: "+gu_obj.get(j).toString());
					// //System.out.print(gu_obj.get(j).toString() + "\t");

					System.out.print("[2] " + gu_obj.get(j).toString() + ","); // pedigree
					// term
					parse = new JSONObject();

					if (gu_obj.get(j).toString().contains("/")
							&& gu_obj.get(j).toString().contains("*")) {
						parse = new CrossOp().main(gu_obj.get(j).toString(),false); // not to standardize the parent
						JSONObject parse_array = (JSONObject) parse;
						correctedList = new ArrayList<String>();
						correctedList = (List<String>) parse_array.get("correctedList");
						// System.out.println("***ERROR: "+ error);
						System.out.println("***JSON STRING: "
								+ parse_array.toJSONString());
						System.out.println("\n------ @ MODEL.java");
						for (int l = 0; l < correctedList.size(); l++) {
							System.out.println("::" + correctedList.get(l));
						}
						System.out.println("------");
						error = (String) parse_array.get("error");

					} else {
						error = new NomenclatureRules()
						.checkString(gu_obj.get(j).toString());
					}
					row.add("" + k); // id
					// //System.out.print("[3] "+k + ",");
					// //System.out.print("[ERROR] "+error + ",");

					k++;
					if (error.equals("")) {

						// //System.out.print("[4] "+"in standardized format,");
						row.add("in standardized format"); // remarks
						// //System.out.print(" in standardized format,");
						// //remarks

						if (gu_obj.get(j).toString().contains("/")
								&& gu_obj.get(j).toString().contains("*")) {

							gid = "";
							System.out.println("tokens: " + gid);
							for (int n = 1; n < correctedList.size(); n++) {
								gid = gid + "#" + correctedList.get(n);

								System.out.println("tokens: " + gid);
							}
							System.out.println("tokens: " + gid);
						} else {
							Pattern p = Pattern.compile("IR");
							Matcher m = p.matcher(gu_obj.get(j));

							if (m.lookingAt()) {
								String[] tokens = new Tokenize()
								.tokenize(gu_obj.get(j).toString());
								gid = new Tokenize().stringTokens(tokens);
							} else {
								gid = "";
							}

						}

						row.add(gid); // GID

					} else {
						// //System.out.print("\"" + error + "\"" + ","); //
						// remarks
						row.add(error); // remarks
						row.add("N/A"); // GID
						// //System.out.print("N/A,");
					}
					gid = "";
					System.out.println("#####" + gu_obj.get(j).toString());
					row.add(gu_obj.get(j).toString()); // pedigree term

				}
			}
			j++;
			// System.out.println(j+" :: "+gu_obj.get(j).toString());
			row.add(gu_obj.get(j).toString()); // cross' date of creation
			j++;
			// System.out.println(j+" :: "+gu_obj.get(j).toString());

			// //System.out.println();
			// //System.out.println("line: "+line);

			// row.add(line);
			output.add(row);

			// //System.out.println("\n \n **output: "+output);
		}
		// System.out.println("output: "+output);
		// //System.out.println("output size: "+output.size());

		return Response.status(201).entity(output).build();

	}

	/**
	 * Entry point in searching GID to view pedigree Tree
	 * 
	 * @param data
	 * @return outputTree that contains the boolean if GID is found and the JsonString of the pedigree tree
	 * @throws JSONException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws MiddlewareQueryException
	 * @throws ParseException
	 * @throws ConfigException
	 * @throws URISyntaxException
	 */
	@Path("/searchGID")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchGID(JSONObject data) throws JSONException,
	FileNotFoundException, IOException, MiddlewareQueryException,
	ParseException, ConfigException, URISyntaxException {

		List<String> db_details = new ArrayList<String>();
		JSONObject json_array = (JSONObject) data;

		db_details = getDbDetails(db_details, json_array); // get db
		// configuraton

		ManagerFactory factory = new Config().configDB(db_details);
		JSONObject outputTree=new JSONObject();
		outputTree=new SearchGid().main(factory,json_array,outputTree);
		factory.close();


		return Response.status(200).entity(outputTree).build();
	}

	@Path("/show_germplasm_details")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response show_germplasm_details(JSONObject data)
	throws JSONException, FileNotFoundException, IOException,
	MiddlewareQueryException, ParseException, ConfigException,
	URISyntaxException {

		// new Editor();
		// Editor.show_germplasm_details((JSONObject)data);
		return Response.status(200).entity("OK!").build();

	}

	@Path("/editGermplasm")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editGermplasm(JSONObject data) throws JSONException,
	FileNotFoundException, IOException, MiddlewareQueryException,
	ParseException, ConfigException, URISyntaxException {

		List<String> db_details = new ArrayList<String>();
		JSONObject json_array = (JSONObject) data;

		db_details = getDbDetails(db_details, json_array); // get db
		// configuraton

		ManagerFactory factory = new Config().configDB(db_details);
		GermplasmDataManager man = factory.getGermplasmDataManager();

		// g.setGid(50534);
		Integer nameId = 50533; // Assumption: id=-1 exists
		Name name = man.getGermplasmNameByID(nameId);
		String nameBefore = name.toString();
		// name.setLocationId(man.getLocationByID(9000).getLocid());
		// //Assumption: location with id=1 exists
		// man.updateGermplasmName(name);
		name.setNval("IR64-1kel");
		man.updateGermplasmName(name);
		Debug.println(0, "testUpdateGermplasmName(" + nameId + ") RESULTS: "
				+ "\n\tBEFORE: " + nameBefore + "\n\tAFTER: " + name.toString());

		System.out.println("Edit success!");

		return Response.status(200).entity("OK!").build();
	}
}
