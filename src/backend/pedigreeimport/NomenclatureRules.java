package backend.pedigreeimport;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *Entry point of the Nomenclature rules
 * @author Nikki G.Carumba
 */
public class NomenclatureRules {

	/**
	 * Checking For IRRI lines
	 * @param line
	 * @return
	 */
	public String checkString(String line) {

		String list = "";

		Pattern p = Pattern.compile("IR");
		Matcher m = p.matcher(line);

		if (m.lookingAt()) {    // Breeding Line is IRRI
			if(line.contains("(")){
				list+="";
			}else{
				list+= new IRRI().standardIRRI(line);
			}
		}
		return list;
	}
}   // end class NomenclatureRules
