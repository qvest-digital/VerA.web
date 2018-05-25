package org.evolvis.veraweb.export;

import de.tarent.extract.ColumnMapping;
import de.tarent.extract.ExtractorQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicExtractorQuery extends ExtractorQuery {

    private List<String> selectedColumns;

    private String sql;
    private Map<String, ColumnMapping> mappings = new HashMap<String, ColumnMapping>();
    private int progressInterval = 500;

    public String getSql() {
        return sql;
    }

    public void setSql(final String sql) {
        this.sql = sql;
    }

    public void setSelectedColumns(final List<String> selectedColumns) {
        this.selectedColumns = selectedColumns;
    }

    public void setMappings(final Map<String, ColumnMapping> mappings) {
        this.mappings = mappings;
    }

    public Map<String, ColumnMapping> getMappings() {
        if (selectedColumns == null || selectedColumns.isEmpty()) {
            return mappings;
        }
        Map<String, ColumnMapping> mappingsToKeep = new HashMap<>();

        for (String selectedColumn : selectedColumns) {
            mappingsToKeep.put(selectedColumn, mappings.get(selectedColumn));
        }
        return mappingsToKeep;
    }

    public int getProgressInterval() {
        return progressInterval;
    }

    public void setProgressInterval(final int progressInterval) {
        this.progressInterval = progressInterval;
    }

}
