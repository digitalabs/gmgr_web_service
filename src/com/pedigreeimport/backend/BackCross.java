package com.pedigreeimport.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BackCross {

	/**
	 * @param args
	 */

	public static void main(String[] args) {
		String line;
		//line="IR 2///IR 5// IR 6/ 4* IR 7";
		//line="IR 5//4* IR 7/ IR 6/// IR 9/IR 8 //// IR 2";
		//line="IR 5/// IR 9//4* IR 7 //// IR 2";
		//line="IR4///IR 88888-21-2-UBN 2-2/2*IR 7//IR06H101A";
		//line="IR5*2/ IR44//IR2";
		//line="IR7-7/3*IR06H101A";

		//line="IR6///IR7-7/3*IR06H101A//IR 5";
		//line="IR5//IR44*2/IR2";
		//line="IR 88888-21-2-UBN 2-2*2/IR 7//IR06H101A";
		line="IR 5////IR7-7//3*IR06H101A///IR 5";

		List<List<String>> twoDim = new ArrayList<List<String>>();
		List<String> row = new ArrayList<String>();
		String temp="";

		int max=0;
		max=maxCross(max, line);
		row.add(line);
		row.add("0");   // 0 for unexplored token
		row.add(""+max);   // no of max
		twoDim.add(row);

		//System.out.println("line: "+line+"\n");
		//temp=algo(max, twoDim, temp,line);
		//temp=getRight(line);
		//temp=getLeft(line);
		temp=getRight_toMale(line);
		//temp=getLeft_toMale(line);
		//System.out.println("result: "+temp);


	}

	/*public String main(String line) {
		//String line="IR 2///IR 5// IR 6/ 4* IR 7";
		//String line="IR 5//4* IR 7/ IR 6/// IR 9/IR 8 //// IR 2";
		//String line="IR 5/// IR 9//4* IR 7 //// IR 2";
		List<List<String>> twoDim = new ArrayList<List<String>>();
		List<String> row = new ArrayList<String>();
		String temp="";

		int max=0;
		max=maxCross(max, line);
		row.add(line);
		row.add("0");   // 0 for unexplored token
		row.add(""+max);   // no of max
		twoDim.add(row);

		//System.out.println("line: "+line+"\n");
		temp=algo(max, twoDim, temp,line);
		//System.out.println("result: "+temp);
	return temp;

	}
	 */
	public static String algo(int max, List<List<String>> twoDim, String temp,String line){
		List<String> row=new ArrayList<String>();
		List<String> new_row;
		String temp3="";
		for(int i=0; i< twoDim.size(); i++){
			row=twoDim.get(i);
			//System.out.println("ROW: "+row);
			if(row.get(1)=="0"){	// check if the node is unexplored (unexplored=="0")
				if(row.get(0).contains("/")){
					String[] tokens=row.get(0).split(printSlash(Integer.valueOf(row.get(2))),2);

					//print(tokens);

					new_row=new ArrayList<String>();
					new_row.add(tokens[0]);
					new_row.add("0");
					int max_local=0;
					max_local=maxCross(max_local, tokens[0]);
					new_row.add(""+max_local);

					twoDim.add(new_row);

					new_row=new ArrayList<String>();
					new_row.add(tokens[1]);
					new_row.add("0");
					max_local=0;
					max_local=maxCross(max_local, tokens[1]);
					new_row.add(""+max_local);
					twoDim.add(new_row);
					tokens=null;

				}else{
					//temp=temp+" "+row.get(0);
					if(row.get(0).contains("*")){
						String[] tokens=row.get(0).split("\\*",2);
						tokens=line.split(tokens[0]+"\\*"+ tokens[1],2);
						print(tokens);
						int slash=0;
						int count = 0, start = tokens[0].length()-1, end = 0;
						char currChar;
						while (start > end) {

							currChar = tokens[0].charAt(start);
							//System.out.println(": "+currChar);
							if (currChar == '/') {
								count++;
								if (slash <= count) {
									slash = count;
								}
							} else {
								break;
							}
							start--;
						}
						//System.out.println("slash: "+slash);

						int slash1=0;
						count = 0; start = 0; end=tokens[1].length();
						String temp2="";

						int temp_slash=slash1;
						String chart="";
						int counter=2;
						int next;
						if(!tokens[1].equals("")){

							next=0;
							while (start < end) {

								currChar = tokens[1].charAt(start);
								next=start+1;
								chart=chart.concat(""+currChar);
								//System.out.println("[ "+start+"]");
								//System.out.println("currchar: "+chart);
								//System.out.println("temp pslash1: "+temp_slash);
								//System.out.println("slash1: "+slash1);
								//System.out.println("temp3: "+temp3);
								//System.out.println("temp2: "+temp2);
								if (currChar == '/') {
									count++;

									if (slash1 <= count ) {

										slash1 = count;
										//System.out.println("\tslash1: "+slash1);

										if(next <tokens[1].length() && tokens[1].charAt(next)!='/' ){
											counter--;
											if(counter==1 && slash<slash1){
												counter--;
											}
											if(counter==0 ){
												if(slash1>temp_slash){
													temp3=temp2;
												}
												break;
											}
										}
										if(slash1>temp_slash){
											//System.out.println("\tcounter: "+counter);
											//System.out.println("\ttokens length: "+tokens[1].length());
											//System.out.println("\tstart+1: "+ next);
											//System.out.println("\t next token: "+tokens[1].charAt(next));
											temp_slash=slash1;
											temp3=temp2;
											//temp2="";
										}
									}
								} else {

									//System.out.println("\ttemp2: "+temp2);	
									count=0;
									slash1=0;									
									temp2=temp2.concat(""+currChar);	

								}
								start++;
								//System.out.println("temp pslash1: "+temp_slash);
								//System.out.println("slash1: "+slash1);
								//System.out.println("temp3: "+temp3);
								//System.out.println("temp2: "+temp2);

								//System.out.println("---------------------");
							}
							//System.out.println("FINAL temp3: "+temp3);
						}
						temp=tokens[0].replaceAll("\\"+printSlash(slash), printSlash(slash+1));
						temp=temp.concat(row.get(0));
						//
					}
					row.set(1, "1");
					twoDim.set(i, row);
				}

			}
		}
		new_row=null;
		return temp3;
	}
	public static String getRight(String line){
		String[] tokens=line.split("\\*",2);
		print(tokens);
		int slash1=0;
		int count = 0, end = 0, start=(tokens[0].length()-1);
		String temp2="";
		String temp3="";

		int temp_slash=slash1;
		char currChar;
		int counter=2;
		int next;

		next=0;
		while (start >= end) {

			currChar = tokens[0].charAt(start);
			next=start-1;
			//	chart=chart.concat(""+currChar);
			/*//System.out.println("[ "+start+"]");
				//System.out.println("currchar: "+chart);
				//System.out.println("temp pslash1: "+temp_slash);
				//System.out.println("slash1: "+slash1);
				//System.out.println("temp3: "+temp3);
				//System.out.println("temp2: "+temp2);
			 */
			if (currChar == '/') {
				count++;

				if (slash1 <= count ) {

					slash1 = count;
					//System.out.println("\tslash1: "+slash1);

					if(next <tokens[0].length() && tokens[0].charAt(next)!='/' ){
						counter--;

						if(counter==0 ){
							if(slash1>temp_slash){
								temp3=temp2;
							}
							break;
						}
					}
					if(slash1>temp_slash){
						/*	//System.out.println("\tcounter: "+counter);
							//System.out.println("\ttokens length: "+tokens[0].length());
							//System.out.println("\tstart+1: "+ next);
							//System.out.println("\t next token: "+tokens[0].charAt(next));
						 */
						temp_slash=slash1;

						temp3=temp2;
						//temp2="";
					}
				}
			} else {

				//System.out.println("\ttemp2: "+temp2);	
				count=0;
				slash1=0;
				temp2=""+currChar+temp2;	
			}
			start--;
			/*//System.out.println("temp pslash1: "+temp_slash);
				//System.out.println("slash1: "+slash1);
				//System.out.println("temp3: "+temp3);
				//System.out.println("temp2: "+temp2);

				//System.out.println("---------------------");
			 */
		}
		//System.out.println("FINAL right: "+temp3);
		return temp3;
	}
	public static String getRight_toMale(String line){
		//System.out.println("LINE: "+line);
		String[] tokens=line.split("\\*",2);

		int slash=0;
		
		//System.out.println("tokens length: "+ tokens[0].length());
		//System.out.println("=="+tokens[0].charAt(tokens[0].length()-1));
		tokens[0]=tokens[0].replaceAll("\\/"+tokens[0].charAt(tokens[0].length()-1),"/");
		int slash_count = 0, end = 0, start=tokens[0].length()-1;
		print(tokens);
		//System.out.println("tokens length: "+ tokens[0].length());
		//System.out.println("tokens 0: "+ tokens[0]);

		String temp="";
		String right="";

		int temp_slash=slash;
		char currChar;
		int counter=0;
		int next;
		String chart="<";

		next=0;
		while (start >= end) {

			currChar = tokens[0].charAt(start);
			if(start>end){
				next=start-1;	
			}
			
				chart=currChar+chart;
			//System.out.println("[ "+start+"]");
			//System.out.println("\tcurrchar: "+chart);
			//System.out.println("\ttemp_slash: "+temp_slash);
			//System.out.println("\tslash: "+slash);
			//System.out.println("\tright: "+right);
			//System.out.println("\ttemp: "+temp);
			//System.out.println("next char: "+tokens[0].charAt(next));

			if (currChar == '/') {
				slash_count++;
				if(start==tokens[0].length()-1){
					slash=slash_count;
					temp_slash=slash;
					temp=currChar+temp;
					right=temp;
					if( tokens[0].charAt(next)!='/' ){
						counter++;
					}
				}else{
					temp=currChar+temp;
					if(slash_count<temp_slash && slash==0){
						temp_slash=slash;
						right=temp;
					}
					if( tokens[0].charAt(next)!='/' ){
						counter++;
					}
					if(counter==2){
						break;
					}
					slash=slash_count;
				}
			}else{
				temp=currChar+temp;
				right=temp;
				slash_count=0;
				
			}start--;
		}
		//System.out.println("FINAL right: "+right);
		return right;
	}

	public static String getLeft_toMale(String line){
		String[] tokens=line.split("\\*",2);
		print(tokens);
		int slash1=0;
		int count = 0, end = tokens[1].length()-1, start=0;

		print(tokens);
		String temp2="";
		String temp3="";

		int temp_slash=slash1;
		char currChar;
		int counter=0;
		String chart="";
		int next=0;

		if(!tokens[1].equals("")){
			while (start < end) {

				currChar = tokens[1].charAt(start);
				next=start+1;
				//chart=chart.concat(""+currChar);
				chart=currChar+chart;
				/*//System.out.println("[ "+start+"]");
				//System.out.println("currchar: "+chart);
				//System.out.println("temp pslash1: "+temp_slash);
				//System.out.println("slash1: "+slash1);
				//System.out.println("temp3: "+temp3);
				//System.out.println("temp2: "+temp2);
				 */
				if (currChar == '/') {
					count++;

					if (slash1 <= count ) {

						slash1 = count;
						//System.out.println("\tslash1: "+slash1);

						if(next <tokens[1].length() && tokens[1].charAt(next)!='/' ){
							counter++;


							if(counter==2 ){
								if(slash1>temp_slash){
									temp2=currChar+temp2;
									temp3=temp2;
								}
								break;
							}
						}
						if(slash1>temp_slash){
							/*		//System.out.println("\tcounter: "+counter);
							//System.out.println("\ttokens length: "+tokens[1].length());
							//System.out.println("\tstart+1: "+ next);
							//System.out.println("\t next token: "+tokens[1].charAt(next));
							 */
							temp_slash=slash1;

							temp3=temp2;
							//temp2="";

						}
					}
				} else {

					//System.out.println("\ttemp2: "+temp2);	
					count=0;
					slash1=0;
					temp2=temp2+currChar;	
					if(next ==tokens[1].length()){
						temp3=temp2;
					}
				}
				start++;
				/*
				//System.out.println("temp pslash1: "+temp_slash);
				//System.out.println("slash1: "+slash1);
				//System.out.println("temp3: "+temp3);
				//System.out.println("temp2: "+temp2);

				//System.out.println("---------------------");
				 */
			}
		}
		//System.out.println("FINAL left: "+temp3);
		return temp3;
	}

	public static String getLeft(String line){
		String[] tokens=line.split("\\*",2);
		print(tokens);
		int slash1=0;
		int count = 0, end = tokens[1].length()-1, start=0;

		//System.out.println("tokens1: "+ tokens[1]);
		//System.out.println("tokens length: "+ tokens[1].length());

		tokens[1]=tokens[1].replaceFirst(""+tokens[1].charAt(0),"");
		//System.out.println("tokens1: "+ tokens[1]);
		//System.out.println("tokens length: "+ tokens[1].length());

		print(tokens);
		String temp2="";
		String temp3="";

		int temp_slash=slash1;
		char currChar;
		int counter=0;
		String chart="";
		int next=0;

		if(!tokens[1].equals("")){
			while (start < end) {

				currChar = tokens[1].charAt(start);
				next=start+1;
				//chart=chart.concat(""+currChar);
				chart=chart+currChar;
				//System.out.println("[ "+start+"]");
				//System.out.println("currchar: "+chart);
				//System.out.println("temp pslash1: "+temp_slash);
				//System.out.println("slash1: "+slash1);
				//System.out.println("temp3: "+temp3);
				//System.out.println("temp2: "+temp2);
				if (currChar == '/') {
					count++;

					if (slash1 <= count ) {

						slash1 = count;
						//System.out.println("\tslash1: "+slash1);

						if(next <tokens[1].length() && tokens[1].charAt(next)!='/' ){
							counter++;

							if(counter==2 ){
								//if(slash1<temp_slash){
								//temp2=temp2+currChar;
								temp3=temp2;
								//}
								break;
							}else{
								temp2=temp2+currChar;
							}
						}
						if(slash1>temp_slash){
							//System.out.println("\tcounter: "+counter);
							//System.out.println("\ttokens length: "+tokens[1].length());
							//System.out.println("\tstart+1: "+ next);
							//System.out.println("\t next token: "+tokens[1].charAt(next));
							temp_slash=slash1;

							temp3=temp2;
							//temp2="";

						}
					}
				} else {

					//System.out.println("\t NOT '/':: temp2: "+temp2);	
					count=0;
					slash1=0;
					temp2=temp2+currChar;	
					if(next ==tokens[1].length()){
						temp3=temp2;
					}
				}
				start++;
				//System.out.println("temp pslash1: "+temp_slash);
				//System.out.println("slash1: "+slash1);
				//System.out.println("temp3: "+temp3);
				//System.out.println("temp2: "+temp2);

				//System.out.println("---------------------");
			}
		}
		//System.out.println("FINAL left: "+temp3);
		return temp3;
	}

	static void print(String[] tokens) {
		//System.out.println(Arrays.toString(tokens));
	}
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

	public static String printSlash(int max){
		String slash="";
		for (int j = max; j > 0;) {
			slash = slash + "/";
			j--;
		}
		return slash;
	}

}
