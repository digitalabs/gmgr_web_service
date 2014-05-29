package backend.pedigreeimport;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *Checks for unrecognized patterns and spacing for IRRI designated germplasm
 * @author Nikki G. Carumba
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
    private static String A_BM = "((\\d{0,4}B)|\\d*R|AC|(C\\d+))"; // breeding methods: “B�? - bulk, “R�? - rapid generation advance or single seed descent, “AC�? - anther culture. “C�? - for composite populations; a succeeding number indicates the specific cycle of composite
    private String line;
    IRRISegGen sg = new IRRISegGen();
    Tokenize t = new Tokenize();
    private String errorsIRRI = "";
    private String errorsFixed = "";
    //private String listErrorsSegGen = "";
    private String errorsReleased = "";
    private String errorsElite = "";

    /**
     * Entry point of IRRI breeding line
     * It can be a segragating, elite, released, or fixed line
     *  
     * @param a_line germplasm name
     * @return errorsIrri list of errors
     */
    public String standardIRRI(String a_line) {

        line = a_line;
        //System.out.print("Breeding Line: IRRI");

        Pattern p = Pattern.compile(A_DASH);
        Matcher m = p.matcher(line);

        if (m.find()) {    //String is a segragating line
           // System.out.println(" (segragating generations)");
            segGen();
        } else {    //String is a either an IRRI elite line or a fixed line
            
            Pattern p2 = Pattern.compile("IR(\\s+)?\\d+(\\s+)?");
            Matcher m2 = p2.matcher(line);
            
            Pattern p1 = Pattern.compile("IRRI");
            Matcher m1 = p1.matcher(line);

            if (m1.lookingAt()) {//String is an IRRI elite line
                eliteLine();
            }else if(m2.matches()) {//String is an IRRI released line
                releasedLine();
            } else {//String is an IRRI fixed line
                fixedLine();
            }
        }
        
        return errorsIRRI;
    }

    /**
     * Entry point of the Segregating line
     */
    private void segGen() {

        Pattern p = Pattern.compile(A_IR + A_SPACE + A_PLANT_NO + "(" + A_DASH + "(((" + A_LOC + A_SPACE + A_SEL_NO + ")|" + A_SEL_NO + ")|" + A_BM + "|" + A_MP + ")){1,5}");
        Matcher m = p.matcher(line);
        if (m.matches()) {
            //System.out.println(" correct");
        } else {
            //System.out.println("\n>>String not properly formatted.. ");
            sg.checkErrors(line);
            //System.out.println(sg.getListErrors());
            errorsIRRI = sg.getListErrors();
        }
    }

    /**
     * Check errors in naming a released line
     */
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
                errorsReleased += temp + "\t;space expected between IR and plant number" + "#";
            }
            Pattern p2 = Pattern.compile("(IR)(\\d+)(\\s+)");
            Matcher m2 = p2.matcher(line);
            if (m2.matches()) {
                temp = temp.replaceAll(m2.group(3), m2.group(3) + "^");
                //System.out.println(temp + "\t;unexpected space/s found");
                errorsReleased += temp + "\t;unexpected space/s found" + "#";
            }
            setListErrorsIRRI(errorsReleased);
        }
    }

    /**
     * Check errors in naming a elite line
     */
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
                errorsElite += temp + "\t;space expected between IRRI and plant number" + "#";
            }

            Pattern p1 = Pattern.compile("(\\d{3,})(\\s+)");
            Matcher m1 = p1.matcher(line);
            if (m1.find()) {
                //printGroup(m);
                temp = line;
                //System.out.println("m: " + m5);
                temp = temp.replaceAll(m1.group(0), m1.group(1) + "^");
                //System.out.println(temp + "\t;unexpected space is found at the end of the token");
                errorsElite += temp + "\t;unexpected space is found at the end of the token" + "#";
            }
            Pattern p3 = Pattern.compile("(IRRI)(\\d{3,})(\\s(.+))");
            Matcher m3 = p3.matcher(line);
            //System.out.println(m2);
            if (m3.find()) {
                temp = temp.replaceAll(m2.group(2), m2.group(2) + "^");
                //System.out.println(temp + "\t;space expected between plant number and popular name/descriptor");
                errorsElite += temp + "\t;space expected between plant number and popular name/descriptor" + "#";
            }
            setListErrorsIRRI(errorsElite);
        }
    }
    /**
     * Checks unrecognized patterns in naming a fixed line
     */
    private void fixedLine() {
    	
        Pattern p = Pattern.compile("IR\\d{2}(N|F|L|T|U|K|W|H|J|D|A|C|M)\\d{3,}((H|R|A|B|S)?)");
        Matcher m = p.matcher(line);
        Pattern p2 = Pattern.compile("IR\\s.+\\(.+\\)");
        Matcher m2 = p2.matcher(line);

        if (m2.matches()) {
        	
        }else if (m.matches()) {
            //System.out.println("correct");
            //String tokens[] = t.tokenize(line);
            //t.stringTokens(tokens);
        } else {
            //System.out.println("\n>>String not properly formatted.. ");
            spacingFixedLine();
            //errorPatternFixedLine();
            Pattern p1 = Pattern.compile("IR\\d{2}(N|F|L|T|U|K|W|H|J|D|A|M|C)?\\d{3,}((H|R|A|B|S)?)");
            Matcher m1 = p1.matcher(line);
            if (!m1.matches()) {
            	//System.out.println(line + "\t;string pattern not recognized ");
                errorsFixed += line + "\t;fixed line string pattern not recognized " + "#";
            }
        }setListErrorsIRRI(errorsFixed);
    }

    /**
     * Check errors in spacing in a released line
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
            errorsFixed += temp + "\t;unexpected space is found" + "#";
        }
        // Error Trapping: space is found at the end of the string
        temp = line;
        Pattern p1 = Pattern.compile("(.)(\\s)($)");
        Matcher m1 = p1.matcher(line);
        if (m1.find()) {
            //printGroup(m1);
            temp = temp.replaceAll(m1.group(0), m1.group(1) + m1.group(2) + "^" + m1.group(3));
            //System.out.println(temp + "\t;unexpected space is found");
            errorsFixed += temp + "\t;unexpected space is found" + "#";
        }

    }

    /**
     * @return the listErrorsIRRI
     */
    public String getListErrorsIRRI() {
        return errorsIRRI;
    }

    /**
     * @param listErrorsIRRI the listErrorsIRRI to set
     */
    public void setListErrorsIRRI(String listErrorsIRRI) {
        this.errorsIRRI = listErrorsIRRI;
    }
}   // end class IRRI
