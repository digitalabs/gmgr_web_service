package com.pedigreeimport.backend;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;

/**
 *
 * @author NCarumba
 */
public class Tokenize {

	List<String> listGID = new ArrayList<String>();

	/**
	 *
	 * @param line 
	 * @return  String tokens[] resulting from the split line
	 */
	public String[] tokenize(String line) {
		String tokens[] = line.split("-");
		return tokens;
	}

	/**
	 *
	 * @param tokens 
	 * @throws MiddlewareQueryException 
	 * @throws IOException 
	 */
	public String stringTokens(String tokens[]) throws MiddlewareQueryException, IOException {
		//FileWriter writer = new FileWriter("c:\\createdGID.csv");
		String s = "", st = "";
		int count=tokens.length-1;
		while(count>0){
			for (int i = 0; i < count; i++) {
				if (i == 0) {
					s = s + tokens[i];
				} else {
					s = s + "-" + tokens[i];
				}
			} 
			
			if(count==tokens.length){
				st = st + s;
			}else{
				st =  st + "#"+ s;
			}
			
			s="";
			count--;
		}
		//System.out.println("st: "+st);
		
		/*
		s = "";
		st = "";
		
		for (int i = 0; i < tokens.length;) {
			if (i == 0) {
				s = s + tokens[i];
			} else {
				s = s + "-" + tokens[i];
			}
			i++;

			if(i==(tokens.length)){
				st = st + s;
			}else{
				st = st + s + "#";
			}
		}
		*/
		return st;

	}
	public void tokens(String tokens[]) throws MiddlewareQueryException, IOException {
		FileWriter writer = new FileWriter("c:\\createdGID.csv");
		String s = "", st = "",f1="";
		s = "";
		for (int i = 0; i < tokens.length;) {
			if (i == 0) {
				s = s + tokens[i];
				f1=s;	//f1 generation
				//createGID(s);
			} else {
				s = s + "-" + tokens[i];
				//int gid=createGID(s);
				//writer.append(""+gid+", \n");
			}
			i++;

			//Germplasm germplasm = manager.getGermplasmByGID(new Integer(50533));
			//System.out.println(germplasm);
			st = st + s + "\n";
		}
		writer.flush();
		writer.close();
		//System.out.println(st);

	}

}
