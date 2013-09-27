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
public class SingleCross {

    List<List<String>> twoDim = new ArrayList<List<String>>();
    List<String> row = new ArrayList<String>();
    String line;

    /**
     *
     * @param aline String inputted
     */
    public void main(String aline) {
        int familyCount = 0;
        line = aline;
        int max = maxCross();
        String temp = line;

        row.add(temp);
        row.add("0");   // 0 for unexplored token
        twoDim.add(row);
        method(max, familyCount);
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
    private int method(int max, int familyCount) {

        if (max > 0) {
            String slash = "";
            for (int i = max; i > 0;) {
                slash = slash + "/";
                i--;
            }
            for (int i = 0; i < twoDim.size(); i++) {
                for (int j = 0; j < row.size(); j++) {
                    if ("0".equals(twoDim.get(i).get(1))) { // if token is not yet explored

                        Pattern p1 = Pattern.compile(slash);
                        Matcher m = p1.matcher(twoDim.get(i).get(0));

                        while (m.find()) {
                            String[] temp2 = twoDim.get(i).get(0).split(slash + "|\\+");

                            familyCount++;
                            for (int k = 0; k < temp2.length; k++) {
                                row = new ArrayList<String>();
                                row.add(temp2[k]);
                                row.add("0");   // 0 for unexplored token
                                twoDim.add(row);

                                if (k % 2 == 0) {
                                    System.out.println("\n(family" + familyCount + ") female:   " + temp2[k]);
                                } else {
                                    System.out.println("(family" + familyCount + ") male:     " + temp2[k] + "\n");
                                }
                                if (!temp2[k].contains("/")) {
                                    new IRRI().standardIRRI(temp2[k]);
                                }
                            }
                            if (max == 1) {
                                System.out.println(">>" + familyCount + " family/s identified");
                            }
                            twoDim.get(i).remove(1);
                            twoDim.get(i).add("1"); //  1 for explored token
                        }
                    }
                }
            }
        } else {
            System.exit(0);
        }
        method(max - 1, familyCount);
        return familyCount;

    }

    /**
     *
     * @param max integer maximum number of forward slashes or crosses in the
     * string
     */
    private int maxCross() {
        int max = 0, count;
        String slash, temp;

        Pattern p = Pattern.compile("(\\/)(\\d+)(\\/)");
        Matcher m1 = p.matcher(line);

        if (m1.find()) {
            m1.reset();
            while (m1.find()) {
                slash = "";
                count = Integer.parseInt(m1.group(2));
                for (int j = count - 2; j > 0; j--) {
                    slash = slash + "/";
                }
                temp = line.replace(m1.group(2), slash);
                line = temp;
                if (count > max) {
                    max = count;
                }
            }
        } else {
            max = new BackCross().maxCross(max);
        }
        return max;
    }
}  // end class SingleCross
