package backend.pedigreeimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.json.simple.JSONObject;

/**
 * CrossOp class handles the parsing of germplasm names with cross operators '/' and '*'
 * This also corrects progeny names that are not in standardized format
 * @author Nikki Carumba
 */
public class CrossOp {

	/**
	 * Entry point in parsing germplasm name with Cross operators '/' and '*'
	 * @param line germplasm name
	 * @param standardize boolean, if true, correct the germplasm name
	 * @return JSON Object that contains the list progenies and the list of progenies with correct names
	 * @throws MiddlewareQueryException
	 * @throws IOException
	 */
	public JSONObject main(String line, Boolean standardize) throws MiddlewareQueryException, IOException{
		int max =0;
		max=maxCross(max,line);
		return method(line, max, standardize); 

	}

	/**
	 * Parse the backcross and correct the germplasm name
	 * @param line germplasm name
	 * @param max dosage of the backcross
	 * @param standardize boolean, if true, correct the name
	 * @return JSON Object that contains the list of the progenies and the list of progenies with correct names
	 * @throws MiddlewareQueryException
	 * @throws IOException
	 */
	
	private static JSONObject method(String line, int max, Boolean standardize) throws MiddlewareQueryException, IOException { // method of backCrossing
		String temp = line;
		List<String> list = new ArrayList<String>();
		List<String> correctedList = new ArrayList<String>();

		int dose=0;
		Pattern p1 = Pattern.compile("(\\d)(\\*)(\\D+)"); // backcross to male
		Matcher m1 = p1.matcher(line);


		//System.out.println("exapanded: "+temp);
		list.add(line);
		correctedList.add(line);
		JSONObject output=new JSONObject();
		output=new ParseCrossOp().main(temp, list, standardize,correctedList);   // call the ParseCross class to simplify into a family unit with male and female parent
		list=(List<String>) output.get("list");
		correctedList=(List<String>) output.get("correctedList");

		// add the parsed  backcrosses
		if(line.contains("*") && line.contains("/")){
			dose=0;
			Pattern p2 = Pattern.compile("\\*\\d"); // backcross to female
			Matcher m2 = p2.matcher(line);
			temp="";
			String temp2="";
			String right_corrected="";
			String left_corrected="";
			String right_list="";
			String left_list="";
			int k=0;

			String[] parsed=line.split("\\*");
			String[] parsed2=correctedList.get(0).split("\\*");
			print(parsed);
			print(parsed2);
			new BackCross();

			while(m2.find()){
				//System.out.println("BackCross to Female");
				right_corrected=BackCross.getRight(correctedList.get(0));
				left_corrected=BackCross.getLeft(correctedList.get(0));
				right_list=BackCross.getRight(list.get(0));
				left_list=BackCross.getLeft(list.get(0));

				for(int j=0; j< list.size();j++){

					if(list.get(j).contains("*") && !list.get(j).contains("/")){
						String dose_s=""+list.get(j).charAt(list.get(j).length()-1);
						dose=Integer.valueOf(dose_s);
						k=j;
						while(dose>2){
							temp="";
							temp2="";
							dose--;
							//System.out.println("dose= "+dose);

							if(right_corrected.equals("") && left_corrected.equals("")){
								temp2=parsed2[0]+"*"+dose;
								temp=parsed[0]+"*"+dose;
								k++;
							}else if(right_corrected.equals("") && !left_corrected.equals("")){
								parsed[1]=parsed[1].replace(""+parsed[1].charAt(0),"");
								parsed2[1]=parsed2[1].replace(""+parsed2[1].charAt(0),"");

								temp2=parsed2[0]+"*"+dose+left_corrected;
								temp=parsed[0]+"*"+dose+left_list;

							}else if(!right_corrected.equals("") && !left_corrected.equals("")){
								temp2=right_corrected+"*"+dose+left_corrected;
								temp=right_list+"*"+dose+left_list;
							}
							correctedList.add(k, temp2);
							list.add(k, temp);
							k++;
						}
						if(right_corrected.equals("") && left_corrected.equals("")){
							correctedList.add( parsed2[0]);
							list.add(parsed[0]);

						}else if(right_corrected.equals("") && !left_corrected.equals("")){
							parsed[1]=parsed[1].replace(""+parsed[1].charAt(0),"");
							parsed2[1]=parsed2[1].replace(""+parsed2[1].charAt(0),"");


							temp=parsed[0]+left_list;
							temp2=parsed2[0]+left_corrected;
							correctedList.add(k, temp2);
							list.add(k, temp);

							list.set(k+1, list.get(k+1).replace("*"+dose_s, ""));
							correctedList.set(k+1, correctedList.get(k+1).replace("*"+dose_s, ""));
						}else if(!right_corrected.equals("") && !left_corrected.equals("")){
							int index=0;

							for(int l=0;l<list.size();l++){
								//System.out.println("**list: "+list.get(l));
								if(list.get(l).equals(temp)){
									index=l;
								}
							}
							if(Integer.valueOf(dose_s)==2){
								k=k-1;
								temp2=right_corrected+left_corrected;
								temp=right_list+left_list;

								list.add(k+1, temp);
								correctedList.add(k+1, temp2);

								list.set(k+2, list.get(k+2).replace("*"+dose_s, ""));
								correctedList.set(k+2, correctedList.get(k+2).replace("*"+dose_s, ""));
							}else{
								temp=right_corrected+left_corrected;
								temp2=right_list+left_list;

								list.add(index+1, temp2);
								correctedList.add(index+1, temp);

								list.set(index+2, list.get(index+2).replace("*"+dose_s, ""));
								correctedList.set(index+2, correctedList.get(index+2).replace("*"+dose_s, ""));	
							}


						}else{
							temp=right_corrected+left_corrected;
							temp2=right_list+left_list;
							correctedList.add(k+1, temp);
							list.add(k+1, temp2);
							correctedList.remove(k+2);
							list.remove(k+2);
						}
						break;
					}
					k++;
				}
			}

			p1 = Pattern.compile("(\\d)(\\*)(\\D+)"); // backcross to male
			m1 = p1.matcher(line);
			dose=0;
			temp2="";
			k=0;
			parsed = line.split("\\d\\*", 2);
			parsed2 = correctedList.get(0).split("\\d\\*", 2);

			while(m1.find()){
				//System.out.println("BackCross to Male");

				right_corrected=BackCross.getRight_toMale(correctedList.get(0));
				left_corrected=BackCross.getLeft_toMale(correctedList.get(0));
				right_list=BackCross.getRight_toMale(list.get(0));
				left_list=BackCross.getLeft_toMale(list.get(0));

				System.out.println("right:"+ right_list);
				System.out.println("left:"+ left_list);


				for(int j=0; j< list.size();j++){
					if(list.get(j).contains("*") && !list.get(j).contains("/")){
						String slash="";
						String tokens_dose[] = list.get(j).split("\\*", 2);
						System.out.println("tokens_dose: "+Arrays.toString(tokens_dose));
						Boolean not_IR=true;
						if(tokens_dose[1].startsWith("IR")){
							not_IR=false;
						}
						System.out.println("tokens_dose[1]: "+tokens_dose[1]);
						if(not_IR==false){

							String dose_s=""+tokens_dose[0].charAt(0);
							dose=Integer.valueOf(dose_s);
							k=j;
							temp=list.get(j);

							while(dose>2){
								temp="";
								temp2="";
								dose--;
								String d=""+(dose);
								if(right_corrected.equals("") && !left_corrected.equals("")){
									temp=parsed[0].concat(d.concat("*").concat(parsed[1]));
									temp2=parsed2[0].concat(d.concat("*").concat(parsed2[1]));
									k++;
									correctedList.add(k, temp2);
									list.add(k, temp);
								}else if(!right_corrected.equals("") && !left_corrected.equals("")){
									temp=right_list+d+"*"+left_list;
									temp2=right_corrected+d+"*"+left_corrected/*+BcToMale.printSlash(max+1)+left_list*/;

									correctedList.add(k, temp2);
									list.add(k, temp);
								}
								else if(!right_corrected.equals("") && left_corrected.equals("")){
									String newList=list.get(k).replace(dose_s+"*", "");
									temp=right_list+d+"*"+newList;
									temp2=right_corrected+d+"*"+newList;

									correctedList.add(k, temp2);
									list.add(k, temp);
								}
								else if (right_corrected.equals("") && left_corrected.equals("")){
									temp=parsed[0].concat(d.concat("*").concat(parsed[1]));
									temp2=parsed2[0].concat(d.concat("*").concat(parsed2[1]));

									correctedList.add( temp2);
									list.add(temp);

								}
								k++;
							}

							if((!right_corrected.equals("") && !left_corrected.equals("")) || (!right_corrected.equals("") && left_corrected.equals(""))){
								int index=0;

								for(int l=0;l<list.size();l++){
									if(list.get(l).equals(temp)){
										index=l;
									}
								}
								if(!right_corrected.equals("") && !left_corrected.equals("")){
									if(Integer.valueOf(dose_s)==2){
										temp=right_list+left_list;
										temp2=right_corrected+left_corrected;

										list.add(index, temp);
										correctedList.add(index,temp2);

										list.set(index+1, list.get(index+1).replace(dose_s+"*", ""));
										correctedList.set(index+1, correctedList.get(index+1).replace(dose_s+"*", ""));	
									}else{

										temp=right_list+left_list;
										temp2=right_corrected+left_corrected;
										list.add(index+1, temp);
										correctedList.add(index+1,temp2);

										list.set(index+2, list.get(index+2).replace(dose_s+"*", ""));
										correctedList.set(index+2, correctedList.get(index+2).replace(dose_s+"*", ""));
									}

								}else{
									if(Integer.valueOf(dose_s)==2){

										temp=correctedList.get(index).replace(Integer.valueOf(dose_s)+"*", "");
										temp=right_corrected+temp;
										correctedList.add(index, temp);

										temp=list.get(index).replace(Integer.valueOf(dose_s)+"*", "");
										temp=right_list+temp;

										list.add(index, temp);



										list.set(index+1, list.get(index+1).replace(dose_s+"*", ""));
										correctedList.set(index+1, correctedList.get(index+1).replace(dose_s+"*", ""));

									}else{

										temp=correctedList.get(index+1).replace(dose_s+"*", "");
										temp=right_corrected+temp;
										correctedList.add(index+1, temp);
										//System.out.println("list index+1: "+list.get(index+1));
										temp=list.get(index+1).replace(dose_s+"*", "");
										temp=right_list+temp;

										list.add(index+1, temp);

										list.set(index+2, list.get(index+2).replace(dose_s+"*", ""));
										correctedList.set(index+2, correctedList.get(index+2).replace(dose_s+"*", ""));
									}
								}

							}else if(right_corrected.equals("") && !left_corrected.equals("")){
								int index=0;
								temp=correctedList.get(index).replace(Integer.valueOf(dose_s)+"*", "");
								temp=temp+"/"+temp;
								correctedList.add(temp);
								temp=list.get(index).replace(Integer.valueOf(dose_s)+"*", "");
								temp=temp+"/"+temp;
								list.add(temp);

								list.add(list.get(index).replace(Integer.valueOf(dose_s)+"*", ""));
								correctedList.add(correctedList.get(index).replace(Integer.valueOf(dose_s)+"*", ""));
							}else if (right_corrected.equals("") && left_corrected.equals("")){
								int index=0;
								temp=correctedList.get(0).replace(Integer.valueOf(dose_s)+"*", "");
								temp=temp+"/"+temp;
								correctedList.add(temp);
								temp=list.get(0).replace(Integer.valueOf(dose_s)+"*", "");
								temp=temp+"/"+temp;
								list.add(temp);

								list.add(list.get(index).replace(Integer.valueOf(dose_s)+"*", ""));
								correctedList.add(correctedList.get(index).replace(Integer.valueOf(dose_s)+"*", ""));
							}

							break;
						}
					}
				}
			}
		}

		output.remove("list");
		output.remove("correctedList");
		output.put("list", list);
		output.put("correctedList", correctedList);
		return output;
	}

	/**
	 * Parse the germplasm name to progenies with cross operators, no cross operators, ans the progenies of the backcross
	 * @param line germplasm name
	 * @param list list of progenies
	 * @return result JSONObject that contains the list-parsed progenies, backcrosses- list of progenies of the backcross,
	 *  crosses- list of progenies with '/', parents- list of progenies that do not have cross operators
	 * @throws MiddlewareQueryException
	 * @throws IOException
	 */
	
	public static JSONObject method2(String line, List<String> list) throws MiddlewareQueryException, IOException { // method of backCrossing
		String temp = line;
		List<List<String>> crosses=new ArrayList<List<String>>();
		List<List<String>> backcrosses=new ArrayList<List<String>>();
		List<String> parents=new ArrayList<String>();
		List<String> row=new ArrayList<String>();

		//System.out.println("\nBackCross to female: ");
		boolean female=false;

		int dose=0;

		new ParseCrossOp();
		list= ParseCrossOp.parsedStrings(temp, list);   // call the ParseCross class to simplify into a family unit with male and female parent
		int max =0;
		max=maxCross(max,line);
		List<String> pedList=new ArrayList<String>();
		for(int j=0; j<list.size();j++){
			pedList.add(list.get(j));
		}
		// add the parsed  backcrosses
		if(line.contains("*") && line.contains("/")){
			dose=0;
			Pattern p2 = Pattern.compile("\\*\\d"); // backcross to female
			Matcher m2 = p2.matcher(line);
			temp="";
			String right_list="";
			String left_list="";
			int k=0;

			String[] parsed=line.split("\\*",2);

			print(parsed);
			new BackCross();

			while(m2.find()){

				//System.out.println("BackCross to Female");
				right_list=BackCross.getRight(pedList.get(0));
				left_list=BackCross.getLeft(pedList.get(0));
				System.out.println("right:"+ right_list);
				System.out.println("left:"+ left_list);
				if(parsed[0].startsWith("IR")){
					for(int j=0; j< pedList.size();j++){
						if(pedList.get(j).contains("*") && !pedList.get(j).contains("/")){

							String dose_s=""+pedList.get(j).charAt(pedList.get(j).length()-1);
							dose=Integer.valueOf(dose_s);
							k=j;
							while(dose>2){
								temp="";
								dose--;
								if(right_list.equals("") && left_list.equals("")){
									temp=parsed[0]+"*"+dose;
									k++;
								}else if(right_list.equals("") && !left_list.equals("")){
									parsed[1]=parsed[1].replace(""+parsed[1].charAt(0),"");
									temp=parsed[0]+"*"+dose+left_list;

								}else if(!right_list.equals("") && !left_list.equals("")){
									temp=right_list+"*"+dose+left_list;
								}

								list.add(k, temp);
								row=new ArrayList<String>();
								row.add(temp);
								row.add("0");
								backcrosses.add(row);

								k++;
							}
							row=new ArrayList<String>();
							if(right_list.equals("") && left_list.equals("")){
								parents.add(list.get(j));
								list.add(parsed[0]+"/"+parsed[0]);
								row.add(parsed[0]+"/"+parsed[0]);
								row.add("0");
								backcrosses.add(row);

							}else if(right_list.equals("") && !left_list.equals("")){
								parsed[1]=parsed[1].replace(""+parsed[1].charAt(0),"");
								temp=parsed[0]+left_list;
								list.add(k, temp);

								list.set(k+1, list.get(k+1).replace("*"+dose_s, ""));
								parents.add( list.get(k+1).replace("*"+dose_s, ""));
								row.add(temp);
								row.add("0");
								backcrosses.add(row);
							}else if(!right_list.equals("") && !left_list.equals("")){
								int index=0;

								for(int l=0;l<list.size();l++){
									if(list.get(l).equals(temp)){
										index=l;
									}
								}
								if(Integer.valueOf(dose_s)==2){
									k=k-1;
									temp=right_list+left_list;
									list.add(k+1, temp);

									list.set(k+2, list.get(k+2).replace("*"+dose_s, ""));

									parents.add(list.get(k+2).replace("*"+dose_s, ""));
									row.add(temp);
									row.add("0");
									backcrosses.add(row);

								}else{
									temp=right_list+left_list;
									list.add(index+1, temp);

									list.set(index+2, list.get(index+2).replace("*"+dose_s, ""));

									parents.add(list.get(index+2).replace("*"+dose_s, ""));

									row.add(temp);
									row.add("0");
									backcrosses.add(row);
								}

								break;
							}else{
								row=new ArrayList<String>();
								temp=right_list+left_list;
								list.add(k+1, temp);
								list.remove(k+2);

								row.add(temp);
								row.add("0");
								backcrosses.add(row);
							}
						}else if(pedList.get(j).contains("*") && pedList.get(j).contains("/")){
							row=new ArrayList<String>();
							row.add(pedList.get(j));
							row.add("0");
							crosses.add(row);
							//System.out.println("\tCROSSES: "+crosses);
						}else{
							parents.add(pedList.get(j));
						}
						k++;
					}
				}else{
					row=new ArrayList<String>();
					row.add(list.get(0));
					row.add("0");
					crosses.add(row);

					for(int j=0; j< pedList.size();j++){
						if(pedList.get(j).contains("*") && !pedList.get(j).contains("/")){

							parents.add(pedList.get(j));
						}
					}
				}
			}

			Pattern p1 = Pattern.compile("(\\d)(\\*)(\\D+)"); // backcross to male
			Matcher m1 = p1.matcher(line);
			dose=0;

			k=0;
			parsed = line.split("\\d\\*", 2);

			while(m1.find()){
				if(parsed[1].startsWith("IR")){

					//System.out.println("BackCross to Male");

					right_list=BackCross.getRight_toMale(pedList.get(0));
					left_list=BackCross.getLeft_toMale(pedList.get(0));

					for(int j=0; j< pedList.size();j++){
						if(pedList.get(j).contains("*") && !pedList.get(j).contains("/")){
							String dose_s=""+pedList.get(j).charAt(0);
							dose=Integer.valueOf(dose_s);
							k=j;
							temp=pedList.get(j);

							while(dose>2){
								temp="";
								dose--;
								String d=""+(dose);
								if(right_list.equals("") && !left_list.equals("")){
									temp=parsed[0].concat(d.concat("*").concat(parsed[1]));
									k++;
								}else if(!right_list.equals("") && !left_list.equals("")){
									temp=right_list+d+"*"+left_list;

								}
								else if(!right_list.equals("") && left_list.equals("")){
									temp=right_list+d+"*"+pedList.get(j).replace(dose_s+"*", "");

								}
								else if (right_list.equals("") && left_list.equals("")){
									temp=parsed[0].concat(d.concat("*").concat(parsed[1]));
								}
								list.add(k, temp);
								row=new ArrayList<String>();
								row.add(temp);
								row.add("0");
								backcrosses.add(row);

								k++;
							}
							row=new ArrayList<String>();
							if((!right_list.equals("") && !left_list.equals("")) || (!right_list.equals("") && left_list.equals(""))){
								int index=0;

								for(int l=0;l<list.size();l++){
									if(list.get(l).equals(temp)){
										index=l;
									}
								}
								if(!right_list.equals("") && !left_list.equals("")){
									if(Integer.valueOf(dose_s)==2){
										temp=right_list+left_list;

										list.add(index, temp);

										list.set(index+1, list.get(index+1).replace(dose_s+"*", ""));

										parents.add(list.get(index+1).replace(dose_s+"*", ""));

									}else{
										temp=right_list+left_list;
										list.add(index+1, temp);

										list.set(index+2, list.get(index+2).replace(dose_s+"*", ""));

										parents.add(list.get(index+2).replace(dose_s+"*", ""));

									}

								}else{
									temp=right_list+temp;

									temp=list.get(index+1).replace(Integer.valueOf(dose_s)+"*", "");
									temp=right_list+temp;

									list.add(index+1, temp);

									index=Integer.valueOf(dose_s);

									list.set(Integer.valueOf(dose_s), list.get(Integer.valueOf(dose_s)).replace(dose_s+"*", ""));

									parents.add(list.get(Integer.valueOf(dose_s)).replace(dose_s+"*", ""));

								}
								row.add(temp);
								row.add("0");
								backcrosses.add(row);

							}else if(right_list.equals("") && !left_list.equals("")){
								int index=0;

								temp=temp+"/"+temp;
								temp=list.get(index).replace(Integer.valueOf(dose_s)+"*", "");
								temp=temp+"/"+temp;
								list.add(temp);

								list.add(list.get(index).replace(Integer.valueOf(dose_s)+"*", ""));

								parents.add(list.get(index).replace(Integer.valueOf(dose_s)+"*", ""));

								row.add(temp);
								row.add("0");
								backcrosses.add(row);
							}else if (right_list.equals("") && left_list.equals("")){
								int index=0;
								temp=temp+"/"+temp;
								temp=list.get(0).replace(Integer.valueOf(dose_s)+"*", "");
								temp=temp+"/"+temp;
								list.add(temp);

								list.add(list.get(index).replace(Integer.valueOf(dose_s)+"*", ""));
								parents.add(list.get(index).replace(Integer.valueOf(dose_s)+"*", ""));

								row.add(temp);
								row.add("0");
								backcrosses.add(row);;
							}

							break;

						}else if(pedList.get(j).contains("*") && pedList.get(j).contains("/")){
							row=new ArrayList<String>();
							row.add(pedList.get(j));
							row.add("0");

							crosses.add(row);
							//System.out.println("\tCROSSES: "+crosses);
						}else{
							parents.add(pedList.get(j));
						}

					}
				}else{
					System.out.println("does not start with IR");
					row=new ArrayList<String>();
					row.add(list.get(0));
					row.add("0");
					crosses.add(row);

					for(int j=0; j< pedList.size();j++){
						if(pedList.get(j).contains("*") && !pedList.get(j).contains("/")){

							parents.add(pedList.get(j));
						}
					}
				}
			}

		}		

		JSONObject result=new JSONObject();
		result.put("list", list);
		result.put("backcrosses", backcrosses);
		result.put("crosses", crosses);
		result.put("parents", parents);
		return result;

	}

	static void print(String[] tokens) {
		//System.out.println(Arrays.toString(tokens));
	}

	/**
	 * GET the number of dosage in the backcross
	 * @param max integer maximum number of forward slashes or crosses in the
	 * string
	 * @return max integer maximum number of forward slashes or crosses in the
	 * string
	 */
	public static int maxCross(int max,String line) {
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
}
