package de.tarent.aa.veraweb.beans;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
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
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2007 Jan Meyer <jan@evolvis.org>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
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

import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.MapBean;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.PersonalConfig;

/**
 * Diese Klasse stellt eine abstrakte Basis für Beans auf Basis der
 * {@link de.tarent.octopus.beans.MapBean} dar.
 *
 * @author christoph
 */
public abstract class AbstractBean extends MapBean {
    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
     * darf.<br>
     * Default-Test ist, ob der Benutzer Administrator ist.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
     */
    public void checkRead(OctopusContext octopusContext) throws BeanException {
        checkGroup(octopusContext, PersonalConfigAA.GROUP_ADMIN);
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
     * werden darf.<br>
     * Default-Test ist, ob der Benutzer Administrator ist.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
     */
    public void checkWrite(OctopusContext octopusContext) throws BeanException {
        checkGroup(octopusContext, PersonalConfigAA.GROUP_ADMIN);
    }

    /**
     * Diese Methode leert beschränkte Felder.<br>
     * Achtung: Bei Benutzern, die diese Bean auch schreiben dürfen
     * (siehe {@link #checkWrite(OctopusContext)}), sollte die Bean hier nicht
     * verändert werden.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException bei Problemen mit der Bean
     */
    public void clearRestrictedFields(OctopusContext octopusContext) throws BeanException {
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext der User der übergebenen
     * Gruppe zugeordenet ist.
     *
     * @param octopusContext FIXME
     * @param group          FIXME
     * @throws BeanException FIXME
     */
    protected void checkGroup(OctopusContext octopusContext, String group) throws BeanException {
        PersonalConfig personalConfig = octopusContext != null ? octopusContext.personalConfig() : null;
        if (personalConfig == null) {
            throw new BeanException("No personal config");
        }
        if (!personalConfig.isUserInGroup(group)) {
            throw new BeanException("Only group " + group + " may write " + getClass().getName());
        }
    }

    /**
     * Checks whether the user is a member of either of the specified groups.
     *
     * @param octopusContext FIXME
     * @param groups         FIXME
     * @throws BeanException FIXME
     */
    protected void checkGroups(OctopusContext octopusContext, String... groups) throws BeanException {
        PersonalConfig personalConfig = (octopusContext != null) ? octopusContext.personalConfig() : null;
        if (personalConfig == null) {
            throw new BeanException("No personal config");
        }
        Boolean found = Boolean.FALSE;
        for (String group : groups) {
            found = personalConfig.isUserInGroup(group);
            if (found) {
                break;
            }
        }
        if (!found) {
            throw new BeanException("Only groups " + groups.toString() + " may write " + getClass().getName());
        }
    }
}
