package backend.pedigreeimport;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks for unrecognized patterns and incorrect spacing in names for Segregating line
 * @author Nikki G. Carumba
 */
public class IRRISegGen {

	private static String A_IR = "(IR)"; // IRRI Line
	private static String A_SPACE = "(\\s)";   // white space
	private static String A_PLANT_NO = "(\\d+)";  // plant number
	private static String A_LOC = "(UBN|AJY|SRN|CPA|KKN|PMI|SKN|SRN|SDO)";   //location
	private static String A_SEL_NO = "(\\d+)"; //selection number
	private static String A_DASH = "(-)";  //dash
	private static String A_MP = "(\\d+MP)";   //mapping population followed by the plant number
	private static String A_BM = "((\\d{0,4})B|R|AC|(C\\d+))"; // breeding methods: “B�? - bulk, “R�? - rapid generation advance or single seed descent, “AC�? - anther culture. “C�? - for composite populations; a succeeding number indicates the specific cycle of composite
	private String line;
	private String tokens[];
	private Pattern p = Pattern.compile(A_IR + A_PLANT_NO + "(\\s+)?"); // no space between IR and plant number
	private Pattern p1 = Pattern.compile("(^\\s+)(.)"); //space/s after dash or at the beginiing of the string
	private Pattern p2 = Pattern.compile(A_LOC + A_SEL_NO); // no space between the location number and the selection number
	private Pattern p3 = Pattern.compile("(\\d{0,5})(\\s+)(B)"); //space/s between the bulk number and the bulk code
	private Pattern p4 = Pattern.compile("(\\d+)(\\s+)(MP)");    ///space/s between the mapping population and the plant number
	private Pattern p5 = Pattern.compile("(C)(\\s+)(\\d+)"); //space/s between the composite population code and the plant number
	private Pattern p6 = Pattern.compile("(.)(\\s+)($)");    //space/s at the end of the string or before dash
	Tokenize t = new Tokenize();
	private String listErrors = "";
	//private List<String> listErrors = new ArrayList<String>();

	/**
	 *
	 * @param aline String inputted
	 */
	public void checkErrors(String aline) { // method to check errors in spacing, codes and pattern

		line = aline;
		tokens = t.tokenize(line);
		String temp = line;

		if (line.contains("WA")) {
			Pattern p = Pattern.compile("(.+)(\\s*WA(.))");  // to divide the line into two groups
			//(.+)(\\s+WA(B|S|R|T)\\s*(\\d*\\s*((-\\s*\\d+\\s*){0,}))?)
			Matcher m = p.matcher(line);
			if (m.find()) {
				//printGroup(m);
				tokens = t.tokenize(m.group(1));    // from IRRI selection
				for (int i = 0; i < tokens.length; i++) {
					checkErrorSpacing(i, m.group(1));
					checkErrorPattern(i, m.group(1));
				}
				tokens = t.tokenize(m.group(2));    // WARDA
				for (int i = 0; i < tokens.length; i++) {
					new WARDA().checkErrorSpacing(i, m.group(1), m.group(2), tokens);
				}
			}
		} else {

			for (int i = 0; i < tokens.length; i++) {
				checkErrorSpacing(i, temp);
				checkErrorPattern(i, temp);
			}
		}
	}

	/**
	 * Check errors in spacing
	 * 
	 * @param i Integer pointer of the token of the string
	 * @param temp temporary String holder of the string inputted line
	 */
	private String checkErrorSpacing(int i, String temp) {    // method to check spacing 
		// Error Trapping: no space between IR and plant number eg. IR^88888
		String tempToken;
		Matcher m = p.matcher(tokens[i]);
		if (m.matches()) {
			//System.out.println(m);
			temp = line;
			tempToken = tokens[i];
			tempToken = tempToken.replaceAll(m.group(1), m.group(1) + "^");
			temp = temp.replace(tokens[i], tempToken);
			//System.out.println(temp + "\t;space expected between IR and plant number");
			listErrors += temp + "\t;space expected between IR and plant number" + "#";
		}
		//Error Trapping: space is found at the beginning of the string
		Matcher m1 = p1.matcher(tokens[i]);
		if (m1.lookingAt()) {
			//printGroup(m1);
			temp = line;
			tempToken = tokens[i];
			tempToken = tempToken.replaceAll(m1.group(0), m1.group(1) + "^" + m1.group(2));
			temp = temp.replace("-" + tokens[i], "-" + tempToken);
			//System.out.println(temp + "\t;unexpected space is found at the start of the token");
			listErrors += temp + "\t;unexpected space is found at the start of the token" + "#";
		}
		// Error Trapping: no space between location code and plant number  eg. KKN^7879
		Matcher m2 = p2.matcher(tokens[i]);
		if (m2.matches()) {
			//printGroup(m2);
			temp = line;
			tempToken = tokens[i];
			tempToken = tempToken.replaceAll(m2.group(1), m2.group(1) + "^");
			temp = temp.replace(tokens[i], tempToken);
			//System.out.println(temp + "\t;space expected between location code and plant number");
			listErrors += temp + "\t;space expected between location code and plant number" + "#";
		}
		// Error Trapping: space is found between number of bulks and the bulk code eg.4 ^B
		Matcher m3 = p3.matcher(tokens[i]);
		if (m3.matches()) {
			//printGroup(m3);
			temp = line;
			tempToken = tokens[i];
			tempToken = tempToken.replaceAll(m3.group(0), m3.group(1) + m3.group(2) + "^" + m3.group(3));
			temp = temp.replace(tokens[i], tempToken);
			//System.out.println(temp + "\t;unexpected space is found between number of bulks and the bulk code");
			listErrors += temp + "\t;unexpected space is found between number of bulks and the bulk code" + "#";
		}
		// Error Trapping: space is found between plant number and th mapping population code eg. 4 ^MP
		Matcher m4 = p4.matcher(tokens[i]);
		if (m4.matches()) {
			//printGroup(m4);
			temp = line;
			tempToken = tokens[i];
			tempToken = tempToken.replaceAll(m4.group(0), m4.group(1) + m4.group(2) + "^" + m4.group(3));
			temp = temp.replace(tokens[i], tempToken);
			//System.out.println(temp + "\t;unexpected space is found between plant number and th mapping population code");
			listErrors += temp + "\t;unexpected space is found between plant number and th mapping population code" + "#";
		}
		// Error Trapping: space is found between plant number and th mapping population code eg. C ^88
		Matcher m5 = p5.matcher(tokens[i]);
		if (m5.matches()) {
			//printGroup(m5);
			temp = line;
			tempToken = tokens[i];
			tempToken = tempToken.replaceAll(m5.group(0), m5.group(1) + m5.group(2) + "^" + m5.group(3));
			temp = temp.replace(tokens[i], tempToken);
			//System.out.println(temp + "\t;unexpected space is found between plant number and the composite population code");
			listErrors += temp + "\t;unexpected space is found between plant number and the composite population code";
		}
		//Error Trapping: space is found at the end of the string
		Matcher m6 = p6.matcher(tokens[i]);
		if (m6.find()) {
			//printGroup(m6);
			temp = line;
			tempToken = tokens[i];
			tempToken = tempToken.replace(m6.group(0), m6.group(1) + m6.group(2) + "^");
			temp = temp.replace(tokens[i], tempToken);
			//System.out.println(temp + "\t;unexpected space is found at the end of the token");
			listErrors += temp + "\t;unexpected space is found at the end of the token" + "#";
		}
		return temp;
	}

	/**
	 * Checks for pattern/s not recognized, unrecognized codes
	 * @param i is the integer pointer of the token of the string
	 * @param temp a temporary variable to hold the string line
	 */
	public void checkErrorPattern(int i, String temp) {  
		temp = line;
		Pattern p11 = Pattern.compile("(\\d+)|(IR\\s\\d+)|((UBN|AJY|SRN|CPA|KKN|PMI|SKN|SRN|SDO)\\s\\d+)|(\\d{0,4})B|R|AC|(C\\d+)|(\\d+MP)");
		Matcher m11 = p11.matcher(tokens[i]);

		if (!m11.matches()) {
			temp = temp.replaceAll(tokens[i], tokens[i] + "^");
			//System.out.println(temp + "\t;string pattern not recognized. ");
			listErrors += temp + "\t;string pattern not recognized. " + "#";
		}
	}

	/**
	 * Checks for unrecognized patterns through all tokens
	 * ERROR TRAPPING: pattern/s not recognized, unrecognized codes
	 */
	public void checkErrorPattern() {  
		for (int i = 0; i < tokens.length; i++) {
			String temp = line;
			Pattern p11 = Pattern.compile("(\\d+)|(IR\\s\\d+)|((UBN|AJY|SRN|CPA|KKN|PMI|SKN|SRN|SDO)\\s\\d+)|(\\d{0,4})B|R|AC|(C\\d+)|(\\d+MP)");
			Matcher m11 = p11.matcher(tokens[i]);
			if (!m11.matches()) {
				temp = temp.replaceAll(tokens[i], tokens[i] + "^");
				listErrors=temp + "\t;string pattern not recognized ";
				//System.out.println(temp + "\t;string pattern not recognized ");
			}
		}
	}

	/**
	 * Checks if the String s in the correct format
	 * @param tokens parsed germplasm name
	 * @param line germplasm name
	 */
	public void correctLine(String[] tokens,String line) { 
		Pattern p12 = Pattern.compile(A_IR + A_SPACE + A_PLANT_NO + "(" + A_DASH + "(((" + A_LOC + A_SPACE + A_SEL_NO + ")|" + A_SEL_NO + ")|" + A_BM + "|" + A_MP + ")){1,5}");
		//Pattern p2 = Pattern.compile("IR\\s\\d+(-((((UBN|AJY|SRN|CPA|KKN|PMI|SKN|SRN)\\s\\d+)|\\d+)|((\\d{0,4}B)|R|AC|(C\\d+))|(\\d+MP))){1,5}");
		Matcher m = p12.matcher(line);
		if (!m.matches()) {
			//System.out.println(" correct");
			//tokens = t.tokenize(line);
			//t.stringTokens(tokens);
			listErrors=line + "\t;string pattern not recognized ";
		}
	}

	/**
	 * @return the listErrors
	 */
	public String getListErrors() {
		return listErrors;
	}
}   // end class IRRISegGen
