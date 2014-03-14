package com.pedigreeimport.restjersey;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.pedigreeimport.backend.BackCross;
import com.pedigreeimport.backend.CrossOp;
import com.pedigreeimport.backend.Tokenize;

public class AssignGid {

	/**
	 * @param args
	 * @throws ParseException
	 * @throws MiddlewareQueryException
	 * @throws IOException
	 */

	int GID;
	String global_id;
	int locationID;
	// List<List<String>> output = new ArrayList<List<String>>();
	List<List<String>> list_local = new ArrayList<List<String>>();
	List<List<String>> existingTerm_local = new ArrayList<List<String>>();
	List<List<String>> createdGID_local = new ArrayList<List<String>>();
	List<String> checked_local = new ArrayList<String>();
	String userID_local;
	String cross_date;

	GermplasmDataManager manager;

	/*
	 * public void main(String[] args) throws IOException,
	 * MiddlewareQueryException, ParseException { // bulk_createGID(); //
	 * copy_corrected(); //updateFile_createdGID(); //single_createdGID();
	 * 
	 * }
	 */
	public JSONObject createNew(JSONObject obj, ManagerFactory factory)
	throws MiddlewareQueryException, IOException, InterruptedException {
		System.out.println("CREATING NEW");
		manager = factory.getGermplasmDataManager();

		JSONObject jsonObject = (JSONObject) obj;
		List<List<String>> createdGID = (List<List<String>>) jsonObject
		.get("createdGID");
		createdGID_local.clear();
		createdGID_local = createdGID;
		createdGID = createdGID_local;
		List<List<String>> list = (List<List<String>>) jsonObject.get("list");
		// ////System.out.println("\t list: "+list.size());
		list_local = list;
		// ////System.out.println("\t list: "+list_local.size());
		List<List<String>> existingTerm = (List<List<String>>) jsonObject
		.get("existingTerm");
		existingTerm_local = existingTerm;
		System.out.println("existingTerm: " + existingTerm);
		System.out.println("existingTerm_l: " + existingTerm_local);
		cross_date = (String) jsonObject.get("cdate");

		String userID = (String) jsonObject.get("userID");
		locationID = Integer.valueOf((String) jsonObject.get("locationID"));
		userID_local = userID;

		String cross = (String) jsonObject.get("cross");
		String gpid1_nval = (String) jsonObject.get("gpid1_nval");
		String gpid2_nval = (String) jsonObject.get("gpid2_nval");
		String fid = (String) jsonObject.get("fid");
		String mid = (String) jsonObject.get("mid");
		String chosenID = (String) jsonObject.get("chosenID");
		String create_nval = (String) jsonObject.get("term");
		String theParent = (String) jsonObject.get("theParent");
		String female_nval = gpid1_nval;
		String male_nval = gpid2_nval;

		Boolean isFound_female = null, isFound_male = null;
		int mgid, fgid;

		System.out.println("cross: " + cross);
		System.out.println("create_nval: " + create_nval);
		System.out.println("gpid1_nval: " + gpid1_nval);
		System.out.println("gpid2_nval: " + gpid2_nval);

		if (cross.equals(create_nval)) {
			System.out.println("CLICK CREATE NEW CROSS");
			updateCreatedGID("NOT SET", fid + "/" + mid, cross, "false",
					createdGID_local);

			if ((female_nval.contains("/") && female_nval.contains("*")) || (female_nval.contains("/") && !female_nval.contains("*"))) {
				System.out.println("female has cross operators");
				isFound_female = checkParent_crossOp_updateCreatedGID(
						female_nval, fid);
			} else {
				System.out.println("Female no cross op");
				isFound_female = checkParent_updateCreatedGID(female_nval, fid);
			}
			// System.out.println("female: "+female);
			fgid = GID;
			// //System.out.println("\nmale:");
			System.out.println("createdGID: " + createdGID_local);
			if (male_nval.contains("/") && male_nval.contains("*")) {
				System.out.println("Male has cross operators");
				isFound_male = checkParent_crossOp_updateCreatedGID(male_nval,
						mid);
			} else {
				System.out.println("Male no cross op");
				isFound_male = checkParent_updateCreatedGID(male_nval, mid);
			}
			// System.out.println("male: "+male);
			System.out.println("createdGID: " + createdGID_local);
			mgid = GID;
			System.out.println("female: " + isFound_female);
			System.out.println("male: " + isFound_male);
			/*
			 * if(isFound_female && isFound_male){
			 * System.out.println("createdGID for cross "+ cross); int
			 * methodID=selectMethodType(fgid,mgid,female_nval,male_nval,cross);
			 * 
			 * 
			 * int cross_gid = (int) addGID( cross,fgid,mgid,methodID,2,true);
			 * 
			 * Germplasm germplasm1 = manager.getGermplasmByGID(cross_gid);
			 * 
			 * //printSuccess(cross,female_nval + "/" + male_nval, fid + "/" +
			 * mid, germplasm1, "new"); updateCreatedGID(""+germplasm1.getGid(),
			 * fid + "/" + mid, cross, "new", createdGID_local);
			 * 
			 * //list_local=update_list(germplasm1, female_id, cross);
			 * ////System.out.println("\t id: "+fid + "/" + mid);
			 * ////System.out.println("\t id: "+ cross); List<Germplasm>
			 * glist=new ArrayList<Germplasm>(); glist.add(germplasm1); for(int
			 * i=0;i<createdGID_local.size();i++){
			 * if(createdGID_local.get(i).get
			 * (1).equals(createdGID_local.get(i).get(2)) &&
			 * createdGID_local.get(i).get(2).equals(cross)){
			 * multipleHits_inLocation(glist, createdGID_local.get(i).get(2),
			 * createdGID_local.get(i).get(0), createdGID_local.get(i).get(1));
			 * createdGID_local=updateCreatedGID("CHOOSE GID",
			 * createdGID_local.get(i).get(0), createdGID_local.get(i).get(2),
			 * "false", createdGID); createdGID=createdGID_local; } }
			 * glist.clear(); germplasm1=null; //germplasm=null;
			 * 
			 * } System.out.println("existingTerm: "+existingTerm_local);
			 */
		} else {
			
			Boolean not_IR=false;
			if (theParent.contains("/") && theParent.contains("*")) {
				int max=0;
				String slash="";

				max = new CrossOp().maxCross(max, theParent);
				slash = new BackCross().printSlash(max);
				String tokens[] = theParent.split("\\" + slash, 2);
				String tokens_dose[];
				String dose;
				if (tokens[0].contains("*")) {
					tokens_dose = tokens[0].split("\\*", 2);
					dose = tokens_dose[1];
					System.out.println("dose: " + dose);
					if(tokens_dose[0].startsWith("IR")){
						not_IR=true;
					}
					System.out.println("not IR: "+tokens_dose[0]);
				}else{
					tokens_dose = tokens[1].split("\\*", 2);
					dose = tokens_dose[0];
					System.out.println("dose: " + dose);
					if(tokens_dose[1].startsWith("IR")){
						not_IR=true;
					}
					System.out.println("not IR: "+tokens_dose[1]);
				}

			}	

			if (theParent.contains("/") || theParent.contains("*") && not_IR) {
				System.out.println("create_nval has cross operators");
				System.out.println("chosenID: " + chosenID);
				System.out.println("createnval: " + create_nval);
				System.out.println("theParent: " + theParent);

				// isFound_female=checkParent_crossOp_updateCreatedGID(
				// female_nval, fid);
				// createGID_crossParents_updateCreatedGID(pedigreeList, index,
				// temp_crossesGID, check, line, max, parent, id)
				// check if create nval is a parent in the cross
				// if yes, create the pedigree Line
				if (create_nval.contains("/") ) {
					updateCreatedGID("NOT SET", chosenID, create_nval, "false",
							createdGID_local);
					createNew_crossOP(chosenID, create_nval, theParent,
							create_nval, createdGID_local);

				} else {
					
					if (theParent.contains("*") && theParent.contains("/")&& not_IR ) {
						System.out.println("Create NEW with backcross");
						createNew_crossOP_parent_bc(chosenID, create_nval,
								theParent, create_nval, createdGID_local);
					} else {
						System.out.println("NOT IR");
						createNew_crossOP_parent(chosenID, create_nval,
								theParent, create_nval, createdGID_local);
					}
				}
				// if no, search for the parents

			} else {
				Pattern p = Pattern.compile("IR");
				Matcher m1 = p.matcher(theParent);
				String[] tokens = { "" };
				if (m1.lookingAt()) {
					tokens = new Tokenize().tokenize(theParent);
				} else {
					tokens[0] = theParent;
				}
				// String[] tokens = new Tokenize().tokenize(theParent);
				ArrayList<String> pedigreeList = new ArrayList<String>();

				pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0]
				// is the
				// most
				// recent
				// pedigree,
				// pedigreeList[size]
				// is the
				// root
				Collections.reverse(pedigreeList);
				int gpid1 = 0, gpid2 = 0, gid = 0;
				Germplasm g;
				for (int i = 0; i < pedigreeList.size(); i++) {

					g = new Germplasm();

					if (i == 0) {
						int methodID = selectMethodType_DER(
								pedigreeList.get(i), theParent);

						gid = (int) addGID(pedigreeList.get(i), gpid1, gpid2,
								methodID, 5, false);
						g = manager.getGermplasmByGID(gid);
						gpid2 = gid;
						gpid1 = gid;
					} else {

						int methodID = selectMethodType_DER(
								pedigreeList.get(i), theParent);
						gid = (int) addGID(pedigreeList.get(i), gpid1, gpid2,
								methodID, 5, false);
						g = manager.getGermplasmByGID(gid);
						gpid2 = gid;
					}
					// pedigreeList_GID.add(gid);

					// ////System.out.println(pedigreeList.get(i) + " gpid1: " +
					// gpid1
					// + " gpid2: " + gpid2);
					if (i == pedigreeList.size() - 1) {
						GID = gid;
					}

					updateCreatedGID("" + gid, chosenID, pedigreeList.get(i),
							"new", createdGID_local);
				}
				g = null;
			}
		}
		//int gid1 = 0, gid2 = 0;
		//gid1 = GID;
		String gid1="", gid2="";
		System.out.println("mid: " + mid);
		System.out.println("fid: " + fid);
		String female_id;
		String male_id;
		String id;
		String parent_id = "";
		String fgid_s = "";
		String mgid_s = "";
		if (chosenID.equals(mid)) {
			id = fid;
			male_id = mid;
			female_id = fid;
		} else {
			id = mid;
			male_id = fid;
			female_id = mid;
		}
		int counter = 0;
		int counter2 = 0;
		
		for (int i = 0; i < createdGID_local.size(); i++) {

			if (id.equals(createdGID_local.get(i).get(0))
					
					&& counter2 == 0 ) {
				gid2 = createdGID_local.get(i).get(3);
				parent_id = createdGID_local.get(i).get(1);
				counter2++;
				System.out.println("id: "+id +" "+"name value: "+createdGID_local.get(i).get(2));
				System.out.println("gid2: "+gid2);
				
			}
			if (chosenID.equals(createdGID_local.get(i).get(0))
					
					&& counter == 0 ) {
				gid1 = createdGID_local.get(i).get(3);
				//parent_id = createdGID.get(i).get(1);
				counter++;
				System.out.println("id: "+chosenID +" "+"name value: "+createdGID_local.get(i).get(2));
				System.out.println("gid1: "+gid1);
				
			}
		}
		createdGID=createdGID_local;
		String maleParent;
		String femaleParent;
		if (chosenID.equals(mid)) {
			maleParent = theParent;
			femaleParent = parent_id;
			mgid_s = gid1;
			fgid_s = gid2;
		} else {
			maleParent = parent_id;
			femaleParent = theParent;
			mgid_s = gid2;
			fgid_s = gid1;
		}
		
		System.out.println("mgid: "+mgid_s);
		System.out.println("fgid: "+fgid_s);
		System.out.println("female: "+femaleParent);
		System.out.println("male: "+maleParent);

		if (!gid1.equals("0") && !gid2.equals("0") && !gid1.equals("NOT SET") && !gid2.equals("NOT SET") && !gid1.equals( "CHOOSE GID") && !gid2.equals("CHOOSE GID")) {
			Germplasm g1 = manager.getGermplasmByGID(Integer.valueOf(gid1));
			List<Name> name = manager.getNamesByGID(g1.getGpid1(), 0, null);
			List<Name> name1 = manager.getNamesByGID(g1.getGpid2(), 0, null);
			int methodID = 0;
			if (name1.size() != 0 && name.size() != 0) {
				if (name.get(0).getNval().equals(maleParent)
						|| name1.get(0).getNval().equals(maleParent)) {
					methodID = 107;
				}
				name.clear();
				name1.clear();
				g1 = null;
			}

			methodID = selectMethodType(Integer.valueOf(gid1), Integer.valueOf(gid2), femaleParent, maleParent,
					cross, methodID);

			int cross_gid = (int) addGID(cross, Integer.valueOf(fgid_s), Integer.valueOf(mgid_s), methodID, 2, true);

			Germplasm germplasm1 = manager.getGermplasmByGID(cross_gid);

			createdGID_local = updateCreatedGID("" + germplasm1.getGid(), fid
					+ "/" + mid, cross, "new", createdGID_local);
			createdGID = createdGID_local;

			list_local = update_list(germplasm1, fid, cross);

		}
		createdGID = createdGID_local;
		JSONObject data_output = new JSONObject();
		data_output.put("list", list_local);
		data_output.put("createdGID", createdGID);
		data_output.put("existingTerm", existingTerm_local);
		manager = null;
		factory.close();
		return data_output;
	}

	public JSONObject chooseGID(JSONObject obj, ManagerFactory factory)
	throws MiddlewareQueryException, IOException, ParseException,
	InterruptedException {

		manager = factory.getGermplasmDataManager();

		JSONObject jsonObject = (JSONObject) obj;
		List<List<String>> createdGID = (List<List<String>>) jsonObject
		.get("createdGID");
		createdGID_local.clear();
		createdGID_local = createdGID;
		createdGID = createdGID_local;
		List<List<String>> list = (List<List<String>>) jsonObject.get("list");
		// ////System.out.println("\t list: "+list.size());
		list_local = list;
		// ////System.out.println("\t list: "+list_local.size());
		List<List<String>> existingTerm = (List<List<String>>) jsonObject
		.get("existingTerm");
		existingTerm_local = existingTerm;

		String userID = (String) jsonObject.get("userID");
		userID_local = userID;

		String lastDeriv_parent = (String) jsonObject.get("term");
		String theParent = (String) jsonObject.get("theParent");
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

		List<String> details = (List<String>) jsonObject.get("germplasm");
		// ////System.out.println("json string:location ID: " + (String)
		// details.get(6));
		locationID = Integer.valueOf((String) details.get(6));
		int gpid1 = Integer.valueOf((String) details.get(8));
		int gpid2 = Integer.valueOf((String) details.get(9));

		cross_date = (String) jsonObject.get("cdate");

		String parent1ID = (String) details.get(0);
		// parent1ID, ID of parent1
		String gid = (String) details.get(3);

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

		String temp;
		mid = mid.replaceAll("\"", "");
		fid = fid.replaceAll("\"", "");
		System.out.println("fid: " + fid);
		System.out.println("mid: " + mid);
		int mid2 = Integer.valueOf(mid), fid2 = Integer.valueOf(fid);

		if (Integer.valueOf(mid) < Integer.valueOf(fid)) {
			temp = mid;
			mid = fid;
			fid = temp;

			temp = maleParent;
			maleParent = femaleParent;
			femaleParent = temp;
		}
		int gid_local = Integer.valueOf(gid);
		int gpid1_local = gpid1, gpid2_local = gpid2;

		System.out.println("theParent: " + theParent);
		System.out.println("lastDeriv_parent: " + lastDeriv_parent);
		System.out.println("gid: " + gid);
		System.out.println("gpid1: " + gpid1);
		System.out.println("gpid2: " + gpid2);
		System.out.println("mid: " + mid);
		System.out.println("fid: " + fid);
		System.out.println("maleParent: " + maleParent);
		System.out.println("femaleParent: " + femaleParent);

		System.out.println("parent1ID: " + parent1ID);
		System.out.println("parent2ID: " + parent2ID);

		if (theParent.contains("/") && theParent.contains("*")) {
			// The Parent has cross operators
			System.out.println("The parent has cross operators");
			System.out.println("parent q");
			if (theParent.contains("*")) {
				createdGID = updateCreatedGID(gid, parent1ID, lastDeriv_parent,
						"false", createdGID);
				createdGID_local = createdGID;
				createdGID = chooseGID_bc(parent1ID, parent1, theParent,
						lastDeriv_parent, gid_local, gpid1_local, gpid2_local,
						gid, gpid1, gpid2, createdGID);
			} else {
				createdGID = updateCreatedGID(gid, parent1ID, lastDeriv_parent,
						"false", createdGID);
				createdGID_local = createdGID;
				createdGID = chooseGID_crossOP(parent1ID, parent1, theParent,
						lastDeriv_parent, gid_local, gpid1_local, gpid2_local,
						gid, gpid1, gpid2, createdGID);
			}

			System.out.println("\n ***** END******* \n ");
		} else {
			System.out.println("term " + theParent + " has no cross operators");
			Pattern p;
			Matcher m1;
			String[] tokens = { "" };
			
			if(parent1.contains("*")){
				String tokens_dose[] = parent1.split("\\*", 2);
				if(tokens_dose[0].contains("*")){
					tokens[0] = tokens_dose[1];
				}else{
					tokens[0] = tokens_dose[0];
				}
			}else{
				p = Pattern.compile("IR");
				m1 = p.matcher(parent1);
				if (m1.lookingAt()) {
					tokens = new Tokenize().tokenize(parent1);

				} else {
					tokens[0] = parent1;
				}

			}
			
			System.out.println("tokens: " + tokens.length);
			ArrayList<String> pedigreeList = new ArrayList<String>();
			pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0]
			// is the most
			// recent
			// pedigree,
			// pedigreeList[size]
			// is the root

			// Collections.reverse(pedigreeList); // index 0 is the root

			int index = -1;
			for (int i = 0; i < pedigreeList.size(); i++) {
				if (pedigreeList.get(i).equals(lastDeriv_parent)) {
					index = i;
				}
			}

			createdGID = getDerivativeLine(index, pedigreeList, gid_local,
					gpid1_local, gpid2_local, gid, gpid1, gpid2, parent1ID,
					parent1, createdGID);
		}
		Germplasm germplasm = new Germplasm();
		System.out.println("male: " + maleParent);
		System.out.println("f: " + femaleParent);
		int mgid = getGID_fromFile(maleParent, mid);
		int fgid = getGID_fromFile(femaleParent, fid);
		System.out.println("mgid: " + mgid);
		System.out.println("fgid: " + fgid);

		System.out.println("parent2ID: " + parent2ID);
		System.out.println("parent2: " + parent2);

		if (mgid != 0 && fgid != 0) {
			System.out.println("cross: " + cross);
			germplasm = isCross_existing(cross, fgid, mgid);
			if (germplasm == null) {
				System.out.println("Create GID for " + cross);
				Germplasm g1 = manager.getGermplasmByGID(fgid);
				List<Name> name = manager.getNamesByGID(g1.getGpid1(), 0, null);
				List<Name> name1 = manager
				.getNamesByGID(g1.getGpid2(), 0, null);
				int methodID = 0;
				if (name1.size() != 0 && name.size() != 0) {
					if (name.get(0).getNval().equals(maleParent)
							|| name1.get(0).getNval().equals(maleParent)) {
						methodID = 107;
					}
					name.clear();
					name1.clear();
					g1 = null;
				}

				methodID = selectMethodType(fgid, mgid, femaleParent,
						maleParent, cross, methodID);

				int cross_gid = (int) addGID(cross, fgid, mgid, methodID, 2,
						true);

				Germplasm germplasm1 = manager.getGermplasmByGID(cross_gid);

				createdGID_local = updateCreatedGID("" + germplasm1.getGid(),
						fid + "/" + mid, cross, "new", createdGID);
				createdGID = createdGID_local;

				list_local = update_list(germplasm1, fid, cross);
				// //System.out.println("\t id: "+fid + "/" + mid);
				// //System.out.println("\t id: "+ cross);
				List<Germplasm> glist = new ArrayList<Germplasm>();
				glist.add(germplasm1);
				for (int i = 0; i < createdGID_local.size(); i++) {
					if (createdGID_local.get(i).get(1)
							.equals(createdGID_local.get(i).get(2))
							&& createdGID_local.get(i).get(2).equals(cross)) {
						multipleHits_inLocation(glist, createdGID_local.get(i)
								.get(2), createdGID_local.get(i).get(0),
								createdGID_local.get(i).get(1));
						createdGID_local = updateCreatedGID("CHOOSE GID",
								createdGID_local.get(i).get(0),
								createdGID_local.get(i).get(2), "false",
								createdGID);
						createdGID = createdGID_local;
					}
				}
				glist.clear();

				germplasm1 = null;

			} else {
				System.out.println("Cross is existing ");
				createdGID_local = updateCreatedGID("" + germplasm.getGid(),
						fid + "/" + mid, cross, "false", createdGID);
				createdGID = createdGID_local;

				list_local = update_list(germplasm, fid, cross);
			}
			System.out.println("createdGID: " + list_local);
			System.out.println("createdGID: " + createdGID);
		}

		details.clear();
		jsonObject.clear();
		germplasm = null;

		JSONObject data_output = new JSONObject();
		data_output.put("list", list_local);
		data_output.put("createdGID", createdGID_local);
		data_output.put("existingTerm", existingTerm_local);
		manager = null;
		factory.close();
		return data_output;

	}

	@SuppressWarnings("unchecked")
	private List<List<String>> chooseGID_bc(String parent1ID, String parent1,
			String theParent, String lastDeriv_parent, int gid_local,
			int gpid1_local, int gpid2_local, String gid, int gpid1, int gpid2,
			List<List<String>> createdGID) throws MiddlewareQueryException,
			IOException, InterruptedException {
		/*
		 * check if the lastDerivative is from parents or one of the derivatives
		 * if yes, set the dervative line
		 * 
		 * check if all parents are existing if yes, create the backcrosses,
		 * crosses if not end.
		 * 
		 * check if the lastDeriv is from the backcrosses if it is the single
		 * cross (A/B) if yes, get the GID of the female and male, set the GID
		 * for the female & male in the parents list and else, set the GID for
		 * the backcrosses set the GID for the parents
		 * 
		 * check if the lastDeriv is from the crosses if the cross is equal to
		 * the lastDeriv and is the bottom in the crosses list (meaning it is
		 * the string with the backcross)
		 * 
		 * set the GID for the backcrosses set the GID for the parents else{
		 * 
		 * }
		 */

		String pedigree = parent1;
		List<String> pedigreeList = new ArrayList<String>();

		List<List<String>> backcrosses = new ArrayList<List<String>>();
		List<List<String>> crosses = new ArrayList<List<String>>();
		List<String> parents_bc = new ArrayList<String>();

		List<List<String>> parents = new ArrayList<List<String>>(); // list of
		// names
		// that
		// ntype=DER
		List<String> row_parents = new ArrayList<String>();

		new CrossOp();

		JSONObject result_method2 = CrossOp.method2(pedigree, pedigreeList);
		pedigreeList = (List<String>) result_method2.get("list");
		backcrosses = (List<List<String>>) result_method2.get("backcrosses");
		crosses = (List<List<String>>) result_method2.get("crosses");
		parents_bc = (List<String>) result_method2.get("parents");
		result_method2.clear();

		int index_derivative = -1; // index of the chosen GID if it is from a
		// derivative

		System.out.println("PEDIGREELIST: " + pedigreeList);
		for (int i = 0; i < pedigreeList.size(); i++) {// parents is the list of
			// parsed strings
			System.out.println(":: " + pedigreeList.get(i));

			if (!pedigreeList.get(i).contains("/")
			/*&& !pedigreeList.get(i).contains("*")*/) {
				row_parents = new ArrayList<String>();
				row_parents.add(pedigreeList.get(i));

				row_parents.add("0");
				parents.add(row_parents);
			}
		}
		System.out.println("PARENTS: " + parents);
		System.out.println("recurrent par: " + parents_bc);
		System.out.println("bc: " + backcrosses);
		System.out.println("crosses: " + crosses);

		if (!lastDeriv_parent.contains("/") /*&& !lastDeriv_parent.contains("*")*/) {
			System.out.println("last deriv parent no / and *");
			for (int i = 0; i < parents.size(); i++) {
				if (parents.get(i).get(0).contains("-")) {
					index_derivative = i; // get the index
					Pattern p = Pattern.compile("IR");
					Matcher m1 = p.matcher(parents.get(i).get(0));
					String[] tokens2 = { "" };
					if (m1.lookingAt()) {
						tokens2 = new Tokenize()
						.tokenize(parents.get(i).get(0));

					} else {
						tokens2[0] = "";
					}
					ArrayList<String> pedigreeList_der = new ArrayList<String>();
					pedigreeList_der = saveToArray(pedigreeList_der, tokens2);
					for (int j = 0; j < pedigreeList_der.size(); j++) {
						System.out.println("pedigreeList: "
								+ pedigreeList_der.get(j) + " chooseNVAL: "
								+ lastDeriv_parent);

						if (pedigreeList_der.get(j).equals(lastDeriv_parent)) { // if
							// the
							// chosen
							// GID
							// is
							// a
							// derivative
							// in
							// one
							// of
							// the
							// parents
							// if derivative, get derivative line
							System.out
							.println("if derivative, get derivative line");
							index_derivative = j; // get the index
							createdGID = getDerivativeLine(index_derivative,
									pedigreeList_der, gid_local, gpid1_local,
									gpid2_local, gid, gpid1, gpid2, parent1ID,
									parent1, createdGID);

							createdGID_local = createdGID;
							if (index_derivative == 0) {
								parents.get(i).set(1, "" + gid_local);

							} else {
								parents.get(i).set(1, "" + GID);

							}

						}
					}
				} else {
					if (lastDeriv_parent.equals(parents.get(i).get(0))) {
						System.out.println("no -");
						parents.get(i).set(1, "" + gid_local);
						System.out.println("gid: " + gid_local);
						System.out.println("theParent: " + theParent);
						System.out.println("term: " + parents.get(i).get(0));
						createdGID = updateFile_createdGID("" + gid_local,
								parent1ID, parents.get(i).get(0), "false",
								theParent); // update the createdGID file

						createdGID_local = createdGID;
					}
					index_derivative = i; // get the index

				}

			}
		}
		// get GID of parents
		parents = checkGID_parents(parent1ID, parents, theParent);

		// check if all parents exists

		int count_parents_exist = 0;
		for (int j = 0; j < parents.size(); j++) {
			if (!parents.get(j).get(1).equals("0")
					&& !parents.get(j).get(1).equals("NOT SET")
					&& !parents.get(j).get(1).equals("CHOOSE GID")) {
				count_parents_exist++;
			}
		}
		// get GID of bc
		for (int j = 0; j < backcrosses.size(); j++) {
			for (int i = 0; i < createdGID_local.size(); i++) {
				if (createdGID_local.get(i).get(0).equals(parent1ID)
						&& createdGID_local.get(i).get(1).equals(theParent)
						&& createdGID_local.get(i).get(2)
						.equals(backcrosses.get(j).get(0))) {
					if (!createdGID_local.get(i).get(3).equals("NOT SET")
							&& !createdGID_local.get(i).get(3)
							.equals("CHOOSE GID")) {
						backcrosses.get(j).set(1,
								createdGID_local.get(i).get(3));
						System.out.println("bc: "
								+ createdGID_local.get(i).get(3));
					}

				}
			}
		}

		// check if all bc exists

		int count_bc_exist = 0;
		for (int j = 0; j < backcrosses.size(); j++) {
			if (!backcrosses.get(j).get(1).equals("0")
					&& !backcrosses.get(j).get(1).equals("NOT SET")
					&& !backcrosses.get(j).get(1).equals("CHOOSE GID")) {
				count_bc_exist++;
			}
		}
		// get GID of crosses
		crosses = checkGID_crosses(parent1ID, crosses, theParent);

		System.out.println("PARENTS: " + parents);
		System.out.println("recurrent par: " + parents_bc);
		System.out.println("bc: " + backcrosses);
		System.out.println("crosses: " + crosses);

		// create GID for back cross and crosses
		System.out.println("count parents : " + count_parents_exist);
		System.out.println("count bc: " + count_bc_exist);
		System.out.println("index deriv: " + index_derivative);
		if (count_parents_exist == parents.size() && index_derivative > -1
				&& count_bc_exist != backcrosses.size()) {
			createGID_bc_backcrosses(parents, backcrosses, crosses, parents_bc,
					createdGID, lastDeriv_parent, parent1ID); // createGID
		} else if (count_bc_exist == backcrosses.size()
				&& count_parents_exist == parents.size()) { // createGID for the
			// crosses not yet
			// created
			System.out
			.println("ALL PARENTS EXIST, CREATE GID for CROSSES*************");
			int index = -1;
			for (int j = 0; j < crosses.size(); j++) {
				for (int i = 0; i < createdGID_local.size(); i++) {
					if (createdGID_local.get(i).get(0).equals(parent1ID)
							&& createdGID_local.get(i).get(1).equals(theParent)
							&& createdGID_local.get(i).get(2)
							.equals(crosses.get(j).get(0))) {
						if (createdGID_local.get(i).get(3).equals("NOT SET")
								|| createdGID_local.get(i).get(3)
								.equals("CHOOSE GID")) {
							index = j;
						}

					}
				}
			}
			createdGID = create_crosses(parents, crosses, createdGID,
					parent1ID, index, parent1);
			createdGID_local = createdGID;
		}

		/* END if chosen GID is a parent */

		/* START if chosen GID is a backcross */
		int index_bc = -1; // flag for backcrosses, if the chosen GID is from
		// backcrosses
		if (index_derivative == -1) { // chosen GID is from crosses or
			// backcrosses
			System.out.println("Chosen is from a backCross or crosses");
			JSONObject result_createdGID_bc_backcrosses = createGID_bc_backcrosses(
					parents, backcrosses, crosses, parents_bc, createdGID,
					lastDeriv_parent, parent1ID, parent1, index_bc,
					gpid1_local, gpid2_local, gid_local);
			index_bc = (Integer) result_createdGID_bc_backcrosses.get("index");
			parents = (List<List<String>>) result_createdGID_bc_backcrosses
			.get("parents");
			backcrosses = (List<List<String>>) result_createdGID_bc_backcrosses
			.get("backcrosses");
			crosses = (List<List<String>>) result_createdGID_bc_backcrosses
			.get("crosses");
			parents_bc = (List<String>) result_createdGID_bc_backcrosses
			.get("parents_bc");

		}

		/* Start if chosen GID from crosses */
		// crosses=checkGID_parents(parent1ID,crosses, theParent);
		if (index_bc == -1 && index_derivative == -1 /*
		 * &&
		 * count_parents_exist==parents
		 * .size()
		 */) {
			System.out.println("Chosen is from a CROSSES");
			JSONObject result_crosses = createGID_bc_crosses(lastDeriv_parent,
					createdGID, crosses, parents, backcrosses, parent1ID,
					parent1, index_bc, gpid1_local, gid_local, gpid2_local,
					parents_bc, theParent);

			parents = (List<List<String>>) result_crosses.get("parents");
			backcrosses = (List<List<String>>) result_crosses
			.get("backcrosses");
			crosses = (List<List<String>>) result_crosses.get("crosses");
			parents_bc = (List<String>) result_crosses.get("parents_bc");

		}

		backcrosses.clear();
		parents_bc.clear();
		crosses.clear();
		parents.clear();
		return createdGID_local;
	}

	private List<List<String>> create_crosses(List<List<String>> parents,
			List<List<String>> crosses, List<List<String>> createdGID,
			String parent1ID, int index, String parent1)
			throws MiddlewareQueryException, IOException {
		int max = 0;
		String slash = "";
		String tokens[];
		String female = "", male = "";
		int fgid = 0, mgid = 0;
		int new_gid = 0;
		int methodID = 0;
		//System.out.println("cross 0: " + crosses.get(0).get(0));
		//System.out.println("cross last: "
			//	+ crosses.get(crosses.size() - 1).get(0));
		String tokens_dose[];
		String dose = "";

		for (int i = index; i >= 0; i--) {
			System.out.println(":: " + crosses.get(i).get(0));
			max = new CrossOp().maxCross(max, crosses.get(i).get(0));
			slash = new BackCross().printSlash(max);
			tokens = crosses.get(i).get(0).split("\\" + slash, 2);

			if (i == crosses.size() - 1) {

				if (tokens[0].contains("*")) {

					tokens_dose = tokens[0].split("\\*", 2);
					dose = tokens_dose[1];
					System.out.println("dose: " + dose);

					tokens[0] = tokens[0].replaceAll("\\*\\" + dose, "");
					if(!tokens[0].startsWith("IR")){
						tokens = crosses.get(i).get(0).split("\\" + slash, 2);
					}

				} else {
					tokens_dose = tokens[1].split("\\*", 2);
					dose = tokens_dose[0];
					System.out.println("dose: " + dose);
					tokens[1] = tokens[1].replaceAll(dose + "\\*", "");
					if(!tokens[1].startsWith("IR")){
						tokens = crosses.get(i).get(0).split("\\" + slash, 2);
					}
				}
				System.out.println("token 0: " + tokens[0]);
				System.out.println("token 1: " + tokens[1]);


				for (int j = 0; j < parents.size(); j++) {
					if (parents.get(j).get(0).equals(tokens[0])) {
						fgid = Integer.valueOf(parents.get(j).get(1));
						female = parents.get(j).get(0);
					}
					if (parents.get(j).get(0).equals(tokens[1])) {
						mgid = Integer.valueOf(parents.get(j).get(1));
						male = parents.get(j).get(0);
					}
				}

			} else {

				if (!tokens[0].contains("/") || !tokens[1].contains("/")) {
					for (int j = 0; j < parents.size(); j++) {
						if (parents.get(j).get(0).equals(tokens[0])) {
							fgid = Integer.valueOf(parents.get(j).get(1));
							female = parents.get(j).get(0);
						}
						if (parents.get(j).get(0).equals(tokens[1])) {
							mgid = Integer.valueOf(parents.get(j).get(1));
							male = parents.get(j).get(0);
						}
					}
				}

				System.out.println("CROSSES: " + crosses);
				if (tokens[0].contains("/") || tokens[1].contains("/")) {
					for (int j = 0; j < crosses.size(); j++) {
						System.out.println("\tCROSSES::::: "
								+ crosses.get(j).get(0));
						if (crosses.get(j).get(0).equals(tokens[0])) {
							fgid = Integer.valueOf(crosses.get(j).get(1));

							female = crosses.get(j).get(0);
						}
						if (crosses.get(j).get(0).equals(tokens[1])) {
							System.out.println("token 1: " + tokens[1]);
							System.out.println("token 1: " + crosses.get(j));
							mgid = Integer.valueOf(crosses.get(j).get(1));
							male = crosses.get(j).get(0);
						}
					}
				}

			}

			System.out.println("male: " + male + " :" + mgid);
			System.out.println("female: " + female + " :" + fgid);
			if (i == crosses.size() - 1) {
				methodID = 107;
			} else {
				methodID = 0;
			}
			methodID = selectMethodType(fgid, mgid, female, male, crosses
					.get(i).get(0), methodID);
			new_gid = addGID(crosses.get(i).get(0), fgid, mgid, methodID, 3,
					false);
			createdGID = updateCreatedGID("" + new_gid, parent1ID,
					crosses.get(i).get(0), "new", createdGID);
			createdGID_local = createdGID;

			crosses = checkGID_crosses(parent1ID, crosses, parent1);

			System.out.println("CG: " + createdGID_local);
		}

		return createdGID;
	}

	private JSONObject createGID_bc_crosses(String lastDeriv_parent,
			List<List<String>> createdGID, List<List<String>> crosses,
			List<List<String>> parents, List<List<String>> backcrosses,
			String parent1ID, String parent1, int index_bc, int gpid1_local,
			int gid_local, int gpid2_local, List<String> parents_bc,
			String theParent) throws MiddlewareQueryException, IOException,
			InterruptedException {
		int index_crosses = -1;
		List<Germplasm> germplasm_fin = new ArrayList<Germplasm>();
		List<Germplasm> germplasm = new ArrayList<Germplasm>();
		int count_LOCAL = 0;
		int count_CENTRAL = 0;
		int count = 0;
		for (int i = 0; i < crosses.size(); i++) {
			if (crosses.get(i).get(0).equals(lastDeriv_parent)) {
				index_crosses = i;
				if (i == 0) {
					// if it is theParent
					System.out.println("if it is the parent");
					setLine_crossOp(parent1, parent1ID,
							Integer.valueOf(gid_local), gpid1_local,
							gpid2_local, createdGID);

				} else if (i == crosses.size() - 1) {
					System.out.println("if it is the backcross");
					Germplasm g = new Germplasm();
					g = manager.getGermplasmByGID(gpid1_local);
					updateCreatedGID("" + gpid1_local, parent1ID, backcrosses
							.get(0).get(0), "false", createdGID);
					updateCreatedGID("" + gpid2_local, parent1ID,
							parents_bc.get(0), "false", createdGID);
					for (int j = 0; j < parents.size(); j++) {

						if (parents.get(j).get(0).equals(parents_bc.get(0))) {
							parents.get(j).set(1, "" + gpid2_local);
						}
					}

					// set the line from backcrosses->parents
					createGID_bc_backcrosses(parents, backcrosses, crosses,
							parents_bc, createdGID, backcrosses.get(0).get(0),
							parent1ID, parent1, index_bc, g.getGpid1(),
							g.getGpid2(), g.getGid());
					g = null;

					// get GID of parents
					for (int j = 0; j < parents.size(); j++) {
						for (int k = 0; k < createdGID_local.size(); k++) {
							if (createdGID_local.get(k).get(0)
									.equals(parent1ID)
									&& createdGID_local.get(k).get(1)
									.equals(theParent)
									&& createdGID_local.get(k).get(2)
									.equals(parents.get(j).get(0))) {
								if (!createdGID_local.get(k).get(3)
										.equals("NOT SET")
										&& !createdGID_local.get(k).get(3)
										.equals("CHOOSE GID")) {
									parents.get(j).set(1,
											createdGID_local.get(k).get(3));
									System.out.println(parents.get(j).get(0)
											+ " has GID of :"
											+ createdGID_local.get(k).get(3));

								} else {
									System.out.println(parents.get(j).get(0)
											+ " is NOT SET :");
									germplasm_fin = new ArrayList<Germplasm>();
									germplasm = new ArrayList<Germplasm>();

									count_LOCAL = countGermplasmByName(parents
											.get(j).get(0), Database.LOCAL);
									count_CENTRAL = countGermplasmByName(
											parents.get(j).get(0),
											Database.CENTRAL);

									germplasm = getGermplasmList(parents.get(j)
											.get(0), count_LOCAL, count_CENTRAL);
									// System.out.println("gcount: "+count_LOCAL);
									for (int l = 0; l < germplasm.size(); l++) {
										if (germplasm.get(l).getLocationId()
												.equals(locationID)
												&& germplasm.get(l).getGrplce() == 0) {
											germplasm_fin.add(germplasm.get(l));
										}
									}
									count = germplasm_fin.size();
									if (count > 0) { // multiple hits
										System.out.println("\t Multiple HIT");
										System.out.println("\t i==0");
										// printChooseGID(pedigree, parent, id);
										updateCreatedGID("CHOOSE GID",
												parent1ID, parents.get(j)
												.get(0), "false",
												createdGID_local);
										multipleHits_inLocation(germplasm_fin,
												parents.get(j).get(0),
												parent1ID, theParent);
									} else {
										Pattern p = Pattern.compile("IR");
										Matcher m1 = p.matcher(parents.get(j)
												.get(0));
										String[] tokens0_der = { "" };
										if (m1.lookingAt()) {
											tokens0_der = new Tokenize()
											.tokenize(parents.get(j)
													.get(0));

										} else {
											tokens0_der[0] = "";
										}
										ArrayList<String> pedigreeList_der = new ArrayList<String>();
										pedigreeList_der = saveToArray(
												pedigreeList_der, tokens0_der);

										List<List<String>> temp_fin = new ArrayList<List<String>>();
										JSONObject output2 = createPedigreeLine_CrossOp(
												pedigreeList_der, parent1ID,
												pedigreeList_der.get(0),
												parent1, temp_fin, GID);
										GID = (Integer) output2.get("GID");
										Germplasm g1 = manager
										.getGermplasmByGID(GID);
										setDerivativeLine_cross(
												pedigreeList_der, g1.getGid(),
												g1.getGpid1(), g1.getGpid2(),
												parent1ID, parents.get(j)
												.get(0));
										temp_fin.clear();
										output2.clear();
										pedigreeList_der.clear();
									}

								}

							}
						}
					}
					System.out.println("PARENTS: " + parents);

				} else {
					System.out.println("not the parent not the cross");
					List<Name> name = manager.getNamesByGID(gpid1_local, 0,
							null);
					List<Name> name1 = manager.getNamesByGID(gpid2_local, 0,
							null);

					updateCreatedGID("" + gpid1_local, parent1ID, name.get(0)
							.getNval(), "false", createdGID);
					updateCreatedGID("" + gpid2_local, parent1ID, name1.get(0)
							.getNval(), "false", createdGID);
					/*
					 * Germplasm g1=manager.getGermplasmByGID(gpid1_local);
					 * Germplasm g2=manager.getGermplasmByGID(gpid2_local);
					 * List<Name> name_gpid1 =
					 * manager.getNamesByGID(g1.getGpid1(), 0, null); List<Name>
					 * name_gpid2 = manager.getNamesByGID(g2.getGpid2(), 0,
					 * null);
					 * 
					 * System.out.println("gpid1: "+name_gpid1.get(0).getNval());
					 * System
					 * .out.println("gpid2: "+name_gpid2.get(0).getNval());
					 * for(int j=0; j< backcrosses.size();j++){
					 * if(backcrosses.get
					 * (j).get(0).equals(name_gpid1.get(0).getNval())){
					 * createGID_bc_backcrosses
					 * (parents,backcrosses,crosses,parents_bc
					 * ,createdGID,backcrosses.get(j).get(0),parent1ID,parent1,
					 * index_bc,g1.getGpid1(), g1.getGpid2()); }
					 * if(backcrosses.get
					 * (j).get(0).equals(name_gpid2.get(0).getNval())){
					 * createGID_bc_backcrosses
					 * (parents,backcrosses,crosses,parents_bc
					 * ,createdGID,backcrosses.get(j).get(0),parent1ID,parent1,
					 * index_bc,g2.getGpid1(), g2.getGpid2());
					 * 
					 * } } g1=null; g2=null; name_gpid1.clear();
					 * name_gpid2.clear(); name.clear(); name1.clear();
					 */
				}
			}
		}

		crosses = checkGID_crosses(parent1ID, crosses, theParent);

		germplasm.clear();
		germplasm_fin.clear();

		int max = 0;
		String slash = "";
		String[] tokens2;

		int fgid = 0, mgid = 0;
		String female = "";
		String male = "";
		int gid_crosses = 0;

		System.out.println("CROSS SIZE: " + crosses.size());
		System.out.println("CROSS : " + crosses);
		System.out.println("CROSS : " + crosses.get(0).get(0));
		if (!lastDeriv_parent.equals(crosses.get(0).get(0))) {
			System.out.println("index_cr: " + index_crosses);
			//System.out
			//		.println("index_cr: " + crosses.get(index_crosses).get(0));
			// System.out.println("index_cr: "+crosses.get(index_crosses-1).get(0));
			for (int j = index_crosses + 1; j < crosses.size(); j++) {
				System.out.println("\t:: " + crosses.get(j).get(0));
				if (j == crosses.size() - 1) {
					if (!crosses.get(j).get(1).equals("NOT SET")
							&& !crosses.get(j).get(1).equals("CHOOSE GID")
							&& !crosses.get(j).get(1).equals("0")) {
						gid_crosses = Integer.valueOf(crosses.get(j).get(1));
					} else {
						gid_crosses = gid_local;
					}

					Germplasm g1 = manager.getGermplasmByGID(gid_crosses);
					gid_local = g1.getGid();
					gpid1_local = g1.getGpid1();
					gpid2_local = g1.getGpid2();
					g1 = manager.getGermplasmByGID(gpid1_local);
					gid_local = g1.getGid();
					gpid1_local = g1.getGpid1();
					gpid2_local = g1.getGpid2();
					createdGID = updateCreatedGID("" + gid_local, parent1ID,
							backcrosses.get(0).get(0), "false", createdGID);
					g1 = manager.getGermplasmByGID(gpid1_local);
					gid_local = g1.getGid();
					gpid1_local = g1.getGpid1();
					gpid2_local = g1.getGpid2();
					update_backcrosses(createdGID, backcrosses, parents,
							parents_bc, parent1ID, parent1, 0, gid_local,
							gpid1_local, gpid2_local);
					g1 = null;

				} else {

				}

			}

			count_LOCAL = 0;
			count_CENTRAL = 0;
			count = 0;
			// get GID of parents
			for (int j = 0; j < parents.size(); j++) {
				for (int k = 0; k < createdGID_local.size(); k++) {
					if (createdGID_local.get(k).get(0).equals(parent1ID)
							&& createdGID_local.get(k).get(1).equals(theParent)
							&& createdGID_local.get(k).get(2)
							.equals(parents.get(j).get(0))) {
						if (!createdGID_local.get(k).get(3).equals("NOT SET")
								&& !createdGID_local.get(k).get(3)
								.equals("CHOOSE GID")) {
							parents.get(j).set(1,
									createdGID_local.get(k).get(3));
							System.out.println(parents.get(j).get(0)
									+ " has GID of :"
									+ createdGID_local.get(k).get(3));

						} else {
							System.out.println(parents.get(j).get(0)
									+ " is NOT SET :");
							germplasm_fin = new ArrayList<Germplasm>();
							germplasm = new ArrayList<Germplasm>();

							count_LOCAL = countGermplasmByName(parents.get(j)
									.get(0), Database.LOCAL);
							count_CENTRAL = countGermplasmByName(parents.get(j)
									.get(0), Database.CENTRAL);

							germplasm = getGermplasmList(parents.get(j).get(0),
									count_LOCAL, count_CENTRAL);
							System.out.println("gcount: " + count_LOCAL);
							for (int l = 0; l < germplasm.size(); l++) {
								if (germplasm.get(l).getLocationId()
										.equals(locationID)
										&& germplasm.get(l).getGrplce() == 0) {
									germplasm_fin.add(germplasm.get(l));
								}
							}
							count = germplasm_fin.size();
							if (count > 0) { // multiple hits
								System.out.println("\t Multiple HIT");
								System.out.println("\t i==0");
								// printChooseGID(pedigree, parent, id);
								updateCreatedGID("CHOOSE GID", parent1ID,
										parents.get(j).get(0), "false",
										createdGID_local);
								multipleHits_inLocation(germplasm_fin, parents
										.get(j).get(0), parent1ID, theParent);
							} else {
								System.out.println("create Pedigree Line");
								Pattern p = Pattern.compile("IR");
								Matcher m1 = p.matcher(parents.get(j).get(0));
								String[] tokens0_der = { "" };
								if (m1.lookingAt()) {
									tokens0_der = new Tokenize()
									.tokenize(parents.get(j).get(0));

								} else {
									tokens0_der[0] = "";
								}
								ArrayList<String> pedigreeList_der = new ArrayList<String>();
								pedigreeList_der = saveToArray(
										pedigreeList_der, tokens0_der);

								List<List<String>> temp_fin = new ArrayList<List<String>>();
								JSONObject output2 = createPedigreeLine_CrossOp(
										pedigreeList_der, parent1ID,
										pedigreeList_der.get(0), parent1,
										temp_fin, 0);
								GID = (Integer) output2.get("GID");
								pedigreeList_der = (ArrayList<String>) output2
								.get("pedList");

								System.out.println();
								if (GID == 0) {
									germplasm_fin = new ArrayList<Germplasm>();
									germplasm = new ArrayList<Germplasm>();

									count_LOCAL = countGermplasmByName(
											pedigreeList_der.get(pedigreeList_der
													.size() - 1),
													Database.LOCAL);
									count_CENTRAL = countGermplasmByName(
											pedigreeList_der.get(pedigreeList_der
													.size() - 1),
													Database.CENTRAL);

									germplasm = getGermplasmList(
											pedigreeList_der.get(pedigreeList_der
													.size() - 1), count_LOCAL,
													count_CENTRAL);
									System.out
									.println("gcount: " + count_LOCAL);
									for (int l = 0; l < germplasm.size(); l++) {
										if (germplasm.get(l).getLocationId()
												.equals(locationID)
												&& germplasm.get(l).getGrplce() == 0) {
											germplasm_fin.add(germplasm.get(l));
										}
									}
									count = germplasm_fin.size();
									if (count > 0) { // multiple hits
										System.out.println("\t Multiple HIT");
										System.out.println("\t i==0");
										// printChooseGID(pedigree, parent, id);
										updateCreatedGID("CHOOSE GID",
												parent1ID,
												pedigreeList_der
												.get(pedigreeList_der
														.size() - 1),
														"false", createdGID_local);
										multipleHits_inLocation(germplasm_fin,
												pedigreeList_der
												.get(pedigreeList_der
														.size() - 1),
														parent1ID, theParent);
									} else {
										System.out.println("create GID for "
												+ pedigreeList_der
												.get(pedigreeList_der
														.size() - 1));
										int methodID = selectMethodType_DER(
												pedigreeList_der.get(pedigreeList_der
														.size() - 1), parent1);

										gid_local = (int) addGID(
												pedigreeList_der.get(pedigreeList_der
														.size() - 1), 0, 0,
														methodID, 5, false);

										createdGID_local = updateFile_createdGID(
												"" + gid_local, parent1ID,
												pedigreeList_der
												.get(pedigreeList_der
														.size() - 1),
														"new", parent1); // update the
										// createdGID
										// file
										createdGID = createdGID_local;
										Germplasm g = manager
										.getGermplasmByGID(gid_local);

										getDerivativeLine(
												pedigreeList_der.size() - 1,
												pedigreeList_der, gid_local,
												g.getGpid1(), g.getGpid2(), ""
												+ gid_local,
												g.getGpid1(), g.getGpid2(),
												parent1ID, parent1, createdGID);
										g = null;
									}
								} else {

									System.out
									.println("update createdGID set derivative line");
									Germplasm g1 = manager
									.getGermplasmByGID(GID);
									System.out.println("pedList: "
											+ pedigreeList_der);
									Collections.reverse(pedigreeList_der);
									setDerivativeLine_cross(pedigreeList_der,
											g1.getGid(), g1.getGpid1(),
											g1.getGpid2(), parent1ID, parents
											.get(j).get(0));
									g1 = null;
								}
								temp_fin.clear();
								output2.clear();
								pedigreeList_der.clear();

							}

						}

					}
				}
			}

			System.out.println("PARENTS: " + parents);
			germplasm.clear();
			germplasm_fin.clear();
		}
		JSONObject result = new JSONObject();
		result.put("index", index_crosses);
		result.put("parents", parents);
		result.put("parents_bc", parents_bc);
		result.put("crosses", crosses);
		result.put("backcrosses", backcrosses);

		return result;
	}

	private List<List<String>> checkGID_parents(String parent1ID,
			List<List<String>> parents, String theParent) {
		for (int j = 0; j < parents.size(); j++) {
			for (int i = 0; i < createdGID_local.size(); i++) {
				if (createdGID_local.get(i).get(0).equals(parent1ID)
						&& createdGID_local.get(i).get(1).equals(theParent)
						&& createdGID_local.get(i).get(2)
						.equals(parents.get(j).get(0))) {
					if (!createdGID_local.get(i).get(3).equals("NOT SET")
							&& !createdGID_local.get(i).get(3)
							.equals("CHOOSE GID")) {
						parents.get(j).set(1, createdGID_local.get(i).get(3));
					}

				}
			}
		}
		return parents;
	}

	private List<List<String>> checkGID_crosses(String parent1ID,
			List<List<String>> crosses, String theParent) {
		// get GID of crosses
		for (int j = 0; j < crosses.size(); j++) {
			for (int i = 0; i < createdGID_local.size(); i++) {
				if (createdGID_local.get(i).get(0).equals(parent1ID)
						&& createdGID_local.get(i).get(1).equals(theParent)
						&& createdGID_local.get(i).get(2)
						.equals(crosses.get(j).get(0))) {
					if (!createdGID_local.get(i).get(3).equals("NOT SET")
							&& !createdGID_local.get(i).get(3)
							.equals("CHOOSE GID")) {
						crosses.get(j).set(1, createdGID_local.get(i).get(3));
					}

				}
			}
		}
		return crosses;
	}

	@SuppressWarnings("unchecked")
	private JSONObject createGID_bc_backcrosses(List<List<String>> parents,
			List<List<String>> backcrosses, List<List<String>> crosses,
			List<String> parents_bc, List<List<String>> createdGID,
			String lastDeriv_parent, String parent1ID, String parent1,
			int index_bc, int gpid1_local, int gpid2_local, int gid_local)
	throws MiddlewareQueryException, IOException, InterruptedException {
		int max = 0;
		String slash = "";
		String[] tokens2;
		int parent2GID = 0;
		String parent2 = "";
		int fgid = 0, mgid = 0;
		String female = "";
		String male = "";
		String dose = "";
		String tokens_dose[];
		System.out.println("lastDer: " + lastDeriv_parent);
		/*System.out.println("backcrosses.get(i).get(0): "
				+ backcrosses.get(0).get(0));
		System.out.println("backcrosses.get(i).get(last): "
				+ backcrosses.get(backcrosses.size() - 1).get(0));
		 */
		for (int i = 0; i < backcrosses.size(); i++) {

			System.out.println("\t:: " + backcrosses.get(i).get(0));
			if (backcrosses.get(i).get(0).equals(lastDeriv_parent)) {
				index_bc = i;
				new CrossOp();
				max = CrossOp.maxCross(max, backcrosses.get(i).get(0));
				new BackCross();
				slash = BackCross.printSlash(max);
				tokens2 = backcrosses.get(i).get(0).split("\\" + slash);

				if (i == backcrosses.size() - 1) {
					System.out.println("\t:: token 0: " + tokens2[0]);
					System.out.println("\t:: token 1: " + tokens2[1]);
					createdGID = updateCreatedGID("" + gpid1_local, parent1ID,
							tokens2[0], "false", createdGID);
					createdGID = updateCreatedGID("" + gpid2_local, parent1ID,
							tokens2[1], "false", createdGID);
					for (int j = 0; j < parents.size(); j++) {

						if (parents.get(j).get(0).equals(tokens2[0])) {
							parents.get(j).set(1, "" + gpid1_local);
						}

						if (parents.get(j).get(0).equals(tokens2[1])) {
							parents.get(j).set(1, "" + gpid2_local);

						}

					}

					Germplasm g = manager.getGermplasmByGID(gpid1_local);
					Pattern p = Pattern.compile("IR");
					Matcher m1 = p.matcher(tokens2[0]);
					String[] tokens0_der = { "" };
					if (m1.lookingAt()) {
						tokens0_der = new Tokenize().tokenize(tokens2[0]);

					} else {
						tokens0_der[0] = "";
					}
					ArrayList<String> pedigreeList_der = new ArrayList<String>();
					pedigreeList_der = saveToArray(pedigreeList_der,
							tokens0_der);
					System.out.println("get the derivative line.." + tokens2[0]
					                                                         + " GID: " + gpid1_local);
					createdGID = getDerivativeLine(0, pedigreeList_der,
							gpid1_local, g.getGpid1(), g.getGpid2(), ""
							+ gpid1_local, g.getGpid1(), g.getGpid2(),
							parent1ID, parent1, createdGID);

					createdGID_local = createdGID;
					g = null;

					g = manager.getGermplasmByGID(gpid2_local);
					p = Pattern.compile("IR");
					m1 = p.matcher(tokens2[1]);
					String[] tokens1_der = { "" };
					if (m1.lookingAt()) {
						tokens1_der = new Tokenize().tokenize(tokens2[1]);

					} else {
						tokens1_der[0] = "";
					}
					pedigreeList_der = new ArrayList<String>();
					pedigreeList_der = saveToArray(pedigreeList_der,
							tokens1_der);
					System.out.println("get the derivative line.." + tokens2[1]
					                                                         + " GID: " + gpid2_local);
					createdGID = getDerivativeLine(0, pedigreeList_der,
							gpid2_local, g.getGpid1(), g.getGpid2(), ""
							+ gpid2_local, g.getGpid1(), g.getGpid2(),
							parent1ID, parent1, createdGID);

					createdGID_local = createdGID;
					g = null;
				} else {
					System.out.println("tokens 1: " + tokens2[0]);
					System.out.println("tokens 2: " + tokens2[1]);

					if (tokens2[0].contains("*")) {

						tokens_dose = tokens2[0].split("\\*", 2);
						dose = tokens_dose[1];

						System.out.println("dose: " + dose);

					} else {
						tokens_dose = tokens2[1].split("\\*", 2);
						dose = tokens_dose[0];

					}
					createdGID = updateCreatedGID("" + gpid1_local, parent1ID,
							backcrosses.get(i + 1).get(0), "false",
							createdGID_local);
					createdGID = updateCreatedGID("" + gpid2_local, parent1ID,
							parents_bc.get(0), "false", createdGID_local);
					createdGID_local = createdGID;
					Germplasm g = manager.getGermplasmByGID(gpid2_local);
					for (int j = 0; j < parents.size(); j++) {
						if (parents.get(j).get(0).equals(parents_bc.get(0))) {
							parents.get(j).set(1, "" + gpid2_local);

							Pattern p = Pattern.compile("IR");
							Matcher m1 = p.matcher(parents_bc.get(0));
							String[] tokens_der = { "" };
							if (m1.lookingAt()) {
								tokens_der = new Tokenize().tokenize(parents_bc
										.get(0));

							} else {
								tokens_der[0] = "";
							}
							ArrayList<String> pedigreeList_der = new ArrayList<String>();
							pedigreeList_der = saveToArray(pedigreeList_der,
									tokens2);

							createdGID = getDerivativeLine(0, pedigreeList_der,
									gpid2_local, g.getGpid1(), g.getGpid2(), ""
									+ gpid2_local, g.getGpid1(),
									g.getGpid2(), parent1ID, parent1,
									createdGID_local);

							createdGID_local = createdGID;
						}
					}

				}

			}

		}
		System.out.println("index bc: " + index_bc);
		if (index_bc > -1) {
			System.out.println("update the parents");

			createdGID = update_backcrosses(createdGID, backcrosses, parents,
					parents_bc, parent1ID, parent1, index_bc, gid_local,
					gpid1_local, gpid2_local);

		}
		System.out.println("INDEX_bc: " + index_bc);
		if (index_bc > -1) {

			int methodID = 0;
			int new_gid = gid_local;
			parent2GID = gid_local;
			parent2 = parents_bc.get(0);
			for (int i = 0; i < parents.size(); i++) {
				if (parents_bc.get(0).equals(parents.get(i).get(0))) {
					parent2GID = Integer.valueOf(parents.get(i).get(1));
					parent2 = parents.get(i).get(0);
				}
			}

			for (int j = index_bc - 1; j >= 0; j--) {
				System.out
				.println("::" + j + ":: " + backcrosses.get(j).get(0));

				if (j == backcrosses.size() - 1) {
					System.out.println();
					max = new CrossOp()
					.maxCross(max, backcrosses.get(j).get(0));
					slash = new BackCross().printSlash(max);
					tokens2 = backcrosses.get(j).get(0).split("\\" + slash);

					for (int i = 0; i < parents.size(); i++) {
						if (parents_bc.get(0).equals(parents.get(i).get(0))) {
							parent2GID = Integer.valueOf(parents.get(i).get(1));
							parent2 = parents.get(i).get(0);
						}
						if (parents.get(i).get(0).equals(tokens2[0])
								|| parents.get(i).get(0).equals(tokens2[1])) {
							if (parents.get(i).get(0).equals(tokens2[0])) {
								fgid = Integer.valueOf(parents.get(i).get(1));
								female = tokens2[0];
								// fgid=manager.getGermplasmByGID(Integer.valueOf(female));
							} else {
								mgid = Integer.valueOf(parents.get(i).get(1));
								male = tokens2[1];
								// mgid=manager.getGermplasmByGID(Integer.valueOf(male));
							}

						}
					}
					methodID = selectMethodType(fgid, mgid, female, male,
							backcrosses.get(j).get(0), 0);
					new_gid = addGID(backcrosses.get(j).get(0), fgid, mgid,
							methodID, 3, false);
					createdGID = updateCreatedGID("" + new_gid, parent1ID,
							backcrosses.get(j).get(0), "new", createdGID);
					createdGID_local = createdGID;
					backcrosses.get(j).set(1, "" + new_gid);
					backcrosses.set(j, backcrosses.get(j));

				} else {
					System.out.println();
					methodID = selectMethodType(new_gid, parent2GID,
							backcrosses.get(j + 1).get(0), parent2, backcrosses
							.get(j).get(0), 107);
					new_gid = addGID(backcrosses.get(j).get(0), new_gid,
							parent2GID, methodID, 3, false);
					createdGID = updateCreatedGID("" + new_gid, parent1ID,
							backcrosses.get(j).get(0), "new", createdGID);
					createdGID_local = createdGID;
					backcrosses.get(j).set(1, "" + new_gid);
					backcrosses.set(j, backcrosses.get(j));

				}

			}
		}
		createdGID_local = createdGID;

		// get GID of crosses
		crosses = checkGID_crosses(parent1ID, crosses, parent1);
		int count_cross_exist = 0;
		for (int j = 0; j < crosses.size(); j++) {
			if (!crosses.get(j).get(1).equals("0")
					&& !crosses.get(j).get(1).equals("NOT SET")
					&& !crosses.get(j).get(1).equals("CHOOSE GID")) {
				count_cross_exist++;
			}
		}

		if (count_cross_exist != crosses.size()) {
			// Collections.reverse(crosses);
			String theParent = parent1;
			int count_LOCAL = 0;
			int count_CENTRAL = 0;
			int count = 0;
			List<Germplasm> germplasm_fin;
			List<Germplasm> germplasm;
			// get GID of parents
			for (int j = 0; j < parents.size(); j++) {
				for (int k = 0; k < createdGID_local.size(); k++) {
					if (createdGID_local.get(k).get(0).equals(parent1ID)
							&& createdGID_local.get(k).get(1).equals(theParent)
							&& createdGID_local.get(k).get(2)
							.equals(parents.get(j).get(0))) {
						if (!createdGID_local.get(k).get(3).equals("NOT SET")
								&& !createdGID_local.get(k).get(3)
								.equals("CHOOSE GID")) {
							parents.get(j).set(1,
									createdGID_local.get(k).get(3));
							System.out.println(parents.get(j).get(0)
									+ " has GID of :"
									+ createdGID_local.get(k).get(3));

						} else {
							System.out.println(parents.get(j).get(0)
									+ " is NOT SET :");
							germplasm_fin = new ArrayList<Germplasm>();
							germplasm = new ArrayList<Germplasm>();

							count_LOCAL = countGermplasmByName(parents.get(j)
									.get(0), Database.LOCAL);
							count_CENTRAL = countGermplasmByName(parents.get(j)
									.get(0), Database.CENTRAL);

							germplasm = getGermplasmList(parents.get(j).get(0),
									count_LOCAL, count_CENTRAL);
							System.out.println("gcount: " + count_LOCAL);
							for (int l = 0; l < germplasm.size(); l++) {
								if (germplasm.get(l).getLocationId()
										.equals(locationID)
										&& germplasm.get(l).getGrplce() == 0) {
									germplasm_fin.add(germplasm.get(l));
								}
							}
							count = germplasm_fin.size();
							if (count > 0) { // multiple hits
								System.out.println("\t Multiple HIT");
								System.out.println("\t i==0");
								// printChooseGID(pedigree, parent, id);
								updateCreatedGID("CHOOSE GID", parent1ID,
										parents.get(j).get(0), "false",
										createdGID_local);
								multipleHits_inLocation(germplasm_fin, parents
										.get(j).get(0), parent1ID, theParent);
							} else {
								System.out.println("create Pedigree Line");
								Pattern p = Pattern.compile("IR");
								Matcher m1 = p.matcher(parents.get(j).get(0));
								String[] tokens0_der = { "" };
								if (m1.lookingAt()) {
									tokens0_der = new Tokenize()
									.tokenize(parents.get(j).get(0));

								} else {
									tokens0_der[0] = "";
								}
								ArrayList<String> pedigreeList_der = new ArrayList<String>();
								pedigreeList_der = saveToArray(
										pedigreeList_der, tokens0_der);

								List<List<String>> temp_fin = new ArrayList<List<String>>();
								JSONObject output2 = createPedigreeLine_CrossOp(
										pedigreeList_der, parent1ID,
										pedigreeList_der.get(0), parent1,
										temp_fin, 0);
								GID = (Integer) output2.get("GID");
								pedigreeList_der = (ArrayList<String>) output2
								.get("pedList");

								System.out.println();
								if (GID == 0) {
									germplasm_fin = new ArrayList<Germplasm>();
									germplasm = new ArrayList<Germplasm>();

									count_LOCAL = countGermplasmByName(
											pedigreeList_der.get(pedigreeList_der
													.size() - 1),
													Database.LOCAL);
									count_CENTRAL = countGermplasmByName(
											pedigreeList_der.get(pedigreeList_der
													.size() - 1),
													Database.CENTRAL);

									germplasm = getGermplasmList(
											pedigreeList_der.get(pedigreeList_der
													.size() - 1), count_LOCAL,
													count_CENTRAL);
									System.out
									.println("gcount: " + count_LOCAL);
									for (int l = 0; l < germplasm.size(); l++) {
										if (germplasm.get(l).getLocationId()
												.equals(locationID)
												&& germplasm.get(l).getGrplce() == 0) {
											germplasm_fin.add(germplasm.get(l));
										}
									}
									count = germplasm_fin.size();
									if (count > 0) { // multiple hits
										System.out.println("\t Multiple HIT");
										System.out.println("\t i==0");
										// printChooseGID(pedigree, parent, id);
										updateCreatedGID("CHOOSE GID",
												parent1ID,
												pedigreeList_der
												.get(pedigreeList_der
														.size() - 1),
														"false", createdGID_local);
										multipleHits_inLocation(germplasm_fin,
												pedigreeList_der
												.get(pedigreeList_der
														.size() - 1),
														parent1ID, theParent);
									} else {
										System.out.println("create GID for "
												+ pedigreeList_der
												.get(pedigreeList_der
														.size() - 1));
										int methodID = selectMethodType_DER(
												pedigreeList_der.get(pedigreeList_der
														.size() - 1), parent1);

										gid_local = (int) addGID(
												pedigreeList_der.get(pedigreeList_der
														.size() - 1), 0, 0,
														methodID, 5, false);

										createdGID_local = updateFile_createdGID(
												"" + gid_local, parent1ID,
												pedigreeList_der
												.get(pedigreeList_der
														.size() - 1),
														"new", parent1); // update the
										// createdGID
										// file
										createdGID = createdGID_local;
										Germplasm g = manager
										.getGermplasmByGID(gid_local);

										getDerivativeLine(
												pedigreeList_der.size() - 1,
												pedigreeList_der, gid_local,
												g.getGpid1(), g.getGpid2(), ""
												+ gid_local,
												g.getGpid1(), g.getGpid2(),
												parent1ID, parent1, createdGID);
										g = null;
									}
								} else {

									System.out
									.println("update createdGID set derivative line");
									Germplasm g1 = manager
									.getGermplasmByGID(GID);
									System.out.println("pedList: "
											+ pedigreeList_der);
									Collections.reverse(pedigreeList_der);
									setDerivativeLine_cross(pedigreeList_der,
											g1.getGid(), g1.getGpid1(),
											g1.getGpid2(), parent1ID, parents
											.get(j).get(0));
									g1 = null;
								}
								temp_fin.clear();
								output2.clear();
								pedigreeList_der.clear();

							}

						}

					}

				}

			}

			System.out.println("PARENTS: " + parents);
			parents = checkGID_parents(parent1ID, parents, parent1);
			System.out.println("PARENTS: " + parents);
			int count_parents_exist = 0;
			for (int j = 0; j < parents.size(); j++) {
				System.out.println(":: " + parents.get(j));
				if (!parents.get(j).get(1).equals("0")
						&& !parents.get(j).get(1).equals("NOT SET")
						&& !parents.get(j).get(1).equals("CHOOSE GID")) {
					count_parents_exist++;
				}
			}
			if (count_parents_exist == parents.size()) {
				createdGID = create_crosses(parents, crosses, createdGID,
						parent1ID, crosses.size() - 1, parent1);
			}
			// Collections.reverse(crosses);
			germplasm = null;
			germplasm_fin = null;
		}
		createdGID_local = createdGID;
		/*
		 * System.out.println("------------------"); for(int i=0;
		 * i<createdGID_local.size(); i++){ System.out.println("::::::"+
		 * createdGID.get(i)); } System.out.println("------------------");
		 */

		JSONObject result = new JSONObject();
		result.put("index", index_bc);
		result.put("parents", parents);
		result.put("parents_bc", parents_bc);
		result.put("crosses", crosses);
		result.put("backcrosses", backcrosses);

		return result;

	}

	private List<List<String>> update_backcrosses(
			List<List<String>> createdGID, List<List<String>> backcrosses,
			List<List<String>> parents, List<String> parents_bc,
			String parent1ID, String parent1, int index_bc, int gid_local,
			int gpid1_local, int gpid2_local) throws NumberFormatException,
			MiddlewareQueryException, IOException, InterruptedException {
		int max = 0;
		String slash = "";
		String[] tokens2;
		int parent2GID = 0;
		String parent2 = "";
		int fgid = 0, mgid = 0;
		String female = "";
		String male = "";
		String dose = "";
		String tokens_dose[];
		// System.out.println("@ index: "+backcrosses.get(backcrosses.size()-1)
		// );
		// System.out.println("@ index: "+backcrosses.get(index_bc) );
		for (int i = index_bc; i < backcrosses.size(); i++) {
			// System.out.println("\t:: "+backcrosses.get(i).get(0) );
			max = new CrossOp().maxCross(max, backcrosses.get(i).get(0));
			slash = new BackCross().printSlash(max);
			// System.out.println("\tslash: "+slash );
			tokens2 = backcrosses.get(i).get(0).split("\\" + slash, 2);
			if (i == backcrosses.size() - 1) {
				createdGID = updateCreatedGID("" + gpid1_local, parent1ID,
						tokens2[0], "false", createdGID);
				createdGID_local = createdGID;
				createdGID = updateCreatedGID("" + gpid2_local, parent1ID,
						tokens2[1], "false", createdGID);
				createdGID_local = createdGID;

				for (int j = 0; j < parents.size(); j++) {

					if (parents.get(j).get(0).equals(tokens2[0])) {
						parents.get(j).set(1, "" + gpid1_local);
					}

					if (parents.get(j).get(0).equals(tokens2[1])) {
						parents.get(j).set(1, "" + gpid2_local);
					}

				}
				// System.out.println("0: "+tokens2[0]+ "gid: "+ gpid1_local);
				// System.out.println("1: "+tokens2[1]+ "gid: "+ gpid2_local);
				Germplasm g = manager.getGermplasmByGID(gpid1_local);
				Pattern p = Pattern.compile("IR");
				Matcher m1 = p.matcher(tokens2[0]);
				String[] tokens0_der = { "" };
				if (m1.lookingAt()) {
					tokens0_der = new Tokenize().tokenize(tokens2[0]);

				} else {
					tokens0_der[0] = "";
				}
				ArrayList<String> pedigreeList_der = new ArrayList<String>();
				pedigreeList_der = saveToArray(pedigreeList_der, tokens0_der);

				createdGID = getDerivativeLine(0, pedigreeList_der,
						gpid1_local, g.getGpid1(), g.getGpid2(), ""
						+ gpid2_local, g.getGpid1(), g.getGpid2(),
						parent1ID, parent1, createdGID);

				createdGID_local = createdGID;
				g = null;

				g = manager.getGermplasmByGID(gpid2_local);
				p = Pattern.compile("IR");
				m1 = p.matcher(tokens2[1]);
				String[] tokens1_der = { "" };
				if (m1.lookingAt()) {
					tokens1_der = new Tokenize().tokenize(tokens2[1]);

				} else {
					tokens1_der[0] = "";
				}
				pedigreeList_der = new ArrayList<String>();
				pedigreeList_der = saveToArray(pedigreeList_der, tokens1_der);

				createdGID = getDerivativeLine(0, pedigreeList_der,
						gpid2_local, g.getGpid1(), g.getGpid2(), ""
						+ gpid2_local, g.getGpid1(), g.getGpid2(),
						parent1ID, parent1, createdGID);

				createdGID_local = createdGID;
				g = null;
			} else {

				if (tokens2[0].contains("*")) {

					tokens_dose = tokens2[0].split("\\*", 2);
					dose = tokens_dose[1];

					// System.out.println("dose: "+dose);

				} else {
					tokens_dose = tokens2[1].split("\\*", 2);
					dose = tokens_dose[0];

					// System.out.println("dose: "+dose);
				}
				createdGID = updateCreatedGID("" + gpid1_local, parent1ID,
						backcrosses.get(i + 1).get(0), "false", createdGID);
				createdGID_local = createdGID;
				createdGID = updateCreatedGID("" + gpid2_local, parent1ID,
						parents_bc.get(0), "false", createdGID);
				createdGID_local = createdGID;
				for (int j = 0; j < parents.size(); j++) {
					if (parents.get(j).get(0).equals(parents_bc.get(0))) {
						parents.get(j).set(1, "" + gpid2_local);
					}
				}

				Germplasm g = manager.getGermplasmByGID(gpid2_local);
				Pattern p = Pattern.compile("IR");
				Matcher m1 = p.matcher(parents_bc.get(0));
				String[] tokens_der = { "" };
				if (m1.lookingAt()) {
					tokens_der = new Tokenize().tokenize(parents_bc.get(0));

				} else {
					tokens_der[0] = "";
				}
				ArrayList<String> pedigreeList_der = new ArrayList<String>();
				pedigreeList_der = saveToArray(pedigreeList_der, tokens_der);

				createdGID = getDerivativeLine(0, pedigreeList_der,
						gpid2_local, g.getGpid1(), g.getGpid2(), ""
						+ gpid2_local, g.getGpid1(), g.getGpid2(),
						parent1ID, parent1, createdGID);

				createdGID_local = createdGID;
				g = null;

			}

		}
		return createdGID;

	}

	private void createGID_bc_checkBC(List<List<String>> parents,
			List<List<String>> backcrosses, List<List<String>> crosses,
			List<String> parents_bc, List<List<String>> createdGID,
			String lastDeriv_parent, String parent1ID, String theParent)
	throws MiddlewareQueryException, IOException {
		int max = 0;
		String slash = "";
		String[] tokens2;
		int parent2GID = 0;
		String parent2 = "";
		int fgid = 0, mgid = 0;
		String female = "";
		String male = "";
		String dose = "";
		String tokens_dose[];
		System.out.println("@ index: "
				+ backcrosses.get(backcrosses.size() - 1));
		System.out.println("@ index: " + backcrosses.get(0));
		for (int i = 0; i < backcrosses.size(); i++) {
			if (i == backcrosses.size() - 1) {
				/*
				 * for(int j=0; j< parents.size();j++){
				 * 
				 * if(parents.get(j).get(0).equals(tokens2[0])){
				 * parents.get(j).set(1, ""+gpid1_local); }
				 * 
				 * if(parents.get(j).get(0).equals(tokens2[1])){
				 * parents.get(j).set(1, ""+gpid2_local); }
				 * 
				 * }
				 */
			} else {
				// female
				List<Germplasm> germplasm = new ArrayList<Germplasm>();
				List<Germplasm> germplasm_fin = new ArrayList<Germplasm>();

				int count_LOCAL = countGermplasmByName(backcrosses.get(i + 1)
						.get(0), Database.LOCAL);
				int count_CENTRAL = countGermplasmByName(backcrosses.get(i + 1)
						.get(0), Database.CENTRAL);

				germplasm = getGermplasmList(backcrosses.get(i + 1).get(0),
						count_LOCAL, count_CENTRAL);
				System.out.println("gsize: " + germplasm.size());

				for (int j = 0; j < germplasm.size(); j++) {
					if (germplasm.get(j).getLocationId().equals(locationID)
							&& germplasm.get(j).getGrplce() == 0) {
						germplasm_fin.add(germplasm.get(j));
					}
				}

				if (germplasm_fin.size() > 0) {
					updateCreatedGID("CHOOSE GID", parent1ID,
							backcrosses.get(i + 1).get(0), "false", createdGID);
					multipleHits_inLocation(germplasm_fin,
							backcrosses.get(i + 1).get(0), parent1ID, theParent);
				} else {

				}
				;

			}

		}
	}

	private void createGID_bc_checkbackcrosses(List<List<String>> parents,
			List<List<String>> backcrosses, List<List<String>> crosses,
			List<String> parents_bc, List<List<String>> createdGID,
			String lastDeriv_parent, String parent1ID)
	throws MiddlewareQueryException, IOException {
		int index_crossOp = 0;
		String[] tokens2;
		String female = "", male = "";
		// Germplasm fgid,mgid;
		int fgid = 0, mgid = 0;
		int methodID;
		int new_gid = 0;
		int max = 0;
		int parent2GID = 0;
		String parent2 = "";
		String slash = "";

		System.out.println("CREATE BACKCROSS------");
		System.out.println("PARENTS ------");
		System.out.println("\t" + parents);
		System.out.println("PARENTS BC------");
		System.out.println("\t" + parents_bc);
		System.out.println("BACKCROSS------");
		for (int j = backcrosses.size() - 1; j >= 0; j--) {
			System.out.println("::" + backcrosses.get(j).get(0));

			if (index_crossOp == 0) {
				max = new CrossOp().maxCross(max, backcrosses.get(j).get(0));
				slash = new BackCross().printSlash(max);
				tokens2 = backcrosses.get(j).get(0).split("\\" + slash);

				for (int i = 0; i < parents.size(); i++) {
					if (parents_bc.get(0).equals(parents.get(i).get(0))) {
						parent2GID = Integer.valueOf(parents.get(i).get(1));
						parent2 = parents.get(i).get(0);
					}
					if (parents.get(i).get(0).equals(tokens2[0])
							|| parents.get(i).get(0).equals(tokens2[1])) {
						if (parents.get(i).get(0).equals(tokens2[0])) {
							fgid = Integer.valueOf(parents.get(i).get(1));
							female = tokens2[0];
							// fgid=manager.getGermplasmByGID(Integer.valueOf(female));
						} else {
							mgid = Integer.valueOf(parents.get(i).get(1));
							male = tokens2[1];
							// mgid=manager.getGermplasmByGID(Integer.valueOf(male));
						}

					}
				}
				methodID = selectMethodType(fgid, mgid, female, male,
						backcrosses.get(j).get(0), 0);
				new_gid = addGID(backcrosses.get(j).get(0), fgid, mgid,
						methodID, 3, false);
				// crossesGID=printSuccess_temp(backcrosses.get(j).get(0),
				// theParent, parent1ID, manager.getGermplasmByGID(new_gid),
				// "new", crossesGID);
				updateCreatedGID("" + new_gid, parent1ID, backcrosses.get(j)
						.get(0), "new", createdGID);
				backcrosses.get(j).set(1, "" + new_gid);
				backcrosses.set(j, backcrosses.get(j));

			} else {
				methodID = selectMethodType(new_gid, parent2GID, backcrosses
						.get(j + 1).get(0), parent2, backcrosses.get(j).get(0),
						107);
				new_gid = addGID(backcrosses.get(j).get(0), new_gid,
						parent2GID, methodID, 3, false);
				// crossesGID=printSuccess_temp(backcrosses.get(j).get(0),
				// theParent, parentID, manager.getGermplasmByGID(new_gid),
				// "new", crossesGID);
				updateCreatedGID("" + new_gid, parent1ID, backcrosses.get(j)
						.get(0), "new", createdGID);
				backcrosses.get(j).set(1, "" + new_gid);
				backcrosses.set(j, backcrosses.get(j));

			}
			index_crossOp++;
		}

		for (int j = crosses.size() - 1; j >= 0; j--) {
			if (j == crosses.size() - 1) {
				methodID = selectMethodType(new_gid, parent2GID, backcrosses
						.get(0).get(0), parent2, crosses.get(j).get(0), 0);
				new_gid = addGID(crosses.get(j).get(0), new_gid, parent2GID,
						methodID, 3, false);
				// crossesGID=printSuccess_temp(crosses.get(j).get(0), parent,
				// id, manager.getGermplasmByGID(gid), "new", crossesGID);
				updateCreatedGID("" + new_gid, parent1ID,
						crosses.get(j).get(0), "new", createdGID);

				crosses.get(j).set(1, "" + new_gid);
				crosses.set(j, crosses.get(j));
			} else {
				max = new CrossOp().maxCross(max, crosses.get(j).get(0));
				slash = new BackCross().printSlash(max);
				tokens2 = crosses.get(j).get(0).split("\\" + slash, 2);
				System.out.print("tokens2[0]: " + tokens2[0]);
				System.out.println("\ttokens2[1]: " + tokens2[1]);
				if (!tokens2[0].contains("/")) {
					if (tokens2[0].contains("*")) {
						Pattern p2 = Pattern.compile("\\*\\d"); // backcross to
						// female
						Matcher m2 = p2.matcher(tokens2[0]);
						Boolean female_match = false;
						while (m2.find()) {
							female_match = true;
							tokens2[0] = tokens2[0].replaceAll("\\*\\d", "");
							System.out.println("new tokens2[0]: " + tokens2[0]);
						}
						if (!female_match) {
							tokens2[0] = tokens2[0].replaceAll("\\d\\*", "");
							System.out.println("\\d\\*: " + tokens2[0]);
						}
					}
					for (int i = 0; i < parents.size(); i++) {

						if (parents.get(i).get(0).equals(tokens2[0])) {
							fgid = Integer.parseInt(parents.get(i).get(1));
							female = tokens2[0];
						}
					}
				} else {
					for (int i = 0; i < crosses.size(); i++) {
						if (crosses.get(i).get(0).equals(tokens2[0])) {
							fgid = Integer.parseInt(crosses.get(i).get(1));
							female = tokens2[0];
						}
					}

				}
				System.out.println("[1]: " + tokens2[1]);
				if (!tokens2[1].contains("/")) {
					if (tokens2[1].contains("*")) {
						Pattern p2 = Pattern.compile("\\*\\d"); // backcross to
						// female
						Matcher m2 = p2.matcher(tokens2[1]);
						Boolean female_match = false;
						while (m2.find()) {
							female_match = true;
							tokens2[1] = tokens2[1].replaceAll("\\*\\d", "");
							System.out.println("new tokens2[1]: " + tokens2[1]);
						}
						if (!female_match) {
							tokens2[1] = tokens2[1].replaceAll("\\d\\*", "");
							System.out.println("\\d\\*: " + tokens2[1]);
						}
					}
					for (int i = 0; i < parents.size(); i++) {
						if (parents.get(i).get(0).equals(tokens2[1])) {
							mgid = Integer.parseInt(parents.get(i).get(1));
							male = tokens2[1];
						}
					}
				} else {
					for (int i = 0; i < crosses.size(); i++) {
						System.out.println("crosses: " + crosses.get(i).get(0));
						if (crosses.get(i).get(0).equals(tokens2[1])) {
							System.out.println("ditooo "
									+ crosses.get(i).get(1));
							mgid = Integer.parseInt(crosses.get(i).get(1));
							male = tokens2[1];
						}
					}
				}
				System.out.println("mgid: " + mgid);
				System.out.println("fgid: " + fgid);
				Germplasm g1 = manager.getGermplasmByGID(fgid);
				List<Name> name = manager.getNamesByGID(g1.getGpid1(), 0, null);
				List<Name> name1 = manager
				.getNamesByGID(g1.getGpid2(), 0, null);
				methodID = 0;
				if (name.get(0).getNval().equals(male)
						|| name1.get(0).getNval().equals(male)) {
					methodID = 107;
				}
				name.clear();
				name1.clear();
				g1 = null;

				methodID = selectMethodType(fgid, mgid, female, male, crosses
						.get(j).get(0), methodID);
				new_gid = addGID(crosses.get(j).get(0), fgid, mgid, methodID,
						3, false);
				// crossesGID=printSuccess_temp(crosses.get(j).get(0), parent,
				// id, manager.getGermplasmByGID(gid), "new", crossesGID);
				updateCreatedGID("" + new_gid, parent1ID,
						crosses.get(j).get(0), "new", createdGID);
				crosses.get(j).set(1, "" + new_gid);
				crosses.set(j, crosses.get(j));
				if (j == 0) {
					GID = new_gid;
				}
			}
		}

	}

	private void createGID_bc_backcrosses(List<List<String>> parents,
			List<List<String>> backcrosses, List<List<String>> crosses,
			List<String> parents_bc, List<List<String>> createdGID,
			String lastDeriv_parent, String parent1ID)
	throws MiddlewareQueryException, IOException {
		// int index_crossOp=0;
		String[] tokens2;
		String female = "", male = "";
		// Germplasm fgid,mgid;
		int fgid = 0, mgid = 0;
		int methodID;
		int new_gid = 0;
		int max = 0;
		int parent2GID = 0;
		String parent2 = "";
		String slash = "";

		System.out.println("CREATE BACKCROSS------ createGID_bc_backcrosses");
		System.out.println("PARENTS ------");
		System.out.println("\t" + parents);
		System.out.println("PARENTS BC------");
		System.out.println("\t" + parents_bc);
		System.out.println("BACKCROSS------");
		for (int j = backcrosses.size() - 1; j >= 0; j--) {
			System.out.println("::" + backcrosses.get(j).get(0));

			if (j == backcrosses.size() - 1) {
				max = new CrossOp().maxCross(max, backcrosses.get(j).get(0));
				slash = new BackCross().printSlash(max);
				tokens2 = backcrosses.get(j).get(0).split("\\" + slash);

				for (int i = 0; i < parents.size(); i++) {
					if (parents_bc.get(0).equals(parents.get(i).get(0))) {
						parent2GID = Integer.valueOf(parents.get(i).get(1));
						parent2 = parents.get(i).get(0);
					}
					if (parents.get(i).get(0).equals(tokens2[0])
							|| parents.get(i).get(0).equals(tokens2[1])) {
						fgid = Integer.valueOf(parents.get(i).get(1));
						female = tokens2[0];
						// fgid=manager.getGermplasmByGID(Integer.valueOf(female));
					}
					if (parents.get(i).get(0).equals(tokens2[1])) {
						mgid = Integer.valueOf(parents.get(i).get(1));
						male = tokens2[1];
					}
					// mgid=manager.getGermplasmByGID(Integer.valueOf(male));
				}
				methodID = selectMethodType(fgid, mgid, female, male,
						backcrosses.get(j).get(0), 0);
				new_gid = addGID(backcrosses.get(j).get(0), fgid, mgid,
						methodID, 3, false);
				// crossesGID=printSuccess_temp(backcrosses.get(j).get(0),
				// theParent, parent1ID, manager.getGermplasmByGID(new_gid),
				// "new", crossesGID);
				updateCreatedGID("" + new_gid, parent1ID, backcrosses.get(j)
						.get(0), "new", createdGID);
				backcrosses.get(j).set(1, "" + new_gid);
				backcrosses.set(j, backcrosses.get(j));

			} else {
				methodID = selectMethodType(new_gid, parent2GID, backcrosses
						.get(j + 1).get(0), parent2, backcrosses.get(j).get(0),
						107);
				new_gid = addGID(backcrosses.get(j).get(0), new_gid,
						parent2GID, methodID, 3, false);
				// crossesGID=printSuccess_temp(backcrosses.get(j).get(0),
				// theParent, parentID, manager.getGermplasmByGID(new_gid),
				// "new", crossesGID);
				updateCreatedGID("" + new_gid, parent1ID, backcrosses.get(j)
						.get(0), "new", createdGID);
				backcrosses.get(j).set(1, "" + new_gid);
				backcrosses.set(j, backcrosses.get(j));

			}

		}
		System.out.println("CROSSES------");
		System.out.println("\t" + crosses);
		System.out.println("create crosses");
		for (int j = crosses.size() - 1; j >= 0; j--) {
			System.out.println("\t::" + crosses.get(j).get(0));
			if (j == crosses.size() - 1) {
				if(backcrosses.size()==0){
					/*for(int i=0;i<parents.size(); i++){
						if(parents.get(i).get(0).equals() )){
							
						}
					}
					*/
				
					
					methodID = selectMethodType(new_gid, parent2GID, parents
							.get(0).get(0), parent2, crosses.get(j).get(0), 0);
				}else{
				methodID = selectMethodType(new_gid, parent2GID, backcrosses
						.get(0).get(0), parent2, crosses.get(j).get(0), 107);
				}
						
				new_gid = addGID(crosses.get(j).get(0), new_gid, parent2GID,
						methodID, 3, false);
				// crossesGID=printSuccess_temp(crosses.get(j).get(0), parent,
				// id, manager.getGermplasmByGID(gid), "new", crossesGID);
				updateCreatedGID("" + new_gid, parent1ID,
						crosses.get(j).get(0), "new", createdGID);

				crosses.get(j).set(1, "" + new_gid);
				crosses.set(j, crosses.get(j));
				System.out.println("\t" + crosses);
			} else {
				max = new CrossOp().maxCross(max, crosses.get(j).get(0));
				slash = new BackCross().printSlash(max);
				tokens2 = crosses.get(j).get(0).split("\\" + slash, 2);
				System.out.print("tokens2[0]: " + tokens2[0]);
				System.out.println("\ttokens2[1]: " + tokens2[1]);
				if (!tokens2[0].contains("/")) {
					if (tokens2[0].contains("*")) {
						Pattern p2 = Pattern.compile("\\*\\d"); // backcross to
						// female
						Matcher m2 = p2.matcher(tokens2[0]);
						Boolean female_match = false;
						while (m2.find()) {
							female_match = true;
							tokens2[0] = tokens2[0].replaceAll("\\*\\d", "");
							System.out.println("new tokens2[0]: " + tokens2[0]);
						}
						if (!female_match) {
							tokens2[0] = tokens2[0].replaceAll("\\d\\*", "");
							System.out.println("\\d\\*: " + tokens2[0]);
						}
					}
					for (int i = 0; i < parents.size(); i++) {

						if (parents.get(i).get(0).equals(tokens2[0])) {
							fgid = Integer.parseInt(parents.get(i).get(1));
							female = tokens2[0];
						}
					}
				} else {
					for (int i = 0; i < crosses.size(); i++) {
						if (crosses.get(i).get(0).equals(tokens2[0])) {
							fgid = Integer.parseInt(crosses.get(i).get(1));
							female = tokens2[0];
						}
					}

				}
				System.out.println("[1]: " + tokens2[1]);
				if (!tokens2[1].contains("/")) {
					if (tokens2[1].contains("*")) {
						Pattern p2 = Pattern.compile("\\*\\d"); // backcross to
						// female
						Matcher m2 = p2.matcher(tokens2[1]);
						Boolean female_match = false;
						while (m2.find()) {
							female_match = true;
							tokens2[1] = tokens2[1].replaceAll("\\*\\d", "");
							System.out.println("new tokens2[1]: " + tokens2[1]);
						}
						if (!female_match) {
							tokens2[1] = tokens2[1].replaceAll("\\d\\*", "");
							System.out.println("\\d\\*: " + tokens2[1]);
						}
					}
					for (int i = 0; i < parents.size(); i++) {
						if (parents.get(i).get(0).equals(tokens2[1])) {
							mgid = Integer.parseInt(parents.get(i).get(1));
							male = tokens2[1];
						}
					}
				} else {
					for (int i = 0; i < crosses.size(); i++) {
						System.out.println("crosses: " + crosses.get(i).get(0));
						if (crosses.get(i).get(0).equals(tokens2[1])) {
							System.out.println("ditooo "
									+ crosses.get(i).get(1));
							mgid = Integer.parseInt(crosses.get(i).get(1));
							male = tokens2[1];
						}
					}
				}
				System.out.println("mgid: " + mgid);
				System.out.println("fgid: " + fgid);
				Germplasm g1 = manager.getGermplasmByGID(fgid);
				List<Name> name = manager.getNamesByGID(g1.getGpid1(), 0, null);
				List<Name> name1 = manager
				.getNamesByGID(g1.getGpid2(), 0, null);
				methodID = 0;
				if (name.get(0).getNval().equals(male)
						|| name1.get(0).getNval().equals(male)) {
					methodID = 107;
				}
				name.clear();
				name1.clear();
				g1 = null;

				methodID = selectMethodType(fgid, mgid, female, male, crosses
						.get(j).get(0), methodID);

				new_gid = addGID(crosses.get(j).get(0), fgid, mgid, methodID,
						3, false);
				// crossesGID=printSuccess_temp(crosses.get(j).get(0), parent,
				// id, manager.getGermplasmByGID(gid), "new", crossesGID);
				updateCreatedGID("" + new_gid, parent1ID,
						crosses.get(j).get(0), "new", createdGID);
				crosses.get(j).set(1, "" + new_gid);
				crosses.set(j, crosses.get(j));
				System.out.println("\t" + crosses);
				if (j == 0) {
					GID = new_gid;
				}
			}
		}

	}

	public JSONObject chooseGID_cross(JSONObject obj, ManagerFactory factory)
	throws MiddlewareQueryException, IOException, ParseException,
	InterruptedException {

		manager = factory.getGermplasmDataManager();
		System.out.println("usr has chosen gid for thde cross");

		JSONObject jsonObject = (JSONObject) obj;
		List<List<String>> createdGID = (List<List<String>>) jsonObject
		.get("createdGID");
		createdGID_local.clear();
		createdGID_local = createdGID;
		createdGID = createdGID_local;
		List<List<String>> list = (List<List<String>>) jsonObject.get("list");
		// ////System.out.println("\t list: "+list.size());
		list_local = list;
		// ////System.out.println("\t list: "+list_local.size());
		List<List<String>> existingTerm = (List<List<String>>) jsonObject
		.get("existingTerm");
		existingTerm_local = existingTerm;

		String userID = (String) jsonObject.get("userID");
		String female_nval = (String) jsonObject.get("female");
		String male_nval = (String) jsonObject.get("male");
		String cross = (String) jsonObject.get("cross");
		// the name of the cross of parent1 and parent2
		System.out.println("female: " + female_nval);
		System.out.println("male: " + male_nval);
		System.out.println("cross: " + cross);

		userID_local = userID;

		List<String> details = (List<String>) jsonObject.get("germplasm");
		String cross_id = (String) details.get(0);
		locationID = Integer.valueOf((String) jsonObject.get("locationID"));
		int gpid1 = Integer.valueOf((String) jsonObject.get("gpid1"));
		int gpid2 = Integer.valueOf((String) jsonObject.get("gpid2"));
		int gid = Integer.valueOf((String) jsonObject.get("gid"));
		cross_date = (String) jsonObject.get("cdate");

		String female_id = (String) jsonObject.get("female_id");
		String male_id = (String) jsonObject.get("male_id");

		String temp;

		System.out.println("male_id: " + male_id);
		System.out.println("female_id: " + female_id);
		System.out.println("cross_id: " + cross_id);

		System.out.println("Cross: " + cross);

		System.out.println("gpid1: " + gpid1);
		System.out.println("gpid2: " + gpid2);

		String nval, nval2;
		String id, id2;

		// System.out.println("CreatedGID before : "+createdGID_local);
		int gpid1_local = gpid1, gpid2_local = gpid2;
		for (int i = 0; i < 2; i++) {
			if (i == 0) {
				nval = female_nval;
				id = female_id;
				nval2 = male_nval;
				id2 = male_id;
				// gid=gpid1;
				Germplasm g = manager.getGermplasmByGID(Integer
						.valueOf(gpid1_local));
				gid = g.getGid();
				gpid1 = g.getGpid1();
				gpid2 = g.getGpid2();
				System.out.println("gpid1: " + gpid1);
				g = null;

			} else {
				nval = male_nval;
				id = male_id;
				nval2 = female_nval;
				id2 = female_id;
				// gid=gpid2;
				Germplasm g = manager.getGermplasmByGID(gpid2_local);
				gid = g.getGid();
				gpid1 = g.getGpid1();
				gpid2 = g.getGpid2();
				g = null;

			}
			if (nval.contains("/") || nval.contains("*")) {
				// The Parent has cross operators
				System.out.println("The parent has cross operators");

				//
				// System.out.println("createdGID: "+createdGID_local);
				createdGID = setLine_crossOp(nval, id, Integer.valueOf(gid),
						gpid1, gpid2, createdGID);
				// getLine_crossOp( nval,id, Integer.valueOf(gid), gpid1,
				// gpid2);
				// chooseGID_crossOP(parent1ID, parent1, parent2ID, parent2,
				// theParent,lastDeriv_parent,gid_local, gpid1_local,
				// gpid2_local,gid, gpid1, gpid2,createdGID);
				System.out.println("\n ***** END******* \n ");
			} else {
				Pattern p = Pattern.compile("IR");
				Matcher m1 = p.matcher(nval);
				String[] tokens = { "" };
				if (m1.lookingAt()) {
					tokens = new Tokenize().tokenize(nval);

				} else {
					tokens[0] = nval;
				}

				ArrayList<String> pedigreeList = new ArrayList<String>();
				pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0]
				// is the
				// most
				// recent
				// pedigree,
				// pedigreeList[size]
				// is the
				// root
				System.out.println("PedigreeLiost: " + pedigreeList);

				// getDerivativeLine_cross(pedigreeList, ""+gid, gpid1, gpid2,
				// id, nval);
				setDerivativeLine_cross(pedigreeList, gid, gpid1, gpid2, id,
						nval);
				// createdGID_local=getDerivativeLine( -1, pedigreeList,
				// gid_local, gpid1_local, gpid2_local, ""+gid, gpid1, gpid2,
				// id2, nval2, createdGID_local);
			}
		}
		createdGID = createdGID_local;
		// System.out.println("cross "+ cross+ " already exists");
		// printSuccess(cross,female_nval + "/" + male_nval, female_id + "/" +
		// male_id, germplasm_filtered.get(0), "old");

		/*
		 * if (has_GID(parent2ID, parent2)) { System.out.println("cross: "+
		 * cross); germplasm=isCross_existing( cross,fgid,mgid); if
		 * (germplasm==null){ int
		 * methodID=selectMethodType(fgid,mgid,femaleParent,maleParent);
		 * 
		 * int cross_gid = (int) addGID( cross,fgid,mgid, methodID);
		 * 
		 * Germplasm germplasm1 = manager.getGermplasmByGID(cross_gid);
		 * 
		 * createdGID_local=updateCreatedGID(""+germplasm1.getGid(), fid + "/" +
		 * mid, cross, "new", createdGID); createdGID=createdGID_local;
		 * 
		 * list_local=update_list(germplasm1, fid, cross);
		 * ////System.out.println("\t id: "+fid + "/" + mid);
		 * ////System.out.println("\t id: "+ cross);
		 * 
		 * germplasm1=null;
		 * 
		 * }else{ createdGID_local=updateCreatedGID(""+germplasm.getGid(), fid +
		 * "/" + mid, cross, "false", createdGID); createdGID=createdGID_local;
		 * 
		 * list_local=update_list(germplasm, fid , cross); }
		 * System.out.println("createdGID: "+list_local); }
		 */Germplasm germplasm = manager.getGermplasmByGID(Integer
				 .valueOf((String) jsonObject.get("gid")));
		 list_local = update_list(germplasm, female_id, cross);
		 details.clear();
		 jsonObject.clear();
		 // germplasm=null;

		 JSONObject data_output = new JSONObject();
		 data_output.put("list", list_local);
		 data_output.put("createdGID", createdGID);
		 data_output.put("existingTerm", existingTerm_local);

		 factory.close();
		 manager = null;
		 return data_output;

	}

	public void setDerivativeLine_cross(List<String> pedigreeList, int gid,
			int gpid1, int gpid2, String id, String parent)
	throws MiddlewareQueryException, IOException, InterruptedException {
		Germplasm germplasm = new Germplasm();
		System.out.println("pedigreeList: " + pedigreeList);

		for (int i = 0; i < pedigreeList.size(); i++) {

			if (i == 0) {
				germplasm = manager.getGermplasmByGID(Integer.valueOf(gid));
				// printSuccess(pedigreeList.get(i), parent, id, germplasm,
				// "false");

			} else {
				germplasm = manager.getGermplasmByGID(Integer.valueOf(gpid2));
				// printSuccess(pedigreeList.get(i), parent, id, germplasm,
				// "false");

				gpid2 = germplasm.getGpid2();
				gpid1 = germplasm.getGpid1();
			}
			createdGID_local = updateCreatedGID("" + germplasm.getGid(), id,
					pedigreeList.get(i), "false", createdGID_local);
		}
		germplasm = null;
		System.out.println("createdGID: " + createdGID_local);

	}

	public void getDerivativeLine_cross(List<String> pedigreeList, String gid,
			int gpid1, int gpid2, String id, String parent)
	throws MiddlewareQueryException, IOException, InterruptedException {
		Germplasm germplasm = new Germplasm();
		System.out.println("pedigreeList: " + pedigreeList);

		for (int i = 0; i < pedigreeList.size(); i++) {

			if (i == 0) {
				germplasm = manager.getGermplasmByGID(Integer.valueOf(gid));
				printSuccess(pedigreeList.get(i), parent, id, germplasm,
				"false");
			} else {
				germplasm = manager.getGermplasmByGID(Integer.valueOf(gpid2));
				printSuccess(pedigreeList.get(i), parent, id, germplasm,
				"false");
				gpid2 = germplasm.getGpid2();
				gpid1 = germplasm.getGpid1();
			}
		}
		germplasm = null;
		System.out.println("createdGID: " + createdGID_local);

	}

	public List<List<String>> getDerivativeLine(int index,
			List<String> pedigreeList, int gid_local, int gpid1_local,
			int gpid2_local, String gid, int gpid1, int gpid2,
			String parent1ID, String parent1, List<List<String>> createdGID)
			throws MiddlewareQueryException, IOException, InterruptedException {
		Germplasm germplasm = new Germplasm();
		System.out.println("Get the pedigree Line...");
		Boolean error = false;

		System.out.println("1: " + pedigreeList.get(0));
		System.out
		.println("last: " + pedigreeList.get(pedigreeList.size() - 1));

		System.out.println("GID: " + gid);
		System.out.println("GID_L: " + gid_local);
		System.out.println("GPID1: " + gpid1);
		System.out.println("GPID1_L:" + gpid1_local);
		System.out.println("GPID2:" + gpid2);
		System.out.println("GPID2_L:" + gpid2_local);
		for (int i = index + 1; i < pedigreeList.size(); i++) {
			System.out.println(":: " + pedigreeList.get(i));
			if (error) {
				List<Germplasm> germplasmList = new ArrayList<Germplasm>();

				int count_LOCAL = countGermplasmByName(pedigreeList.get(i),
						Database.LOCAL);
				int count_CENTRAL = countGermplasmByName(pedigreeList.get(i),
						Database.CENTRAL);
				germplasmList = getGermplasmList(pedigreeList.get(i),
						count_LOCAL, count_CENTRAL); // gets lists of germplasm
				// with that name
				germplasm = getGermplasmByGpid(gpid1_local, germplasmList); // get
				// the
				// germplasm
				// of
				// the
				// same
				// gpid1,
				// for
				// derivative
				// line,
				// or
				// gid
				// equals
				// to
				// the
				// gpid1
				gpid2_local = germplasm.getGpid2();
				gpid1_local = germplasm.getGpid1();
				gid_local = germplasm.getGid();
				createdGID_local = updateFile_createdGID("" + gid_local,
						parent1ID, pedigreeList.get(i), "false", parent1); // update
				// the
				// createdGID
				// file
				// System.out.println(" [1] pedigreeList.get(i): "+
				// pedigreeList.get(i)+ " gid: "+ gid_local);
				createdGID = createdGID_local;
				germplasmList.clear(); // clearing object

			} else {
				germplasm = manager.getGermplasmByGID(gpid2_local);
				if (germplasm == null) { // this is an ERROR, the gid should
					// exist
					error = true;
					String remarks = "Does not exist";
					createdGID_local = updateFile_createdGID(remarks,
							parent1ID, pedigreeList.get(i), "false", parent1); // update
					// the
					// createdGID
					// file
					createdGID = createdGID_local;
				} else {
					gpid2_local = germplasm.getGpid2();
					gpid1_local = germplasm.getGpid1();
					gid_local = germplasm.getGid();
					createdGID_local = updateFile_createdGID("" + gid_local,
							parent1ID, pedigreeList.get(i), "false", parent1); // update
					// the
					// createdGID
					// file
					// System.out.println(" [2] pedigreeList.get(i): "+
					// pedigreeList.get(i)+ " gid: "+ gid_local+
					// ""+gpid2_local);
					createdGID = createdGID_local;
					error = false; // set the flag to false
				}
			}

		}
		gid_local = Integer.valueOf(gid);
		gpid1_local = gpid1;
		gpid2_local = Integer.valueOf(gid);
		for (int i = index - 1; i >= 0; i--) {
			System.out.println("\t create GID: " + pedigreeList.get(i));

			int methodID = selectMethodType_DER(pedigreeList.get(i), parent1);

			gid_local = (int) addGID(pedigreeList.get(i), gpid1_local,
					gpid2_local, methodID, 5, false);
			Germplasm g = manager.getGermplasmByGID(gid_local);
			gpid2_local = g.getGid();
			gpid1_local = g.getGpid1();
			g = null;

			if (i == 0) {
				GID = gid_local;
				System.out.println("GID of one of the parent (: "
						+ pedigreeList.get(i) + "): " + GID);
			}

			createdGID_local = updateFile_createdGID("" + gid_local, parent1ID,
					pedigreeList.get(i), "new", parent1); // update the
			// createdGID file
			createdGID = createdGID_local;
		}

		System.out.println("createdGID: " + createdGID_local);
		return createdGID;

	}

	public List<List<String>> chooseGID_crossOP(String chooseGID_id,
			String chooseGID_nval, String theParent, String lastDeriv_parent,
			int gid_local, int gpid1_local, int gpid2_local, String gid,
			int gpid1, int gpid2, List<List<String>> createdGID)
			throws MiddlewareQueryException, IOException, InterruptedException {
		/*
		 * get the list of the parents (female(s) and male(s)) of the cross ->
		 * "parents" (1st index is the germplasm name, 2nd index is the female,
		 * 3rd is the male) get the list of all derivatives of the parents in
		 * the createdGID_local -> "derivatives" (1st dim is the names and 2nd
		 * dim is the GID)
		 * 
		 * check if the GID that has been chosen is a pedigree from the
		 * "derivatives" if yes, then create the line of the parent and update
		 * the createdGID_local
		 * 
		 * if a cross parents (with cross operators), find what index in the
		 * "parents" from the index+1 to the "the parent", (for each parent) get
		 * the female and male parent names and GID search if the parent exists,
		 * if yes, update the createdGID_local else create GID update the
		 * createdGID_local
		 */

		List<List<String>> derivatives = new ArrayList<List<String>>();

		// List<List<String>> temp = new ArrayList<List<String>>();
		List<String> row_createdGID;

		List<List<String>> parents = new ArrayList<List<String>>();
		List<String> row_parents = new ArrayList<String>();

		List<List<String>> twoDim = new ArrayList<List<String>>();
		List<String> row_twoDim = new ArrayList<String>();

		List<String> derivatives_temp = new ArrayList<String>();

		String line = theParent;
		int max = 0;
		max = maxCross(max, line);

		List<String> row_derivatives;
		Boolean isFound = false;

		/*
		 * get the list of the parents (female(s) and male(s)) of the cross ->
		 * "parents" ( indexes [0] is the germplasm name, [1] female, [2] is the
		 * male,[3] female GID [4] male GID)
		 */

		row_twoDim.add(line);
		row_twoDim.add("0"); // 0 for unexplored token
		twoDim.add(row_twoDim);

		parents = method2(max, row_twoDim, twoDim, parents, row_parents);
		parents.get(0).set(0, theParent);

		System.out.println("PARENTS: " + parents);

		// ----- end parent list

		// get the derivatives list
		for (int i = 0; i < parents.size(); i++) {// parents is the list of
			// parsed strings
			row_parents = parents.get(i);

			if ((!row_parents.get(1).contains("/") /*&& !row_parents.get(1)
					.contains("*")*/)
					&& !derivatives_temp.contains(row_parents.get(1))) {
				derivatives_temp.add(row_parents.get(1));
			}

			if ((!row_parents.get(2).contains("/") /*&& !row_parents.get(2)
					.contains("*")*/)
					&& !derivatives_temp.contains(row_parents.get(2))) {
				derivatives_temp.add(row_parents.get(2));
			}
		}
		System.out.println("TEMP DERIVATIVES: " + derivatives_temp);

		for (int i = 0; i < derivatives_temp.size(); i++) { // derivatives is
			// the list of
			// strings with
			// delimiter '-'

			row_derivatives = new ArrayList<String>();
			row_derivatives.add(derivatives_temp.get(i));
			for (int j = 0; j < createdGID_local.size(); j++) { // gets the
				// derivatives
				// from the
				// createdGID
				// and adds to
				// the list
				row_createdGID = createdGID_local.get(j);
				if (row_createdGID.get(2).equals(derivatives_temp.get(i))
						&& row_createdGID.get(0).equals(chooseGID_id)) {
					row_derivatives.add(row_createdGID.get(3));
					isFound = true;
					break;
				}
			}
			if (!isFound) {
				row_derivatives.add("0");
			}
			derivatives.add(row_derivatives);
		}
		derivatives_temp.clear();

		System.out.println("DERIVATIVES: " + derivatives);

		ArrayList<String> pedigreeList = new ArrayList<String>();

		int index_derivative = -1;

		for (int i = 0; i < derivatives.size(); i++) {
			row_derivatives = derivatives.get(i);

			pedigreeList = new ArrayList<String>();
			// System.out.println("Parse "+row_derivatives.get(0));
			Pattern p = Pattern.compile("IR");
			Matcher m1 = p.matcher(row_derivatives.get(0));
			String[] tokens = { "" };
			if (m1.lookingAt()) {
				tokens = new Tokenize().tokenize(row_derivatives.get(0));
			} else {
				tokens[0] = row_derivatives.get(0);
			}
			// tokens = new Tokenize().tokenize(row_derivatives.get(0));
			pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0]
			// is the most
			// recent
			// pedigree,
			// pedigreeList[size]
			// is the root
			System.out.println("pedigreeList: " + pedigreeList);
			for (int j = 0; j < pedigreeList.size(); j++) {
				System.out.println("pedigreeList: " + pedigreeList.get(j)
						+ " chooseNVAL: " + lastDeriv_parent);

				if (pedigreeList.get(j).equals(lastDeriv_parent)) { // if the
					// chosen
					// GID is a
					// derivative
					// in one of
					// the
					// parents
					// if derivative, get derivative line
					index_derivative = j; // get the index
					createdGID = getDerivativeLine(index_derivative,
							pedigreeList, gid_local, gpid1_local, gpid2_local,
							gid, gpid1, gpid2, chooseGID_id, chooseGID_nval,
							createdGID);
					createdGID_local = createdGID;
					if (index_derivative == 0) {
						row_derivatives.set(1, "" + gid_local);
					} else {
						row_derivatives.set(1, "" + GID);
					}

					break;
				}
			}
		}
		System.out.println("createdGID: " + createdGID_local);
		System.out.println("DERIVATIVES: " + derivatives);
		int index_parent = -1;
		if (index_derivative == -1) { // chosen GID is a cross
			System.out.println("the chosen GID has cross operator");
			row_parents = new ArrayList<String>();
			for (int i = 0; i < parents.size(); i++) {
				row_parents = parents.get(i);
				System.out.println("parent: " + row_parents.get(0)
						+ " choose: " + lastDeriv_parent);
				if (row_parents.get(0).equals(lastDeriv_parent)) {
					index_parent = i;
					if (i == 0) {
						// if it is theParent
						System.out.println("if it is the parent");
						setLine_crossOp(chooseGID_nval, chooseGID_id,
								Integer.valueOf(gid), gpid1, gpid2, createdGID);

					}
				}
			}
		}
		if (index_parent > 0) {
			setLine_crossOp(lastDeriv_parent, chooseGID_id,
					Integer.valueOf(gid), gpid1, gpid2, createdGID);
			// set the line for the germplasm with cross operators
			System.out
			.println("set the line for the germplasm with cross operators");
			int gid1 = 0, gid2 = 0;
			int methodID = 0;
			int gid_cross = 0;

			for (int i = index_parent - 1; i >= 0; i--) {
				row_parents = parents.get(i);
				System.out.println("parent: " + row_parents);

				row_createdGID = new ArrayList<String>();
				for (int j = 0; j < createdGID_local.size(); j++) {
					row_createdGID = createdGID.get(j);
					// System.out.println("\t parents: "+row_parents);
					System.out.println(" row_createdGID.get(2): "
							+ row_createdGID.get(2));
					// System.out.print("1: "+ row_parents.get(1));//+
					// " row_createdGID.get(1): " + row_createdGID.get(2));
					// System.out.println(" \t 2: "+ row_parents.get(2));//+
					// " row_createdGID.get(2): " + row_createdGID.get(2));
					if (chooseGID_id.equals(row_createdGID.get(0))
							&& row_parents.get(1).equals(row_createdGID.get(2))) {
						gid1 = Integer.valueOf(row_createdGID.get(3));
						System.out.println("gid1: " + gid1);
					}
					if (chooseGID_id.equals(row_createdGID.get(0))
							&& row_parents.get(2).equals(row_createdGID.get(2))) {
						gid2 = Integer.valueOf(row_createdGID.get(3));
						System.out.println("gid2: " + gid2);
						// break;
					}
				}
				Germplasm g1 = manager.getGermplasmByGID(gid1);
				List<Name> name = manager.getNamesByGID(g1.getGpid1(), 0, null);
				List<Name> name1 = manager
				.getNamesByGID(g1.getGpid2(), 0, null);
				methodID = 0;
				if (name.get(0).getNval().equals(row_parents.get(2))
						|| name1.get(0).getNval().equals(row_parents.get(2))) {
					methodID = 107;
				}
				name.clear();
				name1.clear();
				g1 = null;

				methodID = selectMethodType(gid1, gid2, row_parents.get(1),
						row_parents.get(2), row_parents.get(0), methodID);

				gid_cross = addGID(row_parents.get(0), gid1, gid2, methodID, 2,
						false);
				createdGID = updateFile_createdGID("" + gid_cross,
						chooseGID_id, row_parents.get(0), "new", theParent); // update
				// the
				// createdGID
				// file
				createdGID_local = createdGID;

			}
			System.out.println("createdGID: " + createdGID_local);
		}

		// end get DERIVATIVES
		int count_allParentsExist = 0;
		for (int i = 0; i < derivatives.size(); i++) {
			System.out.println("gid: " + derivatives.get(i).get(1));
			if (!derivatives.get(i).get(1).equals("0")
					&& !derivatives.get(i).get(1).equals("NOT SET")
					&& !derivatives.get(i).get(1).equals("CHOOSE GID")) {
				count_allParentsExist++;
			}
		}
		System.out.println("CREATE GID for parents with cross opeartors");
		System.out.println("1: " + count_allParentsExist);
		System.out.println("2: " + derivatives.size());

		if (count_allParentsExist == derivatives.size()) {

			int female_gid = 0, male_gid = 0, gid_cross;
			String female_nval = "", male_nval = "";
			for (int i = parents.size() - 1; i >= 0; i--) {

				row_parents = parents.get(i);

				System.out.println("pedigreelist: " + row_parents.get(0)
						+ " female: " + row_parents.get(1) + " male: "
						+ row_parents.get(2));
				for (int j = 0; j < derivatives.size(); j++) {
					if (derivatives.get(j).get(0).equals(row_parents.get(1))) {
						female_nval = row_parents.get(1);
						female_gid = Integer.valueOf(derivatives.get(j).get(1));
						// female=true;
					}
					if (derivatives.get(j).get(0).equals(row_parents.get(2))) {
						male_gid = Integer.valueOf(derivatives.get(j).get(1));
						male_nval = row_parents.get(2);
						// break;
					}
				}
				// Germplasm g1=manager.getGermplasmByGID(check.get(j));
				Germplasm g1 = manager.getGermplasmByGID(female_gid);
				List<Name> name = manager.getNamesByGID(g1.getGpid1(), 0, null);
				List<Name> name1 = manager
				.getNamesByGID(g1.getGpid2(), 0, null);
				int methodID = 0;
				if (name.size() > 0 && name1.size() > 0) {
					if (name.get(0).getNval().equals(male_nval)
							|| name1.get(0).getNval().equals(male_nval)) {
						methodID = 107;
					}
					name.clear();
					name1.clear();
					g1 = null;
				}
				methodID = selectMethodType(female_gid, male_gid, female_nval,
						male_nval, row_parents.get(0), methodID);
				gid_cross = addGID(row_parents.get(0), female_gid, male_gid,
						methodID, 2, false);
				if (i == 0) {

					GID = gid_cross;
					// g1=manager.getGermplasmByGID(gid_cross);
					// update createdGID_local
				}
				// String gid, String id,String pedigree, String newGID,String
				// parent
				createdGID = updateFile_createdGID("" + gid_cross,
						chooseGID_id, row_parents.get(0), "new", theParent); // update
				// the
				// createdGID
				// file
				createdGID_local = createdGID;
				row_derivatives = new ArrayList<String>();
				row_derivatives.add(row_parents.get(0));
				row_derivatives.add("" + gid_cross);
				derivatives.add(row_derivatives);

			}

		}
		System.out.println("createdGID: " + createdGID_local);
		System.out.println("DERIVATIVES: " + derivatives);

		pedigreeList.clear();
		derivatives.clear();
		parents.clear();

		return createdGID;
	}

	public List<List<String>> createNew_crossOP(String chooseGID_id,
			String chooseGID_nval, String theParent, String lastDeriv_parent,
			List<List<String>> createdGID) throws MiddlewareQueryException,
			IOException, InterruptedException {
		/*
		 * get the list of the parents (female(s) and male(s)) of the cross ->
		 * "parents" (1st index is the germplasm name, 2nd index is the female,
		 * 3rd is the male) get the list of all derivatives of the parents in
		 * the createdGID_local -> "derivatives" (1st dim is the names and 2nd
		 * dim is the GID)
		 * 
		 * check if the GID that has been chosen is a pedigree from the
		 * "derivatives" if yes, then create the line of the parent and update
		 * the createdGID_local
		 * 
		 * if a cross parents (with cross operators), find what index in the
		 * "parents" from the index+1 to the "the parent", (for each parent) get
		 * the female and male parent names and GID search if the parent exists,
		 * if yes, update the createdGID_local else create GID update the
		 * createdGID_local
		 */

		List<List<String>> derivatives = new ArrayList<List<String>>();

		// List<List<String>> temp = new ArrayList<List<String>>();
		List<String> row_createdGID;

		List<List<String>> parents = new ArrayList<List<String>>();
		List<String> row_parents = new ArrayList<String>();

		List<List<String>> twoDim = new ArrayList<List<String>>();
		List<String> row_twoDim = new ArrayList<String>();

		List<String> derivatives_temp = new ArrayList<String>();

		JSONObject output = getParse(theParent);
		String line = (String) output.get("line");
		int max = (Integer) output.get("max");

		List<String> row_derivatives;
		Boolean isFound = false;

		/*
		 * get the list of the parents (female(s) and male(s)) of the cross ->
		 * "parents" ( indexes [0] is the germplasm name, [1] female, [2] is the
		 * male,[3] female GID [4] male GID)
		 */

		row_twoDim.add(line);
		row_twoDim.add("0"); // 0 for unexplored token
		twoDim.add(row_twoDim);

		parents = method2(max, row_twoDim, twoDim, parents, row_parents);
		parents.get(0).set(0, theParent);

		System.out.println("PARENTS: " + parents);

		// ----- end parent list

		// get the derivatives list
		for (int i = 0; i < parents.size(); i++) {
			row_parents = parents.get(i);

			if ((!row_parents.get(1).contains("/") /*&& !row_parents.get(1)
					.contains("*")*/)
					&& !derivatives_temp.contains(row_parents.get(1))) {
				derivatives_temp.add(row_parents.get(1));
			}

			if ((!row_parents.get(2).contains("/")/* && !row_parents.get(2)
					.contains("*")*/)
					&& !derivatives_temp.contains(row_parents.get(2))) {
				derivatives_temp.add(row_parents.get(2));
			}
		}
		System.out.println("TEMP DERIVATIVES: " + derivatives_temp);

		for (int i = 0; i < derivatives_temp.size(); i++) {

			row_derivatives = new ArrayList<String>();
			row_derivatives.add(derivatives_temp.get(i));
			for (int j = 0; j < createdGID_local.size(); j++) {
				row_createdGID = createdGID_local.get(j);
				// System.out.println("\tid: "+ row_createdGID.get(0) +
				// " chosenGID_id: "+ chooseGID_id);
				// System.out.println("\t row_createdGID.get(2): "+
				// row_createdGID.get(2) + " derivtive: "+
				// derivatives_temp.get(i));
				if (row_createdGID.get(2).equals(derivatives_temp.get(i))
						&& row_createdGID.get(0).equals(chooseGID_id)) {
					row_derivatives.add(row_createdGID.get(3));
					isFound = true;
					break;
				}
			}
			if (!isFound) {
				row_derivatives.add("0");
			}
			derivatives.add(row_derivatives);
		}
		derivatives_temp.clear();

		System.out.println("DERIVATIVES: " + derivatives);

		// end get derivatives list
		List<Name> name;
		String pedigree;
		List<Germplasm> germplasm;
		List<Germplasm> germplasm_fin;

		for (int i = 0; i < derivatives.size(); i++) {
			row_derivatives = derivatives.get(i);
			pedigree = row_derivatives.get(0);
			int count_LOCAL = countGermplasmByName(pedigree, Database.LOCAL);
			int count_CENTRAL = countGermplasmByName(pedigree, Database.CENTRAL);
			germplasm = new ArrayList<Germplasm>();
			germplasm = getGermplasmList(pedigree, count_LOCAL, count_CENTRAL);
			System.out.println("count in db: " + germplasm.size());
			germplasm_fin = new ArrayList<Germplasm>();
			for (int j = 0; j < germplasm.size(); j++) {
				// System.out.println("\t ["+j+"]: "+germplasm.get(j).getLocationId());
				if (germplasm.get(j).getLocationId().equals(locationID)
						&& germplasm.get(j).getGrplce() == 0) {
					germplasm_fin.add(germplasm.get(j));
				}
			}
			int count = germplasm_fin.size();
			multipleHits_inLocation(germplasm_fin, pedigree, chooseGID_id,
					theParent);
			updateCreatedGID("CHOOSE GID", chooseGID_id, pedigree, "false",
					createdGID);

		}

		System.out.println("createdGID: " + createdGID_local);
		System.out.println("DERIVATIVES: " + derivatives);

		derivatives.clear();
		parents.clear();

		return createdGID;
	}

	public List<List<String>> createNew_crossOP_parent_bc(String chooseGID_id,
			String chooseGID_nval, String theParent, String lastDeriv_parent,
			List<List<String>> createdGID) throws MiddlewareQueryException,
			IOException, InterruptedException {
		/*
		 * get the list of the parents (female(s) and male(s)) of the cross ->
		 * "parents" (1st index is the germplasm name, 2nd index is the female,
		 * 3rd is the male) get the list of all derivatives of the parents in
		 * the createdGID_local -> "derivatives" (1st dim is the names and 2nd
		 * dim is the GID)
		 * 
		 * check if the GID that has been chosen is a pedigree from the
		 * "derivatives" if yes, then create the line of the parent and update
		 * the createdGID_local
		 * 
		 * if a cross parents (with cross operators), find what index in the
		 * "parents" from the index+1 to the "the parent", (for each parent) get
		 * the female and male parent names and GID search if the parent exists,
		 * if yes, update the createdGID_local else create GID update the
		 * createdGID_local
		 */

		String parent1ID = chooseGID_id;
		String parent1 = chooseGID_nval;
		List<String> pedigreeList = new ArrayList<String>();

		List<List<String>> backcrosses = new ArrayList<List<String>>();
		List<List<String>> crosses = new ArrayList<List<String>>();
		List<String> parents_bc = new ArrayList<String>();

		List<List<String>> parents = new ArrayList<List<String>>(); // list of
		// names
		// that
		// ntype=DER
		List<String> row_parents = new ArrayList<String>();

		new CrossOp();

		JSONObject result_method2 = CrossOp.method2(theParent, pedigreeList);
		pedigreeList = (List<String>) result_method2.get("list");
		backcrosses = (List<List<String>>) result_method2.get("backcrosses");
		crosses = (List<List<String>>) result_method2.get("crosses");
		parents_bc = (List<String>) result_method2.get("parents");
		result_method2.clear();

		int index_derivative = -1; // index of the chosen GID if it is from a
		// derivative

		System.out.println("PEDIGREELIST: " + pedigreeList);
		for (int i = 0; i < pedigreeList.size(); i++) {// parents is the list of
			// parsed strings
			System.out.println(":: " + pedigreeList.get(i));

			if (!pedigreeList.get(i).contains("/")
					/*&& !pedigreeList.get(i).contains("*")*/) {
				row_parents = new ArrayList<String>();
				row_parents.add(pedigreeList.get(i));

				row_parents.add("0");
				parents.add(row_parents);
			}
		}
		System.out.println("PARENTS: " + parents);

		// get GID of parents
		parents = checkGID_parents(parent1ID, parents, theParent);
		System.out.println("PARENTS: " + parents);

		if (!lastDeriv_parent.contains("/") /*&& !lastDeriv_parent.contains("*")*/) {
			ArrayList<String> pedigreeList1 = new ArrayList<String>();
			ArrayList<String> pedigreeList_der;
			for (int i = 0; i < parents.size(); i++) {

				row_parents = parents.get(i);
				if (lastDeriv_parent.equals(row_parents.get(0))) {
					pedigreeList = new ArrayList<String>();
					// System.out.println("Parse "+row_derivatives.get(0));
					Pattern p = Pattern.compile("IR");
					Matcher m1 = p.matcher(row_parents.get(0));
					String[] tokens = { "" };
					if (m1.lookingAt()) {
						tokens = new Tokenize().tokenize(row_parents.get(0));
					} else {
						tokens[0] = row_parents.get(0);
					}
					// tokens = new Tokenize().tokenize(row_derivatives.get(0));
					pedigreeList = saveToArray(pedigreeList1, tokens); // pedigreeList[0]
					// is
					// the
					// most
					// recent
					// pedigree,
					// pedigreeList[size]
					// is
					// the
					// root
					System.out.println("pedigreeList: " + pedigreeList);
					for (int j = 0; j < pedigreeList.size(); j++) {
						System.out.println("pedigreeList: "
								+ pedigreeList.get(j) + " chooseNVAL: "
								+ lastDeriv_parent);
						if (pedigreeList.get(j).equals(lastDeriv_parent)) { // if
							// the
							// chosen
							// GID
							// is
							// a
							// derivative
							// in
							// one
							// of
							// the
							// parents
							// if derivative, get derivative line
							index_derivative = j; // get the index

							// create the pedigreeLine
							tokens = new Tokenize().tokenize(lastDeriv_parent);
							pedigreeList_der = new ArrayList<String>();

							pedigreeList = saveToArray(pedigreeList_der, tokens); // pedigreeList[0]
							// is
							// the
							// most
							// recent
							// pedigree,
							// pedigreeList[size]
							// is
							// the
							// root
							Collections.reverse(pedigreeList_der);
							int gpid1 = 0, gpid2 = 0, gid = 0;
							Germplasm g;
							for (int k = 0; k < pedigreeList_der.size(); k++) {

								g = new Germplasm();

								if (k == 0) {

									int methodID = selectMethodType_DER(
											pedigreeList_der.get(k), theParent);
									gid = (int) addGID(pedigreeList_der.get(k),
											gpid1, gpid2, methodID, 2, false);
									g = manager.getGermplasmByGID(gid);
									gpid2 = gid;
									gpid1 = gid;
								} else {

									int methodID = selectMethodType_DER(
											pedigreeList_der.get(k), theParent);
									gid = (int) addGID(pedigreeList_der.get(k),
											gpid1, gpid2, methodID, 2, false);
									g = manager.getGermplasmByGID(gid);
									gpid2 = gid;
									gpid1 = g.getGpid1();
								}
								// pedigreeList_GID.add(gid);

								System.out.println(pedigreeList_der.get(k)
										+ " gid: " + gid + " gpid1: " + gpid1
										+ " gpid2: " + gpid2);
								if (k == pedigreeList_der.size() - 1) {
									GID = gid;
									row_parents.set(1, "" + GID);
								}
								g = null;
								updateCreatedGID("" + gid, chooseGID_id,
										pedigreeList_der.get(k), "new",
										createdGID_local);
							}
							// end create pedigreeLine
							pedigreeList_der.clear();
							break;
						}
					}
					pedigreeList.clear();
				}
			}
		}

		// check if all parents exists
		System.out.println("PARENTS: " + parents);
		int count_parents_exist = 0;
		for (int j = 0; j < parents.size(); j++) {
			System.out.println(":: " + parents.get(j));
			if (!parents.get(j).get(1).equals("0")
					&& !parents.get(j).get(1).equals("NOT SET")
					&& !parents.get(j).get(1).equals("CHOOSE GID")) {
				count_parents_exist++;
			}
		}
		System.out.println("PARENTS: " + parents);

		// get GID of bc
		for (int j = 0; j < backcrosses.size(); j++) {
			for (int i = 0; i < createdGID_local.size(); i++) {
				if (createdGID_local.get(i).get(0).equals(parent1ID)
						&& createdGID_local.get(i).get(1).equals(theParent)
						&& createdGID_local.get(i).get(2)
						.equals(backcrosses.get(j).get(0))) {
					if (!createdGID_local.get(i).get(3).equals("NOT SET")
							&& !createdGID_local.get(i).get(3)
							.equals("CHOOSE GID")) {
						backcrosses.get(j).set(1,
								createdGID_local.get(i).get(3));
						System.out.println("bc: "
								+ createdGID_local.get(i).get(3));
					}

				}
			}
		}
		System.out.println("bc: " + backcrosses);

		// check if all bc exists

		int count_bc_exist = 0;
		for (int j = 0; j < backcrosses.size(); j++) {
			if (!backcrosses.get(j).get(1).equals("0")
					&& !backcrosses.get(j).get(1).equals("NOT SET")
					&& !backcrosses.get(j).get(1).equals("CHOOSE GID")) {
				count_bc_exist++;
			}
		}
		// get GID of crosses
		crosses = checkGID_crosses(parent1ID, crosses, theParent);

		// create GID for back cross and crosses
		System.out.println("count parents : " + count_parents_exist);
		System.out.println("count bc: " + count_bc_exist);
		System.out.println("index deriv: " + index_derivative);
		System.out.println("bc size: " + backcrosses.size());
		if (count_parents_exist == parents.size() && index_derivative > -1) {
			createGID_bc_backcrosses(parents, backcrosses, crosses, parents_bc,
					createdGID, lastDeriv_parent, parent1ID); // createGID
		}
		return createdGID;
	}

	public List<List<String>> createNew_crossOP_parent(String chooseGID_id,
			String chooseGID_nval, String theParent, String lastDeriv_parent,
			List<List<String>> createdGID) throws MiddlewareQueryException,
			IOException, InterruptedException {
		/*
		 * get the list of the parents (female(s) and male(s)) of the cross ->
		 * "parents" (1st index is the germplasm name, 2nd index is the female,
		 * 3rd is the male) get the list of all derivatives of the parents in
		 * the createdGID_local -> "derivatives" (1st dim is the names and 2nd
		 * dim is the GID)
		 * 
		 * check if the GID that has been chosen is a pedigree from the
		 * "derivatives" if yes, then create the line of the parent and update
		 * the createdGID_local
		 * 
		 * if a cross parents (with cross operators), find what index in the
		 * "parents" from the index+1 to the "the parent", (for each parent) get
		 * the female and male parent names and GID search if the parent exists,
		 * if yes, update the createdGID_local else create GID update the
		 * createdGID_local
		 */

		List<List<String>> derivatives = new ArrayList<List<String>>();

		// List<List<String>> temp = new ArrayList<List<String>>();
		List<String> row_createdGID;

		List<List<String>> parents = new ArrayList<List<String>>();
		List<String> row_parents = new ArrayList<String>();

		List<List<String>> twoDim = new ArrayList<List<String>>();
		List<String> row_twoDim = new ArrayList<String>();

		List<String> derivatives_temp = new ArrayList<String>();

		JSONObject output = getParse(theParent);
		String line = (String) output.get("line");
		int max = (Integer) output.get("max");

		List<String> row_derivatives;
		Boolean isFound = false;

		/*
		 * get the list of the parents (female(s) and male(s)) of the cross ->
		 * "parents" ( indexes [0] is the germplasm name, [1] female, [2] is the
		 * male,[3] female GID [4] male GID)
		 */

		row_twoDim.add(line);
		row_twoDim.add("0"); // 0 for unexplored token
		twoDim.add(row_twoDim);

		parents = method2(max, row_twoDim, twoDim, parents, row_parents);
		parents.get(0).set(0, theParent);

		System.out.println("PARENTS: " + parents);

		// ----- end parent list

		// get the derivatives list
		for (int i = 0; i < parents.size(); i++) {
			row_parents = parents.get(i);

			if ((!row_parents.get(1).contains("/") /*&& !row_parents.get(1)
					.contains("*")*/)
					&& !derivatives_temp.contains(row_parents.get(1))) {
				derivatives_temp.add(row_parents.get(1));
			}

			if ((!row_parents.get(2).contains("/") /*&& !row_parents.get(2)
					.contains("*")*/)
					&& !derivatives_temp.contains(row_parents.get(2))) {
				derivatives_temp.add(row_parents.get(2));
			}
		}
		System.out.println("TEMP DERIVATIVES: " + derivatives_temp);

		for (int i = 0; i < derivatives_temp.size(); i++) {

			row_derivatives = new ArrayList<String>();
			row_derivatives.add(derivatives_temp.get(i));
			for (int j = 0; j < createdGID_local.size(); j++) {
				row_createdGID = createdGID_local.get(j);
				if (row_createdGID.get(2).equals(derivatives_temp.get(i))
						&& row_createdGID.get(0).equals(chooseGID_id)) {
					row_derivatives.add(row_createdGID.get(3));
					isFound = true;
					break;
				}
			}
			if (!isFound) {
				row_derivatives.add("0");
			}
			derivatives.add(row_derivatives);
		}
		derivatives_temp.clear();

		System.out.println("DERIVATIVES: " + derivatives);

		// end get derivatives list

		ArrayList<String> pedigreeList;
		ArrayList<String> pedigreeList_der;

		// String[] tokens;
		// int index_derivative=-1;

		for (int i = 0; i < derivatives.size(); i++) {
			row_derivatives = derivatives.get(i);

			pedigreeList = new ArrayList<String>();
			// System.out.println("Parse "+row_derivatives.get(0));
			Pattern p = Pattern.compile("IR");
			Matcher m1 = p.matcher(row_derivatives.get(0));
			String[] tokens = { "" };
			if (m1.lookingAt()) {
				tokens = new Tokenize().tokenize(row_derivatives.get(0));
			} else {
				tokens[0] = row_derivatives.get(0);
			}
			// tokens = new Tokenize().tokenize(row_derivatives.get(0));
			pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0]
			// is the most
			// recent
			// pedigree,
			// pedigreeList[size]
			// is the root
			System.out.println("pedigreeList: " + pedigreeList);
			for (int j = 0; j < pedigreeList.size(); j++) {
				System.out.println("pedigreeList: " + pedigreeList.get(j)
						+ " chooseNVAL: " + lastDeriv_parent);
				if (pedigreeList.get(j).equals(lastDeriv_parent)) { // if the
					// chosen
					// GID is a
					// derivative
					// in one of
					// the
					// parents
					// if derivative, get derivative line
					// index_derivative=j; // get the index
					System.out.println("HERE");
					p = Pattern.compile("IR");
					m1 = p.matcher(row_derivatives.get(0));
					//tokens[] = { "" };
					if (m1.lookingAt()) {
						tokens = new Tokenize().tokenize(row_derivatives.get(0));
					} else {
						tokens[0] = row_derivatives.get(0);
					}
					// create the pedigreeLine
					
					pedigreeList_der = new ArrayList<String>();

					pedigreeList = saveToArray(pedigreeList_der, tokens); // pedigreeList[0]
					// is
					// the
					// most
					// recent
					// pedigree,
					// pedigreeList[size]
					// is
					// the
					// root
					Collections.reverse(pedigreeList_der);
					int gpid1 = 0, gpid2 = 0, gid = 0;
					Germplasm g;
					int index = -1;
					System.out.println("FIRST: " + pedigreeList_der.get(0));
					for (int l = 0; l < pedigreeList_der.size(); l++) {

						if (pedigreeList_der.get(l).equals(chooseGID_nval)) {

							if (l == 0) {
								System.out.println("pedList: "
										+ pedigreeList_der);
								for (int k = 0; k < pedigreeList_der.size(); k++) {
									System.out.println("FIRST: "
											+ pedigreeList_der.get(0));
									g = new Germplasm();

									if (i == 0) {

										int methodID = selectMethodType_DER(
												pedigreeList_der.get(k),
												theParent);
										gid = (int) addGID(
												pedigreeList_der.get(k), gpid1,
												gpid2, methodID, 2, false);
										g = manager.getGermplasmByGID(gid);
										gpid2 = gid;
										gpid1 = gid;
									} else {

										int methodID = selectMethodType_DER(
												pedigreeList_der.get(k),
												theParent);
										gid = (int) addGID(
												pedigreeList_der.get(k), gpid1,
												gpid2, methodID, 2, false);
										g = manager.getGermplasmByGID(gid);
										gpid2 = gid;
									}
									// pedigreeList_GID.add(gid);

									System.out.println(pedigreeList_der.get(k)
											+ " gid: " + gid + " gpid1: "
											+ gpid1 + " gpid2: " + gpid2);
									if (k == pedigreeList_der.size() - 1) {
										GID = gid;
										row_derivatives.set(1, "" + GID);
									}
									g = null;

									updateCreatedGID("" + gid, chooseGID_id,
											pedigreeList_der.get(k), "new",
											createdGID_local);
								}
							} else {
								updateCreatedGID("NOT SET", chooseGID_id,
										pedigreeList_der.get(l), "false",
										createdGID_local);
								updateCreatedGID("CHOOSE GID", chooseGID_id,
										pedigreeList_der.get(0), "false",
										createdGID_local);
								int count_LOCAL = countGermplasmByName(
										pedigreeList_der.get(0), Database.LOCAL);
								int count_CENTRAL = countGermplasmByName(
										pedigreeList_der.get(0),
										Database.CENTRAL);

								List<Germplasm> germplasm = getGermplasmList(
										pedigreeList_der.get(0), count_LOCAL,
										count_CENTRAL);
								List<Germplasm> germplasm_fin = new ArrayList<Germplasm>();
								System.out.println("gcount: " + count_LOCAL);
								for (int m = 0; m < germplasm.size(); m++) {
									if (germplasm.get(m).getLocationId()
											.equals(locationID)
											&& germplasm.get(m).getGrplce() == 0) {
										germplasm_fin.add(germplasm.get(m));
									}
								}
								multipleHits_inLocation(germplasm_fin,
										pedigreeList_der.get(0), chooseGID_id,
										theParent);
								germplasm = null;
							}
						}
					}
					// end create pedigreeLine
					pedigreeList_der.clear();
					break;
				}
			}
			pedigreeList.clear();
		}
		System.out.println("createdGID: " + createdGID_local);
		System.out.println("DERIVATIVES: " + derivatives);

		// end get DERIVATIVES
		int count_allParentsExist = 0;
		for (int i = 0; i < derivatives.size(); i++) {
			System.out.println("gid: " + derivatives.get(i).get(1));
			if (!derivatives.get(i).get(1).equals("0")
					&& !derivatives.get(i).get(1).equals("NOT SET")
					&& !derivatives.get(i).get(1).equals("CHOOSE GID")) {
				count_allParentsExist++;
			}
		}
		System.out.println("CREATE GID for parents with cross opeartors");
		System.out.println("1: " + count_allParentsExist);
		System.out.println("2: " + derivatives.size());

		if (count_allParentsExist == derivatives.size()) {

			int female_gid = 0, male_gid = 0, gid_cross;
			String female_nval = "", male_nval = "";
			for (int i = parents.size() - 1; i >= 0; i--) {
				row_parents = parents.get(i);
				System.out.println("pedigreelist: " + row_parents.get(0)
						+ " female: " + row_parents.get(1) + " male: "
						+ row_parents.get(2));
				for (int j = 0; j < derivatives.size(); j++) {
					if (derivatives.get(j).get(0).equals(row_parents.get(1))) {
						female_nval = row_parents.get(1);
						female_gid = Integer.valueOf(derivatives.get(j).get(1));
					}
					if (derivatives.get(j).get(0).equals(row_parents.get(2))) {
						male_gid = Integer.valueOf(derivatives.get(j).get(1));
						male_nval = row_parents.get(2);
						break;
					}
				}
				// Germplasm g1=manager.getGermplasmByGID(check.get(j));
				Germplasm g1 = manager.getGermplasmByGID(female_gid);
				List<Name> name = manager.getNamesByGID(g1.getGpid1(), 0, null);
				List<Name> name1 = manager
				.getNamesByGID(g1.getGpid2(), 0, null);
				int methodID = 0;
				if (name.size() > 0 && name1.size() > 0) {
					if (name.get(0).getNval().equals(male_nval)
							|| name1.get(0).getNval().equals(male_nval)) {
						methodID = 107;
					}
					name.clear();
					name1.clear();
					g1 = null;
				}
				methodID = selectMethodType(female_gid, male_gid, female_nval,
						male_nval, row_parents.get(0), methodID);
				gid_cross = addGID(row_parents.get(0), female_gid, male_gid,
						methodID, 2, true);
				if (i == 0) {
					GID = gid_cross;

				}
				// String gid, String id,String pedigree, String newGID,String
				// parent
				createdGID = updateFile_createdGID("" + gid_cross,
						chooseGID_id, row_parents.get(0), "new", theParent); // update
				// the
				// createdGID
				// file
				createdGID_local = createdGID;
				row_derivatives = new ArrayList<String>();
				row_derivatives.add(row_parents.get(0));
				row_derivatives.add("" + gid_cross);
				derivatives.add(row_derivatives);

			}

		}
		System.out.println("createdGID: " + createdGID_local);
		System.out.println("DERIVATIVES: " + derivatives);

		derivatives.clear();
		parents.clear();

		return createdGID;
	}

	public Germplasm isCross_existing(String cross, int fgid, int mgid)
	throws MiddlewareQueryException, IOException {
		List<Germplasm> germplasm = new ArrayList<Germplasm>();
		List<Germplasm> germplasm_fin = new ArrayList<Germplasm>();

		int count_LOCAL = countGermplasmByName(cross, Database.LOCAL);
		int count_CENTRAL = countGermplasmByName(cross, Database.CENTRAL);

		germplasm = getGermplasmList(cross, count_LOCAL, count_CENTRAL);
		System.out.println("gsize: " + germplasm.size());

		for (int j = 0; j < germplasm.size(); j++) {
			if (germplasm.get(j).getLocationId().equals(locationID)
					&& germplasm.get(j).getGrplce() == 0) {
				germplasm_fin.add(germplasm.get(j));
			}
		}
		System.out.println("gfin size: " + germplasm.size());
		if (germplasm_fin.size() != 0) {
			for (int i = 0; i < germplasm_fin.size(); i++) {
				if (germplasm_fin.get(i).getGpid1() == fgid
						&& germplasm_fin.get(i).getGpid2() == mgid) {
					return germplasm_fin.get(i);
				}
			}
			return null;
		} else {
			return null;
		}
	}

	public int selectMethodType_DER(String pedigree, String parent) {
		int methodID = 31; // unknown
		String tokens[] = pedigree.split("-");
		Pattern p = Pattern
		.compile("(IR\\s\\d+)|(B)|(\\d+B)|(\\d*R)|(\\d*AC)|(C\\d+)|(\\d+MP)|((UBN|AJY|SRN|CPA|KKN|PMI|SKN|SRN|SDO)\\s\\d+)");
		System.out.println("pedigree: " + tokens[tokens.length - 1]);
		String gen;
		if (!pedigree.contains("-")) { // if F1
			gen = pedigree;
		} else {
			int i = 0;
			gen = tokens[tokens.length - 1];
			Matcher m = p.matcher(gen);
			if (m.find()) {
				printGroup(m);

				System.out.println("group: " + m.group(i));
				i++;

				if (m.group(1) != null && m.group(1).equals(gen)) {
					methodID = 205;// Single plant selection
				} else if (m.group(2) != null && m.group(2).equals(gen)) {
					methodID = 31; // root is unknown
				} else if (m.group(3) != null && m.group(3).equals(gen)) {
					methodID = 207; // random bulk
				} else if (m.group(4) != null && m.group(4).equals(gen)) {
					methodID = 206; // selected bulk
				} else if (m.group(6) != null && m.group(6).equals(gen)) {
					methodID = 202; // AC
				}
				/*
				 * else if(m.group(5)!=null && m.group(5).equals(gen)){
				 * methodID=208; // R }
				 */
				else {
					methodID = 205; // unknown
				}
			} else { // plant number only
				methodID = 205;// Single plant selection
			}

		}

		return methodID;
	}

	public void printGroup(Matcher m) {
		System.out.println("Group count: " + m.groupCount());
		int i;
		for (i = 0; i <= m.groupCount(); i++) {
			System.out.println(i + " : " + m.group(i));
		}
	}

	public JSONObject single_createGID(JSONObject obj, ManagerFactory factory)
	throws MiddlewareQueryException, IOException, ParseException,
	InterruptedException {

		manager = factory.getGermplasmDataManager();

		JSONObject jsonObject = (JSONObject) obj;
		List<List<String>> createdGID = (List<List<String>>) jsonObject
		.get("createdGID");
		createdGID_local.clear();
		createdGID_local = createdGID;
		createdGID = createdGID_local;
		List<List<String>> list = (List<List<String>>) jsonObject.get("list");
		// ////System.out.println("\t list: "+list.size());
		list_local = list;
		// ////System.out.println("\t list: "+list_local.size());
		List<List<String>> existingTerm = (List<List<String>>) jsonObject
		.get("existingTerm");
		existingTerm_local = existingTerm;

		String userID = (String) jsonObject.get("userID");
		userID_local = userID;

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

		List<String> details = (List<String>) jsonObject.get("germplasm");
		// ////System.out.println("json string:location ID: " + (String)
		// details.get(6));
		locationID = Integer.valueOf((String) details.get(6));
		int gpid1 = Integer.valueOf((String) details.get(8));
		int gpid2 = Integer.valueOf((String) details.get(9));
		String root_id = (String) jsonObject.get("root_id");

		String parent1ID = (String) details.get(0);
		// parent1ID, ID of parent1
		String gid = (String) details.get(3);
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

		String temp;
		mid = mid.replaceAll("\"", "");
		fid = fid.replaceAll("\"", "");
		int mid2 = Integer.valueOf(mid), fid2 = Integer.valueOf(fid);
		// ////System.out.println("fid: "+fid2);
		// ////System.out.println("mid: "+mid2);
		if (Integer.valueOf(mid) < Integer.valueOf(fid)) {
			temp = mid;
			mid = fid;
			fid = temp;

			temp = maleParent;
			maleParent = femaleParent;
			femaleParent = temp;
		}
		// ////System.out.println("mid: "+mid);

		createdGID_local = updateCreatedGID(gid, parent1ID, parent1, "false",
				createdGID); // update the createdGID file
		createdGID = createdGID_local;

		parse(parent1, gid, parent1ID, gpid1, gpid2); // parse the germplasm
		// name of the chosen
		// GID
		createdGID = createdGID_local;

		// locationID=getLocationID();
		int mgid = getGID_fromFile(maleParent, mid);
		int fgid = getGID_fromFile(femaleParent, fid);

		Germplasm germplasm = isExisting(fgid, mgid);

		if (has_GID(parent2ID, parent2)) {
			// //System.out.println("PROCESSING CROSS");

			if (germplasm.getGid() == null || germplasm.getGpid1() != fgid
					|| germplasm.getGpid2() != mgid) {
				// //System.out.println("fgid: "+ fgid);
				// //System.out.println("mgid: "+ mgid);
				Germplasm g1 = manager.getGermplasmByGID(fgid);
				List<Name> name = manager.getNamesByGID(g1.getGpid1(), 0, null);
				List<Name> name1 = manager
				.getNamesByGID(g1.getGpid2(), 0, null);
				int methodID = 0;
				if (name.size() > 0 && name1.size() > 0) {
					if (name.get(0).getNval().equals(maleParent)
							|| name1.get(0).getNval().equals(maleParent)) {
						methodID = 107;
					}
					name.clear();
					name1.clear();
					g1 = null;
				}
				methodID = selectMethodType(fgid, mgid, femaleParent,
						maleParent, cross, methodID);

				int cross_gid = (int) addGID(cross, fgid, mgid, methodID, 2,
						true);

				Germplasm germplasm1 = manager.getGermplasmByGID(cross_gid);

				createdGID_local = updateCreatedGID("" + germplasm1.getGid(),
						fid + "/" + mid, cross, "new", createdGID);
				createdGID = createdGID_local;

				list_local = update_list(germplasm1, fid, cross);
				// //System.out.println("\t id: "+fid + "/" + mid);
				// //System.out.println("\t id: "+ cross);

				germplasm1 = null;
			} else {
				// //System.out.println(" or HERE"+ germplasm.getGid());
				List<Name> name = new ArrayList<Name>();
				name = manager.getNamesByGID(germplasm.getGid(), 0, null);

				createdGID_local = updateCreatedGID("" + germplasm.getGid(),
						fid + "/" + mid, name.get(0).getNval(), "false",
						createdGID);
				createdGID = createdGID_local;

				list_local = update_list(germplasm, fid, name.get(0).getNval());
				name = null;
			}
		}

		// clear all object, free memory
		details.clear();
		jsonObject.clear();
		germplasm = null;

		// createdGID_local.clear();

		JSONObject data_output = new JSONObject();
		data_output.put("list", list_local);
		data_output.put("createdGID", createdGID);
		data_output.put("existingTerm", existingTerm_local);

		// ////System.out.println("createdGID: "+createdGID_local);
		// ////System.out.println("\t existing: "+existingTerm_local);
		// //System.out.println("\t list: "+list_local);

		// //System.out.println("END SINGLE CREATED @ test.java");
		manager = null;
		factory.close();

		return data_output;

	}

	public void parse(String parent, String gid, String id, int gpid1, int gpid2)
	throws MiddlewareQueryException, IOException, InterruptedException {

		// //System.out.println("### STARTING parsing in sInlge creation of GID");
		Pattern p = Pattern.compile("IR");
		Matcher m1 = p.matcher(parent);
		String[] tokens = { "" };
		if (m1.lookingAt()) {
			tokens = new Tokenize().tokenize(parent);
		} else {
			tokens[0] = parent;
		}
		// String[] tokens = new Tokenize().tokenize(parent);
		ArrayList<String> pedigreeList = new ArrayList<String>();

		pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0] is
		// the most recent
		// pedigree,
		// pedigreeList[size]
		// is the root
		String pedigree;

		if (pedigreeList.size() > 1) {
			pedigreeList.remove(0); // remove the pedigree already processed

			pedigree = pedigreeList.get(0); // first element is the parent

			// //System.out.print(">> "+ pedigreeList.get(0) + "\t");
			// call function to assign GID to the pedigree line
			assignGID(id, parent, gpid1, gpid2, pedigreeList);
		}/*
		 * else{ pedigree=pedigreeList.get(0); updateFile_createdGID(gid, id,
		 * pedigree); //update the createdGID file }
		 */

		// clearing objects
		pedigreeList.clear();
		tokens = null;

		// //System.out.println("### END parsing in sInlge creation of GID");
	}

	public void assignGID(String id, String parent, int gpid1, int gpid2,
			ArrayList<String> pedigreeList) throws MiddlewareQueryException,
			IOException, InterruptedException {
		Boolean error = false;
		String gid; // set the gid to be string
		Germplasm germplasm;
		List<Germplasm> germplasmList;
		for (int i = 0; i < pedigreeList.size(); i++) {
			String pedigree = pedigreeList.get(i);
			// //System.out.print(">> "+ pedigree + "\t");
			germplasm = new Germplasm();

			if (error) { // if the precedent pedigree does not exist

				germplasmList = new ArrayList<Germplasm>();

				int count_LOCAL = countGermplasmByName(pedigree, Database.LOCAL);
				int count_CENTRAL = countGermplasmByName(pedigree,
						Database.CENTRAL);
				germplasmList = getGermplasmList(pedigree, count_LOCAL,
						count_CENTRAL); // gets lists of germplasm with that
				// name
				germplasm = getGermplasmByGpid(gpid1, germplasmList); // get the
				// germplasm
				// of
				// the
				// same
				// gpid1,
				// for
				// derivative
				// line,
				// or
				// gid
				// equals
				// to
				// the
				// gpid1

				germplasmList.clear(); // clearing object
			} else {
				germplasm = manager.getGermplasmByGID(gpid2);
			}

			if (germplasm == null) { // this is an ERROR, the gid should exist
				error = true;
				gid = "Does not exist";
				// createdGID_local=updateFile_createdGID(gid, id, pedigree,
				// "false"); //update the createdGID file

			} else {
				gpid2 = germplasm.getGpid2();
				gpid1 = germplasm.getGpid1();
				gid = "" + germplasm.getGid();
				error = false; // set the flag to false
			}
			createdGID_local = updateCreatedGID(gid, id, pedigree, "false",
					createdGID_local); // update the createdGID file

		}

		// clearing objects
		germplasm = null;

	}

	public JSONObject bulk_createGID2(List<List<String>> createdGID,
			List<List<String>> list, List<String> checked, int locationID_l,
			List<List<String>> existingTerm, String userID,
			ManagerFactory factory) throws IOException,
			MiddlewareQueryException, ParseException, InterruptedException,
			java.text.ParseException {

		System.out
		.println(" ###Starting..BULK CREATION of GID oF UNPROCESSED DATA");
		manager = factory.getGermplasmDataManager();

		createdGID_local = new ArrayList<List<String>>();
		createdGID_local = createdGID;
		list_local = new ArrayList<List<String>>();
		list_local = list;
		checked_local = new ArrayList<String>();

		if (existingTerm == null) {
			// System.out.println();
			existingTerm = new ArrayList<List<String>>();
			;
			existingTerm_local = new ArrayList<List<String>>();
		}
		existingTerm_local = existingTerm;
		checked_local = checked;

		userID_local = userID;

		locationID = locationID_l;
		List<String> row_output = new ArrayList<String>();

		System.out.println("HERE BULK2 existing: " + existingTerm);
		System.out.println("HERE BULK2 checked: " + checked);
		System.out.println("HERE BULK2 createdGID: " + createdGID);
		System.out.println("HERE BULK2 list: " + list);
		System.out.println("HERE: " + checked.size());
		for (int i = 0; i < checked.size(); i++) {
			// ////System.out.println("\n CHECK:: "+ checked.get(i));

			for (int j = 0; j < list.size(); j++) {

				row_output = list.get(j);
				// ////System.out.println("row_output: "+row_output);
				String female_id = row_output.get(2);
				String female_remarks = row_output.get(3);
				String female_nval = row_output.get(5);
				// male
				String male_id = row_output.get(6);
				String male_remarks = row_output.get(7);
				String male_nval = row_output.get(9);
				cross_date = row_output.get(10);
				// cross name
				String cross = row_output.get(1);

				if (female_id.equals(checked.get(i))
						|| male_id.equals(checked.get(i))) {
					// ////System.out.println("female_remarks: "+female_remarks);
					// ////System.out.println("male_remarks: "+male_remarks);

					if (female_remarks.equals("in standardized format")
							&& male_remarks.equals("in standardized format")) {

						// //System.out.println("\t" + "  ***Process parents");

						/*
						 * ////System.out.println("locationID: "+locationID);
						 * ////System.out.println("checked: "+ checked);
						 * ////System.out.println("list: "+ list);
						 * ////System.out.println("existingTerm: " +
						 * existingTerm);
						 */

						Boolean result = processParents(female_nval, female_id,
								male_nval, male_id, cross, list);
						// if(!result){
						// printNotSet(cross, female_nval+"/"+male_nval,
						// female_id + "/" + male_id);
						// }
						// //System.out.println("\t" +
						// "  ***END Process parents");
					}
					break;
				}
			}
		}
		// row_output.clear();
		// list.clear();
		// existingTerm.clear();

		// ////System.out.println("output: "+createdGID_local);
		// ////System.out.println("list: "+list_local);
		existingTerm = existingTerm_local;
		JSONObject data_output = new JSONObject();
		data_output.put("existingTerm", existingTerm);
		data_output.put("list", list_local);
		data_output.put("createdGID", createdGID_local);

		// //System.out.println("\n createdGID: "+createdGID_local.size()+"\t"+createdGID_local);
		// //System.out.println("\n list: "+list_local.size()+"\t"+list_local);
		// System.out.println("END existing: "+existingTerm);

		// //System.out.println(" ###ENDING..BULK CREATION of GID \n");
		manager = null;
		factory.close();
		return data_output;
	}

	public JSONObject bulk_createGID(List<List<String>> list,
			List<String> checked, int locationID_l,
			List<List<String>> existingTerm, String userID,
			ManagerFactory factory) throws IOException,
			MiddlewareQueryException, ParseException, InterruptedException,
			java.text.ParseException {

		System.out.println(" ###Starting..BULK CREATION of GID");
		manager = factory.getGermplasmDataManager();

		createdGID_local = new ArrayList<List<String>>();
		list_local = new ArrayList<List<String>>();
		checked_local = new ArrayList<String>();

		existingTerm_local = existingTerm;
		checked_local = checked;
		list_local = list;
		userID_local = userID;

		locationID = locationID_l;
		List<String> row_output = new ArrayList<String>();

		// ////System.out.println("HERE: "+list);
		// ////System.out.println("HERE: "+checked.size());
		for (int i = 0; i < checked.size(); i++) {
			// ////System.out.println("\n CHECK:: "+ checked.get(i));

			for (int j = 0; j < list.size(); j++) {

				row_output = list.get(j);
				// ////System.out.println("row_output: "+row_output);
				String female_id = row_output.get(2);
				String female_remarks = row_output.get(3);
				String female_nval = row_output.get(5);
				// male
				String male_id = row_output.get(6);
				String male_remarks = row_output.get(7);
				String male_nval = row_output.get(9);
				cross_date = row_output.get(10);
				// cross name
				String cross = row_output.get(1);

				if (female_id.equals(checked.get(i))
						|| male_id.equals(checked.get(i))) {
					// ////System.out.println("female_remarks: "+female_remarks);
					// ////System.out.println("male_remarks: "+male_remarks);

					if (female_remarks.equals("in standardized format")
							&& male_remarks.equals("in standardized format")) {

						// //System.out.println("\t" + "  ***Process parents");

						/*
						 * ////System.out.println("locationID: "+locationID);
						 * ////System.out.println("checked: "+ checked);
						 * ////System.out.println("list: "+ list);
						 * ////System.out.println("existingTerm: " +
						 * existingTerm);
						 */

						Boolean result = processParents(female_nval, female_id,
								male_nval, male_id, cross, list);
						list = list_local;
						// if(!result){
						// printNotSet(cross, female_nval+"/"+male_nval,
						// female_id + "/" + male_id);
						// }
						// //System.out.println("\t" +
						// "  ***END Process parents");
					}
					break;
				}
			}
		}
		// row_output.clear();
		// list.clear();
		// existingTerm.clear();

		// ////System.out.println("output: "+createdGID_local);
		System.out.println("list: " + list_local);
		existingTerm = existingTerm_local;
		JSONObject data_output = new JSONObject();
		data_output.put("existingTerm", existingTerm);
		data_output.put("list", list_local);
		data_output.put("createdGID", createdGID_local);
		System.out.println("existingTerm @ bulkCreatedGID:"
				+ existingTerm_local);

		// //System.out.println("\t created: "+createdGID_local.size()+"\t"+createdGID_local);

		// //System.out.println(" ###ENDING..BULK CREATION of GID \n");
		factory.close();
		manager = null;
		return data_output;
	}

	public int maxCross(int max, String line) {
		int count = 0, start = 0, end = line.length();
		char currChar;
		while (start < end) {
			currChar = line.charAt(start);
			if (currChar == '/') {
				count++;
				if (max < count) {
					max = count;
				}
			} else {
				count = 0;
			}
			start++;
		}
		return max;
	}

	private JSONObject getParse(String line) throws MiddlewareQueryException,
	IOException { // method of backCrossing
		String temp = line;

		Pattern p = Pattern.compile("\\*\\d"); // backcross to female
		Matcher m = p.matcher(line);

		int max = 0;
		max = maxCross(max, line);
if(line.contains("/") && line.contains("*")){
		while (m.find()) {
			String[] tokens = temp.split("\\*\\d", 2);
			// print(tokens);

			String slash = "";
			max++;
			for (int j = max; j > 0;) {
				slash = slash + "/";
				j--;
			}
			if(tokens[0].startsWith("IR")){
				System.out.println("token: " + tokens[0]);
				tokens[0] = tokens[0].concat(slash).concat(tokens[0]);
				temp.replaceFirst("\\*\\d", tokens[0]);
				temp = tokens[0].concat(tokens[1]);
				System.out.println("token: " + temp);
			}
		}

		System.out.println("\nBackCross to male; ");
		Pattern p1 = Pattern.compile("\\d\\*\\D"); // backcross to male
		Matcher m1 = p1.matcher(line);

		while (m1.find()) {
			System.out.println("slash: " + max);
			String slash1 = "";
			for (int j = max; j > 0;) {
				slash1 = slash1 + "/";
				j--;
			}
			System.out.println("slash: " + slash1);
			String[] tokens = temp.split("\\" + slash1 + "\\d\\*", 2);
			// print(tokens);
			System.out.println(Arrays.toString(tokens));
			String slash = "";

			max++;
			for (int j = max; j > 0;) {
				slash = slash + "/";
				j--;
			}

			if(tokens[1].startsWith("IR")){
				if (tokens.length == 1) {

					tokens = temp.split("\\d\\*", 2);
					tokens[0] = tokens[0].concat(tokens[1]);
					temp.replaceFirst("\\*\\d", tokens[0]);
					System.out.println("token[0]: " + tokens[0]);
					temp = tokens[0].concat(slash.concat(tokens[1]));
					System.out.println("token: " + temp);
				} else {
					tokens[0] = tokens[0].concat(slash.concat(tokens[1]));
					temp.replaceFirst("\\*\\d", tokens[0]);
					System.out.println("token[0]: " + tokens[0]);
					temp = tokens[0].concat(slash1.concat(tokens[1]));
					System.out.println("token: " + temp);	

				}
			}
		}
}
		JSONObject output = new JSONObject();
		output.put("max", max);
		output.put("line", temp);
		return output;

	}

	public List<List<String>> method2(int max, List<String> row,
			List<List<String>> twoDim, List<List<String>> parents,
			List<String> row_parents) throws MiddlewareQueryException,
			IOException {
		if (max > 0) {
			String slash = "";
			for (int i = max; i > 0;) {
				slash = slash + "/";
				i--;
			}

			for (int i = 0; i < twoDim.size(); i++) {
				for (int j = 0; j < row.size(); j++) {
					if ("0".equals(twoDim.get(i).get(1))) {
						// System.out.println("token: " + twoDim.get(i).get(0));

						Pattern p1 = Pattern.compile(slash);
						Matcher m = p1.matcher(twoDim.get(i).get(0));

						while (m.find()) {
							String[] temp2 = twoDim.get(i).get(0)
							.split(slash + "|\\+"); // ncarumba used the
							// character + just
							// to flag where to
							// split the string
							// System.out.println(Arrays.toString(temp2));
							row_parents = new ArrayList<String>();
							row_parents.add(twoDim.get(i).get(0));
							for (int k = 0; k < temp2.length; k++) {
								row = new ArrayList<String>();

								row.add(temp2[k]);
								row.add("0");
								twoDim.add(row);

								if (k % 2 == 0) {
									System.out.println("\n female:   "
											+ temp2[k]);
									row_parents.add(temp2[k]);
								} else {
									System.out.println(" male:     " + temp2[k]
									                                         + "\n");
									row_parents.add(temp2[k]);
								}
							}

							parents.add(row_parents);

							twoDim.get(i).remove(1);
							twoDim.get(i).add("1");
							// System.out.println("end finding "+slash);
						}

						// System.out.println("list:" + twoDim);
					}

				}
			}
			method2(max - 1, row, twoDim, parents, row_parents);
		} else {
			//

			// for(int m=0; m<correctedList.size(); m++){
			// System.out.println("tokens @ parseCross: "+
			// correctedList.get(m));
			// }

			return parents;
		}
		// for(int m=0; m<list.size(); m++){
		// System.out.println("@ return tokens @ parseCross: "+ list.get(m));
		// }

		return parents;
	}

	public Boolean checkString(String id, String pedigree) {
		for (int i = 0; i < createdGID_local.size(); i++) {
			List<String> row = createdGID_local.get(i);
			if (row.get(0).equals(id) && row.get(1).equals(pedigree)) {
				return true;
			}
		}

		return false;
	}

	public void printNotSet_parents_CrossOp(String nval, String id)
	throws MiddlewareQueryException, IOException {
		String parent = nval;
		List<String> pedigreeList = new ArrayList<String>();
		JSONObject result = CrossOp.method2(parent, pedigreeList);
		pedigreeList = (List<String>) result.get("list");
		result.clear();

		System.out.println("PRINT NOT SET");
		for (int i = 0; i < pedigreeList.size(); i++) {
			printNotSet(pedigreeList.get(i), parent, id);
			System.out.println("pedigreeList.get(i): " + pedigreeList.get(i));
			if (pedigreeList.get(i).contains("-")
					&& !pedigreeList.get(i).contains("/")
					&& pedigreeList.get(i).startsWith("IR")) {

				String[] tokens = new Tokenize().tokenize(pedigreeList.get(i));
				ArrayList<String> pedigreeList_der = new ArrayList<String>();

				new AssignGid();
				pedigreeList_der = saveToArray(pedigreeList_der, tokens);
				for (int j = 1; j < pedigreeList_der.size(); j++) {
					// System.out.println("der: "+pedigreeList_der.get(j));
					printNotSet(pedigreeList_der.get(j), parent, id);
				}
				pedigreeList_der.clear();
			}
		}
		pedigreeList.clear();
	}

	public Boolean checkParent_crossOp_updateCreatedGID(String nval, String id)
	throws MiddlewareQueryException, IOException {
		String parent = nval;
		List<String> pedigreeList = new ArrayList<String>();
		List<List<String>> temp = new ArrayList<List<String>>();
		List<List<String>> parents = new ArrayList<List<String>>();
		// pedigreeList.add(parent);
		List<String> row = new ArrayList<String>();
		List<List<String>> backcrosses = new ArrayList<List<String>>();
		List<List<String>> crosses = new ArrayList<List<String>>();
		List<String> parents_bc = new ArrayList<String>();

		new CrossOp();
		JSONObject result_method2 = CrossOp.method2(parent, pedigreeList);
		pedigreeList = (List<String>) result_method2.get("list");
		backcrosses = (List<List<String>>) result_method2.get("backcrosses");
		crosses = (List<List<String>>) result_method2.get("crosses");
		parents_bc = (List<String>) result_method2.get("parents");
		result_method2.clear();
		// pedigreeList.remove(1);

		Boolean isParent = false;
		int notParent_index = -1;
		for (int j = 0; j < pedigreeList.size(); j++) {
			row = new ArrayList<String>();
			// System.out.println("::"+pedigreeList.get(j));
			if ((!pedigreeList.get(j).contains("/")
					&& !pedigreeList.get(j).contains("*")) || (!pedigreeList.get(j).contains("/")
							&& pedigreeList.get(j).contains("*"))) {
				if (!isParent) {
					notParent_index = j;
				}
				row.add(pedigreeList.get(j));
				row.add("0");
				row.add("0");
				parents.add(row);
				// System.out.println("row_parents; "+row);
				isParent = true;
			}
		}
		System.out.println("PARENTS: " + parents);
		System.out.println("last index of the crosses:" + notParent_index);

		System.out.println("------");
		// pedigreeList= parse_crossOp(pedigreeList, nval, id);

		Boolean flag = true;
		Boolean single_hit = false;
		Boolean multiple_hit = false;
		Boolean no_hit = true;
		Boolean no_hit_flag = false;
		Boolean exitLoop = false;
		Boolean result = false;

		List<Integer> check = new ArrayList<Integer>();
		int index = 0;
		int gpid2 = 0;
		int gpid1 = 0;
		int gid = 0;
		int count_LOCAL;
		int count_CENTRAL;
		int count = -1;
		;
		List<Germplasm> germplasm_fin;
		List<Germplasm> germplasm;

		for (int i = 0; i < pedigreeList.size(); i++) {

			germplasm_fin = new ArrayList<Germplasm>();
			germplasm = new ArrayList<Germplasm>();

			String pedigree = pedigreeList.get(i);
			System.out.println("pedigree: " + pedigree);
			if (no_hit) {

				count_LOCAL = countGermplasmByName(pedigree, Database.LOCAL);
				count_CENTRAL = countGermplasmByName(pedigree, Database.CENTRAL);

				germplasm = getGermplasmList(pedigree, count_LOCAL,
						count_CENTRAL);
				// System.out.println("gcount: "+count_LOCAL);
				for (int j = 0; j < germplasm.size(); j++) {
					if (germplasm.get(j).getLocationId().equals(locationID)
							&& germplasm.get(j).getGrplce() == 0) {
						germplasm_fin.add(germplasm.get(j));
					}
				}
				count = germplasm_fin.size();
			}
			System.out.println("count: " + count);
			if (count == -1 || single_hit) {
				System.out.println("\t Single HIT");
				single_hit = true;

				if (i == 0 || no_hit) {
					if (i == 0) {
						// result=true;
					}
					no_hit = false;

					// temp=printSuccess_temp(pedigree, parent, id,
					// germplasm_fin.get(0), "false",temp);
					updateCreatedGID("" + germplasm_fin.get(0).getGid(), id,
							pedigree, "false", createdGID_local);
					GID = germplasm_fin.get(0).getGid();
					gpid2 = germplasm_fin.get(0).getGpid2();
					gpid1 = germplasm_fin.get(0).getGpid1();
					System.out.println("gpid1: " + gpid1);
					System.out.println("gpid2: " + gpid2);
					gid = germplasm_fin.get(0).getGid();

					check.add(gid);
					if (gpid1 != 0 || gpid2 != 0) {
						check.add(gpid1);
						check.add(gpid2);
					}
				} else {

					System.out.println("\t checked: " + check);
					for (int j = 0; j < check.size(); j++) {
						Germplasm g1 = manager.getGermplasmByGID(check.get(j));
						List<Name> name = new ArrayList<Name>();
						System.out.println("check: " + check.get(j));
						System.out.println("gid: " + g1.getGid());
						name = manager.getNamesByGID(g1.getGid(), 0, null);
						System.out.println("name: " + name.get(0).getNval());
						System.out.println("P: " + pedigreeList.get(i));
						if (name.get(0).getNval().equals(pedigreeList.get(i))) {

							System.out.println("\t " + pedigreeList.get(i)
									+ " is found with GID=" + g1.getGid());
							// temp=printSuccess_temp(pedigreeList.get(i),
							// parent, id, g1, "false",temp);
							updateCreatedGID("" + g1.getGid(), id, pedigree,
									"false", createdGID_local);
							if ((!check.contains(g1.getGpid1()))) {
								// System.out.println("\t gpid1 is not in the check list");
								if (g1.getGpid1() != 0) {
									System.out.println("\t gpid1 is not 0");
									check.add(g1.getGpid1());
								}
							}

							if ((!check.contains(g1.getGpid2()))) {
								// System.out.println("\t gpid2 is not in the check list");
								if (g1.getGpid2() != 0) {
									System.out.println("\t gpid2 is not 0");
									check.add(g1.getGpid2());
								}
							}

							gpid1 = g1.getGpid1();
							gpid2 = g1.getGpid2();
							// System.out.println("\t gpid1: "+gpid1);
							// System.out.println("\t gpid2: "+gpid2);

							if (pedigreeList.get(i).contains("-")
									&& !pedigreeList.get(i).contains("/")
									&& pedigreeList.get(i).startsWith("IR")) {
								String[] tokens = new Tokenize()
								.tokenize(pedigreeList.get(i));
								ArrayList<String> pedigreeList_der = new ArrayList<String>();

								new AssignGid();
								pedigreeList_der = saveToArray(
										pedigreeList_der, tokens);
								Germplasm germplasm2 = new Germplasm();
								for (int n = 1; n < pedigreeList_der.size(); n++) {
									System.out.println("add:: "
											+ pedigreeList_der.get(n));
									germplasm2 = manager
									.getGermplasmByGID(gpid2);
									// temp=printSuccess_temp(pedigreeList_der.get(n),
									// parent, id, germplasm2, "false",temp);
									updateCreatedGID("" + germplasm2.getGid(),
											id, pedigreeList_der.get(n),
											"false", createdGID_local);
									gpid1 = germplasm2.getGpid1();
									gpid2 = germplasm2.getGpid2();
									/*
									 * if(i==pedigreeList_der.size()-2){
									 * if(gpid1!=gpid2){
									 * germplasm2=manager.getGermplasmByGID
									 * (gpid1);
									 * //printSuccess(pedigreeList_der.get(n),
									 * parent, id, germplasm2, "false");
									 * updateCreatedGID(""+germplasm2.getGid(),
									 * id, pedigreeList_der.get(n), "false",
									 * createdGID_local); } }
									 */
								}
								germplasm2 = null;
								pedigreeList_der.clear();
								tokens = null;
							}
						}
						name = null;
						g1 = null;
					}
				}

			} else if (count > 0 || multiple_hit) { // multiple hits
				System.out.println("\t Multiple HIT");
				multiple_hit = true;
				if (i == 0) {
					System.out.println("\t i==0");
					// printChooseGID(pedigree, parent, id);
					updateCreatedGID("CHOOSE GID", id, pedigree, "false",
							createdGID_local);
					multipleHits_inLocation(germplasm_fin, pedigree, id, parent);
				} else {
					if (no_hit) {
						System.out.println("\t i!=0");
						// for(int k=0;k<temp.size();k++){
						// createdGID_local.add(temp.get(k));
						// }temp.clear();

						// printChooseGID(pedigree, parent, id);
						updateCreatedGID("CHOOSE GID", id, pedigree, "false",
								createdGID_local);
						multipleHits_inLocation(germplasm_fin, pedigree, id,
								parent);
					} else {
						// printNotSet(pedigreeList.get(i), parent, id);
						// updateCreatedGID("CHOOSE GID", id, pedigree, "false",
						// createdGID_local);
					}
				}
				no_hit = false;

			} else { // not existing
				System.out.println("\t No Hit");
				// System.out.println("pedigree: "+pedigree);
				// temp=printNotSet_temp(pedigree, parent, id, temp);
				index = i;
				no_hit = true;
				no_hit_flag = true;

				if (i == notParent_index - 1) {
					System.out.println("EXIT the LOOP");
					exitLoop = true;
					// should exit the loop, check if parents exist
					break;
				}

			}

			germplasm.clear();
			germplasm_fin.clear();
		}
		if (exitLoop) {
			// check parents if existing
			// if all parents are existing then create GID for crosses with
			// operators
			// if not existing the create GID for that parent
			// if existing and there is a mutliple hits the set CHOOSE GID for
			// that parent
			int gid_parent = 0;
			List<String> row_parents = new ArrayList<String>();

			for (int i = 0; i < parents.size(); i++) {
				row_parents = parents.get(i);

				// System.out.println("r_parent::"+row_parents);
				JSONObject output = getPedigreeLine_updateCreatedGID(
						row_parents.get(0), id, temp, parent, gid_parent);
				Boolean result_flag = (Boolean) output.get("result");
				temp = (List<List<String>>) output.get("temp_fin");
				gid_parent = (Integer) output.get("gid_parent");
				check.add(gid_parent);
				if (result_flag) {
					System.out.println("result::" + result_flag);
					row_parents.set(1, "1");
					row_parents.set(2, "" + gid_parent);
				}

				parents.set(i, row_parents);
				output.clear();
			}
			System.out.println("PARENTS: " + parents);
			int allExisting = 0;
			for (int i = 0; i < parents.size(); i++) {
				row_parents = parents.get(i);
				;
				if (!row_parents.get(2).equals("0")) {
					allExisting++;
				}
			}
			if (allExisting == parents.size()) {
				result = true;
				// all parents exists
				// created GID for the crosses
				// update the temp
				JSONObject output = getParse(pedigreeList.get(0));
				String line = (String) output.get("line");
				int max = (Integer) output.get("max");

				List<List<String>> temp_crossesGID = new ArrayList<List<String>>();

				temp_crossesGID = createGID_crossParents_updateCreatedGID(
						pedigreeList, index, temp_crossesGID, check, line, max,
						parent, id);

			}

		} else if (!exitLoop && no_hit_flag && !multiple_hit) { // if there is a
			// no hit and
			// then a single
			// hit after
			result = true;
			// create GID for the pedigreeList NOT SET
			System.out
			.println("if there is a no hit and then a single hit after");
			JSONObject output = getParse(pedigreeList.get(0));
			String line = (String) output.get("line");
			int max = (Integer) output.get("max");

			List<List<String>> temp_crossesGID = new ArrayList<List<String>>();

			temp_crossesGID = createGID_crossParents_updateCreatedGID(
					pedigreeList, index, temp_crossesGID, check, line, max,
					parent, id);

		}

		temp.clear();
		check.clear();
		pedigreeList.clear();

		return result;
	}

	public Boolean checkParent_crossOp(String nval, String id)
	throws MiddlewareQueryException, IOException {
		String parent = nval;
		List<String> pedigreeList = new ArrayList<String>();
		List<List<String>> temp = new ArrayList<List<String>>();
		List<List<String>> parents = new ArrayList<List<String>>();
		List<List<String>> backcrosses = new ArrayList<List<String>>();
		List<List<String>> crosses = new ArrayList<List<String>>();
		List<String> parents_bc = new ArrayList<String>();

		List<String> row = new ArrayList<String>();
		new CrossOp();

		JSONObject result_method2 = CrossOp.method2(parent, pedigreeList);
		pedigreeList = (List<String>) result_method2.get("list");
		backcrosses = (List<List<String>>) result_method2.get("backcrosses");
		crosses = (List<List<String>>) result_method2.get("crosses");
		parents_bc = (List<String>) result_method2.get("parents");
		result_method2.clear();

		// pedigreeList.remove(1);
		System.out.println("PEDIGREELIST: " + pedigreeList);

		Boolean isParent = false;
		int notParent_index = -1;
		for (int j = 0; j < pedigreeList.size(); j++) {
			row = new ArrayList<String>();
			// System.out.println("::"+pedigreeList.get(j));
			if ((!pedigreeList.get(j).contains("/")
					&& !pedigreeList.get(j).contains("*")) || (!pedigreeList.get(j).contains("/")
							&& pedigreeList.get(j).contains("*"))) {
				if (!isParent) {
					notParent_index = j;
				}
				row.add(pedigreeList.get(j));
				row.add("0");
				row.add("0");
				parents.add(row);
				// System.out.println("row_parents; "+row);
				isParent = true;
			}
		}
		System.out.println("PARENTS: " + parents);
		System.out.println("last index of the crosses:" + notParent_index);

		System.out.println("------");

		Boolean single_hit = false;
		Boolean multiple_hit = false;
		Boolean no_hit = true;
		Boolean no_hit_flag = false;
		Boolean exitLoop = false;
		Boolean result = false;

		List<Integer> check = new ArrayList<Integer>();
		int index = 0;
		int gpid2 = 0;
		int gpid1 = 0;
		int gid = 0;
		int count_LOCAL;
		int count_CENTRAL;
		int count = -1;
		;

		for (int i = 0; i < pedigreeList.size(); i++) {

			List<Germplasm> germplasm_fin = new ArrayList<Germplasm>();
			List<Germplasm> germplasm = new ArrayList<Germplasm>();

			String pedigree = pedigreeList.get(i);
			System.out.println("pedigree: " + pedigree);
			if (no_hit) {

				count_LOCAL = countGermplasmByName(pedigree, Database.LOCAL);
				count_CENTRAL = countGermplasmByName(pedigree, Database.CENTRAL);

				germplasm = getGermplasmList(pedigree, count_LOCAL,
						count_CENTRAL);
				System.out.println("gcount: " + count_LOCAL);
				for (int j = 0; j < germplasm.size(); j++) {
					if (germplasm.get(j).getLocationId().equals(locationID)
							&& germplasm.get(j).getGrplce() == 0) {
						germplasm_fin.add(germplasm.get(j));
					}
				}
				count = germplasm_fin.size();
			}
			System.out.println("count: " + count);
			if (count == -1 || single_hit) {
				System.out.println("\t Single HIT");
				single_hit = true;

				if (i == 0 || no_hit) {
					if (i == 0) {
						result = true;
					}
					no_hit = false;

					temp = printSuccess_temp(pedigree, parent, id,
							germplasm_fin.get(0), "false", temp);
					GID = germplasm_fin.get(0).getGid();
					gpid2 = germplasm_fin.get(0).getGpid2();
					gpid1 = germplasm_fin.get(0).getGpid1();
					System.out.println("gpid1: " + gpid1);
					System.out.println("gpid2: " + gpid2);
					gid = germplasm_fin.get(0).getGid();

					check.add(gid);
					if (gpid1 != 0 || gpid2 != 0) {
						check.add(gpid1);
						check.add(gpid2);
					}
				} else {

					System.out.println("\t checked: " + check);
					for (int j = 0; j < check.size(); j++) {
						Germplasm g1 = manager.getGermplasmByGID(check.get(j));
						List<Name> name = new ArrayList<Name>();
						// System.out.println("check: "+check.get(j));
						// System.out.println("gid: "+g1.getGid());
						name = manager.getNamesByGID(g1.getGid(), 0, null);
						// System.out.println("name: "+name.get(0).getNval());
						// System.out.println("P: "+pedigreeList.get(i));
						if (name.get(0).getNval().equals(pedigreeList.get(i))) {

							System.out.println("\t " + pedigreeList.get(i)
									+ " is found with GID=" + g1.getGid());
							temp = printSuccess_temp(pedigreeList.get(i),
									parent, id, g1, "false", temp);
							if ((!check.contains(g1.getGpid1()))) {
								// System.out.println("\t gpid1 is not in the check list");
								if (g1.getGpid1() != 0) {
									System.out.println("\t gpid1 is not 0");
									check.add(g1.getGpid1());
								}
							}

							if ((!check.contains(g1.getGpid2()))) {
								// System.out.println("\t gpid2 is not in the check list");
								if (g1.getGpid2() != 0) {
									System.out.println("\t gpid2 is not 0");
									check.add(g1.getGpid2());
								}
							}

							gpid1 = g1.getGpid1();
							gpid2 = g1.getGpid2();
							// System.out.println("\t gpid1: "+gpid1);
							// System.out.println("\t gpid2: "+gpid2);

							if (pedigreeList.get(i).contains("-")
									&& !pedigreeList.get(i).contains("/")
									&& pedigreeList.get(i).startsWith("IR")) {
								Pattern p = Pattern.compile("IR");
								Matcher m1 = p.matcher(parent);
								String[] tokens = { "" };
								if (m1.lookingAt()) {
									tokens = new Tokenize()
									.tokenize(pedigreeList.get(i));
								} else {
									tokens[0] = pedigreeList.get(i);
								}
								// String[] tokens = new
								// Tokenize().tokenize(pedigreeList.get(i));
								ArrayList<String> pedigreeList_der = new ArrayList<String>();

								new AssignGid();
								pedigreeList_der = saveToArray(
										pedigreeList_der, tokens);
								for (int n = 1; n < pedigreeList_der.size(); n++) {
									System.out.println("add:: "
											+ pedigreeList_der.get(n));
									Germplasm germplasm2 = manager
									.getGermplasmByGID(gpid2);
									temp = printSuccess_temp(
											pedigreeList_der.get(n), parent,
											id, germplasm2, "false", temp);
									gpid1 = germplasm2.getGpid1();
									gpid2 = germplasm2.getGpid2();
									if (i == pedigreeList_der.size() - 2) {
										if (gpid1 != gpid2) {
											germplasm2 = manager
											.getGermplasmByGID(gpid1);
											printSuccess(
													pedigreeList_der.get(n),
													parent, id, germplasm2,
													"false");
										}
									}
								}
								pedigreeList_der.clear();
								tokens = null;
							}
						}
						name = null;
						g1 = null;
					}
				}

			} else if (count > 0 || multiple_hit) { // multiple hits
				System.out.println("\t Multiple HIT");
				multiple_hit = true;
				if (i == 0) {
					printChooseGID(pedigree, parent, id);
					multipleHits_inLocation(germplasm_fin, pedigree, id, parent);
				} else {
					if (no_hit) {
						System.out.println("TEMP: " + temp);
						for (int k = 0; k < temp.size(); k++) {
							System.out.println("\t: " + temp.get(k));
							createdGID_local.add(temp.get(k));

						}
						temp.clear();
						printChooseGID(pedigree, parent, id);
						multipleHits_inLocation(germplasm_fin, pedigree, id,
								parent);
					} else {
						printNotSet(pedigreeList.get(i), parent, id);
						if (pedigreeList.get(i).contains("-")
								&& !pedigreeList.get(i).contains("/")
								&& pedigreeList.get(i).startsWith("IR")) {

							String[] tokens = new Tokenize()
							.tokenize(pedigreeList.get(i));
							ArrayList<String> pedigreeList_der = new ArrayList<String>();

							new AssignGid();
							pedigreeList_der = saveToArray(pedigreeList_der,
									tokens);
							for (int n = 1; n < pedigreeList_der.size(); n++) {
								printNotSet(pedigreeList_der.get(n), parent, id);
							}
						}
					}
				}
				no_hit = false;

			} else { // not existing
				System.out.println("\t No Hit");
				// System.out.println("pedigree: "+pedigree);
				temp = printNotSet_temp(pedigree, parent, id, temp);
				index = i;
				no_hit = true;
				no_hit_flag = true;

				if (i == notParent_index - 1) {
					System.out.println("EXIT the LOOP");
					exitLoop = true;
					// should exit the loop, check if parents exist
					break;
				}

			}

			germplasm.clear();
			germplasm_fin.clear();
		}
		if (exitLoop) {
			// check parents if existing
			// if all parents are existing then create GID for crosses with
			// operators
			// if not existing the create GID for that parent
			// if existing and there are mutliple hits, set CHOOSE GID for that
			// parent
			int gid_parent = 0;
			List<String> row_parents = new ArrayList<String>();

			for (int i = 0; i < parents.size(); i++) {
				row_parents = parents.get(i);

				// System.out.println("r_parent::"+row_parents);
				JSONObject output = getPedigreeLine(row_parents.get(0), id,
						temp, parent, gid_parent);
				Boolean result_flag = (Boolean) output.get("result");
				temp = (List<List<String>>) output.get("temp_fin");
				gid_parent = (Integer) output.get("gid_parent");
				check.add(gid_parent);
				if (result_flag) {
					System.out.println("result::" + result_flag);
					row_parents.set(1, "1");
					row_parents.set(2, "" + gid_parent);
				}

				parents.set(i, row_parents);
				output.clear();
			}
			System.out.println("PARENTS: " + parents);
			int allExisting = 0;
			for (int i = 0; i < parents.size(); i++) {
				row_parents = parents.get(i);
				;
				if (!row_parents.get(2).equals("0")) {
					allExisting++;
				}
			}
			if (allExisting == parents.size()) {
				result = true;
				// all parents exists
				// created GID for the crosses
				// update the temp
				// JSONObject output=getParse(pedigreeList.get(0));
				// String line= (String) output.get("line");
				// int max= (Integer) output.get("max");
				String line = pedigreeList.get(0);
				int max = 0;
				max = maxCross(max, line);

				List<List<String>> temp_crossesGID = new ArrayList<List<String>>();
				if (line.contains("*")) {
					temp_crossesGID = createGID_bc(temp_crossesGID,
							backcrosses, crosses, parents, parents_bc, parent,
							id);

				} else {
					temp_crossesGID = createGID_crossParents(pedigreeList,
							index, temp_crossesGID, check, line, max, parent,
							id);
				}

				for (int i = 0; i < temp_crossesGID.size(); i++) {
					for (int j = 0; j < temp.size(); j++) {
						if (temp_crossesGID.get(i).get(2)
								.equals(temp.get(j).get(2))) {
							temp.set(j, temp_crossesGID.get(i));
						}
						// System.out.println(""+temp_crossesGID.get(i));

					}
				}
				// System.out.println("[2]***********");
				for (int i = 0; i < temp.size(); i++) {
					// System.out.println(""+temp.get(i));
					createdGID_local.add(temp.get(i));
				}
				// System.out.println("***********");

			} else {
				//
				for (int k = 0; k < temp.size(); k++) {
					System.out.println("" + temp.get(k));
					createdGID_local.add(temp.get(k));
				}// temp.clear();
			}

		} else if (!exitLoop && no_hit_flag && !multiple_hit) { // if there is a
			// no hit and
			// then a single
			// hit after
			result = true;
			// create GID for the pedigreeList NOT SET
			System.out
			.println("if there is a no hit and then a single hit after");
			JSONObject output = getParse(pedigreeList.get(0));
			String line = (String) output.get("line");
			int max = (Integer) output.get("max");

			List<List<String>> temp_crossesGID = new ArrayList<List<String>>();

			temp_crossesGID = createGID_crossParents(pedigreeList, index,
					temp_crossesGID, check, line, max, parent, id);

			for (int i = 0; i < temp_crossesGID.size(); i++) {
				for (int j = 0; j < temp.size(); j++) {
					if (temp_crossesGID.get(i).get(2)
							.equals(temp.get(j).get(2))) {
						temp.set(j, temp_crossesGID.get(i));
					}
					// System.out.println(""+temp_crossesGID.get(i));

				}
			}
			// System.out.println("[2]***********");
			for (int i = 0; i < temp.size(); i++) {
				System.out.println("" + temp.get(i));
				createdGID_local.add(temp.get(i));
			}

		} else {
			System.out.println("temp size: " + temp.size());
			for (int i = 0; i < temp.size(); i++) {
				System.out.println("" + temp.get(i));
				createdGID_local.add(temp.get(i));
			}

		}

		// temp.clear();
		// check.clear();
		// pedigreeList.clear();

		return result;
	}

	public List<List<String>> createGID_bc(List<List<String>> temp_crossesGID,
			List<List<String>> backcrosses, List<List<String>> crosses,
			List<List<String>> parents, List<String> parents_bc, String parent,
			String id) throws MiddlewareQueryException, IOException {
		int index_crossOp = 0;
		String[] tokens2;
		String female = "", male = "";
		// Germplasm fgid,mgid;
		int fgid = 0, mgid = 0;
		int methodID;
		int gid = 0;
		int max = 0;
		int parent2GID = 0;
		String parent2 = "";
		String slash = "";

		List<List<String>> crossesGID = new ArrayList<List<String>>();

		System.out.println("CREATE BACKCROSS------createGID_bc_backcrosses");
		System.out.println("PARENTS ------");
		System.out.println("\t" + parents);
		System.out.println("PARENTS BC------");
		System.out.println("\t" + parents_bc);
		System.out.println("BACKCROSS------");
		for (int j = backcrosses.size() - 1; j >= 0; j--) {
			System.out.println("::" + backcrosses.get(j).get(0));

			if (index_crossOp == 0) {
				max = new CrossOp().maxCross(max, backcrosses.get(j).get(0));
				slash = new BackCross().printSlash(max);
				tokens2 = backcrosses.get(j).get(0).split("\\" + slash);

				for (int i = 0; i < parents.size(); i++) {
					if (parents_bc.get(0).equals(parents.get(i).get(0))) {
						parent2GID = Integer.valueOf(parents.get(i).get(2));
						parent2 = parents.get(i).get(0);
					}
					if (parents.get(i).get(0).equals(tokens2[0])
							|| parents.get(i).get(0).equals(tokens2[1])) {
						if (parents.get(i).get(0).equals(tokens2[0])) {
							fgid = Integer.valueOf(parents.get(i).get(2));
							female = tokens2[0];
							// fgid=manager.getGermplasmByGID(Integer.valueOf(female));
						} else {
							mgid = Integer.valueOf(parents.get(i).get(2));
							male = tokens2[1];
							// mgid=manager.getGermplasmByGID(Integer.valueOf(male));
						}

					}
				}
				methodID = selectMethodType(fgid, mgid, female, male,
						backcrosses.get(j).get(0), 0);
				gid = addGID(backcrosses.get(j).get(0), fgid, mgid, methodID,
						3, false);
				crossesGID = printSuccess_temp(backcrosses.get(j).get(0),
						parent, id, manager.getGermplasmByGID(gid), "new",
						crossesGID);
				backcrosses.get(j).set(1, "" + gid);
				backcrosses.set(j, backcrosses.get(j));

			} else {
				Germplasm g1 = manager.getGermplasmByGID(gid);
				List<Name> name = manager.getNamesByGID(g1.getGpid1(), 0, null);
				List<Name> name1 = manager
				.getNamesByGID(g1.getGpid2(), 0, null);
				methodID = 0;
				if (name.size() > 0 && name1.size() > 0) {
					if (name.get(0).getNval().equals(parent2)
							|| name1.get(0).getNval().equals(parent2)) {
						methodID = 107;
					}
					name.clear();
					name1.clear();
					g1 = null;
				}
				methodID = selectMethodType(gid, parent2GID,
						backcrosses.get(j + 1).get(0), parent2, backcrosses
						.get(j).get(0), methodID);
				gid = addGID(backcrosses.get(j).get(0), gid, parent2GID,
						methodID, 3, false);
				crossesGID = printSuccess_temp(backcrosses.get(j).get(0),
						parent, id, manager.getGermplasmByGID(gid), "new",
						crossesGID);
				backcrosses.get(j).set(1, "" + gid);
				backcrosses.set(j, backcrosses.get(j));

			}
			index_crossOp++;
		}
		System.out.println("BACKCROSS------");
		for (int j = 0; j < backcrosses.size(); j++) {
			System.out.println("::" + backcrosses.get(j));
		}
		System.out.println("------");

		for (int j = crosses.size() - 1; j >= 0; j--) {
			if (j == crosses.size() - 1) {
				System.out.println("GID: " + gid);
				System.out.println("GID2: " + parent2GID);
				methodID = selectMethodType(gid, parent2GID, backcrosses.get(0)
						.get(0), parent2, crosses.get(j).get(0), 107);
				gid = addGID(crosses.get(j).get(0), gid, parent2GID, methodID,
						3, false);
				crossesGID = printSuccess_temp(crosses.get(j).get(0), parent,
						id, manager.getGermplasmByGID(gid), "new", crossesGID);
				crosses.get(j).set(1, "" + gid);
				crosses.set(j, crosses.get(j));
			} else {

				max = CrossOp.maxCross(max, crosses.get(j).get(0));
				new BackCross();
				slash = BackCross.printSlash(max);
				tokens2 = crosses.get(j).get(0).split("\\" + slash, 2);
				System.out.print("tokens2[0]: " + tokens2[0]);
				System.out.println("\ttokens2[1]: " + tokens2[1]);
				if (!tokens2[0].contains("/")) {
					if (tokens2[0].contains("*")) {
						Pattern p2 = Pattern.compile("\\*\\d"); // backcross to
						// female
						Matcher m2 = p2.matcher(tokens2[0]);
						Boolean female_match = false;
						while (m2.find()) {
							female_match = true;
							tokens2[0] = tokens2[0].replaceAll("\\*\\d", "");
							System.out.println("new tokens2[0]: " + tokens2[0]);
						}
						if (!female_match) {
							tokens2[0] = tokens2[0].replaceAll("\\d\\*", "");
							System.out.println("\\d\\*: " + tokens2[0]);
						}
					}
					for (int i = 0; i < parents.size(); i++) {

						if (parents.get(i).get(0).equals(tokens2[0])) {
							fgid = Integer.parseInt(parents.get(i).get(2));
							female = tokens2[0];
						}
					}
				} else {
					for (int i = 0; i < crosses.size(); i++) {
						if (crosses.get(i).get(0).equals(tokens2[0])) {
							fgid = Integer.parseInt(crosses.get(i).get(1));
							female = tokens2[0];
						}
					}

				}

				if (!tokens2[1].contains("/")) {
					if (tokens2[1].contains("*")) {
						Pattern p2 = Pattern.compile("\\*\\d"); // backcross to
						// female
						Matcher m2 = p2.matcher(tokens2[1]);
						Boolean female_match = false;
						while (m2.find()) {
							female_match = true;
							tokens2[1] = tokens2[1].replaceAll("\\*\\d", "");
							System.out.println("new tokens2[1]: " + tokens2[1]);
						}
						if (!female_match) {
							tokens2[1] = tokens2[1].replaceAll("\\d\\*", "");
							System.out.println("\\d\\*: " + tokens2[1]);
						}
					}
					for (int i = 0; i < parents.size(); i++) {
						if (parents.get(i).get(0).equals(tokens2[1])) {
							mgid = Integer.parseInt(parents.get(i).get(2));
							male = tokens2[1];
						}
					}
				} else {
					for (int i = 0; i < crosses.size(); i++) {
						if (crosses.get(i).get(0).equals(tokens2[1])) {
							mgid = Integer.parseInt(crosses.get(i).get(1));
							male = tokens2[1];
						}
					}
				}

				Germplasm g1 = manager.getGermplasmByGID(fgid);
				List<Name> name = manager.getNamesByGID(g1.getGpid1(), 0, null);
				List<Name> name1 = manager
				.getNamesByGID(g1.getGpid2(), 0, null);
				methodID = 0;
				if (name.size() > 0 && name1.size() > 0) {
					if (name.get(0).getNval().equals(male)
							|| name1.get(0).getNval().equals(male)) {
						methodID = 107;
					}
					name.clear();
					name1.clear();
					g1 = null;
				}

				methodID = selectMethodType(fgid, mgid, female, male, crosses
						.get(j).get(0), methodID);
				gid = addGID(crosses.get(j).get(0), fgid, mgid, methodID, 3,
						false);
				crossesGID = printSuccess_temp(crosses.get(j).get(0), parent,
						id, manager.getGermplasmByGID(gid), "new", crossesGID);
				crosses.get(j).set(1, "" + gid);
				crosses.set(j, crosses.get(j));
				if (j == 0) {
					GID = gid;
				}
			}
		}
		// Collections.reverse(crosses);

		return crossesGID;
	}

	public List<List<String>> createGID_crossParents_updateCreatedGID(
			List<String> pedigreeList, int index,
			List<List<String>> temp_crossesGID, List<Integer> check,
			String line, int max, String parent, String id)
			throws MiddlewareQueryException, IOException {

		String female_nval = null, male_nval;

		int methodID = 0;
		int gid_cross;
		int gpid1 = 0, gpid2 = 0;

		List<List<String>> twoDim = new ArrayList<List<String>>();
		List<String> row2 = new ArrayList<String>();
		List<List<String>> crosses = new ArrayList<List<String>>();
		List<String> row_crosses = new ArrayList<String>();

		row2.add(line);
		row2.add("0"); // 0 for unexplored token
		twoDim.add(row2);
		crosses = method2(max, row2, twoDim, crosses, row_crosses);
		crosses.get(0).set(0, parent);

		System.out.println("***" + crosses);
		System.out.println("***" + check);
		for (int i = crosses.size() - 1; i >= 0; i--) {
			row_crosses = crosses.get(i);
			for (int a = index; a >= 0; a--) {
				System.out.println("pedigreelist: " + pedigreeList.get(a)
						+ " crosses: " + row_crosses.get(0));
				if (pedigreeList.get(a).equals(row_crosses.get(0))) {
					for (int k = 1; k < 3; k++) {
						for (int j = 0; j < check.size(); j++) {
							System.out.println("K: " + k);
							System.out.println(" crosses: "
									+ row_crosses.get(k));
							Germplasm g1 = manager.getGermplasmByGID(check
									.get(j));
							List<Name> name = new ArrayList<Name>();
							// System.out.println("check: "+check.get(j));
							// System.out.println("gid: "+g1.getGid());
							name = manager.getNamesByGID(g1.getGid(), 0, null);
							System.out
							.println("name: " + name.get(0).getNval());
							// System.out.println("P: "+pedigreeList.get(i));
							if (name.get(0).getNval()
									.equals(row_crosses.get(k))) {

								if (k == 1) {
									System.out.println("FEMALE: ");
									System.out.println("\t "
											+ row_crosses.get(k)
											+ " is found with GID="
											+ g1.getGid());
									female_nval = row_crosses.get(k);
									gpid1 = g1.getGid();
								} else {
									System.out.println("MALE: ");
									System.out.println("\t "
											+ row_crosses.get(k)
											+ " is found with GID="
											+ g1.getGid());

									gpid2 = g1.getGid();
									male_nval = row_crosses.get(k);

									Germplasm g2 = manager
									.getGermplasmByGID(gpid1);
									List<Name> name1 = manager.getNamesByGID(
											g2.getGpid1(), 0, null);
									List<Name> name2 = manager.getNamesByGID(
											g2.getGpid2(), 0, null);
									methodID = 0;
									if (name.size() > 0 && name1.size() > 0) {
										if (name1.get(0).getNval()
												.equals(male_nval)
												|| name2.get(0).getNval()
												.equals(male_nval)) {
											methodID = 107;
										}
										name.clear();
										name1.clear();
										g2 = null;
									}

									methodID = selectMethodType(gpid1, gpid2,
											female_nval, male_nval, parent,
											methodID);
									if (i == 0) {

										gid_cross = addGID(parent, gpid1,
												gpid2, methodID, 2, false); // ntype=2
										GID = gid_cross;
										g1 = manager
										.getGermplasmByGID(gid_cross);
										// temp_crossesGID=printSuccess_temp(parent,
										// parent, id, g1, "new",
										// temp_crossesGID);
										updateCreatedGID("" + g1.getGid(), id,
												parent, "new", createdGID_local);
									} else {

										gid_cross = addGID(row_crosses.get(0),
												gpid1, gpid2, methodID, 2,
												false); // ntype=2
										g1 = manager
										.getGermplasmByGID(gid_cross);
										// temp_crossesGID=printSuccess_temp(row_crosses.get(0),
										// parent, id, g1, "new",
										// temp_crossesGID);
										updateCreatedGID("" + g1.getGid(), id,
												parent, "new", createdGID_local);
									}

									check.add(gid_cross);

								}
								break;
							}
							name = null;
							g1 = null;
						}
					}
				}
			}
		}
		// System.out.println("[1]**********");
		twoDim.clear();
		row_crosses.clear();
		crosses.clear();
		row2.clear();

		return temp_crossesGID;
	}

	public List<List<String>> createGID_crossParents(List<String> pedigreeList,
			int index, List<List<String>> temp_crossesGID, List<Integer> check,
			String line, int max, String parent, String id)
			throws MiddlewareQueryException, IOException {

		String female_nval = null, male_nval;

		int methodID = 0;
		int gid_cross;
		int gpid1 = 0, gpid2 = 0;

		List<List<String>> twoDim = new ArrayList<List<String>>();
		List<String> row2 = new ArrayList<String>();
		List<List<String>> crosses = new ArrayList<List<String>>();
		List<String> row_crosses = new ArrayList<String>();

		row2.add(line);
		row2.add("0"); // 0 for unexplored token
		twoDim.add(row2);
		crosses = method2(max, row2, twoDim, crosses, row_crosses);
		crosses.get(0).set(0, parent);

		System.out.println("***" + crosses);
		System.out.println("***" + check);
		for (int i = crosses.size() - 1; i >= 0; i--) {
			row_crosses = crosses.get(i);
			for (int a = index; a >= 0; a--) {
				System.out.println("pedigreelist: " + pedigreeList.get(a)
						+ " crosses: " + row_crosses.get(0));
				if (pedigreeList.get(a).equals(row_crosses.get(0))) {
					for (int k = 1; k < 3; k++) {
						for (int j = 0; j < check.size(); j++) {
							System.out.println("K: " + k);
							System.out.println(" crosses: "
									+ row_crosses.get(k));
							Germplasm g1 = manager.getGermplasmByGID(check
									.get(j));
							List<Name> name = new ArrayList<Name>();
							// System.out.println("check: "+check.get(j));
							// System.out.println("gid: "+g1.getGid());
							name = manager.getNamesByGID(g1.getGid(), 0, null);
							System.out
							.println("name: " + name.get(0).getNval());
							// System.out.println("P: "+pedigreeList.get(i));
							if (name.get(0).getNval()
									.equals(row_crosses.get(k))) {

								if (k == 1) {
									System.out.println("FEMALE: ");
									System.out.println("\t "
											+ row_crosses.get(k)
											+ " is found with GID="
											+ g1.getGid());
									female_nval = row_crosses.get(k);
									gpid1 = g1.getGid();
								} else {
									System.out.println("MALE: ");
									System.out.println("\t "
											+ row_crosses.get(k)
											+ " is found with GID="
											+ g1.getGid());

									gpid2 = g1.getGid();
									male_nval = row_crosses.get(k);
									Germplasm g2 = manager
									.getGermplasmByGID(gpid1);
									List<Name> name1 = manager.getNamesByGID(
											g2.getGpid1(), 0, null);
									List<Name> name2 = manager.getNamesByGID(
											g2.getGpid2(), 0, null);
									methodID = 0;
									if (name.size() > 0 && name1.size() > 0) {
										if (name1.get(0).getNval()
												.equals(male_nval)
												|| name2.get(0).getNval()
												.equals(male_nval)) {
											methodID = 107;
										}
										name1.clear();
										name2.clear();
										g2 = null;
									}
									methodID = selectMethodType(gpid1, gpid2,
											female_nval, male_nval, parent,
											methodID);
									if (i == 0) {

										gid_cross = addGID(parent, gpid1,
												gpid2, methodID, 3, false); // ntype=3
										GID = gid_cross;
										g1 = manager
										.getGermplasmByGID(gid_cross);
										temp_crossesGID = printSuccess_temp(
												parent, parent, id, g1, "new",
												temp_crossesGID);
									} else {

										gid_cross = addGID(row_crosses.get(0),
												gpid1, gpid2, methodID, 3,
												false);// ntype=3
										g1 = manager
										.getGermplasmByGID(gid_cross);
										temp_crossesGID = printSuccess_temp(
												row_crosses.get(0), parent, id,
												g1, "new", temp_crossesGID);
									}

									check.add(gid_cross);

								}
								break;
							}
							name = null;
							g1 = null;
						}
					}
				}
			}
		}
		// System.out.println("[1]**********");
		twoDim.clear();
		row_crosses.clear();
		crosses.clear();
		row2.clear();

		return temp_crossesGID;
	}

	public JSONObject getPedigreeLine_updateCreatedGID(String parent,
			String id, List<List<String>> temp_fin, String parent2, int GID)
	throws MiddlewareQueryException, IOException {

		String[] tokens = new Tokenize().tokenize(parent);
		ArrayList<String> pedigreeList = new ArrayList<String>();

		pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0] is
		// the most recent
		// pedigree,
		// pedigreeList[size]
		// is the root

		Boolean flag = true;
		Boolean single_hit = false;
		Boolean multiple_hit = false;
		Boolean error = false;
		Boolean result = false;

		List<Germplasm> germplasm_fin = new ArrayList<Germplasm>();
		List<Germplasm> germplasm = new ArrayList<Germplasm>();
		List<Germplasm> germplasmList = null;

		List<List<String>> temp = new ArrayList<List<String>>();
		int index = 0;
		int gpid2 = 0;
		int gpid1 = 0;
		int gid = 0;
		int gpid2_index = 0;

		// index 0 is the parent, index pedigreeList.size()-1 is the root
		for (int i = 0; i < pedigreeList.size(); i++) {
			String pedigree = pedigreeList.get(i);
			System.out.println("pedigree: " + pedigree);
			System.out.println("result: " + result);
			if (flag) {
				int count_LOCAL = countGermplasmByName(pedigree, Database.LOCAL);
				int count_CENTRAL = countGermplasmByName(pedigree,
						Database.CENTRAL);

				germplasm = getGermplasmList(pedigree, count_LOCAL,
						count_CENTRAL);
				System.out.println("count in db: " + germplasm.size());
				for (int j = 0; j < germplasm.size(); j++) {
					// System.out.println("\t ["+j+"]: "+germplasm.get(j).getLocationId());
					if (germplasm.get(j).getLocationId().equals(locationID)
							&& germplasm.get(j).getGrplce() == 0) {
						germplasm_fin.add(germplasm.get(j));
					}
				}
				int count = germplasm_fin.size();

				if (count == 1) { // only one germplasm with that name in the
					// location
					System.out.print("count==1");
					System.out.println("\t " + pedigree);

					if (i == 0) { // if it is the parent
						// temp_fin=printSuccess_temp(pedigree, parent2, id,
						// germplasm_fin.get(0), "false",temp_fin);
						updateCreatedGID("" + germplasm_fin.get(0).getGid(),
								id, pedigree, "false", createdGID_local);
						GID = germplasm_fin.get(0).getGid();
						gpid2 = germplasm_fin.get(0).getGpid2();
						gpid1 = germplasm_fin.get(0).getGpid1();
						gid = germplasm_fin.get(0).getGid();
					} else if (i == pedigreeList.size() - 1) { // if root
						// germplasm_fin.get(0)
						List<Germplasm> list = new ArrayList<Germplasm>();
						gid = germplasm_fin.get(0).getGid();
						index = i;

						System.out.println("create GID from "
								+ pedigreeList.get(i - 1));
						for (int k = i - 1; k >= 0; k--) {
							System.out.println("\t create GID: "
									+ pedigreeList.get(k));
							if (k == i - 1) {
								gpid1 = gid;
							}
							int methodID = selectMethodType_DER(
									pedigreeList.get(i), parent);

							int gid_single_hit = (int) addGID(
									pedigreeList.get(k), gpid1, gid, methodID,
									5, false); // ntype=5
							if (k == 0) {
								GID = gid_single_hit;
							}
							gid = gid_single_hit;

							Germplasm g = manager
							.getGermplasmByGID(gid_single_hit);

							list.add(g);

						}
						int l = 0;
						for (int m = list.size() - 1; m >= 0; m--) {
							// printSuccess(pedigreeList.get(l), parent, id,
							// list.get(m), "new");
							// temp_fin=printSuccess_temp(pedigreeList.get(l),
							// parent2, id, list.get(m), "new",temp_fin);
							updateCreatedGID("" + list.get(0).getGid(), id,
									pedigreeList.get(l), "new",
									createdGID_local);
							l++;
						}
						list.clear();

						// temp_fin=printSuccess_temp(pedigreeList.get(i),
						// parent2, id, germplasm_fin.get(0), "false",temp_fin);
						updateCreatedGID("" + germplasm_fin.get(0).getGid(),
								id, pedigreeList.get(i), "false",
								createdGID_local);
					} else { // not root and not the parent
						System.out.print("i: " + i);
						System.out.println("\t|  " + pedigree);
						// temp=printSuccess_temp(pedigree, parent2, id,
						// germplasm_fin.get(0), "false", temp);
						updateCreatedGID("" + germplasm_fin.get(0).getGid(),
								id, pedigree, "false", createdGID_local);
						gpid1 = germplasm_fin.get(0).getGpid1();
						gpid2_index = germplasm_fin.get(0).getGpid2();
						gid = germplasm_fin.get(0).getGid();
					}

					flag = false;
					single_hit = true;
					multiple_hit = false;
					index = i;
					result = true;

					// System.out.println("TEMP: "+temp);

				} else if (count > 1) { // multiple germplasm name in a location
					System.out.print("count>1");
					System.out.println("\t " + pedigree);

					multipleHits_inLocation(germplasm_fin, pedigree, id,
							parent2);
					if (i == 0) // if it is the parent
						// temp_fin=printChooseGID_temp(pedigree, parent2,
						// id,temp_fin);
						updateCreatedGID("CHOOSE GID", id, pedigree, "false",
								createdGID_local);
					else if (i == pedigreeList.size() - 1) { // if it is the
						// root

						// temp_fin=printChooseGID_temp(pedigree, parent2,
						// id,temp_fin);
						updateCreatedGID("CHOOSE GID", id, pedigree, "false",
								createdGID_local);
					} else { // if not the root and not the parent
						// temp=printChooseGID_temp(pedigree, parent2, id,
						// temp);
						updateCreatedGID("CHOOSE GID", id, pedigree, "false",
								createdGID_local);
					}
					multiple_hit = true;
					flag = false;
					index = i;
					result = false;

				} else {
					System.out.print("count==0");
					System.out.println("\t " + pedigree);

					// no germplasm name in the list's location
					if (i == pedigreeList.size() - 1) { // if root assign GID
						// from the root
						JSONObject output2 = createPedigreeLine_CrossOp(
								pedigreeList, id, pedigreeList.get(i), parent2,
								temp_fin, GID);
						GID = (Integer) output2.get("GID");
						temp_fin = (List<List<String>>) output2.get("temp_fin");

						result = true;
					} else { // else, not root, print NOT SET

						// temp=printNotSet_temp(pedigree, parent2, id, temp);
						single_hit = false;
						multiple_hit = false;
						flag = true;
					}
				}

			} else {
				if (multiple_hit) {
					System.out.println("\t multiple hit is true");

					single_hit = false;
					flag = false;
					multiple_hit = true;
					result = false;
				} else if (single_hit) {
					System.out.println("\t Single hit is true");
					if (i - 1 == index && (i - 1) != 0) { // if the previous is
						// not the parent
						// and is the
						// 'index'
						// create GID for the index onwards

						List<Germplasm> list = new ArrayList<Germplasm>();
						// gid=germplasm_fin.get(0).getGid();

						System.out.println("create GID from "
								+ pedigreeList.get(i - 2));
						for (int k = i - 2; k >= 0; k--) {
							System.out.println("\t create GID: "
									+ pedigreeList.get(k));

							int methodID = selectMethodType_DER(
									pedigreeList.get(i), parent);

							gpid2 = gid;

							System.out.println("gpid1: " + gpid1);
							System.out.println("gpid2: " + gpid2);

							int gid_single_hit = (int) addGID(
									pedigreeList.get(k), gpid1, gpid2,
									methodID, 5, false); // ntype=5
							if (k == 0) {
								GID = gid_single_hit;
							}
							gid = gid_single_hit;

							Germplasm g = manager
							.getGermplasmByGID(gid_single_hit);

							// printSuccess(pedigreeList.get(k), parent, id, g,
							// "false");
							list.add(g);

						}
						int l = 0;
						for (int m = list.size() - 1; m >= 0; m--) {
							// temp_fin=printSuccess_temp(pedigreeList.get(l),
							// parent2, id, list.get(m), "new",temp_fin);
							updateCreatedGID("" + list.get(m).getGid(), id,
									pedigreeList.get(l), "new",
									createdGID_local);
							l++;
						}
						list.clear();

						// printSuccess(pedigreeList.get(i-1), parent, id,
						// germplasm_fin.get(0), "false");
						// temp_fin.add(temp.get(index));
						updateCreatedGID("" + germplasm_fin.get(0).getGid(),
								id, pedigreeList.get(l), "false",
								createdGID_local);

						Germplasm g = manager.getGermplasmByGID(gpid2_index);

						if (g == null) { // this is an ERROR, the gid should
							// exist
							error = true;

							// temp_fin=printError_temp(pedigree,
							// parent2,id,temp); //prints ERROR to file

						} else {
							gpid2 = g.getGpid2();
							gpid1 = g.getGpid1();
							error = false; // set the flag to false

							// temp_fin=printSuccess_temp(pedigree,parent2,id,
							// g, "false",temp_fin); //print to file
							updateCreatedGID("" + g.getGid(), id, pedigree,
									"new", createdGID_local);
							System.out.println("\t" + pedigree + " is found");
						}
					} else {
						Germplasm g;
						if (error) { // if the precedent pedigree does not exist

							germplasmList = new ArrayList<Germplasm>();

							int count_LOCAL = countGermplasmByName(pedigree,
									Database.LOCAL);
							int count_CENTRAL = countGermplasmByName(pedigree,
									Database.CENTRAL);
							germplasmList = getGermplasmList(pedigree,
									count_LOCAL, count_CENTRAL); // gets lists
							// of
							// germplasm
							// with that
							// name
							g = getGermplasmByGpid(gpid1, germplasmList); // get
							// the
							// germplasm
							// of
							// the
							// same
							// gpid1,
							// for
							// derivative
							// line,
							// or
							// gid
							// equals
							// to
							// the
							// gpid1
						} else {
							g = manager.getGermplasmByGID(gpid2);
							System.out.println("gpid2=" + gpid2);
						}
						if (g == null) { // this is an ERROR, the gid should
							// exist
							error = true;

							// temp_fin=printError_temp(pedigree,
							// parent2,id,temp_fin); //prints ERROR to file

						} else {
							gpid2 = g.getGpid2();
							gpid1 = g.getGpid1();
							error = false; // set the flag to false

							// temp_fin=printSuccess_temp(pedigree,parent2,id,
							// g, "false",temp_fin); //print to file
							updateCreatedGID("" + g.getGid(), id, pedigree,
									"new", createdGID_local);
						}
					}
					single_hit = true;
					flag = false;
					multiple_hit = false;
					result = true;
				}
			}
		}

		temp.clear();
		germplasm.clear();
		germplasm_fin.clear();
		JSONObject output = new JSONObject();
		System.out.println(parent + ": " + result);
		output.put("result", result);
		output.put("temp_fin", temp_fin);
		output.put("gid_parent", GID);
		return output;
	}

	public JSONObject getPedigreeLine(String parent, String id,
			List<List<String>> temp_fin, String parent2, int GID)
	throws MiddlewareQueryException, IOException {
		Pattern p = Pattern.compile("^IR");
		Matcher m1 = p.matcher(parent);
		String[] tokens = { "" };
		if (m1.lookingAt()) {
			System.out.println("starts at IR @ getPedLine");
			tokens = new Tokenize().tokenize(parent);
		} else {
			tokens[0] = parent;
		}
		// String[] tokens = new Tokenize().tokenize(parent);
		ArrayList<String> pedigreeList = new ArrayList<String>();

		pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0] is
		// the most recent
		// pedigree,
		// pedigreeList[size]
		// is the root
		System.out.println("ped list @ get Ped line: " + pedigreeList);
		System.out.println("parent @ get Ped line: " + parent);

		Boolean flag = true;
		Boolean single_hit = false;
		Boolean multiple_hit = false;
		Boolean error = false;
		Boolean result = false;

		List<Germplasm> germplasm_fin = new ArrayList<Germplasm>();
		List<Germplasm> germplasm = new ArrayList<Germplasm>();
		List<Germplasm> germplasmList = null;

		List<List<String>> temp = new ArrayList<List<String>>();
		int index = 0;
		int gpid2 = 0;
		int gpid1 = 0;
		int gid = 0;
		int gpid2_index = 0;

		// index 0 is the parent, index pedigreeList.size()-1 is the root
		for (int i = 0; i < pedigreeList.size(); i++) {
			String pedigree = pedigreeList.get(i);
			System.out.println("pedigree: " + pedigree);
			System.out.println("result: " + result);
			if (flag) {
				int count_LOCAL = countGermplasmByName(pedigree, Database.LOCAL);
				int count_CENTRAL = countGermplasmByName(pedigree,
						Database.CENTRAL);

				germplasm = getGermplasmList(pedigree, count_LOCAL,
						count_CENTRAL);
				System.out.println("count in db: " + germplasm.size());
				for (int j = 0; j < germplasm.size(); j++) {
					// System.out.println("\t ["+j+"]: "+germplasm.get(j).getLocationId());
					if (germplasm.get(j).getLocationId().equals(locationID)
							&& germplasm.get(j).getGrplce() == 0) {
						germplasm_fin.add(germplasm.get(j));
					}
				}
				int count = germplasm_fin.size();

				if (count == -1) { // only one germplasm with that name in the
					// location
					System.out.print("count==1");
					System.out.println("\t " + pedigree);

					if (i == 0) { // if it is the parent
						temp_fin = printSuccess_temp(pedigree, parent2, id,
								germplasm_fin.get(0), "false", temp_fin);
						GID = germplasm_fin.get(0).getGid();
						gpid2 = germplasm_fin.get(0).getGpid2();
						gpid1 = germplasm_fin.get(0).getGpid1();
						gid = germplasm_fin.get(0).getGid();
					} else if (i == pedigreeList.size() - 1) { // if root
						// germplasm_fin.get(0)
						List<Germplasm> list = new ArrayList<Germplasm>();
						gid = germplasm_fin.get(0).getGid();
						index = i;

						System.out.println("create GID from "
								+ pedigreeList.get(i - 1));
						for (int k = i - 1; k >= 0; k--) {
							System.out.println("\t create GID: "
									+ pedigreeList.get(k));
							if (k == i - 1) {
								gpid1 = gid;
							}
							int methodID = selectMethodType_DER(
									pedigreeList.get(i), parent);

							int gid_single_hit = (int) addGID(
									pedigreeList.get(k), gpid1, gid, methodID,
									5, false); // ntype=5
							if (k == 0) {
								GID = gid_single_hit;
							}
							gid = gid_single_hit;

							Germplasm g = manager
							.getGermplasmByGID(gid_single_hit);

							list.add(g);

						}
						int l = 0;
						for (int m = list.size() - 1; m >= 0; m--) {
							// printSuccess(pedigreeList.get(l), parent, id,
							// list.get(m), "new");
							temp_fin = printSuccess_temp(pedigreeList.get(l),
									parent2, id, list.get(m), "new", temp_fin);
							l++;
						}
						list.clear();

						temp_fin = printSuccess_temp(pedigreeList.get(i),
								parent2, id, germplasm_fin.get(0), "false",
								temp_fin);
					} else { // not root and not the parent
						System.out.print("i: " + i);
						System.out.println("\t|  " + pedigree);
						temp = printSuccess_temp(pedigree, parent2, id,
								germplasm_fin.get(0), "false", temp);
						gpid1 = germplasm_fin.get(0).getGpid1();
						gpid2_index = germplasm_fin.get(0).getGpid2();
						gid = germplasm_fin.get(0).getGid();
					}

					flag = false;
					single_hit = true;
					multiple_hit = false;
					index = i;
					result = true;

					// System.out.println("TEMP: "+temp);

				} else if (count > 0) { // multiple germplasm name in a location
					System.out.print("count>1");
					System.out.println("\t " + pedigree);

					multipleHits_inLocation(germplasm_fin, pedigree, id,
							parent2);
					if (i == 0) // if it is the parent
						temp_fin = printChooseGID_temp(pedigree, parent2, id,
								temp_fin);
					else if (i == pedigreeList.size() - 1) { // if it is the
						// root
						for (int k = 0; k < temp.size(); k++) {
							temp_fin.add(temp.get(k));
						}
						temp_fin = printChooseGID_temp(pedigree, parent2, id,
								temp_fin);
					} else { // if not the root and not the parent
						temp = printChooseGID_temp(pedigree, parent2, id, temp);
					}
					multiple_hit = true;
					flag = false;
					index = i;
					result = false;

				} else {
					System.out.print("count==0");
					System.out.println("\t " + pedigree);

					// no germplasm name in the list's location
					if (i == pedigreeList.size() - 1) { // if root, assign GID
						// from the root
						JSONObject output2 = createPedigreeLine_CrossOp(
								pedigreeList, id, pedigreeList.get(i), parent2,
								temp_fin, GID);
						GID = (Integer) output2.get("GID");
						temp_fin = (List<List<String>>) output2.get("temp_fin");

						result = true;
					} else { // else, not root, print NOT SET

						temp = printNotSet_temp(pedigree, parent2, id, temp);
						single_hit = false;
						multiple_hit = false;
						flag = true;
					}
				}

			} else {
				if (multiple_hit) {
					System.out.println("\t multiple hit is true");
					if (i - 1 == index && (i - 1) != 0) { // if the previous is
						// the 'index' and
						// not the parent

						for (int k = 0; k < temp.size(); k++) {
							temp_fin.add(temp.get(k));
						}
						temp_fin = printNotSet_temp(pedigree, parent2, id,
								temp_fin);
					} else {
						System.out.println("\t" + pedigree + "is NOT SET");
						temp_fin = printNotSet_temp(pedigree, parent2, id,
								temp_fin);
					}
					single_hit = false;
					flag = false;
					multiple_hit = true;
					result = false;
				} else if (single_hit) {
					System.out.println("\t Single hit is true");
					if (i - 1 == index && (i - 1) != 0) { // if the previous is
						// not the parent
						// and is the
						// 'index'
						// create GID for the index onwards

						List<Germplasm> list = new ArrayList<Germplasm>();
						// gid=germplasm_fin.get(0).getGid();

						System.out.println("create GID from "
								+ pedigreeList.get(i - 2));
						for (int k = i - 2; k >= 0; k--) {
							System.out.println("\t create GID: "
									+ pedigreeList.get(k));

							int methodID = selectMethodType_DER(
									pedigreeList.get(i), parent);

							gpid2 = gid;

							System.out.println("gpid1: " + gpid1);
							System.out.println("gpid2: " + gpid2);

							int gid_single_hit = (int) addGID(
									pedigreeList.get(k), gpid1, gpid2,
									methodID, 5, false); // ntype=5
							if (k == 0) {
								GID = gid_single_hit;
							}
							gid = gid_single_hit;

							Germplasm g = manager
							.getGermplasmByGID(gid_single_hit);

							// printSuccess(pedigreeList.get(k), parent, id, g,
							// "false");
							list.add(g);

						}
						int l = 0;
						for (int m = list.size() - 1; m >= 0; m--) {
							temp_fin = printSuccess_temp(pedigreeList.get(l),
									parent2, id, list.get(m), "new", temp_fin);
							l++;
						}
						list.clear();

						// printSuccess(pedigreeList.get(i-1), parent, id,
						// germplasm_fin.get(0), "false");
						temp_fin.add(temp.get(index));

						Germplasm g = manager.getGermplasmByGID(gpid2_index);

						if (g == null) { // this is an ERROR, the gid should
							// exist
							error = true;

							temp_fin = printError_temp(pedigree, parent2, id,
									temp); // prints ERROR to file

						} else {
							gpid2 = g.getGpid2();
							gpid1 = g.getGpid1();
							error = false; // set the flag to false

							temp_fin = printSuccess_temp(pedigree, parent2, id,
									g, "false", temp_fin); // print to file
							System.out.println("\t" + pedigree + " is found");
						}
					} else {
						Germplasm g;
						if (error) { // if the precedent pedigree does not exist

							germplasmList = new ArrayList<Germplasm>();

							int count_LOCAL = countGermplasmByName(pedigree,
									Database.LOCAL);
							int count_CENTRAL = countGermplasmByName(pedigree,
									Database.CENTRAL);
							germplasmList = getGermplasmList(pedigree,
									count_LOCAL, count_CENTRAL); // gets lists
							// of
							// germplasm
							// with that
							// name
							g = getGermplasmByGpid(gpid1, germplasmList); // get
							// the
							// germplasm
							// of
							// the
							// same
							// gpid1,
							// for
							// derivative
							// line,
							// or
							// gid
							// equals
							// to
							// the
							// gpid1
						} else {
							g = manager.getGermplasmByGID(gpid2);
							System.out.println("gpid2=" + gpid2);
						}
						if (g == null) { // this is an ERROR, the gid should
							// exist
							error = true;

							temp_fin = printError_temp(pedigree, parent2, id,
									temp_fin); // prints ERROR to file

						} else {
							gpid2 = g.getGpid2();
							gpid1 = g.getGpid1();
							error = false; // set the flag to false

							temp_fin = printSuccess_temp(pedigree, parent2, id,
									g, "false", temp_fin); // print to file
						}
					}
					single_hit = true;
					flag = false;
					multiple_hit = false;
					result = true;
				}
			}
		}

		temp.clear();
		germplasm.clear();
		germplasm_fin.clear();
		JSONObject output = new JSONObject();
		System.out.println(parent + ": " + result);
		output.put("result", result);
		output.put("temp_fin", temp_fin);
		output.put("gid_parent", GID);
		return output;
	}

	public JSONObject isCross_existing_compareDates(String cross,
			String female_nval, String male_nval)
	throws MiddlewareQueryException, IOException,
	java.text.ParseException {
		List<Germplasm> germplasm = new ArrayList<Germplasm>();
		List<Germplasm> germplasm_fin = new ArrayList<Germplasm>();
		List<Germplasm> germplasm_list = new ArrayList<Germplasm>();
		List<Germplasm> germplasm_all = new ArrayList<Germplasm>();
		List<Germplasm> germplasm_filtered = new ArrayList<Germplasm>();

		JSONObject output = new JSONObject();

		int count_LOCAL = countGermplasmByName(cross, Database.LOCAL);
		int count_CENTRAL = countGermplasmByName(cross, Database.CENTRAL);

		germplasm = getGermplasmList(cross, count_LOCAL, count_CENTRAL);
		System.out.println("gsize: " + germplasm.size());

		for (int j = 0; j < germplasm.size(); j++) {
			if (germplasm.get(j).getLocationId().equals(locationID)
					&& germplasm.get(j).getGrplce() == 0) {
				germplasm_fin.add(germplasm.get(j));
			}
		}
		System.out.println("gfin size: " + germplasm.size());
		System.out.println("fgid: " + female_nval);
		System.out.println("mgid: " + male_nval);
		if (germplasm_fin.size() != 0) {
			// if(germplasm_fin.get(0).getGpid1()==fgid &&
			// germplasm_fin.get(0).getGpid2()==mgid){

			for (int i = 0; i < germplasm_fin.size(); i++) {
				List<Name> name_female = manager.getNamesByGID(germplasm_fin
						.get(i).getGpid1(), 0, null);
				List<Name> name_male = manager.getNamesByGID(
						germplasm_fin.get(i).getGpid2(), 0, null);
				if (name_female.size() > 0 && name_male.size() > 0) {
					if (name_female.get(0).getNval().equals(female_nval)
							&& name_male.get(0).getNval().equals(male_nval)) {
						germplasm_list.add(germplasm_fin.get(i));
					}
				}

			}
			germplasm_fin.clear();

			for (int i = 0; i < germplasm_list.size(); i++) {
				// compares thecross date in the list and the date of the
				// creation of the GIDs
				// // check the format of the date
				// if date is just the year
				// filter all he dates that is after year0101 or equals year0101
				// && before year1231
				//
				// else if date is in the format yyyyMMdd
				// return the germplasm that is exactly with that date
				System.out.println("list: cross_date: " + cross_date);
				if (!cross_date.equals("not specified")) {
					Integer date_int = germplasm_list.get(i).getGdate();
					String date_string = date_int.toString();
					// System.out.println("length of the date: "+date_string.length());
					String temp = date_string;
					String cross_date_l = cross_date;
					if (!date_string.equals(0) && !date_string.equals("0")) {
						if (cross_date.length() == 4) {
							cross_date_l = cross_date.concat("0101");
						} else if (cross_date.length() == 10) {
							if (cross_date.contains("/")) {
								cross_date_l = cross_date.replace("/", "");
							} else if (cross_date.contains("-")) {
								cross_date_l = cross_date.replace("-", "");
							}
						}

						if (temp.length() == 4) {
							temp = temp.concat("0101");
						} else if (temp.length() == 10) {
							if (temp.contains("/")) {
								temp = temp.replace("/", "");
							} else if (temp.contains("-")) {
								cross_date_l = temp.replace("-", "");
							}
						}
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
						Date date1 = sdf.parse(temp);
						Date date2 = sdf.parse(cross_date_l);
						// System.out.println("Date1: "+date1);
						// System.out.println("Date2: "+date2);

						/*
						 * if(cross_date.length()==4 ||
						 * date_string.length()==4){ if(cross_date.length()==4){
						 * 
						 * Date dateEnd = sdf.parse(cross_date+"1231");
						 * if((date2.after(date1) || date2.equals(date1)) &&
						 * (date1.before(dateEnd) || date1.equals(dateEnd))){
						 * 
						 * } }
						 * 
						 * if(date_string.length()==4){
						 * 
						 * Date dateEnd = sdf.parse(date_string+"1231");
						 * if((date2.after(date1) || date2.equals(date1)) &&
						 * (date1.before(dateEnd) || date1.equals(dateEnd))){
						 * 
						 * } } }else{
						 */
						if (date1.equals(date2)) {
							System.out.println("Date1 is equal Date2");
							germplasm_filtered.add(germplasm_list.get(i));

						}
						/*
						 * }
						 */
						germplasm_all.add(germplasm_list.get(i));
					} else { // date in db is 0
						germplasm_all.add(germplasm_list.get(i));
					}
				} else {
					germplasm_all.add(germplasm_list.get(i));
				}
			}
			System.out.println("ALL: " + germplasm_all);
			System.out.println("FILTERED: " + germplasm_filtered);
			output.put("all", germplasm_all);
			output.put("filtered", germplasm_filtered);
			return output;
		} else {

			output.put("all", germplasm_all);
			output.put("filtered", germplasm_filtered);
			return output;
		}
	}

	public List<List<String>> setLine_crossOp(String nval, String id, int gid,
			int gpid1, int gpid2, List<List<String>> createdGID)
			throws MiddlewareQueryException, IOException {
		String parent = nval;
		List<String> pedigreeList = new ArrayList<String>();
		List<List<String>> temp = new ArrayList<List<String>>();
		List<List<String>> parents = new ArrayList<List<String>>();

		List<String> row = new ArrayList<String>();
		new CrossOp();

		JSONObject result_method2 = CrossOp.method2(parent, pedigreeList);
		pedigreeList = (List<String>) result_method2.get("list");
		result_method2.clear();

		System.out.println("" + pedigreeList);
		// pedigreeList.remove(1);

		for (int j = 0; j < pedigreeList.size(); j++) {
			row = new ArrayList<String>();
			// System.out.println("::"+pedigreeList.get(j));
			if ((!pedigreeList.get(j).contains("/")
					&& !pedigreeList.get(j).contains("*")) || (!pedigreeList.get(j).contains("/")
							&& pedigreeList.get(j).contains("*"))) {

				row.add(pedigreeList.get(j));
				row.add("0");
				row.add("0");
				parents.add(row);
				// System.out.println("row_parents; "+row);

			}
		}
		System.out.println("PARENTS: " + parents);
		System.out.println("id: " + id);
		System.out.println("gid: " + gid);
		System.out.println("id: " + gpid1);
		System.out.println("id: " + gpid2);
		// System.out.println("last index of the crosses:"+notParent_index);

		System.out.println("------");
		// pedigreeList= parse_crossOp(pedigreeList, nval, id);

		List<Integer> check = new ArrayList<Integer>();

		for (int i = 0; i < pedigreeList.size(); i++) {

			String pedigree = pedigreeList.get(i);
			System.out.println("pedigree: " + pedigree);
			System.out.println("\t Single HIT");
			if (i == 0) {
				Germplasm g = manager.getGermplasmByGID(gid);
				System.out.println("\t " + pedigreeList.get(i)
						+ " is found with GID=" + g.getGid());
				createdGID_local = updateCreatedGID("" + gid, id, pedigree,
						"false", createdGID);
				System.out.println("createdGID: " + createdGID_local);

				check.add(gid);
				if (g.getGpid1() != 0 || g.getGpid2() != 0) {
					check.add(g.getGpid1());
					check.add(g.getGpid2());
				}
				g = null;
			} else {

				System.out.println("\t checked: " + check);
				for (int j = 0; j < check.size(); j++) {
					Germplasm g1 = manager.getGermplasmByGID(check.get(j));
					List<Name> name = new ArrayList<Name>();
					// System.out.println("check: "+check.get(j));
					// System.out.println("gid: "+g1.getGid());
					name = manager.getNamesByGID(g1.getGid(), 0, null);
					System.out.println("name: " + name.get(0).getNval());
					System.out.println("P: " + pedigreeList.get(i));
					if (name.get(0).getNval().equals(pedigreeList.get(i))) {

						System.out.println("\t " + pedigreeList.get(i)
								+ " is found with GID=" + g1.getGid());
						// temp=printSuccess_temp(pedigreeList.get(i), parent,
						// id, g1, "false",temp);
						createdGID_local = updateCreatedGID("" + g1.getGid(),
								id, pedigreeList.get(i), "false", createdGID);

						if ((!check.contains(g1.getGpid1()))) {
							System.out
							.println("\t gpid1 is not in the check list "
									+ g1.getGpid1());
							if (g1.getGpid1() != 0) {
								System.out.println("\t gpid1 is not 0");
								check.add(g1.getGpid1());
							}
						}

						if ((!check.contains(g1.getGpid2()))) {
							System.out
							.println("\t gpid2 is not in the check list "
									+ g1.getGpid2());
							if (g1.getGpid2() != 0) {
								System.out.println("\t gpid2 is not 0");
								check.add(g1.getGpid2());
							}
						}

						gpid1 = g1.getGpid1();
						gpid2 = g1.getGpid2();
						System.out.println("\t gpid1: " + gpid1);
						System.out.println("\t gpid2: " + gpid2);

						if (pedigreeList.get(i).contains("-")
								&& !pedigreeList.get(i).contains("/")
								&& pedigreeList.get(i).startsWith("IR")) {

							String[] tokens = new Tokenize()
							.tokenize(pedigreeList.get(i));
							ArrayList<String> pedigreeList_der = new ArrayList<String>();

							new AssignGid();
							pedigreeList_der = saveToArray(pedigreeList_der,
									tokens);
							for (int n = 1; n < pedigreeList_der.size(); n++) {
								// System.out.println("add:: "+pedigreeList_der.get(n));
								Germplasm germplasm2 = manager
								.getGermplasmByGID(gpid2);
								// temp=printSuccess_temp(pedigreeList_der.get(n),
								// parent, id, germplasm2, "false",temp);
								System.out.println("\t " + pedigreeList.get(i)
										+ " is found with GID="
										+ germplasm2.getGid());
								createdGID_local = updateCreatedGID(""
										+ germplasm2.getGid(), id,
										pedigreeList_der.get(n), "false",
										createdGID);

								gpid1 = germplasm2.getGpid1();
								gpid2 = germplasm2.getGpid2();
								/*
								 * if(i==pedigreeList_der.size()-2){
								 * if(gpid1!=gpid2){
								 * germplasm2=manager.getGermplasmByGID(gpid1);
								 * temp
								 * =printSuccess_temp(pedigreeList_der.get(n),
								 * parent, id, germplasm2, "false",temp); } }
								 */
							}
							pedigreeList_der.clear();
							tokens = null;
						}
					}

					name = null;
					g1 = null;
				}
			}
			// System.out.println("gpid1: "+gpid1);
			// System.out.println("gpid2: "+gpid2);
			System.out.println("createdGID: " + createdGID_local);
		}

		temp.clear();
		check.clear();
		pedigreeList.clear();

		return createdGID_local;
	}

	public void getLine_crossOp(String nval, String id, int gid, int gpid1,
			int gpid2) throws MiddlewareQueryException, IOException {
		System.out.println("................GetLine CrossOP.....");
		String parent = nval;
		List<String> pedigreeList = new ArrayList<String>();
		List<List<String>> temp = new ArrayList<List<String>>();
		List<List<String>> parents = new ArrayList<List<String>>();

		List<String> row = new ArrayList<String>();
		new CrossOp();
		JSONObject result_method2 = CrossOp.method2(parent, pedigreeList);
		pedigreeList = (List<String>) result_method2.get("list");
		result_method2.clear();
		// pedigreeList.remove(1);

		Boolean isParent = false;
		int notParent_index = -1;
		for (int j = 0; j < pedigreeList.size(); j++) {
			row = new ArrayList<String>();
			// System.out.println("::"+pedigreeList.get(j));
			if ((!pedigreeList.get(j).contains("/")
					&& !pedigreeList.get(j).contains("*")) || (!pedigreeList.get(j).contains("/")
							&& pedigreeList.get(j).contains("*"))) {
				if (!isParent) {
					notParent_index = j;
				}
				row.add(pedigreeList.get(j));
				row.add("0");
				row.add("0");
				parents.add(row);
				// System.out.println("row_parents; "+row);
				isParent = true;
			}
		}
		System.out.println("PARENTS: " + parents);
		System.out.println("last index of the crosses:" + notParent_index);

		System.out.println("------");

		List<Integer> check = new ArrayList<Integer>();

		for (int i = 0; i < pedigreeList.size(); i++) {

			String pedigree = pedigreeList.get(i);
			System.out.println("pedigree: " + pedigree);
			System.out.println("\t Single HIT");
			if (i == 0) {
				Germplasm g = manager.getGermplasmByGID(gid);
				temp = printSuccess_temp(pedigree, parent, id, g, "false", temp);
				check.add(gid);
				if (gpid1 != 0 || gpid2 != 0) {
					check.add(gpid1);
					check.add(gpid2);
				}
				g = null;
			} else {

				System.out.println("\t checked: " + check);
				for (int j = 0; j < check.size(); j++) {
					Germplasm g1 = manager.getGermplasmByGID(check.get(j));
					List<Name> name = new ArrayList<Name>();
					// System.out.println("check: "+check.get(j));
					// System.out.println("gid: "+g1.getGid());
					name = manager.getNamesByGID(g1.getGid(), 0, null);
					// System.out.println("name: "+name.get(0).getNval());
					// System.out.println("P: "+pedigreeList.get(i));
					if (name.get(0).getNval().equals(pedigreeList.get(i))) {

						System.out.println("\t " + pedigreeList.get(i)
								+ " is found with GID=" + g1.getGid());
						temp = printSuccess_temp(pedigreeList.get(i), parent,
								id, g1, "false", temp);
						if ((!check.contains(g1.getGpid1()))) {
							// System.out.println("\t gpid1 is not in the check list");
							if (g1.getGpid1() != 0) {
								System.out.println("\t gpid1 is not 0");
								check.add(g1.getGpid1());
							}
						}

						if ((!check.contains(g1.getGpid2()))) {
							// System.out.println("\t gpid2 is not in the check list");
							if (g1.getGpid2() != 0) {
								System.out.println("\t gpid2 is not 0");
								check.add(g1.getGpid2());
							}
						}

						gpid1 = g1.getGpid1();
						gpid2 = g1.getGpid2();
						// System.out.println("\t gpid1: "+gpid1);
						// System.out.println("\t gpid2: "+gpid2);

						if (pedigreeList.get(i).contains("-")
								&& !pedigreeList.get(i).contains("/")
								&& pedigreeList.get(i).startsWith("IR")) {

							String[] tokens = new Tokenize()
							.tokenize(pedigreeList.get(i));
							ArrayList<String> pedigreeList_der = new ArrayList<String>();

							new AssignGid();
							pedigreeList_der = saveToArray(pedigreeList_der,
									tokens);
							for (int n = 1; n < pedigreeList_der.size(); n++) {
								System.out.println("add:: "
										+ pedigreeList_der.get(n));
								Germplasm germplasm2 = manager
								.getGermplasmByGID(gpid2);
								temp = printSuccess_temp(
										pedigreeList_der.get(n), parent, id,
										germplasm2, "false", temp);
								gpid1 = germplasm2.getGpid1();
								gpid2 = germplasm2.getGpid2();
								/*
								 * if(i==pedigreeList_der.size()-2){
								 * if(gpid1!=gpid2){
								 * germplasm2=manager.getGermplasmByGID(gpid1);
								 * temp
								 * =printSuccess_temp(pedigreeList_der.get(n),
								 * parent, id, germplasm2, "false",temp); } }
								 */
							}
							pedigreeList_der.clear();
							tokens = null;
						}
					}
					name = null;
					g1 = null;
				}
			}
		}
		System.out.println("CreatedGID @ getLine_crossOp: " + createdGID_local);
		for (int i = 0; i < temp.size(); i++) {
			System.out.println("" + temp.get(i));
			createdGID_local.add(temp.get(i));
		}
		temp.clear();
		check.clear();
		pedigreeList.clear();

	}

	private Boolean processParents(String female_nval, String female_id,
			String male_nval, String male_id, String cross,
			List<List<String>> list) throws MiddlewareQueryException,
			IOException, InterruptedException, java.text.ParseException {

		boolean female = false, male = false;
		int fgid = 0, mgid = 0;
		System.out.println("Processing parents..........");

		JSONObject output = isCross_existing_compareDates(cross, female_nval,
				male_nval);
		List<Germplasm> germplasm_filtered = (List<Germplasm>) output
		.get("filtered");
		List<Germplasm> germplasm_all = (List<Germplasm>) output.get("all");
		if (germplasm_filtered.size() == 1) { // there exists a cross name that
			// is equals with the female and
			// male names and equals with
			// given date
			System.out
			.println("there exists a cross name that is equals with the female and male names and equals with given date");
			// get the female gid and male gid
			// set the line for the female using that gid, same goes to the male
			// line

			// for the female parent
			int gid = germplasm_filtered.get(0).getGid(), gpid1 = germplasm_filtered
			.get(0).getGpid1(), gpid2 = germplasm_filtered.get(0)
			.getGpid2();
			int gid_local = gid, gpid1_local = gpid1, gpid2_local = gpid2;
			String nval, nval2;
			String id, id2;
			// System.out.println("CreatedGID before : "+createdGID_local);

			for (int i = 0; i < 2; i++) {
				if (i == 0) {
					nval = female_nval;
					id = female_id;
					nval2 = male_nval;
					id2 = male_id;
					gid = germplasm_filtered.get(0).getGpid1();
					Germplasm g = manager.getGermplasmByGID(gid);
					gpid1 = g.getGpid1();
					gpid2 = g.getGpid2();
					g = null;
					gid_local = gid;
					gpid1_local = gpid1;
					gpid2_local = gpid2;

				} else {
					nval = male_nval;
					id = male_id;
					nval2 = female_nval;
					id2 = female_id;
					gid = germplasm_filtered.get(0).getGpid2();
					Germplasm g = manager.getGermplasmByGID(gid);
					gpid1 = g.getGpid1();
					gpid2 = g.getGpid2();
					g = null;
					gid_local = gid;
					gpid1_local = gpid1;
					gpid2_local = gpid2;
				}
				if (nval.contains("/") || nval.contains("*")) {
					// The Parent has cross operators
					System.out.println("The parent has cross operators");
					// System.out.println("parent q");

					getLine_crossOp(nval, id, gid, gpid1, gpid2);
					// chooseGID_crossOP(parent1ID, parent1, parent2ID, parent2,
					// theParent,lastDeriv_parent,gid_local, gpid1_local,
					// gpid2_local,gid, gpid1, gpid2,createdGID);
					System.out.println("\n ***** END******* \n ");
				} else {
					Pattern p = Pattern.compile("IR");
					Matcher m1 = p.matcher(nval);
					String[] tokens = { "" };
					if (m1.lookingAt()) {
						tokens = new Tokenize().tokenize(nval);
					} else {
						tokens[0] = nval;
					}
					// String[] tokens = new Tokenize().tokenize(nval);
					ArrayList<String> pedigreeList = new ArrayList<String>();
					pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0]
					// is
					// the
					// most
					// recent
					// pedigree,
					// pedigreeList[size]
					// is
					// the
					// root

					getDerivativeLine_cross(pedigreeList, "" + gid, gpid1,
							gpid2, id, nval);
					// createdGID_local=getDerivativeLine( -1, pedigreeList,
					// gid_local, gpid1_local, gpid2_local, ""+gid, gpid1,
					// gpid2, id2, nval2, createdGID_local);
				}
			}
			System.out.println("cross " + cross + " already exists");
			printSuccess(cross, female_nval + "/" + male_nval, female_id + "/"
					+ male_id, germplasm_filtered.get(0), "old");

			// list_local=update_list(germplasm_filtered.get(0), female_id ,
			// cross);
			// germplasm=null;
			return true;
		} else {
			if (germplasm_filtered.size() > 1) { // there are multiple matches
				// with the cross name with
				// the same date
				System.out
				.println("there are multiple matches with the cross name with the same date");
				// should print choose GID sa cross
				// should print NOT set sa male at female
				if ((female_nval.contains("/") && female_nval.contains("*")) || (female_nval.contains("/") && !female_nval.contains("*"))) {
					printNotSet_parents_CrossOp(female_nval, female_id);
				} else {
					Pattern p = Pattern.compile("IR");
					Matcher m1 = p.matcher(female_nval);
					String[] tokens = { "" };
					if (m1.lookingAt()) {
						tokens = new Tokenize().tokenize(female_nval);
					} else {
						tokens[0] = female_nval;
					}
					// String[] tokens = new Tokenize().tokenize(female_nval);
					ArrayList<String> pedigreeList = new ArrayList<String>();
					pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0]
					// is
					// the
					// most
					// recent
					// pedigree,
					// pedigreeList[size]
					// is
					// the
					// root

					// Collections.reverse(pedigreeList); // index 0 is the root
					System.out.println("pedigreeList der: " + pedigreeList);
					for (int j = 0; j < pedigreeList.size(); j++) {
						printNotSet(pedigreeList.get(j), female_nval, female_id);
					}
					pedigreeList.clear();
				}

				if (male_nval.contains("-") || male_nval.contains("*")) {
					printNotSet_parents_CrossOp(male_nval, male_id);
				} else {
					Pattern p = Pattern.compile("IR");
					Matcher m1 = p.matcher(male_nval);
					String[] tokens = { "" };
					if (m1.lookingAt()) {
						tokens = new Tokenize().tokenize(male_nval);
					} else {
						tokens[0] = male_nval;
					}
					// String[] tokens = new Tokenize().tokenize(male_nval);
					ArrayList<String> pedigreeList = new ArrayList<String>();
					pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0]
					// is
					// the
					// most
					// recent
					// pedigree,
					// pedigreeList[size]
					// is
					// the
					// root

					// Collections.reverse(pedigreeList); // index 0 is the root
					System.out.println("pedigreeList der: " + pedigreeList);
					for (int j = 0; j < pedigreeList.size(); j++) {
						printNotSet(pedigreeList.get(j), male_nval, male_id);
					}
					pedigreeList.clear();
				}

				System.out.println("choose GID");
				printChooseGID(cross, female_nval + "/" + male_nval, female_id
						+ "/" + male_id);
				System.out.println("end choose GID");

				List<String> row = new ArrayList<String>();
				for (int i = 0; i < germplasm_filtered.size(); i++) {
					row = new ArrayList<String>();
					row.add(female_id + "/" + male_id);
					row.add(cross);

					row.add("" + germplasm_filtered.get(i).getGpid1());
					row.add(female_nval);

					row.add("" + germplasm_filtered.get(i).getGpid2());
					row.add(male_nval);

					row.add("" + germplasm_filtered.get(i).getGid()); // gid

					Location location = manager
					.getLocationByID(germplasm_filtered.get(i)
							.getLocationId());
					Method method = manager.getMethodByID(germplasm_filtered
							.get(i).getMethodId());

					String meth = method.getMname().replace(",", "#");
					row.add("" + germplasm_filtered.get(i).getMethodId());
					row.add(meth); // method
					String loc = location.getLname().replace(",", "#");
					row.add("" + germplasm_filtered.get(i).getLocationId());
					row.add(loc); // location
					row.add(cross); // pedigree name

					String date = germplasm_filtered.get(i).getGdate()
					.toString();

					String yr, day, mo;
					// System.out.println("date: "+date);
					if (date.equals("0")) {
						yr = "0000";
						day = "00";
						mo = "00";
						// row.add(yr.concat(day).concat(mo)); //date of
						// creation
						row.add("0"); // date of creation
					} else if ((date.charAt(3) == '0' && date.charAt(4) == '0')
							|| (date.charAt(6) == '0' && date.charAt(7) == '0')) {
						if((date.charAt(6) == '0' && date.charAt(7) == '0') && !(date.charAt(3) == '0' && date.charAt(4) == '0')){

							yr = date.charAt(0) + "" + date.charAt(1) + ""
							+ date.charAt(2) + "" + date.charAt(3)+"-"
							+ date.charAt(4) + "" + date.charAt(5);
							row.add(yr.concat("-01")); // date of
							// creation
						}else if(!(date.charAt(6) == '0' && date.charAt(7) == '0') && (date.charAt(3) == '0' && date.charAt(4) == '0')){
							yr = date.charAt(0) + "" + date.charAt(1) + ""
							+ date.charAt(2) + "" + date.charAt(3)+"-01-"
							+ date.charAt(6) + "" + date.charAt(7);
							row.add(yr); // date of creation

						}else{//both true
							yr = date.charAt(0) + "" + date.charAt(1) + ""
							+ date.charAt(2) + "" + date.charAt(3)+"-01-01";
							row.add(yr);
						}
					} else {
						// System.out.println(date.charAt(0));
						yr = date.charAt(0) + "" + date.charAt(1) + ""
						+ date.charAt(2) + "" + date.charAt(3) + "-";
						mo = date.charAt(4) + "" + date.charAt(5) + "-";
						day = date.charAt(6) + "" + date.charAt(7) + "";
						row.add(yr.concat(mo).concat(day)); // date of creation
					}

					// System.out.println("date: "+yr.concat(day).concat(mo));
					row.add("0"); // date of creation
					row.add(cross_date); // date of creation

					// clearing memory
					location = null;
					method = null;

					existingTerm_local.add(row);

				}
				return false;
			} else { // no date match
				System.out.println("No date Match");
				System.out.println("germplasm_all: " + germplasm_all);
				if (germplasm_all.size() > 0) { // if there are matches same
					// name, same female at male but
					// not the date
					System.out
					.println("if there are matches same name, same female at male but not the date");
					// print choose GID or create new
					if ((female_nval.contains("/") && female_nval.contains("*")) || (female_nval.contains("/") && !female_nval.contains("*"))) {
						printNotSet_parents_CrossOp(female_nval, female_id);
					} else {
						Pattern p = Pattern.compile("IR");
						Matcher m1 = p.matcher(female_nval);
						String[] tokens = { "" };
						if (m1.lookingAt()) {
							tokens = new Tokenize().tokenize(female_nval);
						} else {
							tokens[0] = female_nval;
						}
						// String[] tokens = new
						// Tokenize().tokenize(female_nval);
						ArrayList<String> pedigreeList = new ArrayList<String>();
						pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0]
						// is
						// the
						// most
						// recent
						// pedigree,
						// pedigreeList[size]
						// is
						// the
						// root

						// Collections.reverse(pedigreeList); // index 0 is the
						// root

						for (int j = 0; j < pedigreeList.size(); j++) {
							printNotSet(pedigreeList.get(j), female_nval,
									female_id);
						}
						pedigreeList.clear();
					}

					if (male_nval.contains("/") || male_nval.contains("*")) {
						printNotSet_parents_CrossOp(male_nval, male_id);
					} else {
						Pattern p = Pattern.compile("IR");
						Matcher m1 = p.matcher(male_nval);
						String[] tokens = { "" };
						if (m1.lookingAt()) {
							tokens = new Tokenize().tokenize(male_nval);
						} else {
							tokens[0] = male_nval;
						} // String[] tokens = new
						// Tokenize().tokenize(male_nval);
						ArrayList<String> pedigreeList = new ArrayList<String>();
						pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0]
						// is
						// the
						// most
						// recent
						// pedigree,
						// pedigreeList[size]
						// is
						// the
						// root

						// Collections.reverse(pedigreeList); // index 0 is the
						// root

						for (int j = 0; j < pedigreeList.size(); j++) {
							printNotSet(pedigreeList.get(j), male_nval, male_id);
						}
						pedigreeList.clear();
					}

					printChooseGID(cross, female_nval + "/" + male_nval,
							female_id + "/" + male_id);
					List<String> row = new ArrayList<String>();
					for (int i = 0; i < germplasm_all.size(); i++) {
						row = new ArrayList<String>();
						row.add(female_id + "/" + male_id);
						row.add(cross);

						row.add("" + germplasm_all.get(i).getGpid1());
						row.add(female_nval);

						row.add("" + germplasm_all.get(i).getGpid2());
						row.add(male_nval);

						row.add("" + germplasm_all.get(i).getGid()); // gid

						Location location = manager
						.getLocationByID(germplasm_all.get(i)
								.getLocationId());
						Method method = manager.getMethodByID(germplasm_all
								.get(i).getMethodId());

						String meth = method.getMname().replace(",", "#");
						row.add("" + germplasm_all.get(i).getMethodId());
						row.add(meth); // method
						String loc = location.getLname().replace(",", "#");
						row.add("" + germplasm_all.get(i).getLocationId());
						row.add(loc); // location
						row.add(cross); // pedigree name

						String date = germplasm_all.get(i).getGdate()
						.toString();

						String yr, day, mo;
						System.out.println("date: " + date);
						if (date.equals("0")) {
							yr = "0000";
							day = "00";
							mo = "00";
							// row.add(yr.concat(day).concat(mo)); //date of
							// creation
							row.add("0"); // date of creation
						} else if ((date.charAt(3) == '0' && date.charAt(4) == '0')
								|| (date.charAt(6) == '0' && date.charAt(7) == '0')) {

							if(date.charAt(6) == '0' && date.charAt(7) == '0'){

								yr = date.charAt(0) + "" + date.charAt(1) + ""
								+ date.charAt(2) + "" + date.charAt(3)+"-"
								+ date.charAt(4) + "" + date.charAt(5);
								row.add(yr.concat("-01")); // date of
								// creation
							}else{
								yr = date.charAt(0) + "" + date.charAt(1) + ""
								+ date.charAt(2) + "" + date.charAt(3)+"-01-"
								+ date.charAt(6) + "" + date.charAt(7);
								row.add(yr); // date of creation
							}

						} else {
							// System.out.println(date.charAt(0));
							yr = date.charAt(0) + "" + date.charAt(1) + ""
							+ date.charAt(2) + "" + date.charAt(3)
							+ "-";
							mo = date.charAt(4) + "" + date.charAt(5) + "-";
							day = date.charAt(6) + "" + date.charAt(7) + "";
							row.add(yr.concat(mo).concat(day)); // date of
							// creation
						}
						row.add(cross_date); // date of creation

						// clearing memory
						location = null;
						method = null;

						existingTerm_local.add(row);

					}
					return false;

				} else { // no existing same cross name and same female at male
					System.out
					.println("no existing same cross name and same female at male");
					System.out.println("female has cross operators");
					System.out.println("create the cross");
					if ((female_nval.contains("/") && female_nval.contains("*")) || (female_nval.contains("/") && !female_nval.contains("*"))) {

						female = checkParent_crossOp(female_nval, female_id);

					} else {
						female = checkParent(female_nval, female_id);
					}
					System.out.println("END checking female.....");
					System.out.println("female: " + female);
					fgid = GID;
					// //System.out.println("\nmale:");
					System.out.println("createdGID: " + createdGID_local);
					System.out.println("checking male.....");
					if ((male_nval.contains("/") && male_nval.contains("*")) || (male_nval.contains("/") && !male_nval.contains("*"))) {
						male = checkParent_crossOp(male_nval, male_id);
					} else {

						male = checkParent(male_nval, male_id);

					}
					System.out.println("END checking male.....");
					System.out.println("male: " + male);
					mgid = GID;
					if (male && female) {
						System.out.println("createdGID for cross " + cross);

						Germplasm g1 = manager.getGermplasmByGID(fgid);
						List<Name> name = manager.getNamesByGID(g1.getGpid1(),
								0, null);
						List<Name> name1 = manager.getNamesByGID(g1.getGpid2(),
								0, null);
						int methodID = 0;
						if (name.size() > 0 && name1.size() > 0) {
							if (name.get(0).getNval().equals(male)
									|| name1.get(0).getNval().equals(male)) {
								methodID = 107;
							}
							name.clear();
							name1.clear();
							g1 = null;
						}
						methodID = selectMethodType(fgid, mgid, female_nval,
								male_nval, cross, methodID);

						int cross_gid = (int) addGID(cross, fgid, mgid,
								methodID, 2, true); // ntype=2

						Germplasm germplasm1 = manager
						.getGermplasmByGID(cross_gid);

						printSuccess(cross, female_nval + "/" + male_nval,
								female_id + "/" + male_id, germplasm1, "new");

						list_local=update_list(germplasm1, female_id, cross);
						// //System.out.println("\t id: "+fid + "/" + mid);
						// //System.out.println("\t id: "+ cross);
						germplasm1 = null;
						// germplasm=null;

					} else {
						System.out.println("NOT SET FOR cross " + cross);
						printNotSet(cross, female_nval + "/" + male_nval,
								female_id + "/" + male_id);
					}
					if (male && female) {
						return true;
					} else {
						return false;
					}
				}
			}
		}

		// ////System.out.println("list_local: "+list_local);
		// //System.out.println(" ###END..Processing of Parents \n");

	}

	public Boolean checkParent(String parent, String id)
	throws MiddlewareQueryException, IOException {
		Pattern p = Pattern.compile("IR");
		Matcher m1 = p.matcher(parent);
		String[] tokens = { "" };
		if (m1.lookingAt()) {
			tokens = new Tokenize().tokenize(parent);
		} else {
			tokens[0] = parent;
		}
		// String[] tokens = new Tokenize().tokenize(parent);
		ArrayList<String> pedigreeList = new ArrayList<String>();

		pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0] is
		// the most recent
		// pedigree,
		// pedigreeList[size]
		// is the root

		Boolean flag = true;
		Boolean single_hit = false;
		Boolean multiple_hit = false;
		Boolean error = false;
		Boolean result = false;

		List<Germplasm> germplasm_fin = new ArrayList<Germplasm>();
		List<Germplasm> germplasm = new ArrayList<Germplasm>();
		List<Germplasm> germplasmList = null;

		List<List<String>> temp = new ArrayList<List<String>>();
		int index = 0;
		int gpid2 = 0;
		int gpid1 = 0;
		int gid = 0;
		int gpid2_index = 0;

		// index 0 is the parent, index pedigreeList.size()-1 is the root
		for (int i = 0; i < pedigreeList.size(); i++) {
			String pedigree = pedigreeList.get(i);

			if (flag) {
				int count_LOCAL = countGermplasmByName(pedigree, Database.LOCAL);
				int count_CENTRAL = countGermplasmByName(pedigree,
						Database.CENTRAL);

				germplasm = getGermplasmList(pedigree, count_LOCAL,
						count_CENTRAL);
				System.out.println("count in db: " + germplasm.size());
				for (int j = 0; j < germplasm.size(); j++) {
					// System.out.println("\t ["+j+"]: "+germplasm.get(j).getLocationId());
					if (germplasm.get(j).getLocationId().equals(locationID)
							&& germplasm.get(j).getGrplce() == 0) {
						germplasm_fin.add(germplasm.get(j));
					}
				}
				int count = germplasm_fin.size();

				if (count == -1) { // only one germplasm with that name in the
					// location
					System.out.print("count==1");
					System.out.println("\t " + pedigree);

					if (i == 0) { // if it is the parent
						printSuccess(pedigree, parent, id,
								germplasm_fin.get(0), "false");
						GID = germplasm_fin.get(0).getGid();
						gpid2 = germplasm_fin.get(0).getGpid2();
						gpid1 = germplasm_fin.get(0).getGpid1();
						gid = germplasm_fin.get(0).getGid();
					} else if (i == pedigreeList.size() - 1) { // if root
						// germplasm_fin.get(0)
						List<Germplasm> list = new ArrayList<Germplasm>();
						gid = germplasm_fin.get(0).getGid();
						index = i;

						System.out.println("create GID from "
								+ pedigreeList.get(i - 1));
						for (int k = i - 1; k >= 0; k--) {
							System.out.println("\t create GID: "
									+ pedigreeList.get(k));
							if (k == i - 1) {
								gpid1 = gid;
							}
							int methodID = selectMethodType_DER(
									pedigreeList.get(i), parent);

							int gid_single_hit = (int) addGID(
									pedigreeList.get(k), gpid1, gid, methodID,
									5, false); // ntype=5
							if (k == 0) {
								GID = gid_single_hit;
							}
							gid = gid_single_hit;

							Germplasm g = manager
							.getGermplasmByGID(gid_single_hit);

							list.add(g);

						}
						int l = 0;
						for (int m = list.size() - 1; m >= 0; m--) {
							printSuccess(pedigreeList.get(l), parent, id,
									list.get(m), "new");
							l++;
						}
						list.clear();

						printSuccess(pedigreeList.get(i), parent, id,
								germplasm_fin.get(0), "false");
					} else { // not root and not the parent
						System.out.print("i: " + i);
						System.out.println("\t|  " + pedigree);
						temp = printSuccess_temp(pedigree, parent, id,
								germplasm_fin.get(0), "false", temp);
						gpid1 = germplasm_fin.get(0).getGpid1();
						gpid2_index = germplasm_fin.get(0).getGpid2();
						gid = germplasm_fin.get(0).getGid();
					}

					flag = false;
					single_hit = true;
					multiple_hit = false;
					index = i;
					result = true;

					// System.out.println("TEMP: "+temp);

				} else if (count > 0) { // multiple germplasm name in a location
					System.out.print("count>1");
					System.out.println("\t " + pedigree);

					multipleHits_inLocation(germplasm_fin, pedigree, id, parent);
					if (i == 0) // if it is the parent
						printChooseGID(pedigree, parent, id);
					else if (i == pedigreeList.size() - 1) { // if it is the
						// root
						for (int k = 0; k < temp.size(); k++) {
							createdGID_local.add(temp.get(k));
						}
						printChooseGID(pedigree, parent, id);
					} else { // if not the root and not the parent
						temp = printChooseGID_temp(pedigree, parent, id, temp);
					}
					multiple_hit = true;
					flag = false;
					index = i;
					result = false;

				} else {
					System.out.print("count==0");
					System.out.println("\t " + pedigree);

					// no germplasm name in the list's location
					if (i == pedigreeList.size() - 1) { // if root assign GID
						// from the root
						System.out.println("\t " + pedigree + "is the root");
						/*
						 * int fid=-1;
						 * 
						 * if(Integer.valueOf(id)%2==0){
						 * fid=Integer.valueOf(id); }else{
						 * fid=Integer.valueOf(id)-1; }
						 * System.out.println("\t fid: "+fid);
						 * 
						 * Boolean isExisting=true; for(int j=0;
						 * j<list_local.size();j++){
						 * 
						 * System.out.println("\t 0: "+list_local.get(j).get(2));
						 * if(Integer.valueOf(list_local.get(j).get(2))<fid){
						 * System
						 * .out.println("\t here @ "+list_local.get(j).get(0));
						 * if(list_local.get(j).get(1).equals(pedigree) &&
						 * !list_local.get(j).get(0).equals("N/A")){
						 * System.out.println
						 * ("\t here @ "+list_local.get(j).get(1)+" "+pedigree);
						 * isExisting=true; }
						 * 
						 * } } if(isExisting){ printNotSet(pedigree, parent,
						 * id); result=false; }else{
						 */
						createPedigreeLine2(pedigreeList, id, parent);
						result = true;
						// }
						// checks if it is a parent in earlier entries
						// if yes print NOT SET
						// if no create pedigreeLine
					} else { // else, not root, print NOT SET

						temp = printNotSet_temp(pedigree, parent, id, temp);
						single_hit = false;
						multiple_hit = false;
						flag = true;
					}
				}

			} else {
				if (multiple_hit) {
					System.out.println("\t multiple hit is true");
					if (i - 1 == index && (i - 1) != 0) { // if the previous is
						// the 'index' and
						// not the parent

						for (int k = 0; k < temp.size(); k++) {
							createdGID_local.add(temp.get(k));
						}
						printNotSet(pedigree, parent, id);
					} else {
						System.out.println("\t" + pedigree + "is NOT SET");
						printNotSet(pedigree, parent, id);
					}
					single_hit = false;
					flag = false;
					multiple_hit = true;
					result = false;
				} else if (single_hit) {
					System.out.println("\t Single hit is true");
					if (i - 1 == index && (i - 1) != 0) { // if the previous is
						// not the parent
						// and is the
						// 'index'
						// create GID for the index onwards

						List<Germplasm> list = new ArrayList<Germplasm>();
						// gid=germplasm_fin.get(0).getGid();

						System.out.println("create GID from "
								+ pedigreeList.get(i - 2));
						for (int k = i - 2; k >= 0; k--) {
							System.out.println("\t create GID: "
									+ pedigreeList.get(k));

							int methodID = selectMethodType_DER(
									pedigreeList.get(i), parent);

							gpid2 = gid;

							System.out.println("gpid1: " + gpid1);
							System.out.println("gpid2: " + gpid2);

							int gid_single_hit = (int) addGID(
									pedigreeList.get(k), gpid1, gpid2,
									methodID, 5, false); // ntype=5
							if (k == 0) {
								GID = gid_single_hit;
							}
							gid = gid_single_hit;

							Germplasm g = manager
							.getGermplasmByGID(gid_single_hit);

							// printSuccess(pedigreeList.get(k), parent, id, g,
							// "false");
							list.add(g);

						}
						int l = 0;
						for (int m = list.size() - 1; m >= 0; m--) {
							printSuccess(pedigreeList.get(l), parent, id,
									list.get(m), "new");
							l++;
						}
						list.clear();

						// printSuccess(pedigreeList.get(i-1), parent, id,
						// germplasm_fin.get(0), "false");
						createdGID_local.add(temp.get(index));

						Germplasm g = manager.getGermplasmByGID(gpid2_index);

						if (g == null) { // this is an ERROR, the gid should
							// exist
							error = true;

							printError(pedigree, parent, id); // prints ERROR to
							// file

						} else {
							gpid2 = g.getGpid2();
							gpid1 = g.getGpid1();
							error = false; // set the flag to false

							printSuccess(pedigree, parent, id, g, "false"); // print
							// to
							// file
							System.out.println("\t" + pedigree + " is found");
						}
					} else {
						Germplasm g;
						if (error) { // if the precedent pedigree does not exist

							germplasmList = new ArrayList<Germplasm>();

							int count_LOCAL = countGermplasmByName(pedigree,
									Database.LOCAL);
							int count_CENTRAL = countGermplasmByName(pedigree,
									Database.CENTRAL);
							germplasmList = getGermplasmList(pedigree,
									count_LOCAL, count_CENTRAL); // gets lists
							// of
							// germplasm
							// with that
							// name
							g = getGermplasmByGpid(gpid1, germplasmList); // get
							// the
							// germplasm
							// of
							// the
							// same
							// gpid1,
							// for
							// derivative
							// line,
							// or
							// gid
							// equals
							// to
							// the
							// gpid1
						} else {
							g = manager.getGermplasmByGID(gpid2);
						}
						if (g == null) { // this is an ERROR, the gid should
							// exist
							error = true;

							printError(pedigree, parent, id); // prints ERROR to
							// file

						} else {
							gpid2 = g.getGpid2();
							gpid1 = g.getGpid1();
							error = false; // set the flag to false

							printSuccess(pedigree, parent, id, g, "false"); // print
							// to
							// file
						}
					}
					single_hit = true;
					flag = false;
					multiple_hit = false;
					result = true;
				}
			}
		}

		temp.clear();
		germplasm.clear();
		germplasm_fin.clear();
		return result;
	}

	public Boolean checkParent_updateCreatedGID(String parent, String id)
	throws MiddlewareQueryException, IOException {
		Pattern p = Pattern.compile("IR");
		Matcher m1 = p.matcher(parent);
		String[] tokens = { "" };
		if (m1.lookingAt()) {
			tokens = new Tokenize().tokenize(parent);
		} else {
			tokens[0] = parent;
		}
		// String[] tokens = new Tokenize().tokenize(parent);
		ArrayList<String> pedigreeList = new ArrayList<String>();

		pedigreeList = saveToArray(pedigreeList, tokens); // pedigreeList[0] is
		// the most recent
		// pedigree,
		// pedigreeList[size]
		// is the root

		Boolean flag = true;
		Boolean single_hit = false;
		Boolean multiple_hit = false;
		Boolean error = false;
		Boolean result = false;

		List<Germplasm> germplasm_fin = new ArrayList<Germplasm>();
		List<Germplasm> germplasm = new ArrayList<Germplasm>();
		List<Germplasm> germplasmList = null;

		List<List<String>> temp = new ArrayList<List<String>>();
		int index = 0;
		int gpid2 = 0;
		int gpid1 = 0;
		int gid = 0;
		int gpid2_index = 0;

		// index 0 is the parent, index pedigreeList.size()-1 is the root
		for (int i = 0; i < pedigreeList.size(); i++) {
			String pedigree = pedigreeList.get(i);

			if (flag) {
				int count_LOCAL = countGermplasmByName(pedigree, Database.LOCAL);
				int count_CENTRAL = countGermplasmByName(pedigree,
						Database.CENTRAL);

				germplasm = getGermplasmList(pedigree, count_LOCAL,
						count_CENTRAL);
				System.out.println("count in db: " + germplasm.size());
				for (int j = 0; j < germplasm.size(); j++) {
					// System.out.println("\t ["+j+"]: "+germplasm.get(j).getLocationId());
					if (germplasm.get(j).getLocationId().equals(locationID)
							&& germplasm.get(j).getGrplce() == 0) {
						germplasm_fin.add(germplasm.get(j));
					}
				}
				int count = germplasm_fin.size();

				if (count == -1) { // only one germplasm with that name in the
					// location
					System.out.print("count==1");
					System.out.println("\t " + pedigree);

					if (i == 0) { // if it is the parent
						// printSuccess(pedigree, parent, id,
						// germplasm_fin.get(0), "false");
						updateCreatedGID("" + germplasm_fin.get(0).getGid(),
								id, pedigree, "false", createdGID_local);
						GID = germplasm_fin.get(0).getGid();
						gpid2 = germplasm_fin.get(0).getGpid2();
						gpid1 = germplasm_fin.get(0).getGpid1();
						gid = germplasm_fin.get(0).getGid();
					} else if (i == pedigreeList.size() - 1) { // if root
						// germplasm_fin.get(0)
						List<Germplasm> list = new ArrayList<Germplasm>();
						gid = germplasm_fin.get(0).getGid();
						index = i;

						System.out.println("create GID from "
								+ pedigreeList.get(i - 1));
						for (int k = i - 1; k >= 0; k--) {
							System.out.println("\t create GID: "
									+ pedigreeList.get(k));
							if (k == i - 1) {
								gpid1 = gid;
							}
							int methodID = selectMethodType_DER(
									pedigreeList.get(i), parent);

							int gid_single_hit = (int) addGID(
									pedigreeList.get(k), gpid1, gid, methodID,
									5, false); // ntype=5
							if (k == 0) {
								GID = gid_single_hit;
							}
							gid = gid_single_hit;

							Germplasm g = manager
							.getGermplasmByGID(gid_single_hit);

							list.add(g);

						}
						int l = 0;
						for (int m = list.size() - 1; m >= 0; m--) {
							// printSuccess(pedigreeList.get(l), parent, id,
							// list.get(m), "new");
							updateCreatedGID("" + list.get(m).getGid(), id,
									pedigreeList.get(l), "new",
									createdGID_local);
							l++;
						}
						list.clear();

						// printSuccess(pedigreeList.get(i), parent, id,
						// germplasm_fin.get(0), "false");
						updateCreatedGID("" + germplasm_fin.get(0).getGid(),
								id, pedigreeList.get(i), "false",
								createdGID_local);
					} else { // not root and not the parent
						System.out.print("i: " + i);
						System.out.println("\t|  " + pedigree);
						// temp=printSuccess_temp(pedigree, parent, id,
						// germplasm_fin.get(0), "false", temp);
						updateCreatedGID("" + germplasm_fin.get(0).getGid(),
								id, pedigree, "false", createdGID_local);
						gpid1 = germplasm_fin.get(0).getGpid1();
						gpid2_index = germplasm_fin.get(0).getGpid2();
						gid = germplasm_fin.get(0).getGid();
					}

					flag = false;
					single_hit = true;
					multiple_hit = false;
					index = i;
					result = true;

					// System.out.println("TEMP: "+temp);

				} else if (count > 0) { // multiple germplasm name in a location
					System.out.print("count>1");
					System.out.println("\t " + pedigree);

					multipleHits_inLocation(germplasm_fin, pedigree, id, parent);
					if (i == 0) // if it is the parent
						// printChooseGID(pedigree, parent, id);
						updateCreatedGID("CHOOSE GID", id, pedigree, "false",
								createdGID_local);
					else if (i == pedigreeList.size() - 1) { // if it is the
						// root
						/*
						 * for(int k=0;k<temp.size();k++){
						 * createdGID_local.add(temp.get(k)); }
						 */
						// printChooseGID(pedigree, parent, id);
						updateCreatedGID("CHOOSE GID", id, pedigree, "false",
								createdGID_local);
					} else { // if not the root and not the parent
						// temp=printChooseGID_temp(pedigree, parent, id, temp);
						updateCreatedGID("CHOOSE GID", id, pedigree, "false",
								createdGID_local);
					}
					multiple_hit = true;
					flag = false;
					index = i;
					result = false;

				} else {
					System.out.print("count==0");
					System.out.println("\t " + pedigree);

					// no germplasm name in the list's location
					if (i == pedigreeList.size() - 1) { // if root assign GID
						// from the root
						createPedigreeLine2(pedigreeList, id, parent);
						result = true;
					} else { // else, not root, print NOT SET

						// temp=printNotSet_temp(pedigree, parent, id, temp);
						single_hit = false;
						multiple_hit = false;
						flag = true;
					}
				}

			} else {
				if (multiple_hit) {
					System.out.println("\t multiple hit is true");
					if (i - 1 == index && (i - 1) != 0) { // if the previous is
						// the 'index' and
						// not the parent

						/*
						 * for(int k=0;k<temp.size();k++){
						 * createdGID_local.add(temp.get(k)); }
						 * printNotSet(pedigree, parent, id);
						 */
					} else {
						/*
						 * System.out.println("\t"+pedigree+"is NOT SET");
						 * printNotSet(pedigree, parent, id);
						 */
					}
					single_hit = false;
					flag = false;
					multiple_hit = true;
					result = false;
				} else if (single_hit) {
					System.out.println("\t Single hit is true");
					if (i - 1 == index && (i - 1) != 0) { // if the previous is
						// not the parent
						// and is the
						// 'index'
						// create GID for the index onwards

						List<Germplasm> list = new ArrayList<Germplasm>();
						// gid=germplasm_fin.get(0).getGid();

						System.out.println("create GID from "
								+ pedigreeList.get(i - 2));
						for (int k = i - 2; k >= 0; k--) {
							System.out.println("\t create GID: "
									+ pedigreeList.get(k));

							int methodID = selectMethodType_DER(
									pedigreeList.get(i), parent);

							gpid2 = gid;

							System.out.println("gpid1: " + gpid1);
							System.out.println("gpid2: " + gpid2);

							int gid_single_hit = (int) addGID(
									pedigreeList.get(k), gpid1, gpid2,
									methodID, 5, false); // ntype=5
							if (k == 0) {
								GID = gid_single_hit;
							}
							gid = gid_single_hit;

							Germplasm g = manager
							.getGermplasmByGID(gid_single_hit);

							// printSuccess(pedigreeList.get(k), parent, id, g,
							// "false");
							list.add(g);

						}
						int l = 0;
						for (int m = list.size() - 1; m >= 0; m--) {
							// printSuccess(pedigreeList.get(l), parent, id,
							// list.get(m), "new");
							updateCreatedGID("" + list.get(m).getGid(), id,
									pedigreeList.get(l), "new",
									createdGID_local);
							l++;
						}
						list.clear();

						// printSuccess(pedigreeList.get(i-1), parent, id,
						// germplasm_fin.get(0), "false");
						createdGID_local.add(temp.get(index));
						updateCreatedGID("" + germplasm_fin.get(0).getGid(),
								id, pedigreeList.get(l), "false",
								createdGID_local);

						Germplasm g = manager.getGermplasmByGID(gpid2_index);

						if (g == null) { // this is an ERROR, the gid should
							// exist
							error = true;

							printError(pedigree, parent, id); // prints ERROR to
							// file

						} else {
							gpid2 = g.getGpid2();
							gpid1 = g.getGpid1();
							error = false; // set the flag to false

							printSuccess(pedigree, parent, id, g, "false"); // print
							// to
							// file
							System.out.println("\t" + pedigree + " is found");
						}
					} else {
						Germplasm g;
						if (error) { // if the precedent pedigree does not exist

							germplasmList = new ArrayList<Germplasm>();

							int count_LOCAL = countGermplasmByName(pedigree,
									Database.LOCAL);
							int count_CENTRAL = countGermplasmByName(pedigree,
									Database.CENTRAL);
							germplasmList = getGermplasmList(pedigree,
									count_LOCAL, count_CENTRAL); // gets lists
							// of
							// germplasm
							// with that
							// name
							g = getGermplasmByGpid(gpid1, germplasmList); // get
							// the
							// germplasm
							// of
							// the
							// same
							// gpid1,
							// for
							// derivative
							// line,
							// or
							// gid
							// equals
							// to
							// the
							// gpid1
						} else {
							g = manager.getGermplasmByGID(gpid2);
						}
						if (g == null) { // this is an ERROR, the gid should
							// exist
							error = true;

							printError(pedigree, parent, id); // prints ERROR to
							// file

						} else {
							gpid2 = g.getGpid2();
							gpid1 = g.getGpid1();
							error = false; // set the flag to false

							printSuccess(pedigree, parent, id, g, "false"); // print
							// to
							// file
						}
					}
					single_hit = true;
					flag = false;
					multiple_hit = false;
					result = true;
				}
			}
		}

		temp.clear();
		germplasm.clear();
		germplasm_fin.clear();
		return result;
	}

	private List<List<String>> printError_temp(String pedigree, String parent,
			String id, List<List<String>> temp) {
		List<String> row = new ArrayList<String>();

		row.add(id);
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		// //System.out.println("Does not Exist");

		row.add("Does not Exist"); // gid
		row.add("N/A"); // method
		row.add("N/A"); // method
		row.add("N/A"); // location
		row.add("N/A"); // location
		row.add("N/A"); // gpid1
		row.add("N/A"); // gpid2
		row.add("" + false); // tag
		row.add(cross_date); // cross' date of creation specified in the list
		row.add("N/A"); // date of the created GID

		createdGID_local.add(row);
		// ////System.out.println("row: "+row);
		// ////System.out.println("output: "+createdGID_local);
		// row.clear();
		temp.add(row);
		return temp;
	}

	private List<List<String>> printSuccess_temp(String pedigree,
			String parent, String id, Germplasm germplasm, String tag,
			List<List<String>> temp) throws MiddlewareQueryException {
		List<String> row = new ArrayList<String>();
		row.add(id);
		// ////System.out.println(id + ",");
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		// ////System.out.println("gid: " + germplasm.getGid());

		Location location = manager.getLocationByID(germplasm.getLocationId());
		Method method = manager.getMethodByID(germplasm.getMethodId());

		row.add("" + germplasm.getGid()); // gid
		String meth = method.getMname().replaceAll(",", "#");

		row.add("" + germplasm.getMethodId()); // method
		row.add(meth); // method

		String loc = location.getLname().replaceAll(",", "#");

		// ////System.out.print("loc "+loc);
		row.add("" + germplasm.getLocationId()); // location
		row.add(loc); // location
		row.add("" + germplasm.getGpid1()); // gpid1
		row.add("" + germplasm.getGpid2()); // gpid2
		row.add(tag); // gpid2
		row.add(cross_date); // cross' date of creation specified in the list
		row.add("" + germplasm.getGdate()); // date of the created GID

		/*
		 * ////System.out.print(id + ","); ////System.out.print(parent + ",");
		 * // parent ////System.out.print(pedigree + ","); // pedigree
		 * ////System.out.print(germplasm.getGid() + ","); // gid
		 * ////System.out.print(germplasm.getMethodId() + "," + meth + ","); //
		 * method ////System.out.print(germplasm.getLocationId() + "," + loc +
		 * ","); // location ////System.out.print(germplasm.getGpid1() + ",");
		 * // gpid1 ////System.out.print(germplasm.getGpid2() + ","); // gpid2
		 * ////System.out.println(tag + ",\n"); // gpid2
		 */
		// clearing memory
		germplasm = null;

		method = null;
		location = null;
		germplasm = null;

		temp.add(row);

		/*
		 * System.out.println("size:"+temp.size()+" temp: "+row);
		 * 
		 * System.out.println("\n------------"); for(int i=0; i<temp.size();
		 * i++){ System.out.println(""+temp.get(i)); }
		 * System.out.println("------------");
		 */
		// ////System.out.println("output: "+createdGID_local);
		// row.clear();
		return temp;
	}

	private List<List<String>> printChooseGID_temp(String pedigree,
			String parent, String id, List<List<String>> temp) {
		List<String> row = new ArrayList<String>();
		row.add(id);
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		// //System.out.println("CHOOSE GID" );

		row.add("CHOOSE GID"); // gid
		row.add("N/A"); // method
		row.add("N/A"); // method
		row.add("N/A"); // location
		row.add("N/A"); // location
		row.add("N/A"); // gpid1
		row.add("N/A"); // gpid2
		row.add("" + false); // gpid2
		row.add(cross_date); // cross' date of creation specified in the list
		row.add("N/A"); // date of the created GID

		temp.add(row);
		// ////System.out.println("row: "+row);
		// ////System.out.println("output: "+createdGID_local);
		// row.clear();
		return temp;

	}

	public List<List<String>> printNotSet_temp(String pedigree, String parent,
			String id, List<List<String>> temp) {
		List<String> row = new ArrayList<String>();
		row.add(id);
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		// //System.out.println("NOT SET");

		row.add("NOT SET"); // gid
		row.add("N/A"); // method
		row.add("N/A"); // method
		row.add("N/A"); // location
		row.add("N/A"); // location
		row.add("N/A"); // gpid1
		row.add("N/A"); // gpid2
		row.add("" + false); // tag
		row.add(cross_date); // cross' date of creation specified in the list
		row.add("N/A"); // date of the created GID

		temp.add(row);
		// ////System.out.println("row: "+row);
		// ////System.out.println("output: "+createdGID_local);
		// row.clear();
		return temp;

	}

	public void multipleHits_inLocation(List<Germplasm> germplasm,
			String pedigree, String id, String root) throws IOException,
			MiddlewareQueryException {

		List<Name> name = null;
		String nval_gpid1, nval_gpid2;
		List<String> row = new ArrayList<String>();

		// System.out.println("1 existingTerm:"+existingTerm_local);

		for (int i = 0; i < germplasm.size(); i++) {

			row = new ArrayList<String>();
			// //System.out.println(germplasm.get(i).getGid());
			row.add(id);
			row.add(root);
			/*
			 * ////System.out.println("\n string: " + root);
			 * ////System.out.println("GID: " + germplasm.get(i).getGid());
			 * ////System.out.println("gpid1: " + germplasm.get(i).getGpid1());
			 * ////System.out.println("gpid2: " + germplasm.get(i).getGpid2());
			 */
			name = new ArrayList<Name>();
			if (germplasm.get(i).getGpid1() != 0
					&& germplasm.get(i).getGpid2() != 0) {

				name = manager.getNamesByGID(germplasm.get(i).getGpid1(), 0,
						null);
				nval_gpid1 = name.get(0).getNval();

				// //System.out.println("nval_gpid1: " + nval_gpid1);
				name = manager.getNamesByGID(germplasm.get(i).getGpid2(), 0,
						null);
				nval_gpid2 = name.get(0).getNval();

				// //System.out.println("nval_gpid2: " + nval_gpid2);
			} else {
				if (germplasm.get(i).getGpid1() == 0
						&& germplasm.get(i).getGpid2() != 0) {
					nval_gpid1 = "Source unknown";
					name = manager.getNamesByGID(germplasm.get(i).getGpid2(),
							0, null);
					nval_gpid2 = name.get(0).getNval();

				} else if (germplasm.get(i).getGpid2() == 0
						&& germplasm.get(i).getGpid1() != 0) {
					name = manager.getNamesByGID(germplasm.get(i).getGpid1(),
							0, null);
					nval_gpid1 = name.get(0).getNval();
					nval_gpid2 = "Source unknown";
				} else {
					nval_gpid1 = "Source unknown";
					nval_gpid2 = "Source unknown";
				}
			}
			Location location = manager.getLocationByID(germplasm.get(i)
					.getLocationId());
			Method method = manager.getMethodByID(germplasm.get(i)
					.getMethodId());

			row.add("" + germplasm.get(i).getGpid1());
			row.add(nval_gpid1);

			row.add("" + germplasm.get(i).getGpid2());
			row.add(nval_gpid2);

			row.add("" + germplasm.get(i).getGid()); // gid

			String meth = method.getMname().replace(",", "#");
			row.add("" + germplasm.get(i).getMethodId());
			row.add(meth); // method
			String loc = location.getLname().replace(",", "#");
			row.add("" + germplasm.get(i).getLocationId());
			row.add(loc); // location
			row.add(pedigree); // pedigree name

			String date = germplasm.get(i).getGdate().toString();

			String yr, day, mo;
			// System.out.println("date: "+date);
			if (date.equals("0")) {
				yr = "0000";
				day = "00";
				mo = "00";
				// row.add(yr.concat(day).concat(mo)); //date of creation
				row.add("0"); // date of creation
			} else if ((date.charAt(3) == '0' && date.charAt(4) == '0')
					|| (date.charAt(6) == '0' && date.charAt(7) == '0')) {
				if(date.charAt(6) == '0' && date.charAt(7) == '0'){

					yr = date.charAt(0) + "" + date.charAt(1) + ""
					+ date.charAt(2) + "" + date.charAt(3)+"-"
					+ date.charAt(4) + "" + date.charAt(5);
					row.add(yr.concat("-01")); // date of
					// creation
				}else{
					yr = date.charAt(0) + "" + date.charAt(1) + ""
					+ date.charAt(2) + "" + date.charAt(3)+"-01-"
					+ date.charAt(6) + "" + date.charAt(7);
					row.add(yr); // date of creation
				}
			} else {
				// System.out.println(date.charAt(0));
				yr = date.charAt(0) + "" + date.charAt(1) + "" + date.charAt(2)
				+ "" + date.charAt(3) + "-";
				mo = date.charAt(4) + "" + date.charAt(5) + "-";
				day = date.charAt(6) + "" + date.charAt(7) + "";
				row.add(yr.concat(mo).concat(day)); // date of creation
			}

			// System.out.println("date: "+yr.concat(day).concat(mo));

			row.add(cross_date); // date of creation

			// clearing memory
			location = null;
			method = null;

			existingTerm_local.add(row);
			// System.out.println("row: "+row);

		}
		// existingTerm_local = existingTerm;

		System.out.println("existingTerm:" + existingTerm_local);
		//
		germplasm.clear();
		name.clear();
	}

	private void createPedigreeLine2(ArrayList<String> pedigreeList, String id,
			String parent) throws MiddlewareQueryException {
		int gpid2 = 0, gpid1 = 0, gid;

		Collections.reverse(pedigreeList);

		List<Germplasm> list = new ArrayList<Germplasm>();
		Germplasm g;
		for (int i = 0; i < pedigreeList.size(); i++) {

			g = new Germplasm();

			if (i == 0) {
				int methodID = selectMethodType_DER(pedigreeList.get(i), parent);

				gid = (int) addGID(pedigreeList.get(i), gpid1, gpid2, methodID,
						5, false); // ntype=5
				g = manager.getGermplasmByGID(gid);
				gpid2 = gid;
				gpid1 = gid;
			} else {
				int methodID = selectMethodType_DER(pedigreeList.get(i), parent);

				gid = (int) addGID(pedigreeList.get(i), gpid1, gpid2, methodID,
						5, false); // ntype=5
				g = manager.getGermplasmByGID(gid);
				gpid2 = gid;
			}
			// pedigreeList_GID.add(gid);
			list.add(g);
			// ////System.out.println(pedigreeList.get(i) + " gpid1: " + gpid1
			// + " gpid2: " + gpid2);
			if (i == pedigreeList.size() - 1) {
				GID = gid;
			}
		}

		for (int i = list.size() - 1; i >= 0; i--) {
			System.out.println("pedigreeList: " + pedigreeList.get(i));
			printSuccess(pedigreeList.get(i), parent, id, list.get(i), "new");
		}

		// clearing memory
		pedigreeList.clear();
		list.clear();

		g = null;

	}

	private JSONObject createPedigreeLine_CrossOp(
			ArrayList<String> pedigreeList, String id, String parent,
			String parent2, List<List<String>> temp_fin, int GID)
	throws MiddlewareQueryException {
		int gpid2 = 0, gpid1 = 0, gid;

		Collections.reverse(pedigreeList);

		List<Germplasm> list = new ArrayList<Germplasm>();
		Germplasm g;
		for (int i = 0; i < pedigreeList.size(); i++) {

			g = new Germplasm();

			if (i == 0) {
				int methodID = selectMethodType_DER(pedigreeList.get(i), parent);

				gid = (int) addGID(pedigreeList.get(i), gpid1, gpid2, methodID,
						5, false); // ntype=5
				g = manager.getGermplasmByGID(gid);
				gpid2 = gid;
				gpid1 = gid;
			} else {
				int methodID = selectMethodType_DER(pedigreeList.get(i), parent);

				gid = (int) addGID(pedigreeList.get(i), gpid1, gpid2, methodID,
						5, false); // ntype=5
				g = manager.getGermplasmByGID(gid);
				gpid2 = gid;
			}
			// pedigreeList_GID.add(gid);
			list.add(g);
			// ////System.out.println(pedigreeList.get(i) + " gpid1: " + gpid1
			// + " gpid2: " + gpid2);
			if (i == pedigreeList.size() - 1) {
				GID = gid;
			}
		}

		for (int i = list.size() - 1; i >= 0; i--) {
			System.out.println("pedigreeList: " + pedigreeList.get(i));
			temp_fin = printSuccess_temp(pedigreeList.get(i), parent2, id,
					list.get(i), "new", temp_fin);
		}

		// clearing memory
		// pedigreeList.clear();
		list.clear();

		g = null;

		JSONObject output = new JSONObject();
		output.put("GID", GID);
		output.put("temp_fin", temp_fin);
		output.put("pedList", pedigreeList);
		return output;

	}

	private void printError(String pedigree, String parent, String id) {
		List<String> row = new ArrayList<String>();

		row.add(id);
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		// //System.out.println("Does not Exist");

		row.add("Does not Exist"); // gid
		row.add("N/A"); // method
		row.add("N/A"); // method
		row.add("N/A"); // location
		row.add("N/A"); // location
		row.add("N/A"); // gpid1
		row.add("N/A"); // gpid2
		row.add("" + false); // gpid2
		row.add(cross_date); // cross' date of creation specified in the list
		row.add("N/A"); // date of the created GID

		createdGID_local.add(row);
		// ////System.out.println("row: "+row);
		// ////System.out.println("output: "+createdGID_local);
		// row.clear();

	}

	private void printNotSet(String pedigree, String parent, String id) {
		List<String> row = new ArrayList<String>();
		row.add(id);
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		// //System.out.println("NOT SET");

		row.add("NOT SET"); // gid
		row.add("N/A"); // method
		row.add("N/A"); // method
		row.add("N/A"); // location
		row.add("N/A"); // location
		row.add("N/A"); // gpid1
		row.add("N/A"); // gpid2
		row.add("" + false); // gpid2
		row.add(cross_date); // cross' date of creation specified in the list
		row.add("N/A"); // date of the created GID

		createdGID_local.add(row);
		System.out.println("row: " + row);
		// ////System.out.println("output: "+createdGID_local);
		// row.clear();

	}

	private void printChooseGID(String pedigree, String parent, String id) {
		List<String> row = new ArrayList<String>();
		row.add(id);
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		// //System.out.println("CHOOSE GID" );

		row.add("CHOOSE GID"); // gid
		row.add("N/A"); // method
		row.add("N/A"); // method
		row.add("N/A"); // location
		row.add("N/A"); // location
		row.add("N/A"); // gpid1
		row.add("N/A"); // gpid2
		row.add("" + false); // gpid2
		row.add(cross_date); // cross' date of creation specified in the list
		row.add("N/A"); // date of the created GID

		createdGID_local.add(row);

		// ////System.out.println("row: "+row);
		// ////System.out.println("output: "+createdGID_local);
		// row.clear();

	}

	private void printSuccess(String pedigree, String parent, String id,
			Germplasm germplasm, String tag) throws MiddlewareQueryException {
		List<String> row = new ArrayList<String>();
		row.add(id);
		// ////System.out.println(id + ",");
		row.add(parent); // parent
		row.add(pedigree); // pedigree

		// ////System.out.println("gid: " + germplasm.getGid());
		// System.out.println("location: " + germplasm.getLocationId());
		Location location = manager.getLocationByID(germplasm.getLocationId());
		Method method = manager.getMethodByID(germplasm.getMethodId());

		row.add("" + germplasm.getGid()); // gid
		// System.out.println("GID: "+germplasm.getGid());
		// System.out.println("MID: "+germplasm.getMethodId());
		// System.out.println("MNAME:  "+method.getMname());
		String meth = method.getMname().replaceAll(",", "#");

		row.add("" + germplasm.getMethodId()); // method
		row.add(meth); // method

		String loc = location.getLname().replaceAll(",", "#");

		// ////System.out.print("loc "+loc);
		row.add("" + germplasm.getLocationId()); // location
		row.add(loc); // location
		row.add("" + germplasm.getGpid1()); // gpid1
		row.add("" + germplasm.getGpid2()); // gpid2
		row.add(tag); // gpid2
		row.add(cross_date); // cross' date of creation specified in the list
		row.add("" + germplasm.getGdate()); // date of the created GID

		/*
		 * ////System.out.print(id + ","); ////System.out.print(parent + ",");
		 * // parent ////System.out.print(pedigree + ","); // pedigree
		 * ////System.out.print(germplasm.getGid() + ","); // gid
		 * ////System.out.print(germplasm.getMethodId() + "," + meth + ","); //
		 * method ////System.out.print(germplasm.getLocationId() + "," + loc +
		 * ","); // location ////System.out.print(germplasm.getGpid1() + ",");
		 * // gpid1 ////System.out.print(germplasm.getGpid2() + ","); // gpid2
		 * ////System.out.println(tag + ",\n"); // gpid2
		 */
		// clearing memory
		germplasm = null;

		method = null;
		location = null;
		germplasm = null;

		createdGID_local.add(row);
		System.out.println("row: " + row);
		// ////System.out.println("output: "+createdGID_local);
		// row.clear();
	}

	public Germplasm getGermplasmByGpid(int gpid1, List<Germplasm> germplasmList) {
		for (int i = 0; i < germplasmList.size(); i++) {
			if (germplasmList.get(i).getGpid1() == gpid1
					|| germplasmList.get(i).getGid() == gpid1
					&& germplasmList.get(i).getLocationId() == locationID) {
				return germplasmList.get(i);
			}
		}
		return null;
	}

	public List<Germplasm> getGermplasm(Database db, String pedigree, int count)
	throws MiddlewareQueryException, IOException {

		List<Germplasm> germplasm = new ArrayList<Germplasm>();

		germplasm = manager.getGermplasmByName(pedigree, 0, count,
				GetGermplasmByNameModes.NORMAL, Operation.EQUAL, 0, null, db);

		return germplasm;
	}

	public List<Germplasm> getGermplasmList(String pedigree, int count_LOCAL,
			int count_CENTRAL) throws MiddlewareQueryException, IOException {

		List<Germplasm> germplasm = new ArrayList<Germplasm>();

		germplasm = manager.getGermplasmByName(pedigree, 0, count_LOCAL,
				GetGermplasmByNameModes.NORMAL, Operation.EQUAL, 0, null,
				Database.LOCAL);
		List<Germplasm> germplasm2 = new ArrayList<Germplasm>();
		germplasm2 = manager.getGermplasmByName(pedigree, 0, count_CENTRAL,
				GetGermplasmByNameModes.NORMAL, Operation.EQUAL, 0, null,
				Database.CENTRAL);

		for (int i = 0; i < germplasm2.size(); i++) {
			germplasm.add(germplasm2.get(i));

		}
		germplasm2.clear();
		return germplasm;
	}

	public ArrayList<String> saveToArray(ArrayList<String> array,
			String[] tokens) {

		String s = "";
		for (int i = 0; i < tokens.length; i++) {
			if (i == 0) {
				s = s + tokens[i];
			} else {
				s = s + "-" + tokens[i];
			}
			// ////System.out.println(""+s);
			array.add(s);
		}
		Collections.reverse(array);
		System.out.println("PEDLIST: " + array);
		// tokens=null;
		return array;
	}

	public int countGermplasmByName(String s, Database db)
	throws MiddlewareQueryException {
		// System.out.println("s: "+s);
		int count = (int) manager.countGermplasmByName(s,
				GetGermplasmByNameModes.NORMAL, Operation.EQUAL, 0, null, db);

		return count;
	}

	public int addGID(String pedigree, int gpid1, int gpid2, int methodID,
			int nameType, Boolean newCross) throws MiddlewareQueryException {

		int gid;

		// Germplasm Object
		Germplasm germplasm1 = new Germplasm();
		germplasm1.setMethodId(methodID);
		int gnpgs = 0;
		/*
		 * if(methodID==107){ // 107 = backcross gnpgs=2; //cross nameType=3; //
		 * unnamed cross }else
		 */
		// if(nameType==3){
		// nameType=3; //unnamed cross
		// }
		if (methodID == 205 || methodID == 31 || methodID == 207
				|| methodID == 206) { // 205=SPS 33=UNKNOWN 207=random bulk
			// 206=selected bulk
			gnpgs = -1; // derivative
			// nameType=5; //derivative
		} else {
			gnpgs = 2; // cross
			// nameType=2; //cross
		}
		germplasm1.setGnpgs(gnpgs);
		germplasm1.setGpid1(gpid1);
		// int setGpid2=
		germplasm1.setGpid2(gpid2);
		germplasm1.setUserId(Integer.valueOf(userID_local));
		germplasm1.setLgid(-1);
		germplasm1.setLocationId(locationID);
		// Date date = new Date();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Integer gdate;
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		if (newCross) {
			String yr = "";
			String day = "";
			String mo = "";
			if (cross_date.equals("not specified")) {
				gdate = 0;
			} else {
				String cross_date_l = "";
				if (cross_date.length() == 10) {

					if (cross_date.contains("/")) {
						cross_date_l = cross_date.replace("/", "");
					} else if (cross_date.contains("-")) {
						cross_date_l = cross_date.replace("-", "");
					}
					yr = cross_date_l.charAt(4) + "" + cross_date_l.charAt(5)
					+ "" + cross_date_l.charAt(6) + ""
					+ cross_date_l.charAt(7);
					day = cross_date_l.charAt(2) + "" + cross_date_l.charAt(3);
					mo = cross_date_l.charAt(0) + "" + cross_date_l.charAt(1)
					+ "";

					cross_date_l = yr.concat(mo).concat(day);
				} else if (cross_date.length() == 9) {

					if (cross_date.contains("/")) {
						cross_date_l = cross_date.replace("/", "");
					} else if (cross_date.contains("-")) {
						cross_date_l = cross_date.replace("-", "");
					}
					yr = cross_date_l.charAt(3) + "" + cross_date_l.charAt(4)
					+ "" + cross_date_l.charAt(5) + ""
					+ cross_date_l.charAt(7);
					day = cross_date_l.charAt(1) + "" + cross_date_l.charAt(2);
					mo = cross_date_l.charAt(0) + "";

					cross_date_l = yr.concat(mo).concat(day);
				} else if (cross_date.length() == 8) {

					yr = cross_date.charAt(4) + "" + cross_date.charAt(5) + ""
					+ cross_date.charAt(6) + "" + cross_date.charAt(7);
					day = cross_date.charAt(2) + "" + cross_date.charAt(3);
					mo = cross_date.charAt(0) + "" + cross_date.charAt(1) + "";

					cross_date_l = yr.concat(mo).concat(day);
				} else if (cross_date.length() == 4) {
					cross_date_l=cross_date+"0000";
				}else {
					cross_date_l = "0";
				}
				gdate = Integer.valueOf(cross_date_l);
			}
		} else {
			gdate = 0;
		}

		germplasm1.setGdate(gdate);
		germplasm1.setGrplce(0);
		germplasm1.setMgid(0);
		germplasm1.setReferenceId(1);
		germplasm1.setPreferredAbbreviation("N/A");
		germplasm1.setPreferredAbbreviation("N/A");

		// Name object
		Name name1 = new Name();
		name1.setNdate(gdate);
		name1.setNstat(0);
		name1.setReferenceId(0);
		name1.setUserId(Integer.valueOf(userID_local));
		name1.setLocationId(locationID);
		name1.setNval(pedigree);
		name1.setTypeId(nameType); // SET THE NAME TYPE 2 for derivative, 5 for
		// cross name

		gid = manager.addGermplasm(germplasm1, name1);
		// //System.out.println("Germplasm" + gid);

		germplasm1 = null;
		name1 = null;
		return gid;
	}

	public int selectMethodType(int femaleGID, int maleGID, String female_nval,
			String male_nval, String nval_created, int methodID)
	throws MiddlewareQueryException, IOException {
		System.out.println("****SELECT METHOD TYPE");
		Germplasm female_germplasm = manager.getGermplasmByGID(femaleGID);
		Germplasm male_germplasm = manager.getGermplasmByGID(maleGID);

		System.out.println("female: " + female_nval);
		System.out.println("male: " + male_nval);
		System.out.println("nval_created: " + nval_created);
		int methodType = 0;
		String methodDesc = "";
		if (methodID == 107) {
			methodType = methodID;
			methodDesc = "Backcross";
		} else {
			if ((female_germplasm.getMethodId() == 101 && male_germplasm
					.getMethodId() == 101)) {
				methodType = 103; // if (AXB)X(CXD); double cross
				methodDesc = "Double Cross";
			} else if ((female_germplasm.getMethodId() == 101 && male_germplasm
					.getMethodId() == 205)
					|| (female_germplasm.getMethodId() == 205 && male_germplasm
							.getMethodId() == 101)
							|| (female_germplasm.getMethodId() == 101 && male_germplasm
									.getMethodId() == 31)
									|| (female_germplasm.getMethodId() == 31 && male_germplasm
											.getMethodId() == 101)
											|| (female_germplasm.getMethodId() == 207 && male_germplasm
													.getMethodId() == 101)
													|| (female_germplasm.getMethodId() == 101 && male_germplasm
															.getMethodId() == 207)
															|| (female_germplasm.getMethodId() == 202 && male_germplasm
																	.getMethodId() == 101)
																	|| (female_germplasm.getMethodId() == 101 && male_germplasm
																			.getMethodId() == 202)
																			|| (female_germplasm.getMethodId() == 206 && male_germplasm
																					.getMethodId() == 101)
																					|| (female_germplasm.getMethodId() == 101 && male_germplasm
																							.getMethodId() == 206)

			) {
				methodType = 102; // if (AXB)XC; 3 way cross
				methodDesc = "Three-way cross";
			} else if ((female_germplasm.getMethodId() == 102 && male_germplasm
					.getMethodId() == 102)
					|| (female_germplasm.getMethodId() == 106 && male_germplasm
							.getMethodId() == 102)
							|| (female_germplasm.getMethodId() == 102 && male_germplasm
									.getMethodId() == 106)
									|| (female_germplasm.getMethodId() == 106 && male_germplasm
											.getMethodId() == 106)
											|| (female_germplasm.getMethodId() == 101 && male_germplasm
													.getMethodId() == 106)
													|| (female_germplasm.getMethodId() == 106 && male_germplasm
															.getMethodId() == 101)
															|| (female_germplasm.getMethodId() == 106 && male_germplasm
																	.getMethodId() > 0)
																	|| (female_germplasm.getMethodId() > 0 && male_germplasm
																			.getMethodId() == 106)
																			|| (female_germplasm.getMethodId() == 102 && male_germplasm
																					.getMethodId() > 0)
																					|| (female_germplasm.getMethodId() > 0 && male_germplasm
																							.getMethodId() == 102)
																							|| (female_germplasm.getMethodId() == 103 && male_germplasm
																									.getMethodId() > 0)
																									|| (female_germplasm.getMethodId() > 0 && male_germplasm
																											.getMethodId() == 103)

			) {
				methodType = 106; // Cross between two three-way or more complex
				// crosses or 1 3-way and a single cross |
				// methodTypes==DER
				methodDesc = "Complex Cross";
			} else {

				methodType = 101; // if AXB; single cross
				methodDesc = "Single cross";

			}
		}

		/*
		 * if(methodID==107){ System.out.println("Method is bc 1");
		 * methodType=107; methodDesc="Backcross"; }else
		 * if(female_nval.equals(male_nval)){
		 * System.out.println("Method is bc 2"); methodType=107;
		 * methodDesc="Backcross"; }else if(female_nval.contains("/") &&
		 * !male_nval.contains("/")){
		 * 
		 * int max=0; max=new CrossOp().maxCross(max, female_nval);
		 * if(female_nval.contains("*")){ max++; }
		 * 
		 * if(max==1 && (male_germplasm.getMethodId()==101 ||
		 * male_germplasm.getMethodId()==205 ||
		 * female_germplasm.getMethodId()==33)){ // A/B X C methodType=103; //if
		 * (AXB)X(CXD); double cross methodDesc=""; }else if(max==1 &&
		 * (male_germplasm.getMethodId()==103)){ methodType=102; //if
		 * [(AXB)X(CXD)] X (EXF); 3 way cross methodDesc="Three-way cross"; }
		 * else if(max==2 && (male_germplasm.getMethodId()==101 ||
		 * male_germplasm.getMethodId()==205 ||
		 * female_germplasm.getMethodId()==33)){ methodType=102; //if
		 * [(AXB)X(CXD)] X (EXF); 3 way cross methodDesc="Three-way cross";
		 * }else if(max >3){ methodType=106; //Cross between two three-way or
		 * more complex crosses or 1 3-way and a single cross
		 * methodDesc="Complex Cross"; }
		 * 
		 * }else if(male_nval.contains("/") && !female_nval.contains("/")){ int
		 * max=0; max=new CrossOp().maxCross(max, male_nval);
		 * if(male_nval.contains("*")){ max++; } if(max==1 &&
		 * (female_germplasm.getMethodId()==101 ||
		 * female_germplasm.getMethodId()==205 ||
		 * female_germplasm.getMethodId()==33 )){ // A/B X C methodType=103;
		 * //if (AXB)X(CXD); double cross methodDesc=""; }else if(max==1 &&
		 * (female_germplasm.getMethodId()==103)){ methodType=102; //if
		 * [(AXB)X(CXD)] X (EXF); 3 way cross methodDesc="Three-way cross"; }
		 * else if(max==2 && (female_germplasm.getMethodId()==101 ||
		 * female_germplasm.getMethodId()==205 ||
		 * female_germplasm.getMethodId()==33)){ methodType=102; //if
		 * [(AXB)X(CXD)] X (EXF); 3 way cross methodDesc="Three-way cross";
		 * }else if(max >3){ methodType=106; //Cross between two three-way or
		 * more complex crosses or 1 3-way and a single cross
		 * methodDesc="Complex Cross"; }
		 * 
		 * }else if(male_nval.contains("/") && female_nval.contains("/")){ /*int
		 * max1=0; max1=new CrossOp().maxCross(max1, female_nval); int max2=0;
		 * max2=new CrossOp().maxCross(max2, male_nval);
		 * if(female_nval.contains("*")){ max1++; } if(male_nval.contains("*")){
		 * max2++; } if(max1==1 && max2==1){ methodType=103; //if (AXB)X(CXD);
		 * double cross methodDesc=""; }else if(max1==1 && max2==2){
		 * methodType=102; //if [(AXB)X(CXD)] X (EXF); 3 way cross
		 * methodDesc="Three-way cross"; }else if(max1==2 && max2==1){
		 * methodType=102; //if [(AXB)X(CXD)] X (EXF); 3 way cross
		 * methodDesc="Three-way cross"; }else{ methodType=106; //Cross between
		 * two three-way or more complex crosses or 1 3-way and a single cross
		 * methodDesc="Complex Cross"; }
		 * 
		 * }
		 */

		// clearing memory
		female_germplasm = null;
		male_germplasm = null;

		return methodType;

	}

	/* GET/SEARCH FROM FILE METHODS */

	public Germplasm isExisting(String pedigree)
	throws MiddlewareQueryException {
		Germplasm g = new Germplasm();
		Location location = manager.getLocationByID(locationID);

		long count = manager.countGermplasmByLocationName(location.getLname(),
				Operation.EQUAL, Database.LOCAL);
		count += manager.countGermplasmByLocationName(location.getLname(),
				Operation.EQUAL, Database.CENTRAL);

		List<Germplasm> germplasm = manager.getGermplasmByLocationName(
				location.getLname(), 0, (int) count, Operation.EQUAL,
				Database.LOCAL);
		List<Germplasm> germplasm2 = new ArrayList<Germplasm>();
		germplasm2 = manager.getGermplasmByLocationName(location.getLname(), 0,
				(int) count, Operation.EQUAL, Database.CENTRAL);
		for (int i = 0; i < germplasm2.size(); i++) {
			germplasm.add(germplasm2.get(i));
		}

		// clearing memory
		germplasm2.clear();

		// //System.out.println("size: "+germplasm.size());
		if (germplasm.isEmpty()) {
			return g;
		} else {
			return germplasm.get(0);
		}
	}

	public Germplasm isExisting(int fgid, int mgid)
	throws MiddlewareQueryException {
		Germplasm g = new Germplasm();
		Location location = manager.getLocationByID(locationID);

		long count = manager.countGermplasmByLocationName(location.getLname(),
				Operation.EQUAL, Database.LOCAL);
		count += manager.countGermplasmByLocationName(location.getLname(),
				Operation.EQUAL, Database.CENTRAL);

		List<Germplasm> germplasm = manager.getGermplasmByLocationName(
				location.getLname(), 0, (int) count, Operation.EQUAL,
				Database.LOCAL);
		List<Germplasm> germplasm2 = new ArrayList<Germplasm>();
		germplasm2 = manager.getGermplasmByLocationName(location.getLname(), 0,
				(int) count, Operation.EQUAL, Database.CENTRAL);
		for (int i = 0; i < germplasm2.size(); i++) {
			germplasm.add(germplasm2.get(i));

		}

		// //System.out.println("fgid: "+ fgid);
		// //System.out.println("mgid: "+ mgid);

		for (int i = 0; i < germplasm.size(); i++) {
			if ((germplasm.get(i).getGpid1().equals(fgid) && germplasm.get(i)
					.getGpid2().equals(mgid))
					&& (mgid != 0 && fgid != 0)
					&& germplasm.get(i).getLocationId() == locationID) {
				// Name
				// name=manager.getGermplasmNameByID(germplasm.get(i).getGid());
				// ////System.out.println(germplasm.get(i).getGid()+" "+
				// name.getNval());
				g = germplasm.get(i);
			}
		}

		// clearing memory
		germplasm2.clear();
		germplasm.clear();
		return g;
	}

	private int getGID_fromFile(String pedigree, String id) throws IOException {
		int gid = 0;

		for (int i = 0; i < createdGID_local.size(); i++) {
			List<String> row_object = createdGID_local.get(i);
			if (row_object.get(0).equals(id)
					&& ((row_object.get(2).equals(pedigree) && row_object
							.get(1).equals(pedigree)))) {

				if (row_object.get(3).equals("CHOOSE GID")
						|| row_object.get(3).equals("NOT SET")) {
					gid = 0;
				} else {
					gid = Integer.valueOf(row_object.get(3));
					break;
				}
			}
		}

		// //System.out.println("get GID from file: "+gid);

		// //System.out.println("  ###END getGID_fromFile \n");

		// clearing memory

		return gid;
	}

	public boolean has_GID(String id, String parent) throws IOException {

		for (int i = 0; i < createdGID_local.size(); i++) {
			List<String> row_object = createdGID_local.get(i);

			if (row_object.get(0) == id && row_object.get(1) == parent) {
				if (row_object.get(3) == "NOT SET"
					|| row_object.get(3) == "CHOOSE GID") {
					return false;
				}
			}
		}

		return true;
	}

	/* END GET/SEARCH FROM FILE METHODS */

	/* UPDATE FILES METHODS */
	private List<List<String>> updateCreatedGID(String gid, String id,
			String pedigree, String newGID, List<List<String>> createdGID)
			throws NumberFormatException, MiddlewareQueryException {

		System.out.println("Start updating ...");
		// //System.out.println("SIZE:"+ createdGID.size());
		// //System.out.println("CREATED**: "+createdGID);

		for (int i = 0; i < createdGID.size(); i++) {
			List<String> row_object = createdGID.get(i);
			if (gid.equals("CHOOSE GID")) {
				if (row_object.get(0).equals(id)
						&& row_object.get(2).equals(pedigree)) {
					row_object.set(3, "CHOOSE GID");
					createdGID.set(i, row_object);
					break;
				}

			} else if (gid.equals("NOT SET")) {
				if (row_object.get(0).equals(id)
						&& row_object.get(2).equals(pedigree)) {
					row_object.set(3, "NOT SET");
					createdGID.set(i, row_object);
					break;
				}
			} else {
				Germplasm germplasm = manager.getGermplasmByGID(Integer
						.valueOf(gid));

				// System.out.println("\t id "+ id+ " row_object.get(0: "+
				// row_object.get(0));
				// System.out.println("\t pedigree "+ pedigree+
				// " row_object.get2: "+
				// row_object.get(2));

				if (row_object.get(0).equals(id)
						&& row_object.get(2).equals(pedigree)) {
					// System.out.println("FOUND");
					Location location = manager.getLocationByID(germplasm
							.getLocationId());

					Method method = manager.getMethodByID(germplasm
							.getMethodId());
					/*
					 * output_object.add(id);
					 * output_object.add(row_object.get(1));
					 * output_object.add(pedigree);
					 * output_object.add(germplasm.getGid().toString());
					 * output_object.add(germplasm.getMethodId().toString());
					 * output_object
					 * .add(method.getMname().toString().replaceAll(",", "#"));
					 * output_object.add(germplasm.getLocationId().toString());
					 * output_object
					 * .add(location.getLname().toString().replaceAll(",",
					 * "#"));
					 * output_object.add(germplasm.getGpid1().toString());
					 * output_object.add(germplasm.getGpid2().toString());
					 * output_object.add(newGID);
					 */

					row_object.set(0, id);
					// row_object.set(,row_object.get(1));
					row_object.set(2, pedigree);
					row_object.set(3, germplasm.getGid().toString());
					row_object.set(4, germplasm.getMethodId().toString());
					row_object.set(5,
							method.getMname().toString().replaceAll(",", "#"));
					row_object.set(6, germplasm.getLocationId().toString());
					row_object.set(7, location.getLname().toString()
							.replaceAll(",", "#"));
					row_object.set(8, germplasm.getGpid1().toString());
					row_object.set(9, germplasm.getGpid2().toString());
					row_object.set(10, newGID);
					row_object.set(12, "" + germplasm.getGdate());

					// clearing memory
					createdGID.set(i, row_object);
					// row_object.clear();
					location = null;
					method = null;
					break;

				}
			}
		}
		// System.out.println("CREATED**: "+createdGID);

		System.out.println("*** END Updating createdGID");
		createdGID_local = createdGID;
		return createdGID_local;

	}

	public List<List<String>> updateFile_createdGID(String gid, String id,
			String pedigree, String newGID, String parent) throws IOException,
			MiddlewareQueryException, InterruptedException {

		List<List<String>> output = new ArrayList<List<String>>();

		for (int i = 0; i < createdGID_local.size(); i++) {

			List<String> row_object = createdGID_local.get(i);
			List<String> output_object = new ArrayList<String>();

			if (row_object.get(0).equals(id)
					&& row_object.get(2).equals(pedigree)) {
				Germplasm germplasm = manager.getGermplasmByGID(Integer
						.valueOf(gid));

				Location location = manager.getLocationByID(germplasm
						.getLocationId());

				Method method = manager.getMethodByID(germplasm.getMethodId());

				output_object.add(id);
				output_object.add(parent);
				output_object.add(pedigree);
				output_object.add(germplasm.getGid().toString());
				output_object.add(germplasm.getMethodId().toString());
				// cells[4] = ""+methodID;
				output_object.add(method.getMname().toString()
						.replaceAll(",", "#"));
				output_object.add(germplasm.getLocationId().toString());
				output_object.add(location.getLname().toString()
						.replaceAll(",", "#"));
				output_object.add(germplasm.getGpid1().toString());
				output_object.add(germplasm.getGpid2().toString());
				output_object.add(newGID);
				output_object.add(cross_date);
				output_object.add(germplasm.getGdate().toString());

				// clearing memeory

				location = null;
				method = null;
			} else {
				for (int j = 0; j < row_object.size(); j++) {
					output_object.add(row_object.get(j));
				}
			}

			output.add(output_object);
		}

		return output;
	}

	private List<List<String>> update_list(Germplasm germplasm, String id,
			String pedigree) throws IOException, MiddlewareQueryException {

		System.out.println("*** Starting Updating List");
		System.out.println("*** ");
		List<List<String>> output = new ArrayList<List<String>>();
		List<String> row_object = new ArrayList<String>();
		// //System.out.println("\t list: "+list_local);
		for (int i = 0; i < list_local.size(); i++) {

			row_object = list_local.get(i);
			System.out.println("row_object.get(2): " + row_object.get(2)
					+ " id:" + id);
			if (row_object.get(2).equals(id)) {
				row_object.set(0, germplasm.getGid().toString());
				list_local.set(i, row_object);

			}
		}
		System.out.println("\n\t list: " + list_local);
		// //System.out.println("*** END Updating corrected.csv");

		return list_local;
	}

	/* END OF UPDATE FILES METHODS */

	/* PRINT To FILE METHODS */

	/* END PRINT To FILE METHODS */

}