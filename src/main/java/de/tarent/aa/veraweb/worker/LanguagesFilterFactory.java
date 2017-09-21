package de.tarent.aa.veraweb.worker;

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
                "languages_b_e1"};
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
