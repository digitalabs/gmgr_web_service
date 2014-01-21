package com.pedigreeimport.backend;


import java.util.ArrayList;
import java.util.List;
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
public class ParseCrossOp {

    /**
     *
     * @param aline String inputted
     */
    public String main(String line) {
        int familyCount = 0;
        List<List<String>> twoDim = new ArrayList<List<String>>();
        List<String> row = new ArrayList<String>();
        List<String> list = new ArrayList<String>();
        String error="";
       
        int max = maxCross(line);
        String temp = line;

        row.add(temp);
        row.add("0");   // 0 for unexplored token
        twoDim.add(row);
        return method(max, familyCount,error,row,list,twoDim);
    }

    /**
     *
     * @param max integer maximum number of forward slashes or crosses in the
     * string
     * @param familyCount integer variable counting the number of families in
     * the crosses
     * @return max integer maximum number of forward slashes or crosses in the
     * string
     */
    private String method(int max, int familyCount, String error,List<String> row,List<String> list,List<List<String>> twoDim) {


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
                                row = new ArrayList<String>();
                                row.add(temp2[k]);
                                row.add("0");
                                twoDim.add(row);

                                if (k % 2 == 0) {
                                    System.out.println("\n(family" + familyCount + ") female:   " + temp2[k]);
                                } else {
                                	System.out.println("(family" + familyCount + ") male:     " + temp2[k]+"\n");
                                }

                                if(!list.contains(temp2[k])){
                                	list.add(temp2[k]);
                                	//new IRRI().standardIRRI(temp2[k]);
                                	if(!temp2[k].contains("/")){
                                		String result = new Main().checkString(temp2[k]);
                                		if(!result.equals("")){
                                			error+=result;
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
            method(max - 1, familyCount,error,row,list,twoDim);
        } else {
        	System.out.println("error here: "+error);
        	return error;
        }

        return error;

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
