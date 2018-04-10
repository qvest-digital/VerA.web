package de.tarent.commons.datahandling;

/**
 * Abstraction of the parameters for filtering, sorting and paging of result lists.
 *
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
