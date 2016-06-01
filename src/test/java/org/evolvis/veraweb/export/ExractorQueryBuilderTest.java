package org.evolvis.veraweb.export;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import de.tarent.extract.ExtractorQuery;
public class ExractorQueryBuilderTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private ExtractorQuery extractorQuery;

    @Test
    public void canSubstituteSingleValues() {
        when(extractorQuery.getSql()).thenReturn("fnarz ${foo} fnarz ${bar}");
        ExtractorQueryBuilder queryTemplate = new ExtractorQueryBuilder(extractorQuery);
        ExtractorQuery query = queryTemplate.replace("foo",42).replace("bar","bang").build();
        assertEquals("fnarz 42 fnarz bang", query.getSql());
    }
    
    @Test
    public void canApplyASetOfSubstitutions(){
        when(extractorQuery.getSql()).thenReturn("fnarz ${foo} fnarz ${bar}");
        ExtractorQueryBuilder queryTemplate = new ExtractorQueryBuilder(extractorQuery);
        HashMap<String, String> substitutions = new HashMap<String,String>();
        substitutions.put("foo", "42");
        substitutions.put("bar", "bang");
        
        ExtractorQuery query = queryTemplate.replace(substitutions).build();
        assertEquals("fnarz 42 fnarz bang", query.getSql());
    }

}
