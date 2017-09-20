package org.evolvis.veraweb.export;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), export module is
 * Copyright © 2016, 2017
 * 	Атанас Александров <a.alexandrov@tarent.de>
 * Copyright © 2016
 * 	Lukas Degener <l.degener@tarent.de>
 * 	Max Weierstall <m.weierstall@tarent.de>
 * Copyright © 2017
 * 	mirabilos <t.glaser@tarent.de>
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
