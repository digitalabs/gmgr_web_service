package com.pedigreeimport.backend;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.json.simple.JSONObject;


/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author NCarumba
 */
public class CrossOp {

	/* 

    	static String line;
		public static void main(String[] args)  {

        line = "IR 2/IR888//IR44";
        //String line = aline;
        //System.out.println(line);

        int max =0;
        max=maxCross(max);
        method(line, max);
    }
	 */

	public JSONObject main(String line, Boolean standardize) throws MiddlewareQueryException, IOException{
		int max =0;
		max=maxCross(max,line);

		return method(line, max, standardize); 

	}

	private static JSONObject method(String line, int max, Boolean standardize) throws MiddlewareQueryException, IOException { // method of backCrossing
		String temp = line;
		List<String> list = new ArrayList<String>();
		List<String> correctedList = new ArrayList<String>();
		
		System.out.println("\nBackCross to female: ");

		Pattern p = Pattern.compile("\\*\\d\\/"); // backcross to female
		Matcher m = p.matcher(line);
		Boolean backcross=false;

		while (m.find()) {
			String[] tokens = temp.split("\\*\\d", 2);
			print(tokens);

			String slash = "";
			max++;
			for (int j = max; j > 0;) {
				slash = slash + "/";
				j--;
			}
			//System.out.println("token: " + tokens[0]);
			tokens[0] = tokens[0].concat(slash).concat(tokens[0]);
			temp.replaceFirst("\\*\\d", tokens[0]);
			temp = tokens[0].concat(tokens[1]);
			//System.out.println("token: " + temp);
			backcross=true;
		}
		
		System.out.println("\nBackCross to male; ");
        Pattern p1 = Pattern.compile("\\d\\*\\D"); // backcross to male
        Matcher m1 = p1.matcher(line);
		
		while (m1.find()) {
            String[] tokens = temp.split("\\d\\*", 2);
            print(tokens);

            String slash = "";
            System.out.println("slash: "+max);
            max++;
            for (int j = max; j > 0;) {
                slash = slash + "/";
                j--;
            }
            /*System.out.println("slash: "+slash);
            if(tokens[1].contains("/")){	// if after the *+number has proceeding parent(s)
            	String male[]=tokens[1].split(slash_max);
            	temp = tokens[0].concat(male[0].concat(slash_max.concat(male[0].concat(slash.concat(male[1])))));
            	//tokens[0].concat(male[0].concat(slash_max.concat(male[0].concat(slash.concat(male[1])))))
                System.out.println("here1: "+temp);
            }else{
            	temp = tokens[0].concat(tokens[1].concat(slash.concat(tokens[1])));
                System.out.println("here2: "+temp);
            }
            */
           // line=temp;
            
            //return method();
            tokens[0] = tokens[0].concat(tokens[1]);
            temp.replaceFirst("\\*\\d", tokens[0]);
            temp = tokens[0].concat(slash.concat(tokens[1]));
            System.out.println("token: " + temp);
        }

		
			System.out.println("exapanded: "+line);
			list.add(line);
			correctedList.add(line);
			return new ParseCrossOp().main(temp, list, standardize,correctedList);   // call the SngleCross class to simplify into a family unit with male and female parent
		
		
	}
	public static List<String> method2(String line, List<String> list) throws MiddlewareQueryException, IOException { // method of backCrossing
		String temp = line;
		int max =0;
		max=maxCross(max,line);
		System.out.println("\nBackCross to female; ");
		Pattern p = Pattern.compile("\\*\\d\\/"); // backcross to female
		Matcher m = p.matcher(line);


		while (m.find()) {
			String[] tokens = temp.split("\\*\\d", 2);
			print(tokens);

			String slash = "";
			max++;
			for (int j = max; j > 0;) {
				slash = slash + "/";
				j--;
			}
			//System.out.println("token: " + tokens[0]);
			tokens[0] = tokens[0].concat(slash).concat(tokens[0]);
			temp.replaceFirst("\\*\\d", tokens[0]);
			temp = tokens[0].concat(tokens[1]);
			//System.out.println("token: " + temp);
			
		}
		
		System.out.println("\nBackCross to male; ");
        Pattern p1 = Pattern.compile("\\d\\*\\D"); // backcross to male
        Matcher m1 = p1.matcher(line);
		
		while (m1.find()) {
            String[] tokens = temp.split("\\d\\*", 2);
            print(tokens);

            String slash = "";
            System.out.println("slash: "+max);
            max++;
            for (int j = max; j > 0;) {
                slash = slash + "/";
                j--;
            }
            /*System.out.println("slash: "+slash);
            if(tokens[1].contains("/")){	// if after the *+number has proceeding parent(s)
            	String male[]=tokens[1].split(slash_max);
            	temp = tokens[0].concat(male[0].concat(slash_max.concat(male[0].concat(slash.concat(male[1])))));
            	//tokens[0].concat(male[0].concat(slash_max.concat(male[0].concat(slash.concat(male[1])))))
                System.out.println("here1: "+temp);
            }else{
            	temp = tokens[0].concat(tokens[1].concat(slash.concat(tokens[1])));
                System.out.println("here2: "+temp);
            }
            */
           // line=temp;
            
            //return method();
            tokens[0] = tokens[0].concat(tokens[1]);
            temp.replaceFirst("\\*\\d", tokens[0]);
            temp = tokens[0].concat(slash.concat(tokens[1]));
            System.out.println("token: " + temp);
        }

		
			System.out.println("exapanded: "+temp);
		
			new ParseCrossOp();
			return ParseCrossOp.parsedStrings(temp, list);   // call the SngleCross class to simplify into a family unit with male and female parent
		
		
	}

	static void print(String[] tokens) {
		System.out.println(Arrays.toString(tokens));
	}

	/**
	 *
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
