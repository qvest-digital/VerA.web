package org.evolvis.veraweb.common;
import java.util.HashMap;
import java.util.Map;

public enum Placeholder {
    SALUTATION("salutation"),
    FIRSTNAME("firstname"),
    LASTNAME("lastname"),
    TITLE("title"),
    FUNCTION("function"),
    COMPANY("company"),
    STREET("street"),
    ZIPCODE("zipcode"),
    CITY("city"),
    COUNTRY("country"),
    POSTALBOX_ZIPCODE("poboxzipcode"),
    POSTALBOX("pobox"),
    SUFFIX_1("suffix1"),
    SUFFIX_2("suffix2"),
    PHONE("phone"),
    FAX("fax"),
    MOBILE("mobile"),
    EMAIL("email"),
    URL("url"),
    HINT_FOR_HOST("hintforhost"),
    HINT_FOR_ORGA("hintfororga"),
    REMARK("remark"),
    COMPLETE_SALUTATION_1("salutationComplete1"),
    COMPLETE_SALUTATION_2("salutationComplete2"),
    ENVELOPE_ONE("envelope1"),
    INTERNAL_ID("internalID");

    public String key;

    Placeholder(String key) {
        this.key = key;
    }

    public static Map toStringMap() {
        Map<String, String> map = new HashMap<>();
        for(Placeholder placeholder : Placeholder.values()){
            map.put(placeholder.toString(),placeholder.key);
        }
        return map;
    }
}
