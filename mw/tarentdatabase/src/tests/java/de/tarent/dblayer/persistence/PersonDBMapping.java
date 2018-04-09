/*
 * tarent-database,
 * jdbc database library
 * Copyright (c) 2005-2006 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-database'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */


package de.tarent.dblayer.persistence;

import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.engine.DBContext;


public class PersonDBMapping extends AbstractDBMapping {

    protected static final int PERSON_FIRMA_FIELDS = 128;

    public static final String STMT_PERSON_FIRMA = "stmtPersonFirmaOne";

    
    public PersonDBMapping(DBContext cntx) {
        super(cntx);
    }

    public String getTargetTable() {
        return "person";
    }
    
    public void configureMapping() {
        addField("person.pk_person", "id", PRIMARY_KEY_FIELDS | MINIMAL_FIELDS | COMMON_FIELDS);
        addField("person.fk_firma", "firmaFk"); 
        addField("person.vorname", "givenName", DEFAULT_FIELD_SET | MINIMAL_FIELDS);
        addField("person.nachname", "lastName", DEFAULT_FIELD_SET | MINIMAL_FIELDS);
        addField("person.geburtstag", "birthday");
        
        addField("firma.pk_firma", concatPropCol("firma", "id"), PERSON_FIRMA_FIELDS);
        addField("firma.name", concatPropCol("firma","name"), PERSON_FIRMA_FIELDS);
        addField("firma.umsatz", concatPropCol("firma", "turnover"), PERSON_FIRMA_FIELDS);

        addQuery(STMT_SELECT_ALL, createBasicSelectAll().orderBy(Order.asc("person.vorname")), DEFAULT_FIELD_SET);
        addQuery(STMT_PERSON_FIRMA, createBasicSelectOne().join("firma", "person.fk_firma", "firma.pk_firma"), PERSON_FIRMA_FIELDS | COMMON_FIELDS );
    }
}