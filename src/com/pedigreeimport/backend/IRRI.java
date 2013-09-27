package com.pedigreeimport.backend;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * @author ncarumba
 */
public class IRRI {
    // For IRRI LINES

    private static String A_IR = "(IR)"; // IRRI Line
    private static String A_SPACE = "(\\s)";   // white space
    private static String A_PLANT_NO = "(\\d+)";  // plant number
    private static String A_LOC = "(UBN|AJY|SRN|CPA|KKN|PMI|SKN|SRN)";   //location
    private static String A_SEL_NO = "(\\d+)"; //selection number
    private static String A_DASH = "(-)";  //dash
    private static String A_MP = "(\\d+MP)";   //mapping population followed by the plant number
    private static String A_BM = "((\\d{0,4}B)|R|AC|(C\\d+))"; // breeding methods: “B�? - bulk, “R�? - rapid generation advance or single seed descent, “AC�? - anther culture. “C�? - for composite populations; a succeeding number indicates the specific cycle of composite
    private String line;
    IRRISegGen sg = new IRRISegGen();
    Tokenize t = new Tokenize();
    private String listErrorsIRRI = "";
    private String listErrorsFixed = "";
    //private String listErrorsSegGen = "";
    private String listErrorsReleased = "";
    private String listErrorsElite = "";

    /**
     *
     * @param a_line
     */
    public String standardIRRI(String a_line) {

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

            if (m1.lookingAt()) {//String is an IRRI elite line
                //System.out.println(" (elite line)");
                eliteLine();
                System.exit(0);
            }
            Pattern p2 = Pattern.compile("IR(\\s+)?\\d+(\\s+)?");
            Matcher m2 = p2.matcher(line);

            if (m2.matches()) {//String is an IRRI elite line
                //System.out.println(" (released line)");
                releasedLine();
            } else {//String is an IRRI fixed line
                //System.out.println(" (fixed line)");
                fixedLine();
            }
        }
        //System.out.println("list @standardIRRI(): " + listErrorsIRRI);
        return listErrorsIRRI;
    }

    private void segGen() {

        Pattern p = Pattern.compile(A_IR + A_SPACE + A_PLANT_NO + "(" + A_DASH + "(((" + A_LOC + A_SPACE + A_SEL_NO + ")|" + A_SEL_NO + ")|" + A_BM + "|" + A_MP + ")){1,5}");
        //Pattern p2 = Pattern.compile("IR\\s\\d+(-((((UBN|AJY|SRN|CPA|KKN|PMI|SKN|SRN)\\s\\d+)|\\d+)|((\\d{0,4}B)|R|AC|(C\\d+))|(\\d+MP))){1,5}");
        Matcher m = p.matcher(line);
        if (m.matches()) {
            //System.out.println(" correct");
            //String tokens[] = t.tokenize(line);
            //t.stringTokens(tokens);
        } else {
            //System.out.println("\n>>String not properly formatted.. ");
            sg.checkErrors(line);
            //System.out.println(sg.getListErrors());
            listErrorsIRRI = sg.getListErrors();
        }
    }

    private void releasedLine() {
        String temp = line;
        Pattern p = Pattern.compile("IR\\s\\d+");
        Matcher m = p.matcher(line);

        if (m.matches()) {
            //System.out.println("correct");
            //String tokens[] = t.tokenize(line);
            //t.stringTokens(tokens);
        } else {
            //System.out.println("\n>>String not properly formatted.. ");
            Pattern p1 = Pattern.compile("(IR)(\\d+)");
            Matcher m1 = p1.matcher(line);
            if (m1.matches()) {
                temp = temp.replaceAll(m1.group(1), m1.group(1) + "^");
                System.out.println(temp + "\t;space expected between IR and plant number");
                listErrorsReleased += temp + "\t;space expected between IR and plant number" + "#";
            }
            Pattern p2 = Pattern.compile("(IR)(\\d+)(\\s+)");
            Matcher m2 = p2.matcher(line);
            if (m2.matches()) {
                temp = temp.replaceAll(m2.group(3), m2.group(3) + "^");
                //System.out.println(temp + "\t;unexpected space/s found");
                listErrorsReleased += temp + "\t;unexpected space/s found" + "#";
            }
            setListErrorsIRRI(listErrorsReleased);
        }
    }

    private void eliteLine() {
        String temp = line;
        Pattern pp = Pattern.compile("IRRI\\s\\d{3,}\\s(.+)");
        Matcher m = pp.matcher(line);
        if (m.matches()) {
            //System.out.println(" correct");
            //String tokens[] = t.tokenize(line);
            //t.stringTokens(tokens);
        } else {
            //System.out.println("\n>>String not properly formatted.. ");
            //System.out.println(" incorrect");
            Pattern p2 = Pattern.compile("(IRRI)(\\d{3,})");
            Matcher m2 = p2.matcher(line);
            //System.out.println(m2);
            if (m2.find()) {
                temp = temp.replaceAll(m2.group(1), m2.group(1) + "^");
                //System.out.println(temp + "\t;space expected between IRRI and plant number");
                listErrorsElite += temp + "\t;space expected between IRRI and plant number" + "#";
            }

            Pattern p1 = Pattern.compile("(\\d{3,})(\\s+)");
            Matcher m1 = p1.matcher(line);
            if (m1.find()) {
                //printGroup(m);
                temp = line;
                //System.out.println("m: " + m5);
                temp = temp.replaceAll(m1.group(0), m1.group(1) + "^");
                //System.out.println(temp + "\t;unexpected space is found at the end of the token");
                listErrorsElite += temp + "\t;unexpected space is found at the end of the token" + "#";
            }
            Pattern p3 = Pattern.compile("(IRRI)(\\d{3,})(\\s(.+))");
            Matcher m3 = p3.matcher(line);
            //System.out.println(m2);
            if (m3.find()) {
                temp = temp.replaceAll(m2.group(2), m2.group(2) + "^");
                //System.out.println(temp + "\t;space expected between plant number and popular name/descriptor");
                listErrorsElite += temp + "\t;space expected between plant number and popular name/descriptor" + "#";
            }
            setListErrorsIRRI(listErrorsElite);
        }
    }
    private void fixedLine() {
        Pattern p = Pattern.compile("IR\\d{2}(N|F|L|T|U|K|W|H|J|D)\\d{3,}((H|R|A|B|S)?)");
        Matcher m = p.matcher(line);

        if (m.matches()) {
            //System.out.println("correct");
            //String tokens[] = t.tokenize(line);
            //t.stringTokens(tokens);
        } else {
            //System.out.println("\n>>String not properly formatted.. ");
            spacingFixedLine();
            //errorPatternFixedLine();
            Pattern p1 = Pattern.compile("IR\\d{2}(N|F|L|T|U|K|W|H|J|D)\\d{3,}((H|R|A|B|S)?)");
            Matcher m1 = p1.matcher(line);
            if (!m1.matches()) {
            	//System.out.println(line + "\t;string pattern not recognized ");
                listErrorsFixed += line + "\t;string pattern not recognized " + "#";
            }
        }setListErrorsIRRI(listErrorsFixed);
    }

    /**
     *
     */
    public void spacingFixedLine() {
        String temp = line;
        // Error Trapping: space is found in between the string
        Pattern p = Pattern.compile("(.)(\\s)(.)");
        Matcher m = p.matcher(line);
        if (m.find()) {
            //printGroup(m);
            temp = temp.replaceAll(m.group(0), m.group(1) + m.group(2) + "^" + m.group(3));
            //System.out.println(temp + "\t;unexpected space is found");
            listErrorsFixed += temp + "\t;unexpected space is found" + "#";
        }
        // Error Trapping: space is found at the end of the string
        temp = line;
        Pattern p1 = Pattern.compile("(.)(\\s)($)");
        Matcher m1 = p1.matcher(line);
        if (m1.find()) {
            //printGroup(m1);
            temp = temp.replaceAll(m1.group(0), m1.group(1) + m1.group(2) + "^" + m1.group(3));
            //System.out.println(temp + "\t;unexpected space is found");
            listErrorsFixed += temp + "\t;unexpected space is found" + "#";
        }

    }

    private void errorPatternFixedLine() {
        String temp = line;
        Pattern p = Pattern.compile("(IR\\d{2})(.)");
        //Pattern p = Pattern.compile("(IR\\d{2})|(N|F|L|T|U|K|W|H|J|D)(\\d{3,})|(((H|R|AA|B|S)?)$)");
        Matcher m = p.matcher(line);
        //System.out.println(m);
        if (m.find()) {
            //System.out.println(" no match found");
            //System.out.println(" m: " + m.groupCount());
            //temp = temp.replaceAll(m.group(0), m.group(1) + "^");
            ///System.out.println(temp + "\t;string pattern not recognized ");
            listErrorsFixed += temp + "\t;string pattern not recognized " + "#";
        }
        temp = line;
        Pattern p1 = Pattern.compile("(.)(N|F|L|T|U|K|W|H|J|D)(\\d{3,})");
        Matcher m1 = p1.matcher(line);
        if (m1.find()) {
            temp = temp.replaceAll(m1.group(0), m1.group(0) + "^");
            //System.out.println(temp + "\t;string pattern not recognized ");
            listErrorsFixed += temp + "\t;string pattern not recognized " + "#";
        }
        temp = line;
        Pattern p2 = Pattern.compile("(\\D)$");
        Matcher m2 = p2.matcher(line);
        if (m2.find()) {
            //System.out.println("there is a character [" + m2.group(0) + "]at the end of the line");
            Pattern p3 = Pattern.compile("(H|R|A|B|S)");
            Matcher m3 = p3.matcher(m2.group(0));
            if (!m3.find()) {
                temp = temp.replaceAll(m2.group(0), m2.group(0) + "^");
                //System.out.println(temp + "\t;string pattern not recognized ");
                listErrorsFixed += temp + "\t;string pattern not recognized " + "#";
            }
        }
        setListErrorsIRRI(listErrorsFixed);
    }

   /* private void fixFixedLine() {
        String answer;
        do {
            Scanner user_input = new Scanner(System.in);
            System.out.print("\n>>Fix String? (Y/N) ");
            answer = user_input.nextLine();

            if (answer.equalsIgnoreCase("Y")) {

                Pattern p = Pattern.compile("(.)(\\s)(.)");
                Matcher m = p.matcher(line);
                if (m.find()) {
                    //printGroup(m);
                    line = line.replaceAll(m.group(0), m.group(1) + m.group(3));
                    System.out.println("processed.");
                    System.out.println("string: " + line + "#");
                }
                Pattern p1 = Pattern.compile("(.)(\\s)($)");
                Matcher m1 = p1.matcher(line);
                if (m1.find()) {
                    //printGroup(m1);
                    line = line.replaceAll(m1.group(0), m1.group(1) + m1.group(3));
                    System.out.println("processed.");
                    System.out.println("string: " + line + "#");
                }
            }
        } while (answer.equalsIgnoreCase("Y") == false);
    }
    */

    /**
     * @return the listErrorsIRRI
     */
    public String getListErrorsIRRI() {
        return listErrorsIRRI;
    }

    /**
     * @param listErrorsIRRI the listErrorsIRRI to set
     */
    public void setListErrorsIRRI(String listErrorsIRRI) {
        this.listErrorsIRRI = listErrorsIRRI;
    }
}   // end class IRRI
