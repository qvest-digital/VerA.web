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
