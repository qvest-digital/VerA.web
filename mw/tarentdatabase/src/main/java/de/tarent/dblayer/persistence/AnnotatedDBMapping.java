package de.tarent.dblayer.persistence;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

import de.tarent.commons.utils.Pojo;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.persistence.annotations.Column;
import de.tarent.dblayer.persistence.annotations.Fields;
import de.tarent.dblayer.persistence.annotations.Id;
import de.tarent.dblayer.persistence.annotations.MappingType;
import de.tarent.dblayer.persistence.annotations.Reference;
import de.tarent.dblayer.persistence.annotations.Table;
import de.tarent.dblayer.persistence.annotations.Transient;

/**
 * This DBMapping is used when an annotated Bean is used. It analyzes
 * the annotations of a bean and defines the mapping depending on these
 * information.
 *
 * @author Martin Pelzer, tarent GmbH
 */
public abstract class AnnotatedDBMapping extends AbstractDBMapping {

    public AnnotatedDBMapping() {
        super();
    }

    public AnnotatedDBMapping(DBContext dbc, Class bean) {
        super(dbc, bean);
    }

    /**
     * This method uses the tarent-database annotations in the
     * associated bean to configure the mapping of the attributes
     */
    @Override
    public void configureMapping() {
        // Iterate over getter methods of associated bean and
        // check annotations for each method. Depending on annotations
        // add field to mapping.
        Object bean;
        try {
            bean = this.associatedBean.newInstance();
        } catch (InstantiationException e) {
            // bean could not be instantiated. Don't do anything because
            // we can't!
            return;
        } catch (IllegalAccessException e) {
            // bean could not be instantiated. Don't do anything because
            // we can't!
            return;
        }

        // Maybe there are defined some custom field definitions.
        // We have to add them to the list so that they can be
        // accessed and used later.
        // fields don't have to be defined any longer. They are defined by using.
        //String [] newFields = ((Fields) this.associatedBean.getAnnotation(Fields.class)).value();
        //for (int i = 0; i < newFields.length; i++) {
        //	super.addCustomFieldDefinition(newFields[i]);
        //}

        Set attributes = Pojo.getPropertyNames(bean.getClass());

        Iterator iter = attributes.iterator();
        while (iter.hasNext()) {
            // get next attribute name and get get method for this attribute
            String attributeName = (String) iter.next();

            if (attributeName.equals("class")) {
                continue;
            }

            Method method = Pojo.getGetMethod(bean, attributeName);

            // If annotation @Transient is present for this method,
            // we do nothing.
            if (!method.isAnnotationPresent(Transient.class)) {
                if (method.isAnnotationPresent(Reference.class)) {
                    // If annotation @Reference is present we have to add
                    // several fields of another bean. To do that we use method "addFields".
                    Reference reference = method.getAnnotation(Reference.class);

                    int includeBitMask = getFieldSetBitMask(reference.fields());
                    int fieldSet = getFieldSetBitMask(method);
                    DBMapping dbMapping = DAORegistry.getDAOForBean(reference.bean()).getDbMapping();

                    addFields((AbstractDBMapping) dbMapping, attributeName, includeBitMask, fieldSet);
                } else {
                    // standard column mapping

                    String tableName; // name of the table in the database
                    String columnName; // name of the column in the database

                    // get column name for mapping
                    Column column = method.getAnnotation(Column.class);
                    if (column == null) {
                        columnName = attributeName;
                    } else {
                        columnName = column.name();
                    }

                    // get table name for mapping
                    if (column == null || column.table().equals("")) {
                        // no table name defined for this column;
                        // use table defined for whole bean
                        tableName = getTargetTable();
                    } else {
                        // use table defined for this attribute
                        tableName = column.table();
                    }

                    // get fields for mapping
                    int fieldSet = getFieldSetBitMask(method);

                    // check if this key is a primary key field
                    if (method.isAnnotationPresent(Id.class)) {
                        // this attribute shall be the primary key
                        fieldSet = fieldSet | super.getFieldDefinitionValue("primaryKeyFields") |
                          super.getFieldDefinitionValue("minimalFields") | super.getFieldDefinitionValue("commonFields");
                    }

                    // add field mapping
                    addField(this.mapToDbConventions(tableName) + PROPERTY_SEPARATOR + this.mapToDbConventions(columnName),
                      attributeName, fieldSet);
                }
            }
        }

        // call method for adding queries
        this.addQueries();
    }

    private int getFieldSetBitMask(Method method) {
        int fieldSet = 0;
        Fields fields = method.getAnnotation(Fields.class);
        if (fields == null) {
            // use default field set (but not if pk)
            if (!method.isAnnotationPresent(Id.class)) {
                fieldSet = super.getFieldDefinitionValue("defaultFieldSet");
            }
        } else {
            String[] fieldSets = fields.value();

            // iterate over all given field sets, fetch value for each one from list and add it
            for (int i = 0; i < fieldSets.length; i++) {
                fieldSet = fieldSet | super.getFieldDefinitionValue(fieldSets[i]);
            }
        }
        return fieldSet;
    }

    private int getFieldSetBitMask(String[] fieldSets) {
        int fieldSet = 0;

        // iterate over all given field sets, fetch value for each one from list and add it
        for (int i = 0; i < fieldSets.length; i++) {
            fieldSet = fieldSet | super.getFieldDefinitionValue(fieldSets[i]);
        }

        return fieldSet;
    }

    /**
     * Returns the name of the target table in the database
     * as defined in the associated bean. If there is no associated
     * bean or no annotation @Table defined in the bean null is
     * returned.
     */
    @Override
    public String getTargetTable() {
        if (this.associatedBean != null) {
            Table annotation = (Table) this.associatedBean.getAnnotation(Table.class);
            if (annotation != null) {
                return annotation.name();
            }
        }
        return null;
    }

    /**
     * method for adding queries
     */
    public abstract void addQueries();

    /**
     * Maps a string to the naming conventions of the
     * database as given in the MappingType annotation. If
     * MappingType is not defined, the string is returned as
     * is.
     * NOTE: the input parameter has to be in camelCase.
     */
    private String mapToDbConventions(String string) {
        MappingType mappingType = (MappingType) this.associatedBean.getAnnotation(MappingType.class);
        if (mappingType == null) {
            return string;
        }

        if (mappingType.value().equals(MappingType.Value.LOWER_CASE)) {
            return string.toLowerCase();
        } else if (mappingType.value().equals(MappingType.Value.UPPER_CASE)) {
            return string.toUpperCase();
        } else if (mappingType.value().equals(MappingType.Value.LOWER_CASE_UNDERSCORE)) {
            for (int i = 'A'; i <= 'Z'; i++) {
                String compare = "" + (char) i;
                string = string.replaceAll(compare.trim(), "_" + Character.toLowerCase((char) i));
            }
            return string;
        } else if (mappingType.value().equals(MappingType.Value.UPPER_CASE_UNDERSCORE)) {
            for (int i = 'A'; i <= 'Z'; i++) {
                String compare = "" + (char) i;
                string = string.replaceAll(compare.trim(), "_" + Character.toUpperCase((char) i));
            }
            return string;
        } else
        // camel case (input is already camel case)
        {
            return string;
        }
    }
}
