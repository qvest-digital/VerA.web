package de.tarent.aa.veraweb.worker;
import de.tarent.aa.veraweb.beans.PersonSearch;
import de.tarent.aa.veraweb.utils.DatabaseHelper;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Where;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class LanguagesFilterFactory {
    public Clause createLanguagesFilter(PersonSearch personSearch) {
        final String[] values = personSearch.languages.split("\\W+");
        final String[] columns = {
          "languages_a_e1",
          "languages_b_e1" };
        Clause clause = null;
        for (String currentLanguage : values) {
            final Clause currentWhere = DatabaseHelper.getWhere("*" + currentLanguage + "*", columns);
            if (clause == null) {
                clause = currentWhere;
            } else {
                clause = Where.or(clause, currentWhere);
            }
        }

        return clause;
    }
}
