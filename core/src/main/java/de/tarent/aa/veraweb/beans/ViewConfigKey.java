package de.tarent.aa.veraweb.beans;

public enum ViewConfigKey {
    GUESTLIST_FUNCTION("guestListFunction", "false"),
    GUESTLIST_INTERNAL_ID("guestInternalId", "false"),
    GUESTLIST_CITY("guestListCity", "false"),
    GUESTLIST_PHONE("guestListPhone", "false"),
    GUESTLIST_LFDNR("guestLfdNr", "true"),
    GUESTLIST_RESERVE("guestReserve", "true"),
    GUESTLIST_COMPANY("guestCompany", "true"),
    GUESTLIST_CATEGORY("guestCategory", "false"),
    GUESTLIST_FIRSTNAME("guestFirstname", "true"),
    GUESTLIST_LASTNAME("guestLastname", "true"),
    GUESTLIST_EMAIL("guestEmail", "true"),
    GUESTLIST_MAINPERSON("guestMainPerson", "true"),
    GUESTLIST_PARTNER("guestPartner", "true"),

    PERSONLIST_STATE("personListState", "false"),
    PERSONLIST_INTERNAL_ID("personInternalId", "false"),
    PERSONLIST_ID("personID", "true"),
    PERSONLIST_WORKAREA("personWorkarea", "true"),
    PERSONLIST_FIRSTNAME("personFirstname", "true"),
    PERSONLIST_EMAIL("personEmail", "true"),
    PERSONLIST_CATEGORY("personCategory", "false"),
    PERSONLIST_LASTNAME("personLastname", "true"),
    PERSONLIST_FUNCTION("personFunction", "true"),
    PERSONLIST_COMPANY("personCompany", "true"),
    PERSONLIST_STREET("personStreet", "true"),
    PERSONLIST_POSTCODE("personPostCode", "true"),
    PERSONLIST_CITY("personCity", "true");

    public final String key;
    public String defaultValue;

    ViewConfigKey(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public static boolean containsKey(String key) {
        for (ViewConfigKey configKey : ViewConfigKey.values()) {
            if (configKey.key.equals(key)) {
                return true;
            }
        }
        return false;
    }
}
