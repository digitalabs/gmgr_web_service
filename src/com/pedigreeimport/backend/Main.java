package com.pedigreeimport.backend;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author NCarumba
 */
public class Main {

    public String checkString(String line) {
        
        String list = "list \n";
        
        //System.out.println("\n starting CheckString method...");

        /*if (line.contains("/")) {   //checks it is BackCross or Single Cross
            if (line.contains("*")) {       //if it is BackCross
                new BackCross().main(line);
            } else {    //if it is Single Cross
                new SingleCross().main(line);
            }
        } else {
        */
            Pattern p = Pattern.compile("IR");
            Matcher m = p.matcher(line);

            if (m.lookingAt()) {    // Breeding Line is IRRI
                list= new IRRI().standardIRRI(line);
                //list = new IRRI().getListErrorsIRRI();
                
            }else{
            	list="";
            }
//            Pattern p1 = Pattern.compile("WA");
//            Matcher m1 = p1.matcher(line);
//
//            if (m1.lookingAt()) {   // Breeding line is WARDA
//                System.out.println("WARDA line");
//                new WARDA().standardWARDA(line);
//            }
        //}
       // System.out.println("list @main: " + list);
        return list;
    }
}   // end class Main
