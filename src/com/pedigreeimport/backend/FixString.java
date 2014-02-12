/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pedigreeimport.backend;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;

/**
 *
 * @author NCarumba
 */
public class FixString {

    private static String A_IR = "(IR)"; // IRRI Line
    private static String A_SPACE = "(\\s)";   // white space
    private static String A_PLANT_NO = "(\\d+)";  // plant number
    private static String A_LOC = "(UBN|AJY|SRN|CPA|KKN|PMI|SKN|SRN)";   //location
    private static String A_SEL_NO = "(\\d+)"; //selection number
    private static String A_DASH = "(-)";  //dash
    private static String A_MP = "(\\d+MP)";   //mapping population followed by the plant number
    private static String A_BM = "((\\d{0,4}B)|R|AC|(C\\d+))"; // breeding methods: â€œBâ€? - bulk, â€œRâ€? - rapid generation advance or single seed descent, â€œACâ€? - anther culture. â€œCâ€? - for composite populations; a succeeding number indicates the specific cycle of composite
    private Pattern p = Pattern.compile(A_IR + A_PLANT_NO + "(\\s+)?"); // no space between IR and plant number
    private Pattern p1 = Pattern.compile("(^\\s+)(.)"); //space/s after dash or at the beginiing of the string
    private Pattern p2 = Pattern.compile(A_LOC + A_SEL_NO); // no space between the location number and the selection number
    private Pattern p3 = Pattern.compile("(\\d{0,5})(\\s+)(B)"); //space/s between the bulk number and the bulk code
    private Pattern p4 = Pattern.compile("(\\d+)(\\s+)(MP)");    ///space/s between the mapping population and the plant number
    private Pattern p5 = Pattern.compile("(C)(\\s+)(\\d+)"); //space/s between the composite population code and the plant number
    private Pattern p6 = Pattern.compile("(.)(\\s+)($)");    //space/s at the end of the string or before dash
    private String line;
    IRRISegGen sg = new IRRISegGen();
    Tokenize t = new Tokenize();
    private String tokens[];
    private String errorPatternList = "";

    public String checkString(String line) throws MiddlewareQueryException, IOException {

        Pattern p = Pattern.compile("IR");
        Matcher m = p.matcher(line);
        String standard = line;

        if (m.lookingAt()) {    // Breeding Line is IRRI
            standard = standardIRRI(line);
            //list = new IRRI().getListErrorsIRRI();

        }
//            Pattern p1 = Pattern.compile("WA");
//            Matcher m1 = p1.matcher(line);
//
//            if (m1.lookingAt()) {   // Breeding line is WARDA
//                System.out.println("WARDA line");
//                new WARDA().standardWARDA(line);
//            }

        //System.out.println("list @main: " + list);
        return standard;
    }

    /**
     *
     * @param a_line
     * @throws IOException 
     * @throws MiddlewareQueryException 
     */
    public String standardIRRI(String a_line) throws MiddlewareQueryException, IOException {

        line = a_line;
        //System.out.print("Breeding Line: IRRI");

        Pattern p = Pattern.compile(A_DASH);
        Matcher m = p.matcher(line);

        if (m.find()) {    //String is a segragating line
            //System.out.println(" (segragating generations)");
            segGen();
        } else {    //String is a either an IRRI elite line or a fixed line
            Pattern p1 = Pattern.compile("IRRI");
            Matcher m1 = p1.matcher(line);

            Pattern p2 = Pattern.compile("IR(\\s+)?\\d+(\\s+)?");
            Matcher m2 = p2.matcher(line);
            
            if (m1.lookingAt()) {//String is an IRRI elite line
                //System.out.println(" (elite line)");
                eliteLine();
               // System.exit(0);
            }else if (m2.matches()) {//String is an IRRI released line
                //System.out.println(" (released line)");
                releasedLine();
            } else {//String is an IRRI fixed line
                //System.out.println(" (fixed line)");
                fixedLine();
            }
        }
        //System.out.println("list @standardIRRI(): " + listErrorsIRRI);
        return line;
    }

    private void releasedLine() throws MiddlewareQueryException, IOException {
        Pattern p = Pattern.compile("IR\\s\\d+");
        Matcher m = p.matcher(line);

        if (!m.matches()) {
            fixReleasedLine();
            String tokens[] = t.tokenize(line);
            t.stringTokens(tokens);
        }
    }

    private void fixReleasedLine() {

        Pattern p1 = Pattern.compile("(IR)(\\d+)");
        Matcher m1 = p1.matcher(line);
        if (m1.matches()) {
            line = line.replaceAll(m1.group(1), m1.group(1) + " ");
            System.out.println("processed.");
            System.out.println("string: " + line + "\n");
        }
        Pattern p2 = Pattern.compile("(IR)(\\d+)(\\s+)");
        Matcher m2 = p2.matcher(line);
        if (m2.matches()) {
            line = line.replaceAll(m2.group(0), m2.group(1) + m2.group(2));
            System.out.println("processed.");
            System.out.println("string: " + line + "\n");
        }

    }

    private void eliteLine() throws MiddlewareQueryException, IOException {
        Pattern pp = Pattern.compile("IRRI\\s\\d{3,}\\s(.+)");
        Matcher m = pp.matcher(line);
        if (!m.matches()) {

            fixEliteLine();
            String tokens[] = t.tokenize(line);
            t.stringTokens(tokens);
        }
    }

    private void fixEliteLine() {

        Pattern p1 = Pattern.compile("(\\d{3,})(\\s+)");
        Matcher m1 = p1.matcher(line);
        if (m1.find()) {
            line = line.replaceAll(m1.group(0), m1.group(1));
            System.out.println("processed.");
            System.out.println("string: " + line + "\n");
        }
        Pattern p2 = Pattern.compile("(IRRI)(\\d{3,})");
        Matcher m2 = p2.matcher(line);
        if (m2.find()) {
            line = line.replaceAll(m2.group(1), m2.group(1) + " ");
            System.out.println("processed.");
            System.out.println("string: " + line + "\n");
        }
        Pattern p3 = Pattern.compile("(IRRI)(\\d{3,})(\\s(.+))");
        Matcher m3 = p3.matcher(line);
        if (m3.find()) {
            line = line.replaceAll(m2.group(2), m2.group(2) + " ");
            System.out.println("processed.");
            System.out.println("string: " + line + "\n");
        }

    }

    private void fixedLine() {
        Pattern p = Pattern.compile("IR\\d{2}(N|F|L|T|U|K|W|H|J|D|A|M|C)\\d{3,}((H|R|A|B|S)?)");
        Matcher m = p.matcher(line);

        if (!m.matches()) {
            fixFixedLine();
//            fixedLine();
        }
    }

    private void fixFixedLine() {

        Pattern p = Pattern.compile("(.)(\\s)(.)");
        Matcher m = p.matcher(line);
        if (m.find()) {
            //printGroup(m);
            line = line.replaceAll(m.group(0), m.group(1) + m.group(3));
            System.out.println("processed.");
            System.out.println("string: " + line + "\n");
        }
        Pattern p1 = Pattern.compile("(.)(\\s)($)");
        Matcher m1 = p1.matcher(line);
        if (m1.find()) {
            //printGroup(m1);
            line = line.replaceAll(m1.group(0), m1.group(1) + m1.group(3));
            System.out.println("processed.");
            System.out.println("string: " + line + "\n");
        }
    }
    private void segGen() {

        Pattern p = Pattern.compile(A_IR + A_SPACE + A_PLANT_NO + "(" + A_DASH + "(((" + A_LOC + A_SPACE + A_SEL_NO + ")|" + A_SEL_NO + ")|" + A_BM + "|" + A_MP + ")){1,5}");
        //Pattern p2 = Pattern.compile("IR\\s\\d+(-((((UBN|AJY|SRN|CPA|KKN|PMI|SKN|SRN)\\s\\d+)|\\d+)|((\\d{0,4}B)|R|AC|(C\\d+))|(\\d+MP))){1,5}");
        Matcher m = p.matcher(line);
        
        if (!m.matches()) {
            tokens = t.tokenize(line);
            line = fixString(tokens, line);
            //sg.correctLine(tokens, line);
        }
    }
    private String fixString(String[] tokens, String line) {  //method to fix all the errors found in the String
        String tempToken;

        for (int i = 0; i < tokens.length; i++) {
            Matcher m2 = p2.matcher(tokens[i]);
            if (m2.find()) {
                //System.out.println("no space between the location number and the selection number");
                tempToken = tokens[i];
                tokens[i] = tokens[i].replace(m2.group(0), m2.group(1) + " " + m2.group(2));
                line = line.replace(tempToken, tokens[i]);
                tokens = t.tokenize(line);
            }

            Matcher m = p.matcher(tokens[i]);
            if (m.find()) {
                //System.out.println("no space between IR and plant number");
                tempToken = tokens[i];
                tokens[i] = tokens[i].replace(m.group(1), m.group(1) + " ");
                line = line.replace(tempToken, tokens[i]);
                tokens = t.tokenize(line);
            }

            Matcher m3 = p3.matcher(tokens[i]);
            if (m3.find()) {
                //System.out.println("space/s between the bulk number and the bulk code");
                tempToken = tokens[i];
                tokens[i] = tokens[i].replace(m3.group(1) + m3.group(2) + m3.group(3), m3.group(1) + m3.group(3));
                line = line.replace(tempToken, tokens[i]);
                tokens = t.tokenize(line);
            }

            Matcher m4 = p4.matcher(tokens[i]);
            if (m4.find()) {
                //System.out.println("space/s between the mapping population and the plant number");
                tempToken = tokens[i];
                tokens[i] = tokens[i].replace(m4.group(1) + m4.group(2) + m4.group(3), m4.group(1) + m4.group(3));
                line = line.replace(tempToken, tokens[i]);
                tokens = t.tokenize(line);
            }

            Matcher m5 = p5.matcher(tokens[i]);
            if (m5.find()) {
                //System.out.println("space/s between the composite population code and the plant number");
                tempToken = tokens[i];
                tokens[i] = tokens[i].replace(m5.group(1) + m5.group(2) + m5.group(3), m5.group(1) + m5.group(3));
                line = line.replace(tempToken, tokens[i]);
                tokens = t.tokenize(line);
            }

            Matcher m1 = p1.matcher(tokens[i]);
            if (m1.find()) {
                //System.out.println("space/s at the start of the string");
                tempToken = tokens[i];
                tokens[i] = tokens[i].replace(m1.group(0), m1.group(2));
                line = line.replace("-" + tempToken, "-" + tokens[i]);
                tokens = t.tokenize(line);
            }

            Matcher m6 = p6.matcher(tokens[i]);
            if (m6.find()) {
                //System.out.println("space/s at the end of the string ");
                tempToken = tokens[i];
                tokens[i] = tokens[i].replace(m6.group(0), m6.group(1) + m6.group(3));
                line = line.replace(tempToken, tokens[i]);
                tokens = t.tokenize(line);
            }
        }
        tokens = t.tokenize(line);
        checkErrorPattern(tokens,line);
        return line;
    }

    public String checkErrorPattern(String[] tokens, String line) {
        //ERROR TRAPPING: pattern/s not recognized, unrecognized codes
        String temp;
        
        for (int i = 0; i < tokens.length; i++) {
            temp = line;
            Pattern p11 = Pattern.compile("(\\d+)|(IR\\s\\d+)|((UBN|AJY|SRN|CPA|KKN|PMI|SKN|SRN)\\s\\d+)|(\\d{0,4}B)|R|AC|(C\\d+)|(\\d+MP)");
            Matcher m11 = p11.matcher(tokens[i]);
            if (!m11.matches()) {
                temp = temp.replaceAll(tokens[i], tokens[i] + "^");
                //System.out.println(temp + "\t;string pattern not recognized ");
                errorPatternList += temp + "\t;string pattern not recognized \n";
            }
        }
        //System.out.println("errorPatternList:"+errorPatternList);
        return errorPatternList;
    }
}
