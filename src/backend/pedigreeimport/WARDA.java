package backend.pedigreeimport;


import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;


/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author NCarumba
 */
public class WARDA {

    private static String A_WA = "WA"; // IRRI Line
    private static String A_STATION = "(B|S|R|T)"; // cross number
    private static String A_CN = "(\\d+)"; // cross number
    private String line;
    Tokenize t = new Tokenize();
    private String tokens[];

    String standardWARDA(String aline) throws MiddlewareQueryException, IOException {
        line = aline;
        Pattern p = Pattern.compile(A_WA + A_STATION + "(\\d+" + "((-" + A_CN + "){0,}))?");
        Matcher m = p.matcher(line);
        if (m.matches()) {
            System.out.println(line);
            System.out.println(" correct");
            tokens = t.tokenize(line);
            t.stringTokens(tokens);
        } else {
            System.out.println("\n>>String not properly formatted.. ");
            checkErrors(line);
        }
        return line;
    }

    private void checkErrors(String line) {
        tokens = t.tokenize(line);
        String temp = line;

        for (int i = 0; i < tokens.length; i++) {
            //toFix.add(new ArrayList<String>());
            checkErrorSpacing(i, "", temp, tokens);
            //checkErrorPattern(i, temp);

            //fixString();
            //correctFixedLine();
        }
    }

    String checkErrorSpacing(int i, String line, String temp, String[] tokens) {

        String temp2 = temp;
        Pattern p1 = Pattern.compile("(^\\s+)(.)");
        Matcher m1 = p1.matcher(tokens[i]);
        if (m1.lookingAt()) {
            temp2 = temp2.replaceAll(m1.group(0), m1.group(1) + "^" + m1.group(2));
            System.out.println(line + temp2 + "\t;unexpected space(s) is found athe beginning of the token");
        }

        temp2 = temp;
        Pattern p = Pattern.compile(A_STATION + "(\\s+)(\\d+)");
        Matcher m = p.matcher(tokens[i]);
        if (m.find()) {
            temp2 = temp2.replaceAll(m.group(2), m.group(2) + "^");
            System.out.println(line + temp2 + "\t;unexpected space(s) between Station and plant number");
        }

        temp2 = temp;
        Pattern p2 = Pattern.compile("(.+)(\\s+)($)");    //space/s at the end of the string or before dash
        Matcher m2 = p2.matcher(tokens[i]);
        if (m2.find()) {
            temp2 = temp2.replaceAll(m2.group(0), m2.group(1) + m2.group(2) + "^");
            System.out.println(line + temp2 + "\t;unexpected space(s) is found athe end of the token");
        }
        //System.out.println("temp: "+ temp);        
        return temp;
    }

    void fixString() {
        String answer, tempToken;
        do {
            Scanner user_input = new Scanner(System.in);
            System.out.print("\n>>Fix String? (Y/N) ");
            answer = user_input.nextLine();

            if (answer.equalsIgnoreCase("Y")) {
                for (int i = 0; i < tokens.length; i++) {
                    Pattern p = Pattern.compile(A_STATION + "(\\s+)(\\d+)");
                    Matcher m = p.matcher(line);
                    if (m.find()) {
                        //printGroup(m);
                        tempToken = tokens[i];
                        tokens[i] = tokens[i].replace(m.group(0), m.group(1) + m.group(2));
                        line = line.replace(tempToken, tokens[i]);
                        tokens = t.tokenize(line);
                    }
                    Pattern p1 = Pattern.compile("(^\\s+)(.)");
                    Matcher m1 = p1.matcher(line);
                    if (m1.find()) {
                        //printGroup(m1);
                        tempToken = tokens[i];
                        tokens[i] = tokens[i].replace(m1.group(0), m1.group(1));
                        line = line.replace(tempToken, tokens[i]);
                        tokens = t.tokenize(line);
                    }
                }
            }
        } while (answer.equalsIgnoreCase("Y") == false);
    }

    public String checkErrorPattern( String temp2, String temp, String token) {
        Pattern p11 = Pattern.compile("WA(B|S|R|T)\\s(\\d*((-\\d+){0,}))?");
        Matcher m11 = p11.matcher(token);

        if (!m11.matches()) {
            temp = temp.replaceAll(token, token + "^");
            System.out.println(temp2 + temp + "\t;string pattern not recognized (WARDA) ");
        }
        return temp;
    }

    private static void printGroup(Matcher m) {
        System.out.println("Group count: " + m.groupCount());
        int i;
        for (i = 0; i <= m.groupCount(); i++) {
            System.out.println(i + " : " + m.group(i));
        }
    }
}
