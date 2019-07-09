package org.evolvis.veraweb.export;
import de.tarent.extract.ExtractorQuery;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ExtractorQueryBuilderTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private ExtractorQuery extractorQuery;

    @Test
    public void canSubstituteSingleValues() {
        when(extractorQuery.getSql()).thenReturn("fnarz ${foo} fnarz ${bar}");
        ExtractorQueryBuilder queryTemplate = new ExtractorQueryBuilder(extractorQuery);
        ExtractorQuery query = queryTemplate.replace("foo", 42).replace("bar", "bang").build();
        assertEquals("fnarz 42 fnarz bang", query.getSql());
    }

    @Test
    public void canApplyASetOfSubstitutions() {
        when(extractorQuery.getSql()).thenReturn("fnarz ${foo} fnarz ${bar}");
        ExtractorQueryBuilder queryTemplate = new ExtractorQueryBuilder(extractorQuery);
        HashMap<String, String> substitutions = new HashMap<String, String>();
        substitutions.put("foo", "42");
        substitutions.put("bar", "bang");

        ExtractorQuery query = queryTemplate.replace(substitutions).build();
        assertEquals("fnarz 42 fnarz bang", query.getSql());
    }

    @Test
    public void applyingFiltersDontModifyBaseQuerySql() {
        //given:
        String basicQueryString = "SELECT * FROM tguest WHERE x=y";
        when(extractorQuery.getSql()).thenReturn(basicQueryString);
        ExtractorQueryBuilder queryTemplate = new ExtractorQueryBuilder(extractorQuery);
        HashMap<String, String> filterSettings = new HashMap<>();
        filterSettings.put(ValidExportFilter.CATEGORY_ID_FILTER.key, "0");
        filterSettings.put(ValidExportFilter.INVITATIONSTATUS_FILTER.key, "2");

        //when:
        ExtractorQuery query = queryTemplate.setFilters(filterSettings).build();

        //then:
        assertTrue(query.getSql().startsWith(basicQueryString));
    }

    @Test
    public void applyingFiltersExtendsBaseQuerySql() {
        //given:
        String basicQueryString = "SELECT * FROM tguest WHERE x=y";
        when(extractorQuery.getSql()).thenReturn(basicQueryString);
        ExtractorQueryBuilder queryTemplate = new ExtractorQueryBuilder(extractorQuery);
        HashMap<String, String> filterSettings = new HashMap<>();
        filterSettings.put(ValidExportFilter.CATEGORY_ID_FILTER.key, "0");
        filterSettings.put(ValidExportFilter.INVITATIONSTATUS_FILTER.key, "2");
        filterSettings.put("filterStupid", "0");
        filterSettings.put(ValidExportFilter.SEARCHWORD_FILTER.key, "2; DELETE FROM tconfig");

        //when:
        ExtractorQuery query = queryTemplate.setFilters(filterSettings).build();

        //then:
        String expectedStatusFilter = "((invitationtype = 1 AND (invitationstatus = 1 OR invitationstatus_p = 1)) OR (invitationtype = 2 AND invitationstatus = 1) OR (invitationtype = 3 AND invitationstatus_p = 1))";
        assertEquals(query.getSql(), basicQueryString + " AND " + expectedStatusFilter + " AND g.fk_category = 0");
    }
}
