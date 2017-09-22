package de.tarent.aa.veraweb.utils;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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
