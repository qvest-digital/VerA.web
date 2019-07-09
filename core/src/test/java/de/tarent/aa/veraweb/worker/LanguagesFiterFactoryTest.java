package de.tarent.aa.veraweb.worker;
import de.tarent.aa.veraweb.beans.PersonSearch;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.WhereList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class LanguagesFiterFactoryTest {

    private LanguagesFilterFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new LanguagesFilterFactory();
    }

    @Test
    public void testGetLanguagesFilterWithOneLanguage() {
        PersonSearch personSearch = new PersonSearch();
        personSearch.languages = "Deutsch";
        WhereList klaus = (WhereList) factory.createLanguagesFilter(personSearch);

        assertEquals(
          "(veraweb.upper_fix(languages_a_e1) LIKE veraweb.upper_fix('%Deutsch%') OR veraweb.upper_fix(languages_b_e1) " +
            "LIKE veraweb" +
            ".upper_fix('%Deutsch%'))",
          klaus.clauseToString());
    }

    @Test
    public void testGetLanguagesFilterWithTwoLanguages() {
        PersonSearch personSearch = new PersonSearch();
        personSearch.languages = "Deutsch, English";
        Clause klaus = factory.createLanguagesFilter(personSearch);

        assertEquals(
          "(veraweb.upper_fix(languages_a_e1) LIKE veraweb.upper_fix('%Deutsch%') OR veraweb.upper_fix(languages_b_e1) " +
            "LIKE veraweb" +
            ".upper_fix('%Deutsch%')) OR (veraweb.upper_fix(languages_a_e1) LIKE veraweb.upper_fix('%English%') OR " +
            "veraweb.upper_fix" +
            "(languages_b_e1) LIKE veraweb.upper_fix('%English%'))",
          klaus.clauseToString());
    }
}
