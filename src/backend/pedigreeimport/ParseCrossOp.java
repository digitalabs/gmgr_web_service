package backend.pedigreeimport;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.json.simple.JSONObject;
import backend.pedigreeimport.Tokenize;

/**
 * Handles parsing of germplasm names with cross operators to get the progenies
 * @author Nikki G. Carumba
 */
public class ParseCrossOp {

	/**
	 * @param line germplasm name
	 * @param list list of progenies
	 * @param standardize boolean, if true, correct the germplasm name
	 * @param correctedList lis of progenies with corrected names
	 * @return output JSOB object with error,list,correctedList
	 * @throws MiddlewareQueryException
	 * @throws IOException
	 */
	public JSONObject main(String line,List<String> list, Boolean standardize, List<String> correctedList) throws MiddlewareQueryException, IOException {
		int familyCount = 0;
		List<List<String>> twoDim = new ArrayList<List<String>>();
		List<String> row = new ArrayList<String>();

		String error="";

		int max = maxCross(line);
		String temp = line;

		row.add(temp);
		row.add("0");   // 0 for unexplored token
		twoDim.add(row);

		JSONObject output = new JSONObject();

		output=method(max, familyCount,error,row,list,twoDim, output, correctedList, standardize);

		return output;
	}

	/**
	 * Entry point to get the progenies of the germplasm name with cross operator '/'
	 * @param line germplasm name
	 * @param list empty list of progenies
	 * @return list updated list of the progenies
	 * @throws MiddlewareQueryException
	 * @throws IOException
	 */
	public static List<String> parsedStrings(String line, List<String> list) throws MiddlewareQueryException, IOException{
		int max = maxCross(line);
		String temp = line;
		int familyCount = 0;
		List<List<String>> twoDim = new ArrayList<List<String>>();
		List<String> row = new ArrayList<String>();

		row.add(temp);
		row.add("0");   // 0 for unexplored token
		twoDim.add(row);
		list.add(line);
		list=getParsed_parents(max, familyCount, row, list, twoDim);

		return list;

	}

	/**
	 * Get the progenies of the germplasm name with cross operator '/'
	 * @param max integer maximum number of forward slashes or crosses in the string
	 * @param familyCount count of the number of families in the crosses
	 * @param row first dimensional array twoDim
	 * @param list list of progenies
	 * @param twoDim two dimensional array that stores the parsed germplasm name with cross opertors
	 * @return
	 * @throws MiddlewareQueryException
	 * @throws IOException
	 */
	private static List<String> getParsed_parents(int max, int familyCount,List<String> row,List<String> list,List<List<String>> twoDim) throws MiddlewareQueryException, IOException {


		if (max > 0) {
			String slash = "";
			for (int i = max; i > 0;) {
				slash = slash + "/";
				i--;
			}

			for (int i = 0; i < twoDim.size(); i++) {
				for (int j = 0; j < row.size(); j++) {
					if ("0".equals(twoDim.get(i).get(1))) {
						Pattern p1 = Pattern.compile(slash);
						Matcher m = p1.matcher(twoDim.get(i).get(0));

						while (m.find()) {
							String[] temp2 = twoDim.get(i).get(0).split(slash + "|\\+");   // use the character '+' just to flag where to split the string
							familyCount++;
							for (int k = 0; k < temp2.length; k++) {
								row = new ArrayList<String>();
								row.add(temp2[k]);
								row.add("0");
								twoDim.add(row);

								if(!list.contains(temp2[k])){
									list=sort(list,temp2[k]);
								}
							}
							twoDim.get(i).remove(1);
							twoDim.get(i).add("1");
						}
					}
				}
			}
			list=getParsed_parents(max - 1, familyCount,row,list,twoDim);
			return list;
		} else {
			return list;
		}
	}

	/**
	 * Recursive method that go through the progenies of the cross and checks if it is in standardized format
	 * 
	 * @param max integer maximum number of forward slashes or crosses in the string
	 * @param familyCount count of the number of families in the crosses
	 * @param error list of all errors in spaces or unrecognized patterns in the germplasm name
	 * @param row first dimensional array twoDim
	 * @param list list of progenies
	 * @param twoDim two dimensional array that stores the parsed germplasm name with cross opertors
	 * @param output JSONObject containing error, the list of progenies and the list of progenies with corrected germplasm names
	 * @param correctedList list of porgenies with corrected germplasm names 
	 * @param standardize boolean, if true, corrects the germplasm name
	 * @return output JSONObject containing error, the list of progenies and the list of progenies with corrected germplasm names
	 * @throws MiddlewareQueryException
	 * @throws IOException
	 */
	private JSONObject method(int max, int familyCount, String error,List<String> row,List<String> list,List<List<String>> twoDim, JSONObject output, List<String> correctedList, Boolean standardize) throws MiddlewareQueryException, IOException {

		int index;
		if (max > 0) {
			String slash = "";
			for (int i = max; i > 0;) {
				slash = slash + "/";
				i--;
			}
			String result = "";
			for (int i = 0; i < twoDim.size(); i++) {
				for (int j = 0; j < row.size(); j++) {
					if ("0".equals(twoDim.get(i).get(1))) {
						//System.out.println("token: " + twoDim.get(i).get(0));

						Pattern p1 = Pattern.compile(slash);
						Matcher m = p1.matcher(twoDim.get(i).get(0));

						while (m.find()) {
							String[] temp2 = twoDim.get(i).get(0).split(slash + "|\\+");   // use the character '+' just to flag where to split the string
							//System.out.println(Arrays.toString(temp2));
							familyCount++;
							for (int k = 0; k < temp2.length; k++) {
								row = new ArrayList<String>();
								row.add(temp2[k]);
								row.add("0");
								twoDim.add(row);

								if (k % 2 == 0) {
									System.out.println("\n(family" + familyCount + ") female:   " + temp2[k]);
								} else {
									System.out.println("(family" + familyCount + ") male:     " + temp2[k]+"\n");
								}
								System.out.println(temp2[k]+"? "+list.contains(temp2[k]));
								if(!list.contains(temp2[k])){

									if(temp2[k].contains("/")){
										list=sort(list,temp2[k]);
										correctedList=sort(correctedList,temp2[k]);
										index=0;
										for(int n=0; n<correctedList.size();n++ ){

											if(temp2[k].equals(list.get(n))){
												index=n;
											}
											System.out.println("this: "+temp2[k]);
											System.out.println("\t contain: "+list.get(n));
											System.out.println("\t to be replaced by: "+correctedList.get(n));
											if(temp2[k].contains(list.get(n)) ){

												String newTerm=temp2[k].replace(list.get(n), correctedList.get(n));
												temp2[k]=newTerm;
												correctedList.set(index, newTerm);
												System.out.println("list: "+correctedList.get(index));
											}

											System.out.println("---------");
											for(int r=0; r<correctedList.size(); r++){
												System.out.println(" "+ correctedList.get(r));
											}
											System.out.println("---------");

										}
									}
									if(!temp2[k].contains("/")){	
										System.out.println("does not contain '/'");
										if(standardize){
											String correctedTerm ;

											if(temp2[k].contains("*")){
												Pattern p2 = Pattern.compile("(\\d)(\\*)(\\D)(.+)"); // backcross to male
												Matcher m2 = p2.matcher(temp2[k]);
												if(m2.matches()){
													String[] parsed=temp2[k].split("\\*");
													correctedTerm = new FixString().checkString(parsed[1]);
													result = new NomenclatureRules().checkString(correctedTerm);
													//correctedTerm=parsed[0].concat("*".concat(correctedTerm));
													if(parsed[1].startsWith("IR")){
														//temp2[k]=parsed[1];
														//temp2[k]=parsed[0]+"*"+temp2[k];
														correctedTerm=parsed[0]+"*"+correctedTerm;
													}else{
														//temp2[k]=parsed[0]+"*"+temp2[k];
														correctedTerm=parsed[0]+"*"+correctedTerm;
													}

												}else{
													p2 = Pattern.compile("(\\d+)(\\*)(\\d)(.+)"); // backcross to female
													m2 = p2.matcher(temp2[k]);
													String[] parsed=temp2[k].split("\\*");
													correctedTerm = new FixString().checkString(parsed[0]);
													result = new NomenclatureRules().checkString(correctedTerm);
													//=correctedTerm.concat("*".concat(parsed[1]));
													if(parsed[0].startsWith("IR")){
														//temp2[k]=parsed[0];
														//temp2[k]=parsed[0]+"*"+correctedTerm;
														correctedTerm=correctedTerm+"*"+parsed[1];
													}else{

														correctedTerm=correctedTerm+"*"+parsed[1];
														//temp2[k]=correctedTerm+"*"+parsed[1];

													}
												}

											}else{
												correctedTerm = new FixString().checkString(temp2[k]);
												result = new NomenclatureRules().checkString(correctedTerm);
											}
											//System.out.println("standardize");

											System.out.println("correctedTermz; "+correctedTerm);

											if(!result.equals("")){
												error+=result;
												System.out.println("result: "+result);

											}else{
												System.out.println("LIST: ");
												list=sort(list,temp2[k]);
												System.out.println("CORRECTED LIST: ");
												correctedList=sort(correctedList,correctedTerm);

												for(int n=0; n<correctedList.size();n++ ){
													System.out.println("here: "+correctedList.get(n));
													if(correctedList.get(n).contains(temp2[k])){

														String newTerm=correctedList.get(n).replace(temp2[k], correctedTerm);
														System.out.println("correctedTerm: "+correctedTerm);
														System.out.println("newTerm: "+newTerm);
														correctedList.set(n, newTerm);
														System.out.println("list: "+correctedList.get(n));
													}
												}
												System.out.println("---------"); 
												index=-1;
												for(int r=0; r<correctedList.size(); r++){
													if(correctedList.get(r).equals(correctedTerm)){
														index=r;
													}

												}
												System.out.println("index @ST: "+ index);
												System.out.println("corrected@ST: "+ correctedList);
												System.out.println("---------");


												//if(temp2[k].contains("-")){
												if(temp2[k].contains("-") || temp2[k].contains("*") && !temp2[k].contains("/")){

													System.out.println("correctedTermz; "+correctedTerm);
													Pattern p = Pattern.compile("IR");
													Matcher m1 = p.matcher(correctedTerm);
													String[] tokens={""};
													String[] tokens_list={""};

													if (m1.lookingAt()) {
														tokens = new Tokenize().tokenize(correctedTerm);
														tokens_list = new Tokenize().tokenize(temp2[k]);

													}else{
														tokens[0]="";
														tokens_list[0]="";
													}
													ArrayList<String> pedigreeList = new ArrayList<String>();


													//String[] tokens = new Tokenize().tokenize(correctedTerm);

													pedigreeList =new AssignGid().saveToArray(pedigreeList, tokens);
													ArrayList<String> pedigreeList_list = new ArrayList<String>(); 
													pedigreeList_list=new AssignGid().saveToArray(pedigreeList_list, tokens_list);
													index++;
													for(int n=1; n<pedigreeList.size();n++){
														correctedList.add(index,pedigreeList.get(n));
														System.out.println("add:: "+pedigreeList.get(n));														
														list.add(index,pedigreeList_list.get(n));
														index++;
													}

													System.out.println("add:: "+pedigreeList);		

												}

											}

										}else{
											System.out.println("NOT ST");
											String correctedTerm ;
											//String result = "";

											if(temp2[k].contains("*")){
												Pattern p2 = Pattern.compile("(\\d)(\\*)(\\D)(.+)"); // backcross to male
												Matcher m2 = p2.matcher(temp2[k]);
												if(m2.matches()){
													String[] parsed=temp2[k].split("\\*");
													//correctedTerm = new FixString().checkString(parsed[1]);
													//result = new Main().checkString(correctedTerm);
													result = new NomenclatureRules().checkString(parsed[1]);
													//correctedTerm=parsed[0].concat("*".concat(correctedTerm));
													temp2[k]=parsed[1];
												}else{
													p2 = Pattern.compile("(\\d+)(\\*)(\\d)(.+)"); // backcross to female
													m2 = p2.matcher(temp2[k]);
													String[] parsed=temp2[k].split("\\*");
													//correctedTerm = new FixString().checkString(parsed[0]);
													//result = new Main().checkString(correctedTerm);
													result = new NomenclatureRules().checkString(parsed[0]);
													//correctedTerm=correctedTerm.concat("*".concat(parsed[1]));
													temp2[k]=parsed[0];
												}

											}else{
												correctedTerm = new FixString().checkString(temp2[k]);
												result = new NomenclatureRules().checkString(temp2[k]);
											}

											//correctedTerm = new FixString().checkString(temp2[k]);
											//result = new Main().checkString(temp2[k]);
											System.out.println("result:" +result);
											//System.out.println("correctedTerm::" +correctedTerm);


											if(!result.equals("")){
												System.out.println("!!!ERROR FOUND");
												error+=result;
											}else{
												System.out.println("NO ERROR");
												correctedList=sort(correctedList,temp2[k]);
												list=sort(list,temp2[k]);

												if(temp2[k].contains("-") || temp2[k].contains("*") && !temp2[k].contains("/")){
													System.out.println("NO / and *");

													Pattern p = Pattern.compile("IR.");
													Matcher m1 = p.matcher(temp2[k]);
													String[] tokens={""};
													if (m1.lookingAt()) {
														tokens = new Tokenize().tokenize(temp2[k]);
														System.out.println("Starts with  IR: ");

													}else{
														tokens[0]="";
													}

													ArrayList<String> pedigreeList = new ArrayList<String>();

													pedigreeList = new AssignGid().saveToArray(pedigreeList, tokens);

													for(int n=1; n<pedigreeList.size();n++){
														correctedList.add(pedigreeList.get(n));
														System.out.println("add:: "+pedigreeList.get(n));														list.add(pedigreeList.get(n));
													}

													System.out.println("add 2:: "+pedigreeList);	

												}
											}
										}
									} 
								}
							}
							twoDim.get(i).remove(1);
							twoDim.get(i).add("1");
							//System.out.println("end finding "+slash);
						}

						//System.out.println("list:" + twoDim);
					}

				}
			}
			return method(max - 1, familyCount,error,row,list,twoDim, output, correctedList, standardize);
		} else {

			output.put("error",error);
			output.put("list",list);
			output.put("correctedList",correctedList);
			return output;
		}
	}
	/**
	 * Sorts the list, progenies with no cross operators should be at the top of the list
	 * @param list list of parsed germplasm names with cross operators
	 * @param line germplasm name
	 * @return list sorted list
	 */
	private static List<String> sort(List<String> list, String line) {
		String temp=line;
		Boolean crossOp=false;
		if(line.contains("/") || line.contains("*") || (line.contains("/") && line.contains("*"))){
			crossOp=true;
		}
		if(list.size()==1){
			list.add(line);
			return list;
		}

		for(int i=1; i< list.size();i++){
			if(list.get(i).contains("/") || list.get(i).contains("*") || (list.get(i).contains("*") && list.get(i).contains("*") ) ){
				// if list contains / or * or both

				if(i==list.size()-1){
					list.add(line);
					break;
				}
			}else{
				if(i==list.size()-1){ // if last index
					if(crossOp){
						//if last index does not contain / or * or both, swap them
						temp=list.get(i);
						list.set(i, line);
						line=temp;
					}
					list.add(line);	
					break;
				}else{
					if(crossOp){
						//if last index does not contain / or * or both, swap them
						temp=list.get(i);
						list.set(i, line);
						line=temp;
					}
				}
			}
		}
		return list;

	}

	/**
	 * GET the number of dosage in the backcross
	 * @param max integer maximum number of forward slashes or crosses in the
	 * string
	 * @return max integer maximum number of forward slashes or crosses in the
	 * string
	 */
	private static int maxCross(String line) {
		char currChar;
		int count = 0, start = 0, end = line.length(), max = 0;
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
}  // end class SingleCross
