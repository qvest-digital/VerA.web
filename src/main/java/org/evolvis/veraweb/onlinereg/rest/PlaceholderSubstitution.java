package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Person;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderSubstitution {

    /* TODO:  
     * this is a first, naive take at this.
     * We probably want to think this over.
     */
    
    final private Map<String, String> map;

    public PlaceholderSubstitution(Person person) {
        this(createMap(person));

    }

    private static Map<String, String> createMap(Person person) {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("firstname", person.getFirstname_a_e1());
        map.put("lastname", person.getLastname_a_e1());
        map.put("salutation", person.getSalutation_a_e1());
        map.put("title", person.getTitle_a_e1());
        map.put("function", person.getFunction_a_e1());
        map.put("company", person.getCompany_a_e1());
        map.put("street", person.getStreet_a_e1());
        map.put("zipcode", person.getZipcode_a_e1());
        map.put("city", person.getCity_a_e1());
        map.put("country", person.getCountry_a_e1());
        map.put("poboxzipcode", person.getPoboxzipcode_a_e1());
        map.put("pobox", person.getPobox_a_e1());
        map.put("suffix1",person.getSuffix1_a_e1());
        map.put("suffix2", person.getSuffix2_a_e1());
        map.put("phone", person.getFon_a_e1());
        map.put("fax", person.getFax_a_e1());
        map.put("mobile", person.getMobil_a_e1());
        map.put("email", person.getMail_a_e1());
        map.put("url", person.getUrl_a_e1());
        return map;
    }

    public PlaceholderSubstitution(Map<String, String> map) {
        this.map = map;
    }

    public String apply(String text) {
        
        Pattern p = Pattern.compile("<(\\w+)>");
        Matcher matcher = p.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            final String name = matcher.group(1);
            final String replacement = lookup(name);
            matcher.appendReplacement(sb, replacement == null ? matcher.group() : replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String lookup(String name) {
        if(map.containsKey(name)){
            String val = map.get(name);
            return val == null ? "" : val; 
        }
        //return null only to signal
        return null;
    }

}
