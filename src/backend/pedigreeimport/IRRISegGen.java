package backend.pedigreeimport;

import java.util.Scanner;
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
					//System.out.println("here inside the IRRI sel");
				}
				//printGroup(m);
				tokens = t.tokenize(m.group(2));    // WARDA
				for (int i = 0; i < tokens.length; i++) {
					new WARDA().checkErrorSpacing(i, m.group(1), m.group(2), tokens);
				}
				//toFix();
			}
		} else {

			for (int i = 0; i < tokens.length; i++) {
				checkErrorSpacing(i, temp);
				checkErrorPattern(i, temp);
			}
			//System.out.println("listErrors:" + listErrors);

			//             toFix(tokens,line);
			//            correctFixedLine(tokens,line);
		}
		//System.out.println(toFix);
	}

	/**
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
	 *
	 * @param i is the integer pointer of the token of the string
	 * @param temp a temporary variable to hold the string line
	 */
	public void checkErrorPattern(int i, String temp) {   //ERROR TRAPPING: pattern/s not recognized, unrecognized codes
		temp = line;
		Pattern p11 = Pattern.compile("(\\d+)|(IR\\s\\d+)|((UBN|AJY|SRN|CPA|KKN|PMI|SKN|SRN|SDO)\\s\\d+)|(\\d{0,4})B|R|AC|(C\\d+)|(\\d+MP)");
		Matcher m11 = p11.matcher(tokens[i]);

		if (!m11.matches()) {
			temp = temp.replaceAll(tokens[i], tokens[i] + "^");
			//System.out.println(temp + "\t;string pattern not recognized. ");
			listErrors += temp + "\t;string pattern not recognized. " + "#";
		}
	}

	public void checkErrorPattern() {   //ERROR TRAPPING: pattern/s not recognized, unrecognized codes
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

	public void correctLine(String[] tokens,String line) {   // method to check if the String s in the correct format
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

	void toFix(String[] tokens,String line) {
		String answer;
		do {
			Scanner user_input = new Scanner(System.in);
			//System.out.print("\n>>Fix String? (Y/N) ");
			answer = user_input.nextLine();

			if (answer.equalsIgnoreCase("Y")) {
				// System.out.println(line + "#");
				tokens = t.tokenize(line);
				String temp = line;
				if (line.contains("WA")) {
					Pattern p = Pattern.compile("(.+)(\\s+WA(.+))");  // to divide the line into two groups
					Matcher m = p.matcher(line);
					if (m.find()) {
						//printGroup(m);
						tokens = t.tokenize(m.group(1));    // from IRRI selection
						for (int i = 0; i < tokens.length; i++) {
							fixString();
						}
						//System.out.println("processed.");
						//System.out.println(">>line: " + line);
						//printGroup(m);
						tokens = t.tokenize(line);    // WARDA
						Matcher m1 = p.matcher(line);
						if (m1.find()) {
							for (int i = 0; i < tokens.length; i++) {
								temp = new WARDA().checkErrorPattern(m.group(1) + "-", m.group(2), tokens[i]);
								//System.out.println(">>here: " + temp);
							}
						}

					}
				} else {
					//fixString();
					System.out.println("processed.");
					tokens = t.tokenize(line);
					checkErrorPattern();
				}
			} else if (answer.equalsIgnoreCase("N")) {
				System.exit(1);
			}
		} while (answer.equalsIgnoreCase("Y") == false);

	}

	private void fixString() {  //method to fix all the errors found in the String
		String tempToken;
		//System.out.println("toFix():"+line);
		/*for (int i = 0; i <tokens.length; i++) {
            System.out.println(tokens[i]);
        }*/
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
	}

	public static void printGroup(Matcher m) {
		System.out.println("Group count: " + m.groupCount());
		int i;
		for (i = 0; i <= m.groupCount(); i++) {
			System.out.println(i + " : " + m.group(i));
		}
	}

	/**
	 * @return the listErrors
	 */
	public String getListErrors() {
		return listErrors;
	}
}   // end class IRRISegGen
