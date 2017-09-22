package de.tarent.aa.veraweb.utils;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.helper.ResultMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class VerawebUtils {

    public static List copyResultListToArrayList(ResultList allResults) {
        final ArrayList fullList = new ArrayList();
        final int size = allResults.size();
        for (int i = 0; i < size; i++) {
            final ResultMap resultMap = (ResultMap) allResults.get(i);
            final HashMap map = new HashMap();
            map.putAll(resultMap);
            fullList.add(map);
        }

        return fullList;
    }

    public static String clearCommaSeparatedString(String inputStringWithSpacesAndCommas) {
        final String[] keywords = splitKeywords(inputStringWithSpacesAndCommas);
        final StringBuffer finalKeywordList = new StringBuffer();
        for (String keyword : keywords) {
            if (finalKeywordList.length() == 0) {
                finalKeywordList.append(keyword.trim());
            } else {
                finalKeywordList.append(",").append(keyword.trim());
            }
        }
        return finalKeywordList.toString();
    }

    public static String[] splitKeywords(String inputStringWithSpacesAndCommas) {
        return inputStringWithSpacesAndCommas.split("[^\\p{L}\\p{Nd}]+");
    }

    /**
     * Diese Methode liefert eine String-Darstellung eines Einladungsstatus
     *
     * @param status FIXME
     * @return FIXME
     */
    public static String getStatus(Integer status) {
        if (status == null || status.intValue() == EventConstants.STATUS_OPEN) {
            return "Offen";
        } else if (status.intValue() == EventConstants.STATUS_ACCEPT) {
            return "Zusage";
        } else if (status.intValue() == EventConstants.STATUS_REFUSE) {
            return "Absage";
        } else { // status == 3
			/*
			 * modified to support forth invitation state as per change request for version 1.2.0
			 * cklein
			 * 2008-02-26
			 */
            return "Teilnahme";
        }
    }

    /**
     * Diese Methode liefert eine String-Darstellung eines Veranstaltungstyps
     * @param type  FIXME
     * @return  FIXME
     */
    public static String getType(Integer type) {
        if (type == null || type.intValue() == EventConstants.TYPE_MITPARTNER) {
            return "Mit Partner";
        } else if (type.intValue() == EventConstants.TYPE_OHNEPARTNER) {
            return "Ohne Partner";
        } else { // type.intValue() == EventConstants.TYPE_NURPARTNER
            return "Nur Partner";
        }
    }
}