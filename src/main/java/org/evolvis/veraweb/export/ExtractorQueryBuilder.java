package org.evolvis.veraweb.export;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tarent.extract.ColumnMapping;
import de.tarent.extract.ExtractorQuery;

public class ExtractorQueryBuilder {

    public static final String OPTIONAL_FIELD_LABEL_PREFIX = "OPTIONAL_FIELD_LABEL_";
    private final ExtractorQuery template;
    private Map<String, String> substitutions = new HashMap<String, String>();

    public ExtractorQueryBuilder(ExtractorQuery template) {
        this.template = template;
    }

    public ExtractorQueryBuilder replace(String string, Object o) {
        substitutions.put(string, o == null ? null : o.toString());
        return this;
    }

    public ExtractorQuery build() {
        final ExtractorQuery q = new ExtractorQuery();
        q.setMappings(applyMappingsSubstitutions(template.getMappings()));
        q.setProgressInterval(template.getProgressInterval());
        q.setSql(applySubstitutions(template.getSql()));
        return q;
    }

    private Map<String, ColumnMapping> applyMappingsSubstitutions(Map<String, ColumnMapping> mappings) {
        for (Map.Entry<String, ColumnMapping> entry : mappings.entrySet()) {
            if (entry.getKey().startsWith(OPTIONAL_FIELD_LABEL_PREFIX)) {
                final String mapTo = entry.getValue().getMapTo();
                final String group = mapTo.substring(2,mapTo.length()-1);
                final ColumnMapping replacement = new ColumnMapping(mapTo.replaceAll("\\$\\{([^\\}]+)\\}", lookupSubstitution(group)));
                mappings.put(entry.getKey(), replacement);
                mappings.remove(entry);
            }
        }
        return mappings;
    }

    private String applySubstitutions(String sql) {
        final StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile("\\$\\{([^\\}]+)\\}");
        Matcher m = p.matcher(sql);
        while (m.find()) {
            final String replacement = lookupSubstitution(m.group(1));
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private String lookupSubstitution(String group) {
        final String key = group.trim();
        if (substitutions.containsKey(key)) {
            return substitutions.get(key);
        }
        return key;
    }

    public ExtractorQueryBuilder replace(Map<String, String> substitutions) {
        this.substitutions.putAll(substitutions);
        return this;
    }

}
