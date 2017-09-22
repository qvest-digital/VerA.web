package org.evolvis.veraweb.onlinereg.rest;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
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
        
        Pattern p = Pattern.compile("&lt;(\\w+)&gt;");
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
