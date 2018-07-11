package org.evolvis.veraweb.export;

import org.springframework.util.StringUtils;

public enum ValidExportFilter {

    CATEGORY_ID_FILTER("filterCategoryId", "[0-9]{1,20}", "g.fk_category = ?"),
    SEARCHWORD_FILTER("filterWord", "[0-9a-zA-Z]{1,50}", "(p.firstname_a_e1 = '?' OR p.lastname_a_e1 = '?')"),//TODO expand and modify to expected search word filtering
    INVITATIONSTATUS_FILTER("filterInvStatus", "[0-9]", "g.invitationstatus = ?"),
    RESERVE_FILTER("filterReserve", "[0-1]", "g.reserve = ?");

    public final String key;
    public final String pattern;
    public final String dbPath;

    ValidExportFilter(String key, String pattern, String dbPath) {
        this.key = key;
        this.pattern = pattern;
        this.dbPath = dbPath;
    }

    public static ValidExportFilter valueOfKey(String key) {
        if (key != null) {
            for (ValidExportFilter validExportFilter : ValidExportFilter.values()) {
                if (validExportFilter.key.equals(key)) {
                    return validExportFilter;
                }
            }
        }
        return null;
    }

    public static boolean isValidFilterSetting(String key, String value) {
        if (!(StringUtils.isEmpty(key) || StringUtils.isEmpty(value))) {
            ValidExportFilter validExportFilter = valueOfKey(key);
            if (validExportFilter != null) {
                return value.matches(validExportFilter.pattern);
            }
        }
        return false;
    }

    public static String buildDBPathPartial(String key, String value) {
        if (!(StringUtils.isEmpty(key) || StringUtils.isEmpty(value))) {
            ValidExportFilter validExportFilter = valueOfKey(key);
            return validExportFilter.dbPath.replaceAll("\\?", value);
        }
        return null;
    }
}
