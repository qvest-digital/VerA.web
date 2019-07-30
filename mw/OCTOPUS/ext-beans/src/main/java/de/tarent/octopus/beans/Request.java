package de.tarent.octopus.beans;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.tarent.octopus.server.OctopusContext;

/**
 * Konkrete {@link BeanFactory}, die Beans aus den Request-Parametern ausliest.
 *
 * @author Michael Klink, Alex Steeg, Christoph Jerolimov
 * @version 1.3
 */
public abstract class Request extends BeanFactory {
    //
    // Konstruktor
    //

    /**
     * Der Konstruktor merkt sich den übergebenen {@link OctopusContext} und
     * initialisiert die {@link BeanFactory} mit dem übergebenen Bean-Package.
     *
     * @param cntx        {@link OctopusContext}, aus dessen Request-Parameter Beans
     *                    ausgelesen werden.
     * @param beanPackage Package der zu nutzenden Bean-Klassen.
     */
    public Request(OctopusContext cntx, String beanPackage) {
        super(beanPackage);
        this.cntx = cntx;
    }

    //
    // öffentliche Methoden
    //

    /**
     * Diese Methode liefert eine Bean vom übergebenen Typ aus den passenden
     * Request-Parametern mit leerem Präfix.
     *
     * @param beanname Klasse der zu holenden Bean
     * @return die ausgelesene Bean oder <code>null</code>
     */
    public Bean getBean(String beanname) throws BeanException {
        return getBean(beanname, null);
    }

    /**
     * Diese Methode liefert eine Bean vom übergebenen Typ aus den passenden
     * Request-Parametern mit dem übergebenen Präfix.
     *
     * @param beanname Klasse der zu holenden Bean
     * @param prefix   Präfix der zu benutzenden Request-Parameter; falls
     *                 <code>null</code>, so wird kein Präfix voran gestellt.
     * @return die ausgelesene Bean oder <code>null</code>
     * @throws BeanException
     */
    public Bean getBean(String beanname, String prefix) throws BeanException {
        setPrefixes(Collections.singletonList(prefix));
        Bean bean = createBean(beanname);
        return hasNext() ? fillBean(bean) : null;
    }

    /**
     * Diese Methode holt eine Liste mit Bean-Präfixen aus dem Request und
     * generiert daraus eine Liste mit Bean-Instanzen vom übergebenen Typ.
     *
     * @param beanname Klasse der zu holenden Beans
     * @param listname Name des Request-Parameters, dessen Inhalt als Liste
     *                 von Bean-Präfixrn interpretiert wird
     * @return eine Liste ausgelesener Beans.
     * @throws BeanException
     */
    public List getBeanList(String beanname, String listname) throws BeanException {
        setPrefixes((List) BeanFactory.transform(cntx.requestAsObject(listname), List.class));
        return fillBeanList(beanname);
    }

    //
    // Basisklassen BeanFactory
    //

    /**
     * Diese Methode setzt basierend auf dem Inhalt des Request-Parameters
     * <code>[{@link #prefix PRÄFIX}-]modified</code> das Bean-Feld
     * {@link Bean#isModified() Modified}. Sollten keine Informationen
     * verfügbar sein, bleibt das Feld unangetastet, es sollte also vor dem
     * Aufruf dieser Methode sinnvoll vorbelegt sein.
     *
     * @param bean Bohne, deren {@link Bean#isModified() Modified}-Feld aktualisiert werden soll.
     * @throws BeanException
     * @see BeanFactory#checkModified(Bean)
     */
    protected void checkModified(Bean bean) throws BeanException {
        Object o = getField("modified");
        if (o != null && o instanceof String) {
            bean.setModified(Boolean.valueOf((String) o).booleanValue());
        }
    }

    /**
     * Liefert ein Objekt, das in ein bestimmtes Feld der "aktuellen" Bean
     * gesetzt werden soll.
     *
     * @param key Feld-Schlüssel
     * @return Feldinhalt oder null
     * @throws BeanException bei Datenzugriffsfehlern.
     * @see BeanFactory#getField(String)
     */
    public Object getField(String key) throws BeanException {
        return cntx.requestAsObject(prefix != null ? prefix + '-' + key : key);
    }

    /**
     * Wird verwendet, um bei Bean-Listen zur nächsten Bohne zu springen,
     * wird vor dem Einlesen einer Bean aufgerufen.
     *
     * @return <code>true</code>, wenn weitere Beans vorhanden sind, ansonsten
     * <code>false</code>.
     * @throws BeanException bei Datenzugriffsfehlern.
     * @see BeanFactory#hasNext()
     */
    public boolean hasNext() throws BeanException {
        if (it.hasNext()) {
            prefix = (String) it.next(); // FIXME: hasNext should not move the cursor
            return true;
        } else {
            return false;
        }
    }

    //
    // geschätzte Hilfsmethoden
    //

    /**
     * Diese Methode löscht das aktuelle Präfix und trägt als Präfix-Iterator
     * einen {@link Iterator} über die übergebene {@link Collection} ein.
     *
     * @param prefixes Sammlung von Präfixen.
     */
    void setPrefixes(Collection prefixes) {
        prefix = null;
        it = prefixes.iterator();
    }

    //
    // geschätzte Membervariablen
    //
    /**
     * Octopus-Kontext, in dessen Request-Parametern gearbeitet wird
     */
    final OctopusContext cntx;

    /**
     * Präfix der aktuellen Bean
     */
    String prefix = null;

    /**
     * Präfix-Iterator der aktuell noch abzuholenden Beans
     */
    Iterator it = null;
}
