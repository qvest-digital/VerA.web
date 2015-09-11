package de.tarent.aa.veraweb.utils;

import java.util.Map;
/*
 * Language Helper was implemented to create a general message when deleting, creating or updating any entity
 * This utility is called from velocity through sending it to the OctopusContext
 */

public class LanguageHelper {
	
	public String createMessage (String entity, String action, String count, Map<String,String> placeholderWithTranslation) {
		
		String message;
		// singular or plural
		String singularOrPluralOrNone;
		if(count.equals("0")) {
			singularOrPluralOrNone = "N";
		} else if(count.equals("1")) {
			singularOrPluralOrNone = "S";
		} else {
		singularOrPluralOrNone = "P";
		}
		
		String placeholdername = "GM_"+ entity + "_" + action + "_" + singularOrPluralOrNone;
		if(singularOrPluralOrNone.equals("P")) {
			message = String.format(placeholderWithTranslation.get(placeholdername), count);
		} else {
			message = placeholderWithTranslation.get(placeholdername);
		}
//		return placeholdername;
		return message;
	}
	
	public String give (String enter) {
		return enter;
	}

}
