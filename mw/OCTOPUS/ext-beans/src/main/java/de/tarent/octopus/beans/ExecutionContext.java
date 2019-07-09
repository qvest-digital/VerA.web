package de.tarent.octopus.beans;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import de.tarent.dblayer.sql.Statement;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.engine.DBContext;

/**
 * Diese Schnittstelle beschreibt einen Kontext, der SQL-Statements ausführen kann.
 *
 * @author mikel
 */
public interface ExecutionContext extends DBContext {
    /**
     * Diese Methode führt das übergebene {@link Statement} aus.
     *
     * @param statement auszuführendes {@link Statement}
     * @throws BeanException
     */
    public void execute(Statement statement) throws BeanException;

    /**
     * Diese Methode führt das übergebene {@link Select}-{@link Statement}
     * aus und erwartet als Resultat ein {@link ResultSet}, das dann
     * zurückgegeben wird.
     *
     * @param statement auszuführendes {@link Select}-{@link Statement}
     * @return resultierendes {@link ResultSet}
     * @throws BeanException
     */
    public ResultSet result(Select statement) throws BeanException;

    /**
     * This method closes a {@link ResultSet} returned by {@link #result(Select)}.
     * It may also close the {@link java.sql.Statement} and {@link java.sql.Connection}
     * used for creating the {@link ResultSet} if they were opened just for this
     * task.
     *
     * @param resultSet a {@link ResultSet} returned by {@link #result(Select)}.
     */
    public void close(ResultSet resultSet) throws BeanException;

    /**
     * Diese Methode bereitet das übergebene {@link Statement} vor.
     *
     * @param statement vorzubereitendes {@link Statement}
     * @return resultierendes {@link PreparedStatement}
     * @throws BeanException
     */
    public PreparedStatement prepare(Statement statement) throws BeanException;

    /**
     * Diese Methode liefert die {@link Database}, in der dieser Kontext arbeitet.
     *
     * @return zugehörige {@link Database}
     */
    public Database getDatabase();
}
