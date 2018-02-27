package de.tarent.aa.veraweb.utils;

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
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
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
import java.util.Locale;
import java.util.Map;
/*
 * Language Helper was implemented to create a general message when deleting, creating or updating any entity
 * This utility is called from velocity through sending it to the OctopusContext
 */

public class LanguageHelper {

    final Map<String,String> placeholderWithTranslation;
    
    public LanguageHelper(Map<String, String> placeholderWithTranslation) {
        this.placeholderWithTranslation = placeholderWithTranslation;
    }

    @Deprecated
    public String createMessage(String entity,
            String action,
            String count,
            Map<String, String> placeholderWithTranslation) {
        return createMessage(entity, action, count);
    }

    public String createMessage(String entity,
                                String action,
                                String count) {

        String message;
        // singular or plural
        String singularOrPluralOrNone;
        if (count.equals("0")) {
            singularOrPluralOrNone = "N";
        } else if (count.equals("1")) {
            singularOrPluralOrNone = "S";
        } else {
            singularOrPluralOrNone = "P";
        }

        String placeholdername = "GM_" + entity + "_" + action + "_" + singularOrPluralOrNone;
        if (singularOrPluralOrNone.equals("P")) {
            message = String.format(placeholderWithTranslation.get(placeholdername), count);
        } else {
            message = placeholderWithTranslation.get(placeholdername);
        }

        return message;
    }

   

    public String makeFirstLetterLowerCase (String input) {
        Locale.setDefault(new Locale("en"));
        char c[] = input.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        String LowerCase = new String(c);

        return LowerCase;
    }

    
    // velocity 1.4 does not support varargs...
    public String l10n(String code){
        return translate(code);
    }

 // velocity 1.4 does not support varargs...
    public String l10n(String code,Object arg0){
        return translate(code,arg0);
    }
 // velocity 1.4 does not support varargs...
    public String l10n(String code, Object arg0, Object arg1){
        return translate(code, arg0, arg1);
    }
 // velocity 1.4 does not support varargs...
    public String l10n(String code, Object arg0, Object arg1, Object arg2){
        return translate(code, arg0, arg1, arg2);
    }
 // velocity 1.4 does not support varargs...
    public String l10n(String code, Object arg0, Object arg1, Object arg2, Object arg3){
        return translate(code, arg0, arg1, arg2, arg3);
    }
    public String translate(String code, Object ... args) {
        String format = placeholderWithTranslation.containsKey(code) ? placeholderWithTranslation.get(code): code;
        return String.format(format, args);
    }

}
