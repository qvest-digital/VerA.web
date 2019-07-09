package org.evolvis.veraweb.onlinereg.rest;
import org.evolvis.veraweb.common.Placeholder;
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

    PlaceholderSubstitution(Person person) {
        this(createMap(person));
    }

    public static Map<String, String> createMap(Person person) {
        final HashMap<String, String> map = new HashMap<>();
        map.put(Placeholder.FIRSTNAME.key, person.getFirstname_a_e1());
        map.put(Placeholder.LASTNAME.key, person.getLastname_a_e1());
        map.put(Placeholder.SALUTATION.key, person.getSalutation_a_e1());
        map.put(Placeholder.TITLE.key, person.getTitle_a_e1());
        map.put(Placeholder.FUNCTION.key, person.getFunction_a_e1());
        map.put(Placeholder.COMPANY.key, person.getCompany_a_e1());
        map.put(Placeholder.STREET.key, person.getStreet_a_e1());
        map.put(Placeholder.ZIPCODE.key, person.getZipcode_a_e1());
        map.put(Placeholder.CITY.key, person.getCity_a_e1());
        map.put(Placeholder.COUNTRY.key, person.getCountry_a_e1());
        map.put(Placeholder.POSTALBOX_ZIPCODE.key, person.getPoboxzipcode_a_e1());
        map.put(Placeholder.POSTALBOX.key, person.getPobox_a_e1());
        map.put(Placeholder.SUFFIX_1.key, person.getSuffix1_a_e1());
        map.put(Placeholder.SUFFIX_2.key, person.getSuffix2_a_e1());
        map.put(Placeholder.PHONE.key, person.getFon_a_e1());
        map.put(Placeholder.FAX.key, person.getFax_a_e1());
        map.put(Placeholder.MOBILE.key, person.getMobil_a_e1());
        map.put(Placeholder.EMAIL.key, person.getMail_a_e1());
        map.put(Placeholder.URL.key, person.getUrl_a_e1());
        map.put(Placeholder.REMARK.key, person.getNote_a_e1());
        map.put(Placeholder.HINT_FOR_HOST.key, person.getNotehost_a_e1());
        map.put(Placeholder.HINT_FOR_ORGA.key, person.getNoteForOrga());
        map.put(Placeholder.INTERNAL_ID.key, person.getInternalId());
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
        if (map.containsKey(name)) {
            String val = map.get(name);
            return val == null ? "" : val;
        }
        //return null only to signal
        return null;
    }
}
