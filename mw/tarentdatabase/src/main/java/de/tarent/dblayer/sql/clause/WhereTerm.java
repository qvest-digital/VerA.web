package de.tarent.dblayer.sql.clause;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.engine.SetDbContextImpl;
import de.tarent.dblayer.sql.ParamHolder;
import de.tarent.dblayer.sql.ParamValue;
import de.tarent.dblayer.sql.SyntaxErrorException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This {@link Clause} represents a collection of {@link Clause Clauses}
 * connected by boolean operators. They are actually given as a number of
 * key to {@link Clause} mappings and a {@link String} containing these
 * keys connected by whitespaces, brackets, '&amp;', '|' or '!'.
 */
public final class WhereTerm extends SetDbContextImpl implements Clause {
    /**
     * regular expression pattern for parsing the {@link #_term term}
     */
    private final static Pattern pattern = Pattern.compile("\\&|\\||\\!|[a-zA-Z0-9]*");

    /**
     * term to be interpreted in {@link #clauseToString(StringBuffer, DBContext)}
     */
    private final String _term;

    /**
     * mappings for '&', '|' and '!'
     */
    private final static Map BASE_VALUES = new HashMap();

    /** initialization of {@link #BASE_VALUES} */
    static {
        BASE_VALUES.put("&", Where.AND);
        BASE_VALUES.put("|", Where.OR);
        BASE_VALUES.put("!", Where.NOT);
    }

    /**
     * mapping of keys and '&', '|' and '!' to {@link Clause Clauses} and {@link Where#AND},
     * {@link Where#OR} and {@link Where#NOT} initialized with {@link #BASE_VALUES} and
     * expanded in {@link #set(String, Clause)}
     */
    HashMap _values = new HashMap(BASE_VALUES);

    //
    // constructors
    //

    /**
     * This constructor stores the given {@link String} as term for
     * {@link #clauseToString(StringBuffer, DBContext) evaluation}
     * using the {@link Clause Clauses} set by key.
     *
     * @param term {@link String} consisting of keys connected by whitespaces,
     *             brackets, '&amp;', '|' or '!'.
     * @see #set(String, Clause)
     */
    public WhereTerm(String term) {
        _term = term;
    }

    /**
     * Appends the parameters of the paramHolder to the supplied list.
     * The order of the params is determined by the order of appearance
     * of the params in the holder object.
     *
     * @param paramList A list to take up ParamValue ebjects.
     * @see ParamHolder#getParams(List)
     */
    public void getParams(List paramList) {
        for (Iterator iter = _values.values().iterator(); iter.hasNext(); ) {
            Object item = iter.next();
            if (item instanceof ParamValue) {
                paramList.add(item);
            } else if (item instanceof ParamHolder) {
                ((ParamHolder) item).getParams(paramList);
            }
        }
    }

    //
    // public methods
    //

    /**
     * This method registers a {@link Clause} with a key {@link String}.
     *
     * @param key   a key for the value {@link Clause}; valid keys consist of
     *              digits and basic letters only
     * @param value the {@link Clause} to be registered with the key
     * @return this {@link WhereTerm} instance
     */
    public WhereTerm set(String key, Clause value) {
        _values.put(key, value);
        return this;
    }

    /**
     * This method returns the size of the expected length of the expanded term.
     */
    public int prizeSize() {
        return _term.length() * 10;
    }

    /**
     * This method appends a string representation of the clause model
     * for use in SQL statements to the given {@link StringBuffer}.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgreSQL DBMS.
     *
     * @param buffer the {@link StringBuffer} to append to
     * @return the given {@link StringBuffer}
     * @deprecated use {@link #clauseToString(StringBuffer, DBContext)} instead
     */
    public StringBuffer clauseToString(StringBuffer buffer) throws SyntaxErrorException {
        return clauseToString(buffer, getDBContext());
    }

    /**
     * This method appends a string representation of the clause model
     * for use in SQL statements to the given {@link StringBuffer}.<br>
     * Keys in the term are replaced by {@link String} representations
     * of the {@link Clause Clauses}, '&amp;', '|' and '!' are replaced by
     * {@link Where#AND " AND "}, {@link Where#OR " OR "} and
     * {@link Where#NOT " NOT "} respectively, and whitespaces and
     * brackets are left in place.
     *
     * @param buffer  the {@link StringBuffer} to append to
     * @param context the db layer context to use for formatting parameters
     * @return the given {@link StringBuffer}
     */
    public StringBuffer clauseToString(StringBuffer buffer, DBContext context) throws SyntaxErrorException {
        Matcher matcher = pattern.matcher(_term);
        int last = 0;
        String group;
        Object entry;

        while (matcher.find()) {
            if (matcher.end() - matcher.start() > 0) {
                buffer.append(_term.substring(last, matcher.start()));
                group = matcher.group();
                entry = _values.get(group);
                if (entry instanceof Clause) {
                    buffer.append(((Clause) entry).clauseToString(context));
                } else if (entry != null) // only non null non Clauses are Where.AND, Where.OR and Where.NOT
                {
                    buffer.append(entry);
                } else {
                    throw new SyntaxErrorException("key '" + group + "' for term '" + _term + "' not found.");
                }
                last = matcher.end();
            }
        }
        buffer.append(_term.substring(last, _term.length()));
        return buffer;
    }

    //
    // interface {@link Clause}
    //

    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgreSQL DBMS.
     *
     * @return string representation of the clause model
     * @see de.tarent.dblayer.sql.clause.Clause#clauseToString()
     * @deprecated use {@link #clauseToString(DBContext)} instead
     */
    public String clauseToString() {
        return clauseToString(getDBContext());
    }

    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.<br>
     * This method MAY as a side effect change the {@link DBContext} of this
     * {@link Clause} to the given one.<br>
     * TODO: This method should be able to throw qualified exceptions
     *
     * @param dbContext the db layer context to use for formatting parameters
     * @return string representation of the clause model
     * @see de.tarent.dblayer.sql.clause.Clause#clauseToString(de.tarent.dblayer.engine.DBContext)
     */
    public String clauseToString(DBContext dbContext) {
        try {
            return clauseToString(new StringBuffer(prizeSize()), dbContext).toString();
        } catch (SyntaxErrorException e) {
            return e.toString();
        }
    }

    /**
     * Returns an independent clone of this statement.
     *
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            WhereTerm theClone = (WhereTerm) super.clone();
            theClone._values = (HashMap) _values.clone();
            return theClone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}
