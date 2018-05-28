package de.tarent.commons.datahandling;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.tarent.octopus.server.OctopusContext;

/**
 * Default implementation for the list filter.
 *
 * At the moment this implementation only supports simple filters concatenated by and.
 */
public class ListFilterImpl implements ListFilter {

    int count;
    int start = 0;
    int limit = 20;
    String sortField;
    String sortDirection;
    boolean useLimit = false;

    List possibleAndFilterParams;
    /**
     * supplied filter list from the request
     */
    List requestFilterList = null;

    /**
     * Inits the values of the filter from the octopus context.
     *
     * @return Returns this.
     */
    public ListFilterImpl init(OctopusContext cntx, String prefix) {
        if (cntx.requestAsString(prefix + "." + PARAM_RESET) != null &&
          !"".equals(cntx.requestAsString(prefix + "." + PARAM_RESET))) {
            resetSession(cntx, prefix);
        }

        if (cntx.requestAsString(prefix + "." + PARAM_RESET_FILTER) != null &&
          !"".equals(cntx.requestAsString(prefix + "." + PARAM_RESET_FILTER))) {
            resetFilter(cntx, prefix);
        }

        setStart(getOctopusInt(cntx, prefix + "." + PARAM_START, getStart()));
        setLimit(getOctopusInt(cntx, prefix + "." + PARAM_LIMIT, getLimit()));
        setSortField(getOctopusString(cntx, prefix + "." + PARAM_SORT_FIELD, getSortField()));
        setSortDirection(getOctopusString(cntx, prefix + "." + PARAM_SORT_DIRECTION, getSortDirection()));
        setUseLimit(true);

        requestFilterList = (List) cntx.requestAsObject(prefix + "." + PARAM_FILTER_LIST);

        if (possibleAndFilterParams != null) {
            for (Iterator iter = possibleAndFilterParams.iterator(); iter.hasNext(); ) {
                FilterParam param = (FilterParam) iter.next();
                String key = prefix + "." + param.columnName;
                Object filterValue = cntx.getContextField(key);
                if (filterValue == null) {
                    filterValue = cntx.sessionAsObject(key);
                }

                if (filterValue instanceof String && ((String) filterValue).trim().length() == 0) {
                    param.value = null;
                } else if (filterValue != null) {
                    param.value = filterValue;
                }

                cntx.setSession(key, param.value);
            }
        }
        cntx.setContent(prefix, this);

        return this;
    }

    /**
     * Reset all Values in the session
     */
    public void resetSession(OctopusContext cntx, String prefix) {
        cntx.setSession(prefix + "." + PARAM_START, null);
        // we should not reset the limit
        cntx.setSession(prefix + "." + PARAM_SORT_FIELD, null);
        cntx.setSession(prefix + "." + PARAM_SORT_DIRECTION, null);
        resetFilter(cntx, prefix);
    }

    /**
     * Reset all filter Values in the session
     */
    public void resetFilter(OctopusContext cntx, String prefix) {
        if (possibleAndFilterParams != null) {
            for (Iterator iter = possibleAndFilterParams.iterator(); iter.hasNext(); ) {
                FilterParam param = (FilterParam) iter.next();
                String key = prefix + "." + param.columnName;

                cntx.setSession(key, null);
            }
        }
    }

    protected int getOctopusInt(OctopusContext cntx, String key, int defaultValue) {
        String s = cntx.requestAsString(key);
        Integer result = null;
        if (s != null && s.length() != 0) {
            try {
                result = new Integer(s);
            } catch (NumberFormatException e) {
                result = new Integer(defaultValue);
            }
        }
        if (result == null) {
            result = (Integer) cntx.sessionAsObject(key);
        }

        if (result == null || (s != null && s.trim().length() == 0)) {
            result = new Integer(defaultValue);
        }
        cntx.setSession(key, result);
        return result.intValue();
    }

    protected String getOctopusString(OctopusContext cntx, String key, String defaultValue) {
        String result = cntx.requestAsString(key);
        if (result == null) {
            result = cntx.sessionAsString(key);
        }

        if (result == null || result.trim().length() == 0) {
            result = defaultValue;
        }
        cntx.setSession(key, result);

        return result;
    }

    /**
     * Returns the column for sorting of the list
     */
    public String getSortField() {
        return sortField;
    }

    /**
     * Sets the column or colums the SQL should use for ORDER BY
     * You can use one Popertyname that will be mapped to a database column
     * or simply the columnname as a String. Even multiple colums as a
     * concatenated String like "table1.column1, table2.column1" can be used
     *
     * @param sortField
     */
    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    /**
     * Returns the sort direction. This may be one of the constants DIRECTION_ASC, DIRECTION_DESC.
     */
    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        if (DIRECTION_DESC.equalsIgnoreCase(sortDirection)) {
            this.sortDirection = DIRECTION_DESC;
        } else {
            this.sortDirection = DIRECTION_ASC;
        }
    }

    /**
     * Returns the total count of records in the list.
     */
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Returns the start position. (Beginning at 0)
     */
    public int getStart() {
        /**
         * this solution "return Math.min(start, getLastPageStart());" considered the special case
         * of a web application that needs full pages of data. So instead of the given offset the
         * offset of the current page was used.
         * This should be considered when setting the offset instead of cerrecting it in the database
         * layer.
         */
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    /**
     * Returns the count of records to return, beginning at position start.
     */
    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Returns the count of pages for the total list, based on the values for limit and count.
     */
    public int getPages() {
        return count / limit + (count % limit == 0 ? 0 : 1);
    }

    /**
     * Returns the current page number.
     */
    public int getPage() {
        return start / limit + 1;
    }

    /**
     * Returns the start position of the previous page, based on the values for start, limit and count.
     */
    public int getPreviousPageStart() {
        int prev = start - limit;
        if (prev < 0) {
            return 0;
        }
        return prev;
    }

    /**
     * Returns the start position of the next page, based on the values for start, limit and count.
     */
    public int getNextPageStart() {
        int next = start + limit;
        if (next > getLastPageStart()) {
            return getLastPageStart();
        }
        return next;
    }

    /**
     * Returns the start position of the last page, based on the values for start, limit and count.
     */
    public int getLastPageStart() {
        return count - (count % limit) - (count != 0 && count % limit == 0 ? limit : 0);
    }

    public boolean useLimit() {
        return useLimit;
    }

    public void setUseLimit(boolean newUseLimit) {
        this.useLimit = newUseLimit;
    }

    public void addFilter(String columnName, ListFilterOperator relation) {
        // TODO: It would be more clean to store the ListFilterOperator directly
        addFilterParam(new FilterParam(columnName, relation.toString()));
    }

    public void addFilter(String columnName, String relation) {
        addFilterParam(new FilterParam(columnName, relation));
    }

    public void addFilter(String columnName, ListFilterOperator relation, Object defaultValue) {
        addFilterParam(new FilterParam(columnName, relation.toString(), defaultValue));
    }

    public void addFilter(String columnName, String relation, Object defaultValue) {
        addFilterParam(new FilterParam(columnName, relation, defaultValue));
    }

    public void setFilterValue(String columnName, Object value) {
        if (possibleAndFilterParams == null) {
            return;
        }

        for (Iterator iter = possibleAndFilterParams.iterator(); iter.hasNext(); ) {
            FilterParam param = (FilterParam) iter.next();
            if (param.columnName.equals(columnName)) {
                param.value = value;
            }
        }
    }

    public Object getFilterValue(String columnName) {
        if (possibleAndFilterParams == null) {
            return null;
        }

        for (Iterator iter = possibleAndFilterParams.iterator(); iter.hasNext(); ) {
            FilterParam param = (FilterParam) iter.next();
            if (param.columnName.equals(columnName)) {
                return param.value;
            }
        }
        return null;
    }

    public void clearAllFilterValues(String columnName, Object value) {
        if (possibleAndFilterParams == null) {
            return;
        }

        for (Iterator iter = possibleAndFilterParams.iterator(); iter.hasNext(); ) {
            FilterParam param = (FilterParam) iter.next();
            param.value = null;
        }
    }

    public void addFilterParam(FilterParam param) {
        if (possibleAndFilterParams == null) {
            possibleAndFilterParams = new ArrayList();
        }
        possibleAndFilterParams.add(param);
    }

    /**
     * Checks wether all not optional paramters are set
     */
    public boolean isComplete() {
        for (Iterator iter = possibleAndFilterParams.iterator(); iter.hasNext(); ) {
            FilterParam param = (FilterParam) iter.next();
            if (!(param.optional || param.isSet())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the filter tree as list in Reverse Polish Notation (Umgekehrte Polnische Notation UPN)
     * The first (left) operand is allways the String column name, the right operand may be of any type.
     * The operators are ListFilterOperator objects.
     */
    public List getFilterList() {
        int stackCounter = 0;
        LinkedList filterList = new LinkedList();
        if (requestFilterList != null) {
            filterList.addAll(requestFilterList);
        }
        if (possibleAndFilterParams != null) {
            for (Iterator iter = possibleAndFilterParams.iterator(); iter.hasNext(); ) {
                FilterParam param = (FilterParam) iter.next();
                if (param.isSet()) {
                    filterList.add(param.columnName);
                    filterList.add(param.value);
                    filterList.add(new ListFilterOperator(param.relation));
                    stackCounter++;
                }
            }
            while (--stackCounter > 0) {
                filterList.add(ListFilterOperator.AND);
            }
        }
        if (requestFilterList != null && requestFilterList.size() > 0
          && possibleAndFilterParams != null && possibleAndFilterParams.size() > 0) {
            filterList.add(ListFilterOperator.AND);
        }

        return filterList;
    }

    public class FilterParam {
        boolean optional = true;
        String columnName;
        String relation;
        Object value;

        public FilterParam(String columnName, String relation) {
            this.columnName = columnName;
            this.relation = relation;
        }

        public FilterParam(String columnName, String relation, Object value) {
            this.columnName = columnName;
            this.relation = relation;
            this.value = value;
        }

        public FilterParam(String columnName, String relation, boolean optional) {
            this.columnName = columnName;
            this.relation = relation;
            this.optional = optional;
        }

        public boolean isSet() {
            if (value instanceof String) {
                return ((String) value).trim().length() > 0;
            }
            return value != null;
        }
    }

    public int getStartForPage(int page) {
        if (page <= 1) {
            return 0;
        }
        if ((page - 1) * limit >= getLastPageStart()) {
            return getLastPageStart();
        } else {
            return (page - 1) * limit;
        }
    }

    public String getFilterName() {
        return toString();
    }
}
