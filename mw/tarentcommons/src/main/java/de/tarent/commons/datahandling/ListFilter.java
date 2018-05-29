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

/**
 * Abstraction of the parameters for filtering, sorting and paging of result lists.
 */
public interface ListFilter extends ListFilterProvider {

    public static final String PARAM_RESET = "reset";
    public static final String PARAM_RESET_FILTER = "resetFilter";
    public static final String PARAM_START = "start";
    public static final String PARAM_LIMIT = "limit";
    public static final String PARAM_SORT_FIELD = "sortField";
    public static final String PARAM_SORT_DIRECTION = "sortDirection";
    public static final String PARAM_FILTER_LIST = "filterList";

    public static final String DIRECTION_ASC = "ASC";
    public static final String DIRECTION_DESC = "DESC";

    /**
     * Returns the column for sorting of the list
     */
    public String getSortField();

    /**
     * Returns the sort direction. This may be one of the constants DIRECTION_ASC, DIRECTION_DESC.
     */
    public String getSortDirection();

    /**
     * Returns the total count of records in the list.
     */
    public int getCount();

    /**
     * Sets the count of records in the list.
     */
    public void setCount(int count);

    /**
     * Returns the start position. (Beginning at 0)
     */
    public int getStart();

    /**
     * Returns the start position for the given page.
     *
     * @param page number of page
     * @return start position.
     */
    public int getStartForPage(int page);

    /**
     * Returns the count of records to return, beginning at position start.
     */
    public int getLimit();

    /**
     * Returns, if the result should be limited to <code>limit<code> rows.
     */
    public boolean useLimit();

    /**
     * Returns the count of pages for the total list, based on the values for limit and count.
     */
    public int getPages();

    /**
     * Returns the current page number.
     */
    public int getPage();

    /**
     * Returns the start position of the previous page, based on the values for start, limit and count.
     */
    public int getPreviousPageStart();

    /**
     * Returns the start position of the next page, based on the values for start, limit and count.
     */
    public int getNextPageStart();

    /**
     * Returns the start position of the last page, based on the values for start, limit and count.
     */
    public int getLastPageStart();

    public String getFilterName();
}
