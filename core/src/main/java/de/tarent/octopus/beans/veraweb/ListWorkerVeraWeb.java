package de.tarent.octopus.beans.veraweb;
import de.tarent.aa.veraweb.worker.JumpOffset;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.GroupBy;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.BeanListWorker;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse konkretisiert den abstrakten Basis-Worker {@link BeanListWorker}
 * auf die Benutzung der VerA.web-spezifischen Implementierungen von
 * {@link Database} und {@link Request} hin.
 */
public abstract class ListWorkerVeraWeb extends BeanListWorker {
    //
    // Konstruktoren
    //

    /**
     * Dieser Konstruktor gibt den Namen der zugrunde liegenden Bean weiter.
     */
    protected ListWorkerVeraWeb(String beanName) {
        super(beanName);
    }

    //
    // Oberklasse BeanListWorker
    //

    /**
     * Diese Methode liefert eine {@link DatabaseVeraWeb}-Instanz
     *
     * @see BeanListWorker#getDatabase(OctopusContext)
     */
    @Override
    protected Database getDatabase(OctopusContext cntx) {
        return new DatabaseVeraWeb(cntx);
    }

    /**
     * Diese Methode liefert eine {@link RequestVeraWeb}-Instanz.
     *
     * @see BeanListWorker#getRequest(OctopusContext)
     */
    @Override
    protected Request getRequest(OctopusContext cntx) {
        return new RequestVeraWeb(cntx);
    }

    /**
     * Table column to use for Jump Offsets (a.k.a. "Direkteinsprung")
     * <br>
     *
     * @param octopusContext The {@link OctopusContext}
     * @return the db column name or <code>null</code> if no jump offsets should be generated.
     * @throws BeanException beanException
     */
    protected String getJumpOffsetsColumn(OctopusContext octopusContext) throws BeanException {
        return null;
    }

    public static final String INPUT_getJumpOffsets[] = {};
    public static final String OUTPUT_getJumpOffsets = "jumpOffsets";

    public List<JumpOffset> getJumpOffsets(OctopusContext octopusContext) throws BeanException, IOException, SQLException {
        final Database database = getDatabase(octopusContext);
        final Integer start = getStart(octopusContext);
        final Integer limit = getLimit(octopusContext);
        final String col = getJumpOffsetsColumn(octopusContext);
        if (col == null) {
            return null;
        }
        final Select subQuery = getSelect(database);
        extendWhere(octopusContext, subQuery);
        extendColumns(octopusContext, subQuery);
        subQuery.setDistinct(false);

        final Select statement = SQL
          .Select(database)
          .setDistinct(false)
          .selectAs("s1.letter", "letter")
          .selectAs("min(rownum)", "rownum")
          .from("( select upper(substring(trim(" + col + "),1,1)) as letter, row_number() over () -1 as rownum from (" +
            subQuery.statementToString() + " ) s0 ) ", "s1")
          .groupBy(new GroupBy("letter"))
          .orderBy(Order.asc("rownum"));
        ResultSet rs = statement
          .getResultSet();
        final ArrayList<JumpOffset> offsets = new ArrayList<JumpOffset>();
        while (rs.next()) {
            offsets.add(new JumpOffset(rs.getString("letter"), rs.getInt("rownum"), start, limit));
        }
        return offsets;
    }
}
