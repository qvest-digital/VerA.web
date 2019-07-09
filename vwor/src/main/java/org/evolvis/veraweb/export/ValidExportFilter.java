package org.evolvis.veraweb.export;
import org.springframework.util.StringUtils;

public enum ValidExportFilter {

    CATEGORY_ID_FILTER("filterCategoryId", "[0-9]{1,20}", "g.fk_category = ?"),
    SEARCHWORD_FILTER("filterWord", "[0-9a-zA-Z ]{1,50}", null),
    INVITATIONSTATUS_FILTER("filterInvStatus", "[0-9]", null),
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
            if (validExportFilter.dbPath != null) {
                return validExportFilter.dbPath.replaceAll("\\?", value);
            }
            if (ValidExportFilter.INVITATIONSTATUS_FILTER.equals(validExportFilter))  {
                return getInvitationStatusFilter(value);
            }
            if (ValidExportFilter.SEARCHWORD_FILTER.equals(validExportFilter)) {
                return getKeywordFilter(value);
            }
        }
        return null;
    }

    public static String getKeywordFilter(String words){
        StringBuilder queryPart = new StringBuilder();
        if (words != null && !words.trim().isEmpty() && isValidFilterSetting(SEARCHWORD_FILTER.key, words)) {
            final String[] wordsSplit = words.split("[^\\p{L}\\p{Nd}]+");
            for (int i = 0; i < wordsSplit.length; i++) {
                if (i > 0 && i < wordsSplit.length){
                    queryPart.append(" AND ");
                }
                queryPart.append("g.keywords LIKE '%").append(wordsSplit[i]).append("%'");
            }
        }
        return queryPart.toString();
    }

    /**
     * Creates a filter based on the status selected in the UI.
     *
     * See also {@link de.tarent.aa.veraweb.beans.GuestSearch#addGuestListFilter(de.tarent.dblayer.sql.clause.WhereList)}
     *
     * @param value the value selected in the UI status dropdown
     * @return the query filter
     */
    private static String getInvitationStatusFilter(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        switch (Integer.parseInt(value)) {
            case 1:
                // nur Offen
                return  // Mit Partner
                        "((invitationtype = 1 AND (invitationstatus IS NULL OR invitationstatus=0 OR " +
                                "invitationstatus_p IS NULL OR invitationstatus_p=0)) OR " +
                                // Ohne Partner
                                "(invitationtype = 2 AND (invitationstatus IS NULL OR invitationstatus=0)) OR " +
                                // Nur Partner
                                "(invitationtype = 3 AND (invitationstatus_p IS NULL OR invitationstatus_p=0)))";
            case 2:
                // nur Zusagen
                return  // Mit Partner
                        "((invitationtype = 1 AND (invitationstatus = 1 OR invitationstatus_p = 1)) OR " +
                                // Ohne Partner
                                "(invitationtype = 2 AND invitationstatus = 1) OR " +
                                // Nur Partner
                                "(invitationtype = 3 AND invitationstatus_p = 1))";
            case 3:
                // nur Absagen
                return  // Mit Partner
                        "((invitationtype = 1 AND (invitationstatus = 2 OR invitationstatus_p = 2)) OR " +
                                // Ohne Partner
                                "(invitationtype = 2 AND invitationstatus = 2) OR " +
                                // Nur Partner
                                "(invitationtype = 3 AND invitationstatus_p = 2))";
            case 4:
                // nur Teilnahmen
                return  // Mit Partner
                        "((invitationtype = 1 AND (invitationstatus = 3 OR invitationstatus_p = 3)) OR " +
                                // Ohne Partner
                                "(invitationtype = 2 AND invitationstatus = 3) OR " +
                                // Nur Partner
                                "(invitationtype = 3 AND invitationstatus_p = 3))";
        }
        return null;
    }
}
