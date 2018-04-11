package de.tarent.octopus.config;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
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

/**
 * Klasse zur Kapselung der Informaionen zu einem ContentWorker.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @see de.tarent.octopus.content.TcContentWorker
 */
public class ContentWorkerDeclaration {

    String workerName;
    String implementationSource;
    String factory;
    boolean singletonInstantiation = true;

    /**
     * Gibt an ob von dem Worker immer neue Instanzen erzeugt werden sollen,
     * oder immer die gleiche verwendet wird. Default ist singletonInstantiation = true
     */
    public boolean isSingletonInstantiation() {
        return singletonInstantiation;
    }

    public void setSingletonInstantiation(final boolean newSingletonInstantiation) {
        this.singletonInstantiation = newSingletonInstantiation;
    }

    /**
     * Liefert die Factory, von der dieser Worker erzeugt werden kann.
     *
     * @return Voll qualifizierter Klassenname der Factory
     */
    public String getFactory() {
        return factory;
    }

    public void setFactory(final String newFactory) {
        this.factory = newFactory;
    }

    /**
     * Liefert die Factoryspezifische Quelle des Workers.
     * Dies kann z.B. der Klassenname oder Scriptname des Workers sein.
     */
    public String getImplementationSource() {
        return implementationSource;
    }

    public void setImplementationSource(final String newImplementationSource) {
        this.implementationSource = newImplementationSource;
    }

    /**
     * Eindeutiger Bezeichner, unter dem den Worker in dem Modul verfügbar ist.
     */
    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(final String newWorkerName) {
        this.workerName = newWorkerName;
    }
}
