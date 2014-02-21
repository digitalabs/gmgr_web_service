package com.pedigreeimport.backend;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.json.simple.JSONObject;

import com.pedigreeimport.restjersey.AssignGid;
import com.pedigreeimport.backend.Tokenize;


/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author NCarumba
 */
public class ParseCrossOp {

	/**
	 *
	 * @param aline String inputted
	 * @throws IOException 
	 * @throws MiddlewareQueryException 
	 */
	/*public static void main(String[] args) throws MiddlewareQueryException, IOException  {
		String line="IR64/2*IR 88888-21-2-UBN 2-2//IR06H101A";
		List<String> list = new ArrayList<String>();
		List<String> correctedList = new ArrayList<String>();
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

		output=method(max, familyCount,error,row,list,twoDim, output, correctedList, false);
		
	}
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
		
		
		System.out.println("------");
		for(int j=0; j<list.size();j++){
			System.out.println("::"+list.get(j));
		}
		System.out.println("------");
		
		
		
		return list;
		
	}
	
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
						//System.out.println("token: " + twoDim.get(i).get(0));

						Pattern p1 = Pattern.compile(slash);
						Matcher m = p1.matcher(twoDim.get(i).get(0));

						while (m.find()) {
							String[] temp2 = twoDim.get(i).get(0).split(slash + "|\\+");   // ncarumba used the character + just to flag where to split the string
							//System.out.println(Arrays.toString(temp2));
							familyCount++;
							for (int k = 0; k < temp2.length; k++) {
								//System.out.println("temp2[k] "+temp2[k]);
								row = new ArrayList<String>();
								row.add(temp2[k]);
								row.add("0");
								twoDim.add(row);
								
								if(!list.contains(temp2[k])){
										list=sort(list,temp2[k]);
										//System.out.println("LIST: "+list);
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
			list=getParsed_parents(max - 1, familyCount,row,list,twoDim);
			return list;
		} else {
			//

			//for(int m=0; m<correctedList.size(); m++){
			//System.out.println("tokens @ parseCross: "+ correctedList.get(m));
			//}

			return list;
		}
		/*for(int m=0; m<list.size(); m++){
		System.out.println("@ return tokens @ parseCross: "+ list.get(m));
		}
		*/
		
		

	}
	
	/**
	 *
	 * @param max integer maximum number of forward slashes or crosses in the
	 * string
	 * @param familyCount integer variable counting the number of families in
	 * the crosses
	 * @return max integer maximum number of forward slashes or crosses in the
	 * string
	 * @throws IOException 
	 * @throws MiddlewareQueryException 
	 */
	private JSONObject method(int max, int familyCount, String error,List<String> row,List<String> list,List<List<String>> twoDim, JSONObject output, List<String> correctedList, Boolean standardize) throws MiddlewareQueryException, IOException {


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
							String[] temp2 = twoDim.get(i).get(0).split(slash + "|\\+");   // ncarumba used the character + just to flag where to split the string
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
										int index=0;
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
										//System.out.println("does not contains '/'");
										if(standardize){
											String correctedTerm ;
											
											if(temp2[k].contains("*")){
												Pattern p2 = Pattern.compile("(\\d)(\\*)(\\D)(.+)"); // backcross to male
												Matcher m2 = p2.matcher(temp2[k]);
												if(m2.matches()){
													String[] parsed=temp2[k].split("\\*");
													correctedTerm = new FixString().checkString(parsed[1]);
													result = new Main().checkString(correctedTerm);
													correctedTerm=parsed[0].concat("*".concat(correctedTerm));
													//temp2[k]=parsed[1];
												}else{
													p2 = Pattern.compile("(\\d+)(\\*)(\\d)(.+)"); // backcross to female
													m2 = p2.matcher(temp2[k]);
													String[] parsed=temp2[k].split("\\*");
													correctedTerm = new FixString().checkString(parsed[0]);
													result = new Main().checkString(correctedTerm);
													correctedTerm=correctedTerm.concat("*".concat(parsed[1]));
													//temp2[k]=parsed[0];
												}
												
											}else{
												correctedTerm = new FixString().checkString(temp2[k]);
												result = new Main().checkString(correctedTerm);
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
												System.out.println("---------"); int index=0;
												for(int r=0; r<correctedList.size(); r++){
													if(correctedList.get(r).equals(correctedTerm)){
														index=r;
													}
												System.out.println(" "+ correctedList.get(r));
												}
												System.out.println("---------");


												//if(temp2[k].contains("-")){
													if(temp2[k].contains("-") && !temp2[k].contains("*") && !temp2[k].contains("/")){
												
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
													

													new AssignGid();
													
													pedigreeList = AssignGid.saveToArray(pedigreeList, tokens);
													ArrayList<String> pedigreeList_list = new ArrayList<String>(); 
													pedigreeList_list=AssignGid.saveToArray(pedigreeList_list, tokens_list);
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
													correctedTerm = new FixString().checkString(parsed[1]);
													result = new Main().checkString(correctedTerm);
													correctedTerm=parsed[0].concat("*".concat(correctedTerm));
													//temp2[k]=parsed[1];
												}else{
													p2 = Pattern.compile("(\\d+)(\\*)(\\d)(.+)"); // backcross to female
													m2 = p2.matcher(temp2[k]);
													String[] parsed=temp2[k].split("\\*");
													correctedTerm = new FixString().checkString(parsed[0]);
													result = new Main().checkString(correctedTerm);
													correctedTerm=correctedTerm.concat("*".concat(parsed[1]));
													//temp2[k]=parsed[0];
												}
												
											}else{
												correctedTerm = new FixString().checkString(temp2[k]);
												result = new Main().checkString(temp2[k]);
											}
											
												//correctedTerm = new FixString().checkString(temp2[k]);
												//result = new Main().checkString(temp2[k]);
												System.out.println("result:" +result);
												
												
											if(!result.equals("")){
												error+=result;
											}else{
												correctedList=sort(correctedList,temp2[k]);
												list=sort(list,temp2[k]);
												
												if(temp2[k].contains("-") && !temp2[k].contains("*") && !temp2[k].contains("/")){
													
													Pattern p = Pattern.compile("IR");
										            Matcher m1 = p.matcher(correctedTerm);
										            String[] tokens={""};
										            if (m1.lookingAt()) {
										            	tokens = new Tokenize().tokenize(correctedTerm);
														
										            }else{
										            	tokens[0]="";
										            }
													
													ArrayList<String> pedigreeList = new ArrayList<String>();

													new AssignGid();
													pedigreeList = AssignGid.saveToArray(pedigreeList, tokens);
													
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
			//System.out.println("error here: "+error);
			System.out.println("result2: "+ error);
			output.put("error",error);
			//output.put("error",error);
			output.put("list",list);
			output.put("correctedList",correctedList);
			return output;
		}
		//for(int m=0; m<list.size(); m++){
		//System.out.println("@ return tokens @ parseCross: "+ list.get(m));
		//}
		

	}
	private static List<String> sort(List<String> list, String line) {
		String temp=line;
		Boolean crossOp=false;
//System.out.println("Sort");
		if(line.contains("/") || line.contains("*") || (line.contains("/") && line.contains("*"))){
			crossOp=true;
		}
		if(list.size()==1){
			list.add(line);
			return list;
		}
		//System.out.println("LINE: "+ line);
		
		
		for(int i=1; i< list.size();i++){
			//System.out.println("HERE: "+ list.get(i));
			if(list.get(i).contains("/") || list.get(i).contains("*") || (list.get(i).contains("*") && list.get(i).contains("*") ) ){
				//System.out.println("List index ["+i+"] has cross operators");
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
		/*System.out.println("\n---------");
		for(int m=0; m<list.size(); m++){
		System.out.println(" "+ list.get(m));
		}
		
		
		System.out.println("---------");
*/
		
		return list;

	}

	private List<String> sort2(List<String> list, String line) {
		String temp=line;
		Boolean crossOp=false;

		if(line.contains("/") || line.contains("*") || (line.contains("/") && line.contains("*"))){
			crossOp=true;
		}
		if(list.isEmpty()){
			list.add(line);
		}

		for(int i=0; i< list.size();i++){
			//System.out.println("HERE");
			if(list.get(i).contains("/") || list.get(i).contains("*") || (list.get(i).contains("*") && list.get(i).contains("*") ) ){
				//System.out.println("List index ["+i+"] has cross operators");
				// if list contains / or * or both

				if(i==list.size()-1){
					list.add(line);
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
