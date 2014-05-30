package backend.pedigreeimport;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;

/**
 * Handles parsing of germplasm names into tokens
 * @author Nikki G. Carumba
 */
public class Tokenize {

	/**
	 * Split germplasm name into tokens
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
		
		return st;

	}
}
