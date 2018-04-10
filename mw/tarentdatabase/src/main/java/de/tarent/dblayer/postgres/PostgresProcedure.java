package de.tarent.dblayer.postgres;

import java.util.Iterator;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.SyntaxErrorException;
import de.tarent.dblayer.sql.statement.Procedure;

/**
 * @author kirchner
 *
 */
public class PostgresProcedure extends Procedure {

	/**
	 * Default-Constructor @see {@link Procedure#Procedure(DBContext, String)}
	 * @param dbx
	 * @param name
	 */
	public PostgresProcedure(DBContext dbx, String name) {
		super(dbx, name);
	}

	public String statementToString() throws SyntaxErrorException {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM ");
		sb.append(_name);
		sb.append("(");
		Iterator it = _params.iterator();
		boolean first = true;
		while(it.hasNext()){
			if(!first){
				sb.append(", ");
			}
			else
				first = false;

			sb.append(SQL.format(getDBContext(), it.next()));
		}
		sb.append(")");
		return sb.toString();
	}

}
