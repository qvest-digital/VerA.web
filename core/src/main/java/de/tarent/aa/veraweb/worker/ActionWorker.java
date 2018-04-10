package de.tarent.aa.veraweb.worker;

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
import de.tarent.aa.veraweb.utils.OnlineRegistrationHelper;
import de.tarent.octopus.server.OctopusContext;

/**
 * Einfacher Worker, der User-Aktionen verwaltet und dazu den
 * Parameter 'action' aus dem Request in der Session verwaltet
 * und in den Content stellt.
 *
 * @author christoph
 */
public class ActionWorker {
    /**
     * Octopus-Eingabe-Parameter für {@link #load(OctopusContext, String)}
     */
    public static final String[] INPUT_load = { "action" };
    /**
     * Octopus-Eingabepflicht-Parameter für {@link #load(OctopusContext, String)}
     */
    public static final boolean[] MANDATORY_load = { false };
    /**
     * Octopus-Ausgabe-Parameter für {@link #load(OctopusContext, String)}
     */
    public static final String OUTPUT_load = "action";

    /**
     * Diese Worker-Aktion ermittelt aus Request und Session die aktuelle
     * Aktion, legt sie in der Session ab und gibt sie zurück.<br>
     * TODO: action Parameter Hack entfernen, siehe eventDetail.vm,
     * dort kann man sonst die form-action nicht setzten!!
     *
     * @param cntx   Octopus-Kontext
     * @param action optionaler Parameter "action"
     * @return aktuelle Aktion
     */
    public String load(OctopusContext cntx, String action) {
        String a = cntx.requestAsString("a");
        if (a != null && a.length() != 0) {
            action = a;
        }
        if (action == null) {
            action = cntx.sessionAsString("action");
        }
        if (action == null) {
            action = "";
        }
        cntx.setSession("action", action);
        loadOnlineRegistrationConfig(cntx);

        return action;
    }

    /**
     * Octopus-Eingabe-Parameter für {@link #remove(OctopusContext, String)}
     */
    public static final String[] INPUT_remove = { "action" };
    /**
     * Octopus-Eingabepflicht-Parameter für {@link #remove(OctopusContext, String)}
     */
    public static final boolean[] MANDATORY_remove = { false };
    /**
     * Octopus-Ausgabe-Parameter für {@link #remove(OctopusContext, String)}
     */
    public static final String OUTPUT_remove = "action";
    /**
     * Octopus-Config for Online Registration plattform activation
     */
    public static final String ONLINEREG_ACTIVATION = "online-registration.activated";
    /**
     * Octopus-Config for Online Registration mandant deactivation
     */
    public static final String ONLINEREG_MANDANT_DEACTIVATION = "mandanten-online-registration.deactivated";

    /**
     * Diese Methode löscht die aktuelle Aktion und ersetzt sie gegebenenfalls
     * durch die übergebene.
     *
     * @param cntx   Octopus-Kontext
     * @param action optionaler Parameter "action"
     * @return aktuelle Aktion
     */
    public String remove(OctopusContext cntx, String action) {
        cntx.setSession("action", action);
        return action;
    }

    /*
     * Setting Online Registration Config
     *
     * @param octopusContext OctopusContext
     */
    private void loadOnlineRegistrationConfig(OctopusContext cntx) {
        cntx.setContent(ONLINEREG_ACTIVATION, OnlineRegistrationHelper.isOnlineregActive(cntx));
        cntx.setContent(ONLINEREG_MANDANT_DEACTIVATION, OnlineRegistrationHelper.getDeactivatedMandantsAsArray(cntx));
    }

}
