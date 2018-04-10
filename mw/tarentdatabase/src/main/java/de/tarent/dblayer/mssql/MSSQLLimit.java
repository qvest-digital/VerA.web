package de.tarent.dblayer.mssql;

import de.tarent.dblayer.sql.clause.Limit;

public class MSSQLLimit extends Limit {

	public MSSQLLimit(Limit limit){
		this(new Integer(limit.getLimit()), new Integer(limit.getOffset()));
	}

	public MSSQLLimit(int limit, int offset) {
		this(new Integer(limit), new Integer(offset));
	}

	public MSSQLLimit(Integer limit, Integer offset){
		super(limit, offset);
	}

    /**
     * This method attaches the actual <code>LIMIT</code> and <code>OFFSET</code>
     * clauses to a {@link StringBuffer}.
     */
	public void clauseToString(StringBuffer sb) {
	    if (getLimit()> 0) {
		    sb.append("TOP ");
		    sb.append(getLimit());
		    sb.append(" ");
		}
	    if(getOffset()!=0){
		throw new RuntimeException("Offsets other than zero are not supportet on MSSQL.");
	    }
	}

}
