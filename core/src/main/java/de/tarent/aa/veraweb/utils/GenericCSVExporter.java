package de.tarent.aa.veraweb.utils;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.data.exchange.ExchangeFormat;
import de.tarent.data.exchange.FieldMapping.Entity;
import de.tarent.data.exchange.MappingException;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import lombok.extern.log4j.Log4j2;
import org.evolvis.tartools.csvfile.CSVFileWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diese Klasse implementiert einen generischen CSV-Export von VerA.web-Personen.
 *
 * @author mikel
 */
@Log4j2
public class GenericCSVExporter extends GenericCSVBase implements Exporter {
    /**
     * Dieser Konstruktor ist leer; dieser wird von {@link ExchangeFormat#getExporterClass()} genutzt.
     */
    public GenericCSVExporter() {
    }

    /**
     * Diese Methode wird zu jeder zu exportierenden Person aufgerufen, übergeben wird die Person als Zusammenstellung von
     * {@link Person}. Sie fügt dem Export eine Beschreibung der übergebenen VerA.web-Person hinzu.
     *
     * @param person {@link Person}-Bean
     * @see de.tarent.aa.veraweb.utils.Exporter#exportPerson(de.tarent.aa.veraweb.beans.Person)
     */
    public void exportPerson(Person person) throws BeanException, IOException {
        assert csvFieldNames != null;
        assert fieldMapping != null;
        assert csvWriter != null;
        if (person == null) {
            return;
        }
        Entity entity = new PersonEntity(person);
        List<String> line = new ArrayList<>(csvFieldNames.size());
        for (Object csvFieldName : csvFieldNames) {
            line.add(fieldMapping.resolve(csvFieldName.toString(), entity));
        }
        csvWriter.writeFields(line);
    }

    /**
     * Diese Methode wird zu Beginn eines Exports aufgerufen. In ihr kann etwa das Dokument mit einem Kopf zu schreiben
     * begonnen werden.
     *
     * @throws IOException FIXME
     * @see de.tarent.aa.veraweb.utils.Exporter#startExport()
     */
    public void startExport() throws IOException {
        if (exchangeFormat == null) {
            throw new IOException("Der Export benötigt ein ExchangeFormat.");
        }
        if (database == null) {
            throw new IOException("Der Export benötigt eine Database.");
        }
        if (outputStream == null) {
            throw new IOException("Der Export benötigt einen OutputStream.");
        }
        try {
            readProperties();
            parseFormat(true);
            initWriter();
            writeHeaderLine();
        } catch (MappingException e) {
            throw new IOException("Fehler im Feldmapping", e);
        } catch (BeanException e) {
            throw new IOException("Fehler beim Daten-Bean-Zugriff", e);
        }
    }

    /**
     * Diese Methode wird zum Ende eines Exports aufgerufen. In ihr kann etwa das bisher gesammelte Dokument
     * festgeschrieben werden.
     *
     * @throws IOException FIXME
     * @see de.tarent.aa.veraweb.utils.Exporter#endExport()
     */
    public void endExport() throws IOException {
        assert csvWriter != null;
        csvWriter.close();
    }

    //
    // geschützte Hilfsmethoden
    //

    /**
     * Diese Methode initialisiert den internen {@link CSVFileWriter}.
     */
    void initWriter() throws IOException {
        assert exchangeFormat != null;
        assert outputStream != null;
        Writer writer = new OutputStreamWriter(outputStream, fileEncoding);
        csvWriter = new CSVFileWriter(writer, fieldSeparator, textQualifier);
    }

    /**
     * Diese Methode schreibt die Kopfzeile mit den Spaltennamen.
     */
    void writeHeaderLine() {
        assert csvWriter != null;
        assert fieldMapping != null;
        csvWriter.writeFields(csvFieldNames);
    }

    //
    // geschützte innere Klassen
    //

    /**
     * Diese Klasse setzt die {@link Entity}-Facade für {@link Person}-Instanzen um.
     */
    class PersonEntity implements Entity {
        //
        // Schnittstelle Entity
        //

        /**
         * Diese Methode erlaubt das Abfragen von Daten zu einem bestimmten Schlüssel. Die Schlüssel werden in
         * {@link GenericCSVExporter#getAvailableFields()} erstellt.
         *
         * @param sourceKey Quellfeldschlüssel
         * @return Quellfeldwert als {@link String}; <code>null</code>-Felder werden als Leerstring <code>""</code>
         * geliefert; <code>Date</code>-Felder werden mit {@link GenericCSVBase#dateFormat} formatiert.
         */
        public String get(String sourceKey) {
            Object result = null;
            // Personenstammdatenfelder
            try {
                if (sourceKey.equals(":workarea_name")) {
                    result = getWorkareaName(person.workarea);
                } else if (sourceKey.charAt(0) == ':') {
                    result = person.get(sourceKey.substring(1));
                } else if (sourceKey.startsWith("CAT:")) {
                    result = getRank(sourceKey.substring(4));
                } else if (sourceKey.startsWith("EVE:")) {
                    result = getRank(sourceKey.substring(4));
                } else if (sourceKey.startsWith("COR:")) {
                    result = getRank(sourceKey.substring(4));
                } else {
                    logger.warn("Unbekanntes Quellfeld");
                }
            } catch (Exception e) {
                logger.warn("Fehler beim Beziehen von Personendaten", e);
            }
            if (result instanceof Date) {
                return dateFormat.format(result);
            }
            return result == null ? "" : result.toString();
        }

        //
        // Konstruktor
        //

        /**
         * Der Konstruktor vermerkt die Person
         */
        PersonEntity(Person person) {
            this.person = person;
        }

        //
        // Hilfsmethoden
        //

        /**
         * Diese Methode liefert den Rang einer Person in einer Kategorie.
         *
         * @param categoryName Name der Kategorie
         * @return Rang der Person in der Kategorie; <code>null</code>, wenn nicht in der Kategorie,
         * {@link GenericCSVExporter#DEFAULT_RANK}, wenn weder ein individueller noch ein allgemeiner Rang zu der
         * Kategorie vorliegt, jedoch sehr wohl Kategorienzugehörigkeit besteht.
         */
        Object getRank(String categoryName) throws BeanException, IOException {
            if (ranks == null) {
                ranks = new HashMap();
                Bean sampleCategory = database.createBean("Categorie");
                Bean samplePersonCategory = database.createBean("PersonCategorie");
                Select select = new Select(false).from(database.getProperty(samplePersonCategory, "table")).join(
                  database.getProperty(sampleCategory, "table"), database.getProperty(sampleCategory, "id"),
                  database.getProperty(samplePersonCategory, "categorie"))
                  .selectAs(database.getProperty(sampleCategory, "name"), "name")
                  .selectAs(database.getProperty(sampleCategory, "rank"), "rankDefault")
                  .selectAs(database.getProperty(samplePersonCategory, "rank"), "rank")
                  .where(Expr.equal(database.getProperty(samplePersonCategory, "person"), person.id));
                final List entries = database.getList(select, database);
                for (Object currentEntry : entries) {
                    final Map entry = (Map) currentEntry;
                    final Object name = entry.get("name");
                    if (name == null) {
                        continue;
                    }
                    Object rank = entry.get("rank");
                    if (rank == null) {
                        rank = entry.get("rankDefault");
                        if (rank == null) {
                            rank = DEFAULT_RANK;
                        }
                    }
                    ranks.put(name, rank);
                }
            }
            return ranks.get(categoryName);
        }

        private String getWorkareaName(int workareaId) throws BeanException {
            String workareaName = null;
            if (workareaId != 0) {
                Select select = new Select(false).from("tworkarea")
                  .selectAs("name", "name")
                  .where(Expr.equal("pk", workareaId));
                final List sqlResult = database.getList(select, database);
                final Map entry = (Map) sqlResult.get(0);
                if (entry != null) {
                    workareaName = entry.get("name").toString();
                }
            }
            return workareaName;
        }

        //
        // Membervariablen
        //
        /**
         * Die {@link Person}, für die dies eine Facade ist.
         */
        final Person person;

        /**
         * Die Ränge der Person in den Kategorien
         */
        Map ranks = null;
    }

    /**
     * Einschränkung auf Mandant
     */
    protected Integer orgUnitId = null;

    /**
     * Einschränkung auf Kategorie <code>Null</code> = alle Kategorien, 0 = keine Kategorie
     */
    protected Integer categoryId = null;

    //
    // geschützte Membervariablen
    //
    /**
     * CSV-Ausgabe-Objekt
     */
    CSVFileWriter csvWriter = null;

    /*
     * (non-Javadoc)
     *
     * @see de.tarent.aa.veraweb.utils.Exporter#setOrgUnitId(java.lang.Integer)
     */
    public void setOrgUnitId(Integer orgUnitId) {
        this.orgUnitId = orgUnitId;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.tarent.aa.veraweb.utils.Exporter#setCategoryId(java.lang.Integer)
     */
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Diese Methode holt alle notwendigen Kategorien aus der Datenbank.<br>
     * Nur die vom Nutzer ausgewählte Kategorien. Nur die Kategorien des Mandanten
     *
     * @throws IOException   FIXME
     * @throws BeanException FIXME
     */
    @Override
    protected List getCategoriesFromDB() throws BeanException, IOException {
        // alle Kategorien.
        if (this.categoryId == null && this.orgUnitId == null) {
            return super.getCategoriesFromDB();
        }
        // keine Kategorien
        if (this.categoryId != null && this.categoryId == -1) {
            return null;
        }

        Select sel = database.getSelect("Categorie");

        Clause expr1 = null, expr2 = null;
        if (this.categoryId != null) {
            expr1 = Expr.equal("pk", this.categoryId);
        }
        if (this.orgUnitId != null && this.orgUnitId != -1) {
            expr2 = Expr.equal("fk_orgunit", this.orgUnitId);
        }
        if (expr1 != null && expr2 != null) {
            sel.where(Where.and(expr1, expr2));
        } else if (expr1 != null) {
            sel.where(expr1);
        } else if (expr2 != null) {
            sel.where(expr2);
        }

        return database.getBeanList("Categorie", sel);
    }
}
