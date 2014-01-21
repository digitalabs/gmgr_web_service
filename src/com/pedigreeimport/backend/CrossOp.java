package com.pedigreeimport.backend;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

	public String main(String line){
		int max =0;
		max=maxCross(max,line);

		return method(line, max); 

	}

	private static String method(String line, int max) { // method of backCrossing
		String temp = line;

		Pattern p = Pattern.compile("\\*\\d"); // backcross to female
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
			System.out.println("token: " + tokens[0]);
			tokens[0] = tokens[0].concat(slash).concat(tokens[0]);
			temp.replaceFirst("\\*\\d", tokens[0]);
			temp = tokens[0].concat(tokens[1]);
			System.out.println("token: " + temp);
		}


		return new ParseCrossOp().main(temp);   // call the SngleCross class to simplify into a family unit with male and female parent
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
