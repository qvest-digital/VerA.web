package org.evolvis.veraweb.onlinereg.rest;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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
import org.evolvis.veraweb.onlinereg.entities.Delegation;
import org.evolvis.veraweb.onlinereg.entities.OptionalField;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldTypeContent;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldValue;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import javax.ws.rs.container.ResourceContext;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by aalexa on 20.01.15.
 *
 * @author aalexa
 * @author jnunez
 */
@RunWith(MockitoJUnitRunner.class)
public class DelegationResourceTest {

    @Mock
    private static SessionFactory mockitoSessionFactory;
    @Mock
    private static Session mockitoSession;
    @Mock
    ResourceContext resourceContext;

    DelegationResource delegationResource;

    public DelegationResourceTest() {
        delegationResource = new DelegationResource();
        delegationResource.context = mock(ServletContext.class);
    }

    @AfterClass
    public static void tearDown() {
        mockitoSessionFactory.close();
        mockitoSession.disconnect();
        mockitoSession.close();
    }

    private List<OptionalField> getDummyOptionalFields() {
        List<OptionalField> optionalFields = new ArrayList<OptionalField>();

        // -------------------------------------------------
            OptionalField inputField = new OptionalField();
            inputField.setFk_type(1);
            inputField.setFk_event(1);
            inputField.setLabel("My input field");
            inputField.setPk(11);

            OptionalField singleComboField = new OptionalField();
            singleComboField.setFk_type(2);
            singleComboField.setFk_event(1);
            singleComboField.setLabel("My combobox field");
            singleComboField.setPk(12);

            OptionalField multipleComboField = new OptionalField();
            multipleComboField.setFk_type(3);
            multipleComboField.setFk_event(1);
            multipleComboField.setLabel("My multiple combobox field");
            multipleComboField.setPk(13);
        // -------------------------------------------------

        optionalFields.add(inputField);
        optionalFields.add(singleComboField);
        optionalFields.add(multipleComboField);

        return optionalFields;
    }

    private List<OptionalFieldTypeContent> getDummyOptionalFieldTypeContents() {
        List<OptionalFieldTypeContent> optionalFieldTypeContents = new ArrayList<OptionalFieldTypeContent>();
        // -------------------------------------------------
            OptionalFieldTypeContent optionalFieldTypeContent1 = new OptionalFieldTypeContent();
            optionalFieldTypeContent1.setContent("Option single combo 1");
            optionalFieldTypeContent1.setFk_optional_field(12);
            optionalFieldTypeContent1.setPk(1);

            OptionalFieldTypeContent optionalFieldTypeContent2 = new OptionalFieldTypeContent();
            optionalFieldTypeContent2.setContent("Option single combo 2");
            optionalFieldTypeContent2.setFk_optional_field(12);
            optionalFieldTypeContent2.setPk(2);

            OptionalFieldTypeContent optionalFieldTypeContent3 = new OptionalFieldTypeContent();
            optionalFieldTypeContent3.setContent("Option multiple combo 1");
            optionalFieldTypeContent3.setFk_optional_field(13);
            optionalFieldTypeContent3.setPk(3);

            OptionalFieldTypeContent optionalFieldTypeContent4 = new OptionalFieldTypeContent();
            optionalFieldTypeContent3.setContent("Option multiple combo 2");
            optionalFieldTypeContent3.setFk_optional_field(13);
            optionalFieldTypeContent3.setPk(4);
        // -------------------------------------------------

        optionalFieldTypeContents.add(optionalFieldTypeContent1);
        optionalFieldTypeContents.add(optionalFieldTypeContent2);
        optionalFieldTypeContents.add(optionalFieldTypeContent3);
        optionalFieldTypeContents.add(optionalFieldTypeContent4);

        return optionalFieldTypeContents;
    }

    @Test
    public void testGetFields() {
        prepareSession();
        Query findByEventIdQuery = mock(Query.class);
        ResourceContext resourceContext = mock(ResourceContext.class);
        OptionalFieldResource optionalFieldResource = mock(OptionalFieldResource.class);
        delegationResource.setResourceContext(resourceContext);
        when(mockitoSession.getNamedQuery(OptionalField.OPTIONAL_FIELD_FIND_BY_EVENT_ID)).thenReturn(findByEventIdQuery);
        when(findByEventIdQuery.list()).thenReturn(getDummyOptionalFields());

        Query findByGuestIdQuery = mock(Query.class);
        when(mockitoSession.getNamedQuery("Delegation.findByGuestId")).thenReturn(findByGuestIdQuery);
        when(findByGuestIdQuery.list()).thenReturn(new ArrayList<Delegation>());

        Query findTypeContentsByOptionalFieldQuery = mock(Query.class);
        when(mockitoSession.getNamedQuery("OptionalFieldTypeContent.findTypeContentsByOptionalField"))
                .thenReturn(findTypeContentsByOptionalFieldQuery);
        when(findTypeContentsByOptionalFieldQuery.list()).thenReturn(getDummyOptionalFieldTypeContents());
        when(resourceContext.getResource(any(Class.class))).thenReturn(optionalFieldResource);
        when(optionalFieldResource.getOptionalFields(any(Integer.class))).thenReturn(getDummyOptionalFields());

        List<OptionalFieldValue> fields = delegationResource.getFieldsFromEvent(1, 1);
        assertEquals(3, fields.size());
        assertEquals("My input field", fields.get(0).getLabel());
        assertEquals(1, fields.get(1).getFk_event().intValue());
    }

    @Test
    public void testGetLabelId() {
        prepareSession();
        Query findByEventAndLabelQuery = mock(Query.class);
        when(mockitoSession.getNamedQuery(OptionalField.OPTIONAL_FIELD_FIND_BY_EVENT_ID_AND_LABEL)).thenReturn(findByEventAndLabelQuery);
        when(findByEventAndLabelQuery.uniqueResult()).thenReturn(1);

        Integer labelId = delegationResource.getLabelIdfromEventAndLabel(1, "Label 1");

        assertEquals(new Integer(1), labelId);
    }

    @Test
    public void testSaveField() {
        prepareSession();
        Delegation delegation = delegationResource.saveOptionalField(1, 1, "Value Field 2");
        assertNotNull(delegation);
    }

    private void prepareSession() {
        when(delegationResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
    }

}
