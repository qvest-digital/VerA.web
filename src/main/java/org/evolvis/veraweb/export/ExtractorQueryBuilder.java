package org.evolvis.veraweb.export;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), export module is
 * Copyright © 2016, 2017
 * 	Атанас Александров <a.alexandrov@tarent.de>
 * Copyright © 2016
 * 	Lukas Degener <l.degener@tarent.de>
 * 	Max Weierstall <m.weierstall@tarent.de>
 * Copyright © 2017
 * 	mirabilos <t.glaser@tarent.de>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
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

    public ExtractorQueryBuilder replace(Map<String, String> substitutions) {
        this.substitutions.putAll(substitutions);
        return this;
    }

    private Map<String, ColumnMapping> applyMappingsSubstitutions(Map<String, ColumnMapping> mappings) {
        for (Map.Entry<String, ColumnMapping> entry : mappings.entrySet()) {
            if (entry.getKey().startsWith(OPTIONAL_FIELD_LABEL_PREFIX)) {
                executeReplacement(mappings, entry);
            }
        }
        return mappings;
    }

    private void executeReplacement(Map<String, ColumnMapping> mappings, Map.Entry<String, ColumnMapping> entry) {
        final String mapTo = entry.getValue().getMapTo();
        final String group = mapTo.substring(2,mapTo.length()-1);
        final ColumnMapping replacement = new ColumnMapping(lookupSubstitution(group));
        mappings.put(entry.getKey(), replacement);
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
}
